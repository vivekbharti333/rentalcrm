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
import com.datfusrental.dao.EnquirySourceDetailsDao;
import com.datfusrental.entities.EnquirySourceDetails;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.entities.LeadDetailsHistory;
import com.datfusrental.entities.User;
import com.datfusrental.enums.Status;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.LeadRequestObject;

@Component
public class EnquirySourceHelper {
	
	@Autowired
	private EnquirySourceDetailsDao enquirySourceDetailsDao;
	
	public void validateLeadRequest(LeadRequestObject leadRequestObject) throws BizException {
		if (leadRequestObject == null) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Bad Request Object Null");
		}
	}
	
	@Transactional
	public EnquirySourceDetails getEnquirySourceDetailsById(Long id) {

		CriteriaBuilder criteriaBuilder = enquirySourceDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<EnquirySourceDetails> criteriaQuery = criteriaBuilder.createQuery(EnquirySourceDetails.class);
		Root<EnquirySourceDetails> root = criteriaQuery.from(EnquirySourceDetails.class);
		Predicate restriction = criteriaBuilder.equal(root.get("id"), id);
		criteriaQuery.where(restriction);
		EnquirySourceDetails enquirySourceDetails = enquirySourceDetailsDao.getSession().createQuery(criteriaQuery).uniqueResult();
		return enquirySourceDetails;
	}
	
	@Transactional
	public EnquirySourceDetails getEnquirySourceDetailsByEnquirySource(String enquirySource) {

		CriteriaBuilder criteriaBuilder = enquirySourceDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<EnquirySourceDetails> criteriaQuery = criteriaBuilder.createQuery(EnquirySourceDetails.class);
		Root<EnquirySourceDetails> root = criteriaQuery.from(EnquirySourceDetails.class);
		Predicate restriction = criteriaBuilder.equal(root.get("enquirySource"), enquirySource);
		criteriaQuery.where(restriction);
		EnquirySourceDetails enquirySourceDetails = enquirySourceDetailsDao.getSession().createQuery(criteriaQuery).uniqueResult();
		return enquirySourceDetails;
	}
	
	
	public EnquirySourceDetails getEnquirySourceDetailsByReqObj(LeadRequestObject leadRequest) {
		
		EnquirySourceDetails enquirySourceDetails = new EnquirySourceDetails();
		
		enquirySourceDetails.setEnquirySource(leadRequest.getEnquirySource());
		enquirySourceDetails.setStatus("ACTIVE");
		enquirySourceDetails.setSuperadminId(leadRequest.getSuperadminId());
		return enquirySourceDetails;
	}

	@Transactional
	public EnquirySourceDetails saveEnquirySourceDetails(EnquirySourceDetails enquirySourceDetails) {
		enquirySourceDetailsDao.persist(enquirySourceDetails);
		return enquirySourceDetails;
	}
	
public EnquirySourceDetails getUpdatedEnquirySourceDetailsByReqObj(LeadRequestObject leadRequest, EnquirySourceDetails enquirySourceDetails) {
		enquirySourceDetails.setEnquirySource(leadRequest.getEnquirySource());
		return enquirySourceDetails;
	}

	@Transactional
	public EnquirySourceDetails updateEnquirySourceDetails(EnquirySourceDetails enquirySourceDetails) {
		enquirySourceDetailsDao.update(enquirySourceDetails);
		return enquirySourceDetails;
	}

	public List<EnquirySourceDetails> getEnquirySource(LeadRequestObject leadRequest) {
		List<EnquirySourceDetails> results = new ArrayList<>();
		if (leadRequest.getRequestedFor().equalsIgnoreCase("ALL")) {
			results = enquirySourceDetailsDao.getEntityManager().createQuery(
					"SELECT ES FROM EnquirySourceDetails ES ORDER BY ES.id DESC", EnquirySourceDetails.class)
					.getResultList();
		} else if (leadRequest.getRequestedFor().equalsIgnoreCase("ACTIVE")) {
			results = enquirySourceDetailsDao.getEntityManager().createQuery(
					"SELECT ES FROM EnquirySourceDetails ES WHERE status =:status ORDER BY ES.id DESC", EnquirySourceDetails.class)
		            .setParameter("status", Status.ACTIVE.name())
					.getResultList();
		}
		return results;
	}
	

}
