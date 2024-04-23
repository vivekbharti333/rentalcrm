package com.datfusrental.helper;

import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.datfusrental.constant.Constant;
import com.datfusrental.dao.AddressDetailsDao;
import com.datfusrental.entities.AddressDetails;
import com.datfusrental.entities.UserDetails;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.AddressRequestObject;
import com.datfusrental.object.request.UserRequestObject;

@Component
public class AddressHelper {

	@Autowired
	private AddressDetailsDao addressDetailsDao;

	public void validateAddressRequest(AddressRequestObject addressRequestObject) throws BizException {
		if (addressRequestObject == null) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Bad Request Object Null");
		}
	}

	public static String generateRandomChars(String candidateChars, int length) {
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(candidateChars.charAt(random.nextInt(candidateChars.length())));
		}
		return sb.toString();
	}
	
	@Transactional
	public AddressDetails getAddressDetailsByUserIdAndAddressType(Long userId, String addressType, String superadminId) {

	    CriteriaBuilder criteriaBuilder = addressDetailsDao.getSession().getCriteriaBuilder();
	    CriteriaQuery<AddressDetails> criteriaQuery = criteriaBuilder.createQuery(AddressDetails.class);
	    Root<AddressDetails> root = criteriaQuery.from(AddressDetails.class);

	    Predicate userIdPredicate = criteriaBuilder.equal(root.get("userId"), userId);
	    Predicate addressTypePredicate = criteriaBuilder.equal(root.get("addressType"), addressType);
	    Predicate restriction = criteriaBuilder.and(userIdPredicate, addressTypePredicate);

	    criteriaQuery.where(restriction);
	    AddressDetails addressDetails = addressDetailsDao.getSession().createQuery(criteriaQuery).uniqueResult();
	    return addressDetails;
	}
	
	public AddressRequestObject setAddressRequestObjectByUserReqObj(UserRequestObject userRequest) 
	{
		AddressRequestObject addressRequestObj = new AddressRequestObject();

		addressRequestObj.setUserType(userRequest.getUserType());
		addressRequestObj.setAddressType(userRequest.getAddressType());
		addressRequestObj.setAddressLine(userRequest.getAddressLine());
		addressRequestObj.setLandmark(userRequest.getLandmark());
		addressRequestObj.setDistrict(userRequest.getDistrict());
		addressRequestObj.setCity(userRequest.getCity());
		addressRequestObj.setState(userRequest.getState());
		addressRequestObj.setPincode(userRequest.getPincode());

		return addressRequestObj;
	}

	

	@Transactional
	public AddressDetails getAddressDetailsByReqObj(AddressRequestObject addressRequest, Long id, String superadminId) {

		AddressDetails addressDetails = new AddressDetails();
		
		addressDetails.setUserId(id);
		addressDetails.setUserType(addressRequest.getUserType());
		addressDetails.setAddressType(addressRequest.getAddressType());
		addressDetails.setAddressLine(addressRequest.getAddressLine());
		addressDetails.setLandmark(addressRequest.getLandmark());
		addressDetails.setDistrict(addressRequest.getDistrict());
		addressDetails.setCity(addressRequest.getCity());
		addressDetails.setState(addressRequest.getState());
		addressDetails.setCountry("INDIA");
		addressDetails.setPincode(addressRequest.getPincode());

		addressDetails.setCreatedAt(new Date());
		addressDetails.setUpdatedAt(new Date());
		addressDetails.setSuperadminId(superadminId);

		return addressDetails;
	}

	@Transactional
	public AddressDetails saveAddressDetails(AddressDetails addressDetails) {
		addressDetailsDao.persist(addressDetails);
		return addressDetails;
	}
	
	@Transactional
	public AddressDetails getUpdatedAddressDetailsByReqObj(AddressRequestObject addressRequest, AddressDetails addressDetails) {

		addressDetails.setAddressType(addressRequest.getAddressType());
		addressDetails.setAddressLine(addressRequest.getAddressLine());
		addressDetails.setLandmark(addressRequest.getLandmark());
		addressDetails.setDistrict(addressRequest.getDistrict());
		addressDetails.setCity(addressRequest.getCity());
		addressDetails.setState(addressRequest.getState());
		addressDetails.setCountry("INDIA");
		addressDetails.setPincode(addressRequest.getPincode());

		addressDetails.setUpdatedAt(new Date());
		return addressDetails;
	}
	
	@Transactional
	public AddressDetails updateAddressDetails(AddressDetails addressDetails) {
		addressDetailsDao.update(addressDetails);
		return addressDetails;
	}


	@SuppressWarnings("unchecked")
	public List<AddressDetails> getAddressDetails(UserRequestObject userRequest) {

		String hqlQuery = "";
		if (userRequest.getRequestedFor().equals("ALL")) {
			hqlQuery = "SELECT UD FROM AddressDetails UD";
		} else if (userRequest.getRequestedFor().equals("STUDENT")) {

		}
		List<AddressDetails> results = addressDetailsDao.getEntityManager().createQuery(hqlQuery).getResultList();
		return results;
	}

}
