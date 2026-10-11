package com.datfusrental.object.request;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@SuppressWarnings("serial")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class KnowlarityCallLogRequest implements Serializable {

	/**
	 * Date filter: TODAY, YESTERDAY, LAST_WEEK or CUSTOM.
	 * Defaults to TODAY when blank or not provided.
	 */
	private String requestedFor;

	/**
	 * Required when requestedFor = CUSTOM. Format: yyyy-MM-dd.
	 * firstDate is inclusive, lastDate is inclusive (whole day).
	 */
	private String firstDate;
	private String lastDate;

}
