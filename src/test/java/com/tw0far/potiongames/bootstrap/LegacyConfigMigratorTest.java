package com.tw0far.potiongames.bootstrap;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Startup migration of a legacy arenadata.yml / lobbies.yml. */
class LegacyConfigMigratorTest {

    private YamlConfiguration legacyFile() {
        YamlConfiguration legacy = new YamlConfiguration();
        legacy.set("pg.lobbies.1.coords", "legacy-spawn");
        legacy.set("pg.lobbies.1.teamSize", 4);
        legacy.set("pg.lobbies.1.maxPlayers", 12);
        legacy.set("pg.lobbies.2.coords", "second-spawn");
        legacy.set("pg.lobbies.MyLobby.coords", "not-numeric");
        return legacy;
    }

    @Test
    void createsMissingLobbiesWithSpawnAndSettings() {
        YamlConfiguration legacy = legacyFile();
        YamlConfiguration lobbies = new YamlConfiguration();

        int created = LegacyConfigMigrator.createMissingLobbies(
                legacy.getConfigurationSection("pg.lobbies"), lobbies);

        assertEquals(2, created);
        assertEquals("legacy-spawn", lobbies.getString("pg.lobbies.1.spawn"));
        assertEquals(4, lobbies.getInt("pg.lobbies.1.settings.teamSize"));
        assertEquals(12, lobbies.getInt("pg.lobbies.1.settings.maxPlayers"));
        assertEquals("second-spawn", lobbies.getString("pg.lobbies.2.spawn"));
        // Lobbies are addressed by number, so a non-numeric legacy id is skipped
        assertFalse(lobbies.contains("pg.lobbies.MyLobby"));
    }

    @Test
    void existingLobbiesAreLeftAlone() {
        YamlConfiguration legacy = legacyFile();
        YamlConfiguration lobbies = new YamlConfiguration();
        lobbies.set("pg.lobbies.1.spawn", "already-set");
        lobbies.set("pg.lobbies.1.settings.teamSize", 2);

        int created = LegacyConfigMigrator.createMissingLobbies(
                legacy.getConfigurationSection("pg.lobbies"), lobbies);

        assertEquals(1, created);
        assertEquals("already-set", lobbies.getString("pg.lobbies.1.spawn"));
        assertEquals(2, lobbies.getInt("pg.lobbies.1.settings.teamSize"));
        assertTrue(lobbies.contains("pg.lobbies.2"));
    }

    @Test
    void movesRootSettingsIntoTheSettingsSection() {
        YamlConfiguration lobbies = new YamlConfiguration();
        lobbies.set("pg.lobbies.1.teamSize", 4);
        lobbies.set("pg.lobbies.1.roundTime", 10);
        lobbies.set("pg.lobbies.1.spawn", "spawn");
        lobbies.set("pg.lobbies.2.teamSize", 3);
        lobbies.set("pg.lobbies.2.settings.teamSize", 8); // settings win

        int moved = LegacyConfigMigrator.moveLobbySettings(lobbies);

        assertEquals(3, moved);
        assertEquals(4, lobbies.getInt("pg.lobbies.1.settings.teamSize"));
        assertEquals(10, lobbies.getInt("pg.lobbies.1.settings.roundTime"));
        assertEquals("spawn", lobbies.getString("pg.lobbies.1.spawn"));
        assertEquals(8, lobbies.getInt("pg.lobbies.2.settings.teamSize"));
        // only one source of truth remains
        assertFalse(lobbies.contains("pg.lobbies.1.teamSize"));
        assertFalse(lobbies.contains("pg.lobbies.2.teamSize"));
    }

    @Test
    void lowestLobbyIdIsNumeric() {
        YamlConfiguration lobbies = new YamlConfiguration();
        lobbies.createSection("pg.lobbies.10");
        lobbies.createSection("pg.lobbies.2");
        lobbies.createSection("pg.lobbies.two");

        assertEquals(2, LegacyConfigMigrator.lowestLobbyId(lobbies).intValue());
        assertNull(LegacyConfigMigrator.lowestLobbyId(new YamlConfiguration()));
    }

