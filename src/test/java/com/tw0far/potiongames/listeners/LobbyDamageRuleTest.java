package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.models.GameStates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Players are damage free while they are in the lobby, not during a round. */
class LobbyDamageRuleTest {

    @Test
    void waitingAndPreparingAreSafe() {
        assertTrue(GameStates.WAITING.isLobbyPhase());
        assertTrue(GameStates.PREPARING.isLobbyPhase());
    }

    @Test
    void resetIsSafeAgain() {
        assertTrue(GameStates.RESET.isLobbyPhase());
    }

    @Test
    void roundsKeepDamage() {
        assertFalse(GameStates.INGAME.isLobbyPhase());
        assertFalse(GameStates.DEATHMATCH.isLobbyPhase());
        assertFalse(GameStates.ENDING.isLobbyPhase());
    }
}
