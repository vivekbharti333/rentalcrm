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
import com.datfusrental.entities.AddressDetails;
import com.datfusrental.entities.UserDetails;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.LoginRequestObject;
import com.datfusrental.object.request.Request;
import com.datfusrental.object.request.UserRequestObject;
import com.datfusrental.object.response.GenricResponse;
import com.datfusrental.object.response.Response;
import com.datfusrental.services.UserService;

@CrossOrigin(origins = "*")
@RestController
public class UserController {
	
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	UserService userService;
	

//	@RequestMapping(path = "doLogin", method = RequestMethod.POST)
//	public Response<UserRequestObject> doLogin(@RequestBody Request<UserRequestObject> userRequestObject,
//			HttpServletRequest request) {
//		GenricResponse<UserRequestObject> responseObj = new GenricResponse<UserRequestObject>();
//		try {
//			UserRequestObject response = userService.doLogin(userRequestObject);
//			return responseObj.createSuccessResponse(response, Constant.SUCCESS_CODE);
//		} catch (BizException e) {
//			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
//		} catch (Exception e) {
//			e.printStackTrace();
//			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
//		}
//	}
	
	@RequestMapping(path = "doLogin", method = RequestMethod.POST)
	public Response<LoginRequestObject> doLogin(@RequestBody Request<LoginRequestObject> loginRequestObject,
			HttpServletRequest request) {
		GenricResponse<LoginRequestObject> responseObj = new GenricResponse<LoginRequestObject>();
		try {
			LoginRequestObject response = userService.doLogin(loginRequestObject);
			return responseObj.createSuccessResponse(response, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}
	
	@RequestMapping(path = "getUserDetailsByLoginId", method = RequestMethod.POST)
	public Response<UserRequestObject> getUserDetailsByLoginId(@RequestBody Request<UserRequestObject> userRequestObject, HttpServletRequest request) {
		GenricResponse<UserRequestObject> responseObj = new GenricResponse<UserRequestObject>();
		try {
			
			UserRequestObject response = userService.getUserDetailsByLoginId(userRequestObject);
			return responseObj.createSuccessResponse(response, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}

	@RequestMapping(path = "userRegistration", method = RequestMethod.POST)
	public Response<UserRequestObject> userRegistration(@RequestBody Request<UserRequestObject> userRequestObject,
			HttpServletRequest request) {
		GenricResponse<UserRequestObject> responseObj = new GenricResponse<UserRequestObject>();
		try {
			UserRequestObject responce = userService.userRegistration(userRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}

	@RequestMapping(path = "updateUserDetails", method = RequestMethod.POST)
	public Response<UserRequestObject> updateUserDetails(@RequestBody Request<UserRequestObject> userRequestObject,
			HttpServletRequest request) {
		GenricResponse<UserRequestObject> responseObj = new GenricResponse<UserRequestObject>();
		try {
			
			UserRequestObject responce = userService.updateUserDetails(userRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}
	
	@RequestMapping(path = "changeUserPassword", method = RequestMethod.POST)
	public Response<UserRequestObject> changeUserPassword(@RequestBody Request<UserRequestObject> userRequestObject,
			HttpServletRequest request) {
		GenricResponse<UserRequestObject> responseObj = new GenricResponse<UserRequestObject>();
		try {
			UserRequestObject responce = userService.changeUserPassword(userRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}

	@RequestMapping(path = "changeUserStatus", method = RequestMethod.POST)
	public Response<UserRequestObject> changeUserStatus(@RequestBody Request<UserRequestObject> userRequestObject,
			HttpServletRequest request) {
		GenricResponse<UserRequestObject> responseObj = new GenricResponse<UserRequestObject>();
		try {
			UserRequestObject responce = userService.changeUserStatus(userRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}
	
	@RequestMapping(path = "removeUserParmanent", method = RequestMethod.POST)
	public Response<UserRequestObject> removeUserParmanent(@RequestBody Request<UserRequestObject> userRequestObject,
			HttpServletRequest request) {
		GenricResponse<UserRequestObject> responseObj = new GenricResponse<UserRequestObject>();
		try {
			UserRequestObject responce = userService.removeUserParmanent(userRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}
	
	@RequestMapping(path = "changeTeamLeader", method = RequestMethod.POST)
	public Response<UserRequestObject> changeTeamLeader(@RequestBody Request<UserRequestObject> userRequestObject,
			HttpServletRequest request) {
		GenricResponse<UserRequestObject> responseObj = new GenricResponse<UserRequestObject>();
		try {
			UserRequestObject responce = userService.changeTeamLeader(userRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}
	
	@RequestMapping(path = "changeUserRole", method = RequestMethod.POST)
	public Response<UserRequestObject> changeUserRole(@RequestBody Request<UserRequestObject> userRequestObject,
			HttpServletRequest request) {
		GenricResponse<UserRequestObject> responseObj = new GenricResponse<UserRequestObject>();
		try {
			UserRequestObject responce = userService.changeUserRole(userRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}
	

	@RequestMapping(path = "getUserDetails", method = RequestMethod.POST)
	public Response<UserDetails> getUserDetails(@RequestBody Request<UserRequestObject> userRequestObject) {
		GenricResponse<UserDetails> response = new GenricResponse<UserDetails>();
		try {
			List<UserDetails> userList = userService.getUserDetails(userRequestObject);
			int totalNum = userList.size();
			System.out.println(totalNum+": tyt");
			return response.createListResponse(userList, 200, String.valueOf(userList.size()));
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}
	
	
	@RequestMapping(path = "getUserDetailsByUserRole", method = RequestMethod.POST)
	public Response<UserDetails> getUserDetailsByUserRole(@RequestBody Request<UserRequestObject> userRequestObject) {
		GenricResponse<UserDetails> response = new GenricResponse<UserDetails>();
		try {
			List<UserDetails> userList = userService.getUserDetailsByUserRole(userRequestObject);
			return response.createListResponse(userList, 200);
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}
	
	
	@RequestMapping(path = "getUserListForDropDown", method = RequestMethod.POST)
	public Response<UserDetails> getUserListForDropDown(@RequestBody Request<UserRequestObject> userRequestObject) {
		GenricResponse<UserDetails> response = new GenricResponse<UserDetails>();
		try {
			List<UserDetails> userList = userService.getUserListForDropDown(userRequestObject);
			return response.createListResponse(userList, 200);
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}
	
	@RequestMapping(path = "getAddressDetails", method = RequestMethod.POST)
	public Response<AddressDetails> getAddressDetails(@RequestBody Request<UserRequestObject> userRequestObject) {
		GenricResponse<AddressDetails> response = new GenricResponse<AddressDetails>();
		try {
			List<AddressDetails> addressList = userService.getAddressDetails(userRequestObject);
			return response.createListResponse(addressList, 200);
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}
}
