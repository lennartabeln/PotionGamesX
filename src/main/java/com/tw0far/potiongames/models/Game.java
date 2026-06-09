package com.tw0far.potiongames.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.tw0far.potiongames.main.PotionGames;

public class Game {
    private ArrayList<Lobby> lobbies = new ArrayList<>();
    
    // ===== PHASE 7.1: Global Shop & Loot State =====
    // Shop items (single-arena mode)
    private ArrayList<String> shopitem = new ArrayList<>();
    private ArrayList<String> shopkit = new ArrayList<>();
    private ArrayList<Integer> shopcost = new ArrayList<>();
    private ArrayList<Integer> shopsale = new ArrayList<>();
    
    // Loot items
    private ArrayList<ItemStack> food1 = new ArrayList<>();
    private ArrayList<ItemStack> food2 = new ArrayList<>();
    private ArrayList<ItemStack> armour1 = new ArrayList<>();
    private ArrayList<ItemStack> armour2 = new ArrayList<>();
    private ArrayList<ItemStack> armour3 = new ArrayList<>();
    private ArrayList<ItemStack> armour4 = new ArrayList<>();
    private ArrayList<ItemStack> armour5 = new ArrayList<>();
    private ArrayList<ItemStack> weapons1 = new ArrayList<>();
    private ArrayList<ItemStack> weapons2 = new ArrayList<>();
    private ArrayList<PotionEffect> potions = new ArrayList<>();
    
