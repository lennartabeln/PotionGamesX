package com.tw0far.potiongames.config;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Per-lobby configuration settings.
 * Each lobby can override global settings for specific behavior customization.
 * 
 * DESIGN:
 * - Inherits defaults from global config
 * - Can override any setting per-lobby
 * - Provides immutable access to settings
 * - Thread-safe (created once, not modified)
 * 
 * BENEFITS:
 * - Different lobbies can have different rules
 * - Easier than nested HashMaps
 * - Type-safe access
 * - Centralized documentation
 */
public class LobbySettings {
    private final int lobbyId;
    private final ConfigurationSection section;
    
    // Game Settings
    private final int maxPlayers;
    private final int minPlayers;
    private final int teamSize;
    private final int roundTime;
    
    // Features
    private final boolean teamsEnabled;
    private final boolean kitsEnabled;
    private final boolean shopEnabled;
    private final boolean airdropsEnabled;
    private final boolean deathmatchEnabled;
    
    // Behavior
    private final boolean friendlyFire;
    private final boolean startOnJoin;
    private final boolean compassOnSpawn;
    
    public LobbySettings(int lobbyId, ConfigurationSection section) {
        this.lobbyId = lobbyId;
        this.section = section;
        
        // Load settings with fallback to defaults
        this.maxPlayers = section.getInt("maxPlayers", 24);
        this.minPlayers = section.getInt("minPlayers", 2);
        this.teamSize = section.getInt("teamSize", 2);
        this.roundTime = section.getInt("roundTime", 30);
        
        this.teamsEnabled = section.getBoolean("activateTeams", true);
        this.kitsEnabled = section.getBoolean("activateKits", true);
        this.shopEnabled = section.getBoolean("activateShop", true);
        this.airdropsEnabled = section.getBoolean("activateAirdrops", true);
        this.deathmatchEnabled = section.getBoolean("activateDeathmatch", true);
        
        this.friendlyFire = section.getBoolean("friendlyFire", false);
        this.startOnJoin = section.getBoolean("startOnJoin", false);
        this.compassOnSpawn = section.getBoolean("compassOnSpawn", false);
    }
    
    // ===== GETTERS =====
    public int getLobbyId() {
        return lobbyId;
    }
    
    public int getMaxPlayers() {
        return maxPlayers;
    }
    
    public int getMinPlayers() {
        return minPlayers;
    }
    
    public int getTeamSize() {
        return teamSize;
    }
    
    public int getRoundTime() {
        return roundTime;
    }
    
    public boolean isTeamsEnabled() {
        return teamsEnabled;
    }
    
    public boolean isKitsEnabled() {
        return kitsEnabled;
    }
    
    public boolean isShopEnabled() {
        return shopEnabled;
    }
    
    public boolean isAirdropsEnabled() {
        return airdropsEnabled;
    }
    
    public boolean isDeathmatchEnabled() {
        return deathmatchEnabled;
    }
    
    public boolean isFriendlyFire() {
        return friendlyFire;
    }
    
    public boolean isStartOnJoin() {
        return startOnJoin;
    }
    
    public boolean isCompassOnSpawn() {
        return compassOnSpawn;
    }
    
    /**
     * Get the raw configuration section for advanced access
     */
    public ConfigurationSection getSection() {
        return section;
    }
    
    /**
     * Get a custom integer setting
     */
    public int getInt(String key, int defaultValue) {
        return section.getInt(key, defaultValue);
    }
    
    /**
     * Get a custom string setting
     */
    public String getString(String key, String defaultValue) {
        return section.getString(key, defaultValue);
    }
    
    /**
     * Get a custom boolean setting
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return section.getBoolean(key, defaultValue);
    }
    
    /**
     * Validate lobby settings (check constraints)
     * 
     * @return true if settings are valid
     */
    public boolean isValid() {
        if (minPlayers < 1 || minPlayers > maxPlayers) {
            return false;
        }
        if (maxPlayers < minPlayers || maxPlayers > 128) {
            return false;
        }
        if (teamSize < 1 || teamSize > maxPlayers / 2) {
            return false;
        }
        if (roundTime < 1 || roundTime > 3600) {
            return false;
        }
        return true;
    }
    
    /**
     * Get validation error message if invalid
     */
    public String getValidationError() {
        if (minPlayers < 1 || minPlayers > maxPlayers) {
            return "minPlayers must be 1-" + maxPlayers;
        }
        if (maxPlayers < minPlayers || maxPlayers > 128) {
            return "maxPlayers must be " + minPlayers + "-128";
        }
        if (teamSize < 1 || teamSize > maxPlayers / 2) {
            return "teamSize must be 1-" + (maxPlayers / 2);
        }
        if (roundTime < 1 || roundTime > 3600) {
            return "roundTime must be 1-3600 seconds";
        }
        return null;
    }
    
    @Override
    public String toString() {
        return String.format(
            "LobbySettings{lobby=%d, maxPlayers=%d, minPlayers=%d, teamSize=%d, roundTime=%ds}",
            lobbyId, maxPlayers, minPlayers, teamSize, roundTime
        );
    }
}
