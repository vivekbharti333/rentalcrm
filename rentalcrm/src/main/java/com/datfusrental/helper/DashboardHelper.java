package com.datfusrental.helper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.datfusrental.constant.Constant;
import com.datfusrental.dao.LeadDetailsDao;
import com.datfusrental.enums.RoleType;
import com.datfusrental.enums.Status;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.DashboardRequestObject;

@Component
public class DashboardHelper {

	@Autowired
	private LeadDetailsDao leadDetailsDao;

	public void validateDashboardRequest(DashboardRequestObject dashboardRequest) throws BizException {
		if (dashboardRequest == null) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Bad Request Object Null");
		}
		if (dashboardRequest.getRoleType() == null || dashboardRequest.getRoleType().trim().isEmpty()) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Role Type is required");
		}
		if (!"TODAY".equalsIgnoreCase(dashboardRequest.getRequestedFor())) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "requestFor must be TODAY");
		}
		String ownerId = isSuperadmin(dashboardRequest) ? dashboardRequest.getSuperadminId() : dashboardRequest.getCreatedBy();
		if (ownerId == null || ownerId.trim().isEmpty()) {
			throw new BizException(Constant.BAD_REQUEST_CODE,
					isSuperadmin(dashboardRequest) ? "Superadmin Id is required" : "Created By is required");
		}
	}

	public boolean isSuperadmin(DashboardRequestObject dashboardRequest) {
		return RoleType.SUPERADMIN.name().equalsIgnoreCase(dashboardRequest.getRoleType());
	}

	public void populateTodayCounts(DashboardRequestObject dashboardRequest) {
		ZoneId zone = ZoneId.of("Asia/Kolkata");
		LocalDate today = LocalDate.now(zone);
		Date startDate = Date.from(today.atStartOfDay(zone).toInstant());
		Date endDate = Date.from(today.plusDays(1).atStartOfDay(zone).toInstant());
		boolean team = isSuperadmin(dashboardRequest);
		String ownerFilter = team ? "LD.superadminId = :ownerId" : "LD.createdBy = :ownerId";
		Object[] counts = leadDetailsDao.getEntityManager()
				.createQuery("SELECT COUNT(LD), SUM(LD.actualAmount) "
						+ "FROM LeadDetails LD WHERE LD.status = :status AND " + ownerFilter
						+ " AND LD.changeStatusDate >= :startDate AND LD.changeStatusDate < :endDate", Object[].class)
				.setParameter("status", Status.WON.name())
				.setParameter("ownerId", team ? dashboardRequest.getSuperadminId() : dashboardRequest.getCreatedBy())
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate)
				.getSingleResult();
		dashboardRequest.setTodayTotalWinCount(((Number) counts[0]).longValue());
		dashboardRequest.setTodayTotalWonAmount(counts[1] == null ? 0L : ((Number) counts[1]).longValue());
	}
	
	public List<DashboardRequestObject> getTodayWonSummaryByCreatedBy(DashboardRequestObject dashboardRequest)
			throws BizException {

		ZoneId zone = ZoneId.of("Asia/Kolkata");
		LocalDate today = LocalDate.now(zone);
		Date startDate = Date.from(today.atStartOfDay(zone).toInstant());
		Date endDate = Date.from(today.plusDays(1).atStartOfDay(zone).toInstant());
		boolean allData = dashboardRequest != null && Boolean.TRUE.equals(dashboardRequest.getAllData());
		String roleFilter = allData ? "" : "AND U.roleType IN :roleTypes ";

		TypedQuery<Object[]> summaryQuery = leadDetailsDao.getEntityManager()
				.createQuery("SELECT LD.createdBy, U.firstName, U.lastName, U.roleType, COUNT(LD), SUM(LD.actualAmount) "
						+ "FROM LeadDetails LD, User U "
						+ "WHERE LD.createdBy = U.loginId "
						+ roleFilter
						+ "AND LD.status IN :statuses "
						+ "AND LD.changeStatusDate >= :startDate AND LD.changeStatusDate < :endDate "
						+ "GROUP BY LD.createdBy, U.firstName, U.lastName, U.roleType "
						+ "ORDER BY COUNT(LD) DESC, U.firstName ASC, U.lastName ASC", Object[].class)
				.setParameter("statuses", List.of(Status.WON.name(), "ASSIGNED"))
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate);
		if (!allData) {
			summaryQuery.setParameter("roleTypes", List.of("SALE_EXECUTIVE", "SALE EXECUTIVE"));
		}
		List<Object[]> groupedResults = summaryQuery.getResultList();

		List<DashboardRequestObject> summary = new ArrayList<>();
		for (Object[] row : groupedResults) {
			DashboardRequestObject item = new DashboardRequestObject();
			String firstName = row[1] == null ? "" : row[1].toString().trim();
			String lastName = row[2] == null ? "" : row[2].toString().trim();
			item.setCreatedBy((String) row[0]);
			item.setCreatedByName((firstName + " " + lastName).trim());
			item.setRoleType((String) row[3]);
			item.setWonLeadCount(((Number) row[4]).longValue());
			item.setTotalActualAmount(row[5] == null ? 0L : ((Number) row[5]).longValue());
			// Keep the existing count field for clients already using it.
			item.setActualAmountCount(item.getWonLeadCount());
			summary.add(item);
		}

		return summary;
	}


