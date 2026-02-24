package com.datfusrental.services;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datfusrental.constant.Constant;
import com.datfusrental.entities.ApplicationHeaderDetails;
import com.datfusrental.entities.InvoiceHeaderDetails;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.ApplicationHelper;
import com.datfusrental.helper.InvoiceHeaderHelper;
import com.datfusrental.jwt.JwtTokenUtil;
import com.datfusrental.object.request.ApplicationRequestObject;
import com.datfusrental.object.request.InvoiceHeaderRequestObject;
import com.datfusrental.object.request.Request;

@Service
public class InvoiceHeaderService {

	@Autowired
	private InvoiceHeaderHelper invoiceHeaderHelper;

	@Autowired
	public HttpServletRequest request;

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Transactional
	public InvoiceHeaderRequestObject addUpdateInvoiceHeader(
			Request<InvoiceHeaderRequestObject> invoiceHeaderRequestObject) throws BizException, Exception {
		InvoiceHeaderRequestObject invoiceHeaderRquest = invoiceHeaderRequestObject.getPayload();
		invoiceHeaderHelper.validateInvoiceHeaderRequest(invoiceHeaderRquest);

		InvoiceHeaderDetails invoiceHeader = invoiceHeaderHelper.getInvoiceHeaderDetailsBySuperadminId(invoiceHeaderRquest.getSuperadminId());
		if (invoiceHeader == null) {

			InvoiceHeaderDetails invoiceHeaderDetails = invoiceHeaderHelper.getInvoiceHeaderDetailsByReqObj(invoiceHeaderRquest);
			invoiceHeaderDetails = invoiceHeaderHelper.saveInvoiceHeaderDetails(invoiceHeaderDetails);

			invoiceHeaderRquest.setRespCode(Constant.SUCCESS_CODE);
			invoiceHeaderRquest.setRespMesg("Successfully Register");
			return invoiceHeaderRquest;
		} else {
			invoiceHeader = invoiceHeaderHelper.getUpdatedInvoiceHeaderDetailsByReqObj(invoiceHeaderRquest,invoiceHeader);
			invoiceHeader = invoiceHeaderHelper.updateInvoiceHeaderDetails(invoiceHeader);

			invoiceHeaderRquest.setRespCode(Constant.SUCCESS_CODE);
			invoiceHeaderRquest.setRespMesg("Update Successfully");
			return invoiceHeaderRquest;
		}
	}

	public List<InvoiceHeaderDetails> getApplicationHeaderDetails(Request<InvoiceHeaderRequestObject> applicationRequestObject) {
		InvoiceHeaderRequestObject invoiceHeaderRequest = applicationRequestObject.getPayload();
		List<InvoiceHeaderDetails> invoiceHeaderDetails = invoiceHeaderHelper.getInvoiceHeaderDetailsBySuperadminId(invoiceHeaderRequest);
		return invoiceHeaderDetails;

	}

}
