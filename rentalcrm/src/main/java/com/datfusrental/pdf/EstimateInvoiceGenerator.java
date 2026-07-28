package com.datfusrental.pdf;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datfusrental.entities.LeadDetails;
import com.datfusrental.helper.LeadHelper;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Service
public class EstimateInvoiceGenerator {

	@Autowired
	private LeadHelper leadHelper;

	public byte[] generatePdf(String bookingId) throws Exception {

		if (bookingId == null || bookingId.trim().isEmpty()) {
			throw new IllegalArgumentException("Booking ID is required");
		}

		LeadDetails leadDetails = leadHelper.getLeadDetailsByBookingId(bookingId);

		if (leadDetails == null) {
			throw new IllegalArgumentException("Booking not found for bookingId: " + bookingId);
		}

		URL templateDirectory = getClass().getResource("/templates/");

		if (templateDirectory == null) {
			throw new IllegalStateException("Templates directory not found");
		}

		String templatePath = selectTemplate(leadDetails);

		try (InputStream inputStream = getClass().getResourceAsStream(templatePath);

				ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

			if (inputStream == null) {
				throw new IllegalStateException("Estimate template not found: " + templatePath);
			}

			String html = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

			html = replaceAllVariables(html, leadDetails);

			validateNoUnresolvedPlaceholders(html, templatePath);

			PdfRendererBuilder builder = new PdfRendererBuilder();

			builder.useFastMode();

			builder.withHtmlContent(html, templateDirectory.toExternalForm());

			builder.toStream(outputStream);
			builder.run();

			return outputStream.toByteArray();
		}
	}

	private String selectTemplate(LeadDetails leadDetails) {

		String superCategory = leadDetails.getSuperCategory();

		boolean needGstInvoice = Boolean.TRUE.equals(leadDetails.getNeedGstInvoice());

		if ("ACTIVITY".equalsIgnoreCase(superCategory)) {

			return needGstInvoice ? "/templates/activity-estimate-with-gst.html"
					: "/templates/activity-estimate-without-gst.html";
		}

		return needGstInvoice ? "/templates/vehicle-estimate-with-gst.html"
				: "/templates/vehicle-estimate-without-gst.html";
	}

