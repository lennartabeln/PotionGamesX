package com.tw0far.potiongames.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class LobbyAvailableTeamsTest {

    @Test
    void everyTeamIsAvailableWhileNobodyPickedOne() {
        // A team without players has no entry in the sparse count map; the
        // candidate list must still contain it.
        assertEquals(List.of(1, 2, 3, 4), Lobby.availableTeams(4, teamId -> false));
    }

    @Test
    void fullTeamsAreSkipped() {
        List<Integer> available = Lobby.availableTeams(4, Set.of(2, 4)::contains);
        assertEquals(List.of(1, 3), available);
    }

    @Test
    void noCandidatesWhenEveryTeamIsFull() {
        assertEquals(List.of(), Lobby.availableTeams(3, teamId -> true));
    }

    @Test
    void emptyRangeYieldsNoCandidates() {
        assertEquals(List.of(), Lobby.availableTeams(0, teamId -> false));
    }
}
