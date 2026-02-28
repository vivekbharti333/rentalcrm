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
import com.datfusrental.entities.User;
import com.datfusrental.entities.VendorDetails;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.Request;
import com.datfusrental.object.request.UserRequestObject;
import com.datfusrental.object.response.GenricResponse;
import com.datfusrental.object.response.Response;
import com.datfusrental.services.VendorService;

@CrossOrigin(origins = "*")
@RestController
public class VendorController {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	VendorService vendorService;

	@RequestMapping(path = "vendorRegistration", method = RequestMethod.POST)
	public Response<UserRequestObject> vendorRegistration(@RequestBody Request<UserRequestObject> userRequestObject,
			HttpServletRequest request) {
		GenricResponse<UserRequestObject> responseObj = new GenricResponse<UserRequestObject>();
		try {
			UserRequestObject responce = vendorService.vendorRegistration(userRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}

	@RequestMapping(path = "updateVendorDetails", method = RequestMethod.POST)
	public Response<UserRequestObject> updateUserDetails(@RequestBody Request<UserRequestObject> userRequestObject,
			HttpServletRequest request) {
		GenricResponse<UserRequestObject> responseObj = new GenricResponse<UserRequestObject>();
		try {

			UserRequestObject responce = vendorService.updateVendorDetails(userRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}

	@RequestMapping(path = "getVendorDetails", method = RequestMethod.POST)
	public Response<VendorDetails> getUserDetails(@RequestBody Request<UserRequestObject> userRequestObject) {
		GenricResponse<VendorDetails> response = new GenricResponse<VendorDetails>();
		try {
			List<VendorDetails> userList = vendorService.getVendorDetails(userRequestObject);
			return response.createListResponse(userList, 200, String.valueOf(userList.size()));
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}

}
