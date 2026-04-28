package com.tw0far.potiongames.util;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests for SafeMapAccess utility class
 */
public class SafeMapAccessTest {
    private Map<String, Map<String, String>> nestedMap;
    
    @Before
    public void setUp() {
        nestedMap = new HashMap<>();
        nestedMap.put("key1", new HashMap<>());
        nestedMap.get("key1").put("inner1", "value1");
    }
    
    @Test
    public void testGetValidNestedValue() {
        String result = SafeMapAccess.get(nestedMap, "key1", "inner1");
        assertEquals("value1", result);
    }
    
    @Test
    public void testGetMissingInnerKey() {
        String result = SafeMapAccess.get(nestedMap, "key1", "nonexistent");
        assertNull(result);
    }
    
    @Test
    public void testGetMissingOuterKey() {
        String result = SafeMapAccess.get(nestedMap, "nonexistent", "inner1");
        assertNull(result);
    }
    
    @Test
    public void testGetNullMap() {
        String result = SafeMapAccess.get(null, "key1", "inner1");
        assertNull(result);
    }
    
    @Test
    public void testGetWithDefaultValue() {
        String result = SafeMapAccess.get(nestedMap, "nonexistent", "inner1", "default");
        assertEquals("default", result);
    }
    
    @Test
    public void testGetValidWithDefault() {
        String result = SafeMapAccess.get(nestedMap, "key1", "inner1", "default");
        assertEquals("value1", result);
    }
    
    @Test
    public void testPutNewKey() {
        SafeMapAccess.put(nestedMap, "key2", "inner2", "value2");
        assertEquals("value2", SafeMapAccess.get(nestedMap, "key2", "inner2"));
    }
    
    @Test
    public void testPutCreatesInnerMap() {
        SafeMapAccess.put(nestedMap, "key3", "inner3", "value3");
        assertTrue(nestedMap.containsKey("key3"));
        assertEquals("value3", nestedMap.get("key3").get("inner3"));
    }
    
    @Test
    public void testPutNullMap() {
        // Should handle gracefully without throwing
        SafeMapAccess.put(null, "key1", "inner1", "value");
    }
    
    @Test
    public void testRemoveExistingKey() {
        String removed = SafeMapAccess.remove(nestedMap, "key1", "inner1");
        assertEquals("value1", removed);
        assertNull(SafeMapAccess.get(nestedMap, "key1", "inner1"));
    }
    
    @Test
    public void testRemoveCleanupEmptyMap() {
        SafeMapAccess.remove(nestedMap, "key1", "inner1");
        // Empty map should be removed from outer map
        assertFalse(nestedMap.containsKey("key1"));
    }
    
    @Test
    public void testRemoveNonexistentKey() {
        String removed = SafeMapAccess.remove(nestedMap, "key1", "nonexistent");
        assertNull(removed);
    }
    
    @Test
    public void testContainsExistingKey() {
        boolean exists = SafeMapAccess.contains(nestedMap, "key1", "inner1");
        assertTrue(exists);
    }
    
    @Test
    public void testContainsMissingKey() {
        boolean exists = SafeMapAccess.contains(nestedMap, "key1", "nonexistent");
        assertFalse(exists);
    }
    
    @Test
    public void testContainsMissingOuterKey() {
        boolean exists = SafeMapAccess.contains(nestedMap, "nonexistent", "inner1");
        assertFalse(exists);
    }
    
    @Test
    public void testGetOrCreateExisting() {
        Map<String, String> result = SafeMapAccess.getOrCreate(nestedMap, "key1");
        assertNotNull(result);
        assertTrue(result.containsKey("inner1"));
    }
    
    @Test
    public void testGetOrCreateNew() {
        Map<String, String> result = SafeMapAccess.getOrCreate(nestedMap, "newkey");
        assertNotNull(result);
        assertTrue(nestedMap.containsKey("newkey"));
    }
    
    @Test
    public void testSafeCastValid() {
        String value = "test";
        String result = SafeMapAccess.safeCast(value, String.class);
        assertEquals("test", result);
    }
    
    @Test
    public void testSafeCastInvalid() {
        Integer value = 42;
        String result = SafeMapAccess.safeCast(value, String.class);
        assertNull(result);
    }
    
    @Test
    public void testSafeCastNull() {
        String result = SafeMapAccess.safeCast(null, String.class);
        assertNull(result);
    }
    
    @Test
    public void testGetOrDefaultWithValue() {
        Map<String, String> map = new HashMap<>();
        map.put("key", "value");
        String result = SafeMapAccess.getOrDefault(map, "key", "default");
        assertEquals("value", result);
    }
    
    @Test
    public void testGetOrDefaultMissing() {
        Map<String, String> map = new HashMap<>();
        String result = SafeMapAccess.getOrDefault(map, "missing", "default");
        assertEquals("default", result);
    }
}
