package com.tw0far.potiongames.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShopEntriesTest {

    @Test
    void countsTheConfiguredNumberOfEntries() {
        assertEquals(1, ShopEntries.activeCount(1, 20));
        assertEquals(19, ShopEntries.activeCount(19, 20));
        assertEquals(20, ShopEntries.activeCount(20, 20));
    }

    @Test
    void zeroActivatesNothing() {
        assertEquals(0, ShopEntries.activeCount(0, 20));
    }

    @Test
    void negativesActivateNothing() {
        assertEquals(0, ShopEntries.activeCount(-5, 20));
    }

    @Test
    void moreThanAvailableIsCappedAtTheFileSize() {
        assertEquals(20, ShopEntries.activeCount(25, 20));
    }

    @Test
    void emptyShopStaysEmpty() {
        assertEquals(0, ShopEntries.activeCount(10, 0));
    }
}
