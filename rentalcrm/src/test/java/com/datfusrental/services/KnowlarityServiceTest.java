package com.datfusrental.services;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import junit.framework.TestCase;
import com.datfusrental.dao.KnowlarityCallLogDao;
import com.datfusrental.entities.KnowlarityCallLog;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.entities.User;
import com.datfusrental.helper.LeadHelper;
import com.datfusrental.helper.UserHelper;
import com.datfusrental.object.request.KnowlarityWebhookRequest;

public class KnowlarityServiceTest extends TestCase {
    public void testStoresSuppliedCallFields() throws Exception {
        KnowlarityService service = new KnowlarityService();
        final KnowlarityCallLog[] persisted = new KnowlarityCallLog[1];
        KnowlarityCallLogDao dao = new KnowlarityCallLogDao() {
            @Override public KnowlarityCallLog persist(KnowlarityCallLog value) {
                persisted[0] = value;
                return value;
            }
        };
        Field field = KnowlarityService.class.getDeclaredField("knowlarityCallLogDao");
        field.setAccessible(true);
        field.set(service, dao);
        Field userHelperField = KnowlarityService.class.getDeclaredField("userHelper");
        userHelperField.setAccessible(true);
        userHelperField.set(service, new UserHelper() {
            @Override public User getUserDetailsByAlternateMobileNo(String number) { return null; }
        });
        final LeadDetails[] lead = new LeadDetails[1];
        Field leadHelperField = KnowlarityService.class.getDeclaredField("leadHelper");
        leadHelperField.setAccessible(true);
        leadHelperField.set(service, new LeadHelper() {
            @Override public LeadDetails saveLeadDetails(LeadDetails value) {
                lead[0] = value;
                return value;
            }
        });

        // No call UUID avoids a database lookup; outbound skips the inbound enquiry lookup.
        String json = "{\"call_date\":\"2026-09-26\",\"call_time\":\"14:30:00\","
                + "\"caller_number\":\"9876543210\",\"call_direction\":\"Outbound\","
                + "\"called_number\":\"08012345678\",\"call_status\":\"Answered\","
                + "\"agent_number\":\"9999999999\",\"call_transfer_status\":\"Transferred\","
                + "\"caller_duration\":42,\"recording_url\":\"https://example.com/call.mp3\","
                + "\"hangup_cause\":\"Normal\",\"menu_extension\":\"101\"}";
        KnowlarityCallLog saved = service.processWebhook(json);
        assertSame(saved, persisted[0]);
        assertEquals("2026-09-26", saved.getCallDate());
        assertEquals("14:30:00", saved.getCallTime());
        assertEquals("9876543210", saved.getCallerNumber());
        assertEquals("Outbound", saved.getCallDirection());
        assertEquals("08012345678", saved.getCalledNumber());
        assertEquals("Answered", saved.getCallStatus());
        assertEquals("Answered", saved.getEvent());
        assertEquals("9999999999", saved.getAgentNumber());
        assertEquals("Transferred", saved.getCallTransferStatus());
        assertEquals("42", saved.getCallerDuration());
        assertEquals("https://example.com/call.mp3", saved.getRecordingUrl());
        assertEquals("Normal", saved.getHangupCause());
        assertEquals("101", saved.getMenuExtension());
        assertFalse(saved.getLeadCreated());
        assertEquals("", saved.getCountryDialCode());
        assertEquals("", saved.getCallerCountryDialCode());
        assertEquals("", saved.getCalledCountryDialCode());
        assertEquals("", saved.getAgentCountryDialCode());
        KnowlarityCallLog international = service.processWebhook(json.replace("9876543210", "+919876543210"));
        assertEquals("9876543210", international.getCallerNumber());
        assertEquals("+91", international.getCountryDialCode());
        assertEquals("9876543210", lead[0].getCustomerMobile());
        assertEquals("+91", lead[0].getCountryDialCode());
        KnowlarityCallLog encoded = service.processWebhook(json.replace("9876543210", "%2b918105295871"));
        assertEquals("8105295871", encoded.getCallerNumber());
        assertEquals("+91", encoded.getCountryDialCode());
        assertEquals("8105295871", lead[0].getCustomerMobile());
        assertEquals("+91", lead[0].getCountryDialCode());
        KnowlarityCallLog bothEncoded = service.processWebhook(json
                .replace("9876543210", "%2b918105295871")
                .replace("08012345678", "%2b919513166378"));
        assertEquals("8105295871", bothEncoded.getCallerNumber());
        assertEquals("+91", bothEncoded.getCountryDialCode());
        assertEquals("+91", bothEncoded.getCallerCountryDialCode());
        assertEquals("9513166378", bothEncoded.getCalledNumber());
        assertEquals("+91", bothEncoded.getCalledCountryDialCode());
        assertEquals("8105295871", lead[0].getCustomerMobile());
        assertEquals("+91", lead[0].getCountryDialCode());
        assertEquals("9513166378", service.processWebhook(json
                .replace("08012345678", "+919513166378")).getCalledNumber());
        assertEquals("9513166378", service.processWebhook(json
                .replace("08012345678", "%2B919513166378")).getCalledNumber());
        for (String agent : new String[] { "%2b919999999999", "%2B919999999999", "+919999999999" }) {
            KnowlarityCallLog agentLog = service.processWebhook(json.replace("9999999999", agent));
            assertEquals("9999999999", agentLog.getAgentNumber());
            assertEquals("+91", agentLog.getAgentCountryDialCode());
        }
        KnowlarityCallLog noAgent = service.processWebhook(json.replace("9999999999", "False"));
        assertNull(noAgent.getAgentNumber());
        assertEquals("", noAgent.getAgentCountryDialCode());
        String explicitCodes = ",\"caller_country_dial_code\":\"+91\","
                + "\"called_country_dial_code\":\"%2b91\",\"agent_country_dial_code\":\"91\"}";
        KnowlarityCallLog explicit = service.processWebhook(json
                .replace("08012345678", "9513166378")
                .replace("}", explicitCodes));
        assertEquals("9876543210", explicit.getCallerNumber());
        assertEquals("9513166378", explicit.getCalledNumber());
        assertEquals("9999999999", explicit.getAgentNumber());
        assertEquals("+91", explicit.getCallerCountryDialCode());
        assertEquals("+91", explicit.getCalledCountryDialCode());
        assertEquals("+91", explicit.getAgentCountryDialCode());
        KnowlarityCallLog explicitEncoded = service.processWebhook(json
                .replace("9876543210", "%2b918105295871")
                .replace("08012345678", "%2b919513166378")
                .replace("9999999999", "%2b919999999999")
                .replace("}", explicitCodes));
        assertEquals("8105295871", explicitEncoded.getCallerNumber());
        assertEquals("9513166378", explicitEncoded.getCalledNumber());
        assertEquals("9999999999", explicitEncoded.getAgentNumber());
    }

