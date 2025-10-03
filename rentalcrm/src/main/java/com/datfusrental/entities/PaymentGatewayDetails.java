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
@Table(name = "payment_gateways_details")
@Data
public class PaymentGatewayDetails {
		
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "payment_gateways_name")
	private String paymentGatewaysName;
	
	@Column(name = "client_id")
	private String clientId;
	
	@Column(name = "security_key")
	private String securityKey;
	
	@Column(name = "salt")
	private String salt;
	
	@Column(name = "version")
	private String version;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_At")
	private Date updatedAt;
	
	
}
