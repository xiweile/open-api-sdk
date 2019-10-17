package com.weiller.sdk.core.config;

import lombok.Data;

/**
 * SdkConfigVo @version 1.0
 */
@Data
public class SdkConfig {

	private String appId;
	private long timestamp;
	
	//rocketMq
	private String mqNameSrvAddr;
	//ons
	private String onsNameSrvAddr;
	private String onsAccessKey;
	private String onsSecretKey;

	private String fjExtensions;
	//fdfs
	private String fdfsTrackerServer;
	//oss
	private String ossAccessKeyId;
	private String ossAccessKeySecret;
	private String ossEndpoint;
	private String ossBucketName;

	private String mqTopicSb;
	private String mqTopicJyQz;
	private String mqTopicTzQz;
	private String mqPidQz;
	private String mqCidQz;


}
