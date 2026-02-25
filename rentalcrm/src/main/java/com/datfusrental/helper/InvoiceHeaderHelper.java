package com.datfusrental.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.datfusrental.constant.Constant;
import com.datfusrental.dao.ApplicationHeaderDetailsDao;
import com.datfusrental.dao.InvoiceHeaderDetailsDao;
import com.datfusrental.entities.ApplicationHeaderDetails;
import com.datfusrental.entities.InvoiceHeaderDetails;
import com.datfusrental.enums.Status;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.ApplicationRequestObject;
import com.datfusrental.object.request.InvoiceHeaderRequestObject;

@Component
public class InvoiceHeaderHelper {

	@Autowired
	private InvoiceHeaderDetailsDao invoiceHeaderDetailsDao;

	public void validateInvoiceHeaderRequest(InvoiceHeaderRequestObject invoiceHeaderRequestObject)
			throws BizException {
		if (invoiceHeaderRequestObject == null) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Bad Request Object Null");
		}
	}

	@Transactional
	public InvoiceHeaderDetails getInvoiceHeaderDetailsBySuperadminId(String superadminId) {
		CriteriaBuilder criteriaBuilder = invoiceHeaderDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<InvoiceHeaderDetails> criteriaQuery = criteriaBuilder.createQuery(InvoiceHeaderDetails.class);
		Root<InvoiceHeaderDetails> root = criteriaQuery.from(InvoiceHeaderDetails.class);
		Predicate restriction = criteriaBuilder.equal(root.get("superadminId"), superadminId);
		criteriaQuery.where(restriction);
		InvoiceHeaderDetails invoiceHeaderDetails = invoiceHeaderDetailsDao.getSession().createQuery(criteriaQuery)
				.uniqueResult();
		return invoiceHeaderDetails;
	}

	public InvoiceHeaderDetails getInvoiceHeaderDetailsByReqObj(InvoiceHeaderRequestObject invoiceHeaderRequest) {

		InvoiceHeaderDetails invoiceHeader = new InvoiceHeaderDetails();

		invoiceHeader.setInvoiceInitial(invoiceHeaderRequest.getInvoiceInitial());
		invoiceHeader.setInvoiceNumber(invoiceHeaderRequest.getInvoiceNumber());
		invoiceHeader.setCompanyLogo(invoiceHeaderRequest.getCompanyLogo());
		invoiceHeader.setCompanyName(invoiceHeaderRequest.getCompanyName());
		invoiceHeader.setCompanyAddress(invoiceHeaderRequest.getCompanyAddress());
		invoiceHeader.setCity(invoiceHeaderRequest.getCity());
		invoiceHeader.setPin(invoiceHeaderRequest.getPin());
		invoiceHeader.setPhoneNumber(invoiceHeaderRequest.getPhoneNumber());
		invoiceHeader.setEmailId(invoiceHeaderRequest.getEmailId());
		invoiceHeader.setWebsite(invoiceHeaderRequest.getWebsite());
		invoiceHeader.setNotes(invoiceHeaderRequest.getNotes());
		invoiceHeader.setTermCondition(invoiceHeaderRequest.getTermCondition());
		
		invoiceHeader.setStatus(Status.ACTIVE.name());
		invoiceHeader.setSuperadminId(invoiceHeaderRequest.getSuperadminId());

		return invoiceHeader;
	}

	@Transactional
	public InvoiceHeaderDetails saveInvoiceHeaderDetails(InvoiceHeaderDetails invoiceHeaderDetails) {
		invoiceHeaderDetailsDao.persist(invoiceHeaderDetails);
		return invoiceHeaderDetails;
	}

	public InvoiceHeaderDetails getUpdatedInvoiceHeaderDetailsByReqObj(InvoiceHeaderRequestObject invoiceHeaderRequest,
			InvoiceHeaderDetails invoiceHeader) {

		invoiceHeader.setInvoiceInitial(invoiceHeaderRequest.getInvoiceInitial());
		invoiceHeader.setInvoiceNumber(invoiceHeaderRequest.getInvoiceNumber());
		invoiceHeader.setCompanyLogo(invoiceHeaderRequest.getCompanyLogo());
		invoiceHeader.setCompanyName(invoiceHeaderRequest.getCompanyName());
		invoiceHeader.setCompanyAddress(invoiceHeaderRequest.getCompanyAddress());
		invoiceHeader.setCity(invoiceHeaderRequest.getCity());
		invoiceHeader.setPin(invoiceHeaderRequest.getPin());
		invoiceHeader.setPhoneNumber(invoiceHeaderRequest.getPhoneNumber());
		invoiceHeader.setEmailId(invoiceHeaderRequest.getEmailId());
		invoiceHeader.setWebsite(invoiceHeaderRequest.getWebsite());
		invoiceHeader.setNotes(invoiceHeaderRequest.getNotes());
		invoiceHeader.setTermCondition(invoiceHeaderRequest.getTermCondition());

		invoiceHeader.setStatus(invoiceHeaderRequest.getStatus());

		return invoiceHeader;
	}

	@Transactional
	public InvoiceHeaderDetails updateInvoiceHeaderDetails(InvoiceHeaderDetails invoiceHeaderDetails) {
		invoiceHeaderDetailsDao.update(invoiceHeaderDetails);
		return invoiceHeaderDetails;
	}

	@SuppressWarnings("unchecked")
	public List<InvoiceHeaderDetails> getInvoiceHeaderDetailsBySuperadminId(InvoiceHeaderRequestObject invoiceHeaderRequest) {
		List<InvoiceHeaderDetails> results = new ArrayList<>();
		results = invoiceHeaderDetailsDao.getEntityManager().createQuery(
				"SELECT DD FROM InvoiceHeaderDetails DD WHERE DD.status =:status AND DD.superadminId =:superadminId ORDER BY DD.id DESC")
				.setParameter("superadminId", invoiceHeaderRequest.getSuperadminId())
				.setParameter("status", Status.ACTIVE.name()).getResultList();
		return results;
	}

}
