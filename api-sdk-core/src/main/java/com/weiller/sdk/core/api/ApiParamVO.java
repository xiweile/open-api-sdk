package com.weiller.sdk.core.api;

/**
 * OpenApi	@version 1.0
 */
public class ApiParamVO {

    /**
     * 参数名字
     * @return
     */
    private String name;



    public String getName() {
        return name;
    }

    public ApiParamVO setName(String name) {
        this.name = name;
        return this;
    }
}
