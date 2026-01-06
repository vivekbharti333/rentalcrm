package com.datfusrental.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.ZoneId;

import com.datfusrental.common.GetDate;
import com.datfusrental.constant.Constant;
import com.datfusrental.dao.LeadDetailsHistoryDao;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.entities.LeadDetailsHistory;
import com.datfusrental.enums.RequestFor;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.AssignedLeadHelper;
import com.datfusrental.helper.LeadByPickAndDropHelper;
import com.datfusrental.helper.LeadByStatusHelper;
import com.datfusrental.helper.LeadDetailsHistoryHelper;
import com.datfusrental.helper.LeadHelper;
import com.datfusrental.jwt.JwtTokenUtil;
import com.datfusrental.object.request.LeadRequestObject;
import com.datfusrental.object.request.Request;
import com.datfusrental.paymentgateways.CashfreePaymentGateways;
import com.datfusrental.util.EntityDiffUtil;
import com.fasterxml.jackson.databind.ser.std.StdArraySerializers.DoubleArraySerializer;


@Service
public class LeadService {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private LeadHelper leadHelper;

	@Autowired
	private GetDate getDate;

	@Autowired
	private LeadByStatusHelper leadByStatusHelper;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private LeadDetailsHistoryHelper leadDetailsHistoryHelper;
	
	@Autowired
	EntityDiffUtil entityDiffUtil;

	@Autowired
	private LeadByPickAndDropHelper leadByPickAndDropHelper;
	
	@Autowired
	private AssignedLeadHelper assignedLeadHelper;
	
	@Autowired
	private CashfreePaymentGateways cashfreePaymentGateways;

	@Transactional
	public LeadRequestObject changeLeadStatus(Request<LeadRequestObject> leadRequestObject)
			throws BizException, Exception {
		LeadRequestObject leadRequest = leadRequestObject.getPayload();
		leadHelper.validateLeadRequest(leadRequest);

//		Boolean isValid = jwtTokenUtil.validateJwtToken(leadRequest.getLoginId(), leadRequest.getToken());
//		if (isValid) {
			LeadDetails leadDetails = leadHelper.getLeadDetailsById(leadRequest.getId());
			if (leadDetails != null) {
				if(!leadRequest.getVendorName().equalsIgnoreCase("") || leadRequest.getVendorName() != null) {
					leadDetails.setVendorName(leadRequest.getVendorName());
				}
				leadDetails.setStatus(leadRequest.getStatus());
				leadHelper.updateLeadDetails(leadDetails);

				leadRequest.setRespCode(Constant.SUCCESS_CODE);
				leadRequest.setRespMesg("Successfully Updated to " + leadRequest.getStatus());
				return leadRequest;
			} else {
				leadRequest.setRespCode(Constant.NOT_EXISTS);
				leadRequest.setRespMesg(Constant.NOT_EXIST_MSG);
				return leadRequest;
			}
//		} else {
//			leadRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
//			leadRequest.setRespMesg(Constant.INVALID_TOKEN);
//			return leadRequest;
//		}
	}

	@Transactional
	public LeadRequestObject registerLead(Request<LeadRequestObject> leadRequestObject) throws BizException, Exception {
		LeadRequestObject leadRequest = leadRequestObject.getPayload();
		leadHelper.validateLeadRequest(leadRequest);

//		Boolean isValid = jwtTokenUtil.validateJwtToken(leadRequest.getLoginId(), leadRequest.getToken());
//		if (isValid) {

		// Generate & set booking id
//		String bookingId = StringUtils.substring(RandomStringUtils.random(64, true, true), 0, 12);
		leadRequest.setBookingId(StringUtils.substring(RandomStringUtils.random(64, true, true), 0, 12));

		LeadDetails existsLeadDetails = leadHelper.getLeadDetailsByBookingId(leadRequest.getBookingId());
		if (existsLeadDetails == null) {

			// Condition if category is cruise check available sheet

			// Get agent name
//				User user = userHelper.getUserDetailsByLoginId(leadRequest.getCreatedBy());
//				leadRequest.setCreatedByName(user.getFirstName()+" "+user.getLastName());
//				leadR

			// Lead Details
			LeadDetails leadDetails = leadHelper.getLeadDetailsByReqObj(leadRequest);
			leadDetails = leadHelper.saveLeadDetails(leadDetails);
			
			//Payment Gateways
			if(leadRequest.getLeadOrigine().equalsIgnoreCase("WEBSITE")) {
				leadRequest.setPaymentUrl(cashfreePaymentGateways.getCashfreePaymentLink(leadRequest));
			}

			// history

			// message
			// String response = sendWhatsappMsg.sendWhatsAppMessage(leadDetails);
			// logger.info("Message response : "+response);

			leadRequest.setRespCode(Constant.SUCCESS_CODE);
			leadRequest.setRespMesg(Constant.REGISTERED_SUCCESS);
			return leadRequest;
		} else {
			leadRequest.setRespCode(Constant.BAD_REQUEST_CODE);
			leadRequest.setRespMesg("Booking id already exist. Try again");
			return leadRequest;
		}
//		} else {
//			leadRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
//			leadRequest.setRespMesg(Constant.INVALID_TOKEN);
//			return leadRequest;
//		}
	}
	
