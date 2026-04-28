package com.tw0far.potiongames.models;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Game {
    private ArrayList<Lobby> lobbies = new ArrayList<>();
    
    // Player tracking (migrated from PotionGames)
    private ArrayList<Player> activePlayers = new ArrayList<>();      // pgPlayers
    private ArrayList<Player> spectatorPlayers = new ArrayList<>();   // specPlayers
    
    // Player state mappings
    private HashMap<Player, String> playerTeams = new HashMap<>();    // teamplayernames
    private HashMap<Player, String> playerKits = new HashMap<>();     // kitplayernames
    private HashMap<Player, String> playerVotes = new HashMap<>();    // voteplayernames

    public void load() {
        if (Settings.arenadata.contains("pg.lobbies")) {
            for (String key : Settings.arenadata.getConfigurationSection("pg.lobbies").getKeys(false)) {
                int lobbyId = Integer.parseInt(key);
                Lobby lobby = new Lobby(lobbyId);
                lobby.load();
                lobbies.add(lobby);
            }
        }
    }

    public Lobby getLobby(int lobbyId) {
        for (Lobby lobby : lobbies) {
            if (lobby.getId() == lobbyId) {
                return lobby;
            }
        }
        return null;
    }

    public Lobby getLobbyByPlayer(Player p) {
        for (Lobby lobby : lobbies) {
            for (Participant part : lobby.getParticipants()) {
                if (part.getPlayer().equals(p)) {
                    return lobby;
                }
            }
        }
        return null;
    }

    public boolean addLobby(int lobbyId, Location location) {
        Lobby lobby = new Lobby(lobbyId);
        boolean success = lobby.add(location);
        if (success) {
            lobbies.add(lobby);
        }
        return success;
    }

    public boolean removeLobby(int lobbyId) {
        Lobby lobby = getLobby(lobbyId);
        if (lobby != null) {
            boolean success = lobby.remove();
            if (success) {
                lobbies.remove(lobby);
            }
            return success;
        }
        return false;
    }

    public ArrayList<Lobby> getLobbies() {
        return lobbies;
    }

    // ===== Player Management (Migrated from PotionGames) =====
    
    /**
     * Get list of active game players
     */
    public ArrayList<Player> getActivePlayers() {
        return activePlayers;
    }
    
    /**
     * Get list of spectator players
     */
    public ArrayList<Player> getSpectatorPlayers() {
        return spectatorPlayers;
    }
    
    /**
     * Add a player to the active players list
     */
    public void addActivePlayer(Player player) {
        if (!activePlayers.contains(player)) {
            activePlayers.add(player);
        }
    }
    
    /**
     * Add a player to the spectator list
     */
    public void addSpectatorPlayer(Player player) {
        if (!spectatorPlayers.contains(player)) {
            spectatorPlayers.add(player);
        }
    }
    
    /**
     * Remove a player from active players
     */
    public void removeActivePlayer(Player player) {
        activePlayers.remove(player);
    }
    
    /**
     * Remove a player from spectators
     */
    public void removeSpectatorPlayer(Player player) {
        spectatorPlayers.remove(player);
    }
    
    /**
     * Get count of active players
     */
    public int getActivePlayerCount() {
        return activePlayers.size();
    }
    
    /**
     * Get count of spectator players
     */
    public int getSpectatorPlayerCount() {
        return spectatorPlayers.size();
    }
    
    /**
     * Check if player is active
     */
    public boolean isActivePlayer(Player player) {
        return activePlayers.contains(player);
    }
    
    /**
     * Check if player is spectator
     */
    public boolean isSpectatorPlayer(Player player) {
        return spectatorPlayers.contains(player);
    }
    
    /**
     * Clear all players (useful for game reset)
     */
    public void clearAllPlayers() {
        activePlayers.clear();
        spectatorPlayers.clear();
        playerTeams.clear();
        playerKits.clear();
        playerVotes.clear();
    }
    
    // ===== Team Management =====
    
    /**
     * Set player's team
     */
    public void setPlayerTeam(Player player, String team) {
        playerTeams.put(player, team);
    }
    
    /**
     * Get player's team
     */
    public String getPlayerTeam(Player player) {
        return playerTeams.get(player);
    }
    
    /**
     * Remove player from team
     */
    public void removePlayerTeam(Player player) {
        playerTeams.remove(player);
    }
    
    /**
     * Check if player has team assigned
     */
    public boolean hasTeam(Player player) {
        return playerTeams.containsKey(player);
    }
    
    // ===== Kit Management =====
    
    /**
     * Set player's kit
     */
    public void setPlayerKit(Player player, String kit) {
        playerKits.put(player, kit);
    }
    
    /**
     * Get player's kit
     */
    public String getPlayerKit(Player player) {
        return playerKits.get(player);
    }
    
    /**
     * Remove player from kit
     */
    public void removePlayerKit(Player player) {
        playerKits.remove(player);
    }
    
    /**
     * Check if player has kit assigned
     */
    public boolean hasKit(Player player) {
        return playerKits.containsKey(player);
    }
    
    // ===== Vote Management =====
    
    /**
     * Set player's vote
     */
    public void setPlayerVote(Player player, String vote) {
        playerVotes.put(player, vote);
    }
    
    /**
     * Get player's vote
     */
    public String getPlayerVote(Player player) {
        return playerVotes.get(player);
    }
    
    /**
     * Remove player's vote
     */
    public void removePlayerVote(Player player) {
        playerVotes.remove(player);
    }
    
    /**
     * Check if player has voted
     */
    public boolean hasVoted(Player player) {
        return playerVotes.containsKey(player);
    }
}
