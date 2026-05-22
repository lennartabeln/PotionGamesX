package com.tw0far.potiongames.examples;

import com.tw0far.potiongames.error.ErrorHandler;
import com.tw0far.potiongames.error.PotionGamesError;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

/**
 * Example integration of error handling in command execution.
 * Shows how to handle different error scenarios with user-friendly messages.
 * 
 * NOTE: Mock helper methods (isPlayerInGame, isLobbyFull, etc.) are intentionally
 * empty or return constants to keep this example focused on error handling patterns.
 * In production, these would contain actual game logic.
 */
public class CommandErrorHandlingExample {
    private final ErrorHandler errorHandler;
    
    public CommandErrorHandlingExample(Logger logger) {
        this.errorHandler = new ErrorHandler(logger);
    }
    
    /**
     * Example: Handle join command with error checking
     */
    public void handleJoinCommand(Player player, String lobbyId) {
        // Check if player has permission
        if (!player.hasPermission("pg.join")) {
            errorHandler.sendToPlayer(player, PotionGamesError.PERMISSION_DENIED);
            return;
        }
        
        // Check if player already in game
        if (isPlayerInGame(player)) {
            errorHandler.sendToPlayer(player, PotionGamesError.PLAYER_ALREADY_IN_GAME);
            return;
        }
        
        // Check if lobby is full
        if (isLobbyFull(lobbyId)) {
            errorHandler.sendToPlayer(player, PotionGamesError.GAME_FULL);
            return;
        }
        
        // Check if game already running
        if (isGameRunning(lobbyId)) {
            errorHandler.sendToPlayer(player, PotionGamesError.GAME_IN_PROGRESS);
            return;
        }
        
        // Success
        addPlayerToLobby(player, lobbyId);
        errorHandler.sendSuccess(player, "Joined lobby successfully!");
    }
    
    /**
     * Example: Handle command with validation and custom error
     */
    public void handleSetupCommand(Player player, String[] args) {
        // Validate arguments
        if (args.length < 2) {
            errorHandler.sendToPlayer(player, 
                "Usage: /pg setup <arena> <spawn|chest|deathmatch>");
            return;
        }
        
        String arenaName = args[0];
        String locationType = args[1];
        
        // Validate location type
        if (!isValidLocationType(locationType)) {
            errorHandler.sendToPlayer(player, 
                "Invalid location type. Use: spawn, chest, or deathmatch");
            return;
        }
        
        try {
            setupLocation(player, arenaName, locationType);
            errorHandler.sendSuccess(player, "Location added: " + locationType);
        } catch (Exception e) {
            errorHandler.sendToPlayer(player, PotionGamesError.SETUP_SPAWN_INVALID);
            errorHandler.logError("SETUP_ERR", "Setup failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Example: Handle stats command with database error handling
     */
    public void handleStatsCommand(Player player, String targetPlayerName) {
        // Check if player exists in database
        try {
            String stats = fetchPlayerStats(targetPlayerName);
            if (stats == null) {
                errorHandler.sendToPlayer(player, PotionGamesError.PLAYER_NOT_FOUND);
                return;
            }
            
            errorHandler.sendInfo(player, stats);
        } catch (DatabaseException e) {
            // Log for admin debugging
            errorHandler.handle("DB_002", 
                "Could not retrieve statistics",
                "Database query failed: " + e.getMessage(),
                e);
            
            // Friendly message to player
            errorHandler.sendToPlayer(player, 
                "Statistics unavailable. Please try again later.");
        }
    }
    
    // Mock methods for example
    private boolean isPlayerInGame(Player player) { return false; }
    private boolean isLobbyFull(String lobbyId) { return false; }
    private boolean isGameRunning(String lobbyId) { return false; }
    private void addPlayerToLobby(Player player, String lobbyId) {}
    private boolean isValidLocationType(String type) { return true; }
    private void setupLocation(Player player, String arena, String type) {}
    private String fetchPlayerStats(String name) throws DatabaseException { return null; }
    
    private static class DatabaseException extends Exception {
        DatabaseException(String message) { super(message); }
    }
}
