package com.tw0far.potiongames.main;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.sign.Side;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import com.tw0far.potiongames.commands.CommandDispatcher;
import com.tw0far.potiongames.handlers.ISetupHandler;
import com.tw0far.potiongames.handlers.SetupHandler;
import com.tw0far.potiongames.listeners.*;
import com.tw0far.potiongames.managers.*;
import com.tw0far.potiongames.models.Game;
import com.tw0far.potiongames.models.GameStates;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.Settings;
import com.tw0far.potiongames.updatechecker.UpdateChecker;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

public class PotionGames extends JavaPlugin {

    //New
    private static PotionGames instance;
    public final Game game = new Game();
    private IConfigurationManager configManager;
    private ILobbyStateManager lobbyStateManager;
    private IPlayerStateManager playerStateManager;
    private IArenaStateManager arenaStateManager;
    private IItemStateManager itemStateManager;
    private IBlockStateManager blockStateManager;
    private ISetupStateManager setupStateManager;
    private IManager databaseManager;
    public static PotionGames getInstance() { return instance; }
    public Game getGame() { return game; }
    public IConfigurationManager getConfigManager() { return configManager; }
    public ILobbyStateManager getLobbyStateManager() { return lobbyStateManager; }
    public IPlayerStateManager getPlayerStateManager() { return playerStateManager; }
    public IArenaStateManager getArenaStateManager() { return arenaStateManager; }
    public IItemStateManager getItemStateManager() { return itemStateManager; }
    public IBlockStateManager getBlockStateManager() { return blockStateManager; }
    public ISetupStateManager getSetupStateManager() { return setupStateManager; }
    public IManager getDatabaseManager() { return databaseManager; }
    public ISetupHandler setupHandler = new SetupHandler(this);
    
    // ===== Delegation Methods for Player Lists (Phase 3.4 Migration) =====
    // These methods provide backwards-compatible access to Game's player tracking
    // Callers can use plugin.pgPlayers or plugin.getActivePlayers() interchangeably
    
    /**
     * Get active players (delegates to Game class)
     * Use this instead of pgPlayers for new code
     */
    public ArrayList<Player> getActivePlayers() {
        return game.getActivePlayers();
    }

    /**
     * Get loaded world names for lobby protections.
     */
    public ArrayList<String> getWorlds() {
        return worlds;
    }

    public ArrayList<String> getArenas() { return arenas; }
    public ArrayList<String> getVoted() { return voted; }
    public ArrayList<String> getTeams() { return teams; }
    public ArrayList<String> getTeamed() { return teamed; }
    public ArrayList<String> getKits() { return kits; }
    public ArrayList<String> getKited() { return kited; }
    public ArrayList<String> getChatmessages() { return chatmessages; }
    public ArrayList<String> getShop() { return shop; }
    public ArrayList<PotionEffect> getShoppotion() { return shoppotion; }
    public ArrayList<ItemStack> getShoppotiontype() { return shoppotiontype; }
    public ArrayList<String> getShopkit() { return shopkit; }
    public ArrayList<Integer> getShopcost() { return shopcost; }
    public ArrayList<Integer> getShopsale() { return shopsale; }
    public ArrayList<Location> getRankhead() { return rankhead; }
    public ArrayList<Location> getRanksign() { return ranksign; }
    public ArrayList<ItemStack> getFood1() { return food1; }
    public ArrayList<ItemStack> getFood2() { return food2; }
    public ArrayList<ItemStack> getArmour1() { return armour1; }
    public ArrayList<ItemStack> getArmour2() { return armour2; }
    public ArrayList<ItemStack> getArmour3() { return armour3; }
    public ArrayList<ItemStack> getArmour4() { return armour4; }
    public ArrayList<ItemStack> getArmour5() { return armour5; }
    public ArrayList<ItemStack> getWeapons1() { return weapons1; }
    public ArrayList<ItemStack> getWeapons2() { return weapons2; }
    public ArrayList<PotionEffect> getPotions() { return potions; }
    public HashMap<Integer, String> getRank() { return rank; }
    public HashMap<String, Integer> getVotes() { return votes; }
    public HashMap<Player, String> getVoteplayernames() { return voteplayernames; }
    public HashMap<String, Integer> getTeamplayers() { return teamplayers; }
    public HashMap<Player, String> getTeamplayernames() { return teamplayernames; }
    public HashMap<String, Integer> getKitplayers() { return kitplayers; }
    public HashMap<Player, String> getKitplayernames() { return kitplayernames; }
    public HashMap<String, ItemStack[]> getInv() { return inv; }
    public HashMap<String, ItemStack[]> getArmor() { return armor; }
    public HashMap<String, Integer> getLvl() { return lvl; }
    public HashMap<String, Float> getExp() { return exp; }
    public HashMap<String, Location> getLoc() { return loc; }
    public HashMap<String, GameMode> getGm() { return gm; }
    public HashMap<String, ArrayList<Player>> getChannels() { return channels; }
    public HashMap<Player, String> getPlayerChannelMap() { return playerChannel; }
    public HashMap<String, Integer> getLobbyAmountMap() { return lobbyAmount; }
    public HashMap<String, GameStates> getLobbyStates() { return lobbyStates; }
    public HashMap<String, Boolean> getLobbyDeathmatch() { return lobbyDeathmatch; }
    public HashMap<String, Boolean> getLobbyMove() { return lobbyMove; }
    public HashMap<String, Boolean> getLobbyJoinable() { return lobbyJoinable; }
    public HashMap<String, Boolean> getLobbyForcearena() { return lobbyForcearena; }
    public HashMap<String, Boolean> getLobbyVoteallowed() { return lobbyVoteallowed; }
    public HashMap<String, Boolean> getLobbyTeamallowed() { return lobbyTeamallowed; }
    public HashMap<String, Boolean> getLobbyKitallowed() { return lobbyKitallowed; }
    public HashMap<String, Boolean> getLobbyTickstarted() { return lobbyTickstarted; }
    public HashMap<String, Boolean> getLobbyBuild() { return lobbyBuild; }
    public HashMap<String, Boolean> getLobbyPause() { return lobbyPause; }
    public HashMap<String, Boolean> getLobbyActivateTeamsMap() { return lobbyActivateTeams; }
    public HashMap<String, Boolean> getLobbyActivateKitsMap() { return lobbyActivateKits; }
    public HashMap<String, Boolean> getLobbyActivateShopMap() { return lobbyActivateShop; }
    public HashMap<String, Boolean> getLobbyActivateAirdropsMap() { return lobbyActivateAirdrops; }
    public HashMap<String, Boolean> getLobbyCheckArenas() { return lobbyCheckArenas; }
    public HashMap<String, String> getLobbyVoteMap() { return lobbyVote; }
    public HashMap<String, String> getLobbyVotedarenaMap() { return lobbyVotedarena; }
    public HashMap<String, HashMap<Integer, Integer>> getLobbyteams() { return lobbyteams; }
    public HashMap<Player, String> getLobbyteamplayernamesdata() { return lobbyteamplayernamesdata; }
    public HashMap<String, HashMap<Player, String>> getLobbyteamplayernames() { return lobbyteamplayernames; }
    public HashMap<String, HashMap<String, Integer>> getLobbyvotes() { return lobbyvotes; }
    public HashMap<Player, String> getLobbyvoteplayernamesdata() { return lobbyvoteplayernamesdata; }
    public HashMap<String, HashMap<Player, String>> getLobbyvoteplayernames() { return lobbyvoteplayernames; }
    public HashMap<String, Integer> getLobbyteamSize() { return lobbyteamSize; }
    public HashMap<String, Integer> getLobbymaxPlayers() { return lobbymaxPlayers; }
    public HashMap<String, Integer> getLobbyminPlayers() { return lobbyminPlayers; }
    public HashMap<String, Integer> getLobbyteamAmount() { return lobbyteamAmount; }
    public HashMap<String, Integer> getLobbyroundTime() { return lobbyroundTime; }
    public HashMap<String, Integer> getLobbyroundTimeSeconds() { return lobbyroundTimeSeconds; }
    public HashMap<String, HashMap<Location, Block>> getLobbyLiquidPlaced() { return lobbyLiquidPlaced; }
    public HashMap<String, HashMap<Location, Material>> getLobbyPlacedBlocks() { return lobbyPlacedBlocks; }
    public HashMap<String, HashMap<Location, Material>> getLobbyBreakedBlocks() { return lobbyBreakedBlocks; }
    public HashMap<String, HashMap<Location, BlockData>> getLobbyWaterBlocks() { return lobbyWaterBlocks; }
    public HashMap<Location, Block> getLobbyLiquidPlacedData() { return lobbyLiquidPlacedData; }
    public HashMap<Location, Material> getLobbyPlacedBlocksData() { return lobbyPlacedBlocksData; }
    public HashMap<Location, Material> getLobbyBreakedBlocksData() { return lobbyBreakedBlocksData; }
    public HashMap<Location, BlockData> getLobbyWaterBlocksData() { return lobbyWaterBlocksData; }
    /**
     * Clear legacy collections that are still owned by PotionGames for reload compatibility.
     * New manager-backed state should be cleared through the dedicated managers instead.
     */
    public void clearLegacyCollectionsForReload() {
        game.clearAllPlayers();
        game.clearShopItems();
        game.clearRankData();
        game.clearAllLoot();
        game.clearChests();
        game.clearScoreboards();
        game.clearChannels();
        setupPlayer.clear();
        inv.clear();
        armor.clear();
        lvl.clear();
        exp.clear();
        loc.clear();
        gm.clear();

        arenas.clear();
        voted.clear();
        teams.clear();
        teamed.clear();
        kits.clear();
        kited.clear();
        chatmessages.clear();

        votes.clear();
        voteplayernames.clear();
        teamplayers.clear();
        teamplayernames.clear();
        kitplayers.clear();
        kitplayernames.clear();

    }

    /**
     * Get spectator players (delegates to Game class)
     * Use this instead of specPlayers for new code
     */
    public ArrayList<Player> getSpectatorPlayers() {
        return game.getSpectatorPlayers();
    }
    
    /**
     * Set player's team (delegates to Game class)
     */
    public void setPlayerTeam(Player player, String team) {
        game.setPlayerTeam(player, team);
    }
    
    /**
     * Get player's team (delegates to Game class)
     */
    public String getPlayerTeam(Player player) {
        return game.getPlayerTeam(player);
    }
    
    /**
     * Set player's kit (delegates to Game class)
     */
    public void setPlayerKit(Player player, String kit) {
        game.setPlayerKit(player, kit);
    }
    
    /**
     * Get player's kit (delegates to Game class)
     */
    public String getPlayerKit(Player player) {
        return game.getPlayerKit(player);
    }
    
    /**
     * Set player's channel (delegates to Game class)
     */
    public void setPlayerChannel(Player player, String channel) {
        game.setPlayerChannel(player, channel);
    }
    
    /**
     * Get player's channel (delegates to Game class)
     */
    public String getPlayerChannel(Player player) {
        return game.getPlayerChannel(player);
    }
    
    /**
     * Set player's lobby (delegates to Game class - multi-lobby)
     */
    public void setPlayerLobby(Player player, String lobbyId) {
        game.setPlayerLobby(player, lobbyId);
    }
    
    /**
     * Get player's lobby (delegates to Game class - multi-lobby)
     */
    public String getPlayerLobby(Player player) {
        return game.getPlayerLobby(player);
    }
    
    /**
     * Set spectator's lobby (delegates to Game class - multi-lobby)
     */
    public void setSpectatorLobby(Player player, String lobbyId) {
        game.setSpectatorLobby(player, lobbyId);
    }
    
    /**
     * Get spectator's lobby (delegates to Game class - multi-lobby)
     */
    public String getSpectatorLobby(Player player) {
        return game.getSpectatorLobby(player);
    }

    // ===== Delegation Methods for LobbyStateManager (Phase 4.1) =====
    // These methods provide convenient access to lobby-specific state
    
    public int getLobbyCountdown(String lobbyId) {
        return lobbyStateManager.getCountdown(lobbyId);
    }
    
    public void setLobbyCountdown(String lobbyId, int value) {
        lobbyStateManager.setCountdown(lobbyId, value);
    }
    
    public int getLobbyAmount(String lobbyId) {
        return lobbyStateManager.getLobbyAmount(lobbyId);
    }
    
