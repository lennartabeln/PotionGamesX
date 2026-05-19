package com.tw0far.potiongames.managers;

import org.bukkit.entity.Player;

/**
 * Interface for managing setup mode player state
 * Handles setup players list and inventory backup (inventory, armor, level, exp, location, gamemode)
 */
public interface ISetupStateManager extends IManager {
    
    // Setup player tracking
    void addSetupPlayer(Player player);
    void removeSetupPlayer(Player player);
    boolean isSetupPlayer(Player player);
    void clearAllSetupPlayers();

    // Selected lobby/arena context for setup flows
    void setSelectedLobby(Player player, Integer lobbyId);
    Integer getSelectedLobby(Player player);
    void removeSelectedLobby(Player player);
    void setSelectedArena(Player player, String arenaName);
    String getSelectedArena(Player player);
    void removeSelectedArena(Player player);
    void clearSelection(Player player);
    
    // Inventory backup (for /pg setup command)
    void savePlayerInventory(Player player, org.bukkit.inventory.ItemStack[] inventory);
    org.bukkit.inventory.ItemStack[] getPlayerInventory(Player player);
    void removeSavedInventory(Player player);
    
    // Armor backup
    void savePlayerArmor(Player player, org.bukkit.inventory.ItemStack[] armor);
    org.bukkit.inventory.ItemStack[] getPlayerArmor(Player player);
    void removeSavedArmor(Player player);
    
    // Level backup
    void savePlayerLevel(Player player, int level);
    Integer getPlayerLevel(Player player);
    void removeSavedLevel(Player player);
    
    // Experience backup
    void savePlayerExp(Player player, float exp);
    Float getPlayerExp(Player player);
    void removeSavedExp(Player player);
    
    // Location backup
    void savePlayerLocation(Player player, org.bukkit.Location location);
    org.bukkit.Location getPlayerLocation(Player player);
    void removeSavedLocation(Player player);
    
    // GameMode backup
    void savePlayerGameMode(Player player, org.bukkit.GameMode gameMode);
    org.bukkit.GameMode getPlayerGameMode(Player player);
    void removeSavedGameMode(Player player);
}
