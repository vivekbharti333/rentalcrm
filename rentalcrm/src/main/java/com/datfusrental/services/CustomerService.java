package com.datfusrental.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datfusrental.constant.Constant;
import com.datfusrental.entities.CustomerPickupDetails;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.CustomerHelper;
import com.datfusrental.helper.LeadHelper;
import com.datfusrental.object.request.LeadRequestObject;
import com.datfusrental.object.request.Request;


@Service
public class CustomerService {

	@Autowired
	private LeadHelper leadHelper;
	
	@Autowired
	private CustomerHelper customerHelper;
	
	
	public LeadRequestObject uploadVehiclePhoto(Request<LeadRequestObject> leadRequestObject) throws BizException, Exception {
	    LeadRequestObject leadRequest = leadRequestObject.getPayload();
	    leadHelper.validateLeadRequest(leadRequest);

	    LeadDetails leadDetails = leadHelper.getLeadDetailsByBookingId(leadRequest.getBookingId());
	    if (leadDetails != null) {
	    	CustomerPickupDetails existsDetails = customerHelper.getCustomerPickupDetailsByBookingId(leadRequest.getBookingId());
	    	if(existsDetails == null) {
	    		leadRequest.setPaymentImage(saveBase64File(leadRequest.getPaymentImage(), leadRequest.getBookingId(), "image", "payment"));
	    		leadRequest.setVehicleVideo(saveBase64File(leadRequest.getVehicleVideo(), leadRequest.getBookingId(), "video", "vehicle-video"));
	    		leadRequest.setFrantImage(saveBase64File(leadRequest.getFrantImage(), leadRequest.getBookingId(), "image", "front"));
	    		leadRequest.setBackImage(saveBase64File(leadRequest.getBackImage(), leadRequest.getBookingId(), "image", "back"));
	    		leadRequest.setLeftImage(saveBase64File(leadRequest.getLeftImage(), leadRequest.getBookingId(), "image", "left"));
	    		leadRequest.setRightImage(saveBase64File(leadRequest.getRightImage(), leadRequest.getBookingId(), "image", "right"));
			leadRequest.setFuelGaugeImage(saveBase64File(leadRequest.getFuelGaugeImage(), leadRequest.getBookingId(), "image", "fuel-gauge"));
			if (!"cash".equalsIgnoreCase(leadRequest.getBalanceAmountPayMode())) {
				leadRequest.setBalanceAmountPayImage(saveBase64File(leadRequest.getBalanceAmountPayImage(), leadRequest.getBookingId(), "image", "balance-payment"));
			}
			leadRequest.setSecurityAmountImage(saveBase64File(leadRequest.getSecurityAmountImage(), leadRequest.getBookingId(), "image", "security-amount"));
	    		
	    		CustomerPickupDetails customerPickupDetails =  customerHelper.getCustomerPickupDetailsByReqObj(leadRequest);
		    	customerHelper.saveCustomerPickupDetails(customerPickupDetails);
		    	
		    	leadRequest.setRespCode(Constant.SUCCESS_CODE);
		    	leadRequest.setRespMesg("Successfully Submitted");
				return leadRequest;
	    	}
	    	else {
		    	
	    	} 
	    		
	    	
	    }else {
	    	leadRequest.setRespCode(Constant.BAD_REQUEST_CODE);
	    	leadRequest.setRespMesg("Invalid Request");
			return leadRequest;
	    }
		return leadRequest;

	      
	}

	public CustomerPickupDetails getCustomerPickupDetailsByBookingId(String bookingId) {
		return customerHelper.getCustomerPickupDetailsByBookingId(bookingId);
	}

	private String saveBase64File(String value, String bookingId, String type, String name) throws IOException {
		if (value == null || value.trim().isEmpty()) return null;
		String base64 = value;
		String extension = "png";
		if (value.startsWith("data:")) {
			int separator = value.indexOf(',');
			if (separator < 0) throw new IOException("Invalid Base64 upload");
			String metadata = value.substring(0, separator);
			if (metadata.contains("video/mp4")) extension = "mp4";
			else if (metadata.contains("video/quicktime")) extension = "mov";
			else if (metadata.contains("image/jpeg")) extension = "jpg";
			base64 = value.substring(separator + 1);
		}
		Path directory = Paths.get(Constant.docLocation, "vehicle_image", bookingId, type);
		Files.createDirectories(directory);
		Path file = directory.resolve(name + "." + extension);
		Files.write(file, Base64.getDecoder().decode(base64));
		return file.toString();
	}


	

}
