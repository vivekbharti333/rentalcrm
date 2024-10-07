package com.datfusrental.helper;

import org.springframework.stereotype.Component;

import com.datfusrental.constant.Constant;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.DashboardRequestObject;

@Component
public class DashboardHelper {



	public void validateDashboardRequest(DashboardRequestObject dashboardRequestObject) throws BizException {
		if (dashboardRequestObject == null) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Bad Request Object Null");
		}
	}

	

	

}