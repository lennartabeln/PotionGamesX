package com.tw0far.potiongames.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class PlayerUuidResolverTest {
    private static final UUID ONLINE_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID CACHED_UUID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @Test
    void resolvesOnlinePlayerBeforeCachedOfflinePlayer() {
        assertEquals(ONLINE_UUID, PlayerUuidResolver.resolve("Alex", name -> ONLINE_UUID, name -> CACHED_UUID));
    }

    @Test
    void resolvesCachedOfflinePlayerWhenNotOnline() {
        assertEquals(CACHED_UUID, PlayerUuidResolver.resolve("Alex", name -> null, name -> CACHED_UUID));
    }

    @Test
    void returnsNullWhenNeitherLookupKnowsTheName() {
        assertNull(PlayerUuidResolver.resolve("Unknown", name -> null, name -> null));
    }
}
