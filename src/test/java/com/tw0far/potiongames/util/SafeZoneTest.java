package com.tw0far.potiongames.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SafeZoneTest {

    @Test
    void insideTheRadiusIsProtected() {
        assertTrue(SafeZone.within(0, 0, 16));
        assertTrue(SafeZone.within(15, 0, 16));
        assertTrue(SafeZone.within(0, -15, 16));
        assertTrue(SafeZone.within(8, 8, 16));
    }

    @Test
    void outsideTheRadiusIsNotProtected() {
        assertFalse(SafeZone.within(17, 0, 16));
        assertFalse(SafeZone.within(0, 19, 16));
        assertFalse(SafeZone.within(12, 12, 16));
    }

    @Test
    void theEdgeCountsAsInside() {
        assertTrue(SafeZone.within(16, 0, 16));
    }

    @Test
    void zeroOrNegativeRadiusDisablesTheZone() {
        assertFalse(SafeZone.within(0, 0, 0));
        assertFalse(SafeZone.within(0, 0, -5));
    }
}
