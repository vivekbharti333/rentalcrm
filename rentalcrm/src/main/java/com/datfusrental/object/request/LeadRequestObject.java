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
	private String categoryTypeName;
	private Long superCategoryId;
	private String superCategory;
	private Long categoryId;
	private String category;
	private String subCategory;
	private String itemName;
	private Date pickupDateTime;
	private String pickupLocation;
	private String pickupPoint;
	private Date dropDateTime;
	private String dropLocation;
	private String dropPoint;
	private String customeName;
	private String countryDialCode;
	private String customerMobile;
	private String customerEmailId;
	private int totalDays;
	private int quantity;
	private int childrenQuantity;
	private int infantQuantity;
	private long vendorRate;
	private long payToVendor;
	private long companyRate;
	private long payToCompany;
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
	private String remarks;
	private Date nextFollowupDate;
	

	private String superadminId;
	private String adminId;
	private String teamleaderId;
	private String createdBy;
	private String createdByName;
	private String updatedBy;
	private String status;
	private String leadOrigine;
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
