package com.datfusrental.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;

@Data
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


}
