package com.tw0far.potiongames.error;

import org.bukkit.entity.Player;

import java.util.logging.Logger;

/**
 * Central error handler for the PotionGames plugin.
 * Manages error reporting to players, admins, and console.
 */
public class ErrorHandler {
    private final Logger logger;
    
    public ErrorHandler(Logger logger) {
        this.logger = logger;
    }
    
    /**
     * Handle a predefined error
     */
    public void handle(PotionGamesError error) {
        new ErrorContext.Builder(error.getCode())
            .userMessage(error.getUserMessage())
            .adminMessage(error.getTechnicalMessage())
            .consoleMessage(error.getTechnicalMessage())
            .level(ErrorContext.ErrorLevel.ERROR)
            .build()
            .logToConsole(logger);
    }
    
    /**
     * Handle an error with custom messages
     */
    public void handle(String errorCode, String userMsg, String adminMsg, Throwable ex) {
        new ErrorContext.Builder(errorCode)
            .userMessage(userMsg)
            .adminMessage(adminMsg)
            .exception(ex)
            .level(ex != null ? ErrorContext.ErrorLevel.CRITICAL : ErrorContext.ErrorLevel.ERROR)
            .build()
            .logToConsole(logger);
    }
    
    /**
     * Send error to player
     */
    public void sendToPlayer(Player player, PotionGamesError error) {
        new ErrorContext.Builder(error.getCode())
            .userMessage(error.getUserMessage())
            .level(ErrorContext.ErrorLevel.ERROR)
            .build()
            .sendToPlayer(player);
    }
    
    /**
     * Send custom error to player
     */
    public void sendToPlayer(Player player, String message) {
        new ErrorContext.Builder("CUSTOM")
            .userMessage(message)
            .level(ErrorContext.ErrorLevel.ERROR)
            .build()
            .sendToPlayer(player);
    }
    
    /**
     * Send info to player
     */
    public void sendInfo(Player player, String message) {
        new ErrorContext.Builder("INFO")
            .userMessage(message)
            .level(ErrorContext.ErrorLevel.INFO)
            .build()
            .sendToPlayer(player);
    }
    
    /**
     * Send warning to player
     */
    public void sendWarning(Player player, String message) {
        new ErrorContext.Builder("WARNING")
            .userMessage(message)
            .level(ErrorContext.ErrorLevel.WARNING)
            .build()
            .sendToPlayer(player);
    }
    
    /**
     * Send success to player (using info level)
     */
    public void sendSuccess(Player player, String message) {
        sendInfo(player, "✓ " + message);
    }
    
    /**
     * Log error to console
     */
    public void logError(String errorCode, String message, Throwable ex) {
        new ErrorContext.Builder(errorCode)
            .consoleMessage(message)
            .exception(ex)
            .level(ErrorContext.ErrorLevel.ERROR)
            .build()
            .logToConsole(logger);
    }
    
    /**
     * Log warning to console
     */
    public void logWarning(String errorCode, String message) {
        new ErrorContext.Builder(errorCode)
            .consoleMessage(message)
            .level(ErrorContext.ErrorLevel.WARNING)
            .build()
            .logToConsole(logger);
    }
    
    /**
     * Log info to console
     */
    public void logInfo(String message) {
        new ErrorContext.Builder("INFO")
            .consoleMessage(message)
            .level(ErrorContext.ErrorLevel.INFO)
            .build()
            .logToConsole(logger);
    }
}
