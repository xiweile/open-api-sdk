package com.weiller.utils.validate;

/**
 * 校验异常	@version 1.0
 */
public class ValidationException extends RuntimeException {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -118159199582907435L;

    public ValidationException(String message) {
    	super(message);
    }

    public ValidationException(String code,String message) {
    	super(String.format("参数错误：(%s)%s", code,message));
    }
}
