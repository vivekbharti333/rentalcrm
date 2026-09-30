package com.datfusrental.services;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.entities.User;
import com.datfusrental.helper.LeadByPickAndDropHelper;
import com.datfusrental.helper.UserHelper;
import com.datfusrental.object.request.LeadRequestObject;
import com.datfusrental.object.request.Request;

@Service
public class DropWonLeadService {
    private static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");
    private final Clock clock;
    @Autowired private UserHelper userHelper;
    @Autowired private LeadByPickAndDropHelper leadByPickAndDropHelper;

    public DropWonLeadService() { this(Clock.system(ZONE)); }
    DropWonLeadService(Clock clock) { this.clock = clock.withZone(ZONE); }

    @Transactional(readOnly = true)
    public List<LeadDetails> getDropWonLeadList(Request<LeadRequestObject> request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new IllegalArgumentException("Sign in to view drop bookings.");
        }
        User user = userHelper.getUserDetailsByLoginId(auth.getName());
        if (user == null || blank(user.getLoginId())) throw new IllegalArgumentException("Invalid signed-in user.");
        String role = user.getRoleType() == null ? "" : user.getRoleType().trim().toUpperCase(Locale.ROOT).replace(' ', '_');
        if (!List.of("SUPERADMIN", "ADMIN", "TEAM_LEADER", "SALE_EXECUTIVE", "SALES_EXECUTIVE", "CUSTOMER_EXECUTIVE").contains(role)) {
            throw new IllegalArgumentException("Only staff can view drop bookings.");
        }
        String tenant = "SUPERADMIN".equals(role) ? user.getLoginId() : user.getSuperadminId();
        if (blank(tenant)) throw new IllegalArgumentException("No company is associated with this user.");
        if (request == null || request.getPayload() == null) throw new IllegalArgumentException("Request payload is required.");
        LeadRequestObject input = request.getPayload();
        if (blank(input.getRequestedFor())) throw new IllegalArgumentException("requestedFor is required.");

        // Read the clock on every call, including after midnight/month rollover.
        LocalDate today = LocalDate.now(clock);
        LocalDate first;
        LocalDate exclusiveLast;
        switch (input.getRequestedFor().trim().toUpperCase(Locale.ROOT)) {
            case "TODAY":
                first = today; exclusiveLast = today.plusDays(1); break;
            case "TOMORROW":
                first = today.plusDays(1); exclusiveLast = today.plusDays(2); break;
            case "MONTH":
                first = today.withDayOfMonth(1); exclusiveLast = first.plusMonths(1); break;
            case "CUSTOM": case "CUSTOME": // Keep deployed older clients working.
                if (input.getFirstDate() == null || input.getLastDate() == null) {
                    throw new IllegalArgumentException("First date and last date are required for custom search.");
                }
                first = input.getFirstDate().toInstant().atZone(ZONE).toLocalDate();
                LocalDate last = input.getLastDate().toInstant().atZone(ZONE).toLocalDate();
                if (last.isBefore(first)) throw new IllegalArgumentException("Last date must not be before first date.");
                exclusiveLast = last.plusDays(1); break;
            default:
                throw new IllegalArgumentException("requestedFor must be TODAY, TOMORROW, MONTH or CUSTOM.");
        }

        // Never trust role, company, login or owner IDs from the browser.
        LeadRequestObject scoped = new LeadRequestObject();
        scoped.setRoleType(role);
        scoped.setSuperadminId(tenant);
        scoped.setLoginId(user.getLoginId());
        scoped.setFirstDate(Date.from(first.atStartOfDay(ZONE).toInstant()));
        scoped.setLastDate(Date.from(exclusiveLast.atStartOfDay(ZONE).toInstant()));
        return leadByPickAndDropHelper.getDropWonLeadList(scoped);
    }

    private static boolean blank(String value) { return value == null || value.trim().isEmpty(); }
}
