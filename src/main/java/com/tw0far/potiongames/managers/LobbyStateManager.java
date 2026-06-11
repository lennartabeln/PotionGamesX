package com.tw0far.potiongames.managers;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.GameStates;

import java.util.HashMap;
import java.util.Map;

public class LobbyStateManager implements ILobbyStateManager {
    private final PotionGamesX plugin;

    // Countdown & Reset
    private final Map<String, Integer> countdownLobby = new HashMap<>();
    private final Map<String, Integer> resetLobby = new HashMap<>();

    // Lobby Info
    private final Map<String, Integer> lobbyAmount = new HashMap<>();

    // Game State
    private final Map<String, GameStates> lobbyStates = new HashMap<>();

    // Feature Flags
    private final Map<String, Boolean> lobbyDeathmatch = new HashMap<>();
    private final Map<String, Boolean> lobbyMove = new HashMap<>();
    private final Map<String, Boolean> lobbyJoinable = new HashMap<>();
    private final Map<String, Boolean> lobbyForcearena = new HashMap<>();
    private final Map<String, Boolean> lobbyVoteallowed = new HashMap<>();
    private final Map<String, Boolean> lobbyTeamallowed = new HashMap<>();
    private final Map<String, Boolean> lobbyKitallowed = new HashMap<>();

    // Active State
    private final Map<String, Boolean> lobbyTickstarted = new HashMap<>();
    private final Map<String, Boolean> lobbyBuild = new HashMap<>();
    private final Map<String, Boolean> lobbyPause = new HashMap<>();

    // Activation Features
    private final Map<String, Boolean> lobbyActivateTeams = new HashMap<>();
    private final Map<String, Boolean> lobbyActivateKits = new HashMap<>();
    private final Map<String, Boolean> lobbyActivateShop = new HashMap<>();
    private final Map<String, Boolean> lobbyActivateAirdrops = new HashMap<>();
    private final Map<String, Boolean> lobbyCheckArenas = new HashMap<>();
    private final Map<String, Boolean> lobbySingleArena = new HashMap<>();

    // Voting State
    private final Map<String, String> lobbyVote = new HashMap<>();
    private final Map<String, String> lobbyVotedarena = new HashMap<>();

    // Lobby Settings
    private final Map<String, Integer> lobbymaxPlayers = new HashMap<>();
    private final Map<String, Integer> lobbyminPlayers = new HashMap<>();
    private final Map<String, Integer> lobbyroundTime = new HashMap<>();
    private final Map<String, Integer> lobbyroundTimeSeconds = new HashMap<>();

    public LobbyStateManager(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
        clearAllLobbies();
    }

    // ===== Countdown & Reset =====
    @Override
    public void setCountdown(String lobbyId, Integer value) {
        countdownLobby.put(lobbyId, value);
    }

    // ===== Lobby Info =====
    @Override
    public void setLobbyAmount(String lobbyId, Integer value) {
        lobbyAmount.put(lobbyId, value);
    }

    // ===== Game State =====
    @Override
    public GameStates getGameState(String lobbyId) {
        return lobbyStates.getOrDefault(lobbyId, GameStates.WAITING);
    }

    @Override
    public void setGameState(String lobbyId, GameStates state) {
        lobbyStates.put(lobbyId, state);
    }

    // ===== Feature Flags =====
    @Override
    public void setDeathmatchEnabled(String lobbyId, Boolean value) {
        lobbyDeathmatch.put(lobbyId, value);
    }

    @Override
    public void setMoveAllowed(String lobbyId, Boolean value) {
        lobbyMove.put(lobbyId, value);
    }

    @Override
    public void setJoinable(String lobbyId, Boolean value) {
        lobbyJoinable.put(lobbyId, value);
    }

    @Override
    public void setForcearena(String lobbyId, Boolean value) {
        lobbyForcearena.put(lobbyId, value);
    }

    @Override
    public Boolean isVoteallowed(String lobbyId) {
        return lobbyVoteallowed.getOrDefault(lobbyId, false);
    }

    @Override
    public void setVoteallowed(String lobbyId, Boolean value) {
        lobbyVoteallowed.put(lobbyId, value);
    }

    @Override
    public Boolean isTeamallowed(String lobbyId) {
        return lobbyTeamallowed.getOrDefault(lobbyId, false);
    }

