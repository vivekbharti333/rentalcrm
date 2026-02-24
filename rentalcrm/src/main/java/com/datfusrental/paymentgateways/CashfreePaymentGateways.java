package com.datfusrental.paymentgateways;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
	
	@Autowired
	private ObjectMapper objectMapper;


    public String getCashfreePaymentLink(LeadRequestObject leadRequest) throws IOException, InterruptedException {

        String param = this.getCashfreePaymentParam(leadRequest);

        // Make API call
        HttpResponse<String> response = this.getCashfreePaymentRequestPage(param, leadRequest);

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
//        jsonBody.put("link_amount", leadRequest.getBookingAmount());
        jsonBody.put("link_amount", leadRequest.getActualAmount());
        jsonBody.put("link_currency", "INR");
        jsonBody.put("link_purpose", leadRequest.getCategory());

        JSONObject customerDetails = new JSONObject();
        customerDetails.put("customer_phone", leadRequest.getCustomerMobile());
        customerDetails.put("customer_email", leadRequest.getCustomerEmailId()); 
        customerDetails.put("customer_name", leadRequest.getCustomeName());
        jsonBody.put("customer_details", customerDetails);

        JSONObject linkNotify = new JSONObject();
        linkNotify.put("send_sms", true);
        linkNotify.put("send_email", true);
        jsonBody.put("link_notify", linkNotify);

        JSONObject linkMeta = new JSONObject();
//        linkMeta.put("return_url", "https://romeyourway.com/payment-status/" + leadRequest.getBookingId());
        linkMeta.put("return_url", leadRequest.getPgRespUrl() + leadRequest.getBookingId());
        jsonBody.put("link_meta", linkMeta);

        System.out.println(" Response : "+jsonBody.toString());
        
        return jsonBody.toString();
    }

    /**
     * Call Cashfree API 
     */
    public HttpResponse<String> getCashfreePaymentRequestPage(String param, LeadRequestObject leadRequest)
            throws IOException, InterruptedException {
    	
    	PaymentGatewayDetails pgDetails =  paymentGatewaysHelper.getPaymentGatewayDetailsByName(PaymentGateway.CASHFREE.name(), leadRequest.getEnquirySource() );

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.cashfree.com/pg/links"))
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
    
    public String getCashFreePaymentOrderIdByLinkIdStatus(String linkId, LeadRequestObject leadRequest) {
        OkHttpClient client = new OkHttpClient();
        PaymentGatewayDetails pgDetails = paymentGatewaysHelper.getPaymentGatewayDetailsByName(PaymentGateway.CASHFREE.name(), leadRequest.getEnquirySource());

        Request request = new Request.Builder()
                .url("https://api.cashfree.com/pg/links/" + linkId + "/orders")
                .get()
                .addHeader("x-api-version", "2025-01-01")
                .addHeader("x-client-id", pgDetails.getClientId())
                .addHeader("x-client-secret", pgDetails.getSecurityKey())
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                JsonNode root = objectMapper.readTree(responseBody);

                if (root.isArray() && root.size() > 0) {
                    JsonNode firstOrder = root.get(0);

                    // the "order_id" returned for the first order
                    if (firstOrder.has("order_id")) {
                        return firstOrder.get("order_id").asText();
                    }
                }
            } else {
                System.out.println("Request failed. Code: " + response.code());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    
    public String getCashFreePaymentStatusByOrderId(String orderId, LeadRequestObject leadRequest) {
        OkHttpClient client = new OkHttpClient();
        PaymentGatewayDetails pgDetails = paymentGatewaysHelper.getPaymentGatewayDetailsByName(PaymentGateway.CASHFREE.name(), leadRequest.getEnquirySource());

        Request request = new Request.Builder()
                .url("https://api.cashfree.com/pg/orders/" + orderId + "/payments")
                .get()
                .addHeader("x-api-version", "2025-01-01")
                .addHeader("x-client-id", pgDetails.getClientId())
                .addHeader("x-client-secret", pgDetails.getSecurityKey())
                .addHeader("Accept", "application/json")
                .build();
        
        try (Response response = client.newCall(request).execute()) {

            if (response.isSuccessful() && response.body() != null) {
                return response.body().string(); // ✅ return full JSON
            } else {
                System.out.println("Request failed. Code: " + response.code());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

//        try (Response response = client.newCall(request).execute()) {
//            if (response.isSuccessful() && response.body() != null) {
//                String responseBody = response.body().string();
//
//                // Parse the root JSON as an array
//                JsonNode rootArray = objectMapper.readTree(responseBody);
//
//                if (rootArray.isArray() && rootArray.size() > 0) {
//                    JsonNode firstPayment = rootArray.get(0);
//
//                    // Read the payment_status field
//                    if (firstPayment.has("payment_status")) {
//                        return firstPayment.get("payment_status").asText();
//                    }
//                }
//            } else {
//                System.out.println("Request failed. Code: " + response.code());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return null;
    }


}
