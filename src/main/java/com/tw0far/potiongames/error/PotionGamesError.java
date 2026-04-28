package com.tw0far.potiongames.error;

/**
 * Predefined error codes and messages for the PotionGames plugin.
 * Ensures consistent error handling across the plugin.
 */
public enum PotionGamesError {
    // Configuration Errors
    CONFIG_LOAD_FAILED("CFG_001", "Failed to load configuration", "Configuration file could not be loaded"),
    CONFIG_INVALID("CFG_002", "Invalid configuration value", "Configuration contains invalid values"),
    CONFIG_SAVE_FAILED("CFG_003", "Failed to save configuration", "Configuration file could not be saved"),
    
    // Database Errors
    DB_CONNECTION_FAILED("DB_001", "Database connection failed", "Could not connect to database"),
    DB_QUERY_FAILED("DB_002", "Database query failed", "Query execution error"),
    DB_INSERT_FAILED("DB_003", "Failed to save player data", "Could not insert/update player statistics"),
    DB_CLOSE_FAILED("DB_004", "Database connection did not close properly", "Connection close failed"),
    
    // Game State Errors
    GAME_NOT_RUNNING("GAME_001", "Game is not running", "No active game session"),
    GAME_FULL("GAME_002", "Game is full", "Cannot join - maximum players reached"),
    GAME_IN_PROGRESS("GAME_003", "Cannot join - game in progress", "Join not allowed after game start"),
    LOBBY_NOT_FOUND("GAME_004", "Lobby not found", "Specified lobby does not exist"),
    ARENA_NOT_FOUND("GAME_005", "Arena not found", "Specified arena does not exist"),
    
    // Player Errors
    PLAYER_NOT_FOUND("PLAYER_001", "Player not found", "Target player is not online"),
    PLAYER_NOT_IN_GAME("PLAYER_002", "Player not in game", "Target player is not participating"),
    PLAYER_ALREADY_IN_GAME("PLAYER_003", "Already in game", "Player is already in a game"),
    INSUFFICIENT_PLAYERS("PLAYER_004", "Not enough players to start", "Minimum player count not met"),
    
    // Command Errors
    INVALID_COMMAND("CMD_001", "Invalid command", "Command syntax incorrect"),
    INVALID_ARGS("CMD_002", "Invalid arguments", "Wrong number or type of arguments"),
    PERMISSION_DENIED("CMD_003", "Permission denied", "Insufficient permissions to execute command"),
    COMMAND_EXECUTION_FAILED("CMD_004", "Command failed to execute", "An error occurred while executing command"),
    
    // Setup Errors
    SETUP_SPAWN_INVALID("SETUP_001", "Invalid spawn location", "Spawn must be in a loaded chunk"),
    SETUP_DUPLICATE_ARENA("SETUP_002", "Arena already exists", "Cannot create duplicate arena"),
    SETUP_NO_SPAWNS("SETUP_003", "Arena has no spawns", "At least one spawn is required"),
    
    // Economy/Reward Errors
    VAULT_NOT_FOUND("ECON_001", "Vault plugin not found", "Economy system unavailable"),
    INSUFFICIENT_FUNDS("ECON_002", "Insufficient funds", "Player does not have enough currency"),
    
    // System Errors
    PLUGIN_INITIALIZATION_FAILED("SYS_001", "Plugin failed to initialize", "Fatal initialization error"),
    INVALID_STATE("SYS_002", "Invalid state transition", "Game state change not allowed"),
    UNKNOWN_ERROR("SYS_999", "An unknown error occurred", "Please contact an administrator");
    
    private final String code;
    private final String userMessage;
    private final String technicalMessage;
    
    PotionGamesError(String code, String userMessage, String technicalMessage) {
        this.code = code;
        this.userMessage = userMessage;
        this.technicalMessage = technicalMessage;
    }
    
    public String getCode() { return code; }
    public String getUserMessage() { return userMessage; }
    public String getTechnicalMessage() { return technicalMessage; }
}
