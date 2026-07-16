package com.datfusrental.whatsapp.request;

import lombok.Data;

@Data
public class TemplateButtonVariableRequest {

	private String type; // url / phone_number / quick_reply
	private String text; // button label
	private String value; // url OR phone number (not needed for quick_reply)
}
