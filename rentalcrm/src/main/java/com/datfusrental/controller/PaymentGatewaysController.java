package com.datfusrental.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.datfusrental.constant.Constant;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.PaymentGatewaysRequestObject;
import com.datfusrental.object.request.Request;
import com.datfusrental.object.response.GenricResponse;
import com.datfusrental.object.response.Response;
import com.datfusrental.services.PaymentGatewaysService;

@CrossOrigin(origins = "*")
@RestController
public class PaymentGatewaysController {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private PaymentGatewaysService paymentGatewaysService;


	@RequestMapping(path = "addPaymentGatewaysDetails", method = RequestMethod.POST)
	public Response<PaymentGatewaysRequestObject> addPaymentGatewaysDetails(@RequestBody Request<PaymentGatewaysRequestObject> paymentGatewaysRequestObject,
			HttpServletRequest request) {
		GenricResponse<PaymentGatewaysRequestObject> responseObj = new GenricResponse<PaymentGatewaysRequestObject>();
		try {
			PaymentGatewaysRequestObject responce = paymentGatewaysService.addPaymentGatewaysDetails(paymentGatewaysRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}

}
