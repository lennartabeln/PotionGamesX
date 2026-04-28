package com.tw0far.potiongames.commands;

import org.bukkit.entity.Player;

/**
 * Interface for individual command handlers.
 */
public interface ICommand {
    /**
     * Get the command name
     */
    String getName();
    
    /**
     * Get the required permission node
     */
    String getPermission();
    
    /**
     * Check if command requires a specific game state
     */
    boolean requiresGameServer();
    
    /**
     * Execute the command
     */
    boolean execute(Player player, String[] args);
    
    /**
     * Get usage information
     */
    String getUsage();
}
