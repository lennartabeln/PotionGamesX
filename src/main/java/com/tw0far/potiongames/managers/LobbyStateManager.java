package com.tw0far.potiongames.managers;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.GameStates;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of ILobbyStateManager.
 * Consolidates 21 HashMaps from PotionGames into a single manager.
 * 
 * Reduces coupling and makes lobby state management centralized and testable.
 */
public class LobbyStateManager implements ILobbyStateManager {
    private final PotionGames plugin;
    
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
    private final Map<String, Integer> lobbyteamSize = new HashMap<>();
    private final Map<String, Integer> lobbymaxPlayers = new HashMap<>();
    private final Map<String, Integer> lobbyminPlayers = new HashMap<>();
    private final Map<String, Integer> lobbyteamAmount = new HashMap<>();
    private final Map<String, Integer> lobbyroundTime = new HashMap<>();
    private final Map<String, Integer> lobbyroundTimeSeconds = new HashMap<>();
    
    public LobbyStateManager(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void onEnable() {
        // Initialize lobbies from config if needed
    }
    
    @Override
    public void onDisable() {
        clearAllLobbies();
    }
    
    @Override
    public void reload() {
        // Re-load lobby state from config if persistent
    }
    
    // ===== Countdown & Reset =====
    @Override
    public Integer getCountdown(String lobbyId) {
        return countdownLobby.getOrDefault(lobbyId, plugin.getConfigManager().getCountdown());
    }
    
    @Override
    public void setCountdown(String lobbyId, Integer value) {
        countdownLobby.put(lobbyId, value);
    }
    
    @Override
    public Integer getResetTime(String lobbyId) {
        return resetLobby.getOrDefault(lobbyId, 10);
    }
    
    @Override
    public void setResetTime(String lobbyId, Integer value) {
        resetLobby.put(lobbyId, value);
    }
    
    // ===== Lobby Info =====
    @Override
    public Integer getLobbyAmount(String lobbyId) {
        return lobbyAmount.getOrDefault(lobbyId, 0);
    }
    
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
    public Boolean isDeathmatchEnabled(String lobbyId) {
        return lobbyDeathmatch.getOrDefault(lobbyId, false);
    }
    
    @Override
    public void setDeathmatchEnabled(String lobbyId, Boolean value) {
        lobbyDeathmatch.put(lobbyId, value);
    }
    
    @Override
    public Boolean isMoveAllowed(String lobbyId) {
        return lobbyMove.getOrDefault(lobbyId, true);
    }
    
    @Override
    public void setMoveAllowed(String lobbyId, Boolean value) {
        lobbyMove.put(lobbyId, value);
    }
    
    @Override
    public Boolean isJoinable(String lobbyId) {
        return lobbyJoinable.getOrDefault(lobbyId, true);
    }
    
    @Override
    public void setJoinable(String lobbyId, Boolean value) {
        lobbyJoinable.put(lobbyId, value);
    }
    
    @Override
    public Boolean isForcearena(String lobbyId) {
        return lobbyForcearena.getOrDefault(lobbyId, false);
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
    public Boolean isTickstarted(String lobbyId) {
        return lobbyTickstarted.getOrDefault(lobbyId, false);
    }
    
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
    public Boolean isActivateKits(String lobbyId) {
        return lobbyActivateKits.getOrDefault(lobbyId, true);
    }
    
    @Override
    public void setActivateKits(String lobbyId, Boolean value) {
        lobbyActivateKits.put(lobbyId, value);
    }
    
    @Override
    public Boolean isActivateShop(String lobbyId) {
        return lobbyActivateShop.getOrDefault(lobbyId, plugin.getConfigManager().isActivateShop());
    }
    
    @Override
    public void setActivateShop(String lobbyId, Boolean value) {
        lobbyActivateShop.put(lobbyId, value);
    }
    
    @Override
    public Boolean isActivateAirdrops(String lobbyId) {
        return lobbyActivateAirdrops.getOrDefault(lobbyId, false);
    }
    
    @Override
    public void setActivateAirdrops(String lobbyId, Boolean value) {
        lobbyActivateAirdrops.put(lobbyId, value);
    }
    
    @Override
    public Boolean isCheckArenas(String lobbyId) {
        return lobbyCheckArenas.getOrDefault(lobbyId, false);
    }
    
    @Override
    public void setCheckArenas(String lobbyId, Boolean value) {
        lobbyCheckArenas.put(lobbyId, value);
    }
    
    @Override
    public Boolean isSingleArena(String lobbyId) {
        return lobbySingleArena.getOrDefault(lobbyId, false);
    }
    
    @Override
    public void setSingleArena(String lobbyId, Boolean value) {
        lobbySingleArena.put(lobbyId, value);
    }
    
    // ===== Voting State =====
    @Override
    public String getCurrentVote(String lobbyId) {
        return lobbyVote.get(lobbyId);
    }
    
    @Override
    public void setCurrentVote(String lobbyId, String votedArena) {
        lobbyVote.put(lobbyId, votedArena);
    }
    
    @Override
    public String getVotedArena(String lobbyId) {
        return lobbyVotedarena.get(lobbyId);
    }
    
    @Override
    public void setVotedArena(String lobbyId, String votedArena) {
        lobbyVotedarena.put(lobbyId, votedArena);
    }
    
    // ===== Lobby Settings =====
    @Override
    public Integer getTeamSize(String lobbyId) {
        return lobbyteamSize.getOrDefault(lobbyId, plugin.getConfigManager().getTeamSize());
    }
    
    @Override
    public void setTeamSize(String lobbyId, Integer value) {
        lobbyteamSize.put(lobbyId, value);
    }
    
    @Override
    public Integer getMaxPlayers(String lobbyId) {
        return lobbymaxPlayers.getOrDefault(lobbyId, plugin.getConfigManager().getMaxPlayers());
    }
    
    @Override
    public void setMaxPlayers(String lobbyId, Integer value) {
        lobbymaxPlayers.put(lobbyId, value);
    }
    
    @Override
    public Integer getMinPlayers(String lobbyId) {
        return lobbyminPlayers.getOrDefault(lobbyId, plugin.getConfigManager().getMinPlayers());
    }
    
    @Override
    public void setMinPlayers(String lobbyId, Integer value) {
        lobbyminPlayers.put(lobbyId, value);
    }
    
    @Override
    public Integer getTeamAmount(String lobbyId) {
        return lobbyteamAmount.getOrDefault(lobbyId, plugin.getConfigManager().getTeamAmount());
    }
    
    @Override
    public void setTeamAmount(String lobbyId, Integer value) {
        lobbyteamAmount.put(lobbyId, value);
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
    public Integer getRoundTimeSeconds(String lobbyId) {
        return lobbyroundTimeSeconds.getOrDefault(lobbyId, 1800);
    }
    
    @Override
    public void setRoundTimeSeconds(String lobbyId, Integer value) {
        lobbyroundTimeSeconds.put(lobbyId, value);
    }
    
    // ===== Batch Operations =====
    @Override
    public void initializeLobby(String lobbyId) {
        // Set up default values for a new lobby
        if (!countdownLobby.containsKey(lobbyId)) {
            countdownLobby.put(lobbyId, plugin.getConfigManager().getCountdown());
        }
        if (!resetLobby.containsKey(lobbyId)) {
            resetLobby.put(lobbyId, 10);
        }
        if (!lobbyAmount.containsKey(lobbyId)) {
            lobbyAmount.put(lobbyId, 0);
        }
        if (!lobbyStates.containsKey(lobbyId)) {
            lobbyStates.put(lobbyId, GameStates.WAITING);
        }
        if (!lobbyJoinable.containsKey(lobbyId)) {
            lobbyJoinable.put(lobbyId, true);
        }
        if (!lobbyMove.containsKey(lobbyId)) {
            lobbyMove.put(lobbyId, true);
        }
        if (!lobbymaxPlayers.containsKey(lobbyId)) {
            lobbymaxPlayers.put(lobbyId, plugin.getConfigManager().getMaxPlayers());
        }
        if (!lobbyminPlayers.containsKey(lobbyId)) {
            lobbyminPlayers.put(lobbyId, plugin.getConfigManager().getMinPlayers());
        }
    }
    
    @Override
    public void clearLobby(String lobbyId) {
        countdownLobby.remove(lobbyId);
        resetLobby.remove(lobbyId);
        lobbyAmount.remove(lobbyId);
        lobbyStates.remove(lobbyId);
        lobbyDeathmatch.remove(lobbyId);
        lobbyMove.remove(lobbyId);
        lobbyJoinable.remove(lobbyId);
        lobbyForcearena.remove(lobbyId);
        lobbyVoteallowed.remove(lobbyId);
        lobbyTeamallowed.remove(lobbyId);
        lobbyKitallowed.remove(lobbyId);
        lobbyTickstarted.remove(lobbyId);
        lobbyBuild.remove(lobbyId);
        lobbyPause.remove(lobbyId);
        lobbyActivateTeams.remove(lobbyId);
        lobbyActivateKits.remove(lobbyId);
        lobbyActivateShop.remove(lobbyId);
        lobbyActivateAirdrops.remove(lobbyId);
        lobbyCheckArenas.remove(lobbyId);
        lobbySingleArena.remove(lobbyId);
        lobbyVote.remove(lobbyId);
        lobbyVotedarena.remove(lobbyId);
        lobbyteamSize.remove(lobbyId);
        lobbymaxPlayers.remove(lobbyId);
        lobbyminPlayers.remove(lobbyId);
        lobbyteamAmount.remove(lobbyId);
        lobbyroundTime.remove(lobbyId);
        lobbyroundTimeSeconds.remove(lobbyId);
    }
    
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
        lobbyteamSize.clear();
        lobbymaxPlayers.clear();
        lobbyminPlayers.clear();
        lobbyteamAmount.clear();
        lobbyroundTime.clear();
        lobbyroundTimeSeconds.clear();
    }
}
