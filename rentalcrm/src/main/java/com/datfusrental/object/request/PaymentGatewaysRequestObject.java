package com.datfusrental.object.request;

import java.util.Date;

import lombok.Data;

@Data
public class PaymentGatewaysRequestObject {

	private Long id;
	private String token;
	private String paymentGatewaysName;
	private String clientId;
	private String securityKey;
	private String salt;
	private String version;
	private String status;
	private Date createdAt;
	private Date updatedAt;

	private int respCode;
	private String respMesg;
}
