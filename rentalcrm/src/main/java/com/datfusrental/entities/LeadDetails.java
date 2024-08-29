package com.datfusrental.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "lead_details")
@Data
public class LeadDetails {
		
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "booking_id")
	private String bookingId;
	
	@Column(name = "company_name")
	private String companyName;
	
	@Column(name = "enquiry_source")
	private String enquirySource;
	
	@Column(name = "category_type_name")
	private String categoryTypeName;
	
	@Column(name = "super_category")
	private String superCategory;
	
	@Column(name = "category")
	private String category;
	
	@Column(name = "sub_category")
	private String subCategory;
	
	@Column(name = "item_name")
	private String itemName;
	
	@Column(name = "pickup_date_time")
	private Date pickupDateTime;
	
	@Column(name = "pickup_location")
	private String pickupLocation;
	
	@Column(name = "drop_date_time")
	private Date dropDateTime;
	
	@Column(name = "drop_location")
	private String dropLocation;
	
	@Column(name = "customeName")
	private String customeName;
	
	@Column(name = "country_dial_code")
	private String countryDialCode;
	
	@Column(name = "customer_mobile")
	private String customerMobile;
	
	@Column(name = "customer_email_id")
	private String customerEmailId;
	
	@Column(name = "total_days")
	private int totalDays;
	
	@Column(name = "quantity")
	private int quantity;
	
	@Column(name = "vendor_rate")
	private long vendorRate;
	
	@Column(name = "pay_to_vendor")
	private long payToVendor;
	
	@Column(name = "company_rate")
	private long companyRate;
	
	@Column(name = "pay_to_company")
	private long payToCompany;
	
	@Column(name = "booking_amount")
	private long bookingAmount;
	
	@Column(name = "balance_amount")
	private long balanceAmount;
	
	@Column(name = "total_amount")
	private long totalAmount;
	
	@Column(name = "security_amount")
	private long securityAmount;
	
	@Column(name = "delivery_amount_to_company")
	private long deliveryAmountToCompany;
	
	@Column(name = "delivery_amount_to_vendor")
	private long deliveryAmountToVendor;
	
	@Column(name = "vendor_name")
	private String vendorName;
	
	@Lob
	@Column(name = "remarks")
	private String remarks;
	
	@Column(name = "created_by")
	private String createdBy;
	
	@Column(name = "superadmin_id")
	private String superadminId;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "lead_type")
	private String leadType;
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_At")
	private Date updatedAt;
}
