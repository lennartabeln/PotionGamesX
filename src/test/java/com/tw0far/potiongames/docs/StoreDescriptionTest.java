package com.tw0far.potiongames.docs;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The SpigotMC resource description is maintained by hand (Spigot only speaks BBCode, Markdown
 * would show up as raw text). This test keeps it from drifting: it must name every command that
 * has a help line, use balanced BBCode tags and contain no Markdown leftovers.
 */
class StoreDescriptionTest {

    private static final Path DESCRIPTION = Path.of("docs", "store", "spigot-description.txt");
    private static final Pattern TAG = Pattern.compile("\\[(/?)([A-Z]+)(?:=[^\\]]*)?\\]");

    private static String description;
    private static Map<String, Object> helpKeys;

    @BeforeAll
    static void load() throws Exception {
        assertTrue(Files.exists(DESCRIPTION), "store description missing: " + DESCRIPTION.toAbsolutePath());
        description = Files.readString(DESCRIPTION, StandardCharsets.UTF_8);

        YamlConfiguration messages = new YamlConfiguration();
        try (InputStream in = StoreDescriptionTest.class.getResourceAsStream("/messages.yml")) {
            assertTrue(in != null, "messages.yml not on the classpath");
            messages.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        }
        helpKeys = messages.getConfigurationSection("en_US.messages.help").getValues(false);
    }

    @Test
    void mentionsEveryCommandThatHasAHelpLine() {
        List<String> missing = new ArrayList<>();
        for (String key : helpKeys.keySet()) {
            if (!description.contains(key)) {
                missing.add(key);
            }
        }
        assertTrue(missing.isEmpty(), "commands missing from the store description: " + missing);
    }

    /** Tags XenForo 1.5 (Spigot's forum software) actually understands. */
    private static final java.util.Set<String> SUPPORTED_TAGS = java.util.Set.of(
        "B", "I", "U", "S", "COLOR", "SIZE", "FONT", "CENTER", "LEFT", "RIGHT",
        "INDENT", "LIST", "QUOTE", "CODE", "SPOILER", "URL", "IMG", "MEDIA", "*");

    @Test
    void usesNoUnknownTags() {
        Matcher matcher = TAG.matcher(description);
        List<String> unknown = new ArrayList<>();
        while (matcher.find()) {
            String tag = matcher.group(2);
            if (!SUPPORTED_TAGS.contains(tag)) {
                unknown.add(tag);
            }
        }
        assertTrue(unknown.isEmpty(), "tags Spigot does not know: " + unknown);
    }

    @Test
    void hasBalancedBbCodeTags() {
        Map<String, Integer> balance = new java.util.HashMap<>();
        Matcher matcher = TAG.matcher(description);
        while (matcher.find()) {
            String tag = matcher.group(2);
            if (tag.equals("HR") || tag.equals("*")) {
                continue;
            }
            int delta = matcher.group(1).isEmpty() ? 1 : -1;
            balance.merge(tag, delta, Integer::sum);
        }
        balance.forEach((tag, delta) -> assertTrue(delta == 0, "unbalanced [" + tag + "]: " + delta));
    }

    @Test
    void usesOnlyTagsThatXenForoSupports() {
        // Spigot runs XenForo 1.5, which has no [HR] tag - it comes from vBulletin/MyBB and shows
        // up as literal text in the preview.
        assertFalse(description.contains("[HR]"), "[HR] is not supported by Spigot's BBCode");
    }

    @Test
    void containsNoMarkdown() {
        assertFalse(Pattern.compile("(?m)^#{1,6} ").matcher(description).find(), "Markdown heading found");
        assertFalse(description.contains("**"), "Markdown bold found");
        assertFalse(description.contains("`"), "Markdown code found");
        assertFalse(description.contains("](http"), "Markdown link found");
    }

    @Test
    void usesThePrefixColoursForTheTitle() {
        // [ = dark gray, Potion = dark purple, Games = gold, X = dark aqua, ] = dark gray
        assertTrue(description.contains("[COLOR=#AA00AA]Potion[/COLOR]"), "title: Potion is not dark purple");
        assertTrue(description.contains("[COLOR=#FFAA00]Games[/COLOR]"), "title: Games is not gold");
        assertTrue(description.contains("[COLOR=#00AAAA]X[/COLOR]"), "title: X is not dark aqua");
    }
}
