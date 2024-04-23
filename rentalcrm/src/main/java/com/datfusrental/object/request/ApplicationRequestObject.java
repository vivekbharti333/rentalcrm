package com.datfusrental.object.request;

import lombok.Data;

@Data
public class ApplicationRequestObject {
	
	private Long id;
	private String status;
	private String loginPageWallpaper;
	private String loginPageLogo;
	private String ipAddress;
	private String displayLogo;
	private String displayName;
	private String emailId;
	private String website;
	private String phoneNumber;
	private String superadminId;
	
	private int respCode;
	private String respMesg;
	
}
