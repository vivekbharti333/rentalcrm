package com.datfusrental.object.request;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import lombok.Data;

@Data
public class LeadRequestObject {
	
	private Long id;
	private String token;
	
	private Date createdAt;
	private Date passwordUpdatedAt;
	private Date updatedAt;
	private String createdBy;
	private String teamleaderId;
	private String adminId;
	private String superadminId;
	
	private String requestedFor;
	private String searchParam;
	
	private int respCode;
	private String respMesg;
	

}
