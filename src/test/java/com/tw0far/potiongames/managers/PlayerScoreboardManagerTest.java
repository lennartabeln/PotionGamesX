package com.tw0far.potiongames.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PlayerScoreboardManagerTest {

    @Test
    void formatsRemainingTimeAsMinutesAndSeconds() {
        assertEquals("4:35", PlayerScoreboardManager.clock(275));
    }

    @Test
    void keepsSecondsTwoDigit() {
        assertEquals("0:07", PlayerScoreboardManager.clock(7));
        assertEquals("10:00", PlayerScoreboardManager.clock(600));
    }

    @Test
    void clampsNegativeTimersToZero() {
        assertEquals("0:00", PlayerScoreboardManager.clock(-1));
        assertEquals("0:00", PlayerScoreboardManager.clock(0));
    }
}
