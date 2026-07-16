package com.whatsapp.services;


import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datfusrental.entities.WhatsAppMessage;
import com.datfusrental.whatsapp.request.WhatsAppMessageRequestObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatsapp.helper.WhatsAppWebhookMessageHelper;



@Service
public class WhatsAppWebhookMessageService {
	
	@Autowired
	private WhatsAppWebhookMessageHelper whatsAppWebhookMessageHelper;

	@Transactional
	public void processWebhook(String json) {

	    try {
	        ObjectMapper mapper = new ObjectMapper();
	        JsonNode root = mapper.readTree(json);

	        JsonNode value = root.path("entry")
	                .get(0)
	                .path("changes")
	                .get(0)
	                .path("value");

	        	// 🔹 Handle Messages
	        if (value.has("messages")) {

	            JsonNode messageNode = value.path("messages").get(0);

	            // Convert to Request Object
	            WhatsAppMessageRequestObject req = whatsAppWebhookMessageHelper.parseMessage(value, messageNode, json);
	            // Save
	            WhatsAppMessage whatsAppMessage = whatsAppWebhookMessageHelper.getWhatsAppMessageByReqObject(req);
	            whatsAppMessage = whatsAppWebhookMessageHelper.saveWhatsAppMessage(whatsAppMessage);
	            
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}  
	
	

}
