package com.tw0far.potiongames.models;

import com.tw0far.potiongames.commands.CommandDispatcher;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** No user facing line may leave the plugin without the chat prefix. */
class MessagePrefixTest {

    private static final String PREFIX = "[PotionGamesX] ";

    @BeforeAll
    static void setUp() {
        Settings.messages = new org.bukkit.configuration.file.YamlConfiguration();
        Settings.language = "en_US";
    }

    private static String plain(net.kyori.adventure.text.Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    @Test
    void lineIsPrefixed() {
        assertTrue(plain(Messages.line("No lobbies available.", NamedTextColor.GRAY)).startsWith(PREFIX));
    }

    @Test
    void dispatcherErrorsArePrefixed() {
        assertTrue(plain(CommandDispatcher.createError("Unknown command!")).startsWith(PREFIX));
        assertTrue(plain(CommandDispatcher.createInfo("Starting plugin reload...")).startsWith(PREFIX));
        assertTrue(plain(CommandDispatcher.createSuccess("Plugin successfully reloaded!")).startsWith(PREFIX));
    }

    @Test
    void commandHelpIsPrefixed() {
        assertTrue(plain(Messages.CommandHelp("list")).startsWith(PREFIX));
    }
}
