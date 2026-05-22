package com.tw0far.potiongames.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Safe property accessor for nested map structures with proper generics.
 * Provides typed methods to avoid raw Object casts and ClassCastException at call sites.
 */
public final class SafeMapAccess {

    private SafeMapAccess() {}

    /**
     * Safe access to 2-level nested Map (read-only). Returns null if missing.
     */
    public static <K1, K2, V> V get(Map<K1, ? extends Map<K2, V>> map, K1 key1, K2 key2) {
        if (map == null) return null;
        Map<K2, V> inner = map.get(key1);
        if (inner == null) return null;
        return inner.get(key2);
    }

    /**
     * Safe access to 2-level nested Map with default value.
     */
    public static <K1, K2, V> V get(Map<K1, ? extends Map<K2, V>> map, K1 key1, K2 key2, V defaultValue) {
        V v = get(map, key1, key2);
        return v != null ? v : defaultValue;
    }

    // Single-level map helpers (backwards-compatible)
    public static <K, V> V get(Map<K, V> map, K key) {
        if (map == null) return null;
        return map.get(key);
    }


    /**
     * Safe put to 2-level nested Map (mutating). Creates missing inner maps.
     */
    public static <K1, K2, V> void put(Map<K1, Map<K2, V>> map, K1 key1, K2 key2, V value) {
        if (map == null) return;
        Map<K2, V> inner = map.computeIfAbsent(key1, k -> new HashMap<>());
        inner.put(key2, value);
    }

    /**
     * Safe remove from 2-level nested Map. Cleans up empty inner maps.
     */
    public static <K1, K2, V> V remove(Map<K1, Map<K2, V>> map, K1 key1, K2 key2) {
        if (map == null) return null;
        Map<K2, V> inner = map.get(key1);
        if (inner == null) return null;
        V removed = inner.remove(key2);
        if (inner.isEmpty()) {
            map.remove(key1);
        }
        return removed;
    }

    /**
     * Get or create the inner map (mutating). Returns the inner map instance.
     */
    public static <K1, K2, V> Map<K2, V> getOrCreate(Map<K1, Map<K2, V>> map, K1 key1) {
        if (map == null) return new HashMap<>();
        return map.computeIfAbsent(key1, k -> new HashMap<>());
    }

    /**
     * Safe contains check for nested maps.
     */
    public static <K1, K2> boolean contains(Map<K1, ? extends Map<K2, ?>> map, K1 key1, K2 key2) {
        if (map == null) return false;
        Map<K2, ?> inner = map.get(key1);
        if (inner == null) return false;
        return inner.containsKey(key2);
    }

    /**
     * Safe getOrDefault for single-level maps.
     */
    public static <K, V> V getOrDefault(Map<K, V> map, K key, V defaultValue) {
        if (map == null) return defaultValue;
        return map.getOrDefault(key, defaultValue);
    }

    /**
     * Safe cast with type checking.
     */
    @SuppressWarnings("unchecked")
    public static <T> T safeCast(Object obj, Class<T> targetClass) {
        if (obj == null) return null;
        if (!targetClass.isInstance(obj)) return null;
        return (T) obj;
    }
}

