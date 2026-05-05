package com.tw0far.potiongames.managers;

import org.bukkit.entity.Player;
import java.util.*;

/**
 * Implementation of IArenaStateManager.
 * Consolidates 9 HashMaps for arena voting from PotionGames:
 * - arenas (list)
 * - votes, voteplayernames (global voting)
 * - voted (marked voters)
 * - lobbyvotes, lobbyvoteplayernames (per-lobby voting)
 * - lobbyvoted, lobbyVoted (per-lobby marked voters)
 */
public class ArenaStateManager implements IArenaStateManager {
    
    // Arena list
    private final Set<String> arenas = new HashSet<>();
    
    // Global voting
    private final Map<String, Integer> votes = new HashMap<>();
    private final Map<Player, String> voteplayernames = new HashMap<>();
    private final Set<String> voted = new HashSet<>();  // Player names that voted
    
    // Per-lobby voting
    private final Map<String, Map<String, Integer>> lobbyvotes = new HashMap<>();
    private final Map<String, Map<Player, String>> lobbyvoteplayernames = new HashMap<>();
    private final Map<Player, String> lobbyvoted = new HashMap<>();  // Player -> lobbyId
    private final Map<Player, String> lobbyVoted = new HashMap<>();  // Player -> voted arena
    
    // Per-lobby team state
    private final Map<String, Map<Integer, Integer>> lobbyteams = new HashMap<>();  // lobbyId -> (teamId -> playerCount)
    private final Map<String, Map<Player, String>> lobbyteamplayernames = new HashMap<>();  // lobbyId -> (player -> teamId)
    private final Map<Player, String> lobbyTeamed = new HashMap<>();  // player -> lobbyId
    private final Map<String, Integer> lobbyteamSize = new HashMap<>();  // lobbyId -> maxTeamSize
    private final Map<String, Integer> lobbyteamAmount = new HashMap<>();  // lobbyId -> numberOfTeams
    
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
    
    // ===== ARENA TRACKING =====
    @Override
    public Collection<String> getArenas() {
        return new HashSet<>(arenas);
    }
    
    @Override
    public void addArena(String arenaName) {
        arenas.add(arenaName);
        if (!votes.containsKey(arenaName)) {
            votes.put(arenaName, 0);
        }
    }
    
    @Override
    public void removeArena(String arenaName) {
        arenas.remove(arenaName);
        votes.remove(arenaName);
    }
    
    @Override
    public boolean arenaExists(String arenaName) {
        return arenas.contains(arenaName);
    }
    
    @Override
    public int getArenaCount() {
        return arenas.size();
    }
    
    // ===== VOTING TRACKING (Global) =====
    @Override
    public Integer getVoteCount(String arenaName) {
        return votes.getOrDefault(arenaName, 0);
    }
    
    @Override
    public void addVote(String arenaName) {
        votes.put(arenaName, getVoteCount(arenaName) + 1);
    }
    
    @Override
    public void removeVote(String arenaName) {
        Integer current = votes.getOrDefault(arenaName, 0);
        if (current > 0) {
            votes.put(arenaName, current - 1);
        }
    }
    
    @Override
    public void resetVotes() {
        for (String arena : arenas) {
            votes.put(arena, 0);
        }
        voteplayernames.clear();
    }
    
