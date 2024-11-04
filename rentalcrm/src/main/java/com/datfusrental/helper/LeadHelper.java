package com.datfusrental.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.TemporalType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.datfusrental.constant.Constant;
import com.datfusrental.dao.LeadDetailsDao;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.entities.User;
import com.datfusrental.enums.RequestFor;
import com.datfusrental.enums.RoleType;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.LeadRequestObject;

@Component
public class LeadHelper {

	@Autowired
	private LeadDetailsDao leadDetailsDao;
	
	@Autowired
	private UserHelper userHelper;

	public void validateLeadRequest(LeadRequestObject leadRequestObject) throws BizException {
		if (leadRequestObject == null) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Bad Request Object Null");
		}
	}

	@Transactional
	public LeadDetails getLeadDetailsById(Long id) {

		CriteriaBuilder criteriaBuilder = leadDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<LeadDetails> criteriaQuery = criteriaBuilder.createQuery(LeadDetails.class);
		Root<LeadDetails> root = criteriaQuery.from(LeadDetails.class);
		Predicate restriction = criteriaBuilder.equal(root.get("id"), id);
		criteriaQuery.where(restriction);
		LeadDetails leadDetails = leadDetailsDao.getSession().createQuery(criteriaQuery).uniqueResult();
		return leadDetails;
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
		
		User user = userHelper.getUserDetailsByLoginId(leadRequest.getCreatedBy());

		LeadDetails leadDetails = new LeadDetails();

		leadDetails.setBookingId(leadRequest.getBookingId());
		leadDetails.setCompanyName(leadRequest.getCompanyName());
		leadDetails.setCategoryTypeName(leadRequest.getCategoryTypeName());
		leadDetails.setSuperCategory(leadRequest.getSuperCategory());
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
		leadDetails.setChildrenQuantity(leadRequest.getChildrenQuantity());
		leadDetails.setInfantQuantity(leadRequest.getInfantQuantity());

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
		leadDetails.setCreatedByName(user.getFirstName()+ " "+user.getLastName());
		leadDetails.setTeamleaderId(user.getTeamleaderId());
		leadDetails.setAdminId(user.getAdminId());
		leadDetails.setSuperadminId(leadRequest.getSuperadminId());

		return leadDetails;
	}

	@Transactional
	public LeadDetails saveLeadDetails(LeadDetails leadDetails) {
		leadDetailsDao.persist(leadDetails);
		return leadDetails;
	}

	public LeadDetails getUpdatedLeadDetailsByReqObj(LeadRequestObject leadRequest) {

		LeadDetails leadDetails = new LeadDetails();

		leadDetails.setBookingId(leadRequest.getBookingId());
		leadDetails.setCompanyName(leadRequest.getCompanyName());
		leadDetails.setCategoryTypeName(leadRequest.getCategoryTypeName());
		leadDetails.setSuperCategory(leadRequest.getSuperCategory());
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
		leadDetails.setChildrenQuantity(leadRequest.getChildrenQuantity());
		leadDetails.setInfantQuantity(leadRequest.getInfantQuantity());

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

		leadDetails.setUpdatedAt(new Date());

		leadDetails.setCreatedBy(leadRequest.getCreatedBy());
		leadDetails.setSuperadminId(leadRequest.getSuperadminId());

		return leadDetails;
	}

	@Transactional
	public LeadDetails updateLeadDetails(LeadDetails leadDetails) {
		leadDetailsDao.persist(leadDetails);
		return leadDetails;
	}

