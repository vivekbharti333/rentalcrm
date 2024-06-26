package com.datfusrental.object.request;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class ItemRequestObject {
	
	private Long id;
	private String categoryType;
	private Long categoryId;
	private String category;
	private String subCategory;
	private String categoryAndSubCategoryIds;
	
	private String requestedFor;
	private String token;
	private String superadminId;
	private Date createdAt;
	private Date updatedAt;
	private String createdBy;
	
	private int respCode;
	private String respMesg;
	
}
