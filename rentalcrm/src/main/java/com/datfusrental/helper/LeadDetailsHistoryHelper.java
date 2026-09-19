package com.datfusrental.helper;

import java.util.Date;
import java.util.Map;

import javax.transaction.Transactional;

import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.datfusrental.constant.Constant;
import com.datfusrental.dao.LeadDetailsHistoryDao;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.entities.LeadDetailsHistory;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.LeadRequestObject;
import com.datfusrental.util.EntityDiffUtil;

@Component
public class LeadDetailsHistoryHelper {

	@Autowired
	private LeadDetailsHistoryDao leadDetailsHistoryDao;

	@Autowired
	private EntityDiffUtil entityDiffUtil;

	public void validateLeadRequest(LeadRequestObject leadRequestObject) throws BizException {
		if (leadRequestObject == null) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Bad Request Object Null");
		}
	}

	@Transactional
	public void updateLeadHistory(LeadDetails oldLead, LeadDetails newLead, LeadRequestObject leadRequest) {
		String updatedBy = null;
		if (leadRequest != null) {
			updatedBy = leadRequest.getLoginId();
			if (updatedBy == null || updatedBy.isBlank()) {
				updatedBy = leadRequest.getUpdatedBy();
			}
		}
		updateLeadHistory(oldLead, newLead, updatedBy);
	}

	@Transactional
	public void updateLeadHistory(LeadDetails oldLead, LeadDetails newLead, String updatedBy) {
		if (oldLead == null || newLead == null || newLead.getId() == null) {
			return;
		}

		Map<String, Map<String, Object>> differences;
		try {
			differences = entityDiffUtil.getDifferences(oldLead, newLead);
		} catch (IllegalAccessException exception) {
			throw new IllegalStateException("Unable to create lead history", exception);
		}
		if (differences.isEmpty()) {
			return;
		}

		LeadDetailsHistory history = new LeadDetailsHistory();
		history.setLeadId(newLead.getId());
		history.setChangedData(new JSONObject(differences).toString());
		if (updatedBy == null || updatedBy.isBlank()) {
			updatedBy = newLead.getUpdatedBy();
		}
		history.setUpdatedBy(updatedBy == null || updatedBy.isBlank() ? "SYSTEM" : updatedBy);
		history.setUpdatedAt(new Date());
		history.setActionType("UPDATED");
		leadDetailsHistoryDao.persist(history);
	}

	public LeadDetails snapshot(LeadDetails leadDetails) {
		if (leadDetails == null) {
			return null;
		}
		LeadDetails snapshot = new LeadDetails();
		BeanUtils.copyProperties(leadDetails, snapshot);
		return snapshot;
	}

}
