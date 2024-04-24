package com.datfusrental.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;   
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.datfusrental.constant.Constant;
import com.datfusrental.entities.UserRoleMaster;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.Request;
import com.datfusrental.object.request.UserRequestObject;
import com.datfusrental.object.response.GenricResponse;
import com.datfusrental.object.response.Response;
import com.datfusrental.services.UserRoleService;

@CrossOrigin(origins = "*")
@RestController
public class UserRoleController {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	UserRoleService userRoleService;

	@RequestMapping(path = "addUserRoleMaster", method = RequestMethod.POST)
	public Response<UserRequestObject> addUserRoleMaster(@RequestBody Request<UserRequestObject> userRequestObject,
			HttpServletRequest request) {
		GenricResponse<UserRequestObject> responseObj = new GenricResponse<UserRequestObject>();
		try {
			UserRequestObject responce = userRoleService.addUserRoleMaster(userRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}

	@RequestMapping(path = "updateUserRoleMaster", method = RequestMethod.POST)
	public Response<UserRequestObject> updateUserRoleMaster(@RequestBody Request<UserRequestObject> userRequestObject,
			HttpServletRequest request) {
		GenricResponse<UserRequestObject> responseObj = new GenricResponse<UserRequestObject>();
		try {

			UserRequestObject responce = userRoleService.updateUserRoleMaster(userRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}

	@RequestMapping(path = "getUserRoleMaster", method = RequestMethod.POST)
	public Response<UserRoleMaster> getUserRoleMaster(@RequestBody Request<UserRequestObject> userRequestObject) {
		GenricResponse<UserRoleMaster> response = new GenricResponse<UserRoleMaster>();
		try {
			List<UserRoleMaster> roleMasterList = userRoleService.getUserRoleMaster(userRequestObject);
			return response.createListResponse(roleMasterList, 200);
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}
	
	@RequestMapping(path = "addUserRole", method = RequestMethod.POST)
	public Response<UserRequestObject> addUserRole(@RequestBody Request<UserRequestObject> userRequestObject,
			HttpServletRequest request) {
		GenricResponse<UserRequestObject> responseObj = new GenricResponse<UserRequestObject>();
		try {
			UserRequestObject responce = userRoleService.addUserRole(userRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}

//	@RequestMapping(path = "getUserRole", method = RequestMethod.POST)
//	public Response<UserRole> getUserRole(@RequestBody Request<UserRequestObject> userRequestObject) {
//		GenricResponse<UserRole> response = new GenricResponse<UserRole>();
//		try {
//			List<UserRole> roleList = userRoleService.getUserRole(userRequestObject);
//			return response.createListResponse(roleList, 200);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
//		}
//	}
	
}
