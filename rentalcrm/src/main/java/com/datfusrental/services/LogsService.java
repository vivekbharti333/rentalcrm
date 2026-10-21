package com.datfusrental.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datfusrental.constant.Constant;
import com.datfusrental.entities.LoginLogs;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.LogsHelper;
import com.datfusrental.object.request.LoginRequestObject;
import com.datfusrental.object.request.Request;

@Service
public class LogsService {

	@Autowired
	private LogsHelper logsHelper;

	public LoginRequestObject addLoginLogsDetails(Request<LoginRequestObject> loginRequestObject)
			throws BizException, Exception {
		LoginRequestObject loginRequest = loginRequestObject.getPayload();

		LoginLogs loginLogs = logsHelper.getLoginLogsDetailsByReqObj(loginRequest);
		logsHelper.saveLoginLogsDetails(loginLogs);

		loginRequest.setRespCode(Constant.SUCCESS_CODE);
		loginRequest.setRespMesg("Successfully Submitted");
		return loginRequest;

	}

}