    @Override
    public String getWinningArena() {
        String winner = null;
        int maxVotes = 0;
        for (Map.Entry<String, Integer> entry : votes.entrySet()) {
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                winner = entry.getKey();
            }
        }
        return winner;
    }
    
    // ===== PLAYER VOTING (Global) =====
    @Override
    public void recordPlayerVote(Player player, String arenaName) {
        voteplayernames.put(player, arenaName);
        voted.add(player.getName());
    }
    
    @Override
    public String getPlayerVote(Player player) {
        return voteplayernames.get(player);
    }
    
    @Override
    public void markPlayerVoted(Player player, String arenaName) {
        voted.add(player.getName());
    }
    
    @Override
    public boolean hasPlayerVoted(Player player) {
        return voted.contains(player.getName()) || voteplayernames.containsKey(player);
    }
    
    @Override
    public void clearPlayerVotes() {
        voteplayernames.clear();
        voted.clear();
    }
    
    // ===== LOBBY-SPECIFIC VOTING =====
    @Override
    public Integer getLobbyVoteCount(String lobbyId, String arenaName) {
        Map<String, Integer> lobbyVotes = lobbyvotes.get(lobbyId);
        if (lobbyVotes == null) {
            return 0;
        }
        return lobbyVotes.getOrDefault(arenaName, 0);
    }
    
    @Override
    public void addLobbyVote(String lobbyId, String arenaName) {
        Map<String, Integer> lobbyVotes = lobbyvotes.computeIfAbsent(lobbyId, k -> new HashMap<>());
        Integer current = lobbyVotes.getOrDefault(arenaName, 0);
        lobbyVotes.put(arenaName, current + 1);
    }
    
    @Override
    public void removeLobbyVote(String lobbyId, String arenaName) {
        Map<String, Integer> lobbyVotes = lobbyvotes.get(lobbyId);
        if (lobbyVotes != null) {
            Integer current = lobbyVotes.getOrDefault(arenaName, 0);
            if (current > 0) {
                lobbyVotes.put(arenaName, current - 1);
            }
        }
    }
    
    @Override
    public String getWinningArenaForLobby(String lobbyId) {
        Map<String, Integer> lobbyVotes = lobbyvotes.get(lobbyId);
        if (lobbyVotes == null || lobbyVotes.isEmpty()) {
            return null;
        }
        
        String winner = null;
        int maxVotes = 0;
        for (Map.Entry<String, Integer> entry : lobbyVotes.entrySet()) {
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                winner = entry.getKey();
            }
        }
        return winner;
    }
    
    @Override
    public void recordPlayerVoteInLobby(String lobbyId, Player player, String arenaName) {
        Map<Player, String> lobbyPlayerVotes = lobbyvoteplayernames.computeIfAbsent(lobbyId, k -> new HashMap<>());
        lobbyPlayerVotes.put(player, arenaName);
        lobbyvoted.put(player, lobbyId);
    }
    
    @Override
    public String getPlayerVoteInLobby(String lobbyId, Player player) {
        Map<Player, String> lobbyPlayerVotes = lobbyvoteplayernames.get(lobbyId);
        if (lobbyPlayerVotes == null) {
            return null;
        }
        return lobbyPlayerVotes.get(player);
    }
    
    @Override
    public boolean hasPlayerVotedInLobby(String lobbyId, Player player) {
        Map<Player, String> lobbyPlayerVotes = lobbyvoteplayernames.get(lobbyId);
        if (lobbyPlayerVotes == null) {
            return false;
        }
        return lobbyPlayerVotes.containsKey(player);
    }
    
    @Override
    public void markPlayerVotedInLobby(String lobbyId, Player player) {
        lobbyvoted.put(player, lobbyId);
    }
    
    @Override
    public void clearLobbyVotes(String lobbyId) {
        lobbyvotes.remove(lobbyId);
        lobbyvoteplayernames.remove(lobbyId);
        lobbyvoted.entrySet().removeIf(entry -> entry.getValue().equals(lobbyId));
        lobbyVoted.entrySet().removeIf(entry -> {
            // Remove if this was this lobby's vote
            String playersLobby = lobbyvoted.get(entry.getKey());
            return playersLobby != null && playersLobby.equals(lobbyId);
        });
    }
    
    @Override
    public void clearAllLobbyVotes() {
        lobbyvotes.clear();
        lobbyvoteplayernames.clear();
        lobbyvoted.clear();
        lobbyVoted.clear();
    }
    
    // ===== LOBBY TEAM MANAGEMENT =====
    @Override
    public Integer getLobbyTeamPlayerCount(String lobbyId, Integer teamId) {
        Map<Integer, Integer> teams = lobbyteams.get(lobbyId);
        if (teams == null) {
            return 0;
        }
        return teams.getOrDefault(teamId, 0);
    }
    
    @Override
    public void incrementLobbyTeamPlayers(String lobbyId, Integer teamId) {
        Map<Integer, Integer> teams = lobbyteams.computeIfAbsent(lobbyId, k -> new HashMap<>());
        Integer current = teams.getOrDefault(teamId, 0);
        teams.put(teamId, current + 1);
    }
    
    @Override
    public void decrementLobbyTeamPlayers(String lobbyId, Integer teamId) {
        Map<Integer, Integer> teams = lobbyteams.get(lobbyId);
        if (teams != null) {
            Integer current = teams.getOrDefault(teamId, 0);
            if (current > 0) {
                teams.put(teamId, current - 1);
            }
        }
    }
    
    @Override
    public void recordPlayerTeamInLobby(String lobbyId, Player player, String teamId) {
        Map<Player, String> teamPlayers = lobbyteamplayernames.computeIfAbsent(lobbyId, k -> new HashMap<>());
        teamPlayers.put(player, teamId);
        lobbyTeamed.put(player, lobbyId);
    }
    
    @Override
    public String getPlayerTeamInLobby(String lobbyId, Player player) {
        Map<Player, String> teamPlayers = lobbyteamplayernames.get(lobbyId);
        if (teamPlayers == null) {
            return null;
        }
        return teamPlayers.get(player);
    }
    
    @Override
    public void removePlayerTeamInLobby(String lobbyId, Player player) {
        Map<Player, String> teamPlayers = lobbyteamplayernames.get(lobbyId);
        if (teamPlayers != null) {
            teamPlayers.remove(player);
        }
        lobbyTeamed.remove(player);
    }
    
    @Override
    public boolean hasPlayerTeamInLobby(String lobbyId, Player player) {
        Map<Player, String> teamPlayers = lobbyteamplayernames.get(lobbyId);
        if (teamPlayers == null) {
            return false;
        }
        return teamPlayers.containsKey(player);
    }
    
    @Override
    public Map<Integer, Integer> getLobbyTeams(String lobbyId) {
        return lobbyteams.getOrDefault(lobbyId, new HashMap<>());
    }
    
    @Override
    public void initializeLobbyTeams(String lobbyId, Integer teamCount) {
        Map<Integer, Integer> teams = lobbyteams.computeIfAbsent(lobbyId, k -> new HashMap<>());
        teams.clear();
        for (int i = 1; i <= teamCount; i++) {
            teams.put(i, 0);
        }
        lobbyteamAmount.put(lobbyId, teamCount);
    }
    
    @Override
    public void setLobbyTeamSize(String lobbyId, Integer teamSize) {
        lobbyteamSize.put(lobbyId, teamSize);
    }
    
    @Override
    public Integer getLobbyTeamSize(String lobbyId) {
        return lobbyteamSize.getOrDefault(lobbyId, 1);
    }
    
    @Override
    public Integer getLobbyTeamAmount(String lobbyId) {
        return lobbyteamAmount.getOrDefault(lobbyId, 1);
    }
    
    @Override
    public void clearLobbyTeams(String lobbyId) {
        lobbyteams.remove(lobbyId);
        lobbyteamplayernames.remove(lobbyId);
        lobbyTeamed.entrySet().removeIf(entry -> entry.getValue().equals(lobbyId));
        lobbyteamSize.remove(lobbyId);
        lobbyteamAmount.remove(lobbyId);
    }
    
    // ===== BATCH OPERATIONS =====
    @Override
    public void clearAll() {
        arenas.clear();
        votes.clear();
        voteplayernames.clear();
        voted.clear();
        clearAllLobbyVotes();
        clearAllLobbyTeams();
    }
    
    public void clearAllLobbyTeams() {
        lobbyteams.clear();
        lobbyteamplayernames.clear();
        lobbyTeamed.clear();
        lobbyteamSize.clear();
        lobbyteamAmount.clear();
    }
}
