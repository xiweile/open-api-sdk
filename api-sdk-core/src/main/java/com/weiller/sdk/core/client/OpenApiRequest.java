package com.weiller.sdk.core.client;

import com.weiller.utils.api.ApiRequestBody;
import com.weiller.utils.api.ApiRequestHead;
import com.weiller.utils.encrypt.DigestKit;
import com.weiller.utils.encrypt.Sm3Util;
import com.weiller.utils.json.JsonKit;

import java.util.UUID;

/**
 * OpenApiRequest @version 1.0
 */
public class OpenApiRequest {
	
	private String appId;
	private String signSecret;
	private String host;
    private String path;
    
    private ApiRequestBody body = new ApiRequestBody();

    public OpenApiRequest(String path) {
        this.path = path;
    }
    public String getPath() {
        return path;
    }

    public ApiRequestBody getBody() {
        return body;
    }

	public  OpenApiRequest setHead(ApiRequestHead header) {
		this.getBody().setHead(header);
		return this;
	}
	public OpenApiRequest setReqData(Object reqData) {
		this.getBody().setReqData(reqData);
		return this;
	}
	public String getAppId() {
		return appId;
	}
	public  OpenApiRequest setAppId(String appId) {
		this.appId = appId;
		return this;
	}
	public String getSignSecret() {
		return signSecret;
	}
	public OpenApiRequest setSignSecret(String signSecret) {
		this.signSecret = signSecret;
		return this;
	}
	
	
	public String getHost() {
		return host;
	}
	public  OpenApiRequest setHost(String host) {
		this.host = host;
		return this;
	}
	/**
	 * 构建header
	 */
	public  OpenApiRequest buildHeader() {
		String requestId = UUID.randomUUID().toString().replace("-", "");
		String nonce = UUID.randomUUID().toString().replace("-", "");
        long timestamp = System.currentTimeMillis();
        
        //for test
        //timestamp = DateUtils.addMinutes(new Date(), -30).getTime();
       
        ApiRequestHead header = new ApiRequestHead();
        header.setRequestId(requestId);
        header.setTimestamp(timestamp);
        header.setNonce(nonce);
        header.setAppId(appId);
        
        setHead(header);
        
        calcSignatureWithSm3(this);
        
        return this;
    }
	
	public static void main(String[] args) {
		OpenApiRequest openApiRequest = new OpenApiRequest("");
		openApiRequest.setSignSecret("a6d19538-9888-11e9-8107-48df373b5df8");
		ApiRequestHead header = new ApiRequestHead();
		header.setTimestamp(1561723866550L);
		header.setNonce("8E82CE29047B4CCEB0DBE51F23706550");
		header.setRequestId("8E82CE29047B4CCEB0DBE51F23706550");
		header.setAppId("15000001");
		header.setSignature("a6d19538-9888-11e9-8107-48df373b5df8");
		openApiRequest.setHead(header);

		//openApiRequest.setReqData(dto);
		
		openApiRequest.calcSignatureWithSm3(openApiRequest);
		System.err.println(openApiRequest.getBody().getHead().getSignature());
	}
	
	private void calcSignatureWithSm3( OpenApiRequest openApiRequest){
		String bizJson = JsonKit.toString(openApiRequest.getBody().getReqData());
		ApiRequestHead header = openApiRequest.getBody().getHead();
		String messageText = header.getAppId().concat(openApiRequest.getSignSecret()).concat(header.getRequestId())
				.concat(header.getNonce()).concat(String.valueOf(header.getTimestamp())).concat(bizJson);
		System.out.println("签名参数："+messageText);
		String signature = Sm3Util.encodePassword(messageText, null);
		header.setSignature(signature);
	}
	/**
	 * 签名算法：sha256(appId + secret + requestId + nonce + timestamp + bizJson)
	 * @param openApiRequest
	 */
	private void calcSignature( OpenApiRequest openApiRequest){
		String bizJson = JsonKit.toString(openApiRequest.getBody().getReqData());
		ApiRequestHead header = openApiRequest.getBody().getHead();
		String messageText = header.getAppId().concat(openApiRequest.getSignSecret()).concat(header.getRequestId())
				.concat(header.getNonce()).concat(String.valueOf(header.getTimestamp())).concat(bizJson);
		byte[] bytes = DigestKit.sha256(messageText);
		String signature = DigestKit.toHexString(bytes);
		header.setSignature(signature);
	}
}