    public void setLobbyAmount(String lobbyId, int value) {
        lobbyStateManager.setLobbyAmount(lobbyId, value);
    }

    public GameStates getLobbyGameState(String lobbyId) {
        return lobbyStateManager.getGameState(lobbyId);
    }

    public void setLobbyGameState(String lobbyId, GameStates state) {
        lobbyStateManager.setGameState(lobbyId, state);
    }

    public boolean isLobbyDeathmatchEnabled(String lobbyId) {
        return lobbyStateManager.isDeathmatchEnabled(lobbyId);
    }

    public void setLobbyDeathmatchEnabled(String lobbyId, boolean value) {
        lobbyStateManager.setDeathmatchEnabled(lobbyId, value);
    }

    public boolean isLobbyMoveAllowed(String lobbyId) {
        return lobbyStateManager.isMoveAllowed(lobbyId);
    }

    public void setLobbyMoveAllowed(String lobbyId, boolean value) {
        lobbyStateManager.setMoveAllowed(lobbyId, value);
    }

    public boolean isLobbyForcearena(String lobbyId) {
        return lobbyStateManager.isForcearena(lobbyId);
    }

    public void setLobbyForcearena(String lobbyId, boolean value) {
        lobbyStateManager.setForcearena(lobbyId, value);
    }

    public String getLobbyCurrentVote(String lobbyId) {
        return lobbyStateManager.getCurrentVote(lobbyId);
    }

    public void setLobbyCurrentVote(String lobbyId, String votedArena) {
        lobbyStateManager.setCurrentVote(lobbyId, votedArena);
    }

    public String getLobbyVotedArena(String lobbyId) {
        return lobbyStateManager.getVotedArena(lobbyId);
    }

    public void setLobbyVotedArena(String lobbyId, String votedArena) {
        lobbyStateManager.setVotedArena(lobbyId, votedArena);
    }

    public boolean isLobbyBuildAllowed(String lobbyId) {
        return lobbyStateManager.isBuildAllowed(lobbyId);
    }

    public void setLobbyBuildAllowed(String lobbyId, boolean value) {
        lobbyStateManager.setBuildAllowed(lobbyId, value);
    }

    public boolean isLobbyPaused(String lobbyId) {
        return lobbyStateManager.isPaused(lobbyId);
    }

    public void setLobbyPaused(String lobbyId, boolean value) {
        lobbyStateManager.setPaused(lobbyId, value);
    }

    public int getLobbyMinPlayers(String lobbyId) {
        return lobbyStateManager.getMinPlayers(lobbyId);
    }

    public void setLobbyMinPlayers(String lobbyId, int value) {
        lobbyStateManager.setMinPlayers(lobbyId, value);
    }
    
    // ===== Delegation Methods for PlayerStateManager (Phase 4.2) =====
    // These methods provide convenient access to player tracking state
    
    public boolean isActivePlayer(Player player) {
        return playerStateManager.isActivePlayer(player);
    }
    
    public boolean isSpectatorPlayer(Player player) {
        return playerStateManager.isSpectator(player);
    }
    
    public void addActivePlayer(Player player) {
        playerStateManager.addActivePlayer(player);
    }
    
    public void removeActivePlayer(Player player) {
        playerStateManager.removeActivePlayer(player);
    }
    
    public void addSpectatorPlayer(Player player) {
        playerStateManager.addSpectator(player);
    }
    
    public void removeSpectatorPlayer(Player player) {
        playerStateManager.removeSpectator(player);
    }
    
    // ===== Delegation Methods for Player Setup State (Phase 4.2.1) =====
    // These methods save/restore player state for setup mode
    
    public void addSetupPlayer(Player player) {
        setupPlayer.add(player);
    }
    
    public void removeSetupPlayer(Player player) {
        setupPlayer.remove(player);
    }
    
    public boolean isSetupPlayer(Player player) {
        return setupPlayer.contains(player);
    }
    
    public void savePlayerInventory(Player player, ItemStack[] inventory) {
        inv.put(player.getName(), inventory);
    }
    
    public ItemStack[] getPlayerInventory(Player player) {
        return inv.get(player.getName());
    }
    
    public void savePlayerArmor(Player player, ItemStack[] armor) {
        this.armor.put(player.getName(), armor);
    }
    
    public ItemStack[] getPlayerArmor(Player player) {
        return this.armor.get(player.getName());
    }
    
    public void savePlayerLevel(Player player, int level) {
        lvl.put(player.getName(), level);
    }
    
    public Integer getPlayerLevel(Player player) {
        return lvl.get(player.getName());
    }
    
    public void savePlayerExp(Player player, float exp) {
        this.exp.put(player.getName(), exp);
    }
    
    public Float getPlayerExp(Player player) {
        return this.exp.get(player.getName());
    }
    
    public void savePlayerLocation(Player player, Location location) {
        loc.put(player.getName(), location);
    }
    
    public Location getPlayerLocation(Player player) {
        return loc.get(player.getName());
    }
    
    public void savePlayerGameMode(Player player, GameMode gameMode) {
        gm.put(player.getName(), gameMode);
    }
    
    public GameMode getPlayerGameMode(Player player) {
        return gm.get(player.getName());
    }

    // ===== Delegation Methods for ArenaStateManager (Phase 4.3) =====
    // These methods provide convenient access to arena and voting state
    
    public void recordPlayerVote(Player player, String arena) {
        arenaStateManager.recordPlayerVote(player, arena);
    }
    
    public String getPlayerVote(Player player) {
        return arenaStateManager.getPlayerVote(player);
    }
    
    public String getWinningArena() {
        return arenaStateManager.getWinningArena();
    }
    
    // Team management delegation methods (Phase 5.2)
    public Integer getLobbyTeamPlayerCount(String lobbyId, Integer teamId) {
        return arenaStateManager.getLobbyTeamPlayerCount(lobbyId, teamId);
    }
    
    public void incrementLobbyTeamPlayers(String lobbyId, Integer teamId) {
        arenaStateManager.incrementLobbyTeamPlayers(lobbyId, teamId);
    }
    
    public void decrementLobbyTeamPlayers(String lobbyId, Integer teamId) {
        arenaStateManager.decrementLobbyTeamPlayers(lobbyId, teamId);
    }
    
    public void recordPlayerTeamInLobby(String lobbyId, Player player, String teamId) {
        arenaStateManager.recordPlayerTeamInLobby(lobbyId, player, teamId);
    }
    
    public String getPlayerTeamInLobby(String lobbyId, Player player) {
        return arenaStateManager.getPlayerTeamInLobby(lobbyId, player);
    }
    
    public void removePlayerTeamInLobby(String lobbyId, Player player) {
        arenaStateManager.removePlayerTeamInLobby(lobbyId, player);
    }
    
    public boolean hasPlayerTeamInLobby(String lobbyId, Player player) {
        return arenaStateManager.hasPlayerTeamInLobby(lobbyId, player);
    }
    
    public Map<Integer, Integer> getLobbyTeams(String lobbyId) {
        return arenaStateManager.getLobbyTeams(lobbyId);
    }
    
    public void initializeLobbyTeams(String lobbyId, Integer teamCount) {
        arenaStateManager.initializeLobbyTeams(lobbyId, teamCount);
    }
    
    public void setLobbyTeamSize(String lobbyId, Integer teamSize) {
        arenaStateManager.setLobbyTeamSize(lobbyId, teamSize);
    }
    
    public Integer getLobbyTeamSize(String lobbyId) {
        return arenaStateManager.getLobbyTeamSize(lobbyId);
    }
    
    public Integer getLobbyTeamAmount(String lobbyId) {
        return arenaStateManager.getLobbyTeamAmount(lobbyId);
    }
    
    // Lobby voting delegation methods (Phase 5.2)
    public Integer getLobbyVoteCount(String lobbyId, String arenaName) {
        return arenaStateManager.getLobbyVoteCount(lobbyId, arenaName);
    }
    
    public void addLobbyVote(String lobbyId, String arenaName) {
        arenaStateManager.addLobbyVote(lobbyId, arenaName);
    }
    
    public void removeLobbyVote(String lobbyId, String arenaName) {
        arenaStateManager.removeLobbyVote(lobbyId, arenaName);
    }
    
    public String getWinningArenaForLobby(String lobbyId) {
        return arenaStateManager.getWinningArenaForLobby(lobbyId);
    }
    
    public void recordPlayerVoteInLobby(String lobbyId, Player player, String arenaName) {
        arenaStateManager.recordPlayerVoteInLobby(lobbyId, player, arenaName);
        try {
            Lobby lobby = getLobbyById(Integer.parseInt(lobbyId));
            if (lobby != null) {
                lobby.recordVote(player, arenaName);
            }
        } catch (NumberFormatException ignored) {
        }
    }
    
    public String getPlayerVoteInLobby(String lobbyId, Player player) {
        return arenaStateManager.getPlayerVoteInLobby(lobbyId, player);
    }
    
    public boolean hasPlayerVotedInLobby(String lobbyId, Player player) {
        return arenaStateManager.hasPlayerVotedInLobby(lobbyId, player);
    }

    // ===== Delegation Methods for ItemStateManager (Phase 4.4) =====
    // These methods provide convenient access to loot and shop state
    
    public ItemStack getRandomFood1() {
        return itemStateManager.getRandomFood1();
    }
    
    public ItemStack getRandomFood2() {
        return itemStateManager.getRandomFood2();
    }
    
    public ItemStack getRandomArmor(int poolId) {
        return itemStateManager.getRandomArmor(poolId);
    }
    
    public ItemStack getRandomWeapon(int poolId) {
        return itemStateManager.getRandomWeapon(poolId);
    }

    // ===== Delegation Methods for BlockStateManager (Phase 4.5) =====
    // These methods provide convenient access to block tracking state
    
    public void trackPlacedBlock(Location location, Material material) {
        blockStateManager.trackPlacedBlock(location, material);
    }
    
    public void trackBrokenBlock(Location location, Material material) {
        blockStateManager.trackBrokenBlock(location, material);
    }
    
    public void trackWaterBlock(Location location, BlockData blockData) {
        blockStateManager.trackWaterBlock(location, blockData);
    }

    public void trackLobbyWaterBlock(String lobbyId, Location location, BlockData blockData) {
        blockStateManager.trackLobbyWaterBlock(lobbyId, location, blockData);
    }

    // ===== Delegation Methods for Game Shop/Kit/Loot (Phase 7.5) =====
    // These delegate to Game class for global shop/loot items
    
    public ArrayList<String> getGameShopItems() {
        return game.getShopItems();
    }
    
    public ArrayList<String> getGameShopKits() {
        return game.getShopKits();
    }
    
    public ArrayList<Integer> getGameShopCosts() {
        return game.getShopCosts();
    }
    
    public ArrayList<Integer> getGameShopSales() {
        return game.getShopSales();
    }
    
    // ===== Delegation Methods for Accessing Lobbies (Phase 7.5) =====
    // Get Lobby object by ID for accessing per-lobby state
    
    public Lobby getLobbyById(int lobbyId) {
        return game.getLobby(lobbyId);
    }
    
    public Lobby getLobbyByPlayer(Player player) {
        return game.getLobbyByPlayer(player);
    }
    
