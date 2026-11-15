package com.tw0far.potiongames.listeners;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ServerListPingTest {

    @Test
    void aGameServerOwnsTheServerListEntry() {
        assertTrue(ServerListPingEventListener.shouldOverrideMotd(true, false));
        assertTrue(ServerListPingEventListener.shouldOverrideMotd(true, true));
    }

    @Test
    void autoJoinOnConnectAlsoOwnsTheServerListEntry() {
        assertTrue(ServerListPingEventListener.shouldOverrideMotd(false, true));
    }

    @Test
    void aPlainLobbyServerKeepsItsOwnMotd() {
        assertFalse(ServerListPingEventListener.shouldOverrideMotd(false, false));
    }
}
