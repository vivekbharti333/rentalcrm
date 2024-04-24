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
import com.datfusrental.dao.UserRoleDao;
import com.datfusrental.dao.UserRoleMasterDao;
import com.datfusrental.entities.AddressDetails;
import com.datfusrental.entities.UserDetails;
import com.datfusrental.entities.UserRole;
import com.datfusrental.entities.UserRoleMaster;
import com.datfusrental.enums.RequestFor;
import com.datfusrental.enums.RoleType;
import com.datfusrental.enums.Status;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.UserRequestObject;

@Component
public class UserRoleHelper {

	@Autowired
	private UserRoleMasterDao userRoleMasterDao;

	@Autowired
	private UserRoleDao userRoleDao;

	public void validateUserRequest(UserRequestObject userRequestObject) throws BizException {
		if (userRequestObject == null) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Bad Request Object Null");
		}
	}

	@Transactional
	public UserRoleMaster getUserRoleMasterByRoleType(String roleType) {

		CriteriaBuilder criteriaBuilder = userRoleMasterDao.getSession().getCriteriaBuilder();
		CriteriaQuery<UserRoleMaster> criteriaQuery = criteriaBuilder.createQuery(UserRoleMaster.class);
		Root<UserRoleMaster> root = criteriaQuery.from(UserRoleMaster.class);
		Predicate restriction = criteriaBuilder.equal(root.get("roleType"), roleType);
		criteriaQuery.where(restriction);
		UserRoleMaster userRoleMaster = userRoleMasterDao.getSession().createQuery(criteriaQuery).uniqueResult();
		return userRoleMaster;
	}

	@Transactional
	public UserRole getUserRoleBySuperadminId(String superadminId) {

		CriteriaBuilder criteriaBuilder = userRoleDao.getSession().getCriteriaBuilder();
		CriteriaQuery<UserRole> criteriaQuery = criteriaBuilder.createQuery(UserRole.class);
		Root<UserRole> root = criteriaQuery.from(UserRole.class);
		Predicate restriction = criteriaBuilder.equal(root.get("superadminId"), superadminId);
		criteriaQuery.where(restriction);
		UserRole userRole = userRoleDao.getSession().createQuery(criteriaQuery).uniqueResult();
		return userRole;
	}

	public UserRoleMaster getUserRoleMasterByReqObj(UserRequestObject userRequest) {

		UserRoleMaster userRoleMaster = new UserRoleMaster();

		userRoleMaster.setRoleType(userRequest.getRoleType());
		userRoleMaster.setStatus(Status.ACTIVE.name());
		userRoleMaster.setCreatedAt(new Date());
		userRoleMaster.setUpdatedAt(new Date());

		return userRoleMaster;
	}

	@Transactional
	public UserRoleMaster saveUserRoleMaster(UserRoleMaster userRoleMaster) {
		userRoleMasterDao.persist(userRoleMaster);
		return userRoleMaster;
	}

	public UserRoleMaster getUpdatedUserRoleMasterByReqObj(UserRoleMaster userRoleMaster,
			UserRequestObject userRequest) {

		userRoleMaster.setRoleType(userRequest.getRoleType());
		userRoleMaster.setCreatedAt(new Date());
		userRoleMaster.setUpdatedAt(new Date());
		return userRoleMaster;
	}

	@Transactional
	public UserRoleMaster updateUserRoleMaster(UserRoleMaster userRoleMaster) {
		userRoleMasterDao.update(userRoleMaster);
		return userRoleMaster;
	}

	@SuppressWarnings("unchecked")
	public List<UserRoleMaster> getUserRoleMaster(UserRequestObject userRequest) {
		if (userRequest.getRequestedFor().equals("ALL")) {
			List<UserRoleMaster> results = userRoleMasterDao.getEntityManager()
					.createQuery("SELECT AD FROM UserRoleMaster AD ORDER BY AD.roleType ASC").getResultList();
			return results;
		} else if (userRequest.getRequestedFor().equals("ACTIVE")) {
			List<UserRoleMaster> results = userRoleMasterDao.getEntityManager()
					.createQuery("SELECT AD FROM UserRoleMaster AD WHERE AD.status =:status ORDER BY AD.roleType ASC")
					.setParameter("status", Status.ACTIVE.name()).getResultList();
			return results;
		} else if (userRequest.getRequestedFor().equals("BYSUPERADMINID")) {
			List<UserRoleMaster> results = userRoleMasterDao.getEntityManager()
					.createQuery("SELECT AD FROM UserRoleMaster AD WHERE AD.id IN (:ids) ORDER BY AD.roleType ASC")
					.setParameter("ids", userRequest.getRoleTypeIds()).getResultList();
			return results;
		}
		return null;
	}

	public UserRole getUserRoleByReqObj(UserRequestObject userRequest) {

		UserRole userRole = new UserRole();

		userRole.setRoleTypeIds(userRequest.getRoleType());
		userRole.setCreatedAt(new Date());
		userRole.setUpdatedAt(new Date());

		return userRole;
	}

	@Transactional
	public UserRole saveUserRole(UserRole userRole) {
		userRoleDao.persist(userRole);
		return userRole;
	}

	@Transactional
	public UserRole updateUserRole(UserRole userRole) {
		userRoleDao.update(userRole);
		return userRole;
	}

//	@SuppressWarnings("unchecked")
//	public List<UserRole> getUserRole(UserRequestObject userRequest) {
//		if(userRequest.getRequestedFor().equals("ALL")) {
//			List<UserRole> results = userRoleMasterDao.getEntityManager()
//					.createQuery("SELECT AD FROM UserRole AD ORDER BY AD.roleType ASC")
//					.getResultList();
//			return results;
//		}
//		return null;
//	}

}
