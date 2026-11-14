package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.models.GameStates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Players are damage free while they are in the lobby, not during a round. */
class LobbyDamageRuleTest {

    @Test
    void waitingAndPreparingAreSafe() {
        assertTrue(DamageEventListener.lobbyPhase(GameStates.WAITING));
        assertTrue(DamageEventListener.lobbyPhase(GameStates.PREPARING));
    }

    @Test
    void resetIsSafeAgain() {
        assertTrue(DamageEventListener.lobbyPhase(GameStates.RESET));
    }

    @Test
    void roundsKeepDamage() {
        assertFalse(DamageEventListener.lobbyPhase(GameStates.INGAME));
        assertFalse(DamageEventListener.lobbyPhase(GameStates.DEATHMATCH));
        assertFalse(DamageEventListener.lobbyPhase(GameStates.ENDING));
    }
}
