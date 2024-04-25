package com.datfusrental.helper;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.datfusrental.constant.Constant;
import com.datfusrental.dao.CategoryDetailsDao;
import com.datfusrental.dao.CategoryMasterDao;
import com.datfusrental.dao.SubCategoryMasterDao;
import com.datfusrental.entities.CategoryDetails;
import com.datfusrental.entities.CategoryMaster;
import com.datfusrental.entities.SubCategoryMaster;
import com.datfusrental.entities.UserRoleMaster;
import com.datfusrental.enums.Status;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.ItemRequestObject;
import com.datfusrental.object.request.UserRequestObject;

@Component
public class CategoryHelper {

	@Autowired
	private CategoryMasterDao categoryMasterDao;

	@Autowired
	private SubCategoryMasterDao subCategoryMasterDao;
	
	@Autowired
	private CategoryDetailsDao categoryDetailsDao;

	public void validateItemRequest(ItemRequestObject itemRequestObject) throws BizException {
		if (itemRequestObject == null) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Bad Request Object Null");
		}
	}

	@Transactional
	public CategoryMaster getCategoryMasterByCategory(String categoryName) {

		CriteriaBuilder criteriaBuilder = categoryMasterDao.getSession().getCriteriaBuilder();
		CriteriaQuery<CategoryMaster> criteriaQuery = criteriaBuilder.createQuery(CategoryMaster.class);
		Root<CategoryMaster> root = criteriaQuery.from(CategoryMaster.class);
		Predicate restriction = criteriaBuilder.equal(root.get("categoryName"), categoryName);
		criteriaQuery.where(restriction);
		CategoryMaster categoryMaster = categoryMasterDao.getSession().createQuery(criteriaQuery).uniqueResult();
		return categoryMaster;
	}

	@Transactional
	public SubCategoryMaster getSubCategoryMasterByCategory(String subCategoryName) {

		CriteriaBuilder criteriaBuilder = subCategoryMasterDao.getSession().getCriteriaBuilder();
		CriteriaQuery<SubCategoryMaster> criteriaQuery = criteriaBuilder.createQuery(SubCategoryMaster.class);
		Root<SubCategoryMaster> root = criteriaQuery.from(SubCategoryMaster.class);
		Predicate restriction = criteriaBuilder.equal(root.get("subCategoryName"), subCategoryName);
		criteriaQuery.where(restriction);
		SubCategoryMaster subCategoryMaster = subCategoryMasterDao.getSession().createQuery(criteriaQuery)
				.uniqueResult();
		return subCategoryMaster;
	}
	
	@Transactional
	public CategoryDetails getCategoryDetailsBySuperadminIdCategoryType(String superadminId, String categoryType) {

		CriteriaBuilder criteriaBuilder = categoryDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<CategoryDetails> criteriaQuery = criteriaBuilder.createQuery(CategoryDetails.class);
		Root<CategoryDetails> root = criteriaQuery.from(CategoryDetails.class);
		Predicate restriction1 = criteriaBuilder.equal(root.get("superadminId"), superadminId);
		Predicate restriction2 = criteriaBuilder.equal(root.get("categoryType"), categoryType);
		criteriaQuery.where(restriction1, restriction2);
		CategoryDetails categoryDetails = categoryDetailsDao.getSession().createQuery(criteriaQuery)
				.uniqueResult();
		return categoryDetails;
	}

	public CategoryMaster getCategoryMasterByReqObj(ItemRequestObject itemRequest) {

		CategoryMaster categoryMaster = new CategoryMaster();

		categoryMaster.setCategoryName(itemRequest.getCategoryName());
		categoryMaster.setStatus(Status.ACTIVE.name());
		categoryMaster.setCreatedAt(new Date());
		categoryMaster.setUpdatedAt(new Date());

		return categoryMaster;
	}

	@Transactional
	public CategoryMaster saveCategoryMaster(CategoryMaster categoryMaster) {
		categoryMasterDao.persist(categoryMaster);
		return categoryMaster;
	}

	public CategoryMaster getUpdatedCategoryMasterByReqObj(CategoryMaster categoryMaster,
			ItemRequestObject itemRequest) {

		categoryMaster.setCategoryName(itemRequest.getCategory());
		categoryMaster.setUpdatedAt(new Date());
		return categoryMaster;
	}

	@Transactional
	public CategoryMaster updateCategoryMaster(CategoryMaster categoryMaster) {
		categoryMasterDao.update(categoryMaster);
		return categoryMaster;
	}

	@SuppressWarnings("unchecked")
	public List<CategoryMaster> getUserRoleMaster(ItemRequestObject itemRequest) {
		if (itemRequest.getRequestedFor().equals("ALL")) {
			List<CategoryMaster> results = categoryMasterDao.getEntityManager()
					.createQuery("SELECT CM FROM CategoryMaster CM ORDER BY CM.id ASC").getResultList();
			return results;
		} else if (itemRequest.getRequestedFor().equals("ACTIVE")) {
			List<CategoryMaster> results = categoryMasterDao.getEntityManager()
					.createQuery("SELECT CM FROM CategoryMaster CM WHERE CM.status =:status ORDER BY CM.id ASC")
					.setParameter("status", Status.ACTIVE.name()).getResultList();
			return results;
		}
		return null;
	}

	public SubCategoryMaster getSubCategoryMasterByReqObj(ItemRequestObject itemRequest) {

		SubCategoryMaster subCategoryMaster = new SubCategoryMaster();
		subCategoryMaster.setSubCategoryName(itemRequest.getSubCategory());
		subCategoryMaster.setStatus(Status.ACTIVE.name());
		subCategoryMaster.setCreatedAt(new Date());
		subCategoryMaster.setUpdatedAt(new Date());
		return subCategoryMaster;
	}

	@Transactional
	public SubCategoryMaster saveSubCategoryMaster(SubCategoryMaster subCategoryMaster) {
		subCategoryMasterDao.persist(subCategoryMaster);
		return subCategoryMaster;
	}
	
	public SubCategoryMaster getUpdatedSubCategoryMasterByReqObj(SubCategoryMaster subCategoryMaster,
			ItemRequestObject itemRequest) {

		subCategoryMaster.setSubCategoryName(itemRequest.getSubCategory());
		subCategoryMaster.setUpdatedAt(new Date());
		return subCategoryMaster;
	}

	@Transactional
	public SubCategoryMaster updateSubCategoryMaster(SubCategoryMaster subCategoryMaster) {
		subCategoryMasterDao.update(subCategoryMaster);
		return subCategoryMaster;
	}

	@SuppressWarnings("unchecked")
	public List<SubCategoryMaster> getSubCategoryMaster(ItemRequestObject itemRequest) {
		List<SubCategoryMaster> results = categoryMasterDao.getEntityManager()
				.createQuery("SELECT SC FROM SubCategoryMaster SC ORDER BY SC.id ASC").getResultList();
		return results;
	}
	
	public CategoryDetails getCategoryDetailsByReqObj(ItemRequestObject itemRequest) {

		CategoryDetails categoryDetails = new CategoryDetails();
		categoryDetails.setCategoryType(itemRequest.getCategoryType());
		categoryDetails.setCategoryId(itemRequest.getCategoryId());
		categoryDetails.setCategoryName(itemRequest.getCategoryName());
		
		categoryDetails.setSuperadminId(itemRequest.getSuperadminId());
		categoryDetails.setStatus(Status.ACTIVE.name());
		categoryDetails.setCreatedAt(new Date());
		categoryDetails.setUpdatedAt(new Date());
		return categoryDetails;
	}

	@Transactional
	public CategoryDetails saveCategoryDetails(CategoryDetails categoryDetails) {
		categoryDetailsDao.persist(categoryDetails);
		return categoryDetails;
	}
	
	public CategoryDetails getUpdatedCategoryDetailsByReqObj(ItemRequestObject itemRequest, CategoryDetails categoryDetails) {

		categoryDetails.setCategoryId(itemRequest.getCategoryId());
		categoryDetails.setCategoryName(itemRequest.getCategoryName());
		
		categoryDetails.setStatus(Status.ACTIVE.name());
		categoryDetails.setUpdatedAt(new Date());
		return categoryDetails;
	}
	
	@Transactional
	public CategoryDetails updateCategoryDetails(CategoryDetails categoryDetails) {
		categoryDetailsDao.update(categoryDetails);
		return categoryDetails;
	}
	
	@SuppressWarnings("unchecked")
	public List<CategoryDetails> getCategoryDetails(ItemRequestObject itemRequest) {
		List<CategoryDetails> results = categoryMasterDao.getEntityManager()
				.createQuery("SELECT SC FROM CategoryDetails SC WHERE SC.categoryType =:categoryType AND SC.id IN (:id) ORDER BY SC.id ASC")
				.setParameter("id", itemRequest.getCategoryName())
				.setParameter("categoryType", itemRequest.getCategoryType())
				.getResultList();
		return results;
	}
}
