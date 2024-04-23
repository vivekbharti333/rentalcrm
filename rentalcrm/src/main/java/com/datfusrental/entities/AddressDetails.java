package com.datfusrental.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "address_details")
public class AddressDetails {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name  = "user_id", nullable = false)
	private Long userId;
	
	@Column(name = "user_type", nullable = false)
	private String userType;
	
	@Column(name = "address_type", nullable = false)
	private String addressType;
	
	@Column(name = "address_line")
	private String addressLine;
	
	@Column(name = "landmark")
	private String landmark;
	
	@Column(name = "district")
	private String district;
	
	@Column(name = "city")
	private String city;
	
	@Column(name = "state")
	private String state;
	
	@Column(name = "country")
	private String country;
	
	@Column(name = "pincode")
	private String pincode;
	
	@Column(name = "superadmin_id", nullable = false)
	private String superadminId;
	

	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_at")
	private Date updatedAt;
	
}
