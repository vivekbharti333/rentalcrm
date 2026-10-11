package com.datfusrental.object.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One stage row of the Follow-up Health "Open pool (CRM tabs)" breakdown:
 * an open CRM status (ENQUIRY / FOLLOWUP / HOT / ...) and how many leads of
 * the follow-up pool are sitting in it.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowupStageCount {

	private String stage;
	private Long count;

}
