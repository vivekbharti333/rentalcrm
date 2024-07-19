package com.datfusrental.services;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datfusrental.common.GetDate;
import com.datfusrental.constant.Constant;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.enums.RequestFor;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.LeadHelper;
import com.datfusrental.jwt.JwtTokenUtil;
import com.datfusrental.object.request.LeadRequestObject;
import com.datfusrental.object.request.Request;

@Service
public class LeadService {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private LeadHelper leadHelper;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private GetDate getDate;

	@Transactional
	public LeadRequestObject registerLead(Request<LeadRequestObject> leadRequestObject)
			throws BizException, Exception {
		LeadRequestObject leadRequest = leadRequestObject.getPayload();
		leadHelper.validateLeadRequest(leadRequest);

		Boolean isValid = jwtTokenUtil.validateJwtToken(leadRequest.getCreatedBy(), leadRequest.getToken());
		if (!isValid) {
			String bookingId = "123456";
			
			LeadDetails existsLeadDetails = leadHelper.getLeadDetailsByBookingId(bookingId);
			if (existsLeadDetails == null) {

				LeadDetails leadDetails = leadHelper.getLeadDetailsByReqObj(leadRequest);
				leadDetails = leadHelper.saveLeadDetails(leadDetails);

				leadRequest.setRespCode(Constant.SUCCESS_CODE);
				leadRequest.setRespMesg(Constant.REGISTERED_SUCCESS);
				return leadRequest;
			} else {
				leadRequest.setRespCode(Constant.BAD_REQUEST_CODE);
				leadRequest.setRespMesg(Constant.USER_EXIST);
				return leadRequest;
			}
		} else {
			leadRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
			leadRequest.setRespMesg(Constant.INVALID_TOKEN);
			return leadRequest;
		}
	}

	public List<LeadDetails> getFollowupOne(Request<LeadRequestObject> leadRequestObject) {
		LeadRequestObject leadRequest = leadRequestObject.getPayload();
		
		Date k = getDate.driveDate(RequestFor.PREVIOUS_DATE.name());
//		Date k = getDate.driveDate(RequestFor.NEXT_DATE.name());
		leadRequest.setFirstDate(getDate.driveDate(RequestFor.PREVIOUS_DATE.name()));
		leadRequest.setLastDate(new Date());
		List<LeadDetails> leadList = leadHelper.getEnquaryDetailsByDate(leadRequest);
		return leadList;
	}

}
