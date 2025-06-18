package com.datfusrental.entities;

import java.time.LocalTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Entity
@Table(name = "category_details")
@Data
public class CategoryDetails {
		
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "category_type_id")
	private Long categoryTypeId;
	
	@Column(name = "super_category_id")
	private Long superCategoryId;
	
	@Column(name = "category_image")
	private String categoryImage;
	
	@Column(name = "category")
	private String category;
	
	@Column(name = "security_amount")
	private long securityAmount;
	
	@Column(name = "vendor_rate")
	private long vendorRate;
	
	@Column(name = "vendor_rate_for_kids")
	private long vendorRateForKids;
	
	@Column(name = "company_rate")
	private long companyRate;
	
	@Column(name = "company_rate_for_kids")
	private long companyRateForKids;
	
	@Column(name = "start_date")
	private Date startDate; 
	
	@Column(name = "end_date")
	private Date endDate; 
	
	@JsonFormat(pattern = "HH:mm")
	@Column(name = "start_time")
	private LocalTime startTime; 
	
	@JsonFormat(pattern = "HH:mm")
	@Column(name = "end_time")
	private LocalTime  endTime; 
	
	@Column(name = "quantity")
	private int quantity;
	
	@Column(name = "kid_quantity")
	private int kidQuantity;
	
	@Column(name = "infant_quantity")
	private int infantQuantity;
	
	@Lob
	@Column(name = "description")
	private String description;
	
	@Column(name = "pickup_hub")
	private String pickupHub;
	
	@Column(name = "drop_hub")
	private String dropHub;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "superadmin_id")
	private String superadminId;
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_At")
	private Date updatedAt;
}
