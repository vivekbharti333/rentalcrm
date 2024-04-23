package com.datfusrental.exceptions;

@SuppressWarnings("serial")
public class BizException extends Exception {
    private final int errorCode;
    private final String errorMessage;

    public BizException(String message) {
        super();
        this.errorCode = 0;
        this.errorMessage = message;
    }
    
    public BizException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorMessage = message;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getMessage() {
        return this.errorMessage;
    }
}

