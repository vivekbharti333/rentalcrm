package com.datfusrental.services;

import java.text.SimpleDateFormat;
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
import com.datfusrental.helper.LeadHelper;
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
	private LeadHelper leadHelper;

	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Processes a Knowlarity notification (Streaming API telephony event or CDR).
	 * 1. Parses the raw JSON payload.
	 * 2. Ignores duplicates (same uuid + event).
	 * 3. Persists the call notification in knowlarity_call_log.
	 * 4. For inbound calls with a caller number, auto-creates a CRM lead (enquiry)
	 *    if no lead exists yet for that mobile number.
	 *
	 * @return the saved log entry, or null when the notification was a duplicate
	 */
	@Transactional
	public KnowlarityCallLog processWebhook(String rawBody) throws Exception {

		KnowlarityWebhookRequest notification = objectMapper.readValue(rawBody, KnowlarityWebhookRequest.class);

		String uuid = StringUtils.defaultString(notification.getUuid());
		String event = StringUtils.defaultString(notification.getEvent());
		if (StringUtils.isBlank(event)) {
			event = StringUtils.defaultString(notification.getType()); // CDR events carry type=CDR
		}

		// 1) Deduplicate - Knowlarity may retry/re-send the same event
		if (StringUtils.isNotBlank(uuid) && isDuplicateNotification(uuid, event)) {
			logger.info("Duplicate Knowlarity notification ignored. uuid=" + uuid + ", event=" + event);
			return null;
		}

		// 2) Persist the raw notification
		KnowlarityCallLog callLog = new KnowlarityCallLog();
		callLog.setUuid(uuid);
		callLog.setEvent(event);
		callLog.setCallDirection(StringUtils.defaultString(notification.getCallDirection()));
		callLog.setBusinessCallType(StringUtils.defaultString(notification.getBusinessCallType()));
		callLog.setCustomerNumber(StringUtils.defaultString(notification.getCustomerNumber()));
		callLog.setAgentNumber(StringUtils.defaultString(notification.getAgentNumber()));
		callLog.setKnowlarityNumber(StringUtils.defaultString(notification.getKnowlarityNumber()));
		callLog.setCallRecording(StringUtils.defaultString(notification.getCallRecording()));
		callLog.setLeadCreated(false);
		callLog.setRawPayload(rawBody);
		callLog.setCreatedAt(new Date());
		knowlarityCallLogDao.persist(callLog);

		// 3) Auto-create a lead for inbound calls with a caller number
		String callerNumber = extractCallerNumber(notification);
		if (StringUtils.isNotBlank(callerNumber) && isInboundCall(notification)
				&& !isLeadExistsByMobile(callerNumber)) {
			try {
				createEnquiryLead(callerNumber, notification, callLog);
				callLog.setLeadCreated(true);
				knowlarityCallLogDao.update(callLog);
			} catch (Exception e) {
				// Lead creation must never break the webhook acknowledgement
				logger.error("Failed to auto-create lead for caller " + callerNumber, e);
			}
		}

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
		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createdAt")));

		return knowlarityCallLogDao.getSession().createQuery(criteriaQuery).getResultList();
	}

	// -------------------------------------------------------------------------
	// Private helpers
	// -------------------------------------------------------------------------

	private boolean isDuplicateNotification(String uuid, String event) {
		CriteriaBuilder criteriaBuilder = knowlarityCallLogDao.getSession().getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<KnowlarityCallLog> root = criteriaQuery.from(KnowlarityCallLog.class);

		Predicate uuidPredicate = criteriaBuilder.equal(root.get("uuid"), uuid);
		Predicate eventPredicate = criteriaBuilder.equal(root.get("event"), event);
		criteriaQuery.where(criteriaBuilder.and(uuidPredicate, eventPredicate));
		criteriaQuery.select(criteriaBuilder.count(root));

		Long count = knowlarityCallLogDao.getSession().createQuery(criteriaQuery).uniqueResult();
		return count != null && count > 0;
	}

	private String extractCallerNumber(KnowlarityWebhookRequest notification) {
		// Telephony events carry customer_number; CDR carries caller_id for incoming calls
		String caller = StringUtils.defaultIfBlank(notification.getCustomerNumber(), notification.getCallerId());
		return StringUtils.trimToNull(caller);
	}

	private boolean isInboundCall(KnowlarityWebhookRequest notification) {
		if (StringUtils.isNotBlank(notification.getCallDirection())) {
			return "Inbound".equalsIgnoreCase(notification.getCallDirection().trim());
		}
		// CDR event
		return "Incoming".equalsIgnoreCase(StringUtils.defaultString(notification.getCallType()).trim());
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
		leadDetails.setCountryDialCode(digitsOnly.startsWith("91") && digitsOnly.length() == 12 ? "+91" : "");
		leadDetails.setLeadOrigine("KNOWLARITY");
		leadDetails.setLeadType("CALL");
		leadDetails.setStatus("NEW");
		leadDetails.setSelfPdType("na");
		leadDetails.setRemarks("Inbound call on "
				+ StringUtils.defaultIfBlank(notification.getKnowlarityNumber(),
						StringUtils.defaultString(notification.getDispnumber()))
				+ " | uuid: " + StringUtils.defaultString(notification.getUuid()));
		leadDetails.setCreatedBy("KNOWLARITY_WEBHOOK");
		leadDetails.setCreatedByName("Knowlarity Webhook");
		leadDetails.setCreatedAt(new Date());
		leadDetails.setUpdatedAt(new Date());

		leadDetailsDao.persist(leadDetails);
		logger.info("Knowlarity inbound lead created. mobile=" + callerNumber + ", bookingId=" + bookingId
				+ ", logId=" + callLog.getId());
	}
}
