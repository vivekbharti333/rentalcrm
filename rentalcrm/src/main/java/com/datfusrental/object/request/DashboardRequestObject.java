package com.datfusrental.object.request;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class DashboardRequestObject {

	private Long id;
	private String token;

	private Long totalNoOfUser;
	private Long totalTodayOfTeam;
	private Long totalTodayWonOfTeam;
	private Long totalTodayOfIndividual;
	private Long totalTodayWonOfIndividual;

	private String roleType;
	private String status;

	@JsonAlias("requestFor")
	private String requestedFor;
	private String loginId;
	private String createdBy;
	private String teamleaderId;
	private String adminId;
	private String superadminId;

	private int respCode;
	private String respMesg;
}
