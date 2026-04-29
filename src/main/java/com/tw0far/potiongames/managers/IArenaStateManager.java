package com.tw0far.potiongames.managers;

import org.bukkit.entity.Player;
import java.util.*;

/**
 * Manager for arena/voting state tracking.
 * Consolidates 9 HashMaps for arena voting and selection from PotionGames.
 * 
 * Manages:
 * - Arena list (arenas)
 * - Vote tracking (votes, voteplayernames, lobbyvotes, lobbyvoteplayernames)
 * - Voted players (voted, lobbyvoted, lobbyVoted)
 */
public interface IArenaStateManager extends IManager {
    
    // ===== ARENA TRACKING =====
    /**
     * Get list of all available arenas
     */
    Collection<String> getArenas();
    
    /**
     * Add an arena to the pool
     */
    void addArena(String arenaName);
    
    /**
     * Remove an arena from the pool
     */
    void removeArena(String arenaName);
    
    /**
     * Check if arena exists
     */
    boolean arenaExists(String arenaName);
    
    /**
     * Get arena count
     */
    int getArenaCount();
    
    // ===== VOTING TRACKING (Global) =====
    /**
     * Get vote count for an arena (global)
     */
    Integer getVoteCount(String arenaName);
    
    /**
     * Increment votes for an arena (global)
     */
    void addVote(String arenaName);
    
    /**
     * Decrement votes for an arena (global)
     */
    void removeVote(String arenaName);
    
    /**
     * Reset all votes for global arena (global)
     */
    void resetVotes();
    
    /**
     * Get arena with most votes (global)
     */
    String getWinningArena();
    
    // ===== PLAYER VOTING (Global) =====
    /**
     * Record that player voted for arena (global)
     */
    void recordPlayerVote(Player player, String arenaName);
    
    /**
     * Get which arena player voted for (global)
     */
    String getPlayerVote(Player player);
    
    /**
     * Mark player as having voted (global)
     */
    void markPlayerVoted(Player player, String arenaName);
    
    /**
     * Check if player voted (global)
     */
    boolean hasPlayerVoted(Player player);
    
    /**
     * Clear player votes (global)
     */
    void clearPlayerVotes();
    
    // ===== LOBBY-SPECIFIC VOTING =====
    /**
     * Get vote count for arena in specific lobby
     */
    Integer getLobbyVoteCount(String lobbyId, String arenaName);
    
    /**
     * Add vote to arena in lobby
     */
    void addLobbyVote(String lobbyId, String arenaName);
    
    /**
     * Remove vote from arena in lobby
     */
    void removeLobbyVote(String lobbyId, String arenaName);
    
    /**
     * Get winning arena for a lobby
     */
    String getWinningArenaForLobby(String lobbyId);
    
    /**
     * Record player vote in lobby
     */
    void recordPlayerVoteInLobby(String lobbyId, Player player, String arenaName);
    
    /**
     * Get player's vote in lobby
     */
    String getPlayerVoteInLobby(String lobbyId, Player player);
    
    /**
     * Check if player voted in lobby
     */
    boolean hasPlayerVotedInLobby(String lobbyId, Player player);
    
    /**
     * Mark player as voted in lobby
     */
    void markPlayerVotedInLobby(String lobbyId, Player player);
    
    /**
     * Clear votes for a lobby
     */
    void clearLobbyVotes(String lobbyId);
    
    /**
     * Clear all lobby voting
     */
    void clearAllLobbyVotes();
    
    // ===== BATCH OPERATIONS =====
    /**
     * Clear all arena state
     */
    void clearAll();
}
