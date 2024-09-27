package com.datfusrental.services;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.datfusrental.constant.Constant;
import com.datfusrental.entities.AddressDetails;
import com.datfusrental.entities.User;
import com.datfusrental.enums.Status;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.AddressHelper;
import com.datfusrental.helper.UserHelper;
import com.datfusrental.jwt.JwtTokenUtil;
import com.datfusrental.object.request.AddressRequestObject;
import com.datfusrental.object.request.LoginRequestObject;
import com.datfusrental.object.request.Request;
import com.datfusrental.object.request.UserRequestObject;


@Service
public class UserService {
	
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private UserHelper userHelper;

	@Autowired
	private AddressService addressService;
	
	@Autowired
	private AddressHelper addressHelper;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
//	@Autowired
//	private ModelMapper modelMapper;
	
//	public LoginRequestObject authenticUser(String loginId) {
//	    UserDetails userDetails = userHelper.getUserDetailsByLoginId(loginId);
//	    
//	    LoginRequestObject loginRequest = modelMapper.map(userDetails, LoginRequestObject.class);
//	    return loginRequest;
//	}

	public LoginRequestObject doLogin(Request<LoginRequestObject> loginRequestObject) throws BizException, Exception {
		LoginRequestObject loginRequest = loginRequestObject.getPayload();
		userHelper.validateLoginRequest(loginRequest);
		
		System.out.println("User id : "+loginRequest.getLoginId());
		User user = userHelper.getUserDetailsByLoginId(loginRequest.getLoginId());
		if (user != null) {
			if(user.getStatus().equalsIgnoreCase(Status.INACTIVE.name())) {
				
				loginRequest.setRespCode(Constant.BAD_REQUEST_CODE);
				loginRequest.setRespMesg(Constant.INACTIVE_USER);
				return loginRequest;
			}
			
			boolean isValid = userHelper.checkValidityOfUser(user.getValidityExpireOn());
			if (isValid) {
				if (BCrypt.checkpw(loginRequest.getPassword(), user.getSalt())) {
					logger.info("Login Successfully: " + loginRequest);

					// generate secret key.
					String secretKey = jwtTokenUtil.generateSecretKey();

					// update secret key in UserDetails.
					user.setSecretTokenKey(secretKey);
					userHelper.UpdateUserDetails(user);

					String token = jwtTokenUtil.generateJwtToken(user);

					loginRequest.setLoginId(user.getLoginId());
					loginRequest.setPassword(null);

					loginRequest.setUserPicture(user.getUserPicture());
					loginRequest.setFirstName(user.getFirstName());
					loginRequest.setLastName(user.getLastName());
					loginRequest.setService(user.getService());
					loginRequest.setPermissions(user.getPermissions());
					loginRequest.setRoleType(user.getRoleType());
					loginRequest.setSuperadminId(user.getSuperadminId());
					loginRequest.setAdminId(user.getAdminId());
					loginRequest.setTeamLeaderId(user.getTeamleaderId());
					loginRequest.setToken(token);

					loginRequest.setRespCode(Constant.SUCCESS_CODE);
					loginRequest.setRespMesg(Constant.LOGIN_SUCCESS);
					return loginRequest;
				} else {
					loginRequest.setRespCode(Constant.BAD_REQUEST_CODE);
					loginRequest.setRespMesg(Constant.INVALID_LOGIN);
					return loginRequest;
				}
			} else {
				loginRequest.setRespCode(Constant.BAD_REQUEST_CODE);
				loginRequest.setRespMesg(Constant.EXPIRED_LOGIN);
				return loginRequest;
			}
		} else {
			loginRequest.setRespCode(Constant.BAD_REQUEST_CODE);
			loginRequest.setRespMesg(Constant.INVALID_LOGIN);
			return loginRequest;
		}
	}
	
//	public UserRequestObject getUserDetailsByLoginId(Request<UserRequestObject> userRequestObject) throws BizException {
//		UserRequestObject userRequest = userRequestObject.getPayload();
//		userHelper.validateUserRequest(userRequest);
//
//		UserDetails userDetails = userHelper.getUserDetailsByLoginId(userRequest.getLoginId());
//		if (userDetails != null) {
//			
//			userRequest.setFirstName(userDetails.getFirstName());
//			userRequest.setLastName(userDetails.getLastName());
//			userRequest.setUserPicture(userDetails.getUserPicture());
//			userRequest.setRoleType(userDetails.getRoleType());
//			
//			userRequest.setRespCode(Constant.SUCCESS_CODE);
//			return userRequest;
//		}
//		return userRequest;
//	}
	

