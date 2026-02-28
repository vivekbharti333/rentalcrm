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
import com.datfusrental.helper.MobileHelper;
import com.datfusrental.helper.WebsiteLeadHelper;
import com.datfusrental.helper.WonLeadHelper;
import com.datfusrental.object.request.LeadRequestObject;
import com.datfusrental.object.request.Request;
import com.datfusrental.paymentgateways.CashfreePaymentGateways;
import com.datfusrental.util.EntityDiffUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class MobileService {
	
	
	private Date startOfDay(LocalDate date, ZoneId zone) {
	    return Date.from(date.atStartOfDay(zone).toInstant());
	}

	private Date endOfDay(LocalDate date, ZoneId zone) {
	    return Date.from(date.atTime(23, 59, 59).atZone(zone).toInstant());
	}


	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private MobileHelper mobileHelper;
	


	
	public List<LeadDetails> getMobileInstantList(Request<LeadRequestObject> leadRequestObject) {

		LeadRequestObject leadRequest = leadRequestObject.getPayload();

		LocalDate today = LocalDate.now();
		ZoneId zone = ZoneId.systemDefault();

		switch (leadRequest.getRequestedFor().toUpperCase()) {

		case "TODAY":
			// Today (00:00 today → 00:00 tomorrow)
			leadRequest.setFirstDate(Date.from(today.atStartOfDay(zone).toInstant()));
			leadRequest.setLastDate(Date.from(today.plusDays(1).atStartOfDay(zone).toInstant()));
			break;

		case "TOMORROW":
			// Yesterday (00:00 yesterday → 00:00 today)
			leadRequest.setFirstDate(Date.from(today.plusDays(1).atStartOfDay(zone).toInstant()));
			leadRequest.setLastDate(Date.from(today.plusDays(2).atStartOfDay(zone).toInstant()));
			break;

		case "AFTER_TOMORROW":
			// Day before yesterday
			leadRequest.setFirstDate(Date.from(today.plusDays(2).atStartOfDay(zone).toInstant()));
			leadRequest.setLastDate(Date.from(today.plusDays(3).atStartOfDay(zone).toInstant()));
			break;

		case "CUSTOM":
			// Custom range (inclusive of both dates)
			LocalDate customStart = leadRequest.getFirstDate().toInstant().atZone(zone).toLocalDate();
			LocalDate customEnd = leadRequest.getLastDate().toInstant().atZone(zone).toLocalDate();

			leadRequest.setFirstDate(Date.from(customStart.atStartOfDay(zone).toInstant()));
			leadRequest.setLastDate(Date.from(customEnd.plusDays(1).atStartOfDay(zone).toInstant()));
			break;

		default:
			throw new IllegalArgumentException("Invalid requestedFor value: " + leadRequest.getRequestedFor());
		}

		System.out.println("First Date : " + leadRequest.getFirstDate());
		System.out.println("Last Date  : " + leadRequest.getLastDate());

		return mobileHelper.getMobileInstantList(leadRequest);
	}





	

	
	



}
