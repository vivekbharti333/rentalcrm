package com.datfusrental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.datfusrental.services.LogsService;
	
	
@CrossOrigin(origins = "*")
@RestController
public class LogsController {

	@Autowired
	private LogsService logsService;
	
	
//	@RequestMapping(path = "getCusto", method = RequestMethod.POST)
//	public Response<CustomerPickupDetails> getCusto(@RequestBody Request<LeadRequestObject> requestObject) {
//		GenricResponse<CustomerPickupDetails> responseObj = new GenricResponse<CustomerPickupDetails>();
//		try {
//			CustomerPickupDetails details = logsService.getCustomerPickupDetailsByBookingId(requestObject.getPayload().getBookingId());
//			return responseObj.createSuccessResponse(details, Constant.SUCCESS_CODE);
//		} catch (Exception e) {
//			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
//		}
//	}

	
	
	
	
	
}
