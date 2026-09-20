package com.datfusrental.object.request;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class DashboardRequestObject {

	private Long id;
	private String token;

	private Long totalNoOfUser;
	private Long todayTotalWinCount;
	private Long todayTotalWonAmount;
	private String createdByName;
	private Long wonLeadCount;
	private Long totalActualAmount;
	private Long actualAmountCount;

	// Agent Leaderboard fields (per-agent row)
	private String pseudoName;
	private Long leadsReceived;
	private Long wonCount;
	private Double cohortConvPct;
	private Long sameDayLeads;
	private Long sameDayWon;
	private Long followupPool;
	private Long overdueFollowups;
	private Long salesAmount;

	// KPI strip fields (team level, dashCount)
	private Long leadsReceivedToday;
	private Long wonCountToday;
	private Long salesAmountToday;
	private Long salesBookingAmountToday;
	private Long sameDayLeadsToday;
	private Long sameDayWonToday;
	private Double sameDayConvPct;
	private Long followupWonToday;
	private Long lostCountToday;
	private Double lostRatePct;
	private Long untouchedLeads;
	private Double revenuePerLead;
	private Double avgBookingValue;

	// Sales vs Target card fields (one item per period: today/week/month/year)
	private String targetLabel;
	private String targetPeriodLabel;
	private Long targetActualAmount;
	private Long targetAmount;
	private Double targetAchievementPct;
	private Double targetPacePct;
	private Long targetGap;
	private Long targetRequiredDaily;
	private Long targetProjected;

	// Sales vs Target inputs (targets set by the client, optional)
	private Long dailyTarget;
	private Long weeklyTarget;
	private Long biweeklyTarget;
	private Long monthlyTarget;

	private String roleType;
	private String status;

	@JsonAlias("requestFor")
	private String requestedFor;
	private Boolean allData;
	private String loginId;
	private String createdBy;
	private String teamleaderId;
	private String adminId;
	private String superadminId;

	private int respCode;
	private String respMesg;
}
