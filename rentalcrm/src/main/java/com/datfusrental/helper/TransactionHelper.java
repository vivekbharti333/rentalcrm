package com.datfusrental.helper;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.datfusrental.constant.Constant;
import com.datfusrental.dao.TransactionDetailsDao;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.entities.TransactionDetails;
import com.datfusrental.enums.RequestFor;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.LeadRequestObject;

@Component
public class TransactionHelper {

	@Autowired
	private TransactionDetailsDao transactionDetailsDao;
	

	public void validateOtpRequest(LeadRequestObject leadRequestObject) throws BizException {
		if (leadRequestObject == null) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Bad Request Object Null");
		}
	}


	public TransactionDetails getTransactionDetailsByReqObj(LeadRequestObject leadRequest) {

	    TransactionDetails transactionDetails = new TransactionDetails();

	    // Booking Details
	    transactionDetails.setBookingId(leadRequest.getBookingId());

	    // Pickup Date Time
	    transactionDetails.setPickupDateTime(leadRequest.getPickupDateTime());

	    // Customer Details
	    transactionDetails.setCustomerMobile(leadRequest.getCustomerMobile());

	    // Vendor Details
	    transactionDetails.setVendorId(leadRequest.getVendorId());
	    transactionDetails.setVendorName(leadRequest.getVendorName());

	    // Company Amount
	    transactionDetails.setCompanyAmount(leadRequest.getCompanyAmount());
//	    transactionDetails.setCompanyTrasanctionType(leadRequest.gsetCompanyTrasanctionType());

	    // Vendor Amount
	    transactionDetails.setVendorAmount(leadRequest.getVendorAmount());
//	    transactionDetails.setVendorAmountType(leadRequest.getVendorAmountType());s

	    // Net Paid Amount
	    transactionDetails.setNetAmountPaid(leadRequest.getNetAmountPaid());

	    // Created Date
	    transactionDetails.setCreatedAt(new Date());

	    return transactionDetails;
	}
	
	@Transactional
	public TransactionDetails saveTransactionDetails(TransactionDetails transactionDetails) {
		transactionDetailsDao.persist(transactionDetails);
		return transactionDetails;
	}


	
	
	public List<TransactionDetails> getTransactionDetailsByVendorId(LeadRequestObject leadRequest) {
	   
	        return transactionDetailsDao.getEntityManager()
	            .createQuery(
	                "SELECT TD FROM TransactionDetails TD WHERE LD.superadminId = :superadminId AND vendorId =:vendorId ORDER BY LD.id DESC",
	                TransactionDetails.class
	            )
	            .setParameter("superadminId", leadRequest.getSuperadminId())
	            .setParameter("vendorId", leadRequest.getVendorId())
	            .setFirstResult(Constant.FIRST_RESULT)
	            .setMaxResults(Constant.MAX_RESULT)
	            .getResultList();

	    
	}
	
}
