package com.whatsapp.controller;

import java.io.BufferedReader;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whatsapp.services.WhatsAppWebhookMessageService;


@CrossOrigin(origins = "*")
@RestController
public class WhatsAppWebhookMessageController {
	


	private static final Logger logger = LogManager.getLogger(WhatsAppWebhookMessageController.class);
	
	private static final String VERIFY_TOKEN = "romeyourway_webhook_7kj9zPpkLj_2026";

	@Autowired
	private WhatsAppWebhookMessageService whatsAppWebhookMessageService;

	
	@RequestMapping(path = "whatsAppWebhook", method = {RequestMethod.GET, RequestMethod.POST})
	public String handleWebhook(
	        HttpServletRequest request,
	        @RequestParam(value = "hub.mode", required = false) String mode,
	        @RequestParam(value = "hub.challenge", required = false) String challenge,
	        @RequestParam(value = "hub.verify_token", required = false) String token
	) {

	    logger.info("Webhook hit");

	    try {
	        // 🔹 Verification
	        if ("GET".equalsIgnoreCase(request.getMethod())) {
	            if ("subscribe".equals(mode) && VERIFY_TOKEN.equals(token)) {
	                return challenge;
	            }
	            return "Verification failed";
	        }

	        // 🔹 Read payload
	        BufferedReader reader = request.getReader();
	        StringBuilder payload = new StringBuilder();
	        String line;

	        while ((line = reader.readLine()) != null) {
	            payload.append(line);
	        }

	        String json = payload.toString();
	        logger.info("Incoming Webhook: {}"+ json);

	        // 🔹 Call service
	        whatsAppWebhookMessageService.processWebhook(json);

	        return "EVENT_RECEIVED";

	    } catch (Exception e) {
	        logger.error("Webhook error", e);
	    }

	    return "OK";
	}
	
	
}
