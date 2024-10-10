package com.datfusrental.object.request;

import javax.persistence.Column;
import javax.persistence.Lob;

import lombok.Data;

@Data
public class InvoiceHeaderRequestObject {
	
	private Long id;
	private String token;
	private String invoiceInitial;
	private int invoiceNumber;
	private String companyName;
	private String companyAddress;
	private String city;
	private String pin;
	private String phoneNumber;
	private String emailId;
	private String website;
	private String status;
	private String notes;
	private String termCondition;
	private String companyLogo;
	private String superadminId;

	private int respCode;
	private String respMesg;
	

}
