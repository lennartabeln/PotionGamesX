package com.tw0far.potiongames.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SettingsConfigurationReloadTest {
    @TempDir
    File directory;

    @Test
    void reloadsSecondaryYamlFilesFromTheirConfiguredPaths() throws IOException {
        Settings.lobbiesFile = new File(directory, "lobbies.yml");
        Settings.chestsFile = new File(directory, "chests.yml");
        Settings.messagesFile = new File(directory, "messages.yml");
        Settings.kitsFile = new File(directory, "kits.yml");
        Settings.shopFile = new File(directory, "shop.yml");

        Files.writeString(Settings.lobbiesFile.toPath(), "pg:\n  lobbies:\n    1:\n      minPlayers: 2\n");
        Files.writeString(Settings.chestsFile.toPath(), "pg:\n  chestloot:\n    marker: first\n");
        Files.writeString(Settings.messagesFile.toPath(), "en_US:\n  messages:\n    marker: first\n");
        Files.writeString(Settings.kitsFile.toPath(), "pg:\n  kits:\n    marker: first\n");
        Files.writeString(Settings.shopFile.toPath(), "pg:\n  potions:\n    marker: first\n");

        Settings.loadConfigurations();
        assertEquals("first", Settings.chests.getString("pg.chestloot.marker"));
        assertEquals("first", Settings.messages.getString("en_US.messages.marker"));
        assertEquals("first", Settings.kitdata.getString("pg.kits.marker"));
        assertEquals("first", Settings.shopdata.getString("pg.potions.marker"));

        Files.writeString(Settings.chestsFile.toPath(), "pg:\n  chestloot:\n    marker: second\n");
        Files.writeString(Settings.messagesFile.toPath(), "en_US:\n  messages:\n    marker: second\n");
        Files.writeString(Settings.kitsFile.toPath(), "pg:\n  kits:\n    marker: second\n");
        Files.writeString(Settings.shopFile.toPath(), "pg:\n  potions:\n    marker: second\n");

        Settings.loadConfigurations();
        assertEquals("second", Settings.chests.getString("pg.chestloot.marker"));
        assertEquals("second", Settings.messages.getString("en_US.messages.marker"));
        assertEquals("second", Settings.kitdata.getString("pg.kits.marker"));
        assertEquals("second", Settings.shopdata.getString("pg.potions.marker"));
    }
}
