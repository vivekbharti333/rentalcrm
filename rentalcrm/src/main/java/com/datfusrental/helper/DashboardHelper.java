package com.datfusrental.helper;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
		populateKpiCounts(dashboardRequest, team, startDate, endDate);
	}
	
	public List<DashboardRequestObject> saleVsTarget(DashboardRequestObject dashboardRequest) {
		ZoneId zone = ZoneId.of("Asia/Kolkata");
		LocalDate today = LocalDate.now(zone);
		LocalDateTime now = LocalDateTime.now(zone);

		LocalDate dayStart = today;
		LocalDate weekStart = today.with(DayOfWeek.MONDAY);
		LocalDate biweeklyStart = weekStart.minusDays(7);
		LocalDate monthStart = today.withDayOfMonth(1);

		boolean team = isSuperadmin(dashboardRequest);
		String ownerFilter = team ? "LD.superadminId = :ownerId" : "LD.createdBy = :ownerId";
		List<String> wonStatuses = List.of(Status.WON.name(), "ASSIGNED");

		// Sales collected (min of actual/booking per won lead) for each period,
		// one query, conditional sums.
		Object[] actuals = leadDetailsDao.getEntityManager()
				.createQuery("SELECT "
						+ "SUM(CASE WHEN LD.changeStatusDate >= :dayStart THEN CASE WHEN LD.actualAmount < LD.bookingAmount THEN LD.actualAmount ELSE LD.bookingAmount END ELSE 0 END), "
						+ "SUM(CASE WHEN LD.changeStatusDate >= :weekStart THEN CASE WHEN LD.actualAmount < LD.bookingAmount THEN LD.actualAmount ELSE LD.bookingAmount END ELSE 0 END), "
						+ "SUM(CASE WHEN LD.changeStatusDate >= :biweeklyStart THEN CASE WHEN LD.actualAmount < LD.bookingAmount THEN LD.actualAmount ELSE LD.bookingAmount END ELSE 0 END), "
						+ "SUM(CASE WHEN LD.changeStatusDate >= :monthStart THEN CASE WHEN LD.actualAmount < LD.bookingAmount THEN LD.actualAmount ELSE LD.bookingAmount END ELSE 0 END) "
						+ "FROM LeadDetails LD WHERE LD.status IN :wonStatuses AND " + ownerFilter + " "
						+ "AND LD.changeStatusDate >= :biweeklyStart AND LD.changeStatusDate < :dayEnd", Object[].class)
				.setParameter("ownerId", team ? dashboardRequest.getSuperadminId() : dashboardRequest.getCreatedBy())
				.setParameter("wonStatuses", wonStatuses)
				.setParameter("dayStart", atStartOfDay(dayStart, zone))
				.setParameter("weekStart", atStartOfDay(weekStart, zone))
				.setParameter("biweeklyStart", atStartOfDay(biweeklyStart, zone))
				.setParameter("monthStart", atStartOfDay(monthStart, zone))
				.setParameter("dayEnd", atStartOfDay(dayStart.plusDays(1), zone))
				.getSingleResult();

		List<DashboardRequestObject> targets = new ArrayList<>();
		targets.add(buildTargetCard("Today", "Daily target", nz(actuals[0]), nz(dashboardRequest.getDailyTarget()),
				dayStart.atStartOfDay(), dayStart.plusDays(1).atStartOfDay(), now));
		targets.add(buildTargetCard("This Week", "Weekly target", nz(actuals[1]), nz(dashboardRequest.getWeeklyTarget()),
				weekStart.atStartOfDay(), weekStart.plusDays(7).atStartOfDay(), now));
		targets.add(buildTargetCard("Bi-weekly", "Biweekly target", nz(actuals[2]), nz(dashboardRequest.getBiweeklyTarget()),
				biweeklyStart.atStartOfDay(), weekStart.plusDays(7).atStartOfDay(), now));
		targets.add(buildTargetCard("This Month", "Monthly target", nz(actuals[3]), nz(dashboardRequest.getMonthlyTarget()),
				monthStart.atStartOfDay(), monthStart.plusMonths(1).atStartOfDay(), now));
		return targets;
	}

	private DashboardRequestObject buildTargetCard(String label, String periodLabel, long actual, long target,
			LocalDateTime periodStart, LocalDateTime periodEnd, LocalDateTime now) {
		DashboardRequestObject card = new DashboardRequestObject();
		double fraction = elapsedFraction(periodStart, periodEnd, now);
		long gap = actual - target;
		long requiredDaily = 0L;
		if (gap < 0) {
			double remainingDays = Math.max(1, Math.ceil(daysBetween(now, periodEnd)));
			requiredDaily = (long) Math.ceil(-gap / remainingDays);
		}
		long projected = fraction > 0 ? Math.round(actual / fraction) : actual;

		card.setTargetLabel(label);
		card.setTargetPeriodLabel(periodLabel);
		card.setTargetActualAmount(actual);
		card.setTargetAmount(target);
		card.setTargetAchievementPct(pct(actual, target));
		card.setTargetPacePct(round2(fraction * 100));
		card.setTargetGap(gap);
		card.setTargetRequiredDaily(requiredDaily);
		card.setTargetProjected(projected);
		return card;
	}

	private Date atStartOfDay(LocalDate date, ZoneId zone) {
		return Date.from(date.atStartOfDay(zone).toInstant());
	}

	private double elapsedFraction(LocalDateTime periodStart, LocalDateTime periodEnd, LocalDateTime now) {
		double total = Duration.between(periodStart, periodEnd).toMillis();
		if (total <= 0) {
			return 1;
		}
		double elapsed = Duration.between(periodStart, now).toMillis();
		return Math.max(0, Math.min(1, elapsed / total));
	}

	private double daysBetween(LocalDateTime from, LocalDateTime to) {
		return Duration.between(from, to).toMillis() / 86400000.0;
	}

	// KPI strip values for the dashboard cards (Leads Received, Conversions,
	// Sales Collected, Same-Day Requirement, Follow-up Pool, Lost / Disposed).
	private void populateKpiCounts(DashboardRequestObject dashboardRequest, boolean team, Date startDate, Date endDate) {
		ZoneId zone = ZoneId.of("Asia/Kolkata");
		Date now = new Date();
		String ownerFilter = team ? "LD.superadminId = :ownerId" : "LD.createdBy = :ownerId";
		List<String> wonStatuses = List.of(Status.WON.name(), "ASSIGNED");
		List<String> closedStatuses = List.of(Status.WON.name(), "ASSIGNED", "LOST");

		Object[] kpi = leadDetailsDao.getEntityManager()
				.createQuery("SELECT "
						+ "SUM(CASE WHEN LD.createdAt >= :startDate AND LD.createdAt < :endDate THEN 1 ELSE 0 END), "
						+ "SUM(CASE WHEN LD.status IN :wonStatuses AND LD.changeStatusDate >= :startDate AND LD.changeStatusDate < :endDate THEN 1 ELSE 0 END), "
						+ "SUM(CASE WHEN LD.status IN :wonStatuses AND LD.changeStatusDate >= :startDate AND LD.changeStatusDate < :endDate "
						+ "     THEN CASE WHEN LD.actualAmount < LD.bookingAmount THEN LD.actualAmount ELSE LD.bookingAmount END ELSE 0 END), "
						+ "SUM(CASE WHEN LD.status IN :wonStatuses AND LD.changeStatusDate >= :startDate AND LD.changeStatusDate < :endDate THEN LD.bookingAmount ELSE 0 END), "
						+ "SUM(CASE WHEN LD.createdAt >= :startDate AND LD.createdAt < :endDate "
						+ "     AND LD.pickupDateTime >= :startDate AND LD.pickupDateTime < :endDate THEN 1 ELSE 0 END), "
						+ "SUM(CASE WHEN LD.createdAt >= :startDate AND LD.createdAt < :endDate "
						+ "     AND LD.pickupDateTime >= :startDate AND LD.pickupDateTime < :endDate AND LD.status IN :wonStatuses THEN 1 ELSE 0 END), "
						+ "SUM(CASE WHEN LD.status IN :wonStatuses AND LD.changeStatusDate >= :startDate AND LD.changeStatusDate < :endDate "
						+ "     AND LD.nextFollowupDate IS NOT NULL THEN 1 ELSE 0 END), "
						+ "SUM(CASE WHEN LD.status NOT IN :closedStatuses AND LD.nextFollowupDate IS NOT NULL THEN 1 ELSE 0 END), "
						+ "SUM(CASE WHEN LD.status NOT IN :closedStatuses AND LD.nextFollowupDate IS NOT NULL AND LD.nextFollowupDate < :now THEN 1 ELSE 0 END), "
						+ "SUM(CASE WHEN LD.status = 'LOST' AND LD.changeStatusDate >= :startDate AND LD.changeStatusDate < :endDate THEN 1 ELSE 0 END), "
						+ "SUM(CASE WHEN LD.status NOT IN :closedStatuses AND LD.nextFollowupDate IS NULL THEN 1 ELSE 0 END) "
						+ "FROM LeadDetails LD WHERE " + ownerFilter + " "
						+ "AND ((LD.createdAt >= :startDate AND LD.createdAt < :endDate) "
						+ "     OR (LD.status IN :wonStatuses AND LD.changeStatusDate >= :startDate AND LD.changeStatusDate < :endDate) "
						+ "     OR (LD.status = 'LOST' AND LD.changeStatusDate >= :startDate AND LD.changeStatusDate < :endDate) "
						+ "     OR (LD.status NOT IN :closedStatuses))", Object[].class)
				.setParameter("ownerId", team ? dashboardRequest.getSuperadminId() : dashboardRequest.getCreatedBy())
				.setParameter("wonStatuses", wonStatuses)
				.setParameter("closedStatuses", closedStatuses)
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate)
				.setParameter("now", now)
				.getSingleResult();

		long leadsReceivedToday = nz(kpi[0]);
		long wonCountToday = nz(kpi[1]);
		long salesAmountToday = nz(kpi[2]);
		long salesBookingAmountToday = nz(kpi[3]);
		long sameDayLeadsToday = nz(kpi[4]);
		long sameDayWonToday = nz(kpi[5]);
		long followupWonToday = nz(kpi[6]);
		long followupPool = nz(kpi[7]);
		long overdueFollowups = nz(kpi[8]);
		long lostCountToday = nz(kpi[9]);
		long untouchedLeads = nz(kpi[10]);

		dashboardRequest.setLeadsReceivedToday(leadsReceivedToday);
		dashboardRequest.setWonCountToday(wonCountToday);
		dashboardRequest.setSalesAmountToday(salesAmountToday);
		dashboardRequest.setSalesBookingAmountToday(salesBookingAmountToday);
		dashboardRequest.setSameDayLeadsToday(sameDayLeadsToday);
		dashboardRequest.setSameDayWonToday(sameDayWonToday);
		dashboardRequest.setFollowupWonToday(followupWonToday);
		dashboardRequest.setFollowupPool(followupPool);
		dashboardRequest.setOverdueFollowups(overdueFollowups);
		dashboardRequest.setLostCountToday(lostCountToday);
		dashboardRequest.setUntouchedLeads(untouchedLeads);
		dashboardRequest.setCohortConvPct(pct(wonCountToday, leadsReceivedToday));
		dashboardRequest.setSameDayConvPct(pct(sameDayWonToday, sameDayLeadsToday));
		dashboardRequest.setLostRatePct(pct(lostCountToday, lostCountToday + wonCountToday));
		dashboardRequest.setRevenuePerLead(round2(leadsReceivedToday == 0L ? 0D
				: salesAmountToday * 1.0 / leadsReceivedToday));
		dashboardRequest.setAvgBookingValue(round2(wonCountToday == 0L ? 0D
				: salesBookingAmountToday * 1.0 / wonCountToday));
	}

	private long nz(Object value) {
		return value == null ? 0L : ((Number) value).longValue();
	}

	private double pct(long part, long whole) {
		return whole == 0L ? 0D : Math.round(part * 10000.0 / whole) / 100.0;
	}

	private double round2(double value) {
		return Math.round(value * 100.0) / 100.0;
	}

	public List<DashboardRequestObject> getTodayWonSummaryByCreatedBy(DashboardRequestObject dashboardRequest)
			throws BizException {

		ZoneId zone = ZoneId.of("Asia/Kolkata");
		LocalDate today = LocalDate.now(zone);
		Date startDate = Date.from(today.atStartOfDay(zone).toInstant());
		Date endDate = Date.from(today.plusDays(1).atStartOfDay(zone).toInstant());
		Date now = new Date();
		boolean allData = dashboardRequest != null && Boolean.TRUE.equals(dashboardRequest.getAllData());
		String roleFilter = allData ? "" : "AND U.roleType IN :roleTypes ";
		List<String> wonStatuses = List.of(Status.WON.name(), "ASSIGNED");
		List<String> closedStatuses = List.of(Status.WON.name(), "ASSIGNED", "LOST");

		// Single grouped query returning every Agent Leaderboard metric per agent:
		// won today, sales collected (min of actual/booking per won lead), leads
		// received today, same-day leads/won, follow-up pool and overdue follow-ups.
		TypedQuery<Object[]> summaryQuery = leadDetailsDao.getEntityManager()
				.createQuery("SELECT LD.createdBy, U.firstName, U.lastName, U.roleType, U.pseudoName, "
						+ "SUM(CASE WHEN LD.status IN :wonStatuses AND LD.changeStatusDate >= :startDate AND LD.changeStatusDate < :endDate THEN 1 ELSE 0 END), "
						+ "SUM(CASE WHEN LD.status IN :wonStatuses AND LD.changeStatusDate >= :startDate AND LD.changeStatusDate < :endDate "
						+ "     THEN CASE WHEN LD.actualAmount < LD.bookingAmount THEN LD.actualAmount ELSE LD.bookingAmount END ELSE 0 END), "
						+ "SUM(CASE WHEN LD.createdAt >= :startDate AND LD.createdAt < :endDate THEN 1 ELSE 0 END), "
						+ "SUM(CASE WHEN LD.createdAt >= :startDate AND LD.createdAt < :endDate "
						+ "     AND LD.pickupDateTime >= :startDate AND LD.pickupDateTime < :endDate THEN 1 ELSE 0 END), "
						+ "SUM(CASE WHEN LD.createdAt >= :startDate AND LD.createdAt < :endDate "
						+ "     AND LD.pickupDateTime >= :startDate AND LD.pickupDateTime < :endDate AND LD.status IN :wonStatuses THEN 1 ELSE 0 END), "
						+ "SUM(CASE WHEN LD.status NOT IN :closedStatuses AND LD.nextFollowupDate IS NOT NULL THEN 1 ELSE 0 END), "
						+ "SUM(CASE WHEN LD.status NOT IN :closedStatuses AND LD.nextFollowupDate IS NOT NULL AND LD.nextFollowupDate < :now THEN 1 ELSE 0 END) "
						+ "FROM LeadDetails LD, User U "
						+ "WHERE LD.createdBy = U.loginId "
						+ roleFilter
						+ "AND ((LD.createdAt >= :startDate AND LD.createdAt < :endDate) "
						+ "     OR (LD.status IN :wonStatuses AND LD.changeStatusDate >= :startDate AND LD.changeStatusDate < :endDate) "
						+ "     OR (LD.status NOT IN :closedStatuses AND LD.nextFollowupDate IS NOT NULL)) "
						+ "GROUP BY LD.createdBy, U.firstName, U.lastName, U.roleType, U.pseudoName "
						+ "ORDER BY SUM(CASE WHEN LD.status IN :wonStatuses AND LD.changeStatusDate >= :startDate AND LD.changeStatusDate < :endDate THEN 1 ELSE 0 END) DESC, U.firstName ASC, U.lastName ASC",
						Object[].class)
				.setParameter("wonStatuses", wonStatuses)
				.setParameter("closedStatuses", closedStatuses)
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate)
				.setParameter("now", now);
		if (!allData) {
			summaryQuery.setParameter("roleTypes", List.of("SALE_EXECUTIVE", "SALE EXECUTIVE"));
		}
		List<Object[]> groupedResults = summaryQuery.getResultList();

		List<DashboardRequestObject> summary = new ArrayList<>();
		for (Object[] row : groupedResults) {
			DashboardRequestObject item = new DashboardRequestObject();
			String firstName = row[1] == null ? "" : row[1].toString().trim();
			String lastName = row[2] == null ? "" : row[2].toString().trim();
			long wonCount = ((Number) row[5]).longValue();
			long leadsReceived = ((Number) row[7]).longValue();
			item.setCreatedBy((String) row[0]);
			item.setCreatedByName((firstName + " " + lastName).trim());
			item.setRoleType((String) row[3]);
			item.setPseudoName((String) row[4]);
			// Existing fields kept for clients already using them.
			item.setWonLeadCount(wonCount);
			item.setTotalActualAmount(row[6] == null ? 0L : ((Number) row[6]).longValue());
			item.setActualAmountCount(wonCount);
			// Agent Leaderboard fields.
			item.setWonCount(wonCount);
			item.setSalesAmount(item.getTotalActualAmount());
			item.setLeadsReceived(leadsReceived);
			item.setCohortConvPct(leadsReceived == 0L ? 0D
					: Math.round((wonCount * 10000.0 / leadsReceived)) / 100.0);
			item.setSameDayLeads(((Number) row[8]).longValue());
			item.setSameDayWon(((Number) row[9]).longValue());
			item.setFollowupPool(((Number) row[10]).longValue());
			item.setOverdueFollowups(((Number) row[11]).longValue());
			summary.add(item);
		}

		return summary;
	}



}
