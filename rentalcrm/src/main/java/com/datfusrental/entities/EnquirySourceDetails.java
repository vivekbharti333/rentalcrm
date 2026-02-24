package com.datfusrental.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "enquiry_source_details")
@Data
public class EnquirySourceDetails {
		
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "enquiry_source")
	private String enquirySource;
	
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "superadmin_id")
	private String superadminId;
	
}