    /**
     * Check if shop is activated for a specific lobby ID
     */
    public boolean isLobbyActivateShop(String lobbyId) {
        try {
            int id = Integer.parseInt(lobbyId);
            Lobby lobby = game.getLobby(id);
            return lobby != null && lobby.isActivateShop();
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Get or create chest inventory in a lobby
     */
    public void setLobbyChestInventory(String lobbyId, Location location, Inventory inventory) {
        try {
            int id = Integer.parseInt(lobbyId);
            Lobby lobby = game.getLobby(id);
            if (lobby != null && inventory != null) {
                lobby.setChestInventory(location, inventory.getContents());
            }
        } catch (NumberFormatException e) {
            // ignore
        }
    }
    
    /**
     * Get chest inventory from a lobby
     */
    public Inventory getLobbyChestInventory(String lobbyId, Location location) {
        try {
            int id = Integer.parseInt(lobbyId);
            Lobby lobby = game.getLobby(id);
            if (lobby != null && lobby.hasChestInventory(location)) {
                Inventory inv = Bukkit.createInventory(null, 27);
                ItemStack[] items = lobby.getChestInventory(location);
                if (items != null) {
                    inv.setContents(items);
                }
                return inv;
            }
        } catch (NumberFormatException e) {
            // ignore
        }
        return null;
    }
    
    /**
     * Check if lobby has chest at location
     */
    public boolean hasLobbyChest(String lobbyId, Location location) {
        try {
            int id = Integer.parseInt(lobbyId);
            Lobby lobby = game.getLobby(id);
            return lobby != null && lobby.hasChestInventory(location);
        } catch (NumberFormatException e) {
            return false;
        }
    }


    
    // ===== Delegation Methods for Game Loot Data (Phase 7.5) =====
    // These delegate to Game class for global loot tables
    
    public ArrayList<ItemStack> getFoodTier1() {
        return game.getFoodTier1();
    }
    
    public ArrayList<ItemStack> getFoodTier2() {
        return game.getFoodTier2();
    }
    
    public ArrayList<ItemStack> getArmourTier1() {
        return game.getArmourTier1();
    }
    
    public ArrayList<ItemStack> getArmourTier2() {
        return game.getArmourTier2();
    }
    
    public ArrayList<ItemStack> getArmourTier3() {
        return game.getArmourTier3();
    }
    
    public ArrayList<ItemStack> getArmourTier4() {
        return game.getArmourTier4();
    }
    
    public ArrayList<ItemStack> getArmourTier5() {
        return game.getArmourTier5();
    }
    
    public ArrayList<ItemStack> getWeaponsTier1() {
        return game.getWeaponsTier1();
    }
    
    public ArrayList<ItemStack> getWeaponsTier2() {
        return game.getWeaponsTier2();
    }
    
    // ===== PHASE 7.5: Team Delegation Methods =====
    
    public String getPlayerTeam(String lobbyId, Player player) {
        try {
            int id = Integer.parseInt(lobbyId);
            Lobby lobby = game.getLobby(id);
            if (lobby != null) {
                return lobby.getPlayerTeam(player);
            }
        } catch (NumberFormatException e) {
            // Invalid lobbyId
        }
        return null;
    }
    
    public void setPlayerTeam(String lobbyId, Player player, String teamId) {
        try {
            int id = Integer.parseInt(lobbyId);
            Lobby lobby = game.getLobby(id);
            if (lobby != null) {
                lobby.setPlayerTeam(player, teamId);
            }
        } catch (NumberFormatException e) {
            // Invalid lobbyId
        }
    }
    
    public void removePlayerTeam(String lobbyId, Player player) {
        try {
            int id = Integer.parseInt(lobbyId);
            Lobby lobby = game.getLobby(id);
            if (lobby != null) {
                lobby.removePlayerTeam(player);
            }
        } catch (NumberFormatException e) {
            // Invalid lobbyId
        }
    }
    
    public void decrementTeamCount(String lobbyId, int teamId) {
        try {
            int id = Integer.parseInt(lobbyId);
            Lobby lobby = game.getLobby(id);
            if (lobby != null) {
                lobby.decrementTeamCount(teamId);
            }
        } catch (NumberFormatException e) {
            // Invalid lobbyId
        }
    }
    
    public boolean isLobbyActivateTeams(String lobbyId) {
        try {
            int id = Integer.parseInt(lobbyId);
            Lobby lobby = game.getLobby(id);
            if (lobby != null) {
                return lobby.isActivateTeams();
            }
        } catch (NumberFormatException e) {
            // Invalid lobbyId
        }
        return isActivateTeams();
    }

    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;
    private final ArrayList<String> worlds = new ArrayList<>();
    private final ArrayList<Player> pgPlayers = new ArrayList<>();
    private final ArrayList<Player> specPlayers = new ArrayList<>();
    private final ArrayList<Player> setupPlayer = new ArrayList<>();
    private final ArrayList<String> arenas = new ArrayList<>();
    private final ArrayList<String> voted = new ArrayList<>();
    private final ArrayList<String> teams = new ArrayList<>();
    private final ArrayList<String> teamed = new ArrayList<>();
    private final ArrayList<String> kits = new ArrayList<>();
    private final ArrayList<String> kited = new ArrayList<>();
    private final ArrayList<String> chatmessages = new ArrayList<>();
    private final ArrayList<String> shop = new ArrayList<>();
    private final ArrayList<PotionEffect> shoppotion = new ArrayList<>();
    private final ArrayList<ItemStack> shoppotiontype = new ArrayList<>();
    private final ArrayList<String> shopkit = new ArrayList<>();
    private final ArrayList<Integer> shopcost = new ArrayList<>();
    private final ArrayList<Integer> shopsale = new ArrayList<>();
    private final ArrayList<Location> rankhead = new ArrayList<>();
    private final ArrayList<Location> ranksign = new ArrayList<>();
    private final ArrayList<ItemStack> food1 = new ArrayList<>();
    private final ArrayList<ItemStack> food2 = new ArrayList<>();
    private final ArrayList<ItemStack> armour1 = new ArrayList<>();
    private final ArrayList<ItemStack> armour2 = new ArrayList<>();
    private final ArrayList<ItemStack> armour3 = new ArrayList<>();
    private final ArrayList<ItemStack> armour4 = new ArrayList<>();
    private final ArrayList<ItemStack> armour5 = new ArrayList<>();
    private final ArrayList<ItemStack> weapons1 = new ArrayList<>();
    private final ArrayList<ItemStack> weapons2 = new ArrayList<>();
    private final ArrayList<PotionEffect> potions = new ArrayList<>();
    private final HashMap<Integer, String> rank = new HashMap<>();
    private final HashMap<String, Integer> votes = new HashMap<>();
    private final HashMap<Player, String> voteplayernames = new HashMap<>();
    private final HashMap<String, Integer> teamplayers = new HashMap<>();
    private final HashMap<Player, String> teamplayernames = new HashMap<>();
    private final HashMap<String, Integer> kitplayers = new HashMap<>();
    private final HashMap<Player, String> kitplayernames = new HashMap<>();
    private final HashMap<String, ItemStack[]> inv = new HashMap<>();
    private final HashMap<String, ItemStack[]> armor = new HashMap<>();
    private final HashMap<String, Integer> lvl = new HashMap<>();
    private final HashMap<String, Float> exp = new HashMap<>();
    private final HashMap<String, Location> loc = new HashMap<>();
    private final HashMap<String, GameMode> gm = new HashMap<>();
    private final HashMap<String, ArrayList<Player>> channels = new HashMap<>();
    private final HashMap<Player, String> playerChannel = new HashMap<>();
    private final HashMap<String, Integer> lobbyAmount = new HashMap<>();
    private final HashMap<String, GameStates> lobbyStates = new HashMap<>();
    private final HashMap<String, Boolean> lobbyDeathmatch = new HashMap<>();
    private final HashMap<String, Boolean> lobbyMove = new HashMap<>();
    private final HashMap<String, Boolean> lobbyJoinable = new HashMap<>();
    private final HashMap<String, Boolean> lobbyForcearena = new HashMap<>();
    private final HashMap<String, Boolean> lobbyVoteallowed = new HashMap<>();
    private final HashMap<String, Boolean> lobbyTeamallowed = new HashMap<>();
    private final HashMap<String, Boolean> lobbyKitallowed = new HashMap<>();
    private final HashMap<String, Boolean> lobbyTickstarted = new HashMap<>();
    private final HashMap<String, Boolean> lobbyBuild = new HashMap<>();
    private final HashMap<String, Boolean> lobbyPause = new HashMap<>();
    private final HashMap<String, Boolean> lobbyActivateTeams = new HashMap<>();
    private final HashMap<String, Boolean> lobbyActivateKits = new HashMap<>();
    private final HashMap<String, Boolean> lobbyActivateShop = new HashMap<>();
    private final HashMap<String, Boolean> lobbyActivateAirdrops = new HashMap<>();
    private final HashMap<String, Boolean> lobbyCheckArenas = new HashMap<>();
    private final HashMap<String, String> lobbyVote = new HashMap<>();
    private final HashMap<String, String> lobbyVotedarena = new HashMap<>();
    private final HashMap<String, HashMap<Integer, Integer>> lobbyteams = new HashMap<>();
    private final HashMap<Player, String> lobbyteamplayernamesdata = new HashMap<>();
    private final HashMap<String, HashMap<Player, String>> lobbyteamplayernames = new HashMap<>();
    private final HashMap<String, HashMap<String, Integer>> lobbyvotes = new HashMap<>();
    private final HashMap<Player, String> lobbyvoteplayernamesdata = new HashMap<>();
    private final HashMap<String, HashMap<Player, String>> lobbyvoteplayernames = new HashMap<>();
    private final HashMap<String, Integer> lobbyteamSize = new HashMap<>();
    private final HashMap<String, Integer> lobbymaxPlayers = new HashMap<>();
    private final HashMap<String, Integer> lobbyminPlayers = new HashMap<>();
    private final HashMap<String, Integer> lobbyteamAmount = new HashMap<>();
    private final HashMap<String, Integer> lobbyroundTime = new HashMap<>();
    private final HashMap<String, Integer> lobbyroundTimeSeconds = new HashMap<>();
    private final HashMap<String, HashMap<Location, Block>> lobbyLiquidPlaced = new HashMap<>();
    private final HashMap<String, HashMap<Location, Material>> lobbyPlacedBlocks = new HashMap<>();
    private final HashMap<String, HashMap<Location, Material>> lobbyBreakedBlocks = new HashMap<>();
    private final HashMap<String, HashMap<Location, BlockData>> lobbyWaterBlocks = new HashMap<>();
    private final HashMap<Location, Block> lobbyLiquidPlacedData = new HashMap<>();
    private final HashMap<Location, Material> lobbyPlacedBlocksData = new HashMap<>();
    private final HashMap<Location, Material> lobbyBreakedBlocksData = new HashMap<>();
    private final HashMap<Location, BlockData> lobbyWaterBlocksData = new HashMap<>();
    private final ItemStack coin = new ItemStack(Material.GOLD_NUGGET);
    private final ItemStack bottle = new ItemStack(Material.GLASS_BOTTLE);
    private int tick;
    private int countdown = 60;
    private int teamSize = 2;
    private int maxPlayers = 24;
    private int minPlayers = maxPlayers / 2;
    private int teamAmount = maxPlayers / teamSize;
    private int roundTime = 30;
    private int roundTimeSeconds = roundTime * 60;
    private int activePotions = 19;
    private int activeKits = 6;
    private int winningReward = 100;
    private int killReward = 10;
    private String language = "en_US";
    private String host = "localhost";
    private String port = "3306";
    private String database = "potiongames";
    private String user = "root";
    private String password;
    private boolean deathmatch = false;
    private boolean joinable = true;
    private boolean pause = false;
    private boolean build = false;
    private boolean move = true;
    private boolean startOnJoin = false;
    private boolean activateTeams = true;
    private boolean activateKits = true;
    private boolean activateShop = true;
    private boolean activateAirdrops = true;
    private boolean activateMysql = false;
    private boolean mysql = false;
    private boolean gameServer = true;
    private boolean addlobby = false;
    private boolean addarena = false;
    private boolean dellobby = false;
    private boolean delarena = false;
    private boolean compassOnSpawn = false;
    private boolean allowOutsideChat = false;
    private boolean changeGamerules = true;
    private boolean checkArenas = false;
    private boolean activateScoreboard = true;
    private boolean friendlyFire = false;
    private boolean joinStarted = false;
    private boolean activateDeathmatch = true;
    private boolean enableRewards = false;
    private boolean broadcastStarting = false;
    private Connection con;
    private Statement st;

    public static Economy getEconomy() {
        return econ;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        // Initialize configuration manager
        configManager = new ConfigurationManager(this);
        configManager.onEnable();
        
        // Initialize state managers
        lobbyStateManager = new LobbyStateManager(this);
        lobbyStateManager.onEnable();
        playerStateManager = new PlayerStateManager();
        playerStateManager.onEnable();
        arenaStateManager = new ArenaStateManager();
        arenaStateManager.onEnable();
        itemStateManager = new ItemStateManager();
        itemStateManager.onEnable();
        blockStateManager = new BlockStateManager();
        blockStateManager.onEnable();
        setupStateManager = new SetupStateManager();
        setupStateManager.onEnable();
        
        // Initialize database manager
        databaseManager = new DatabaseManager(this, (ConfigurationManager) configManager);
        databaseManager.onEnable();
        
        PluginManager pm = Bukkit.getPluginManager();
        
        // Register new event listeners (refactored from monolithic Events.java)
        // Player-related events
        pm.registerEvents(new PlayerEventListener(this), this);
        pm.registerEvents(new RespawnEventListener(this), this);
        pm.registerEvents(new SpectatorEventListener(this), this);
        pm.registerEvents(new TeleportEventListener(this), this);
        pm.registerEvents(new ChatEventListener(this), this);
        pm.registerEvents(new ItemDropEventListener(this), this);
        pm.registerEvents(new ItemConsumeEventListener(this), this);
        pm.registerEvents(new FoodLevelEventListener(this), this);
        
        // Block-related events
        pm.registerEvents(new BlockEventListener(this), this);
        pm.registerEvents(new BlockFadeEventListener(this), this);
        pm.registerEvents(new LeavesDecayEventListener(this), this);
        pm.registerEvents(new BlockFlowEventListener(this), this);
        pm.registerEvents(new BucketEventListener(this), this);
        pm.registerEvents(new InteractEventListener(this), this);
        
        // Combat-related events
        pm.registerEvents(new CombatEventListener(this), this);
        pm.registerEvents(new DamageEventListener(this), this);
        pm.registerEvents(new DeathEventListener(this), this);
        
        // Environmental events
        pm.registerEvents(new WeatherEventListener(this), this);
        pm.registerEvents(new ExplosionEventListener(this), this);
        pm.registerEvents(new CreatureSpawnEventListener(this), this);
        pm.registerEvents(new SignChangeEventListener(this), this);
        
        // Inventory events
        pm.registerEvents(new InventoryEventListener(this), this);
        
        // Register new command dispatcher (refactored from monolithic Commands.java)
        Objects.requireNonNull(getCommand("pg")).setExecutor(new CommandDispatcher(this));

        //New
        Settings.arenadatafile = new File(getDataFolder() + File.separator + "arenadata.yml");
        Settings.chestdatafile = new File(getDataFolder() + File.separator + "chestdata.yml");
        Settings.kitdatafile = new File(getDataFolder() + File.separator + "kitdata.yml");
        Settings.messagesfile = new File(getDataFolder() + File.separator + "messages.yml");
        Settings.shopdatafile = new File(getDataFolder() + File.separator + "shopdata.yml");
        // Ensure default messages.yml is available on first run
        if (!Settings.messagesfile.exists()) {
            saveResource("messages.yml", false);
        }
        Settings.loadConfigurations();
        Settings.loadSettings(this);
        game.load();
        //New

        chatmessages.add("Waiting for players!");
        chatmessages.add("The game starts in");
        chatmessages.add("Player-Finder");
        chatmessages.add("The game starts now!");
        chatmessages.add("has won the game!");
        chatmessages.add("Teleporting to lobby in");
        chatmessages.add("Teleporting to lobby now!");
        chatmessages.add("will be played!");
        chatmessages.add("Dead");
        chatmessages.add("was killed by");
        chatmessages.add("died");
        chatmessages.add("I'm on fire!");
        chatmessages.add("Blocks away from next player");
        chatmessages.add("No player found!");
        chatmessages.add("Arena-Selector");
        chatmessages.add("Votes");
        chatmessages.add("You have voted for");
        chatmessages.add("in spectator mode");
        chatmessages.add("could not be teleported to the lobby!");
        chatmessages.add("The game has already started!");
        chatmessages.add("The game has been started!");
        chatmessages.add("Not enough players to start the game!");
        chatmessages.add("Pause");
        chatmessages.add("Build");
        chatmessages.add("Lobby successfully set!");
        chatmessages.add("Server stopped!");
        chatmessages.add("has been forced as arena!");
        chatmessages.add("is not an arena!");
        chatmessages.add("successfully removed!");
        chatmessages.add("successfully set!");
        chatmessages.add("Successfully joined lobby");
        chatmessages.add("is not a valid spawn!");
        chatmessages.add("Successfully left lobby");
        chatmessages.add("Place");
        chatmessages.add("Head successfully set!");
        chatmessages.add("Sign successfully set!");
        chatmessages.add("Connection to database established!");
        chatmessages.add("Connection to database failed! For more information see console.");
        chatmessages.add("Connection to database closed!");
        chatmessages.add("Failed to close connection to database! For more information see console.");
        chatmessages.add("Plugin started successfully!");
        chatmessages.add("Plugin stopped successfully!");
        chatmessages.add("Random");
        chatmessages.add("Team-Selector");
        chatmessages.add("Players");
        chatmessages.add("You are now in team");
        chatmessages.add("You now have the kit");
        chatmessages.add("This team is already full!");
        chatmessages.add("Update-Checker-Error");
        chatmessages.add("Shop");
        chatmessages.add("Duration");
        chatmessages.add("Price");
        chatmessages.add("Coins");
        chatmessages.add("You not have enough Coins!");
        chatmessages.add("You not have an empty bottle!");
        chatmessages.add("Coin");
        chatmessages.add("Stats");
        chatmessages.add("Won");
        chatmessages.add("Lost");
        chatmessages.add("Kills");
        chatmessages.add("Deaths");
        chatmessages.add("K/D");
        chatmessages.add("Kit-Selector");
        chatmessages.add("File loading / saving fail! For more information see console.");
        chatmessages.add("Commands");
        chatmessages.add("Rounds");
        chatmessages.add("Lobby successfully removed!");
        chatmessages.add("seconds remaining to end this round!");
        chatmessages.add("Nobody won this round!");
        chatmessages.add("Type lobby number in chat to add it!");
        chatmessages.add("Type arena name in chat to add it!");
        chatmessages.add("Type lobby number in chat to remove it!");
        chatmessages.add("Type arena name in chat to remove it!");
        chatmessages.add("could not be teleported to a spawn!");
        chatmessages.add("This lobby does not exists!");
        chatmessages.add("Use /pg help for help!");
        chatmessages.add("There is not a new update available.");
        chatmessages.add("There is a new update available.");
        chatmessages.add("Plugin successfully reloaded!");
        chatmessages.add("Extremely explosive TNT");
        chatmessages.add("Leave");
        chatmessages.add("Teleporting to deathmatch arena in");
        chatmessages.add("Teleporting to deathmatch arena now!");
        chatmessages.add("Deathmatch is starting in");
        chatmessages.add("Deathmatch started!");
        chatmessages.add("Could not update Rank-Wall!");
        chatmessages.add("Please inform an admin!");
        chatmessages.add("Could not join lobby!");
        chatmessages.add("Could not teleport to arena!");
        chatmessages.add("Could not teleport to deathmatch arena!");
        chatmessages.add("Could not load an arena!");
        chatmessages.add("Reward");
        chatmessages.add("An error occurred");
        chatmessages.add("For winning the round you get");
        chatmessages.add("For killing a player you get");
        chatmessages.add("Lobby%s is starting! Join with /pg join%s");
        chatmessages.add("You have a block above you!");
        chatmessages.add("Airdrop is falling at your location!");
        chatmessages.add("Airdrop is falling at");
        chatmessages.add("This arena does not exists!");
        chatmessages.add("Lobby enabled!");
        chatmessages.add("Lobby disabled!");
        shop.add("JUMP_BOOST");
        shoppotion.add(new PotionEffect(PotionEffectType.JUMP_BOOST, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.POTION));
        shopkit.add("Looter");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("RESISTANCE");
        shoppotion.add(new PotionEffect(PotionEffectType.RESISTANCE, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.POTION));
        shopkit.add("Tank");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("SPEED");
        shoppotion.add(new PotionEffect(PotionEffectType.SPEED, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.POTION));
        shopkit.add("Looter");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("ABSORPTION");
        shoppotion.add(new PotionEffect(PotionEffectType.ABSORPTION, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.POTION));
        shopkit.add("Tank");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("FIRE_RESISTANCE");
        shoppotion.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.POTION));
        shopkit.add("Tank");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("INSTANT_HEALTH");
        shoppotion.add(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.POTION));
        shopkit.add("Healer");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("HEALTH_BOOST");
        shoppotion.add(new PotionEffect(PotionEffectType.HEALTH_BOOST, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.POTION));
        shopkit.add("Healer");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("INVISIBILITY");
        shoppotion.add(new PotionEffect(PotionEffectType.INVISIBILITY, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.POTION));
        shopkit.add("Ghost");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("REGENERATION");
        shoppotion.add(new PotionEffect(PotionEffectType.REGENERATION, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.POTION));
        shopkit.add("Healer");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("SATURATION");
        shoppotion.add(new PotionEffect(PotionEffectType.SATURATION, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.POTION));
        shopkit.add("Looter");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("STRENGTH");
        shoppotion.add(new PotionEffect(PotionEffectType.STRENGTH, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.POTION));
        shopkit.add("Fighter");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("DOLPHINS_GRACE");
        shoppotion.add(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.POTION));
        shopkit.add("Looter");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("NIGHT_VISION");
        shoppotion.add(new PotionEffect(PotionEffectType.NIGHT_VISION, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.POTION));
        shopkit.add("Ghost");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("WATER_BREATHING");
        shoppotion.add(new PotionEffect(PotionEffectType.WATER_BREATHING, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.POTION));
        shopkit.add("Ghost");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("WEAKNESS");
        shoppotion.add(new PotionEffect(PotionEffectType.WEAKNESS, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.SPLASH_POTION));
        shopkit.add("Fighter");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("WITHER");
        shoppotion.add(new PotionEffect(PotionEffectType.WITHER, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.SPLASH_POTION));
        shopkit.add("Fighter");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("GLOWING");
        shoppotion.add(new PotionEffect(PotionEffectType.GLOWING, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.SPLASH_POTION));
        shopkit.add("Fighter");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("BLINDNESS");
        shoppotion.add(new PotionEffect(PotionEffectType.BLINDNESS, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.SPLASH_POTION));
        shopkit.add("Fighter");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("NAUSEA");
        shoppotion.add(new PotionEffect(PotionEffectType.NAUSEA, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.SPLASH_POTION));
        shopkit.add("Fighter");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("BAD_OMEN");
        shoppotion.add(new PotionEffect(PotionEffectType.BAD_OMEN, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.SPLASH_POTION));
        shopkit.add("Fighter");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("BAD_OMEN2");
        shoppotion.add(new PotionEffect(PotionEffectType.BAD_OMEN, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.SPLASH_POTION));
        shopkit.add("Fighter");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("BAD_OMEN3");
        shoppotion.add(new PotionEffect(PotionEffectType.BAD_OMEN, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.SPLASH_POTION));
        shopkit.add("Fighter");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("BAD_OMEN4");
        shoppotion.add(new PotionEffect(PotionEffectType.BAD_OMEN, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.SPLASH_POTION));
        shopkit.add("Fighter");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("BAD_OMEN5");
        shoppotion.add(new PotionEffect(PotionEffectType.BAD_OMEN, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.SPLASH_POTION));
        shopkit.add("Fighter");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("BAD_OMEN6");
        shoppotion.add(new PotionEffect(PotionEffectType.BAD_OMEN, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.SPLASH_POTION));
        shopkit.add("Fighter");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("BAD_OMEN7");
        shoppotion.add(new PotionEffect(PotionEffectType.BAD_OMEN, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.SPLASH_POTION));
        shopkit.add("Fighter");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("BAD_OMEN8");
        shoppotion.add(new PotionEffect(PotionEffectType.BAD_OMEN, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.SPLASH_POTION));
        shopkit.add("Fighter");
        shopcost.add(4);
        shopsale.add(2);
        if (getConfig().get("pg.activateMySQL") == null) {
            getConfig().addDefault("pg.activateMySQL", activateMysql);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            activateMysql = getConfig().getBoolean("pg.activateMySQL");
        }
        if (getConfig().get("pg.countdown") == null) {
            getConfig().addDefault("pg.countdown", countdown);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            countdown = getConfig().getInt("pg.countdown");
        }
        if (getConfig().get("pg.startOnJoin") == null) {
            getConfig().addDefault("pg.startOnJoin", startOnJoin);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            startOnJoin = getConfig().getBoolean("pg.startOnJoin");
        }
        if (getConfig().get("pg.compassOnSpawn") == null) {
            getConfig().addDefault("pg.compassOnSpawn", compassOnSpawn);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            compassOnSpawn = getConfig().getBoolean("pg.compassOnSpawn");
        }
        if (getConfig().get("pg.allowOutsideChat") == null) {
            getConfig().addDefault("pg.allowOutsideChat", allowOutsideChat);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            allowOutsideChat = getConfig().getBoolean("pg.allowOutsideChat");
        }
        if (getConfig().get("pg.changeGamerules") == null) {
            getConfig().addDefault("pg.changeGamerules", changeGamerules);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            changeGamerules = getConfig().getBoolean("pg.changeGamerules");
        }
        if (getConfig().get("pg.activateTeams") == null) {
            getConfig().addDefault("pg.activateTeams", activateTeams);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            activateTeams = getConfig().getBoolean("pg.activateTeams");
        }
        if (getConfig().get("pg.activateKits") == null) {
            getConfig().addDefault("pg.activateKits", activateKits);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            activateKits = getConfig().getBoolean("pg.activateKits");
        }
        if (getConfig().get("pg.activateShop") == null) {
            getConfig().addDefault("pg.activateShop", activateShop);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            activateShop = getConfig().getBoolean("pg.activateShop");
        }
        if (getConfig().get("pg.activateAirdrops") == null) {
            getConfig().addDefault("pg.activateAirdrops", activateAirdrops);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            activateAirdrops = getConfig().getBoolean("pg.activateAirdrops");
        }
        if (getConfig().get("pg.gameServer") == null) {
            getConfig().addDefault("pg.gameServer", gameServer);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            gameServer = getConfig().getBoolean("pg.gameServer");
        }
        if (getConfig().get("pg.maxPlayers") == null) {
            getConfig().addDefault("pg.maxPlayers", maxPlayers);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            maxPlayers = getConfig().getInt("pg.maxPlayers");
        }
        if (getConfig().get("pg.minPlayers") == null) {
            getConfig().addDefault("pg.minPlayers", minPlayers);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            minPlayers = getConfig().getInt("pg.minPlayers");
        }
        if (getConfig().get("pg.teamSize") == null) {
            getConfig().addDefault("pg.teamSize", teamSize);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            teamSize = getConfig().getInt("pg.teamSize");
            teamAmount = maxPlayers / teamSize;
        }
        if (getConfig().get("pg.roundTime") == null) {
            getConfig().addDefault("pg.roundTime", roundTime);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            roundTime = getConfig().getInt("pg.roundTime");
            roundTimeSeconds = roundTime * 60;
        }
        if (getConfig().get("pg.activePotions") == null) {
            getConfig().addDefault("pg.activePotions", activePotions);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            activePotions = getConfig().getInt("pg.activePotions");
        }
        if (getConfig().get("pg.activeKits") == null) {
            getConfig().addDefault("pg.activeKits", activeKits);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            activeKits = getConfig().getInt("pg.activeKits");
        }
        if (getConfig().get("pg.activateScoreboard") == null) {
            getConfig().addDefault("pg.activateScoreboard", activateScoreboard);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            activateScoreboard = getConfig().getBoolean("pg.activateScoreboard");
        }
        if (getConfig().get("pg.friendlyFire") == null) {
            getConfig().addDefault("pg.friendlyFire", friendlyFire);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            friendlyFire = getConfig().getBoolean("pg.friendlyFire");
        }
        if (getConfig().get("pg.joinStarted") == null) {
            getConfig().addDefault("pg.joinStarted", joinStarted);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            joinStarted = getConfig().getBoolean("pg.joinStarted");
        }
        if (getConfig().get("pg.activateDeathmatch") == null) {
            getConfig().addDefault("pg.activateDeathmatch", activateDeathmatch);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            activateDeathmatch = getConfig().getBoolean("pg.activateDeathmatch");
        }
        if (getConfig().get("pg.enableRewards") == null) {
            getConfig().addDefault("pg.enableRewards", enableRewards);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            enableRewards = getConfig().getBoolean("pg.enableRewards");
        }
        if (getConfig().get("pg.broadcastStarting") == null) {
            getConfig().addDefault("pg.broadcastStarting", broadcastStarting);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            broadcastStarting = getConfig().getBoolean("pg.broadcastStarting");
        }
        if (getConfig().get("pg.winningReward") == null) {
            getConfig().addDefault("pg.winningReward", winningReward);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            winningReward = getConfig().getInt("pg.winningReward");
        }
        if (getConfig().get("pg.killReward") == null) {
            getConfig().addDefault("pg.killReward", killReward);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            killReward = getConfig().getInt("pg.killReward");
        }
        if (getConfig().get("pg.language") == null) {
            getConfig().addDefault("pg.language", language);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            language = getConfig().getString("pg.language");
        }
        int message = 1;
        for (int i = 0; i < chatmessages.size(); i++) {
            if (Settings.messages.get("pg.messages." + language + "." + message) == null) {
                Settings.messages.addDefault("pg.messages." + language + "." + message, chatmessages.get(message - 1));
                Settings.messages.options().copyDefaults(true);
            } else {
                String text = Settings.messages.getString("pg.messages." + language + "." + message);
                chatmessages.set(message - 1, text);
            }
            message++;
        }
        try {
            Settings.messages.save(Settings.messagesfile);
        } catch (IOException ex) {
            Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + chatmessages.get(63) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
        }
        int shopitem = 1;
        for (int i = 0; i < shop.size(); i++) {
            if (Settings.shopdata.get("pg.potions." + shopitem) == null) {
                Settings.shopdata.addDefault("pg.potions." + shopitem, shop.get(shopitem - 1));
                Settings.shopdata.addDefault("pg.potions." + shopitem + ".name", shop.get(shopitem - 1));
                Settings.shopdata.addDefault("pg.potions." + shopitem + "." + "shoppotion", shoppotion.get(shopitem - 1));
                Settings.shopdata.addDefault("pg.potions." + shopitem + "." + "shoppotiontype", shoppotiontype.get(shopitem - 1));
                Settings.shopdata.addDefault("pg.potions." + shopitem + ".kit", shopkit.get(shopitem - 1));
                Settings.shopdata.addDefault("pg.potions." + shopitem + ".cost", shopcost.get(shopitem - 1));
                Settings.shopdata.addDefault("pg.potions." + shopitem + ".sale", shopsale.get(shopitem - 1));
                Settings.shopdata.options().copyDefaults(true);
            } else {
                String name = Settings.shopdata.getString("pg.potions." + shopitem + ".name");
                shop.set(shopitem - 1, name);
                PotionEffect potion = (PotionEffect) Settings.shopdata.get("pg.potions." + shopitem + "." + "shoppotion");
                shoppotion.set(shopitem - 1, potion);
                ItemStack potiontype = (ItemStack) Settings.shopdata.get("pg.potions." + shopitem + "." + "shoppotiontype");
                shoppotiontype.set(shopitem - 1, potiontype);
                String kitname = Settings.shopdata.getString("pg.potions." + shopitem + ".kit");
                shopkit.set(shopitem - 1, kitname);
                Integer cost = (Integer) Settings.shopdata.get("pg.potions." + shopitem + ".cost");
                shopcost.set(shopitem - 1, cost);
                Integer sale = (Integer) Settings.shopdata.get("pg.potions." + shopitem + ".sale");
                shopsale.set(shopitem - 1, sale);
            }
            shopitem++;
        }
        try {
            Settings.shopdata.save(Settings.shopdatafile);
        } catch (IOException ex) {
            Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + chatmessages.get(63) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
        }
        try {
            Settings.arenadata.save(Settings.arenadatafile);
        } catch (IOException ex) {
            Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + chatmessages.get(63) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
        }
        kits.add("Rich Kid");
        kits.add("Fighter");
        kits.add("Healer");
        kits.add("Looter");
        kits.add("Ghost");
        kits.add("Tank");
        for (int i = 7; i < 27; i++) {
            kits.add("kit" + i);
        }
        kitplayers.put(chatmessages.get(42), 0);
        for (String all : kits) {
            kitplayers.put(all, 0);
        }
        int kititem = 1;
        for (int i = 0; i < kits.size(); i++) {
            if (Settings.kitdata.get("pg.kits." + kititem) == null) {
                Settings.kitdata.addDefault("pg.kits." + kititem, kits.get(kititem - 1));
                Settings.kitdata.addDefault("pg.kits." + kititem + ".name", kits.get(kititem - 1));
                Settings.kitdata.options().copyDefaults(true);
            } else {
                String name = Settings.kitdata.getString("pg.kits." + kititem + ".name");
                kits.set(kititem - 1, name);
            }
            kititem++;
        }
        try {
            Settings.kitdata.save(Settings.kitdatafile);
        } catch (IOException ex) {
            Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + chatmessages.get(63) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
        }
        chestData();
        if (getConfig().get("pg.mysql") == null) {
            getConfig().addDefault("pg.mysql.host", "localhost");
            getConfig().addDefault("pg.mysql.port", "3306");
            getConfig().addDefault("pg.mysql.database", "potiongames");
            getConfig().addDefault("pg.mysql.user", "root");
            getConfig().addDefault("pg.mysql.password", "");
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            host = getConfig().getString("pg.mysql.host");
            port = getConfig().getString("pg.mysql.port");
            database = getConfig().getString("pg.mysql.database");
            user = getConfig().getString("pg.mysql.user");
            password = getConfig().getString("pg.mysql.password");
        }
        connect();
        ConnectMySQL();
    ItemMeta coinmeta = coin.getItemMeta();
    assert coinmeta != null;
    // use Adventure component for item display name
    coinmeta.displayName(Component.text(chatmessages.get(55)).color(NamedTextColor.DARK_AQUA));
    coin.setItemMeta(coinmeta);
        if (gameServer) {
                for (int lobby = 1; lobby <= 27; lobby++) {
                    if (Settings.arenadata.contains("pg.lobbies." + lobby)) {
                        String s = Integer.toString(lobby);
                        lobbyCheckArenas.put(s, false);
                        lobbyActivateTeams.put(s, true);
                        lobbyActivateKits.put(s, true);
                        lobbyActivateShop.put(s, true);
                        lobbyActivateAirdrops.put(s, true);
                        lobbyJoinable.put(s, true);
                        lobbyForcearena.put(s, false);
                        lobbyDeathmatch.put(s, false);
                        lobbyMove.put(s, true);
                        lobbyVoteallowed.put(s, false);
                        lobbyTeamallowed.put(s, false);
                        lobbyKitallowed.put(s, false);
                        lobbyAmount.put(s, 0);
                        lobbyTickstarted.put(s, true);
                        lobbyBuild.put(s, false);
                        lobbyPause.put(s, false);
                        lobbyVote.put(s, null);
                        lobbyVotedarena.put(s, null);
                        lobbyteamSize.put(s, 2);
                        lobbymaxPlayers.put(s, 24);
                        int minPlayersNumber = lobbymaxPlayers.get(s) / 2;
                        lobbyminPlayers.put(s, minPlayersNumber);
                        int teamAmountNumber = lobbymaxPlayers.get(s) / lobbyteamSize.get(s);
                        lobbyteamAmount.put(s, teamAmountNumber);
                        lobbyroundTime.put(s, 30);
                        int roundTimeSecondsNumber = lobbyroundTime.get(s) * 60;
                        lobbyroundTimeSeconds.put(s, roundTimeSecondsNumber);
                        if (Settings.arenadata.get("pg.lobbies." + s + ".activateTeams") == null) {
                            Settings.arenadata.addDefault("pg.lobbies." + s + ".activateTeams", activateTeams);
                            Settings.arenadata.options().copyDefaults(true);
                            try {
                                Settings.arenadata.save(Settings.arenadatafile);
                            } catch (IOException ex) {
                                Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + chatmessages.get(63) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
                            }
                        } else {
                            lobbyActivateTeams.replace(s, Settings.arenadata.getBoolean("pg.lobbies." + s + ".activateTeams"));
                        }
                        if (Settings.arenadata.get("pg.lobbies." + s + ".activateKits") == null) {
                            Settings.arenadata.addDefault("pg.lobbies." + s + ".activateKits", activateKits);
                            Settings.arenadata.options().copyDefaults(true);
                            try {
                                Settings.arenadata.save(Settings.arenadatafile);
                            } catch (IOException ex) {
                                Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + chatmessages.get(63) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
                            }
                        } else {
                            lobbyActivateKits.replace(s, Settings.arenadata.getBoolean("pg.lobbies." + s + ".activateKits"));
                        }
                        if (Settings.arenadata.get("pg.lobbies." + s + ".activateShop") == null) {
                            Settings.arenadata.addDefault("pg.lobbies." + s + ".activateShop", activateShop);
                            Settings.arenadata.options().copyDefaults(true);
                            try {
                                Settings.arenadata.save(Settings.arenadatafile);
                            } catch (IOException ex) {
                                Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + chatmessages.get(63) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
                            }
                        } else {
                            lobbyActivateShop.replace(s, Settings.arenadata.getBoolean("pg.lobbies." + s + ".activateShop"));
                        }
                        if (Settings.arenadata.get("pg.lobbies." + s + ".activateAirdrops") == null) {
                            Settings.arenadata.addDefault("pg.lobbies." + s + ".activateAirdrops", activateAirdrops);
                            Settings.arenadata.options().copyDefaults(true);
                            try {
                                Settings.arenadata.save(Settings.arenadatafile);
                            } catch (IOException ex) {
                                Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + chatmessages.get(63) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
                            }
                        } else {
                            lobbyActivateAirdrops.replace(s, Settings.arenadata.getBoolean("pg.lobbies." + s + ".activateAirdrops"));
                        }
                        if (Settings.arenadata.get("pg.lobbies." + s + ".teamSize") == null) {
                            Settings.arenadata.addDefault("pg.lobbies." + s + ".teamSize", teamSize);
                            Settings.arenadata.options().copyDefaults(true);
                            try {
                                Settings.arenadata.save(Settings.arenadatafile);
                            } catch (IOException ex) {
                                Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + chatmessages.get(63) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
                            }
                        } else {
                            lobbyteamSize.replace(s, Settings.arenadata.getInt("pg.lobbies." + s + ".teamSize"));
                        }
                        if (Settings.arenadata.get("pg.lobbies." + s + ".maxPlayers") == null) {
                            Settings.arenadata.addDefault("pg.lobbies." + s + ".maxPlayers", maxPlayers);
                            Settings.arenadata.options().copyDefaults(true);
                            try {
                                Settings.arenadata.save(Settings.arenadatafile);
                            } catch (IOException ex) {
                                Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + chatmessages.get(63) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
                            }
                        } else {
                            lobbymaxPlayers.replace(s, Settings.arenadata.getInt("pg.lobbies." + s + ".maxPlayers"));
                        }
                        if (Settings.arenadata.get("pg.lobbies." + s + ".minPlayers") == null) {
                            Settings.arenadata.addDefault("pg.lobbies." + s + ".minPlayers", minPlayers);
                            Settings.arenadata.options().copyDefaults(true);
                            try {
                                Settings.arenadata.save(Settings.arenadatafile);
                            } catch (IOException ex) {
                                Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + chatmessages.get(63) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
                            }
                        } else {
                            lobbyminPlayers.replace(s, Settings.arenadata.getInt("pg.lobbies." + s + ".minPlayers"));
                        }
                        if (Settings.arenadata.get("pg.lobbies." + s + ".roundTime") == null) {
                            Settings.arenadata.addDefault("pg.lobbies." + s + ".roundTime", roundTime);
                            Settings.arenadata.options().copyDefaults(true);
                            try {
                                Settings.arenadata.save(Settings.arenadatafile);
                            } catch (IOException ex) {
                                Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + chatmessages.get(63) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
                            }
                        } else {
                            lobbyroundTime.replace(s, Settings.arenadata.getInt("pg.lobbies." + s + ".roundTime"));
                        }
                        roundTimeSecondsNumber = lobbyroundTime.get(s) * 60;
                        lobbyroundTimeSeconds.put(s, roundTimeSecondsNumber);
                        teamAmountNumber = lobbymaxPlayers.get(s) / lobbyteamSize.get(s);
                        lobbyteamAmount.put(s, teamAmountNumber);
                        if (!lobbyVoteallowed.get(s)) {
                            lobbyVoteallowed.replace(s, true);
                            HashMap<String, Integer> temp = new HashMap<>();
                            temp.put(chatmessages.get(42), 0);
                            for (int max = 1; max < 27; max++) {
                                if (Settings.arenadata.contains("pg.lobbies." + s + "." + max + ".name")) {
                                    temp.put(Settings.arenadata.getString("pg.lobbies." + s + "." + max + ".name"), 0);
                                    lobbyvoteplayernamesdata.put(null, Settings.arenadata.getString("pg.lobbies." + s + "." + max + ".name"));
                                }
                            }
                            lobbyvotes.put(s, temp);
                            lobbyvoteplayernames.put(s, lobbyvoteplayernamesdata);
                        }
                        if (!lobbyTeamallowed.get(s)) {
                            lobbyTeamallowed.replace(s, true);
                            HashMap<Integer, Integer> temp = new HashMap<>();
                            for (int max = 1; max <= lobbyteamAmount.get(s); max++) {
                                temp.put(max, 0);
                                lobbyteamplayernamesdata.put(null, Integer.toString(max));
                            }
                            lobbyteams.put(s, temp);
                            lobbyteamplayernames.put(s, lobbyteamplayernamesdata);
                        }
                        if (!lobbyKitallowed.get(s)) {
                            lobbyKitallowed.replace(s, true);
                            kitplayers.put(chatmessages.get(42), 0);
                            for (String all : kits) {
                                kitplayers.put(all, 0);
                            }
                        }
                        lobbyLiquidPlaced.put(s, lobbyLiquidPlacedData);
                        lobbyPlacedBlocks.put(s, lobbyPlacedBlocksData);
                        lobbyBreakedBlocks.put(s, lobbyBreakedBlocksData);
                        lobbyWaterBlocks.put(s, lobbyWaterBlocksData);
                        lobbyStates.put(s, GameStates.WAITING);
                        tickLobby(s);
                    }
                }
        } else {
            hubStats();
        }
        if (activateMysql && !mysql) {
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(chatmessages.get(40)).color(NamedTextColor.GREEN)));
        }
        new UpdateChecker(this, 87633).getVersion(version -> {
            if (getPluginMeta().getVersion().equalsIgnoreCase(version)) {
                Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(chatmessages.get(76)).color(NamedTextColor.GRAY)).append(Component.text(" " + getPluginMeta().getVersion()).color(NamedTextColor.GRAY)));
            } else {
                Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(chatmessages.get(77)).color(NamedTextColor.GRAY)).append(Component.text(" " + getPluginMeta().getVersion() + " -> " + version).color(NamedTextColor.GRAY)));
            }
        });
        if (enableRewards) {
            if (!setupEconomy()) {
                log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getPluginMeta().getName()));
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }
        if (getConfig().contains("pg.RankWall.headp1") && getConfig().contains("pg.RankWall.headp2") && getConfig().contains("pg.RankWall.headp3") && getConfig().contains("pg.RankWall.signp1") && getConfig().contains("pg.RankWall.signp2") && getConfig().contains("pg.RankWall.signp3")) {
            try (ResultSet rs = query("SELECT UUID FROM Stats ORDER BY WINS DESC LIMIT 3")) {
                int ii = 0;
                while (rs.next()) {
                    ii++;
                    rank.put(ii, rs.getString("UUID"));
                }
                rankhead.add(getConfig().getLocation("pg.RankWall.headp1"));
                rankhead.add(getConfig().getLocation("pg.RankWall.headp2"));
                rankhead.add(getConfig().getLocation("pg.RankWall.headp3"));
                ranksign.add(getConfig().getLocation("pg.RankWall.signp1"));
                ranksign.add(getConfig().getLocation("pg.RankWall.signp2"));
                ranksign.add(getConfig().getLocation("pg.RankWall.signp3"));
                for (int iii = 0; iii < rank.size(); iii++) {
                    int id = iii + 1;
                    Skull skull = (Skull) rankhead.get(iii).getBlock().getState();
                    UUID uuid = UUID.fromString(rank.get(id));
                    OfflinePlayer name = Bukkit.getOfflinePlayer(uuid);
                    skull.setOwningPlayer(name);
                    skull.update();
                }
                for (int iii = 0; iii < rank.size(); iii++) {
                    int id = iii + 1;
                    BlockState b = ranksign.get(iii).getBlock().getState();
                    OfflinePlayer name = Bukkit.getOfflinePlayer(UUID.fromString(rank.get(id)));
                    Sign sign = (Sign) b;
                    sign.getSide(Side.FRONT).line(0, Messages.SignPlace(id));
                    sign.getSide(Side.FRONT).line(1, Component.text(name.getName()));
                    sign.getSide(Side.FRONT).line(2, Messages.SignWins(getWins(rank.get(id))));
                    sign.getSide(Side.FRONT).line(3, Messages.SignKD(getKD(rank.get(id))));
                    sign.update();
                }
            } catch (SQLException ex) {
                Bukkit.getConsoleSender().sendMessage(Messages.RankwallCouldNotUpdate());
            }
        }
    }

    // onReload() deleted - moved to ReloadHandler/ConfigurationManager

    @Override
    public void onDisable() {
        log.info(String.format("[%s] Disabled Version %s", getPluginMeta().getName(), getPluginMeta().getVersion()));
        
        // Disable state managers and database manager
        if (configManager != null) configManager.onDisable();
        if (lobbyStateManager != null) lobbyStateManager.onDisable();
        if (playerStateManager != null) playerStateManager.onDisable();
        if (arenaStateManager != null) arenaStateManager.onDisable();
        if (itemStateManager != null) itemStateManager.onDisable();
        if (blockStateManager != null) blockStateManager.onDisable();
        if (databaseManager != null) databaseManager.onDisable();
        
        close();
        if (gameServer && startOnJoin) {
            for (Player all : Bukkit.getOnlinePlayers()) {
                all.kick(Settings.prefix.append(Component.text(chatmessages.get(25)).color(NamedTextColor.RED)));
            }
        }
        if (!startOnJoin) {
            for (Iterator<Player> it = pgPlayers.iterator(); it.hasNext(); ) {
                Player all = it.next();
                it.remove();
                onLeave(all);
            }
            for (Iterator<Player> it = specPlayers.iterator(); it.hasNext(); ) {
                Player all = it.next();
                it.remove();
                onLeave(all);
            }
        }
        for (Player all : Bukkit.getOnlinePlayers()) {
            String playerLobbyId = game.getPlayerLobby(all);
            if (playerLobbyId != null) {
                onLeaveLobby(all, playerLobbyId);
            }
            String spectatorLobbyId = game.getSpectatorLobby(all);
            if (spectatorLobbyId != null) {
                onLeaveLobby(all, spectatorLobbyId);
            }
        }
        Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(chatmessages.get(41)).color(NamedTextColor.RED)));
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    // joinChannel() deleted - moved to GameManager

    public void leaveChannel(Player player, String channelName) {
        ArrayList<Player> players = channels.get(channelName);
        players.remove(player);
        channels.put(channelName, players);
        playerChannel.remove(player);
    }

    // spawnFirework() deleted - moved to GameManager

    public void hubStats() {
        tick = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            if (getConfig().contains("pg.RankWall.headp1") && getConfig().contains("pg.RankWall.headp2") && getConfig().contains("pg.RankWall.headp3") && getConfig().contains("pg.RankWall.signp1") && getConfig().contains("pg.RankWall.signp2") && getConfig().contains("pg.RankWall.signp3")) {
                try (ResultSet rs = query("SELECT UUID FROM Stats ORDER BY WINS DESC LIMIT 3")) {
                    int ii = 0;
                    while (rs.next()) {
                        ii++;
                        rank.put(ii, rs.getString("UUID"));
                    }
                    rankhead.add(getConfig().getLocation("pg.RankWall.headp1"));
                    rankhead.add(getConfig().getLocation("pg.RankWall.headp2"));
                    rankhead.add(getConfig().getLocation("pg.RankWall.headp3"));
                    ranksign.add(getConfig().getLocation("pg.RankWall.signp1"));
                    ranksign.add(getConfig().getLocation("pg.RankWall.signp2"));
                    ranksign.add(getConfig().getLocation("pg.RankWall.signp3"));
                    for (int iii = 0; iii < rank.size(); iii++) {
                        int id = iii + 1;
                        Skull skull = (Skull) rankhead.get(iii).getBlock().getState();
                        UUID uuid = UUID.fromString(rank.get(id));
                        OfflinePlayer name = Bukkit.getOfflinePlayer(uuid);
                        skull.setOwningPlayer(name);
                        skull.update();
                    }
                    for (int iii = 0; iii < rank.size(); iii++) {
                        int id = iii + 1;
                        BlockState b = ranksign.get(iii).getBlock().getState();
                        OfflinePlayer name = Bukkit.getOfflinePlayer(UUID.fromString(rank.get(id)));
                        Sign sign = (Sign) b;
                        sign.getSide(Side.FRONT).line(0, Messages.SignPlace(id));
                        sign.getSide(Side.FRONT).line(1, Component.text(name.getName()));
                        sign.getSide(Side.FRONT).line(2, Messages.SignWins(getWins(rank.get(id))));
                        sign.getSide(Side.FRONT).line(3, Messages.SignKD(getKD(rank.get(id))));
                        sign.update();
                    }
                } catch (SQLException ex) {
                    Bukkit.getConsoleSender().sendMessage(Messages.RankwallCouldNotUpdate());
                }
            }
        }, 0, 1200);
    }

    // chestData() deleted - moved to initialization

    public void setup(Player p) {
        ItemStack addlobby = new ItemStack(Material.STICK);
    ItemMeta addlobbymeta = addlobby.getItemMeta();
    assert addlobbymeta != null;
    addlobbymeta.displayName(Component.text("Add(Left)/Del(Right) Lobby").color(NamedTextColor.DARK_AQUA));
        addlobby.setItemMeta(addlobbymeta);
        p.getInventory().setItem(1, addlobby);
        ItemStack chooselobby = new ItemStack(Material.CLOCK);
        ItemMeta chooselobbymeta = chooselobby.getItemMeta();
        assert chooselobbymeta != null;
        chooselobbymeta.displayName(Component.text("Choose Lobby").color(NamedTextColor.DARK_AQUA));
        chooselobby.setItemMeta(chooselobbymeta);
        p.getInventory().setItem(2, chooselobby);
        ItemStack addarena = new ItemStack(Material.STICK);
    ItemMeta addarenameta = addarena.getItemMeta();
    assert addarenameta != null;
    addarenameta.displayName(Component.text("Add(Left)/Del(Right) Arena").color(NamedTextColor.DARK_AQUA));
        addarena.setItemMeta(addarenameta);
        p.getInventory().setItem(3, addarena);
        ItemStack choosearena = new ItemStack(Material.CLOCK);
    ItemMeta choosearenameta = choosearena.getItemMeta();
    assert choosearenameta != null;
    choosearenameta.displayName(Component.text("Choose Arena").color(NamedTextColor.DARK_AQUA));
        choosearena.setItemMeta(choosearenameta);
        p.getInventory().setItem(4, choosearena);
        ItemStack addspawn = new ItemStack(Material.STICK);
    ItemMeta addspawnmeta = addspawn.getItemMeta();
    assert addspawnmeta != null;
    addspawnmeta.displayName(Component.text("Add(Left)/Del(Right) Spawn").color(NamedTextColor.DARK_AQUA));
        addspawn.setItemMeta(addspawnmeta);
        p.getInventory().setItem(5, addspawn);
        ItemStack signsetup = new ItemStack(Material.OAK_SIGN);
    ItemMeta signsetupmeta = signsetup.getItemMeta();
    assert signsetupmeta != null;
    signsetupmeta.displayName(Component.text("Set Join-Sign").color(NamedTextColor.DARK_AQUA));
        signsetup.setItemMeta(signsetupmeta);
        p.getInventory().setItem(6, signsetup);
        ItemStack leavesetup = new ItemStack(Material.BARRIER);
    ItemMeta leavesetupmeta = leavesetup.getItemMeta();
    assert leavesetupmeta != null;
    leavesetupmeta.displayName(Component.text("Leave Setup-Mode").color(NamedTextColor.DARK_AQUA));
        leavesetup.setItemMeta(leavesetupmeta);
        p.getInventory().setItem(7, leavesetup);
    }

    // clearEffects() deleted - moved to GameManager

    // setGameRules() deleted - moved to GameManager

    public void onLeave(Player p) {
        game.removeActivePlayer(p);
        game.removeSpectatorPlayer(p);
    }

    public void tickLobby(String lobbyId) {
        try {
            Lobby lobby = getLobbyById(Integer.parseInt(lobbyId));
            if (lobby != null) {
                lobby.startTick();
            }
        } catch (NumberFormatException ignored) {
        }
    }

    public void connect() {
        if (activateMysql) {
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", user, password);
                Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(chatmessages.get(36)).color(NamedTextColor.GREEN)));
                mysql = true;
            } catch (SQLException ex) {
                Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + chatmessages.get(37) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
                mysql = false;
            }
        } else {
            con = null;
            try {
                File dbFile = new File(getDataFolder(), "stats.db");
                String url = "jdbc:sqlite:" + dbFile.getPath();
                con = DriverManager.getConnection(url);
                st = con.createStatement();
                Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(chatmessages.get(36)).color(NamedTextColor.GREEN)));
            } catch (SQLException ex) {
                Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + chatmessages.get(37) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
            }
        }
    }

    public void close() {
        try {
            if (con != null) {
                con.close();
                Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(chatmessages.get(38)).color(NamedTextColor.GREEN)));
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + chatmessages.get(39) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
        }
    }

    public void update(String qry) {
        if (activateMysql) {
            if (con != null) {
                try {
                    st = con.createStatement();
                    st.executeUpdate(qry);
                    st.close();
                } catch (SQLException ex) {
                    Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + chatmessages.get(37) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
                    connect();
                }
            }
        } else {
            try {
                st.execute(qry);
            } catch (SQLException ex) {
                Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + chatmessages.get(37) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
            }
        }
    }

    public ResultSet query(String qry) {
        if (activateMysql) {
            if (con != null) {
                ResultSet rs = null;
                try {
                    st = con.createStatement();
                    rs = st.executeQuery(qry);
                } catch (SQLException ex) {
                    Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + chatmessages.get(37) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
                    connect();
                }
                return rs;
            }
        } else {
            try {
                return st.executeQuery(qry);
            } catch (SQLException ex) {
                Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + chatmessages.get(37) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
            }
        }
        return null;
    }

    public void ConnectMySQL() {
        if (con != null) {
            try {
                update("CREATE TABLE IF NOT EXISTS Stats(UUID varchar(64), ROUNDS int, WINS int, LOSSES int, KILLS int, DEATHS int, KD double);");
            } catch (Exception ex) {
                update("CREATE TABLE IF NOT EXISTS Stats(UUID varchar(64), ROUNDS int, WINS int, LOSTS int, KILLS int, DEATHS int, KD double);");
            }
        }
    }

    public boolean playerExists(String uuid) {
        try (ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'")) {
            if (rs.next()) {
                return rs.getString("UUID") != null;
            }
            return false;
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + chatmessages.get(37) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
        }
        return false;
    }

    public void createPlayer(String uuid) {
        if (!playerExists(uuid)) {
            try {
                update("INSERT INTO Stats(UUID, ROUNDS, WINS, LOSSES, KILLS, DEATHS, KD) VALUES ('" + uuid + "', '0', '0', '0', '0', '0', '0');");
            } catch (Exception ex) {
                update("INSERT INTO Stats(UUID, ROUNDS, WINS, LOSTS, KILLS, DEATHS, KD) VALUES ('" + uuid + "', '0', '0', '0', '0', '0', '0');");
            }
        }
    }

    public int getKills(String uuid) {
        int i = 0;
        if (playerExists(uuid)) {
            try (ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'")) {
                if ((rs.next())) {
                    rs.getInt("KILLS");
                }
                i = rs.getInt("KILLS");
            } catch (SQLException ex) {
                Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + chatmessages.get(37) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
            }
        } else {
            createPlayer(uuid);
            getKills(uuid);
        }
        return i;
    }

    public int getDeaths(String uuid) {
        int i = 0;
        if (playerExists(uuid)) {
            try (ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'")) {
                if ((rs.next())) {
                    rs.getInt("DEATHS");
                }
                i = rs.getInt("DEATHS");
            } catch (SQLException ex) {
                Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + chatmessages.get(37) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
            }
        } else {
            createPlayer(uuid);
            getDeaths(uuid);
        }
        return i;
    }

    public double getKD(String uuid) {
        double i = 0;
        if (playerExists(uuid)) {
            try (ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'")) {
                if ((rs.next())) {
                    rs.getDouble("KD");
                }
                i = rs.getDouble("KD");
            } catch (SQLException ex) {
                Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + chatmessages.get(37) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
            }
        } else {
            createPlayer(uuid);
            getKD(uuid);
        }
        return i;
    }

    public int getWins(String uuid) {
        int i = 0;
        if (playerExists(uuid)) {
            try (ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'")) {
                if ((rs.next())) {
                    rs.getInt("WINS");
                }
                i = rs.getInt("WINS");
            } catch (SQLException ex) {
                Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + chatmessages.get(37) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
            }
        } else {
            createPlayer(uuid);
            getWins(uuid);
        }
        return i;
    }

    public int getLosses(String uuid) {
        int i = 0;
        if (playerExists(uuid)) {
            try (ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'")) {
                if ((rs.next())) {
                    try {
                        rs.getInt("LOSSES");
                    } catch (SQLException ex) {
                        rs.getInt("LOSTS");
                    }
                }
                try {
                    i = rs.getInt("LOSSES");
                } catch (SQLException ex) {
                    i = rs.getInt("LOSTS");
                }
            } catch (SQLException ex) {
                Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(chatmessages.get(37) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
            }
        } else {
            createPlayer(uuid);
            getLosses(uuid);
        }
        return i;
    }

    public int getRounds(String uuid) {
        int i = 0;
        if (playerExists(uuid)) {
            try (ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'")) {
                if ((rs.next())) {
                    rs.getInt("ROUNDS");
                }
                i = rs.getInt("ROUNDS");
            } catch (SQLException ex) {
                Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + chatmessages.get(37) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
            }
        } else {
            createPlayer(uuid);
            getRounds(uuid);
        }
        return i;
    }

    public void setKills(String uuid, int kills) {
        if (playerExists(uuid)) {
            update("UPDATE Stats SET KILLS= '" + kills + "' WHERE UUID= '" + uuid + "';");
        } else {
            createPlayer(uuid);
            setKills(uuid, kills);
        }
    }

    public void setDeaths(String uuid, int deaths) {
        if (playerExists(uuid)) {
            update("UPDATE Stats SET DEATHS= '" + deaths + "' WHERE UUID= '" + uuid + "';");
        } else {
            createPlayer(uuid);
            setDeaths(uuid, deaths);
        }
    }

    public void setKD(String uuid, double kd) {
        if (playerExists(uuid)) {
            int kills = getKills(uuid);
            int deaths = getDeaths(uuid);
            if (deaths != 0) {
                kd = ((double) kills) / ((double) deaths);
            } else {
                kd = kills;
            }
            kd = kd * 1000;
            kd = Math.round(kd);
            kd = kd / 1000;
            update("UPDATE Stats SET KD= '" + kd + "' WHERE UUID= '" + uuid + "';");
        } else {
            createPlayer(uuid);
            setKD(uuid, kd);
        }
    }

    public void setWins(String uuid, int wins) {
        if (playerExists(uuid)) {
            update("UPDATE Stats SET WINS= '" + wins + "' WHERE UUID= '" + uuid + "';");
        } else {
            createPlayer(uuid);
            setWins(uuid, wins);
        }
    }

    public void setLosses(String uuid, int losses) {
        if (playerExists(uuid)) {
            try {
                update("UPDATE Stats SET LOSSES= '" + losses + "' WHERE UUID= '" + uuid + "';");
            } catch (Exception ex) {
                update("UPDATE Stats SET LOSTS= '" + losses + "' WHERE UUID= '" + uuid + "';");
            }
        } else {
            createPlayer(uuid);
            setLosses(uuid, losses);
        }
    }

    public void setRounds(String uuid, int rounds) {
        if (playerExists(uuid)) {
            int wins = getWins(uuid);
            int losses = getLosses(uuid);
            rounds = wins + losses;
            update("UPDATE Stats SET ROUNDS= '" + rounds + "' WHERE UUID= '" + uuid + "';");
        } else {
            createPlayer(uuid);
            setRounds(uuid, rounds);
        }
    }

    public void addKills(String uuid, int kills) {
        if (playerExists(uuid)) {
            setKills(uuid, (getKills(uuid) + kills));
            setKD(uuid, 0);
        } else {
            createPlayer(uuid);
            addKills(uuid, kills);
        }
    }

    public void addDeaths(String uuid, int deaths) {
        if (playerExists(uuid)) {
            setDeaths(uuid, (getDeaths(uuid) + deaths));
            setKD(uuid, 0);
        } else {
            createPlayer(uuid);
            addDeaths(uuid, deaths);
        }
    }

    public void addWins(String uuid, int wins) {
        if (playerExists(uuid)) {
            setWins(uuid, (getWins(uuid) + wins));
            setRounds(uuid, 0);
        } else {
            createPlayer(uuid);
            addWins(uuid, wins);
        }
    }

    public void addLosses(String uuid, int losses) {
        if (playerExists(uuid)) {
            setLosses(uuid, (getLosses(uuid) + losses));
            setRounds(uuid, 0);
        } else {
            createPlayer(uuid);
            addLosses(uuid, losses);
        }
    }

    // ===== RESTORED STUB METHODS (Delegated or Simple Implementations) =====
    
    /**
     * Joins a player to a communication channel.
     * Maintains chat channels for targeted messaging.
     */
    public void joinChannel(Player player, String channelName) {
        if (playerChannel.get(player) != null) {
            String prevChannel = playerChannel.get(player);
            leaveChannel(player, prevChannel);
        }
        ArrayList<Player> players = channels.get(channelName);
        if (players == null) {
            players = new ArrayList<>();
        }
        players.add(player);
        channels.put(channelName, players);
        playerChannel.put(player, channelName);
    }

    /**
     * Initializes chest loot data with food, armor, and weapon itemstacks.
     * Called during onEnable() to populate loot tables.
     */
    public void chestData() {
        // Food tier 1
        food1.add(new ItemStack(Material.CAKE, 1));
        food1.add(new ItemStack(Material.BREAD, 3));
        food1.add(new ItemStack(Material.PUMPKIN_PIE, 3));
        food1.add(new ItemStack(Material.COOKIE, 3));
        food1.add(new ItemStack(Material.BAKED_POTATO, 3));
        
        // Food tier 2
        food2.add(new ItemStack(Material.RABBIT_STEW, 1));
        food2.add(new ItemStack(Material.MUSHROOM_STEW, 1));
        food2.add(new ItemStack(Material.BEETROOT_SOUP, 1));
        food2.add(new ItemStack(Material.GOLDEN_CARROT, 1));
        food2.add(new ItemStack(Material.MILK_BUCKET, 1));
        
        // Armor tier 1
        armour1.add(new ItemStack(Material.LEATHER_HELMET, 1));
        armour1.add(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
        armour1.add(new ItemStack(Material.LEATHER_LEGGINGS, 1));
        armour1.add(new ItemStack(Material.LEATHER_BOOTS, 1));
        
        // Armor tier 2
        armour2.add(new ItemStack(Material.CHAINMAIL_HELMET, 1));
        armour2.add(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1));
        armour2.add(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1));
        armour2.add(new ItemStack(Material.CHAINMAIL_BOOTS, 1));
        
        // Armor tier 3
        armour3.add(new ItemStack(Material.GOLDEN_HELMET, 1));
        armour3.add(new ItemStack(Material.GOLDEN_CHESTPLATE, 1));
        armour3.add(new ItemStack(Material.GOLDEN_LEGGINGS, 1));
        armour3.add(new ItemStack(Material.GOLDEN_BOOTS, 1));
        
        // Armor tier 4 (Iron)
        armour4.add(new ItemStack(Material.IRON_HELMET, 1));
        armour4.add(new ItemStack(Material.IRON_CHESTPLATE, 1));
        armour4.add(new ItemStack(Material.IRON_LEGGINGS, 1));
        armour4.add(new ItemStack(Material.IRON_BOOTS, 1));
        
        // Armor tier 5 (Diamond)
        armour5.add(new ItemStack(Material.DIAMOND_HELMET, 1));
        armour5.add(new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
        armour5.add(new ItemStack(Material.DIAMOND_LEGGINGS, 1));
        armour5.add(new ItemStack(Material.DIAMOND_BOOTS, 1));
        
        // Weapons tier 1
        weapons1.add(new ItemStack(Material.WOODEN_SWORD, 1));
        weapons1.add(new ItemStack(Material.STONE_SWORD, 1));
        weapons1.add(new ItemStack(Material.WOODEN_AXE, 1));
        
        // Weapons tier 2
        weapons2.add(new ItemStack(Material.IRON_SWORD, 1));
        weapons2.add(new ItemStack(Material.DIAMOND_SWORD, 1));
        weapons2.add(new ItemStack(Material.IRON_AXE, 1));
    }

    /**
     * Removes all active potion effects from a player.
     * Used when preparing player for lobby or cleanup.
     */
    public void clearEffects(Player all) {
        // Remove all configured potion effects
        int chestitem = 1;
        while (Settings.chestdata.contains("pg.potions." + chestitem)) {
            PotionEffect effect = (PotionEffect) Settings.chestdata.get("pg.potions." + chestitem);
            if (effect != null) {
                all.removePotionEffect(effect.getType());
            }
            chestitem++;
        }
        
        // Remove all known potion effect types
        PotionEffectType[] effectTypes = {
            PotionEffectType.SPEED, PotionEffectType.SLOWNESS, PotionEffectType.STRENGTH,
            PotionEffectType.INSTANT_HEALTH, PotionEffectType.INSTANT_DAMAGE,
            PotionEffectType.JUMP_BOOST, PotionEffectType.NAUSEA, PotionEffectType.REGENERATION,
            PotionEffectType.RESISTANCE, PotionEffectType.FIRE_RESISTANCE, PotionEffectType.INVISIBILITY,
            PotionEffectType.BLINDNESS, PotionEffectType.HUNGER, PotionEffectType.WEAKNESS,
            PotionEffectType.POISON, PotionEffectType.WITHER, PotionEffectType.ABSORPTION,
            PotionEffectType.GLOWING, PotionEffectType.HEALTH_BOOST, PotionEffectType.DOLPHINS_GRACE,
            PotionEffectType.NIGHT_VISION, PotionEffectType.WATER_BREATHING
        };
        
        for (PotionEffectType type : effectTypes) {
            if (type != null && all.hasPotionEffect(type)) {
                all.removePotionEffect(type);
            }
        }
    }

    /**
     * Called when a player joins a lobby.
     * Sets up player state: inventory, effects, scoreboard, and teleports to lobby.
     */
    public void onJoinLobby(Player p, String s) {
        try {
            Lobby lobby = getLobbyById(Integer.parseInt(s));
            if (lobby != null) {
                game.setPlayerLobby(p, s);
                lobby.join(p);
            }
        } catch (NumberFormatException ignored) {
            // invalid lobby id
        }
    }

    /**
     * Called when a player leaves a lobby.
     * Restores player state: inventory, location, effects, and updates lobby state.
     */
    public void onLeaveLobby(Player p, String s) {
        try {
            Lobby lobby = getLobbyById(Integer.parseInt(s));
            if (lobby != null) {
                lobby.leave(p);
            }
        } catch (NumberFormatException ignored) {
            // invalid lobby id
        }
        game.removePlayerLobby(p);
        game.removeSpectatorLobby(p);
    }

    public ArrayList<Player> getChannel(Player player) {
        String channelName = playerChannel.get(player);
        return channels.get(channelName);
    }

    public ItemStack getCoin() {
        return coin;
    }

    public ItemStack getBottle() {
        return bottle;
    }

    public int getMaxPlayers() {
        return configManager.getMaxPlayers();
    }

    public int getMinPlayers() {
        return configManager.getMinPlayers();
    }

    public int getTeamSize() {
        return configManager.getTeamSize();
    }

    public int getPlayerAmount() {
        return configManager.getPlayerAmount();
    }

    public int getTeamAmount() {
        return configManager.getTeamAmount();
    }

    public int getCountdown() {
        return configManager.getCountdown();
    }

    public void setCountdown(int countdown) {
        configManager.setCountdown(countdown);
    }

    public int getActivePotions() {
        return configManager.getActivePotions();
    }

    public int getActiveKits() {
        return configManager.getActiveKits();
    }

    public int getKillReward() {
        return configManager.getKillReward();
    }

    public boolean isEnableRewards() {
        return configManager.isEnableRewards();
    }

    public boolean isStartOnJoin() {
        return configManager.isStartOnJoin();
    }

    public boolean isActivateTeams() {
        return configManager.isActivateTeams();
    }
    
    public boolean isActivateTeams(String lobbyId) {
        return lobbyStateManager.isActivateTeams(lobbyId);
    }

    public boolean isActivateShop() {
        return configManager.isActivateShop();
    }

    public boolean isActivateAirdrops() {
        return configManager.isActivateAirdrops();
    }

    public boolean isGameServer() {
        return configManager.isGameServer();
    }

    public boolean isAllowOutsideChat() {
        return configManager.isAllowOutsideChat();
    }

    public boolean isActivateScoreboard() {
        return configManager.isActivateScoreboard();
    }

    public boolean isFriendlyFire() {
        return configManager.isFriendlyFire();
    }

    public boolean isMove() {
        return configManager.isMove();
    }

    public boolean isPause() {
        return configManager.isPause();
    }

    public boolean isBuild() {
        return configManager.isBuild();
    }

    public boolean isAddlobby() {
        return addlobby;
    }

    public void setAddlobby(boolean addlobby) {
        this.addlobby = addlobby;
    }

    public boolean isAddarena() {
        return addarena;
    }

    public void setAddarena(boolean addarena) {
        this.addarena = addarena;
    }

    public boolean isDellobby() {
        return dellobby;
    }

    public void setDellobby(boolean dellobby) {
        this.dellobby = dellobby;
    }

    public boolean isDelarena() {
        return delarena;
    }

    public void setDelarena(boolean delarena) {
        this.delarena = delarena;
    }

    // ===== PHASE 7.5: Kit Delegation Methods =====
    
    /**
     * Check if player has a kit assignment
     */
    public boolean hasPlayerKit(Player player) {
        return kitplayernames.containsKey(player);
    }
    
    // ===== PHASE 7.5: Lobby Airdrop Delegation =====
    
    /**
     * Check if airdrop is activated for a lobby
     */
    public boolean isLobbyActivateAirdrops(String lobbyId) {
        try {
            Lobby lobby = getLobbyById(Integer.parseInt(lobbyId));
            if (lobby != null) {
                return lobby.isActivateAirdrops();
            }
        } catch (NumberFormatException e) {
            // ignored
        }
        return false;
    }
    
    // ===== PHASE 7.5: Lobby Teams Delegation =====
    
    /**
     * Get teams map for a specific lobby
     */
    public HashMap<Integer, Integer> getLobbiesTeamsMap(String lobbyId) {
        try {
            Lobby lobby = getLobbyById(Integer.parseInt(lobbyId));
            if (lobby != null) {
                return lobby.getTeamsMap();
            }
        } catch (NumberFormatException e) {
            // ignored
        }
        return new HashMap<>();
    }
    
    /**
     * Get team player names map for a specific lobby
     */
    public HashMap<Player, String> getLobbiesTeamPlayerNamesMap(String lobbyId) {
        try {
            Lobby lobby = getLobbyById(Integer.parseInt(lobbyId));
            if (lobby != null) {
                return lobby.getTeamPlayerNamesMap();
            }
        } catch (NumberFormatException e) {
            // ignored
        }
        return new HashMap<>();
    }
    
    // ===== PHASE 7.5: Lobby Placed Blocks Delegation =====
    
    /**
     * Add placed block to a lobby
     */
    public void addLobbyPlacedBlock(String lobbyId, Location loc, Object material) {
        try {
            Lobby lobby = getLobbyById(Integer.parseInt(lobbyId));
            if (lobby != null) {
                lobby.addPlacedBlock(loc, material);
            }
        } catch (NumberFormatException e) {
            // ignored
        }
    }
    
    // ===== PHASE 7.5: Game Players Delegation =====
    
    /**
     * Get players in a specific lobby (delegation method)
     */
    public ArrayList<Player> getPlayersInLobby(String lobbyId) {
        return game.getPlayersInLobby(lobbyId);
    }
    
}




