package com.whatsapp.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datfusrental.entities.WhatsAppMessage;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.SendWhatsAppTextMessageHelper;
import com.datfusrental.object.request.Request;
import com.datfusrental.object.response.WhatsAppMessageResponse;
import com.datfusrental.whatsapp.request.WhatsAppMessageRequestObject;
import com.whatsapp.helper.SendTextMessageHelper;
import com.whatsapp.helper.TemplateMapperHelper;
import com.whatsapp.helper.WhatsAppMessageHelper;
import com.whatsapp.helper.WhatsAppWebhookMessageHelper;


@Service
public class WhatsAppMessageServices {

	@Autowired
	private TemplateMapperHelper templateMapperHelper;
	
	@Autowired
	private SendWhatsAppTextMessageHelper sendTextMessageHelper;
	
	@Autowired 
	private WhatsAppMessageHelper whatsAppMessageHelper;
	
	@Autowired
	private WhatsAppWebhookMessageHelper whatsAppWebhookMessageHelper;

	public WhatsAppMessageResponse sendTextTemplateMessage(Request<WhatsAppMessageRequestObject> whatsAppMessageRequestObject)
			throws BizException, Exception {
		WhatsAppMessageRequestObject whatsAppRequest = whatsAppMessageRequestObject.getPayload();
		whatsAppMessageHelper.validateWhatsAppMessageRequest(whatsAppRequest);
		
//		String templateParameter = sendTextMessageHelper.getTextTemplateParameter(whatsAppRequest);
		String templateParameter = sendTextMessageHelper.getTextTemplateParameterButton(whatsAppRequest);
		WhatsAppMessageResponse sendMessageResponse =  sendTextMessageHelper.callSendTemplateTextMessage(templateParameter);

		return sendMessageResponse;
	}
	
	
	public WhatsAppMessageResponse replyMessage(Request<WhatsAppMessageRequestObject> whatsAppMessageRequestObject) throws BizException, Exception {
		WhatsAppMessageRequestObject whatsAppMessageRequest = whatsAppMessageRequestObject.getPayload();
		whatsAppMessageHelper.validateWhatsAppMessageRequest(whatsAppMessageRequest);
		
		String param = whatsAppMessageHelper.setParameterForReplyMessage(whatsAppMessageRequest);
		String response = whatsAppMessageHelper.apiToSendReplyMessage(param);
		
		System.out.println("response : " + response);
		JSONObject jsonResponse = new JSONObject(response);

		if (jsonResponse.has("messages")) {
		    JSONArray messagesArray = jsonResponse.getJSONArray("messages");

		    if (messagesArray.length() > 0
		            && messagesArray.getJSONObject(0).has("id")) {

		        String messageId = messagesArray.getJSONObject(0).getString("id");
		        
		        whatsAppMessageRequest.setMessageId(messagesArray.getJSONObject(0).getString("id"));
		        whatsAppMessageRequest.setStatus("SEND");

		        System.out.println("Message Sent Successfully");
		        System.out.println("Message ID : " + messageId);

		    } else {

		    	whatsAppMessageRequest.setStatus("FAILD");
		        System.out.println("Message Failed");

		    }

		} else if (jsonResponse.has("error")) {

		    JSONObject errorObject = jsonResponse.getJSONObject("error");

		    String errorMessage = errorObject.getString("message");
		    int errorCode = errorObject.getInt("code");
		    
	    	whatsAppMessageRequest.setStatus("FAILD");

		    System.out.println("Message Failed");
		    System.out.println("Error Code : " + errorCode);
		    System.out.println("Error Message : " + errorMessage);

		} else {

		    System.out.println("Unknown Response");
	    	whatsAppMessageRequest.setStatus("FAILD");

		}
		
		whatsAppMessageRequest.setDirection("OUTGOING");
		whatsAppMessageRequest.setPhoneNumberId("902659692940310");
		whatsAppMessageRequest.setMessageType("text");

		WhatsAppMessage whatsAppMessage = whatsAppWebhookMessageHelper.getWhatsAppMessageByReqObject(whatsAppMessageRequest);
        whatsAppMessage = whatsAppWebhookMessageHelper.saveWhatsAppMessage(whatsAppMessage);
		
//		{"messaging_product":"whatsapp","contacts":[{"input":"918800689752","wa_id":"918800689752"}],"messages":[{"id":"wamid.HBgMOTE4ODAwNjg5NzUyFQIAERgSMDgxOEE2MTE2RTBEMDdEQUFFAA=="}]}

		
		System.out.println("response : "+response);
		return null;
	}


	public List<WhatsAppMessage> getWhatsAppMessage(Request<WhatsAppMessageRequestObject> whatsAppMessageRequestObject) 
		throws IOException, BizException {
		WhatsAppMessageRequestObject whatsAppMessageRequest = whatsAppMessageRequestObject.getPayload();
		whatsAppMessageHelper.validateWhatsAppMessageRequest(whatsAppMessageRequest);

		List<WhatsAppMessage> messageList = new ArrayList<>();
		messageList = whatsAppMessageHelper.getWhatsAppMessage(whatsAppMessageRequest);
		return messageList;
	}


}
