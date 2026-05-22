package com.tw0far.potiongames.examples;

import com.tw0far.potiongames.config.*;
import com.tw0far.potiongames.database.DatabaseQueryBuilder;
import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Complete example showing how to use the optimized configuration and database systems together.
 * 
 * This demonstrates:
 * 1. Loading configuration with translation keys
 * 2. Per-lobby customization
 * 3. Building chest loot with the DSL
 * 4. Building shop items with the DSL
 * 5. Querying database with prepared statements
 */
public class ConfigurationIntegrationExample {
    
    private final PotionGames plugin;
    private final YamlConfigLoader configLoader;
    private final Connection dbConnection;
    
    public ConfigurationIntegrationExample(PotionGames plugin) {
        this.plugin = plugin;
        this.configLoader = new YamlConfigLoader(plugin);
        this.dbConnection = null; // Would get from DatabaseManager in real code
    }
    
    /**
     * Example 1: Initialize plugin configuration on startup
     */
    public void initializeConfiguration() {
        // Load configuration from disk
        configLoader.load();
        
        // Access global settings using translation keys
        int globalCountdown = configLoader.getInt(ConfigKeys.COUNTDOWN);
        int globalMaxPlayers = configLoader.getInt(ConfigKeys.MAX_PLAYERS);
        boolean teamsEnabled = configLoader.getBoolean(ConfigKeys.ACTIVATE_TEAMS);
        
        plugin.getLogger().info("Global settings loaded:");
        plugin.getLogger().info("  Countdown: " + globalCountdown + "s");
        plugin.getLogger().info("  Max Players: " + globalMaxPlayers);
        plugin.getLogger().info("  Teams: " + (teamsEnabled ? "Enabled" : "Disabled"));
        
        // Load per-lobby settings
        int[] lobbyIds = configLoader.getAllLobbyIds();
        for (int lobbyId : lobbyIds) {
            LobbySettings settings = configLoader.getLobbySettings(lobbyId);
            
            if (!settings.isValid()) {
                plugin.getLogger().warning("Lobby " + lobbyId + " has invalid settings!");
                plugin.getLogger().warning("Error: " + settings.getValidationError());
                continue;
            }
            
            plugin.getLogger().info("Lobby " + lobbyId + " loaded: " + settings);
        }
    }
    
    /**
     * Example 2: Create arena-specific loot based on lobby configuration
     */
    public ItemStack[] createArenaLoot(int lobbyId) {
        // Get lobby-specific settings
        LobbySettings lobbySettings = configLoader.getLobbySettings(lobbyId);
        
        // Create loot builder based on lobby configuration
        ChestLootBuilder builder = new ChestLootBuilder();
        
        // Always add basic weapons
        builder.addWeapon("IRON_SWORD", 30)
               .addWeapon("WOODEN_PICKAXE", 20);
        
        // Add armor conditionally based on settings
        builder.addArmor("IRON_CHESTPLATE", 15)
               .addArmor("IRON_LEGS", 10);
        
        // Add food
        builder.addFood("COOKED_BEEF", 50)
               .addFood("APPLE", 30);
        
        // Add potions only if airdrops are enabled
        if (lobbySettings.isAirdropsEnabled()) {
            builder.addPotion("SPEED", 1, 300, 20)
                   .addPotion("STRENGTH", 0, 300, 15)
                   .addSplashPotion("HEALING", 0, 300, 10);
        }
        
        // Add utilities
        builder.addItem("TORCH", 8, 40)
               .addItem("LADDER", 4, 30);
        
        plugin.getLogger().info("Created loot for lobby " + lobbyId + " (" + builder.size() + " item types)");
        
        return builder.build();
    }
    
    /**
     * Example 3: Create lobby-specific shop
     */
    public List<ShopBuilder.ShopItemEntry> createLobbyShop(int lobbyId) {
        LobbySettings lobbySettings = configLoader.getLobbySettings(lobbyId);
        
        ShopBuilder builder = new ShopBuilder();
        
        // Basic shop items
        builder.addWeapon("IRON_SWORD", 50, "Basic sword")
               .addArmor("DIAMOND_CHESTPLATE", 200, "Strong protection")
               .addFood("COOKED_BEEF", 5, 100, "Restores hunger");
        
        // Add premium items only for lobbies with high max players (team-based)
        if (lobbySettings.getMaxPlayers() >= 16 && lobbySettings.getTeamSize() >= 2) {
            builder.addWeapon("DIAMOND_SWORD", 150, "Premium sword")
                   .addArmor("DIAMOND_LEGS", 150, "Premium leg armor");
        }
        
        // Add utility items based on features
        if (lobbySettings.isKitsEnabled()) {
            builder.addUtility("COMPASS", 1, 10, "Find your way");
        }
        
        if (lobbySettings.isTeamsEnabled()) {
            builder.addUtility("WOOL", 16, 5, "Team wool");
        }
        
        List<ShopBuilder.ShopItemEntry> shopItems = builder.buildEntries();
        
        plugin.getLogger().info("Created shop for lobby " + lobbyId + " (" + shopItems.size() + " items)");
        
        return shopItems;
    }
    
