package com.datfusrental.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "customer_tickets_details")
public class CustomerTicketsDetails {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "booking_id")
	private String bookingId;
	
	@Column(name = "custome_name")
	private String customeName;
	
	@Column(name = "customer_mobile")
	private String customerMobile;
	
	@Column(name = "category")
	private String category;
	
	@Column(name = "sub_category")
	private String subCategory;
	
	@Column(name = "notes")
	private String notes;
	
	@Column(name = "status")
	private String status;


}
