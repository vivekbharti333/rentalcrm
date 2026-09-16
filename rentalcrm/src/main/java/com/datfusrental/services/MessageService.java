package com.datfusrental.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datfusrental.constant.Constant;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.LeadHelper;
import com.datfusrental.object.request.LeadRequestObject;
import com.datfusrental.object.request.Request;

@Service
public class MessageService {

	@Autowired
	private LeadHelper leadHelper;


	@Transactional
	public LeadRequestObject changeLeadStatus(Request<LeadRequestObject> leadRequestObject)
			throws BizException, Exception {
		LeadRequestObject leadRequest = leadRequestObject.getPayload();
		leadHelper.validateLeadRequest(leadRequest);

		LeadDetails leadDetails = leadHelper.getLeadDetailsById(leadRequest.getId());

		if (leadDetails != null) {

			// Send Message
			leadDetails = leadHelper.updateLeadDetails(leadDetails);
//			leadRequest = sendBookingConfirmationIfWon(leadRequest, leadDetails);

			leadRequest.setRespCode(Constant.SUCCESS_CODE);
			leadRequest.setRespMesg("Successfully Updated to " + leadRequest.getStatus());

			return leadRequest;

		}
		return leadRequest;

	}

}
