package com.datfusrental.services;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datfusrental.constant.Constant;
import com.datfusrental.entities.OtpDetails;
import com.datfusrental.entities.User;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.OtpHelper;
import com.datfusrental.helper.SendWhatsAppTextMessageHelper;
import com.datfusrental.helper.UserHelper;
import com.datfusrental.object.request.LeadRequestObject;
import com.datfusrental.object.request.OtpRequestObject;
import com.datfusrental.object.request.Request;
import com.datfusrental.object.response.WhatsAppMessageResponse;
import com.datfusrental.whatsapp.request.TemplateBodyVariableRequest;
import com.datfusrental.whatsapp.request.TemplateButtonVariableRequest;
import com.datfusrental.whatsapp.request.WhatsAppMessageRequestObject;

@Service
public class OtpService {

	private static final int MAX_WRONG_ATTEMPTS = 5;
	private static final long OTP_VALIDITY_MILLIS = TimeUnit.MINUTES.toMillis(10);

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private UserHelper userHelper;
	
	@Autowired
	private OtpHelper otpHelper;

	@Autowired
	private SendWhatsAppTextMessageHelper sendWhatsAppTextMessageHelper;

	/** Sends rywcrm_pass with the same OTP in the body and URL button. */
	public WhatsAppMessageResponse sendWhatsAppPassMessage(String mobileNumber, String otp) throws Exception {
		if (mobileNumber == null || mobileNumber.isBlank()) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Mobile number is required");
		}
		
		TemplateBodyVariableRequest otpVariable = new TemplateBodyVariableRequest();
		otpVariable.setVariableType("text");
		otpVariable.setBodyVariable(otp);

		TemplateButtonVariableRequest otpButton = new TemplateButtonVariableRequest();
		otpButton.setType("url");
		otpButton.setValue(otp);

		WhatsAppMessageRequestObject messageRequest = new WhatsAppMessageRequestObject();
		messageRequest.setMessageTo(mobileNumber);
		messageRequest.setTemplateName("rywcrm_pass");
		messageRequest.setLanguage("en");
		messageRequest.setMsgBodyVariable(List.of(otpVariable));
		messageRequest.setButtonVariable(List.of(otpButton));

		String payload = sendWhatsAppTextMessageHelper.getTextTemplateParameter(messageRequest);
		return sendWhatsAppTextMessageHelper.callSendTemplateTextMessage(payload);
	}

	public OtpRequestObject generateOtp(Request<OtpRequestObject> otpRequestObject) throws BizException, Exception {
		OtpRequestObject otpRequest = otpRequestObject.getPayload();
		otpHelper.validateOtpRequest(otpRequest);
		if (otpRequest.getCustomerMobile() == null || otpRequest.getCustomerMobile().isBlank()) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Mobile number is required");
		}
		
		User user = userHelper.getUserDetailsByLoginId(otpRequest.getCustomerMobile());
		if(user == null) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "User not found for this mobile number");
		}

		OtpDetails existsOtpDetails = otpHelper.getOtpDetailsByMobile(otpRequest.getCustomerMobile());
		if (existsOtpDetails == null) {

			OtpDetails otpDetails = otpHelper.getOtpDetailsByReqObj(otpRequest);
			otpDetails = otpHelper.saveOtpDetails(otpDetails);
			
			this.sendWhatsAppPassMessage("+91"+user.getAlternateMobile(), otpDetails.getOtp());

			otpRequest.setLoginId(user.getLoginId());
			otpRequest.setOtp(null);
			otpRequest.setRespCode(Constant.SUCCESS_CODE);
			otpRequest.setRespMesg("Otp Send Successfully");
			return otpRequest;
		} else {
			existsOtpDetails = otpHelper.getUpdatedOtpDetailsByReqObj(otpRequest, existsOtpDetails);
			existsOtpDetails = otpHelper.updateOtpDetails(existsOtpDetails);
			
			this.sendWhatsAppPassMessage("+91"+user.getAlternateMobile(), existsOtpDetails.getOtp());

			otpRequest.setLoginId(user.getLoginId());
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
	    
	    if (otpRequest.getCustomerMobile() == null || otpRequest.getCustomerMobile().isBlank()) {
	        throw new BizException(Constant.BAD_REQUEST_CODE, "Mobile number is required");
	    }
	    User user = userHelper.getUserDetailsByLoginId(otpRequest.getCustomerMobile());
	    if (user == null) {
	        throw new BizException(Constant.BAD_REQUEST_CODE, "User not found for this mobile number");
	    }

	    // Use the same lookup key as generateOtp; the alternate mobile is only the delivery address.
	    OtpDetails otpDetails = otpHelper.getOtpDetailsByMobile(otpRequest.getCustomerMobile());

	    if (otpDetails == null) {
	        otpRequest.setRespCode(Constant.BAD_REQUEST_CODE);
			otpRequest.setRespMesg("OTP not found");
	        return otpRequest;
	    }

	    // 1️⃣ Status must be INIT
	    if (!"INIT".equalsIgnoreCase(otpDetails.getStatus())) {
	    	 otpRequest.setRespCode(Constant.BAD_REQUEST_CODE);
				otpRequest.setRespMesg("EXPIRED".equalsIgnoreCase(otpDetails.getStatus()) ? "OTP expired" : "Wrong OTP");
		        return otpRequest;
	    }

	    Date now = new Date();

	    // Expire exactly 10 minutes after the latest OTP generation, including across midnight.
	    long diffMillis = now.getTime() - otpDetails.getUpdatedAt().getTime();

	    if (diffMillis >= OTP_VALIDITY_MILLIS) {
	        expireOtp(otpDetails);
	        
	        otpRequest.setRespCode(Constant.BAD_REQUEST_CODE);
			otpRequest.setRespMesg("OTP expired");
	        return otpRequest;
	    }

	    // 3️⃣ Too many wrong attempts → block and force a new OTP
	    if (otpDetails.getOtpCount() >= MAX_WRONG_ATTEMPTS) {
	        expireOtp(otpDetails);

	        otpRequest.setRespCode(Constant.BAD_REQUEST_CODE);
			otpRequest.setRespMesg("Too many wrong attempts. Please request a new OTP");
	        return otpRequest;
	    }

	    // 4️⃣ OTP match check
	    if (!otpDetails.getOtp().equals(otpRequest.getOtp())) {
	        otpDetails.setOtpCount(otpDetails.getOtpCount() + 1);
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
