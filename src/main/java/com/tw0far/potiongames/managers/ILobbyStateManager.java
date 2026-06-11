package com.tw0far.potiongames.managers;

import com.tw0far.potiongames.models.GameStates;

/**
 * Manager for all per-lobby state tracking.
 * Consolidates 21 HashMap fields that were previously in PotionGamesX.
 */
public interface ILobbyStateManager extends IManager {

    // Countdown & Reset
    void setCountdown(String lobbyId, Integer value);

    // Lobby Info
    void setLobbyAmount(String lobbyId, Integer value);

    // Game State
    GameStates getGameState(String lobbyId);
    void setGameState(String lobbyId, GameStates state);

    // Feature Flags
    void setDeathmatchEnabled(String lobbyId, Boolean value);
    void setMoveAllowed(String lobbyId, Boolean value);
    void setJoinable(String lobbyId, Boolean value);
    void setForcearena(String lobbyId, Boolean value);
    Boolean isVoteallowed(String lobbyId);
    void setVoteallowed(String lobbyId, Boolean value);
    Boolean isTeamallowed(String lobbyId);
    void setTeamallowed(String lobbyId, Boolean value);
    Boolean isKitallowed(String lobbyId);
    void setKitallowed(String lobbyId, Boolean value);

    // Active State
    void setTickstarted(String lobbyId, Boolean value);
    Boolean isBuildAllowed(String lobbyId);
    void setBuildAllowed(String lobbyId, Boolean value);
    Boolean isPaused(String lobbyId);
    void setPaused(String lobbyId, Boolean value);

    // Activation Features
    Boolean isActivateTeams(String lobbyId);
    void setActivateTeams(String lobbyId, Boolean value);
    void setActivateKits(String lobbyId, Boolean value);
    void setActivateShop(String lobbyId, Boolean value);
    void setActivateAirdrops(String lobbyId, Boolean value);
    void setCheckArenas(String lobbyId, Boolean value);
    Boolean isSingleArena(String lobbyId);

    // Voting State
    void setCurrentVote(String lobbyId, String votedArena);
    void setVotedArena(String lobbyId, String votedArena);

    // Lobby Settings
    void setMaxPlayers(String lobbyId, Integer value);
    void setMinPlayers(String lobbyId, Integer value);
    Integer getRoundTime(String lobbyId);
    void setRoundTime(String lobbyId, Integer value);
    void setRoundTimeSeconds(String lobbyId, Integer value);

    // Batch Operations
    void clearAllLobbies();
}
