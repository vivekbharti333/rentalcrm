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
import com.datfusrental.entities.TransactionDetails;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.LeadRequestObject;
import com.datfusrental.object.request.Request;
import com.datfusrental.object.response.GenricResponse;
import com.datfusrental.object.response.Response;
import com.datfusrental.services.LeadService;
import com.datfusrental.services.TransactionService;

@CrossOrigin(origins = "*")
@RestController
public class TransactionController {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private TransactionService transactionService;
	

	
	
	
	@RequestMapping(path = "getTransactionDetailsByVendorId", method = RequestMethod.POST)
	public Response<TransactionDetails> getTransactionDetailsByVendorId(@RequestBody Request<LeadRequestObject> leadRequestObject) {
		GenricResponse<TransactionDetails> response = new GenricResponse<TransactionDetails>();
		try {
			List<TransactionDetails> transactionList = transactionService.getTransactionDetailsByVendorId(leadRequestObject);
			return response.createListResponse(transactionList, 200, String.valueOf(transactionList.size()));
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}
	
	
	
	
	
	
	
}
