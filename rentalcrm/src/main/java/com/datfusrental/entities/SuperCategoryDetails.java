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
@Table(name = "super_category_details")
@Data
public class SuperCategoryDetails {
		
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "category_type_id")
	private Long categoryTypeId;
	
	@Column(name = "super_category_image")
	private String superCategoryImage;
	
	@Column(name = "super_category")
	private String superCategory;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "superadmin_id")
	private String superadminId;
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_At")
	private Date updatedAt;
}
