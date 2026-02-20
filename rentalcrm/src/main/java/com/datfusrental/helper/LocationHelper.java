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
import com.datfusrental.object.request.LocationRequestObject;

@Component
public class LocationHelper {

	@Autowired
	private LocationDetailsDao locationDetailsDao;

	public void validateLocationRequest(LocationRequestObject locationRequestObject) throws BizException {
		if (locationRequestObject == null) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Bad Request Object Null");
		}
	}

	@Transactional
	public LocationDetails getLocationDetailsById(Long id) {

		CriteriaBuilder criteriaBuilder = locationDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<LocationDetails> criteriaQuery = criteriaBuilder.createQuery(LocationDetails.class);
		Root<LocationDetails> root = criteriaQuery.from(LocationDetails.class);
		Predicate restriction = criteriaBuilder.equal(root.get("id"), id);
		criteriaQuery.where(restriction);
		LocationDetails locationDetails = locationDetailsDao.getSession().createQuery(criteriaQuery).uniqueResult();
		return locationDetails;
	}

	@Transactional
	public LocationDetails getLocationDetailsByType(String location) {

		CriteriaBuilder criteriaBuilder = locationDetailsDao.getSession().getCriteriaBuilder();
		CriteriaQuery<LocationDetails> criteriaQuery = criteriaBuilder.createQuery(LocationDetails.class);
		Root<LocationDetails> root = criteriaQuery.from(LocationDetails.class);
		Predicate restriction = criteriaBuilder.equal(root.get("location"), location);
		criteriaQuery.where(restriction);
		LocationDetails locationDetails = locationDetailsDao.getSession().createQuery(criteriaQuery).uniqueResult();
		return locationDetails;
	}

	public LocationDetails getLocationDetailsByReqObj(LocationRequestObject locationRequest) {

		LocationDetails locationDetails = new LocationDetails();

		locationDetails.setLocation(locationRequest.getLocation());
		locationDetails.setLocationType(locationRequest.getLocationType());
		locationDetails.setStatus(Status.ACTIVE.name());
		locationDetails.setSuperadminId(locationRequest.getSuperadminId());
		locationDetails.setCreatedAt(new Date());
		locationDetails.setUpdatedAt(new Date());

		return locationDetails;
	}

	@Transactional
	public LocationDetails saveLocationDetails(LocationDetails locationDetails) {
		locationDetailsDao.persist(locationDetails);
		return locationDetails;
	}

	public LocationDetails getUpdatedLocationDetailsByReqObj(LocationRequestObject locationRequest, LocationDetails locationDetails) {

		locationDetails.setLocation(locationRequest.getLocation());
		locationDetails.setUpdatedAt(new Date());

		return locationDetails;
	}

	@Transactional
	public LocationDetails updateLocationDetails(LocationDetails locationDetails) {
		locationDetailsDao.update(locationDetails);
		return locationDetails;
	}

	@SuppressWarnings("unchecked")
	public List<LocationDetails> getLocationDetails(LocationRequestObject locationRequest) {
		List<LocationDetails> results = new ArrayList<>();
		if (locationRequest.getRequestedFor().equalsIgnoreCase(RequestFor.ALL.name())) {
			results = locationDetailsDao.getEntityManager().createQuery(
					"SELECT LD FROM LocationDetails LD WHERE LD.superadminId =:superadminId ORDER BY LD.id desc")
					.setParameter("superadminId", locationRequest.getSuperadminId())
					.getResultList();

		} else if (locationRequest.getRequestedFor().equalsIgnoreCase(RequestFor.BYTYPE.name())) {
			results = locationDetailsDao.getEntityManager().createQuery(
					"SELECT LD FROM LocationDetails LD WHERE LD.locationType =:locationType AND LD.status =:status AND LD.superadminId =:superadminId ORDER BY LD.id desc")
					.setParameter("locationType", locationRequest.getLocationType())
					.setParameter("status", Status.ACTIVE.name())
					.setParameter("superadminId", locationRequest.getSuperadminId())
					.getResultList();

		}  else if (locationRequest.getRequestedFor().equalsIgnoreCase(RequestFor.NEW.name())) {
			results = locationDetailsDao.getEntityManager().createQuery(
					"SELECT LD FROM LocationDetails LD WHERE LD.status =:status AND LD.superadminId =:superadminId ORDER BY LD.id desc")
					.setParameter("status", Status.PENDING.name())
					.setParameter("superadminId", locationRequest.getSuperadminId())
					.getResultList();

		}
		return results;
	}

//	@SuppressWarnings("unchecked")
//	public List<LocationDetails> getLocationDetailsForApproval(LocationRequestObject locationRequest) {
//		List<LocationDetails> results = new ArrayList<>();
//			results = locationDetailsDao.getEntityManager().createQuery(
//					"SELECT LD FROM LocationDetails LD WHERE LD.status =:status AND LD.superadminId =:superadminId ORDER BY LD.id desc")
//					.setParameter("status", Status.INACTIVE.name())
//					.setParameter("superadminId", locationRequest.getSuperadminId())
//					.getResultList();
//		return results;
//	}

}