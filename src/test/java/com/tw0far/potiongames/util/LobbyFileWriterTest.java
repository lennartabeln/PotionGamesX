package com.tw0far.potiongames.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The comment block of the shipped lobbies.yml is the documentation that is written back on every
 * save, so it has to stay complete: it must explain the per-lobby settings with an example.
 */
class LobbyFileWriterTest {

    @Test
    void headerIsTheDocumentationOfTheShippedFile() {
        List<String> header = LobbyFileWriter.header();
        assertFalse(header.isEmpty(), "no header found in lobbies.yml");
        assertTrue(header.stream().allMatch(line -> line.startsWith("#")), "header contains non-comment lines");
    }

    @Test
    void headerExplainsPerLobbySettingsWithAnExample() {
        String header = String.join("\n", LobbyFileWriter.header());
        assertTrue(header.contains("settings:"), "no settings example");
        assertTrue(header.contains("countdown:"), "settings example without countdown");
        assertTrue(header.contains("roundTime:"), "settings example without roundTime");
        assertTrue(header.contains("pg.defaults"), "header does not explain the fallback order");
    }
}
