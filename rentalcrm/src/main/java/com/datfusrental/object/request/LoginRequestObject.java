package com.datfusrental.object.request;

import lombok.Data;

@Data
public class LoginRequestObject {
	
	private Long id;
	private String token;
	private String userPicture;
	private String userCode;
	private String loginId;
	private String password;
	private String salt;
	private String roleType;
	private String firstName;
	private String lastName;
	private String service;
	private String permissions;

	private String pseudoName;
	private String createdBy;
	private String teamLeaderId;
	private String adminId;
	private String superadminId;
	
	private int respCode;
	private String respMesg;
	

}
