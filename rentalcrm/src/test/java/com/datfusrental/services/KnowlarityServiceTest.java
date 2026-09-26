package com.datfusrental.services;

import java.lang.reflect.Field;
import junit.framework.TestCase;
import com.datfusrental.dao.KnowlarityCallLogDao;
import com.datfusrental.entities.KnowlarityCallLog;

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

        // No call UUID avoids a database lookup; outbound calls never create leads.
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
    }
}
