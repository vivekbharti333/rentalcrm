package com.datfusrental.helper;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.datfusrental.constant.Constant;
import com.datfusrental.dao.LeadDetailsDao;
import com.datfusrental.dao.UserRoleMasterDao;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.entities.UserRoleMaster;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.LeadRequestObject;
import com.datfusrental.object.request.UserRequestObject;

@Component
public class LeadHelper {

	@Autowired
	private UserRoleMasterDao userRoleMasterDao;
	
	@Autowired
	private LeadDetailsDao leadDetailsDao;

	public void validateLeadRequest(LeadRequestObject leadRequestObject) throws BizException {
		if (leadRequestObject == null) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Bad Request Object Null");
		}
	}

	@Transactional
	public LeadDetails getLeadDetailsByBookingId(String bookingId) {

		CriteriaBuilder criteriaBuilder = leadDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<LeadDetails> criteriaQuery = criteriaBuilder.createQuery(LeadDetails.class);
		Root<LeadDetails> root = criteriaQuery.from(LeadDetails.class);
		Predicate restriction = criteriaBuilder.equal(root.get("bookingId"), bookingId);
		criteriaQuery.where(restriction);
		LeadDetails leadDetails = leadDetailsDao.getSession().createQuery(criteriaQuery).uniqueResult();
		return leadDetails;
	}

	public LeadDetails getLeadDetailsByReqObj(LeadRequestObject leadRequest) {

		LeadDetails leadDetails = new LeadDetails();

		leadDetails.setBookingId(leadRequest.getBookingId());
		leadDetails.setCompanyName(leadRequest.getCompanyName());
		leadDetails.setEnquiryBy(leadRequest.getEnquiryBy());
		leadDetails.setCategory(leadRequest.getCategory());
		leadDetails.setSubCategory(leadRequest.getSubCategory());
		leadDetails.setItemName(leadRequest.getItemName());
		
		leadDetails.setPickupDateTime(leadRequest.getPickupDateTime());
		leadDetails.setPickupLocation(leadRequest.getPickupLocation());
		
		leadDetails.setDropDateTime(leadRequest.getDropDateTime());
		leadDetails.setDropLocation(leadRequest.getDropLocation());
		
		leadDetails.setCustomeName(leadRequest.getCustomeName());
		leadDetails.setCountryDialCode(leadRequest.getCountryDialCode());
		leadDetails.setCustomerMobile(leadRequest.getCustomerMobile());
		leadDetails.setCustomerEmailId(leadRequest.getCustomerEmailId());
		
		leadDetails.setQuantity(leadRequest.getQuantity());
		
		leadDetails.setVendorAmount(leadRequest.getVendorAmount());
		leadDetails.setSellAmount(leadRequest.getSellAmount());
		
		leadDetails.setBookingAmount(leadRequest.getBookingAmount());
		leadDetails.setBalanceAmount(leadRequest.getBalanceAmount());
		leadDetails.setTotalAmount(leadRequest.getTotalAmount());
		leadDetails.setSecurityAmount(leadRequest.getSecurityAmount());
		
		leadDetails.setVendorName(leadRequest.getVendorName());
		leadDetails.setNotes(leadRequest.getNotes());
		leadDetails.setStatus(leadRequest.getStatus());
		
		leadDetails.setCreatedAt(new Date());
		leadDetails.setUpdatedAt(new Date());
		
		leadDetails.setCreatedBy(leadRequest.getCreatedBy());
		leadDetails.setSuperadminId(leadRequest.getSuperadminId());

		return leadDetails;
	}

	@Transactional
	public LeadDetails saveLeadDetails(LeadDetails leadDetails) {
		leadDetailsDao.persist(leadDetails);
		return leadDetails;
	}

	@SuppressWarnings("unchecked")
	public List<UserRoleMaster> getUserRoleMaster(UserRequestObject userRequest) {
		if (userRequest.getRequestedFor().equals("ALL")) {
			List<UserRoleMaster> results = userRoleMasterDao.getEntityManager()
					.createQuery("SELECT AD FROM UserRoleMaster AD ORDER BY AD.roleType ASC").getResultList();
			return results;
		}
		return null;
	}

}