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
    public final ArrayList<String> chatmessages;
    public final ArrayList<String> shop;
    public final ArrayList<PotionEffect> shoppotion;
    public final ArrayList<ItemStack> shoppotiontype;
    public final ArrayList<String> shopkit;
    public final ArrayList<Integer> shopcost;
    public final ArrayList<Integer> shopsale;
    public final ArrayList<String> kits;
    public final HashMap<String, Integer> kitplayers;
    public final HashMap<String, Boolean> lobbyCheckArenas;
    public final HashMap<String, Boolean> lobbyActivateTeams;
    public final HashMap<String, Boolean> lobbyActivateKits;
    public final HashMap<String, Boolean> lobbyActivateShop;
    public final HashMap<String, Boolean> lobbyActivateAirdrops;
    public final HashMap<String, Boolean> lobbyJoinable;
    public final HashMap<String, Boolean> lobbyForcearena;
    public final HashMap<String, Boolean> lobbyDeathmatch;
    public final HashMap<String, Boolean> lobbyMove;
    public final HashMap<String, Boolean> lobbyVoteallowed;
    public final HashMap<String, Boolean> lobbyTeamallowed;
    public final HashMap<String, Boolean> lobbyKitallowed;
    public final HashMap<String, Integer> lobbyAmount;
    public final HashMap<String, GameStates> lobbyStates;
    public final HashMap<String, Boolean> lobbyTickstarted;
    public final HashMap<String, Boolean> lobbyBuild;
    public final HashMap<String, Boolean> lobbyPause;
    public final HashMap<String, String> lobbyVote;
    public final HashMap<String, String> lobbyVotedarena;
    public final HashMap<String, Integer> lobbyteamSize;
    public final HashMap<String, Integer> lobbymaxPlayers;
    public final HashMap<String, Integer> lobbyminPlayers;
    public final HashMap<String, Integer> lobbyteamAmount;
    public final HashMap<String, Integer> lobbyroundTime;
    public final HashMap<String, Integer> lobbyroundTimeSeconds;
    public final HashMap<String, HashMap<Integer, Integer>> lobbyteams;
    public final HashMap<Player, String> lobbyteamplayernamesdata;
    public final HashMap<String, HashMap<Player, String>> lobbyteamplayernames;
    public final HashMap<String, HashMap<String, Integer>> lobbyvotes;
    public final HashMap<Player, String> lobbyvoteplayernamesdata;
    public final HashMap<String, HashMap<Player, String>> lobbyvoteplayernames;
    public final HashMap<String, HashMap<Location, Block>> lobbyLiquidPlaced;
    public final HashMap<String, HashMap<Location, Material>> lobbyPlacedBlocks;
    public final HashMap<String, HashMap<Location, Material>> lobbyBreakedBlocks;
    public final HashMap<String, HashMap<Location, BlockData>> lobbyWaterBlocks;
    public final HashMap<Location, Block> lobbyLiquidPlacedData;
    public final HashMap<Location, Material> lobbyPlacedBlocksData;
    public final HashMap<Location, Material> lobbyBreakedBlocksData;
    public final HashMap<Location, BlockData> lobbyWaterBlocksData;
    public final ItemStack coin;

    public boolean activateMysql;
    public int countdown;
    public boolean startOnJoin;
    public boolean compassOnSpawn;
    public boolean allowOutsideChat;
    public boolean changeGamerules;
    public boolean activateTeams;
    public boolean activateKits;
    public boolean activateShop;
    public boolean activateAirdrops;
    public boolean gameServer;
    public int maxPlayers;
    public int minPlayers;
    public int teamSize;
    public int teamAmount;
    public int roundTime;
    public int roundTimeSeconds;
    public int activePotions;
    public int activeKits;
    public boolean activateScoreboard;
    public boolean friendlyFire;
    public boolean joinStarted;
    public boolean activateDeathmatch;
    public boolean enableRewards;
    public boolean broadcastStarting;
    public int winningReward;
    public int killReward;
    public String language;
    public String host;
    public String port;
    public String database;
    public String user;
    public String password;

    public EnableBootstrapContext(
            ArrayList<String> chatmessages, ArrayList<String> shop, ArrayList<PotionEffect> shoppotion,
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
        this.chatmessages = chatmessages;
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
}
