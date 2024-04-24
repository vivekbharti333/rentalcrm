package com.datfusrental.services;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datfusrental.constant.Constant;
import com.datfusrental.entities.UserRole;
import com.datfusrental.entities.UserRoleMaster;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.UserRoleHelper;
import com.datfusrental.jwt.JwtTokenUtil;
import com.datfusrental.object.request.Request;
import com.datfusrental.object.request.UserRequestObject;

@Service
public class UserRoleService {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private UserRoleHelper userRoleHelper;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Transactional
	public UserRequestObject addUserRoleMaster(Request<UserRequestObject> userRequestObject)
			throws BizException, Exception {
		UserRequestObject userRequest = userRequestObject.getPayload();
		userRoleHelper.validateUserRequest(userRequest);

		Boolean isValid = jwtTokenUtil.validateJwtToken(userRequest.getCreatedBy(), userRequest.getToken());
		if (isValid) {

			UserRoleMaster existsUserDetails = userRoleHelper.getUserRoleMasterByRoleType(userRequest.getRoleType());
			if (existsUserDetails == null) {

				UserRoleMaster userRoleMaster = userRoleHelper.getUserRoleMasterByReqObj(userRequest);
				userRoleMaster = userRoleHelper.saveUserRoleMaster(userRoleMaster);

				userRequest.setRespCode(Constant.SUCCESS_CODE);
				userRequest.setRespMesg(Constant.REGISTERED_SUCCESS);
				return userRequest;
			} else {
				userRequest.setRespCode(Constant.BAD_REQUEST_CODE);
				userRequest.setRespMesg(Constant.USER_EXIST);
				return userRequest;
			}
		} else {
			userRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
			userRequest.setRespMesg(Constant.INVALID_TOKEN);
			return userRequest;
		}
	}

	@Transactional
	public UserRequestObject updateUserRoleMaster(Request<UserRequestObject> userRequestObject)
			throws BizException, Exception {
		UserRequestObject userRequest = userRequestObject.getPayload();
		userRoleHelper.validateUserRequest(userRequest);

		Boolean isValid = jwtTokenUtil.validateJwtToken(userRequest.getCreatedBy(), userRequest.getToken());
		if (isValid) {

			UserRoleMaster userRoleMaster = userRoleHelper.getUserRoleMasterByRoleType(userRequest.getLoginId());
			if (userRoleMaster != null) {

				userRoleMaster = userRoleHelper.getUpdatedUserRoleMasterByReqObj(userRoleMaster, userRequest);
				userRoleMaster = userRoleHelper.updateUserRoleMaster(userRoleMaster);

				userRequest.setRespCode(Constant.SUCCESS_CODE);
				userRequest.setRespMesg(Constant.UPDATED_SUCCESS);
				return userRequest;
			} else {
				userRequest.setRespCode(Constant.BAD_REQUEST_CODE);
				userRequest.setRespMesg(Constant.NOT_EXIST);
				return userRequest;
			}
		} else {
			userRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
			userRequest.setRespMesg(Constant.INVALID_TOKEN);
			return userRequest;
		}
	}

	public List<UserRoleMaster> getUserRoleMaster(Request<UserRequestObject> userRequestObject) {
		UserRequestObject userRequest = userRequestObject.getPayload();
		List<UserRoleMaster> roleMasterList = userRoleHelper.getUserRoleMaster(userRequest);
		return roleMasterList;
	}

	public UserRequestObject addUserRole(Request<UserRequestObject> userRequestObject) throws BizException, Exception {
		UserRequestObject userRequest = userRequestObject.getPayload();
		userRoleHelper.validateUserRequest(userRequest);

		Boolean isValid = jwtTokenUtil.validateJwtToken(userRequest.getCreatedBy(), userRequest.getToken());
		if (isValid) {

			UserRole existsUserRole = userRoleHelper.getUserRoleBySuperadminId(userRequest.getSuperadminId());
			if (existsUserRole == null) {

				UserRole userRole = userRoleHelper.getUserRoleByReqObj(userRequest);
				userRole = userRoleHelper.saveUserRole(userRole);

				userRequest.setRespCode(Constant.SUCCESS_CODE);
				userRequest.setRespMesg(Constant.REGISTERED_SUCCESS);
				return userRequest;
			} else {
				existsUserRole.setRoleTypeIds(userRequest.getRoleTypeIds());
				userRoleHelper.updateUserRole(existsUserRole);

				userRequest.setRespCode(Constant.SUCCESS_CODE);
				userRequest.setRespMesg(Constant.UPDATED_SUCCESS);
				return userRequest;
			}
		} else {
			userRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
			userRequest.setRespMesg(Constant.INVALID_TOKEN);
			return userRequest;
		}
	}

//	public List<UserRole> getUserRole(Request<UserRequestObject> userRequestObject) {
//		UserRequestObject userRequest = userRequestObject.getPayload();
//		List<UserRole> roleList = userRoleHelper.getUserRole(userRequest);
//		return roleList;
//	}

}
