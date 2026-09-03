package com.datfusrental.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "customer_pickup_details")
@Data
public class CustomerPickupDetails {
		
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "booking_id")
	private String bookingId;
	
	@Column(name = "payment_image")
	private String paymentImage;
	
	@Column(name = "vehicle_video")
	private String vehicleVideo;
	
	@Column(name = "front_image")
	private String frantImage;
	
	@Column(name = "back_image")
	private String backImage;
	
	@Column(name = "left_image")
	private String leftImage;
	
	@Column(name = "right_image")
	private String rightImage;
	
	@Column(name = "fuel_gauge_image")
	private String fuelGaugeImage;
	
	@Column(name = "balance_amount_pay_to")
	private String balanceAmountPayTo;
	
	@Column(name = "balance_amount_pay_mode")
	private String balanceAmountPayMode;
	
	@Column(name = "balance_amount_pay_image")
	private String balanceAmountPayImage;
	
	@Column(name = "security_amount_image")
	private String securityAmountImage;
	
	@Lob
	@Column(name = "notes")
	private String notes;
	
	
	

	
}
