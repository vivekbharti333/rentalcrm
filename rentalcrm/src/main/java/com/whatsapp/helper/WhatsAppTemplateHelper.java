package com.whatsapp.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.datfusrental.constant.Constant;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.whatsapp.request.TemplateBodyVariableRequest;
import com.datfusrental.whatsapp.request.TemplateButtonVariableRequest;
import com.datfusrental.whatsapp.request.TemplateRequestObject;
import com.datfusrental.whatsapp.request.WhatsAppErrorResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component
public class WhatsAppTemplateHelper {
	
	public void validateTemplateRequest(TemplateRequestObject templateRequestObject) throws BizException {
		if (templateRequestObject == null) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Bad Request Object Null");
		}
	}

	public String callMethiod() {

	    TemplateRequestObject req = new TemplateRequestObject();

	    req.setTemplateName("promo_template_1");
	    req.setLanguage("en");
	    req.setCategory("MARKETING");
	    req.setParameterFormat("NAMED");

	    // ================= HEADER =================
	    req.setHeaderAvailable(true);
	    req.setHeaderFormat("IMAGE");
	    req.setHeaderExample(Arrays.asList("MEDIA_HANDLE_ID")); // from upload API

	    // ================= BODY =================
	    req.setMsgBodyText("Hello {{name}}, check our offer!");

	    TemplateBodyVariableRequest var = new TemplateBodyVariableRequest();
	    var.setBodyVariable("name");

	    req.setMsgBodyVariable(Arrays.asList(var));

	    // ================= FOOTER =================
	    req.setFooterAvailable(true);
	    req.setFooterText("Thank you");

	    // ================= BUTTONS =================
	    req.setReplyButtonAvailable(true);

	    List<TemplateButtonVariableRequest> buttonList = new ArrayList<>();

	    // URL Button
	    TemplateButtonVariableRequest b1 = new TemplateButtonVariableRequest();
	    b1.setType("url");
	    b1.setText("Visit Website");
	    b1.setValue("https://example.com");

	    // Phone Button
	    TemplateButtonVariableRequest b2 = new TemplateButtonVariableRequest();
	    b2.setType("phone_number");
	    b2.setText("Call Us");
	    b2.setValue("+919999999999");

	    // Quick Reply Button
	    TemplateButtonVariableRequest b3 = new TemplateButtonVariableRequest();
	    b3.setType("quick_reply");
	    b3.setText("Stop Promotions");

	    buttonList.add(b1);
	    buttonList.add(b2);
	    buttonList.add(b3);

	    req.setButtonVariable(buttonList);

	    // ================= BUILD JSON =================
	    String json = buildMarketingTemplatePayload(req);

	    System.out.println("Generated Payload: " + json);

	    return json;
	}


	public String buildMarketingTemplatePayload(TemplateRequestObject req) {
	    try {
	        Map<String, Object> payload = new HashMap<>();

	        payload.put("name", req.getTemplateName());
	        payload.put("language", req.getLanguage());
	        payload.put("category", req.getCategory().toLowerCase());
	        payload.put("parameter_format", req.getParameterFormat());

	        List<Map<String, Object>> components = new ArrayList<>();

	        // ================= HEADER =================
	        if (req.isHeaderAvailable()) {
	        	
	        	if ("TEXT".equalsIgnoreCase(req.getHeaderFormat())
	                    && (req.getHeaderText() == null || req.getHeaderText().isEmpty())) {
	                throw new RuntimeException("Header text required for TEXT format");
	            }

	            if (!"TEXT".equalsIgnoreCase(req.getHeaderFormat())
	                    && (req.getHeaderExample() == null || req.getHeaderExample().isEmpty())) {
	                throw new RuntimeException("Media handle required for IMAGE/DOCUMENT header");
	            }

	            Map<String, Object> header = new HashMap<>();
	            String format = req.getHeaderFormat().toUpperCase();

	            header.put("type", "header");
	            header.put("format", format.toLowerCase()); // API expects lowercase

	            // ================= TEXT HEADER =================
	            if ("TEXT".equalsIgnoreCase(format)) {

	                header.put("text", req.getHeaderText());

	                if (req.getHeaderExample() != null && !req.getHeaderExample().isEmpty()) {
	                    Map<String, Object> example = new HashMap<>();
	                    example.put("header_text", req.getHeaderExample());
	                    header.put("example", example);
	                }
	            }

	            // ================= IMAGE HEADER =================
	            else if ("IMAGE".equalsIgnoreCase(format)) {

	                Map<String, Object> example = new HashMap<>();
	                example.put("header_handle", req.getHeaderExample()); // must be media handle
	                header.put("example", example);
	            }

	            // ================= DOCUMENT HEADER =================
	            else if ("DOCUMENT".equalsIgnoreCase(format)) {

	                Map<String, Object> example = new HashMap<>();
	                example.put("header_handle", req.getHeaderExample()); // PDF handle
	                header.put("example", example);
	            }

	            components.add(header);
	        }

	        // ================= BODY =================
	        Map<String, Object> body = new HashMap<>();
	        body.put("type", "body");
	        body.put("text", req.getMsgBodyText());

	        if ("NAMED".equalsIgnoreCase(req.getParameterFormat())
	                && req.getMsgBodyVariable() != null) {

	            List<Map<String, String>> namedParams = new ArrayList<>();

	            for (TemplateBodyVariableRequest var : req.getMsgBodyVariable()) {
	                Map<String, String> param = new HashMap<>();
	                param.put("param_name", var.getBodyVariable());
	                param.put("example", "sample_value");
	                namedParams.add(param);
	            }

	            Map<String, Object> example = new HashMap<>();
	            example.put("body_text_named_params", namedParams);
	            body.put("example", example);

	        } else if (req.getBodyExample() != null) {
	            Map<String, Object> example = new HashMap<>();
	            example.put("body_text", req.getBodyExample());
	            body.put("example", example);
	        }

	        components.add(body);

	        // ================= FOOTER =================
	        if (req.isFooterAvailable()) {
	            Map<String, Object> footer = new HashMap<>();
	            footer.put("type", "footer");
	            footer.put("text", req.getFooterText());
	            components.add(footer);
	        }

	        // ================= BUTTONS (USING YOUR CLASS) =================
	        if (req.isReplyButtonAvailable()
	                && req.getButtonVariable() != null
	                && !req.getButtonVariable().isEmpty()) {

	            Map<String, Object> buttons = new HashMap<>();
	            buttons.put("type", "buttons");

	            List<Map<String, Object>> buttonList = new ArrayList<>();

	            for (TemplateButtonVariableRequest btn : req.getButtonVariable()) {

	                Map<String, Object> btnMap = new HashMap<>();

	                String type = btn.getType().toLowerCase();

	                btnMap.put("type", type);
	                btnMap.put("text", btn.getText());

	                // ✅ Dynamic handling
	                if ("url".equals(type)) {
	                    btnMap.put("url", btn.getValue());
	                } else if ("phone_number".equals(type)) {
	                    btnMap.put("phone_number", btn.getValue());
	                }
	                // quick_reply → only text needed

	                buttonList.add(btnMap);
	            }

	            buttons.put("buttons", buttonList);
	            components.add(buttons);
	        }

	        payload.put("components", components);

	        return new ObjectMapper().writeValueAsString(payload);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	

	public TemplateRequestObject callWhatsAppApiForMarketingTemplate(
	        String parameter) {

	    try {
	        OkHttpClient client = new OkHttpClient();

	        String url = Constant.WHATS_APP_BASE_URL
	                + "v24.0/1580519719664370/message_templates";

	        RequestBody body = RequestBody.create(
	                parameter,
	                MediaType.get("application/json")
	        );

	        Request request = new Request.Builder()
	                .url(url)
	                .post(body)
	                .addHeader("Authorization", "Bearer EAARXhxNUBPgBQrD3LOphV0ks82HE4qmgZCGOxZB8BYhmZBYwt4nWfySm1Vlonw1ZBr9ajqahAiRqbzynunamkuahEW" )
	                .addHeader("Content-Type", "application/json")
	                .build();

	        try (Response response = client.newCall(request).execute()) {

	            String resp = response.body() != null ? response.body().string() : "";

	            System.out.println("WhatsApp API Response: " + resp);

	            ObjectMapper mapper = new ObjectMapper();
	            JsonNode root = mapper.readTree(resp);

	            TemplateRequestObject result = new TemplateRequestObject();

	            // ✅ Check if error exists
	            if (root.has("error")) {

	                WhatsAppErrorResponse errorResponse = mapper.readValue(resp, WhatsAppErrorResponse.class);

	                result.setRespCode(errorResponse.getError().getCode());
	                result.setRespMesg(errorResponse.getError().getMessage());

	                System.out.println("❌ Error Code: " + errorResponse.getError().getCode());
	                System.out.println("❌ Error Message: " + errorResponse.getError().getMessage());

	                return result;
	            }

	            // ✅ Success case
//	            result.setId(root.get("id").asText());
	            result.setStatus(root.get("status").asText());
	            result.setCategory(root.get("category").asText());
	            result.setRespCode(200);
	            result.setRespMesg("SUCCESS");

	            return result;
	        }

	    } catch (Exception e) {
	        e.printStackTrace();

	        TemplateRequestObject errorObj = new TemplateRequestObject();
	        errorObj.setRespCode(500);
	        errorObj.setRespMesg(e.getMessage());
	        return errorObj;
	    }
	}
	

//	public TemplateRequestObject callWhatsAppApiForMarketingTemplate(
//	        WhatsAppDetails whatsAppDetails, String parameter) {
//
//	    try {
//	        OkHttpClient client = new OkHttpClient();
//
//	        String url = Constant.WHATS_APP_BASE_URL 
//	                + whatsAppDetails.getVersion() 
//	                + "/1580519719664370/message_templates";
//
//	        RequestBody body = RequestBody.create(
//	                parameter,
//	                MediaType.get("application/json")
//	        );
//
//	        Request request = new Request.Builder()
//	                .url(url)
//	                .post(body)
//	                .addHeader("Authorization", "Bearer " + whatsAppDetails.getUserAccessToken())
//	                .addHeader("Content-Type", "application/json")
//	                .build();
//
//	        try (Response response = client.newCall(request).execute()) {
//
//	            String resp = response.body() != null ? response.body().string() : "";
//
//	            // ✅ Print raw response
//	            System.out.println("WhatsApp API Response: " + resp);
//
//	            // ✅ Convert JSON → Object
//	            ObjectMapper mapper = new ObjectMapper();
//	            return mapper.readValue(resp, TemplateRequestObject.class);
//	        }
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//
//	        TemplateRequestObject errorObj = new TemplateRequestObject();
//	        errorObj.setRespCode(500);
//	        errorObj.setRespMesg(e.getMessage());
//	        return errorObj;
//	    }
//	}	
}