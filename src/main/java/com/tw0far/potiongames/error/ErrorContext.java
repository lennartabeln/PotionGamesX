package com.tw0far.potiongames.error;

import com.tw0far.potiongames.util.MessageUtil;
import com.tw0far.potiongames.models.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Error context for handling different error scenarios.
 * Provides appropriate messages for players, admins, and console.
 */
public class ErrorContext {
    private final String errorCode;
    private final String userMessage;
    private final String adminMessage;
    private final String consoleMessage;
    private final Throwable exception;
    private final ErrorLevel level;
    
    public enum ErrorLevel {
        INFO, WARNING, ERROR, CRITICAL
    }
    
    private ErrorContext(Builder builder) {
        this.errorCode = builder.errorCode;
        this.userMessage = builder.userMessage;
        this.adminMessage = builder.adminMessage;
        this.consoleMessage = builder.consoleMessage;
        this.exception = builder.exception;
        this.level = builder.level;
    }
    
    /**
     * Send error to player
     */
    public void sendToPlayer(Player player) {
        if (player == null) return;
        
        Component message = switch (level) {
            case INFO -> MessageUtil.createInfo(userMessage);
            case WARNING -> MessageUtil.createWarning(userMessage);
            case ERROR, CRITICAL -> MessageUtil.createError(userMessage);
        };
        
        player.sendMessage(message);
    }
    
    /**
     * Send admin message to player with permission
     */
    public void sendToAdmin(Player player) {
        if (player == null || !player.hasPermission("pg.admin")) return;
        
        Component message = Component.text(Messages.raw("admin.prefix", "[PG-Admin]")).color(NamedTextColor.GOLD)
            .append(Component.text(" ").color(NamedTextColor.GOLD))
            .append(Component.text(adminMessage).color(NamedTextColor.GRAY));
        
        player.sendMessage(message);
    }
    
    /**
     * Log to console
     */
    public void logToConsole(Logger logger) {
        String logMessage = String.format("[%s] %s - %s", errorCode, level.name(), consoleMessage);
        
        if (exception != null) {
            logger.log(level == ErrorLevel.CRITICAL ? Level.SEVERE : Level.WARNING, logMessage, exception);
        } else {
            logger.log(
                level == ErrorLevel.CRITICAL || level == ErrorLevel.ERROR ? Level.SEVERE :
                level == ErrorLevel.WARNING ? Level.WARNING : Level.INFO,
                logMessage
            );
        }
    }
    
    // Getters
    public String getErrorCode() { return errorCode; }
    public String getUserMessage() { return userMessage; }
    public String getAdminMessage() { return adminMessage; }
    public String getConsoleMessage() { return consoleMessage; }
    public Throwable getException() { return exception; }
    public ErrorLevel getLevel() { return level; }
    
    /**
     * Builder for ErrorContext
     */
    public static class Builder {
        private final String errorCode;
        private String userMessage;
        private String adminMessage;
        private String consoleMessage;
        private Throwable exception;
        private ErrorLevel level = ErrorLevel.ERROR;
        
        public Builder(String errorCode) {
            this.errorCode = errorCode;
        }
        
        public Builder userMessage(String message) {
            this.userMessage = message;
            return this;
        }
        
        public Builder adminMessage(String message) {
            this.adminMessage = message;
            return this;
        }
        
        public Builder consoleMessage(String message) {
            this.consoleMessage = message;
            return this;
        }
        
        public Builder exception(Throwable ex) {
            this.exception = ex;
            return this;
        }
        
        public Builder level(ErrorLevel lvl) {
            this.level = lvl;
            return this;
        }
        
        public ErrorContext build() {
            if (userMessage == null) userMessage = "An error occurred. Please try again.";
            if (adminMessage == null) adminMessage = "Error: " + errorCode;
            if (consoleMessage == null) consoleMessage = "Error " + errorCode + " occurred";
            
            return new ErrorContext(this);
        }
    }
}
