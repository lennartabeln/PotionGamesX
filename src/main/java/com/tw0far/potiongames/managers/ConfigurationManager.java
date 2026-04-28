package com.tw0far.potiongames.managers;

import com.tw0far.potiongames.main.PotionGames;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Centralized configuration manager.
 * Extracts all config getters/setters from main PotionGames class.
 */
public class ConfigurationManager implements IManager {
    private final PotionGames plugin;
    private final FileConfiguration config;
    
    // Game settings
    private int countdown;
    private int maxPlayers;
    private int minPlayers;
    private int teamSize;
    private int roundTime;
    private int roundTimeSeconds;
    private int activePotions;
    private int activeKits;
    private int winningReward;
    private int killReward;
    
    // Feature flags
    private boolean activateTeams;
    private boolean activateKits;
    private boolean activateShop;
    private boolean activateAirdrops;
    private boolean activateMysql;
    private boolean activateScoreboard;
    private boolean activateDeathmatch;
    private boolean enableRewards;
    private boolean broadcastStarting;
    
    // Behavior flags
    private boolean startOnJoin;
    private boolean friendlyFire;
    private boolean joinStarted;
    private boolean compassOnSpawn;
    private boolean allowOutsideChat;
    private boolean changeGamerules;
    private boolean gameServer;
    private boolean lobbySystem;
    
    // Database
    private String dbHost;
    private String dbPort;
    private String dbName;
    private String dbUser;
    private String dbPassword;
    
    // Localization
    private String language;
    
    public ConfigurationManager(PotionGames plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }
    
    @Override
    public void onEnable() {
        loadAllConfig();
    }
    
    @Override
    public void onDisable() {
        // No cleanup needed
    }
    
    private void loadAllConfig() {
        // Game settings
        countdown = config.getInt("countdown", 60);
        maxPlayers = config.getInt("maxPlayers", 24);
        minPlayers = config.getInt("minPlayers", 12);
        teamSize = config.getInt("teamSize", 2);
        roundTime = config.getInt("roundTime", 30);
        roundTimeSeconds = roundTime * 60;
        activePotions = config.getInt("activePotions", 19);
        activeKits = config.getInt("activeKits", 6);
        winningReward = config.getInt("winningReward", 100);
        killReward = config.getInt("killReward", 10);
        
        // Feature flags
        activateTeams = config.getBoolean("activateTeams", true);
        activateKits = config.getBoolean("activateKits", true);
        activateShop = config.getBoolean("activateShop", true);
        activateAirdrops = config.getBoolean("activateAirdrops", true);
        activateMysql = config.getBoolean("activateMySQL", false);
        activateScoreboard = config.getBoolean("activateScoreboard", true);
        activateDeathmatch = config.getBoolean("activateDeathmatch", true);
        enableRewards = config.getBoolean("enableRewards", false);
        broadcastStarting = config.getBoolean("broadcastStarting", false);
        
        // Behavior flags
        startOnJoin = config.getBoolean("startOnJoin", false);
        friendlyFire = config.getBoolean("friendlyFire", false);
        joinStarted = config.getBoolean("joinStarted", true);
        compassOnSpawn = config.getBoolean("compassOnSpawn", false);
        allowOutsideChat = config.getBoolean("allowOutsideChat", false);
        changeGamerules = config.getBoolean("changeGamerules", true);
        gameServer = config.getBoolean("gameServer", true);
        lobbySystem = config.getBoolean("lobbySystem", false);
        
        // Database
        dbHost = config.getString("mysql.host", "localhost");
        dbPort = config.getString("mysql.port", "3306");
        dbName = config.getString("mysql.database", "potiongames");
        dbUser = config.getString("mysql.user", "root");
        dbPassword = config.getString("mysql.password", "");
        
        // Localization
        language = config.getString("language", "en_US");
    }
    
    public void reload() {
        plugin.reloadConfig();
        loadAllConfig();
    }
    
    // Getters
    public int getCountdown() { return countdown; }
    public int getMaxPlayers() { return maxPlayers; }
    public int getMinPlayers() { return minPlayers; }
    public int getTeamSize() { return teamSize; }
    public int getRoundTime() { return roundTime; }
    public int getRoundTimeSeconds() { return roundTimeSeconds; }
    public int getActivePotions() { return activePotions; }
    public int getActiveKits() { return activeKits; }
    public int getWinningReward() { return winningReward; }
    public int getKillReward() { return killReward; }
    
    public boolean isActivateTeams() { return activateTeams; }
    public boolean isActivateKits() { return activateKits; }
    public boolean isActivateShop() { return activateShop; }
    public boolean isActivateAirdrops() { return activateAirdrops; }
    public boolean isActivateMysql() { return activateMysql; }
    public boolean isActivateScoreboard() { return activateScoreboard; }
    public boolean isActivateDeathmatch() { return activateDeathmatch; }
    public boolean isEnableRewards() { return enableRewards; }
    public boolean isBroadcastStarting() { return broadcastStarting; }
    
    public boolean isStartOnJoin() { return startOnJoin; }
    public boolean isFriendlyFire() { return friendlyFire; }
    public boolean isJoinStarted() { return joinStarted; }
    public boolean isCompassOnSpawn() { return compassOnSpawn; }
    public boolean isAllowOutsideChat() { return allowOutsideChat; }
    public boolean isChangeGamerules() { return changeGamerules; }
    public boolean isGameServer() { return gameServer; }
    public boolean isLobbySystem() { return lobbySystem; }
    
    public String getDbHost() { return dbHost; }
    public String getDbPort() { return dbPort; }
    public String getDbName() { return dbName; }
    public String getDbUser() { return dbUser; }
    public String getDbPassword() { return dbPassword; }
    
    public String getLanguage() { return language; }
    
    // Setters
    public void setCountdown(int value) { countdown = value; }
    public void setMaxPlayers(int value) { maxPlayers = value; }
    public void setMinPlayers(int value) { minPlayers = value; }
    public void setTeamSize(int value) { teamSize = value; }
    public void setRoundTime(int value) { 
        roundTime = value; 
        roundTimeSeconds = roundTime * 60;
    }
    public void setActivePotions(int value) { activePotions = value; }
    public void setActiveKits(int value) { activeKits = value; }
    public void setWinningReward(int value) { winningReward = value; }
    public void setKillReward(int value) { killReward = value; }
    
    public void setActivateTeams(boolean value) { activateTeams = value; }
    public void setActivateKits(boolean value) { activateKits = value; }
    public void setActivateShop(boolean value) { activateShop = value; }
    public void setActivateAirdrops(boolean value) { activateAirdrops = value; }
    public void setStartOnJoin(boolean value) { startOnJoin = value; }
    public void setFriendlyFire(boolean value) { friendlyFire = value; }
}
