package com.datfusrental.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.datfusrental.constant.Constant;
import com.datfusrental.entities.CategoryDetails;
import com.datfusrental.entities.CategoryType;
import com.datfusrental.entities.SuperCategoryDetails;
import com.datfusrental.entities.SubCategoryDetails;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.ItemRequestObject;
import com.datfusrental.object.request.Request;
import com.datfusrental.object.response.GenricResponse;
import com.datfusrental.object.response.Response;
import com.datfusrental.services.CategoryService;

@CrossOrigin(origins = "*")
@RestController
public class CategoryController {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	CategoryService categoryService;
	
	@RequestMapping(path = "addCategoryType", method = RequestMethod.POST)
	public Response<ItemRequestObject> addCategoryType(@RequestBody Request<ItemRequestObject> itemRequestObject,
			HttpServletRequest request) {
		GenricResponse<ItemRequestObject> responseObj = new GenricResponse<ItemRequestObject>();
		try {
			ItemRequestObject responce = categoryService.addCategoryType(itemRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}
	
	@RequestMapping(path = "getCategoryType", method = RequestMethod.POST)
	public Response<CategoryType> getCategoryType(
			@RequestBody Request<ItemRequestObject> itemRequestObject) {
		GenricResponse<CategoryType> response = new GenricResponse<CategoryType>();
		try {
			List<CategoryType> categoryTypeList = categoryService.getCategoryType(itemRequestObject);
//			return response.createListResponse(categoryTypeList, 200);
			return response.createListResponse(categoryTypeList, 200, String.valueOf(categoryTypeList.size()));
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}

	@RequestMapping(path = "addSuperCategory", method = RequestMethod.POST)
	public Response<ItemRequestObject> addSuperCategory(@RequestBody Request<ItemRequestObject> itemRequestObject,
			HttpServletRequest request) {
		GenricResponse<ItemRequestObject> responseObj = new GenricResponse<ItemRequestObject>();
		try {
			ItemRequestObject responce = categoryService.addSuperCategory(itemRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}

	@RequestMapping(path = "getSuperCategoryDetails", method = RequestMethod.POST)
	public Response<SuperCategoryDetails> getSuperCategoryDetails(@RequestBody Request<ItemRequestObject> itemRequestObject) {
		GenricResponse<SuperCategoryDetails> response = new GenricResponse<SuperCategoryDetails>();
		try {
			List<SuperCategoryDetails> superCategoryDetailsList = categoryService.getSuperCategoryDetails(itemRequestObject);
//			return response.createListResponse(superCategoryDetailsList, 200);
			return response.createListResponse(superCategoryDetailsList, 200, String.valueOf(superCategoryDetailsList.size()));
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}

	@RequestMapping(path = "addCategoryDetails", method = RequestMethod.POST)
	public Response<ItemRequestObject> addCategoryDetails(@RequestBody Request<ItemRequestObject> itemRequestObject,
			HttpServletRequest request) {
		GenricResponse<ItemRequestObject> responseObj = new GenricResponse<ItemRequestObject>();
		try {
			ItemRequestObject responce = categoryService.addCategoryDetails(itemRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}

	@RequestMapping(path = "getCategoryDetailsBySuperCategoryId", method = RequestMethod.POST)
	public Response<CategoryDetails> getCategoryDetailsBySuperCategoryId(@RequestBody Request<ItemRequestObject> itemRequestObject) {
		GenricResponse<CategoryDetails> response = new GenricResponse<CategoryDetails>();
		try {
			List<CategoryDetails> categoryMasterList = categoryService.getCategoryDetailsBySuperCategoryId(itemRequestObject);
			return response.createListResponse(categoryMasterList, 200);
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}

	@RequestMapping(path = "addSubCategoryDetails", method = RequestMethod.POST)
	public Response<ItemRequestObject> addSubCategoryDetails(@RequestBody Request<ItemRequestObject> itemRequestObject,
			HttpServletRequest request) {
		GenricResponse<ItemRequestObject> responseObj = new GenricResponse<ItemRequestObject>();
		try {
			ItemRequestObject responce = categoryService.addSubCategoryDetails(itemRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}

	@RequestMapping(path = "getSubCategoryDetailsByCategoryId", method = RequestMethod.POST)
	public Response<SubCategoryDetails> getSubCategoryDetailsByCategoryId(
			@RequestBody Request<ItemRequestObject> itemRequestObject) {
		GenricResponse<SubCategoryDetails> response = new GenricResponse<SubCategoryDetails>();
		try {
			List<SubCategoryDetails> subCategoryMasterList = categoryService.getSubCategoryDetailsByCategoryId(itemRequestObject);
			return response.createListResponse(subCategoryMasterList, 200);
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}

}
