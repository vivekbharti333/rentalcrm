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
@Table(name = "email_details")
public class EmailDetails {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "invoice_head_id")
	private Long invoiceHeadId;

	@Column(name = "servive_provider")
	private String serviceProvider;

	@Column(name = "from_email")
	private String fromEmail;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "api_key")
	private String apiKey;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "superadmin_id")
	private String superadminId;


}
