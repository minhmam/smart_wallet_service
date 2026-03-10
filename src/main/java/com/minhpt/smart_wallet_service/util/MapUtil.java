package com.minhpt.smart_wallet_service.util;


import java.util.HashMap;
import java.util.Map;

public class MapUtil {
    private MapUtil() {
        throw new IllegalStateException("MapUtil class");
    }
    public static <K, V> Map<K, V> of(K key, V value) {
        Map<K, V> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    public static <K, V> V get(Map<K, Object> map, K key, Class<V> classType) {
        Object value = map.get(key);
        if(classType.isInstance(value)) return classType.cast(value);
        return null;
    }

    public static <K> Object get(Map<K, Object> map, K key) {
        return map.get(key);
    }
}
