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
import com.datfusrental.object.request.DashboardRequestObject;
import com.datfusrental.object.request.Request;
import com.datfusrental.object.response.GenricResponse;
import com.datfusrental.object.response.Response;
import com.datfusrental.services.DashboardService;

@CrossOrigin(origins = "*")
@RestController
public class DashboardController {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private DashboardService locationService;


	@RequestMapping(path = "dashCount", method = RequestMethod.POST)
	public Response<DashboardRequestObject> dashCount(@RequestBody Request<DashboardRequestObject> dashboardRequestObject,
			HttpServletRequest request) {
		GenricResponse<DashboardRequestObject> responseObj = new GenricResponse<DashboardRequestObject>();
		try {
			DashboardRequestObject responce = locationService.dashCount(dashboardRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}

}
