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
import com.datfusrental.dao.LeadDetailsHistoryDao;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.entities.LeadDetailsHistory;
import com.datfusrental.entities.User;
import com.datfusrental.enums.RequestFor;
import com.datfusrental.enums.RoleType;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.LeadRequestObject;

@Component
public class MobileHelper {

	@Autowired
	private LeadDetailsDao leadDetailsDao;
	
	@Autowired
	private LeadDetailsHistoryDao leadDetailsHistoryDao;
	
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


	
}