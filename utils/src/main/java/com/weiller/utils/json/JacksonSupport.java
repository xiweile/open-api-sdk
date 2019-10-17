package com.weiller.utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

/**
 * JacksonSupport @version 1.0
 */
public class JacksonSupport implements JsonSupport {

    private ObjectMapper objectMapper = new ObjectMapper();

    {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    }

    @Override
    public String toString(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("序列化失败", e);
        }
    }

    @Override
    public <T> T fromJson(String json, Class<T> cls) {
        JavaType javaType = objectMapper.getTypeFactory().constructType(cls);
        return fromJsonViaJavaType(json, javaType);
    }


    private <T> T fromJsonViaJavaType(String json, JavaType javaType) {
        try {
            return objectMapper.readerFor(javaType).readValue(json);
        } catch (IOException var3) {
            throw new IllegalStateException("反序列化失败", var3);
        }
    }
    public JavaType getParametricType(Class<?> paramtricClass,Class<?> elementClass){
        return objectMapper.getTypeFactory().constructParametricType(paramtricClass,elementClass);
    }

    public <T> T fromJson(String json, Class<T> klass, Class<?> elementClass) {
        JavaType javaType = getParametricType(klass, elementClass);
        return fromJsonViaJavaType(json, javaType);
    }
}
