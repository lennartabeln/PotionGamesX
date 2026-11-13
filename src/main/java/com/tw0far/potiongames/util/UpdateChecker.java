package com.tw0far.potiongames.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * Looks up the newest published version of the plugin.
 *
 * <p>Versions come from the GitHub releases of the project. The legacy SpigotMC
 * resource is not used any more: it still carries the old {@code 7.x} line, so
 * every 1.x install was told to "update" to an older build.</p>
 */
public record UpdateChecker(JavaPlugin plugin, String repository) {

    /** Repository that hosts the releases. */
    public static final String DEFAULT_REPOSITORY = "lennartabeln/PotionGamesX";

    private static final String TAG_KEY = "\"tag_name\"";
    private static final String DRAFT_KEY = "\"draft\"";

    public UpdateChecker(JavaPlugin plugin) {
        this(plugin, DEFAULT_REPOSITORY);
    }

    /**
     * Fetches the newest version asynchronously. The consumer is always invoked:
     * with the newest version on success, or with null when the lookup fails.
     */
    public void getVersion(final Consumer<String> consumer) {
        String url = "https://api.github.com/repos/" + repository + "/releases?per_page=50";
        Bukkit.getAsyncScheduler().runNow(plugin, scheduledTask -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URI(url).toURL().openConnection();
                connection.setRequestProperty("Accept", "application/vnd.github+json");
                connection.setRequestProperty("User-Agent", "PotionGamesX-UpdateChecker");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    consumer.accept(null);
                    return;
                }
                String body;
                try (InputStream inputStream = connection.getInputStream();
                     Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
                    body = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                }
                consumer.accept(newestTagIn(body));
            } catch (IOException | URISyntaxException e) {
                plugin.getLogger().log(Level.INFO, "Cannot look for updates: " + e.getMessage());
                consumer.accept(null);
            }
        });
    }

    /**
     * Newest non-draft tag in a GitHub releases response.
     *
     * <p>Plain index scan instead of a regular expression: the response is
     * attacker-influenced data and a backtracking pattern over it is a
     * denial-of-service risk (CodeQL {@code java/polynomial-redos}).</p>
     */
    static String newestTagIn(String body) {
        String newest = null;
        int cursor = 0;
        while (cursor < body.length()) {
            int tagKey = body.indexOf(TAG_KEY, cursor);
            if (tagKey < 0) {
                break;
            }
            int nextTagKey = body.indexOf(TAG_KEY, tagKey + TAG_KEY.length());
            int limit = nextTagKey < 0 ? body.length() : nextTagKey;

            String tag = quotedValueAfter(body, tagKey + TAG_KEY.length(), limit);
            if (tag != null && !isDraft(body, tagKey, limit)
                && (newest == null || PluginVersion.isNewer(tag, newest))) {
                newest = tag;
            }
            cursor = limit;
        }
        return newest;
    }

    /** Reads the string literal that follows a JSON key's colon. */
    private static String quotedValueAfter(String body, int from, int limit) {
        int colon = body.indexOf(':', from);
        if (colon < 0 || colon >= limit) {
            return null;
        }
        int open = body.indexOf('"', colon + 1);
        if (open < 0 || open >= limit) {
            return null;
        }
        int close = body.indexOf('"', open + 1);
        if (close < 0 || close > limit) {
            return null;
        }
        return body.substring(open + 1, close).trim();
    }

    /** Draft flag of the release object that starts at {@code from}. */
    private static boolean isDraft(String body, int from, int limit) {
        int key = body.indexOf(DRAFT_KEY, from);
        if (key < 0 || key >= limit) {
            return false;
        }
        int colon = body.indexOf(':', key);
        if (colon < 0 || colon >= limit) {
            return false;
        }
        for (int i = colon + 1; i < limit; i++) {
            char c = body.charAt(i);
            if (c == 't') {
                return true;
            }
            if (c == 'f') {
                return false;
            }
        }
        return false;
    }
}
