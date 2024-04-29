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
import com.datfusrental.entities.CategoryMaster;
import com.datfusrental.entities.SubCategoryMaster;
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

	@RequestMapping(path = "addCategoryMaster", method = RequestMethod.POST)
	public Response<ItemRequestObject> addCategoryMaster(@RequestBody Request<ItemRequestObject> itemRequestObject,
			HttpServletRequest request) {
		GenricResponse<ItemRequestObject> responseObj = new GenricResponse<ItemRequestObject>();
		try {
			ItemRequestObject responce = categoryService.addCategoryMaster(itemRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}

	@RequestMapping(path = "getCategoryMaster", method = RequestMethod.POST)
	public Response<CategoryMaster> getCategoryMaster(@RequestBody Request<ItemRequestObject> itemRequestObject) {
		GenricResponse<CategoryMaster> response = new GenricResponse<CategoryMaster>();
		try {
			List<CategoryMaster> categoryMasterList = categoryService.getCategoryMaster(itemRequestObject);
			return response.createListResponse(categoryMasterList, 200);
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}
	
	@RequestMapping(path = "addSubCategoryMaster", method = RequestMethod.POST)
	public Response<ItemRequestObject> addSubCategoryMaster(@RequestBody Request<ItemRequestObject> itemRequestObject,
			HttpServletRequest request) {
		GenricResponse<ItemRequestObject> responseObj = new GenricResponse<ItemRequestObject>();
		try {
			ItemRequestObject responce = categoryService.addSubCategoryMaster(itemRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}
	
	@RequestMapping(path = "getSubCategoryMaster", method = RequestMethod.POST)
	public Response<SubCategoryMaster> getSubCategoryMaster(@RequestBody Request<ItemRequestObject> itemRequestObject) {
		GenricResponse<SubCategoryMaster> response = new GenricResponse<SubCategoryMaster>();
		try {
			List<SubCategoryMaster> subCategoryMasterList = categoryService.getSubCategoryMaster(itemRequestObject);
			return response.createListResponse(subCategoryMasterList, 200);
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

	@RequestMapping(path = "getCategoryDetails", method = RequestMethod.POST)
	public Response<CategoryDetails> getCategoryDetails(@RequestBody Request<ItemRequestObject> itemRequestObject) {
		GenricResponse<CategoryDetails> response = new GenricResponse<CategoryDetails>();
		try {
			List<CategoryDetails> categoryMasterList = categoryService.getCategoryDetails(itemRequestObject);
			return response.createListResponse(categoryMasterList, 200);
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}
}
