package com.tw0far.potiongames.managers;

import com.tw0far.potiongames.models.GameStates;

/**
 * Manager for all per-lobby state tracking.
 * Consolidates 21 HashMap fields that were previously in PotionGames.
 * 
 * Manages:
 * - Lobby metadata (countdownLobby, resetLobby, lobbyAmount)
 * - Game state (lobbyStates, lobbyDeathmatch, lobbyMove, lobbyJoinable)
 * - Permissions (lobbyVoteallowed, lobbyTeamallowed, lobbyKitallowed, lobbyForcearena)
 * - Active state (lobbyTickstarted, lobbyBuild, lobbyPause)
 * - Feature toggles (lobbyActivateTeams, lobbyActivateKits, lobbyActivateShop, lobbyActivateAirdrops, lobbyCheckArenas, lobbySingleArena)
 * - Voting state (lobbyVote, lobbyVotedarena)
 * - Lobby assignments (teamSize, maxPlayers, minPlayers, teamAmount, roundTime, roundTimeSeconds per lobby)
 */
public interface ILobbyStateManager extends IManager {
    
    // Countdown & Reset
    Integer getCountdown(String lobbyId);
    void setCountdown(String lobbyId, Integer value);
    Integer getResetTime(String lobbyId);
    void setResetTime(String lobbyId, Integer value);
    
    // Lobby Info
    Integer getLobbyAmount(String lobbyId);
    void setLobbyAmount(String lobbyId, Integer value);
    
    // Game State
    GameStates getGameState(String lobbyId);
    void setGameState(String lobbyId, GameStates state);
    
    // Feature Flags
    Boolean isDeathmatchEnabled(String lobbyId);
    void setDeathmatchEnabled(String lobbyId, Boolean value);
    Boolean isMoveAllowed(String lobbyId);
    void setMoveAllowed(String lobbyId, Boolean value);
    Boolean isJoinable(String lobbyId);
    void setJoinable(String lobbyId, Boolean value);
    Boolean isForcearena(String lobbyId);
    void setForcearena(String lobbyId, Boolean value);
    Boolean isVoteallowed(String lobbyId);
    void setVoteallowed(String lobbyId, Boolean value);
    Boolean isTeamallowed(String lobbyId);
    void setTeamallowed(String lobbyId, Boolean value);
    Boolean isKitallowed(String lobbyId);
    void setKitallowed(String lobbyId, Boolean value);
    
    // Active State
    Boolean isTickstarted(String lobbyId);
    void setTickstarted(String lobbyId, Boolean value);
    Boolean isBuildAllowed(String lobbyId);
    void setBuildAllowed(String lobbyId, Boolean value);
    Boolean isPaused(String lobbyId);
    void setPaused(String lobbyId, Boolean value);
    
    // Activation Features
    Boolean isActivateTeams(String lobbyId);
    void setActivateTeams(String lobbyId, Boolean value);
    Boolean isActivateKits(String lobbyId);
    void setActivateKits(String lobbyId, Boolean value);
    Boolean isActivateShop(String lobbyId);
    void setActivateShop(String lobbyId, Boolean value);
    Boolean isActivateAirdrops(String lobbyId);
    void setActivateAirdrops(String lobbyId, Boolean value);
    Boolean isCheckArenas(String lobbyId);
    void setCheckArenas(String lobbyId, Boolean value);
    Boolean isSingleArena(String lobbyId);
    void setSingleArena(String lobbyId, Boolean value);
    
    // Voting State
    String getCurrentVote(String lobbyId);
    void setCurrentVote(String lobbyId, String votedArena);
    String getVotedArena(String lobbyId);
    void setVotedArena(String lobbyId, String votedArena);
    
    // Lobby Settings (per-lobby overrides)
    Integer getMaxPlayers(String lobbyId);
    void setMaxPlayers(String lobbyId, Integer value);
    Integer getMinPlayers(String lobbyId);
    void setMinPlayers(String lobbyId, Integer value);
    Integer getRoundTime(String lobbyId);
    void setRoundTime(String lobbyId, Integer value);
    Integer getRoundTimeSeconds(String lobbyId);
    void setRoundTimeSeconds(String lobbyId, Integer value);
    
    // Batch Operations
    void initializeLobby(String lobbyId);
    void clearLobby(String lobbyId);
    void clearAllLobbies();
}
