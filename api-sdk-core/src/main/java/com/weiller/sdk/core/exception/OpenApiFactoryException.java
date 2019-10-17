package com.weiller.sdk.core.exception;


/**
 * OpenApiFactoryException	@version 1.0
 */
public class OpenApiFactoryException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OpenApiFactoryException(String s) {
        super(s);
    }

    public OpenApiFactoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