	public LeadRequestObject updatePaymentDetails(Request<LeadRequestObject> leadRequestObject) throws BizException, Exception {
	    LeadRequestObject leadRequest = leadRequestObject.getPayload();
	    leadHelper.validateLeadRequest(leadRequest);

	    LeadDetails leadDetails = leadHelper.getLeadDetailsByBookingId(leadRequest.getBookingId());
	    if (leadDetails != null) {
	    	
	    	System.out.println("Enter 1");

	        // 1) Get order ID using payment link ID
	        String orderId = cashfreePaymentGateways.getCashFreePaymentOrderIdByLinkIdStatus(leadRequest.getBookingId());

	        System.out.println("Enter 2 : "+orderId);
	        if (orderId == null) {
	            leadRequest.setRespCode(Constant.BAD_REQUEST_CODE);
	            leadRequest.setRespMesg("No order found for this payment link.");
	            return leadRequest;
	        }

	        // 2) Get payment status using order ID
	        String paymentStatus = cashfreePaymentGateways.getCashFreePaymentStatusByOrderId(orderId);
	        
	        System.out.println("Enter 3 : "+paymentStatus);

	        if (paymentStatus == null) {
	            leadRequest.setRespCode(Constant.BAD_REQUEST_CODE);
	            leadRequest.setRespMesg("Unable to fetch payment status.");
	            return leadRequest;
	        }

	        // 3) Update status based on payment
	        if (paymentStatus.equalsIgnoreCase("SUCCESS") || paymentStatus.equalsIgnoreCase("PAID")) {
	            leadDetails.setStatus("WON");
	        } else {
	            leadDetails.setStatus(paymentStatus);
	        }

	        leadDetails.setUpdatedAt(new Date());
	        leadHelper.updateLeadDetails(leadDetails);

	        leadRequest.setRespCode(Constant.SUCCESS_CODE);
	        leadRequest.setRespMesg("Your Payment is " + paymentStatus);
	        return leadRequest;

	    } else {
	        leadRequest.setRespCode(Constant.BAD_REQUEST_CODE);
	        leadRequest.setRespMesg("Invalid booking id or Payment");
	        return leadRequest;
	    }
	}


	@Transactional
	public LeadRequestObject updateLead(Request<LeadRequestObject> leadRequestObject) throws BizException, Exception {
	    LeadRequestObject leadRequest = leadRequestObject.getPayload();
	    leadHelper.validateLeadRequest(leadRequest);

	    System.out.println(leadRequest.getId() + " id");
	    LeadDetails existingLead = leadHelper.getLeadDetailsById(leadRequest.getId());
	    
	    if (existingLead != null) {

	        // ✅ Clone the old entity before updating
	        LeadDetails oldLead = new LeadDetails();
	        BeanUtils.copyProperties(existingLead, oldLead);

	        // ✅ Update and persist new lead
	        existingLead = leadHelper.getUpdatedLeadDetailsByReqObj(leadRequest, existingLead);
	        existingLead = leadHelper.updateLeadDetails(existingLead);

	        leadDetailsHistoryHelper.updateLeadHistory(oldLead, existingLead);

	        leadRequest.setRespCode(Constant.SUCCESS_CODE);
	        leadRequest.setRespMesg(Constant.UPDATED_SUCCESS);
	        return leadRequest;
	    } else {
	        leadRequest.setRespCode(Constant.BAD_REQUEST_CODE);
	        leadRequest.setRespMesg(Constant.NOT_EXIST_MSG);
	        return leadRequest;
	    }
	}

	public List<LeadDetails> getLeadListByStatus(Request<LeadRequestObject> leadRequestObject) {
		LeadRequestObject leadRequest = leadRequestObject.getPayload();

		List<LeadDetails> leadList = leadByStatusHelper.getLeadListByStatus(leadRequest);
		return leadList;
	}

	public List<LeadDetails> getAllLeadList(Request<LeadRequestObject> leadRequestObject) {
		LeadRequestObject leadRequest = leadRequestObject.getPayload();
		List<LeadDetails> leadList = leadHelper.getAllLeadList(leadRequest);
		return leadList;
	}

	public List<LeadDetails> getAllHotLeadList(Request<LeadRequestObject> leadRequestObject) {
		LeadRequestObject leadRequest = leadRequestObject.getPayload();
		List<LeadDetails> leadList = leadHelper.getAllHotLeadList(leadRequest);
		return leadList;
	}
	
