package com.weiller.sdk.core.client;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * 对外接口工厂类帮助类	@version 1.0
 */
public class OpenApiFactoryHelper {

	private static LoadingCache<OpenApiFactoryKey, OpenApiFactory> openApiFactoryCache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterAccess(1, TimeUnit.MINUTES)
            .build(new CacheLoader<OpenApiFactoryKey, OpenApiFactory>() {
                @Override
                public OpenApiFactory load(OpenApiFactoryKey key) {
                    return generateValue(key);
                }

                private OpenApiFactory generateValue(OpenApiFactoryKey key) {
                	return new OpenApiFactory(key.getGatewayAddress(),key.getAppId(), key.getSignSecret());
                }
            });
	
	private OpenApiFactoryHelper(){};
	
	 public static OpenApiFactory getOpenApiFactory(final String gatewayAddress,final String appId,String signSecret) {
		 OpenApiFactoryKey key = new OpenApiFactoryKey(gatewayAddress,appId,signSecret);
		 return openApiFactoryCache.getUnchecked(key);
	 }
	 
	@Data
	@AllArgsConstructor
	static class OpenApiFactoryKey{
		private String 	gatewayAddress;
		private String	appId;
	    private String 	signSecret;
	}
}
