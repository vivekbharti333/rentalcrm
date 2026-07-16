package com.datfusrental.object.response;

import lombok.Data;

@Data
public class WhatsAppMessageResponse {
	
	private String messagingProduct;
	private String sendTo;
	private String waId;
	private String messagesId;
	private String messageStatus;
	
	private String errorMessage;
	private String errorType;
	private int errorCode;
	private int errorSubcode;
	
    // Response
    private int respCode;
    private String respMesg;
}
