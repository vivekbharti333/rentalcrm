package com.datfusrental.helper;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.datfusrental.constant.Constant;
import com.datfusrental.dao.LeadDetailsDao;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.entities.LeadDetailsHistory;
import com.datfusrental.enums.RequestFor;
import com.datfusrental.enums.RoleType;
import com.datfusrental.object.request.LeadRequestObject;

@Component
public class LeadByStatusHelper {

	@Autowired
	private LeadDetailsDao leadDetailsDao;

	
	@SuppressWarnings("unchecked")
	public List<LeadDetails> getLeadListByStatus(LeadRequestObject leadRequest) {
		List<LeadDetails> results = new ArrayList<LeadDetails>();
		if (leadRequest.getRequestedFor().equalsIgnoreCase(RequestFor.BYDATE.name())) {
			results = leadDetailsDao.getEntityManager().createQuery(
					"SELECT LD FROM LeadDetails LD WHERE LD.status =:status AND LD.superadminId =:superadminId AND LD.createdAt BETWEEN :firstDate AND :lastDate ORDER BY LD.id DESC")
					.setParameter("superadminId", leadRequest.getSuperadminId())
					.setParameter("status", leadRequest.getStatus())
					.setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.DATE)
					.setParameter("lastDate", leadRequest.getLastDate(), TemporalType.DATE)
					.getResultList();
			return results;
		} else {
			results = leadDetailsDao.getEntityManager().createQuery(
					"SELECT LD FROM LeadDetails LD WHERE LD.status =:status AND LD.superadminId =:superadminId ORDER BY LD.id DESC")
					.setParameter("superadminId", leadRequest.getSuperadminId())
					.setParameter("status", leadRequest.getStatus())
					.setFirstResult(Constant.FIRST_RESULT)
					.setMaxResults(Constant.MAX_RESULT)
					.getResultList();
		}
		return results;
	}
	
	@SuppressWarnings("unchecked")
	public List<LeadDetails> getEnquiryList(LeadRequestObject leadRequest) {
		List<LeadDetails> results = new ArrayList<LeadDetails>();
		results = leadDetailsDao.getEntityManager().createQuery(
				"SELECT LD FROM LeadDetails LD WHERE LD.status =:status AND LD.superadminId =:superadminId AND LD.createdAt >= :firstDate AND LD.createdAt <= :lastDate ORDER BY LD.id DESC")
				.setParameter("superadminId", leadRequest.getSuperadminId())
				.setParameter("status", "ENQUIRY")
				.setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.DATE)
				.setParameter("lastDate", leadRequest.getLastDate(), TemporalType.DATE)
				.setFirstResult(Constant.FIRST_RESULT)
				.setMaxResults(Constant.MAX_RESULT)
				.getResultList();

		return results;
	}



	

}
