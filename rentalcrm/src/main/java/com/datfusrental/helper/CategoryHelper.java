package com.datfusrental.helper;

import java.util.ArrayList;
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
import com.datfusrental.dao.SuperCategoryDetailsDao;
import com.datfusrental.dao.SubCategoryDetailsDao;
import com.datfusrental.entities.CategoryDetails;
import com.datfusrental.entities.SuperCategoryDetails;
import com.datfusrental.entities.SubCategoryDetails;
import com.datfusrental.entities.UserRoleMaster;
import com.datfusrental.enums.Status;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.ItemRequestObject;
import com.datfusrental.object.request.UserRequestObject;

@Component
public class CategoryHelper {

	@Autowired
	private SuperCategoryDetailsDao superCategoryDetailsDao;

	@Autowired
	private CategoryDetailsDao categoryDetailsDao;

	@Autowired
	private SubCategoryDetailsDao subCategoryDetailsDao;
	

	public void validateItemRequest(ItemRequestObject itemRequestObject) throws BizException {
		if (itemRequestObject == null) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Bad Request Object Null");
		}
	}

	@Transactional
	public SuperCategoryDetails getSuperCategoryDetailsBySuperadminId(String superadminId) {

		CriteriaBuilder criteriaBuilder = superCategoryDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<SuperCategoryDetails> criteriaQuery = criteriaBuilder.createQuery(SuperCategoryDetails.class);
		Root<SuperCategoryDetails> root = criteriaQuery.from(SuperCategoryDetails.class);
		Predicate restriction = criteriaBuilder.equal(root.get("superadminId"), superadminId);
		criteriaQuery.where(restriction);
		SuperCategoryDetails superCategoryDetails = superCategoryDetailsDao.getSession().createQuery(criteriaQuery)
				.uniqueResult();
		return superCategoryDetails;
	}

	@Transactional
	public SubCategoryDetails getSubCategoryDetailsByCategoryIdAndSuperadminId(Long categoryId, String superadminId) {

		CriteriaBuilder criteriaBuilder = subCategoryDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<SubCategoryDetails> criteriaQuery = criteriaBuilder.createQuery(SubCategoryDetails.class);
		Root<SubCategoryDetails> root = criteriaQuery.from(SubCategoryDetails.class);
		Predicate restriction1 = criteriaBuilder.equal(root.get("categoryId"), categoryId);
		Predicate restriction2 = criteriaBuilder.equal(root.get("superadminId"), superadminId);
		criteriaQuery.where(restriction1, restriction2);
		SubCategoryDetails subCategoryDetails = subCategoryDetailsDao.getSession().createQuery(criteriaQuery)
				.uniqueResult();
		return subCategoryDetails;
	}

	@Transactional
	public CategoryDetails getCategoryDetailsBySuperadminId(Long superCategoryId, String superadminId) {

		CriteriaBuilder criteriaBuilder = categoryDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<CategoryDetails> criteriaQuery = criteriaBuilder.createQuery(CategoryDetails.class);
		Root<CategoryDetails> root = criteriaQuery.from(CategoryDetails.class);
		Predicate restriction1 = criteriaBuilder.equal(root.get("superCategoryId"), superCategoryId);
		Predicate restriction2 = criteriaBuilder.equal(root.get("superadminId"), superadminId);
		criteriaQuery.where(restriction1, restriction2);
		CategoryDetails categoryDetails = categoryDetailsDao.getSession().createQuery(criteriaQuery).uniqueResult();
		return categoryDetails;
	}

	public SuperCategoryDetails getSuperCategoryDetailsByReqObj(ItemRequestObject itemRequest) {

		SuperCategoryDetails superCategoryDetails = new SuperCategoryDetails();

		superCategoryDetails.setSuperCategory(itemRequest.getSuperCategory());
		superCategoryDetails.setStatus(Status.ACTIVE.name());
		superCategoryDetails.setSuperadminId(itemRequest.getSuperadminId());
		superCategoryDetails.setCreatedAt(new Date());
		superCategoryDetails.setUpdatedAt(new Date());

		return superCategoryDetails;
	}

	@Transactional
	public SuperCategoryDetails saveSuperCategoryDetails(SuperCategoryDetails superCategoryDetails) {
		superCategoryDetailsDao.persist(superCategoryDetails);
		return superCategoryDetails;
	}

	public SuperCategoryDetails getUpdatedSuperCategoryDetailsByReqObj(SuperCategoryDetails superCategoryDetails,
			ItemRequestObject itemRequest) {

		superCategoryDetails.setSuperCategory(itemRequest.getSuperCategory());
		superCategoryDetails.setUpdatedAt(new Date());
		return superCategoryDetails;
	}

	@Transactional
	public SuperCategoryDetails updateSuperCategoryDetails(SuperCategoryDetails superCategoryDetails) {
		superCategoryDetailsDao.update(superCategoryDetails);
		return superCategoryDetails;
	}

	@SuppressWarnings("unchecked")
	public List<SuperCategoryDetails> getSuperCategoryDetails(ItemRequestObject itemRequest) {
		if (itemRequest.getRequestedFor().equals("ALL")) {
			List<SuperCategoryDetails> results = superCategoryDetailsDao.getEntityManager().createQuery(
					"SELECT SC FROM SuperCategoryDetails SC WHERE SC.superadminId =:superadminId AND SC.status =:status ORDER BY SC.id ASC")
					.setParameter("superadminId", itemRequest.getSuperadminId())
					.setParameter("status", Status.ACTIVE.name()).getResultList();
			return results;
		}
		return null;
	}

	public CategoryDetails getCategoryDetailsByReqObj(ItemRequestObject itemRequest) {

		CategoryDetails categoryDetails = new CategoryDetails();
		categoryDetails.setSuperCategoryId(itemRequest.getSuperCategoryId());
		categoryDetails.setCategory(itemRequest.getCategory());
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

	public CategoryDetails getUpdatedCategoryDetailsByReqObj(ItemRequestObject itemRequest,
			CategoryDetails categoryDetails) {

		categoryDetails.setSuperCategoryId(itemRequest.getSuperCategoryId());
		categoryDetails.setCategory(itemRequest.getCategory());
		categoryDetails.setUpdatedAt(new Date());
		return categoryDetails;
	}

	@Transactional
	public CategoryDetails updateCategoryDetails(CategoryDetails categoryDetails) {
		categoryDetailsDao.update(categoryDetails);
		return categoryDetails;
	}

	@SuppressWarnings("unchecked")
	public List<CategoryDetails> getCategoryDetailsBySuperCategoryId(ItemRequestObject itemRequest) {
		List<CategoryDetails> results = categoryDetailsDao.getEntityManager().createQuery(
				"SELECT CD FROM CategoryDetails CD WHERE CD.superCategoryId =:superCategoryId AND CD.superadminId =:superadminId AND status =:status ORDER BY CD.id ASC")
				.setParameter("superCategoryId", itemRequest.getSuperCategoryId())
				.setParameter("superadminId", itemRequest.getSuperadminId())
				.setParameter("status", Status.ACTIVE.name()).getResultList();
		return results;
	}

	public SubCategoryDetails getSubCategoryDetailsByReqObj(ItemRequestObject itemRequest) {

		SubCategoryDetails subCategoryDetails = new SubCategoryDetails();
		subCategoryDetails.setCategoryId(itemRequest.getCategoryId());
		subCategoryDetails.setSubCategory(itemRequest.getSubCategory());
		subCategoryDetails.setStatus(Status.ACTIVE.name());
		subCategoryDetails.setCreatedAt(new Date());
		subCategoryDetails.setUpdatedAt(new Date());
		return subCategoryDetails;
	}

	@Transactional
	public SubCategoryDetails saveSubCategoryDetails(SubCategoryDetails subCategoryDetails) {
		subCategoryDetailsDao.persist(subCategoryDetails);
		return subCategoryDetails;
	}

	public SubCategoryDetails getUpdatedSubCategoryDetailsByReqObj(SubCategoryDetails subCategoryDetails,
			ItemRequestObject itemRequest) {

		subCategoryDetails.setCategoryId(itemRequest.getCategoryId());
		subCategoryDetails.setSubCategory(itemRequest.getSubCategory());
		subCategoryDetails.setUpdatedAt(new Date());
		return subCategoryDetails;
	}

	@Transactional
	public SubCategoryDetails updateSubCategoryDetails(SubCategoryDetails subCategoryDetails) {
		subCategoryDetailsDao.update(subCategoryDetails);
		return subCategoryDetails;
	}

	@SuppressWarnings("unchecked")
	public List<SubCategoryDetails> getSubCategoryDetails(ItemRequestObject itemRequest) {
		List<SubCategoryDetails> results = subCategoryDetailsDao.getEntityManager().createQuery(
				"SELECT SC FROM SubCategoryMaster SC WHERE SC.categoryId =:categoryId AND SC.superadminId =:superadminId AND SC.status =:status ORDER BY SC.subCategory DESC")
				.setParameter("categoryId", itemRequest.getCategoryId())
				.setParameter("superadminId", itemRequest.getSuperadminId())
				.setParameter("status", Status.ACTIVE.name()).getResultList();
		return results;
	}

}