	@Transactional
	public UserRequestObject userRegistration(Request<UserRequestObject> userRequestObject)
			throws BizException, Exception {
		UserRequestObject userRequest = userRequestObject.getPayload();
		userHelper.validateUserRequest(userRequest);

//		String password = userHelper.generateRandomChars("ABCD145pqrs678abcdef90EF9GHxyzIJKL5MNOPQRghijS1234560TUVWXYlmnoZ1234567tuvw890", 10);
//		userRequest.setPassword("test@123");
		
		System.out.println(userRequest.getFirstName());

		Boolean isValid = jwtTokenUtil.validateJwtToken(userRequest.getCreatedBy(), userRequest.getToken());
//		if (isValid) {
		
		if(userRequest.getFirstName() == null || userRequest.getFirstName().equalsIgnoreCase("")) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Enter First Name");
		}
		if(userRequest.getLastName() == null || userRequest.getLastName().equalsIgnoreCase("")) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Enter Last Name");
		}
		if(userRequest.getMobileNo() == null || userRequest.getMobileNo().equalsIgnoreCase("")) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Enter Mobile No");
		}

			User existsUserDetails = userHelper.getUserDetailsByLoginIdAndSuperadminId(userRequest.getMobileNo(), userRequest.getSuperadminId());
			if (existsUserDetails == null) {
				userRequest.setPassword("12345");

				String salt = BCrypt.gensalt();
				String password = BCrypt.hashpw(userRequest.getPassword(), salt);
				userRequest.setPassword(password);
				userRequest.setSalt(password);
				
//				String userCode = userRequest.getFirstName().substring(0,1)+userRequest.getLastName().substring(0,1);

				User user = userHelper.getUserDetailsByReqObj(userRequest);
				user = userHelper.saveUserDetails(user);
				
				// Save Address

				for (AddressRequestObject addressRequest : userRequest.getAddressList()) {
					addressRequest.setUserType(userRequest.getRoleType());
					addressService.saveAddressDetailsByRequest(addressRequest, user.getId(), userRequest.getSuperadminId());
				}

				// send sms

				userRequest.setRespCode(Constant.SUCCESS_CODE);
				userRequest.setRespMesg(Constant.REGISTERED_SUCCESS);
				return userRequest;
			} else {
				userRequest.setRespCode(Constant.BAD_REQUEST_CODE);
				userRequest.setRespMesg(Constant.USER_EXIST);
				return userRequest;
			}
