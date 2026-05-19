package com.tw0far.potiongames.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;

import com.tw0far.potiongames.main.PotionGames;

public class Game {
    private ArrayList<Lobby> lobbies = new ArrayList<>();
    
    // Player lobby tracking (GAME-LEVEL ONLY)
    // Player team/kit are stored in Participant objects within their Lobby
    // But we keep these for backwards-compatibility with existing code
    // DEPRECATED: New code should use Lobby.getParticipant(player).getTeam()
    private ArrayList<Player> setupPlayers = new ArrayList<>();
    private HashMap<Player, String> playerTeams = new HashMap<>();    // teamplayernames (deprecated - use Participant)
    private HashMap<Player, String> playerKits = new HashMap<>();     // kitplayernames (deprecated - use Participant)
    private HashMap<Player, String> playerVotes = new HashMap<>();    // voteplayernames (deprecated - use Participant)
    private HashMap<Player, String> playerChannels = new HashMap<>(); // playerChannel (chat)
    // ===== PHASE 7.1: Global Shop & Loot State =====
    // Shop items (single-arena mode)
    private ArrayList<String> shopitem = new ArrayList<>();
    private ArrayList<String> shopkit = new ArrayList<>();
    private ArrayList<Integer> shopcost = new ArrayList<>();
    private ArrayList<Integer> shopsale = new ArrayList<>();
    
    // Rank data (leaderboard/signs)
    private HashMap<Integer, String> rank = new HashMap<>();
    private ArrayList<Location> rankhead = new ArrayList<>();
    private ArrayList<Location> ranksign = new ArrayList<>();
    
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
    
    // Global chests
    private HashMap<Location, ItemStack[]> chests = new HashMap<>();
    
    // Global scoreboards
    private HashMap<Player, Scoreboard> info = new HashMap<>();
    
    // Channel tracking
    private HashMap<String, ArrayList<Player>> channels = new HashMap<>();
    
    // Setup player state backup (for /pg setup command)
    private HashMap<String, ItemStack[]> inv = new HashMap<>();       // player name -> inventory
    private HashMap<String, ItemStack[]> armor = new HashMap<>();     // player name -> armor
    private HashMap<String, Integer> lvl = new HashMap<>();           // player name -> level
    private HashMap<String, Float> exp = new HashMap<>();             // player name -> exp
    private HashMap<String, Location> loc = new HashMap<>();          // player name -> location
    private HashMap<String, GameMode> gm = new HashMap<>();           // player name -> game mode

