package com.datfusrental.helper;

import java.security.SecureRandom;
import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.datfusrental.constant.Constant;
import com.datfusrental.dao.OtpDetailsDao;
import com.datfusrental.entities.OtpDetails;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.LeadRequestObject;
import com.datfusrental.object.request.OtpRequestObject;

@Component
public class OtpHelper {

	@Autowired
	private OtpDetailsDao otpDetailsDao;
	
	private static final SecureRandom secureRandom = new SecureRandom();

	public void validateOtpRequest(OtpRequestObject otpRequestObject) throws BizException {
		if (otpRequestObject == null) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Bad Request Object Null");
		}
	}

	

//    public static String generate6DigitOtp() {
//        int otp = 100000 + secureRandom.nextInt(900000);
//        return String.valueOf(otp);
//    }

	/** Generates a cryptographically secure 6 digit OTP. */
	public static String generate6DigitOtp() {
		int otp = 100000 + secureRandom.nextInt(900000);
		return String.valueOf(otp);
	}
	
	@Transactional
	public OtpDetails getOtpDetailsByMobile(String customerMobile) {

		CriteriaBuilder criteriaBuilder = otpDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<OtpDetails> criteriaQuery = criteriaBuilder.createQuery(OtpDetails.class);
		Root<OtpDetails> root = criteriaQuery.from(OtpDetails.class);
		Predicate restriction = criteriaBuilder.equal(root.get("customerMobile"), customerMobile);
		criteriaQuery.where(restriction);
		OtpDetails otpDetails = otpDetailsDao.getSession().createQuery(criteriaQuery).uniqueResult();
		return otpDetails;
	}

	public OtpDetails getOtpDetailsByReqObj(OtpRequestObject otpRequest) {

		OtpDetails otpDetails = new OtpDetails();

		otpDetails.setCustomerMobile(otpRequest.getCustomerMobile());
		otpDetails.setCustomerEmailId(otpRequest.getCustomerEmailId());
		otpDetails.setOtp(generate6DigitOtp());
		otpDetails.setStatus("INIT");
		otpDetails.setOtpCount(0);
		otpDetails.setCreatedAt(new Date());
		otpDetails.setUpdatedAt(new Date());

		return otpDetails;
	}

	@Transactional
	public OtpDetails saveOtpDetails(OtpDetails otpDetails) {
		otpDetailsDao.persist(otpDetails);
		return otpDetails;
	}

	public OtpDetails getUpdatedOtpDetailsByReqObj(OtpRequestObject otpRequest, OtpDetails otpDetails) {

		Date now = new Date();

		// A newly generated OTP gets its own wrong-attempt allowance.
		otpDetails.setOtpCount(0);

		otpDetails.setOtp(generate6DigitOtp());
		otpDetails.setStatus("INIT");
		otpDetails.setUpdatedAt(now);

		return otpDetails;
	}

	@Transactional
	public OtpDetails updateOtpDetails(OtpDetails otpDetails) {
		otpDetailsDao.update(otpDetails);
		return otpDetails;
	}

}
