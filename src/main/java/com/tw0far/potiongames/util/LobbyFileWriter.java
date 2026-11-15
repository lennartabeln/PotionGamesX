package com.tw0far.potiongames.util;

import com.tw0far.potiongames.models.Settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Writes {@code lobbies.yml} with its documentation header.
 *
 * <p>Bukkit's {@code YamlConfiguration#save} writes plain SnakeYAML and drops every comment, so a
 * file the plugin touched lost its documentation and looked like an empty default. This writer
 * prepends the comment block of the shipped {@code lobbies.yml} (the single source for the text)
 * to the serialised content, and re-adds it on start when an existing file has none.</p>
 */
public final class LobbyFileWriter {

    private static final Logger LOGGER = Logger.getLogger("PotionGamesX");
    private static final String RESOURCE = "/lobbies.yml";
    private static List<String> header;

    private LobbyFileWriter() {
    }

    /** Saves the lobby configuration with the documentation header on top. */
    public static void save() {
        if (Settings.lobbies == null || Settings.lobbiesFile == null) {
            return;
        }
        write(Settings.lobbies.saveToString());
    }

    /** Adds the header to an existing file that does not have one yet. */
    public static void ensureHeader() {
        if (Settings.lobbies == null || Settings.lobbiesFile == null) {
            return;
        }
        Path path = Settings.lobbiesFile.toPath();
        if (Files.exists(path)) {
            try {
                String current = Files.readString(path, StandardCharsets.UTF_8);
                List<String> lines = header();
                if (!lines.isEmpty() && current.startsWith(lines.get(0))) {
                    return;
                }
                write(current.isBlank() ? Settings.lobbies.saveToString() : current);
                return;
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "[PotionGamesX] Could not read lobbies.yml", e);
                return;
            }
        }
        write(Settings.lobbies.saveToString());
    }

    private static void write(String content) {
        try {
            Path path = Settings.lobbiesFile.toPath();
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Files.writeString(path, String.join("\n", header()) + "\n" + content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "[PotionGamesX] Could not save lobbies.yml", e);
        }
    }

    /**
     * The leading comment block of the shipped {@code lobbies.yml} - the same file the admin
     * reads, so the documentation cannot drift from what is written back.
     */
    static List<String> header() {
        if (header != null) {
            return header;
        }
        List<String> lines = new ArrayList<>();
        try (InputStream in = LobbyFileWriter.class.getResourceAsStream(RESOURCE)) {
            if (in == null) {
                header = lines;
                return header;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith("#")) {
                        break;
                    }
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "[PotionGamesX] Could not read the lobbies.yml header", e);
        }
        while (!lines.isEmpty() && lines.get(lines.size() - 1).isBlank()) {
            lines.remove(lines.size() - 1);
        }
        header = lines;
        return header;
    }
}
