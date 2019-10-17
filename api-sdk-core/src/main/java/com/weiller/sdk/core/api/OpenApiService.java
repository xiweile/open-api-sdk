package com.weiller.sdk.core.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * OpenApiService	@version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface OpenApiService {

    /**
     * 协议
     * @return
     */
	OpenApiProtocol protocol() default OpenApiProtocol.HTTP;
    
    /**
     * 应用服务名称
     * @return
     */
    String name();
}
