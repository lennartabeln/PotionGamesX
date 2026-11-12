package com.tw0far.potiongames.handlers;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Game;
import com.tw0far.potiongames.models.GameStates;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.util.ReloadStepRunner;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Level;

/**
 * Comprehensive reload handler that properly stops all game activities before reloading.
 *
 * This fixes the critical bug where reload command:
 * - Doesn't stop active lobbies
 * - Doesn't clear player data
 * - Doesn't close DB connections
 * - Doesn't cancel scheduled tasks
 * - Leaves orphaned resources causing memory leaks
 */
public class ReloadHandler {
    private final PotionGamesX plugin;

    public ReloadHandler(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    /**
     * Perform a complete plugin reload with proper cleanup
     *
     * @return true if reload succeeded
     */
    public boolean performReload() {
        try {
            plugin.getLogger().info("=== Starting Plugin Reload ===");

            boolean reloadSucceeded = true;

            // Step 1: Stop all active games and lobbies
            plugin.getLogger().info("1. Stopping all active games and lobbies...");
            reloadSucceeded &= runReloadStep("stop games", () -> requireSuccess(stopAllGames(), "Could not stop every lobby"));

            // Step 2: Clear all player data
            plugin.getLogger().info("2. Clearing player data...");
            reloadSucceeded &= runReloadStep("restore player data", () -> requireSuccess(clearPlayerData(), "Could not restore every setup player"));

            // Step 3: Cancel all scheduled tasks
            plugin.getLogger().info("3. Canceling scheduled tasks...");
            reloadSucceeded &= runReloadStep("cancel scheduled tasks", () -> requireSuccess(cancelScheduledTasks(), "Could not cancel all tasks"));

            // Step 4: Close database connection
            plugin.getLogger().info("4. Closing database connection...");
            reloadSucceeded &= runReloadStep("close database", () -> requireSuccess(closeDatabase(), "Could not close database"));

            // Step 5: Clear all collections to free memory
            plugin.getLogger().info("5. Clearing collections...");
            reloadSucceeded &= runReloadStep("clear collections", () -> requireSuccess(clearCollections(), "Could not clear all collections"));

            // Step 6: Reload configuration files
            plugin.getLogger().info("6. Reloading configuration files...");
            reloadSucceeded &= runReloadStep("configuration", this::reloadConfiguration);

            // Step 7: Reconnect to database
            plugin.getLogger().info("7. Reconnecting to database...");
            reloadSucceeded &= runReloadStep("database reconnect", this::reconnectDatabase);

            // Step 8: Reload game data
            plugin.getLogger().info("8. Reloading game data...");
            reloadSucceeded &= runReloadStep("game data", this::reloadGameData);

            if (!reloadSucceeded) {
                plugin.getLogger().severe("=== Plugin Reload Failed (one or more steps failed) ===");
                return false;
            }

            plugin.getLogger().info("=== Plugin Reload Complete ===");
            return true;

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Plugin reload failed! Errors:", e);
            return false;
        }
    }

    /**
     * Step 1: Stop all active games and lobbies
     */
    private boolean stopAllGames() {
        boolean success = true;
        try {
            Game game = plugin.getGame();
            if (game != null) {
                for (Lobby lobby : new ArrayList<>(game.getLobbies())) {
                    if (lobby == null) {
                        continue;
                    }

                    try {
                        lobby.clearParticipants();
                        lobby.clearVoting();
                        lobby.clearTeams();
                        lobby.clearChests();
                        lobby.clearBlockTracking();
                        lobby.setCurrentArena(null);
                        lobby.setState(GameStates.WAITING);
                        lobby.setJoinable(true);
                        lobby.setPaused(false);
                        lobby.setDeathmatch(false);
                    } catch (Exception e) {
                        success = false;
                        plugin.getLogger().log(Level.WARNING, "Error stopping lobby " + lobby.getId(), e);
                    }
                }
            }
        } catch (Exception e) {
            success = false;
            plugin.getLogger().log(Level.WARNING, "Error in stopAllGames", e);
        }
        return success;
    }

    /**
     * Step 2: Clear all player data and fully restore setup players
     */
    private boolean clearPlayerData() {
        boolean success = true;
        try {
            List<Player> allPlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
            for (Player player : allPlayers) {
                try {
                    var ssm = plugin.getSetupStateManager();
                    if (!ssm.isSetupPlayer(player)) {
                        continue;
                    }

                    ItemStack[] inventory = ssm.getPlayerInventory(player);
                    if (inventory != null) player.getInventory().setContents(inventory);
                    ItemStack[] armor = ssm.getPlayerArmor(player);
                    if (armor != null) player.getInventory().setArmorContents(armor);

                    Location savedLocation = ssm.getPlayerLocation(player);
                    if (savedLocation != null) player.teleport(savedLocation);
                    Integer level = ssm.getPlayerLevel(player);
                    if (level != null) player.setLevel(level);
                    Float exp = ssm.getPlayerExp(player);
                    if (exp != null) player.setExp(exp);
                    GameMode gameMode = ssm.getPlayerGameMode(player);
                    player.setGameMode(gameMode != null ? gameMode : GameMode.SURVIVAL);
                    Double health = ssm.getPlayerHealth(player);
                    player.setHealth(health != null ? health : 20.0);
                    Integer food = ssm.getPlayerFoodLevel(player);
                    player.setFoodLevel(food != null ? food : 20);

                    ssm.removeSavedInventory(player);
                    ssm.removeSavedArmor(player);
                    ssm.removeSavedLevel(player);
                    ssm.removeSavedExp(player);
                    ssm.removeSavedLocation(player);
                    ssm.removeSavedGameMode(player);
                    ssm.removeSavedHealth(player);
                    ssm.removeSavedFoodLevel(player);
                } catch (Exception e) {
                    success = false;
                    plugin.getLogger().log(Level.WARNING, "Error restoring player " + player.getName(), e);
                }
            }
        } catch (Exception e) {
            success = false;
            plugin.getLogger().log(Level.WARNING, "Error in clearPlayerData", e);
        }
        return success;
    }

    /**
     * Step 3: Cancel all scheduled tasks
     */
    private boolean cancelScheduledTasks() {
        try {
            plugin.getServer().getGlobalRegionScheduler().cancelTasks(plugin);
            Bukkit.getAsyncScheduler().cancelTasks(plugin);
            plugin.getLogger().info("All scheduled tasks canceled");
            return true;
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error canceling tasks", e);
            return false;
        }
    }

    /**
     * Step 4: Close database connection
     */
    private boolean closeDatabase() {
        try {
            plugin.getDatabaseManager().closeConnection();
            if (plugin.getDatabaseManager().isConnected()) {
                throw new IllegalStateException("Database manager remained connected after close");
            }
            return true;
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error closing database", e);
            return false;
        }
    }

    /**
     * Step 5: Clear all collections to free memory
     * Uses manager delegation to clear all state managers instead of direct HashMap access.
     */
    private boolean clearCollections() {
        try {
            plugin.getLobbyStateManager().clearAllLobbies();
            plugin.getArenaStateManager().clearAll();
            plugin.getItemStateManager().clearAll();
            plugin.getGame().clearAllPlayers();
            plugin.getGame().clearShopItems();
            plugin.getSetupStateManager().clearAllSetupPlayers();
            plugin.getLogger().info("All collections cleared");
            return true;
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error clearing collections", e);
            return false;
        }
    }

    private static void requireSuccess(boolean success, String message) {
        if (!success) {
            throw new IllegalStateException(message);
        }
    }

    private boolean runReloadStep(String stepName, ReloadStepRunner.ReloadOperation operation) {
        return ReloadStepRunner.run(operation, exception ->
                plugin.getLogger().log(Level.SEVERE, "Reload step failed: " + stepName, exception));
    }

    /** Reload main configuration and the derived scalar settings. */
    private void reloadConfiguration() {
        plugin.reloadConfig();
        plugin.getConfigManager().reload();
        com.tw0far.potiongames.models.Settings.loadSettings(plugin);
        plugin.getLogger().info("Configuration reloaded");
    }

    /** Reconnect to the configured database and fail if the manager remains disconnected. */
    private void reconnectDatabase() {
        plugin.getDatabaseManager().connect();
        if (!plugin.getDatabaseManager().isConnected()) {
            throw new IllegalStateException("Database manager did not establish a connection");
        }
        plugin.getLogger().info("Database reconnected");
    }

    /** Reload game data (lobbies, arenas, etc.) */
    private void reloadGameData() {
        if (plugin.getGame() != null) {
            plugin.getGame().load();
            // Restart the per-lobby tick loops (cancelled in step 3)
            for (Lobby lobby : plugin.getGame().getLobbies()) {
                lobby.startTick();
            }
        }
        // Restart the rank wall updater (cancelled in step 3)
        new com.tw0far.potiongames.bootstrap.RankWallUpdater(plugin).start();
        plugin.getLogger().info("Game data reloaded");
    }
}
