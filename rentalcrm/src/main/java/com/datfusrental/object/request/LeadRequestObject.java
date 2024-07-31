package com.datfusrental.object.request;

import java.util.Date;

import javax.persistence.Column;

import lombok.Data;

@Data
public class LeadRequestObject {
	
	private Long id;
	private String token;
	private String bookingId;
	private String companyName;
	private String enquirySource;
	private Long superCategoryId;
	private String superCategory;
	private Long categoryId;
	private String category;
	private String subCategory;
	private String itemName;
	private Date pickupDateTime;
	private String pickupLocation;
	private Date dropDateTime;
	private String dropLocation;
	private String customeName;
	private String countryDialCode;
	private String customerMobile;
	private String customerEmailId;
	private int quantity;
	private long vendorAmount;
	private long sellAmount;
	private long bookingAmount;
	private long balanceAmount;
	private long totalAmount;
	private long securityAmount;
	
	private long amountToCompany;
	private long amountToVendor;
	private long deliveryAmountToCompany;
	private long deliveryAmountToVendor;
	
	private String vendorName;
	private String notes;
	private Date followupDateTime;
	
	private String createdBy;
	private String superadminId;
	private String status;
	private String leadType;
	private Date createdAt;
	private Date updatedAt;
	
	private String requestedFor;
	private Date firstDate;
	private Date lastDate;
	private String roleType;
	
	private int respCode;
	private String respMesg;
	

}
