package com.weiller.utils.api;

import lombok.Data;

/**
 * OpenApiResponseHeader	@version 1.0
 */
@Data
public class ApiResponseHead {

	/**
	 * 请求ID
	 */
	private String requestId;

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

	/**
	 * 错误代码（0为正常）
	 */
	private String retCode;

	/**
	 * 错误消息（retCode不为0时有值）
	 */
	private String retMsg;

	/**
	 * 请求耗时（网关填充）
	 */
	private long costTime;


	public ApiResponseHead(){}

	public ApiResponseHead(String requestId, String retCode, String retMsg, long timestamp, long costTime) {
		this.requestId = requestId;
		this.retCode = retCode;
		this.retMsg = retMsg;
		this.timestamp = timestamp;
		this.costTime = costTime;
	}
}