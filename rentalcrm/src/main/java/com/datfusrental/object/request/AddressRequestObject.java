package com.datfusrental.object.request;

import java.util.Date;
import lombok.Data;

@Data
public class AddressRequestObject {
	
	private Long id;
	private String status;
	private String userType;
	private String addressType;
	private String addressLine;
	private String landmark;
	private String district;
	private String city;
	private String state;
	private String country;
	private String pincode;
	
	private Date createdAt;
	private Date updatedAt;
	private String createdBy;
	private String superadminId;
	
	private int respCode;
	private String respMesg;
	
}
