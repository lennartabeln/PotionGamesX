package com.tw0far.potiongames.models;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The three selectors print their confirmation in the legacy layout: a dashed line with the
 * selector name above and below, the green label and the light purple value in between. The
 * inventory titles carry the chat prefix as well.
 */
class SelectorFeedbackTest {

    @BeforeAll
    static void loadMessages() throws Exception {
        YamlConfiguration messages = new YamlConfiguration();
        try (InputStream in = SelectorFeedbackTest.class.getResourceAsStream("/messages.yml")) {
            messages.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        }
        Settings.messages = messages;
        Settings.language = "en_US";
    }

    private static String plain(net.kyori.adventure.text.Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    @Test
    void sectionHeaderIsThePlainNameWithoutDashes() {
        // The dashed legacy line went past the chat width, so the name alone is printed.
        assertEquals("[PotionGamesX] Team-Selector", plain(Messages.TeamSelectorSection()));
        assertEquals("[PotionGamesX] Kit-Selector", plain(Messages.KitSelectorSection()));
        assertEquals("[PotionGamesX] Arena-Selector", plain(Messages.ArenaSelector()));
    }

    @Test
    void kitConfirmationUsesTheLegacyWording() {
        assertEquals("[PotionGamesX] You now have the kit: Tank", plain(Messages.KitNowHave("Tank")));
    }

    @Test
    void teamConfirmationUsesTheLegacyWording() {
        assertEquals("[PotionGamesX] You are now in team: 2", plain(Messages.TeamNowIn("2")));
        assertEquals("[PotionGamesX] Players: 1/2", plain(Messages.TeamPlayers(1, 2)));
    }

    @Test
    void voteFeedbackUsesTheLegacyWording() {
        assertEquals("[PotionGamesX] You have voted for: arena1", plain(Messages.VotedFor("arena1")));
        assertEquals("[PotionGamesX] Votes: 3", plain(Messages.VoteCount(3)));
    }

    @Test
    void inventoryTitlesCarryThePrefix() {
        assertEquals("[PotionGamesX] Kit-Selector", plain(Messages.KitSelector()));
        assertEquals("[PotionGamesX] Team-Selector", plain(Messages.SelectorTeamTitle()));
        assertEquals("[PotionGamesX] Arena-Selector", plain(Messages.ArenaSelectorTitle()));
    }
}
