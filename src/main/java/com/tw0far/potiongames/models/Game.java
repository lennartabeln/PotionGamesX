package com.tw0far.potiongames.models;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;

public class Game {
    private ArrayList<Lobby> lobbies = new ArrayList<>();
    
    // Player tracking (migrated from PotionGames)
    private ArrayList<Player> activePlayers = new ArrayList<>();      // pgPlayers
    private ArrayList<Player> spectatorPlayers = new ArrayList<>();   // specPlayers
    private ArrayList<Player> setupPlayers = new ArrayList<>();       // setupPlayer
    
    // Player lobby tracking (GAME-LEVEL ONLY)
    // Player team/kit are stored in Participant objects within their Lobby
    // But we keep these for backwards-compatibility with existing code
    // DEPRECATED: New code should use Lobby.getParticipant(player).getTeam()
    private HashMap<Player, String> playerTeams = new HashMap<>();    // teamplayernames (deprecated - use Participant)
    private HashMap<Player, String> playerKits = new HashMap<>();     // kitplayernames (deprecated - use Participant)
    private HashMap<Player, String> playerVotes = new HashMap<>();    // voteplayernames (deprecated - use Participant)
    private HashMap<Player, String> playerChannels = new HashMap<>(); // playerChannel (chat)
    private HashMap<Player, String> playerLobbies = new HashMap<>();  // playerLobby (multi-lobby)
    private HashMap<Player, String> specLobbies = new HashMap<>();    // specLobby (multi-lobby)
    
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
        playerChannels.clear();
        playerLobbies.clear();
        specLobbies.clear();
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
        playerLobbies.put(player, lobbyId);
    }
    
    /**
     * Get player's lobby (multi-lobby mode)
     */
    public String getPlayerLobby(Player player) {
        return playerLobbies.get(player);
    }
    
    /**
     * Remove player from lobby
     */
    public void removePlayerLobby(Player player) {
        playerLobbies.remove(player);
    }
    
    /**
     * Check if player is in a lobby
     */
    public boolean isInLobby(Player player) {
        return playerLobbies.containsKey(player);
    }
    
    /**
     * Get all players in a specific lobby
     */
    public ArrayList<Player> getPlayersInLobby(String lobbyId) {
        ArrayList<Player> lobbyPlayers = new ArrayList<>();
        for (Player p : playerLobbies.keySet()) {
            if (playerLobbies.get(p).equals(lobbyId)) {
                lobbyPlayers.add(p);
            }
        }
        return lobbyPlayers;
    }
    
    // ===== Spectator Lobby Management (Multi-Lobby Only) =====
    
    /**
     * Set spectator's lobby (multi-lobby mode)
     */
    public void setSpectatorLobby(Player player, String lobbyId) {
        specLobbies.put(player, lobbyId);
    }
    
    /**
     * Get spectator's lobby (multi-lobby mode)
     */
    public String getSpectatorLobby(Player player) {
        return specLobbies.get(player);
    }
    
    /**
     * Remove spectator from lobby
     */
    public void removeSpectatorLobby(Player player) {
        specLobbies.remove(player);
    }
    
    /**
     * Check if player is spectating in a lobby
     */
    public boolean isSpectatingInLobby(Player player) {
        return specLobbies.containsKey(player);
    }
    
    /**
     * Check if player is in spec lobby (alias for isSpectatingInLobby)
     */
    public boolean isInSpecLobby(Player player) {
        return specLobbies.containsKey(player);
    }
    
    /**
     * Get all spectators in a specific lobby
     */
    public ArrayList<Player> getSpectatorsInLobby(String lobbyId) {
        ArrayList<Player> lobbySpecs = new ArrayList<>();
        for (Player p : specLobbies.keySet()) {
            if (specLobbies.get(p).equals(lobbyId)) {
                lobbySpecs.add(p);
            }
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
    
    /**
    
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
