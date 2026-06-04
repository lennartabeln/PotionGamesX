package com.tw0far.potiongames.bootstrap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;
import com.tw0far.potiongames.models.GameStates;

public final class EnableBootstrapContext {
    private final ArrayList<String> shop;
    private final ArrayList<PotionEffect> shoppotion;
    private final ArrayList<ItemStack> shoppotiontype;
    private final ArrayList<String> shopkit;
    private final ArrayList<Integer> shopcost;
    private final ArrayList<Integer> shopsale;
    private final ArrayList<String> kits;
    private final HashMap<String, Integer> kitplayers;
    private final HashMap<String, Boolean> lobbyCheckArenas;
    private final HashMap<String, Boolean> lobbyActivateTeams;
    private final HashMap<String, Boolean> lobbyActivateKits;
    private final HashMap<String, Boolean> lobbyActivateShop;
    private final HashMap<String, Boolean> lobbyActivateAirdrops;
    private final HashMap<String, Boolean> lobbyJoinable;
    private final HashMap<String, Boolean> lobbyForcearena;
    private final HashMap<String, Boolean> lobbyDeathmatch;
    private final HashMap<String, Boolean> lobbyMove;
    private final HashMap<String, Boolean> lobbyVoteallowed;
    private final HashMap<String, Boolean> lobbyTeamallowed;
    private final HashMap<String, Boolean> lobbyKitallowed;
    private final HashMap<String, Integer> lobbyAmount;
    private final HashMap<String, GameStates> lobbyStates;
    private final HashMap<String, Boolean> lobbyTickstarted;
    private final HashMap<String, Boolean> lobbyBuild;
    private final HashMap<String, Boolean> lobbyPause;
    private final HashMap<String, String> lobbyVote;
    private final HashMap<String, String> lobbyVotedarena;
    private final HashMap<String, Integer> lobbyteamSize;
    private final HashMap<String, Integer> lobbymaxPlayers;
    private final HashMap<String, Integer> lobbyminPlayers;
    private final HashMap<String, Integer> lobbyteamAmount;
    private final HashMap<String, Integer> lobbyroundTime;
    private final HashMap<String, Integer> lobbyroundTimeSeconds;
    private final HashMap<String, HashMap<Integer, Integer>> lobbyteams;
    private final HashMap<Player, String> lobbyteamplayernamesdata;
    private final HashMap<String, HashMap<Player, String>> lobbyteamplayernames;
    private final HashMap<String, HashMap<String, Integer>> lobbyvotes;
    private final HashMap<Player, String> lobbyvoteplayernamesdata;
    private final HashMap<String, HashMap<Player, String>> lobbyvoteplayernames;
    private final HashMap<String, HashMap<Location, Block>> lobbyLiquidPlaced;
    private final HashMap<String, HashMap<Location, Material>> lobbyPlacedBlocks;
    private final HashMap<String, HashMap<Location, Material>> lobbyBreakedBlocks;
    private final HashMap<String, HashMap<Location, BlockData>> lobbyWaterBlocks;
    private final HashMap<Location, Block> lobbyLiquidPlacedData;
    private final HashMap<Location, Material> lobbyPlacedBlocksData;
    private final HashMap<Location, Material> lobbyBreakedBlocksData;
    private final HashMap<Location, BlockData> lobbyWaterBlocksData;
    private final ItemStack coin;

    // mutable configuration fields
    private boolean activateMysql;
    private int countdown;
    private boolean startOnJoin;
    private boolean compassOnSpawn;
    private boolean allowOutsideChat;
    private boolean changeGamerules;
    private boolean activateTeams;
    private boolean activateKits;
    private boolean activateShop;
    private boolean activateAirdrops;
    private boolean gameServer;
    private int maxPlayers;
    private int minPlayers;
    private int teamSize;
    private int teamAmount;
    private int roundTime;
    private int roundTimeSeconds;
    private int activePotions;
    private int activeKits;
    private boolean activateScoreboard;
    private boolean friendlyFire;
    private boolean joinStarted;
    private boolean activateDeathmatch;
    private boolean enableRewards;
    private boolean broadcastStarting;
    private int winningReward;
    private int killReward;
    private String language;
    private String host;
    private String port;
    private String database;
    private String user;
    private String password;

