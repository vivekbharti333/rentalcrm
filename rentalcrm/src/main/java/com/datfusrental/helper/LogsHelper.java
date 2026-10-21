package com.datfusrental.helper;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.datfusrental.dao.LoginLogsDao;
import com.datfusrental.entities.LoginLogs;
import com.datfusrental.object.request.LoginRequestObject;

@Component
public class LogsHelper {

	@Autowired
	private LoginLogsDao loginLogsDao;


	public LoginLogs getLoginLogsDetailsByReqObj(LoginRequestObject loginRequest) {

		LoginLogs loginLogs = new LoginLogs();
		
		loginLogs.setLoginId(loginRequest.getLoginId());
		loginLogs.setStatus(loginRequest.getStatus());
		loginLogs.setIpAddress(loginRequest.getIpAddress());
		loginLogs.setIsRegisteredIp(loginRequest.getIsRegisteredIp());
		loginLogs.setReason(loginRequest.getRespMesg());
		loginLogs.setCreatedAt(new Date());

		return loginLogs;
	}

	@Transactional
	public LoginLogs saveLoginLogsDetails(LoginLogs loginLogs) {
		loginLogsDao.persist(loginLogs);
		return loginLogs;
	}

}
