package com.datfusrental.object.request;

import javax.persistence.Column;

import lombok.Data;

@Data
public class DashboardRequestObject {

	private Long id;
	private String token;
	
	private Long totalNoOfUser;
	
	private String roleType;
	private String status;

	private String requestedFor;
	private String loginId;
	private String createdBy;
	private String teamleaderId;
	private String adminId;
	private String superadminId;

	private int respCode;
	private String respMesg;
}
