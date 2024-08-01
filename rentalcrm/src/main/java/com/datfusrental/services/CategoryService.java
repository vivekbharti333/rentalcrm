package com.datfusrental.services;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datfusrental.constant.Constant;
import com.datfusrental.entities.CategoryDetails;
import com.datfusrental.entities.CategoryType;
import com.datfusrental.entities.SuperCategoryDetails;
import com.datfusrental.entities.SubCategoryDetails;
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
	public ItemRequestObject addCategoryType(Request<ItemRequestObject> itemRequestObject) 
			throws BizException, Exception {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		categoryHelper.validateItemRequest(itemRequest);
		
		Boolean isValid = jwtTokenUtil.validateJwtToken(itemRequest.getLoginId(), itemRequest.getToken());
		if (!isValid) {

			CategoryType existsCategoryType = categoryHelper.getCategoryTypeByCategoryTypeName(itemRequest.getCategoryTypeName(), itemRequest.getSuperadminId());
			if (existsCategoryType == null) {
//
				CategoryType categoryType = categoryHelper.getCategoryTypeByReqObj(itemRequest);
				categoryType = categoryHelper.saveCategoryType(categoryType);

				itemRequest.setRespCode(Constant.SUCCESS_CODE);
				itemRequest.setRespMesg(Constant.REGISTERED_SUCCESS);
				return itemRequest;
			} else {
				itemRequest.setRespCode(Constant.ALREADY_EXISTS);
				itemRequest.setRespMesg(Constant.ALREADY_EXISTS_MSG);
				return itemRequest;
			}
		} else {
			itemRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
			itemRequest.setRespMesg(Constant.INVALID_TOKEN);
			return itemRequest;
		}
	}
	

	public List<CategoryType> getCategoryType(Request<ItemRequestObject> itemRequestObject) {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		List<CategoryType> categoryType = categoryHelper.getCategoryType(itemRequest);
		return categoryType;
	}

	@Transactional
	public ItemRequestObject addSuperCategory(Request<ItemRequestObject> itemRequestObject)
			throws BizException, Exception {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		categoryHelper.validateItemRequest(itemRequest);

		Boolean isValid = jwtTokenUtil.validateJwtToken(itemRequest.getLoginId(), itemRequest.getToken());
		if (!isValid) {

			SuperCategoryDetails existsSuperCategory = categoryHelper.getSuperCategoryDetailsBySuperadminId(itemRequest.getSuperadminId());
			if (existsSuperCategory == null) {
//
				SuperCategoryDetails superCategoryDetails = categoryHelper.getSuperCategoryDetailsByReqObj(itemRequest);
				superCategoryDetails = categoryHelper.saveSuperCategoryDetails(superCategoryDetails);

				itemRequest.setRespCode(Constant.SUCCESS_CODE);
				itemRequest.setRespMesg(Constant.REGISTERED_SUCCESS);
				return itemRequest;
			} else {
				existsSuperCategory = categoryHelper.getUpdatedSuperCategoryDetailsByReqObj(existsSuperCategory, itemRequest);
				existsSuperCategory = categoryHelper.updateSuperCategoryDetails(existsSuperCategory);
				
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

	
	public List<SuperCategoryDetails> getSuperCategoryDetailsByCategoryTypeId(Request<ItemRequestObject> itemRequestObject) {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		List<SuperCategoryDetails> superCategoryDetailsList = categoryHelper.getSuperCategoryDetailsByCategoryTypeId(itemRequest);
		return superCategoryDetailsList;
	}
	
	
	public ItemRequestObject addCategoryDetails(Request<ItemRequestObject> itemRequestObject) 
			throws BizException, Exception {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		categoryHelper.validateItemRequest(itemRequest);
		
		Boolean isValid = jwtTokenUtil.validateJwtToken(itemRequest.getCreatedBy(), itemRequest.getToken());
		if (!isValid) {
			
			CategoryDetails existsCategoryDetails = categoryHelper.getCategoryDetailsBySuperadminId(itemRequest.getCategory(),itemRequest.getSuperadminId());
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


	public List<CategoryDetails> getCategoryDetailsBySuperCategoryId(Request<ItemRequestObject> itemRequestObject) {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		List<CategoryDetails> categoryDetailsList = categoryHelper.getCategoryDetailsBySuperCategoryId(itemRequest);
		return categoryDetailsList;
	}


	public ItemRequestObject addSubCategoryDetails(Request<ItemRequestObject> itemRequestObject) 
			throws BizException, Exception {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		categoryHelper.validateItemRequest(itemRequest);
		
		Boolean isValid = jwtTokenUtil.validateJwtToken(itemRequest.getCreatedBy(), itemRequest.getToken());
		if (!isValid) {
			SubCategoryDetails existsSubCategoryMaster = categoryHelper.getSubCategoryDetailsByCategoryIdAndSuperadminId(itemRequest.getSubCategory(), itemRequest.getSuperadminId());
			if(existsSubCategoryMaster == null) {
				
				SubCategoryDetails subCategoryDetails = categoryHelper.getSubCategoryDetailsByReqObj(itemRequest);
				subCategoryDetails = categoryHelper.saveSubCategoryDetails(subCategoryDetails);
				
				itemRequest.setRespCode(Constant.SUCCESS_CODE);
				itemRequest.setRespMesg(Constant.REGISTERED_SUCCESS);
				return itemRequest;
			}else {
				existsSubCategoryMaster = categoryHelper.getUpdatedSubCategoryDetailsByReqObj(existsSubCategoryMaster, itemRequest);
				existsSubCategoryMaster = categoryHelper.updateSubCategoryDetails(existsSubCategoryMaster);
				
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


	public List<SubCategoryDetails> getSubCategoryDetailsByCategoryId(Request<ItemRequestObject> itemRequestObject) {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		List<SubCategoryDetails> subCategoryMasterList = categoryHelper.getSubCategoryDetailsByCategoryId(itemRequest);
		return subCategoryMasterList;
	}







	


}
