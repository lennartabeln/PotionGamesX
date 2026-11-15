package com.tw0far.potiongames.models;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * On a game server the player can either leave the server or stay in the lobby when a round ends
 * and when they use {@code /pg leave}. Both decisions are independent settings; the shipped
 * defaults must keep the round end in the lobby and make {@code /pg leave} leave the server.
 */
class KickOnLeaveConfigTest {

    private static YamlConfiguration config;

    @BeforeAll
    static void load() throws Exception {
        config = new YamlConfiguration();
        try (InputStream in = KickOnLeaveConfigTest.class.getResourceAsStream("/config.yml")) {
            config.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        }
    }

    @Test
    void roundEndStaysInTheLobbyByDefault() {
        assertFalse(config.getBoolean("pg.kickOnRoundEnd", false));
        assertTrue(config.contains("pg.kickOnRoundEnd"), "pg.kickOnRoundEnd missing from config.yml");
    }

    @Test
    void leavingAlwaysLeavesTheServerByDefault() {
        assertTrue(config.getBoolean("pg.kickOnLeave", true));
        assertTrue(config.contains("pg.kickOnLeave"), "pg.kickOnLeave missing from config.yml");
    }

    @Test
    void theHubTargetIsStillConfigured() {
        assertEquals("hub", config.getString("pg.bungeeServer"));
    }
}
