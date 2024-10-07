package com.datfusrental.whatsapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datfusrental.constant.Constant;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.object.request.LeadRequestObject;

@Service
public class SendWhatsappMsg {

	@Autowired
	private InteraktApi interaktApi;

	@Autowired
	private VehicleParameter vehicleParameter;

	public String sendWhatsAppMessage(LeadDetails leadDetails) throws Exception {
		String response = "";
		if (leadDetails.getCategoryTypeName().equalsIgnoreCase(Constant.VEHICLE)) {

			// bike
			if (leadDetails.getSuperCategory().equalsIgnoreCase(Constant.BIKE)) {
				if (leadDetails.getStatus().equalsIgnoreCase(Constant.ENQUIRY)) {
					

				} else if (leadDetails.getStatus().equalsIgnoreCase(Constant.FOLLOW_UP)) {

				} else if (leadDetails.getStatus().equalsIgnoreCase(Constant.FOLLOW_UP)) {

					String reqParam = vehicleParameter.setFollowupParameterForBike(leadDetails);
					response = interaktApi.sendInteraktMsg(reqParam);

				} else if (leadDetails.getStatus().equalsIgnoreCase(Constant.FOLLOW_UP)) {

				}
			}
			// car
			if (leadDetails.getSuperCategory().equalsIgnoreCase(Constant.CAR)) {

			}
		}
		return response;
	}

}
