package com.weiller.utils.common;

import java.util.HashMap;
import java.util.Map;

/**
 * ObjectParamsBuilder	@version 1.0
 */
public class ObjectParamsBuilder{
	
	private Map<String,Object> params;
	
	public static ObjectParamsBuilder newBuilder(){
		ObjectParamsBuilder builder = new ObjectParamsBuilder();
		builder.params = new HashMap<String, Object>();
		return builder;
	}
	
	public ObjectParamsBuilder addParam(String key,Object value){
		params.put(key, value);
		return this;
	}
	
	public Map<String,Object> build(){
		return params;
	}

}
