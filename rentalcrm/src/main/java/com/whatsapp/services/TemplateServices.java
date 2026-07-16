package com.whatsapp.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datfusrental.constant.Constant;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.Request;
import com.datfusrental.whatsapp.request.TemplateRequestObject;
import com.whatsapp.helper.TemplateMapperHelper;
import com.whatsapp.helper.WhatsAppTemplateHelper;


@Service
public class TemplateServices {
	
	@Autowired
	private WhatsAppTemplateHelper whatsAppTemplateHelper;

	@Autowired
	private TemplateMapperHelper templateMapperHelper;
	
//	@Autowired 
//	private WhatsAppHelper whatsAppHelper;
//	
//	public TemplateRequestObject addTemplates(Request<TemplateRequestObject> templateRequestObject) throws Exception {
//
//	    TemplateRequestObject templateRequest = templateRequestObject.getPayload();
//	    templateMapperHelper.validateTemplateRequest(templateRequest);
//
//	    WhatsAppDetails whatsAppDetails = whatsAppHelper.getWhatsAppBySuperadminId(Constant.GLOBAL_SUPERADMIN_ID);
//	    System.out.println("Whats App : "+whatsAppDetails);
//	    String parameter = whatsAppTemplateHelper.buildMarketingTemplatePayload(templateRequest);
//	    templateRequest =  whatsAppTemplateHelper.callWhatsAppApiForMarketingTemplate(whatsAppDetails, parameter);
//
//	    if (templateRequest.getRespCode() == 200) {
//	    	templateRequest.setRespCode(Constant.SUCCESS_CODE);
//	    	templateRequest.setRespMesg("Template Created");
//	        System.out.println("✅ Template Created");
////	        System.out.println("ID: " + response.getId());
//	        System.out.println("Status: " + templateRequest.getStatus());
//	    } else {
//	    	templateRequest.setRespCode(Constant.BAD_REQUEST_CODE);
//	    	templateRequest.setRespMesg("Template Creation Failed");
//	        System.out.println("❌ Template Creation Failed");
//	        System.out.println("Error Code: " + templateRequest.getRespCode());
//	        System.out.println("Error Message: " + templateRequest.getRespMesg());
//	    }
//	    return templateRequest;
//	}

	public List<TemplateRequestObject> getWhatsAppTemplate(Request<TemplateRequestObject> templateRequestObject)
			throws IOException, BizException {
		TemplateRequestObject templateRequest = templateRequestObject.getPayload();
		templateMapperHelper.validateTemplateRequest(templateRequest);
		
		System.out.println("Enter in getWhatsAppTemplate");

		List<TemplateRequestObject> templateList = new ArrayList<>();
		templateList = templateMapperHelper.getTemplates(templateRequest);
		return templateList;
	}


//	public TemplateRequestObject deleteWhatsAppTemplateByName(Request<TemplateRequestObject> templateRequestObject) throws Exception {
//		TemplateRequestObject templateRequest = templateRequestObject.getPayload();
//		templateMapperHelper.validateTemplateRequest(templateRequest);
//		
//		WhatsAppDetails whatsAppDetails = whatsAppHelper.getWhatsAppBySuperadminId(Constant.GLOBAL_SUPERADMIN_ID);
//		
//		String apiResp = templateMapperHelper.deleteTemplate(templateRequest.getTemplateName(), whatsAppDetails);
//		
//		
//		// ✅ Parse JSON and print success
//	    if (apiResp != null) {
//	        JSONObject json = new JSONObject(apiResp);
//
//	        if (json.has("success")) {
//	            boolean success = json.getBoolean("success");
//	            
//	        	templateRequest.setRespCode(Constant.SUCCESS_CODE);
//	        	templateRequest.setRespMesg("Successfully Deleted");
//	        	return templateRequest;
//	        }else {
//	        	templateRequest.setRespCode(Constant.BAD_REQUEST_CODE);
//	        	templateRequest.setRespMesg("Can not Delete");
//	        	return templateRequest;
//	        }   }
//		return templateRequest;
//	}



}
