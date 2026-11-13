package com.tw0far.potiongames.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class SetupInputModesTest {
    private static final UUID FIRST = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID SECOND = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @Test
    void keepsPendingSetupModesIndependentPerPlayer() {
        SetupInputModes modes = new SetupInputModes();

        modes.set(FIRST, SetupInputMode.ADD_LOBBY);
        modes.set(SECOND, SetupInputMode.ADD_ARENA);

        assertEquals(SetupInputMode.ADD_LOBBY, modes.get(FIRST));
        assertEquals(SetupInputMode.ADD_ARENA, modes.get(SECOND));

        modes.remove(FIRST);
        assertNull(modes.get(FIRST));
        assertEquals(SetupInputMode.ADD_ARENA, modes.get(SECOND));
    }

    @Test
    void clearingOnePlayersModeLeavesOtherPendingModesAlone() {
        SetupInputModes modes = new SetupInputModes();
        modes.set(FIRST, SetupInputMode.ADD_LOBBY);
        modes.set(SECOND, SetupInputMode.DEL_LOBBY);

        modes.set(FIRST, null);

        assertFalse(modes.has(FIRST));
        assertTrue(modes.has(SECOND));
        assertEquals(SetupInputMode.DEL_LOBBY, modes.get(SECOND));
    }

    @Test
    void clearRemovesEveryPlayer() {
        SetupInputModes modes = new SetupInputModes();
        modes.set(FIRST, SetupInputMode.ADD_LOBBY);
        modes.set(SECOND, SetupInputMode.DEL_ARENA);

        modes.clear();

        assertFalse(modes.has(FIRST));
        assertFalse(modes.has(SECOND));
        assertEquals(0, modes.size());
    }

    @Test
    void ignoresNullPlayers() {
        SetupInputModes modes = new SetupInputModes();
        modes.set(null, SetupInputMode.ADD_LOBBY);

        assertNull(modes.get(null));
        assertFalse(modes.has(null));
        assertEquals(0, modes.size());
    }
}
