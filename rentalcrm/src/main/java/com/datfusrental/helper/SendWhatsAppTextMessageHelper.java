package com.datfusrental.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.datfusrental.constant.Constant;
import com.datfusrental.entities.WhatsAppMessage;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.response.WhatsAppMessageResponse;
import com.datfusrental.whatsapp.request.TemplateButtonVariableRequest;
import com.datfusrental.whatsapp.request.TemplateRequestObject;
import com.datfusrental.whatsapp.request.WhatsAppMessageRequestObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

@Component
public class SendWhatsAppTextMessageHelper {


	public void validateTemplateRequest(TemplateRequestObject templateRequestObject) throws BizException {
		if (templateRequestObject == null) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Bad Request Object Null");
		}
	}

	private static final OkHttpClient client = new OkHttpClient();
	

	public WhatsAppMessage getSendMessageDetailsByReqObj(WhatsAppMessageRequestObject whatsAppMessageRequest) throws Exception {
		
		WhatsAppMessage whatsAppMessage = new WhatsAppMessage();
		
		whatsAppMessage.setTemplateName(whatsAppMessageRequest.getTemplateName());
		whatsAppMessage.setTemplateLanguage(whatsAppMessageRequest.getTemplateLanguage());
		whatsAppMessage.setMessageFrom(whatsAppMessageRequest.getMessageFrom());
		whatsAppMessage.setMessageTo(whatsAppMessageRequest.getMessageTo());
		
		return whatsAppMessage;
	}

	public String getTextTemplateParameter(WhatsAppMessageRequestObject whatsAppMessageRequest) throws Exception {

		Map<String, Object> root = new HashMap<>();
		root.put("messaging_product", "whatsapp");
		root.put("to", whatsAppMessageRequest.getMessageTo());
		root.put("type", "template");

		// ---- Template ----
		Map<String, Object> template = new HashMap<>();
		template.put("name", whatsAppMessageRequest.getTemplateName());

		Map<String, String> language = new HashMap<>();
		language.put("code", whatsAppMessageRequest.getLanguage());
		template.put("language", language);

		// ---- Body Parameters ----
		List<Map<String, Object>> parameters = new ArrayList<>();

		if (whatsAppMessageRequest.getMsgBodyVariable() != null) {
			whatsAppMessageRequest.getMsgBodyVariable().forEach(var -> {
				Map<String, Object> param = new HashMap<>();
				param.put("type", "text");
				param.put("text", var.getBodyVariable());
				parameters.add(param);
			});
		}

		Map<String, Object> bodyComponent = new HashMap<>();
		bodyComponent.put("type", "body");
		bodyComponent.put("parameters", parameters);

		template.put("components", List.of(bodyComponent));
		root.put("template", template);

		return new ObjectMapper().writeValueAsString(root);
	}
	
	public String getTextTemplateParameterButton(WhatsAppMessageRequestObject whatsAppMessageRequest) throws Exception {

	    Map<String, Object> root = new HashMap<>();
	    root.put("messaging_product", "whatsapp");
	    root.put("to", whatsAppMessageRequest.getMessageTo());
	    root.put("type", "template");

	    // Template
	    Map<String, Object> template = new HashMap<>();
	    template.put("name", whatsAppMessageRequest.getTemplateName());

	    Map<String, String> language = new HashMap<>();
	    language.put("code", whatsAppMessageRequest.getLanguage());
	    template.put("language", language);

	    List<Map<String, Object>> components = new ArrayList<>();

	    // =========================
	    // BODY PARAMETERS
	    // =========================
	    if (whatsAppMessageRequest.getMsgBodyVariable() != null
	            && !whatsAppMessageRequest.getMsgBodyVariable().isEmpty()) {

	        List<Map<String, Object>> bodyParameters = new ArrayList<>();

	        whatsAppMessageRequest.getMsgBodyVariable().forEach(var -> {
	            Map<String, Object> param = new HashMap<>();
	            param.put("type", "text");
	            param.put("text", var.getBodyVariable());
	            bodyParameters.add(param);
	        });

	        Map<String, Object> bodyComponent = new HashMap<>();
	        bodyComponent.put("type", "body");
	        bodyComponent.put("parameters", bodyParameters);

	        components.add(bodyComponent);
	    }


	 // =========================
	 // BUTTON PARAMETERS
	 // =========================
	 if (whatsAppMessageRequest.getButtonVariable() != null
	         && !whatsAppMessageRequest.getButtonVariable().isEmpty()) {

	     for (int i = 0; i < whatsAppMessageRequest.getButtonVariable().size(); i++) {

	         TemplateButtonVariableRequest btn =
	                 whatsAppMessageRequest.getButtonVariable().get(i);

	         Map<String, Object> buttonComponent = new HashMap<>();
	         buttonComponent.put("type", "button");

	         // url / quick_reply
	         buttonComponent.put("sub_type", btn.getType());

	         // button index (0,1,...)
	         buttonComponent.put("index", String.valueOf(i));

	         List<Map<String, Object>> buttonParameters = new ArrayList<>();

	         Map<String, Object> parameter = new HashMap<>();

	         if ("url".equalsIgnoreCase(btn.getType())) {

	             parameter.put("type", "text");
	             parameter.put("text", btn.getValue());

	         } else if ("quick_reply".equalsIgnoreCase(btn.getType())) {

	             parameter.put("type", "payload");
	             parameter.put("payload", btn.getValue());

	         } else if ("phone_number".equalsIgnoreCase(btn.getType())) {

	             parameter.put("type", "text");
	             parameter.put("text", btn.getValue());
	         }

	         buttonParameters.add(parameter);

	         buttonComponent.put("parameters", buttonParameters);

	         components.add(buttonComponent);
	     }
	 }

	    template.put("components", components);
	    root.put("template", template);
	    
//	    System.out.println(" Response  : "+new ObjectMapper().writeValueAsString(root));

	    return new ObjectMapper().writeValueAsString(root);
	}

	public WhatsAppMessageResponse callSendTemplateTextMessage(String jsonPayload) throws Exception {

		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(jsonPayload, mediaType);

		Request request = new Request.Builder().url("https://graph.facebook.com/"+Constant.VERSION+"/"+Constant.PHONE_NUMBER_ID+"/messages")
				.post(body).addHeader("Content-Type", "application/json")
				.addHeader("Authorization", "Bearer " + Constant.ACCESS_TOKEN).build();

		try (okhttp3.Response response = client.newCall(request).execute()) {

			String responseBody = response.body() != null ? response.body().string() : "";

			ObjectMapper mapper = new ObjectMapper();
			WhatsAppMessageResponse res = new WhatsAppMessageResponse();

			// ❌ ERROR CASE
			if (!response.isSuccessful()) {

				JsonNode root = mapper.readTree(responseBody);
				JsonNode errorNode = root.path("error");

				String errorMessage = errorNode.path("message").asText();
				String errorType = errorNode.path("type").asText();
				int errorCode = errorNode.path("code").asInt();
				int errorSubCode = errorNode.path("error_subcode").asInt();

				// 🔍 LOG ERROR
				System.err.println("WhatsApp API Error");
				System.err.println("HTTP Status     : " + response.code());
				System.err.println("Message         : " + errorMessage);
				System.err.println("Type            : " + errorType);
				System.err.println("Code            : " + errorCode);
				System.err.println("Error Sub Code  : " + errorSubCode);

				// ✅ SET ERROR INTO RESPONSE OBJECT
				res.setErrorMessage(errorMessage);
				res.setErrorType(errorType);
				res.setErrorCode(errorCode);
				res.setErrorSubcode(errorSubCode);

				return res; // 👈 RETURN ERROR RESPONSE
			}

			// ✅ SUCCESS CASE
			JsonNode root = mapper.readTree(responseBody);

			res.setMessagingProduct(root.path("messaging_product").asText());

			// contacts[0]
			JsonNode contact = root.path("contacts").get(0);
			if (contact != null) {
				res.setSendTo(contact.path("input").asText());
				res.setWaId(contact.path("wa_id").asText());
			}

			// messages[0]
			JsonNode message = root.path("messages").get(0);
			if (message != null) {
				res.setMessagesId(message.path("id").asText());
				res.setMessageStatus(message.path("message_status").asText());
			}

			return res;
		}
	}

}
