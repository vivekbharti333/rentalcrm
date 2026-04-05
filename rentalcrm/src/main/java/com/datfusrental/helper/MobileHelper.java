package com.datfusrental.helper;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.datfusrental.dao.CategoryTypeDao;
import com.datfusrental.dao.LeadDetailsDao;
import com.datfusrental.entities.CategoryType;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.enums.RequestFor;
import com.datfusrental.enums.Status;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.ItemRequestObject;
import com.datfusrental.object.request.LeadRequestObject;

@Component
public class MobileHelper {

	@Autowired
	private LeadDetailsDao leadDetailsDao;
	
	@Autowired
	private CategoryTypeDao categoryTypeDao;
	

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


	public List<LeadDetails> getMobileInstantList(LeadRequestObject leadRequest) {
		 List<String> includeStatuses = List.of("WON", "ASSIGNED");

	    return leadDetailsDao.getEntityManager()
	        .createQuery(
	            "SELECT LD FROM LeadDetails LD WHERE LD.superadminId = :superadminId AND LD.status IN (:statuses) " +
	            "AND LD.pickupDateTime BETWEEN :firstDate AND :lastDate " +
	            "ORDER BY LD.id DESC",
	            LeadDetails.class
	        )
	        .setParameter("superadminId", leadRequest.getSuperadminId())
	        .setParameter("statuses", includeStatuses)
	        .setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.TIMESTAMP)
	        .setParameter("lastDate", leadRequest.getLastDate(), TemporalType.TIMESTAMP)
	        .getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<CategoryType> getMobileActivityCategoryType(ItemRequestObject itemRequest) {
	    List<CategoryType> results = new ArrayList<>();

	    // Store in variable
	    List<String> categoryNames = Arrays.asList("Cruises", "Watersports", "Adventure", "Yacht", "Sightseeing");

	    results = categoryTypeDao.getEntityManager().createQuery(
	            "SELECT SC FROM CategoryType SC WHERE SC.superadminId = :superadminId AND SC.categoryTypeName IN (:categoryNames) ORDER BY SC.id DESC")
	            .setParameter("superadminId", itemRequest.getSuperadminId())
	            .setParameter("categoryNames", categoryNames)
	            .getResultList();  
	    return results;
	}

	
	public List<LeadDetails> getLeadListByCategoryTypeName(LeadRequestObject leadRequest) {
	    
		if("ALL".equalsIgnoreCase(leadRequest.getCategoryTypeName())) {
			return leadDetailsDao.getEntityManager()
			        .createQuery(
			            "SELECT LD FROM LeadDetails LD WHERE LD.superadminId = :superadminId " +
			            "AND LD.categoryTypeName IN (:categoryTypeName) " +
			            "AND LD.pickupDateTime BETWEEN :firstDate AND :lastDate " +
			            "ORDER BY LD.id DESC", LeadDetails.class)
			        .setParameter("superadminId", leadRequest.getSuperadminId())
			        .setParameter("categoryTypeName", leadRequest.getCategoryTypeName())
			        .setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.TIMESTAMP)
			        .setParameter("lastDate", leadRequest.getLastDate(), TemporalType.TIMESTAMP)
			        .getResultList();
			} else {
				return leadDetailsDao.getEntityManager()
				        .createQuery(
				            "SELECT LD FROM LeadDetails LD WHERE LD.superadminId = :superadminId " +
				            "AND LD.categoryTypeName IN (:categoryTypeName) " +
				            "AND LD.pickupDateTime BETWEEN :firstDate AND :lastDate " +
				            "ORDER BY LD.id DESC", LeadDetails.class)
				        .setParameter("superadminId", leadRequest.getSuperadminId())
				        .setParameter("categoryTypeName", leadRequest.getCategoryTypeName())
				        .setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.TIMESTAMP)
				        .setParameter("lastDate", leadRequest.getLastDate(), TemporalType.TIMESTAMP)
				        .getResultList();
				}
			}
		
	    
	
}