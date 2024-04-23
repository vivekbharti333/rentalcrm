package com.datfusrental.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;


@Entity
@Table(name = "application_header_details")
public class ApplicationHeaderDetails {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "ip_address")
	private String ipAddress;
	
	@Lob
	@Column(name = "login_page_wallpaper")
	private String loginPageWallpaper;
	
	@Lob
	@Column(name = "login_page_logo")
	private String loginPageLogo;
	
	@Lob
	@Column(name = "display_logo")
	private String displayLogo;
	
	@Column(name = "display_name")
	private String displayName;
	
	@Column(name = "email_id")
	private String emailId;
	
	@Column(name = "website")
	private String website;
	
	@Column(name = "phone_number")
	private String phoneNumber;
	
	@Column(name = "superadmin_id")
	private String superadminId;

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

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
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

}
