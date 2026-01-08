package com.datfusrental.helper;

import java.util.Date;
import java.util.Map;

import javax.transaction.Transactional;

import org.json.JSONObject;
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
	public void updateLeadHistory(LeadDetails oldLead, LeadDetails newLead, LeadRequestObject leadRequest) throws BizException, Exception {

		// ✅ Compare old vs new
		Map<String, Map<String, Object>> differences = entityDiffUtil.getDifferences(oldLead, newLead);

		// ✅ Save history
		LeadDetailsHistory history = new LeadDetailsHistory();
		history.setLeadId(newLead.getId());
		history.setChangedData(new JSONObject(differences).toString());
		history.setUpdatedBy(newLead.getUpdatedBy());
		history.setUpdatedAt(new Date());
		history.setActionType("UPDATED");
		history.setUpdatedBy(leadRequest.getLoginId());
		leadDetailsHistoryDao.persist(history);

	}

}
