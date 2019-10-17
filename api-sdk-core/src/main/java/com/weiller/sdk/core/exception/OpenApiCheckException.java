package com.weiller.sdk.core.exception;

/**
 * OpenApiCheckException @version 1.0
 */
public class OpenApiCheckException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OpenApiCheckException(String message) {
        super(message);
    }

    public OpenApiCheckException(String message, Throwable cause) {
        super(message, cause);
    }
}
