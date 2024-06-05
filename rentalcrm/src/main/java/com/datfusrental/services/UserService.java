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
import com.datfusrental.entities.UserDetails;
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
		
		UserDetails userDetails = userHelper.getUserDetailsByLoginId(loginRequest.getLoginId());
		if (userDetails != null) {
			if(userDetails.getStatus().equalsIgnoreCase(Status.INACTIVE.name())) {
				
				loginRequest.setRespCode(Constant.BAD_REQUEST_CODE);
				loginRequest.setRespMesg(Constant.INACTIVE_USER);
				return loginRequest;
			}
			
			boolean isValid = userHelper.checkValidityOfUser(userDetails.getValidityExpireOn());
			if (isValid) {
				if (BCrypt.checkpw(loginRequest.getPassword(), userDetails.getSalt())) {
					logger.info("Login Successfully: " + loginRequest);

					// generate secret key.
					String secretKey = jwtTokenUtil.generateSecretKey();

					// update secret key in UserDetails.
					userDetails.setSecretTokenKey(secretKey);
					userHelper.UpdateUserDetails(userDetails);

					String token = jwtTokenUtil.generateJwtToken(userDetails);

					loginRequest.setLoginId(userDetails.getLoginId());
					loginRequest.setPassword(null);

					loginRequest.setFirstName(userDetails.getFirstName());
					loginRequest.setLastName(userDetails.getLastName());
					loginRequest.setService(userDetails.getService());
					loginRequest.setPermissions(userDetails.getPermissions());
					loginRequest.setRoleType(userDetails.getRoleType());
					loginRequest.setSuperadminId(userDetails.getSuperadminId());
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
	
	public UserRequestObject getUserDetailsByLoginId(Request<UserRequestObject> userRequestObject) throws BizException {
		UserRequestObject userRequest = userRequestObject.getPayload();
		userHelper.validateUserRequest(userRequest);

		UserDetails userDetails = userHelper.getUserDetailsByLoginId(userRequest.getLoginId());
		if (userDetails != null) {
			
			userRequest.setFirstName(userDetails.getFirstName());
			userRequest.setLastName(userDetails.getLastName());
			userRequest.setUserPicture(userDetails.getUserPicture());
			userRequest.setRoleType(userDetails.getRoleType());
			
			userRequest.setRespCode(Constant.SUCCESS_CODE);
			return userRequest;
		}
		return userRequest;
	}
	

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

			UserDetails existsUserDetails = userHelper.getUserDetailsByLoginIdAndSuperadminId(userRequest.getMobileNo(), userRequest.getSuperadminId());
			if (existsUserDetails == null) {
				userRequest.setPassword("12345");

				String salt = BCrypt.gensalt();
				String password = BCrypt.hashpw(userRequest.getPassword(), salt);
				userRequest.setPassword(password);
				userRequest.setSalt(password);
				
//				String userCode = userRequest.getFirstName().substring(0,1)+userRequest.getLastName().substring(0,1);

				UserDetails userDetails = userHelper.getUserDetailsByReqObj(userRequest);
				userDetails = userHelper.saveUserDetails(userDetails);
				
				// Save Address

				for (AddressRequestObject addressRequest : userRequest.getAddressList()) {
					addressRequest.setUserType(userRequest.getRoleType());
					addressService.saveAddressDetailsByRequest(addressRequest, userDetails.getId(), userRequest.getSuperadminId());
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
			
		UserDetails userDetails = userHelper.getUserDetailsByLoginId(userRequest.getLoginId());
		if (userDetails != null) {
			System.out.println("Enter 1");
			userDetails = userHelper.getUpdatedUserDetailsByReqObj(userDetails, userRequest);
			userDetails = userHelper.UpdateUserDetails(userDetails);
			
			System.out.println("Enter 2");
			
			if(userRequest.getRequestedFor().equalsIgnoreCase("WEB")) {
				for (AddressRequestObject addressRequest : userRequest.getAddressList()) {
					AddressDetails addressDetails = addressHelper.getAddressDetailsByUserIdAndAddressType(userDetails.getId(), addressRequest.getAddressType(), userDetails.getSuperadminId());
					
					if(addressDetails != null) {
						addressService.updateAddressDetailsByRequest(addressRequest,addressDetails);
					}
				}
			}else {
				AddressRequestObject addressRequestObj = addressHelper.setAddressRequestObjectByUserReqObj(userRequest);
				AddressDetails addressDetails = addressHelper.getAddressDetailsByUserIdAndAddressType(userDetails.getId(), addressRequestObj.getAddressType(), userDetails.getSuperadminId());
				
				if(addressDetails != null) {
					addressService.updateAddressDetailsByRequest(addressRequestObj,addressDetails);
				}
			}
			

			userRequest.setRespCode(Constant.SUCCESS_CODE);
			userRequest.setRespMesg(Constant.UPDATED_SUCCESS);
			return userRequest;
		} else {
			userRequest.setRespCode(Constant.BAD_REQUEST_CODE);
			userRequest.setRespMesg(Constant.NOT_EXIST);
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

		UserDetails userDetails = userHelper.getUserDetailsByLoginId(userRequest.getLoginId());
		if (userDetails != null) {
			String password = BCrypt.hashpw(userRequest.getPassword(), BCrypt.gensalt());
			userDetails.setPassword(password);
//			userDetails.setIsPassChanged("YES");
			userDetails = userHelper.UpdateUserDetails(userDetails);
			
			userRequest.setStatus(userDetails.getStatus());
			userRequest.setRespCode(Constant.SUCCESS_CODE);
			userRequest.setRespMesg(Constant.UPDATED_SUCCESS);
			return userRequest;
		}else {
			userRequest.setRespCode(Constant.BAD_REQUEST_CODE);
			userRequest.setRespMesg(Constant.NOT_EXIST);
			return userRequest;
		}
	}

	
	public UserRequestObject changeUserStatus(Request<UserRequestObject> userRequestObject)
			throws BizException, Exception {
		UserRequestObject userRequest = userRequestObject.getPayload();
		userHelper.validateUserRequest(userRequest);

		UserDetails userDetails = userHelper.getUserDetailsByLoginId(userRequest.getLoginId());
		if (userDetails != null) {

			if (userDetails.getStatus().equalsIgnoreCase(Status.INACTIVE.name())) {
				userDetails.setStatus(Status.ACTIVE.name());
			} else {
				userDetails.setStatus(Status.INACTIVE.name());
			}
			userDetails = userHelper.UpdateUserDetails(userDetails);

			userRequest.setStatus(userDetails.getStatus());
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
		UserDetails userDetails = userHelper.getUserDetailsByLoginIdAndSuperadminId(userRequest.getLoginId(), userRequest.getSuperadminId());
		if (userDetails != null) {
			
			userDetails.setStatus(Status.REMOVED.name());
			
			userDetails.setLoginId(userDetails.getLoginId()+"removed");
			userDetails.setCreatedBy(userDetails.getCreatedBy()+"removed");
			userDetails.setSuperadminId(userDetails.getSuperadminId()+"removed");
			userDetails = userHelper.UpdateUserDetails(userDetails);
			
			userRequest.setStatus(userDetails.getStatus());
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
		
		UserDetails userDetails = userHelper.getUserDetailsByLoginId(userRequest.getLoginId());
		if (userDetails != null) {
			
			userDetails.setCreatedBy(userRequest.getTeamleaderId());
			userHelper.UpdateUserDetails(userDetails);
			
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
		
		UserDetails userDetails = userHelper.getUserDetailsByLoginId(userRequest.getLoginId());
		if (userDetails != null) {

			userDetails.setRoleType(userRequest.getRoleType());
			userDetails.setCreatedBy(userDetails.getSuperadminId());
			
			userHelper.UpdateUserDetails(userDetails);
			userRequest.setRespCode(Constant.SUCCESS_CODE);
			userRequest.setRespMesg(Constant.UPDATED_SUCCESS);
			return userRequest;
		} else {
			userRequest.setRespCode(Constant.BAD_REQUEST_CODE);
			userRequest.setRespMesg("User Not Found");
			return userRequest;
		}
	}


	public List<UserDetails> getUserDetails(Request<UserRequestObject> userRequestObject) {
		UserRequestObject userRequest = userRequestObject.getPayload();
		List<UserDetails> userList = new ArrayList<UserDetails>();
//		Boolean isValid = jwtTokenUtil.validateJwtToken(userRequest.getCreatedBy(), userRequest.getToken());
//		if (isValid) {
		userList = userHelper.getUserDetails(userRequest);  
		System.out.println(userHelper.getUserDetails(userRequest));
//	}
		return userList;
	}
	
	public List<UserDetails> getUserDetailsByRoleType(Request<UserRequestObject> userRequestObject) {
		UserRequestObject userRequest = userRequestObject.getPayload();
		System.out.println(" hh : "+userRequest);
		List<UserDetails> userList = userHelper.getUserDetailsByRoleType(userRequest);
		return userList;
	}
	
	public List<UserDetails> getUserListForDropDown(Request<UserRequestObject> userRequestObject) {
		UserRequestObject userRequest = userRequestObject.getPayload();
		List<UserDetails> userList = userHelper.getUserListForDropDown(userRequest);
		return userList;
	}

	public List<AddressDetails> getAddressDetails(Request<UserRequestObject> userRequestObject) {
		UserRequestObject userRequest = userRequestObject.getPayload();
		List<AddressDetails> addressList = userHelper.getAddressDetails(userRequest);
		return addressList;
	}

	
}
