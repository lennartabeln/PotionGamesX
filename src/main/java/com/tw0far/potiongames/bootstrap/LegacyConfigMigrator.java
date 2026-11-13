package com.tw0far.potiongames.bootstrap;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Settings;
import com.tw0far.potiongames.util.LegacyArenaImporter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Migrates configuration written by the legacy release (7.0.1) on startup.
 *
 * <p>The legacy plugin kept everything in {@code arenadata.yml}: the arenas
 * globally under {@code pg.arenas.<n>} and, in multi-lobby mode, the lobbies
 * under {@code pg.lobbies.<id>} with their spawn in {@code coords} and the
 * settings directly on the lobby section. The rewrite stores the lobbies in
 * {@code lobbies.yml} with a {@code spawn} and a {@code settings} section.</p>
 *
 * <p>Everything runs once per start and only fills gaps: existing lobbies,
 * arenas and settings are never overwritten, so running it repeatedly (or after
 * an admin already did things by hand) is harmless.</p>
 */
public final class LegacyConfigMigrator {

    /** File the legacy release wrote into the plugin folder. */
    public static final String LEGACY_FILE = "arenadata.yml";

    /** Settings the legacy release stored directly under {@code pg.lobbies.<id>.<key>}. */
    private static final List<String> LOBBY_SETTINGS = List.of(
            "countdown", "roundTime", "maxPlayers", "minPlayers", "teamSize",
            "activateTeams", "activateKits", "activateShop", "activateAirdrops");

    /** What the migration did, for the log line and the tests. */
    public record Result(int lobbies, int arenas, int settings, boolean singleLobby) {
        public boolean isEmpty() {
            return lobbies == 0 && arenas == 0 && settings == 0 && !singleLobby;
        }
    }

    /** Arenas imported, and into how many lobbies. */
    public record ArenaImport(int lobbies, int arenas) {
    }

    private final PotionGamesX plugin;