	public List<LeadDetails> getFollowupLeadList(Request<LeadRequestObject> leadRequestObject) {
		LeadRequestObject leadRequest = leadRequestObject.getPayload();

		LocalDate today = LocalDate.now(); // should be initialized elsewhere
		ZoneId zone = ZoneId.systemDefault();

		switch (leadRequest.getRequestedFor().toUpperCase()) {
		case "FOLLOWUP_ONE":
			leadRequest.setFirstDate(Date.from(today.plusDays(1).atStartOfDay(zone).toInstant()));
			leadRequest.setLastDate(Date.from(today.plusDays(2).atStartOfDay(zone).toInstant()));
			break;
		case "FOLLOWUP_TWO":
			leadRequest.setFirstDate(Date.from(today.plusDays(2).atStartOfDay(zone).toInstant()));
			leadRequest.setLastDate(Date.from(today.plusDays(3).atStartOfDay(zone).toInstant()));
			break;
		case "FOLLOWUP_THREE":
			leadRequest.setFirstDate(Date.from(today.plusDays(3).atStartOfDay(zone).toInstant()));
			leadRequest.setLastDate(Date.from(today.plusDays(4).atStartOfDay(zone).toInstant()));
			break;
		default:
			leadRequest.setFirstDate(getDate.driveDate(RequestFor.TODAY.name()));
			leadRequest.setLastDate(getDate.driveDate(RequestFor.NEXT_DATE.name()));
		}
			
		List<LeadDetails> leadList = leadHelper.getFollowupLeadList(leadRequest);
		return leadList;
	}

	public List<LeadDetails> getPickupLeadList(Request<LeadRequestObject> leadRequestObject) {
		LeadRequestObject leadRequest = leadRequestObject.getPayload();

		if (leadRequest.getRequestedFor().equalsIgnoreCase(RequestFor.TODAY.name())) {
			leadRequest.setFirstDate(new Date());
			leadRequest.setLastDate(getDate.driveDate(RequestFor.NEXT_DATE.name()));
		}

		if (leadRequest.getRequestedFor().equalsIgnoreCase(RequestFor.TOMORROW.name())) {
			leadRequest.setFirstDate(getDate.driveDate(RequestFor.NEXT_DATE.name()));
			leadRequest.setLastDate(getDate.driveDate(RequestFor.NEXT_TO_NEXT_DATE.name()));
		}

		if (leadRequest.getRequestedFor().equalsIgnoreCase(RequestFor.MONTH.name())) {
			leadRequest.setFirstDate(getDate.driveDate(RequestFor.MONTH_FIRST_DATE.name()));
			leadRequest.setLastDate(getDate.driveDate(RequestFor.MONTH_LAST_DATE.name()));
		}

		List<LeadDetails> leadList = leadByPickAndDropHelper.getPickupLeadList(leadRequest);
		return leadList;
	}

	public List<LeadDetails> getDropLeadList(Request<LeadRequestObject> leadRequestObject) {
		LeadRequestObject leadRequest = leadRequestObject.getPayload();

		if (leadRequest.getRequestedFor().equalsIgnoreCase(RequestFor.TODAY.name())) {
			leadRequest.setFirstDate(new Date());
			leadRequest.setLastDate(getDate.driveDate(RequestFor.NEXT_DATE.name()));
		}

		if (leadRequest.getRequestedFor().equalsIgnoreCase(RequestFor.TOMORROW.name())) {
			leadRequest.setFirstDate(getDate.driveDate(RequestFor.NEXT_DATE.name()));
			leadRequest.setLastDate(getDate.driveDate(RequestFor.NEXT_TO_NEXT_DATE.name()));
		}

		if (leadRequest.getRequestedFor().equalsIgnoreCase(RequestFor.MONTH.name())) {
			leadRequest.setFirstDate(getDate.driveDate(RequestFor.MONTH_FIRST_DATE.name()));
			leadRequest.setLastDate(getDate.driveDate(RequestFor.MONTH_LAST_DATE.name()));
		}

		List<LeadDetails> leadList = leadByPickAndDropHelper.getDropLeadList(leadRequest);
		return leadList;
	}

	public List<LeadDetails> getLeadByStatus(Request<LeadRequestObject> leadRequestObject) {
	    LeadRequestObject leadRequest = leadRequestObject.getPayload();

	    switch (leadRequest.getRequestedFor().toUpperCase()) {
	        case "TODAY":
	            leadRequest.setFirstDate(getDate.driveDate(RequestFor.TODAY.name()));
	            leadRequest.setLastDate(getDate.driveDate(RequestFor.NEXT_DATE.name()));
	            break;
	        case "TOMORROW":
	            leadRequest.setFirstDate(getDate.driveDate(RequestFor.NEXT_DATE.name()));
	            leadRequest.setLastDate(getDate.driveDate(RequestFor.NEXT_TO_NEXT_DATE.name()));
	            break;
	        case "MONTH":
	            leadRequest.setFirstDate(getDate.driveDate(RequestFor.MONTH_FIRST_DATE.name()));
	            leadRequest.setLastDate(getDate.driveDate(RequestFor.MONTH_LAST_DATE.name()));
	            break;
	        default:
	            leadRequest.setFirstDate(getDate.driveDate(RequestFor.TODAY.name()));
	            leadRequest.setLastDate(getDate.driveDate(RequestFor.NEXT_DATE.name()));
	    }

	    System.out.println(leadRequest.getRequestedFor());
	    System.out.println(leadRequest.getFirstDate());
	    System.out.println(leadRequest.getLastDate());
	    System.out.println(leadRequest.getStatus());

	    return assignedLeadHelper.getLeadByStatus(leadRequest);
	}



}
