package com.datfusrental.helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.datfusrental.constant.Constant;
import com.datfusrental.dao.LeadDetailsDao;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.enums.RequestFor;
import com.datfusrental.enums.RoleType;
import com.datfusrental.object.request.LeadRequestObject;

@Component
public class LeadByStatusHelper {

	@Autowired
	private LeadDetailsDao leadDetailsDao;

//	@SuppressWarnings("unchecked")
//	public List<LeadDetails> getLeadListByStatus(LeadRequestObject leadRequest) {
//		List<LeadDetails> results = new ArrayList<LeadDetails>();
//		if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.SUPERADMIN.name())) {
//			if (leadRequest.getRequestedFor().equalsIgnoreCase(RequestFor.BYDATE.name())) {
//				results = leadDetailsDao.getEntityManager().createQuery(
//					"SELECT LD FROM LeadDetails LD WHERE LD.status =:status AND LD.superadminId =:superadminId AND LD.createdAt BETWEEN :firstDate AND :lastDate ORDER BY LD.id DESC")
//						.setParameter("superadminId", leadRequest.getSuperadminId())
//						.setParameter("status", leadRequest.getStatus())
//						.setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.DATE)
//						.setParameter("lastDate", leadRequest.getLastDate(), TemporalType.DATE)
//						.getResultList();
//				return results;
//			} else {
//				results = leadDetailsDao.getEntityManager().createQuery(
//					"SELECT LD FROM LeadDetails LD WHERE LD.status =:status AND LD.superadminId =:superadminId ORDER BY LD.id DESC")
//						.setParameter("superadminId", leadRequest.getSuperadminId())
//						.setParameter("status", leadRequest.getStatus())
//						.setFirstResult(Constant.FIRST_RESULT)
//						.setMaxResults(Constant.MAX_RESULT)
//						.getResultList();
//			}
//		} else if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.ADMIN.name())) {
//			if (leadRequest.getRequestedFor().equalsIgnoreCase(RequestFor.BYDATE.name())) {
//				results = leadDetailsDao.getEntityManager().createQuery(
//						"SELECT LD FROM LeadDetails LD WHERE LD.status =:status AND LD.superadminId =:superadminId AND LD.adminId=:adminId AND LD.createdAt BETWEEN :firstDate AND :lastDate ORDER BY LD.id DESC")
//						.setParameter("status", leadRequest.getStatus())
//						.setParameter("superadminId", leadRequest.getSuperadminId())
//						.setParameter("adminId", leadRequest.getAdminId())
//						.setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.DATE)
//						.setParameter("lastDate", leadRequest.getLastDate(), TemporalType.DATE)
//						.getResultList();
//			} else {
//				results = leadDetailsDao.getEntityManager().createQuery(
//						"SELECT LD FROM LeadDetails LD WHERE LD.status =:status AND LD.superadminId =:superadminId AND LD.adminId=:adminId ORDER BY LD.id DESC")
//						.setParameter("status", leadRequest.getStatus())
//						.setParameter("superadminId", leadRequest.getSuperadminId())
//						.setParameter("adminId", leadRequest.getAdminId())
//						.setFirstResult(Constant.FIRST_RESULT)
//						.setMaxResults(Constant.MAX_RESULT)
//						.getResultList();
//			}
//		} else if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.TEAM_LEADER.name())) {
//			if (leadRequest.getRequestedFor().equalsIgnoreCase(RequestFor.BYDATE.name())) {
//				results = leadDetailsDao.getEntityManager().createQuery(
//						"SELECT LD FROM LeadDetails LD WHERE LD.status =:status AND LD.superadminId =:superadminId AND LD.adminId=:adminId AND LD.teamLeaderId =:teamLeaderId AND LD.createdAt BETWEEN :firstDate AND :lastDate ORDER BY LD.id DESC")
//						.setParameter("status", leadRequest.getStatus())
//						.setParameter("superadminId", leadRequest.getSuperadminId())
//						.setParameter("adminId", leadRequest.getAdminId())
//						.setParameter("teamLeaderId", leadRequest.getTeamleaderId())
//						.setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.DATE)
//						.setParameter("lastDate", leadRequest.getLastDate(), TemporalType.DATE)
//						.getResultList();
//			} else {
//				results = leadDetailsDao.getEntityManager().createQuery(
//						"SELECT LD FROM LeadDetails LD WHERE LD.status =:status AND LD.superadminId =:superadminId AND LD.adminId=:adminId AND LD.teamLeaderId =:teamLeaderId ORDER BY LD.id DESC")
//						.setParameter("status", leadRequest.getStatus())
//						.setParameter("superadminId", leadRequest.getSuperadminId())
//						.setParameter("adminId", leadRequest.getAdminId())
//						.setParameter("teamLeaderId", leadRequest.getTeamleaderId())
//						.setFirstResult(Constant.FIRST_RESULT)
//						.setMaxResults(Constant.MAX_RESULT)
//						.getResultList();
//			}
//		} else if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.CUSTOMER_EXECUTIVE.name())) {
//			if (leadRequest.getRequestedFor().equalsIgnoreCase(RequestFor.BYDATE.name())) {
//				results = leadDetailsDao.getEntityManager().createQuery(
//						"SELECT LD FROM LeadDetails LD WHERE LD.status =:status AND LD.createdBy =:createdBy AND LD.adminId=:adminId AND LD.superadminId =:superadminId AND LD.createdAt BETWEEN :firstDate AND :lastDate ORDER BY LD.id DESC")
//						.setParameter("status", leadRequest.getStatus())
//						.setParameter("createdBy", leadRequest.getCreatedBy())
//						.setParameter("adminId", leadRequest.getAdminId())
//						.setParameter("firstDate", leadRequest.getFirstDate(), TemporalType.DATE)
//						.setParameter("lastDate", leadRequest.getLastDate(), TemporalType.DATE)
//						.setParameter("superadminId", leadRequest.getSuperadminId())
//						.getResultList();
//			} else {
//				results = leadDetailsDao.getEntityManager().createQuery(
//						"SELECT LD FROM LeadDetails LD WHERE LD.status =:status AND LD.createdBy =:createdBy AND LD.adminId=:adminId AND LD.superadminId =:superadminId ORDER BY LD.id DESC")
//						.setParameter("status", leadRequest.getStatus())
//						.setParameter("createdBy", leadRequest.getCreatedBy())
//						.setParameter("adminId", leadRequest.getAdminId())
//						.setParameter("superadminId", leadRequest.getSuperadminId())
//						.setFirstResult(Constant.FIRST_RESULT)
//						.setMaxResults(Constant.MAX_RESULT)
//						.getResultList();
//			}
//		}
//		return results;
//	}
	
