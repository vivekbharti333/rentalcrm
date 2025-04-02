package com.datfusrental.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.datfusrental.constant.Constant;
import com.datfusrental.entities.LocationDetails;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.LocationRequestObject;
import com.datfusrental.object.request.Request;
import com.datfusrental.object.response.GenricResponse;
import com.datfusrental.object.response.Response;
import com.datfusrental.services.LocationService;

@CrossOrigin(origins = "*")
@RestController
public class LocationController {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private LocationService locationService;

	
	@RequestMapping(path = "addLocation", method = RequestMethod.POST)
	public Response<LocationRequestObject> addLocation(@RequestBody Request<LocationRequestObject> locationRequestObject,
			HttpServletRequest request) {
		GenricResponse<LocationRequestObject> responseObj = new GenricResponse<LocationRequestObject>();
		try {
			LocationRequestObject responce = locationService.addLocation(locationRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}

	@RequestMapping(path = "updateLocation", method = RequestMethod.POST)
	public Response<LocationRequestObject> updateLocation(@RequestBody Request<LocationRequestObject> locationRequestObject,
			HttpServletRequest request) {
		GenricResponse<LocationRequestObject> responseObj = new GenricResponse<LocationRequestObject>();
		try {
			LocationRequestObject responce = locationService.updateLocation(locationRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}

	@RequestMapping(path = "changeLocationStatus", method = RequestMethod.POST)
	public Response<LocationRequestObject> changeLocationStatus(@RequestBody Request<LocationRequestObject> locationRequestObject,
			HttpServletRequest request) {
		GenricResponse<LocationRequestObject> responseObj = new GenricResponse<LocationRequestObject>();
		try {
			LocationRequestObject responce = locationService.changeLocationStatus(locationRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}

	@RequestMapping(path = "getLocationDetails", method = RequestMethod.POST)
	public Response<LocationDetails> getLocationDetails(@RequestBody Request<LocationRequestObject> locationRequestObject) {
		GenricResponse<LocationDetails> response = new GenricResponse<LocationDetails>();
		try {
			List<LocationDetails> locationDetailsList = locationService.getLocationDetails(locationRequestObject);
//			return response.createListResponse(locationDetailsList, 200);
			return response.createListResponse(locationDetailsList, 200, String.valueOf(locationDetailsList.size()));
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}

}
