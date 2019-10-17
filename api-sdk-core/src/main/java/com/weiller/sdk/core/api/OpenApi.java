package com.weiller.sdk.core.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * OpenApi	@version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface OpenApi {

    /**
     * api访问路径
     * MQ：topic:tags
     * HTTP：服务url
     * @return
     */
    String path() default "";
    
    /**
     * api名字
     * @return
     */
    String name();
}
