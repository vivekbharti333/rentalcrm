package com.datfusrental.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.datfusrental.constant.Constant;
import com.datfusrental.entities.KnowlarityCallLog;
import com.datfusrental.object.request.KnowlarityWebhookRequest;
import com.datfusrental.object.response.GenricResponse;
import com.datfusrental.object.response.Response;
import com.datfusrental.services.KnowlarityService;

@CrossOrigin(origins = "*")
@RestController
public class knowlarityController {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	KnowlarityService knowlarityService;

	/**
	 * Public webhook called by Knowlarity Notifications (Streaming API).
	 * Receives every call event (ORIGINATE, CUSTOMER_CALL, BRIDGE, HANGUP, CDR...)
	 * and captures the caller's mobile number into the CRM.
	 */
	@RequestMapping(path = "knowlarity/webhook", method = RequestMethod.POST)
	public Response<KnowlarityWebhookRequest> knowlarityWebhook(@RequestBody String rawBody) {
		GenricResponse<KnowlarityWebhookRequest> responseObj = new GenricResponse<KnowlarityWebhookRequest>();
		try {
			KnowlarityCallLog callLog = knowlarityService.processWebhook(rawBody);

			KnowlarityWebhookRequest responce = new KnowlarityWebhookRequest();
			if (callLog == null) {
				responce.setRespCode(Constant.SUCCESS_CODE);
				responce.setRespMesg("Duplicate notification ignored");
			} else {
				responce.setRespCode(Constant.SUCCESS_CODE);
				responce.setRespMesg("Webhook received. caller=" + callLog.getCustomerNumber()
						+ ", event=" + callLog.getEvent() + ", leadCreated=" + callLog.getLeadCreated());
			}
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}

	/**
	 * Returns today's Knowlarity call notifications (for CRM verification).
	 */
	@RequestMapping(path = "knowlarity/getTodayCallLogs", method = RequestMethod.GET)
	public Response<KnowlarityCallLog> getTodayCallLogs() {
		GenricResponse<KnowlarityCallLog> response = new GenricResponse<KnowlarityCallLog>();
		try {
			List<KnowlarityCallLog> callLogs = knowlarityService.getTodayCallLogs();
			return response.createListResponse(callLogs, Constant.SUCCESS_CODE, String.valueOf(callLogs.size()));
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}
}
