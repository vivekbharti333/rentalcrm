package com.datfusrental.object.request;

import java.util.Date;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;

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
	
	private Date pickupDate;
	private Date pickupTime;
	private String pickupHub;
	private String pickupPoint;
	
	private String activityLocation;
	private String otherPickLocation;
	private String otherDropLocation;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Kolkata")
	private Date pickupDateTime;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Kolkata")
	private Date dropDateTime;
	
//	private Date dropDate;
//	private Date dropTime;
	
	private String dropHub;
	private String dropPoint;
	private String customeName;
	private String countryDialCode;
	private String customerMobile;
	private String alternateMobile;
	private String customerEmailId;
	private int totalDays;
	private int quantity;
	private int kidQuantity;
	private int infantQuantity;
	private long vendorRate;
	private long vendorRateForKids;
	private long payToVendor;
	private long companyRate;
	private long companyRateForKids;
	private long payToCompany;
	private long bookingAmount;
	private long balanceAmount;
	private long totalAmount;
	private long securityAmount;
	private long actualAmount;

	private String paymentType;
	
	private String discountType;
	private long discount;
	
	private long amountToCompany;
	private long amountToVendor;
	private long deliveryAmountToCompany;
	private long deliveryAmountToVendor;
	
	private String vendorName;
	private String notes;
	private Date followupDateTime;
	private String remarks;
	private Date nextFollowupDate;
	
	private String enquirySource;
	private String superadminId;
	private String adminId;
	private String teamleaderId;
	private String pseudoName;
	private String createdBy;
	private String createdByName;
	private String loginId;
	private String updatedBy;
	private String status;
	private String secondStatus;
	private String leadOrigine;
	private String leadType;
	
//	private Date followupDate;

	private Date createdAt;
	private Date updatedAt;
	private Date changeStatusDate;
	
	private String requestedFor;
	private Date firstDate;
	private Date lastDate;
	private String roleType;
	
	private String paymentUrl;
	private String pgUrl;
	private String pgRespUrl;
	private String otp;
	private int otpCount;
	
	private String pgResponseBody;
	
	private int respCode;
	private String respMesg;
	

}
