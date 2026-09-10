package com.datfusrental.helper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

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
				.createQuery("SELECT COUNT(LD), SUM(CASE WHEN LD.status = :status THEN 1 ELSE 0 END) "
						+ "FROM LeadDetails LD WHERE " + ownerFilter
						+ " AND LD.createdAt >= :startDate AND LD.createdAt < :endDate", Object[].class)
				.setParameter("status", Status.WON.name())
				.setParameter("ownerId", team ? dashboardRequest.getSuperadminId() : dashboardRequest.getCreatedBy())
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate)
				.getSingleResult();
		Long total = ((Number) counts[0]).longValue();
		Long won = counts[1] == null ? 0L : ((Number) counts[1]).longValue();
		dashboardRequest.setTotalTodayOfTeam(team ? total : null);
		dashboardRequest.setTotalTodayWonOfTeam(team ? won : null);
		dashboardRequest.setTotalTodayOfIndividual(team ? null : total);
		dashboardRequest.setTotalTodayWonOfIndividual(team ? null : won);
	}
}
