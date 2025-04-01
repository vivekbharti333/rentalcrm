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
@Table(name = "sub_category_details")
@Data
public class SubCategoryDetails {
		
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "category_type_id")
	private Long categoryTypeId;
	
	@Column(name = "super_category_id")
	private Long superCategoryId;
	
	@Column(name = "category_id")
	private Long categoryId;
	
	@Column(name = "sub_category_image")
	private String subCategoryImage;
	
	@Column(name = "sub_category")
	private String subCategory;
	
	@Column(name = "security_amount")
	private long securityAmount;
	
	@Column(name = "vendor_rate")
	private long vendorRate;
	
	@Column(name = "vendor_rate_for_kids")
	private long vendorRateForKids;
	
	@JsonFormat(pattern = "HH:mm")
	@Column(name = "start_time")
	private LocalTime startTime; 
	
	@JsonFormat(pattern = "HH:mm")
	@Column(name = "end_time")
	private LocalTime endTime; 
	
	@Lob
	@Column(name = "description")
	private String description;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "superadmin_id")
	private String superadminId;
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_At")
	private Date updatedAt;
}