    @Test
    void globalArenasGoIntoEveryLobbyWithoutOverwriting() {
        YamlConfiguration legacy = new YamlConfiguration();
        legacy.set("pg.arenas.1.name", "Arena One");
        legacy.set("pg.arenas.1.spawns.1", "spawn-1");
        YamlConfiguration lobbies = new YamlConfiguration();
        lobbies.createSection("pg.lobbies.1");
        lobbies.createSection("pg.lobbies.2");
        lobbies.set("pg.lobbies.2.arenas.Own Arena.spawns.1", "own-spawn");

        LegacyConfigMigrator.ArenaImport imported = LegacyConfigMigrator.importGlobalArenas(
                legacy.getConfigurationSection("pg.arenas"), lobbies);

        assertEquals(2, imported.lobbies());
        assertEquals(2, imported.arenas());
        assertEquals("spawn-1", lobbies.getString("pg.lobbies.1.arenas.Arena One.spawns.1"));
        // the configured lobby keeps its own arena and gains the legacy one
        assertEquals("spawn-1", lobbies.getString("pg.lobbies.2.arenas.Arena One.spawns.1"));
        assertEquals("own-spawn", lobbies.getString("pg.lobbies.2.arenas.Own Arena.spawns.1"));
    }

    @Test
    void noArenasMeansNoImport() {
        YamlConfiguration lobbies = new YamlConfiguration();
        lobbies.createSection("pg.lobbies.1");

        LegacyConfigMigrator.ArenaImport imported =
                LegacyConfigMigrator.importGlobalArenas(null, lobbies);

        assertEquals(0, imported.lobbies());
        assertEquals(0, imported.arenas());
    }

    @Test
    void legacySingleLobbyModeIsCarriedOver() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("pg.lobbySystem", false);

        assertTrue(LegacyConfigMigrator.enableSingleLobby(config));
        assertTrue(config.getBoolean("pg.singleLobby"));
    }

    @Test
    void singleLobbyIsNotTouchedWhenAlreadyConfigured() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("pg.lobbySystem", false);
        config.set("pg.singleLobby", false);

        assertFalse(LegacyConfigMigrator.enableSingleLobby(config));
        assertFalse(config.getBoolean("pg.singleLobby"));
    }

    @Test
    void multiLobbyLegacyConfigStaysMultiLobby() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("pg.lobbySystem", true);

        assertTrue(LegacyConfigMigrator.enableSingleLobby(config));
        assertFalse(config.getBoolean("pg.singleLobby"));
    }

    @Test
    void singleLobbyConfigBecomesLobbyOne() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("pg.Lobby.coords", "legacy-hub-spawn");
        config.set("pg.Lobby.sign", "legacy-sign");
        YamlConfiguration lobbies = new YamlConfiguration();

        assertTrue(LegacyConfigMigrator.createSingleLobby(config, lobbies));

        assertEquals("legacy-hub-spawn", lobbies.getString("pg.lobbies.1.spawn"));
        assertEquals("legacy-sign", lobbies.getString("pg.lobbies.1.joinSign"));
    }

    @Test
    void singleLobbyIsNotCreatedNextToExistingLobbies() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("pg.Lobby.coords", "legacy-hub-spawn");
        YamlConfiguration lobbies = new YamlConfiguration();
        lobbies.createSection("pg.lobbies.3");

        assertFalse(LegacyConfigMigrator.createSingleLobby(config, lobbies));
        assertFalse(lobbies.contains("pg.lobbies.1"));
    }

    @Test
    void missingLegacyLobbyKeysDoNotCreateALobby() {
        assertFalse(LegacyConfigMigrator.createSingleLobby(new YamlConfiguration(), new YamlConfiguration()));
    }
}
