package com.datfusrental.helper;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.datfusrental.dao.CategoryTypeDao;
import com.datfusrental.dao.SuperCategoryDetailsDao;
import com.datfusrental.dao.SubCategoryDetailsDao;
import com.datfusrental.entities.CategoryDetails;
import com.datfusrental.entities.CategoryType;
import com.datfusrental.entities.SuperCategoryDetails;
import com.datfusrental.entities.SubCategoryDetails;
import com.datfusrental.enums.RequestFor;
import com.datfusrental.enums.Status;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.ItemRequestObject;


@Component
public class CategoryHelper {
	
	@Autowired
	private CategoryTypeDao categoryTypeDao;

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
	public CategoryType getCategoryTypeByCategoryTypeName(String categoryTypeName, String superadminId) {

		CriteriaBuilder criteriaBuilder = categoryTypeDao.getSession().getCriteriaBuilder();
		CriteriaQuery<CategoryType> criteriaQuery = criteriaBuilder.createQuery(CategoryType.class);
		Root<CategoryType> root = criteriaQuery.from(CategoryType.class);
		Predicate restriction1 = criteriaBuilder.equal(root.get("categoryTypeName"), categoryTypeName);
		Predicate restriction2 = criteriaBuilder.equal(root.get("superadminId"), superadminId);
		criteriaQuery.where(restriction1, restriction2);
		CategoryType categoryType = categoryTypeDao.getSession().createQuery(criteriaQuery)
				.uniqueResult();
		return categoryType;
	}
	
	@Transactional
	public CategoryType getCategoryTypeById(long id) {

		CriteriaBuilder criteriaBuilder = categoryTypeDao.getSession().getCriteriaBuilder();
		CriteriaQuery<CategoryType> criteriaQuery = criteriaBuilder.createQuery(CategoryType.class);
		Root<CategoryType> root = criteriaQuery.from(CategoryType.class);
		Predicate restriction = criteriaBuilder.equal(root.get("id"), id);
		criteriaQuery.where(restriction);
		CategoryType categoryType = categoryTypeDao.getSession().createQuery(criteriaQuery)
				.uniqueResult();
		return categoryType;
	}
	
	@Transactional
	public SuperCategoryDetails getSuperCategoryDetailsBySuperadminId(Long categoryTypeId, String superCategory, String superadminId) {

		CriteriaBuilder criteriaBuilder = superCategoryDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<SuperCategoryDetails> criteriaQuery = criteriaBuilder.createQuery(SuperCategoryDetails.class);
		Root<SuperCategoryDetails> root = criteriaQuery.from(SuperCategoryDetails.class);
		Predicate restriction1 = criteriaBuilder.equal(root.get("categoryTypeId"), categoryTypeId);
		Predicate restriction2 = criteriaBuilder.equal(root.get("superCategory"), superCategory);
		Predicate restriction3 = criteriaBuilder.equal(root.get("superadminId"), superadminId);
		criteriaQuery.where(restriction1,restriction2,restriction3);
		SuperCategoryDetails superCategoryDetails = superCategoryDetailsDao.getSession().createQuery(criteriaQuery)
				.uniqueResult();
		return superCategoryDetails;
	}
	@Transactional
	public SuperCategoryDetails getSuperCategoryDetailsById(Long superCategoryId) {

		CriteriaBuilder criteriaBuilder = superCategoryDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<SuperCategoryDetails> criteriaQuery = criteriaBuilder.createQuery(SuperCategoryDetails.class);
		Root<SuperCategoryDetails> root = criteriaQuery.from(SuperCategoryDetails.class);
		Predicate restriction = criteriaBuilder.equal(root.get("id"), superCategoryId);
		criteriaQuery.where(restriction);
		SuperCategoryDetails superCategoryDetails = superCategoryDetailsDao.getSession().createQuery(criteriaQuery)
				.uniqueResult();
		return superCategoryDetails;
	}

	@Transactional
	public SubCategoryDetails getSubCategoryDetailsByCategoryIdAndSuperadminId(String subCategory, Long categoryId, String superadminId) {

		CriteriaBuilder criteriaBuilder = subCategoryDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<SubCategoryDetails> criteriaQuery = criteriaBuilder.createQuery(SubCategoryDetails.class);
		Root<SubCategoryDetails> root = criteriaQuery.from(SubCategoryDetails.class);
		Predicate restriction1 = criteriaBuilder.equal(root.get("categoryId"), categoryId);
		Predicate restriction2 = criteriaBuilder.equal(root.get("subCategory"), subCategory);
		Predicate restriction3 = criteriaBuilder.equal(root.get("superadminId"), superadminId);
		criteriaQuery.where(restriction1, restriction2, restriction3);
		SubCategoryDetails subCategoryDetails = subCategoryDetailsDao.getSession().createQuery(criteriaQuery)
				.uniqueResult();
		return subCategoryDetails;
	}
	
