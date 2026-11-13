package com.tw0far.potiongames.util;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * Copies arenas from a legacy {@code arenadata.yml} into a lobby of the current
 * {@code lobbies.yml} layout.
 *
 * <p>The legacy release kept arenas globally under {@code pg.arenas.<n>} with a
 * {@code name}, a {@code world} and the sections {@code spawns.<n>} /
 * {@code deathmatch.<n>}. The rewrite stores them per lobby under
 * {@code pg.lobbies.<id>.arenas.<name>.spawns.<n>}. Spawn locations are copied
 * verbatim: both sides use Bukkit's own location serialization.</p>
 */
public final class LegacyArenaImporter {

    /** Outcome of one import run. */
    public record Result(List<String> imported, List<String> skipped, int unnamed) {
        public boolean isEmpty() {
            return imported.isEmpty() && skipped.isEmpty();
        }
    }

    private LegacyArenaImporter() {
    }

    /**
     * Imports every named arena of {@code pg.arenas} into the given lobby section.
     * Arenas whose name already exists in the lobby are skipped, never overwritten.
     *
     * @param sourceArenas the {@code pg.arenas} section of the legacy file (may be {@code null})
     * @param lobbySection the {@code pg.lobbies.<id>} section of {@code lobbies.yml}
     * @return what was imported, skipped, and how many entries had no name
     */
    public static Result importArenas(ConfigurationSection sourceArenas, ConfigurationSection lobbySection) {
        List<String> imported = new ArrayList<>();
        List<String> skipped = new ArrayList<>();
        if (sourceArenas == null || lobbySection == null) {
            return new Result(imported, skipped, 0);
        }

        int unnamed = 0;
        for (String key : sourceArenas.getKeys(false)) {
            ConfigurationSection source = sourceArenas.getConfigurationSection(key);
            if (source == null) {
                continue;
            }
            String name = source.getString("name");
            if (name == null || name.isBlank()) {
                unnamed++;
                continue;
            }
            // The arena name is a config key in the new layout, so it must not
            // introduce nested paths.
            String safeName = name.trim().replace('.', '_');

            String base = "arenas." + safeName;
            if (lobbySection.contains(base)) {
                skipped.add(safeName);
                continue;
            }
            lobbySection.createSection(base);
            copyLocations(source.getConfigurationSection("spawns"), lobbySection, base + ".spawns");
            copyLocations(source.getConfigurationSection("deathmatch"), lobbySection, base + ".deathmatch");
            imported.add(safeName);
        }
        return new Result(imported, skipped, unnamed);
    }

    private static void copyLocations(ConfigurationSection from, ConfigurationSection to, String targetPath) {
        if (from == null) {
            return;
        }
        for (String key : from.getKeys(false)) {
            Object value = from.get(key);
            if (value != null) {
                to.set(targetPath + "." + key, value);
            }
        }
    }
}
