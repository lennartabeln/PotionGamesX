package com.tw0far.potiongames.models;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BroadcastMessageTest {

    @BeforeAll
    static void setUp() {
        Settings.messages = new YamlConfiguration();
        Settings.language = "en_US";
    }

    @Test
    void broadcastFollowsThePrefixAndUsesBrackets() {
        String plain = PlainTextComponentSerializer.plainText()
            .serialize(Messages.Broadcast("Server restarts in 5 minutes"));
        assertTrue(plain.startsWith("[PotionGamesX] [Broadcast] "), plain);
        assertTrue(plain.endsWith("Server restarts in 5 minutes"), plain);
    }

    @Test
    void labelIsLightPurpleAndTextAqua() {
        Component announcement = Messages.Broadcast("hello");
        TextColor labelColor = null;
        TextColor textColor = null;
        for (Component part : announcement.children()) {
            String content = PlainTextComponentSerializer.plainText().serialize(part);
            if ("Broadcast".equals(content)) {
                labelColor = part.color();
            } else if (" hello".equals(content)) {
                textColor = part.color();
            }
        }
        assertEquals(NamedTextColor.LIGHT_PURPLE, labelColor);
        assertEquals(NamedTextColor.AQUA, textColor);
    }

    @Test
    void bracketsAroundTheLabelAreDarkGrey() {
        Component announcement = Messages.Broadcast("hello");
        var parts = announcement.children();
        int label = -1;
        for (int i = 0; i < parts.size(); i++) {
            if ("Broadcast".equals(PlainTextComponentSerializer.plainText().serialize(parts.get(i)))) {
                label = i;
                break;
            }
        }
        assertTrue(label > 0, "label not found");
        assertEquals("[", PlainTextComponentSerializer.plainText().serialize(parts.get(label - 1)));
        assertEquals("]", PlainTextComponentSerializer.plainText().serialize(parts.get(label + 1)));
        assertEquals(NamedTextColor.DARK_GRAY, parts.get(label - 1).color());
        assertEquals(NamedTextColor.DARK_GRAY, parts.get(label + 1).color());
    }
}
