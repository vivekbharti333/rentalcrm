package com.datfusrental.helper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.datfusrental.dao.LeadDetailsDao;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.object.request.LeadRequestObject;

@Component
public class WebsiteLeadHelper {

	@Autowired
	private LeadDetailsDao leadDetailsDao;

	public List<LeadDetails> getWebsiteLeadList(LeadRequestObject leadRequest) {

	    return leadDetailsDao.getEntityManager()
	        .createQuery(
	            "SELECT LD FROM LeadDetails LD WHERE LD.superadminId = :superadminId AND LD.status =:status AND LD.leadOrigine =:leadOrigine  ORDER BY LD.id DESC", LeadDetails.class)
	        .setParameter("superadminId", leadRequest.getSuperadminId())
	        .setParameter("status", "WON")
	        .setParameter("leadOrigine", "WEBSITE")

	        .getResultList();
	}


}
