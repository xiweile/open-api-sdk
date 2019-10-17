package com.weiller.sdk.core.exception;

/**
 * OpenApiInvokeException @version 1.0
 */
public class OpenApiInvokeException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OpenApiInvokeException(String message) {
        super(message);
    }

    public OpenApiInvokeException(String message, Throwable cause) {
        super(message, cause);
    }
}
