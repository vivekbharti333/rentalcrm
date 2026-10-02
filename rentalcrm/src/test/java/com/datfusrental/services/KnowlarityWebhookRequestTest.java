package com.datfusrental.services;

import junit.framework.TestCase;
import com.datfusrental.object.request.KnowlarityWebhookRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KnowlarityWebhookRequestTest extends TestCase {
    public void testParsesSuppliedWebhookFields() throws Exception {
        String json = "{\"call_date\":\"2026-09-26\",\"call_time\":\"14:30:00\","
                + "\"caller_number\":\"9876543210\",\"call_direction\":\"Inbound\","
                + "\"called_number\":\"08012345678\",\"call_status\":\"Answered\","
                + "\"agent_number\":\"9999999999\",\"call_transfer_status\":\"Transferred\","
                + "\"caller_duration\":42,\"recording_url\":\"https://example.com/call.mp3\","
                + "\"call_uuid\":\"call-123\",\"hangup_cause\":\"Normal\","
                + "\"menu_extension\":\"101\"}";
        KnowlarityWebhookRequest request = new ObjectMapper().readValue(json, KnowlarityWebhookRequest.class);
        assertEquals("2026-09-26", request.getCallDate());
        assertEquals("14:30:00", request.getCallTime());
        assertEquals("9876543210", request.getCallerNumber());
        assertEquals("Inbound", request.getCallDirection());
        assertEquals("08012345678", request.getCalledNumber());
        assertEquals("Answered", request.getCallStatus());
        assertEquals("9999999999", request.getAgentNumber());
        assertEquals("Transferred", request.getCallTransferStatus());
        assertEquals("42", request.getCallerDuration());
        assertEquals("https://example.com/call.mp3", request.getRecordingUrl());
        assertEquals("call-123", request.getCallUuid());
        assertEquals("Normal", request.getHangupCause());
        assertEquals("101", request.getMenuExtension());
    }

    public void testMenuExtensionMayBeAbsent() throws Exception {
        KnowlarityWebhookRequest request = new ObjectMapper().readValue(
                "{\"call_uuid\":\"call-456\",\"caller_number\":\"9876543210\"}",
                KnowlarityWebhookRequest.class);
        assertNull(request.getMenuExtension());
    }

    public void testParsesAllCountryDialCodes() throws Exception {
        KnowlarityWebhookRequest request = new ObjectMapper().readValue(
                "{\"caller_country_dial_code\":\"+91\",\"called_country_dial_code\":\"+91\","
                + "\"agent_country_dial_code\":\"+91\"}", KnowlarityWebhookRequest.class);
        assertEquals("+91", request.getCallerCountryDialCode());
        assertEquals("+91", request.getCalledCountryDialCode());
        assertEquals("+91", request.getAgentCountryDialCode());
    }
}