    public void testCallerNumberFormats() throws Exception {
        assertSplit("+91 98765-43210", "9876543210", "+91");
        assertSplit("+919876543210", "9876543210", "+91");
        assertSplit("%2b918105295871", "8105295871", "+91");
        assertSplit("%2B918105295871", "8105295871", "+91");
        assertSplit("%2b919513166378", "9513166378", "+91");
        assertSplit("919876543210", "9876543210", "+91");
        assertSplit("00919876543210", "9876543210", "+91");
        assertSplit("0091 98765-43210", "9876543210", "+91");
        assertSplit("98765-43210", "9876543210", "");
        assertSplit("(98765) 43210", "9876543210", "");
        assertSplit("09876543210", "9876543210", "");
        assertSplit("9123456789", "9123456789", "");
        assertSplit("---", null, "");
        assertSplit(null, null, "");
        assertSplit(" ", null, "");
    }

    private void assertSplit(String input, String mobile, String code) throws Exception {
        KnowlarityWebhookRequest request = new KnowlarityWebhookRequest();
        request.setCallerNumber(input);
        KnowlarityService service = new KnowlarityService();
        Method numberMethod = KnowlarityService.class.getDeclaredMethod("extractCallerNumber", KnowlarityWebhookRequest.class);
        numberMethod.setAccessible(true);
        Method codeMethod = KnowlarityService.class.getDeclaredMethod("extractCountryDialCode", KnowlarityWebhookRequest.class);
        codeMethod.setAccessible(true);
        assertEquals(mobile, numberMethod.invoke(service, request));
        assertEquals(code, codeMethod.invoke(service, request));
    }
}