	@Transactional
	public SubCategoryDetails getSubCategoryDetailsById(Long id) {

		CriteriaBuilder criteriaBuilder = subCategoryDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<SubCategoryDetails> criteriaQuery = criteriaBuilder.createQuery(SubCategoryDetails.class);
		Root<SubCategoryDetails> root = criteriaQuery.from(SubCategoryDetails.class);
		Predicate restriction = criteriaBuilder.equal(root.get("id"), id);
		criteriaQuery.where(restriction);
		SubCategoryDetails subCategoryDetails = subCategoryDetailsDao.getSession().createQuery(criteriaQuery)
				.uniqueResult();
		return subCategoryDetails;
	}

	@Transactional
	public CategoryDetails getCategoryDetailsBySuperadminId(Long superCategoryId, String category, String superadminId) {

		CriteriaBuilder criteriaBuilder = categoryDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<CategoryDetails> criteriaQuery = criteriaBuilder.createQuery(CategoryDetails.class);
		Root<CategoryDetails> root = criteriaQuery.from(CategoryDetails.class);
		Predicate restriction1 = criteriaBuilder.equal(root.get("superCategoryId"), superCategoryId);
		Predicate restriction2 = criteriaBuilder.equal(root.get("category"), category);
		Predicate restriction3 = criteriaBuilder.equal(root.get("superadminId"), superadminId);
		criteriaQuery.where(restriction1, restriction2, restriction3);
		CategoryDetails categoryDetails = categoryDetailsDao.getSession().createQuery(criteriaQuery).uniqueResult();
		return categoryDetails;
	}
	
	@Transactional
	public CategoryDetails getCategoryDetailsById(Long categoryId) {

		CriteriaBuilder criteriaBuilder = categoryDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<CategoryDetails> criteriaQuery = criteriaBuilder.createQuery(CategoryDetails.class);
		Root<CategoryDetails> root = criteriaQuery.from(CategoryDetails.class);
		Predicate restriction = criteriaBuilder.equal(root.get("id"), categoryId);
		criteriaQuery.where(restriction);
		CategoryDetails categoryDetails = categoryDetailsDao.getSession().createQuery(criteriaQuery).uniqueResult();
		return categoryDetails;
	}
	
	public CategoryType getCategoryTypeByReqObj(ItemRequestObject itemRequest) {

		CategoryType categoryType = new CategoryType();

		categoryType.setCategoryTypeImage(itemRequest.getCategoryTypeImage());;
		categoryType.setCategoryTypeName(itemRequest.getCategoryTypeName());
		categoryType.setStatus(Status.ACTIVE.name());
		categoryType.setIsChecked(itemRequest.getIsChecked());
		categoryType.setSuperadminId(itemRequest.getSuperadminId());
		categoryType.setCreatedAt(new Date());
		categoryType.setUpdatedAt(new Date());

		return categoryType;
	}
	
	@Transactional
	public CategoryType saveCategoryType(CategoryType categoryType) {
		categoryTypeDao.persist(categoryType);
		return categoryType;
	}
	
	public CategoryType getEditedCategoryTypeByReqObj(ItemRequestObject itemRequest, CategoryType categoryType) {

		categoryType.setCategoryTypeName(itemRequest.getCategoryTypeName());
//		categoryType.setStatus(Status.ACTIVE.name());
//		categoryType.setIsChecked(itemRequest.getIsChecked());
//		categoryType.setSuperadminId(itemRequest.getSuperadminId());
//		categoryType.setCreatedAt(new Date());
		categoryType.setUpdatedAt(new Date());

		return categoryType;
	}
	
	@Transactional
	public CategoryType updateCategoryType(CategoryType categoryType) {
		categoryTypeDao.update(categoryType);
		return categoryType;
	}
	
