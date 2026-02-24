package com.datfusrental.services;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datfusrental.constant.Constant;
import com.datfusrental.entities.PaymentGatewayDetails;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.PaymentGatewaysHelper;
import com.datfusrental.helper.UserHelper;
import com.datfusrental.jwt.JwtTokenUtil;
import com.datfusrental.object.request.PaymentGatewaysRequestObject;
import com.datfusrental.object.request.Request;

@Service
public class PaymentGatewaysService {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private PaymentGatewaysHelper paymentGatewaysHelper;


	public PaymentGatewaysRequestObject addPaymentGatewaysDetails(Request<PaymentGatewaysRequestObject> paymentGatewaysRequestObject) throws BizException {
		PaymentGatewaysRequestObject paymentGatewaysRequest = paymentGatewaysRequestObject.getPayload();
		paymentGatewaysHelper.validatePaymentGatewaysRequest(paymentGatewaysRequest);
		
		PaymentGatewayDetails existsPaymentGatewayDetails =  paymentGatewaysHelper.getPaymentGatewayDetailsByName(paymentGatewaysRequest.getPaymentGatewaysName(), paymentGatewaysRequest.getCompanyName());
		if(existsPaymentGatewayDetails == null) {
			PaymentGatewayDetails paymentGatewayDetails = paymentGatewaysHelper.getPaymentGatewayDetailsReqObj(paymentGatewaysRequest);
			paymentGatewayDetails = paymentGatewaysHelper.savePaymentGateways(paymentGatewayDetails);
			
			paymentGatewaysRequest.setRespCode(Constant.SUCCESS_CODE);
			paymentGatewaysRequest.setRespMesg(Constant.REGISTERED_SUCCESS);
			return paymentGatewaysRequest;
		}else {
			paymentGatewaysRequest.setRespCode(Constant.ALREADY_EXISTS);
			paymentGatewaysRequest.setRespMesg(Constant.ALREADY_EXISTS_MSG);
			return paymentGatewaysRequest;
		}

	}

}