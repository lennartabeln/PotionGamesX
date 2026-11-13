package com.tw0far.potiongames.util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Importing a legacy {@code arenadata.yml} into a lobby of the current layout. */
class LegacyArenaImporterTest {

    private YamlConfiguration legacyFile() {
        YamlConfiguration legacy = new YamlConfiguration();
        legacy.set("pg.arenas.1.name", "Arena One");
        legacy.set("pg.arenas.1.world", "world");
        legacy.set("pg.arenas.1.spawns.1", "spawn-1");
        legacy.set("pg.arenas.1.spawns.2", "spawn-2");
        legacy.set("pg.arenas.1.deathmatch.1", "deathmatch-1");
        legacy.set("pg.arenas.2.name", "Arena Two");
        legacy.set("pg.arenas.2.spawns.1", "spawn-3");
        return legacy;
    }

    private YamlConfiguration lobbies() {
        YamlConfiguration lobbies = new YamlConfiguration();
        lobbies.createSection("pg.lobbies.1");
        return lobbies;
    }

    @Test
    void copiesSpawnsAndDeathmatchSpawns() {
        YamlConfiguration legacy = legacyFile();
        YamlConfiguration lobbies = lobbies();

        LegacyArenaImporter.Result result = LegacyArenaImporter.importArenas(
                legacy.getConfigurationSection("pg.arenas"),
                lobbies.getConfigurationSection("pg.lobbies.1"));

        assertEquals(2, result.imported().size());
        assertTrue(result.skipped().isEmpty());
        assertEquals(0, result.unnamed());

        assertEquals("spawn-1", lobbies.getString("pg.lobbies.1.arenas.Arena One.spawns.1"));
        assertEquals("spawn-2", lobbies.getString("pg.lobbies.1.arenas.Arena One.spawns.2"));
        assertEquals("deathmatch-1", lobbies.getString("pg.lobbies.1.arenas.Arena One.deathmatch.1"));
        assertEquals("spawn-3", lobbies.getString("pg.lobbies.1.arenas.Arena Two.spawns.1"));
        // The legacy world field is not needed: the world comes from the spawns.
        assertNull(lobbies.getString("pg.lobbies.1.arenas.Arena One.world"));
    }

    @Test
    void neverOverwritesExistingArenas() {
        YamlConfiguration legacy = legacyFile();
        YamlConfiguration lobbies = lobbies();
        lobbies.set("pg.lobbies.1.arenas.Arena One.spawns.1", "already-configured");

        LegacyArenaImporter.Result result = LegacyArenaImporter.importArenas(
                legacy.getConfigurationSection("pg.arenas"),
                lobbies.getConfigurationSection("pg.lobbies.1"));

        assertEquals(1, result.imported().size());
        assertEquals(1, result.skipped().size());
        assertEquals("already-configured", lobbies.getString("pg.lobbies.1.arenas.Arena One.spawns.1"));
    }

    @Test
    void countsUnnamedEntriesAndSanitisesDots() {
        YamlConfiguration legacy = new YamlConfiguration();
        legacy.set("pg.arenas.1.spawns.1", "orphan-spawn");
        legacy.set("pg.arenas.2.name", "   ");
        legacy.set("pg.arenas.3.name", "Spawn.City");
        YamlConfiguration lobbies = lobbies();

        LegacyArenaImporter.Result result = LegacyArenaImporter.importArenas(
                legacy.getConfigurationSection("pg.arenas"),
                lobbies.getConfigurationSection("pg.lobbies.1"));

        assertEquals(2, result.unnamed());
        assertEquals(1, result.imported().size());
        assertEquals("Spawn_City", result.imported().get(0));
        assertTrue(lobbies.isConfigurationSection("pg.lobbies.1.arenas.Spawn_City"));
    }

    @Test
    void emptyInputProducesEmptyResult() {
        YamlConfiguration lobbies = lobbies();

        LegacyArenaImporter.Result result = LegacyArenaImporter.importArenas(
                null, lobbies.getConfigurationSection("pg.lobbies.1"));

        assertTrue(result.isEmpty());
        assertTrue(result.imported().isEmpty());
        assertTrue(result.skipped().isEmpty());
    }
}
