package com.datfusrental.services;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datfusrental.constant.Constant;
import com.datfusrental.entities.ApplicationHeaderDetails;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.ApplicationHelper;
import com.datfusrental.jwt.JwtTokenUtil;
import com.datfusrental.object.request.ApplicationRequestObject;
import com.datfusrental.object.request.Request;


@Service
public class ApplicationService {
	
	@Autowired
	private ApplicationHelper applicationHelper;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	public HttpServletRequest request;
	
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	
	
	@Transactional
	public ApplicationRequestObject addUpdateApplicationHeader(Request<ApplicationRequestObject> applicationRequestObject)
			throws BizException, Exception {
		ApplicationRequestObject applicationRequest = applicationRequestObject.getPayload();
		applicationHelper.validateApplicationRequest(applicationRequest);
		
//		Boolean isValid = jwtTokenUtil.validateJwtToken(applicationRequest.getCreatedBy(), donationRequest.getToken());
//		logger.info("Add Donation. Is valid? : " + donationRequest.getLoginId() + " is " + isValid);
//
//		if (isValid) {
		
		ApplicationHeaderDetails applicationHeaderdetails = applicationHelper.getApplicationHeaderDetailsBySuperadminId(applicationRequest.getSuperadminId());
		if(applicationHeaderdetails == null) {
			
			ApplicationHeaderDetails applicationHeader = applicationHelper.getApplicationDetailsByReqObj(applicationRequest);
			applicationHeader = applicationHelper.saveApplicationHeaderDetails(applicationHeader);

			applicationRequest.setRespCode(Constant.SUCCESS_CODE);
			applicationRequest.setRespMesg("Successfully Register");
			return applicationRequest;
		}else {
			applicationHeaderdetails = applicationHelper.getUpdatedApplicationDetailsByReqObj(applicationRequest, applicationHeaderdetails);
			applicationHeaderdetails = applicationHelper.updateApplicationHeaderDetails(applicationHeaderdetails);
			
			applicationRequest.setRespCode(Constant.SUCCESS_CODE);
			applicationRequest.setRespMesg("Update Successfully");
			return applicationRequest;
		}	
	}

	public ApplicationRequestObject getApplicationHeaderDetailsBySuperadminId(Request<ApplicationRequestObject> applicationRequestObject) 
			throws BizException, Exception {
		ApplicationRequestObject applicationRequest = applicationRequestObject.getPayload();
		applicationHelper.validateApplicationRequest(applicationRequest);
		
		ApplicationHeaderDetails applicationHeaderdetails = applicationHelper.getApplicationHeaderDetailsBySuperadminId(applicationRequest.getSuperadminId());
		if(applicationHeaderdetails != null) {
			
			applicationRequest.setDisplayLogo(applicationHeaderdetails.getDisplayLogo());
			applicationRequest.setDisplayName(applicationHeaderdetails.getDisplayName());
			applicationRequest.setWebsite(applicationHeaderdetails.getWebsite());
			applicationRequest.setEmailId(applicationHeaderdetails.getEmailId());
			applicationRequest.setPhoneNumber(applicationHeaderdetails.getPhoneNumber());
			
			applicationRequest.setRespCode(Constant.SUCCESS_CODE);
			applicationRequest.setRespMesg("Successfully");
			return applicationRequest;
		}
		
		return null;
	}
	
	public ApplicationRequestObject getApplicationDetailsByIpAddress(Request<ApplicationRequestObject> applicationRequestObject) 
			throws BizException {
		ApplicationRequestObject applicationRequest = applicationRequestObject.getPayload();
		applicationHelper.validateApplicationRequest(applicationRequest);
		
		applicationRequest.setIpAddress(request.getHeader("X-Forwarded-For") != null ? request.getHeader("X-Forwarded-For") : request.getRemoteAddr());
		System.out.println(applicationRequest.getIpAddress()+" IP Address");
		ApplicationHeaderDetails applicationHeaderdetails = applicationHelper.getApplicationHeaderDetailsByIpAddress(applicationRequest.getIpAddress());
		if(applicationHeaderdetails != null) {
			
			applicationRequest.setLoginPageWallpaper(applicationHeaderdetails.getLoginPageWallpaper());
			applicationRequest.setLoginPageLogo(applicationHeaderdetails.getLoginPageLogo());
			
			applicationRequest.setRespCode(Constant.SUCCESS_CODE);
			applicationRequest.setRespMesg("Successfully");
			return applicationRequest;
		}
		
		return null;
	}
	

	public List<ApplicationHeaderDetails> getApplicationHeaderDetails(Request<ApplicationRequestObject> applicationRequestObject) {
		ApplicationRequestObject applicationRequest = applicationRequestObject.getPayload();
		List<ApplicationHeaderDetails> applicationHeaderDetails = applicationHelper.getApplicationHeaderDetailsBySuperadminId(applicationRequest);
		return applicationHeaderDetails;

	}

	





	
	
	

}

