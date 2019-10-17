package com.weiller.utils.api;

import com.google.common.base.MoreObjects;
import com.weiller.utils.json.JsonKit;

/**
 * ApiResponse @version 1.0
 */
public class ApiResponse<T> {

    private T                   	resData;
    private ApiResponseHead  	    head;


    public ApiResponseHead  getHead() {
        return head ;
    }

    public ApiResponse<T> setHead(ApiResponseHead header) {
        this.head  = header;
        return this;
    }


    public T getResData() {
		return resData;
	}
    
	public T getResData(Class<T> cls) {
        if (this.resData == null) {
            return null;
        }
        String json = JsonKit.toString(this.resData);
        return JsonKit.fromJson(json, cls);
    }

    public  ApiResponse<T> setResData(T body) {
        this.resData = body;
        return this;
    }

    @Override
    public String toString() {
    	return MoreObjects.toStringHelper(this).add("head", head ).add("resData", resData).toString();
    }

    private static ApiResponse apiResponse;

    public static ApiResponse success(ApiResponseHead head, Object body) {
        apiResponse = new ApiResponse();
        apiResponse.setHead(head);
        apiResponse.setResData(body);
        return apiResponse;
    }

    public static ApiResponse fail(ApiResponseHead head) {
        apiResponse = new ApiResponse();
        apiResponse.setHead(head);
        return apiResponse;
    }
}
