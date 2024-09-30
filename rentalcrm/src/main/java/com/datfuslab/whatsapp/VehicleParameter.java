package com.datfuslab.whatsapp;

import java.text.SimpleDateFormat;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.datfusrental.entities.LeadDetails;

@Component
public class VehicleParameter {

	public String setFollowupParameterForBike(LeadDetails leadDetails) throws Exception {

		JSONObject parameters = new JSONObject();
		JSONObject templete = new JSONObject();
		JSONObject zero = new JSONObject();

		String[] headerValues = { "Alert" };
		String[] zeroValue = { "12344" };

		String[] bodyValues = { leadDetails.getCustomeName() };

		templete.put("name", "rmb_followup_b4");
		templete.put("languageCode", "en");
		templete.put("headerValues", headerValues);
		templete.put("fileName", "dumy.pdf");
		templete.put("bodyValues", bodyValues);
		templete.put("buttonValues", zero);

		zero.put("0", zeroValue);

		parameters.put("countryCode", leadDetails.getCountryDialCode());
		parameters.put("phoneNumber", leadDetails.getCustomerMobile());
		parameters.put("type", "Template");
		parameters.put("callbackData:", "some_callback_data");
		parameters.put("template", templete);

		return parameters.toString();
	}
	
	
	
	

}
