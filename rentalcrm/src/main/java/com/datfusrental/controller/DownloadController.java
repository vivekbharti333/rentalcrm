package com.datfusrental.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import com.datfusrental.pdf.EstimateInvoiceGenerator;
import com.datfusrental.pdf.PaymentReceiptGenerator;

@CrossOrigin(origins = "*")
@RestController
public class DownloadController {

	private final PaymentReceiptGenerator paymentReceiptGenerator;
	private final EstimateInvoiceGenerator estimateInvoiceGenerator;

	public DownloadController(PaymentReceiptGenerator paymentReceiptGenerator,
			EstimateInvoiceGenerator estimateInvoiceGenerator) {

		this.paymentReceiptGenerator = paymentReceiptGenerator;
		this.estimateInvoiceGenerator = estimateInvoiceGenerator;
	}

	@GetMapping(value = "/downloadPaymentReceipt/**", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> downloadPaymentReceipt(HttpServletRequest request) throws Exception {

		String bookingId = extractBookingId(request, "/downloadPaymentReceipt/");

		byte[] pdf = paymentReceiptGenerator.generatePdf(bookingId);

		String filename = "Receipt-" + sanitizeFilename(bookingId) + ".pdf";

		return createPdfResponse(pdf, filename);
	}

	private String extractBookingId(HttpServletRequest request, String endpointPrefix) {

		String requestPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

		if (requestPath == null || !requestPath.startsWith(endpointPrefix)) {
			throw new IllegalArgumentException("Invalid download URL");
		}

		String bookingId = requestPath.substring(endpointPrefix.length());

		if (bookingId.isBlank()) {
			throw new IllegalArgumentException("Booking ID is required");
		}

		return bookingId;
	}

	@GetMapping(value = "/downloadEstimateInvoice/**", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> downloadEstimateInvoice(HttpServletRequest request) throws Exception {

		String bookingId = extractBookingId(request, "/downloadEstimateInvoice/");

		byte[] pdf = estimateInvoiceGenerator.generatePdf(bookingId);

		String filename = "Estimate-" + sanitizeFilename(bookingId) + ".pdf";

		return createPdfResponse(pdf, filename);
	}
	

	private ResponseEntity<byte[]> createPdfResponse(byte[] pdf, String filename) {

		ContentDisposition disposition = ContentDisposition.attachment().filename(filename).build();

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
				.contentType(MediaType.APPLICATION_PDF).contentLength(pdf.length).body(pdf);
	}

	private String sanitizeFilename(String value) {

		if (value == null || value.isBlank()) {
			return "document";
		}

		return value.replaceAll("[^a-zA-Z0-9_-]", "_");
	}
}
