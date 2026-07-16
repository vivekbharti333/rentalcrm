package com.whatsapp.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.datfusrental.constant.Constant;
import com.datfusrental.dao.WhatsAppMessageDao;
import com.datfusrental.entities.WhatsAppMessage;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.whatsapp.request.WhatsAppMessageRequestObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component
public class WhatsAppMessageHelper {
	
	@Autowired
	private WhatsAppMessageDao whatsAppMessageDao;


	public void validateWhatsAppMessageRequest(WhatsAppMessageRequestObject whatsAppMessageRequestObject) throws BizException {
		if (whatsAppMessageRequestObject == null) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Bad Request Object Null");
		}
	}

	@SuppressWarnings("unchecked")
	public List<WhatsAppMessage> getWhatsAppMessage(WhatsAppMessageRequestObject whatsAppMessageRequest) {
		List<WhatsAppMessage> results = new ArrayList<>();
		results = whatsAppMessageDao.getEntityManager().createQuery("SELECT WA FROM WhatsAppMessage WA ORDER BY WA.id ASC")
				.getResultList();
		return results;
	}


	public String setParameterForReplyMessage(WhatsAppMessageRequestObject whatsAppMessageRequest) {

	    JSONObject json = new JSONObject();

	    json.put("messaging_product", "whatsapp");
	    json.put("recipient_type", "individual");
	    json.put("to", whatsAppMessageRequest.getWaId());
	    json.put("type", "text");

	    JSONObject text = new JSONObject();
	    text.put("preview_url", false);
	    text.put("body", whatsAppMessageRequest.getMessageText());

	    json.put("text", text);

	    return json.toString();
	}

	public String apiToSendReplyMessage(String requestParam) throws IOException {

	    OkHttpClient client = new OkHttpClient.Builder().build();

	    MediaType mediaType = MediaType.parse("application/json");

	    RequestBody body = RequestBody.create(
	            requestParam,
	            mediaType
	    );

	    Request request = new Request.Builder()
	            .url("https://graph.facebook.com/v24.0/902659692940310/messages")
	            .post(body)
	            .addHeader("Content-Type", "application/json")
	            .addHeader("Authorization", "Bearer EAARXhxNUBPgBQrD3LOphV0ks82HE4qmgZCGOxZB8BYhmZBYwt4nWfySm1Vlonw1ZBr9ajqahAiRqbzynunamkuahEWTnZA0EaM4sQHbhlujDWkPEeAZDZD")
	            .build();

	    Response response = client.newCall(request).execute();
	    System.out.println("Response : "+response);

	    return response.body() != null ? response.body().string() : "";
	}

	
	
}
