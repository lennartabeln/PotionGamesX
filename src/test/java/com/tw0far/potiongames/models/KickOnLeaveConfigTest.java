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
 * On a game server a player can either be kicked off the server or stay in the lobby, both when a
 * round ends and when they use {@code /pg leave}. The two settings are independent and both
 * default to {@code false}, so an upgrade never kicks players that used to stay.
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
    void leavingStaysInTheLobbyByDefault() {
        assertFalse(config.getBoolean("pg.kickOnLeave", false));
        assertTrue(config.contains("pg.kickOnLeave"), "pg.kickOnLeave missing from config.yml");
    }

    @Test
    void theHubTargetIsStillConfigured() {
        assertEquals("hub", config.getString("pg.bungeeServer"));
    }
}
