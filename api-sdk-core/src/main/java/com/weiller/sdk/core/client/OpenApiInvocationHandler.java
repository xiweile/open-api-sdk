package com.weiller.sdk.core.client;

import com.google.common.base.Strings;
import com.weiller.sdk.core.api.*;
import com.weiller.sdk.core.exception.OpenApiCheckException;
import com.weiller.sdk.core.exception.OpenApiCreationException;
import com.weiller.sdk.core.exception.OpenApiResponseException;
import com.weiller.utils.api.ApiResponse;
import com.weiller.utils.api.ApiResponseHead;
import com.weiller.utils.common.ClassKit;
import com.weiller.utils.json.Jackson;
import com.weiller.utils.json.JsonKit;
import com.weiller.utils.validate.Validations;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态代理 @version 1.0
 */
public class OpenApiInvocationHandler implements InvocationHandler{

	/**
	 * 开发者参数，防止消息监听错乱
	 */
	private static final String developName = System.getProperty("MTAX_DEVELOPER_NAME") == null?"":System.getProperty("MTAX_DEVELOPER_NAME");

	private OpenApiFactory openApiFactory;
	private ServiceInfo serviceInfo;
	
	public OpenApiInvocationHandler(OpenApiFactory openApiFactory,ServiceInfo serviceInfo){
		this.openApiFactory = openApiFactory;
		this.serviceInfo = serviceInfo;
	}

	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Map<String,Object> reqData = assembleReqData(method, args);
        OpenApiVO openApiVO = getOpenApi(method);
        OpenApiRequest openApiRequest = new OpenApiRequest(openApiVO.getPath())
        	   .setHost(this.openApiFactory.getGatewayAddress())
    		   .setAppId(this.openApiFactory.getAppId())
    		   .setSignSecret(this.openApiFactory.getSignSecret())
    		   .setReqData(reqData).buildHeader();
        Object rt = null;
        switch (openApiVO.getProtocol()) {
       		case HTTP:
       			rt = doHttpInvoke(method, openApiRequest);
       			break;
       		default:
       			break;
        }
        return rt;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object doHttpInvoke(Method method,OpenApiRequest openApiRequest){
		String respText = this.openApiFactory.getOpenHttpClient().post(openApiRequest);
        
        Type genericReturnType = method.getGenericReturnType();
        Class<?> returnType = method.getReturnType();

        ApiResponse entireResponse = JsonKit.fromJson(respText, ApiResponse.class);

        if (!(genericReturnType instanceof ParameterizedTypeImpl)) {
            // 不是泛型

            if (returnType.isAssignableFrom(ApiResponse.class)) {
                return entireResponse;
            }

            if (entireResponse.getHead().getRetCode().equals("0")) {
            	ApiResponseHead header = entireResponse.getHead ();
                throw new OpenApiResponseException(header.getRetCode(), header.getRetMsg(), header.getRequestId());
            }
            if (returnType == Void.class) {
                return null;
            }

            return entireResponse.getResData(returnType);
        }
        ParameterizedTypeImpl genericReturnTypeImpl = (ParameterizedTypeImpl) genericReturnType;
        Type type = genericReturnTypeImpl.getActualTypeArguments()[0];
        Object javaBody = entireResponse.getResData();
        // 如果返回类型是ApiResponse或ApiResponse的子类，则直接返回
        if (returnType.isAssignableFrom(ApiResponse.class)) {
           // Object o = JsonKit.fromJson(JsonKit.toString(javaBody), (Class<?>) type);
            Object o = Jackson.fromJson(JsonKit.toString(javaBody),type);
            entireResponse.setResData(o);
            return entireResponse;
        } else {
        	if(javaBody==null){
        		return null;
        	}
            Object o = JsonKit.fromJson(JsonKit.toString(javaBody), returnType, (Class<?>) type);
            return o;
        }
	}
	@SuppressWarnings("unchecked")
	private Map<String,Object> assembleReqData(Method method, Object[] args) {
        ApiParamVO[] apiParams = lookupParameterNames(method);
        Class<?>[] parameterTypes = method.getParameterTypes();

        // 参数只有一个,并且是复合类型
        if (apiParams.length == 1 && !isSimpleType(parameterTypes[0])) {
        	Validations.validate(args[0]);
        	return JsonKit.fromJson(JsonKit.toString(args[0]), Map.class);
        }

        Map<String, Object> bodyMap = new HashMap<String, Object>();
        for (int i = 0; i < apiParams.length; i++) {
            String parameterName = apiParams[i].getName();
            Object arg = args[i];
            Validations.validate(arg);
            Class<?> parameterType = parameterTypes[i];
            if (isSimpleType(parameterType)) {
                // 简单类型
                bodyMap.put(parameterName, arg);
            } else {
                Map<String, Object> map = beanToMap(arg);
                bodyMap.putAll(map);
            }
        }
        return bodyMap;
    }
	
	private OpenApiVO getOpenApi(Method method) {
        OpenApi openApi = method.getAnnotation(OpenApi.class);
        if(openApi ==null)
            throw  new OpenApiCheckException("未配置openApi");
        if(Strings.isNullOrEmpty(openApi.path()))
            throw  new OpenApiCheckException("未配置path");
        return new OpenApiVO().setProtocol(serviceInfo.getProtocol()).setPath(openApi.path()).setName(openApi.name());
    }
	
    private ApiParamVO[] lookupParameterNames(Method method) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        ApiParamVO[] params = new ApiParamVO[parameterAnnotations.length];
        for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation[] parameterAnnotation = parameterAnnotations[i];
            for (Annotation annotation : parameterAnnotation) {
                if (annotation instanceof ApiParam) {
                    ApiParam apiParam = (ApiParam) annotation;
                    ApiParamVO apiParamVO = new ApiParamVO().setName(apiParam.value());
                    params[i] = apiParamVO;
                }
            }
        }
        return params;
    }
    
    private boolean isSimpleType(Class<?> klass) {
        if (ClassKit.isPrimitiveOrWrapper(klass) || klass == String.class || Map.class.isAssignableFrom(klass)
                || Collection.class.isAssignableFrom(klass)) {
            return true;
        }
        return false;
    }
    
    
    
    
    private Map<String, Object> beanToMap(Object arg) {
        if (arg == null) {
            return Collections.emptyMap();
        }
        try {
            return doBeanToMap(arg);
        } catch (IllegalAccessException e) {
            throw new OpenApiCreationException("bean转map错误," + JsonKit.toString(arg), e);
        }
    }
    
    private Map<String, Object> doBeanToMap(Object arg) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<String, Object>();
        Field[] declaredFields = arg.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            String fieldName = declaredField.getName();
            declaredField.setAccessible(true);
            Object value = declaredField.get(arg);
            map.put(fieldName, value);
        }
        return map;
    }


}
