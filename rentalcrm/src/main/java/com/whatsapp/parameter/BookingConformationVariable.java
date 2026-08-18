package com.whatsapp.parameter;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.datfusrental.entities.LeadDetails;
import com.datfusrental.object.request.LeadRequestObject;
import com.datfusrental.whatsapp.request.TemplateBodyVariableRequest;
import com.datfusrental.whatsapp.request.TemplateButtonVariableRequest;
import com.datfusrental.whatsapp.request.WhatsAppMessageRequestObject;

@Component
public class BookingConformationVariable {
	
	public TemplateBodyVariableRequest createVariable(String value) {
	    TemplateBodyVariableRequest variable = new TemplateBodyVariableRequest();
	    variable.setBodyVariable(value);
	    return variable;
	}
	
	public TemplateButtonVariableRequest createButtonVariable(String type, String value) {
	    TemplateButtonVariableRequest variable = new TemplateButtonVariableRequest();
	    variable.setType(type);
	    variable.setValue(value);
	    return variable;
	}
	

	public String convertDate(Date timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String date = sdf.format(timestamp);
		System.out.println(date);
		return date;
	}
	
	public String convertTime(Date timestamp) {
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		String time = timeFormat.format(timestamp);
		System.out.println(time);
		return time;
	}
	
	public LeadRequestObject setMessageVaribaleForVehicleBookingConfirmation(LeadRequestObject leadRequest, LeadDetails leadDetails) {

		List<TemplateBodyVariableRequest> bodyVariables = Arrays.asList(
			    createVariable(leadDetails.getCategory()),
			    createVariable(leadDetails.getSubCategory()),
			    createVariable(String.valueOf(leadDetails.getQuantity())),
			    createVariable(String.valueOf(leadDetails.getTotalDays())),
			    createVariable(String.valueOf(this.convertDate(leadDetails.getPickupDateTime())+" | "+this.convertTime(leadDetails.getPickupDateTime()))),
			    createVariable(leadDetails.getPickupHub()),
			    createVariable(leadDetails.getPickupPoint()),
			    createVariable(String.valueOf(this.convertDate(leadDetails.getDropDateTime()))),
			    createVariable(String.valueOf(leadDetails.getDropHub())),
			    createVariable(String.valueOf(leadDetails.getDropPoint())),
			    createVariable(leadDetails.getCountryDialCode()+leadDetails.getCustomerMobile()),
			    createVariable(String.valueOf(leadDetails.getCustomerMobile())),
			    createVariable(String.valueOf(leadDetails.getAlternateMobile())),
			    ///
			    createVariable(String.valueOf(leadDetails.getTotalAmount())),
			    createVariable(String.valueOf(leadDetails.getBookingAmount())),
			    createVariable(String.valueOf(leadDetails.getBalanceAmount())),
			    //
			    createVariable(String.valueOf(leadDetails.getSecurityAmount()))
			);

			leadRequest.setMsgBodyVariable(bodyVariables);
			
			List<TemplateButtonVariableRequest> buttonVariables = Arrays.asList(
				    createButtonVariable("url", leadDetails.getBookingId())
				    );

			leadRequest.setButtonVariable(buttonVariables);
			leadRequest.setTemplateName("rental_confirmation");
		
		return leadRequest;
	}
	

	public LeadRequestObject setMessageVaribaleForActivityBookingConfirmation(LeadRequestObject leadRequest, LeadDetails leadDetails) {
//		Date timestamp = leadDetails.getPickupDateTime();
//
//		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//		String date = sdf.format(timestamp);
//		System.out.println(date);
//		
//		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
//		String time = timeFormat.format(timestamp);
//		System.out.println(time);
		
		
		List<TemplateBodyVariableRequest> bodyVariables = Arrays.asList(
			    createVariable(leadDetails.getCategory()),
			    createVariable(leadDetails.getSubCategory()),
			    createVariable(String.valueOf(this.convertDate(leadDetails.getPickupDateTime())+" | "+this.convertTime(leadDetails.getPickupDateTime()))),
			    createVariable(String.valueOf(this.convertDate(leadDetails.getDropDateTime())+" | "+this.convertTime(leadDetails.getDropDateTime()))),
			    createVariable(leadDetails.getPickupHub()),
			    createVariable(leadDetails.getDropHub()),
			    createVariable(leadDetails.getCustomerMobile()),
			    //
			    createVariable(leadDetails.getCustomeName()),
			    createVariable(leadDetails.getCountryDialCode()+leadDetails.getCustomeName()),
			    createVariable((leadDetails.getCountryDialCode()+ leadDetails.getAlternateMobile())),
			    //
			    createVariable(String.valueOf(leadDetails.getQuantity())),
			    createVariable(String.valueOf(leadDetails.getKidQuantity())),
			    createVariable(String.valueOf(leadDetails.getInfantQuantity())),
			    //
			    createVariable(String.valueOf(leadDetails.getTotalAmount())),
			    createVariable(String.valueOf(leadDetails.getBookingAmount())),
			    createVariable(String.valueOf(leadDetails.getBalanceAmount()))

			);

			leadRequest.setMsgBodyVariable(bodyVariables);
			
			List<TemplateButtonVariableRequest> buttonVariables = Arrays.asList(
				    createButtonVariable("url", leadDetails.getBookingId()),
				    createButtonVariable("url", leadDetails.getBookingId())
				    );

			leadRequest.setButtonVariable(buttonVariables);
			leadRequest.setTemplateName("activity_confirmation");
		
		return leadRequest;
	}




}
