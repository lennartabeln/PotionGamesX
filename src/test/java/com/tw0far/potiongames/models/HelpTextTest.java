package com.tw0far.potiongames.models;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Every command shows its /pg help line when it is called with incomplete
 * arguments, so the help section is the single source of truth and has to stay
 * complete and consistent across both locales.
 */
class HelpTextTest {

    private static YamlConfiguration messages;

    @BeforeAll
    static void load() throws Exception {
        messages = new YamlConfiguration();
        try (InputStream in = HelpTextTest.class.getResourceAsStream("/messages.yml")) {
            messages.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        }
    }

    private static Map<String, Object> help(String locale) {
        return messages.getConfigurationSection(locale + ".messages.help").getValues(false);
    }

    @Test
    void bothLocalesKnowTheSameCommands() {
        Set<String> en = new TreeSet<>(help("en_US").keySet());
        Set<String> de = new TreeSet<>(help("de_DE").keySet());
        assertEquals(en, de);
    }

    @Test
    void everyLineIsACommandWithADescription() {
        for (Map.Entry<String, Object> entry : help("en_US").entrySet()) {
            String line = String.valueOf(entry.getValue());
            assertTrue(line.startsWith("/pg " + entry.getKey()), entry.getKey() + ": " + line);
            assertTrue(line.contains(" - "), entry.getKey() + " has no description: " + line);
        }
    }

    @Test
    void incompleteCommandsPrintTheHelpLineWithPrefix() {
        Settings.messages = messages;
        Settings.language = "en_US";
        String expected = "[PotionGamesX] " + help("en_US").get("addlobby");
        assertEquals(expected,
            PlainTextComponentSerializer.plainText().serialize(Messages.CommandHelp("addlobby")));
    }
}
