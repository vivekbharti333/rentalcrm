package com.datfusrental.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.datfusrental.constant.Constant;
import com.datfusrental.entities.CustomerPickupDetails;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.LeadRequestObject;
import com.datfusrental.object.request.Request;
import com.datfusrental.object.response.GenricResponse;
import com.datfusrental.object.response.Response;
import com.datfusrental.services.CustomerService;
	
	
@CrossOrigin(origins = "*")
@RestController
public class CustomerController {

	@Autowired
	private CustomerService customerService;
	
	
	@RequestMapping(path = "getCustomerPickupDetailsByBookingId", method = RequestMethod.POST)
	public Response<CustomerPickupDetails> getCustomerPickupDetailsByBookingId(@RequestBody Request<LeadRequestObject> requestObject) {
		GenricResponse<CustomerPickupDetails> responseObj = new GenricResponse<CustomerPickupDetails>();
		try {
			CustomerPickupDetails details = customerService.getCustomerPickupDetailsByBookingId(requestObject.getPayload().getBookingId());
			return responseObj.createSuccessResponse(details, Constant.SUCCESS_CODE);
		} catch (Exception e) {
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}

	
	
	@RequestMapping(path = "uploadVehiclePhoto", method = RequestMethod.POST)
	public Response<LeadRequestObject> changeLeadStatus123(@RequestBody Request<LeadRequestObject> leadRequestObject,
			HttpServletRequest request) {
		GenricResponse<LeadRequestObject> responseObj = new GenricResponse<LeadRequestObject>();
		try {
			LeadRequestObject responce = customerService.uploadVehiclePhoto(leadRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}
	
	
}
