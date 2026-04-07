package com.datfusrental.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Entity
@Table(name = "transaction_details")
@Data
public class TransactionDetails {
		
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "booking_id")
	private String bookingId;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "pickup_date_time")
	private Date pickupDateTime;
	
	@Column(name = "customer_mobile")
	private String customerMobile;
	
	@Column(name = "vendor_id")
	private Long vendorId;
	
	@Column(name = "vendor_name")
	private String vendorName;
	
	@Column(name = "company_amount")
	private long companyAmount;
	
	@Column(name = "company_trasaction_type")
	private String companyTransactionType;
	
	@Column(name = "vendor_amount")
	private long vendorAmount;
	
	@Column(name = "vendor_transaction_type")
	private String vendorTransactionType;
	
	@Column(name = "net_amount_paid")
	private long netAmountPaid;
	
	@Column(name ="created_at")
	private Date createdAt;
	
	
}
