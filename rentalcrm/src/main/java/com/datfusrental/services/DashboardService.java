package com.datfusrental.services;

import java.util.List;

import javax.transaction.Transactional;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datfusrental.constant.Constant;
import com.datfusrental.entities.LocationDetails;
import com.datfusrental.enums.Status;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.DashboardHelper;
import com.datfusrental.helper.LocationHelper;
import com.datfusrental.helper.UserHelper;
import com.datfusrental.jwt.JwtTokenUtil;
import com.datfusrental.object.request.DashboardRequestObject;
import com.datfusrental.object.request.LocationRequestObject;
import com.datfusrental.object.request.Request;

@Service
public class DashboardService {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private DashboardHelper dashboardHelper;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private UserHelper userHelper;

	public DashboardRequestObject dashCount(Request<DashboardRequestObject> dashboardRequestObject)
			throws BizException, Exception {
		DashboardRequestObject dashboardRequest = dashboardRequestObject.getPayload();
		dashboardHelper.validateDashboardRequest(dashboardRequest);

		Boolean isValid = jwtTokenUtil.validateJwtToken(dashboardRequest.getLoginId(), dashboardRequest.getToken());
		if (isValid) {
			Long totalNoOfUser = userHelper.getActiveAndInactiveUserCount(dashboardRequest);

			dashboardRequest.setTotalNoOfUser(totalNoOfUser);
			
			
		}
		return null;
	}

}