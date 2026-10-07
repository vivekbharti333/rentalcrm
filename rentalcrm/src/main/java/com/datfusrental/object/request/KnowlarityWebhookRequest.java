package com.datfusrental.object.request;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/** Fields supplied by the Knowlarity call webhook. */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class KnowlarityWebhookRequest {
    @JsonProperty("call_date")
    private String callDate;

    @JsonProperty("call_time")
    private String callTime;
    
    @JsonProperty("caller_country_dial_code")
	private String callerCountryDialCode;

    @JsonProperty("caller_number")
    private String callerNumber;

    @JsonProperty("call_direction")
    private String callDirection;

    @JsonProperty("called_country_dial_code")
	private String calledCountryDialCode;
    
    @JsonProperty("called_number")
    private String calledNumber;

    @JsonProperty("call_status")
    private String callStatus;
    
    @JsonProperty("agent_country_dial_code")
	private String agentCountryDialCode;

    @JsonProperty("agent_number")
    private String agentNumber;

    @JsonProperty("call_transfer_status")
    private String callTransferStatus;

    @JsonProperty("caller_duration")
    private String callerDuration;

    @JsonProperty("recording_url")
    private String recordingUrl;

    @JsonProperty("call_uuid")
    private String callUuid;

    @JsonProperty("hangup_cause")
    private String hangupCause;

    @JsonProperty("menu_extension")
    private String menuExtension;
    
    @JsonProperty("agent_name")
	private String agentName;
	
    @JsonProperty("login_id")
	private String loginId;

    // Response fields used by the existing webhook acknowledgement.
    private int respCode;
    private String respMesg;
}
