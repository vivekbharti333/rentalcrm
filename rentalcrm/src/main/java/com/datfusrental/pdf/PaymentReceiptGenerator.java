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
public class PaymentReceiptGenerator {

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
				throw new IllegalStateException("Receipt template not found: " + templatePath);
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

	private void validateNoUnresolvedPlaceholders(String html, String templatePath) {

		int start = html.indexOf("{{");

		if (start < 0) {
			return;
		}

		int end = html.indexOf("}}", start);

		String placeholder = end >= 0 ? html.substring(start, end + 2) : html.substring(start);

		throw new IllegalStateException("Unresolved placeholder " + placeholder + " in template: " + templatePath);
	}

	private String selectTemplate(LeadDetails leadDetails) {

		String superCategory = leadDetails.getSuperCategory();

		boolean needGstInvoice = Boolean.TRUE.equals(leadDetails.getNeedGstInvoice());

		if ("ACTIVITY".equalsIgnoreCase(superCategory)) {

			if (needGstInvoice) {
				return "/templates/" + "activity-payment-receipt-with-gst.html";
			}

			return "/templates/" + "activity-payment-receipt-without-gst.html";
		}

		if ("CAR".equalsIgnoreCase(superCategory) || "BIKE".equalsIgnoreCase(superCategory)) {

			if (needGstInvoice) {
				return "/templates/" + "vehicle-payment-receipt-with-gst.html";
			}

			return "/templates/" + "vehicle-payment-receipt-without-gst.html";
		}

		/*
		 * Default template for any other category.
		 */
		if (needGstInvoice) {
			return "/templates/" + "vehicle-payment-receipt-with-gst.html";
		}

		return "/templates/" + "vehicle-payment-receipt-without-gst.html";
	}

