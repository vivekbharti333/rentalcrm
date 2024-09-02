package com.datfusrental.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.datfusrental.common.GetBase64Image;
import com.datfusrental.constant.Constant;
import com.datfusrental.entities.CategoryDetails;
import com.datfusrental.entities.CategoryType;
import com.datfusrental.entities.SuperCategoryDetails;
import com.datfusrental.enums.ImageType;
import com.datfusrental.entities.SubCategoryDetails;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.ItemRequestObject;
import com.datfusrental.object.request.Request;
import com.datfusrental.object.response.GenricResponse;
import com.datfusrental.object.response.Response;
import com.datfusrental.services.LocationService;

@CrossOrigin(origins = "*")
@RestController
public class LocationController {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private LocationService locationService;
	
	@Autowired
	private GetBase64Image getBase64Image;
	
	@RequestMapping(path = "addLocation", method = RequestMethod.POST)
	public Response<ItemRequestObject> addLocation(@RequestBody Request<ItemRequestObject> itemRequestObject,
			HttpServletRequest request) {
		GenricResponse<ItemRequestObject> responseObj = new GenricResponse<ItemRequestObject>();
		try {
			ItemRequestObject responce = locationService.addLocation(itemRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}
	
	@RequestMapping(path = "editCategoryType", method = RequestMethod.POST)
	public Response<ItemRequestObject> editCategoryType(@RequestBody Request<ItemRequestObject> itemRequestObject,
			HttpServletRequest request) {
		GenricResponse<ItemRequestObject> responseObj = new GenricResponse<ItemRequestObject>();
		try {
			ItemRequestObject responce = locationService.editCategoryType(itemRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}
	
	@RequestMapping(path = "changeCategoryTypeStatus", method = RequestMethod.POST)
	public Response<ItemRequestObject> changeCategoryTypeStatus(@RequestBody Request<ItemRequestObject> itemRequestObject,
			HttpServletRequest request) {
		GenricResponse<ItemRequestObject> responseObj = new GenricResponse<ItemRequestObject>();
		try {
			ItemRequestObject responce = locationService.changeCategoryTypeStatus(itemRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}
	
	@RequestMapping(path = "getCategoryType", method = RequestMethod.POST)
	public Response<CategoryType> getCategoryType(@RequestBody Request<ItemRequestObject> itemRequestObject) {
		GenricResponse<CategoryType> response = new GenricResponse<CategoryType>();
		try {
			List<CategoryType> categoryTypeList = locationService.getCategoryType(itemRequestObject);
//			return response.createListResponse(categoryTypeList, 200);
			return response.createListResponse(categoryTypeList, 200, String.valueOf(categoryTypeList.size()));
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}

	
}