    public EnableBootstrapContext(
            ArrayList<String> shop, ArrayList<PotionEffect> shoppotion,
            ArrayList<ItemStack> shoppotiontype, ArrayList<String> shopkit, ArrayList<Integer> shopcost,
            ArrayList<Integer> shopsale, ArrayList<String> kits, HashMap<String, Integer> kitplayers,
            HashMap<String, Boolean> lobbyCheckArenas, HashMap<String, Boolean> lobbyActivateTeams,
            HashMap<String, Boolean> lobbyActivateKits, HashMap<String, Boolean> lobbyActivateShop,
            HashMap<String, Boolean> lobbyActivateAirdrops, HashMap<String, Boolean> lobbyJoinable,
            HashMap<String, Boolean> lobbyForcearena, HashMap<String, Boolean> lobbyDeathmatch,
            HashMap<String, Boolean> lobbyMove, HashMap<String, Boolean> lobbyVoteallowed,
            HashMap<String, Boolean> lobbyTeamallowed, HashMap<String, Boolean> lobbyKitallowed,
            HashMap<String, Integer> lobbyAmount, HashMap<String, GameStates> lobbyStates,
            HashMap<String, Boolean> lobbyTickstarted,
            HashMap<String, Boolean> lobbyBuild, HashMap<String, Boolean> lobbyPause, HashMap<String, String> lobbyVote,
            HashMap<String, String> lobbyVotedarena, HashMap<String, Integer> lobbyteamSize,
            HashMap<String, Integer> lobbymaxPlayers, HashMap<String, Integer> lobbyminPlayers,
            HashMap<String, Integer> lobbyteamAmount, HashMap<String, Integer> lobbyroundTime,
            HashMap<String, Integer> lobbyroundTimeSeconds, HashMap<String, HashMap<Integer, Integer>> lobbyteams,
            HashMap<Player, String> lobbyteamplayernamesdata, HashMap<String, HashMap<Player, String>> lobbyteamplayernames,
            HashMap<String, HashMap<String, Integer>> lobbyvotes, HashMap<Player, String> lobbyvoteplayernamesdata,
            HashMap<String, HashMap<Player, String>> lobbyvoteplayernames,
            HashMap<String, HashMap<Location, Block>> lobbyLiquidPlaced,
            HashMap<String, HashMap<Location, Material>> lobbyPlacedBlocks,
            HashMap<String, HashMap<Location, Material>> lobbyBreakedBlocks,
            HashMap<String, HashMap<Location, BlockData>> lobbyWaterBlocks,
            HashMap<Location, Block> lobbyLiquidPlacedData, HashMap<Location, Material> lobbyPlacedBlocksData,
            HashMap<Location, Material> lobbyBreakedBlocksData, HashMap<Location, BlockData> lobbyWaterBlocksData,
            ItemStack coin, boolean activateMysql, int countdown, boolean startOnJoin, boolean compassOnSpawn,
            boolean allowOutsideChat, boolean changeGamerules, boolean activateTeams, boolean activateKits,
            boolean activateShop, boolean activateAirdrops, boolean gameServer, int maxPlayers, int minPlayers,
            int teamSize, int teamAmount, int roundTime, int roundTimeSeconds, int activePotions, int activeKits,
            boolean activateScoreboard, boolean friendlyFire, boolean joinStarted, boolean activateDeathmatch,
            boolean enableRewards, boolean broadcastStarting, int winningReward, int killReward, String language,
            String host, String port, String database, String user, String password
    ) {
        this.shop = shop;
        this.shoppotion = shoppotion;
        this.shoppotiontype = shoppotiontype;
        this.shopkit = shopkit;
        this.shopcost = shopcost;
        this.shopsale = shopsale;
        this.kits = kits;
        this.kitplayers = kitplayers;
        this.lobbyCheckArenas = lobbyCheckArenas;
        this.lobbyActivateTeams = lobbyActivateTeams;
        this.lobbyActivateKits = lobbyActivateKits;
        this.lobbyActivateShop = lobbyActivateShop;
        this.lobbyActivateAirdrops = lobbyActivateAirdrops;
        this.lobbyJoinable = lobbyJoinable;
        this.lobbyForcearena = lobbyForcearena;
        this.lobbyDeathmatch = lobbyDeathmatch;
        this.lobbyMove = lobbyMove;
        this.lobbyVoteallowed = lobbyVoteallowed;
        this.lobbyTeamallowed = lobbyTeamallowed;
        this.lobbyKitallowed = lobbyKitallowed;
        this.lobbyAmount = lobbyAmount;
        this.lobbyStates = lobbyStates;
        this.lobbyTickstarted = lobbyTickstarted;
        this.lobbyBuild = lobbyBuild;
        this.lobbyPause = lobbyPause;
        this.lobbyVote = lobbyVote;
        this.lobbyVotedarena = lobbyVotedarena;
        this.lobbyteamSize = lobbyteamSize;
        this.lobbymaxPlayers = lobbymaxPlayers;
        this.lobbyminPlayers = lobbyminPlayers;
        this.lobbyteamAmount = lobbyteamAmount;
        this.lobbyroundTime = lobbyroundTime;
        this.lobbyroundTimeSeconds = lobbyroundTimeSeconds;
        this.lobbyteams = lobbyteams;
        this.lobbyteamplayernamesdata = lobbyteamplayernamesdata;
        this.lobbyteamplayernames = lobbyteamplayernames;
        this.lobbyvotes = lobbyvotes;
        this.lobbyvoteplayernamesdata = lobbyvoteplayernamesdata;
        this.lobbyvoteplayernames = lobbyvoteplayernames;
        this.lobbyLiquidPlaced = lobbyLiquidPlaced;
        this.lobbyPlacedBlocks = lobbyPlacedBlocks;
        this.lobbyBreakedBlocks = lobbyBreakedBlocks;
        this.lobbyWaterBlocks = lobbyWaterBlocks;
        this.lobbyLiquidPlacedData = lobbyLiquidPlacedData;
        this.lobbyPlacedBlocksData = lobbyPlacedBlocksData;
        this.lobbyBreakedBlocksData = lobbyBreakedBlocksData;
        this.lobbyWaterBlocksData = lobbyWaterBlocksData;
        this.coin = coin;
        this.activateMysql = activateMysql;
        this.countdown = countdown;
        this.startOnJoin = startOnJoin;
        this.compassOnSpawn = compassOnSpawn;
        this.allowOutsideChat = allowOutsideChat;
        this.changeGamerules = changeGamerules;
        this.activateTeams = activateTeams;
        this.activateKits = activateKits;
        this.activateShop = activateShop;
        this.activateAirdrops = activateAirdrops;
        this.gameServer = gameServer;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.teamSize = teamSize;
        this.teamAmount = teamAmount;
        this.roundTime = roundTime;
        this.roundTimeSeconds = roundTimeSeconds;
        this.activePotions = activePotions;
        this.activeKits = activeKits;
        this.activateScoreboard = activateScoreboard;
        this.friendlyFire = friendlyFire;
        this.joinStarted = joinStarted;
        this.activateDeathmatch = activateDeathmatch;
        this.enableRewards = enableRewards;
        this.broadcastStarting = broadcastStarting;
        this.winningReward = winningReward;
        this.killReward = killReward;
        this.language = language;
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    // Getters for collections - return the mutable collections so the initializer can populate them
    public ArrayList<String> getShop() { return shop; }
    public ArrayList<PotionEffect> getShoppotion() { return shoppotion; }
    public ArrayList<ItemStack> getShoppotiontype() { return shoppotiontype; }
    public ArrayList<String> getShopkit() { return shopkit; }
    public ArrayList<Integer> getShopcost() { return shopcost; }
    public ArrayList<Integer> getShopsale() { return shopsale; }
    public ArrayList<String> getKits() { return kits; }
    public HashMap<String, Integer> getKitplayers() { return kitplayers; }
    public HashMap<String, Boolean> getLobbyCheckArenas() { return lobbyCheckArenas; }
    public HashMap<String, Boolean> getLobbyActivateTeams() { return lobbyActivateTeams; }
    public HashMap<String, Boolean> getLobbyActivateKits() { return lobbyActivateKits; }
    public HashMap<String, Boolean> getLobbyActivateShop() { return lobbyActivateShop; }
    public HashMap<String, Boolean> getLobbyActivateAirdrops() { return lobbyActivateAirdrops; }
    public HashMap<String, Boolean> getLobbyJoinable() { return lobbyJoinable; }
    public HashMap<String, Boolean> getLobbyForcearena() { return lobbyForcearena; }
    public HashMap<String, Boolean> getLobbyDeathmatch() { return lobbyDeathmatch; }
    public HashMap<String, Boolean> getLobbyMove() { return lobbyMove; }
    public HashMap<String, Boolean> getLobbyVoteallowed() { return lobbyVoteallowed; }
    public HashMap<String, Boolean> getLobbyTeamallowed() { return lobbyTeamallowed; }
    public HashMap<String, Boolean> getLobbyKitallowed() { return lobbyKitallowed; }
    public HashMap<String, Integer> getLobbyAmount() { return lobbyAmount; }
    public HashMap<String, GameStates> getLobbyStates() { return lobbyStates; }
    public HashMap<String, Boolean> getLobbyTickstarted() { return lobbyTickstarted; }
    public HashMap<String, Boolean> getLobbyBuild() { return lobbyBuild; }
    public HashMap<String, Boolean> getLobbyPause() { return lobbyPause; }
    public HashMap<String, String> getLobbyVote() { return lobbyVote; }
    public HashMap<String, String> getLobbyVotedarena() { return lobbyVotedarena; }
    public HashMap<String, Integer> getLobbyteamSize() { return lobbyteamSize; }
    public HashMap<String, Integer> getLobbymaxPlayers() { return lobbymaxPlayers; }
    public HashMap<String, Integer> getLobbyminPlayers() { return lobbyminPlayers; }
    public HashMap<String, Integer> getLobbyteamAmount() { return lobbyteamAmount; }
    public HashMap<String, Integer> getLobbyroundTime() { return lobbyroundTime; }
    public HashMap<String, Integer> getLobbyroundTimeSeconds() { return lobbyroundTimeSeconds; }
    public HashMap<String, HashMap<Integer, Integer>> getLobbyteams() { return lobbyteams; }
    public HashMap<Player, String> getLobbyteamplayernamesdata() { return lobbyteamplayernamesdata; }
    public HashMap<String, HashMap<Player, String>> getLobbyteamplayernames() { return lobbyteamplayernames; }
    public HashMap<String, HashMap<String, Integer>> getLobbyvotes() { return lobbyvotes; }
    public HashMap<Player, String> getLobbyvoteplayernamesdata() { return lobbyvoteplayernamesdata; }
    public HashMap<String, HashMap<Player, String>> getLobbyvoteplayernames() { return lobbyvoteplayernames; }
    public HashMap<String, HashMap<Location, Block>> getLobbyLiquidPlaced() { return lobbyLiquidPlaced; }
    public HashMap<String, HashMap<Location, Material>> getLobbyPlacedBlocks() { return lobbyPlacedBlocks; }
    public HashMap<String, HashMap<Location, Material>> getLobbyBreakedBlocks() { return lobbyBreakedBlocks; }
    public HashMap<String, HashMap<Location, BlockData>> getLobbyWaterBlocks() { return lobbyWaterBlocks; }
    public HashMap<Location, Block> getLobbyLiquidPlacedData() { return lobbyLiquidPlacedData; }
    public HashMap<Location, Material> getLobbyPlacedBlocksData() { return lobbyPlacedBlocksData; }
    public HashMap<Location, Material> getLobbyBreakedBlocksData() { return lobbyBreakedBlocksData; }
    public HashMap<Location, BlockData> getLobbyWaterBlocksData() { return lobbyWaterBlocksData; }
    public ItemStack getCoin() { return coin; }

    // Getters and setters for mutable configuration fields
    public boolean isActivateMysql() { return activateMysql; }
    public void setActivateMysql(boolean activateMysql) { this.activateMysql = activateMysql; }
    public int getCountdown() { return countdown; }
    public void setCountdown(int countdown) { this.countdown = countdown; }
    public boolean isStartOnJoin() { return startOnJoin; }
    public void setStartOnJoin(boolean startOnJoin) { this.startOnJoin = startOnJoin; }
    public boolean isCompassOnSpawn() { return compassOnSpawn; }
    public void setCompassOnSpawn(boolean compassOnSpawn) { this.compassOnSpawn = compassOnSpawn; }
    public boolean isAllowOutsideChat() { return allowOutsideChat; }
    public void setAllowOutsideChat(boolean allowOutsideChat) { this.allowOutsideChat = allowOutsideChat; }
    public boolean isChangeGamerules() { return changeGamerules; }
    public void setChangeGamerules(boolean changeGamerules) { this.changeGamerules = changeGamerules; }
    public boolean isActivateTeams() { return activateTeams; }
    public void setActivateTeams(boolean activateTeams) { this.activateTeams = activateTeams; }
    public boolean isActivateKits() { return activateKits; }
    public void setActivateKits(boolean activateKits) { this.activateKits = activateKits; }
    public boolean isActivateShop() { return activateShop; }
    public void setActivateShop(boolean activateShop) { this.activateShop = activateShop; }
    public boolean isActivateAirdrops() { return activateAirdrops; }
    public void setActivateAirdrops(boolean activateAirdrops) { this.activateAirdrops = activateAirdrops; }
    public boolean isGameServer() { return gameServer; }
    public void setGameServer(boolean gameServer) { this.gameServer = gameServer; }
    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }
    public int getMinPlayers() { return minPlayers; }
    public void setMinPlayers(int minPlayers) { this.minPlayers = minPlayers; }
    public int getTeamSize() { return teamSize; }
    public void setTeamSize(int teamSize) { this.teamSize = teamSize; }
    public int getTeamAmount() { return teamAmount; }
    public void setTeamAmount(int teamAmount) { this.teamAmount = teamAmount; }
    public int getRoundTime() { return roundTime; }
    public void setRoundTime(int roundTime) { this.roundTime = roundTime; }
    public int getRoundTimeSeconds() { return roundTimeSeconds; }
    public void setRoundTimeSeconds(int roundTimeSeconds) { this.roundTimeSeconds = roundTimeSeconds; }
    public int getActivePotions() { return activePotions; }
    public void setActivePotions(int activePotions) { this.activePotions = activePotions; }
    public int getActiveKits() { return activeKits; }
    public void setActiveKits(int activeKits) { this.activeKits = activeKits; }
    public boolean isActivateScoreboard() { return activateScoreboard; }
    public void setActivateScoreboard(boolean activateScoreboard) { this.activateScoreboard = activateScoreboard; }
    public boolean isFriendlyFire() { return friendlyFire; }
    public void setFriendlyFire(boolean friendlyFire) { this.friendlyFire = friendlyFire; }
    public boolean isJoinStarted() { return joinStarted; }
    public void setJoinStarted(boolean joinStarted) { this.joinStarted = joinStarted; }
    public boolean isActivateDeathmatch() { return activateDeathmatch; }
    public void setActivateDeathmatch(boolean activateDeathmatch) { this.activateDeathmatch = activateDeathmatch; }
    public boolean isEnableRewards() { return enableRewards; }
    public void setEnableRewards(boolean enableRewards) { this.enableRewards = enableRewards; }
    public boolean isBroadcastStarting() { return broadcastStarting; }
    public void setBroadcastStarting(boolean broadcastStarting) { this.broadcastStarting = broadcastStarting; }
    public int getWinningReward() { return winningReward; }
    public void setWinningReward(int winningReward) { this.winningReward = winningReward; }
    public int getKillReward() { return killReward; }
    public void setKillReward(int killReward) { this.killReward = killReward; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public String getPort() { return port; }
    public void setPort(String port) { this.port = port; }
    public String getDatabase() { return database; }
    public void setDatabase(String database) { this.database = database; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
