package com.datfusrental.services;

import java.util.List;

import javax.transaction.Transactional;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datfusrental.constant.Constant;
import com.datfusrental.entities.LocationDetails;
import com.datfusrental.enums.Status;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.LocationHelper;
import com.datfusrental.jwt.JwtTokenUtil;
import com.datfusrental.object.request.LocationRequestObject;
import com.datfusrental.object.request.Request;

@Service
public class LocationService {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private LocationHelper locationHelper;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Transactional
	public LocationRequestObject addLocation(Request<LocationRequestObject> itemRequestObject) throws BizException, Exception {
		LocationRequestObject itemRequest = itemRequestObject.getPayload();
		locationHelper.validateLocationRequest(itemRequest);

		Boolean isValid = jwtTokenUtil.validateJwtToken(itemRequest.getLoginId(), itemRequest.getToken());
		if (isValid) {
			LocationDetails existsLocationDetails = locationHelper.getLocationDetailsByType(itemRequest.getLocation(),
					itemRequest.getLocationType(), itemRequest.getSuperadminId());
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
		} else {
			itemRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
			itemRequest.setRespMesg(Constant.INVALID_TOKEN);
			return itemRequest;
		}
	}

	public LocationRequestObject updateLocation(Request<LocationRequestObject> itemRequestObject)
			throws BizException, Exception {
		LocationRequestObject itemRequest = itemRequestObject.getPayload();
		locationHelper.validateLocationRequest(itemRequest);

		Boolean isValid = jwtTokenUtil.validateJwtToken(itemRequest.getLoginId(), itemRequest.getToken());
		if (isValid) {

			LocationDetails locationDetails = locationHelper.getLocationDetailsById(itemRequest.getId());
			if (locationDetails != null) {

				locationDetails = locationHelper.getUpdatedLocationDetailsByReqObj(itemRequest, locationDetails);
				locationDetails = locationHelper.updateLocationDetails(locationDetails);

				itemRequest.setRespCode(Constant.SUCCESS_CODE);
				itemRequest.setRespMesg(Constant.UPDATED_SUCCESS);
				return itemRequest;
			} else {
				itemRequest.setRespCode(Constant.NOT_EXISTS);
				itemRequest.setRespMesg(Constant.NOT_EXIST_MSG);
				return itemRequest;
			}
		} else {
			itemRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
			itemRequest.setRespMesg(Constant.INVALID_TOKEN);
			return itemRequest;
		}
	}

	public LocationRequestObject changeLocationStatus(Request<LocationRequestObject> itemRequestObject)
			throws BizException, Exception {
		LocationRequestObject itemRequest = itemRequestObject.getPayload();
		locationHelper.validateLocationRequest(itemRequest);

		Boolean isValid = jwtTokenUtil.validateJwtToken(itemRequest.getLoginId(), itemRequest.getToken());
		if (isValid) {
			LocationDetails locationDetails = locationHelper.getLocationDetailsById(itemRequest.getId());
			if (locationDetails != null) {
				if (locationDetails.getStatus().equalsIgnoreCase(Status.INACTIVE.name())) {
					locationDetails.setStatus(Status.ACTIVE.name());
				} else {
					locationDetails.setStatus(Status.INACTIVE.name());
				}
				locationHelper.updateLocationDetails(locationDetails);
			} else {
				itemRequest.setRespCode(Constant.NOT_EXISTS);
				itemRequest.setRespMesg(Constant.NOT_EXIST_MSG);
				return itemRequest;
			}
		} else {
			itemRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
			itemRequest.setRespMesg(Constant.INVALID_TOKEN);
			return itemRequest;
		}
		return itemRequest;

	}

	public List<LocationDetails> getLocationDetails(Request<LocationRequestObject> locationRequestObject) {
		LocationRequestObject locationRequest = locationRequestObject.getPayload();
		List<LocationDetails> locationDetails = locationHelper.getLocationDetails(locationRequest);
		return locationDetails;
	}

}