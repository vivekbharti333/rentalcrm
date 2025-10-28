package com.datfusrental.helper;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.TemporalType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.datfusrental.constant.Constant;
import com.datfusrental.dao.LeadDetailsDao;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.enums.RequestFor;
import com.datfusrental.enums.RoleType;
import com.datfusrental.object.request.LeadRequestObject;

@Component
public class AssignedLeadHelper {

	@Autowired
	private LeadDetailsDao leadDetailsDao;

	@SuppressWarnings("unchecked")
	public List<LeadDetails> getLeadByStatus(LeadRequestObject leadRequest) {
	    List<LeadDetails> results = new ArrayList<>();

	    if (RoleType.SUPERADMIN.name().equalsIgnoreCase(leadRequest.getRoleType())) {
	        results = leadDetailsDao.getEntityManager().createQuery(
	                "SELECT LD FROM LeadDetails LD WHERE LD.status = :status AND LD.createdAt BETWEEN :firstDate AND :lastDate ORDER BY LD.pickupDateTime DESC")
	        		.setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.TIMESTAMP)
	        		.setParameter("lastDate", leadRequest.getLastDate(), TemporalType.TIMESTAMP)
	            .setParameter("status", leadRequest.getStatus())
	            .getResultList();

	    } else if (RoleType.SALES_EXECUTIVE.name().equalsIgnoreCase(leadRequest.getRoleType())) {
	        results = leadDetailsDao.getEntityManager().createQuery(
	                "SELECT LD FROM LeadDetails LD WHERE LD.status = :status AND LD.createdBy = :createdBy AND LD.createdAt BETWEEN :firstDate AND :lastDate ORDER BY LD.pickupDateTime DESC")
	            .setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.TIMESTAMP)
	            .setParameter("lastDate", leadRequest.getLastDate(), TemporalType.TIMESTAMP)
	            .setParameter("status", leadRequest.getStatus())
	            .setParameter("createdBy", leadRequest.getLoginId())
	            .getResultList();
	    }

	    return results;
	}
}