    public LegacyConfigMigrator(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs the migration and saves the affected files.
     *
     * @return a summary of what was migrated (empty when there was nothing to do)
     */
    public Result migrate() {
        int lobbies = 0;
        int arenas = 0;

        File legacyFile = new File(plugin.getDataFolder(), LEGACY_FILE);
        if (legacyFile.isFile()) {
            YamlConfiguration legacy = YamlConfiguration.loadConfiguration(legacyFile);
            lobbies = createMissingLobbies(legacy.getConfigurationSection("pg.lobbies"), Settings.lobbies);
            arenas = importGlobalArenas(legacy);
        }

        // Older lobbies.yml files kept the settings directly on the lobby section.
        int settings = moveLobbySettings(Settings.lobbies);

        boolean changed = lobbies > 0 || arenas > 0 || settings > 0;
        if (changed) {
            try {
                Settings.lobbies.save(Settings.lobbiesFile);
            } catch (IOException ex) {
                plugin.getLogger().warning("Could not save migrated lobbies.yml: " + ex.getMessage());
            }
        }

        boolean singleLobby = enableSingleLobby(plugin.getConfig());
        if (singleLobby) {
            plugin.saveConfig();
        }

        Result result = new Result(lobbies, arenas, settings, singleLobby);
        if (!result.isEmpty()) {
            plugin.getLogger().info(String.format(
                    "Legacy migration: %d lobby/lobbies, %d arena(s), %d setting(s), singleLobby=%s",
                    lobbies, arenas, settings, singleLobby));
        }
        return result;
    }

    /**
     * Imports the legacy global arenas into every lobby that has no arena yet.
     *
     * <p>The legacy release kept the arenas globally, so any lobby could vote for
     * any of them. Copying them into every empty lobby keeps that behaviour; a
     * lobby that is already set up stays untouched.</p>
     */
    private int importGlobalArenas(YamlConfiguration legacy) {
        ArenaImport imported = importGlobalArenas(legacy.getConfigurationSection("pg.arenas"), Settings.lobbies);
        if (imported.arenas() == 0) {
            if (legacy.getConfigurationSection("pg.arenas") != null
                    && lowestLobbyId(Settings.lobbies) == null) {
                plugin.getLogger().warning("Found a legacy " + LEGACY_FILE
                        + " with arenas, but no lobby exists yet. Create one and run /pg importarenas <lobby>.");
            }
            return 0;
        }
        plugin.getLogger().info(String.format("Imported %d legacy arena(s) into %d lobby/lobbies.",
                imported.arenas(), imported.lobbies()));
        return imported.arenas();
    }

    /**
     * Copies all legacy global arenas into the lobbies that do not have any yet.
     *
     * @param sourceArenas the {@code pg.arenas} section of the legacy file
     * @param lobbies      the {@code lobbies.yml} configuration
     * @return arenas imported and the number of lobbies they were added to
     */
    public static ArenaImport importGlobalArenas(ConfigurationSection sourceArenas, ConfigurationSection lobbies) {
        if (sourceArenas == null || lobbies == null) {
            return new ArenaImport(0, 0);
        }
        int arenaCount = 0;
        int lobbyCount = 0;
        for (Integer lobbyId : lobbyIds(lobbies)) {
            String path = "pg.lobbies." + lobbyId;
            ConfigurationSection target = lobbies.getConfigurationSection(path);
            if (target == null) {
                continue;
            }
            ConfigurationSection existing = target.getConfigurationSection("arenas");
            if (existing != null && !existing.getKeys(false).isEmpty()) {
                continue;
            }
            LegacyArenaImporter.Result result = LegacyArenaImporter.importArenas(sourceArenas, target);
            if (result.imported().isEmpty()) {
                continue;
            }
            arenaCount += result.imported().size();
            lobbyCount++;
        }
        return new ArenaImport(lobbyCount, arenaCount);
    }

    /**
     * Creates the lobbies that only exist in the legacy file, including their
     * {@code coords} spawn and settings. Non-numeric legacy ids are skipped
     * because the rewrite addresses lobbies by number.
     */
    public static int createMissingLobbies(ConfigurationSection legacyLobbies, ConfigurationSection lobbies) {
        if (legacyLobbies == null || lobbies == null) {
            return 0;
        }
        int created = 0;
        for (String id : legacyLobbies.getKeys(false)) {
            if (!isNumeric(id)) {
                continue;
            }
            ConfigurationSection legacy = legacyLobbies.getConfigurationSection(id);
            if (legacy == null) {
                continue;
            }
            String path = "pg.lobbies." + id;
            if (lobbies.contains(path)) {
                continue;
            }
            ConfigurationSection target = lobbies.createSection(path);
            if (target == null) {
                continue;
            }
            Object spawn = legacy.get("coords");
            if (spawn != null) {
                target.set("spawn", spawn);
            }
            copySettings(legacy, target);
            created++;
        }
        return created;
    }

    /**
     * Moves settings stored directly on a lobby section into its {@code settings}
     * section (the layout the documentation describes). The legacy copy is
     * removed so there is only one source of truth; an existing
     * {@code settings.<key>} always wins.
     *
     * @return how many settings were moved or dropped
     */
    public static int moveLobbySettings(ConfigurationSection lobbies) {
        if (lobbies == null) {
            return 0;
        }
        ConfigurationSection lobbyRoot = lobbies.getConfigurationSection("pg.lobbies");
        if (lobbyRoot == null) {
            return 0;
        }
        int moved = 0;
        for (String id : lobbyRoot.getKeys(false)) {
            ConfigurationSection lobby = lobbies.getConfigurationSection("pg.lobbies." + id);
            if (lobby == null) {
                continue;
            }
            for (String key : LOBBY_SETTINGS) {
                if (!lobby.contains(key)) {
                    continue;
                }
                if (!lobby.contains("settings." + key)) {
                    lobby.set("settings." + key, lobby.get(key));
                }
                lobby.set(key, null);
                moved++;
            }
        }
        return moved;
    }

    /** All numeric lobby ids in ascending order. */
    public static List<Integer> lobbyIds(ConfigurationSection lobbies) {
        List<Integer> ids = new ArrayList<>();
        if (lobbies == null) {
            return ids;
        }
        ConfigurationSection lobbyRoot = lobbies.getConfigurationSection("pg.lobbies");
        if (lobbyRoot == null) {
            return ids;
        }
        for (String id : lobbyRoot.getKeys(false)) {
            if (isNumeric(id)) {
                ids.add(Integer.parseInt(id));
            }
        }
        Collections.sort(ids);
        return ids;
    }

    /** Lowest numeric lobby id, or {@code null} when no lobby is configured. */
    public static Integer lowestLobbyId(ConfigurationSection lobbies) {
        if (lobbies == null) {
            return null;
        }
        ConfigurationSection lobbyRoot = lobbies.getConfigurationSection("pg.lobbies");
        if (lobbyRoot == null) {
            return null;
        }
        Integer lowest = null;
        for (String id : lobbyRoot.getKeys(false)) {
            if (!isNumeric(id)) {
                continue;
            }
            int value = Integer.parseInt(id);
            if (lowest == null || value < lowest) {
                lowest = value;
            }
        }
        return lowest;
    }

    /**
     * The legacy release defaulted {@code pg.lobbySystem} to {@code false}, which
     * meant "one lobby on this server". Sets {@code pg.singleLobby} accordingly
     * when the old key is present and the new one has never been written.
     *
     * @return true when the config was changed and has to be saved
     */
    public static boolean enableSingleLobby(FileConfiguration config) {
        if (config == null || !config.contains("pg.lobbySystem") || config.contains("pg.singleLobby")) {
            return false;
        }
        if (config.getBoolean("pg.lobbySystem")) {
            return false;
        }
        config.set("pg.singleLobby", true);
        return true;
    }

    private static void copySettings(ConfigurationSection from, ConfigurationSection to) {
        for (String key : LOBBY_SETTINGS) {
            if (!from.contains(key) || to.contains("settings." + key)) {
                continue;
            }
            to.set("settings." + key, from.get(key));
        }
    }

    private static boolean isNumeric(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
