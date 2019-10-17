package com.weiller.utils.api;

import lombok.Data;

import java.io.Serializable;

/**
 * OpenApiRequestHeader	@version 1.0
 */
@Data
public class ApiRequestHead implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 请求ID
	 */
	private String requestId;

	/**
	 * tokenID
	 */
	private String tokenId;

	/**
	 * 调用方APPID
	 */
	private String appId;
    
	/**
	 * 请求随机数
	 */
	private String nonce;
    
	/**
	 * 请求时间戳
	 */
	private long timestamp;

	/**
	 * 请求数据签名值
	 */
	private String signature;
}