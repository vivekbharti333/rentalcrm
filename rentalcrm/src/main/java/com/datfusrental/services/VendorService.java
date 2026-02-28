package com.datfusrental.services;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.datfusrental.constant.Constant;
import com.datfusrental.entities.AddressDetails;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.entities.LeadDetailsHistory;
import com.datfusrental.entities.User;
import com.datfusrental.entities.VendorDetails;
import com.datfusrental.enums.Status;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.AddressHelper;
import com.datfusrental.helper.UserHelper;
import com.datfusrental.helper.VendorHelper;
import com.datfusrental.jwt.JwtTokenUtil;
import com.datfusrental.object.request.AddressRequestObject;
import com.datfusrental.object.request.LoginRequestObject;
import com.datfusrental.object.request.Request;
import com.datfusrental.object.request.UserRequestObject;


@Service
public class VendorService {
	
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private VendorHelper vendorHelper;

	
	



	@Transactional
	public UserRequestObject vendorRegistration(Request<UserRequestObject> userRequestObject)
			throws BizException, Exception {
		UserRequestObject userRequest = userRequestObject.getPayload();
		vendorHelper.validateUserRequest(userRequest);


		VendorDetails existsVendorDetails = vendorHelper.getVendorDetailsByWhatsAppNumber(userRequest.getWhatsappNo());
		if (existsVendorDetails == null) {

			VendorDetails vendorDetails = vendorHelper.getVendorDetailsByReqObj(userRequest);
			vendorDetails = vendorHelper.saveVendorDetails(vendorDetails);
			
			userRequest.setRespCode(Constant.SUCCESS_CODE);
			userRequest.setRespMesg(Constant.REGISTERED_SUCCESS);
			return userRequest;
		} else {
			userRequest.setRespCode(Constant.BAD_REQUEST_CODE);
			userRequest.setRespMesg(Constant.USER_EXIST);
			return userRequest;
		}
	}
	
	@Transactional
	public UserRequestObject updateVendorDetails(Request<UserRequestObject> userRequestObject)
			throws BizException, Exception {
		UserRequestObject userRequest = userRequestObject.getPayload();
		vendorHelper.validateUserRequest(userRequest);


		VendorDetails vendorDetails = vendorHelper.getVendorDetailsById(userRequest.getId());
		if (vendorDetails != null) {

			vendorDetails = vendorHelper.getUpdatedVendorDetailsByReqObj(vendorDetails, userRequest);
			vendorDetails = vendorHelper.saveVendorDetails(vendorDetails);
			
			userRequest.setRespCode(Constant.SUCCESS_CODE);
			userRequest.setRespMesg(Constant.UPDATED_SUCCESS);
			return userRequest;
		} else {
			userRequest.setRespCode(Constant.BAD_REQUEST_CODE);
			userRequest.setRespMesg(Constant.NOT_EXIST_USER);
			return userRequest;
		}
	}

	
	
	public List<VendorDetails> getVendorDetails(Request<UserRequestObject> userRequestObject) {
		UserRequestObject userRequest = userRequestObject.getPayload();
		List<VendorDetails> vendorList = vendorHelper.getVendorDetails(userRequest);
		return vendorList;
	}
	
	
	







	
}
