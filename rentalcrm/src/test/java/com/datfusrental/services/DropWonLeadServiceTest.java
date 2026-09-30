package com.datfusrental.services;

import java.lang.reflect.Field;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import junit.framework.TestCase;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.entities.User;
import com.datfusrental.helper.LeadByPickAndDropHelper;
import com.datfusrental.helper.UserHelper;
import com.datfusrental.object.request.LeadRequestObject;
import com.datfusrental.object.request.Request;

public class DropWonLeadServiceTest extends TestCase {
    private DropWonLeadService service;
    private User user;
    private LeadRequestObject captured;
    private AtomicReference<Instant> now;
    private final ZoneId zone = ZoneId.of("Asia/Kolkata");

    private void inject(String name, Object value) throws Exception {
        Field field = DropWonLeadService.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(service, value);
    }
    @Override protected void setUp() throws Exception {
        now = new AtomicReference<>(Instant.parse("2026-09-30T18:29:59Z"));
        Clock clock = new Clock() {
            public ZoneId getZone() { return zone; }
            public Clock withZone(ZoneId requested) { return this; }
            public Instant instant() { return now.get(); }
        };
        service = new DropWonLeadService(clock);
        user = new User();
        user.setLoginId("agent"); user.setRoleType("SALE_EXECUTIVE"); user.setSuperadminId("company");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("agent", null, Collections.emptyList()));
        inject("userHelper", new UserHelper() {
            @Override public User getUserDetailsByLoginId(String login) { return "agent".equals(login) ? user : null; }
        });
        inject("leadByPickAndDropHelper", new LeadByPickAndDropHelper() {
            @Override public List<LeadDetails> getDropWonLeadList(LeadRequestObject scoped) {
                captured = scoped; return Collections.emptyList();
            }
        });
    }
    @Override protected void tearDown() { SecurityContextHolder.clearContext(); }
    private Request<LeadRequestObject> request(String filter) {
        LeadRequestObject input = new LeadRequestObject(); input.setRequestedFor(filter);
        Request<LeadRequestObject> request = new Request<>(); request.setPayload(input); return request;
    }
    private Date day(String value) { return Date.from(LocalDate.parse(value).atStartOfDay(zone).toInstant()); }
    private void range(String first, String last) {
        assertEquals(day(first), captured.getFirstDate()); assertEquals(day(last), captured.getLastDate());
    }
    private void rejected(Request<LeadRequestObject> input) {
        captured = null;
        try { service.getDropWonLeadList(input); fail("Expected invalid request"); }
        catch (IllegalArgumentException expected) { assertNull(captured); }
    }
    public void testTodayUsesIndiaMidnightAndExclusiveTomorrow() {
        service.getDropWonLeadList(request("TODAY")); range("2026-09-30", "2026-10-01");
    }
    public void testTomorrowAcrossMonthBoundary() {
        service.getDropWonLeadList(request("TOMORROW")); range("2026-10-01", "2026-10-02");
    }
    public void testMonthIncludesEntireLastDay() {
        service.getDropWonLeadList(request("MONTH")); range("2026-09-01", "2026-10-01");
        now.set(Instant.parse("2028-02-20T12:00:00Z"));
        service.getDropWonLeadList(request("MONTH")); range("2028-02-01", "2028-03-01");
    }
    public void testSameServiceRefreshesDatesAfterMidnight() {
        service.getDropWonLeadList(request("TODAY")); range("2026-09-30", "2026-10-01");
        now.set(now.get().plusSeconds(2));
        service.getDropWonLeadList(request("TODAY")); range("2026-10-01", "2026-10-02");
        service.getDropWonLeadList(request("MONTH")); range("2026-10-01", "2026-11-01");
    }
    public void testCustomSingleDayAndLegacySpellingIncludeFullDay() {
        for (String name : List.of("CUSTOM", "CUSTOME")) {
            Request<LeadRequestObject> input = request(name);
            input.getPayload().setFirstDate(day("2026-09-30"));
            input.getPayload().setLastDate(day("2026-09-30"));
            service.getDropWonLeadList(input); range("2026-09-30", "2026-10-01");
        }
    }
    public void testRejectsMissingReversedAndUnknownFilters() {
        rejected(null); rejected(new Request<LeadRequestObject>()); rejected(request(null)); rejected(request("ALL"));
        rejected(request("CUSTOM"));
        Request<LeadRequestObject> input = request("CUSTOM");
        input.getPayload().setFirstDate(day("2026-10-02")); input.getPayload().setLastDate(day("2026-10-01"));
        rejected(input);
    }
    public void testIgnoresForgedCompanyRoleAndOwner() {
        Request<LeadRequestObject> input = request("TODAY");
        input.getPayload().setRoleType("SUPERADMIN"); input.getPayload().setSuperadminId("other-company");
        input.getPayload().setLoginId("other-agent"); input.getPayload().setCreatedBy("other-agent");
        service.getDropWonLeadList(input);
        assertEquals("SALE_EXECUTIVE", captured.getRoleType());
        assertEquals("company", captured.getSuperadminId()); assertEquals("agent", captured.getLoginId());
        assertNull(captured.getCreatedBy());
    }
    public void testSuperadminUsesOwnCompanyId() {
        user.setRoleType("SUPERADMIN"); user.setSuperadminId("other-company");
        service.getDropWonLeadList(request("TODAY")); assertEquals("agent", captured.getSuperadminId());
    }
    public void testRejectsUnsignedCustomerAndMissingCompany() {
        user.setRoleType("CUSTOMER"); rejected(request("TODAY"));
        user.setRoleType("SALE_EXECUTIVE"); user.setSuperadminId(null); rejected(request("TODAY"));
        SecurityContextHolder.clearContext(); rejected(request("TODAY"));
    }
}
