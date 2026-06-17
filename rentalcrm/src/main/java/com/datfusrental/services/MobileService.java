package com.datfusrental.services;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datfusrental.entities.CategoryType;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.helper.MobileHelper;
import com.datfusrental.object.request.ItemRequestObject;
import com.datfusrental.object.request.LeadRequestObject;
import com.datfusrental.object.request.Request;


@Service
public class MobileService {

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
	
	public List<CategoryType> getMobileActivityCategoryType(Request<ItemRequestObject> itemRequestObject) {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		List<CategoryType> categoryType = mobileHelper.getMobileActivityCategoryType(itemRequest);
		return categoryType;
	}

	
	public List<LeadDetails> getLeadListByCategoryTypeName(Request<LeadRequestObject> leadRequestObject) {

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
		return mobileHelper.getLeadListByCategoryTypeName(leadRequest);
	}

	public List<LeadDetails> getUpdatedLeadList(Request<LeadRequestObject> leadRequestObject) {
		LeadRequestObject leadRequest = leadRequestObject.getPayload();
		
		
		return mobileHelper.getUpdatedLeadList(leadRequest);
	}






	

	
	



}