//	@SuppressWarnings("unchecked")
//	public List<LeadDetails> getEnquaryDetailsByDate(LeadRequestObject leadRequest) {
//		List<LeadDetails> results = new ArrayList<LeadDetails>();
//		if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.SUPERADMIN.name())) {
//			if (leadRequest.getRequestedFor().equalsIgnoreCase(RequestFor.BYCATEGORY.name())) {
//				
//				results = leadDetailsDao.getEntityManager().createQuery(
//						"SELECT LD FROM LeadDetails LD WHERE LD.status =:status AND LD.category =:category AND LD.superadminId =:superadminId AND LD.createdAt BETWEEN :firstDate AND :lastDate ORDER BY LD.id DESC")
//						.setParameter("status", leadRequest).setParameter("superadminId", leadRequest.getSuperadminId())
//						.setParameter("category", leadRequest.getCategory())
//						.setParameter("firstDate", leadRequest.getFirstDate())
//						.setParameter("lastDate", leadRequest.getLastDate()).getResultList();
//				return results;
//				
//			} else  {
//				results = leadDetailsDao.getEntityManager().createQuery(
//						"SELECT LD FROM LeadDetails LD WHERE LD.superadminId =:superadminId AND LD.createdAt BETWEEN :firstDate AND :lastDate ORDER BY LD.id DESC")
//						.setParameter("superadminId", leadRequest.getSuperadminId())
//						.setParameter("firstDate", leadRequest.getFirstDate())
//						.setParameter("lastDate", leadRequest.getLastDate()).getResultList();
//				return results;
//			}
//
//		} else {
//			if (leadRequest.getRequestedFor().equalsIgnoreCase(RequestFor.ALL.name())) {
//				results = leadDetailsDao.getEntityManager().createQuery(
//						"SELECT LD FROM LeadDetails LD WHERE LD.status =:status AND LD.superadminId =:superadminId AND LD.createdBy =:createdBy AND LD.createdAt BETWEEN :firstDate AND :lastDate ORDER BY LD.id DESC")
//						.setParameter("status", leadRequest).setParameter("superadminId", leadRequest.getSuperadminId())
//						.setParameter("createdBy", leadRequest.getCreatedBy())
//						.setParameter("firstDate", leadRequest.getFirstDate())
//						.setParameter("lastDate", leadRequest.getLastDate()).getResultList();
//				return results;
//			} else if (leadRequest.getRequestedFor().equalsIgnoreCase(RequestFor.BYCATEGORY.name())) {
//				results = leadDetailsDao.getEntityManager().createQuery(
//						"SELECT LD FROM LeadDetails LD WHERE LD.status =:status AND LD.category =:category AND LD.superadminId =:superadminId AND LD.createdBy =:createdBy AND LD.createdAt BETWEEN :firstDate AND :lastDate ORDER BY LD.id DESC")
//						.setParameter("status", leadRequest).setParameter("superadminId", leadRequest.getSuperadminId())
//						.setParameter("createdBy", leadRequest.getCreatedBy())
//						.setParameter("category", leadRequest.getCategory())
//						.setParameter("firstDate", leadRequest.getFirstDate())
//						.setParameter("lastDate", leadRequest.getLastDate()).getResultList();
//				return results;
//			}
//		}
//		return results;
//	}

	@SuppressWarnings("unchecked")
	public List<LeadDetails> getAllLeadList(LeadRequestObject leadRequest) {
		List<LeadDetails> results = new ArrayList<LeadDetails>();
		if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.SUPERADMIN.name())) {
			if (leadRequest.getRequestedFor().equalsIgnoreCase(RequestFor.BYDATE.name())) {
				results = leadDetailsDao.getEntityManager().createQuery(
						"SELECT LD FROM LeadDetails LD WHERE LD.superadminId =:superadminId AND LD.createdAt BETWEEN :firstDate AND :lastDate ORDER BY LD.id DESC")
						.setParameter("superadminId", leadRequest.getSuperadminId())
						.setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.DATE)
						.setParameter("lastDate", leadRequest.getLastDate(), TemporalType.DATE)
						.getResultList();
			} else {
				results = leadDetailsDao.getEntityManager().createQuery(
						"SELECT LD FROM LeadDetails LD WHERE LD.superadminId =:superadminId ORDER BY LD.id DESC")
						.setParameter("superadminId", leadRequest.getSuperadminId())
						.setFirstResult(Constant.FIRST_RESULT)
						.setMaxResults(Constant.MAX_RESULT)
						.getResultList();
			}
		} else if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.ADMIN.name())) {
			if (leadRequest.getRequestedFor().equalsIgnoreCase(RequestFor.BYDATE.name())) {
				results = leadDetailsDao.getEntityManager().createQuery(
						"SELECT LD FROM LeadDetails LD WHERE LD.superadminId =:superadminId AND LD.adminId=:adminId AND LD.createdAt BETWEEN :firstDate AND :lastDate ORDER BY LD.id DESC")
						.setParameter("superadminId", leadRequest.getSuperadminId())
						.setParameter("adminId", leadRequest.getAdminId())
						.setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.DATE)
						.setParameter("lastDate", leadRequest.getLastDate(), TemporalType.DATE)
						.getResultList();
			} else {
				results = leadDetailsDao.getEntityManager().createQuery(
						"SELECT LD FROM LeadDetails LD WHERE LD.superadminId =:superadminId AND LD.adminId=:adminId ORDER BY LD.id DESC")
						.setParameter("superadminId", leadRequest.getSuperadminId())
						.setParameter("adminId", leadRequest.getAdminId())
						.setFirstResult(Constant.FIRST_RESULT)
						.setMaxResults(Constant.MAX_RESULT)
						.getResultList();
			}
		} else if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.TEAM_LEADER.name())) {
			if (leadRequest.getRequestedFor().equalsIgnoreCase(RequestFor.BYDATE.name())) {
				results = leadDetailsDao.getEntityManager().createQuery(
						"SELECT LD FROM LeadDetails LD WHERE LD.superadminId =:superadminId AND LD.adminId=:adminId AND LD.teamleaderId =:teamleaderId AND LD.createdAt BETWEEN :firstDate AND :lastDate ORDER BY LD.id DESC")
						.setParameter("superadminId", leadRequest.getSuperadminId())
						.setParameter("adminId", leadRequest.getAdminId())
						.setParameter("teamLeaderId", leadRequest.getTeamleaderId())
						.setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.DATE)
						.setParameter("lastDate", leadRequest.getLastDate(), TemporalType.DATE)
						.getResultList();
			} else {
				results = leadDetailsDao.getEntityManager().createQuery(
						"SELECT LD FROM LeadDetails LD WHERE LD.superadminId =:superadminId AND LD.adminId=:adminId AND LD.teamleaderId =:teamleaderId ORDER BY LD.id DESC")
						.setParameter("superadminId", leadRequest.getSuperadminId())
						.setParameter("adminId", leadRequest.getAdminId())
						.setParameter("teamleaderId", leadRequest.getTeamleaderId())
						.setFirstResult(Constant.FIRST_RESULT).setMaxResults(Constant.MAX_RESULT)
						.getResultList();
			}
			// role type else
		} else {
			if (leadRequest.getRequestedFor().equalsIgnoreCase(RequestFor.BYDATE.name())) {
				results = leadDetailsDao.getEntityManager().createQuery(
						"SELECT LD FROM LeadDetails LD WHERE LD.createdBy =:createdBy AND LD.adminId=:adminId AND LD.superadminId =:superadminId AND LD.createdAt BETWEEN :firstDate AND :lastDate ORDER BY LD.id DESC")
						.setParameter("createdBy", leadRequest.getCreatedBy())
						.setParameter("adminId", leadRequest.getAdminId())
						.setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.DATE)
						.setParameter("lastDate", leadRequest.getLastDate(), TemporalType.DATE)
						.setParameter("superadminId", leadRequest.getSuperadminId())
						.getResultList();
			} else {
				results = leadDetailsDao.getEntityManager().createQuery(
						"SELECT LD FROM LeadDetails LD WHERE LD.createdBy =:createdBy AND LD.adminId=:adminId AND LD.superadminId =:superadminId ORDER BY LD.id DESC")
						.setParameter("createdBy", leadRequest.getCreatedBy())
						.setParameter("adminId", leadRequest.getAdminId())
						.setParameter("superadminId", leadRequest.getSuperadminId())
						.setFirstResult(Constant.FIRST_RESULT).setMaxResults(Constant.MAX_RESULT)
						.getResultList();
			}
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<LeadDetails> getAllHotLeadList(LeadRequestObject leadRequest) {
		List<LeadDetails> results = new ArrayList<LeadDetails>();
		if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.SUPERADMIN.name())) {
			results = leadDetailsDao.getEntityManager().createQuery(
					"SELECT LD FROM LeadDetails LD WHERE LD.superadminId =:superadminId AND LD.pickupDateTime =:pickupDateTime ORDER BY LD.id DESC")
					.setParameter("superadminId", leadRequest.getSuperadminId())
					.setParameter("pickupDateTime", new Date(), TemporalType.DATE).getResultList();
		}
		return results;
	}

}