	public List<LeadDetails> getLeadListByStatus(LeadRequestObject leadRequest) {

	    List<LeadDetails> results = new ArrayList<>();
	    EntityManager em = leadDetailsDao.getEntityManager();

	    boolean byDate = RequestFor.BYDATE.name().equalsIgnoreCase(leadRequest.getRequestedFor());

	    Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    Date startDate = cal.getTime();
	    cal.add(Calendar.DAY_OF_MONTH, 1);
	    Date endDate = cal.getTime();

	    if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.SUPERADMIN.name())) {

	        String q = byDate
	            ? "SELECT LD FROM LeadDetails LD WHERE LD.status=:status AND LD.superadminId=:superadminId AND LD.createdAt>=:startDate AND LD.createdAt<:endDate ORDER BY LD.id DESC"
	            : "SELECT LD FROM LeadDetails LD WHERE LD.status=:status AND LD.superadminId=:superadminId ORDER BY LD.id DESC";

	        TypedQuery<LeadDetails> query = em.createQuery(q, LeadDetails.class)
	            .setParameter("status", leadRequest.getStatus())
	            .setParameter("superadminId", leadRequest.getSuperadminId());

	        if (byDate) {
	            query.setParameter("startDate", startDate, TemporalType.TIMESTAMP);
	            query.setParameter("endDate", endDate, TemporalType.TIMESTAMP);
	        } else {
	            query.setFirstResult(Constant.FIRST_RESULT);
	            query.setMaxResults(Constant.MAX_RESULT);
	        }

	        results = query.getResultList();

	    } else if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.ADMIN.name())) {

	        String q = byDate
	            ? "SELECT LD FROM LeadDetails LD WHERE LD.status=:status AND LD.superadminId=:superadminId AND LD.adminId=:adminId AND LD.createdAt>=:startDate AND LD.createdAt<:endDate ORDER BY LD.id DESC"
	            : "SELECT LD FROM LeadDetails LD WHERE LD.status=:status AND LD.superadminId=:superadminId AND LD.adminId=:adminId ORDER BY LD.id DESC";

	        TypedQuery<LeadDetails> query = em.createQuery(q, LeadDetails.class)
	            .setParameter("status", leadRequest.getStatus())
	            .setParameter("superadminId", leadRequest.getSuperadminId())
	            .setParameter("adminId", leadRequest.getAdminId());

	        if (byDate) {
	            query.setParameter("startDate", startDate, TemporalType.TIMESTAMP);
	            query.setParameter("endDate", endDate, TemporalType.TIMESTAMP);
	        } else {
	            query.setFirstResult(Constant.FIRST_RESULT);
	            query.setMaxResults(Constant.MAX_RESULT);
	        }

	        results = query.getResultList();

	    } else if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.TEAM_LEADER.name())) {

	        String q = byDate
	            ? "SELECT LD FROM LeadDetails LD WHERE LD.status=:status AND LD.superadminId=:superadminId AND LD.adminId=:adminId AND LD.teamLeaderId=:teamLeaderId AND LD.createdAt>=:startDate AND LD.createdAt<:endDate ORDER BY LD.id DESC"
	            : "SELECT LD FROM LeadDetails LD WHERE LD.status=:status AND LD.superadminId=:superadminId AND LD.adminId=:adminId AND LD.teamLeaderId=:teamLeaderId ORDER BY LD.id DESC";

	        TypedQuery<LeadDetails> query = em.createQuery(q, LeadDetails.class)
	            .setParameter("status", leadRequest.getStatus())
	            .setParameter("superadminId", leadRequest.getSuperadminId())
	            .setParameter("adminId", leadRequest.getAdminId())
	            .setParameter("teamLeaderId", leadRequest.getTeamleaderId());

	        if (byDate) {
	            query.setParameter("startDate", startDate, TemporalType.TIMESTAMP);
	            query.setParameter("endDate", endDate, TemporalType.TIMESTAMP);
	        } else {
	            query.setFirstResult(Constant.FIRST_RESULT);
	            query.setMaxResults(Constant.MAX_RESULT);
	        }

	        results = query.getResultList();

	    } else if (leadRequest.getRoleType().equalsIgnoreCase(RoleType.CUSTOMER_EXECUTIVE.name())) {

	        String q = byDate
	            ? "SELECT LD FROM LeadDetails LD WHERE LD.status=:status AND LD.createdBy=:createdBy AND LD.adminId=:adminId AND LD.superadminId=:superadminId AND LD.createdAt>=:startDate AND LD.createdAt<:endDate ORDER BY LD.id DESC"
	            : "SELECT LD FROM LeadDetails LD WHERE LD.status=:status AND LD.createdBy=:createdBy AND LD.adminId=:adminId AND LD.superadminId=:superadminId ORDER BY LD.id DESC";

	        TypedQuery<LeadDetails> query = em.createQuery(q, LeadDetails.class)
	            .setParameter("status", leadRequest.getStatus())
	            .setParameter("createdBy", leadRequest.getCreatedBy())
	            .setParameter("adminId", leadRequest.getAdminId())
	            .setParameter("superadminId", leadRequest.getSuperadminId());

	        if (byDate) {
	            query.setParameter("startDate", startDate, TemporalType.TIMESTAMP);
	            query.setParameter("endDate", endDate, TemporalType.TIMESTAMP);
	        } else {
	            query.setFirstResult(Constant.FIRST_RESULT);
	            query.setMaxResults(Constant.MAX_RESULT);
	        }

	        results = query.getResultList();
	    }

	    return results;
	}



}
