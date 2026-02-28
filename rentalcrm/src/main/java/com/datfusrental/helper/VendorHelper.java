package com.datfusrental.helper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
import com.datfusrental.dao.VendorDetailsDao;
import com.datfusrental.entities.AddressDetails;
import com.datfusrental.entities.User;
import com.datfusrental.entities.VendorDetails;
import com.datfusrental.enums.RoleType;
import com.datfusrental.enums.Status;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.DashboardRequestObject;
import com.datfusrental.object.request.LoginRequestObject;
import com.datfusrental.object.request.UserRequestObject;

@Component
public class VendorHelper {

	@Autowired
	private VendorDetailsDao vendorDetailsDao;

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

	@Transactional
	public VendorDetails getVendorDetailsById(Long id) {

		CriteriaBuilder criteriaBuilder = vendorDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<VendorDetails> criteriaQuery = criteriaBuilder.createQuery(VendorDetails.class);
		Root<VendorDetails> root = criteriaQuery.from(VendorDetails.class);
		Predicate restriction = criteriaBuilder.equal(root.get("id"), id);
		criteriaQuery.where(restriction);
		VendorDetails user = vendorDetailsDao.getSession().createQuery(criteriaQuery).uniqueResult();
		return user;
	}

	@Transactional
	public VendorDetails getVendorDetailsByWhatsAppNumber(String whatsappNo) {

		CriteriaBuilder criteriaBuilder = vendorDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<VendorDetails> criteriaQuery = criteriaBuilder.createQuery(VendorDetails.class);
		Root<VendorDetails> root = criteriaQuery.from(VendorDetails.class);
		Predicate restriction = criteriaBuilder.equal(root.get("whatsappNo"), whatsappNo);
		criteriaQuery.where(restriction);
		VendorDetails user = vendorDetailsDao.getSession().createQuery(criteriaQuery).uniqueResult();
		return user;
	}

	public VendorDetails getVendorDetailsByReqObj(UserRequestObject userRequest) {

		VendorDetails user = new VendorDetails();

		user.setLoginId(userRequest.getLoginId());
		user.setPseudoName(userRequest.getPseudoName());
		user.setFirstName(userRequest.getFirstName());
		user.setLastName(userRequest.getLastName());
		user.setGender(userRequest.getGender());

		user.setWhatsappLink(userRequest.getWhatsappLink());
		user.setProductName(userRequest.getProductName());

		user.setStatus(Status.ACTIVE.name());
		user.setRoleType("VENDOR");

		user.setWhatsappNo(userRequest.getWhatsappNo());
		user.setAlternateMobile(userRequest.getAlternateMobile());
		user.setEmailId(userRequest.getEmailId());

		user.setVendorState(userRequest.getVendorState());
		user.setVendorRegion(userRequest.getVendorRegion());
		user.setVendorCity(userRequest.getVendorCity());

		user.setPanCard(userRequest.getPanCard());
		user.setAadharCard(userRequest.getAadharCard());
		user.setGst(userRequest.getGst());

		user.setVendorWebsiteLink(userRequest.getVendorWebsiteLink());
		user.setAccountDetails(userRequest.getAccountDetails());

		// Wallet defaults
		user.setUserWalletAmount(0L);

		user.setCreatedBy(userRequest.getCreatedBy());
		user.setSuperadminId(userRequest.getSuperadminId());

		user.setCreatedAt(new Date());
		user.setUpdatedAt(new Date());

		return user;
	}

	@Transactional
	public VendorDetails saveVendorDetails(VendorDetails vendorDetails) {
		vendorDetailsDao.persist(vendorDetails);
		return vendorDetails;
	}

	public VendorDetails getUpdatedVendorDetailsByReqObj(VendorDetails user, UserRequestObject userRequest) {

		user.setPseudoName(userRequest.getPseudoName());
		user.setFirstName(userRequest.getFirstName());
		user.setLastName(userRequest.getLastName());
		user.setGender(userRequest.getGender());

		user.setWhatsappLink(userRequest.getWhatsappLink());
		user.setProductName(userRequest.getProductName());

		user.setStatus(Status.ACTIVE.name());
		user.setRoleType(userRequest.getRoleType());

		user.setWhatsappNo(userRequest.getWhatsappNo());
		user.setAlternateMobile(userRequest.getAlternateMobile());
		user.setEmailId(userRequest.getEmailId());

		user.setVendorState(userRequest.getVendorState());
		user.setVendorRegion(userRequest.getVendorRegion());
		user.setVendorCity(userRequest.getVendorCity());

		user.setPanCard(userRequest.getPanCard());
		user.setAadharCard(userRequest.getAadharCard());
		user.setGst(userRequest.getGst());

		user.setVendorWebsiteLink(userRequest.getVendorWebsiteLink());
		user.setAccountDetails(userRequest.getAccountDetails());

		// Wallet defaults
//	    user.setUserWalletAmount(0L);

		user.setUpdatedAt(new Date());

		return user;
	}

	@Transactional
	public VendorDetails UpdateVendorDetails(VendorDetails vendorDetails) {
		vendorDetailsDao.update(vendorDetails);
		return vendorDetails;
	}

	@SuppressWarnings("unchecked")
	public List<VendorDetails> getVendorDetails(UserRequestObject userRequest) {
		List<VendorDetails> results = vendorDetailsDao.getEntityManager()
				.createQuery("SELECT UD FROM VendorDetails UD ORDER BY UD.id DESC").getResultList();
		return results;
	}

}
