package com.datfusrental.services;

import java.io.File;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datfusrental.common.GetBase64Image;
import com.datfusrental.constant.Constant;
import com.datfusrental.entities.CategoryDetails;
import com.datfusrental.entities.CategoryType;
import com.datfusrental.entities.SuperCategoryDetails;
import com.datfusrental.enums.ImageType;
import com.datfusrental.enums.Status;
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
	
	@Autowired
	private GetBase64Image getBase64Image;
	
	
	@Transactional
	public ItemRequestObject addCategoryType(Request<ItemRequestObject> itemRequestObject) 
			throws BizException, Exception {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		categoryHelper.validateItemRequest(itemRequest);
		
		Boolean isValid = jwtTokenUtil.validateJwtToken(itemRequest.getLoginId(), itemRequest.getToken());
		if (!isValid) {
			CategoryType existsCategoryType = categoryHelper.getCategoryTypeByCategoryTypeName(itemRequest.getCategoryTypeName(), itemRequest.getSuperadminId());
			if (existsCategoryType == null) {
				if(itemRequest.getCategoryTypeImage() != null || !itemRequest.getCategoryTypeImage().isEmpty()) {
					
					String basePath = getBase64Image.getPathToUploadFile(ImageType.CATEGORY.name());
					String fileName = "CATTYP"+itemRequest.getCategoryTypeName() + itemRequest.getSuperadminId().replaceAll(" ", "");
					String finalFileName = basePath + File.separator + fileName;
					String serverPath =  fileName;
					finalFileName = getBase64Image.uploadPhotos(itemRequest.getCategoryTypeImage(), finalFileName);
					itemRequest.setCategoryTypeImage(serverPath);
				}
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
	
	
	
	@Transactional
	public ItemRequestObject editCategoryType(Request<ItemRequestObject> itemRequestObject) 
			throws BizException, Exception {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		categoryHelper.validateItemRequest(itemRequest);
		
		Boolean isValid = jwtTokenUtil.validateJwtToken(itemRequest.getLoginId(), itemRequest.getToken());
		if (!isValid) {

			CategoryType categoryType = categoryHelper.getCategoryTypeById(itemRequest.getCategoryTypeId());
			if (categoryType != null) {
//
				categoryType = categoryHelper.getEditedCategoryTypeByReqObj(itemRequest, categoryType);
				categoryType = categoryHelper.updateCategoryType(categoryType);

				itemRequest.setRespCode(Constant.SUCCESS_CODE);
				itemRequest.setRespMesg(Constant.UPDATED_SUCCESS);
				return itemRequest;
			} else {
				itemRequest.setRespCode(Constant.NOT_EXISTS);
				itemRequest.setRespMesg(Constant.DATA_NOT_FOUND);
				return itemRequest;
			}
		} else {
			itemRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
			itemRequest.setRespMesg(Constant.INVALID_TOKEN);
			return itemRequest;
		}
	}
	
	
	
	@Transactional
	public ItemRequestObject changeCategoryTypeStatus(Request<ItemRequestObject> itemRequestObject) 
			throws BizException, Exception {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		categoryHelper.validateItemRequest(itemRequest);
		
		Boolean isValid = jwtTokenUtil.validateJwtToken(itemRequest.getLoginId(), itemRequest.getToken());
		if (!isValid) {

			CategoryType categoryType = categoryHelper.getCategoryTypeById(itemRequest.getCategoryTypeId());
			if (categoryType != null) {

				if(categoryType.getStatus().equalsIgnoreCase(Status.INACTIVE.name())) {
					categoryType.setStatus(Status.ACTIVE.name());
				}else {
					categoryType.setStatus(Status.INACTIVE.name());
				}
				categoryType = categoryHelper.updateCategoryType(categoryType);

				itemRequest.setRespCode(Constant.SUCCESS_CODE);
				itemRequest.setRespMesg(Constant.UPDATED_SUCCESS);
				return itemRequest;
			} else {
				itemRequest.setRespCode(Constant.NOT_EXISTS);
				itemRequest.setRespMesg(Constant.DATA_NOT_FOUND);
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
//		if (!isValid) {

			SuperCategoryDetails existsSuperCategory = categoryHelper.getSuperCategoryDetailsBySuperadminId(itemRequest.getCategoryTypeId(),itemRequest.getSuperCategory(),itemRequest.getSuperadminId());
			if (existsSuperCategory == null) {
				
				if(itemRequest.getSuperCategoryImage() != null || !itemRequest.getSuperCategoryImage().isEmpty()) {
					
					String basePath = getBase64Image.getPathToUploadFile(ImageType.CATEGORY.name());
					String fileName = "SUPCAT"+itemRequest.getSuperCategory() + itemRequest.getSuperadminId().replaceAll(" ", "");
					String finalFileName = basePath + File.separator + fileName;
					String serverPath =  fileName;
					finalFileName = getBase64Image.uploadPhotos(itemRequest.getSuperCategoryImage(), finalFileName);
					itemRequest.setSuperCategoryImage(serverPath);
				}
//
				SuperCategoryDetails superCategoryDetails = categoryHelper.getSuperCategoryDetailsByReqObj(itemRequest);
				superCategoryDetails = categoryHelper.saveSuperCategoryDetails(superCategoryDetails);

				itemRequest.setRespCode(Constant.SUCCESS_CODE);
				itemRequest.setRespMesg(Constant.REGISTERED_SUCCESS);
				return itemRequest;
			} else {
				itemRequest.setRespCode(Constant.ALREADY_EXISTS);
				itemRequest.setRespMesg(Constant.ALREADY_EXISTS_MSG);
				return itemRequest;
			}
//		} else {
//			itemRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
//			itemRequest.setRespMesg(Constant.INVALID_TOKEN);
//			return itemRequest;
//		}
	}
	
	@Transactional
	public ItemRequestObject editSuperCategory(Request<ItemRequestObject> itemRequestObject)
			throws BizException, Exception {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		categoryHelper.validateItemRequest(itemRequest);

		Boolean isValid = jwtTokenUtil.validateJwtToken(itemRequest.getLoginId(), itemRequest.getToken());
		if (!isValid) {

			SuperCategoryDetails superCategoryDetails = categoryHelper.getSuperCategoryDetailsById(itemRequest.getSuperCategoryId());
			if (superCategoryDetails != null) {
				
				superCategoryDetails = categoryHelper.getUpdatedSuperCategoryDetailsByReqObj(itemRequest, superCategoryDetails);
				superCategoryDetails = categoryHelper.updateSuperCategoryDetails(superCategoryDetails);
				
				itemRequest.setRespCode(Constant.SUCCESS_CODE);
				itemRequest.setRespMesg(Constant.UPDATED_SUCCESS);
				return itemRequest;
			} else {
				itemRequest.setRespCode(Constant.NOT_EXISTS);
				itemRequest.setRespMesg(Constant.DATA_NOT_FOUND);
				return itemRequest;
			}
		} else {
			itemRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
			itemRequest.setRespMesg(Constant.INVALID_TOKEN);
			return itemRequest;
		}
	}

	
	@Transactional
	public ItemRequestObject changeSuperCategoryStatus(Request<ItemRequestObject> itemRequestObject)
			throws BizException, Exception {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		categoryHelper.validateItemRequest(itemRequest);
		Boolean isValid = jwtTokenUtil.validateJwtToken(itemRequest.getLoginId(), itemRequest.getToken());
		if (!isValid) {

			SuperCategoryDetails superCategoryDetails = categoryHelper.getSuperCategoryDetailsById(itemRequest.getSuperCategoryId());
			if (superCategoryDetails != null) {
				
				if(superCategoryDetails.getStatus().equalsIgnoreCase(Status.INACTIVE.name())) {
					superCategoryDetails.setStatus(Status.ACTIVE.name());
				} else {
					superCategoryDetails.setStatus(Status.INACTIVE.name());
				}
				categoryHelper.updateSuperCategoryDetails(superCategoryDetails);
				
				itemRequest.setRespCode(Constant.SUCCESS_CODE);
				itemRequest.setRespMesg(Constant.UPDATED_SUCCESS);
				return itemRequest;
			} else {
				itemRequest.setRespCode(Constant.NOT_EXISTS);
				itemRequest.setRespMesg(Constant.DATA_NOT_FOUND);
				return itemRequest;
			}
		} else {
			itemRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
			itemRequest.setRespMesg(Constant.INVALID_TOKEN);
			return itemRequest;
		}
	}
	
	public List<SuperCategoryDetails> getSuperCategoryDetails(Request<ItemRequestObject> itemRequestObject) {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		List<SuperCategoryDetails> superCategoryDetailsList = categoryHelper.getSuperCategoryDetails(itemRequest);
		return superCategoryDetailsList;
	}
	
	
	public ItemRequestObject addCategoryDetails(Request<ItemRequestObject> itemRequestObject) 
			throws BizException, Exception {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		categoryHelper.validateItemRequest(itemRequest);
		
		Boolean isValid = jwtTokenUtil.validateJwtToken(itemRequest.getCreatedBy(), itemRequest.getToken());
//		if (isValid) {
			
			CategoryDetails existsCategoryDetails = categoryHelper.getCategoryDetailsBySuperadminId(itemRequest.getSuperCategoryId(), itemRequest.getCategory(), itemRequest.getSuperadminId());
			if(existsCategoryDetails == null) {
				
				if(itemRequest.getCategoryImage() != null || !itemRequest.getCategoryImage().isEmpty()) {
					
					String basePath = getBase64Image.getPathToUploadFile(ImageType.CATEGORY.name());
					String fileName = "CAT"+itemRequest.getCategory().replaceAll(" ", "") + itemRequest.getSuperadminId();
					String finalFileName = basePath + File.separator + fileName;
					String serverPath =  fileName;
					finalFileName = getBase64Image.uploadPhotos(itemRequest.getCategoryImage(), finalFileName);
					itemRequest.setCategoryImage(serverPath);
				}

				CategoryDetails categoryDetails = categoryHelper.getCategoryDetailsByReqObj(itemRequest);
				categoryDetails = categoryHelper.saveCategoryDetails(categoryDetails);
				
				itemRequest.setRespCode(Constant.SUCCESS_CODE);
				itemRequest.setRespMesg(Constant.REGISTERED_SUCCESS);
				return itemRequest;
			}else {
				itemRequest.setRespCode(Constant.ALREADY_EXISTS);
				itemRequest.setRespMesg(Constant.ALREADY_EXISTS_MSG);
				return itemRequest;
			}
//		} else {
//			itemRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
//			itemRequest.setRespMesg(Constant.INVALID_TOKEN);
//			return itemRequest;
//		}
	}

	public ItemRequestObject editCategoryDetails(Request<ItemRequestObject> itemRequestObject) 
			throws BizException, Exception {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		categoryHelper.validateItemRequest(itemRequest);
		
		Boolean isValid = jwtTokenUtil.validateJwtToken(itemRequest.getCreatedBy(), itemRequest.getToken());
		if (isValid) {
			
			CategoryDetails categoryDetails = categoryHelper.getCategoryDetailsById(itemRequest.getCategoryId());
			if(categoryDetails != null) {
				categoryDetails = categoryHelper.getUpdatedCategoryDetailsByReqObj(itemRequest, categoryDetails);
				categoryDetails = categoryHelper.updateCategoryDetails(categoryDetails);
				
				itemRequest.setRespCode(Constant.SUCCESS_CODE);
				itemRequest.setRespMesg(Constant.UPDATED_SUCCESS);
				return itemRequest;
			}else {
				itemRequest.setRespCode(Constant.NOT_EXISTS);
				itemRequest.setRespMesg(Constant.DATA_NOT_FOUND);
				return itemRequest;
			}
		} else {
			itemRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
			itemRequest.setRespMesg(Constant.INVALID_TOKEN);
			return itemRequest;
		}
	}
	
	public ItemRequestObject changeCategoryStatus(Request<ItemRequestObject> itemRequestObject) 
			throws BizException, Exception {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		categoryHelper.validateItemRequest(itemRequest);
		
		Boolean isValid = jwtTokenUtil.validateJwtToken(itemRequest.getCreatedBy(), itemRequest.getToken());
		if (isValid) {
			
			CategoryDetails categoryDetails = categoryHelper.getCategoryDetailsById(itemRequest.getCategoryId());
			
			if(categoryDetails != null) {
				System.out.println(categoryDetails.getStatus());
				if(categoryDetails.getStatus().equalsIgnoreCase(Status.INACTIVE.name())) {
					categoryDetails.setStatus(Status.ACTIVE.name());
				} else {
					categoryDetails.setStatus(Status.INACTIVE.name());
				}
				categoryHelper.updateCategoryDetails(categoryDetails);
				
				itemRequest.setRespCode(Constant.SUCCESS_CODE);
				itemRequest.setRespMesg(Constant.UPDATED_SUCCESS);
				return itemRequest;
			}else {
				itemRequest.setRespCode(Constant.NOT_EXISTS);
				itemRequest.setRespMesg(Constant.DATA_NOT_FOUND);
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


	public ItemRequestObject addSubCategoryDetails(Request<ItemRequestObject> itemRequestObject) 
			throws BizException, Exception {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		categoryHelper.validateItemRequest(itemRequest);
		
		Boolean isValid = jwtTokenUtil.validateJwtToken(itemRequest.getCreatedBy(), itemRequest.getToken());
		if (isValid) {
			SubCategoryDetails existsSubCategoryMaster = categoryHelper.getSubCategoryDetailsByCategoryIdAndSuperadminId(itemRequest.getSubCategory(), itemRequest.getSuperadminId());
			if(existsSubCategoryMaster == null) {
				
				if(itemRequest.getSubCategoryImage() != null || !itemRequest.getSubCategoryImage().isEmpty()) {
					
					String basePath = getBase64Image.getPathToUploadFile(ImageType.CATEGORY.name());
					String fileName = "SUBCAT"+itemRequest.getSubCategory().replaceAll(" ", "") + itemRequest.getSuperadminId();
					String finalFileName = basePath + File.separator + fileName;
					String serverPath =  fileName;
					finalFileName = getBase64Image.uploadPhotos(itemRequest.getSubCategoryImage(), finalFileName);
					itemRequest.setSubCategoryImage(serverPath);
				}
				
				SubCategoryDetails subCategoryDetails = categoryHelper.getSubCategoryDetailsByReqObj(itemRequest);
				subCategoryDetails = categoryHelper.saveSubCategoryDetails(subCategoryDetails);
				
				itemRequest.setRespCode(Constant.SUCCESS_CODE);
				itemRequest.setRespMesg(Constant.REGISTERED_SUCCESS);
				return itemRequest;
			}else {
				itemRequest.setRespCode(Constant.ALREADY_EXISTS);
				itemRequest.setRespMesg(Constant.ALREADY_EXISTS_MSG);
				return itemRequest;
			}
		}else {
			itemRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
			itemRequest.setRespMesg(Constant.INVALID_TOKEN);
			return itemRequest;
		}
	}
	
	
	public ItemRequestObject updateSubCategoryDetails(Request<ItemRequestObject> itemRequestObject) 
			throws BizException, Exception {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		categoryHelper.validateItemRequest(itemRequest);
		
		Boolean isValid = jwtTokenUtil.validateJwtToken(itemRequest.getCreatedBy(), itemRequest.getToken());
//		if (!isValid) {
			SubCategoryDetails subCategoryDetails = categoryHelper.getSubCategoryDetailsByCategoryIdAndSuperadminId(itemRequest.getSubCategory(), itemRequest.getSuperadminId());
			if(subCategoryDetails != null) {
				
				if(itemRequest.getSubCategoryImage() != null || !itemRequest.getSubCategoryImage().isEmpty()) {
					
					String basePath = getBase64Image.getPathToUploadFile(ImageType.CATEGORY.name());
					String fileName = "SUBCAT"+itemRequest.getSubCategory().replaceAll(" ", "") + itemRequest.getSuperadminId();
					String finalFileName = basePath + File.separator + fileName;
					String serverPath =  fileName;
					finalFileName = getBase64Image.uploadPhotos(itemRequest.getSubCategoryImage(), finalFileName);
					itemRequest.setSubCategoryImage(serverPath);
				}
				
				subCategoryDetails = categoryHelper.getUpdatedSubCategoryDetailsByReqObj(subCategoryDetails, itemRequest);
				subCategoryDetails = categoryHelper.updateSubCategoryDetails(subCategoryDetails);
				
				itemRequest.setRespCode(Constant.SUCCESS_CODE);
				itemRequest.setRespMesg(Constant.UPDATED_SUCCESS);
				return itemRequest;
			}else {
				itemRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
				itemRequest.setRespMesg(Constant.INVALID_TOKEN);
				return itemRequest;
			}
//		}else {
//			itemRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
//			itemRequest.setRespMesg(Constant.INVALID_TOKEN);
//			return itemRequest;
//		}
	}
	
	public ItemRequestObject changeSubCategoryStatus(Request<ItemRequestObject> itemRequestObject) 
			throws BizException, Exception {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		categoryHelper.validateItemRequest(itemRequest);
		
		Boolean isValid = jwtTokenUtil.validateJwtToken(itemRequest.getCreatedBy(), itemRequest.getToken());
		if (isValid) {
			
			SubCategoryDetails subCategory = categoryHelper.getSubCategoryDetailsById(itemRequest.getSubCategoryId());
			
			if(subCategory != null) {

				if(subCategory.getStatus().equalsIgnoreCase(Status.INACTIVE.name())) {
					subCategory.setStatus(Status.ACTIVE.name());
				} else {
					subCategory.setStatus(Status.INACTIVE.name());
				}
				categoryHelper.updateSubCategoryDetails(subCategory);
				
				itemRequest.setRespCode(Constant.SUCCESS_CODE);
				itemRequest.setRespMesg(Constant.UPDATED_SUCCESS);
				return itemRequest;
			}else {
				itemRequest.setRespCode(Constant.NOT_EXISTS);
				itemRequest.setRespMesg(Constant.DATA_NOT_FOUND);
				return itemRequest;
			}
		} else {
			itemRequest.setRespCode(Constant.INVALID_TOKEN_CODE);
			itemRequest.setRespMesg(Constant.INVALID_TOKEN);
			return itemRequest;
		}
	}


	public List<SubCategoryDetails> getSubCategoryDetails(Request<ItemRequestObject> itemRequestObject) {
		ItemRequestObject itemRequest = itemRequestObject.getPayload();
		List<SubCategoryDetails> subCategoryMasterList = categoryHelper.getSubCategoryDetails(itemRequest);
		return subCategoryMasterList;
	}







	


}