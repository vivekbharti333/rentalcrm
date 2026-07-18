package com.whatsapp.parameter;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.datfusrental.entities.LeadDetails;
import com.datfusrental.whatsapp.request.TemplateBodyVariableRequest;
import com.datfusrental.whatsapp.request.TemplateButtonVariableRequest;
import com.datfusrental.whatsapp.request.WhatsAppMessageRequestObject;

@Component
public class BookingConformationVariable {
	
	private TemplateBodyVariableRequest createVariable(String value) {
	    TemplateBodyVariableRequest variable = new TemplateBodyVariableRequest();
	    variable.setBodyVariable(value);
	    return variable;
	}
	
	private TemplateButtonVariableRequest createButtonVariable(String type, String value) {
	    TemplateButtonVariableRequest variable = new TemplateButtonVariableRequest();
	    variable.setType(type);
	    variable.setValue(value);
	    return variable;
	}
	
	WhatsAppMessageRequestObject setMessageVaribaleForVehicleBookingConfirmation(WhatsAppMessageRequestObject whatsAppMessageRequest, LeadDetails leadDetails) {
		Date timestamp = leadDetails.getPickupDateTime();

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String date = sdf.format(timestamp);
		System.out.println(date);
		
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		String time = timeFormat.format(timestamp);
		System.out.println(time);
		
		
		List<TemplateBodyVariableRequest> bodyVariables = Arrays.asList(
			    createVariable(leadDetails.getCategory()),
			    createVariable(leadDetails.getSubCategory()),
			    createVariable(String.valueOf(leadDetails.getQuantity())),
			    createVariable(String.valueOf(leadDetails.getTotalDays())),
			    createVariable(String.valueOf(leadDetails.getPickupDateTime())),
			    createVariable(leadDetails.getPickupHub()),
			    createVariable(leadDetails.getPickupPoint()),
			    createVariable(String.valueOf(leadDetails.getDropDateTime())),
			    createVariable(String.valueOf(leadDetails.getDropHub())),
			    createVariable(String.valueOf(leadDetails.getDropPoint())),
			    createVariable(leadDetails.getCountryDialCode()+leadDetails.getCustomeName()),
			    createVariable(String.valueOf(leadDetails.getCustomerMobile())),
			    createVariable(String.valueOf(leadDetails.getAlternateMobile())),
			    ///
			    createVariable(String.valueOf(leadDetails.getTotalAmount())),
			    createVariable(String.valueOf(leadDetails.getBookingAmount())),
			    createVariable(String.valueOf(leadDetails.getBalanceAmount())),
			    //
			    createVariable(String.valueOf(leadDetails.getSecurityAmount()))
//			    createVariable(String.valueOf(leadDetails.getBalanceAmount())),
			);

			whatsAppMessageRequest.setMsgBodyVariable(bodyVariables);
			
			List<TemplateButtonVariableRequest> buttonVariables = Arrays.asList(
				    createButtonVariable("url", leadDetails.getBookingId())
				    );

				whatsAppMessageRequest.setButtonVariable(buttonVariables);
		
		return whatsAppMessageRequest;
	}
	
	WhatsAppMessageRequestObject setMessageVaribaleForActivityBookingConfirmation(WhatsAppMessageRequestObject whatsAppMessageRequest, LeadDetails leadDetails) {
		Date timestamp = leadDetails.getPickupDateTime();

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String date = sdf.format(timestamp);
		System.out.println(date);
		
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		String time = timeFormat.format(timestamp);
		System.out.println(time);
		
		
		List<TemplateBodyVariableRequest> bodyVariables = Arrays.asList(
			    createVariable(leadDetails.getCategory()),
			    createVariable(leadDetails.getSubCategory()),
			    createVariable(String.valueOf(date)),
			    createVariable(String.valueOf(time)),
			    createVariable(leadDetails.getPickupHub()),
			    createVariable(leadDetails.getPickupPoint()),
			    createVariable(String.valueOf(leadDetails.getDropDateTime())),
			    createVariable(String.valueOf(leadDetails.getDropHub())),
			    createVariable(String.valueOf(leadDetails.getDropPoint())),
			    createVariable(leadDetails.getCountryDialCode()+leadDetails.getCustomeName()),
			    createVariable(String.valueOf(leadDetails.getCustomerMobile())),
			    createVariable(String.valueOf(leadDetails.getAlternateMobile())),
			    ///
			    createVariable(String.valueOf(leadDetails.getTotalAmount())),
			    createVariable(String.valueOf(leadDetails.getBookingAmount())),
			    createVariable(String.valueOf(leadDetails.getBalanceAmount())),
			    //
			    createVariable(String.valueOf(leadDetails.getSecurityAmount()))
//			    createVariable(String.valueOf(leadDetails.getBalanceAmount())),
			);

			whatsAppMessageRequest.setMsgBodyVariable(bodyVariables);
			
			List<TemplateButtonVariableRequest> buttonVariables = Arrays.asList(
				    createButtonVariable("url", leadDetails.getBookingId())
				    );

				whatsAppMessageRequest.setButtonVariable(buttonVariables);
		
		return whatsAppMessageRequest;
	}


}
