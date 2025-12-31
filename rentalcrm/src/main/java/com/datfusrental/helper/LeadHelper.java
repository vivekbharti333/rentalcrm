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

//	@SuppressWarnings("unchecked")
//	public List<LeadDetails> getAllLeadList(LeadRequestObject leadRequest) {
//		List<LeadDetails> results = new ArrayList<LeadDetails>();
//		if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.SUPERADMIN.name())) {
//			if (leadRequest.getRequestedFor().equalsIgnoreCase(RequestFor.BYDATE.name())) {
//				results = leadDetailsDao.getEntityManager().createQuery(
//						"SELECT LD FROM LeadDetails LD WHERE LD.createdAt BETWEEN :firstDate AND :lastDate AND LD.status NOT IN ('WON','INFO') ORDER BY LD.id DESC")
//						.setParameter("superadminId", leadRequest.getSuperadminId())
//						.setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.DATE)
//						.setParameter("lastDate", leadRequest.getLastDate(), TemporalType.DATE)
//						.setParameter("WON", "WON")
//						.setParameter("INFO", "INFO")
//						.getResultList();
//			} else {
//				results = leadDetailsDao.getEntityManager().createQuery(
//						"SELECT LD FROM LeadDetails LD WHERE LD.status NOT IN ('WON','INFO') ORDER BY LD.id DESC")
//						.setParameter("superadminId", leadRequest.getSuperadminId())
//						.setParameter("WON", "WON")
//						.setParameter("INFO", "INFO")
//						.setFirstResult(Constant.FIRST_RESULT)
//						.setMaxResults(Constant.MAX_RESULT)
//						.getResultList();
//			}
//		} else if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.ADMIN.name())) {
//			if (leadRequest.getRequestedFor().equalsIgnoreCase(RequestFor.BYDATE.name())) {
//				results = leadDetailsDao.getEntityManager().createQuery(
//						"SELECT LD FROM LeadDetails LD WHERE LD.superadminId =:superadminId AND LD.adminId=:adminId AND LD.createdAt BETWEEN :firstDate AND :lastDate AND LD.status NOT IN ('WON','INFO') ORDER BY LD.id DESC")
//						.setParameter("superadminId", leadRequest.getSuperadminId())
//						.setParameter("adminId", leadRequest.getAdminId())
//						.setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.DATE)
//						.setParameter("lastDate", leadRequest.getLastDate(), TemporalType.DATE)
//						.setParameter("WON", "WON")
//						.setParameter("INFO", "INFO")
//						.getResultList();
//			} else {
//				results = leadDetailsDao.getEntityManager().createQuery(
//						"SELECT LD FROM LeadDetails LD WHERE LD.superadminId =:superadminId AND LD.adminId=:adminId LD.status NOT IN ('WON','INFO') ORDER BY LD.id DESC")
//						.setParameter("superadminId", leadRequest.getSuperadminId())
//						.setParameter("adminId", leadRequest.getAdminId())
//						.setParameter("WON", "WON")
//						.setParameter("INFO", "INFO")
//						.setFirstResult(Constant.FIRST_RESULT)
//						.setMaxResults(Constant.MAX_RESULT)
//						.getResultList();
//			}
//		} else if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.TEAM_LEADER.name())) {
//			if (leadRequest.getRequestedFor().equalsIgnoreCase(RequestFor.BYDATE.name())) {
//				results = leadDetailsDao.getEntityManager().createQuery(
//						"SELECT LD FROM LeadDetails LD WHERE LD.superadminId =:superadminId AND LD.adminId=:adminId AND LD.teamleaderId =:teamleaderId AND LD.createdAt BETWEEN :firstDate AND :lastDate AND LD.status NOT IN ('WON','INFO') ORDER BY LD.id DESC")
//						.setParameter("superadminId", leadRequest.getSuperadminId())
//						.setParameter("adminId", leadRequest.getAdminId())
//						.setParameter("teamLeaderId", leadRequest.getTeamleaderId())
//						.setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.DATE)
//						.setParameter("lastDate", leadRequest.getLastDate(), TemporalType.DATE)
//						.setParameter("WON", "WON")
//						.setParameter("INFO", "INFO")
//						.getResultList();
//			} else {
//				results = leadDetailsDao.getEntityManager().createQuery(
//						"SELECT LD FROM LeadDetails LD WHERE LD.superadminId =:superadminId AND LD.adminId=:adminId AND LD.teamleaderId =:teamleaderId AND LD.status NOT IN ('WON','INFO') ORDER BY LD.id DESC")
//						.setParameter("superadminId", leadRequest.getSuperadminId())
//						.setParameter("adminId", leadRequest.getAdminId())
//						.setParameter("teamleaderId", leadRequest.getTeamleaderId())
//						.setParameter("WON", "WON")
//						.setParameter("INFO", "INFO")
//						.setFirstResult(Constant.FIRST_RESULT)
//						.setMaxResults(Constant.MAX_RESULT)
//						.getResultList();
//			}
//			// role type else
//		} else {
//			if (leadRequest.getRequestedFor().equalsIgnoreCase(RequestFor.BYDATE.name())) {
//				results = leadDetailsDao.getEntityManager().createQuery(
//						"SELECT LD FROM LeadDetails LD WHERE LD.createdBy =:createdBy AND LD.adminId=:adminId AND LD.superadminId =:superadminId AND LD.createdAt BETWEEN :firstDate AND :lastDate AND LD.status NOT IN ('WON','INFO') ORDER BY LD.id DESC")
//						.setParameter("createdBy", leadRequest.getCreatedBy())
//						.setParameter("adminId", leadRequest.getAdminId())
//						.setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.DATE)
//						.setParameter("lastDate", leadRequest.getLastDate(), TemporalType.DATE)
//						.setParameter("superadminId", leadRequest.getSuperadminId())
//						.setParameter("WON", "WON")
//						.setParameter("INFO", "INFO")
//						.getResultList();
//			} else {
//				results = leadDetailsDao.getEntityManager().createQuery(
//						"SELECT LD FROM LeadDetails LD WHERE LD.createdBy =:createdBy AND LD.adminId=:adminId AND LD.superadminId =:superadminId AND LD.status NOT IN ('WON','INFO') ORDER BY LD.id DESC")
//						.setParameter("createdBy", leadRequest.getCreatedBy())
//						.setParameter("adminId", leadRequest.getAdminId())
//						.setParameter("superadminId", leadRequest.getSuperadminId())
//						.setParameter("WON", "WON")
//						.setParameter("INFO", "INFO")
//						.setFirstResult(Constant.FIRST_RESULT)
//						.setMaxResults(Constant.MAX_RESULT)
//						.getResultList();
//			}
//		}
//		return results;
//	}
	
	public List<LeadDetails> getAllLeadList(LeadRequestObject leadRequest) {

	    EntityManager em = leadDetailsDao.getEntityManager();
	    List<LeadDetails> results = new ArrayList<>();

	    boolean byDate = RequestFor.BYDATE.name().equalsIgnoreCase(leadRequest.getRequestedFor());

	    if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.SUPERADMIN.name())) {

	        String q = byDate
	                ? "SELECT LD FROM LeadDetails LD WHERE LD.createdAt BETWEEN :firstDate AND :lastDate AND LD.status NOT IN ('WON','INFO') ORDER BY LD.id DESC"
	                : "SELECT LD FROM LeadDetails LD WHERE LD.status NOT IN ('WON','INFO') ORDER BY LD.id DESC";

	        TypedQuery<LeadDetails> query = em.createQuery(q, LeadDetails.class);

	        if (byDate) {
	            query.setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.DATE);
	            query.setParameter("lastDate", leadRequest.getLastDate(), TemporalType.DATE);
	        } else {
	            query.setFirstResult(Constant.FIRST_RESULT);
	            query.setMaxResults(Constant.MAX_RESULT);
	        }

	        results = query.getResultList();

	    } else if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.ADMIN.name())) {

	        String q = byDate
	                ? "SELECT LD FROM LeadDetails LD WHERE LD.superadminId=:superadminId AND LD.adminId=:adminId AND LD.createdAt BETWEEN :firstDate AND :lastDate AND LD.status NOT IN ('WON','INFO') ORDER BY LD.id DESC"
	                : "SELECT LD FROM LeadDetails LD WHERE LD.superadminId=:superadminId AND LD.adminId=:adminId AND LD.status NOT IN ('WON','INFO') ORDER BY LD.id DESC";

	        TypedQuery<LeadDetails> query = em.createQuery(q, LeadDetails.class)
	                .setParameter("superadminId", leadRequest.getSuperadminId())
	                .setParameter("adminId", leadRequest.getAdminId());

	        if (byDate) {
	            query.setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.DATE);
	            query.setParameter("lastDate", leadRequest.getLastDate(), TemporalType.DATE);
	        } else {
	            query.setFirstResult(Constant.FIRST_RESULT);
	            query.setMaxResults(Constant.MAX_RESULT);
	        }

	        results = query.getResultList();

	    } else if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.TEAM_LEADER.name())) {

	        String q = byDate
	                ? "SELECT LD FROM LeadDetails LD WHERE LD.superadminId=:superadminId AND LD.adminId=:adminId AND LD.teamleaderId=:teamleaderId AND LD.createdAt BETWEEN :firstDate AND :lastDate AND LD.status NOT IN ('WON','INFO') ORDER BY LD.id DESC"
	                : "SELECT LD FROM LeadDetails LD WHERE LD.superadminId=:superadminId AND LD.adminId=:adminId AND LD.teamleaderId=:teamleaderId AND LD.status NOT IN ('WON','INFO') ORDER BY LD.id DESC";

	        TypedQuery<LeadDetails> query = em.createQuery(q, LeadDetails.class)
	                .setParameter("superadminId", leadRequest.getSuperadminId())
	                .setParameter("adminId", leadRequest.getAdminId())
	                .setParameter("teamleaderId", leadRequest.getTeamleaderId());

	        if (byDate) {
	            query.setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.DATE);
	            query.setParameter("lastDate", leadRequest.getLastDate(), TemporalType.DATE);
	        } else {
	            query.setFirstResult(Constant.FIRST_RESULT);
	            query.setMaxResults(Constant.MAX_RESULT);
	        }

	        results = query.getResultList();

	    } else {

	        String q = byDate
	                ? "SELECT LD FROM LeadDetails LD WHERE LD.createdBy=:createdBy AND LD.adminId=:adminId AND LD.superadminId=:superadminId AND LD.createdAt BETWEEN :firstDate AND :lastDate AND LD.status NOT IN ('WON','INFO') ORDER BY LD.id DESC"
	                : "SELECT LD FROM LeadDetails LD WHERE LD.createdBy=:createdBy AND LD.adminId=:adminId AND LD.superadminId=:superadminId AND LD.status NOT IN ('WON','INFO') ORDER BY LD.id DESC";

	        TypedQuery<LeadDetails> query = em.createQuery(q, LeadDetails.class)
	                .setParameter("createdBy", leadRequest.getCreatedBy())
	                .setParameter("adminId", leadRequest.getAdminId())
	                .setParameter("superadminId", leadRequest.getSuperadminId());

	        if (byDate) {
	            query.setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.DATE);
	            query.setParameter("lastDate", leadRequest.getLastDate(), TemporalType.DATE);
	        } else {
	            query.setFirstResult(Constant.FIRST_RESULT);
	            query.setMaxResults(Constant.MAX_RESULT);
	        }

	        results = query.getResultList();
	    }

	    return results;
	}


