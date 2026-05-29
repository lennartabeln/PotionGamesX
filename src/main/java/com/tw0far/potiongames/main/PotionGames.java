package com.tw0far.potiongames.main;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.tw0far.potiongames.bootstrap.BootstrapInitializer;
import com.tw0far.potiongames.bootstrap.ChestLootInitializer;
import com.tw0far.potiongames.bootstrap.EnableBootstrapContext;
import com.tw0far.potiongames.bootstrap.EnableBootstrapInitializer;
import com.tw0far.potiongames.commands.CommandDispatcher;
import com.tw0far.potiongames.handlers.JoinLobbyHandler;
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public class PotionGames extends JavaPlugin {

    //New
    private static PotionGames instance;
    private final Game game = new Game();
    private IConfigurationManager configManager;
    private ILobbyStateManager lobbyStateManager;
    private IPlayerStateManager playerStateManager;
    private IArenaStateManager arenaStateManager;
    private IItemStateManager itemStateManager;
    private IBlockStateManager blockStateManager;
    private ISetupStateManager setupStateManager;
    private IDatabaseManager databaseManager;
    public static PotionGames getInstance() { return instance; }
    public Game getGame() { return game; }
    public IConfigurationManager getConfigManager() { return configManager; }
    public ILobbyStateManager getLobbyStateManager() { return lobbyStateManager; }
    public IPlayerStateManager getPlayerStateManager() { return playerStateManager; }
    public IArenaStateManager getArenaStateManager() { return arenaStateManager; }
    public IItemStateManager getItemStateManager() { return itemStateManager; }
    public IBlockStateManager getBlockStateManager() { return blockStateManager; }
    public ISetupStateManager getSetupStateManager() { return setupStateManager; }
    public IDatabaseManager getDatabaseManager() { return databaseManager; }
    public ISetupHandler getSetupHandler() { return setupHandler; }
    private ISetupHandler setupHandler = new SetupHandler(this);
    private final JoinLobbyHandler joinLobbyHandler = new JoinLobbyHandler(this);
    
    // ===== Delegation Methods for Player Lists =====
    
    /**
     * Get active players from Game.
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

    public java.util.List<String> getTeamed() { return java.util.Collections.unmodifiableList(teamed); }
    public java.util.List<String> getKits() { return java.util.Collections.unmodifiableList(kits); }

    public void replaceKits(java.util.List<String> list) {
        kits.clear();
        if (list != null) kits.addAll(list);
    }

    public void replaceKitplayers(java.util.Map<String, Integer> map) {
        kitplayers.clear();
        if (map != null) kitplayers.putAll(map);
    }
    public java.util.List<String> getChatmessages() { return java.util.Collections.unmodifiableList(chatmessages); }
    /**
     * Replace the plugin's chat messages with the provided list (defensive copy).
     * Used during initialization to seed default messages without exposing a mutable view.
     */
    public void replaceChatmessages(java.util.List<String> messages) {
        chatmessages.clear();
        if (messages != null) {
            chatmessages.addAll(messages);
        }
    }
    public java.util.List<String> getShop() { return java.util.Collections.unmodifiableList(shop); }
    public ArrayList<PotionEffect> getShoppotion() { return shoppotion; }
    public ArrayList<ItemStack> getShoppotiontype() { return shoppotiontype; }
    public java.util.List<String> getShopkit() { return java.util.Collections.unmodifiableList(shopkit); }
    public java.util.List<Integer> getShopcost() { return java.util.Collections.unmodifiableList(shopcost); }
    public java.util.List<Integer> getShopsale() { return java.util.Collections.unmodifiableList(shopsale); }

    // Replace methods for shop-related collections (used during bootstrap to populate defaults)
    public void replaceShop(java.util.List<String> list) {
        shop.clear();
        if (list != null) shop.addAll(list);
    }

    public void replaceShoppotion(java.util.List<PotionEffect> list) {
        shoppotion.clear();
        if (list != null) shoppotion.addAll(list);
    }

    public void replaceShoppotiontype(java.util.List<ItemStack> list) {
        shoppotiontype.clear();
        if (list != null) shoppotiontype.addAll(list);
    }

    public void replaceShopkit(java.util.List<String> list) {
        shopkit.clear();
        if (list != null) shopkit.addAll(list);
    }

    public void replaceShopcost(java.util.List<Integer> list) {
        shopcost.clear();
        if (list != null) shopcost.addAll(list);
    }

    public void replaceShopsale(java.util.List<Integer> list) {
        shopsale.clear();
        if (list != null) shopsale.addAll(list);
    }
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

    public HashMap<Integer, String> getRank() { return rank; }

    public java.util.Map<String, Integer> getKitplayers() { return java.util.Collections.unmodifiableMap(kitplayers); }
    public java.util.Map<String, java.util.Map<String, Integer>> getLobbyvotes() { return java.util.Collections.unmodifiableMap(lobbyvotes); }
    /**
     * Get spectator players from Game.
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
    // These delegate to the item state manager for global shop/loot items
    
    public ArrayList<String> getGameShopItems() {
        return new ArrayList<>(itemStateManager.getShopItems());
    }
    
    public ArrayList<String> getGameShopKits() {
        ArrayList<String> kits = new ArrayList<>();
        for (int i = 0; i < itemStateManager.getShopItemCount(); i++) {
            kits.add(itemStateManager.getShopKit(i));
        }
        return kits;
    }
    
    public ArrayList<Integer> getGameShopCosts() {
        ArrayList<Integer> costs = new ArrayList<>();
        for (int i = 0; i < itemStateManager.getShopItemCount(); i++) {
            costs.add(itemStateManager.getShopCost(i));
        }
        return costs;
    }
    
    public ArrayList<Integer> getGameShopSales() {
        ArrayList<Integer> sales = new ArrayList<>();
        for (int i = 0; i < itemStateManager.getShopItemCount(); i++) {
            sales.add(itemStateManager.getShopSale(i));
        }
        return sales;
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
    // These delegate to the item state manager for global loot tables
    
    public ArrayList<ItemStack> getFoodTier1() {
        return new ArrayList<>(itemStateManager.getFoods(1));
    }
    
    public ArrayList<ItemStack> getFoodTier2() {
        return new ArrayList<>(itemStateManager.getFoods(2));
    }
    
    public ArrayList<ItemStack> getArmourTier1() {
        return new ArrayList<>(itemStateManager.getArmors(1));
    }
    
    public ArrayList<ItemStack> getArmourTier2() {
        return new ArrayList<>(itemStateManager.getArmors(2));
    }
    
    public ArrayList<ItemStack> getArmourTier3() {
        return new ArrayList<>(itemStateManager.getArmors(3));
    }
    
    public ArrayList<ItemStack> getArmourTier4() {
        return new ArrayList<>(itemStateManager.getArmors(4));
    }
    
    public ArrayList<ItemStack> getArmourTier5() {
        return new ArrayList<>(itemStateManager.getArmors(5));
    }
    
    public ArrayList<ItemStack> getWeaponsTier1() {
        return new ArrayList<>(itemStateManager.getWeapons(1));
    }
    
    public ArrayList<ItemStack> getWeaponsTier2() {
        return new ArrayList<>(itemStateManager.getWeapons(2));
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
        // Bind configuration manager to legacy Settings read-through so static accessors can prefer the manager
        com.tw0far.potiongames.models.Settings.bindConfigManager(configManager);
        
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

        new BootstrapInitializer(this).initialize();
        new ChestLootInitializer(this).seed();

        EnableBootstrapContext bootstrapContext = createEnableBootstrapContext();
        new EnableBootstrapInitializer(this).initialize(bootstrapContext);
        applyEnableBootstrapContext(bootstrapContext);
        if (activateMysql && !databaseManager.isConnected()) {
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            Bukkit.getConsoleSender().sendMessage(Messages.PluginStarted());
        }
        new UpdateChecker(this, 87633).getVersion(version -> {
            if (getPluginMeta().getVersion().equalsIgnoreCase(version)) {
                Bukkit.getConsoleSender().sendMessage(Messages.UpdateNotAvailable().append(Component.text(" " + getPluginMeta().getVersion()).color(NamedTextColor.GRAY)));
            } else {
                Bukkit.getConsoleSender().sendMessage(Messages.UpdateAvailable(getPluginMeta().getVersion(), version));
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
            try (ResultSet rs = databaseManager.query("SELECT UUID FROM Stats ORDER BY WINS DESC LIMIT 3")) {
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
                    // Use SkullUtil to set owner using modern profile API where available
                    com.tw0far.potiongames.util.SkullUtil.setSkullOwner(skull, uuid);
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

    private EnableBootstrapContext createEnableBootstrapContext() {
        return new EnableBootstrapContext(
                new ArrayList<>(chatmessages), new ArrayList<>(shop), new ArrayList<>(shoppotion), new ArrayList<>(shoppotiontype), new ArrayList<>(shopkit), new ArrayList<>(shopcost), new ArrayList<>(shopsale), new ArrayList<>(kits),
                new HashMap<>(kitplayers), new HashMap<>(lobbyCheckArenas), new HashMap<>(lobbyActivateTeams), new HashMap<>(lobbyActivateKits), new HashMap<>(lobbyActivateShop),
                new HashMap<>(lobbyActivateAirdrops), new HashMap<>(lobbyJoinable), new HashMap<>(lobbyForcearena), new HashMap<>(lobbyDeathmatch), new HashMap<>(lobbyMove),
                new HashMap<>(lobbyVoteallowed), new HashMap<>(lobbyTeamallowed), new HashMap<>(lobbyKitallowed), new HashMap<>(lobbyAmount), new HashMap<>(lobbyStates), new HashMap<>(lobbyTickstarted), new HashMap<>(lobbyBuild),
                new HashMap<>(lobbyPause), new HashMap<>(lobbyVote), new HashMap<>(lobbyVotedarena), new HashMap<>(lobbyteamSize), new HashMap<>(lobbymaxPlayers), new HashMap<>(lobbyminPlayers),
                new HashMap<>(lobbyteamAmount), new HashMap<>(lobbyroundTime), new HashMap<>(lobbyroundTimeSeconds), new HashMap<>(lobbyteams), new HashMap<>(lobbyteamplayernamesdata),
                new HashMap<>(lobbyteamplayernames), new HashMap<>(lobbyvotes), new HashMap<>(lobbyvoteplayernamesdata), new HashMap<>(lobbyvoteplayernames), new HashMap<>(lobbyLiquidPlaced),
                new HashMap<>(lobbyPlacedBlocks), new HashMap<>(lobbyBreakedBlocks), new HashMap<>(lobbyWaterBlocks), new HashMap<>(lobbyLiquidPlacedData), new HashMap<>(lobbyPlacedBlocksData),
                new HashMap<>(lobbyBreakedBlocksData), new HashMap<>(lobbyWaterBlocksData), coin == null ? null : coin.clone(), activateMysql, countdown, startOnJoin,
                compassOnSpawn, allowOutsideChat, changeGamerules, activateTeams, activateKits, activateShop,
                activateAirdrops, gameServer, maxPlayers, minPlayers, teamSize, teamAmount, roundTime,
                roundTimeSeconds, activePotions, activeKits, activateScoreboard, friendlyFire, joinStarted,
                activateDeathmatch, enableRewards, broadcastStarting, winningReward, killReward, language,
                host, port, database, user, password
        );
    }

    private void applyEnableBootstrapContext(EnableBootstrapContext context) {
        activateMysql = context.isActivateMysql();
        countdown = context.getCountdown();
        startOnJoin = context.isStartOnJoin();
        compassOnSpawn = context.isCompassOnSpawn();
        allowOutsideChat = context.isAllowOutsideChat();
        changeGamerules = context.isChangeGamerules();
        activateTeams = context.isActivateTeams();
        activateKits = context.isActivateKits();
        activateShop = context.isActivateShop();
        activateAirdrops = context.isActivateAirdrops();
        gameServer = context.isGameServer();
        maxPlayers = context.getMaxPlayers();
        minPlayers = context.getMinPlayers();
        teamSize = context.getTeamSize();
        teamAmount = context.getTeamAmount();
        roundTime = context.getRoundTime();
        roundTimeSeconds = context.getRoundTimeSeconds();
        activePotions = context.getActivePotions();
        activeKits = context.getActiveKits();
        activateScoreboard = context.isActivateScoreboard();
        friendlyFire = context.isFriendlyFire();
        joinStarted = context.isJoinStarted();
        activateDeathmatch = context.isActivateDeathmatch();
        enableRewards = context.isEnableRewards();
        broadcastStarting = context.isBroadcastStarting();
        winningReward = context.getWinningReward();
        killReward = context.getKillReward();
        language = context.getLanguage();
        host = context.getHost();
        port = context.getPort();
        database = context.getDatabase();
        user = context.getUser();
        password = context.getPassword();
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

        if (gameServer && startOnJoin) {
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (all == null) continue;
                all.kick(Messages.ServerStopped());
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
        Bukkit.getConsoleSender().sendMessage(Messages.PluginStopped());
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        var rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = java.util.Objects.requireNonNull(rsp.getProvider(), "Vault provider returned null");
        return true;
    }

    // joinChannel() deleted - moved to GameManager

    // spawnFirework() deleted - moved to GameManager

    // chestData() deleted - moved to initialization

    // clearEffects() deleted - moved to GameManager

    // setGameRules() deleted - moved to GameManager

    public void onLeave(Player p) {
        game.removeActivePlayer(p);
        game.removeSpectatorPlayer(p);
    }

    public boolean playerExists(String uuid) {
        return databaseManager.playerExists(uuid);
    }

    public void createPlayer(String uuid) {
        databaseManager.createPlayer(uuid);
    }

    public int getKills(String uuid) {
        return databaseManager.getKills(uuid);
    }

    public int getDeaths(String uuid) {
        return databaseManager.getDeaths(uuid);
    }

    public double getKD(String uuid) {
        return databaseManager.getKD(uuid);
    }

    public int getWins(String uuid) {
        return databaseManager.getWins(uuid);
    }

    public int getLosses(String uuid) {
        return databaseManager.getLosses(uuid);
    }

    public int getRounds(String uuid) {
        return databaseManager.getRounds(uuid);
    }

    public void setKills(String uuid, int kills) {
        databaseManager.setKills(uuid, kills);
    }

    public void setDeaths(String uuid, int deaths) {
        databaseManager.setDeaths(uuid, deaths);
    }

    public void setKD(String uuid, double kd) {
        databaseManager.setKD(uuid, kd);
    }

    public void setWins(String uuid, int wins) {
        databaseManager.setWins(uuid, wins);
    }

    public void setLosses(String uuid, int losses) {
        databaseManager.setLosses(uuid, losses);
    }

    public void setRounds(String uuid, int rounds) {
        databaseManager.setRounds(uuid, rounds);
    }

    public void addKills(String uuid, int kills) {
        databaseManager.addKills(uuid, kills);
    }

    public void addDeaths(String uuid, int deaths) {
        databaseManager.addDeaths(uuid, deaths);
    }

    public void addWins(String uuid, int wins) {
        databaseManager.addWins(uuid, wins);
    }

    public void addLosses(String uuid, int losses) {
        databaseManager.addLosses(uuid, losses);
    }

    // ===== RESTORED STUB METHODS (Delegated or Simple Implementations) =====
    
    /**
     * Joins a player to a communication channel.
     * Maintains chat channels for targeted messaging.
     */
    public void joinChannel(Player player, String channelName) {
        if (playerChannel.get(player) != null) {
            String prevChannel = playerChannel.get(player);
            ArrayList<Player> previousPlayers = channels.get(prevChannel);
            if (previousPlayers != null) {
                previousPlayers.remove(player);
                channels.put(prevChannel, previousPlayers);
            }
            playerChannel.remove(player);
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
        new ChestLootInitializer(this).seed();
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
        joinLobbyHandler.onJoinLobby(p, s);
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

    public void setAddlobby(boolean addlobby) {
        this.addlobby = addlobby;
    }

    public void setAddarena(boolean addarena) {
        this.addarena = addarena;
    }

    public void setDellobby(boolean dellobby) {
        this.dellobby = dellobby;
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



