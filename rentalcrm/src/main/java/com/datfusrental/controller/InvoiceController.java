package com.datfusrental.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.datfusrental.entities.LeadDetails;
import com.datfusrental.enums.Status;
import com.datfusrental.helper.LeadHelper;
import com.datfusrental.invoice.ItextInvoice;
import com.datfusrental.object.response.Response;

@CrossOrigin(origins = "*")
@RestController
public class InvoiceController {

	@Autowired
	private LeadHelper leadHelper;

	@Autowired
	private ItextInvoice itextInvoice;

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/paymentreceipt/{reffNo}", method = RequestMethod.GET)
	public Object paymentreceipt(@PathVariable String reffNo) throws Exception {
		LeadDetails leadDetails = leadHelper.getLeadDetailsByBookingId(reffNo);

		Response respObj = new Response();

		if (leadDetails != null) {
			if (!leadDetails.getStatus().equalsIgnoreCase(Status.INACTIVE.name())) {

				ByteArrayOutputStream pdfStream = itextInvoice.invoice(leadDetails);

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
			// Handle the case when donationDetails is null by returning an error view
			respObj.setResponseCode(404);
			respObj.setResponseMessage("Invalid request. Please contact admin for details.");
			return respObj;
		}
	}

}
