package com.datfusrental.services;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datfusrental.constant.Constant;
import com.datfusrental.entities.CategoryDetails;
import com.datfusrental.entities.CategoryMaster;
import com.datfusrental.entities.SubCategoryMaster;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.helper.CategoryHelper;
import com.datfusrental.jwt.JwtTokenUtil;
import com.datfusrental.object.request.ItemRequestObject;
import com.datfusrental.object.request.Request;

@Service
public class CategoryService {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private CategoryHelper categoryHelper;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Transactional
	public ItemRequestObject addCategoryMaster(Request<ItemRequestObject> itemRequestObject)
			throws BizException, Exception {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		categoryHelper.validateItemRequest(itemRequest);

		Boolean isValid = jwtTokenUtil.validateJwtToken(itemRequest.getCreatedBy(), itemRequest.getToken());
		if (!isValid) {

			CategoryMaster existsCategoryMaster = categoryHelper.getCategoryMasterByCategory(itemRequest.getCategory());
			if (existsCategoryMaster == null) {
//
				CategoryMaster categoryMaster = categoryHelper.getCategoryMasterByReqObj(itemRequest);
				categoryMaster = categoryHelper.saveCategoryMaster(categoryMaster);

				itemRequest.setRespCode(Constant.SUCCESS_CODE);
				itemRequest.setRespMesg(Constant.REGISTERED_SUCCESS);
				return itemRequest;
			} else {
				existsCategoryMaster = categoryHelper.getUpdatedCategoryMasterByReqObj(existsCategoryMaster, itemRequest);
				existsCategoryMaster = categoryHelper.updateCategoryMaster(existsCategoryMaster);
				
				itemRequest.setRespCode(Constant.SUCCESS_CODE);
				itemRequest.setRespMesg(Constant.UPDATED_SUCCESS);
				return itemRequest;
			}
		} else {
			itemRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
			itemRequest.setRespMesg(Constant.INVALID_TOKEN);
			return itemRequest;
		}
	}

	
	public List<CategoryMaster> getCategoryMaster(Request<ItemRequestObject> itemRequestObject) {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		List<CategoryMaster> categoryMasterList = categoryHelper.getUserRoleMaster(itemRequest);
		return categoryMasterList;
	}


	public ItemRequestObject addSubCategoryMaster(Request<ItemRequestObject> itemRequestObject) 
			throws BizException, Exception {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		categoryHelper.validateItemRequest(itemRequest);
		
		Boolean isValid = jwtTokenUtil.validateJwtToken(itemRequest.getCreatedBy(), itemRequest.getToken());
		if (!isValid) {
			SubCategoryMaster existsSubCategoryMaster = categoryHelper.getSubCategoryMasterByCategory(itemRequest.getSubCategory());
			if(existsSubCategoryMaster == null) {
				
				SubCategoryMaster subCategoryMaster = categoryHelper.getSubCategoryMasterByReqObj(itemRequest);
				subCategoryMaster = categoryHelper.saveSubCategoryMaster(subCategoryMaster);
				
				itemRequest.setRespCode(Constant.SUCCESS_CODE);
				itemRequest.setRespMesg(Constant.REGISTERED_SUCCESS);
				return itemRequest;
			}else {
				existsSubCategoryMaster = categoryHelper.getUpdatedSubCategoryMasterByReqObj(existsSubCategoryMaster, itemRequest);
				existsSubCategoryMaster = categoryHelper.updateSubCategoryMaster(existsSubCategoryMaster);
				
				itemRequest.setRespCode(Constant.SUCCESS_CODE);
				itemRequest.setRespMesg(Constant.UPDATED_SUCCESS);
				return itemRequest;
			}
		}else {
			itemRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
			itemRequest.setRespMesg(Constant.INVALID_TOKEN);
			return itemRequest;
		}
	}


	public List<SubCategoryMaster> getSubCategoryMaster(Request<ItemRequestObject> itemRequestObject) {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		List<SubCategoryMaster> subCategoryMasterList = categoryHelper.getSubCategoryMaster(itemRequest);
		return subCategoryMasterList;
	}


	public ItemRequestObject addCategoryDetails(Request<ItemRequestObject> itemRequestObject) 
			throws BizException, Exception {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		categoryHelper.validateItemRequest(itemRequest);
		
		Boolean isValid = jwtTokenUtil.validateJwtToken(itemRequest.getCreatedBy(), itemRequest.getToken());
		if (!isValid) {
			
			CategoryDetails existsCategoryDetails = categoryHelper.getCategoryDetailsBySuperadminIdCategoryType(itemRequest.getSuperadminId(), itemRequest.getCategoryType());
			if(existsCategoryDetails == null) {
				CategoryDetails categoryDetails = categoryHelper.getCategoryDetailsByReqObj(itemRequest);
				categoryDetails = categoryHelper.saveCategoryDetails(categoryDetails);
				
				itemRequest.setRespCode(Constant.SUCCESS_CODE);
				itemRequest.setRespMesg(Constant.REGISTERED_SUCCESS);
				return itemRequest;
			}else {
				existsCategoryDetails = categoryHelper.getUpdatedCategoryDetailsByReqObj(itemRequest, existsCategoryDetails);
				existsCategoryDetails = categoryHelper.updateCategoryDetails(existsCategoryDetails);
				
				itemRequest.setRespCode(Constant.SUCCESS_CODE);
				itemRequest.setRespMesg(Constant.UPDATED_SUCCESS);
				return itemRequest;
			}
		} else {
			itemRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
			itemRequest.setRespMesg(Constant.INVALID_TOKEN);
			return itemRequest;
		}
	}


	public List<CategoryDetails> getCategoryDetails(Request<ItemRequestObject> itemRequestObject) {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		List<CategoryDetails> categoryDetailsList = categoryHelper.getCategoryDetails(itemRequest);
		return categoryDetailsList;
	}


}
