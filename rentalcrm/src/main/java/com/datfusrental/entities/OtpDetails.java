package com.datfusrental.entities;




import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "otp_details")
@Data
public class OtpDetails {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "customer_mobile")
	private String customerMobile;
	
	@Column(name = "customer_email_id")
	private String customerEmailId;
	
	@Column(name = "otp")
	private String otp;
	
	@Column(name = "otp_count")
	private int otpCount;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_at")
	private Date updatedAt;
}
