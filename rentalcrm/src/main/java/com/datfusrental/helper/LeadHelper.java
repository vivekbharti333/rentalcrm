package com.datfusrental.helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
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

		leadDetails.setPickupDateTime(leadRequest.getPickupDateTime());
//		leadDetails.setPickupDate(leadRequest.getPickupDate());
//		leadDetails.setPickupTime(leadRequest.getPickupTime());
		leadDetails.setPickupHub(leadRequest.getPickupHub());
		leadDetails.setPickupPoint(leadRequest.getPickupPoint());

		leadDetails.setDropDateTime(leadRequest.getDropDateTime());
		leadDetails.setTotalDays(leadRequest.getTotalDays());
//		leadDetails.setDropDate(leadRequest.getDropDate());
//		leadDetails.setDropTime(leadRequest.getDropTime());
		leadDetails.setDropHub(leadRequest.getDropHub());
		leadDetails.setDropPoint(leadRequest.getDropPoint());

		leadDetails.setCustomeName(leadRequest.getCustomeName());
		leadDetails.setCountryDialCode(leadRequest.getCountryDialCode());
		leadDetails.setCustomerMobile(leadRequest.getCustomerMobile());
		leadDetails.setCustomerEmailId(leadRequest.getCustomerEmailId());

		leadDetails.setQuantity(leadRequest.getQuantity());
		leadDetails.setKidQuantity(leadRequest.getKidQuantity());
		leadDetails.setInfantQuantity(leadRequest.getInfantQuantity());

		leadDetails.setVendorRate(leadRequest.getVendorRate());
		leadDetails.setVendorRateForKids(leadRequest.getVendorRateForKids());

		leadDetails.setCompanyRate(leadRequest.getCompanyRate());
		
		leadDetails.setPayToCompany(leadRequest.getPayToCompany());
		leadDetails.setPayToVendor(leadRequest.getPayToVendor());

		leadDetails.setBookingAmount(leadRequest.getBookingAmount());
		leadDetails.setBalanceAmount(leadRequest.getBalanceAmount());
		leadDetails.setTotalAmount(leadRequest.getTotalAmount());
		leadDetails.setActualAmount(leadRequest.getActualAmount());
		leadDetails.setSecurityAmount(leadRequest.getSecurityAmount());
		
		leadDetails.setDeliveryAmountToCompany(leadRequest.getDeliveryAmountToCompany());
		leadDetails.setDeliveryAmountToVendor(leadRequest.getDeliveryAmountToVendor());
		
		leadDetails.setActualAmount(leadRequest.getActualAmount());
		
		leadDetails.setDiscountType(leadRequest.getDiscountType());
		leadDetails.setDiscount(leadRequest.getDiscount());

		leadDetails.setVendorName(leadRequest.getVendorName());
		leadDetails.setRemarks(leadRequest.getRemarks());
		leadDetails.setStatus(leadRequest.getStatus());

		leadDetails.setLeadOrigine(leadRequest.getLeadOrigine());
		leadDetails.setLeadType(leadRequest.getLeadType());
		
		leadDetails.setNextFollowupDate(leadRequest.getNextFollowupDate());
		leadDetails.setNotes(leadRequest.getNotes());

		leadDetails.setCreatedAt(new Date());
		leadDetails.setUpdatedAt(new Date());

		if(leadRequest.getLeadOrigine().equalsIgnoreCase("WEBSITE")) {
			
			leadDetails.setCreatedBy("WEBSITE");
			leadDetails.setCreatedByName("WEBSITE");
			leadDetails.setTeamleaderId("WEBSITE");
			leadDetails.setAdminId("WEBSITE");
			leadDetails.setSuperadminId("WEBSITE");
		} else {
			leadDetails.setCreatedBy(leadRequest.getCreatedBy());
			leadDetails.setCreatedByName(user.getFirstName()+ " "+user.getLastName());
			leadDetails.setTeamleaderId(user.getTeamleaderId());
			leadDetails.setAdminId(user.getAdminId());
			leadDetails.setSuperadminId(leadRequest.getSuperadminId());
		}
		return leadDetails;
	}

	@Transactional
	public LeadDetails saveLeadDetails(LeadDetails leadDetails) {
		leadDetailsDao.persist(leadDetails);
		return leadDetails;
	}

	public LeadDetails getUpdatedLeadDetailsByReqObj(LeadRequestObject leadRequest, LeadDetails leadDetails) {


//		leadDetails.setBookingId(leadRequest.getBookingId());
//		leadDetails.setCompanyName(leadRequest.getCompanyName());
//		leadDetails.setCategoryTypeName(leadRequest.getCategoryTypeName());
		leadDetails.setSuperCategory(leadRequest.getSuperCategory());
		leadDetails.setCategory(leadRequest.getCategory());
//		leadDetails.setSubCategory(leadRequest.getSubCategory());
//		leadDetails.setItemName(leadRequest.getItemName());

		leadDetails.setPickupDateTime(leadRequest.getPickupDateTime());  
//		leadDetails.setPickupDate(leadRequest.getPickupDate());
//		leadDetails.setPickupTime(leadRequest.getPickupTime());
		leadDetails.setPickupHub(leadRequest.getPickupHub());
		leadDetails.setPickupPoint(leadRequest.getPickupPoint());

		leadDetails.setDropDateTime(leadRequest.getDropDateTime());
//		leadDetails.setDropDate(leadRequest.getDropDate());
//		leadDetails.setDropTime(leadRequest.getDropTime());
		leadDetails.setDropHub(leadRequest.getDropHub());
		leadDetails.setDropPoint(leadRequest.getDropPoint());

		leadDetails.setCustomeName(leadRequest.getCustomeName());
		leadDetails.setCountryDialCode(leadRequest.getCountryDialCode());
		leadDetails.setCustomerMobile(leadRequest.getCustomerMobile());
		leadDetails.setCustomerEmailId(leadRequest.getCustomerEmailId());

		leadDetails.setQuantity(leadRequest.getQuantity());
		leadDetails.setKidQuantity(leadRequest.getKidQuantity());
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

		leadDetails.setUpdatedBy(leadRequest.getUpdatedBy());
		leadDetails.setSuperadminId(leadRequest.getSuperadminId());

		return leadDetails;
	}

	@Transactional
	public LeadDetails updateLeadDetails(LeadDetails leadDetails) {
		leadDetailsDao.update(leadDetails);
		return leadDetails;
	}

	private Date plusOneDay(Date date) {
	    if (date == null) return null;
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.add(Calendar.DAY_OF_MONTH, 1);
	    return cal.getTime();
	}


	public List<LeadDetails> getAllLeadList(LeadRequestObject leadRequest) {

	    List<String> excludedStatus = List.of("WON", "INFO");

	    if (RequestFor.BYDATE.name().equalsIgnoreCase(leadRequest.getRequestedFor())) {

	        return leadDetailsDao.getEntityManager()
	            .createQuery(
	                "SELECT LD FROM LeadDetails LD WHERE LD.superadminId = :superadminId AND LD.createdAt >= :firstDate AND LD.createdAt < :lastDate AND LD.status NOT IN :statusList ORDER BY LD.id DESC",
	                LeadDetails.class
	            )
	            .setParameter("superadminId", leadRequest.getSuperadminId())
	            .setParameter("firstDate", this.plusOneDay(leadRequest.getFirstDate()))
	            .setParameter("lastDate", this.plusOneDay(leadRequest.getLastDate())) 
	            .setParameter("statusList", excludedStatus)
	            .setFirstResult(Constant.FIRST_RESULT)
	            .setMaxResults(Constant.MAX_RESULT)
	            .getResultList();

	    } else {

	        return leadDetailsDao.getEntityManager()
	            .createQuery(
	                "SELECT LD FROM LeadDetails LD WHERE LD.superadminId = :superadminId AND LD.status NOT IN :statusList ORDER BY LD.id DESC",
	                LeadDetails.class
	            )
	            .setParameter("superadminId", leadRequest.getSuperadminId())
	            .setParameter("statusList", excludedStatus)
	            .setFirstResult(Constant.FIRST_RESULT)
	            .setMaxResults(Constant.MAX_RESULT)
	            .getResultList();
	    }
	}

	
	@SuppressWarnings("unchecked")
	public List<LeadDetails> getAllHotLeadList(LeadRequestObject leadRequest) {

		List<LeadDetails> results = new ArrayList<>();
//
//		Calendar cal = Calendar.getInstance();
//		cal.set(Calendar.HOUR_OF_DAY, 0);
//		cal.set(Calendar.MINUTE, 0);
//		cal.set(Calendar.SECOND, 0);
//		cal.set(Calendar.MILLISECOND, 0);
//		Date startDate = cal.getTime();
//
//		cal.add(Calendar.DAY_OF_MONTH, 1);
//		Date endDate = cal.getTime();

		results = leadDetailsDao.getEntityManager().createQuery(
				"SELECT LD FROM LeadDetails LD WHERE LD.superadminId=:superadminId AND LD.pickupDateTime >= :startDate AND LD.pickupDateTime < :endDate AND LD.createdAt >= :startDate AND LD.createdAt < :endDate ORDER BY LD.id DESC")
				.setParameter("superadminId", leadRequest.getSuperadminId())
				.setParameter("firstDate", this.plusOneDay(leadRequest.getFirstDate()), TemporalType.TIMESTAMP)
	            .setParameter("lastDate", this.plusOneDay(leadRequest.getLastDate()), TemporalType.TIMESTAMP) 
//				.setParameter("startDate", startDate, TemporalType.TIMESTAMP)
//				.setParameter("endDate", endDate, TemporalType.TIMESTAMP)
				.getResultList();

		return results;
	}


	public List<LeadDetails> getFollowupLeadList(LeadRequestObject leadRequest) {

	    return leadDetailsDao.getEntityManager()
	        .createQuery(
	            "SELECT LD FROM LeadDetails LD WHERE LD.superadminId = :superadminId AND LD.createdAt >= :firstDate AND LD.createdAt < :lastDate ORDER BY LD.id DESC", LeadDetails.class)
	        .setParameter("superadminId", leadRequest.getSuperadminId())
	        .setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.TIMESTAMP)
	        .setParameter("lastDate", leadRequest.getLastDate(), TemporalType.TIMESTAMP)
	        .getResultList();
	}


	
//	@SuppressWarnings("unchecked")
//	public List<LeadDetails> getAllHotLeadList(LeadRequestObject leadRequest) {
//
//	    List<LeadDetails> results = new ArrayList<>();
//
//	    if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.SUPERADMIN.name())) {
//
//	        Date startDate = org.apache.commons.lang3.time.DateUtils.truncate(new Date(), java.util.Calendar.DAY_OF_MONTH);
//	        Date endDate = org.apache.commons.lang3.time.DateUtils.addDays(startDate, 1);
//
//	        results = leadDetailsDao.getEntityManager()
//	                .createQuery("SELECT LD FROM LeadDetails LD WHERE LD.superadminId=:superadminId AND LD.pickupDateTime >= :startDate AND LD.pickupDateTime < :endDate ORDER BY LD.id DESC")
//	                .setParameter("superadminId", leadRequest.getSuperadminId())
//	                .setParameter("startDate", startDate, TemporalType.TIMESTAMP)
//	                .setParameter("endDate", endDate, TemporalType.TIMESTAMP)
//	                .getResultList();
//	    }
//
//	    return results;
//	}

	

}
