package com.tw0far.potiongames.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** The optional lobby number of the setup commands (legacy short form). */
class SetupArgumentsTest {

    @Test
    void longFormParsesLobbyAndNameIndex() {
        SetupArguments.LobbyArguments parsed =
                SetupArguments.parse(false, new String[]{"addarena", "2", "Arena"});

        assertEquals(2, parsed.lobbyId());
        assertEquals(2, parsed.valueIndex());
    }

    @Test
    void shortFormOnlyWorksInSingleLobbyMode() {
        assertNull(SetupArguments.parse(false, new String[]{"addarena", "Arena"}));

        SetupArguments.LobbyArguments parsed =
                SetupArguments.parse(true, new String[]{"addarena", "Arena"});

        assertEquals(1, parsed.lobbyId());
        assertEquals(1, parsed.valueIndex());
    }

    @Test
    void shortFormAcceptsNumericArenaNames() {
        // "42" is the arena name here, not a lobby number
        SetupArguments.LobbyArguments parsed =
                SetupArguments.parse(true, new String[]{"addarena", "42"});

        assertEquals(1, parsed.lobbyId());
        assertEquals(1, parsed.valueIndex());
    }

    @Test
    void invalidArgumentsAreRejected() {
        assertNull(SetupArguments.parse(true, new String[0]));
        assertNull(SetupArguments.parse(true, new String[]{"addarena"}));
        assertNull(SetupArguments.parse(true, new String[]{"addarena", "x", "Arena"}));
        assertNull(SetupArguments.parse(true, null));
    }

    @Test
    void lobbyOnlyArguments() {
        assertEquals(3, SetupArguments.parseLobbyOnly(false, new String[]{"joinsign", "3"}).intValue());
        assertEquals(1, SetupArguments.parseLobbyOnly(true, new String[]{"joinsign"}).intValue());
        assertNull(SetupArguments.parseLobbyOnly(false, new String[]{"joinsign"}));
        assertNull(SetupArguments.parseLobbyOnly(true, new String[]{"joinsign", "abc"}));
    }
}
