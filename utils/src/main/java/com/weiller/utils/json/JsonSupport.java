package com.weiller.utils.json;

public interface JsonSupport {

    String toString(Object data);

    <T> T fromJson(String json, Class<T> cls);

    <T> T fromJson(String json, Class<T> klass, Class<?> elementClass);

}