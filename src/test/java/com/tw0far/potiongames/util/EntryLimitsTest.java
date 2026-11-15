package com.tw0far.potiongames.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntryLimitsTest {

    @Test
    void countsTheConfiguredNumberOfEntries() {
        assertEquals(1, EntryLimits.activeCount(1, 20));
        assertEquals(2, EntryLimits.activeCount(2, 20));
        assertEquals(20, EntryLimits.activeCount(20, 20));
    }

    @Test
    void zeroActivatesNothing() {
        assertEquals(0, EntryLimits.activeCount(0, 20));
    }

    @Test
    void negativesActivateNothing() {
        assertEquals(0, EntryLimits.activeCount(-5, 20));
    }

    @Test
    void moreThanAvailableIsCappedAtTheFileSize() {
        assertEquals(20, EntryLimits.activeCount(25, 20));
        assertEquals(6, EntryLimits.activeCount(6, 7));
    }

    @Test
    void emptyFileStaysEmpty() {
        assertEquals(0, EntryLimits.activeCount(10, 0));
    }
}
