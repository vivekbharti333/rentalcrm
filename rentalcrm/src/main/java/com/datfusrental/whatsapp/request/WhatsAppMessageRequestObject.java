package com.datfusrental.whatsapp.request;

import java.util.List;

import lombok.Data;

@Data
public class WhatsAppMessageRequestObject {

	private String requestFor;
	private String templateId;
	private String parameterFormat;
	private String headerFormat;
	private String headerText;
	private String msgBodyText;
	private List<TemplateBodyVariableRequest> msgBodyVariable;
	private List<TemplateButtonVariableRequest> buttonVariable;
	private String footerText;
	private String language;
	private String status;
	private String category;
	private String sub_category;
	
	private String messageFrom;
	private String messageTo; 
	

	
	//For WhatsApp Message
	private String waId;
	private String userName;
	private String messageId;
	private String direction; // INCOMING / OUTGOING
	private String messageType; // text, image, document, template
	private String messageText;
	private String templateName;
	private String templateLanguage;
	private String mediaId;
	private String mimeType;
	private String fileName;
	private String filePath;
	private Long statusTimestamp;
	private Long messageTimestamp;
	private String contextMessageId;
	private String rawJson;
	
	
	
    // 🆕 ADD THESE (but better in webhook class)
    private String phoneNumberId;
    private String displayPhoneNumber;

	
	
	//make new object class and remove this value from here to that new class
	
//	private String toWhatsAppNumber;
	
}
