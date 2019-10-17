/**
 * 
 */
package com.weiller.sdk.core.api;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tim
 * 网关返回报文头
 */
@Data
public class ResponseHeaders implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1667056253733919266L;

	/**
	 * 请求ID（网关填充）
	 */
	private String requestId;

	/**
	 * 时间戳（网关填充）
	 */
	private long timestamp;

	/**
	 * 错误代码（0为正常）
	 */
	private int errorCode;

	/**
	 * 错误消息（errorCode不为0时有值）
	 */
	private String errorMsg;

	/**
	 * 请求耗时（网关填充）
	 */
	private long time;



	private static ResponseHeaders responseHead;


	public ResponseHeaders(){}

	public ResponseHeaders(String requestId,int errorCode, String errorMsg, long timestamp, long time) {
		this.requestId = requestId;
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
		this.timestamp = timestamp;
		this.time = time;
	}
	public static ResponseHeaders build() {
		if(responseHead==null){
			responseHead = new ResponseHeaders();
		}
		return responseHead;
	}


}
