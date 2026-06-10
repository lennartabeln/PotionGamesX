package com.tw0far.potiongames.models;

import org.bukkit.configuration.file.FileConfiguration;

import com.tw0far.potiongames.PotionGamesX;

/**
 * Manages configuration for a single lobby with inheritance from global defaults.
 * 
 * Configuration hierarchy:
 * 1. Per-lobby overrides (if set in config)
 * 2. Global defaults (fallback values)
 * 
 * This allows flexible configuration where:
 * - Global defaults apply to all lobbies
 * - Each lobby can override specific settings
 */
public class LobbyConfig {
    private final int lobbyId;
    private final FileConfiguration lobbies;
    
    // Game settings with global defaults
    private int countdown;
    private int maxPlayers;
    private int minPlayers;
    private int teamSize;
    private int roundTime;
    
    // Feature flags with global defaults
    private boolean activateTeams;
    private boolean activateKits;
    private boolean activateShop;
    private boolean activateAirdrops;
    
    /**
     * Create a new LobbyConfig with global defaults.
     * 
     * @param lobbyId The lobby ID
     * @param lobbies The lobbies.yml configuration
     * @param globalCountdown Global default countdown
     * @param globalMaxPlayers Global default max players
     * @param globalMinPlayers Global default min players
     * @param globalTeamSize Global default team size
     * @param globalRoundTime Global default round time (in seconds)
     * @param globalActivateTeams Global default activate teams
     * @param globalActivateKits Global default activate kits
     * @param globalActivateShop Global default activate shop
     * @param globalActivateAirdrops Global default activate airdrops
     */
    public LobbyConfig(
            int lobbyId,
            FileConfiguration lobbies,
            int globalCountdown,
            int globalMaxPlayers,
            int globalMinPlayers,
            int globalTeamSize,
            int globalRoundTime,
            boolean globalActivateTeams,
            boolean globalActivateKits,
            boolean globalActivateShop,
            boolean globalActivateAirdrops) {
        
        this.lobbyId = lobbyId;
        this.lobbies = lobbies;
        
        String defaultsPath = "pg.defaults";
        String lobbyRootPath = "pg.lobbies." + lobbyId;
        String lobbySettingsPath = lobbyRootPath + ".settings";

        int resolvedGlobalCountdown = getIntOrDefault(defaultsPath + ".countdown", getIntOrDefault("pg.countdown", globalCountdown));
        int resolvedGlobalMaxPlayers = getIntOrDefault(defaultsPath + ".maxPlayers", getIntOrDefault("pg.maxPlayers", globalMaxPlayers));
        int resolvedGlobalMinPlayers = getIntOrDefault(defaultsPath + ".minPlayers", getIntOrDefault("pg.minPlayers", globalMinPlayers));
        int resolvedGlobalTeamSize = getIntOrDefault(defaultsPath + ".teamSize", getIntOrDefault("pg.teamSize", globalTeamSize));
        int resolvedGlobalRoundTime = getIntOrDefault(defaultsPath + ".roundTime", getIntOrDefault("pg.roundTime", globalRoundTime));

        boolean resolvedGlobalActivateTeams = getBooleanOrDefault(defaultsPath + ".activateTeams", getBooleanOrDefault("pg.activateTeams", globalActivateTeams));
        boolean resolvedGlobalActivateKits = getBooleanOrDefault(defaultsPath + ".activateKits", getBooleanOrDefault("pg.activateKits", globalActivateKits));
        boolean resolvedGlobalActivateShop = getBooleanOrDefault(defaultsPath + ".activateShop", getBooleanOrDefault("pg.activateShop", globalActivateShop));
        boolean resolvedGlobalActivateAirdrops = getBooleanOrDefault(defaultsPath + ".activateAirdrops", getBooleanOrDefault("pg.activateAirdrops", globalActivateAirdrops));

        this.countdown = getLobbyInt("countdown", lobbySettingsPath, lobbyRootPath, resolvedGlobalCountdown);
        this.maxPlayers = getLobbyInt("maxPlayers", lobbySettingsPath, lobbyRootPath, resolvedGlobalMaxPlayers);
        this.minPlayers = getLobbyInt("minPlayers", lobbySettingsPath, lobbyRootPath, resolvedGlobalMinPlayers);
        this.teamSize = getLobbyInt("teamSize", lobbySettingsPath, lobbyRootPath, resolvedGlobalTeamSize);
        this.roundTime = normalizeRoundTime(getLobbyInt("roundTime", lobbySettingsPath, lobbyRootPath, resolvedGlobalRoundTime));

        this.activateTeams = getLobbyBoolean("activateTeams", lobbySettingsPath, lobbyRootPath, resolvedGlobalActivateTeams);
        this.activateKits = getLobbyBoolean("activateKits", lobbySettingsPath, lobbyRootPath, resolvedGlobalActivateKits);
        this.activateShop = getLobbyBoolean("activateShop", lobbySettingsPath, lobbyRootPath, resolvedGlobalActivateShop);
        this.activateAirdrops = getLobbyBoolean("activateAirdrops", lobbySettingsPath, lobbyRootPath, resolvedGlobalActivateAirdrops);
    }
    
