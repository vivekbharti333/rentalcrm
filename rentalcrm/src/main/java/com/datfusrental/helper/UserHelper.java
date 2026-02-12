package com.datfusrental.helper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import com.datfusrental.entities.User;
import com.datfusrental.enums.RoleType;
import com.datfusrental.enums.Status;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.DashboardRequestObject;
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
	public User getUserDetailsByLoginId(String loginId) {

		CriteriaBuilder criteriaBuilder = userDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
		Root<User> root = criteriaQuery.from(User.class);
		Predicate restriction1 = criteriaBuilder.equal(root.get("loginId"), loginId);
		Predicate restriction2 = criteriaBuilder.notEqual(root.get("status"), Status.REMOVED.name());
		criteriaQuery.where(restriction1,restriction2);
		User user = userDetailsDao.getSession().createQuery(criteriaQuery).uniqueResult();
		return user;
	}
	
	@Transactional
	public User getUserDetailsByLoginIdAndStatus(String loginId) {

		CriteriaBuilder criteriaBuilder = userDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
		Root<User> root = criteriaQuery.from(User.class);
		Predicate restriction1 = criteriaBuilder.equal(root.get("loginId"), loginId);
		Predicate restriction2 = criteriaBuilder.notEqual(root.get("status"), Status.REMOVED.name());
		criteriaQuery.where(restriction1, restriction2);
		User user = userDetailsDao.getSession().createQuery(criteriaQuery).uniqueResult();
		return user;
	}
	
	@Transactional
	public User getUserDetailsByLoginIdAndSuperadminId(String loginId, String superadminId) {

		CriteriaBuilder criteriaBuilder = userDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
		Root<User> root = criteriaQuery.from(User.class);
		Predicate restriction1 = criteriaBuilder.equal(root.get("loginId"), loginId);
		Predicate restriction2 = criteriaBuilder.equal(root.get("superadminId"), superadminId);
		Predicate restriction3 = criteriaBuilder.notEqual(root.get("status"), Status.REMOVED.name());
		criteriaQuery.where(restriction1, restriction2, restriction3);
		User user = userDetailsDao.getSession().createQuery(criteriaQuery).uniqueResult();
		return user;
	}
	

	public User getUserDetailsByReqObj(UserRequestObject userRequest) {

		User user = new User();
		
		user.setUserCode(userRequest.getFirstName().substring(0,1)+userRequest.getLastName().substring(0,1));
		user.setLoginId(userRequest.getMobileNo());
		user.setPassword(userRequest.getPassword());
		user.setSalt(userRequest.getSalt());
		user.setStatus(Status.ACTIVE.name());
		user.setRoleType(userRequest.getRoleType());
		user.setService(userRequest.getService());
		user.setPermissions(userRequest.getPermissions());
		user.setUserPicture(userRequest.getUserPicture());
		user.setPseudoName(userRequest.getPseudoName());
		user.setFirstName(userRequest.getFirstName());
		user.setLastName(userRequest.getLastName());
		user.setMobileNo(userRequest.getMobileNo());
		user.setEmergencyContactRelation1(userRequest.getEmergencyContactRelation1());
		user.setEmergencyContactName1(userRequest.getEmergencyContactName1());
		user.setEmergencyContactNo1(userRequest.getEmergencyContactNo1());
		user.setEmergencyContactRelation2(userRequest.getEmergencyContactRelation2());
		user.setEmergencyContactName2(userRequest.getEmergencyContactName2());
		user.setEmergencyContactNo2(userRequest.getEmergencyContactNo2());
		user.setAlternateMobile(userRequest.getAlternateMobile());
		user.setEmailId(userRequest.getEmailId());
		user.setUserWalletAmount(0L);
		user.setCompanyWalletAmount(0L);
		user.setIdDocumentType(userRequest.getIdDocumentType());
		user.setIdDocumentPicture(userRequest.getIdDocumentPicture());
		user.setPanNumber(userRequest.getPanNumber());
		user.setCreatedBy(userRequest.getCreatedBy());
		user.setAdminId(userRequest.getAdminId());
		user.setTeamleaderId(userRequest.getTeamleaderId());
		
		user.setDob(userRequest.getDob());
		
		user.setCreatedAt(new Date());
		user.setUpdatedAt(new Date());
		user.setPasswordUpdatedAt(new Date());
		if(user.getRoleType().equals(RoleType.SUPERADMIN.name())) {
			user.setSuperadminId(user.getLoginId());
			
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.YEAR, 1);
			Date oneYearLater = calendar.getTime();
			
		    user.setValidityExpireOn(oneYearLater);
		}else {
			User existsUserDetails = this.getUserDetailsByLoginId(userRequest.getSuperadminId());
			user.setSuperadminId(userRequest.getSuperadminId());
			user.setValidityExpireOn(existsUserDetails.getValidityExpireOn());
		}
		if(userRequest.getTeamleaderId() == null || userRequest.getTeamleaderId().equals("")) {
			user.setTeamleaderId(userRequest.getCreatedBy());
		}
		if(userRequest.getAdminId() == null || userRequest.getAdminId().equals("")) {
			user.setAdminId(userRequest.getCreatedBy());
		}
	
		return user;
	}

	@Transactional
	public User saveUserDetails(User user) {
		userDetailsDao.persist(user);
		return user;
	}
	

	public User getUpdatedUserDetailsByReqObj(User user, UserRequestObject userRequest) {

		user.setUserPicture(userRequest.getUserPicture());
		user.setRoleType(userRequest.getRoleType());
		user.setFirstName(userRequest.getFirstName());
		user.setLastName(userRequest.getLastName());
		user.setMobileNo(userRequest.getMobileNo());
		user.setAlternateMobile(userRequest.getAlternateMobile());
		user.setEmailId(userRequest.getEmailId());
		user.setPermissions(userRequest.getPermissions());
		user.setUpdatedAt(new Date());

		return user;
	}

	@Transactional
	public User UpdateUserDetails(User user) {
		userDetailsDao.update(user);
		return user;
	}
	

	@SuppressWarnings("unchecked")
	public List<User> getUserDetails(UserRequestObject userRequest) {
		if (userRequest.getRoleType().equals(RoleType.MAINADMIN.name())) {
			List<User> results = userDetailsDao.getEntityManager()
					.createQuery("SELECT UD FROM UserDetails UD WHERE status NOT IN (:REMOVED) ORDER BY UD.id DESC")
					.setParameter("REMOVED", Status.REMOVED.name()).getResultList();
			return results;
		} else if (userRequest.getRoleType().equals(RoleType.SUPERADMIN.name())) {
			List<User> results = userDetailsDao.getEntityManager().createQuery(
					"SELECT UD FROM User UD WHERE UD.superadminId =:superadminId AND roleType NOT IN :roleType AND status NOT IN :REMOVED ORDER BY UD.id DESC")
					.setParameter("superadminId", userRequest.getSuperadminId())
					.setParameter("roleType", RoleType.SUPERADMIN.name())
					.setParameter("REMOVED", Status.REMOVED.name())
					.getResultList();
			return results;
		} else if (userRequest.getRoleType().equals(RoleType.ADMIN.name())) {
			List<User> results = userDetailsDao.getEntityManager().createQuery(
					"SELECT UD FROM User UD WHERE UD.adminId =:adminId AND UD.superadminId =:superadminId AND status NOT IN :REMOVED ORDER BY UD.id DESC")
					.setParameter("adminId", userRequest.getCreatedBy())
					.setParameter("superadminId", userRequest.getSuperadminId())
					.setParameter("REMOVED", Status.REMOVED.name())
					.getResultList();
			return results;
		} else if (userRequest.getRoleType().equals(RoleType.TEAM_LEADER.name())) {
			List<User> results = userDetailsDao.getEntityManager().createQuery(
					"SELECT UD FROM User UD WHERE UD.createdBy =:createdBy AND UD.superadminId =:superadminId AND status NOT IN :REMOVED ORDER BY UD.id DESC")
					.setParameter("teamleaderId", userRequest.getTeamleaderId())
					.setParameter("superadminId", userRequest.getSuperadminId())
					.setParameter("REMOVED", Status.REMOVED.name()).getResultList();
			return results;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<User> getUserDetailsByRoleType(UserRequestObject userRequest) {
		List<User> results = userDetailsDao.getEntityManager().createQuery(
				"SELECT UD FROM User UD WHERE UD.roleType =:roleType AND UD.superadminId =:superadminId ORDER BY UD.id DESC")
				.setParameter("roleType", userRequest.getRoleType())
				.setParameter("superadminId", userRequest.getSuperadminId())
				.getResultList();
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<User> getUserListForDropDown(UserRequestObject userRequest) {
		
//		List<String> excludedRoleTypes = new ArrayList<String>();
//		if(userRequest.getRoleType().equalsIgnoreCase(RoleType.SUPERADMIN.name())) {
//			excludedRoleTypes = Arrays.asList(RoleType.SUPERADMIN.name());
//			
//		}else if(userRequest.getRoleType().equalsIgnoreCase(RoleType.ADMIN.name())) {
//			excludedRoleTypes = Arrays.asList(RoleType.SUPERADMIN.name(), RoleType.SUPERADMIN.name());
//			
//		}else if(userRequest.getRoleType().equalsIgnoreCase(RoleType.ADMIN.name())) {
//			excludedRoleTypes = Arrays.asList(RoleType.SUPERADMIN.name());
//		}
		
		List<User> results = userDetailsDao.getEntityManager()
				.createQuery("SELECT UD FROM User UD WHERE roleType =:roleType AND UD.superadminId =:superadminId AND status NOT IN :REMOVED")
				.setParameter("roleType", RoleType.SUPERADMIN.name())
				.setParameter("superadminId", userRequest.getSuperadminId())
				.setParameter("REMOVED", Status.REMOVED.name())
				.getResultList();
		return results;
	}
	
	public List<User> getAdminListForDropDown(UserRequestObject userRequest) {
		// TODO Auto-generated method stub
		return null;
	}
	

	@SuppressWarnings("unchecked")
	public List<AddressDetails> getAddressDetails(UserRequestObject userRequest) {
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
	
	
	public Long getActiveAndInactiveUserCount(DashboardRequestObject dashboardRequest) {
		Long count = 0L;
		if (dashboardRequest.getRoleType().equals(RoleType.MAINADMIN.name())) {
			count = (Long) userDetailsDao.getEntityManager().createQuery(
					"SELECT COUNT(*) FROM User DD where roleType NOT IN :roleType")
//					.setParameter("status", status)
					.setParameter("roleType", RoleType.MAINADMIN.name())
					.getSingleResult();
			return count;
		} else if (dashboardRequest.getRoleType().equals(RoleType.SUPERADMIN.name())) {
			count = (Long) userDetailsDao.getEntityManager().createQuery(
					"SELECT COUNT(*) FROM User DD where roleType NOT IN :roleType AND DD.superadminId = :superadminId")
//					.setParameter("status", status)
					.setParameter("roleType", RoleType.SUPERADMIN.name())
					.setParameter("superadminId", dashboardRequest.getSuperadminId())
					.getSingleResult();
			return count;
//		} else if (roleType.equals(RoleType.ADMIN.name())) {
//			count = (Long) userDetailsDao.getEntityManager().createQuery(
//					"SELECT COUNT(*) FROM UserDetails DD where DD.status =:status AND DD.createdBy = :createdBy")
//					.setParameter("status", status)
//					.setParameter("createdBy", createdBy)
//					.getSingleResult();
//			return count;
		}
		return count;
	}




}