//		} else {
//			userRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
//			userRequest.setRespMesg(Constant.INVALID_TOKEN);
//			return userRequest;
//		}
	}

	
	@Transactional
	public UserRequestObject updateUserDetails(Request<UserRequestObject> userRequestObject)
			throws BizException, Exception {
		UserRequestObject userRequest = userRequestObject.getPayload();
		userHelper.validateUserRequest(userRequest);
		
//		Boolean isValid = jwtTokenUtil.validateJwtToken(userRequest.getCreatedBy(), userRequest.getToken());
//		if (isValid) {
			
		User user = userHelper.getUserDetailsByLoginId(userRequest.getLoginId());
		if (user != null) {
			user = userHelper.getUpdatedUserDetailsByReqObj(user, userRequest);
			user = userHelper.UpdateUserDetails(user);
			
			if(userRequest.getRequestedFor().equalsIgnoreCase("WEB")) {
				for (AddressRequestObject addressRequest : userRequest.getAddressList()) {
					AddressDetails addressDetails = addressHelper.getAddressDetailsByUserIdAndAddressType(user.getId(), addressRequest.getAddressType(), user.getSuperadminId());
					
					if(addressDetails != null) {
						addressService.updateAddressDetailsByRequest(addressRequest,addressDetails);
					}
				}
			}else {
				AddressRequestObject addressRequestObj = addressHelper.setAddressRequestObjectByUserReqObj(userRequest);
				AddressDetails addressDetails = addressHelper.getAddressDetailsByUserIdAndAddressType(user.getId(), addressRequestObj.getAddressType(), user.getSuperadminId());
				
				if(addressDetails != null) {
					addressService.updateAddressDetailsByRequest(addressRequestObj,addressDetails);
				}
			}
			

			userRequest.setRespCode(Constant.SUCCESS_CODE);
			userRequest.setRespMesg(Constant.UPDATED_SUCCESS);
			return userRequest;
		} else {
			userRequest.setRespCode(Constant.BAD_REQUEST_CODE);
			userRequest.setRespMesg(Constant.DATA_NOT_FOUND);
			return userRequest;
		}
//		}else {
//			userRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
//			userRequest.setRespMesg(Constant.INVALID_TOKEN);
//			return userRequest;
//		}
	}

	public UserRequestObject changeUserPassword(Request<UserRequestObject> userRequestObject)throws BizException, Exception {
		UserRequestObject userRequest = userRequestObject.getPayload();
		userHelper.validateUserRequest(userRequest);
		
		System.out.println(userRequest.getLoginId()+" , "+userRequest.getPassword());

		User user = userHelper.getUserDetailsByLoginId(userRequest.getLoginId());
		if (user != null) {
			String password = BCrypt.hashpw(userRequest.getPassword(), BCrypt.gensalt());
			user.setPassword(password);
//			userDetails.setIsPassChanged("YES");
			user = userHelper.UpdateUserDetails(user);
			
			userRequest.setStatus(user.getStatus());
			userRequest.setRespCode(Constant.SUCCESS_CODE);
			userRequest.setRespMesg(Constant.UPDATED_SUCCESS);
			return userRequest;
		}else {
			userRequest.setRespCode(Constant.BAD_REQUEST_CODE);
			userRequest.setRespMesg(Constant.DATA_NOT_FOUND);
			return userRequest;
		}
	}

	
	public UserRequestObject changeUserStatus(Request<UserRequestObject> userRequestObject)
			throws BizException, Exception {
		UserRequestObject userRequest = userRequestObject.getPayload();
		userHelper.validateUserRequest(userRequest);

		User user = userHelper.getUserDetailsByLoginId(userRequest.getLoginId());
		if (user != null) {

			if (user.getStatus().equalsIgnoreCase(Status.INACTIVE.name())) {
				user.setStatus(Status.ACTIVE.name());
			} else {
				user.setStatus(Status.INACTIVE.name());
			}
			user = userHelper.UpdateUserDetails(user);

			userRequest.setStatus(user.getStatus());
			userRequest.setRespCode(Constant.SUCCESS_CODE);
			userRequest.setRespMesg(Constant.UPDATED_SUCCESS);
			return userRequest;
		} else {
			userRequest.setRespCode(Constant.BAD_REQUEST_CODE);
			userRequest.setRespMesg("User Not Found");
			return userRequest;
		}
	}
	
	public UserRequestObject removeUserParmanent(Request<UserRequestObject> userRequestObject) 
			throws BizException, Exception {
		UserRequestObject userRequest = userRequestObject.getPayload();
		userHelper.validateUserRequest(userRequest);
		User user = userHelper.getUserDetailsByLoginIdAndSuperadminId(userRequest.getLoginId(), userRequest.getSuperadminId());
		if (user != null) {
			
			user.setStatus(Status.REMOVED.name());
			
			user.setLoginId(user.getLoginId()+"removed");
			user.setCreatedBy(user.getCreatedBy()+"removed");
			user.setSuperadminId(user.getSuperadminId()+"removed");
			user = userHelper.UpdateUserDetails(user);
			
			userRequest.setStatus(user.getStatus());
			userRequest.setRespCode(Constant.SUCCESS_CODE);
			userRequest.setRespMesg(Constant.REMOVED_SUCCESS);
			return userRequest;
		} else {
			userRequest.setRespCode(Constant.BAD_REQUEST_CODE);
			userRequest.setRespMesg("User Not Found");
			return userRequest;
		}
	}
	
	public UserRequestObject changeTeamLeader(Request<UserRequestObject> userRequestObject) 
			throws BizException, Exception {
		UserRequestObject userRequest = userRequestObject.getPayload();
		userHelper.validateUserRequest(userRequest);
		
		User user = userHelper.getUserDetailsByLoginId(userRequest.getLoginId());
		if (user != null) {
			
			user.setCreatedBy(userRequest.getTeamleaderId());
			userHelper.UpdateUserDetails(user);
			
			userRequest.setRespCode(Constant.SUCCESS_CODE);
			userRequest.setRespMesg(Constant.UPDATED_SUCCESS);
			return userRequest;
		}else {
			userRequest.setRespCode(Constant.BAD_REQUEST_CODE);
			userRequest.setRespMesg("User Not Found");
			return userRequest;
		}
	}
	
	public UserRequestObject changeUserRole(Request<UserRequestObject> userRequestObject) 
			throws BizException, Exception {
		UserRequestObject userRequest = userRequestObject.getPayload();
		userHelper.validateUserRequest(userRequest);
		
		User user = userHelper.getUserDetailsByLoginId(userRequest.getLoginId());
		if (user != null) {

			user.setRoleType(userRequest.getRoleType());
			user.setCreatedBy(user.getSuperadminId());
			
			userHelper.UpdateUserDetails(user);
			userRequest.setRespCode(Constant.SUCCESS_CODE);
			userRequest.setRespMesg(Constant.UPDATED_SUCCESS);
			return userRequest;
		} else {
			userRequest.setRespCode(Constant.BAD_REQUEST_CODE);
			userRequest.setRespMesg("User Not Found");
			return userRequest;
		}
	}


	public List<User> getUserDetails(Request<UserRequestObject> userRequestObject) {
		UserRequestObject userRequest = userRequestObject.getPayload();
		List<User> userList = new ArrayList<User>();
//		Boolean isValid = jwtTokenUtil.validateJwtToken(userRequest.getCreatedBy(), userRequest.getToken());
//		if (isValid) {
		userList = userHelper.getUserDetails(userRequest);  
		System.out.println(userHelper.getUserDetails(userRequest));
//	}
		return userList;
	}
	
	public List<User> getUserDetailsByRoleType(Request<UserRequestObject> userRequestObject) {
		UserRequestObject userRequest = userRequestObject.getPayload();
		List<User> userList = userHelper.getUserDetailsByRoleType(userRequest);
		return userList;
	}
	
	public List<User> getUserListForDropDown(Request<UserRequestObject> userRequestObject) {
		UserRequestObject userRequest = userRequestObject.getPayload();
		List<User> userList = userHelper.getUserListForDropDown(userRequest);
		return userList;
	}

	public List<AddressDetails> getAddressDetails(Request<UserRequestObject> userRequestObject) {
		UserRequestObject userRequest = userRequestObject.getPayload();
		List<AddressDetails> addressList = userHelper.getAddressDetails(userRequest);
		return addressList;
	}

	
}