    @Override
    public void setTeamallowed(String lobbyId, Boolean value) {
        lobbyTeamallowed.put(lobbyId, value);
    }

    @Override
    public Boolean isKitallowed(String lobbyId) {
        return lobbyKitallowed.getOrDefault(lobbyId, false);
    }

    @Override
    public void setKitallowed(String lobbyId, Boolean value) {
        lobbyKitallowed.put(lobbyId, value);
    }

    // ===== Active State =====
    @Override
    public void setTickstarted(String lobbyId, Boolean value) {
        lobbyTickstarted.put(lobbyId, value);
    }

    @Override
    public Boolean isBuildAllowed(String lobbyId) {
        return lobbyBuild.getOrDefault(lobbyId, false);
    }

    @Override
    public void setBuildAllowed(String lobbyId, Boolean value) {
        lobbyBuild.put(lobbyId, value);
    }

    @Override
    public Boolean isPaused(String lobbyId) {
        return lobbyPause.getOrDefault(lobbyId, false);
    }

    @Override
    public void setPaused(String lobbyId, Boolean value) {
        lobbyPause.put(lobbyId, value);
    }

    // ===== Activation Features =====
    @Override
    public Boolean isActivateTeams(String lobbyId) {
        return lobbyActivateTeams.getOrDefault(lobbyId, plugin.getConfigManager().isActivateTeams());
    }

    @Override
    public void setActivateTeams(String lobbyId, Boolean value) {
        lobbyActivateTeams.put(lobbyId, value);
    }

    @Override
    public void setActivateKits(String lobbyId, Boolean value) {
        lobbyActivateKits.put(lobbyId, value);
    }

    @Override
    public void setActivateShop(String lobbyId, Boolean value) {
        lobbyActivateShop.put(lobbyId, value);
    }

    @Override
    public void setActivateAirdrops(String lobbyId, Boolean value) {
        lobbyActivateAirdrops.put(lobbyId, value);
    }

    @Override
    public void setCheckArenas(String lobbyId, Boolean value) {
        lobbyCheckArenas.put(lobbyId, value);
    }

    @Override
    public Boolean isSingleArena(String lobbyId) {
        return lobbySingleArena.getOrDefault(lobbyId, false);
    }

    // ===== Voting State =====
    @Override
    public void setCurrentVote(String lobbyId, String votedArena) {
        lobbyVote.put(lobbyId, votedArena);
    }

    @Override
    public void setVotedArena(String lobbyId, String votedArena) {
        lobbyVotedarena.put(lobbyId, votedArena);
    }

    // ===== Lobby Settings =====
    @Override
    public void setMaxPlayers(String lobbyId, Integer value) {
        lobbymaxPlayers.put(lobbyId, value);
    }

    @Override
    public void setMinPlayers(String lobbyId, Integer value) {
        lobbyminPlayers.put(lobbyId, value);
    }

    @Override
    public Integer getRoundTime(String lobbyId) {
        return lobbyroundTime.getOrDefault(lobbyId, 30);
    }

    @Override
    public void setRoundTime(String lobbyId, Integer value) {
        lobbyroundTime.put(lobbyId, value);
    }

    @Override
    public void setRoundTimeSeconds(String lobbyId, Integer value) {
        lobbyroundTimeSeconds.put(lobbyId, value);
    }

    // ===== Batch Operations =====
    @Override
    public void clearAllLobbies() {
        countdownLobby.clear();
        resetLobby.clear();
        lobbyAmount.clear();
        lobbyStates.clear();
        lobbyDeathmatch.clear();
        lobbyMove.clear();
        lobbyJoinable.clear();
        lobbyForcearena.clear();
        lobbyVoteallowed.clear();
        lobbyTeamallowed.clear();
        lobbyKitallowed.clear();
        lobbyTickstarted.clear();
        lobbyBuild.clear();
        lobbyPause.clear();
        lobbyActivateTeams.clear();
        lobbyActivateKits.clear();
        lobbyActivateShop.clear();
        lobbyActivateAirdrops.clear();
        lobbyCheckArenas.clear();
        lobbySingleArena.clear();
        lobbyVote.clear();
        lobbyVotedarena.clear();
        lobbymaxPlayers.clear();
        lobbyminPlayers.clear();
        lobbyroundTime.clear();
        lobbyroundTimeSeconds.clear();
    }
}
