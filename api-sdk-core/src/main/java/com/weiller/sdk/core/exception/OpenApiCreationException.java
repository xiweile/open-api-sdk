package com.weiller.sdk.core.exception;

/**
 * OpenApiCreationException @version 1.0
 */
public class OpenApiCreationException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OpenApiCreationException(String message) {
        super(message);
    }

    public OpenApiCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
