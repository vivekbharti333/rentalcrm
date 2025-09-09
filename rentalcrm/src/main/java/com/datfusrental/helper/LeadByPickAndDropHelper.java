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
public class LeadByPickAndDropHelper {

	@Autowired
	private LeadDetailsDao leadDetailsDao;

	@SuppressWarnings("unchecked")
	public List<LeadDetails> getPickupLeadList(LeadRequestObject leadRequest) {
		List<LeadDetails> results = new ArrayList<LeadDetails>();
		if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.SUPERADMIN.name())) {
				results = leadDetailsDao.getEntityManager().createQuery(
						"SELECT LD FROM LeadDetails LD WHERE LD.superadminId =:superadminId AND LD.dropDateTime BETWEEN :firstDate AND :lastDate ORDER BY LD.dropDateTime DESC")
						.setParameter("superadminId", leadRequest.getSuperadminId())
						.setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.DATE)
						.setParameter("lastDate", leadRequest.getLastDate(), TemporalType.DATE)
						.getResultList();
		} 
		else if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.ADMIN.name())) {
				results = leadDetailsDao.getEntityManager().createQuery(
					"SELECT LD FROM LeadDetails LD WHERE LD.superadminId =:superadminId AND LD.pickupDateTime BETWEEN :firstDate AND :lastDate ORDER BY LD.pickupDateTime DESC")
						.setParameter("superadminId", leadRequest.getSuperadminId())
						.setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.DATE)
						.setParameter("lastDate", leadRequest.getLastDate(), TemporalType.DATE)
						.getResultList();
				return results;
			} 
			
		return results;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<LeadDetails> getDropLeadList(LeadRequestObject leadRequest) {
		List<LeadDetails> results = new ArrayList<LeadDetails>();
		if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.SUPERADMIN.name())) {
				results = leadDetailsDao.getEntityManager().createQuery(
						"SELECT LD FROM LeadDetails LD WHERE LD.superadminId =:superadminId AND LD.dropDateTime BETWEEN :firstDate AND :lastDate ORDER BY LD.dropDateTime DESC")
						.setParameter("superadminId", leadRequest.getSuperadminId())
						.setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.DATE)
						.setParameter("lastDate", leadRequest.getLastDate(), TemporalType.DATE)
						.getResultList();
			
		} 
		else if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.ADMIN.name())) {
				results = leadDetailsDao.getEntityManager().createQuery(
					"SELECT LD FROM LeadDetails LD WHERE LD.superadminId =:superadminId AND LD.dropDateTime BETWEEN :firstDate AND :lastDate ORDER BY LD.pickupDateTime DESC")
						.setParameter("superadminId", leadRequest.getSuperadminId())
						.setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.DATE)
						.setParameter("lastDate", leadRequest.getLastDate(), TemporalType.DATE)
						.getResultList();
				return results;
			} 
			
		return results;
	}
}
