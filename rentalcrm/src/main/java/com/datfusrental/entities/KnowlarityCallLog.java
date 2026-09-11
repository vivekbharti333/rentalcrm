package com.datfusrental.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Entity
@Table(name = "knowlarity_call_log")
@Data
public class KnowlarityCallLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "call_uuid")
	private String uuid;

	@Column(name = "event")
	private String event;

	@Column(name = "call_direction")
	private String callDirection;

	@Column(name = "business_call_type")
	private String businessCallType;

	@Column(name = "customer_number")
	private String customerNumber;

	@Column(name = "agent_number")
	private String agentNumber;

	@Column(name = "knowlarity_number")
	private String knowlarityNumber;

	@Column(name = "call_recording")
	private String callRecording;

	@Column(name = "lead_created")
	private Boolean leadCreated;

	@Lob
	@Column(name = "raw_payload")
	private String rawPayload;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at")
	private Date createdAt;
}
