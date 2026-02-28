package com.datfusrental.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.datfusrental.constant.Constant;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.entities.LeadDetailsHistory;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.LeadRequestObject;
import com.datfusrental.object.request.Request;
import com.datfusrental.object.response.GenricResponse;
import com.datfusrental.object.response.Response;
import com.datfusrental.services.LeadService;
import com.datfusrental.services.MobileService;

@CrossOrigin(origins = "*")
@RestController
public class MobileController {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private MobileService mobileService;
	
	
		
	@RequestMapping(path = "getMobileInstantList", method = RequestMethod.POST)
	public Response<LeadDetails> getMobileInstantList(@RequestBody Request<LeadRequestObject> leadRequestObject) {
		GenricResponse<LeadDetails> response = new GenricResponse<LeadDetails>();
		try {
			List<LeadDetails> instantBookingList = mobileService.getMobileInstantList(leadRequestObject);
			return response.createListResponse(instantBookingList, 200, String.valueOf(instantBookingList.size()));
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}
	
	
	
	
	
}
