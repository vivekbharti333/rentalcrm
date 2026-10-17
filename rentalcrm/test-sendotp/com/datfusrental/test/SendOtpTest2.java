package com.datfusrental.test;

import com.datfusrental.constant.Constant;
import com.datfusrental.object.response.WhatsAppMessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Test send for rywcrm_pass: 1 body variable (mobile) + url button parameter.
 *
 * Usage: java SendOtpTest2 <recipientMobile> <buttonUrlSuffix>
 */
public class SendOtpTest2 {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: java SendOtpTest2 <recipientMobile> <buttonUrlSuffix>");
            System.exit(1);
        }
        String mobileNumber = args[0];
        String buttonSuffix = args[1];

        Map<String, Object> root = new HashMap<>();
        root.put("messaging_product", "whatsapp");
        root.put("to", mobileNumber);
        root.put("type", "template");

        Map<String, Object> template = new HashMap<>();
        template.put("name", "rywcrm_pass");

        Map<String, String> language = new HashMap<>();
        language.put("code", "en");
        template.put("language", language);

        List<Map<String, Object>> components = new ArrayList<>();

        // ---- Body: single variable = mobile number ----
        Map<String, Object> bodyParam = new HashMap<>();
        bodyParam.put("type", "text");
        bodyParam.put("text", mobileNumber);

        Map<String, Object> bodyComponent = new HashMap<>();
        bodyComponent.put("type", "body");
        bodyComponent.put("parameters", List.of(bodyParam));
        components.add(bodyComponent);

        // ---- Button index 0: url suffix parameter ----
        Map<String, Object> btnParam = new HashMap<>();
        btnParam.put("type", "text");
        btnParam.put("text", buttonSuffix);

        Map<String, Object> buttonComponent = new HashMap<>();
        buttonComponent.put("type", "button");
        buttonComponent.put("sub_type", "url");
        buttonComponent.put("index", "0");
        buttonComponent.put("parameters", List.of(btnParam));
        components.add(buttonComponent);

        template.put("components", components);
        root.put("template", template);

        String payload = new ObjectMapper().writeValueAsString(root);
        System.out.println("PAYLOAD: " + payload);

        // ---- Send (same as SendWhatsAppTextMessageHelper.callSendTemplateTextMessage) ----
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(payload, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("https://graph.facebook.com/" + Constant.VERSION + "/" + Constant.PHONE_NUMBER_ID + "/messages")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + Constant.ACCESS_TOKEN)
                .build();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            System.out.println("HTTP STATUS: " + response.code());
            System.out.println("RESPONSE   : " + responseBody);
        }
    }
}
