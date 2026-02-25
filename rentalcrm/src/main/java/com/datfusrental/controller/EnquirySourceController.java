package com.datfusrental.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.datfusrental.constant.Constant;
import com.datfusrental.entities.EnquirySourceDetails;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.LeadRequestObject;
import com.datfusrental.object.request.Request;
import com.datfusrental.object.response.GenricResponse;
import com.datfusrental.object.response.Response;
import com.datfusrental.services.EnquirySourceServices;

@CrossOrigin(origins = "*")
@RestController
public class EnquirySourceController {
	
	@Autowired
	private EnquirySourceServices enquirySourceServices;

	
	@RequestMapping(path = "changeEnquirySourceStatus", method = RequestMethod.POST)
	public Response<LeadRequestObject> changeEnquirySourceStatus(@RequestBody Request<LeadRequestObject> leadRequestObject,
			HttpServletRequest request) {
		GenricResponse<LeadRequestObject> responseObj = new GenricResponse<LeadRequestObject>();
		try {
			LeadRequestObject responce = enquirySourceServices.changeEnquirySourceStatus(leadRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}
	
	@RequestMapping(path = "addEnquirySource", method = RequestMethod.POST)
	public Response<LeadRequestObject> addEnquirySource(@RequestBody Request<LeadRequestObject> leadRequestObject,
			HttpServletRequest request) {
		GenricResponse<LeadRequestObject> responseObj = new GenricResponse<LeadRequestObject>();
		try {
			LeadRequestObject responce = enquirySourceServices.addEnquirySource(leadRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}
	
	@RequestMapping(path = "updateEnquirySource", method = RequestMethod.POST)
	public Response<LeadRequestObject> updateEnquirySource(@RequestBody Request<LeadRequestObject> leadRequestObject,
			HttpServletRequest request) {
		GenricResponse<LeadRequestObject> responseObj = new GenricResponse<LeadRequestObject>();
		try {
			LeadRequestObject responce = enquirySourceServices.updateEnquirySource(leadRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}
	
	@RequestMapping(path = "getEnquirySource", method = RequestMethod.POST)
	public Response<EnquirySourceDetails> getLeadHistoryById(@RequestBody Request<LeadRequestObject> leadRequestObject) {
		GenricResponse<EnquirySourceDetails> response = new GenricResponse<EnquirySourceDetails>();
		try {
			List<EnquirySourceDetails> enquirySourceList = enquirySourceServices.getEnquirySource(leadRequestObject);
			return response.createListResponse(enquirySourceList, 200, String.valueOf(enquirySourceList.size()));
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}
}
