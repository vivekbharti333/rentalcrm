package com.datfusrental.whatsapp.request;

import lombok.Data;

@Data
public class TemplateBodyVariableRequest {

	private String variableType; 
	private String bodyVariable;
}