    public void load() {
        lobbies.clear();
        Set<Integer> lobbyIds = new HashSet<>();

        if (Settings.lobbies.contains("pg.lobbies")) {
            for (String key : Settings.lobbies.getConfigurationSection("pg.lobbies").getKeys(false)) {
                try {
                    lobbyIds.add(Integer.parseInt(key));
                } catch (NumberFormatException ignored) {
                }
            }
        }

        if (PotionGames.getInstance() != null) {
            ConfigurationSection lobbySection = PotionGames.getInstance().getConfig().getConfigurationSection("pg.lobbies");
            if (lobbySection != null) {
                for (String key : lobbySection.getKeys(false)) {
                    try {
                        lobbyIds.add(Integer.parseInt(key));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }

        for (Integer lobbyId : lobbyIds) {
            LobbyConfig lobbyConfig = new LobbyConfig(
                lobbyId,
                Settings.lobbies,
                Settings.countdown,
                Settings.maxPlayers,
                Settings.minPlayers,
                Settings.teamSize,
                Settings.roundTime,
                Settings.activateTeams,
                Settings.activateKits,
                Settings.activateShop,
                Settings.activateAirdrops
            );

            Lobby lobby = new Lobby(lobbyId, lobbyConfig);
            lobby.load();
            lobbies.add(lobby);
        }

        if (lobbies.isEmpty()) {
            PotionGames.getInstance().getLogger().info("PotionGames: No lobbies configured.");
        }
    }

    public boolean autoJoinLobby(Player player) {
        if (lobbies.isEmpty()) {
            return false;
        }
        Lobby lobby = lobbies.get(0);
        lobby.addActivePlayer(player);
        return true;
    }
    
    public void registerPlayer(Player player, Lobby lobby) {
        if (player == null || lobby == null) {
            return;
        }

        unregisterPlayer(player);
        lobby.addActivePlayer(player);
    }

    public void unregisterPlayer(Player player) {
        if (player == null) {
            return;
        }

        Lobby currentLobby = getLobbyByPlayer(player);
        if (currentLobby != null) {
            currentLobby.removeActivePlayer(player);
            currentLobby.removeSpectatorPlayer(player);
        }
    }

    public void startGame(Lobby lobby) {
        if (lobby == null) {
            return;
        }

        if (lobby.getCurrentArena() == null) {
            lobby.selectArena(lobby.getRandomArena());
        }
        lobby.startCountdown();
    }

    public void endGame(Lobby lobby) {
        if (lobby == null) {
            return;
        }

        lobby.endRound();
    }

    public void processDeath(Player deadPlayer, Player killer) {
        Lobby lobby = getLobbyByPlayer(deadPlayer);
        if (lobby == null) {
            return;
        }

        lobby.addDeath(deadPlayer);
        lobby.removeActivePlayer(deadPlayer);
        lobby.addSpectatorPlayer(deadPlayer);

        if (killer != null && !killer.equals(deadPlayer)) {
            addKill(killer);
        }

        checkWinCondition(lobby);
    }

    public void addKill(Player player) {
        Lobby lobby = getLobbyByPlayer(player);
        if (lobby != null) {
            lobby.addKill(player);
        }
    }

    public boolean checkWinCondition(Lobby lobby) {
        if (lobby == null) {
            return false;
        }

        int alive = lobby.getActivePlayers().size();
        if (alive <= 1) {
            announceWinner(lobby);
            endGame(lobby);
            return true;
        }

        if (alive == 2 && Settings.activateDeathmatch && !lobby.isDeathmatch()) {
            activateDeathmatch(lobby);
        }

        return false;
    }

    public void activateDeathmatch(Lobby lobby) {
        if (lobby == null || lobby.getCurrentArena() == null) {
            return;
        }
        lobby.setDeathmatch(true);
        for (Player player : new ArrayList<>(lobby.getActivePlayers())) {
            Location spawn = lobby.getCurrentArena().getRandomDeathmatchSpawn();
            if (spawn != null) {
                player.teleport(spawn);
            }
        }
    }

    public void announceWinner(Lobby lobby) {
        if (lobby == null || lobby.getActivePlayers().isEmpty()) {
            return;
        }

        Player winner = lobby.getActivePlayers().get(0);
        String winnerName = winner == null ? "Unknown" : winner.getName();

        for (Participant participant : lobby.getParticipants()) {
            participant.sendMessage(Messages.WinnerHasWonTheGame(winnerName));
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
            if (lobby.containsPlayer(p)) {
                return lobby;
            }
        }
        return null;
    }

    public boolean addLobby(int lobbyId, Location location) {
        // Enforce 1-based lobby IDs. Do not allow lobby 0 or negative IDs.
        if (lobbyId < 1) {
            PotionGames.getInstance().getLogger().warning("PotionGames: Invalid lobby id " + lobbyId + ". Lobby IDs must start at 1.");
            return false;
        }
        // Prevent duplicate lobby IDs
        if (getLobby(lobbyId) != null) {
            PotionGames.getInstance().getLogger().warning("PotionGames: Lobby " + lobbyId + " already exists.");
            return false;
        }

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
        ArrayList<Player> players = new ArrayList<>();
        for (Lobby lobby : lobbies) {
            players.addAll(lobby.getActivePlayers());
        }
        return players;
    }
    
    /**
     * Get list of spectator players
     */
    public ArrayList<Player> getSpectatorPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        for (Lobby lobby : lobbies) {
            players.addAll(lobby.getSpectatorPlayers());
        }
        return players;
    }
    
    /**
     * Add a player to the active players list
     */
    public void addActivePlayer(Player player) {
        Lobby lobby = getLobbyByPlayer(player);
        if (lobby != null) {
            lobby.addActivePlayer(player);
        }
    }
    
    /**
     * Add a player to the spectator list
     */
    public void addSpectatorPlayer(Player player) {
        Lobby lobby = getLobbyByPlayer(player);
        if (lobby != null) {
            lobby.addSpectatorPlayer(player);
        }
    }
    
    /**
     * Remove a player from active players
     */
    public void removeActivePlayer(Player player) {
        Lobby lobby = getLobbyByPlayer(player);
        if (lobby != null) {
            lobby.removeActivePlayer(player);
        }
    }
    
    /**
     * Remove a player from spectators
     */
    public void removeSpectatorPlayer(Player player) {
        Lobby lobby = getLobbyByPlayer(player);
        if (lobby != null) {
            lobby.removeSpectatorPlayer(player);
        }
    }
    
    /**
     * Get count of active players
     */
    public int getActivePlayerCount() {
        int count = 0;
        for (Lobby lobby : lobbies) {
            count += lobby.getActivePlayers().size();
        }
        return count;
    }
    
    /**
     * Get count of spectator players
     */
    /**
     * Check if player is active
     */
    public boolean isActivePlayer(Player player) {
        for (Lobby lobby : lobbies) {
            if (lobby.isActivePlayer(player)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if player is spectator
     */
    public boolean isSpectatorPlayer(Player player) {
        for (Lobby lobby : lobbies) {
            if (lobby.isSpectatorPlayer(player)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Clear all players (useful for game reset)
     */
    public void clearAllPlayers() {
        for (Lobby lobby : lobbies) {
            lobby.getActivePlayers().clear();
            lobby.getSpectatorPlayers().clear();
        }
    }
    
    // ===== Lobby Management (Multi-Lobby Only) =====
    
    /**
     * Set player's lobby (multi-lobby mode)
     */
    public void setPlayerLobby(Player player, String lobbyId) {
        for (Lobby lobby : lobbies) {
            lobby.removeActivePlayer(player);
            lobby.removeSpectatorPlayer(player);
        }
        Lobby lobby = getLobby(Integer.parseInt(lobbyId));
        if (lobby != null) {
            lobby.addActivePlayer(player);
        }
    }
    
    /**
     * Get player's lobby (multi-lobby mode)
     */
    public String getPlayerLobby(Player player) {
        for (Lobby lobby : lobbies) {
            if (lobby.isActivePlayer(player) || lobby.isSpectatorPlayer(player)) {
                return Integer.toString(lobby.getId());
            }
        }
        return null;
    }
    
    /**
     * Remove player from lobby
     */
    public void removePlayerLobby(Player player) {
        for (Lobby lobby : lobbies) {
            lobby.removeActivePlayer(player);
            lobby.removeSpectatorPlayer(player);
        }
    }
    
    /**
     * Check if player is in a lobby
     */
    public boolean isInLobby(Player player) {
        return getPlayerLobby(player) != null;
    }
    
    /**
     * Get all players in a specific lobby
     */
    public ArrayList<Player> getPlayersInLobby(String lobbyId) {
        ArrayList<Player> lobbyPlayers = new ArrayList<>();
        Lobby lobby = getLobby(Integer.parseInt(lobbyId));
        if (lobby != null) {
            lobbyPlayers.addAll(lobby.getActivePlayers());
        }
        return lobbyPlayers;
    }
    
    // ===== Spectator Lobby Management (Multi-Lobby Only) =====
    
    /**
     * Set spectator's lobby (multi-lobby mode)
     */
    public void setSpectatorLobby(Player player, String lobbyId) {
        for (Lobby lobby : lobbies) {
            lobby.removeActivePlayer(player);
            lobby.removeSpectatorPlayer(player);
        }
        Lobby lobby = getLobby(Integer.parseInt(lobbyId));
        if (lobby != null) {
            lobby.addSpectatorPlayer(player);
        }
    }
    
    /**
     * Get spectator's lobby (multi-lobby mode)
     */
    public String getSpectatorLobby(Player player) {
        for (Lobby lobby : lobbies) {
            if (lobby.isSpectatorPlayer(player)) {
                return Integer.toString(lobby.getId());
            }
        }
        return null;
    }
    
    /**
     * Remove spectator from lobby
     */
    public void removeSpectatorLobby(Player player) {
        for (Lobby lobby : lobbies) {
            lobby.removeSpectatorPlayer(player);
        }
    }
    
    /**
     * Check if player is spectating in a lobby
     */
    public boolean isSpectatingInLobby(Player player) {
        return getSpectatorLobby(player) != null;
    }
    
    /**
     * Check if player is in spec lobby (alias for isSpectatingInLobby)
     */
    public boolean isInSpecLobby(Player player) {
        return isSpectatingInLobby(player);
    }
    
    /**
     * Get all spectators in a specific lobby
     */
    public ArrayList<Player> getSpectatorsInLobby(String lobbyId) {
        ArrayList<Player> lobbySpecs = new ArrayList<>();
        Lobby lobby = getLobby(Integer.parseInt(lobbyId));
        if (lobby != null) {
            lobbySpecs.addAll(lobby.getSpectatorPlayers());
        }
        return lobbySpecs;
    }

    /**
     * Clear all shop items
     */
    public void clearShopItems() {
        shopitem.clear();
        shopkit.clear();
        shopcost.clear();
        shopsale.clear();
    }
    
    // ===== PHASE 7.1: Loot Accessors =====
    
    /**
     * Clear all loot
     */
    public void clearAllLoot() {
        food1.clear();
        food2.clear();
        armour1.clear();
        armour2.clear();
        armour3.clear();
        armour4.clear();
        armour5.clear();
        weapons1.clear();
        weapons2.clear();
        potions.clear();
    }
}
