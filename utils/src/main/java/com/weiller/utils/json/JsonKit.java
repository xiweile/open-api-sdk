package com.weiller.utils.json;

/**
 * JsonKIt @version 1.0
 */
public class JsonKit {

    private static JsonSupport jsonSupport = new JacksonSupport();

    public static void jsonSupprt(JsonSupport jsonSupport) {
        JsonKit.jsonSupport = jsonSupport;
    }

    public static String toString(Object object) {
        return jsonSupport.toString(object);
    }

    public static <T> T fromJson(String json, Class<T> cls) {
        return jsonSupport.fromJson(json, cls);
    }

    public static <T> T fromJson(String json, Class<T> klass, Class<?> elementClass) {
        return (T) jsonSupport.fromJson(json, klass, elementClass);
    }

}
