package com.ejie.x38.serialization;

import java.util.HashMap;
import java.util.Map;

public class ThreadSafeCache {
	
    private static ThreadLocal<Map<String, String>> map = 
        new ThreadLocal<Map<String, String>> () {
            @Override protected Map<String, String> initialValue() {
                return new HashMap<String, String>();
        }
    };

    public static void addValue(String k, String v) {
    	map.get().put(k, v);
    }
    
    public static Map<?, ?> getMap(){
    	return (Map<?, ?>) map.get();
    }
    
    public static void clearCurrentThreadCache(){
    	map.remove();
    }
}