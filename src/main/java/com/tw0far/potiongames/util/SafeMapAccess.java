package com.tw0far.potiongames.util;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Safe property accessor for nested map structures.
 * 
 * FIXES:
 * - NullPointerException from nested map access
 * - Memory leaks from orphaned null entries
 * - Unsafe casting operations
 * 
 * USAGE:
 * OLD: HashMap<String, HashMap<Player, String>> data = plugin.lobbyteamplayernames;
 *      String value = data.get(lobbyId).get(player);  // NPE if lobbyId or player not found
 * 
 * NEW: String value = SafeMapAccess.get(data, lobbyId, player, "default");
 *      // Returns "default" safely if any level is null
 */
public class SafeMapAccess {
    
    /**
     * Safe access to 2-level nested HashMap
     * Returns null if either key doesn't exist
     */
    @SuppressWarnings("unchecked")
    public static <K1, K2, V> V get(Object mapObj, K1 key1, K2 key2) {
        if (mapObj == null) return null;
        try {
            Map<K1, ?> map = (Map<K1, ?>) mapObj;
            Object innerMapObj = map.get(key1);
            if (innerMapObj == null) return null;
            Map<K2, V> innerMap = (Map<K2, V>) innerMapObj;
            return innerMap.get(key2);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
    
    /**
     * Safe access to 2-level nested HashMap with default value
     */
    public static <K1, K2, V> V get(Object mapObj, K1 key1, K2 key2, V defaultValue) {
        V value = get(mapObj, key1, key2);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Safe put to 2-level nested HashMap
     * Creates missing levels automatically
     */
    @SuppressWarnings("unchecked")
    public static <K1, K2, V> void put(Object mapObj, K1 key1, K2 key2, V value) {
        if (mapObj == null) return;
        try {
            Map<K1, Object> map = (Map<K1, Object>) mapObj;
            Object innerMapObj = map.computeIfAbsent(key1, k -> new HashMap<K2, V>());
            Map<K2, V> innerMap = (Map<K2, V>) innerMapObj;
            innerMap.put(key2, value);
        } catch (ClassCastException | NullPointerException e) {
            // Silently fail
        }
    }
    
    /**
     * Safe remove from 2-level nested HashMap
     * Cleans up empty maps
     */
    @SuppressWarnings("unchecked")
    public static <K1, K2, V> V remove(Object mapObj, K1 key1, K2 key2) {
        if (mapObj == null) return null;
        try {
            Map<K1, ?> map = (Map<K1, ?>) mapObj;
            Object innerMapObj = map.get(key1);
            if (innerMapObj == null) return null;
            
            Map<K2, V> innerMap = (Map<K2, V>) innerMapObj;
            V removed = innerMap.remove(key2);
            
            // Clean up empty inner map
            if (innerMap.isEmpty()) {
                map.remove(key1);
            }
            
            return removed;
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
    
    /**
     * Get or create inner map
     */
    @SuppressWarnings("unchecked")
    public static <K1, K2, V> Map<K2, V> getOrCreate(Object mapObj, K1 key1) {
        if (mapObj == null) return new HashMap<>();
        try {
            Map<K1, Object> map = (Map<K1, Object>) mapObj;
            return (Map<K2, V>) map.computeIfAbsent(key1, k -> new HashMap<K2, V>());
        } catch (ClassCastException | NullPointerException e) {
            return new HashMap<>();
        }
    }
    
    /**
     * Safe check if key1+key2 exists
     */
    @SuppressWarnings("unchecked")
    public static <K1, K2> boolean contains(Object mapObj, K1 key1, K2 key2) {
        if (mapObj == null) return false;
        try {
            Map<K1, ?> map = (Map<K1, ?>) mapObj;
            Object innerMapObj = map.get(key1);
            if (innerMapObj == null) return false;
            Map<K2, ?> innerMap = (Map<K2, ?>) innerMapObj;
            return innerMap.containsKey(key2);
        } catch (ClassCastException | NullPointerException e) {
            return false;
        }
    }
    
    /**
     * Safe get for single-level maps
     */
    public static <K, V> V getOrDefault(Map<K, V> map, K key, V defaultValue) {
        if (map == null) return defaultValue;
        return map.getOrDefault(key, defaultValue);
    }
    
    /**
     * Safe cast with type checking
     */
    @SuppressWarnings("unchecked")
    public static <T> T safeCast(Object obj, Class<T> targetClass) {
        if (obj == null) return null;
        if (!targetClass.isInstance(obj)) {
            // Log warning instead of throwing
            return null;
        }
        return (T) obj;
    }
}