	@SuppressWarnings("unchecked")
	public List<CategoryType> getCategoryType(ItemRequestObject itemRequest) {
		List<CategoryType> results = new ArrayList<>();
		if (itemRequest.getRequestedFor().equalsIgnoreCase(RequestFor.ALL.name())) {
			results = categoryTypeDao.getEntityManager().createQuery(
					"SELECT SC FROM CategoryType SC WHERE SC.superadminId =:superadminId ORDER BY SC.id desc")
					.setParameter("superadminId", itemRequest.getSuperadminId())
					.getResultList();

		} else if (itemRequest.getRequestedFor().equalsIgnoreCase(RequestFor.BYCATID.name())) { 
//			results = categoryTypeDao.getEntityManager().createQuery(
//					"SELECT SC FROM CategoryType SC WHERE SC.superadminId =:superadminId AND SC.status =:status ORDER BY SC.id ASC")
//					.setParameter("superadminId", itemRequest.getSuperadminId())
//					.setParameter("status", Status.ACTIVE.name()).getResultList();
		}
		return results;
	}

	public SuperCategoryDetails getSuperCategoryDetailsByReqObj(ItemRequestObject itemRequest) {

		SuperCategoryDetails superCategoryDetails = new SuperCategoryDetails();

		superCategoryDetails.setCategoryTypeId(itemRequest.getCategoryTypeId());
		superCategoryDetails.setSuperCategoryImage(itemRequest.getSuperCategoryImage());
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
	
	public SuperCategoryDetails getUpdatedSuperCategoryDetailsByReqObj(ItemRequestObject itemRequest, SuperCategoryDetails superCategoryDetails) {
		
		superCategoryDetails.setCategoryTypeId(itemRequest.getCategoryTypeId());
		superCategoryDetails.setSuperCategory(itemRequest.getSuperCategory());
		superCategoryDetails.setUpdatedAt(new Date());

		return superCategoryDetails;
	}

	@Transactional
	public SuperCategoryDetails updateSuperCategoryDetails(SuperCategoryDetails superCategoryDetails) {
		superCategoryDetailsDao.update(superCategoryDetails);
		return superCategoryDetails;
	}

//	@SuppressWarnings("unchecked")
//	public List<SuperCategoryDetails> getSuperCategoryDetails(ItemRequestObject itemRequest) {
//		List<ItemRequestObject> superCategoryList = new ArrayList<>();
//		if (itemRequest.getRequestedFor().equalsIgnoreCase(RequestFor.ALL.name())) {
//			List<SuperCategoryDetails> results = superCategoryDetailsDao.getEntityManager().createQuery(          //INNER JOIN Customers ON Orders.CustomerID=Customers.CustomerID;
//					"SELECT sc.id, sc.superCategoryImage, sc.categoryTypeId, ct.categoryTypeName, sc.superCategory, sc.status, sc.createdAt FROM SuperCategoryDetails sc, CategoryType ct WHERE sc.categoryTypeId = ct.id AND sc.superadminId = :superadminId ORDER BY sc.id ASC")
//					.setParameter("superadminId", itemRequest.getSuperadminId())
////					.setParameter("status", Status.ACTIVE.name())
//					.getResultList();
//			for (Object[] row : results) {
//	            ItemRequestObject item = new ItemRequestObject();
//	            item.setId((Long) row[0]);
//	            item.setSuperCategoryImage((String) row[1]);
//	            item.setCategoryTypeId((Long) row[2]);
//	            item.setCategoryTypeName((String) row[3]);
//	            //item.setSuperCategoryId((Long) row[3]);
//	            item.setSuperCategory((String) row[4]);
//	            item.setStatus((String) row[5]);
//	            item.setCreatedAt((Date) row[6]);
//
//	            superCategoryList.add(item);
//	        }
//			
//		} else if (itemRequest.getRequestedFor().equalsIgnoreCase(RequestFor.BYCATID.name())){
//			List<SuperCategoryDetails> results = superCategoryDetailsDao.getEntityManager().createQuery(
//					"SELECT SC FROM SuperCategoryDetails SC WHERE SC.categoryTypeId =:categoryTypeId AND SC.superadminId =:superadminId AND SC.status =:status ORDER BY SC.id ASC")
//					.setParameter("categoryTypeId", itemRequest.getCategoryTypeId())
//					.setParameter("superadminId", itemRequest.getSuperadminId())
//					.setParameter("status", Status.ACTIVE.name()).getResultList();
//			return results;
//		}else {
//			
//		}
//		return null;
//	}
	
	@SuppressWarnings("unchecked")
	public List<ItemRequestObject> getSuperCategoryDetails(ItemRequestObject itemRequest) {
	    List<ItemRequestObject> superCategoryList = new ArrayList<>();
	    
	    if (itemRequest.getRequestedFor().equalsIgnoreCase(RequestFor.ALL.name())) {
	        // Query to fetch details for all categories
	        List<Object[]> results = superCategoryDetailsDao.getEntityManager().createQuery(
	            "SELECT sc.id, sc.superCategoryImage, sc.categoryTypeId, ct.categoryTypeName, sc.superCategory, sc.status, sc.createdAt " +
	            "FROM SuperCategoryDetails sc JOIN CategoryType ct ON sc.categoryTypeId = ct.id " +
	            "WHERE sc.superadminId = :superadminId ORDER BY sc.id ASC")
	            .setParameter("superadminId", itemRequest.getSuperadminId())
	            .getResultList();
	        
	        for (Object[] row : results) {
	        	ItemRequestObject item = new ItemRequestObject();
	            item.setId((Long) row[0]);
	            item.setSuperCategoryImage((String) row[1]);
	            item.setCategoryTypeId((Long) row[2]);
	            item.setCategoryTypeName((String) row[3]);
	            item.setSuperCategory((String) row[4]);
	            item.setStatus((String) row[5]);
	            item.setCreatedAt((Date) row[6]);

	            superCategoryList.add(item);
	        }
	        
	    } else if (itemRequest.getRequestedFor().equalsIgnoreCase(RequestFor.BYCATID.name())) {
	        // Query to fetch details by category ID
	        List<ItemRequestObject> results = superCategoryDetailsDao.getEntityManager().createQuery(
	            "SELECT sc FROM SuperCategoryDetails sc " +
	            "WHERE sc.categoryTypeId = :categoryTypeId AND sc.superadminId = :superadminId AND sc.status = :status " +
	            "ORDER BY sc.id ASC")
	            .setParameter("categoryTypeId", itemRequest.getCategoryTypeId())
	            .setParameter("superadminId", itemRequest.getSuperadminId())
	            .setParameter("status", Status.ACTIVE.name())
	            .getResultList();
	        
	        return results;
	    }
	    
	    // Return empty list instead of null
	    return superCategoryList;
	}

	public CategoryDetails getCategoryDetailsByReqObj(ItemRequestObject itemRequest) {

		CategoryDetails categoryDetails = new CategoryDetails();
		categoryDetails.setCategoryTypeId(itemRequest.getCategoryTypeId());
		categoryDetails.setSuperCategoryId(itemRequest.getSuperCategoryId());
		categoryDetails.setCategoryImage(itemRequest.getCategoryImage());
		categoryDetails.setCategory(itemRequest.getCategory());
		categoryDetails.setSubCategory(itemRequest.getSubCategory());
		
		categoryDetails.setSecurityAmount(itemRequest.getSecurityAmount());
		categoryDetails.setVendorRate(itemRequest.getVendorRate());
		categoryDetails.setVendorRateForKids(itemRequest.getVendorRateForKids());
		
		categoryDetails.setCompanyRate(itemRequest.getCompanyRate());
		categoryDetails.setCompanyRateForKids(itemRequest.getCompanyRateForKids());
		
//		categoryDetails.setStartDate(itemRequest.getStartDate());
//		categoryDetails.setEndDate(itemRequest.getEndDate());
		
		categoryDetails.setStartTime(itemRequest.getStartTime());
		categoryDetails.setEndTime(itemRequest.getEndTime());
		
		categoryDetails.setPickupHub(itemRequest.getPickupHub());
		categoryDetails.setDropHub(itemRequest.getDropHub());
		
		categoryDetails.setPickDropHub(itemRequest.getPickDropHub());
		categoryDetails.setActivityLocation(itemRequest.getActivityLocation());
		
		categoryDetails.setQuantity(itemRequest.getQuantity());
		categoryDetails.setKidQuantity(itemRequest.getKidQuantity());
		categoryDetails.setInfantQuantity(itemRequest.getInfantQuantity());
		
		categoryDetails.setDescription(itemRequest.getDescription());
		
		categoryDetails.setStatus(Status.ACTIVE.name());
		categoryDetails.setSuperadminId(itemRequest.getSuperadminId());
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

		categoryDetails.setCategoryTypeId(itemRequest.getCategoryTypeId());
		categoryDetails.setSuperCategoryId(itemRequest.getSuperCategoryId());
		categoryDetails.setCategory(itemRequest.getCategory());
		categoryDetails.setSubCategory(itemRequest.getSubCategory());
//		categoryDetails.setStatus(Status.ACTIVE.name());
//		categoryDetails.setSuperadminId(itemRequest.getSuperadminId());
//		categoryDetails.setCreatedAt(new Date());
		categoryDetails.setUpdatedAt(new Date());
		return categoryDetails;
	}

	@Transactional
	public CategoryDetails updateCategoryDetails(CategoryDetails categoryDetails) {
		categoryDetailsDao.update(categoryDetails);
		return categoryDetails;
	}

	
	@SuppressWarnings("unchecked")
	public List<ItemRequestObject> getCategoryDetails(ItemRequestObject itemRequest) {
	    List<ItemRequestObject> categoryDetailsList = new ArrayList<>();

	    // Handle case when requestedFor is "ALL"
	    if (itemRequest.getRequestedFor().equalsIgnoreCase(RequestFor.ALL.name())) {
	        List<Object[]> results = categoryDetailsDao.getEntityManager().createQuery(
	            "SELECT cd.id, cd.categoryImage, cd.categoryTypeId, ct.categoryTypeName, cd.superCategoryId, sc.superCategory, cd.category, cd.status, cd.createdAt, cd.securityAmount, cd.vendorRate, cd.vendorRateForKids,"
	            + "cd.companyRate, cd.companyRateForKids,cd.startTime, cd.endTime, cd.quantity, cd.kidQuantity, cd.infantQuantity, cd.description, cd.pickupHub, cd.dropHub, cd.subCategory, cd.pickDropHub, cd.activityLocation " +
	            "FROM com.datfusrental.entities.CategoryDetails cd " +
	            "JOIN com.datfusrental.entities.SuperCategoryDetails sc ON cd.superCategoryId = sc.id " +
	            "JOIN com.datfusrental.entities.CategoryType ct ON cd.categoryTypeId = ct.id " +
	            "WHERE cd.superadminId = :superadminId " +
	            "ORDER BY cd.id DESC")
	            .setParameter("superadminId", itemRequest.getSuperadminId())
	            .getResultList();

	        for (Object[] row : results) {
	            ItemRequestObject item = new ItemRequestObject();
	            item.setId((Long) row[0]);
	            item.setCategoryImage((String) row[1]);
	            item.setCategoryTypeId((Long) row[2]);
	            item.setCategoryTypeName((String) row[3]);
	            item.setSuperCategoryId((Long) row[4]);
	            item.setSuperCategory((String) row[5]);
	            item.setCategory((String) row[6]);
	            item.setStatus((String) row[7]);
	            item.setCreatedAt((Date) row[8]);
	            item.setSecurityAmount((long) row[9]);
	            item.setVendorRate((long) row[10]);
	            item.setVendorRateForKids((long) row[11]);
	            item.setCompanyRate((long) row[12]);
	            item.setCompanyRateForKids((long) row[13]);
//	            item.setStartDate((Date) row[14]);
//	            item.setEndDate((Date) row[15]);
	            item.setStartTime((LocalTime) row[14]);
	            item.setEndTime((LocalTime) row[15]);
	            item.setQuantity((int) row[16]);
	            item.setKidQuantity((int) row[17]);
	            item.setInfantQuantity((int) row[18]);
	            item.setDescription((String) row[19]);
	            item.setPickupHub((String) row[20]);
	            item.setDropHub((String) row[21]);
	            item.setSubCategory((String) row[22]);
	            item.setPickDropHub((String) row[23]);
	            item.setActivityLocation((String) row[24]);
	            categoryDetailsList.add(item);
	        }
	    }
	    // Handle case when requestedFor is "BYCATID"
	    else if (itemRequest.getRequestedFor().equalsIgnoreCase(RequestFor.BYCATID.name())) {
	        List<CategoryDetails> results = categoryDetailsDao.getEntityManager().createQuery(
	            "SELECT CD FROM com.datfusrental.entities.CategoryDetails CD " +
	            "WHERE CD.superCategoryId = :superCategoryId " +
	            "AND CD.superadminId = :superadminId " +
	            "AND CD.status = :status " +
	            "ORDER BY CD.id ASC", CategoryDetails.class)
	            .setParameter("superCategoryId", itemRequest.getSuperCategoryId())
	            .setParameter("superadminId", itemRequest.getSuperadminId())
	            .setParameter("status", Status.ACTIVE.name())
	            .getResultList();

	        // Map the CategoryDetails results to ItemRequestObject
	        for (CategoryDetails cd : results) {
	            ItemRequestObject item = new ItemRequestObject();
	            item.setId(cd.getId());
	            item.setCategoryTypeId(cd.getCategoryTypeId());
	            item.setSuperCategoryId(cd.getSuperCategoryId());
	            item.setCategory(cd.getCategory());
	            item.setStatus(cd.getStatus());
	            item.setCreatedAt(cd.getCreatedAt());
	            
	            categoryDetailsList.add(item);
	        }
	    }
	    return categoryDetailsList;
	}

	public SubCategoryDetails getSubCategoryDetailsByReqObj(ItemRequestObject itemRequest) {
		SubCategoryDetails subCategoryDetails = new SubCategoryDetails();
		
		subCategoryDetails.setCategoryTypeId(itemRequest.getCategoryTypeId());
		subCategoryDetails.setSuperCategoryId(itemRequest.getSuperCategoryId());
		subCategoryDetails.setCategoryId(itemRequest.getCategoryId());
		subCategoryDetails.setSubCategoryImage(itemRequest.getSubCategoryImage());
		subCategoryDetails.setSubCategory(itemRequest.getSubCategory());
		subCategoryDetails.setStatus(Status.ACTIVE.name());
		
		subCategoryDetails.setSuperadminId(itemRequest.getSuperadminId());
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

		subCategoryDetails.setCategoryTypeId(itemRequest.getCategoryTypeId());
		subCategoryDetails.setSuperCategoryId(itemRequest.getSuperCategoryId());
		subCategoryDetails.setCategoryId(itemRequest.getCategoryId());
		subCategoryDetails.setSubCategoryImage(itemRequest.getSubCategoryImage());
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
	public List<ItemRequestObject> getSubCategoryDetails(ItemRequestObject itemRequest) {
	    List<ItemRequestObject> subCategoryList = new ArrayList<>();
	    
	    if (itemRequest.getRequestedFor().equalsIgnoreCase(RequestFor.ALL.name())) {
	        // Query for all subcategory details
	        List<Object[]> results = subCategoryDetailsDao.getEntityManager().createQuery(
	            "SELECT subcd.id, subcd.subCategoryImage, subcd.categoryTypeId, ctd.categoryTypeName, " +
	            "subcd.superCategoryId, scd.superCategory, subcd.categoryId, cd.category, " +
	            "subcd.subCategory, subcd.status, subcd.createdAt  " +
	            "FROM SubCategoryDetails subcd " +
	            "JOIN CategoryDetails cd ON subcd.categoryId = cd.id " +
	            "JOIN SuperCategoryDetails scd ON cd.superCategoryId = scd.id " +
	            "JOIN CategoryType ctd ON cd.categoryTypeId = ctd.id " +
	            "WHERE subcd.superadminId = :superadminId " +
	            "ORDER BY subcd.id ASC")
	            .setParameter("superadminId", itemRequest.getSuperadminId())
	            .getResultList();

	        for (Object[] row : results) {
	        	ItemRequestObject subCategoryDetails = new ItemRequestObject();
	            subCategoryDetails.setId((Long) row[0]);
	            subCategoryDetails.setSubCategoryImage((String) row[1]);
	            subCategoryDetails.setCategoryTypeId((Long) row[2]);
	            subCategoryDetails.setCategoryTypeName((String) row[3]);
	            subCategoryDetails.setSuperCategoryId((Long) row[4]);
	            subCategoryDetails.setSuperCategory((String) row[5]);
	            subCategoryDetails.setCategoryId((Long) row[6]);
	            subCategoryDetails.setCategory((String) row[7]);
	            subCategoryDetails.setSubCategory((String) row[8]);
	            
	            subCategoryDetails.setStatus((String) row[9]);
	            subCategoryDetails.setCreatedAt((Date) row[10]);

	            subCategoryList.add(subCategoryDetails);
	        }
	        
	    } else if (itemRequest.getRequestedFor().equalsIgnoreCase(RequestFor.BYCATID.name())) {
	        // Query for subcategory details by category ID
	        List<ItemRequestObject> results = subCategoryDetailsDao.getEntityManager().createQuery(
	            "SELECT sc FROM SubCategoryDetails sc " +
	            "WHERE sc.categoryId = :categoryId AND sc.superadminId = :superadminId AND sc.status = :status " +
	            "ORDER BY sc.subCategory DESC")
	            .setParameter("categoryId", itemRequest.getCategoryId())
	            .setParameter("superadminId", itemRequest.getSuperadminId())
	            .setParameter("status", Status.ACTIVE.name())
	            .getResultList();

	        return results;
	    }
	    
	    // Return empty list instead of null
	    return subCategoryList;
	}

}