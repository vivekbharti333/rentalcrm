package com.datfusrental.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;
import org.springframework.lang.NonNull;

import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Table(name = "user_details")
@Data
@NoArgsConstructor
public class User {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "secret_key")
	private String secretTokenKey;
	
	@Lob
	@Column(name = "user_picture")
	private String userPicture;
	
	@Column(name = "user_code")
	private String userCode;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;
	
	@Column(name = "gender")
	private String gender;
	
	@NonNull
	@Length(min = 5, max = 20)
	@Column(name = "login_id")
	private String loginId;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "salt")
	private String salt;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "service")
	private String service;
	
	@Lob
	@Column(name = "permissions")
	private String permissions;
	
	@Column(name = "role_type")
	private String roleType;
	
	@Column(name = "mobile_no")
	private String mobileNo;
	
	@Column(name = "alternate_mobile")
	private String alternateMobile;
	
	@Column(name = "email_id")
	private String emailId;
	
	@Column(name = "emergency_contact_relation1")
	private String emergencyContactRelation1;
	
	@Column(name = "emergency_contact_name1")
	private String emergencyContactName1;
	
	@Column(name = "emergency_contact_no1")
	private String emergencyContactNo1;
	
	@Column(name = "emergency_contact_relation2")
	private String emergencyContactRelation2;
	
	@Column(name = "emergency_contact_name2")
	private String emergencyContactName2;
	
	@Column(name = "emergency_contact_no2")
	private String emergencyContactNo2;
	
	@Column(name = "id_document_type")
	private String idDocumentType;
	
	@Lob
	@Column(name = "id_document_picture")
	private String idDocumentPicture;
	
	@Column(name = "pan_number")
	private String panNumber;
	
	@Column(name = "dob")
	private Date dob;
	
	@Column(name = "validity_expire_on")
	private Date validityExpireOn;
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_At")
	private Date updatedAt;
	
	@Column(name = "password_updated_At")
	private Date passwordUpdatedAt;
	
	@Column(name = "created_by")
	private String createdBy;
	
	@Column(name = "teamleader_id")
	private String teamleaderId;
	
	@Column(name = "admin_id")
	private String adminId;
	
	@Column(name = "superadmin_id")
	private String superadminId;

}
