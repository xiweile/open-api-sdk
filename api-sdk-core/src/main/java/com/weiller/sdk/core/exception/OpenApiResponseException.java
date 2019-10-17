package com.weiller.sdk.core.exception;

/**
 * OpenApiResponseException @version 1.0
 *
 */
public class OpenApiResponseException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	private String body;

    private String errorMsg;

    private String errorCode;

    private String requestId;

    public OpenApiResponseException(String s) {
        super(s);
        this.errorMsg = s;
    }

    public OpenApiResponseException(String errorCode,String errorMsg,String requestId) {
        super(String.format( "request error,requestId=[%s],codeCode=[%s],errorMsg=[%s]",requestId,errorCode,errorMsg));
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.requestId = requestId;
    }

    public OpenApiResponseException(String s, Throwable cause) {
        super(s);
        this.errorMsg = s;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public String toString() {
        return "OpenApiResponseException{" +
                "body='" + body + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", requestId='" + requestId + '\'' +
                '}';
    }
}
