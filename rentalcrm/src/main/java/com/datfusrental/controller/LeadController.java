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

@CrossOrigin(origins = "*")
@RestController
public class LeadController {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private LeadService leadService;
	
	
	@RequestMapping(path = "changeLeadStatus", method = RequestMethod.POST)
	public Response<LeadRequestObject> changeLeadStatus(@RequestBody Request<LeadRequestObject> leadRequestObject,
			HttpServletRequest request) {
		GenricResponse<LeadRequestObject> responseObj = new GenricResponse<LeadRequestObject>();
		try {
			LeadRequestObject responce = leadService.changeLeadStatus(leadRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}

	@RequestMapping(path = "registerLead", method = RequestMethod.POST)
	public Response<LeadRequestObject> registerLead(@RequestBody Request<LeadRequestObject> leadRequestObject,
			HttpServletRequest request) {
		GenricResponse<LeadRequestObject> responseObj = new GenricResponse<LeadRequestObject>();
		try {
			LeadRequestObject responce = leadService.registerLead(leadRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}
	
	@RequestMapping(path = "updatePaymentDetails", method = RequestMethod.POST)
	public Response<LeadRequestObject> updatePaymentDetails(@RequestBody Request<LeadRequestObject> leadRequestObject,
			HttpServletRequest request) {
		GenricResponse<LeadRequestObject> responseObj = new GenricResponse<LeadRequestObject>();
		try {
			LeadRequestObject responce = leadService.updatePaymentDetails(leadRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}
	
	
	@RequestMapping(path = "updateLead", method = RequestMethod.POST)
	public Response<LeadRequestObject> updateLead(@RequestBody Request<LeadRequestObject> leadRequestObject,
			HttpServletRequest request) {
		GenricResponse<LeadRequestObject> responseObj = new GenricResponse<LeadRequestObject>();
		try {
			LeadRequestObject responce = leadService.updateLead(leadRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}
	
	@RequestMapping(path = "getLeadHistoryById", method = RequestMethod.POST)
	public Response<LeadDetailsHistory> getLeadHistoryById(@RequestBody Request<LeadRequestObject> leadRequestObject,
			@RequestHeader(value = "Authorization", required = false) String authHeader) {
		GenricResponse<LeadDetailsHistory> response = new GenricResponse<LeadDetailsHistory>();
		try {
			List<LeadDetailsHistory> followupOneList = leadService.getLeadHistoryById(leadRequestObject);
			return response.createListResponse(followupOneList, 200, String.valueOf(followupOneList.size()));
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}
	
//	@RequestMapping(path = "getLeadListByStatus", method = RequestMethod.POST)
//	public Response<LeadDetails> getLeadListByStatus(@RequestBody Request<LeadRequestObject> leadRequestObject,
//			@RequestHeader(value = "Authorization", required = false) String authHeader) {
//		GenricResponse<LeadDetails> response = new GenricResponse<LeadDetails>();
//		try {
//			List<LeadDetails> followupOneList = leadService.getLeadListByStatus(leadRequestObject);
//			return response.createListResponse(followupOneList, 200, String.valueOf(followupOneList.size()));
//		} catch (Exception e) {
//			e.printStackTrace();
//			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
//		}
//	}
	
	@RequestMapping(path = "getLeadListByStatus", method = RequestMethod.POST)
	public Response<LeadDetails> getLeadListByStatus(
	        @RequestBody Request<LeadRequestObject> leadRequestObject,
	        @RequestHeader(value = "Authorization", required = false) String authHeader) {

	    GenricResponse<LeadDetails> response = new GenricResponse<>();

	    try {
	        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	            return response.createErrorResponse(401, "Token missing or invalid");
	        }

	        String token = authHeader.substring(7); // remove "Bearer "

	        // TODO: validate token here
	        System.out.println("Token = " + token);

	        List<LeadDetails> followupOneList = leadService.getLeadListByStatus(leadRequestObject);
	        return response.createListResponse(followupOneList, 200, String.valueOf(followupOneList.size()));
	    } catch (Exception e) {
	        e.printStackTrace();
	        return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
	    }
	}

	
	@RequestMapping(path = "getAllLeadList", method = RequestMethod.POST)
	public Response<LeadDetails> getAllLeadList(@RequestBody Request<LeadRequestObject> leadRequestObject) {
		GenricResponse<LeadDetails> response = new GenricResponse<LeadDetails>();
		try {
			List<LeadDetails> followupOneList = leadService.getAllLeadList(leadRequestObject);
			return response.createListResponse(followupOneList, 200, String.valueOf(followupOneList.size()));
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}
	
	@RequestMapping(path = "getAllHotLeadList", method = RequestMethod.POST)
	public Response<LeadDetails> getAllHotLeadList(@RequestBody Request<LeadRequestObject> leadRequestObject) {
		GenricResponse<LeadDetails> response = new GenricResponse<LeadDetails>();
		try {
			List<LeadDetails> followupOneList = leadService.getAllHotLeadList(leadRequestObject);
			return response.createListResponse(followupOneList, 200, String.valueOf(followupOneList.size()));
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}
	
	@RequestMapping(path = "getFollowupLeadList", method = RequestMethod.POST)
	public Response<LeadDetails> getFollowupLeadList(@RequestBody Request<LeadRequestObject> leadRequestObject) {
		GenricResponse<LeadDetails> response = new GenricResponse<LeadDetails>();
		try {
			List<LeadDetails> followupOneList = leadService.getFollowupLeadList(leadRequestObject);
			return response.createListResponse(followupOneList, 200, String.valueOf(followupOneList.size()));
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}
	
	@RequestMapping(path = "getPickupLeadList", method = RequestMethod.POST)
	public Response<LeadDetails> getPickAndDropLeadList(@RequestBody Request<LeadRequestObject> leadRequestObject) {
		GenricResponse<LeadDetails> response = new GenricResponse<LeadDetails>();
		try {
			List<LeadDetails> followupOneList = leadService.getPickupLeadList(leadRequestObject);
			return response.createListResponse(followupOneList, 200, String.valueOf(followupOneList.size()));
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}
	
	@RequestMapping(path = "getPickupWonLeadList", method = RequestMethod.POST)
	public Response<LeadDetails> getPickupWonLeadList(@RequestBody Request<LeadRequestObject> leadRequestObject) {
		GenricResponse<LeadDetails> response = new GenricResponse<LeadDetails>();
		try {
			List<LeadDetails> followupOneList = leadService.getPickupWonLeadList(leadRequestObject);
			return response.createListResponse(followupOneList, 200, String.valueOf(followupOneList.size()));
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}
	
	
	@RequestMapping(path = "getDropLeadList", method = RequestMethod.POST)
	public Response<LeadDetails> getDropLeadList(@RequestBody Request<LeadRequestObject> leadRequestObject) {
		GenricResponse<LeadDetails> response = new GenricResponse<LeadDetails>();
		try {
			List<LeadDetails> followupOneList = leadService.getDropLeadList(leadRequestObject);
			return response.createListResponse(followupOneList, 200, String.valueOf(followupOneList.size()));
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}
	
	@RequestMapping(path = "getLeadByStatus", method = RequestMethod.POST)
	public Response<LeadDetails> getLeadByStatus(@RequestBody Request<LeadRequestObject> leadRequestObject) {
		GenricResponse<LeadDetails> response = new GenricResponse<LeadDetails>();
		try {
			List<LeadDetails> followupOneList = leadService.getLeadByStatus(leadRequestObject);
			return response.createListResponse(followupOneList, 200, String.valueOf(followupOneList.size()));
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}
	
	
	
	
	
}
