package com.datfusrental.object.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response<T> implements Serializable {

    private T payload;
    private List<T> listPayload;
    @SuppressWarnings("rawtypes")
	private Map mapPayload;
    
    private int responseCode;
    private String responseMessage;
    private String status;
    private String comments;
    private String totalNumber;
    private Throwable throwable;


    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }


    public List<T> getListPayload() {
		return listPayload;
	}

	public void setListPayload(List<T> listPayload) {
		this.listPayload = listPayload;
	}
	
	
	@SuppressWarnings("rawtypes")
	public Map getMapPayload() {
		return mapPayload;
	}

	@SuppressWarnings("rawtypes")
	public void setMapPayload(Map mapPayload) {
		this.mapPayload = mapPayload;
	}

	@Override
	public String toString() {
		return "Response {payload=" + payload + 
				", listPayload=" + listPayload + 
				", responseCode=" + responseCode + 
				", responseMessage=" + responseMessage + 
				", status=" + status + 
				", comments=" + comments + "}";
	}

	/**
	 * @return the throwable
	 */
	public Throwable getThrowable() {
		return throwable;
	}

	/**
	 * @param throwable the throwable to set
	 */
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
	
}
