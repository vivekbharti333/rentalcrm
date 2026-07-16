package com.datfusrental.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
@Entity
@Table(name = "whatsapp_messages")
public class WhatsAppMessage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "phone_number_id")
	private String phoneNumberId;
	
	@Column(name = "message_from")
	private String messageFrom;
	
	@Column(name = "message_to")
	private String messageTo; 

	// 👤 User / Conversation
	@Column(name = "wa_id", nullable = false)
	private String waId;

	@Column(name = "user_name")
	private String userName;

	// 💬 Message Identity
	@Column(name = "message_id", unique = true)
	private String messageId;

	@Column(name = "direction", nullable = false)
	private String direction; // INCOMING / OUTGOING

	@Column(name = "message_type")
	private String messageType; // text, image, document, template

	// 📝 Text Content
	@Column(name = "message_text", columnDefinition = "TEXT")
	private String messageText;

	// 📦 Template Info
	@Column(name = "template_name")
	private String templateName;

	@Column(name = "template_language")
	private String templateLanguage;

	// 📎 Media Info
	@Column(name = "media_id")
	private String mediaId;

	@Column(name = "mime_type")
	private String mimeType;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "file_path")
	private String filePath;

	// 📊 Status Tracking
	@Column(name = "status")
	private String status; // SENT / DELIVERED / READ / FAILED

	@Column(name = "status_timestamp")
	private Long statusTimestamp;

	// ⏱ Message Time (WhatsApp timestamp)
	@Column(name = "message_timestamp")
	private Long messageTimestamp;

	// 🔁 Reply Context
	@Column(name = "context_message_id")
	private String contextMessageId;

	// 🧾 Raw JSON (debugging)
	@Column(name = "raw_json", columnDefinition = "TEXT")
	private String rawJson;

	// 📅 System timestamps
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

}
