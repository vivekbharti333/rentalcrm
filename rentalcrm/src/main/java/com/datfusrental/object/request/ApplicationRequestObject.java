package com.datfusrental.object.request;

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
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getLoginPageWallpaper() {
		return loginPageWallpaper;
	}
	public void setLoginPageWallpaper(String loginPageWallpaper) {
		this.loginPageWallpaper = loginPageWallpaper;
	}
	public String getLoginPageLogo() {
		return loginPageLogo;
	}
	public void setLoginPageLogo(String loginPageLogo) {
		this.loginPageLogo = loginPageLogo;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getDisplayLogo() {
		return displayLogo;
	}
	public void setDisplayLogo(String displayLogo) {
		this.displayLogo = displayLogo;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getSuperadminId() {
		return superadminId;
	}
	public void setSuperadminId(String superadminId) {
		this.superadminId = superadminId;
	}
	public int getRespCode() {
		return respCode;
	}
	public void setRespCode(int respCode) {
		this.respCode = respCode;
	}
	public String getRespMesg() {
		return respMesg;
	}
	public void setRespMesg(String respMesg) {
		this.respMesg = respMesg;
	}
		
}
