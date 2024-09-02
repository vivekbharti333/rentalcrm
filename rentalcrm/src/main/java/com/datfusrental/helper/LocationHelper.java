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
import com.datfusrental.dao.LocationDetailsDao;
import com.datfusrental.dao.SuperCategoryDetailsDao;
import com.datfusrental.dao.SubCategoryDetailsDao;
import com.datfusrental.entities.CategoryDetails;
import com.datfusrental.entities.CategoryType;
import com.datfusrental.entities.LocationDetails;
import com.datfusrental.entities.SuperCategoryDetails;
import com.datfusrental.entities.SubCategoryDetails;
import com.datfusrental.enums.RequestFor;
import com.datfusrental.enums.Status;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.ItemRequestObject;

@Component
public class LocationHelper {

	@Autowired
	private LocationDetailsDao locationDetailsDao;

	public void validateItemRequest(ItemRequestObject itemRequestObject) throws BizException {
		if (itemRequestObject == null) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Bad Request Object Null");
		}
	}

	@Transactional
	public LocationDetails getLocationDetailsByType(String location, String locationType, String superadminId) {

		CriteriaBuilder criteriaBuilder = locationDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<LocationDetails> criteriaQuery = criteriaBuilder.createQuery(LocationDetails.class);
		Root<LocationDetails> root = criteriaQuery.from(LocationDetails.class);
		Predicate restriction1 = criteriaBuilder.equal(root.get("location"), location);
		Predicate restriction2 = criteriaBuilder.equal(root.get("locationType"), locationType);
		Predicate restriction3 = criteriaBuilder.equal(root.get("superadminId"), superadminId);
		criteriaQuery.where(restriction1, restriction2, restriction3);
		LocationDetails locationDetails = locationDetailsDao.getSession().createQuery(criteriaQuery).uniqueResult();
		return locationDetails;
	}

	public LocationDetails getLocationDetailsByReqObj(ItemRequestObject itemRequest) {

		LocationDetails locationDetails = new LocationDetails();

		locationDetails.setLocation(itemRequest.getLocation());
		locationDetails.setLocationType(itemRequest.getLocationType());
		locationDetails.setStatus(Status.ACTIVE.name());
		locationDetails.setSuperadminId(itemRequest.getSuperadminId());
		locationDetails.setCreatedAt(new Date());
		locationDetails.setUpdatedAt(new Date());

		return locationDetails;
	}

	@Transactional
	public LocationDetails saveLocationDetails(LocationDetails locationDetails) {
		locationDetailsDao.persist(locationDetails);
		return locationDetails;
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
//		locationDetailsDao.update(categoryType);
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<CategoryType> getCategoryType(ItemRequestObject itemRequest) {
		List<CategoryType> results = new ArrayList<>();
		if (itemRequest.getRequestedFor().equalsIgnoreCase(RequestFor.ALL.name())) {
			results = locationDetailsDao.getEntityManager()
					.createQuery(
							"SELECT SC FROM CategoryType SC WHERE SC.superadminId =:superadminId ORDER BY SC.id desc")
					.setParameter("superadminId", itemRequest.getSuperadminId()).getResultList();

		} else if (itemRequest.getRequestedFor().equalsIgnoreCase(RequestFor.BYCATID.name())) {
//			results = locationDetailsDao.getEntityManager().createQuery(
//					"SELECT SC FROM CategoryType SC WHERE SC.superadminId =:superadminId AND SC.status =:status ORDER BY SC.id ASC")
//					.setParameter("superadminId", itemRequest.getSuperadminId())
//					.setParameter("status", Status.ACTIVE.name()).getResultList();
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<LocationDetails> getLocationDetails(ItemRequestObject itemRequest) {
		List<LocationDetails> results = new ArrayList<>();
		if (itemRequest.getRequestedFor().equalsIgnoreCase(RequestFor.ALL.name())) {
			results = locationDetailsDao.getEntityManager()
					.createQuery("SELECT LD FROM LocationDetails LD WHERE LD.superadminId =:superadminId ORDER BY LD.id desc")
					.setParameter("superadminId", itemRequest.getSuperadminId()).getResultList();

		} else if (itemRequest.getRequestedFor().equalsIgnoreCase(RequestFor.BYTYPE.name())) {
			results = locationDetailsDao.getEntityManager()
					.createQuery("SELECT SC FROM CategoryType SC WHERE SC.superadminId =:superadminId ORDER BY LD.id desc")
					.setParameter("superadminId", itemRequest.getSuperadminId()).getResultList();

		}
		return results;
	}

	
}