	private String replaceAllVariables(String html, LeadDetails leadDetails) {

		boolean needGstInvoice = Boolean.TRUE.equals(leadDetails.getNeedGstInvoice());

		double cgst = needGstInvoice ? leadDetails.getGstAmount() / 2.0 : 0;

		double sgst = needGstInvoice ? leadDetails.getGstAmount() / 2.0 : 0;

		long taxableAmount = leadDetails.getTotalAmount() - leadDetails.getGstAmount();

		if (taxableAmount < 0) {
			taxableAmount = 0;
		}

		long pickupAmount = leadDetails.getBalanceAmount() + leadDetails.getSecurityAmount();

		String description = firstNotBlank(leadDetails.getPseudoName(), leadDetails.getSubCategory(),
				leadDetails.getCategory(), leadDetails.getCategoryTypeName());

		String pickupLocation = firstNotBlank(leadDetails.getPickupPoint(), leadDetails.getPickupHub(),
				leadDetails.getActivityLocation());

		String dropLocation = firstNotBlank(leadDetails.getDropPoint(), leadDetails.getDropHub(),
				leadDetails.getActivityLocation());

		String boardingLocation = firstNotBlank(leadDetails.getPickupPoint(), leadDetails.getPickupHub(),
				leadDetails.getActivityLocation());

		String customerPhone = joinPhone(leadDetails.getCountryDialCode(), leadDetails.getCustomerMobile());

		String duration = leadDetails.getTotalDays() + " day(s)";

		String receiptFileName = firstNotBlank(leadDetails.getCustomeName(), "Receipt") + "_"
				+ formatFileDate(leadDetails.getCreatedAt());

		/*
		 * Company information. Change these values to your actual company details.
		 */
		String companyName = firstNotBlank(leadDetails.getCompanyName(), "MYRAAN RENTALS AND ADVENTURES PVT. LTD.");

		String companyId = "U52291GA2024OPC016682";

		String companyAddress = "Unit No. 71/195/A, Sattari, Ward No. 7, " + "Hathwada, Near Marathi School, "
				+ "Valpoi, North Goa, Goa - 403506";

		String companyTaxId = "AASCM2597H";

		String companyPhone = "+91 88283 75421";

		String companyEmail = "sales@myraanrentals.com";

		String companyGstin = "30AASCM2597H1Z5";

		/*
		 * Page information.
		 */
		html = replaceValue(html, "{{printedDateTime}}", formatDateTime(new Date()));

		html = replaceValue(html, "{{receiptFileName}}", receiptFileName);

		/*
		 * Company information.
		 */
		html = replaceValue(html, "{{companyName}}", companyName);

		html = replaceValue(html, "{{companyId}}", companyId);

		html = replaceValue(html, "{{companyAddress}}", companyAddress);

		html = replaceValue(html, "{{companyTaxId}}", companyTaxId);

		html = replaceValue(html, "{{companyPhone}}", companyPhone);

		html = replaceValue(html, "{{companyEmail}}", companyEmail);

		html = replaceValue(html, "{{companyGstin}}", companyGstin);

		/*
		 * Invoice information.
		 */
		html = replaceValue(html, "{{bookingId}}", leadDetails.getBookingId());

		html = replaceValue(html, "{{invoiceNumber}}", leadDetails.getBookingId());

		html = replaceValue(html, "{{invoiceDate}}", formatDate(leadDetails.getCreatedAt()));

		html = replaceValue(html, "{{invoiceDateTime}}", formatDateTime(leadDetails.getCreatedAt()));

		html = replaceValue(html, "{{paymentTerms}}", leadDetails.getPaymentType());

		html = replaceValue(html, "{{dueDate}}", formatDate(leadDetails.getPickupDateTime()));

		html = replaceValue(html, "{{placeOfSupply}}", leadDetails.getActivityLocation());

		html = replaceValue(html, "{{status}}", leadDetails.getStatus());

		/*
		 * Customer information.
		 */
		html = replaceValue(html, "{{customerName}}", leadDetails.getCustomeName());

		html = replaceValue(html, "{{customerPhone}}", customerPhone);

		html = replaceValue(html, "{{customerCompanyName}}", leadDetails.getCustomerCompanyName());

		html = replaceValue(html, "{{customerCompanyGST}}", leadDetails.getCustomerCompanyGST());

		html = replaceValue(html, "{{customerAddress}}", leadDetails.getCustomerCompanyAddress());

		/*
		 * Vehicle schedule.
		 */
		html = replaceValue(html, "{{pickupDateTime}}", formatDateTime(leadDetails.getPickupDateTime()));

		html = replaceValue(html, "{{pickupLocation}}", pickupLocation);

		html = replaceValue(html, "{{dropDateTime}}", formatDateTime(leadDetails.getDropDateTime()));

		html = replaceValue(html, "{{dropLocation}}", dropLocation);

		/*
		 * Activity schedule.
		 */
		html = replaceValue(html, "{{activityDateTime}}", formatDateTime(leadDetails.getPickupDateTime()));

		html = replaceValue(html, "{{boardingLocation}}", boardingLocation);

		/*
		 * Description and quantities.
		 */
		html = replaceValue(html, "{{description}}", description);

		html = replaceValue(html, "{{quantity}}", leadDetails.getQuantity());

		html = replaceValue(html, "{{adultQuantity}}", leadDetails.getQuantity());

		html = replaceValue(html, "{{kidQuantity}}", leadDetails.getKidQuantity());

		html = replaceValue(html, "{{infantQuantity}}", leadDetails.getInfantQuantity());

		html = replaceValue(html, "{{duration}}", duration);

		/*
		 * Rates and charges.
		 */
		html = replaceValue(html, "{{perDayRent}}", formatAmount(leadDetails.getCompanyRate()));

		html = replaceValue(html, "{{adultRate}}", formatAmount(leadDetails.getCompanyRate()));

		html = replaceValue(html, "{{kidRate}}", formatAmount(leadDetails.getCompanyRateForKids()));

		html = replaceValue(html, "{{pickupDropCharge}}", formatAmount(leadDetails.getDeliveryAmountToCompany()));

		html = replaceValue(html, "{{discount}}", formatAmount(leadDetails.getDiscount()));

		html = replaceValue(html, "{{taxableAmount}}", formatAmount(taxableAmount));

		/*
		 * GST values.
		 */
		html = replaceValue(html, "{{cgst}}", formatAmount(cgst));

		html = replaceValue(html, "{{sgst}}", formatAmount(sgst));

		html = replaceValue(html, "{{gstAmount}}", formatAmount(leadDetails.getGstAmount()));

		/*
		 * Total amounts.
		 */
		html = replaceValue(html, "{{totalAmount}}", formatAmount(leadDetails.getTotalAmount()));

		html = replaceValue(html, "{{advancePaid}}", formatAmount(leadDetails.getBookingAmount()));

		html = replaceValue(html, "{{balanceDue}}", formatAmount(leadDetails.getBalanceAmount()));

		html = replaceValue(html, "{{securityAmount}}", formatAmount(leadDetails.getSecurityAmount()));

		html = replaceValue(html, "{{pickupAmount}}", formatAmount(pickupAmount));

		return html;
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

	private String formatFileDate(Date date) {

		if (date == null) {
			return "";
		}

		return new SimpleDateFormat("ddMMyyyy").format(date);
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