    /**
     * Get an integer value from config, or return default if not set.
     */
    private int getIntOrDefault(String path, int defaultValue) {
        if (lobbies.contains(path)) {
            return lobbies.getInt(path);
        }
        PotionGamesX plugin = PotionGamesX.getInstance();
        if (plugin != null && plugin.getConfig().contains(path)) {
            return plugin.getConfig().getInt(path);
        }
        return defaultValue;
    }
    
    /**
     * Get a boolean value from config, or return default if not set.
     */
    private boolean getBooleanOrDefault(String path, boolean defaultValue) {
        if (lobbies.contains(path)) {
            return lobbies.getBoolean(path);
        }
        PotionGamesX plugin = PotionGamesX.getInstance();
        if (plugin != null && plugin.getConfig().contains(path)) {
            return plugin.getConfig().getBoolean(path);
        }
        return defaultValue;
    }

    private int getLobbyInt(String key, String lobbySettingsPath, String lobbyRootPath, int defaultValue) {
        String settingsKey = lobbySettingsPath + "." + key;
        if (lobbies.contains(settingsKey)) {
            return lobbies.getInt(settingsKey);
        }
        String legacyKey = lobbyRootPath + "." + key;
        if (lobbies.contains(legacyKey)) {
            return lobbies.getInt(legacyKey);
        }
        return defaultValue;
    }

    private boolean getLobbyBoolean(String key, String lobbySettingsPath, String lobbyRootPath, boolean defaultValue) {
        String settingsKey = lobbySettingsPath + "." + key;
        if (lobbies.contains(settingsKey)) {
            return lobbies.getBoolean(settingsKey);
        }
        String legacyKey = lobbyRootPath + "." + key;
        if (lobbies.contains(legacyKey)) {
            return lobbies.getBoolean(legacyKey);
        }
        return defaultValue;
    }

    private int normalizeRoundTime(int value) {
        if (value > 0 && value <= 300) {
            return value * 60;
        }
        return value;
    }
    
    // ===== GETTERS =====
    
    public int getLobbyId() {
        return lobbyId;
    }
    
    public int getCountdown() {
        return countdown;
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
    
    public boolean isActivateTeams() {
        return activateTeams;
    }
    
    public boolean isActivateKits() {
        return activateKits;
    }
    
    public boolean isActivateShop() {
        return activateShop;
    }
    
    public boolean isActivateAirdrops() {
        return activateAirdrops;
    }
    
    // ===== SETTERS FOR RUNTIME CHANGES =====
    
    public void setCountdown(int countdown) {
        this.countdown = countdown;
        lobbies.set("pg.lobbies." + lobbyId + ".countdown", countdown);
    }
    
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        lobbies.set("pg.lobbies." + lobbyId + ".maxPlayers", maxPlayers);
    }
    
    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
        lobbies.set("pg.lobbies." + lobbyId + ".minPlayers", minPlayers);
    }
    
    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
        lobbies.set("pg.lobbies." + lobbyId + ".teamSize", teamSize);
    }
    
    public void setRoundTime(int roundTime) {
        this.roundTime = roundTime;
        lobbies.set("pg.lobbies." + lobbyId + ".roundTime", roundTime);
    }
    
    public void setActivateTeams(boolean activate) {
        this.activateTeams = activate;
        lobbies.set("pg.lobbies." + lobbyId + ".activateTeams", activate);
    }
    
    public void setActivateKits(boolean activate) {
        this.activateKits = activate;
        lobbies.set("pg.lobbies." + lobbyId + ".activateKits", activate);
    }
    
    public void setActivateShop(boolean activate) {
        this.activateShop = activate;
        lobbies.set("pg.lobbies." + lobbyId + ".activateShop", activate);
    }
    
    public void setActivateAirdrops(boolean activate) {
        this.activateAirdrops = activate;
        lobbies.set("pg.lobbies." + lobbyId + ".activateAirdrops", activate);
    }
}
