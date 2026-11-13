package com.tw0far.potiongames.models;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Guards the shipped messages.yml against the two mistakes that silently drop
 * translations: duplicate mapping keys (SnakeYAML keeps the last one) and
 * languages that do not define the same set of keys.
 */
class MessagesYamlTest {

    private List<String> readLines() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("/messages.yml")) {
            assertNotNull(in, "messages.yml must be on the classpath");
            List<String> lines = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        }
    }

    @Test
    void messagesYmlHasNoDuplicateKeys() throws Exception {
        Deque<int[]> indents = new ArrayDeque<>();
        Deque<String> names = new ArrayDeque<>();
        Set<String> seen = new LinkedHashSet<>();
        List<String> duplicates = new ArrayList<>();

        for (String raw : readLines()) {
            String withoutComment = stripComment(raw);
            if (withoutComment.isBlank()) {
                continue;
            }
            int indent = 0;
            while (indent < withoutComment.length() && withoutComment.charAt(indent) == ' ') {
                indent++;
            }
            int colon = withoutComment.indexOf(':');
            if (colon < 0 || colon < indent) {
                continue;
            }
            String key = withoutComment.substring(indent, colon).trim();
            if (key.isEmpty() || key.startsWith("-")) {
                continue;
            }
            while (!indents.isEmpty() && indents.peek()[0] >= indent) {
                indents.pop();
                names.pop();
            }
            String path = names.isEmpty() ? key : String.join(".", names) + "." + key;
            if (!seen.add(path)) {
                duplicates.add(path);
            }
            indents.push(new int[]{indent});
            names.push(key);
        }

        assertTrue(duplicates.isEmpty(), "messages.yml contains duplicate keys: " + duplicates);
    }

    @Test
    void everyLanguageDefinesTheSameKeys() throws Exception {
        YamlConfiguration yml = new YamlConfiguration();
        try (InputStream in = getClass().getResourceAsStream("/messages.yml")) {
            assertNotNull(in, "messages.yml must be on the classpath");
            yml.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        }

        ConfigurationSection en = yml.getConfigurationSection("en_US.messages");
        ConfigurationSection de = yml.getConfigurationSection("de_DE.messages");
        assertNotNull(en, "en_US.messages must exist");
        assertNotNull(de, "de_DE.messages must exist");

        Set<String> enKeys = en.getKeys(true);
        Set<String> deKeys = de.getKeys(true);

        Set<String> missingInGerman = new LinkedHashSet<>(enKeys);
        missingInGerman.removeAll(deKeys);
        Set<String> missingInEnglish = new LinkedHashSet<>(deKeys);
        missingInEnglish.removeAll(enKeys);

        assertTrue(missingInGerman.isEmpty(), "Missing in de_DE: " + missingInGerman);
        assertTrue(missingInEnglish.isEmpty(), "Missing in en_US: " + missingInEnglish);
    }

    private String stripComment(String line) {
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                quoted = !quoted;
            } else if (c == '#' && !quoted) {
                return line.substring(0, i);
            }
        }
        return line;
    }
}
