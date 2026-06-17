package com.datfusrental.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datfusrental.constant.Constant;
import com.datfusrental.entities.VendorDetails;
import com.datfusrental.enums.Status;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.VendorHelper;
import com.datfusrental.object.request.Request;
import com.datfusrental.object.request.UserRequestObject;


@Service
public class VendorService {
	
	@Autowired
	private VendorHelper vendorHelper;
	
	
	public UserRequestObject changeVendorStatus(Request<UserRequestObject> userRequestObject)
			throws BizException, Exception {
		UserRequestObject userRequest = userRequestObject.getPayload();
		vendorHelper.validateUserRequest(userRequest);

		VendorDetails vendorDetails = vendorHelper.getVendorDetailsById(userRequest.getId());
		if (vendorDetails != null) {
			if (vendorDetails.getStatus().equalsIgnoreCase(Status.ACTIVE.name())) {
				vendorDetails.setStatus(Status.INACTIVE.name());
			} else {
				vendorDetails.setStatus(Status.ACTIVE.name());
			}
			vendorHelper.UpdateVendorDetails(vendorDetails);
			
			userRequest.setRespCode(Constant.SUCCESS_CODE);
			userRequest.setRespMesg(Constant.UPDATED_SUCCESS);
			return userRequest;
		} else {
			userRequest.setRespCode(Constant.BAD_REQUEST_CODE);
			userRequest.setRespMesg(Constant.NOT_EXIST_MSG);
			return userRequest;
		}
	}

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
			vendorDetails = vendorHelper.UpdateVendorDetails(vendorDetails);
			
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
