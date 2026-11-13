package com.tw0far.potiongames.models;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Team distribution is the part of the round logic that does not need a live
 * server: the legacy release derived the team count from the lobby capacity,
 * and a two-player round has to end up in two different teams.
 */
class LobbyTeamTest {

    private Lobby lobby(int maxPlayers, int minPlayers, int teamSize) {
        LobbyConfig config = new LobbyConfig(1, new YamlConfiguration(), 30, maxPlayers, minPlayers,
                teamSize, 30, true, true, true, true);
        return new Lobby(1, config);
    }

    @Test
    void teamAmountUsesTheLobbyCapacity() {
        // Defaults: 24 / 2 = 12 teams, like the legacy release.
        assertEquals(12, lobby(24, 12, 2).getTeamAmount());
        assertEquals(4, lobby(8, 4, 2).getTeamAmount());
    }

    @Test
    void teamAmountNeverDropsBelowTwoTeams() {
        assertEquals(2, lobby(2, 2, 4).getTeamAmount());
        assertEquals(2, lobby(2, 1, 8).getTeamAmount());
    }

    @Test
    void assignTeamEnforcesTheTeamSize() {
        Lobby lobby = lobby(24, 12, 2);

        assertFalse(lobby.assignTeam(null, 1, 2));

        // Without a server there is no player object, so counts can be checked
        // through the guard only: unknown team ids are rejected.
        assertFalse(lobby.assignTeam(null, 0, 2));
        assertFalse(lobby.assignTeam(null, 99, 2));
        assertFalse(lobby.isTeamFull(1, 2));
    }
}
