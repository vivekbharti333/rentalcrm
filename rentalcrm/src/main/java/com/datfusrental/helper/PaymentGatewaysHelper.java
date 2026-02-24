package com.datfusrental.helper;

import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.datfusrental.constant.Constant;
import com.datfusrental.dao.PaymentGatewaysDetailsDao;
import com.datfusrental.entities.CategoryType;
import com.datfusrental.entities.PaymentGatewayDetails;
import com.datfusrental.enums.Status;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.PaymentGatewaysRequestObject;

@Component
public class PaymentGatewaysHelper {

	@Autowired
	private PaymentGatewaysDetailsDao paymentGatewaysDetailsDao;

	public void validatePaymentGatewaysRequest(PaymentGatewaysRequestObject paymentGatewaysRequestObject)
			throws BizException {
		if (paymentGatewaysRequestObject == null) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Bad Request Object Null");
		}
	}

	@Transactional
	public PaymentGatewayDetails getPaymentGatewayDetailsByName(String paymentGatewaysName, String companyName) {

		CriteriaBuilder criteriaBuilder = paymentGatewaysDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<PaymentGatewayDetails> criteriaQuery = criteriaBuilder.createQuery(PaymentGatewayDetails.class);
		Root<PaymentGatewayDetails> root = criteriaQuery.from(PaymentGatewayDetails.class);
		Predicate restriction1 = criteriaBuilder.equal(root.get("paymentGatewaysName"), paymentGatewaysName);
		Predicate restriction2 = criteriaBuilder.equal(root.get("companyName"), companyName);
		criteriaQuery.where(restriction1, restriction2);
		PaymentGatewayDetails paymentGatewayDetails = paymentGatewaysDetailsDao.getSession().createQuery(criteriaQuery).uniqueResult();
		return paymentGatewayDetails;
	}

	public PaymentGatewayDetails getPaymentGatewayDetailsReqObj(PaymentGatewaysRequestObject paymentGatewaysRequest) {

		PaymentGatewayDetails paymentGatewayDetails = new PaymentGatewayDetails();

		paymentGatewayDetails.setPaymentGatewaysName(paymentGatewaysRequest.getPaymentGatewaysName());
		paymentGatewayDetails.setClientId(paymentGatewaysRequest.getClientId());
		paymentGatewayDetails.setSecurityKey(paymentGatewaysRequest.getSecurityKey());
		paymentGatewayDetails.setSalt(paymentGatewaysRequest.getSalt());
		paymentGatewayDetails.setVersion(paymentGatewaysRequest.getVersion());
		paymentGatewayDetails.setStatus(Status.ACTIVE.name());
		paymentGatewayDetails.setCompanyName(paymentGatewaysRequest.getCompanyName());
		paymentGatewayDetails.setCreatedAt(new Date());
		paymentGatewayDetails.setUpdatedAt(new Date());

		return paymentGatewayDetails;
	}

	@Transactional
	public PaymentGatewayDetails savePaymentGateways(PaymentGatewayDetails paymentGatewayDetails) {
		paymentGatewaysDetailsDao.persist(paymentGatewayDetails);
		return paymentGatewayDetails;
	}

}