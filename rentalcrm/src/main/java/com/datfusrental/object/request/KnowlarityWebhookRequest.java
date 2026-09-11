package com.datfusrental.object.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Knowlarity Notifications (Streaming API) / CDR payload.
 * All unknown fields are ignored so both telephony events and CDR events parse safely.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class KnowlarityWebhookRequest {

	// Telephony event fields
	@JsonProperty("uuid")
	private String uuid;

	@JsonProperty("event")
	private String event;

	@JsonProperty("call_direction")
	private String callDirection;

	@JsonProperty("business_call_type")
	private String businessCallType;

	@JsonProperty("customer_number")
	private String customerNumber;

	@JsonProperty("agent_number")
	private String agentNumber;

	@JsonProperty("knowlarity_number")
	private String knowlarityNumber;

	@JsonProperty("call_recording")
	private String callRecording;

	// CDR (Call Detail Record) fields
	@JsonProperty("Call_Type")
	private String callType;

	@JsonProperty("caller_id")
	private String callerId;

	@JsonProperty("destination")
	private String destination;

	@JsonProperty("start_time")
	private String startTime;

	@JsonProperty("end_time")
	private String endTime;

	@JsonProperty("call_duration")
	private String callDuration;

	@JsonProperty("dispnumber")
	private String dispnumber;

	@JsonProperty("type")
	private String type;

	// Internal response fields (not part of Knowlarity payload)
	private int respCode;
	private String respMesg;
}