    public void load() {
        lobbies.clear();
        Set<Integer> lobbyIds = new HashSet<>();

        if (Settings.arenadata.contains("pg.lobbies")) {
            for (String key : Settings.arenadata.getConfigurationSection("pg.lobbies").getKeys(false)) {
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
                Settings.arenadata,
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
            Bukkit.getConsoleSender().sendMessage("PotionGames: No lobbies configured.");
        }
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

    public Map<String, Integer> getPlayerStats(Player player) {
        Map<String, Integer> stats = new HashMap<>();
        Lobby lobby = getLobbyByPlayer(player);
        if (lobby == null) {
            stats.put("kills", 0);
            stats.put("deaths", 0);
            return stats;
        }
        stats.put("kills", lobby.getKills(player));
        stats.put("deaths", lobby.getDeaths(player));
        return stats;
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
    public int getSpectatorPlayerCount() {
        int count = 0;
        for (Lobby lobby : lobbies) {
            count += lobby.getSpectatorPlayers().size();
        }
        return count;
    }
    
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
        setupPlayers.clear();
        playerTeams.clear();
        playerKits.clear();
        playerVotes.clear();
        playerChannels.clear();
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
    
    // ===== Channel Management (Chat Channels) =====
    
    /**
     * Set player's channel
     */
    public void setPlayerChannel(Player player, String channel) {
        playerChannels.put(player, channel);
    }
    
    /**
     * Get player's channel
     */
    public String getPlayerChannel(Player player) {
        return playerChannels.get(player);
    }
    
    /**
     * Remove player from channel
     */
    public void removePlayerChannel(Player player) {
        playerChannels.remove(player);
    }
    
    /**
     * Check if player has channel assigned
     */
    public boolean hasChannel(Player player) {
        return playerChannels.containsKey(player);
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
    
    // ===== PHASE 7.1: Setup Player Management =====
    
    /**
     * Add player to setup mode
     */
    public void addSetupPlayer(Player player) {
        if (!setupPlayers.contains(player)) {
            setupPlayers.add(player);
        }
    }
    
    /**
     * Remove player from setup mode
     */
    public void removeSetupPlayer(Player player) {
        setupPlayers.remove(player);
    }
    
    /**
     * Check if player is in setup mode
     */
    public boolean isSetupPlayer(Player player) {
        return setupPlayers.contains(player);
    }
    
    /**
     * Clear all setup players
     */
    public void clearSetupPlayers() {
        setupPlayers.clear();
        inv.clear();
        armor.clear();
        lvl.clear();
        exp.clear();
        loc.clear();
        gm.clear();
    }
    
    // ===== PHASE 7.1: Setup State Backup (Player Inventory, etc.) =====
    
    /**
     * Save player's inventory
     */
    public void savePlayerInventory(Player player, ItemStack[] inventory) {
        inv.put(player.getName(), inventory);
    }
    
    /**
     * Get player's saved inventory
     */
    public ItemStack[] getPlayerInventory(Player player) {
        return inv.get(player.getName());
    }
    
    /**
     * Remove player's saved inventory
     */
    public void removeSavedInventory(Player player) {
        inv.remove(player.getName());
    }
    
    /**
     * Save player's armor
     */
    public void savePlayerArmor(Player player, ItemStack[] armor) {
        this.armor.put(player.getName(), armor);
    }
    
    /**
     * Get player's saved armor
     */
    public ItemStack[] getPlayerArmor(Player player) {
        return this.armor.get(player.getName());
    }
    
    /**
     * Remove player's saved armor
     */
    public void removeSavedArmor(Player player) {
        this.armor.remove(player.getName());
    }
    
    /**
     * Save player's level
     */
    public void savePlayerLevel(Player player, int level) {
        lvl.put(player.getName(), level);
    }
    
    /**
     * Get player's saved level
     */
    public Integer getPlayerLevel(Player player) {
        return lvl.get(player.getName());
    }
    
    /**
     * Remove player's saved level
     */
    public void removeSavedLevel(Player player) {
        lvl.remove(player.getName());
    }
    
    /**
     * Save player's experience
     */
    public void savePlayerExp(Player player, float experience) {
        exp.put(player.getName(), experience);
    }
    
    /**
     * Get player's saved experience
     */
    public Float getPlayerExp(Player player) {
        return exp.get(player.getName());
    }
    
    /**
     * Remove player's saved experience
     */
    public void removeSavedExp(Player player) {
        exp.remove(player.getName());
    }
    
    /**
     * Save player's location
     */
    public void savePlayerLocation(Player player, Location location) {
        loc.put(player.getName(), location);
    }
    
    /**
     * Get player's saved location
     */
    public Location getPlayerLocation(Player player) {
        return loc.get(player.getName());
    }
    
    /**
     * Remove player's saved location
     */
    public void removeSavedLocation(Player player) {
        loc.remove(player.getName());
    }
    
    /**
     * Save player's game mode
     */
    public void savePlayerGameMode(Player player, GameMode gameMode) {
        gm.put(player.getName(), gameMode);
    }
    
    /**
     * Get player's saved game mode
     */
    public GameMode getPlayerGameMode(Player player) {
        return gm.get(player.getName());
    }
    
    /**
     * Remove player's saved game mode
     */
    public void removeSavedGameMode(Player player) {
        gm.remove(player.getName());
    }
    
    // ===== PHASE 7.1: Shop Accessors =====
    
    /**
     * Get shop items list
     */
    public ArrayList<String> getShopItems() {
        return shopitem;
    }
    
    /**
     * Get shop kits list
     */
    public ArrayList<String> getShopKits() {
        return shopkit;
    }
    
    /**
     * Get shop costs list
     */
    public ArrayList<Integer> getShopCosts() {
        return shopcost;
    }
    
    /**
     * Get shop sale prices list
     */
    public ArrayList<Integer> getShopSales() {
        return shopsale;
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
    
    // ===== PHASE 7.1: Rank Accessors =====
    
    /**
     * Get rank map (rank position -> player name)
     */
    public HashMap<Integer, String> getRankMap() {
        return rank;
    }
    
    /**
     * Get rank head locations
     */
    public ArrayList<Location> getRankHeads() {
        return rankhead;
    }
    
    /**
     * Get rank sign locations
     */
    public ArrayList<Location> getRankSigns() {
        return ranksign;
    }
    
    /**
     * Clear all rank data
     */
    public void clearRankData() {
        rank.clear();
        rankhead.clear();
        ranksign.clear();
    }
    
    // ===== PHASE 7.1: Loot Accessors =====
    
    /**
     * Get food tier 1
     */
    public ArrayList<ItemStack> getFoodTier1() {
        return food1;
    }
    
    /**
     * Get food tier 2
     */
    public ArrayList<ItemStack> getFoodTier2() {
        return food2;
    }
    
    /**
     * Get armor tier 1
     */
    public ArrayList<ItemStack> getArmourTier1() {
        return armour1;
    }
    
    /**
     * Get armor tier 2
     */
    public ArrayList<ItemStack> getArmourTier2() {
        return armour2;
    }
    
    /**
     * Get armor tier 3
     */
    public ArrayList<ItemStack> getArmourTier3() {
        return armour3;
    }
    
    /**
     * Get armor tier 4
     */
    public ArrayList<ItemStack> getArmourTier4() {
        return armour4;
    }
    
    /**
     * Get armor tier 5
     */
    public ArrayList<ItemStack> getArmourTier5() {
        return armour5;
    }
    
    /**
     * Get weapons tier 1
     */
    public ArrayList<ItemStack> getWeaponsTier1() {
        return weapons1;
    }
    
    /**
     * Get weapons tier 2
     */
    public ArrayList<ItemStack> getWeaponsTier2() {
        return weapons2;
    }
    
    /**
     * Get potions list
     */
    public ArrayList<PotionEffect> getPotions() {
        return potions;
    }
    
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
    
    // ===== PHASE 7.1: Team/Kit/Vote Accessors =====

    // ===== PHASE 7.1: Chest Accessors =====
    
    /**
     * Get global chests map
     */
    public HashMap<Location, ItemStack[]> getChests() {
        return chests;
    }
    
    /**
     * Clear all chests
     */
    public void clearChests() {
        chests.clear();
    }
    
    // ===== PHASE 7.1: Scoreboard Accessors =====
    
    /**
     * Get scoreboards map (player -> scoreboard)
     */
    public HashMap<Player, Scoreboard> getScoreboards() {
        return info;
    }
    
    /**
     * Clear all scoreboards
     */
    public void clearScoreboards() {
        info.clear();
    }
    
    // ===== PHASE 7.1: Channel Accessors =====
    
    /**
     * Get channels map (channel name -> players)
     */
    public HashMap<String, ArrayList<Player>> getChannels() {
        return channels;
    }
    
    /**
     * Clear all channels
     */
    public void clearChannels() {
        channels.clear();
    }
}
