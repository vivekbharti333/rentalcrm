package com.datfusrental.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "login_logs")
public class LoginLogs {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "login_id")
	private String loginId;

	@Column(name = "ip_address")
	private String ipAddress;
	
	@Column(name = "is_registered_ip", nullable = true)
	private String isRegisteredIp;
	
	@Column(name = "status")
	private String  status;	
	
	@Column(name = "reason")
	private String  reason;	
	
	@Column(name= "created_at")
	private Date createdAt;
	

}
