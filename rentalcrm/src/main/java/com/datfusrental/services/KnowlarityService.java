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
		callLog.setUuid(uuid);
		callLog.setCallDate(StringUtils.trimToNull(notification.getCallDate()));
		callLog.setCallTime(StringUtils.trimToNull(notification.getCallTime()));
		callLog.setCallerNumber(StringUtils.trimToNull(notification.getCallerNumber()));
		callLog.setCallDirection(StringUtils.trimToNull(notification.getCallDirection()));
		callLog.setCalledNumber(StringUtils.trimToNull(notification.getCalledNumber()));
		callLog.setCallStatus(status);
		callLog.setEvent(status); // Keep the legacy event column populated for existing reports.
		callLog.setAgentNumber(StringUtils.trimToNull(notification.getAgentNumber()));
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
		LeadDetails leadDetails = new LeadDetails();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 10);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		leadDetails.setCustomeName("GUEST");
		leadDetails.setCustomerMobile(StringUtils.trimToNull(notification.getCallerNumber()));
		leadDetails.setStatus("NEW");
		leadDetails.setCreatedAt(new Date());
		leadDetails.setPickupDateTime(new Date());
		leadDetails.setDropDateTime(calendar.getTime());
		leadDetails.setQuantity(1);
		
		User userDetails = userHelper.getUserDetailsByLoginId(StringUtils.trimToNull(notification.getAgentNumber()));
		if(userDetails != null) {
			leadDetails.setCreatedBy(userDetails.getLoginId());
			leadDetails.setAdminId(userDetails.getAdminId());
			leadDetails.setTeamleaderId(userDetails.getTeamleaderId());
			leadDetails.setCreatedByName(userDetails.getFirstName()+" "+userDetails.getLastName());
		}
		
		leadDetails.setSuperadminId("1234567890");
		leadHelper.saveLeadDetails(leadDetails);
		

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
		return StringUtils.trimToNull(notification.getCallerNumber());
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
		leadDetails.setCountryDialCode(digitsOnly.startsWith("91") && digitsOnly.length() == 12 ? "+91" : "");
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