    /**
     * Example 4: Query player statistics with optimized database access
     */
    public void displayPlayerStats(Player player, int lobbyId) throws SQLException {
        if (dbConnection == null || dbConnection.isClosed()) {
            plugin.getLogger().warning("Database connection not available");
            return;
        }
        
        DatabaseQueryBuilder db = new DatabaseQueryBuilder(dbConnection);
        
        // Query player stats with prepared statements (safe from SQL injection)
        try {
            List<Map<String, Object>> results = db
                .select("name", "kills", "deaths", "wins", "games_played")
                .from("player_stats")
                .where("name = ?", player.getName())
                .where("lobby_id = ?", lobbyId)
                .execute();
            
            if (results.isEmpty()) {
                player.sendMessage("No stats found for lobby " + lobbyId);
                return;
            }
            
            Map<String, Object> stats = results.get(0);
            int kills = (Integer) stats.get("kills");
            int deaths = (Integer) stats.get("deaths");
            int wins = (Integer) stats.get("wins");
            int gamesPlayed = (Integer) stats.get("games_played");
            
            player.sendMessage("=== Stats for Lobby " + lobbyId + " ===");
            player.sendMessage("Kills: " + kills);
            player.sendMessage("Deaths: " + deaths);
            player.sendMessage("K/D Ratio: " + String.format("%.2f", (double) kills / Math.max(1, deaths)));
            player.sendMessage("Wins: " + wins);
            player.sendMessage("Games: " + gamesPlayed);
            
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to query player stats: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Example 5: Update player stats with prepared statements
     */
    public void recordPlayerKill(Player killer, Player victim, int lobbyId) throws SQLException {
        if (dbConnection == null || dbConnection.isClosed()) {
            plugin.getLogger().warning("Database connection not available");
            return;
        }
        
        DatabaseQueryBuilder db = new DatabaseQueryBuilder(dbConnection);
        
        try {
            // Update killer's kill count
            int updated = db.update("player_stats")
                .set("kills", "kills + 1")  // Note: In real code, you'd query current value first
                .where("name = ?", killer.getName())
                .where("lobby_id = ?", lobbyId)
                .execute();
            
            if (updated == 0) {
                // Player not found, insert new record
                db.insert("player_stats")
                    .value("name", killer.getName())
                    .value("lobby_id", lobbyId)
                    .value("kills", 1)
                    .value("deaths", 0)
                    .execute();
            }
            
            // Update victim's death count
            db.update("player_stats")
                .set("deaths", "deaths + 1")
                .where("name = ?", victim.getName())
                .where("lobby_id = ?", lobbyId)
                .execute();
            
            plugin.getLogger().info(killer.getName() + " killed " + victim.getName() + " in lobby " + lobbyId);
            
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to record kill: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Example 6: Get top players from a lobby
     */
    public void displayTopPlayers(int lobbyId, int limit) throws SQLException {
        if (dbConnection == null || dbConnection.isClosed()) {
            plugin.getLogger().warning("Database connection not available");
            return;
        }
        
        DatabaseQueryBuilder db = new DatabaseQueryBuilder(dbConnection);
        
        try {
            List<Map<String, Object>> topPlayers = db
                .select("name", "kills", "deaths", "wins")
                .from("player_stats")
                .where("lobby_id = ?", lobbyId)
                .orderBy("kills", false)  // DESC order
                .limit(limit)
                .execute();
            
            plugin.getLogger().info("=== Top Players in Lobby " + lobbyId + " ===");
            int rank = 1;
            for (Map<String, Object> player : topPlayers) {
                String name = (String) player.get("name");
                int kills = (Integer) player.get("kills");
                int deaths = (Integer) player.get("deaths");
                int wins = (Integer) player.get("wins");
                
                plugin.getLogger().info(
                    rank + ". " + name + " - " + kills + " kills, " + 
                    deaths + " deaths, " + wins + " wins"
                );
                rank++;
            }
            
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to query top players: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Example 7: Configuration update and reload
     */
    public void updateConfigurationValue(String key, Object value) {
        // Update config
        configLoader.set(key, value);
        plugin.getLogger().info("Configuration updated: " + key + " = " + value);
        
        // Optionally reload if this is a critical setting
        if (key.contains("maxPlayers") || key.contains("teamSize")) {
            configLoader.reload();
            plugin.getLogger().info("Configuration reloaded due to critical change");
        }
    }
    
    /**
     * Example 8: Performance metrics
     */
    public void printPerformanceMetrics() {
        plugin.getLogger().info(configLoader.getCacheStats());
    }
}
