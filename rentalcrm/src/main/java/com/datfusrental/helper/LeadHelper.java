package com.datfusrental.helper;

import java.util.ArrayList;
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
import com.datfusrental.enums.RequestFor;
import com.datfusrental.enums.RoleType;
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
//		leadDetails.setEnquirySource(leadRequest.getEnquirySource());
		leadDetails.setCategory(leadRequest.getCategory());
		leadDetails.setSubCategory(leadRequest.getSubCategory());
		leadDetails.setItemName(leadRequest.getItemName());
		
		leadDetails.setPickupDateTime(leadRequest.getPickupDateTime());
		leadDetails.setPickupLocation(leadRequest.getPickupLocation());
		leadDetails.setPickupPoint(leadRequest.getPickupPoint());
		
		leadDetails.setDropDateTime(leadRequest.getDropDateTime());
		leadDetails.setDropLocation(leadRequest.getDropLocation());
		leadDetails.setDropPoint(leadRequest.getDropPoint());
		
		leadDetails.setCustomeName(leadRequest.getCustomeName());
		leadDetails.setCountryDialCode(leadRequest.getCountryDialCode());
		leadDetails.setCustomerMobile(leadRequest.getCustomerMobile());
		leadDetails.setCustomerEmailId(leadRequest.getCustomerEmailId());
		
		leadDetails.setQuantity(leadRequest.getQuantity());
		
		leadDetails.setVendorRate(leadRequest.getVendorRate());
		leadDetails.setPayToVendor(leadRequest.getPayToVendor());
		leadDetails.setCompanyRate(leadRequest.getCompanyRate());
		leadDetails.setPayToCompany(leadRequest.getPayToCompany());
		
		leadDetails.setBookingAmount(leadRequest.getBookingAmount());
		leadDetails.setBalanceAmount(leadRequest.getBalanceAmount());
		leadDetails.setTotalAmount(leadRequest.getTotalAmount());
		leadDetails.setSecurityAmount(leadRequest.getSecurityAmount());
		
		leadDetails.setVendorName(leadRequest.getVendorName());
		leadDetails.setRemarks(leadRequest.getRemarks());
		leadDetails.setStatus(leadRequest.getStatus());
		
		leadDetails.setLeadOrigine(leadRequest.getLeadOrigine());
		leadDetails.setLeadType(leadRequest.getLeadType());
		
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
	public List<LeadDetails> getEnquaryDetailsByDate(LeadRequestObject leadRequest) {
		List<LeadDetails> results = new ArrayList<LeadDetails>();
		if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.SUPERADMIN.name())) {
			if (leadRequest.getRequestedFor().equalsIgnoreCase(RequestFor.ALL.name())) {
				results = leadDetailsDao.getEntityManager().createQuery(
//						"SELECT LD FROM LeadDetails LD WHERE LD.status =:status AND LD.superadminId =:superadminId AND LD.createdAt BETWEEN :firstDate AND :lastDate ORDER BY LD.id DESC")
					"SELECT LD FROM LeadDetails LD WHERE LD.superadminId =:superadminId AND LD.createdAt BETWEEN :firstDate AND :lastDate ORDER BY LD.id DESC")
//						.setParameter("status", leadRequest)
						.setParameter("superadminId", leadRequest.getSuperadminId())
						.setParameter("firstDate", leadRequest.getFirstDate())
						.setParameter("lastDate", leadRequest.getLastDate()).getResultList();
				return results;
			} else if (leadRequest.getRequestedFor().equalsIgnoreCase(RequestFor.BYCATEGORY.name())) {
				results = leadDetailsDao.getEntityManager().createQuery(
						"SELECT LD FROM LeadDetails LD WHERE LD.status =:status AND LD.category =:category AND LD.superadminId =:superadminId AND LD.createdAt BETWEEN :firstDate AND :lastDate ORDER BY LD.id DESC")
						.setParameter("status", leadRequest)
						.setParameter("superadminId", leadRequest.getSuperadminId())
						.setParameter("category", leadRequest.getCategory())
						.setParameter("firstDate", leadRequest.getFirstDate())
						.setParameter("lastDate", leadRequest.getLastDate()).getResultList();
				return results;
			}

		} else {
			if (leadRequest.getRequestedFor().equalsIgnoreCase(RequestFor.ALL.name())) {
				results = leadDetailsDao.getEntityManager().createQuery(
						"SELECT LD FROM LeadDetails LD WHERE LD.status =:status AND LD.superadminId =:superadminId AND LD.createdBy =:createdBy AND LD.createdAt BETWEEN :firstDate AND :lastDate ORDER BY LD.id DESC")
						.setParameter("status", leadRequest)
						.setParameter("superadminId", leadRequest.getSuperadminId())
						.setParameter("createdBy", leadRequest.getCreatedBy())
						.setParameter("firstDate", leadRequest.getFirstDate())
						.setParameter("lastDate", leadRequest.getLastDate()).getResultList();
				return results;
			} else if (leadRequest.getRequestedFor().equalsIgnoreCase(RequestFor.BYCATEGORY.name())) {
				results = leadDetailsDao.getEntityManager().createQuery(
						"SELECT LD FROM LeadDetails LD WHERE LD.status =:status AND LD.category =:category AND LD.superadminId =:superadminId AND LD.createdBy =:createdBy AND LD.createdAt BETWEEN :firstDate AND :lastDate ORDER BY LD.id DESC")
						.setParameter("status", leadRequest)
						.setParameter("superadminId", leadRequest.getSuperadminId())
						.setParameter("createdBy", leadRequest.getCreatedBy())
						.setParameter("category", leadRequest.getCategory())
						.setParameter("firstDate", leadRequest.getFirstDate())
						.setParameter("lastDate", leadRequest.getLastDate()).getResultList();
				return results;
			}
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<LeadDetails> getAllLeadList(LeadRequestObject leadRequest) {
		List<LeadDetails> results = new ArrayList<LeadDetails>();
		if(leadRequest.getRoleType().equalsIgnoreCase(RoleType.SUPERADMIN.name())) {
			results = leadDetailsDao.getEntityManager().createQuery(
					"SELECT LD FROM LeadDetails LD WHERE LD.superadminId =:superadminId ORDER BY LD.id DESC")
					.setParameter("superadminId", leadRequest.getSuperadminId())
					.getResultList();
		} else if(leadRequest.getRoleType().equalsIgnoreCase(RoleType.CUSTOMER_EXECUTIVE.name())) {
			results = leadDetailsDao.getEntityManager().createQuery(
					"SELECT LD FROM LeadDetails LD WHERE LD.createdBy =:createdBy AND LD.superadminId =:superadminId ORDER BY LD.id DESC")
					.setParameter("createdBy", leadRequest.getCreatedBy())
					.setParameter("superadminId", leadRequest.getSuperadminId())
					.getResultList();
		}
		return results;
	}
	

}
