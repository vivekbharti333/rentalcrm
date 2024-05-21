package com.datfusrental.object.request;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class UserRequestObject {
	
	private Long id;
	private String token;
	private String userPicture;
	private String userCode;
	private String loginId;
	private String password;
	private String status;
	private String roleTypeIds;
	private String roleType;
	private String firstName;
	private String lastName;
	private String mobileNo;
	private String alternateMobile;
	private String emailId;
	private String idDocumentType;
	private String idDocumentPicture;
	private String panNumber;
	
	private String emergencyContactRelation1;
	private String emergencyContactName1;
	private String emergencyContactNo1;
	private String emergencyContactRelation2;
	private String emergencyContactName2;
	private String emergencyContactNo2;
	
	public List<AddressRequestObject> addressList;
	
	private String service;
	private String permissions;
	
	private Date dob;
	private Date validityExpireOn;
	
	//address start
	private String userType;
	private String addressType;
	private String addressLine;
	private String landmark;
	private String district;
	private String city;
	private String state;
	private String country;
	private String pincode; 
	//address end
	
	private Date createdAt;
	private Date passwordUpdatedAt;
	private Date updatedAt;
	private String createdBy;
	private String teamLeaderId;
	private String superadminId;
	
	private String requestedFor;
	private String searchParam;
	
	private int respCode;
	private String respMesg;
	

}
