package com.datfusrental.object.request;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import lombok.Data;

@Data
public class ItemRequestObject {
	
	private Long id;
	private Long categoryTypeId;
	private String categoryTypeImage;
	private String categoryTypeName;
	private String isChecked;
	private Long superCategoryId;
	private String superCategoryImage;
	private String superCategory;
	private Long categoryId;
	private String categoryImage;
	private String category;
	private String subCategoryImage;
	private Long subCategoryId;
	private String subCategory;
	
	private String location;
	private String locationType;
	
	
	private String requestedFor;
	private String token;
	private String superadminId;
	private Date createdAt;
	private Date updatedAt;
	private String createdBy;
	private String loginId;
	
	private int respCode;
	private String respMesg;
	
}