	private String replaceAllVariables(String html, LeadDetails leadDetails) {

		boolean needGstInvoice = Boolean.TRUE.equals(leadDetails.getNeedGstInvoice());

		double cgst = needGstInvoice ? leadDetails.getGstAmount() / 2.0 : 0;

		double sgst = needGstInvoice ? leadDetails.getGstAmount() / 2.0 : 0;

		long taxableAmount = leadDetails.getTotalAmount() - leadDetails.getGstAmount();

		if (taxableAmount < 0) {
			taxableAmount = 0;
		}

		long advancePaid = amountForInvoice(needGstInvoice, leadDetails.getBookingAmountWithGst(),
				leadDetails.getBookingAmount());

		long balanceDue = amountForInvoice(needGstInvoice, leadDetails.getBalanceAmountWithGst(),
				leadDetails.getBalanceAmount());

		String description = firstNotBlank(leadDetails.getPseudoName(), leadDetails.getSubCategory(),
				leadDetails.getCategory(), leadDetails.getCategoryTypeName());

		String pickupLocation = firstNotBlank(leadDetails.getPickupPoint(), leadDetails.getPickupHub(),
				leadDetails.getActivityLocation());

		String dropLocation = firstNotBlank(leadDetails.getDropPoint(), leadDetails.getDropHub(),
				leadDetails.getActivityLocation());

		String customerPhone = joinPhone(leadDetails.getCountryDialCode(), leadDetails.getCustomerMobile());

		String duration = leadDetails.getTotalDays() + " day(s)";

		String companyName = firstNotBlank(leadDetails.getCompanyName(), "ROME YOUR WAY LLP");
		html = replaceValue(html, "{{printedDateTime}}", formatDateTime(new Date()));

		html = replaceValue(html, "{{companyName}}", companyName);

		html = replaceValue(html, "{{companyId}}", "ACV-7256");

		html = replaceValue(html, "{{companyAddress}}", "Unit No. 71/195/A, Sattari, Ward No. 7, "
				+ "Hathwada, Near Marathi School, " + "Valpoi, North Goa, Goa - 403506");

		html = replaceValue(html, "{{companyTaxId}}", "ABNFR3071N");

		html = replaceValue(html, "{{companyPhone}}", "+91 77159 57719");

		html = replaceValue(html, "{{companyEmail}}", "sales@romeyourway.com");

		html = replaceValue(html, "{{companyGstin}}", "30ABNFR3071N1ZX");
		
		html = replaceValue(html, "{{estimateNumber}}", leadDetails.getBookingId());

		html = replaceValue(html, "{{estimateDate}}", formatDate(leadDetails.getCreatedAt()));

		html = replaceValue(html, "{{paymentTerms}}", leadDetails.getPaymentType());

		html = replaceValue(html, "{{dueDate}}", formatDate(leadDetails.getPickupDateTime()));

		html = replaceValue(html, "{{placeOfSupply}}",
				firstNotBlank(leadDetails.getActivityLocation(), pickupLocation));

		html = replaceValue(html, "{{status}}", firstNotBlank(leadDetails.getStatus(), "Pending"));

		html = replaceValue(html, "{{paymentStatus}}", firstNotBlank(leadDetails.getSecondStatus(), "N/A"));

		html = replaceValue(html, "{{customerName}}", leadDetails.getCustomeName());

		html = replaceValue(html, "{{customerPhone}}", customerPhone);

		html = replaceValue(html, "{{customerCompanyName}}", leadDetails.getCustomerCompanyName());

		html = replaceValue(html, "{{customerCompanyGST}}", leadDetails.getCustomerCompanyGST());

		html = replaceValue(html, "{{customerAddress}}", leadDetails.getCustomerCompanyAddress());

		html = replaceValue(html, "{{pickupDateTime}}", formatDateTime(leadDetails.getPickupDateTime()));

		html = replaceValue(html, "{{pickupLocation}}", pickupLocation);

		html = replaceValue(html, "{{dropDateTime}}", formatDateTime(leadDetails.getDropDateTime()));

		html = replaceValue(html, "{{dropLocation}}", dropLocation);

		html = replaceValue(html, "{{activityDateTime}}", formatDateTime(leadDetails.getPickupDateTime()));

		html = replaceValue(html, "{{boardingLocation}}", pickupLocation);

		html = replaceValue(html, "{{description}}", description);

		html = replaceValue(html, "{{quantity}}", leadDetails.getQuantity());

		html = replaceValue(html, "{{adultQuantity}}", leadDetails.getQuantity());

		html = replaceValue(html, "{{kidQuantity}}", leadDetails.getKidQuantity());

		html = replaceValue(html, "{{infantQuantity}}", leadDetails.getInfantQuantity());

		html = replaceValue(html, "{{duration}}", duration);

		html = replaceValue(html, "{{perDayRent}}", formatAmount(leadDetails.getCompanyRate()));

		html = replaceValue(html, "{{adultRate}}", formatAmount(leadDetails.getCompanyRate()));

		html = replaceValue(html, "{{kidRate}}", formatAmount(leadDetails.getCompanyRateForKids()));

		html = replaceValue(html, "{{pickupDropCharge}}", formatAmount(leadDetails.getDeliveryAmountToCompany()));

		html = replaceValue(html, "{{discount}}", formatAmount(leadDetails.getDiscount()));

		html = replaceValue(html, "{{taxableAmount}}", formatAmount(taxableAmount));

		html = replaceValue(html, "{{cgst}}", formatAmount(cgst));

		html = replaceValue(html, "{{sgst}}", formatAmount(sgst));

		html = replaceValue(html, "{{gstAmount}}", formatAmount(leadDetails.getGstAmount()));

		html = replaceValue(html, "{{totalAmount}}", formatAmount(leadDetails.getTotalAmount()));

		html = replaceValue(html, "{{advancePaid}}", formatAmount(advancePaid));

		html = replaceValue(html, "{{balanceDue}}", formatAmount(balanceDue));

		html = replaceValue(html, "{{securityAmount}}", formatAmount(leadDetails.getSecurityAmount()));

		/*
		 * Replace these with values from your company settings table when available.
		 */
		html = replaceValue(html, "{{upiId}}", "myraan7718@idfcabank");

		html = replaceValue(html, "{{paymentLink}}", "");

		html = replaceValue(html, "{{bankHolder}}", "MYRAAN RENTALS AND ADVENTURES GOA PRIVATE LIMITED");

		html = replaceValue(html, "{{accountNumber}}", "10232609571");

		html = replaceValue(html, "{{ifscCode}}", "IDFB0042406");

		html = replaceValue(html, "{{branchName}}", "VASCO BRANCH");

		html = replaceValue(html, "{{bankName}}", "IDFC FIRST");

		html = replaceValue(html, "{{accountType}}", "Current");

		html = replaceValue(html, "{{swiftCode}}", "IDFBINBBMUM");

		return html;
	}

	private long amountForInvoice(boolean needGstInvoice, Long gstAmount, long standardAmount) {

		if (needGstInvoice && gstAmount != null) {
			return gstAmount;
		}

		return standardAmount;
	}

	private void validateNoUnresolvedPlaceholders(String html, String templatePath) {

		int start = html.indexOf("{{");

		if (start < 0) {
			return;
		}

		int end = html.indexOf("}}", start);

		String placeholder = end >= 0 ? html.substring(start, end + 2) : html.substring(start);

		throw new IllegalStateException("Unresolved placeholder " + placeholder + " in template: " + templatePath);
	}

	private String replaceValue(String html, String placeholder, Object value) {

		String safeValue = value == null ? "" : String.valueOf(value);

		return html.replace(placeholder, escapeHtml(safeValue));
	}

	private String escapeHtml(String value) {

		if (value == null) {
			return "";
		}

		return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
				.replace("'", "&#39;");
	}

	private String formatDate(Date date) {

		if (date == null) {
			return "";
		}

		return new SimpleDateFormat("dd/MM/yyyy").format(date);
	}

	private String formatDateTime(Date date) {

		if (date == null) {
			return "";
		}

		return new SimpleDateFormat("dd/MM/yyyy, HH:mm").format(date);
	}

	private String formatAmount(double amount) {

		if (amount == Math.floor(amount)) {
			return String.format(Locale.US, "%.0f", amount);
		}

		return String.format(Locale.US, "%.2f", amount);
	}

	private String joinPhone(String countryCode, String mobileNumber) {

		String code = countryCode == null ? "" : countryCode.trim();

		String mobile = mobileNumber == null ? "" : mobileNumber.trim();

		if (code.isEmpty()) {
			return mobile;
		}

		if (mobile.isEmpty()) {
			return code;
		}

		return code + " " + mobile;
	}

	private String firstNotBlank(String... values) {

		if (values == null) {
			return "";
		}

		for (String value : values) {
			if (value != null && !value.trim().isEmpty()) {

				return value.trim();
			}
		}

		return "";
	}
}
