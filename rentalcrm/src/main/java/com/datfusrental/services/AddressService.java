package com.datfusrental.services;

import javax.transaction.Transactional;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datfusrental.entities.AddressDetails;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.AddressHelper;
import com.datfusrental.jwt.JwtTokenUtil;
import com.datfusrental.object.request.AddressRequestObject;


@Service
public class AddressService {
	
	private final Logger logger = Logger.getLogger(this.getClass().getName());


	@Autowired
	private AddressHelper addressHelper;

	
	

	@Transactional
	public AddressDetails saveAddressDetailsByRequest(AddressRequestObject addressRequest, Long id, String superadminId)
			throws BizException {
		addressHelper.validateAddressRequest(addressRequest);

		AddressDetails addressDetails = addressHelper.getAddressDetailsByReqObj(addressRequest, id, superadminId);
		addressHelper.saveAddressDetails(addressDetails);
		return addressDetails;
		
	}
	
	@Transactional
	public AddressDetails updateAddressDetailsByRequest(AddressRequestObject addressRequest, AddressDetails addressDetails)
			throws BizException {
		addressHelper.validateAddressRequest(addressRequest);

		addressHelper.getUpdatedAddressDetailsByReqObj(addressRequest, addressDetails);
		addressHelper.updateAddressDetails(addressDetails);
		return addressDetails;
		
	}


	
	
//	public List<UserDetails> getUserDetails(Request<UserRequestObject> userRequestObject) {
//		UserRequestObject userRequest = userRequestObject.getPayload();
//		List<UserDetails> userList = userHelper.getUserDetails(userRequest);
//		return userList;
//	}

}
