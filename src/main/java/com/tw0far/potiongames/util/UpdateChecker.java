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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /** One release object: tag name followed by the scalar fields up to {@code draft}. */
    private static final Pattern RELEASE = Pattern.compile(
        "\"tag_name\"\\s*:\\s*\"([^\"]+)\"[^{}]*?\"draft\"\\s*:\\s*(true|false)");

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
                String newest = null;
                Matcher matcher = RELEASE.matcher(body);
                while (matcher.find()) {
                    if ("true".equals(matcher.group(2))) {
                        continue;
                    }
                    String tag = matcher.group(1).trim();
                    if (newest == null || PluginVersion.isNewer(tag, newest)) {
                        newest = tag;
                    }
                }
                consumer.accept(newest);
            } catch (IOException | URISyntaxException e) {
                plugin.getLogger().log(Level.INFO, "Cannot look for updates: " + e.getMessage());
                consumer.accept(null);
            }
        });
    }
}
