package com.datfusrental.helper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.datfusrental.constant.Constant;
import com.datfusrental.dao.UserDetailsDao;
import com.datfusrental.entities.AddressDetails;
import com.datfusrental.entities.UserDetails;
import com.datfusrental.enums.RequestFor;
import com.datfusrental.enums.RoleType;
import com.datfusrental.enums.Status;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.LoginRequestObject;
import com.datfusrental.object.request.UserRequestObject;

@Component
public class UserHelper {

	@Autowired
	private UserDetailsDao userDetailsDao;
	
	public void validateLoginRequest(LoginRequestObject loginRequest) throws BizException {
		if (loginRequest == null) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Bad Request Object Null");
		}
	}
	
	public void validateUserRequest(UserRequestObject userRequest) throws BizException {
		if (userRequest == null) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Bad Request Object Null");
		}
	}

	public static String generateRandomChars(String candidateChars, int length) {
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(candidateChars.charAt(random.nextInt(candidateChars.length())));
		}
		return sb.toString();
	}
	
	public boolean checkValidityOfUser(Date validityDate) {

		LocalDate nowDate = LocalDate.now();

		Date utilDate = validityDate;

		Instant instant = utilDate.toInstant();
		LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();

		long daysDifference = ChronoUnit.DAYS.between(nowDate, localDate);

		if (daysDifference >= 0) {
			return true;
		} else {
			return false;
		}
	}



	@Transactional
	public UserDetails getUserDetailsByLoginId(String loginId) {

		CriteriaBuilder criteriaBuilder = userDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<UserDetails> criteriaQuery = criteriaBuilder.createQuery(UserDetails.class);
		Root<UserDetails> root = criteriaQuery.from(UserDetails.class);
		Predicate restriction1 = criteriaBuilder.equal(root.get("loginId"), loginId);
		Predicate restriction2 = criteriaBuilder.notEqual(root.get("status"), Status.REMOVED.name());
		criteriaQuery.where(restriction1,restriction2);
		UserDetails userDetails = userDetailsDao.getSession().createQuery(criteriaQuery).uniqueResult();
		return userDetails;
	}
	
	@Transactional
	public UserDetails getUserDetailsByLoginIdAndStatus(String loginId) {

		CriteriaBuilder criteriaBuilder = userDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<UserDetails> criteriaQuery = criteriaBuilder.createQuery(UserDetails.class);
		Root<UserDetails> root = criteriaQuery.from(UserDetails.class);
		Predicate restriction1 = criteriaBuilder.equal(root.get("loginId"), loginId);
		Predicate restriction2 = criteriaBuilder.notEqual(root.get("status"), Status.REMOVED.name());
		criteriaQuery.where(restriction1, restriction2);
		UserDetails userDetails = userDetailsDao.getSession().createQuery(criteriaQuery).uniqueResult();
		return userDetails;
	}
	
	@Transactional
	public UserDetails getUserDetailsByLoginIdAndSuperadminId(String loginId, String superadminId) {

		CriteriaBuilder criteriaBuilder = userDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<UserDetails> criteriaQuery = criteriaBuilder.createQuery(UserDetails.class);
		Root<UserDetails> root = criteriaQuery.from(UserDetails.class);
		Predicate restriction1 = criteriaBuilder.equal(root.get("loginId"), loginId);
		Predicate restriction2 = criteriaBuilder.equal(root.get("superadminId"), superadminId);
		Predicate restriction3 = criteriaBuilder.notEqual(root.get("status"), Status.REMOVED.name());
		criteriaQuery.where(restriction1, restriction2, restriction3);
		UserDetails userDetails = userDetailsDao.getSession().createQuery(criteriaQuery).uniqueResult();
		return userDetails;
	}
	

	public UserDetails getUserDetailsByReqObj(UserRequestObject userRequest) {

		UserDetails userDetails = new UserDetails();
		
		userDetails.setUserCode(userRequest.getFirstName().substring(0,1)+userRequest.getLastName().substring(0,1));
		userDetails.setLoginId(userRequest.getMobileNo());
		userDetails.setPassword(userRequest.getPassword());
		userDetails.setSalt(userRequest.getSalt());
		userDetails.setStatus(Status.ACTIVE.name());
		userDetails.setRoleType(userRequest.getRoleType());
		userDetails.setService(userRequest.getService());
		userDetails.setPermissions(userRequest.getPermissions());
		userDetails.setUserPicture(userRequest.getUserPicture());
		userDetails.setFirstName(userRequest.getFirstName());
		userDetails.setLastName(userRequest.getLastName());
		userDetails.setMobileNo(userRequest.getMobileNo());
		userDetails.setEmergencyContactRelation1(userRequest.getEmergencyContactRelation1());
		userDetails.setEmergencyContactName1(userRequest.getEmergencyContactName1());
		userDetails.setEmergencyContactNo1(userRequest.getEmergencyContactNo1());
		userDetails.setEmergencyContactRelation2(userRequest.getEmergencyContactRelation2());
		userDetails.setEmergencyContactName2(userRequest.getEmergencyContactName2());
		userDetails.setEmergencyContactNo2(userRequest.getEmergencyContactNo2());
		userDetails.setAlternateMobile(userRequest.getAlternateMobile());
		userDetails.setEmailId(userRequest.getEmailId());
		userDetails.setIdDocumentType(userRequest.getIdDocumentType());
		userDetails.setIdDocumentPicture(userRequest.getIdDocumentPicture());
		userDetails.setPanNumber(userRequest.getPanNumber());
		userDetails.setCreatedBy(userRequest.getCreatedBy());
		userDetails.setAdminId(userRequest.getAdminId());
		userDetails.setTeamleaderId(userRequest.getTeamleaderId());
		
		userDetails.setDob(userRequest.getDob());
		
		userDetails.setCreatedAt(new Date());
		userDetails.setUpdatedAt(new Date());
		userDetails.setPasswordUpdatedAt(new Date());
		if(userDetails.getRoleType().equals(RoleType.SUPERADMIN.name())) {
			userDetails.setSuperadminId(userDetails.getLoginId());
			
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.YEAR, 1);
			Date oneYearLater = calendar.getTime();
			
		    userDetails.setValidityExpireOn(oneYearLater);
		}else {
			UserDetails existsUserDetails = this.getUserDetailsByLoginId(userRequest.getSuperadminId());
			userDetails.setSuperadminId(userRequest.getSuperadminId());
			userDetails.setValidityExpireOn(existsUserDetails.getValidityExpireOn());
		}
		if(userRequest.getTeamleaderId() == null || userRequest.getTeamleaderId().equals("")) {
			userDetails.setTeamleaderId(userRequest.getCreatedBy());
		}
		if(userRequest.getAdminId() == null || userRequest.getAdminId().equals("")) {
			userDetails.setAdminId(userRequest.getCreatedBy());
		}
		System.out.println("ADMIN:"+userRequest.getAdminId()+":");
		System.out.println("Teamleader:"+userRequest.getTeamleaderId()+":");
	
		return userDetails;
	}

	@Transactional
	public UserDetails saveUserDetails(UserDetails userDetails) {
		userDetailsDao.persist(userDetails);
		return userDetails;
	}
	

	public UserDetails getUpdatedUserDetailsByReqObj(UserDetails userDetails, UserRequestObject userRequest) {

		userDetails.setUserPicture(userRequest.getUserPicture());
		userDetails.setRoleType(userRequest.getRoleType());
		userDetails.setFirstName(userRequest.getFirstName());
		userDetails.setLastName(userRequest.getLastName());
		userDetails.setMobileNo(userRequest.getMobileNo());
		userDetails.setAlternateMobile(userRequest.getAlternateMobile());
		userDetails.setEmailId(userRequest.getEmailId());
		userDetails.setUpdatedAt(new Date());

		return userDetails;
	}

	@Transactional
	public UserDetails UpdateUserDetails(UserDetails userDetails) {
		userDetailsDao.update(userDetails);
		return userDetails;
	}
	

	@SuppressWarnings("unchecked")
	public List<UserDetails> getUserDetails(UserRequestObject userRequest) {
		if (userRequest.getRoleType().equals(RoleType.MAINADMIN.name())) {
			List<UserDetails> results = userDetailsDao.getEntityManager()
					.createQuery("SELECT UD FROM UserDetails UD WHERE status NOT IN (:REMOVED) ORDER BY UD.id DESC")
					.setParameter("REMOVED", Status.REMOVED.name()).getResultList();
			return results;
		} else if (userRequest.getRoleType().equals(RoleType.SUPERADMIN.name())) {
			List<UserDetails> results = userDetailsDao.getEntityManager().createQuery(
					"SELECT UD FROM UserDetails UD WHERE UD.superadminId =:superadminId AND roleType NOT IN :roleType AND status NOT IN :REMOVED ORDER BY UD.id DESC")
					.setParameter("superadminId", userRequest.getSuperadminId())
					.setParameter("roleType", RoleType.SUPERADMIN.name())
					.setParameter("REMOVED", Status.REMOVED.name())
					.getResultList();
			return results;
		} else if (userRequest.getRoleType().equals(RoleType.ADMIN.name())) {
			List<UserDetails> results = userDetailsDao.getEntityManager().createQuery(
					"SELECT UD FROM UserDetails UD WHERE UD.adminId =:adminId AND UD.superadminId =:superadminId AND status NOT IN :REMOVED ORDER BY UD.id DESC")
					.setParameter("adminId", userRequest.getCreatedBy())
					.setParameter("superadminId", userRequest.getSuperadminId())
					.setParameter("REMOVED", Status.REMOVED.name())
					.getResultList();
			return results;
		} else if (userRequest.getRoleType().equals(RoleType.TEAM_LEADER.name())) {
			List<UserDetails> results = userDetailsDao.getEntityManager().createQuery(
					"SELECT UD FROM UserDetails UD WHERE UD.createdBy =:createdBy AND UD.superadminId =:superadminId AND status NOT IN :REMOVED ORDER BY UD.id DESC")
					.setParameter("createdBy", userRequest.getLoginId())
					.setParameter("superadminId", userRequest.getSuperadminId())
					.setParameter("REMOVED", Status.REMOVED.name()).getResultList();
			return results;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<UserDetails> getUserDetailsByRoleType(UserRequestObject userRequest) {
		System.out.println("kjhhjh : "+userRequest.getRoleType());
		List<UserDetails> results = userDetailsDao.getEntityManager().createQuery(
				"SELECT UD FROM UserDetails UD WHERE UD.roleType =:roleType AND UD.createdBy =:createdBy AND UD.superadminId =:superadminId ORDER BY UD.id DESC")
				.setParameter("roleType", userRequest.getRoleType())
				.setParameter("createdBy", userRequest.getCreatedBy())
				.setParameter("superadminId", userRequest.getSuperadminId())
				.getResultList();
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<UserDetails> getUserListForDropDown(UserRequestObject userRequest) {
		
		List<String> excludedRoleTypes = new ArrayList<String>();
		if(userRequest.getRoleType().equalsIgnoreCase(RoleType.SUPERADMIN.name())) {
			excludedRoleTypes = Arrays.asList(RoleType.SUPERADMIN.name(), RoleType.ADMIN.name());
			
		}else if(userRequest.getRoleType().equalsIgnoreCase(RoleType.ADMIN.name())) {
			excludedRoleTypes = Arrays.asList(RoleType.ADMIN.name(), RoleType.TEAM_LEADER.name());
		}
		
		List<UserDetails> results = userDetailsDao.getEntityManager()
				.createQuery("SELECT UD FROM UserDetails UD WHERE roleType IN :roleType AND UD.superadminId =:superadminId AND status NOT IN :REMOVED")
				.setParameter("roleType", excludedRoleTypes)
				.setParameter("superadminId", userRequest.getSuperadminId())
				.setParameter("REMOVED", Status.REMOVED.name())
				.getResultList();
		return results;
	}
	

	@SuppressWarnings("unchecked")
	public List<AddressDetails> getAddressDetails(UserRequestObject userRequest) {
		System.out.println(userRequest.getRequestedFor()+"  ,  "+userRequest.getLoginId());
		if(userRequest.getRequestedFor().equals("ALL")) {
			List<AddressDetails> results = userDetailsDao.getEntityManager()
					.createQuery("SELECT AD FROM AddressDetails AD WHERE AD.userId =:userId And AD.superadminId =:superadminId ORDER BY AD.id DESC")
					.setParameter("userId", userRequest.getId())
					.setParameter("superadminId", userRequest.getSuperadminId())
					.getResultList();
			return results;
		}
		return null;
	}
	
	
	public Long getActiveAndInactiveUserCount(String roleType, String createdBy, String status) {
		Long count = 0L;
		if (roleType.equals(RoleType.MAINADMIN.name())) {
			count = (Long) userDetailsDao.getEntityManager().createQuery(
					"SELECT COUNT(*) FROM UserDetails DD where DD.status =:status AND roleType NOT IN :roleType")
					.setParameter("status", status)
					.setParameter("roleType", RoleType.MAINADMIN.name())
					.getSingleResult();
			return count;
		} else if (roleType.equals(RoleType.SUPERADMIN.name())) {
			count = (Long) userDetailsDao.getEntityManager().createQuery(
					"SELECT COUNT(*) FROM UserDetails DD where DD.status =:status AND roleType NOT IN :roleType AND DD.superadminId = :superadminId")
					.setParameter("status", status)
					.setParameter("roleType", RoleType.SUPERADMIN.name())
					.setParameter("superadminId", createdBy)
					.getSingleResult();
			return count;
		} else {
			count = (Long) userDetailsDao.getEntityManager().createQuery(
					"SELECT COUNT(*) FROM UserDetails DD where DD.status =:status AND DD.createdBy = :createdBy")
					.setParameter("status", status)
					.setParameter("createdBy", createdBy)
					.getSingleResult();
			return count;
		}
	}


}
