package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.models.GameStates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Lobby members are damage free until their round runs: only INGAME and
 * DEATHMATCH count as a running round, everything else is lobby.
 */
class LobbyDamageRuleTest {

    @Test
    void waitingAndPreparingAreSafe() {
        assertFalse(GameStates.WAITING.isRunning());
        assertFalse(GameStates.PREPARING.isRunning());
    }

    @Test
    void theCleanupAfterARoundIsSafeAgain() {
        assertFalse(GameStates.ENDING.isRunning());
        assertFalse(GameStates.RESET.isRunning());
    }

    @Test
    void onlyRoundStatesKeepDamage() {
        assertTrue(GameStates.INGAME.isRunning());
        assertTrue(GameStates.DEATHMATCH.isRunning());
    }
}
