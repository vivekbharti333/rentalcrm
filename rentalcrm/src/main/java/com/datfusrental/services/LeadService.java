package com.datfusrental.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datfusrental.common.GetDate;
import com.datfusrental.constant.Constant;
import com.datfusrental.dao.LocationDetailsDao;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.entities.LeadDetailsHistory;
import com.datfusrental.entities.LocationDetails;
import com.datfusrental.enums.RequestFor;
import com.datfusrental.enums.Status;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.AssignedLeadHelper;
import com.datfusrental.helper.LeadByPickAndDropHelper;
import com.datfusrental.helper.LeadByStatusHelper;
import com.datfusrental.helper.LeadDetailsHistoryHelper;
import com.datfusrental.helper.LeadHelper;
import com.datfusrental.helper.LocationHelper;
import com.datfusrental.helper.WebsiteLeadHelper;
import com.datfusrental.helper.WonLeadHelper;
import com.datfusrental.object.request.LeadRequestObject;
import com.datfusrental.object.request.Request;
import com.datfusrental.paymentgateways.CashfreePaymentGateways;
import com.datfusrental.util.EntityDiffUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class LeadService {
	
	
	private Date startOfDay(LocalDate date, ZoneId zone) {
	    return Date.from(date.atStartOfDay(zone).toInstant());
	}

	private Date endOfDay(LocalDate date, ZoneId zone) {
	    return Date.from(date.atTime(23, 59, 59).atZone(zone).toInstant());
	}


	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private LeadHelper leadHelper;
	
	@Autowired
	private LocationHelper locationHelper;
	
	@Autowired
	private LocationDetailsDao locationDetailsDao;

	@Autowired
	private GetDate getDate;

	@Autowired
	private LeadByStatusHelper leadByStatusHelper;

	@Autowired
	private WonLeadHelper wonLeadHelper;
	
	@Autowired
	private WebsiteLeadHelper websiteLeadHelper;
	
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
	
	@Autowired
	private ObjectMapper objectMapper;

	@Transactional
	public LeadRequestObject changeLeadStatus(Request<LeadRequestObject> leadRequestObject)
			throws BizException, Exception {
		LeadRequestObject leadRequest = leadRequestObject.getPayload();
		leadHelper.validateLeadRequest(leadRequest);

		LeadDetails leadDetails = leadHelper.getLeadDetailsById(leadRequest.getId());
		if (leadDetails != null) {

			leadDetails.setPaymentType(leadRequest.getPaymentType());
			leadDetails.setNotes(leadRequest.getNotes());
			leadDetails.setStatus(leadRequest.getStatus());
			leadDetails.setChangeStatusDate(new Date());
			leadHelper.updateLeadDetails(leadDetails);

			leadRequest.setRespCode(Constant.SUCCESS_CODE);
			leadRequest.setRespMesg("Successfully Updated to " + leadRequest.getStatus());
			return leadRequest;
		} else {
			leadRequest.setRespCode(Constant.NOT_EXISTS);
			leadRequest.setRespMesg(Constant.NOT_EXIST_MSG);
			return leadRequest;
		}
	}
	
	
	@Transactional
	public LeadRequestObject changeSecondStatus(Request<LeadRequestObject> leadRequestObject)
			throws BizException, Exception {
		LeadRequestObject leadRequest = leadRequestObject.getPayload();
		leadHelper.validateLeadRequest(leadRequest);

			LeadDetails leadDetails = leadHelper.getLeadDetailsById(leadRequest.getId());
			if (leadDetails != null) {

				leadDetails.setSecondStatus(leadRequest.getSecondStatus());
				leadHelper.updateLeadDetails(leadDetails);

				leadRequest.setRespCode(Constant.SUCCESS_CODE);
				leadRequest.setRespMesg("Successfully Updated to " + leadRequest.getSecondStatus());
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
	public LeadRequestObject assignLeadToVendor(Request<LeadRequestObject> leadRequestObject)
			throws BizException, Exception {
		LeadRequestObject leadRequest = leadRequestObject.getPayload();
		leadHelper.validateLeadRequest(leadRequest);

		LeadDetails leadDetails = leadHelper.getLeadDetailsById(leadRequest.getId());
		if (leadDetails != null) {
			
			leadDetails.setVendorName(leadRequest.getVendorName());
			leadDetails.setChangeStatusDate(new Date());
			leadHelper.updateLeadDetails(leadDetails);

			leadRequest.setRespCode(Constant.SUCCESS_CODE);
			leadRequest.setRespMesg("Successfully Assigned to " + leadRequest.getVendorName());
			return leadRequest;
		} else {
			leadRequest.setRespCode(Constant.NOT_EXISTS);
			leadRequest.setRespMesg(Constant.NOT_EXIST_MSG);
			return leadRequest;
		}
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
			
			//Save Other Location
			if(!leadRequest.getOtherPickLocation().equalsIgnoreCase("N/A")){
				
				LocationDetails existsLocationDetails = locationHelper.getLocationDetailsByType(leadRequest.getOtherPickLocation());
				if (existsLocationDetails == null) {
					
					LocationDetails locationDetails = new LocationDetails();
					locationDetails.setLocation(leadRequest.getOtherPickLocation());
					
					locationDetails.setLocationType("PICK");
					locationDetails.setStatus(Status.INACTIVE.name());
					locationDetails.setSuperadminId(leadRequest.getSuperadminId());
					locationDetails.setCreatedAt(new Date());
					locationDetails.setUpdatedAt(new Date());
					
					locationDetailsDao.persist(locationDetails);
					
				}
				
			}
			if(!leadRequest.getOtherDropLocation().equalsIgnoreCase("N/A")){
				
				LocationDetails existsLocationDetails = locationHelper.getLocationDetailsByType(leadRequest.getOtherDropLocation());
				if (existsLocationDetails == null) {
					LocationDetails locationDetails = new LocationDetails();
					locationDetails.setLocation(leadRequest.getOtherDropLocation());
					
					locationDetails.setLocationType("PICK");
					locationDetails.setStatus(Status.INACTIVE.name());
					locationDetails.setSuperadminId(leadRequest.getSuperadminId());
					locationDetails.setCreatedAt(new Date());
					locationDetails.setUpdatedAt(new Date());
					
					locationDetailsDao.persist(locationDetails);
				}
			}


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

//	        // 2) Get payment status using order ID
//	        String paymentStatus = cashfreePaymentGateways.getCashFreePaymentStatusByOrderId(orderId);
//	        
//	        System.out.println("Enter 3 : "+paymentStatus);
//
//	        if (paymentStatus == null) {
//	            leadRequest.setRespCode(Constant.BAD_REQUEST_CODE);
//	            leadRequest.setRespMesg("Unable to fetch payment status.");
//	            return leadRequest;
//	        }
//
//	        // 3) Update status based on payment
//	        if (paymentStatus.equalsIgnoreCase("SUCCESS") || paymentStatus.equalsIgnoreCase("PAID")) {
//	            leadDetails.setStatus("WON");
//	        } else {
//	            leadDetails.setStatus(paymentStatus);
//	        }
	        
	        String responseBody = cashfreePaymentGateways.getCashFreePaymentStatusByOrderId(orderId);

	        System.out.println("Enter 3 : " + responseBody);

	        if (responseBody == null) {
	            leadRequest.setRespCode(Constant.BAD_REQUEST_CODE);
	            leadRequest.setRespMesg("Unable to fetch payment status.");
	            return leadRequest;
	        }

	        // Extract payment_status from responseBody
	        String paymentStatus = null;
	        try {
	            JsonNode rootArray = objectMapper.readTree(responseBody);
	            if (rootArray.isArray() && rootArray.size() > 0) {
	                JsonNode firstPayment = rootArray.get(0);
	                if (firstPayment.has("payment_status")) {
	                    paymentStatus = firstPayment.get("payment_status").asText();
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        if (paymentStatus == null) {
	            leadRequest.setRespCode(Constant.BAD_REQUEST_CODE);
	            leadRequest.setRespMesg("Payment status not found.");
	            return leadRequest;
	        }

	        // Update lead status
	        if ("SUCCESS".equalsIgnoreCase(paymentStatus) || "PAID".equalsIgnoreCase(paymentStatus)) {
	            leadDetails.setStatus("WON");
	        } else {
	            leadDetails.setStatus(paymentStatus);
	        }

	        leadDetails.setUpdatedAt(new Date());
	        leadHelper.updateLeadDetails(leadDetails);
	        
	        leadRequest.setPgResponseBody(responseBody);

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
//	        LeadDetails oldLead = new LeadDetails();
//	        BeanUtils.copyProperties(existingLead, oldLead);

	        // ✅ Update and persist new lead
	        existingLead = leadHelper.getUpdatedLeadDetailsByReqObj(leadRequest, existingLead);
	        existingLead = leadHelper.updateLeadDetails(existingLead);

//	        leadDetailsHistoryHelper.updateLeadHistory(oldLead, existingLead, leadRequest);

	        leadRequest.setRespCode(Constant.SUCCESS_CODE);
	        leadRequest.setRespMesg(Constant.UPDATED_SUCCESS);
	        return leadRequest;
	    } else {
	        leadRequest.setRespCode(Constant.BAD_REQUEST_CODE);
	        leadRequest.setRespMesg(Constant.NOT_EXIST_MSG);
	        return leadRequest;
	    }
	}
	
	public List<LeadDetailsHistory> getLeadHistoryById(Request<LeadRequestObject> leadRequestObject) {
		LeadRequestObject leadRequest = leadRequestObject.getPayload();

		List<LeadDetailsHistory> leadHistoryList = leadHelper.getLeadHistoryById(leadRequest);
		return leadHistoryList;
	}
	
	public List<LeadDetails> getLeadListByStatus(Request<LeadRequestObject> leadRequestObject) {
		LeadRequestObject leadRequest = leadRequestObject.getPayload();

		List<LeadDetails> leadList = leadByStatusHelper.getLeadListByStatus(leadRequest);
		return leadList;
	}

	public List<LeadDetails> getLeadListBySecondStatus(Request<LeadRequestObject> leadRequestObject) {
		LeadRequestObject leadRequest = leadRequestObject.getPayload();

		List<LeadDetails> leadList = leadByStatusHelper.getLeadListBySecondStatus(leadRequest);
		return leadList;
	}

	public List<LeadDetails> getAllLeadList(Request<LeadRequestObject> leadRequestObject) {
		LeadRequestObject leadRequest = leadRequestObject.getPayload();
		List<LeadDetails> leadList = leadHelper.getAllLeadList(leadRequest);
		return leadList;
	}

//	public List<LeadDetails> getAllHotLeadList(Request<LeadRequestObject> leadRequestObject) {
//		LeadRequestObject leadRequest = leadRequestObject.getPayload();
//		
//		LocalDate today = LocalDate.now();
//	    ZoneId zone = ZoneId.systemDefault();
//		
//		leadRequest.setFirstDate(Date.from(today.atStartOfDay(zone).toInstant()));
//        leadRequest.setLastDate(Date.from(today.plusDays(1).atStartOfDay(zone).toInstant()));
//        
//		List<LeadDetails> leadList = leadHelper.getAllHotLeadList(leadRequest);
//		return leadList;
//	}
	
	public List<LeadDetails> getAllHotLeadList(Request<LeadRequestObject> leadRequestObject) {

	    LeadRequestObject leadRequest = leadRequestObject.getPayload();

	    LocalDate today = LocalDate.now(ZoneId.systemDefault());
	    ZoneId zone = ZoneId.systemDefault();

	    // today range [00:00, next day 00:00)
	    leadRequest.setFirstDate(
	        Date.from(today.atStartOfDay(zone).toInstant())
	    );
	    leadRequest.setLastDate(
	        Date.from(today.plusDays(1).atStartOfDay(zone).toInstant())
	    );

	    return leadHelper.getAllHotLeadList(leadRequest);
	}

	
	public List<LeadDetails> getFollowupLeadList(Request<LeadRequestObject> leadRequestObject) {

	    LeadRequestObject leadRequest = leadRequestObject.getPayload();

	    LocalDate today = LocalDate.now();
	    ZoneId zone = ZoneId.systemDefault();

//	    switch (leadRequest.getRequestedFor().toUpperCase()) {
//
//	        case "FOLLOWUP_ONE":
//	            // 2 days ago
//	            leadRequest.setFirstDate(Date.from(today.minusDays(1).atStartOfDay(zone).toInstant()));
//	            leadRequest.setLastDate(Date.from(today.atStartOfDay(zone).toInstant()));
//	            break;
//
//	        case "FOLLOWUP_TWO":
//	            // 3 days ago
//	            leadRequest.setFirstDate(Date.from(today.minusDays(2).atStartOfDay(zone).toInstant()));
//	            leadRequest.setLastDate(Date.from(today.minusDays(1).atStartOfDay(zone).toInstant()));
//	            break;
//
//	        case "FOLLOWUP_THREE":
//	            // 4 days ago
//	            leadRequest.setFirstDate(Date.from(today.minusDays(3).atStartOfDay(zone).toInstant()));
//	            leadRequest.setLastDate(Date.from(today.minusDays(2).atStartOfDay(zone).toInstant()));
//	            break;
//	            
//	        case "FOLLOWUP_END":
//	            // 4 days ago
//	            leadRequest.setFirstDate(Date.from(today.minusYears(10).atStartOfDay(zone).toInstant()));
//	            leadRequest.setLastDate(Date.from(today.minusDays(3).atStartOfDay(zone).toInstant()));
//	            break;
//
//	        default:
//	            leadRequest.setFirstDate(getDate.driveDate(RequestFor.TODAY.name()));
//	            leadRequest.setLastDate(getDate.driveDate(RequestFor.NEXT_DATE.name()));
//	    }
	    
	    switch (leadRequest.getRequestedFor().toUpperCase()) {

	    case "FOLLOWUP_ONE":
	        // Yesterday
	        leadRequest.setFirstDate(startOfDay(today.minusDays(1), zone));
	        leadRequest.setLastDate(endOfDay(today.minusDays(1), zone));
	        break;

	    case "FOLLOWUP_TWO":
	        // Day before yesterday
	        leadRequest.setFirstDate(startOfDay(today.minusDays(2), zone));
	        leadRequest.setLastDate(endOfDay(today.minusDays(2), zone));
	        break;

	    case "FOLLOWUP_THREE":
	        // 3 days ago
	        leadRequest.setFirstDate(startOfDay(today.minusDays(3), zone));
	        leadRequest.setLastDate(endOfDay(today.minusDays(3), zone));
	        break;

	    case "FOLLOWUP_END":
	        // Older than 3 days
	        leadRequest.setFirstDate(startOfDay(today.minusYears(10), zone));
	        leadRequest.setLastDate(endOfDay(today.minusDays(3), zone));
	        break;

	    default:
	        leadRequest.setFirstDate(startOfDay(today, zone));
	        leadRequest.setLastDate(endOfDay(today, zone));
	}

	    
	    System.out.println(leadRequest.getFirstDate());
	    System.out.println(leadRequest.getLastDate());
	    return leadHelper.getFollowupLeadList(leadRequest);
	}

	public List<LeadDetails> getPickupLeadList(Request<LeadRequestObject> leadRequestObject) {
		LeadRequestObject leadRequest = leadRequestObject.getPayload();

	    LocalDate today = LocalDate.now();
	    ZoneId zone = ZoneId.systemDefault();
	    Date firstDate = Date.from(today.atStartOfDay(zone).toInstant());

	    switch (leadRequest.getRequestedFor().toUpperCase()) {

	        case "TODAY_PICKUP":
	            // 2 days ago
	            leadRequest.setFirstDate(firstDate);
	            leadRequest.setLastDate(Date.from(today.plusDays(1).atStartOfDay(zone).toInstant()));
	            break;

	        case "TOMORROW_PICKUP":
	            // 3 days ago
	            leadRequest.setFirstDate(Date.from(today.plusDays(1).atStartOfDay(zone).toInstant()));
	            leadRequest.setLastDate(Date.from(today.plusDays(2).atStartOfDay(zone).toInstant()));
	            break;

	        case "AFTER_TOMORROW_PICKUP":
	            // 4 days ago
	            leadRequest.setFirstDate(Date.from(today.plusDays(2).atStartOfDay(zone).toInstant()));
	            leadRequest.setLastDate(Date.from(today.plusDays(3).atStartOfDay(zone).toInstant()));
	            break;

	        case "CUSTOM":
	            leadRequest.setFirstDate(Date.from(leadRequest.getFirstDate().toInstant().atZone(zone).toLocalDate().atStartOfDay(zone).toInstant()));
	            leadRequest.setLastDate(Date.from(leadRequest.getLastDate().toInstant().atZone(zone).toLocalDate().atStartOfDay(zone).toInstant()));
	        default:
//	            leadRequest.setFirstDate(Date.from(leadRequest.getFirstDate().toInstant().atZone(zone).toLocalDate().atStartOfDay(zone).toInstant()));
//	            leadRequest.setLastDate(Date.from(leadRequest.getLastDate().toInstant().atZone(zone).toLocalDate().atStartOfDay(zone).toInstant()));
	    }

	    System.out.println("First Date : " + leadRequest.getFirstDate());
	    System.out.println("Last Date  : " + leadRequest.getLastDate());

		List<LeadDetails> leadList = leadByPickAndDropHelper.getPickupLeadList(leadRequest);
		return leadList;
	}
	
	public List<LeadDetails> getPickupWonLeadList(Request<LeadRequestObject> leadRequestObject) {
		LeadRequestObject leadRequest = leadRequestObject.getPayload();

	    LocalDate today = LocalDate.now();
	    ZoneId zone = ZoneId.systemDefault();
	    Date firstDate = Date.from(today.atStartOfDay(zone).toInstant());

	    switch (leadRequest.getRequestedFor().toUpperCase()) {

	        case "TODAY_PICKUP":
	            // 2 days ago
	            leadRequest.setFirstDate(firstDate);
	            leadRequest.setLastDate(Date.from(today.plusDays(1).atStartOfDay(zone).toInstant()));
	            break;

	        case "TOMORROW_PICKUP":
	            // 3 days ago
	            leadRequest.setFirstDate(Date.from(today.plusDays(1).atStartOfDay(zone).toInstant()));
	            leadRequest.setLastDate(Date.from(today.plusDays(2).atStartOfDay(zone).toInstant()));
	            break;

	        case "AFTER_TOMORROW_PICKUP":
	            // 4 days ago
	            leadRequest.setFirstDate(Date.from(today.plusDays(2).atStartOfDay(zone).toInstant()));
	            leadRequest.setLastDate(Date.from(today.plusDays(3).atStartOfDay(zone).toInstant()));
	            break;

	        case "CUSTOM":
	            leadRequest.setFirstDate(Date.from(leadRequest.getFirstDate().toInstant().atZone(zone).toLocalDate().atStartOfDay(zone).toInstant()));
	            leadRequest.setLastDate(Date.from(leadRequest.getLastDate().toInstant().atZone(zone).toLocalDate().atStartOfDay(zone).toInstant()));
	        default:
//	            leadRequest.setFirstDate(Date.from(leadRequest.getFirstDate().toInstant().atZone(zone).toLocalDate().atStartOfDay(zone).toInstant()));
//	            leadRequest.setLastDate(Date.from(leadRequest.getLastDate().toInstant().atZone(zone).toLocalDate().atStartOfDay(zone).toInstant()));
	    }

	    System.out.println("First Date : " + leadRequest.getFirstDate());
	    System.out.println("Last Date  : " + leadRequest.getLastDate());

		List<LeadDetails> leadList = leadByPickAndDropHelper.getPickupWonLeadList(leadRequest);
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
	    return assignedLeadHelper.getLeadByStatus(leadRequest);
	}
	
	public List<LeadDetails> getEnquiryList(Request<LeadRequestObject> leadRequestObject) {

	    LeadRequestObject leadRequest = leadRequestObject.getPayload();

	    LocalDate today = LocalDate.now();
	    ZoneId zone = ZoneId.systemDefault();

        leadRequest.setFirstDate(Date.from(today.atStartOfDay(zone).toInstant()));
        leadRequest.setLastDate(Date.from(today.plusDays(1).atStartOfDay(zone).toInstant()));

	    return leadByStatusHelper.getEnquiryList(leadRequest);
	}
	
	
//	public List<LeadDetails> getWonLeadList(Request<LeadRequestObject> leadRequestObject) {
//			LeadRequestObject leadRequest = leadRequestObject.getPayload();
//
//		    LocalDate today = LocalDate.now();
//		    ZoneId zone = ZoneId.systemDefault();
//		    Date firstDate = Date.from(today.atStartOfDay(zone).toInstant());
//
//		    switch (leadRequest.getRequestedFor().toUpperCase()) {
//
//		        case "TODAY_WON":
//		            // 2 days ago
//		            leadRequest.setFirstDate(firstDate);
//		            leadRequest.setLastDate(Date.from(today.plusDays(1).atStartOfDay(zone).toInstant()));
//		            break;
//
//		        case "YESTERDAY_WON":
//		            // 3 days ago
//		            leadRequest.setFirstDate(Date.from(today.minusDays(1).atStartOfDay(zone).toInstant()));
//		            leadRequest.setLastDate(Date.from(today.atStartOfDay(zone).toInstant()));
//		            break;
//
//		        case "BEFORE_YESTERDAY_WON":
//		            // 4 days ago
//		            leadRequest.setFirstDate(Date.from(today.minusDays(2).atStartOfDay(zone).toInstant()));
//		            leadRequest.setLastDate(Date.from(today.minusDays(1).atStartOfDay(zone).toInstant()));
//		            break;
//
//		        case "CUSTOM":
//		            leadRequest.setFirstDate(Date.from(leadRequest.getFirstDate().toInstant().atZone(zone).toLocalDate().atStartOfDay(zone).toInstant()));
//		            leadRequest.setLastDate(Date.from(leadRequest.getLastDate().toInstant().atZone(zone).toLocalDate().atStartOfDay(zone).toInstant()));
//		        default:
//		    }
//		    
//		    System.out.println("First Date :"+leadRequest.getFirstDate());
//		    System.out.println("Last Date :"+leadRequest.getLastDate());
//
//			List<LeadDetails> leadList = wonLeadHelper.getWonLeadList(leadRequest);
//			return leadList;
//		}
	
	public List<LeadDetails> getWonLeadList(Request<LeadRequestObject> leadRequestObject) {

	    LeadRequestObject leadRequest = leadRequestObject.getPayload();

	    LocalDate today = LocalDate.now();
	    ZoneId zone = ZoneId.systemDefault();

	    switch (leadRequest.getRequestedFor().toUpperCase()) {

	        case "TODAY_WON":
	            // Today (00:00 today → 00:00 tomorrow)
	            leadRequest.setFirstDate(
	                Date.from(today.atStartOfDay(zone).toInstant())
	            );
	            leadRequest.setLastDate(
	                Date.from(today.plusDays(1).atStartOfDay(zone).toInstant())
	            );
	            break;

	        case "YESTERDAY_WON":
	            // Yesterday (00:00 yesterday → 00:00 today)
	            leadRequest.setFirstDate(
	                Date.from(today.minusDays(1).atStartOfDay(zone).toInstant())
	            );
	            leadRequest.setLastDate(
	                Date.from(today.atStartOfDay(zone).toInstant())
	            );
	            break;

	        case "BEFORE_YESTERDAY_WON":
	            // Day before yesterday
	            leadRequest.setFirstDate(
	                Date.from(today.minusDays(2).atStartOfDay(zone).toInstant())
	            );
	            leadRequest.setLastDate(
	                Date.from(today.minusDays(1).atStartOfDay(zone).toInstant())
	            );
	            break;

	        case "CUSTOM":
	            // Custom range (inclusive of both dates)
	            LocalDate customStart = leadRequest.getFirstDate()
	                    .toInstant()
	                    .atZone(zone)
	                    .toLocalDate();

	            LocalDate customEnd = leadRequest.getLastDate()
	                    .toInstant()
	                    .atZone(zone)
	                    .toLocalDate();

	            leadRequest.setFirstDate(
	                Date.from(customStart.atStartOfDay(zone).toInstant())
	            );
	            leadRequest.setLastDate(
	                Date.from(customEnd.plusDays(1).atStartOfDay(zone).toInstant())
	            );
	            break;

	        default:
	            throw new IllegalArgumentException(
	                "Invalid requestedFor value: " + leadRequest.getRequestedFor()
	            );
	    }

	    System.out.println("First Date : " + leadRequest.getFirstDate());
	    System.out.println("Last Date  : " + leadRequest.getLastDate());

	    return wonLeadHelper.getWonLeadList(leadRequest);
	}


	
	public List<LeadDetails> getWebsiteLeadList(Request<LeadRequestObject> leadRequestObject) {
		LeadRequestObject leadRequest = leadRequestObject.getPayload();

		List<LeadDetails> leadList = websiteLeadHelper.getWebsiteLeadList(leadRequest);
		return leadList;
	}



	public int updateStatusToLost() {

	    LocalDate today = LocalDate.now(ZoneId.systemDefault());
	    LocalDateTime endOfPrevDay = today.minusDays(1).atTime(23, 59, 59);

	    Date cutoffDate = Date.from(endOfPrevDay.atZone(ZoneId.systemDefault()).toInstant());
	    return leadByStatusHelper.updateStatusToLost(cutoffDate);
	}

	
	
//	public List<LeadDetails> updateStatusToLost() {
//
//	    LeadRequestObject leadRequest = new LeadRequestObject();
//
//		LocalDate today = LocalDate.now(ZoneId.systemDefault());
//
//		// end of previous day → 23:59:59.999
//		LocalDateTime endOfPrevDay = today.minusDays(1).atTime(23, 59, 59);
//
//		Date cutoffDate = Date.from(endOfPrevDay.atZone(ZoneId.systemDefault()).toInstant());
//
//		leadRequest.setFirstDate(cutoffDate);
//
//		System.out.println("First Date : " + leadRequest.getFirstDate());
//   
//	    return leadByStatusHelper.getLostCandidateLeads(leadRequest);
//	}




}
