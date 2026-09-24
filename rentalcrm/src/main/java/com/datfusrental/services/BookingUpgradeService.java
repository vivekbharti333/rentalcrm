package com.datfusrental.services;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.LockModeType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.datfusrental.constant.Constant;
import com.datfusrental.dao.LeadDetailsDao;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.entities.User;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.UserHelper;
import com.datfusrental.object.request.LeadRequestObject;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BookingUpgradeService {
    @Autowired private LeadDetailsDao dao;
    @Autowired private UserHelper userHelper;
    @Autowired private ObjectMapper mapper;

    private BizException invalid(String message) { return new BizException(Constant.BAD_REQUEST_CODE, message); }

    private User authorize(LeadDetails lead) throws BizException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = auth == null ? null : userHelper.getUserDetailsByLoginId(auth.getName());
        String tenant = user == null ? null : ("SUPERADMIN".equalsIgnoreCase(user.getRoleType())
                ? user.getLoginId() : user.getSuperadminId());
        if (user == null || lead == null || tenant == null
                || !Objects.equals(tenant, lead.getSuperadminId())) {
            throw invalid("Booking not found or access denied.");
        }
        String role = Objects.toString(user.getRoleType(), "").trim().toUpperCase(java.util.Locale.ROOT).replace(' ', '_');
        if (!List.of("SUPERADMIN", "ADMIN", "TEAM_LEADER", "SALE_EXECUTIVE", "SALES_EXECUTIVE",
                "CUSTOMER_EXECUTIVE").contains(role)) {
            throw invalid("Only staff agents can upgrade bookings.");
        }
        return user;
    }

    private List<LeadDetails> history(Long rootId) {
        return history(rootId, false);
    }

    private List<LeadDetails> history(Long rootId, boolean currentRead) {
        javax.persistence.TypedQuery<LeadDetails> query = dao.getEntityManager().createQuery(
                "SELECT L FROM LeadDetails L WHERE L.upgradeRootId = :root ORDER BY L.id", LeadDetails.class)
                .setParameter("root", rootId);
        // MySQL repeatable-read snapshots may predate the root lock: use a current read when writing.
        if (currentRead) query.setLockMode(LockModeType.PESSIMISTIC_READ);
        return query.getResultList();
    }

    private LeadDetails root(Long id) throws BizException {
        if (id == null) throw invalid("Booking id is required.");
        LeadDetails selected = dao.getEntityManager().find(LeadDetails.class, id);
        authorize(selected);
        LeadDetails root = selected.getUpgradeRootId() == null ? selected
                : dao.getEntityManager().find(LeadDetails.class, selected.getUpgradeRootId());
        authorize(root);
        return root;
    }

    private LeadDetails snapshot(LeadDetails latest) throws Exception {
        if (latest.getUpgradeRootId() != null) return mapper.readValue(latest.getUpgradeSnapshot(), LeadDetails.class);
        LeadDetails copy = new LeadDetails();
        BeanUtils.copyProperties(latest, copy);
        return copy;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> context(Long id) throws Exception {
        LeadDetails original = root(id);
        List<LeadDetails> upgrades = history(original.getId());
        LeadDetails latest = upgrades.isEmpty() ? original : upgrades.get(upgrades.size() - 1);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("original", original);
        result.put("current", snapshot(latest));
        result.put("previousId", latest.getId());
        result.put("expectedUpdatedAt", latest.getUpdatedAt() == null ? null : latest.getUpdatedAt().getTime());
        result.put("upgrades", upgrades);
        result.put("selectedIsUpgrade", !Objects.equals(id, original.getId()));
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public LeadDetails upgrade(LeadRequestObject request) throws Exception {
        if (request == null) throw invalid("Upgrade request is required.");
        LeadDetails original = root(request.getId());
        // Serialize upgrades to this booking without issuing an UPDATE on the old row.
        dao.getEntityManager().refresh(original, LockModeType.PESSIMISTIC_WRITE);
        User actor = authorize(original);
        List<LeadDetails> upgrades = history(original.getId(), true);
        if (request.getUpgradeRequestId() == null) throw invalid("Upgrade request id is required.");
        try { UUID.fromString(request.getUpgradeRequestId()); }
        catch (IllegalArgumentException e) { throw invalid("Invalid upgrade request id."); }
        for (LeadDetails saved : upgrades) {
            if (request.getUpgradeRequestId().equals(saved.getUpgradeRequestId())) return saved;
        }
        LeadDetails latest = upgrades.isEmpty() ? original : upgrades.get(upgrades.size() - 1);
        if (!Objects.equals(latest.getId(), request.getUpgradePreviousId())) {
            throw invalid("This booking has changed. Reopen it before upgrading.");
        }
        Long updatedAt = latest.getUpdatedAt() == null ? null : latest.getUpdatedAt().getTime();
        if (!Objects.equals(updatedAt, request.getUpgradeExpectedUpdatedAt())) {
            throw invalid("This booking has changed. Reopen it before upgrading.");
        }
        if (!"WON".equalsIgnoreCase(original.getStatus()) && !"ASSIGNED".equalsIgnoreCase(original.getStatus())) {
            throw invalid("Only won or assigned bookings can be upgraded.");
        }
        LeadDetails previous = snapshot(latest);
        if (Objects.equals(previous.getCategory(), request.getCategory())
                && Objects.equals(previous.getSubCategory(), request.getSubCategory())) {
            throw invalid("Select a different category or package for the upgrade.");
        }
        if (request.getCategory() == null || request.getCategory().trim().isEmpty()
                || request.getSubCategory() == null || request.getSubCategory().trim().isEmpty()) {
            throw invalid("Select an upgrade category and package.");
        }
        if (!Objects.equals(previous.getCategoryTypeName(), request.getCategoryTypeName())
                || !Objects.equals(Boolean.TRUE.equals(previous.getNeedGstInvoice()), Boolean.TRUE.equals(request.getNeedGstInvoice()))) {
            throw invalid("An upgrade must retain the booking type and GST setting.");
        }
        if (!Objects.equals(previous.getPickupDateTime(), request.getPickupDateTime())
                || !Objects.equals(previous.getDropDateTime(), request.getDropDateTime())
                || previous.getTotalDays() != request.getTotalDays()
                || previous.getQuantity() != request.getQuantity() || previous.getKidQuantity() != request.getKidQuantity()
                || previous.getInfantQuantity() != request.getInfantQuantity()) {
            throw invalid("A category upgrade must retain the booking dates, duration and quantities.");
        }
        if (previous.getDropDateTime() == null || previous.getDropDateTime().before(new Date())) {
            throw invalid("Completed bookings cannot be upgraded.");
        }
        LeadDetails next = snapshot(latest);
        // Category/pricing come from the request; upgrade ownership comes only from the signed-in user.
        next.setCategory(request.getCategory());
        next.setSuperCategory(request.getSuperCategory());
        next.setSubCategory(request.getSubCategory());
        next.setCompanyRate(request.getCompanyRate());
        next.setCompanyRateForKids(request.getCompanyRateForKids());
        next.setVendorRate(request.getVendorRate());
        next.setVendorRateForKids(request.getVendorRateForKids());
        next.setDeliveryAmountToCompany(request.getDeliveryAmountToCompany());
        next.setDeliveryAmountToVendor(request.getDeliveryAmountToVendor());
        next.setSecurityAmount(request.getSecurityAmount());
        next.setDiscount(request.getDiscount());
        next.setPaymentType(request.getPaymentType());
        next.setRemarks(request.getRemarks());
        try { BookingUpgradeAmounts.calculate(next, request); }
        catch (IllegalArgumentException | ArithmeticException e) { throw invalid(e.getMessage()); }
        if (next.getActualAmount() > previous.getActualAmount()
                && (request.getPaymentType() == null || request.getPaymentType().trim().isEmpty())) {
            throw invalid("Payment type is required for the additional payment.");
        }
        next.setCreatedBy(actor.getLoginId());
        next.setCreatedByName((Objects.toString(actor.getFirstName(), "") + " "
                + Objects.toString(actor.getLastName(), "")).trim());
        next.setPseudoName(actor.getPseudoName());
        next.setAdminId(actor.getAdminId());
        next.setTeamleaderId(actor.getTeamleaderId());
        LeadDetails row = new LeadDetails();
        BeanUtils.copyProperties(next, row);
        row.setId(null);
        row.setUpgradeSnapshot(mapper.writeValueAsString(next));
        try { BookingUpgradeAmounts.makeDifference(row, previous); }
        catch (IllegalArgumentException | ArithmeticException e) { throw invalid(e.getMessage()); }
        row.setUpgradeRootId(original.getId());
        row.setUpgradePreviousId(latest.getId());
        row.setUpgradeRequestId(request.getUpgradeRequestId());
        row.setBookingId("UPG-" + request.getUpgradeRequestId());
        row.setStatus("WON");
        row.setVendorId(null);
        row.setVendorName(null);
        row.setSecondStatus(null);
        row.setPickupConfirmed(null);
        row.setDropConfirmed(null);
        row.setPickDropConfirmedNotes(null);
        row.setNextFollowupDate(null);
        row.setNotes("Category upgrade of " + original.getBookingId() + "; previous record " + latest.getId());
        row.setCreatedAt(new Date());
        row.setUpdatedAt(new Date());
        row.setChangeStatusDate(new Date());
        row.setUpdatedBy(actor.getLoginId());
        dao.persist(row);
        dao.getEntityManager().flush();
        return row;
    }

    public void validateOrdinaryEdit(LeadDetails lead, LeadRequestObject request) throws BizException {
        dao.getEntityManager().refresh(lead, LockModeType.PESSIMISTIC_WRITE);
        if (lead.getUpgradeRootId() != null || !history(lead.getId(), true).isEmpty()) {
            throw invalid("Upgrade records and their original booking cannot be edited. Use Upgrade booking.");
        }
        if (("WON".equalsIgnoreCase(lead.getStatus()) || "ASSIGNED".equalsIgnoreCase(lead.getStatus()))
                && (!Objects.equals(lead.getCategory(), request.getCategory())
                    || !Objects.equals(lead.getSubCategory(), request.getSubCategory()))) {
            throw invalid("Use Upgrade booking to change a booked category without overwriting the original.");
        }
    }
}
