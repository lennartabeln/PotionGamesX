package com.tw0far.potiongames.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SafeZoneTest {

    @Test
    void nearerToTheLobbyThanToTheArenaIsLobby() {
        assertTrue(SafeZone.isLobbySide(4, 100));
        assertTrue(SafeZone.isLobbySide(0, 1));
    }

    @Test
    void nearerToAnArenaIsNotLobby() {
        assertFalse(SafeZone.isLobbySide(100, 4));
        assertFalse(SafeZone.isLobbySide(1, 0));
    }

    @Test
    void theSameDistanceCountsAsArena() {
        // Exactly in the middle: the arena wins, so a round never loses damage
        // because of the zone.
        assertFalse(SafeZone.isLobbySide(25, 25));
    }

    @Test
    void aLobbyWithoutArenasIsLobbyEverywhere() {
        assertTrue(SafeZone.isLobbySide(250000, -1));
    }
}
