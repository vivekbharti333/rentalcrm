package com.datfusrental.services;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datfusrental.constant.Constant;
import com.datfusrental.entities.OtpDetails;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.OtpHelper;
import com.datfusrental.object.request.LeadRequestObject;
import com.datfusrental.object.request.OtpRequestObject;
import com.datfusrental.object.request.Request;

@Service
public class OtpService {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private OtpHelper otpHelper;

	public OtpRequestObject generateOtp(Request<OtpRequestObject> otpRequestObject) throws BizException, Exception {
		OtpRequestObject otpRequest = otpRequestObject.getPayload();
		otpHelper.validateOtpRequest(otpRequest);

		OtpDetails existsOtpDetails = otpHelper.getOtpDetailsByMobile(otpRequest.getCustomerMobile());
		if (existsOtpDetails == null) {

			OtpDetails otpDetails = otpHelper.getOtpDetailsByReqObj(otpRequest);
			otpDetails = otpHelper.saveOtpDetails(otpDetails);

			otpRequest.setOtp(null);
			otpRequest.setRespCode(Constant.SUCCESS_CODE);
			otpRequest.setRespMesg("Otp Send Successfully");
			return otpRequest;
		} else {
			existsOtpDetails = otpHelper.getUpdatedOtpDetailsByReqObj(otpRequest, existsOtpDetails);
			existsOtpDetails = otpHelper.updateOtpDetails(existsOtpDetails);

			otpRequest.setOtp(null);
			otpRequest.setRespCode(Constant.SUCCESS_CODE);
			otpRequest.setRespMesg("Otp Send Successfully");
			return otpRequest;
		}
	}

//	public LeadRequestObject verifyOtp(Request<LeadRequestObject> leadRequestObject)
//	        throws BizException {
//
//	    LeadRequestObject leadRequest = leadRequestObject.getPayload();
//	    otpHelper.validateLeadRequest(leadRequest);
//
//	    OtpDetails otpDetails =
//	            otpHelper.getOtpDetailsByMobile(leadRequest.getCustomerMobile());
//
//	    if (otpDetails == null) {
//	        leadRequest.setRespCode(Constant.BAD_REQUEST_CODE);
//	        leadRequest.setRespMesg("OTP not found");
//	        return leadRequest;
//	    }
//
//	    // ✅ OTP status must be INIT
//	    if (!"INIT".equalsIgnoreCase(otpDetails.getStatus())) {
//	        leadRequest.setRespCode(Constant.BAD_REQUEST_CODE);
//	        leadRequest.setRespMesg("Wrong OTP");
//	        return leadRequest;
//	    }
//
//	    // ✅ Same date validation
//	    LocalDate dbDate = otpDetails.getUpdatedAt()
//	            .toInstant()
//	            .atZone(ZoneId.systemDefault())
//	            .toLocalDate();
//
//	    LocalDate currentDate = LocalDate.now();
//
//	    if (!dbDate.equals(currentDate)) {
//	        leadRequest.setRespCode(Constant.BAD_REQUEST_CODE);
//	        leadRequest.setRespMesg("OTP expired");
//	        return leadRequest;
//	    }
//
//	    // ✅ 10 minutes validity check
//	    long diffMillis = System.currentTimeMillis()
//	            - otpDetails.getUpdatedAt().getTime();
//
//	    long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis);
//
//	    if (diffMinutes > 10) {
//	        leadRequest.setRespCode(Constant.BAD_REQUEST_CODE);
//	        leadRequest.setRespMesg("OTP expired");
//	        return leadRequest;
//	    }
//
//	    // ✅ OTP match check
//	    if (!otpDetails.getOtp().equals(leadRequest.getOtp())) {
//	        leadRequest.setRespCode(Constant.BAD_REQUEST_CODE);
//	        leadRequest.setRespMesg("Wrong OTP");
//	        return leadRequest;
//	    }
//
//	    // ✅ SUCCESS
//	    leadRequest.setRespCode(Constant.SUCCESS_CODE);
//	    leadRequest.setRespMesg("OTP verified successfully");
//
//	    // Optional: update OTP status
//	    otpDetails.setStatus("VERIFIED");
//	    otpHelper.updateOtpDetails(otpDetails);
//
//	    return leadRequest;
//	}
	
	
	public OtpRequestObject verifyOtp(Request<OtpRequestObject> request)
	        throws BizException {
		OtpRequestObject otpRequest = request.getPayload();
	    otpHelper.validateOtpRequest(otpRequest);

	    OtpDetails otpDetails = otpHelper.getOtpDetailsByMobile(otpRequest.getCustomerMobile());

	    if (otpDetails == null) {
	        otpRequest.setRespCode(Constant.BAD_REQUEST_CODE);
			otpRequest.setRespMesg("OTP not found");
	        return otpRequest;
	    }

	    // 1️⃣ Status must be INIT
	    if (!"INIT".equalsIgnoreCase(otpDetails.getStatus())) {
	    	 otpRequest.setRespCode(Constant.BAD_REQUEST_CODE);
				otpRequest.setRespMesg("Wrong OTP");
		        return otpRequest;
	    }

	    Date now = new Date();

	    // 2️⃣ Same date validation
	    LocalDate dbDate = otpDetails.getUpdatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	    LocalDate today = now.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

	    if (!dbDate.equals(today)) {
	        expireOtp(otpDetails);
	        otpRequest.setRespCode(Constant.BAD_REQUEST_CODE);
			otpRequest.setRespMesg("OTP expired");
	        return otpRequest;
	    }

	    // 3️⃣ 10-minute expiry check
	    long diffMillis = now.getTime() - otpDetails.getUpdatedAt().getTime();
	    long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis);

	    System.out.println("diffMinutes : "+diffMinutes);
	    if (diffMinutes > 5) {
	        expireOtp(otpDetails);
	        
	        otpRequest.setRespCode(Constant.BAD_REQUEST_CODE);
			otpRequest.setRespMesg("OTP expired");
	        return otpRequest;
	    }

	    // 4️⃣ OTP match check
	    if (!otpDetails.getOtp().equals(otpRequest.getOtp())) {
	        otpDetails.setOtpCount(otpDetails.getOtpCount() + 1);
	        otpDetails.setUpdatedAt(now);
	        otpHelper.updateOtpDetails(otpDetails);

	        otpRequest.setRespCode(Constant.BAD_REQUEST_CODE);
			otpRequest.setRespMesg("Wrong OTP");
	        return otpRequest;
	    }

	    // 5️⃣ SUCCESS
	    otpDetails.setStatus("VERIFIED");
	    otpDetails.setUpdatedAt(now);
	    otpHelper.updateOtpDetails(otpDetails);

	    otpRequest.setRespCode(Constant.SUCCESS_CODE);
	    otpRequest.setRespMesg("OTP verified successfully");
	    return otpRequest;
	}

	
//	private LeadRequestObject failure(OtpRequestObject otpRequest, String msg) {
//		otpRequest.setRespCode(Constant.BAD_REQUEST_CODE);
//		otpRequest.setRespMesg(msg);
//	    return otpRequest;
//	}

	private void expireOtp(OtpDetails otpDetails) {
	    otpDetails.setStatus("EXPIRED");
//	    otpDetails.setUpdatedAt(new Date());
	    otpHelper.updateOtpDetails(otpDetails);
	}



}
