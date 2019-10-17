package com.weiller.sdk.core.client;

import com.google.common.base.Strings;
import com.weiller.sdk.core.api.BaseOpenApi;
import com.weiller.sdk.core.api.OpenApiService;
import com.weiller.sdk.core.api.ServiceInfo;
import com.weiller.sdk.core.config.SdkConfig;
import com.weiller.sdk.core.exception.OpenApiCheckException;
import com.weiller.sdk.core.exception.OpenApiCreationException;
import com.weiller.sdk.core.exception.OpenApiFactoryException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * 对外接口工厂类	@version 1.0
 */
@Slf4j
public class OpenApiFactory {

	private	String 				gatewayAddress;
	private String 				appId;
    private String 				signSecret;
    
    private OpenHttpClient		openHttpClient;
    /** sdk配置信息，从服务器获取 **/
	private SdkConfig sdkConfig;

    private static Map<Class<?>, Object> instances = new HashMap<Class<?>, Object>();

    /**
     * Create a new instance of OpenApiFactory.
     * @param gatewayAddress	网关地址
     * @param appId				应用ID
     * @param signSecret		签名密钥（默认使用SHA-256签名算法）
     */
	public OpenApiFactory(String gatewayAddress,String appId, String signSecret) {
		super();
        if(Strings.isNullOrEmpty(gatewayAddress))
            throw new OpenApiCheckException("网关地址(gatewayAddress)不能为空");
        if(Strings.isNullOrEmpty(appId))
            throw new OpenApiCheckException("应用ID(appId)不能为空");
        if(Strings.isNullOrEmpty(signSecret))
            throw new OpenApiCheckException("签名密钥(signSecret)不能为空");

		this.gatewayAddress = gatewayAddress;
		this.appId 			= appId;
		this.signSecret 	= signSecret;
		
		//创建HTTP客户端对象
		this.openHttpClient = new OpenHttpClient();
		
		log.info("创建成功:{}",this.toString());
	}
    
	public  <T extends BaseOpenApi> T create(final Class<T> klass) {
        try {
            return doCreate(klass);
        } catch (Exception e) {
            throw new OpenApiCreationException(klass + " api创建失败", e);
        }
    }
	
	
	private synchronized <T extends BaseOpenApi> T doCreate(Class<T> klass) throws Exception {
        // 检查klass是否符合OpenApi的定义
		if(null==instances.get(klass)){
			checkClass(klass);

	        ServiceInfo serviceInfo = parseServiceInfo(klass);
	        
	        // 生成api
	        instances.put(klass, generate(klass,serviceInfo));
		}
		T t = (T) instances.get(klass);
        return t;
    }
	
    private <T extends BaseOpenApi> void checkClass(Class<T> klass) {
        if (!klass.isInterface()) {
            throw new OpenApiFactoryException(klass + "不是一个接口");
        }

        if (!BaseOpenApi.class.isAssignableFrom(klass)) {
            throw new OpenApiFactoryException(klass + "没有继承" + BaseOpenApi.class);
        }
    }
    
    private <T extends BaseOpenApi> ServiceInfo parseServiceInfo(Class<T> klass) {
        ServiceInfo serviceInfo = new ServiceInfo(null, null);
        OpenApiService openApiService = klass.getAnnotation(OpenApiService.class);
        if (openApiService == null && klass.getInterfaces().length > 0) {
            for (Class<?> clazz : klass.getInterfaces()) {
                openApiService = clazz.getAnnotation(OpenApiService.class);
                if (openApiService != null) {
                    break;
                }
            }

        }

        if (openApiService != null) {
            serviceInfo.setProtocol(openApiService.protocol());
            serviceInfo.setName(openApiService.name());
        }

        if (serviceInfo.getProtocol() == null) {
            throw new RuntimeException(String.format("接口[%s]未配置协议。", klass.getName()));
        }
        if (serviceInfo.getName() == null) {
            throw new RuntimeException(String.format("接口[%s]未配置服务名。", klass.getName()));
        }

        return serviceInfo;
    }
    

    @SuppressWarnings("unchecked")
	private <T extends BaseOpenApi> T generate(Class<T> klass,ServiceInfo serviceInfo) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return (T) Proxy.newProxyInstance(classLoader, new Class[] { klass }, new OpenApiInvocationHandler(this,serviceInfo));
    }

	public String getGatewayAddress() {
		return gatewayAddress;
	}

	public String getAppId() {
		return appId;
	}

	public String getSignSecret() {
		return signSecret;
	}

	public OpenHttpClient getOpenHttpClient() {
		return openHttpClient;
	}

}
