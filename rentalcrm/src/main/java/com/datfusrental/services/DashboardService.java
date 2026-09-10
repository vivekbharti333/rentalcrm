package com.datfusrental.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.DashboardHelper;
import com.datfusrental.helper.UserHelper;
import com.datfusrental.object.request.DashboardRequestObject;
import com.datfusrental.object.request.Request;

@Service
public class DashboardService {

	@Autowired
	private DashboardHelper dashboardHelper;

	@Autowired
	private UserHelper userHelper;

	public DashboardRequestObject dashCount(Request<DashboardRequestObject> dashboardRequestObject)
			throws BizException, Exception {
		DashboardRequestObject dashboardRequest = dashboardRequestObject == null ? null : dashboardRequestObject.getPayload();
		dashboardHelper.validateDashboardRequest(dashboardRequest);

		if (dashboardRequest.getRoleType() != null) {
			dashboardRequest.setTotalNoOfUser(userHelper.getActiveAndInactiveUserCount(dashboardRequest));
		}
		dashboardHelper.populateTodayCounts(dashboardRequest);

		return dashboardRequest;
	}
}
