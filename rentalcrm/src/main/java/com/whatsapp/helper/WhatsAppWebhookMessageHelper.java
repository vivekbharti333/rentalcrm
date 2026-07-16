package com.whatsapp.helper;


import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.datfusrental.constant.Constant;
import com.datfusrental.dao.WhatsAppMessageDao;
import com.datfusrental.entities.WhatsAppMessage;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.whatsapp.request.WhatsAppMessageRequestObject;
import com.fasterxml.jackson.databind.JsonNode;


@Component
public class WhatsAppWebhookMessageHelper {

	@Autowired
	private WhatsAppMessageDao whatsAppMessageDao;
    
    
//    public WhatsAppRequestObject parseMessage(JsonNode value, JsonNode messageNode, String rawJson) {
//
//        WhatsAppRequestObject req = new WhatsAppRequestObject();
//
//        String type = messageNode.path("type").asText();
//
//        req.setMessageType(type);
//        req.setMessageId(messageNode.path("id").asText());
//        req.setWaId(messageNode.path("from").asText());
//        req.setMessageTimestamp(messageNode.path("timestamp").asLong());
//
//        // 👤 Name
//        req.setUserName(value.path("contacts")
//                .get(0)
//                .path("profile")
//                .path("name")
//                .asText());
//
//        // 🔁 Context
//        req.setContextMessageId(messageNode.path("context").path("id").asText(null));
//
//        // 🧾 Raw JSON
//        req.setRawJson(rawJson);
//
//        // 📌 Type Handling
//        if ("text".equals(type)) {
//            req.setMessageText(messageNode.path("text").path("body").asText());
//        }
//
//        if ("image".equals(type)) {
//            JsonNode img = messageNode.path("image");
//            req.setMediaId(img.path("id").asText());
//            req.setMimeType(img.path("mime_type").asText());
//        }
//
//        if ("document".equals(type)) {
//            JsonNode doc = messageNode.path("document");
//            req.setMediaId(doc.path("id").asText());
//            req.setMimeType(doc.path("mime_type").asText());
//
//            String fileName = doc.path("filename").asText();
//            fileName = fileName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
//            req.setFileName(fileName);
//        }
//
//        req.setDirection("INCOMING");
//        req.setStatus("RECEIVED");
//
//        return req;
//    }
	
	public WhatsAppMessageRequestObject parseMessage(JsonNode value, JsonNode messageNode, String rawJson) {

		WhatsAppMessageRequestObject req = new WhatsAppMessageRequestObject();

	    String type = messageNode.path("type").asText();

	    // 📦 Message basics
	    req.setMessageType(type);
	    req.setMessageId(messageNode.path("id").asText());
	    req.setWaId(messageNode.path("from").asText());
	    req.setMessageTimestamp(messageNode.path("timestamp").asLong());

	    // 👤 Name
	    req.setUserName(value.path("contacts")
	            .get(0)
	            .path("profile")
	            .path("name")
	            .asText());

	    // 🔁 Context (reply)
	    req.setContextMessageId(messageNode.path("context").path("id").asText(null));

	    // 🧾 Raw JSON
	    req.setRawJson(rawJson);

	    // 🆕 ✅ BUSINESS INFO (VERY IMPORTANT)
	    JsonNode metadata = value.path("metadata");

	    req.setPhoneNumberId(metadata.path("phone_number_id").asText());
	    req.setMessageTo(metadata.path("display_phone_number").asText());

	    // 📌 Type Handling
	    if ("text".equals(type)) {
	        req.setMessageText(messageNode.path("text").path("body").asText());
	    }

	    if ("image".equals(type)) {
	        JsonNode img = messageNode.path("image");
	        req.setMediaId(img.path("id").asText());
	        req.setMimeType(img.path("mime_type").asText());
	    }

	    if ("document".equals(type)) {
	        JsonNode doc = messageNode.path("document");
	        req.setMediaId(doc.path("id").asText());
	        req.setMimeType(doc.path("mime_type").asText());

	        String fileName = doc.path("filename").asText();
	        fileName = fileName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
	        req.setFileName(fileName);
	    }

	    // 📊 Defaults
	    req.setDirection("INCOMING");
	    req.setStatus("RECEIVED");

	    return req;
	}
	
	public void validateSmsTemplateRequest(WhatsAppMessageRequestObject whatsAppRequestObject) throws BizException {
		if (whatsAppRequestObject == null) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Bad Request Object Null");
		}
	}

	public WhatsAppMessage getWhatsAppMessageByReqObject(WhatsAppMessageRequestObject whatsAppRequest) {

	    WhatsAppMessage msg = new WhatsAppMessage();

	    // 👤 User
	    msg.setWaId(whatsAppRequest.getWaId());
	    msg.setUserName(whatsAppRequest.getUserName());

	    // 💬 Message Identity
	    msg.setMessageId(whatsAppRequest.getMessageId());
	    msg.setDirection(whatsAppRequest.getDirection()); // INCOMING / OUTGOING
	    msg.setMessageType(whatsAppRequest.getMessageType());

	    // 📝 Text
	    msg.setMessageText(whatsAppRequest.getMessageText());

	    // 📦 Template
	    msg.setTemplateName(whatsAppRequest.getTemplateName());
	    msg.setTemplateLanguage(whatsAppRequest.getTemplateLanguage());

	    // 📎 Media
	    msg.setMediaId(whatsAppRequest.getMediaId());
	    msg.setMimeType(whatsAppRequest.getMimeType());
	    msg.setFileName(whatsAppRequest.getFileName());
	    msg.setFilePath(whatsAppRequest.getFilePath());

	    // 📊 Status
	    msg.setStatus(whatsAppRequest.getStatus());
	    msg.setStatusTimestamp(whatsAppRequest.getStatusTimestamp());

	    // ⏱ Time
	    msg.setMessageTimestamp(whatsAppRequest.getMessageTimestamp());

	    // 🔁 Context (reply)
	    msg.setContextMessageId(whatsAppRequest.getContextMessageId());

	    // 🧾 Raw JSON
	    msg.setRawJson(whatsAppRequest.getRawJson());

	    // 🆕 BUSINESS IDENTIFICATION (IMPORTANT)
	    msg.setPhoneNumberId(whatsAppRequest.getPhoneNumberId());
	    msg.setMessageTo(whatsAppRequest.getMessageTo());

	    // direction default (safety)
	    if (msg.getDirection() == null) {
	        msg.setDirection("INCOMING");
	    }

	    return msg;
	}
	
	@Transactional
	public WhatsAppMessage saveWhatsAppMessage(WhatsAppMessage whatsAppMessage) {
		whatsAppMessageDao.persist(whatsAppMessage);
		return whatsAppMessage;
	}





	

}
