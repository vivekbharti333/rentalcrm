package com.datfusrental.object.request;

import java.time.LocalTime;
import java.util.Date;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestObject {
	
	private Long id;
	private Long categoryTypeId;
	private String categoryTypeImage;
	private String categoryTypeName;
	private String isChecked;
	private Long superCategoryId;
	private String superCategoryImage;
	private String superCategory;
	private Long categoryId;
	private String categoryImage;
	private String category;
	private String subCategoryImage;
	private Long subCategoryId;
	private String subCategory;
	
	private long securityAmount;
	private long vendorRate;
	private long vendorRateForKids;
	
	@JsonFormat(pattern = "HH:mm")
	private LocalTime startTime;
	
	@JsonFormat(pattern = "HH:mm")
	private LocalTime endTime; 
	
	private String pickupLocation;
	private String dropLocation;
	
	private String status;
	
	private String location;
	private String locationType;
	
	private String requestedFor;
	private String token;
	private String superadminId;
	private Date createdAt;
	private Date updatedAt;
	private String createdBy;
	private String loginId;
	
	private int respCode;
	private String respMesg;
	
}
