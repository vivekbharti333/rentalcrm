package com.datfusrental.object.request;

import java.util.Date;

import lombok.Data;

@Data
public class OtpRequestObject {
	
	private Long id;
	private String token;
	private String customerMobile;
	private String customerEmailId;
	private String status;
	private Date updatedAt;
	private String otp;
	private int otpCount;
	
	private String requestedFor;
	
	private int respCode;
	private String respMesg;
	

}