//	@SuppressWarnings("unchecked")
//	public List<LeadDetails> getAllHotLeadList(LeadRequestObject leadRequest) {
//		List<LeadDetails> results = new ArrayList<LeadDetails>();
//		if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.SUPERADMIN.name())) {
//			results = leadDetailsDao.getEntityManager().createQuery(
//					"SELECT LD FROM LeadDetails LD WHERE LD.superadminId =:superadminId AND LD.pickupDateTime =:pickupDateTime ORDER BY LD.id DESC")
//					.setParameter("superadminId", leadRequest.getSuperadminId())
//					.setParameter("pickupDateTime", new Date(), TemporalType.DATE).getResultList();
//		}
//		return results;
//	}
	
	@SuppressWarnings("unchecked")
	public List<LeadDetails> getAllHotLeadList(LeadRequestObject leadRequest) {

	    List<LeadDetails> results = new ArrayList<>();

	    if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.SUPERADMIN.name())) {

	        Calendar cal = Calendar.getInstance();
	        cal.set(Calendar.HOUR_OF_DAY, 0);
	        cal.set(Calendar.MINUTE, 0);
	        cal.set(Calendar.SECOND, 0);
	        cal.set(Calendar.MILLISECOND, 0);
	        Date startDate = cal.getTime();

	        cal.add(Calendar.DAY_OF_MONTH, 1);
	        Date endDate = cal.getTime();

	        results = leadDetailsDao.getEntityManager()
	            .createQuery(
	                "SELECT LD FROM LeadDetails LD WHERE LD.superadminId=:superadminId AND LD.pickupDateTime >= :startDate AND LD.pickupDateTime < :endDate AND LD.createdAt >= :startDate AND LD.createdAt < :endDate ORDER BY LD.id DESC")
	            .setParameter("superadminId", leadRequest.getSuperadminId())
	            .setParameter("startDate", startDate, TemporalType.TIMESTAMP)
	            .setParameter("endDate", endDate, TemporalType.TIMESTAMP)
	            .getResultList();
	    }

	    return results;
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
