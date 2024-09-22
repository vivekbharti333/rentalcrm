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
import com.datfusrental.entities.CategoryType;
import com.datfusrental.enums.ImageType;
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
	private CategoryService categoryService;
	
	@Autowired
	private GetBase64Image getBase64Image;
	
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
	
	@RequestMapping(path = "editCategoryType", method = RequestMethod.POST)
	public Response<ItemRequestObject> editCategoryType(@RequestBody Request<ItemRequestObject> itemRequestObject,
			HttpServletRequest request) {
		GenricResponse<ItemRequestObject> responseObj = new GenricResponse<ItemRequestObject>();
		try {
			ItemRequestObject responce = categoryService.editCategoryType(itemRequestObject);
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
			ItemRequestObject responce = categoryService.changeCategoryTypeStatus(itemRequestObject);
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
	
	@RequestMapping(path = "editSuperCategory", method = RequestMethod.POST)
	public Response<ItemRequestObject> editSuperCategory(@RequestBody Request<ItemRequestObject> itemRequestObject,
			HttpServletRequest request) {
		GenricResponse<ItemRequestObject> responseObj = new GenricResponse<ItemRequestObject>();
		try {
			ItemRequestObject responce = categoryService.editSuperCategory(itemRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}
	
	@RequestMapping(path = "changeSuperCategoryStatus", method = RequestMethod.POST)
	public Response<ItemRequestObject> changeSuperCategoryStatus(@RequestBody Request<ItemRequestObject> itemRequestObject,
			HttpServletRequest request) {
		GenricResponse<ItemRequestObject> responseObj = new GenricResponse<ItemRequestObject>();
		try {
			ItemRequestObject responce = categoryService.changeSuperCategoryStatus(itemRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}

	@RequestMapping(path = "getSuperCategoryDetails", method = RequestMethod.POST)
	public Response<ItemRequestObject> getSuperCategoryDetails(@RequestBody Request<ItemRequestObject> itemRequestObject) {
		GenricResponse<ItemRequestObject> response = new GenricResponse<ItemRequestObject>();
		try {
			List<ItemRequestObject> superCategoryDetailsList = categoryService.getSuperCategoryDetails(itemRequestObject);
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
	
	@RequestMapping(path = "editCategoryDetails", method = RequestMethod.POST)
	public Response<ItemRequestObject> editCategoryDetails(@RequestBody Request<ItemRequestObject> itemRequestObject,
			HttpServletRequest request) {
		GenricResponse<ItemRequestObject> responseObj = new GenricResponse<ItemRequestObject>();
		try {
			ItemRequestObject responce = categoryService.editCategoryDetails(itemRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}
	
	@RequestMapping(path = "changeCategoryStatus", method = RequestMethod.POST)
	public Response<ItemRequestObject> changeCategoryStatus(@RequestBody Request<ItemRequestObject> itemRequestObject,
			HttpServletRequest request) {
		GenricResponse<ItemRequestObject> responseObj = new GenricResponse<ItemRequestObject>();
		try {
			ItemRequestObject responce = categoryService.changeCategoryStatus(itemRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}

	@RequestMapping(path = "getCategoryDetails", method = RequestMethod.POST)
	public Response<ItemRequestObject> getCategoryDetails(@RequestBody Request<ItemRequestObject> itemRequestObject) {
		GenricResponse<ItemRequestObject> response = new GenricResponse<ItemRequestObject>();
		try {
			List<ItemRequestObject> categoryList = categoryService.getCategoryDetails(itemRequestObject);
			return response.createListResponse(categoryList, 200, String.valueOf(categoryList.size()));
//			return response.createListResponse(categoryMasterList, 200);
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
	
	@RequestMapping(path = "editSubCategoryDetails", method = RequestMethod.POST)
	public Response<ItemRequestObject> editSubCategoryDetails(@RequestBody Request<ItemRequestObject> itemRequestObject,
			HttpServletRequest request) {
		GenricResponse<ItemRequestObject> responseObj = new GenricResponse<ItemRequestObject>();
		try {
			ItemRequestObject responce = categoryService.editSubCategoryDetails(itemRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}
	
	@RequestMapping(path = "changeSubCategoryStatus", method = RequestMethod.POST)
	public Response<ItemRequestObject> changeSubCategoryStatus(@RequestBody Request<ItemRequestObject> itemRequestObject,
			HttpServletRequest request) {
		GenricResponse<ItemRequestObject> responseObj = new GenricResponse<ItemRequestObject>();
		try {
			ItemRequestObject responce = categoryService.changeSubCategoryStatus(itemRequestObject);
			return responseObj.createSuccessResponse(responce, Constant.SUCCESS_CODE);
		} catch (BizException e) {
			return responseObj.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj.createErrorResponse(Constant.INTERNAL_SERVER_ERR, e.getMessage());
		}
	}

	@RequestMapping(path = "getSubCategoryDetails", method = RequestMethod.POST)
	public Response<ItemRequestObject> getSubCategoryDetails(@RequestBody Request<ItemRequestObject> itemRequestObject) {
		GenricResponse<ItemRequestObject> response = new GenricResponse<ItemRequestObject>();
		try {
			List<ItemRequestObject> subCategoryMasterList = categoryService.getSubCategoryDetails(itemRequestObject);
			return response.createListResponse(subCategoryMasterList, 200, String.valueOf(subCategoryMasterList.size()));
//			return response.createListResponse(subCategoryMasterList, 200);
		} catch (Exception e) {
			e.printStackTrace();
			return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
		}
	}
	
	@RequestMapping(value = "/categoryImage/{imageName}", method = RequestMethod.GET)
	public HttpEntity<byte[]> getOrder(@PathVariable String imageName, HttpServletRequest request) throws Exception {

		getBase64Image.getServerPath(request);
		byte[] image;

		String basePath = getBase64Image.getPathToUploadFile(ImageType.CATEGORY.name());
		String file = basePath + File.separator + imageName;
		try {
			image = FileUtils.readFileToByteArray(new File(file));
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_PNG);
			headers.setContentLength(image.length);
			return new HttpEntity<byte[]>(image, headers);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
