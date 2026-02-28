package com.datfusrental.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;
import org.springframework.lang.NonNull;

import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Table(name = "vendor_details")
@Data
@NoArgsConstructor
public class VendorDetails {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "pseudo_name")
	private String pseudoName;
	
	@Column(name = "login_id")
	private String loginId;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;
	
	@Column(name = "gender")
	private String gender;
	
	@Column(name = "whatsapp_link")
	private String whatsappLink;
	
	@Lob
	@Column(name = "product_name")
	private String productName;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "role_type")
	private String roleType;
	
	@Column(name = "whatsapp_no")
	private String whatsappNo;
	
	@Column(name = "alternate_mobile")
	private String alternateMobile;
	
	@Column(name = "email_id")
	private String emailId;
	
	@Column(name = "vendor_state")
	private String vendorState;
	
	@Column(name = "vendor_region")
	private String vendorRegion;
	
	@Column(name = "vendor_city")
	private String vendorCity;
	
	@Column(name = "pan_card")
	private String panCard;
	
	@Column(name = "aadharCard")
	private String aadharCard;
	
	@Column(name = "gst")
	private String gst;
	
	@Column(name = "vendor_website_ink")
	private String vendorWebsiteLink;
	
	@Column(name = "account_details")
	private String accountDetails;
	
	@Column(name = "user_wallet_amount")
	private Long userWalletAmount;
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_At")
	private Date updatedAt;
	
	@Column(name = "created_by")
	private String createdBy;
	
	@Column(name = "superadmin_id")
	private String superadminId;

}
