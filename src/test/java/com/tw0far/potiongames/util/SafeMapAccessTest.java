package com.tw0far.potiongames.util;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SafeMapAccessTest {

    @Test
    public void testGetOrDefaultSingleLevel() {
        Map<String, Integer> map = new HashMap<>();
        map.put("a", 1);

        Integer v1 = SafeMapAccess.getOrDefault(map, "a", 0);
        Integer v2 = SafeMapAccess.getOrDefault(map, "b", 0);

        assertEquals(1, v1);
        assertEquals(0, v2);
    }

    @Test
    public void testGetNestedAndDefault() {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        Map<String, Integer> inner = new HashMap<>();
        inner.put("y", 2);
        map.put("x", inner);

        Integer found = SafeMapAccess.get(map, "x", "y");
        Integer missingDefault = SafeMapAccess.get(map, "x", "z", 5);
        Integer missing = SafeMapAccess.get(map, "no", "k");

        assertEquals(2, found);
        assertEquals(5, missingDefault);
        assertNull(missing);
    }

    @Test
    public void testSafeCastBehavior() {
        Object s = "hello";
        String casted = SafeMapAccess.safeCast(s, String.class);
        Integer wrong = SafeMapAccess.safeCast(s, Integer.class);

        assertEquals("hello", casted);
        assertNull(wrong);

        Object n = 42;
        Number number = SafeMapAccess.safeCast(n, Number.class);
        assertNotNull(number);
        assertTrue(number instanceof Integer);
    }

    @Test
    public void testTypedGenericsWithDifferentMapTypes() {
        // Map with Number as value type (supertype of Integer)
        Map<Object, Map<String, Number>> map = new HashMap<>();
        Map<String, Number> inner = new HashMap<>();
        inner.put("s", 10); // Integer stored as Number
        map.put("k", inner);

        Number result = SafeMapAccess.get(map, "k", "s", 0);
        assertNotNull(result);
        assertEquals(10, result.intValue());

        // When inner map missing, default should be returned
        Number defaultVal = SafeMapAccess.get(map, "k", "no", 99);
        assertEquals(99, defaultVal.intValue());

        // Use wildcard map reference to ensure method tolerates different generic declarations
        Map rawOuter = map; // raw type erases generics
        Number fromRaw = SafeMapAccess.get(rawOuter, "k", "s", 0);
        assertNotNull(fromRaw);
        assertEquals(10, fromRaw.intValue());
    }
}
