package com.datfusrental.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "delivery_boy_details")
@Data
@NoArgsConstructor
public class DeliveryBoyDetails {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "delivery_boy_name")
	private String deliveryBoyName;
	
	@Column(name = "mobile_number")
	private String mobileNumber;
	
	@Column(name = "alternate_mobile_number")
	private String alternateMobileNumber;
	
	@Column(name = "vendor_id")
	private Long vendorId;
	
	@Column(name = "vendor_pseudo_name")
	private String vendorPseudoName;

}
