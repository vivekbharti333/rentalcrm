package com.datfusrental.services;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.datfusrental.dao.KnowlarityCallLogDao;
import com.datfusrental.dao.LeadDetailsDao;
import com.datfusrental.entities.KnowlarityCallLog;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.entities.User;
import com.datfusrental.helper.LeadHelper;
import com.datfusrental.helper.UserHelper;
import com.datfusrental.object.request.KnowlarityWebhookRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KnowlarityService {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private KnowlarityCallLogDao knowlarityCallLogDao;

	@Autowired
	private LeadDetailsDao leadDetailsDao;
	
	@Autowired
	private UserHelper userHelper;

	@Autowired
	private LeadHelper leadHelper;

	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Processes a Knowlarity call webhook.
	 * 1. Parses the raw JSON payload.
	 * 2. Ignores duplicates (same call_uuid + call_status).
	 * 3. Persists the call notification in knowlarity_call_log.
	 * 4. For inbound calls with a caller number, auto-creates a CRM lead (enquiry)
	 *    if no lead exists yet for that mobile number.
	 *
	 * @return the saved log entry, or null when the notification was a duplicate
	 */
	@Transactional
	public KnowlarityCallLog processWebhook(String rawBody) throws Exception {

		KnowlarityWebhookRequest notification = objectMapper.readValue(rawBody, KnowlarityWebhookRequest.class);

		String uuid = StringUtils.trimToEmpty(notification.getCallUuid());
		String status = StringUtils.trimToEmpty(notification.getCallStatus());

		// Knowlarity may retry the same status notification for a call.
		if (StringUtils.isNotBlank(uuid) && isDuplicateNotification(uuid, status)) {
			logger.info("Duplicate Knowlarity notification ignored. call_uuid=" + uuid + ", call_status=" + status);
			return null;
		}

		KnowlarityCallLog callLog = new KnowlarityCallLog();
		String countryDialCode = resolveCountryDialCode(notification.getCallerCountryDialCode(), notification.getCallerNumber());
		String callerNumber = extractPhoneNumber(notification.getCallerNumber(), countryDialCode);
		
		System.out.println("countryDialCode : "+countryDialCode);
		System.out.println("callerNumber : "+callerNumber);
		
		
		

		callLog.setUuid(uuid);
		callLog.setCallDate(StringUtils.trimToNull(notification.getCallDate()));
		callLog.setCallTime(StringUtils.trimToNull(notification.getCallTime()));
		callLog.setCallerNumber(callerNumber);
		callLog.setCountryDialCode(countryDialCode);
		callLog.setCallerCountryDialCode(countryDialCode);
		callLog.setCallDirection(StringUtils.trimToNull(notification.getCallDirection()));
		String calledCountryDialCode = resolveCountryDialCode(notification.getCalledCountryDialCode(), notification.getCalledNumber());
		callLog.setCalledCountryDialCode(calledCountryDialCode);
		callLog.setCalledNumber(StringUtils.isNotBlank(calledCountryDialCode)
				? extractPhoneNumber(notification.getCalledNumber(), calledCountryDialCode)
				: StringUtils.trimToNull(notification.getCalledNumber()));
		callLog.setCallStatus(status);
		callLog.setEvent(status); // Keep the legacy event column populated for existing reports.
		String agentCountryDialCode = resolveCountryDialCode(notification.getAgentCountryDialCode(), notification.getAgentNumber());
		callLog.setAgentCountryDialCode(agentCountryDialCode);
		callLog.setAgentNumber(extractPhoneNumber(notification.getAgentNumber(), agentCountryDialCode));
		User userDetails = StringUtils.isNotBlank(callLog.getAgentNumber())
				? userHelper.getUserDetailsByAlternateMobileNo(callLog.getAgentNumber()) : null;
		if (userDetails != null) {
			callLog.setLoginId(userDetails.getLoginId());
			callLog.setAgentName(userDetails.getFirstName()+" "+userDetails.getLastName());
		}
		callLog.setCallTransferStatus(StringUtils.trimToNull(notification.getCallTransferStatus()));
		callLog.setCallerDuration(StringUtils.trimToNull(notification.getCallerDuration()));
		callLog.setRecordingUrl(StringUtils.trimToNull(notification.getRecordingUrl()));
		callLog.setHangupCause(StringUtils.trimToNull(notification.getHangupCause()));
		callLog.setMenuExtension(StringUtils.trimToNull(notification.getMenuExtension()));
		callLog.setLeadCreated(false);
		callLog.setRawPayload(rawBody);
		callLog.setCreatedAt(new Date());
		knowlarityCallLogDao.persist(callLog);
		
		
		//Save Lead Details
//		LeadDetails leadDetails = new LeadDetails();
//		
//		Calendar calendar = Calendar.getInstance();
//		calendar.add(Calendar.DATE, 1);
//		calendar.set(Calendar.HOUR_OF_DAY, 10);
//		calendar.set(Calendar.MINUTE, 0);
//		calendar.set(Calendar.SECOND, 0);
//		calendar.set(Calendar.MILLISECOND, 0);
//		
//		leadDetails.setCustomeName("GUEST");
//		leadDetails.setCustomerMobile(callerNumber);
//		leadDetails.setCountryDialCode(countryDialCode);
//		leadDetails.setStatus("NEW");
//		leadDetails.setCreatedAt(new Date());
//		leadDetails.setPickupDateTime(new Date());
//		leadDetails.setDropDateTime(calendar.getTime());
//		leadDetails.setQuantity(1);
		
//		User userDetails = StringUtils.isNotBlank(callLog.getAgentNumber())
//				? userHelper.getUserDetailsByAlternateMobileNo(callLog.getAgentNumber()) : null;
//		if(userDetails != null) {
//			leadDetails.setCreatedBy(userDetails.getLoginId());
//			leadDetails.setAdminId(userDetails.getAdminId());
//			leadDetails.setTeamleaderId(userDetails.getTeamleaderId());
//			leadDetails.setCreatedByName(userDetails.getFirstName()+" "+userDetails.getLastName());
//		}
		
//		leadDetails.setSuperadminId("1234567890");
//		leadHelper.saveLeadDetails(leadDetails);
		

		// 3) Auto-create a lead for inbound calls with a caller number
//		if (StringUtils.isNotBlank(callerNumber) && isInboundCall(notification)
//				&& !isLeadExistsByMobile(callerNumber)) {
//			try {
//				createEnquiryLead(callerNumber, notification, callLog);
//				callLog.setLeadCreated(true);
//				knowlarityCallLogDao.update(callLog);
//			} catch (Exception e) {
//				// Lead creation must never break the webhook acknowledgement
//				logger.error("Failed to auto-create lead for caller " + callerNumber, e);
//			}
//		}

		return callLog;
	}

	/**
	 * Returns today's call notifications (for verification from the CRM panel).
	 */
	@Transactional
	public List<KnowlarityCallLog> getTodayCallLogs() {
		CriteriaBuilder criteriaBuilder = knowlarityCallLogDao.getSession().getCriteriaBuilder();
		CriteriaQuery<KnowlarityCallLog> criteriaQuery = criteriaBuilder.createQuery(KnowlarityCallLog.class);
		Root<KnowlarityCallLog> root = criteriaQuery.from(KnowlarityCallLog.class);

		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
		cal.set(java.util.Calendar.MINUTE, 0);
		cal.set(java.util.Calendar.SECOND, 0);
		cal.set(java.util.Calendar.MILLISECOND, 0);

		Predicate restriction = criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), cal.getTime());
		Predicate leadNotCreated = criteriaBuilder.isFalse(root.<Boolean>get("leadCreated"));
		criteriaQuery.where(criteriaBuilder.and(restriction, leadNotCreated));
		criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createdAt")));

		return knowlarityCallLogDao.getSession().createQuery(criteriaQuery).getResultList();
	}

	// -------------------------------------------------------------------------
	// Private helpers
	// -------------------------------------------------------------------------

	private boolean isDuplicateNotification(String uuid, String status) {
		CriteriaBuilder criteriaBuilder = knowlarityCallLogDao.getSession().getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<KnowlarityCallLog> root = criteriaQuery.from(KnowlarityCallLog.class);

		Predicate uuidPredicate = criteriaBuilder.equal(root.get("uuid"), uuid);
		Predicate statusPredicate = criteriaBuilder.equal(root.get("callStatus"), status);
		criteriaQuery.where(criteriaBuilder.and(uuidPredicate, statusPredicate));
		criteriaQuery.select(criteriaBuilder.count(root));

		Long count = knowlarityCallLogDao.getSession().createQuery(criteriaQuery).uniqueResult();
		return count != null && count > 0;
	}

	private String extractCallerNumber(KnowlarityWebhookRequest notification) {
		return extractPhoneNumber(notification.getCallerNumber());
	}

	private String resolveCountryDialCode(String suppliedCode, String rawNumber) {
		if (StringUtils.isBlank(suppliedCode)) {
			return extractCountryDialCode(rawNumber);
		}
		String code = suppliedCode.trim().replaceAll("(?i)%2b", "+");
		if (!code.matches("\\+?[1-9][0-9]{0,2}")) {
			throw new IllegalArgumentException("Invalid country dial code: " + suppliedCode);
		}
		code = code.startsWith("+") ? code : "+" + code;
		String inferred = extractCountryDialCode(rawNumber);
		if (StringUtils.isNotBlank(inferred) && !code.equals(inferred)) {
			throw new IllegalArgumentException("Country dial code does not match phone number");
		}
		return code;
	}

	private String extractPhoneNumber(String rawNumber, String countryCode) {
		String decoded = StringUtils.trimToEmpty(rawNumber).replaceAll("(?i)%2b", "+");
		if (StringUtils.isNotBlank(countryCode) && (decoded.startsWith("+") || decoded.startsWith("00"))) {
			String digits = phoneDigits(decoded);
			if (decoded.startsWith("00")) {
				digits = digits.substring(2);
			}
			String prefix = countryCode.substring(1);
			if (!digits.startsWith(prefix) || digits.length() <= prefix.length()) {
				throw new IllegalArgumentException("Country dial code does not match phone number");
			}
			return digits.substring(prefix.length());
		}
		return extractPhoneNumber(rawNumber);
	}

	private String extractPhoneNumber(String rawNumber) {
		String number = StringUtils.trimToNull(rawNumber);
		if (number == null) {
			return null;
		}
		String digits = phoneDigits(number);
		if (digits.length() == 14 && digits.startsWith("0091")) {
			return digits.substring(4);
		}
		if (digits.length() == 12 && digits.startsWith("91")) {
			return digits.substring(2);
		}
		// Normalize local numbers too, so formatting never reaches customer_mobile.
		if (digits.length() == 11 && digits.startsWith("0")) {
			return digits.substring(1);
		}
		return StringUtils.trimToNull(digits);
	}

	private String extractCountryDialCode(KnowlarityWebhookRequest notification) {
		return extractCountryDialCode(notification.getCallerNumber());
	}

	private String extractCountryDialCode(String rawNumber) {
		String number = phoneDigits(rawNumber);
		return (number.length() == 12 && number.startsWith("91"))
				|| (number.length() == 14 && number.startsWith("0091")) ? "+91" : "";
	}

	private String phoneDigits(String rawNumber) {
		// Knowlarity also sends the '+' prefix percent-encoded inside JSON strings.
		// Replace it before stripping punctuation, otherwise '%2b' contributes a '2'.
		return StringUtils.trimToEmpty(rawNumber)
				.replaceAll("(?i)%2b", "+").replaceAll("\\D", "");
	}

	private boolean isInboundCall(KnowlarityWebhookRequest notification) {
		String direction = StringUtils.trimToEmpty(notification.getCallDirection());
		return "Inbound".equalsIgnoreCase(direction) || "Incoming".equalsIgnoreCase(direction);
	}

	/**
	 * Matches a caller number against lead_details.customer_mobile ignoring '+',
	 * spaces and dashes, so "+919876543210" matches a stored "9876543210" and
	 * vice-versa.
	 */
	private boolean isLeadExistsByMobile(String callerNumber) {
		String digitsOnly = callerNumber.replaceAll("\\D", "");

		String hql = "from LeadDetails where replace(replace(replace(customerMobile, '+', ''), ' ', ''), '-', '') = :digitsOnly";
		List<LeadDetails> leads = leadDetailsDao.getSession().createQuery(hql, LeadDetails.class)
				.setParameter("digitsOnly", digitsOnly)
				.setMaxResults(1)
				.getResultList();
		return leads != null && !leads.isEmpty();
	}

	private void createEnquiryLead(String callerNumber, KnowlarityWebhookRequest notification, KnowlarityCallLog callLog) {
		String digitsOnly = callerNumber.replaceAll("\\D", "");
		String mobileSuffix = StringUtils.leftPad(StringUtils.right(digitsOnly, 4), 4, '0');
		String today = new SimpleDateFormat("ddMMyy").format(new Date());

		String bookingId = mobileSuffix + today;
		int suffix = 1;
		while (leadHelper.getLeadDetailsByBookingId(bookingId) != null) {
			bookingId = mobileSuffix + today + "/" + suffix++;
		}

		LeadDetails leadDetails = new LeadDetails();
		leadDetails.setBookingId(bookingId);
		leadDetails.setCustomeName("Knowlarity Caller");
		leadDetails.setCustomerMobile(callerNumber);
		leadDetails.setCountryDialCode(callLog.getCallerCountryDialCode());
		leadDetails.setLeadOrigine("KNOWLARITY");
		leadDetails.setLeadType("CALL");
		leadDetails.setStatus("NEW");
		leadDetails.setSelfPdType("na");
		leadDetails.setRemarks("Inbound call on "
				+ StringUtils.defaultString(notification.getCalledNumber())
				+ " | uuid: " + StringUtils.defaultString(notification.getCallUuid()));
		leadDetails.setCreatedBy("KNOWLARITY_WEBHOOK");
		leadDetails.setCreatedByName("Knowlarity Webhook");
		leadDetails.setCreatedAt(new Date());
		leadDetails.setUpdatedAt(new Date());

		leadDetailsDao.persist(leadDetails);
		logger.info("Knowlarity inbound lead created. mobile=" + callerNumber + ", bookingId=" + bookingId
				+ ", logId=" + callLog.getId());
	}
}
