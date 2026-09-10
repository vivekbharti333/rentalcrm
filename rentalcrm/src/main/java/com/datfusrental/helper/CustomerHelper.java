package com.datfusrental.helper;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.datfusrental.constant.Constant;
import com.datfusrental.dao.CustomerPickupDetailsDao;
import com.datfusrental.dao.CustomerTicketsDetailsDao;
import com.datfusrental.entities.CustomerPickupDetails;
import com.datfusrental.entities.CustomerTicketsDetails;
import com.datfusrental.enums.Status;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.LeadRequestObject;

@Component
public class CustomerHelper {

	@Autowired
	private CustomerPickupDetailsDao customerPickupDetailsDao;

	@Autowired
	private CustomerTicketsDetailsDao customerTicketsDetailsDao;

	public void validateLeadRequest(LeadRequestObject leadRequestObject) throws BizException {
		if (leadRequestObject == null) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Bad Request Object Null");
		}
	}

	@Transactional
	public CustomerPickupDetails getCustomerPickupDetailsByBookingId(String bookingId) {

		CriteriaBuilder criteriaBuilder = customerPickupDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<CustomerPickupDetails> criteriaQuery = criteriaBuilder.createQuery(CustomerPickupDetails.class);
		Root<CustomerPickupDetails> root = criteriaQuery.from(CustomerPickupDetails.class);
		Predicate restriction = criteriaBuilder.equal(root.get("bookingId"), bookingId);
		criteriaQuery.where(restriction);
		CustomerPickupDetails customerPickupDetails = customerPickupDetailsDao.getSession().createQuery(criteriaQuery)
				.uniqueResult();
		return customerPickupDetails;
	}

	public CustomerPickupDetails getCustomerPickupDetailsByReqObj(LeadRequestObject leadRequest) {

		CustomerPickupDetails customerPickupDetails = new CustomerPickupDetails();

		customerPickupDetails.setBookingId(leadRequest.getBookingId());
		customerPickupDetails.setPaymentImage(leadRequest.getPaymentImage());
		customerPickupDetails.setVehicleVideo(leadRequest.getVehicleVideo());
		customerPickupDetails.setFrantImage(leadRequest.getFrantImage());
		customerPickupDetails.setBackImage(leadRequest.getBackImage());
		customerPickupDetails.setLeftImage(leadRequest.getLeftImage());
		customerPickupDetails.setRightImage(leadRequest.getRightImage());
		customerPickupDetails.setFuelGaugeImage(leadRequest.getFuelGaugeImage());

		customerPickupDetails.setSecurityAmountImage(leadRequest.getSecurityAmountImage());

		customerPickupDetails.setBalanceAmountPayTo(leadRequest.getBalanceAmountPayTo());
		customerPickupDetails.setBalanceAmountPayMode(leadRequest.getBalanceAmountPayMode());
		customerPickupDetails.setBalanceAmountPayImage(leadRequest.getBalanceAmountPayImage());

		customerPickupDetails.setNotes(leadRequest.getNotes());

		return customerPickupDetails;
	}

	@Transactional
	public CustomerPickupDetails saveCustomerPickupDetails(CustomerPickupDetails customerPickupDetails) {
		customerPickupDetailsDao.persist(customerPickupDetails);
		return customerPickupDetails;
	}

	public CustomerTicketsDetails getCustomerTicketsDetailsByReqObj(LeadRequestObject leadRequest) {

		CustomerTicketsDetails customerTicketsDetails = new CustomerTicketsDetails();

		customerTicketsDetails.setBookingId(leadRequest.getBookingId());
		customerTicketsDetails.setCustomeName(leadRequest.getCustomeName());
		customerTicketsDetails.setCustomerMobile(leadRequest.getCustomerMobile());
		customerTicketsDetails.setCategory(leadRequest.getCategory());
		customerTicketsDetails.setSubCategory(leadRequest.getSubCategory());
		customerTicketsDetails.setNotes(leadRequest.getNotes());
		customerTicketsDetails.setStatus(Status.PENDING.name());

		return customerTicketsDetails;
	}

	@Transactional
	public CustomerTicketsDetails saveCustomerTicketsDetails(CustomerTicketsDetails customerTicketsDetails) {
		customerTicketsDetailsDao.persist(customerTicketsDetails);
		return customerTicketsDetails;
	}
}
