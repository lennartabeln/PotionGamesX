package com.tw0far.potiongames.util;

import com.tw0far.potiongames.managers.IConfigurationManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Validates plugin configuration on startup
 * Checks for common issues and provides warnings
 */
public class ConfigValidator {
    private final JavaPlugin plugin;
    private final IConfigurationManager configManager;
    private final List<String> warnings = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();
    
    public ConfigValidator(JavaPlugin plugin, IConfigurationManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }
    
    /**
     * Run all validation checks
     */
    public boolean validate() {
        checkGameDefaults();
        checkDatabaseConfig();
        checkGameRules();
        
        logResults();
        
        return errors.isEmpty();
    }
    
    private void checkGameDefaults() {
        int maxPlayers = configManager.getMaxPlayers();
        int minPlayers = configManager.getMinPlayers();
        int teamSize = configManager.getTeamSize();
        
        if (maxPlayers < 2) {
            errors.add("maxPlayers must be at least 2 (current: " + maxPlayers + ")");
        }
        
        if (minPlayers < 1) {
            errors.add("minPlayers must be at least 1 (current: " + minPlayers + ")");
        }
        
        if (minPlayers > maxPlayers) {
            errors.add("minPlayers (" + minPlayers + ") cannot exceed maxPlayers (" + maxPlayers + ")");
        }
        
        if (configManager.isActivateTeams() && teamSize < 1) {
            errors.add("teamSize must be at least 1 when teams are enabled (current: " + teamSize + ")");
        }
        
        if (configManager.getCountdown() < 10) {
            warnings.add("countdown is less than 10 seconds - may be too fast for players to prepare");
        }
        
        if (configManager.getRoundTime() < 1) {
            errors.add("roundTime must be at least 1 minute (current: " + configManager.getRoundTime() + ")");
        }
    }
    
    private void checkDatabaseConfig() {
        if (configManager.isActivateMysql()) {
            String host = plugin.getConfig().getString("pg.mysql.host");
            String database = plugin.getConfig().getString("pg.mysql.database");
            String user = plugin.getConfig().getString("pg.mysql.user");
            
            if (host == null || host.isEmpty()) {
                errors.add("MySQL enabled but mysql.host is not configured");
            }
            
            if (database == null || database.isEmpty()) {
                errors.add("MySQL enabled but mysql.database is not configured");
            }
            
            if (user == null || user.isEmpty()) {
                warnings.add("MySQL user not configured - will use 'root' by default");
            }
        }
    }
    
    private void checkGameRules() {
        if (!configManager.isActivateKits() && configManager.isActivateTeams()) {
            warnings.add("Teams are enabled but kits are disabled - consider enabling kits for balance");
        }
        
        if (configManager.isActivateDeathmatch() && configManager.getRoundTime() < 5) {
            warnings.add("Deathmatch enabled with short round time - may start too quickly");
        }
    }
    
    private void logResults() {
        if (errors.isEmpty() && warnings.isEmpty()) {
            plugin.getLogger().log(Level.INFO, "✓ Configuration validation: PASSED");
            return;
        }
        
        if (!errors.isEmpty()) {
            plugin.getLogger().log(Level.SEVERE, "❌ Configuration validation: " + errors.size() + " ERRORS found:");
            for (String error : errors) {
                plugin.getLogger().log(Level.SEVERE, "  - " + error);
            }
        }
        
        if (!warnings.isEmpty()) {
            plugin.getLogger().log(Level.WARNING, "⚠ Configuration validation: " + warnings.size() + " WARNINGS:");
            for (String warning : warnings) {
                plugin.getLogger().log(Level.WARNING, "  - " + warning);
            }
        }
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public List<String> getWarnings() {
        return warnings;
    }
}