//	public List<DashboardRequestObject> getTodayWonSummaryByCreatedBy(DashboardRequestObject dashboardRequest)
//			throws BizException {
//
//		ZoneId zone = ZoneId.of("Asia/Kolkata");
//		LocalDate today = LocalDate.now(zone);
//		Date startDate = Date.from(today.atStartOfDay(zone).toInstant());
//		Date endDate = Date.from(today.plusDays(1).atStartOfDay(zone).toInstant());
//		boolean allData = dashboardRequest != null && Boolean.TRUE.equals(dashboardRequest.getAllData());
//		String roleFilter = allData ? "" : "AND U.roleType IN :roleTypes ";
//
//		TypedQuery<Object[]> summaryQuery = leadDetailsDao.getEntityManager()
//				.createQuery("SELECT LD.createdBy, U.firstName, U.lastName, U.roleType, COUNT(LD), SUM(LD.actualAmount) "
//						+ "FROM LeadDetails LD, User U "
//						+ "WHERE LD.createdBy = U.loginId "
//						+ roleFilter
//						+ "AND LD.status IN :statuses "
//						+ "AND LD.changeStatusDate >= :startDate AND LD.changeStatusDate < :endDate "
//						+ "GROUP BY LD.createdBy, U.firstName, U.lastName, U.roleType "
//						+ "ORDER BY COUNT(LD) DESC, U.firstName ASC, U.lastName ASC", Object[].class)
//				.setParameter("statuses", List.of(Status.WON.name(), "ASSIGNED"))
//				.setParameter("startDate", startDate)
//				.setParameter("endDate", endDate);
//		if (!allData) {
//			summaryQuery.setParameter("roleTypes", List.of("SALE_EXECUTIVE", "SALE EXECUTIVE"));
//		}
//		List<Object[]> groupedResults = summaryQuery.getResultList();
//
//		List<DashboardRequestObject> summary = new ArrayList<>();
//		for (Object[] row : groupedResults) {
//			DashboardRequestObject item = new DashboardRequestObject();
//			String firstName = row[1] == null ? "" : row[1].toString().trim();
//			String lastName = row[2] == null ? "" : row[2].toString().trim();
//			item.setCreatedBy((String) row[0]);
//			item.setCreatedByName((firstName + " " + lastName).trim());
//			item.setRoleType((String) row[3]);
//			item.setWonLeadCount(((Number) row[4]).longValue());
//			item.setTotalActualAmount(row[5] == null ? 0L : ((Number) row[5]).longValue());
//			// Keep the existing count field for clients already using it.
//			item.setActualAmountCount(item.getWonLeadCount());
//			summary.add(item);
//		}
//
//		return summary;
//	}

}
