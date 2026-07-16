package com.whatsapp.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.datfusrental.constant.Constant;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.Request;
import com.datfusrental.object.response.GenricResponse;
import com.datfusrental.object.response.Response;
import com.datfusrental.whatsapp.request.TemplateRequestObject;
import com.whatsapp.services.TemplateServices;



@CrossOrigin(origins = "*")
@RestController
public class WhatsAppTemplateController {
	
	@Autowired
	private TemplateServices templateServices;
	
	
//	@RequestMapping(path = "addTemplates", method = RequestMethod.POST)
//	public Response<TemplateRequestObject> addTemplates(@RequestBody Request<TemplateRequestObject> templateRequestObject,
//			HttpServletRequest request) {
//		GenricResponse<TemplateRequestObject> responseObj = new GenricResponse<TemplateRequestObject>();
//		try {
//			TemplateRequestObject response = templateServices.addTemplates(templateRequestObject);
//			return responseObj.createSuccessResponse(response, Constant.SUCCESS_CODE);
//		} catch (BizException e) {
//			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
//		} catch (Exception e) {
//			e.printStackTrace();
//			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
//		}
//	}
	
    
    @RequestMapping(path = "getWhatsAppTemplate", method = RequestMethod.POST)
	public Response<TemplateRequestObject> getWhatsAppTemplate(@RequestBody Request<TemplateRequestObject> templateRequestObject) {
		GenricResponse<TemplateRequestObject> response = new GenricResponse<TemplateRequestObject>();
		try {
			List<TemplateRequestObject> donationList = templateServices.getWhatsAppTemplate(templateRequestObject);
			return response.createListResponse(donationList, Constant.SUCCESS_CODE, String.valueOf(donationList.size()));
		} catch (BizException e) {
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}
    
//    @RequestMapping(path = "deleteWhatsAppTemplateByName", method = RequestMethod.POST)
//	public Response<TemplateRequestObject> deleteWhatsAppTemplateByName(@RequestBody Request<TemplateRequestObject> templateRequestObject,
//			HttpServletRequest request) {
//		GenricResponse<TemplateRequestObject> responseObj = new GenricResponse<TemplateRequestObject>();
//		try {
//			TemplateRequestObject response = templateServices.deleteWhatsAppTemplateByName(templateRequestObject);
//			return responseObj.createSuccessResponse(response, Constant.SUCCESS_CODE);
//		} catch (BizException e) {
//			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
//		} catch (Exception e) {
//			e.printStackTrace();
//			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
//		}
//	}
	
    
   
}