package com.tw0far.potiongames.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.StringReader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

class MessagesMergeTest {

    private static YamlConfiguration yaml(String text) {
        return YamlConfiguration.loadConfiguration(new StringReader(text));
    }

    @Test
    void addsKeysTheUserFileDoesNotHave() {
        YamlConfiguration target = yaml("de_DE:\n  messages:\n    lobby:\n      full: \"Diese Lobby ist voll!\"\n");
        YamlConfiguration bundled = yaml("de_DE:\n  messages:\n    lobby:\n      full: \"shipped\"\n      no_spawn: \"Diese Lobby hat keinen Spawn.\"\n");

        int added = Messages.mergeLocale(target, bundled.getConfigurationSection("de_DE.messages"), "de_DE");

        assertEquals(1, added);
        assertEquals("Diese Lobby hat keinen Spawn.", target.getString("de_DE.messages.lobby.no_spawn"));
    }

    @Test
    void neverOverwritesAnEditedValue() {
        YamlConfiguration target = yaml("de_DE:\n  messages:\n    lobby:\n      full: \"Mein eigener Text\"\n");
        YamlConfiguration bundled = yaml("de_DE:\n  messages:\n    lobby:\n      full: \"shipped\"\n");

        int added = Messages.mergeLocale(target, bundled.getConfigurationSection("de_DE.messages"), "de_DE");

        assertEquals(0, added);
        assertEquals("Mein eigener Text", target.getString("de_DE.messages.lobby.full"));
    }

    @Test
    void walksNestedSections() {
        YamlConfiguration target = yaml("en_US:\n  messages:\n    top:\n      kills: \"Kills\"\n");
        YamlConfiguration bundled = yaml("en_US:\n  messages:\n    top:\n      kills: \"Kills\"\n      empty: \"No entries yet.\"\n");

        int added = Messages.mergeLocale(target, bundled.getConfigurationSection("en_US.messages"), "en_US");

        assertEquals(1, added);
        assertEquals("No entries yet.", target.getString("en_US.messages.top.empty"));
    }
}
