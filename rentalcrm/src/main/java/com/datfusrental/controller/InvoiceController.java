package com.datfusrental.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.datfusrental.constant.Constant;
import com.datfusrental.entities.InvoiceHeaderDetails;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.enums.Status;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.InvoiceHeaderHelper;
import com.datfusrental.helper.LeadHelper;
import com.datfusrental.invoice.ItextInvoice;
import com.datfusrental.object.request.InvoiceHeaderRequestObject;
import com.datfusrental.object.request.Request;
import com.datfusrental.object.response.GenricResponse;
import com.datfusrental.object.response.Response;
import com.datfusrental.services.InvoiceHeaderService;

@CrossOrigin(origins = "*")
@RestController
public class InvoiceController {

	@Autowired
	private InvoiceHeaderHelper invoiceHeaderHelper;

	@Autowired
	private InvoiceHeaderService invoiceHeaderService;

	@Autowired
	private LeadHelper leadHelper;

	@Autowired
	private ItextInvoice itextInvoice;

	@RequestMapping(path = "addUpdateInvoiceHeader", method = RequestMethod.POST)
	public Response<InvoiceHeaderRequestObject> addUpdateInvoiceHeader(
			@RequestBody Request<InvoiceHeaderRequestObject> invoiceHeaderRequestObject, HttpServletRequest request) {
		GenricResponse<InvoiceHeaderRequestObject> responseObj = new GenricResponse<InvoiceHeaderRequestObject>();
		try {
			InvoiceHeaderRequestObject responce = invoiceHeaderService
					.addUpdateInvoiceHeader(invoiceHeaderRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/paymentreceipt/{reffNo}", method = RequestMethod.GET)
	public Object paymentreceipt(@PathVariable String reffNo) throws Exception {
		LeadDetails leadDetails = leadHelper.getLeadDetailsByBookingId(reffNo);

		Response respObj = new Response();

		if (leadDetails != null) {

			InvoiceHeaderDetails invoiceHeader = invoiceHeaderHelper.getInvoiceHeaderDetailsBySuperadminId(leadDetails.getSuperadminId());
			if (invoiceHeader != null) {

				if (!leadDetails.getStatus().equalsIgnoreCase(Status.INACTIVE.name())) {

					ByteArrayOutputStream pdfStream = itextInvoice.invoice(leadDetails, invoiceHeader);

					HttpHeaders headers = new HttpHeaders();
					headers.setContentType(MediaType.APPLICATION_PDF);
					headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
					headers.add("Pragma", "no-cache");
					headers.add("Expires", "0");
					headers.setContentLength(pdfStream.size());

//	        String fileName = invoiceHeader.getCompanyFirstName()+"-invoice.pdf";
					headers.setContentDispositionFormData("attachment", "invoice.pdf");

					InputStreamResource isr = new InputStreamResource(new ByteArrayInputStream(pdfStream.toByteArray()));

					return new ResponseEntity<>(isr, headers, HttpStatus.OK);
				} else {
					respObj.setResponseCode(401);
					respObj.setResponseMessage("Cancelled request. Please contact admin for details.");
					return respObj;
				}
			} else {
				respObj.setResponseCode(401);
				respObj.setResponseMessage("Please add invoice header.");
				return respObj;
			}
		} else {
			// Handle the case when donationDetails is null by returning an error view
			respObj.setResponseCode(404);
			respObj.setResponseMessage("Invalid request. Please contact admin for details.");
			return respObj;
		}
	}

}
