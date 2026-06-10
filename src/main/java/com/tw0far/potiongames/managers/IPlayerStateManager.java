package com.tw0far.potiongames.managers;

import org.bukkit.entity.Player;
import java.util.Collection;

/**
 * Manager for all player-specific state tracking.
 * Consolidates 6 fundamental player lists from PotionGamesX.
 * 
 * Manages:
 * - Active game players (pgPlayers)
 * - Spectating players (specPlayers)
 * - Shop browsing players (richkidPlayers)
 * - Arena setup players (setupPlayer)
 * - Player lobby assignments (playerLobby, specLobby)
 */
public interface IPlayerStateManager extends IManager {
    
    // ===== ACTIVE PLAYER TRACKING =====
    /**
     * Add player to active players list
     */
    void addActivePlayer(Player player);
    
    /**
     * Remove player from active players
     */
    void removeActivePlayer(Player player);
    
    /**
     * Check if player is actively in game
     */
    boolean isActivePlayer(Player player);
    
    /**
     * Get all active players
     */
    Collection<Player> getActivePlayers();
    
    /**
     * Get count of active players
     */
    int getActivePlayerCount();
    
    // ===== SPECTATOR TRACKING =====
    /**
     * Add player to spectators list
     */
    void addSpectator(Player player);
    
    /**
     * Remove player from spectators
     */
    void removeSpectator(Player player);
    
    /**
     * Check if player is spectating
     */
    boolean isSpectator(Player player);
    
    /**
     * Get all spectators
     */
    Collection<Player> getSpectators();
    
    /**
     * Get count of spectators
     */
    int getSpectatorCount();
    
    // ===== SHOP BROWSING =====
    /**
     * Add player to shop
     */
    void addToShop(Player player);
    
    /**
     * Remove player from shop
     */
    void removeFromShop(Player player);
    
    /**
     * Check if player is in shop
     */
    boolean isInShop(Player player);
    
    /**
     * Get all players in shop
     */
    Collection<Player> getShopPlayers();
    
    // ===== LOBBY ASSIGNMENT =====
    /**
     * Assign player to a lobby
     */
    void assignToLobby(Player player, String lobbyId);
    
    /**
     * Get which lobby player is in
     */
    String getLobby(Player player);
    
    /**
     * Remove player from any lobby
     */
    void removeFromLobby(Player player);
    
    /**
     * Check if player is in any lobby
     */
    boolean isInLobby(Player player);
    
    /**
     * Get all players in a specific lobby
     */
    Collection<Player> getPlayersInLobby(String lobbyId);
    
    /**
     * Get count of players in a lobby
     */
    int getPlayerCountInLobby(String lobbyId);
    
    // ===== SPECTATOR LOBBY ASSIGNMENT =====
    /**
     * Assign player to spectate in a lobby
     */
    void assignToSpectatorLobby(Player player, String lobbyId);
    
    /**
     * Get which lobby player is spectating in
     */
    String getSpectatorLobby(Player player);
    
    /**
     * Remove spectator from lobby
     */
    void removeSpectatorFromLobby(Player player);
    
    /**
     * Get all spectators in a specific lobby
     */
    Collection<Player> getSpectatorsInLobby(String lobbyId);
    
    /**
     * Get count of spectators in a lobby
     */
    int getSpectatorCountInLobby(String lobbyId);
    
    // ===== BATCH OPERATIONS =====
    /**
     * Clear all player state
     */
    void clearAll();
    
    /**
     * Clear all players from a specific lobby
     */
    void clearLobby(String lobbyId);
    
    /**
     * Get all tracked players
     */
    Collection<Player> getAllTrackedPlayers();
}
