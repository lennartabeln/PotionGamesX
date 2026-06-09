package com.tw0far.potiongames.managers;

import org.bukkit.entity.Player;
import java.util.*;

/**
 * Implementation of IPlayerStateManager.
 * Consolidates 6 player lists from PotionGames:
 * - pgPlayers (active)
 * - specPlayers (spectators)
 * - richkidPlayers (in shop)
 * - setupPlayer (in setup)
 * - playerLobby (active lobby assignments)
 * - specLobby (spectator lobby assignments)
 * 
 * Provides centralized, type-safe player state management.
 */
public class PlayerStateManager implements IPlayerStateManager {
    
    // Active/Spectator Tracking
    private final Set<Player> activePlayers = new HashSet<>();
    private final Set<Player> spectators = new HashSet<>();
    private final Set<Player> shopPlayers = new HashSet<>();
    // Lobby Assignments
    private final Map<Player, String> playerLobby = new HashMap<>();
    private final Map<Player, String> specLobby = new HashMap<>();
    
    @Override
    public void onEnable() {
        // No initialization needed
    }
    
    @Override
    public void onDisable() {
        clearAll();
    }
    
    @Override
    public void reload() {
        clearAll();
    }
    
    // ===== ACTIVE PLAYER TRACKING =====
    @Override
    public void addActivePlayer(Player player) {
        activePlayers.add(player);
        spectators.remove(player);
        shopPlayers.remove(player);
    }
    
    @Override
    public void removeActivePlayer(Player player) {
        activePlayers.remove(player);
        playerLobby.remove(player);
    }
    
    @Override
    public boolean isActivePlayer(Player player) {
        return activePlayers.contains(player);
    }
    
    @Override
    public Collection<Player> getActivePlayers() {
        return new HashSet<>(activePlayers);
    }
    
    @Override
    public int getActivePlayerCount() {
        return activePlayers.size();
    }
    
    // ===== SPECTATOR TRACKING =====
    @Override
    public void addSpectator(Player player) {
        spectators.add(player);
        activePlayers.remove(player);
        shopPlayers.remove(player);
    }
    
    @Override
    public void removeSpectator(Player player) {
        spectators.remove(player);
        specLobby.remove(player);
    }
    
    @Override
    public boolean isSpectator(Player player) {
        return spectators.contains(player);
    }
    
    @Override
    public Collection<Player> getSpectators() {
        return new HashSet<>(spectators);
    }
    
    @Override
    public int getSpectatorCount() {
        return spectators.size();
    }
    
    // ===== SHOP BROWSING =====
    @Override
    public void addToShop(Player player) {
        shopPlayers.add(player);
    }
    
    @Override
    public void removeFromShop(Player player) {
        shopPlayers.remove(player);
    }
    
    @Override
    public boolean isInShop(Player player) {
        return shopPlayers.contains(player);
    }
    
    @Override
    public Collection<Player> getShopPlayers() {
        return new HashSet<>(shopPlayers);
    }
    
    // ===== LOBBY ASSIGNMENT =====
    @Override
    public void assignToLobby(Player player, String lobbyId) {
        playerLobby.put(player, lobbyId);
        specLobby.remove(player);
    }
    
    @Override
    public String getLobby(Player player) {
        return playerLobby.get(player);
    }
    
    @Override
    public void removeFromLobby(Player player) {
        playerLobby.remove(player);
    }
    
    @Override
    public boolean isInLobby(Player player) {
        return playerLobby.containsKey(player);
    }
    
    @Override
    public Collection<Player> getPlayersInLobby(String lobbyId) {
        Set<Player> result = new HashSet<>();
        for (Map.Entry<Player, String> entry : playerLobby.entrySet()) {
            if (entry.getValue() != null && entry.getValue().equals(lobbyId)) {
                result.add(entry.getKey());
            }
        }
        return result;
    }
    
    @Override
    public int getPlayerCountInLobby(String lobbyId) {
        int count = 0;
        for (String lobby : playerLobby.values()) {
            if (lobby != null && lobby.equals(lobbyId)) {
                count++;
            }
        }
        return count;
    }
    
    // ===== SPECTATOR LOBBY ASSIGNMENT =====
    @Override
    public void assignToSpectatorLobby(Player player, String lobbyId) {
        specLobby.put(player, lobbyId);
        playerLobby.remove(player);
    }
    
    @Override
    public String getSpectatorLobby(Player player) {
        return specLobby.get(player);
    }
    
    @Override
    public void removeSpectatorFromLobby(Player player) {
        specLobby.remove(player);
    }
    
    @Override
    public Collection<Player> getSpectatorsInLobby(String lobbyId) {
        Set<Player> result = new HashSet<>();
        for (Map.Entry<Player, String> entry : specLobby.entrySet()) {
            if (entry.getValue() != null && entry.getValue().equals(lobbyId)) {
                result.add(entry.getKey());
            }
        }
        return result;
    }
    
    @Override
    public int getSpectatorCountInLobby(String lobbyId) {
        int count = 0;
        for (String lobby : specLobby.values()) {
            if (lobby != null && lobby.equals(lobbyId)) {
                count++;
            }
        }
        return count;
    }
    
    // ===== BATCH OPERATIONS =====
    @Override
    public void clearAll() {
        activePlayers.clear();
        spectators.clear();
        shopPlayers.clear();
        playerLobby.clear();
        specLobby.clear();
    }
    
    @Override
    public void clearLobby(String lobbyId) {
        playerLobby.entrySet().removeIf(entry -> entry.getValue() != null && entry.getValue().equals(lobbyId));
        specLobby.entrySet().removeIf(entry -> entry.getValue() != null && entry.getValue().equals(lobbyId));
    }
    
    @Override
    public Collection<Player> getAllTrackedPlayers() {
        Set<Player> all = new HashSet<>();
        all.addAll(activePlayers);
        all.addAll(spectators);
        all.addAll(shopPlayers);
        return all;
    }
}
