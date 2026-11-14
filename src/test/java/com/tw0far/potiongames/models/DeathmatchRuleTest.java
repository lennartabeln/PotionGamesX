package com.tw0far.potiongames.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A deathmatch only exists when the arena provides at least two deathmatch
 * spawns. Legacy teleported survivor n to {@code deathmatch.n}, so anything
 * below two spawns could not hold a 1v1.
 */
class DeathmatchRuleTest {

    @Test
    void noDeathmatchWithoutSpawns() {
        assertFalse(Lobby.deathmatchAvailable(0));
        assertEquals(2, Lobby.deathmatchThreshold(0));
    }

    @Test
    void oneSpawnIsNotEnough() {
        assertFalse(Lobby.deathmatchAvailable(1));
        assertEquals(2, Lobby.deathmatchThreshold(1));
    }

    @Test
    void twoSpawnsAllowTheLegacyOneAgainstOne() {
        assertTrue(Lobby.deathmatchAvailable(2));
        assertEquals(2, Lobby.deathmatchThreshold(2));
    }

    @Test
    void moreSpawnsRaiseTheThreshold() {
        assertTrue(Lobby.deathmatchAvailable(6));
        assertEquals(6, Lobby.deathmatchThreshold(6));
    }
}
