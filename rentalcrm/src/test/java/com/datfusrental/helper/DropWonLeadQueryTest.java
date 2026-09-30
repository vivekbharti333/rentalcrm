package com.datfusrental.helper;

import java.lang.reflect.*;
import java.util.*;
import javax.persistence.*;
import junit.framework.TestCase;
import com.datfusrental.dao.LeadDetailsDao;
import com.datfusrental.object.request.LeadRequestObject;

public class DropWonLeadQueryTest extends TestCase {
    private LeadByPickAndDropHelper helper;
    private String hql;
    private Map<String, Object> parameters;
    private Map<String, TemporalType> types;
    @Override protected void setUp() throws Exception {
        parameters = new HashMap<>(); types = new HashMap<>();
        EntityManager em = (EntityManager) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{EntityManager.class}, (proxy, method, args) -> {
            if (!method.getName().equals("createQuery")) throw new UnsupportedOperationException(method.getName());
            hql = (String) args[0];
            return Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{TypedQuery.class}, (query, operation, values) -> {
                if (operation.getName().equals("setParameter")) {
                    parameters.put((String) values[0], values[1]);
                    if (values.length == 3) types.put((String) values[0], (TemporalType) values[2]);
                    return query;
                }
                if (operation.getName().equals("getResultList")) return Collections.emptyList();
                throw new UnsupportedOperationException(operation.getName());
            });
        });
        helper = new LeadByPickAndDropHelper();
        Field field = LeadByPickAndDropHelper.class.getDeclaredField("leadDetailsDao"); field.setAccessible(true);
        field.set(helper, new LeadDetailsDao() { @Override public EntityManager getEntityManager() { return em; } });
    }
    private void query(String role) {
        LeadRequestObject input = new LeadRequestObject(); input.setRoleType(role); input.setSuperadminId("company"); input.setLoginId("agent");
        input.setFirstDate(new Date(1000)); input.setLastDate(new Date(2000));
        helper.getDropWonLeadList(input);
    }
    public void testSuperadminAlwaysCompanyScoped() {
        query("SUPERADMIN"); assertTrue(hql.contains("LD.superadminId = :superadminId"));
        assertEquals("company", parameters.get("superadminId")); assertFalse(parameters.containsKey("loginId"));
    }
    public void testAdminSortsByDropNotPickup() {
        query("ADMIN"); assertTrue(hql.contains("ORDER BY LD.dropDateTime DESC, LD.id DESC"));
        assertFalse(hql.contains("pickupDateTime")); assertEquals("company", parameters.get("superadminId"));
    }
    public void testAgentsHaveOwnBookingsInCompany() {
        for (String role : List.of("SALE_EXECUTIVE", "SALES_EXECUTIVE", "CUSTOMER_EXECUTIVE")) {
            query(role); assertTrue(hql.contains("AND LD.createdBy = :loginId"));
            assertEquals("agent", parameters.get("loginId")); assertEquals("company", parameters.get("superadminId"));
        }
    }
    public void testTeamLeaderHasTeamAndOwnBookings() {
        query("TEAM_LEADER"); assertTrue(hql.contains("(LD.teamleaderId = :loginId OR LD.createdBy = :loginId)"));
        assertEquals("agent", parameters.get("loginId"));
    }
    public void testTimestampBoundsStatusesAndSupersededFilter() {
        query("SUPERADMIN");
        assertEquals(TemporalType.TIMESTAMP, types.get("firstDate")); assertEquals(TemporalType.TIMESTAMP, types.get("lastDate"));
        assertTrue(hql.contains("LD.dropDateTime >= :firstDate AND LD.dropDateTime < :lastDate"));
        assertEquals(List.of("WON", "ASSIGNED"), parameters.get("statuses"));
        assertTrue(hql.contains("NOT EXISTS (SELECT U.id FROM LeadDetails U WHERE U.upgradePreviousId = LD.id)"));
    }
}
