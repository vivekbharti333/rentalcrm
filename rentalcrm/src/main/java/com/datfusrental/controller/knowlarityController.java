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
import com.datfusrental.object.request.KnowlarityCallLogRequest;
import com.datfusrental.object.request.KnowlarityWebhookRequest;
import com.datfusrental.object.request.Request;
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
	 * Public webhook called by Knowlarity with call details.
	 * Captures inbound caller numbers into the CRM.
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
				responce.setRespMesg("Webhook received. caller=" + callLog.getCallerNumber()
						+ ", status=" + callLog.getCallStatus() + ", leadCreated=" + callLog.getLeadCreated());
			}
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}

	/**
	 * Returns Knowlarity call notifications filtered by date range.
	 * requestedFor: TODAY (default), YESTERDAY, LAST_WEEK or CUSTOM.
	 * For CUSTOM, firstDate and lastDate (yyyy-MM-dd, both inclusive) are required.
	 */
	@RequestMapping(path = "knowlarity/getCallLogs", method = RequestMethod.POST)
	public Response<KnowlarityCallLog> getCallLogs(@RequestBody Request<KnowlarityCallLogRequest> requestObject) {
		GenricResponse<KnowlarityCallLog> response = new GenricResponse<KnowlarityCallLog>();
		try {
			List<KnowlarityCallLog> callLogs = knowlarityService.getCallLogs(requestObject);
			return response.createListResponse(callLogs, Constant.SUCCESS_CODE, String.valueOf(callLogs.size()));
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}
}
