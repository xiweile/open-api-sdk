package com.weiller.utils.common;

import java.util.Map;

public class ThreadLocalUtil {
 
    private final static ThreadLocal<Map> threadLocal = new ThreadLocal<>();
 
    public static void add(Map<String,Object> map) {
        threadLocal.set(map);
    }
 
    public static Map getMap() {
        return threadLocal.get();
    }
 
    public static void remove() {
        threadLocal.remove();
    }
}