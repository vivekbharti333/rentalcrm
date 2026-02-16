package com.datfusrental.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datfusrental.constant.Constant;
import com.datfusrental.entities.EnquirySourceDetails;
import com.datfusrental.entities.LeadDetailsHistory;
import com.datfusrental.enums.Status;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.EnquirySourceHelper;
import com.datfusrental.object.request.LeadRequestObject;
import com.datfusrental.object.request.Request;

@Service
public class EnquirySourceServices {
	
	@Autowired
	private EnquirySourceHelper enquirySourceHelper;
	
	public LeadRequestObject changeEnquirySourceStatus(Request<LeadRequestObject> leadRequestObject) throws BizException, Exception {
		
			LeadRequestObject leadRequest = leadRequestObject.getPayload();
			enquirySourceHelper.validateLeadRequest(leadRequest);

			EnquirySourceDetails enquirySourceDetails = enquirySourceHelper.getEnquirySourceDetailsById(leadRequest.getId());
			if (enquirySourceDetails != null) {

				if (enquirySourceDetails.getStatus().equalsIgnoreCase(Status.ACTIVE.name())) {
					enquirySourceDetails.setStatus(Status.INACTIVE.name());
				} else {
					enquirySourceDetails.setStatus(Status.ACTIVE.name());
				}
				enquirySourceDetails = enquirySourceHelper.updateEnquirySourceDetails(enquirySourceDetails);

				leadRequest.setRespCode(Constant.SUCCESS_CODE);
				leadRequest.setRespMesg(Constant.REGISTERED_SUCCESS);
				return leadRequest;
			} else {
				leadRequest.setRespCode(Constant.NOT_EXISTS);
				leadRequest.setRespMesg(Constant.NOT_EXIST_MSG);
				return leadRequest;
			}

		}
	

	public LeadRequestObject addEnquirySource(Request<LeadRequestObject> leadRequestObject) throws BizException, Exception {
		LeadRequestObject leadRequest = leadRequestObject.getPayload();
		enquirySourceHelper.validateLeadRequest(leadRequest);

		EnquirySourceDetails existsEnquirySourceDetails = enquirySourceHelper.getEnquirySourceDetailsById(leadRequest.getId());
		if (existsEnquirySourceDetails == null) {

			EnquirySourceDetails enquirySourceDetails = enquirySourceHelper.getEnquirySourceDetailsByReqObj(leadRequest);
			enquirySourceDetails = enquirySourceHelper.saveEnquirySourceDetails(enquirySourceDetails);
			
			leadRequest.setRespCode(Constant.SUCCESS_CODE);
			leadRequest.setRespMesg(Constant.REGISTERED_SUCCESS);
			return leadRequest;
		} else {
			leadRequest.setRespCode(Constant.ALREADY_EXISTS);
			leadRequest.setRespMesg(Constant.ALREADY_EXISTS_MSG);
			return leadRequest;
		}

	}
	
	public LeadRequestObject updateEnquirySource(Request<LeadRequestObject> leadRequestObject) throws BizException, Exception {
		LeadRequestObject leadRequest = leadRequestObject.getPayload();
		enquirySourceHelper.validateLeadRequest(leadRequest);

		EnquirySourceDetails enquirySourceDetails = enquirySourceHelper.getEnquirySourceDetailsById(leadRequest.getId());
		if (enquirySourceDetails != null) {

			enquirySourceDetails = enquirySourceHelper.getUpdatedEnquirySourceDetailsByReqObj(leadRequest, enquirySourceDetails);
			enquirySourceDetails = enquirySourceHelper.updateEnquirySourceDetails(enquirySourceDetails);
			
			leadRequest.setRespCode(Constant.SUCCESS_CODE);
			leadRequest.setRespMesg(Constant.UPDATED_SUCCESS);
			return leadRequest;
		} else {
			leadRequest.setRespCode(Constant.NOT_EXISTS);
			leadRequest.setRespMesg(Constant.NOT_EXIST_MSG);
			return leadRequest;
		}

	}

	public List<EnquirySourceDetails> getEnquirySource(Request<LeadRequestObject> leadRequestObject) {
		LeadRequestObject leadRequest = leadRequestObject.getPayload();
		List<EnquirySourceDetails> enquirySourceList = enquirySourceHelper.getEnquirySource(leadRequest);
		return enquirySourceList;
	}

}
