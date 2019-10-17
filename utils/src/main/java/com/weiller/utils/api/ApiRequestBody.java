package com.weiller.utils.api;

import lombok.Data;

import java.io.Serializable;

/**
 * OpenApiRequestBody	@version 1.0
 */
@Data
public class  ApiRequestBody<T> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ApiRequestHead head;
	private T reqData;
}