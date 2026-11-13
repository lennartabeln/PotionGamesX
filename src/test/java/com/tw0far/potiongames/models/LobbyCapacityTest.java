package com.tw0far.potiongames.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Capacity and deathmatch threshold derive from the arena spawn counts. */
class LobbyCapacityTest {

    @Test
    void capacityIsCappedByTheSmallestArena() {
        assertEquals(8, Lobby.capacityFor(24, List.of(12, 8)));
    }

    @Test
    void configuredMaximumWinsWhenSpawnsArePlenty() {
        assertEquals(6, Lobby.capacityFor(6, List.of(12, 30)));
    }

    @Test
    void setupPhaseKeepsTheConfiguredMaximum() {
        // No usable spawn yet: a fresh lobby must stay joinable.
        assertEquals(24, Lobby.capacityFor(24, List.of()));
        assertEquals(24, Lobby.capacityFor(24, List.of(0, 0)));
    }

    @Test
    void deathmatchThresholdFollowsTheSpawnCount() {
        assertEquals(4, Lobby.deathmatchThreshold(4));
        assertEquals(6, Lobby.deathmatchThreshold(6));
    }

    @Test
    void deathmatchThresholdNeverDropsBelowTwo() {
        assertEquals(2, Lobby.deathmatchThreshold(0));
        assertEquals(2, Lobby.deathmatchThreshold(1));
    }
}
