package com.datfusrental.paymentgateways;

import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datfusrental.entities.LeadDetails;
import com.datfusrental.entities.PaymentGatewayDetails;
import com.datfusrental.enums.PaymentGateway;
import com.datfusrental.helper.PaymentGatewaysHelper;
import com.datfusrental.object.request.LeadRequestObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Component
public class CashfreePaymentGateways {

	@Autowired
	HttpServletRequest request;
	
	@Autowired
	private PaymentGatewaysHelper paymentGatewaysHelper;


    public String getCashfreePaymentLink(LeadRequestObject leadRequest) throws IOException, InterruptedException {

        String param = this.getCashfreePaymentParam(leadRequest);

        // Make API call
        HttpResponse<String> response = this.getCashfreePaymentRequestPage(param);

        String linkUrl = null;

        if (response.statusCode() == 200) {
            String responseBody = response.body();
            try {
                JSONObject json = new JSONObject(responseBody);
                linkUrl = json.optString("link_url", null);

                if (linkUrl == null) {
                    System.err.println("No payment link returned: " + responseBody);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new IOException("Invalid JSON response from Cashfree", e);
            }
        } else {
            System.err.println("Payment gateway error response: " + response.body());
            throw new IOException("Cashfree API returned error code: " + response.statusCode());
        }

        return linkUrl; 
    }
 
    public String getCashfreePaymentParam(LeadRequestObject leadRequest) {

        JSONObject jsonBody = new JSONObject(); 

        jsonBody.put("link_id", leadRequest.getBookingId());
        jsonBody.put("link_amount", leadRequest.getTotalAmount());
        jsonBody.put("link_currency", "INR");
        jsonBody.put("link_purpose", leadRequest.getSuperCategory());

        JSONObject customerDetails = new JSONObject();
        customerDetails.put("customer_phone", leadRequest.getCustomerMobile());
        customerDetails.put("customer_email", leadRequest.getCustomerEmailId()); // TODO: replace with actual email
        customerDetails.put("customer_name", leadRequest.getCustomeName());
        jsonBody.put("customer_details", customerDetails);

        JSONObject linkNotify = new JSONObject();
        linkNotify.put("send_sms", true);
        linkNotify.put("send_email", true);
        jsonBody.put("link_notify", linkNotify);

        JSONObject linkMeta = new JSONObject();
        linkMeta.put("return_url", "https://romeyourway.com/payment-status/" + leadRequest.getBookingId());
        jsonBody.put("link_meta", linkMeta);

        System.out.println(" Response : "+jsonBody.toString());
        
        return jsonBody.toString();
    }

    /**
     * Call Cashfree API 
     */
    public HttpResponse<String> getCashfreePaymentRequestPage(String param)
            throws IOException, InterruptedException {
    	
    	PaymentGatewayDetails pgDetails =  paymentGatewaysHelper.getPaymentGatewayDetailsByName(PaymentGateway.CASHFREE.name());

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(pgDetails.getPgUrl()+"links"))
                .header("Content-Type", "application/json")
                .header("x-api-version", "2025-01-01")
                .header("x-client-id", pgDetails.getClientId())
                .header("x-client-secret", pgDetails.getSecurityKey())
                .POST(HttpRequest.BodyPublishers.ofString(param))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Cashfree Raw Response: " + response.body());
        return response;
    }
    
    
    public String getCashFreePaymentStatus(String bookingId) {
    	 OkHttpClient client = new OkHttpClient();
    	 PaymentGatewayDetails pgDetails =  paymentGatewaysHelper.getPaymentGatewayDetailsByName(PaymentGateway.CASHFREE.name());

         Request request = new Request.Builder()
                 .url(pgDetails.getPgUrl()+"links/" + bookingId)
                 .get()
                 .addHeader("x-api-version", "2025-01-01")
                 .addHeader("x-client-id", pgDetails.getClientId())
                 .addHeader("x-client-secret", pgDetails.getSecurityKey())
                 .build();

         try (Response response = client.newCall(request).execute()) {
             if (response.isSuccessful() && response.body() != null) {
                 String responseBody = response.body().string();
                 System.out.println("Response: " + responseBody);
                 
                 
                 ObjectMapper mapper = new ObjectMapper();
                 JsonNode root = mapper.readTree(responseBody);

//                 System.out.println("cf_link_id: " + root.get("cf_link_id").asText());
//                 System.out.println("customer_name: " + root.get("customer_details").get("customer_name").asText());
//                 System.out.println("country_code: " + root.get("customer_details").get("country_code").asText());
//                 System.out.println("customer_phone: " + root.get("customer_details").get("customer_phone").asText());
//                 System.out.println("customer_email: " + root.get("customer_details").get("customer_email").asText());
//
//                 System.out.println("enable_invoice: " + root.get("enable_invoice").asBoolean());
//                 System.out.println("entity: " + root.get("entity").asText());
//                 System.out.println("link_amount: " + root.get("link_amount").asInt());
//                 System.out.println("link_amount_paid: " + root.get("link_amount_paid").asInt());
//                 System.out.println("link_auto_reminders: " + root.get("link_auto_reminders").asBoolean());
//                 System.out.println("link_created_at: " + root.get("link_created_at").asText());
//                 System.out.println("link_currency: " + root.get("link_currency").asText());
//                 System.out.println("link_expiry_time: " + root.get("link_expiry_time").asText());
//                 System.out.println("link_id: " + root.get("link_id").asText());
//
//                 System.out.println("return_url: " + root.get("link_meta").get("return_url").asText());
//                 System.out.println("upi_intent: " + root.get("link_meta").get("upi_intent").asText());
//
//                 System.out.println("send_email: " + root.get("link_notify").get("send_email").asBoolean());
//                 System.out.println("send_sms: " + root.get("link_notify").get("send_sms").asBoolean());
//
//                 System.out.println("link_partial_payments: " + root.get("link_partial_payments").asBoolean());
//                 System.out.println("link_purpose: " + root.get("link_purpose").asText());
//                 System.out.println("link_qrcode: " + root.get("link_qrcode").asText().substring(0, 30) + "..."); // shortened
//                 System.out.println("link_status: " + root.get("link_status").asText());
//                 System.out.println("link_url: " + root.get("link_url").asText());
//                 System.out.println("terms_and_conditions: " + root.get("terms_and_conditions").asText());
//                 System.out.println("thank_you_msg: " + root.get("thank_you_msg").asText());
                 
                 return root.get("link_status").asText();
             } else {
                 System.out.println("Request failed. Code: " + response.code());
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
		return null;
    }
}
