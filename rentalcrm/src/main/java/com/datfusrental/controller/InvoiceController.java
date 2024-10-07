package com.datfusrental.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.datfusrental.constant.Constant;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.enums.Status;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.LeadHelper;
import com.datfusrental.invoice.ItextInvoice;
import com.datfusrental.object.request.DashboardRequestObject;
import com.datfusrental.object.request.Request;
import com.datfusrental.object.response.GenricResponse;
import com.datfusrental.object.response.Response;
import com.datfusrental.services.DashboardService;

@CrossOrigin(origins = "*")
@RestController
public class InvoiceController {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private LeadHelper leadHelper;

	@Autowired
	private ItextInvoice itextInvoice;

	@RequestMapping(value = "/donationinvoice/{reffNo}", method = RequestMethod.GET)
	public Object donationinvoice(@PathVariable String reffNo) throws Exception {
		LeadDetails leadDetails = leadHelper.getLeadDetailsByBookingId(reffNo);

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
				ModelAndView modelAndView = new ModelAndView("message");
				modelAndView.addObject("message", "Cancelled request. Please contact admin for details.");
				modelAndView.setViewName("message");
				return modelAndView;
			}
		} else {
			// Handle the case when donationDetails is null by returning an error view
			ModelAndView modelAndView = new ModelAndView("message");
			modelAndView.addObject("message", "Invalid request. Please contact admin for details.");
			modelAndView.setViewName("message");
			return modelAndView;
		}
	}

}
