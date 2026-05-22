package com.tw0far.potiongames.config;

/**
 * TRANSLATION KEYS for configuration access.
 * Use these constants instead of string magic keys to avoid typos and enable refactoring.
 * 
 * BENEFITS:
 * - Type-safe configuration access
 * - Compiler checks for typos
 * - Easy refactoring
 * - IDE autocomplete
 * - Central configuration documentation
 * - Support for all per-lobby settings
 * 
 * USAGE:
 * OLD: cfg.getInt("pg.countdown")
 * NEW: cfg.getInt(ConfigKeys.COUNTDOWN)
 */
public enum ConfigKeys {
    // ===== GLOBAL SETTINGS =====
    // Database
    ACTIVATE_MYSQL("pg.activateMySQL", "Database", false),
    DB_HOST("pg.mysql.host", "Database", "localhost"),
    DB_PORT("pg.mysql.port", "Database", 3306),
    DB_DATABASE("pg.mysql.database", "Database", "potiongames"),
    DB_USER("pg.mysql.user", "Database", "root"),
    DB_PASSWORD("pg.mysql.password", "Database", ""),
    
    // Game Settings
    COUNTDOWN("pg.countdown", "Game", 60),
    MAX_PLAYERS("pg.maxPlayers", "Game", 24),
    MIN_PLAYERS("pg.minPlayers", "Game", 2),
    TEAM_SIZE("pg.teamSize", "Game", 2),
    ROUND_TIME("pg.roundTime", "Game", 30),
    
    // Features
    ACTIVATE_TEAMS("pg.activateTeams", "Features", true),
    ACTIVATE_KITS("pg.activateKits", "Features", true),
    ACTIVATE_SHOP("pg.activateShop", "Features", true),
    ACTIVATE_AIRDROPS("pg.activateAirdrops", "Features", true),
    ACTIVATE_DEATHMATCH("pg.activateDeathmatch", "Features", true),
    ACTIVATE_SCOREBOARD("pg.activateScoreboard", "Features", true),
    
    // Behavior
    START_ON_JOIN("pg.startOnJoin", "Behavior", false),
    COMPASS_ON_SPAWN("pg.compassOnSpawn", "Behavior", false),
    ALLOW_OUTSIDE_CHAT("pg.allowOutsideChat", "Behavior", false),
    CHANGE_GAMERULES("pg.changeGamerules", "Behavior", true),
    FRIENDLY_FIRE("pg.friendlyFire", "Behavior", false),
    JOIN_STARTED("pg.joinStarted", "Behavior", false),
    BROADCAST_STARTING("pg.broadcastStarting", "Behavior", false),
    
    // System
    GAME_SERVER("pg.getGame()Server", "System", true),
    LANGUAGE("pg.language", "System", "en_US"),
    
    // Economy
    ENABLE_REWARDS("pg.enableRewards", "Economy", false),
    KILL_REWARD("pg.killReward", "Economy", 10),
    WINNING_REWARD("pg.winningReward", "Economy", 100),
    
    // Potions/Kits
    ACTIVE_POTIONS("pg.activePotions", "Content", 19),
    ACTIVE_KITS("pg.activeKits", "Content", 6),
    
    // ===== PER-LOBBY SETTINGS =====
    // Lobby-level overrides (these can be customized per lobby)
    LOBBY_ENABLED("pg.lobbies.{id}.enabled", "Lobby", true),
    LOBBY_SPAWN("pg.lobbies.{id}.spawn", "Lobby", null),
    LOBBY_MAX_PLAYERS("pg.lobbies.{id}.maxPlayers", "Lobby", 24),
    LOBBY_MIN_PLAYERS("pg.lobbies.{id}.minPlayers", "Lobby", 2),
    LOBBY_TEAM_SIZE("pg.lobbies.{id}.teamSize", "Lobby", 2),
    LOBBY_ROUND_TIME("pg.lobbies.{id}.roundTime", "Lobby", 30),
    LOBBY_ACTIVATE_TEAMS("pg.lobbies.{id}.activateTeams", "Lobby", true),
    LOBBY_ACTIVATE_KITS("pg.lobbies.{id}.activateKits", "Lobby", true),
    LOBBY_ACTIVATE_SHOP("pg.lobbies.{id}.activateShop", "Lobby", true),
    LOBBY_ACTIVATE_AIRDROPS("pg.lobbies.{id}.activateAirdrops", "Lobby", true),
    LOBBY_FRIENDLY_FIRE("pg.lobbies.{id}.friendlyFire", "Lobby", false),
    
    // ===== ARENA SETTINGS =====
    ARENA_SPAWNS("pg.lobbies.{lobbyId}.arenas.{arenaName}.spawns", "Arena", null),
    ARENA_DEATHMATCH("pg.lobbies.{lobbyId}.arenas.{arenaName}.deathmatch", "Arena", null),
    
    // ===== CHEST CONFIGURATION =====
    CHEST_LOCATIONS("pg.lobbies.{id}.chests", "Chest", null),
    CHEST_CONTENTS("pg.lobbies.{id}.chestContents", "Chest", null),
    
    // ===== SIGN CONFIGURATION =====
    LOBBY_JOIN_SIGN("pg.lobbies.{id}.joinSign", "Sign", null);
    
    private final String key;
    private final String category;
    private final Object defaultValue;
    
    ConfigKeys(String key, String category, Object defaultValue) {
        this.key = key;
        this.category = category;
        this.defaultValue = defaultValue;
    }
    
    /**
     * Get the configuration key
     */
    public String getKey() {
        return key;
    }
    
    /**
     * Get the configuration key with ID substitution for per-lobby settings
     * 
     * @param id The lobby ID
     * @return The key with {id} replaced
     */
    public String getKey(int id) {
        return key.replace("{id}", String.valueOf(id));
    }
    
    /**
     * Get the configuration key with lobby ID and arena name substitution
     * 
     * @param lobbyId The lobby ID
     * @param arenaName The arena name
     * @return The key with {lobbyId} and {arenaName} replaced
     */
    public String getKey(int lobbyId, String arenaName) {
        return key.replace("{lobbyId}", String.valueOf(lobbyId))
                 .replace("{arenaName}", arenaName);
    }
    
    /**
     * Get the category for organization
     */
    public String getCategory() {
        return category;
    }
    
    /**
     * Get the default value
     */
    public Object getDefaultValue() {
        return defaultValue;
    }
    
    /**
     * Check if this key supports per-lobby customization
     */
    public boolean isPerLobby() {
        return key.contains("{id}");
    }
    
    /**
     * Check if this key supports per-arena customization
     */
    public boolean isPerArena() {
        return key.contains("{arenaName}");
    }
}
