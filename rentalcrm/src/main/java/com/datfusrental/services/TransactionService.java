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
import com.datfusrental.entities.TransactionDetails;
import com.datfusrental.entities.VendorDetails;
import com.datfusrental.enums.RequestFor;
import com.datfusrental.enums.Status;
import com.datfusrental.enums.TransactionType;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.AssignedLeadHelper;
import com.datfusrental.helper.LeadByPickAndDropHelper;
import com.datfusrental.helper.LeadByStatusHelper;
import com.datfusrental.helper.LeadDetailsHistoryHelper;
import com.datfusrental.helper.LeadHelper;
import com.datfusrental.helper.LocationHelper;
import com.datfusrental.helper.TransactionHelper;
import com.datfusrental.helper.VendorHelper;
import com.datfusrental.helper.WebsiteLeadHelper;
import com.datfusrental.helper.WonLeadHelper;
import com.datfusrental.object.request.LeadRequestObject;
import com.datfusrental.object.request.Request;
import com.datfusrental.paymentgateways.CashfreePaymentGateways;
import com.datfusrental.util.EntityDiffUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class TransactionService {
	
	
	private Date startOfDay(LocalDate date, ZoneId zone) {
	    return Date.from(date.atStartOfDay(zone).toInstant());
	}

	private Date endOfDay(LocalDate date, ZoneId zone) {
	    return Date.from(date.atTime(23, 59, 59).atZone(zone).toInstant());
	}


	private final Logger logger = Logger.getLogger(this.getClass().getName());

	
	@Autowired
	private TransactionHelper transactionHelper;
	
	@Autowired
	private CashfreePaymentGateways cashfreePaymentGateways;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	
	public List<TransactionDetails> getTransactionDetailsByVendorId(Request<LeadRequestObject> leadRequestObject) {
		LeadRequestObject leadRequest = leadRequestObject.getPayload();
		List<TransactionDetails> leadList = transactionHelper.getTransactionDetailsByVendorId(leadRequest);
		return leadList;
	}


}
