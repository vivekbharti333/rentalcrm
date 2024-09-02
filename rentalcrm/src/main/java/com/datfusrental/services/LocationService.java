package com.datfusrental.services;

import java.io.File;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datfusrental.common.GetBase64Image;
import com.datfusrental.constant.Constant;
import com.datfusrental.entities.CategoryDetails;
import com.datfusrental.entities.CategoryType;
import com.datfusrental.entities.LocationDetails;
import com.datfusrental.entities.SuperCategoryDetails;
import com.datfusrental.enums.ImageType;
import com.datfusrental.enums.Status;
import com.datfusrental.entities.SubCategoryDetails;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.LocationHelper;
import com.datfusrental.jwt.JwtTokenUtil;
import com.datfusrental.object.request.ItemRequestObject;
import com.datfusrental.object.request.Request;

@Service
public class LocationService {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private LocationHelper locationHelper;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	
	@Transactional
	public ItemRequestObject addLocation(Request<ItemRequestObject> itemRequestObject) 
			throws BizException, Exception {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		locationHelper.validateItemRequest(itemRequest);
		
		Boolean isValid = jwtTokenUtil.validateJwtToken(itemRequest.getLoginId(), itemRequest.getToken());
//		if (!isValid) {
			LocationDetails existsLocationDetails = locationHelper.getLocationDetailsByType(itemRequest.getLocation(), itemRequest.getLocationType(), itemRequest.getSuperadminId());
			if (existsLocationDetails == null) {
				LocationDetails locationDetails = locationHelper.getLocationDetailsByReqObj(itemRequest);
				locationDetails = locationHelper.saveLocationDetails(locationDetails);

				itemRequest.setRespCode(Constant.SUCCESS_CODE);
				itemRequest.setRespMesg(Constant.REGISTERED_SUCCESS);
				return itemRequest;
			} else {
				itemRequest.setRespCode(Constant.ALREADY_EXISTS);
				itemRequest.setRespMesg(Constant.ALREADY_EXISTS_MSG);
				return itemRequest;
			}
//		} else {
//			itemRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
//			itemRequest.setRespMesg(Constant.INVALID_TOKEN);
//			return itemRequest;
//		}
	}
	
	
	
	
//	@Transactional
//	public ItemRequestObject changeCategoryTypeStatus(Request<ItemRequestObject> itemRequestObject) 
//			throws BizException, Exception {
//		ItemRequestObject itemRequest = itemRequestObject.getPayload();
//		locationHelper.validateItemRequest(itemRequest);
//		
//		Boolean isValid = jwtTokenUtil.validateJwtToken(itemRequest.getLoginId(), itemRequest.getToken());
//		if (!isValid) {
//
//			CategoryType categoryType = locationHelper.getCategoryTypeById(itemRequest.getCategoryTypeId());
//			if (categoryType != null) {
//
//				if(categoryType.getStatus().equalsIgnoreCase(Status.INACTIVE.name())) {
//					categoryType.setStatus(Status.ACTIVE.name());
//				}else {
//					categoryType.setStatus(Status.INACTIVE.name());
//				}
//				categoryType = locationHelper.updateCategoryType(categoryType);
//
//				itemRequest.setRespCode(Constant.SUCCESS_CODE);
//				itemRequest.setRespMesg(Constant.UPDATED_SUCCESS);
//				return itemRequest;
//			} else {
//				itemRequest.setRespCode(Constant.NOT_EXISTS);
//				itemRequest.setRespMesg(Constant.DATA_NOT_FOUND);
//				return itemRequest;
//			}
//		} else {
//			itemRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
//			itemRequest.setRespMesg(Constant.INVALID_TOKEN);
//			return itemRequest;
//		}
//	}

	public List<LocationDetails> getLocationDetails(Request<ItemRequestObject> itemRequestObject) {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		List<LocationDetails> locationDetails = locationHelper.getLocationDetails(itemRequest);
		return locationDetails;
	}

	






	


}