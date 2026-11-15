package com.tw0far.potiongames.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

/** The 1.0.0 review renamed a few flags; existing configs must keep their values. */
class ConfigKeyFallbackTest {

    private static YamlConfiguration yaml(String content) {
        return YamlConfiguration.loadConfiguration(new StringReader(content));
    }

    @Test
    void theNewNameWinsWhenBothArePresent() {
        YamlConfiguration cfg = yaml("pg:\n  rewards:\n    kill: 25\n  killReward: 10\n");
        assertEquals(25, ConfigurationManager.number(cfg, "pg.rewards.kill", "pg.killReward", 10));
    }

    @Test
    void theLegacyNameIsStillRead() {
        YamlConfiguration cfg = yaml("pg:\n  killReward: 7\n  enableRewards: true\n");
        assertEquals(7, ConfigurationManager.number(cfg, "pg.rewards.kill", "pg.killReward", 10));
        assertTrue(ConfigurationManager.flag(cfg, "pg.rewards.enabled", "pg.enableRewards", false));
    }

    @Test
    void absentKeysFallBackToTheDefault() {
        YamlConfiguration cfg = yaml("pg:\n  language: en_US\n");
        assertEquals(100, ConfigurationManager.number(cfg, "pg.rewards.win", "pg.winningReward", 100));
        assertFalse(ConfigurationManager.flag(cfg, "pg.compass.spawnItem", "pg.compassOnSpawn", false));
        assertTrue(ConfigurationManager.flag(cfg, "pg.compass.tracking", "pg.playerFinder", true));
    }

    @Test
    void nestedNewNamesAreResolved() {
        YamlConfiguration cfg = yaml("pg:\n  compass:\n    spawnItem: true\n    tracking: false\n");
        assertTrue(ConfigurationManager.flag(cfg, "pg.compass.spawnItem", "pg.compassOnSpawn", false));
        assertFalse(ConfigurationManager.flag(cfg, "pg.compass.tracking", "pg.playerFinder", true));
    }
}
