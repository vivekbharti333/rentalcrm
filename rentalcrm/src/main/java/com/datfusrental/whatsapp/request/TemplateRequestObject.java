package com.datfusrental.whatsapp.request;

import java.util.List;

import lombok.Data;

@Data
public class TemplateRequestObject {
	
 
	private String requestFor;
	private String templateId;
	private String templateName;
	private String parameterFormat;
	
	private boolean headerAvailable;
	private String headerFormat;
	private String headerText;
	private List<TemplateHeaderVariableRequest> headerVariable;
	
	private String msgBodyText;
	private List<TemplateBodyVariableRequest> msgBodyVariable;
	
	private boolean footerAvailable;
	private String footerText;
	
	private String language;
	private String status;
	private String category;
	private String sub_category;
	
	private boolean replyButtonAvailable;
	private List<TemplateButtonVariableRequest> buttonVariable;
	private String buttonsType;

	private List<String> headerExample;
	private List<List<String>> bodyExample;
	
	private String superadminId;
	
    // Response
    private int respCode;
    private String respMesg;
	


}
