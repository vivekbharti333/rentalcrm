package com.datfusrental.services;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import junit.framework.TestCase;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import com.datfusrental.dao.LeadDetailsDao;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.entities.User;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.UserHelper;
import com.datfusrental.object.request.LeadRequestObject;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BookingUpgradeServiceTest extends TestCase {
    private BookingUpgradeService service;
    private LeadDetails original;
    private List<LeadDetails> rows;
    private LeadRequestObject request;
    private User user;

    private void inject(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Override protected void setUp() throws Exception {
        original = new LeadDetails();
        original.setId(1L);
        original.setBookingId("ORIGINAL");
        original.setSuperadminId("tenant");
        original.setCreatedBy("original-agent");
        original.setCreatedByName("Original Agent");
        original.setAdminId("original-admin");
        original.setTeamleaderId("original-team");
        original.setCategoryTypeName("Car");
        original.setCategory("Economy");
        original.setSubCategory("Manual");
        original.setStatus("WON");
        original.setQuantity(1);
        original.setTotalDays(1);
        original.setUpdatedAt(new Date(1000));
        original.setPickupDateTime(new Date(System.currentTimeMillis() + 86400000L));
        original.setDropDateTime(new Date(System.currentTimeMillis() + 172800000L));
        request = new LeadRequestObject();
        request.setQuantity(1);
        request.setTotalDays(1);
        request.setCompanyRate(5000);
        request.setVendorRate(3000);
        request.setActualAmount(2000);
        BookingUpgradeAmounts.calculate(original, request);
        rows = new ArrayList<>();
        rows.add(original);
        user = new User();
        user.setLoginId("agent");
        user.setSuperadminId("tenant");
        user.setRoleType("SALE_EXECUTIVE");
        user.setFirstName("Upgrade");
        user.setLastName("Agent");
        user.setPseudoName("UpgradeAgent");
        user.setAdminId("upgrade-admin");
        user.setTeamleaderId("upgrade-team");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("agent", null));

        EntityManager em = (EntityManager) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{EntityManager.class}, (proxy, method, args) -> {
            switch (method.getName()) {
                case "find": return rows.stream().filter(row -> row.getId().equals(args[1])).findFirst().orElse(null);
                case "refresh": case "flush": return null;
                case "persist":
                    LeadDetails row = (LeadDetails) args[0];
                    row.setId((long) rows.size() + 1);
                    rows.add(row);
                    return null;
                case "createQuery":
                    return Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{TypedQuery.class}, (query, op, values) -> {
                        if (op.getName().equals("setParameter") || op.getName().equals("setLockMode")) return query;
                        if (op.getName().equals("getResultList")) return new ArrayList<>(rows.subList(1, rows.size()));
                        throw new UnsupportedOperationException(op.getName());
                    });
                default: throw new UnsupportedOperationException(method.getName());
            }
        });
        LeadDetailsDao dao = new LeadDetailsDao() {
            @Override public EntityManager getEntityManager() { return em; }
            @Override public LeadDetails persist(LeadDetails value) { em.persist(value); return value; }
        };
        service = new BookingUpgradeService();
        inject(service, "dao", dao);
        inject(service, "userHelper", new UserHelper() {
            @Override public User getUserDetailsByLoginId(String id) { return user.getLoginId().equals(id) ? user : null; }
        });
        inject(service, "mapper", new ObjectMapper());
        request.setId(1L);
        request.setUpgradePreviousId(1L);
        request.setUpgradeExpectedUpdatedAt(1000L);
        request.setUpgradeRequestId(UUID.randomUUID().toString());
        request.setCategoryTypeName("Car");
        request.setCategory("Premium");
        request.setSubCategory("Automatic");
        request.setCompanyRate(7000);
        request.setVendorRate(4000);
        request.setPickupDateTime(original.getPickupDateTime());
        request.setDropDateTime(original.getDropDateTime());
        request.setUpgradeBookingAmount(1000);
        request.setActualAmount(0);
    }
    @Override protected void tearDown() { SecurityContextHolder.clearContext(); }

    public void testPreservesOriginalAndCreatesSeparateDifferenceRecord() throws Exception {
        LeadDetails before = new LeadDetails();
        BeanUtils.copyProperties(original, before);
        LeadDetails saved = service.upgrade(request);
        assertEquals(before, original);
        assertEquals("agent", saved.getCreatedBy());
        assertEquals("Upgrade Agent", saved.getCreatedByName());
        assertEquals("UpgradeAgent", saved.getPseudoName());
        assertEquals("upgrade-admin", saved.getAdminId());
        assertEquals("upgrade-team", saved.getTeamleaderId());
        assertEquals("original-agent", original.getCreatedBy());
        assertEquals(2, rows.size());
        assertEquals(Long.valueOf(1), saved.getUpgradeRootId());
        assertEquals(2000L, saved.getTotalAmount());
        assertEquals(0L, saved.getActualAmount());
        assertFalse(original.getBookingId().equals(saved.getBookingId()));
        assertEquals(7000L, ((LeadDetails) service.context(1L).get("current")).getTotalAmount());
    }
    public void testUpgradeStoresNewPaymentAndBookingDifference() throws Exception {
        request.setActualAmount(500);
        request.setBalanceAmount(4200);
        LeadDetails saved = service.upgrade(request);
        LeadDetails current = (LeadDetails) service.context(1L).get("current");
        assertEquals(500L, saved.getActualAmount());
        assertEquals(4200L, saved.getBalanceAmount());
        assertEquals(1000L, saved.getBookingAmount());
        assertEquals(Long.valueOf(5000), saved.getUpgradeOldTotal());
        assertEquals(Long.valueOf(7000), saved.getUpgradeNewTotal());
        assertEquals(2500L, current.getActualAmount());
        assertEquals(3000L, current.getBookingAmount());
        assertEquals(2000L, original.getActualAmount());
    }
    public void testRetryDoesNotCreateDuplicate() throws Exception {
        LeadDetails saved = service.upgrade(request);
        assertSame(saved, service.upgrade(request));
        assertEquals(2, rows.size());
    }
    public void testStaleUpgradeRejected() throws Exception {
        service.upgrade(request);
        request.setUpgradeRequestId(UUID.randomUUID().toString());
        try { service.upgrade(request); fail(); } catch (BizException expected) { }
        assertEquals(2, rows.size());
    }
    public void testRepeatedUpgradeComparesLatestSnapshot() throws Exception {
        LeadDetails first = service.upgrade(request);
        request.setUpgradePreviousId(first.getId());
        request.setUpgradeExpectedUpdatedAt(first.getUpdatedAt().getTime());
        request.setUpgradeRequestId(UUID.randomUUID().toString());
        request.setCategory("Luxury");
        request.setCompanyRate(8000);
        user.setLoginId("second-agent");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("second-agent", null));
        LeadDetails second = service.upgrade(request);
        assertEquals("agent", first.getCreatedBy());
        assertEquals("second-agent", second.getCreatedBy());
        assertEquals("original-agent", original.getCreatedBy());
        assertEquals(1000L, second.getTotalAmount());
        assertEquals(Long.valueOf(7000), second.getUpgradeOldTotal());
        assertEquals(5000L, original.getTotalAmount());
    }
    public void testUpgradeUsesSavedScheduleAndHeadcounts() throws Exception {
        request.setPickupDateTime(new Date(original.getPickupDateTime().getTime() + 60000));
        request.setDropDateTime(new Date(original.getDropDateTime().getTime() + 60000));
        request.setTotalDays(4);
        request.setQuantity(3);
        request.setKidQuantity(2);
        request.setInfantQuantity(1);

        LeadDetails saved = service.upgrade(request);
        LeadDetails current = (LeadDetails) service.context(1L).get("current");
        assertEquals(original.getPickupDateTime(), current.getPickupDateTime());
        assertEquals(original.getDropDateTime(), current.getDropDateTime());
        assertEquals(original.getTotalDays(), current.getTotalDays());
        assertEquals(original.getQuantity(), current.getQuantity());
        assertEquals(original.getKidQuantity(), current.getKidQuantity());
        assertEquals(original.getInfantQuantity(), current.getInfantQuantity());
        assertEquals(2000L, saved.getTotalAmount());
    }
    public void testOtherTenantDenied() throws Exception {
        user.setSuperadminId("other");
        try { service.upgrade(request); fail(); } catch (BizException expected) { }
        assertEquals(1, rows.size());
    }
    public void testAnotherAgentCanUpgradeAssignedWithoutChangingOriginal() throws Exception {
        original.setStatus("ASSIGNED");
        original.setVendorId(99L);
        LeadDetails before = new LeadDetails();
        BeanUtils.copyProperties(original, before);
        LeadDetails saved = service.upgrade(request);
        assertEquals(before, original);
        assertEquals("agent", saved.getCreatedBy());
        assertEquals("ASSIGNED", original.getStatus());
        assertEquals(Long.valueOf(99L), original.getVendorId());
        assertEquals("WON", saved.getStatus());
        assertNull(saved.getVendorId());
    }
    public void testOrdinaryEditAllowsSelectedRecordWithUpgradeHistory() throws Exception {
        LeadDetails saved = service.upgrade(request);
        service.validateOrdinaryEdit(original, request);
        service.validateOrdinaryEdit(saved, request);
        assertEquals(2, rows.size());
    }
    public void testEditedUpgradeKeepsCumulativeSnapshotInSync() throws Exception {
        request.setActualAmount(500);
        LeadDetails saved = service.upgrade(request);
        LeadDetails before = new LeadDetails();
        BeanUtils.copyProperties(saved, before);
        saved.setActualAmount(700);
        saved.setBalanceAmount(4300);
        saved.setRemarks("Edited current record");
        service.synchronizeEditedUpgrade(before, saved);
        LeadDetails current = (LeadDetails) service.context(1L).get("current");
        assertEquals(2700L, current.getActualAmount());
        assertEquals(3000L, current.getBookingAmount());
        assertEquals(4300L, current.getBalanceAmount());
        assertEquals("Edited current record", current.getRemarks());
        assertEquals(2, rows.size());
    }
    public void testRequestCannotSpoofUpgradeOwner() throws Exception {
        request.setCreatedBy("someone-else");
        request.setAdminId("other-admin");
        request.setTeamleaderId("other-team");
        LeadDetails saved = service.upgrade(request);
        assertEquals("agent", saved.getCreatedBy());
        assertEquals("upgrade-admin", saved.getAdminId());
        assertEquals("upgrade-team", saved.getTeamleaderId());
    }
    public void testNonStaffDenied() throws Exception {
        user.setRoleType("CUSTOMER");
        try { service.upgrade(request); fail(); } catch (BizException expected) { }
        assertEquals(1, rows.size());
    }
    public void testChangedOriginalRejectedBeforeInsert() throws Exception {
        original.setUpdatedAt(new Date(2000));
        try { service.upgrade(request); fail(); } catch (BizException expected) { }
        assertEquals(1, rows.size());
    }
}
