package com.tw0far.potiongames.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

/**
 * Guards the shipped {@code messages.yml}: every locale must define the same
 * keys with compatible placeholders, so a translation can never silently fall
 * back to the English default.
 */
class MessagesResourceTest {

    private static final Pattern PLACEHOLDER = Pattern.compile("%[sd]");

    private Map<String, Map<String, String>> load() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("/messages.yml")) {
            assertNotNull(in, "messages.yml is missing from the plugin resources");
            Map<String, Object> root = new Yaml().load(in);
            Map<String, Map<String, String>> locales = new LinkedHashMap<>();
            for (Map.Entry<String, Object> locale : root.entrySet()) {
                Object messages = locale.getValue() instanceof Map<?, ?> map ? map.get("messages") : null;
                if (messages instanceof Map<?, ?> section) {
                    Map<String, String> flat = new LinkedHashMap<>();
                    flatten(section, "", flat);
                    locales.put(locale.getKey(), flat);
                }
            }
            return locales;
        }
    }

    private void flatten(Map<?, ?> node, String prefix, Map<String, String> out) {
        for (Map.Entry<?, ?> entry : node.entrySet()) {
            String key = prefix + entry.getKey();
            if (entry.getValue() instanceof Map<?, ?> child) {
                flatten(child, key + ".", out);
            } else {
                out.put(key, String.valueOf(entry.getValue()));
            }
        }
    }

    @Test
    void everyLocaleDefinesTheSameKeys() throws Exception {
        Map<String, Map<String, String>> locales = load();
        assertTrue(locales.containsKey("en_US"), "en_US is the fallback locale and must exist");
        Map<String, String> reference = locales.get("en_US");
        assertFalse(reference.isEmpty(), "en_US.messages is empty");

        for (Map.Entry<String, Map<String, String>> locale : locales.entrySet()) {
            if (locale.getKey().equals("en_US")) {
                continue;
            }
            assertEquals(new TreeSet<>(reference.keySet()), new TreeSet<>(locale.getValue().keySet()),
                "locale " + locale.getKey() + " does not define the same keys as en_US");
        }
    }

    @Test
    void placeholdersMatchBetweenLocales() throws Exception {
        Map<String, Map<String, String>> locales = load();
        Map<String, String> reference = locales.get("en_US");

        for (Map.Entry<String, Map<String, String>> locale : locales.entrySet()) {
            if (locale.getKey().equals("en_US")) {
                continue;
            }
            for (Map.Entry<String, String> entry : reference.entrySet()) {
                String translated = locale.getValue().get(entry.getKey());
                if (translated == null) {
                    continue; // reported by the key parity test
                }
                assertEquals(placeholders(entry.getValue()), placeholders(translated),
                    "placeholder mismatch in " + locale.getKey() + "." + entry.getKey());
            }
        }
    }

    private List<String> placeholders(String text) {
        List<String> found = new ArrayList<>();
        Matcher matcher = PLACEHOLDER.matcher(text);
        while (matcher.find()) {
            found.add(matcher.group());
        }
        found.sort(null);
        return found;
    }
}
