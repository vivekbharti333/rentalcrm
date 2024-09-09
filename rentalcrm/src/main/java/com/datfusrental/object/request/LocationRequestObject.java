package com.datfusrental.object.request;

import lombok.Data;

@Data
public class LocationRequestObject {

	private Long id;
	private String token;

	private String location;
	private String locationType;
	private String status;

	private String requestedFor;
	private String loginId;
	private String superadminId;

	private int respCode;
	private String respMesg;
}
