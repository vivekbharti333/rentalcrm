package com.datfuslab.whatsapp;

//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import okhttp3.MediaType;
//import okhttp3.RequestBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;



@Component
public class InteraktApi {
	
	    @Autowired
	    private RestTemplate restTemplate;

	    
	    public String sendInteraktMsg(String parameters) throws Exception {

	        // Setting headers
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.set("Authorization", "Basic dUNkRHc1R01pUlVnaHAtMUx4elAxb2EyamZaT2o2eWtLcjJwYnUzNnlBRTo=");

	        // Creating the request
	        HttpEntity<String> request = new HttpEntity<>(parameters, headers);

	        // Sending the POST request
	        ResponseEntity<String> response = restTemplate.exchange("https://api.interakt.ai/v1/public/message/", HttpMethod.POST, request, String.class);

	        String responseString = response.getBody().toString();
	        return responseString;
	    }

	
//	@SuppressWarnings("deprecation")
//	public String sendInteraktMsg(String parameters) throws Exception {
//
//		OkHttpClient client = new OkHttpClient().newBuilder().build();
//		MediaType mediaType = MediaType.parse("application/json");
//		RequestBody body = RequestBody.create(mediaType, parameters);
//		Request request = new Request.Builder().url("https://api.interakt.ai/v1/public/message/").method("POST", body)
//				.addHeader("Content-Type", "application/json")
//				.addHeader("Authorization", "Basic dUNkRHc1R01pUlVnaHAtMUx4elAxb2EyamZaT2o2eWtLcjJwYnUzNnlBRTo=")
//				.build();
//		Response response = client.newCall(request).execute();
//		return response.toString();
//	}
}
