package com.datfusrental.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "invoice_header_details")
public class InvoiceHeaderDetails {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "invoice_initial")
	private String invoiceInitial;
	
	@Column(name = "invoice_number")
	private int invoiceNumber;
	
	@Column(name = "company_name")
	private String companyName;
	
	@Column(name = "company_address")
	private String companyAddress;
	
	@Column(name = "city")
	private String city;
	
	@Column(name = "pin")
	private String pin;
	
	@Column(name = "phone_number")
	private String phoneNumber;
	
	@Column(name = "email_id")
	private String emailId;
	
	@Column(name = "website")
	private String website;
	
	@Column(name = "status")
	private String status;
	
	@Lob
	@Column(name = "company_logo")
	private String companyLogo;

	@Column(name = "superadmin_id")
	private String superadminId;


}
