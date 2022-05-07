package com.asga.potiongames.main;

import com.asga.potiongames.bstats.Metrics;
import com.asga.potiongames.commands.Commands;
import com.asga.potiongames.events.Events;
import com.asga.potiongames.gamestates.GameStates;
import com.asga.potiongames.updatechecker.UpdateChecker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

public class PotionGames extends JavaPlugin {
    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;
    public final ArrayList<String> worlds = new ArrayList<>();
    public final ArrayList<Player> pgPlayers = new ArrayList<>();
    public final ArrayList<Player> specPlayers = new ArrayList<>();
    public final ArrayList<Player> richkidPlayers = new ArrayList<>();
    public final ArrayList<Player> setupPlayer = new ArrayList<>();
    public final ArrayList<String> arenas = new ArrayList<>();
    public final ArrayList<String> voted = new ArrayList<>();
    public final ArrayList<String> teams = new ArrayList<>();
    public final ArrayList<String> teamed = new ArrayList<>();
    public final ArrayList<String> kits = new ArrayList<>();
    public final ArrayList<String> kited = new ArrayList<>();
    public final ArrayList<String> chatmessages = new ArrayList<>();
    public final ArrayList<String> shop = new ArrayList<>();
    public final ArrayList<PotionEffect> shoppotion = new ArrayList<>();
    public final ArrayList<ItemStack> shoppotiontype = new ArrayList<>();
    public final ArrayList<String> shopkit = new ArrayList<>();
    public final ArrayList<Integer> shopcost = new ArrayList<>();
    public final ArrayList<Integer> shopsale = new ArrayList<>();
    public final ArrayList<Location> rankhead = new ArrayList<>();
    public final ArrayList<Location> ranksign = new ArrayList<>();
    public final ArrayList<ItemStack> food1 = new ArrayList<>();
    public final ArrayList<ItemStack> food2 = new ArrayList<>();
    public final ArrayList<ItemStack> armour1 = new ArrayList<>();
    public final ArrayList<ItemStack> armour2 = new ArrayList<>();
    public final ArrayList<ItemStack> armour3 = new ArrayList<>();
    public final ArrayList<ItemStack> armour4 = new ArrayList<>();
    public final ArrayList<ItemStack> armour5 = new ArrayList<>();
    public final ArrayList<ItemStack> weapons1 = new ArrayList<>();
    public final ArrayList<ItemStack> weapons2 = new ArrayList<>();
    public final ArrayList<PotionEffect> potions = new ArrayList<>();
    public final HashMap<Integer, String> rank = new HashMap<>();
    public final HashMap<String, Integer> votes = new HashMap<>();
    public final HashMap<Player, String> voteplayernames = new HashMap<>();
    public final HashMap<String, Integer> teamplayers = new HashMap<>();
    public final HashMap<Player, String> teamplayernames = new HashMap<>();
    public final HashMap<String, Integer> kitplayers = new HashMap<>();
    public final HashMap<Player, String> kitplayernames = new HashMap<>();
    public final HashMap<Location, Inventory> chests = new HashMap<>();
    public final HashMap<Location, String> lobbychests = new HashMap<>();
    public final HashMap<Location, Inventory> lobbychestsdata = new HashMap<>();
    public final HashMap<Location, Material> placedBlocks = new HashMap<>();
    public final HashMap<Location, Material> breakedBlocks = new HashMap<>();
    public final HashMap<Location, BlockData> waterBlocks = new HashMap<>();
    public final HashMap<Location, Block> liquidPlaced = new HashMap<>();
    public final HashMap<String, ItemStack[]> inv = new HashMap<>();
    public final HashMap<String, ItemStack[]> armor = new HashMap<>();
    public final HashMap<String, Integer> lvl = new HashMap<>();
    public final HashMap<String, Float> exp = new HashMap<>();
    public final HashMap<String, Location> loc = new HashMap<>();
    public final HashMap<String, GameMode> gm = new HashMap<>();
    public final HashMap<String, ArrayList<Player>> channels = new HashMap<>();
    public final HashMap<Player, String> playerChannel = new HashMap<>();
    public final HashMap<Player, String> playerLobby = new HashMap<>();
    public final HashMap<Player, String> specLobby = new HashMap<>();
    public final HashMap<String, String> infoLobby = new HashMap<>();
    public final HashMap<String, Integer> countdownLobby = new HashMap<>();
    public final HashMap<String, Integer> resetLobby = new HashMap<>();
    public final HashMap<String, Integer> lobbyAmount = new HashMap<>();
    public final HashMap<String, GameStates> lobbyStates = new HashMap<>();
    public final HashMap<String, Boolean> lobbyDeathmatch = new HashMap<>();
    public final HashMap<String, Boolean> lobbyMove = new HashMap<>();
    public final HashMap<String, Boolean> lobbyJoinable = new HashMap<>();
    public final HashMap<String, Boolean> lobbyForcearena = new HashMap<>();
    public final HashMap<String, Boolean> lobbyVoteallowed = new HashMap<>();
    public final HashMap<String, Boolean> lobbyTeamallowed = new HashMap<>();
    public final HashMap<String, Boolean> lobbyKitallowed = new HashMap<>();
    public final HashMap<String, Boolean> lobbyTickstarted = new HashMap<>();
    public final HashMap<String, Boolean> lobbyBuild = new HashMap<>();
    public final HashMap<String, Boolean> lobbyPause = new HashMap<>();
    public final HashMap<String, Boolean> lobbyActivateTeams = new HashMap<>();
    public final HashMap<String, Boolean> lobbyActivateKits = new HashMap<>();
    public final HashMap<String, Boolean> lobbyActivateShop = new HashMap<>();
    public final HashMap<String, Boolean> lobbyActivateAirdrops = new HashMap<>();
    public final HashMap<String, Boolean> lobbyCheckArenas = new HashMap<>();
    public final HashMap<String, Boolean> lobbySingleArena = new HashMap<>();
    public final HashMap<String, String> lobbyVote = new HashMap<>();
    public final HashMap<String, String> lobbyVotedarena = new HashMap<>();
    public final HashMap<Player, String> lobbyVoted = new HashMap<>();
    public final HashMap<Player, String> lobbyTeamed = new HashMap<>();
    public final HashMap<Player, String> lobbyKited = new HashMap<>();
    public final HashMap<String, HashMap<Integer, Integer>> lobbyteams = new HashMap<>();
    public final HashMap<Player, String> lobbyteamplayernamesdata = new HashMap<>();
    public final HashMap<String, HashMap<Player, String>> lobbyteamplayernames = new HashMap<>();
    public final HashMap<String, HashMap<String, Integer>> lobbyvotes = new HashMap<>();
    public final HashMap<Player, String> lobbyvoteplayernamesdata = new HashMap<>();
    public final HashMap<String, HashMap<Player, String>> lobbyvoteplayernames = new HashMap<>();
    public final HashMap<String, Integer> lobbyteamSize = new HashMap<>();
    public final HashMap<String, Integer> lobbymaxPlayers = new HashMap<>();
    public final HashMap<String, Integer> lobbyminPlayers = new HashMap<>();
    public final HashMap<String, Integer> lobbyteamAmount = new HashMap<>();
    public final HashMap<String, Integer> lobbyroundTime = new HashMap<>();
    public final HashMap<String, Integer> lobbyroundTimeSeconds = new HashMap<>();
    public final HashMap<String, HashMap<Location, Block>> lobbyLiquidPlaced = new HashMap<>();
    public final HashMap<String, HashMap<Location, Material>> lobbyPlacedBlocks = new HashMap<>();
    public final HashMap<String, HashMap<Location, Material>> lobbyBreakedBlocks = new HashMap<>();
    public final HashMap<String, HashMap<Location, BlockData>> lobbyWaterBlocks = new HashMap<>();
    public final HashMap<Location, Block> lobbyLiquidPlacedData = new HashMap<>();
    public final HashMap<Location, Material> lobbyPlacedBlocksData = new HashMap<>();
    public final HashMap<Location, Material> lobbyBreakedBlocksData = new HashMap<>();
    public final HashMap<Location, BlockData> lobbyWaterBlocksData = new HashMap<>();
    public final HashMap<Player, Scoreboard> info = new HashMap<>();
    public final File shopdatafile = new File(getDataFolder() + File.separator + "shopdata.yml");
    public final File kitdatafile = new File(getDataFolder() + File.separator + "kitdata.yml");
    public final File messagesfile = new File(getDataFolder() + File.separator + "messages.yml");
    public final File arenadatafile = new File(getDataFolder() + File.separator + "arenadata.yml");
    public final File chestdatafile = new File(getDataFolder() + File.separator + "chestdata.yml");
    private final ItemStack coin = new ItemStack(Material.GOLD_NUGGET);
    private final ItemStack bottle = new ItemStack(Material.GLASS_BOTTLE);
    public FileConfiguration messages;
    public FileConfiguration shopdata;
    public FileConfiguration arenadata;
    public FileConfiguration kitdata;
    public FileConfiguration chestdata;
    public String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_PURPLE + "Potion" + ChatColor.GOLD + "Games" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
    private int tick;
    private int countdown = 60;
    private int reset = 10;
    private int teamSize = 2;
    private int maxPlayers = 24;
    private int minPlayers = maxPlayers / 2;
    private int playerAmount = 0;
    private int teamAmount = maxPlayers / teamSize;
    private int roundTime = 30;
    private int roundTimeSeconds = roundTime * 60;
    private int activePotions = 19;
    private int activeKits = 6;
    private int winningReward = 100;
    private int killReward = 10;
    private String language = "en_US";
    private String vote;
    private String votedArena;
    private String host = "localhost";
    private String port = "3306";
    private String database = "potiongames";
    private String user = "root";
    private String password;
    private GameStates gamestate;
    private boolean deathmatch = false;
    private boolean joinable = true;
    private boolean pause = false;
    private boolean build = false;
    private boolean move = true;
    private boolean voteallowed = false;
    private boolean teamallowed = false;
    private boolean kitallowed = false;
    private boolean forcearena = false;
    private boolean startOnJoin = false;
    private boolean activateTeams = true;
    private boolean activateKits = true;
    private boolean activateShop = true;
    private boolean activateAirdrops = true;
    private boolean tickStarted = false;
    private boolean activateMysql = false;
    private boolean mysql = false;
    private boolean lobbySystem = false;
    private boolean gameServer = true;
    private boolean addlobby = false;
    private boolean addarena = false;
    private boolean dellobby = false;
    private boolean delarena = false;
    private boolean reload = false;
    private boolean compassOnSpawn = false;
    private boolean allowOutsideChat = false;
    private boolean changeGamerules = true;
    private boolean checkArenas = false;
    private boolean singleArena = false;
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
        messages = YamlConfiguration.loadConfiguration(messagesfile);
        shopdata = YamlConfiguration.loadConfiguration(shopdatafile);
        arenadata = YamlConfiguration.loadConfiguration(arenadatafile);
        kitdata = YamlConfiguration.loadConfiguration(kitdatafile);
        chestdata = YamlConfiguration.loadConfiguration(chestdatafile);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new Events(this), this);
        Objects.requireNonNull(this.getCommand("pg")).setExecutor(new Commands(this));
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
        chatmessages.add("Connection to database failed! Error");
        chatmessages.add("Connection to database closed!");
        chatmessages.add("Failed to close connection to database! Error");
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
        chatmessages.add("File loading / saving fail! Error");
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
        shop.add("JUMP");
        shoppotion.add(new PotionEffect(PotionEffectType.JUMP, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(Material.POTION));
        shopkit.add("Looter");
        shopcost.add(4);
        shopsale.add(2);
        shop.add("DAMAGE_RESISTANCE");
        shoppotion.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30 * 20, 1));
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
        shop.add("HEAL");
        shoppotion.add(new PotionEffect(PotionEffectType.HEAL, 30 * 20, 1));
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
        shop.add("INCREASE_DAMAGE");
        shoppotion.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 30 * 20, 1));
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
        shop.add("CONFUSION");
        shoppotion.add(new PotionEffect(PotionEffectType.CONFUSION, 30 * 20, 1));
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
        if (getConfig().get("pg.lobbySystem") == null) {
            getConfig().addDefault("pg.lobbySystem", lobbySystem);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            lobbySystem = getConfig().getBoolean("pg.lobbySystem");
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
        if (messages.get("pg.messages." + language + ".prefix") == null) {
            messages.addDefault("pg.messages." + language + ".prefix", prefix);
            messages.options().copyDefaults(true);
        } else {
            prefix = messages.getString("pg.messages." + language + ".prefix");
        }
        int message = 1;
        for (int i = 0; i < chatmessages.size(); i++) {
            if (messages.get("pg.messages." + language + "." + message) == null) {
                messages.addDefault("pg.messages." + language + "." + message, chatmessages.get(message - 1));
                messages.options().copyDefaults(true);
            } else {
                String text = messages.getString("pg.messages." + language + "." + message);
                chatmessages.set(message - 1, text);
            }
            message++;
        }
        try {
            messages.save(messagesfile);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(63) + ": " + e.getMessage());
        }
        int shopitem = 1;
        for (int i = 0; i < shop.size(); i++) {
            if (shopdata.get("pg.potions." + shopitem) == null) {
                shopdata.addDefault("pg.potions." + shopitem, shop.get(shopitem - 1));
                shopdata.addDefault("pg.potions." + shopitem + ".name", shop.get(shopitem - 1));
                shopdata.addDefault("pg.potions." + shopitem + "." + "shoppotion", shoppotion.get(shopitem - 1));
                shopdata.addDefault("pg.potions." + shopitem + "." + "shoppotiontype", shoppotiontype.get(shopitem - 1));
                shopdata.addDefault("pg.potions." + shopitem + ".kit", shopkit.get(shopitem - 1));
                shopdata.addDefault("pg.potions." + shopitem + ".cost", shopcost.get(shopitem - 1));
                shopdata.addDefault("pg.potions." + shopitem + ".sale", shopsale.get(shopitem - 1));
                shopdata.options().copyDefaults(true);
            } else {
                String name = shopdata.getString("pg.potions." + shopitem + ".name");
                shop.set(shopitem - 1, name);
                PotionEffect potion = (PotionEffect) shopdata.get("pg.potions." + shopitem + "." + "shoppotion");
                shoppotion.set(shopitem - 1, potion);
                ItemStack potiontype = (ItemStack) shopdata.get("pg.potions." + shopitem + "." + "shoppotiontype");
                shoppotiontype.set(shopitem - 1, potiontype);
                String kitname = shopdata.getString("pg.potions." + shopitem + ".kit");
                shopkit.set(shopitem - 1, kitname);
                Integer cost = (Integer) shopdata.get("pg.potions." + shopitem + ".cost");
                shopcost.set(shopitem - 1, cost);
                Integer sale = (Integer) shopdata.get("pg.potions." + shopitem + ".sale");
                shopsale.set(shopitem - 1, sale);
            }
            shopitem++;
        }
        try {
            shopdata.save(shopdatafile);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(63) + ": " + e.getMessage());
        }
        try {
            arenadata.save(arenadatafile);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(63) + ": " + e.getMessage());
        }
        if (!kitallowed) {
            kitallowed = true;
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
        }
        int kititem = 1;
        for (int i = 0; i < kits.size(); i++) {
            if (kitdata.get("pg.kits." + kititem) == null) {
                kitdata.addDefault("pg.kits." + kititem, kits.get(kititem - 1));
                kitdata.addDefault("pg.kits." + kititem + ".name", kits.get(kititem - 1));
                kitdata.options().copyDefaults(true);
            } else {
                String name = kitdata.getString("pg.kits." + kititem + ".name");
                kits.set(kititem - 1, name);
            }
            kititem++;
        }
        try {
            kitdata.save(kitdatafile);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(63) + ": " + e.getMessage());
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
        coinmeta.displayName(Component.text(ChatColor.DARK_AQUA + chatmessages.get(55)));
        coin.setItemMeta(coinmeta);
        if (gameServer) {
            if (!lobbySystem) {
                gamestate = GameStates.WAITING;
                tickStarted = true;
                tick();
            } else {
                for (int lobby = 1; lobby <= 27; lobby++) {
                    if (arenadata.contains("pg.lobbies." + lobby)) {
                        String s = Integer.toString(lobby);
                        lobbyCheckArenas.put(s, false);
                        lobbySingleArena.put(s, false);
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
                        if (arenadata.get("pg.lobbies." + s + ".activateTeams") == null) {
                            arenadata.addDefault("pg.lobbies." + s + ".activateTeams", activateTeams);
                            arenadata.options().copyDefaults(true);
                            try {
                                arenadata.save(arenadatafile);
                            } catch (IOException e) {
                                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(63) + ": " + e.getMessage());
                            }
                        } else {
                            lobbyActivateTeams.replace(s, arenadata.getBoolean("pg.lobbies." + s + ".activateTeams"));
                        }
                        if (arenadata.get("pg.lobbies." + s + ".activateKits") == null) {
                            arenadata.addDefault("pg.lobbies." + s + ".activateKits", activateKits);
                            arenadata.options().copyDefaults(true);
                            try {
                                arenadata.save(arenadatafile);
                            } catch (IOException e) {
                                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(63) + ": " + e.getMessage());
                            }
                        } else {
                            lobbyActivateKits.replace(s, arenadata.getBoolean("pg.lobbies." + s + ".activateKits"));
                        }
                        if (arenadata.get("pg.lobbies." + s + ".activateShop") == null) {
                            arenadata.addDefault("pg.lobbies." + s + ".activateShop", activateShop);
                            arenadata.options().copyDefaults(true);
                            try {
                                arenadata.save(arenadatafile);
                            } catch (IOException e) {
                                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(63) + ": " + e.getMessage());
                            }
                        } else {
                            lobbyActivateShop.replace(s, arenadata.getBoolean("pg.lobbies." + s + ".activateShop"));
                        }
                        if (arenadata.get("pg.lobbies." + s + ".activateAirdrops") == null) {
                            arenadata.addDefault("pg.lobbies." + s + ".activateAirdrops", activateAirdrops);
                            arenadata.options().copyDefaults(true);
                            try {
                                arenadata.save(arenadatafile);
                            } catch (IOException e) {
                                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(63) + ": " + e.getMessage());
                            }
                        } else {
                            lobbyActivateAirdrops.replace(s, arenadata.getBoolean("pg.lobbies." + s + ".activateAirdrops"));
                        }
                        if (arenadata.get("pg.lobbies." + s + ".teamSize") == null) {
                            arenadata.addDefault("pg.lobbies." + s + ".teamSize", teamSize);
                            arenadata.options().copyDefaults(true);
                            try {
                                arenadata.save(arenadatafile);
                            } catch (IOException e) {
                                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(63) + ": " + e.getMessage());
                            }
                        } else {
                            lobbyteamSize.replace(s, arenadata.getInt("pg.lobbies." + s + ".teamSize"));
                        }
                        if (arenadata.get("pg.lobbies." + s + ".maxPlayers") == null) {
                            arenadata.addDefault("pg.lobbies." + s + ".maxPlayers", maxPlayers);
                            arenadata.options().copyDefaults(true);
                            try {
                                arenadata.save(arenadatafile);
                            } catch (IOException e) {
                                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(63) + ": " + e.getMessage());
                            }
                        } else {
                            lobbymaxPlayers.replace(s, arenadata.getInt("pg.lobbies." + s + ".maxPlayers"));
                        }
                        if (arenadata.get("pg.lobbies." + s + ".minPlayers") == null) {
                            arenadata.addDefault("pg.lobbies." + s + ".minPlayers", minPlayers);
                            arenadata.options().copyDefaults(true);
                            try {
                                arenadata.save(arenadatafile);
                            } catch (IOException e) {
                                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(63) + ": " + e.getMessage());
                            }
                        } else {
                            lobbyminPlayers.replace(s, arenadata.getInt("pg.lobbies." + s + ".minPlayers"));
                        }
                        if (arenadata.get("pg.lobbies." + s + ".roundTime") == null) {
                            arenadata.addDefault("pg.lobbies." + s + ".roundTime", roundTime);
                            arenadata.options().copyDefaults(true);
                            try {
                                arenadata.save(arenadatafile);
                            } catch (IOException e) {
                                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(63) + ": " + e.getMessage());
                            }
                        } else {
                            lobbyroundTime.replace(s, arenadata.getInt("pg.lobbies." + s + ".roundTime"));
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
                                if (arenadata.contains("pg.lobbies." + s + "." + max + ".name")) {
                                    temp.put(arenadata.getString("pg.lobbies." + s + "." + max + ".name"), 0);
                                    lobbyvoteplayernamesdata.put(null, arenadata.getString("pg.lobbies." + s + "." + max + ".name"));
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
                        infoLobby.put(s, null + " , " + null + " , " + null);
                        lobbyStates.put(s, GameStates.WAITING);
                        tickLobby(s);
                    }
                }
            }
        } else {
            hubStats();
        }
        if (activateMysql && !mysql) {
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            getServer().getConsoleSender().sendMessage(prefix + ChatColor.GREEN + chatmessages.get(40));
        }
        new UpdateChecker(this, 87633).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.GRAY + chatmessages.get(76) + " " + this.getDescription().getVersion());
            } else {
                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.GRAY + chatmessages.get(77) + " " + this.getDescription().getVersion() + " -> " + version);
            }
        });
        if (enableRewards) {
            if (!setupEconomy()) {
                log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }
        if (getConfig().contains("pg.RankWall.headp1") && getConfig().contains("pg.RankWall.headp2") && getConfig().contains("pg.RankWall.headp3") && getConfig().contains("pg.RankWall.signp1") && getConfig().contains("pg.RankWall.signp2") && getConfig().contains("pg.RankWall.signp3")) {
            ResultSet rs = query("SELECT UUID FROM Stats ORDER BY WINS DESC LIMIT 3");
            int ii = 0;
            try {
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
                    OfflinePlayer name = Bukkit.getOfflinePlayer(UUID.fromString(rank.get(id)));
                    skull.setOwningPlayer(name);
                    skull.update();
                }
                for (int iii = 0; iii < rank.size(); iii++) {
                    int id = iii + 1;
                    BlockState b = ranksign.get(iii).getBlock().getState();
                    OfflinePlayer name = Bukkit.getOfflinePlayer(UUID.fromString(rank.get(id)));
                    Sign sign = (Sign) b;
                    sign.line(0, Component.text(chatmessages.get(33) + " #" + id));
                    sign.line(1, Component.text(Objects.requireNonNull(name.getName())));
                    sign.line(2, Component.text("Wins: " + getWins(rank.get(id))));
                    sign.line(3, Component.text("K/D: " + getKD(rank.get(id))));
                    sign.update();
                }
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(85));
            }
        }
        try {
            @SuppressWarnings("unused")
            Metrics metrics = new Metrics(this, 12027);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onReload() {
        Bukkit.getScheduler().cancelTasks(this);
        reloadConfig();
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
        if (getConfig().get("pg.lobbySystem") == null) {
            getConfig().addDefault("pg.lobbySystem", lobbySystem);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            lobbySystem = getConfig().getBoolean("pg.lobbySystem");
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
            getConfig().addDefault("pg.activeKits", activateScoreboard);
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
        if (messages.get("pg.messages." + language + ".prefix") == null) {
            messages.addDefault("pg.messages." + language + ".prefix", prefix);
            messages.options().copyDefaults(true);
        } else {
            prefix = messages.getString("pg.messages." + language + ".prefix");
        }
        int message = 1;
        for (int i = 0; i < chatmessages.size(); i++) {
            if (messages.get("pg.messages." + language + "." + message) == null) {
                messages.addDefault("pg.messages." + language + "." + message, chatmessages.get(message - 1));
                messages.options().copyDefaults(true);
            } else {
                String text = messages.getString("pg.messages." + language + "." + message);
                chatmessages.set(message - 1, text);
            }
            message++;
        }
        try {
            messages.save(messagesfile);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(63) + ": " + e.getMessage());
        }
        int shopitem = 1;
        for (int i = 0; i < shop.size(); i++) {
            if (shopdata.get("pg.potions." + shopitem) == null) {
                shopdata.addDefault("pg.potions." + shopitem, shop.get(shopitem - 1));
                shopdata.addDefault("pg.potions." + shopitem + ".name", shop.get(shopitem - 1));
                shopdata.addDefault("pg.potions." + shopitem + "." + "shoppotion", shoppotion.get(shopitem - 1));
                shopdata.addDefault("pg.potions." + shopitem + "." + "shoppotiontype", shoppotiontype.get(shopitem - 1));
                shopdata.addDefault("pg.potions." + shopitem + ".kit", shopkit.get(shopitem - 1));
                shopdata.addDefault("pg.potions." + shopitem + ".cost", shopcost.get(shopitem - 1));
                shopdata.addDefault("pg.potions." + shopitem + ".sale", shopsale.get(shopitem - 1));
                shopdata.options().copyDefaults(true);
            } else {
                String name = shopdata.getString("pg.potions." + shopitem + ".name");
                shop.set(shopitem - 1, name);
                PotionEffect potion = (PotionEffect) shopdata.get("pg.potions." + shopitem + "." + "shoppotion");
                shoppotion.set(shopitem - 1, potion);
                ItemStack potiontype = (ItemStack) shopdata.get("pg.potions." + shopitem + "." + "shoppotiontype");
                shoppotiontype.set(shopitem - 1, potiontype);
                String kit = shopdata.getString("pg.potions." + shopitem + ".kit");
                shopkit.set(shopitem - 1, kit);
                Integer cost = (Integer) shopdata.get("pg.potions." + shopitem + ".cost");
                shopcost.set(shopitem - 1, cost);
                Integer sale = (Integer) shopdata.get("pg.potions." + shopitem + ".sale");
                shopsale.set(shopitem - 1, sale);
            }
            shopitem++;
        }
        try {
            shopdata.save(shopdatafile);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(63) + ": " + e.getMessage());
        }
        try {
            arenadata.save(arenadatafile);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(63) + ": " + e.getMessage());
        }
        arenas.clear();
        voteallowed = false;
        checkArenas = false;
        singleArena = false;
        kits.clear();
        kitplayers.clear();
        kitplayernames.clear();
        kited.clear();
        if (!kitallowed) {
            kitallowed = true;
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
        }
        int kititem = 1;
        for (int i = 0; i < kits.size(); i++) {
            if (kitdata.get("pg.kits." + kititem) == null) {
                kitdata.addDefault("pg.kits." + kititem, kits.get(kititem - 1));
                kitdata.addDefault("pg.kits." + kititem + ".name", kits.get(kititem - 1));
                kitdata.options().copyDefaults(true);
            } else {
                String name = kitdata.getString("pg.kits." + kititem + ".name");
                kits.set(kititem - 1, name);
            }
            kititem++;
        }
        try {
            kitdata.save(kitdatafile);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(63) + ": " + e.getMessage());
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
        if (gameServer) {
            if (!lobbySystem) {
                gamestate = GameStates.WAITING;
                tickStarted = true;
                tick();
            } else {
                for (int lobby = 1; lobby <= 27; lobby++) {
                    if (arenadata.contains("pg.lobbies." + lobby)) {
                        String s = Integer.toString(lobby);
                        lobbyCheckArenas.put(s, false);
                        lobbySingleArena.put(s, false);
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
                        if (arenadata.get("pg.lobbies." + s + ".activateTeams") == null) {
                            arenadata.addDefault("pg.lobbies." + s + ".activateTeams", activateTeams);
                            arenadata.options().copyDefaults(true);
                            try {
                                arenadata.save(arenadatafile);
                            } catch (IOException e) {
                                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(63) + ": " + e.getMessage());
                            }
                        } else {
                            lobbyActivateTeams.replace(s, arenadata.getBoolean("pg.lobbies." + s + ".activateTeams"));
                        }
                        if (arenadata.get("pg.lobbies." + s + ".activateKits") == null) {
                            arenadata.addDefault("pg.lobbies." + s + ".activateKits", activateKits);
                            arenadata.options().copyDefaults(true);
                            try {
                                arenadata.save(arenadatafile);
                            } catch (IOException e) {
                                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(63) + ": " + e.getMessage());
                            }
                        } else {
                            lobbyActivateKits.replace(s, arenadata.getBoolean("pg.lobbies." + s + ".activateKits"));
                        }
                        if (arenadata.get("pg.lobbies." + s + ".activateShop") == null) {
                            arenadata.addDefault("pg.lobbies." + s + ".activateShop", activateShop);
                            arenadata.options().copyDefaults(true);
                            try {
                                arenadata.save(arenadatafile);
                            } catch (IOException e) {
                                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(63) + ": " + e.getMessage());
                            }
                        } else {
                            lobbyActivateShop.replace(s, arenadata.getBoolean("pg.lobbies." + s + ".activateShop"));
                        }
                        if (arenadata.get("pg.lobbies." + s + ".activateAirdrops") == null) {
                            arenadata.addDefault("pg.lobbies." + s + ".activateAirdrops", activateAirdrops);
                            arenadata.options().copyDefaults(true);
                            try {
                                arenadata.save(arenadatafile);
                            } catch (IOException e) {
                                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(63) + ": " + e.getMessage());
                            }
                        } else {
                            lobbyActivateAirdrops.replace(s, arenadata.getBoolean("pg.lobbies." + s + ".activateAirdrops"));
                        }
                        if (arenadata.get("pg.lobbies." + s + ".teamSize") == null) {
                            arenadata.addDefault("pg.lobbies." + s + ".teamSize", teamSize);
                            arenadata.options().copyDefaults(true);
                            try {
                                arenadata.save(arenadatafile);
                            } catch (IOException e) {
                                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(63) + ": " + e.getMessage());
                            }
                        } else {
                            lobbyteamSize.replace(s, arenadata.getInt("pg.lobbies." + s + ".teamSize"));
                        }
                        if (arenadata.get("pg.lobbies." + s + ".maxPlayers") == null) {
                            arenadata.addDefault("pg.lobbies." + s + ".maxPlayers", maxPlayers);
                            arenadata.options().copyDefaults(true);
                            try {
                                arenadata.save(arenadatafile);
                            } catch (IOException e) {
                                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(63) + ": " + e.getMessage());
                            }
                        } else {
                            lobbymaxPlayers.replace(s, arenadata.getInt("pg.lobbies." + s + ".maxPlayers"));
                        }
                        if (arenadata.get("pg.lobbies." + s + ".minPlayers") == null) {
                            arenadata.addDefault("pg.lobbies." + s + ".minPlayers", minPlayers);
                            arenadata.options().copyDefaults(true);
                            try {
                                arenadata.save(arenadatafile);
                            } catch (IOException e) {
                                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(63) + ": " + e.getMessage());
                            }
                        } else {
                            lobbyminPlayers.replace(s, arenadata.getInt("pg.lobbies." + s + ".minPlayers"));
                        }
                        if (arenadata.get("pg.lobbies." + s + ".roundTime") == null) {
                            arenadata.addDefault("pg.lobbies." + s + ".roundTime", roundTime);
                            arenadata.options().copyDefaults(true);
                            try {
                                arenadata.save(arenadatafile);
                            } catch (IOException e) {
                                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(63) + ": " + e.getMessage());
                            }
                        } else {
                            lobbyroundTime.replace(s, arenadata.getInt("pg.lobbies." + s + ".roundTime"));
                        }
                        roundTimeSecondsNumber = lobbyroundTime.get(s) * 60;
                        lobbyroundTimeSeconds.put(s, roundTimeSecondsNumber);
                        teamAmountNumber = lobbymaxPlayers.get(s) / lobbyteamSize.get(s);
                        lobbyteamAmount.put(s, teamAmountNumber);
                        if (!lobbyVoteallowed.get(s)) {
                            lobbyVoteallowed.replace(s, true);
                            HashMap<String, Integer> temp = new HashMap<>();
                            temp.put(chatmessages.get(42), 0);
                            for (int world = 1; world < 27; world++) {
                                if (arenadata.contains("pg.lobbies." + s + "." + world + ".name")) {
                                    temp.put(arenadata.getString("pg.lobbies." + s + "." + world + ".name"), 0);
                                    lobbyvoteplayernamesdata.put(null, arenadata.getString("pg.lobbies." + s + "." + world + ".name"));
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
                        infoLobby.put(s, null + " , " + null + " , " + null);
                        lobbyStates.put(s, GameStates.WAITING);
                        tickLobby(s);
                    }
                }
            }
        }
        if (getConfig().contains("pg.RankWall.headp1") && getConfig().contains("pg.RankWall.headp2") && getConfig().contains("pg.RankWall.headp3") && getConfig().contains("pg.RankWall.signp1") && getConfig().contains("pg.RankWall.signp2") && getConfig().contains("pg.RankWall.signp3")) {
            ResultSet rs = query("SELECT UUID FROM Stats ORDER BY WINS DESC LIMIT 3");
            int ii = 0;
            try {
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
                    OfflinePlayer name = Bukkit.getOfflinePlayer(UUID.fromString(rank.get(id)));
                    skull.setOwningPlayer(name);
                    skull.update();
                }
                for (int iii = 0; iii < rank.size(); iii++) {
                    int id = iii + 1;
                    BlockState b = ranksign.get(iii).getBlock().getState();
                    OfflinePlayer name = Bukkit.getOfflinePlayer(UUID.fromString(rank.get(id)));
                    Sign sign = (Sign) b;
                    sign.line(0, Component.text(chatmessages.get(33) + " #" + id));
                    sign.line(1, Component.text(Objects.requireNonNull(name.getName())));
                    sign.line(2, Component.text("Wins: " + getWins(rank.get(id))));
                    sign.line(3, Component.text("K/D: " + getKD(rank.get(id))));
                    sign.update();
                }
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(85));
            }
        }
    }

    @Override
    public void onDisable() {
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
        close();
        if (gameServer && startOnJoin && !lobbySystem) {
            for (Player all : Bukkit.getOnlinePlayers()) {
                all.kick(Component.text(prefix + ChatColor.RED + chatmessages.get(25)));
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
            String s;
            for (int i = 1; i <= 27; i++) {
                if (playerLobby.get(all) != null) {
                    if (playerLobby.get(all).contains(Integer.toString(i))) {
                        s = Integer.toString(i);
                        onLeaveLobby(all, s);
                    }
                }
                if (specLobby.get(all) != null) {
                    if (specLobby.get(all).contains(Integer.toString(i))) {
                        s = Integer.toString(i);
                        onLeaveLobby(all, s);
                    }
                }
            }
        }
        getServer().getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(41));
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

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

    public void leaveChannel(Player player, String channelName) {
        ArrayList<Player> players = channels.get(channelName);
        players.remove(player);
        channels.put(channelName, players);
        playerChannel.remove(player);
    }

    public void spawnFirework(Location loc, int amount) {
        Firework fw = (Firework) Objects.requireNonNull(loc.getWorld()).spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(1);
        fwm.addEffect(FireworkEffect.builder().flicker(true).with(Type.STAR).withColor(Color.GREEN, Color.AQUA, Color.RED, Color.PURPLE, Color.YELLOW).build());
        fw.setFireworkMeta(fwm);
        fw.detonate();
        for (int i = 0; i < amount; i++) {
            Firework fw2 = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            fw2.setFireworkMeta(fwm);
        }
    }

    public void hubStats() {
        tick = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            if (getConfig().contains("pg.RankWall.headp1") && getConfig().contains("pg.RankWall.headp2") && getConfig().contains("pg.RankWall.headp3") && getConfig().contains("pg.RankWall.signp1") && getConfig().contains("pg.RankWall.signp2") && getConfig().contains("pg.RankWall.signp3")) {
                ResultSet rs = query("SELECT UUID FROM Stats ORDER BY WINS DESC LIMIT 3");
                int ii = 0;
                try {
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
                        OfflinePlayer name = Bukkit.getOfflinePlayer(UUID.fromString(rank.get(id)));
                        skull.setOwningPlayer(name);
                        skull.update();
                    }
                    for (int iii = 0; iii < rank.size(); iii++) {
                        int id = iii + 1;
                        BlockState b = ranksign.get(iii).getBlock().getState();
                        OfflinePlayer name = Bukkit.getOfflinePlayer(UUID.fromString(rank.get(id)));
                        Sign sign = (Sign) b;
                        sign.line(0, Component.text(chatmessages.get(33) + " #" + id));
                        sign.line(1, Component.text(Objects.requireNonNull(name.getName())));
                        sign.line(2, Component.text("Wins: " + getWins(rank.get(id))));
                        sign.line(3, Component.text("K/D: " + getKD(rank.get(id))));
                        sign.update();
                    }
                } catch (Exception e) {
                    Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(85));
                }
            }
        }, 0, 1200);
    }

    public void chestData() {
        food1.add(new ItemStack(Material.CAKE, 1));
        food1.add(new ItemStack(Material.BREAD, 3));
        food1.add(new ItemStack(Material.PUMPKIN_PIE, 3));
        food1.add(new ItemStack(Material.COOKIE, 3));
        food1.add(new ItemStack(Material.BAKED_POTATO, 3));
        food2.add(new ItemStack(Material.RABBIT_STEW, 1));
        food2.add(new ItemStack(Material.MUSHROOM_STEW, 1));
        food2.add(new ItemStack(Material.BEETROOT_SOUP, 1));
        food2.add(new ItemStack(Material.GOLDEN_CARROT, 1));
        food2.add(new ItemStack(Material.MILK_BUCKET, 1));
        armour1.add(new ItemStack(Material.LEATHER_HELMET, 1));
        armour1.add(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
        armour1.add(new ItemStack(Material.LEATHER_LEGGINGS, 1));
        armour1.add(new ItemStack(Material.LEATHER_BOOTS, 1));
        armour2.add(new ItemStack(Material.CHAINMAIL_HELMET, 1));
        armour2.add(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1));
        armour2.add(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1));
        armour2.add(new ItemStack(Material.CHAINMAIL_BOOTS, 1));
        armour3.add(new ItemStack(Material.GOLDEN_HELMET, 1));
        armour3.add(new ItemStack(Material.GOLDEN_CHESTPLATE, 1));
        armour3.add(new ItemStack(Material.GOLDEN_LEGGINGS, 1));
        armour3.add(new ItemStack(Material.GOLDEN_BOOTS, 1));
        armour4.add(new ItemStack(Material.IRON_HELMET, 1));
        armour4.add(new ItemStack(Material.IRON_CHESTPLATE, 1));
        armour4.add(new ItemStack(Material.IRON_LEGGINGS, 1));
        armour4.add(new ItemStack(Material.IRON_BOOTS, 1));
        armour5.add(new ItemStack(Material.DIAMOND_HELMET, 1));
        armour5.add(new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
        armour5.add(new ItemStack(Material.DIAMOND_LEGGINGS, 1));
        armour5.add(new ItemStack(Material.DIAMOND_BOOTS, 1));
        weapons1.add(new ItemStack(Material.FISHING_ROD, 1));
        weapons1.add(new ItemStack(Material.BOW, 1));
        weapons1.add(new ItemStack(Material.ARROW, 5));
        weapons1.add(new ItemStack(Material.SPECTRAL_ARROW, 1));
        weapons1.add(new ItemStack(Material.SHIELD, 1));
        ItemStack itemStack = new ItemStack(Material.FLINT_AND_STEEL, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof Damageable) {
            ((Damageable) itemMeta).setDamage(60);
        }
        itemStack.setItemMeta(itemMeta);
        weapons1.add(itemStack);
        weapons1.add(new ItemStack(Material.COBWEB, 3));
        ItemStack itemStack2 = new ItemStack(Material.TNT, 1);
        ItemMeta itemMeta2 = itemStack2.getItemMeta();
        assert itemMeta2 != null;
        itemMeta2.displayName(Component.text(ChatColor.DARK_AQUA + chatmessages.get(79)));
        itemStack2.setItemMeta(itemMeta2);
        weapons1.add(itemStack2);
        weapons1.add(new ItemStack(Material.WOODEN_SWORD, 1));
        weapons1.add(new ItemStack(Material.STONE_SWORD, 1));
        weapons1.add(new ItemStack(Material.WOODEN_AXE, 1));
        weapons1.add(new ItemStack(Material.STONE_AXE, 1));
        weapons2.add(new ItemStack(Material.GOLDEN_SWORD, 1));
        weapons2.add(new ItemStack(Material.IRON_SWORD, 1));
        weapons2.add(new ItemStack(Material.GOLDEN_AXE, 1));
        weapons2.add(new ItemStack(Material.IRON_AXE, 1));
        weapons2.add(new ItemStack(Material.DIAMOND_SWORD, 1));
        weapons2.add(new ItemStack(Material.DIAMOND_AXE, 1));
        ItemStack itemStack3 = new ItemStack(Material.COMPASS, 1);
        ItemMeta itemMeta3 = itemStack3.getItemMeta();
        assert itemMeta3 != null;
        itemMeta3.displayName(Component.text(ChatColor.DARK_AQUA + chatmessages.get(2)));
        itemStack3.setItemMeta(itemMeta3);
        weapons2.add(itemStack3);
        weapons2.add(new ItemStack(Material.REDSTONE_TORCH, 1));
        potions.add(new PotionEffect(PotionEffectType.SPEED, 40 * 20, 2));
        potions.add(new PotionEffect(PotionEffectType.SLOW, 40 * 20, 0));
        potions.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40 * 20, 0));
        potions.add(new PotionEffect(PotionEffectType.HEAL, 1, 0));
        potions.add(new PotionEffect(PotionEffectType.HARM, 1, 0));
        potions.add(new PotionEffect(PotionEffectType.JUMP, 40 * 20, 3));
        potions.add(new PotionEffect(PotionEffectType.CONFUSION, 40 * 20, 0));
        potions.add(new PotionEffect(PotionEffectType.REGENERATION, 20 * 20, 1));
        potions.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40 * 20, 0));
        potions.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 60 * 20, 0));
        potions.add(new PotionEffect(PotionEffectType.INVISIBILITY, 40 * 20, 0));
        potions.add(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 20, 0));
        potions.add(new PotionEffect(PotionEffectType.HUNGER, 10 * 20, 1));
        potions.add(new PotionEffect(PotionEffectType.WEAKNESS, 60 * 20, 0));
        potions.add(new PotionEffect(PotionEffectType.POISON, 10 * 20, 1));
        potions.add(new PotionEffect(PotionEffectType.WITHER, 10 * 20, 1));
        potions.add(new PotionEffect(PotionEffectType.ABSORPTION, 60 * 20, 2));
        potions.add(new PotionEffect(PotionEffectType.GLOWING, 20 * 20, 0));
        potions.add(new PotionEffect(PotionEffectType.HEALTH_BOOST, 60 * 20, 2));
        potions.add(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 60 * 20, 2));
        potions.add(new PotionEffect(PotionEffectType.SATURATION, 40 * 20, 0));
        potions.add(new PotionEffect(PotionEffectType.NIGHT_VISION, 60 * 20, 0));
        potions.add(new PotionEffect(PotionEffectType.WATER_BREATHING, 60 * 20, 2));
        int chestitem = 1;
        if (chestdata.get("pg.food1." + chestitem) == null) {
            for (int i = 0; i < food1.size(); i++) {
                chestdata.addDefault("pg.food1." + chestitem, food1.get(chestitem - 1));
                chestdata.options().copyDefaults(true);
                chestitem++;
            }
        } else {
            while (chestdata.contains("pg.food1" + chestitem)) {
                ItemStack item = (ItemStack) chestdata.get("pg.food1." + chestitem);
                food1.set(chestitem - 1, item);
                chestitem++;
            }
        }
        chestitem = 1;
        if (chestdata.get("pg.food2." + chestitem) == null) {
            for (int i = 0; i < food2.size(); i++) {
                chestdata.addDefault("pg.food2." + chestitem, food2.get(chestitem - 1));
                chestdata.options().copyDefaults(true);
                chestitem++;
            }
        } else {
            while (chestdata.contains("pg.food2" + chestitem)) {
                ItemStack item = (ItemStack) chestdata.get("pg.food2." + chestitem);
                food2.set(chestitem - 1, item);
                chestitem++;
            }
        }
        chestitem = 1;
        if (chestdata.get("pg.armour1." + chestitem) == null) {
            for (int i = 0; i < armour1.size(); i++) {
                chestdata.addDefault("pg.armour1." + chestitem, armour1.get(chestitem - 1));
                chestdata.options().copyDefaults(true);
                chestitem++;
            }
        } else {
            while (chestdata.contains("pg.armour1" + chestitem)) {
                ItemStack item = (ItemStack) chestdata.get("pg.armour1." + chestitem);
                armour1.set(chestitem - 1, item);
                chestitem++;
            }
        }
        chestitem = 1;
        if (chestdata.get("pg.armour2." + chestitem) == null) {
            for (int i = 0; i < armour2.size(); i++) {
                chestdata.addDefault("pg.armour2." + chestitem, armour2.get(chestitem - 1));
                chestdata.options().copyDefaults(true);
                chestitem++;
            }
        } else {
            while (chestdata.contains("pg.armour2" + chestitem)) {
                ItemStack item = (ItemStack) chestdata.get("pg.armour2." + chestitem);
                armour2.set(chestitem - 1, item);
                chestitem++;
            }
        }
        chestitem = 1;
        if (chestdata.get("pg.armour3." + chestitem) == null) {
            for (int i = 0; i < armour3.size(); i++) {
                chestdata.addDefault("pg.armour3." + chestitem, armour3.get(chestitem - 1));
                chestdata.options().copyDefaults(true);
                chestitem++;
            }
        } else {
            while (chestdata.contains("pg.armour3" + chestitem)) {
                ItemStack item = (ItemStack) chestdata.get("pg.armour3." + chestitem);
                armour3.set(chestitem - 1, item);
                chestitem++;
            }
        }
        chestitem = 1;
        if (chestdata.get("pg.armour4." + chestitem) == null) {
            for (int i = 0; i < armour4.size(); i++) {
                chestdata.addDefault("pg.armour4." + chestitem, armour4.get(chestitem - 1));
                chestdata.options().copyDefaults(true);
                chestitem++;
            }
        } else {
            while (chestdata.contains("pg.armour4" + chestitem)) {
                ItemStack item = (ItemStack) chestdata.get("pg.armour4." + chestitem);
                armour4.set(chestitem - 1, item);
                chestitem++;
            }
        }
        chestitem = 1;
        if (chestdata.get("pg.armour5." + chestitem) == null) {
            for (int i = 0; i < armour5.size(); i++) {
                chestdata.addDefault("pg.armour5." + chestitem, armour5.get(chestitem - 1));
                chestdata.options().copyDefaults(true);
                chestitem++;
            }
        } else {
            while (chestdata.contains("pg.armour5" + chestitem)) {
                ItemStack item = (ItemStack) chestdata.get("pg.armour5." + chestitem);
                armour5.set(chestitem - 1, item);
                chestitem++;
            }
        }
        chestitem = 1;
        if (chestdata.get("pg.weapons1." + chestitem) == null) {
            for (int i = 0; i < weapons1.size(); i++) {
                chestdata.addDefault("pg.weapons1." + chestitem, weapons1.get(chestitem - 1));
                chestdata.options().copyDefaults(true);
                chestitem++;
            }
        } else {
            while (chestdata.contains("pg.weapons1" + chestitem)) {
                ItemStack item = (ItemStack) chestdata.get("pg.weapons1." + chestitem);
                weapons1.set(chestitem - 1, item);
                chestitem++;
            }
        }
        chestitem = 1;
        if (chestdata.get("pg.weapons2." + chestitem) == null) {
            for (int i = 0; i < weapons2.size(); i++) {
                chestdata.addDefault("pg.weapons2." + chestitem, weapons2.get(chestitem - 1));
                chestdata.options().copyDefaults(true);
                chestitem++;
            }
        } else {
            while (chestdata.contains("pg.weapons2" + chestitem)) {
                ItemStack item = (ItemStack) chestdata.get("pg.weapons2." + chestitem);
                weapons2.set(chestitem - 1, item);
                chestitem++;
            }
        }
        chestitem = 1;
        if (chestdata.get("pg.potions." + chestitem) == null) {
            for (int i = 0; i < potions.size(); i++) {
                chestdata.addDefault("pg.potions." + chestitem, potions.get(chestitem - 1));
                chestdata.options().copyDefaults(true);
                chestitem++;
            }
        } else {
            while (chestdata.contains("pg.potions" + chestitem)) {
                PotionEffect effect = (PotionEffect) chestdata.get("pg.potions." + chestitem);
                potions.set(chestitem - 1, effect);
                chestitem++;
            }
        }
        ItemMeta examplemeta = itemStack.getItemMeta();
        assert examplemeta != null;
        examplemeta.displayName(Component.text(ChatColor.RED + "EXAMPLE"));
        if (examplemeta instanceof Damageable) {
            ((Damageable) examplemeta).setDamage(60);
        }
        ItemStack example = new ItemStack(Material.BOW, 3);
        example.setItemMeta(examplemeta);
        example.addEnchantment(Enchantment.ARROW_FIRE, 1);
        if (chestdata.get("pg.customchests." + 1) == null) {
            chestdata.addDefault("pg.customchests." + 1 + ".activate", false);
            chestdata.addDefault("pg.customchests." + 1 + ".chesttype", Material.COMMAND_BLOCK.toString());
            chestdata.addDefault("pg.customchests." + 1 + ".chestsize", 9);
            chestdata.addDefault("pg.customchests." + 1 + "." + 1 + ".slot", 1);
            chestdata.addDefault("pg.customchests." + 1 + "." + 1 + ".item", example);
            chestdata.options().copyDefaults(true);
        }
        ItemStack arrow = new ItemStack(Material.ARROW);
        ItemStack firebow = new ItemStack(Material.BOW);
        ItemMeta firebowmeta = firebow.getItemMeta();
        assert firebowmeta != null;
        firebowmeta.displayName(Component.text(ChatColor.DARK_AQUA + chatmessages.get(11)));
        firebow.setItemMeta(firebowmeta);
        firebow.addEnchantment(Enchantment.ARROW_FIRE, 1);
        if (chestdata.get("pg.customchests." + 2) == null) {
            chestdata.addDefault("pg.customchests." + 2 + ".activate", true);
            chestdata.addDefault("pg.customchests." + 2 + ".chesttype", Material.TARGET.toString());
            chestdata.addDefault("pg.customchests." + 2 + ".chestsize", 9);
            chestdata.addDefault("pg.customchests." + 2 + "." + 1 + ".slot", 2);
            chestdata.addDefault("pg.customchests." + 2 + "." + 1 + ".item", arrow);
            chestdata.addDefault("pg.customchests." + 2 + "." + 2 + ".slot", 3);
            chestdata.addDefault("pg.customchests." + 2 + "." + 2 + ".item", arrow);
            chestdata.addDefault("pg.customchests." + 2 + "." + 3 + ".slot", 7);
            chestdata.addDefault("pg.customchests." + 2 + "." + 3 + ".item", arrow);
            chestdata.addDefault("pg.customchests." + 2 + "." + 4 + ".slot", 8);
            chestdata.addDefault("pg.customchests." + 2 + "." + 4 + ".item", arrow);
            chestdata.addDefault("pg.customchests." + 2 + "." + 5 + ".slot", 5);
            chestdata.addDefault("pg.customchests." + 2 + "." + 5 + ".item", firebow);
            chestdata.options().copyDefaults(true);
        }
        ItemStack ingot = new ItemStack(Material.NETHERITE_INGOT);
        if (chestdata.get("pg.customchests." + 3) == null) {
            chestdata.addDefault("pg.customchests." + 3 + ".activate", true);
            chestdata.addDefault("pg.customchests." + 3 + ".chesttype", Material.NETHERITE_BLOCK.toString());
            chestdata.addDefault("pg.customchests." + 3 + ".chestsize", 9);
            chestdata.addDefault("pg.customchests." + 3 + "." + 1 + ".slot", 3);
            chestdata.addDefault("pg.customchests." + 3 + "." + 1 + ".item", ingot);
            chestdata.addDefault("pg.customchests." + 3 + "." + 2 + ".slot", 7);
            chestdata.addDefault("pg.customchests." + 3 + "." + 2 + ".item", ingot);
            chestdata.options().copyDefaults(true);
        }
        if (chestdata.get("pg.customchests." + 4) == null) {
            chestdata.addDefault("pg.customchests." + 4 + ".activate", true);
            chestdata.addDefault("pg.customchests." + 4 + ".chesttype", Material.DRIED_KELP_BLOCK.toString());
            chestdata.addDefault("pg.customchests." + 4 + ".chestsize", 9);
            chestdata.addDefault("pg.customchests." + 4 + "." + 1 + ".slot", 2);
            chestdata.addDefault("pg.customchests." + 4 + "." + 1 + ".item", new ItemStack(Material.SHIELD));
            chestdata.addDefault("pg.customchests." + 4 + "." + 2 + ".slot", 3);
            chestdata.addDefault("pg.customchests." + 4 + "." + 2 + ".item", new ItemStack(Material.DIAMOND_SWORD));
            chestdata.addDefault("pg.customchests." + 4 + "." + 3 + ".slot", 5);
            chestdata.addDefault("pg.customchests." + 4 + "." + 3 + ".item", new ItemStack(Material.DIAMOND_HELMET));
            chestdata.addDefault("pg.customchests." + 4 + "." + 4 + ".slot", 6);
            chestdata.addDefault("pg.customchests." + 4 + "." + 4 + ".item", new ItemStack(Material.DIAMOND_CHESTPLATE));
            chestdata.addDefault("pg.customchests." + 4 + "." + 5 + ".slot", 7);
            chestdata.addDefault("pg.customchests." + 4 + "." + 5 + ".item", new ItemStack(Material.DIAMOND_LEGGINGS));
            chestdata.addDefault("pg.customchests." + 4 + "." + 6 + ".slot", 8);
            chestdata.addDefault("pg.customchests." + 4 + "." + 6 + ".item", new ItemStack(Material.DIAMOND_BOOTS));
            chestdata.options().copyDefaults(true);
        }
        if (chestdata.get("pg.chestblocks.normal") == null) {
            chestdata.addDefault("pg.chestblocks.normal", Material.END_PORTAL_FRAME.toString());
            chestdata.options().copyDefaults(true);
        }
        if (chestdata.get("pg.chestblocks.shop") == null) {
            chestdata.addDefault("pg.chestblocks.shop", Material.COMPOSTER.toString());
            chestdata.options().copyDefaults(true);
        }
        try {
            chestdata.save(chestdatafile);
        } catch (IOException ex) {
            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(63) + ": " + ex.getMessage());
        }
    }

    public void setup(Player p) {
        ItemStack addlobby = new ItemStack(Material.STICK);
        ItemMeta addlobbymeta = addlobby.getItemMeta();
        assert addlobbymeta != null;
        addlobbymeta.displayName(Component.text(ChatColor.DARK_AQUA + "Add(Left)/Del(Right) Lobby"));
        addlobby.setItemMeta(addlobbymeta);
        p.getInventory().setItem(1, addlobby);
        if (lobbySystem) {
            ItemStack chooselobby = new ItemStack(Material.CLOCK);
            ItemMeta chooselobbymeta = chooselobby.getItemMeta();
            assert chooselobbymeta != null;
            chooselobbymeta.displayName(Component.text(ChatColor.DARK_AQUA + "Choose Lobby"));
            chooselobby.setItemMeta(chooselobbymeta);
            p.getInventory().setItem(2, chooselobby);
        }
        ItemStack addarena = new ItemStack(Material.STICK);
        ItemMeta addarenameta = addarena.getItemMeta();
        assert addarenameta != null;
        addarenameta.displayName(Component.text(ChatColor.DARK_AQUA + "Add(Left)/Del(Right) Arena"));
        addarena.setItemMeta(addarenameta);
        p.getInventory().setItem(3, addarena);
        ItemStack choosearena = new ItemStack(Material.CLOCK);
        ItemMeta choosearenameta = choosearena.getItemMeta();
        assert choosearenameta != null;
        choosearenameta.displayName(Component.text(ChatColor.DARK_AQUA + "Choose Arena"));
        choosearena.setItemMeta(choosearenameta);
        p.getInventory().setItem(4, choosearena);
        ItemStack addspawn = new ItemStack(Material.STICK);
        ItemMeta addspawnmeta = addspawn.getItemMeta();
        assert addspawnmeta != null;
        addspawnmeta.displayName(Component.text(ChatColor.DARK_AQUA + "Add(Left)/Del(Right) Spawn"));
        addspawn.setItemMeta(addspawnmeta);
        p.getInventory().setItem(5, addspawn);
        ItemStack signsetup = new ItemStack(Material.OAK_SIGN);
        ItemMeta signsetupmeta = signsetup.getItemMeta();
        assert signsetupmeta != null;
        signsetupmeta.displayName(Component.text(ChatColor.DARK_AQUA + "Set Join-Sign"));
        signsetup.setItemMeta(signsetupmeta);
        p.getInventory().setItem(6, signsetup);
        ItemStack leavesetup = new ItemStack(Material.BARRIER);
        ItemMeta leavesetupmeta = leavesetup.getItemMeta();
        assert leavesetupmeta != null;
        leavesetupmeta.displayName(Component.text(ChatColor.DARK_AQUA + "Leave Setup-Mode"));
        leavesetup.setItemMeta(leavesetupmeta);
        p.getInventory().setItem(7, leavesetup);
    }

    public void clearEffects(Player all) {
        int chestitem = 1;
        while (chestdata.contains("pg.potions" + chestitem)) {
            PotionEffect effect = (PotionEffect) chestdata.get("pg.potions." + chestitem);
            assert effect != null;
            all.removePotionEffect(effect.getType());
            chestitem++;
        }
        if (all.hasPotionEffect(PotionEffectType.SPEED))
            all.removePotionEffect(PotionEffectType.SPEED);
        if (all.hasPotionEffect(PotionEffectType.SLOW))
            all.removePotionEffect(PotionEffectType.SLOW);
        if (all.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
            all.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
        if (all.hasPotionEffect(PotionEffectType.HEAL))
            all.removePotionEffect(PotionEffectType.HEAL);
        if (all.hasPotionEffect(PotionEffectType.HARM))
            all.removePotionEffect(PotionEffectType.HARM);
        if (all.hasPotionEffect(PotionEffectType.JUMP))
            all.removePotionEffect(PotionEffectType.JUMP);
        if (all.hasPotionEffect(PotionEffectType.CONFUSION))
            all.removePotionEffect(PotionEffectType.CONFUSION);
        if (all.hasPotionEffect(PotionEffectType.REGENERATION))
            all.removePotionEffect(PotionEffectType.REGENERATION);
        if (all.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE))
            all.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        if (all.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE))
            all.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
        if (all.hasPotionEffect(PotionEffectType.INVISIBILITY))
            all.removePotionEffect(PotionEffectType.INVISIBILITY);
        if (all.hasPotionEffect(PotionEffectType.BLINDNESS))
            all.removePotionEffect(PotionEffectType.BLINDNESS);
        if (all.hasPotionEffect(PotionEffectType.HUNGER))
            all.removePotionEffect(PotionEffectType.HUNGER);
        if (all.hasPotionEffect(PotionEffectType.WEAKNESS))
            all.removePotionEffect(PotionEffectType.WEAKNESS);
        if (all.hasPotionEffect(PotionEffectType.POISON))
            all.removePotionEffect(PotionEffectType.POISON);
        if (all.hasPotionEffect(PotionEffectType.WITHER))
            all.removePotionEffect(PotionEffectType.WITHER);
        if (all.hasPotionEffect(PotionEffectType.ABSORPTION))
            all.removePotionEffect(PotionEffectType.ABSORPTION);
        if (all.hasPotionEffect(PotionEffectType.GLOWING))
            all.removePotionEffect(PotionEffectType.GLOWING);
        if (all.hasPotionEffect(PotionEffectType.HEALTH_BOOST))
            all.removePotionEffect(PotionEffectType.HEALTH_BOOST);
        if (all.hasPotionEffect(PotionEffectType.DOLPHINS_GRACE))
            all.removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
        if (all.hasPotionEffect(PotionEffectType.SATURATION))
            all.removePotionEffect(PotionEffectType.SATURATION);
        if (all.hasPotionEffect(PotionEffectType.NIGHT_VISION))
            all.removePotionEffect(PotionEffectType.NIGHT_VISION);
        if (all.hasPotionEffect(PotionEffectType.WATER_BREATHING))
            all.removePotionEffect(PotionEffectType.WATER_BREATHING);
    }

    public void setGameRules(String name) {
        Objects.requireNonNull(Bukkit.getWorld(name)).setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        Objects.requireNonNull(Bukkit.getWorld(name)).setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        Objects.requireNonNull(Bukkit.getWorld(name)).setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        Objects.requireNonNull(Bukkit.getWorld(name)).setGameRule(GameRule.DO_FIRE_TICK, false);
        Objects.requireNonNull(Bukkit.getWorld(name)).setGameRule(GameRule.DO_MOB_SPAWNING, false);
        Objects.requireNonNull(Bukkit.getWorld(name)).setGameRule(GameRule.MOB_GRIEFING, false);
        Objects.requireNonNull(Bukkit.getWorld(name)).setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        Objects.requireNonNull(Bukkit.getWorld(name)).setTime(0);
    }

    public void tick() {
        if (!lobbySystem) {
            setCountdown(getConfig().getInt("pg.countdown"));
            roundTimeSeconds = roundTime * 60;
            tick = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                if (!pause) {
                    switch (gamestate) {
                        case WAITING -> {
                            if (activateScoreboard) {
                                for (Player all : pgPlayers) {
                                    Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("").setScore(11);
                                    Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("Map").setScore(10);
                                    Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore(" ").setScore(9);
                                    Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("  ").setScore(8);
                                    Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("Team").setScore(7);
                                    Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("   ").setScore(6);
                                    Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("    ").setScore(5);
                                    Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("Kit").setScore(4);
                                    Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("     ").setScore(3);
                                    Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("      ").setScore(2);
                                    Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("Players").setScore(1);
                                    Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("       ").setScore(0);
                                }
                            }
                            if (getConfig().contains("pg.Lobby") && getConfig().getLocation("pg.Lobby.sign") != null) {
                                Location loc = getConfig().getLocation("pg.Lobby.sign");
                                assert loc != null;
                                BlockState b = loc.getBlock().getState();
                                Sign sign = (Sign) b;
                                sign.line(0, Component.text("PotionGames"));
                                if (gamestate == GameStates.WAITING || gamestate == GameStates.PREPARING) {
                                    sign.line(1, Component.text(ChatColor.GREEN + gamestate.toString()));
                                } else {
                                    sign.line(1, Component.text(ChatColor.RED + gamestate.toString()));
                                }
                                if (getVote() != null) {
                                    sign.line(2, Component.text(ChatColor.GOLD + Objects.requireNonNull(arenadata.get("pg.arenas." + getVote() + ".name")).toString()));
                                    if (activateScoreboard) {
                                        for (Player all : pgPlayers) {
                                            Objects.requireNonNull(info.get(all).getTeam("map")).prefix(Component.text(ChatColor.DARK_AQUA + Objects.requireNonNull(arenadata.get("pg.arenas." + getVote() + ".name")).toString()));
                                        }
                                    }
                                } else {
                                    sign.line(2, Component.text(ChatColor.AQUA + "Voting"));
                                    if (activateScoreboard) {
                                        for (Player all : pgPlayers) {
                                            Objects.requireNonNull(info.get(all).getTeam("map")).prefix(Component.text(ChatColor.DARK_AQUA + "Voting"));
                                        }
                                    }
                                }
                                sign.line(3, Component.text(ChatColor.GRAY + "[" + getPlayerAmount() + "/" + maxPlayers + "]"));
                                if (activateScoreboard) {
                                    for (Player all : pgPlayers) {
                                        Objects.requireNonNull(info.get(all).getTeam("players")).prefix(Component.text("" + ChatColor.DARK_AQUA + getPlayerAmount() + "/" + maxPlayers));
                                    }
                                }
                                sign.update();
                            }
                            specPlayers.clear();
                            move = true;
                            joinable = true;
                            if (getConfig().contains("pg.Lobby.world")) {
                                worlds.add(getConfig().getString("pg.Lobby.world"));
                                Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("pg.Lobby.world")))).setPVP(false);
                                if (changeGamerules) {
                                    setGameRules(getConfig().getString("pg.Lobby.world"));
                                    Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("pg.Lobby.world")))).setDifficulty(Difficulty.PEACEFUL);
                                    Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("pg.Lobby.world")))).setGameRule(GameRule.FALL_DAMAGE, false);
                                }
                            }
                            if (!voteallowed) {
                                voteallowed = true;
                                for (int arena = 1; arena < 27; arena++) {
                                    if (arenadata.contains("pg.arenas." + arena)) {
                                        String name = arenadata.getString("pg.arenas." + arena + ".name");
                                        arenas.add(name);
                                    }
                                }
                                votes.put(chatmessages.get(42), 0);
                                for (String all : arenas) {
                                    votes.put(all, 0);
                                }
                            }
                            if (!checkArenas) {
                                checkArenas = true;
                                if (arenas.size() == 1) {
                                    String arena = arenas.get(0);
                                    String arenaNumber = null;
                                    setForcearena(true);
                                    int i = 1;
                                    boolean votetedarena = false;
                                    while (!votetedarena) {
                                        if (arena.matches(Objects.requireNonNull(arenadata.getString("pg.arenas." + i + ".name")))) {
                                            arenaNumber = Integer.toString(i);
                                            setVotedArena(arena);
                                            votetedarena = true;
                                        } else {
                                            i++;
                                        }
                                    }
                                    setVote(arenaNumber);
                                    singleArena = true;
                                }
                            }
                            if (activateTeams) {
                                if (!teamallowed) {
                                    teamallowed = true;
                                    int team = 1;
                                    while (team <= teamAmount) {
                                        String name = Integer.toString(team);
                                        teams.add(name);
                                        team++;
                                    }
                                    teamplayers.put(chatmessages.get(42), 0);
                                    for (String all : teams) {
                                        teamplayers.put(all, 0);
                                    }
                                }
                            }
                            int kit = 1;
                            for (int i = 0; i < activeKits; i++) {
                                String name = kitdata.getString("pg.kits." + kit + ".name");
                                kits.add(name);
                                kit++;
                            }
                            joinable = true;
                            if (getPlayerAmount() < minPlayers) {
                                for (Player all : pgPlayers) {
                                    all.setLevel(0);
                                    all.sendActionBar(Component.text(prefix + chatmessages.get(0) + " " + "[" + ChatColor.AQUA + getPlayerAmount() + ChatColor.GRAY + "/" + ChatColor.AQUA + minPlayers + ChatColor.GRAY + "]"));
                                }
                                setCountdown(getConfig().getInt("pg.countdown"));
                            } else {
                                gamestate = GameStates.PREPARING;
                                for (Player all : pgPlayers) {
                                    all.playSound(all.getLocation(), Sound.ENTITY_ARROW_HIT, 1, 1);
                                }
                                for (Player all : Bukkit.getOnlinePlayers()) {
                                    if (playerChannel.get(all) == null) {
                                        joinChannel(all.getPlayer(), "Global");
                                    }
                                    if (broadcastStarting && playerChannel.get(all).contains("Global")) {
                                        all.sendMessage(String.format(prefix + chatmessages.get(95), "", ""));
                                    }
                                }
                            }
                        }
                        case PREPARING -> {
                            if (getConfig().contains("pg.Lobby") && getConfig().getLocation("pg.Lobby.sign") != null) {
                                Location loc = getConfig().getLocation("pg.Lobby.sign");
                                assert loc != null;
                                BlockState b = loc.getBlock().getState();
                                Sign sign = (Sign) b;
                                sign.line(0, Component.text("PotionGames"));
                                if (gamestate == GameStates.WAITING || gamestate == GameStates.PREPARING) {
                                    sign.line(1, Component.text(ChatColor.GREEN + gamestate.toString()));
                                } else {
                                    sign.line(0, Component.text(ChatColor.RED + gamestate.toString()));
                                }
                                if (getVote() != null) {
                                    sign.line(2, Component.text(ChatColor.GOLD + Objects.requireNonNull(arenadata.get("pg.arenas." + getVote() + ".name")).toString()));
                                    if (activateScoreboard) {
                                        for (Player all : pgPlayers) {
                                            Objects.requireNonNull(info.get(all).getTeam("map")).prefix(Component.text(ChatColor.DARK_AQUA + Objects.requireNonNull(arenadata.get("pg.arenas." + getVote() + ".name")).toString()));
                                        }
                                    }
                                } else {
                                    sign.line(0, Component.text(ChatColor.AQUA + "Voting"));
                                    if (activateScoreboard) {
                                        for (Player all : pgPlayers) {
                                            Objects.requireNonNull(info.get(all).getTeam("map")).prefix(Component.text(ChatColor.DARK_AQUA + "Voting"));
                                        }
                                    }
                                }
                                sign.line(3, Component.text(ChatColor.GRAY + "[" + getPlayerAmount() + "/" + maxPlayers + "]"));
                                if (activateScoreboard) {
                                    for (Player all : pgPlayers) {
                                        Objects.requireNonNull(info.get(all).getTeam("players")).prefix(Component.text("" + ChatColor.DARK_AQUA + getPlayerAmount() + "/" + maxPlayers));
                                    }
                                }
                                sign.update();
                            }
                            if (getPlayerAmount() >= minPlayers) {
                                for (Player all : pgPlayers) {
                                    all.setLevel(countdown);
                                }
                                if (countdown == 10) {
                                    for (Player all : pgPlayers) {
                                        all.setLevel(countdown);
                                        all.playSound(all.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
                                    }
                                    voteResults();
                                }
                                if (countdown < 10) {
                                    for (Player all : pgPlayers) {
                                        all.setLevel(countdown);
                                    }
                                }
                                if (countdown == 0) {
                                    for (Player all : pgPlayers) {
                                        all.setLevel(countdown);
                                    }
                                    teleportAndStart();
                                    setCountdown(11);
                                    gamestate = GameStates.INGAME;
                                    move = false;
                                }
                                countdown--;
                            } else {
                                gamestate = GameStates.WAITING;
                            }
                        }
                        case INGAME -> {
                            if (getConfig().contains("pg.Lobby") && getConfig().getLocation("pg.Lobby.sign") != null) {
                                Location loc = getConfig().getLocation("pg.Lobby.sign");
                                assert loc != null;
                                BlockState b = loc.getBlock().getState();
                                Sign sign = (Sign) b;
                                sign.line(0, Component.text("PotionGames"));
                                if (gamestate == GameStates.WAITING || gamestate == GameStates.PREPARING) {
                                    sign.line(1, Component.text(ChatColor.GREEN + gamestate.toString()));
                                } else {
                                    sign.line(1, Component.text(ChatColor.RED + gamestate.toString()));
                                }
                                if (getVote() != null) {
                                    sign.line(2, Component.text(ChatColor.GOLD + Objects.requireNonNull(arenadata.get("pg.arenas." + getVote() + ".name")).toString()));
                                } else {
                                    sign.line(2, Component.text(ChatColor.AQUA + "Voting"));
                                }
                                sign.line(3, Component.text(ChatColor.GRAY + "[" + getPlayerAmount() + "/" + maxPlayers + "]"));
                                if (activateScoreboard) {
                                    for (Player all : pgPlayers) {
                                        Objects.requireNonNull(info.get(all).getTeam("players")).prefix(Component.text("" + ChatColor.DARK_AQUA + getPlayerAmount() + "/" + maxPlayers));
                                    }
                                }
                                sign.update();
                            }
                            if (countdown != 0 && countdown != -1 && countdown != -4) {
                                for (int world = 1; world < 27; world++) {
                                    if (arenadata.contains("pg.arenas." + world)) {
                                        String name = arenadata.getString("pg.arenas." + world + ".world");
                                        assert name != null;
                                        worlds.add(name);
                                        Objects.requireNonNull(getServer().getWorld(name)).setPVP(false);
                                        if (changeGamerules) {
                                            setGameRules(name);
                                            Objects.requireNonNull(getServer().getWorld(name)).setDifficulty(Difficulty.PEACEFUL);
                                            Objects.requireNonNull(getServer().getWorld(name)).setGameRule(GameRule.FALL_DAMAGE, false);
                                        }
                                    }
                                }
                            }
                            joinable = false;
                            if (getPlayerAmount() != 0) {
                                if (countdown == 10) {
                                    for (Player all : pgPlayers) {
                                        all.getInventory().clear();
                                        all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(1) + " " + ChatColor.AQUA + countdown);
                                        ItemStack playercompass = new ItemStack(Material.COMPASS);
                                        ItemMeta playercompassmeta = playercompass.getItemMeta();
                                        assert playercompassmeta != null;
                                        playercompassmeta.displayName(Component.text(ChatColor.DARK_AQUA + chatmessages.get(2)));
                                        playercompass.setItemMeta(playercompassmeta);
                                        if (compassOnSpawn) {
                                            all.getInventory().setItem(8, playercompass);
                                        }
                                        all.playSound(all.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
                                    }
                                    for (Player rich : richkidPlayers) {
                                        for (int i = 0; i < 5; i++) {
                                            rich.getInventory().addItem(coin);
                                        }
                                    }
                                    countdown--;
                                } else if (countdown <= 9 && countdown > 5) {
                                    countdown--;
                                } else if (countdown <= 5 && countdown > 0) {
                                    for (Player all : pgPlayers) {
                                        all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(1) + " " + ChatColor.AQUA + countdown);
                                        all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                                    }
                                    countdown--;
                                } else if (countdown == 0) {
                                    for (int world = 1; world < 27; world++) {
                                        if (arenadata.contains("pg.arenas." + world)) {
                                            String name = arenadata.getString("pg.arenas." + world + ".world");
                                            assert name != null;
                                            worlds.add(name);
                                            Objects.requireNonNull(getServer().getWorld(name)).setPVP(true);
                                            if (changeGamerules) {
                                                setGameRules(name);
                                                Objects.requireNonNull(getServer().getWorld(name)).setDifficulty(Difficulty.EASY);
                                                Objects.requireNonNull(getServer().getWorld(name)).setGameRule(GameRule.FALL_DAMAGE, true);
                                            }
                                        }
                                    }
                                    move = true;
                                    for (Player all : pgPlayers) {
                                        all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(3));
                                        all.playSound(all.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 1);
                                        all.setGameMode(GameMode.SURVIVAL);
                                        setCountdown(-1);
                                    }
                                } else if (countdown == -1) {
                                    if (activateScoreboard) {
                                        for (Player all : pgPlayers) {
                                            info.get(all).resetScores("");
                                            info.get(all).resetScores("Map");
                                            info.get(all).resetScores(" ");
                                            info.get(all).resetScores("  ");
                                            info.get(all).resetScores("Team");
                                            info.get(all).resetScores("   ");
                                            info.get(all).resetScores("    ");
                                            info.get(all).resetScores("Kit");
                                            info.get(all).resetScores("     ");
                                            Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("           ").setScore(8);
                                            Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("Time").setScore(7);
                                            Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("          ").setScore(6);
                                            Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("         ").setScore(5);
                                            Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("Kills").setScore(4);
                                            Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("        ").setScore(3);
                                            Objects.requireNonNull(info.get(all).getTeam("timeScoreboard")).prefix(Component.text("" + ChatColor.DARK_AQUA + roundTimeSeconds / 60 + " min"));
                                            Objects.requireNonNull(info.get(all).getTeam("kills")).prefix(Component.text(ChatColor.DARK_AQUA + "0"));
                                            String tempString = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(info.get(all).getTeam("kills")).prefix());
                                            tempString = ChatColor.stripColor(tempString);
                                            int tempInt = Integer.parseInt(tempString);
                                            Objects.requireNonNull(info.get(all).getTeam("kills")).prefix(Component.text("" + ChatColor.DARK_AQUA + tempInt));
                                        }
                                    }
                                    if (activateTeams) {
                                        if (pgPlayers.size() == 1 || teams.size() == 1) {
                                            setCountdown(-2);
                                        } else if (pgPlayers.size() == 2 && teams.size() == 2 && activateDeathmatch) {
                                            setCountdown(-4);
                                        }
                                    } else {
                                        if (pgPlayers.size() == 1) {
                                            setCountdown(-2);
                                        } else if (pgPlayers.size() == 2 && activateDeathmatch) {
                                            setCountdown(-4);
                                        }
                                    }
                                    roundTimeSeconds--;
                                    if (roundTimeSeconds == 600) {
                                        for (Player all : pgPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                        for (Player all : specPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                    } else if (roundTimeSeconds == 300) {
                                        for (Player all : pgPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                        for (Player all : specPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                    } else if (roundTimeSeconds == 240) {
                                        for (Player all : pgPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                        for (Player all : specPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                    } else if (roundTimeSeconds == 180) {
                                        for (Player all : pgPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                        for (Player all : specPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                    } else if (roundTimeSeconds == 120) {
                                        for (Player all : pgPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                        for (Player all : specPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                    } else if (roundTimeSeconds == 60) {
                                        for (Player all : pgPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                        for (Player all : specPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                    } else if (roundTimeSeconds == 30) {
                                        for (Player all : pgPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                        for (Player all : specPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                    } else if (roundTimeSeconds == 20) {
                                        for (Player all : pgPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                        for (Player all : specPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                    } else if (roundTimeSeconds == 10) {
                                        for (Player all : pgPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                        for (Player all : specPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                    } else if (roundTimeSeconds == 5) {
                                        for (Player all : pgPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                        for (Player all : specPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                    } else if (roundTimeSeconds == 4) {
                                        for (Player all : pgPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                        for (Player all : specPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                    } else if (roundTimeSeconds == 3) {
                                        for (Player all : pgPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                        for (Player all : specPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                    } else if (roundTimeSeconds == 2) {
                                        for (Player all : pgPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                        for (Player all : specPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                    } else if (roundTimeSeconds == 1) {
                                        for (Player all : pgPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                        for (Player all : specPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + roundTimeSeconds + " " + ChatColor.GREEN + chatmessages.get(67));
                                        }
                                    } else if (roundTimeSeconds == 0) {
                                        for (Player all : pgPlayers) {
                                            all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(68));
                                        }
                                        for (Player all : specPlayers) {
                                            all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(68));
                                        }
                                        setCountdown(-3);
                                    }
                                } else if (countdown == -2) {
                                    for (int i = 0; i < pgPlayers.size(); i++) {
                                        Player winner = pgPlayers.get(i);
                                        for (Player all : pgPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + winner.getName() + ChatColor.GREEN + " " + chatmessages.get(4));
                                        }
                                        for (Player all : specPlayers) {
                                            all.sendMessage(prefix + ChatColor.AQUA + winner.getName() + ChatColor.GREEN + " " + chatmessages.get(4));
                                        }
                                        spawnFirework(winner.getLocation(), 1);
                                        addWins(winner.getUniqueId().toString(), 1);
                                        if (enableRewards) {
                                            for (Player all : pgPlayers) {
                                                EconomyResponse r = econ.depositPlayer(all, winningReward);
                                                if (r.transactionSuccess()) {
                                                    all.sendMessage(String.format(prefix + ChatColor.AQUA + chatmessages.get(91) + ChatColor.GREEN + ": " + chatmessages.get(93) + ChatColor.LIGHT_PURPLE + " %s", econ.format(r.amount)));
                                                } else {
                                                    all.sendMessage(String.format(prefix + ChatColor.RED + chatmessages.get(92) + ": %s", r.errorMessage));
                                                }
                                            }
                                        }
                                        setCountdown(-3);
                                    }
                                } else if (countdown == -3) {
                                    if (reset == 10) {
                                        for (Player all : pgPlayers) {
                                            all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(5) + " " + ChatColor.AQUA + reset);
                                        }
                                        for (Player all : specPlayers) {
                                            all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(5) + " " + ChatColor.AQUA + reset);
                                        }
                                        reset--;
                                    } else if (reset <= 9 && reset > 5) {
                                        reset--;
                                    } else if (reset <= 5 && reset > 0) {
                                        for (Player all : pgPlayers) {
                                            all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(5) + " " + ChatColor.AQUA + reset);
                                        }
                                        for (Player all : specPlayers) {
                                            all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(5) + " " + ChatColor.AQUA + reset);
                                        }
                                        reset--;
                                    } else if (reset == 0) {
                                        for (Player all : pgPlayers) {
                                            all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(6));
                                        }
                                        for (Player all : specPlayers) {
                                            all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(6));
                                        }
                                        gamestate = GameStates.RESET;
                                    }
                                } else if (countdown == -4) {
                                    if (!deathmatch) {
                                        if (reset == 10) {
                                            for (Player all : pgPlayers) {
                                                all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(81) + " " + ChatColor.AQUA + reset);
                                            }
                                            for (Player all : specPlayers) {
                                                all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(81) + " " + ChatColor.AQUA + reset);
                                            }
                                            reset--;
                                        } else if (reset <= 9 && reset > 5) {
                                            reset--;
                                        } else if (reset <= 5 && reset > 0) {
                                            for (Player all : pgPlayers) {
                                                all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(81) + " " + ChatColor.AQUA + reset);
                                            }
                                            for (Player all : specPlayers) {
                                                all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(81) + " " + ChatColor.AQUA + reset);
                                            }
                                            reset--;
                                        } else if (reset == 0) {
                                            for (Player all : pgPlayers) {
                                                all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(82));
                                            }
                                            for (Player all : specPlayers) {
                                                all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(82));
                                            }
                                            teleportDeathmatch();
                                            reset = 10;
                                            deathmatch = true;
                                        }
                                    } else {
                                        if (reset == 10) {
                                            for (int world = 1; world < 27; world++) {
                                                if (arenadata.contains("pg.arenas." + world)) {
                                                    String name = arenadata.getString("pg.arenas." + world + ".world");
                                                    assert name != null;
                                                    worlds.add(name);
                                                    Objects.requireNonNull(getServer().getWorld(name)).setPVP(false);
                                                    if (changeGamerules) {
                                                        setGameRules(name);
                                                        Objects.requireNonNull(getServer().getWorld(name)).setDifficulty(Difficulty.PEACEFUL);
                                                        Objects.requireNonNull(getServer().getWorld(name)).setGameRule(GameRule.FALL_DAMAGE, false);
                                                    }
                                                }
                                            }
                                            for (Player all : pgPlayers) {
                                                all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(83) + " " + ChatColor.AQUA + reset);
                                            }
                                            for (Player all : specPlayers) {
                                                all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(83) + " " + ChatColor.AQUA + reset);
                                            }
                                            reset--;
                                        } else if (reset <= 9 && reset > 5) {
                                            reset--;
                                        } else if (reset <= 5 && reset > 0) {
                                            for (Player all : pgPlayers) {
                                                all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(83) + " " + ChatColor.AQUA + reset);
                                                all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                                            }
                                            for (Player all : specPlayers) {
                                                all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(83) + " " + ChatColor.AQUA + reset);
                                                all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                                            }
                                            reset--;
                                        } else if (reset == 0) {
                                            for (int world = 1; world < 27; world++) {
                                                if (arenadata.contains("pg.arenas." + world)) {
                                                    String name = arenadata.getString("pg.arenas." + world + ".world");
                                                    assert name != null;
                                                    worlds.add(name);
                                                    Objects.requireNonNull(getServer().getWorld(name)).setPVP(true);
                                                    if (changeGamerules) {
                                                        setGameRules(name);
                                                        Objects.requireNonNull(getServer().getWorld(name)).setDifficulty(Difficulty.EASY);
                                                        Objects.requireNonNull(getServer().getWorld(name)).setGameRule(GameRule.FALL_DAMAGE, true);
                                                    }
                                                }
                                            }
                                            move = true;
                                            for (Player all : pgPlayers) {
                                                all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(84));
                                                all.playSound(all.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 1);
                                                all.setGameMode(GameMode.SURVIVAL);
                                            }
                                            for (Player all : specPlayers) {
                                                all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(84));
                                                all.playSound(all.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 1);
                                            }
                                            reset--;
                                        } else if (reset == -1) {
                                            if (pgPlayers.size() == 1) {
                                                reset = 10;
                                                setCountdown(-2);
                                                deathmatch = false;
                                            }
                                        }
                                    }
                                }
                            } else {
                                gamestate = GameStates.RESET;
                            }
                        }
                        case RESET -> {
                            if (getConfig().contains("pg.RankWall.headp1") && getConfig().contains("pg.RankWall.headp2") && getConfig().contains("pg.RankWall.headp3") && getConfig().contains("pg.RankWall.signp1") && getConfig().contains("pg.RankWall.signp2") && getConfig().contains("pg.RankWall.signp3")) {
                                ResultSet rs = query("SELECT UUID FROM Stats ORDER BY WINS DESC LIMIT 3");
                                int ii = 0;
                                try {
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
                                        OfflinePlayer name = Bukkit.getOfflinePlayer(UUID.fromString(rank.get(id)));
                                        skull.setOwningPlayer(name);
                                        skull.update();
                                    }
                                    for (int iii = 0; iii < rank.size(); iii++) {
                                        int id = iii + 1;
                                        BlockState b = ranksign.get(iii).getBlock().getState();
                                        OfflinePlayer name = Bukkit.getOfflinePlayer(UUID.fromString(rank.get(id)));
                                        Sign sign = (Sign) b;
                                        sign.line(0, Component.text(chatmessages.get(33) + " #" + id));
                                        sign.line(1, Component.text(Objects.requireNonNull(name.getName())));
                                        sign.line(2, Component.text("Wins: " + getWins(rank.get(id))));
                                        sign.line(3, Component.text("K/D: " + getKD(rank.get(id))));
                                        sign.update();
                                    }
                                } catch (Exception e) {
                                    Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(85));
                                }
                            }
                            if (activateScoreboard) {
                                for (Player all : pgPlayers) {
                                    info.get(all).resetScores("           ");
                                    info.get(all).resetScores("Time");
                                    info.get(all).resetScores("          ");
                                    info.get(all).resetScores("         ");
                                    info.get(all).resetScores("Kills");
                                    info.get(all).resetScores("        ");
                                    Objects.requireNonNull(Objects.requireNonNull(info.get(all).getTeam("team"))).prefix(Component.text(ChatColor.DARK_AQUA + "none"));
                                    Objects.requireNonNull(Objects.requireNonNull(info.get(all).getTeam("kit"))).prefix(Component.text(ChatColor.DARK_AQUA + "none"));
                                }
                            }
                            if (getConfig().contains("pg.Lobby") && getConfig().getLocation("pg.Lobby.sign") != null) {
                                Location loc = getConfig().getLocation("pg.Lobby.sign");
                                assert loc != null;
                                BlockState b = loc.getBlock().getState();
                                Sign sign = (Sign) b;
                                sign.line(0, Component.text("PotionGames"));
                                if (gamestate == GameStates.WAITING || gamestate == GameStates.PREPARING) {
                                    sign.line(1, Component.text(ChatColor.GREEN + gamestate.toString()));
                                } else {
                                    sign.line(1, Component.text(ChatColor.RED + gamestate.toString()));
                                }
                                sign.line(2, Component.text(ChatColor.AQUA + "Voting"));
                                sign.line(3, Component.text(ChatColor.GRAY + "[" + getPlayerAmount() + "/" + maxPlayers + "]"));
                                sign.update();
                            }
                            reset = 10;
                            setCountdown(getConfig().getInt("pg.countdown"));
                            roundTimeSeconds = roundTime * 60;
                            setVote(null);
                            setVotedArena(null);
                            pgPlayers.addAll(specPlayers);
                            for (Player all : pgPlayers) {
                                Location loc = (Location) getConfig().get("pg.Lobby.coords");
                                assert loc != null;
                                all.teleport(loc);
                                all.setGameMode(GameMode.ADVENTURE);
                                PlayerInventory inv = all.getInventory();
                                inv.clear();
                                inv.setHelmet(null);
                                inv.setChestplate(null);
                                inv.setLeggings(null);
                                inv.setBoots(null);
                                all.setHealth(20);
                                all.setFoodLevel(20);
                                clearEffects(all);
                                all.setFireTicks(0);
                                if (activateTeams) {
                                    ItemStack teamselector = new ItemStack(Material.CLOCK);
                                    ItemMeta teamselectormeta = teamselector.getItemMeta();
                                    assert teamselectormeta != null;
                                    teamselectormeta.displayName(Component.text(ChatColor.DARK_AQUA + chatmessages.get(43)));
                                    teamselector.setItemMeta(teamselectormeta);
                                    inv.setItem(4, teamselector);
                                }
                                if (activateKits) {
                                    ItemStack kitselector = new ItemStack(Material.ENDER_CHEST);
                                    ItemMeta kitselectormeta = kitselector.getItemMeta();
                                    assert kitselectormeta != null;
                                    kitselectormeta.displayName(Component.text(ChatColor.DARK_AQUA + chatmessages.get(62)));
                                    kitselector.setItemMeta(kitselectormeta);
                                    inv.setItem(2, kitselector);
                                }
                                if (!singleArena) {
                                    ItemStack votepaper = new ItemStack(Material.PAPER);
                                    ItemMeta votepapaermeta = votepaper.getItemMeta();
                                    assert votepapaermeta != null;
                                    votepapaermeta.displayName(Component.text(ChatColor.DARK_AQUA + chatmessages.get(14)));
                                    votepaper.setItemMeta(votepapaermeta);
                                    inv.setItem(0, votepaper);
                                }
                                if (!startOnJoin) {
                                    ItemStack leave = new ItemStack(Material.MAGMA_CREAM);
                                    ItemMeta leavemeta = leave.getItemMeta();
                                    assert leavemeta != null;
                                    leavemeta.displayName(Component.text(ChatColor.DARK_AQUA + chatmessages.get(80)));
                                    leave.setItemMeta(leavemeta);
                                    inv.setItem(8, leave);
                                }
                                ItemStack stats = new ItemStack(Material.EMERALD);
                                ItemMeta statsmeta = stats.getItemMeta();
                                assert statsmeta != null;
                                statsmeta.displayName(Component.text(ChatColor.DARK_AQUA + chatmessages.get(56)));
                                stats.setItemMeta(statsmeta);
                                inv.setItem(6, stats);
                            }
                            specPlayers.clear();
                            chests.clear();
                            voted.clear();
                            voteallowed = false;
                            voteplayernames.clear();
                            forcearena = false;
                            teamed.clear();
                            teamallowed = false;
                            teamplayernames.clear();
                            kited.clear();
                            kitallowed = false;
                            kitplayernames.clear();
                            richkidPlayers.clear();
                            checkArenas = false;
                            singleArena = false;
                            deathmatch = false;
                            Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("pg.Lobby.world")))).setPVP(false);
                            if (changeGamerules) {
                                setGameRules(getConfig().getString("pg.Lobby.world"));
                                Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("pg.Lobby.world")))).setDifficulty(Difficulty.PEACEFUL);
                                Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("pg.Lobby.world")))).setGameRule(GameRule.FALL_DAMAGE, false);
                            }
                            if (!voteallowed) {
                                voteallowed = true;
                                votes.put(chatmessages.get(42), 0);
                                for (String all : arenas) {
                                    votes.put(all, 0);
                                }
                            }
                            if (!teamallowed) {
                                teamallowed = true;
                                int team = 1;
                                while (team <= teamAmount) {
                                    String name = Integer.toString(team);
                                    teams.remove(name);
                                    teams.add(name);
                                    team++;
                                }
                                teamplayers.put(chatmessages.get(42), 0);
                                for (String all : teams) {
                                    teamplayers.put(all, 0);
                                }
                            }
                            if (!kitallowed) {
                                kitallowed = true;
                                kitplayers.put(chatmessages.get(42), 0);
                                for (String all : kits) {
                                    kitplayers.put(all, 0);
                                }
                            }
                            for (Entry<Location, Block> entry : liquidPlaced.entrySet()) {
                                Location loc = entry.getKey();
                                loc.getBlock().setType(Material.AIR);
                            }
                            for (Entry<Location, Material> entry : placedBlocks.entrySet()) {
                                Location loc = entry.getKey();
                                loc.getBlock().setType(Material.AIR);
                            }
                            for (Entry<Location, Material> entry : breakedBlocks.entrySet()) {
                                Location loc = entry.getKey();
                                Material mat = entry.getValue();
                                loc.getBlock().setType(mat);
                            }
                            for (Entry<Location, BlockData> entry : waterBlocks.entrySet()) {
                                Location loc = entry.getKey();
                                BlockData data = entry.getValue();
                                loc.getBlock().setBlockData(data);
                            }
                            for (int world = 1; world < 27; world++) {
                                if (arenadata.contains("pg.arenas." + world)) {
                                    String name = arenadata.getString("pg.arenas." + world + ".world");
                                    assert name != null;
                                    World worldName = getServer().getWorld(name);
                                    assert worldName != null;
                                    List<Entity> entList = worldName.getEntities();
                                    for (Entity current : entList) {
                                        if (current instanceof Player) {
                                            break;
                                        } else {
                                            current.remove();
                                        }
                                    }
                                }
                            }
                            gamestate = GameStates.WAITING;
                        }
                        default -> {
                            Bukkit.getScheduler().cancelTask(tick);
                            Bukkit.shutdown();
                        }
                    }
                }
            }, 0, 20);
        }
    }

    public void voteResults() {
        try {
            int max = 0;
            for (int i : votes.values()) {
                if (i > max) {
                    max = i;
                }
            }
            String winner = null;
            for (String all : votes.keySet()) {
                if (votes.get(all) == max) {
                    winner = all;
                }
            }
            ArrayList<Integer> arenaNumber = new ArrayList<>();
            for (int ii = 0; ii < 26; ii++) {
                if (arenadata.contains("pg.arenas." + ii)) {
                    arenaNumber.add(ii);
                }
            }
            Random generator = new Random();
            int rndIndex = generator.nextInt(arenaNumber.size());
            int rndArena = arenaNumber.get(rndIndex);
            if (!forcearena && !voted.isEmpty()) {
                boolean randomArena = false;
                int i = 1;
                boolean votedArena = false;
                while (!votedArena) {
                    assert winner != null;
                    if (winner.equals(chatmessages.get(42))) {
                        String arenaName = null;
                        while (arenaName == null) {
                            winner = Integer.toString(rndArena);
                            arenaName = arenadata.getString("pg.arenas." + winner + ".name");
                            if (arenaName != null) {
                                for (Player all : pgPlayers) {
                                    all.sendMessage(prefix + ChatColor.AQUA + arenadata.get("pg.arenas." + winner + ".name") + ChatColor.GREEN + " " + chatmessages.get(7));
                                }
                            }
                            randomArena = true;
                            votedArena = true;
                        }
                    }
                    if (!randomArena) {
                        if (winner.equals(arenadata.getString("pg.arenas." + i + ".name"))) {
                            winner = Integer.toString(i);
                            for (Player all : pgPlayers) {
                                all.sendMessage(prefix + ChatColor.AQUA + arenadata.get("pg.arenas." + winner + ".name") + ChatColor.GREEN + " " + chatmessages.get(7));
                            }
                            votedArena = true;
                        } else {
                            i++;
                        }
                    }
                }
            } else if (forcearena) {
                winner = getVote();
                for (Player all : pgPlayers) {
                    all.sendMessage(prefix + ChatColor.AQUA + votedArena + ChatColor.GREEN + " " + chatmessages.get(7));
                }
            } else {
                String arenaName = null;
                while (arenaName == null) {
                    winner = Integer.toString(rndArena);
                    arenaName = arenadata.getString("pg.arenas." + winner + ".name");
                    if (arenaName != null) {
                        for (Player all : pgPlayers) {
                            all.sendMessage(prefix + ChatColor.AQUA + arenadata.get("pg.arenas." + winner + ".name") + ChatColor.GREEN + " " + chatmessages.get(7));
                        }
                    }
                }
            }
            setVote(winner);
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(90));
            for (Player all : pgPlayers) {
                all.sendMessage(prefix + ChatColor.RED + chatmessages.get(90));
                all.sendMessage(prefix + ChatColor.RED + chatmessages.get(86));
                onLeave(all);
            }
        }
    }

    public void teleportAndStart() {
        try {
            for (Player all : pgPlayers) {
                int maxteamplayers = teamSize;
                boolean teamfound = false;
                if (activateTeams) {
                    if (!teamed.contains(all.getName())) {
                        while (!teamfound) {
                            Random rnd = new Random();
                            int rndTeam = rnd.nextInt(teams.size());
                            rndTeam++;
                            if (teamplayers.get(Integer.toString(rndTeam)) < maxteamplayers && teamplayers.get(Integer.toString(rndTeam)) >= 0) {
                                teamfound = true;
                                int players = teamplayers.get(Integer.toString(rndTeam));
                                players++;
                                teamplayers.put(Integer.toString(rndTeam), players);
                                all.sendMessage(prefix + "--------------" + chatmessages.get(43) + "--------------");
                                all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(45) + ": " + ChatColor.LIGHT_PURPLE + rndTeam);
                                all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(44) + ": " + ChatColor.AQUA + teamplayers.get(Integer.toString(rndTeam)) + ChatColor.GRAY + "/" + ChatColor.AQUA + maxteamplayers);
                                all.sendMessage(prefix + "--------------" + chatmessages.get(43) + "--------------");
                                teamed.add(all.getName());
                                teamplayernames.put(all, Integer.toString(rndTeam));
                                if (activateScoreboard) {
                                    Objects.requireNonNull(all.getScoreboard().getTeam("team")).prefix(Component.text(ChatColor.DARK_AQUA + Integer.toString(rndTeam)));
                                }
                            }
                        }
                    }
                }
                if (activateKits) {
                    if (!kited.contains(all.getName())) {
                        Random rnd = new Random();
                        int rndKit = rnd.nextInt(activeKits);
                        all.sendMessage(prefix + "--------------" + chatmessages.get(62) + "--------------");
                        all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(46) + ": " + ChatColor.LIGHT_PURPLE + kits.get(rndKit));
                        all.sendMessage(prefix + "--------------" + chatmessages.get(62) + "--------------");
                        kited.add(all.getName());
                        kitplayernames.put(all, kits.get(rndKit));
                        if (activateScoreboard) {
                            Objects.requireNonNull(all.getScoreboard().getTeam("kit")).prefix(Component.text(ChatColor.DARK_AQUA + kits.get(rndKit)));
                        }
                        if (kits.get(rndKit).equals("Rich Kid")) {
                            richkidPlayers.add(all);
                        }
                    }
                }
            }
            if (activateTeams) {
                String teamname;
                for (int i = 1; i <= teamAmount; i++) {
                    teamname = Integer.toString(i);
                    if (teamplayers.get(teamname) == 0) {
                        teams.remove(teamname);
                    }
                }
            }
            for (int i = 1; i <= pgPlayers.size(); i++) {
                Player p = pgPlayers.get(i - 1);
                String vote = getVote();
                Location loc = arenadata.getLocation("pg.arenas." + vote + ".spawns." + i);
                assert loc != null;
                p.teleport(loc);
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(88));
            for (Player all : pgPlayers) {
                all.sendMessage(prefix + ChatColor.RED + chatmessages.get(88));
                all.sendMessage(prefix + ChatColor.RED + chatmessages.get(86));
            }
        }
    }

    public void teleportDeathmatch() {
        try {
            for (int i = 1; i <= specPlayers.size(); i++) {
                Player p = pgPlayers.get(i - 1);
                String vote = getVote();
                Location loc = arenadata.getLocation("pg.arenas." + vote + ".deathmatch." + 1);
                assert loc != null;
                p.teleport(loc);
            }
            for (int i = 1; i <= pgPlayers.size(); i++) {
                Player p = pgPlayers.get(i - 1);
                p.setGameMode(GameMode.ADVENTURE);
                move = false;
                String vote = getVote();
                Location loc = arenadata.getLocation("pg.arenas." + vote + ".deathmatch." + i);
                assert loc != null;
                p.teleport(loc);
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(89));
            for (Player all : pgPlayers) {
                all.sendMessage(prefix + ChatColor.RED + chatmessages.get(89));
                all.sendMessage(prefix + ChatColor.RED + chatmessages.get(86));
            }
            for (Player all : specPlayers) {
                all.sendMessage(prefix + ChatColor.RED + chatmessages.get(89));
                all.sendMessage(prefix + ChatColor.RED + chatmessages.get(86));
            }
        }
    }

    public void onJoin(Player p) {
        try {
            if (activateScoreboard) {
                Scoreboard temp = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
                temp.registerNewObjective("main-content", "dummy", Component.text(" " + prefix));
                Objects.requireNonNull(temp.getObjective("main-content")).setDisplaySlot(DisplaySlot.SIDEBAR);
                temp.registerNewTeam("team");
                temp.registerNewTeam("kit");
                temp.registerNewTeam("players");
                temp.registerNewTeam("timeScoreboard");
                temp.registerNewTeam("map");
                temp.registerNewTeam("kills");
                Objects.requireNonNull(Objects.requireNonNull(temp.getTeam("team"))).prefix(Component.text(ChatColor.DARK_AQUA + "none"));
                Objects.requireNonNull(Objects.requireNonNull(temp.getTeam("kit"))).prefix(Component.text(ChatColor.DARK_AQUA + "none"));
                Objects.requireNonNull(temp.getTeam("map")).addEntry(" ");
                Objects.requireNonNull(temp.getTeam("players")).addEntry("       ");
                Objects.requireNonNull(temp.getTeam("timeScoreboard")).addEntry("          ");
                Objects.requireNonNull(temp.getTeam("team")).addEntry("   ");
                Objects.requireNonNull(temp.getTeam("kit")).addEntry("     ");
                Objects.requireNonNull(temp.getTeam("kills")).addEntry("        ");
                info.put(p, temp);
                p.setScoreboard(temp);
            }
            joinChannel(p.getPlayer(), "Local");
            inv.put(p.getName(), p.getInventory().getContents());
            armor.put(p.getName(), p.getInventory().getArmorContents());
            lvl.put(p.getName(), p.getLevel());
            exp.put(p.getName(), p.getExp());
            loc.put(p.getName(), p.getLocation());
            gm.put(p.getName(), p.getGameMode());
            PlayerInventory inventory = p.getInventory();
            inventory.clear();
            inventory.setHelmet(null);
            inventory.setChestplate(null);
            inventory.setLeggings(null);
            inventory.setBoots(null);
            p.setHealth(20);
            p.setFoodLevel(20);
            p.setLevel(0);
            p.setExp(0);
            p.setGameMode(GameMode.ADVENTURE);
            clearEffects(p);
            p.setFireTicks(0);
            playerAmount++;
            if (joinable && playerAmount <= maxPlayers) {
                pgPlayers.add(p);
                Location loc = (Location) getConfig().get("pg.Lobby.coords");
                assert loc != null;
                p.teleport(loc);
                gamestate = GameStates.WAITING;
                if (!tickStarted) {
                    tick();
                    tickStarted = true;
                }
                if (activateTeams) {
                    ItemStack teamselector = new ItemStack(Material.CLOCK);
                    ItemMeta teamselectormeta = teamselector.getItemMeta();
                    assert teamselectormeta != null;
                    teamselectormeta.displayName(Component.text(ChatColor.DARK_AQUA + chatmessages.get(43)));
                    teamselector.setItemMeta(teamselectormeta);
                    p.getInventory().setItem(4, teamselector);
                }
                if (activateKits) {
                    ItemStack kitselector = new ItemStack(Material.ENDER_CHEST);
                    ItemMeta kitselectormeta = kitselector.getItemMeta();
                    assert kitselectormeta != null;
                    kitselectormeta.displayName(Component.text(ChatColor.DARK_AQUA + chatmessages.get(62)));
                    kitselector.setItemMeta(kitselectormeta);
                    p.getInventory().setItem(2, kitselector);
                }
                if (!singleArena) {
                    ItemStack votepaper = new ItemStack(Material.PAPER);
                    ItemMeta votepapaermeta = votepaper.getItemMeta();
                    assert votepapaermeta != null;
                    votepapaermeta.displayName(Component.text(ChatColor.DARK_AQUA + chatmessages.get(14)));
                    votepaper.setItemMeta(votepapaermeta);
                    p.getInventory().setItem(0, votepaper);
                }
                if (!startOnJoin) {
                    ItemStack leave = new ItemStack(Material.MAGMA_CREAM);
                    ItemMeta leavemeta = leave.getItemMeta();
                    assert leavemeta != null;
                    leavemeta.displayName(Component.text(ChatColor.DARK_AQUA + chatmessages.get(80)));
                    leave.setItemMeta(leavemeta);
                    p.getInventory().setItem(8, leave);
                }
                ItemStack stats = new ItemStack(Material.EMERALD);
                ItemMeta statsmeta = stats.getItemMeta();
                assert statsmeta != null;
                statsmeta.displayName(Component.text(ChatColor.DARK_AQUA + chatmessages.get(56)));
                stats.setItemMeta(statsmeta);
                p.getInventory().setItem(6, stats);
                p.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(30));
            }
            if (!joinable && playerAmount <= maxPlayers && joinStarted) {
                specPlayers.add(p);
                String vote = getVote();
                Location loc = arenadata.getLocation("pg.arenas." + vote + ".spawns." + 1);
                assert loc != null;
                p.teleport(loc);
                p.setGameMode(GameMode.SPECTATOR);
                p.setAllowFlight(true);
                p.setFlying(true);
                p.setLevel(0);
                p.setExp(0);
                p.setFireTicks(0);
                p.setHealth(20);
                p.setFoodLevel(20);
                p.setCanPickupItems(false);
                p.setCollidable(false);
                p.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(30) + " " + chatmessages.get(17));
            }
            p.playSound(p.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(87));
            p.sendMessage(prefix + ChatColor.RED + chatmessages.get(87));
            p.sendMessage(prefix + ChatColor.RED + chatmessages.get(86));
        }
    }

    public void onLeave(Player p) {
        if (activateScoreboard) {
            Objects.requireNonNull(p.getScoreboard().getTeam("team")).prefix(Component.text(""));
            Objects.requireNonNull(p.getScoreboard().getTeam("kit")).prefix(Component.text(""));
            p.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());
        }
        joinChannel(p.getPlayer(), "Global");
        if (gamestate == GameStates.INGAME && pgPlayers.size() > 1 && reload) {
            addLosses(p.getUniqueId().toString(), 1);
        }
        p.getInventory().setContents(inv.get(p.getName()));
        p.getInventory().setArmorContents(armor.get(p.getName()));
        p.teleport(loc.get(p.getName()));
        p.setLevel(lvl.get(p.getName()));
        p.setExp(exp.get(p.getName()));
        p.setGameMode(gm.get(p.getName()));
        inv.remove(p.getName());
        armor.remove(p.getName());
        loc.remove(p.getName());
        lvl.remove(p.getName());
        exp.remove(p.getName());
        gm.remove(p.getName());
        pgPlayers.remove(p);
        specPlayers.remove(p);
        playerAmount--;
        if (activateKits) {
            kited.remove(p.getName());
        }
        if (activateTeams) {
            if (teamed.contains(p.getName())) {
                String teamname = null;
                for (int i = 1; i <= teamAmount; i++) {
                    if (Objects.equals(teamplayernames.get(p), Integer.toString(i))) {
                        teamname = Integer.toString(i);
                    }
                }
                teamplayernames.remove(p, teamname);
                teamplayers.put(teamname, teamplayers.get(teamname) - 1);
                if (gamestate == GameStates.INGAME) {
                    if (teamplayers.get(teamname) == 0) {
                        teams.remove(teamname);
                    }
                }
                teamed.remove(p.getName());
            }
        }
        if (voted.contains(p.getName())) {
            String arenaName = null;
            for (int i = 0; i <= arenas.size(); i++) {
                if (arenadata.contains("pg.arenas." + i)) {
                    if (voteplayernames.containsKey(p) && voteplayernames.containsValue(arenadata.getString("pg.arenas." + i + ".name"))) {
                        arenaName = arenadata.getString("pg.arenas." + i + ".name");
                    }
                } else {
                    arenaName = chatmessages.get(42);
                }
            }
            voteplayernames.remove(p, arenaName);
            votes.replace(arenaName, votes.get(arenaName) - 1);
            voted.remove(p.getName());
        }
        if (playerAmount == 0) {
            gamestate = GameStates.RESET;
        }
        p.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(32));
        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1, 1);
    }

    public void tickLobby(String s) {
        if (lobbySystem) {
            countdownLobby.put(s, getConfig().getInt("pg.countdown"));
            int roundTimeSecondsNumber = lobbyroundTime.get(s) * 60;
            lobbyroundTimeSeconds.put(s, roundTimeSecondsNumber);
            resetLobby.put(s, reset);
            tick = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                if (!lobbyPause.get(s)) {
                    switch (lobbyStates.get(s)) {
                        case WAITING -> {
                            if (activateScoreboard) {
                                for (Player all : playerLobby.keySet()) {
                                    if (playerLobby.get(all).equals(s)) {
                                        Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("").setScore(11);
                                        Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("Map").setScore(10);
                                        Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore(" ").setScore(9);
                                        Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("  ").setScore(8);
                                        Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("Team").setScore(7);
                                        Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("   ").setScore(6);
                                        Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("    ").setScore(5);
                                        Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("Kit").setScore(4);
                                        Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("     ").setScore(3);
                                        Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("      ").setScore(2);
                                        Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("Players").setScore(1);
                                        Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("       ").setScore(0);
                                    }
                                }
                            }
                            if (arenadata.contains("pg.lobbies." + s) && arenadata.getLocation("pg.lobbies." + s + ".sign") != null) {
                                Location loc = arenadata.getLocation("pg.lobbies." + s + ".sign");
                                assert loc != null;
                                BlockState b = loc.getBlock().getState();
                                Sign sign = (Sign) b;
                                sign.line(0, Component.text(s));
                                if (lobbyStates.get(s) == GameStates.WAITING || lobbyStates.get(s) == GameStates.PREPARING) {
                                    sign.line(1, Component.text(ChatColor.GREEN + lobbyStates.get(s).toString()));
                                } else {
                                    sign.line(1, Component.text(ChatColor.RED + lobbyStates.get(s).toString()));
                                }
                                if (lobbyVote.get(s) != null) {
                                    sign.line(2, Component.text(ChatColor.GOLD + Objects.requireNonNull(arenadata.get("pg.lobbies." + s + "." + lobbyVote.get(s) + ".name")).toString()));
                                    if (activateScoreboard) {
                                        for (Player all : playerLobby.keySet()) {
                                            if (playerLobby.get(all).equals(s)) {
                                                Objects.requireNonNull(info.get(all).getTeam("map")).prefix(Component.text(ChatColor.DARK_AQUA + Objects.requireNonNull(arenadata.get("pg.lobbies." + s + "." + lobbyVote.get(s) + ".name")).toString()));
                                            }
                                        }
                                    }
                                } else {
                                    sign.line(2, Component.text(ChatColor.AQUA + "Voting"));
                                    if (activateScoreboard) {
                                        for (Player all : playerLobby.keySet()) {
                                            if (playerLobby.get(all).equals(s)) {
                                                Objects.requireNonNull(info.get(all).getTeam("map")).prefix(Component.text(ChatColor.DARK_AQUA + "Voting"));
                                            }
                                        }
                                    }
                                }
                                sign.line(3, Component.text(ChatColor.GRAY + "[" + lobbyAmount.get(s).toString() + "/" + lobbymaxPlayers.get(s) + "]"));
                                if (activateScoreboard) {
                                    for (Player all : playerLobby.keySet()) {
                                        if (playerLobby.get(all).equals(s)) {
                                            Objects.requireNonNull(info.get(all).getTeam("players")).prefix(Component.text("" + ChatColor.DARK_AQUA + lobbyAmount.get(s).toString() + "/" + lobbymaxPlayers.get(s)));
                                        }
                                    }
                                }
                                sign.update();
                                infoLobby.replace(s, "" + sign.line(1) + ChatColor.WHITE + ", " + sign.line(2) + ChatColor.WHITE + ", " + sign.line(3));
                            }
                            if (!lobbyCheckArenas.get(s)) {
                                lobbyCheckArenas.replace(s, true);
                                if (lobbyvotes.get(s).size() == 2) {
                                    String arena = arenadata.getString("pg.lobbies." + s + "." + 1 + ".name");
                                    String arenaNumber = null;
                                    lobbyForcearena.replace(s, true);
                                    int i = 1;
                                    boolean votetedarena = false;
                                    while (!votetedarena) {
                                        assert arena != null;
                                        if (arena.matches(Objects.requireNonNull(arenadata.getString("pg.lobbies." + s + "." + i + ".name")))) {
                                            arenaNumber = Integer.toString(i);
                                            lobbyVotedarena.replace(s, arena);
                                            votetedarena = true;
                                        } else {
                                            i++;
                                        }
                                    }
                                    lobbyVote.replace(s, arenaNumber);
                                    lobbySingleArena.replace(s, true);
                                }
                            }
                            for (Player all : specLobby.keySet()) {
                                if (specLobby.get(all).equals(s)) {
                                    specLobby.remove(all);
                                }
                            }
                            lobbyMove.replace(s, true);
                            lobbyJoinable.replace(s, true);
                            if (arenadata.contains("pg.lobbies." + s + ".world")) {
                                worlds.add(getConfig().getString("pg.lobbies." + s + ".world"));
                                Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(arenadata.getString("pg.lobbies." + s + ".world")))).setPVP(false);
                                if (changeGamerules) {
                                    setGameRules(arenadata.getString("pg.lobbies." + s + ".world"));
                                    Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(arenadata.getString("pg.lobbies." + s + ".world")))).setDifficulty(Difficulty.PEACEFUL);
                                    Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(arenadata.getString("pg.lobbies." + s + ".world")))).setGameRule(GameRule.FALL_DAMAGE, false);
                                }
                            }
                            lobbyJoinable.replace(s, true);
                            if (lobbyAmount.get(s) < lobbyminPlayers.get(s)) {
                                for (Player all : playerLobby.keySet()) {
                                    if (playerLobby.get(all).equals(s)) {
                                        all.setLevel(0);
                                        all.sendActionBar(Component.text(prefix + chatmessages.get(0) + " " + "[" + ChatColor.AQUA + lobbyAmount.get(s) + ChatColor.GRAY + "/" + ChatColor.AQUA + lobbyminPlayers.get(s) + ChatColor.GRAY + "]"));
                                    }
                                }
                                countdownLobby.replace(s, getConfig().getInt("pg.countdown"));
                            } else {
                                lobbyStates.replace(s, GameStates.PREPARING);
                                for (Player all : playerLobby.keySet()) {
                                    if (playerLobby.get(all).equals(s)) {
                                        all.playSound(all.getLocation(), Sound.ENTITY_ARROW_HIT, 1, 1);
                                    }
                                }
                                for (Player all : Bukkit.getOnlinePlayers()) {
                                    if (playerChannel.get(all) == null) {
                                        joinChannel(all.getPlayer(), "Global");
                                    }
                                    if (broadcastStarting && playerChannel.get(all).contains("Global")) {
                                        all.sendMessage(String.format(prefix + chatmessages.get(95), " " + s, " " + s));
                                    }
                                }
                            }
                        }
                        case PREPARING -> {
                            if (arenadata.contains("pg.lobbies." + s) && arenadata.getLocation("pg.lobbies." + s + ".sign") != null) {
                                Location loc = arenadata.getLocation("pg.lobbies." + s + ".sign");
                                assert loc != null;
                                BlockState b = loc.getBlock().getState();
                                Sign sign = (Sign) b;
                                sign.line(0, Component.text(s));
                                if (lobbyStates.get(s) == GameStates.WAITING || lobbyStates.get(s) == GameStates.PREPARING) {
                                    sign.line(1, Component.text(ChatColor.GREEN + lobbyStates.get(s).toString()));
                                } else {
                                    sign.line(1, Component.text(ChatColor.RED + lobbyStates.get(s).toString()));
                                }
                                if (lobbyVote.get(s) != null) {
                                    sign.line(2, Component.text(ChatColor.GOLD + Objects.requireNonNull(arenadata.get("pg.lobbies." + s + "." + lobbyVote.get(s) + ".name")).toString()));
                                    if (activateScoreboard) {
                                        for (Player all : playerLobby.keySet()) {
                                            if (playerLobby.get(all).equals(s)) {
                                                Objects.requireNonNull(info.get(all).getTeam("map")).prefix(Component.text(ChatColor.DARK_AQUA + Objects.requireNonNull(arenadata.get("pg.lobbies." + s + "." + lobbyVote.get(s) + ".name")).toString()));
                                            }
                                        }
                                    }
                                } else {
                                    sign.line(2, Component.text(ChatColor.AQUA + "Voting"));
                                    if (activateScoreboard) {
                                        for (Player all : playerLobby.keySet()) {
                                            if (playerLobby.get(all).equals(s)) {
                                                Objects.requireNonNull(info.get(all).getTeam("map")).prefix(Component.text(ChatColor.DARK_AQUA + "Voting"));
                                            }
                                        }
                                    }
                                }
                                sign.line(3, Component.text(ChatColor.GRAY + "[" + lobbyAmount.get(s).toString() + "/" + lobbymaxPlayers.get(s) + "]"));
                                if (activateScoreboard) {
                                    for (Player all : playerLobby.keySet()) {
                                        if (playerLobby.get(all).equals(s)) {
                                            Objects.requireNonNull(info.get(all).getTeam("players")).prefix(Component.text("" + ChatColor.DARK_AQUA + lobbyAmount.get(s).toString() + "/" + lobbymaxPlayers.get(s)));
                                        }
                                    }
                                }
                                sign.update();
                                infoLobby.replace(s, "" + sign.line(1) + ChatColor.WHITE + ", " + sign.line(2) + ChatColor.WHITE + ", " + sign.line(3));
                            }
                            if (lobbyAmount.get(s) >= lobbyminPlayers.get(s)) {
                                for (Player all : playerLobby.keySet()) {
                                    if (playerLobby.get(all).equals(s)) {
                                        all.setLevel(countdownLobby.get(s));
                                    }
                                }
                                if (countdownLobby.get(s) == 10) {
                                    for (Player all : playerLobby.keySet()) {
                                        if (playerLobby.get(all).equals(s)) {
                                            all.setLevel(countdownLobby.get(s));
                                            all.playSound(all.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
                                        }
                                    }
                                    voteResultsLobby(s);
                                }
                                if (countdownLobby.get(s) < 10) {
                                    for (Player all : playerLobby.keySet()) {
                                        if (playerLobby.get(all).equals(s)) {
                                            all.setLevel(countdownLobby.get(s));
                                        }
                                    }
                                }
                                if (countdownLobby.get(s) == 0) {
                                    for (Player all : playerLobby.keySet()) {
                                        if (playerLobby.get(all).equals(s)) {
                                            all.setLevel(countdownLobby.get(s));
                                        }
                                    }
                                    teleportAndStartLobby(s);
                                    countdownLobby.replace(s, 11);
                                    lobbyStates.replace(s, GameStates.INGAME);
                                    lobbyMove.replace(s, false);
                                }
                                countdownLobby.replace(s, countdownLobby.get(s) - 1);
                            } else {
                                lobbyStates.replace(s, GameStates.WAITING);
                            }
                        }
                        case INGAME -> {
                            if (arenadata.contains("pg.lobbies." + s) && arenadata.getLocation("pg.lobbies." + s + ".sign") != null) {
                                Location loc = arenadata.getLocation("pg.lobbies." + s + ".sign");
                                assert loc != null;
                                BlockState b = loc.getBlock().getState();
                                Sign sign = (Sign) b;
                                sign.line(0, Component.text(s));
                                if (lobbyStates.get(s) == GameStates.WAITING || lobbyStates.get(s) == GameStates.PREPARING) {
                                    sign.line(1, Component.text(ChatColor.GREEN + lobbyStates.get(s).toString()));
                                } else {
                                    sign.line(1, Component.text(ChatColor.RED + lobbyStates.get(s).toString()));
                                }
                                if (lobbyVote.get(s) != null) {
                                    sign.line(2, Component.text(ChatColor.GOLD + Objects.requireNonNull(arenadata.get("pg.lobbies." + s + "." + lobbyVote.get(s) + ".name")).toString()));
                                } else {
                                    sign.line(2, Component.text(ChatColor.AQUA + "Voting"));
                                }
                                sign.line(3, Component.text(ChatColor.GRAY + "[" + lobbyAmount.get(s).toString() + "/" + lobbymaxPlayers.get(s) + "]"));
                                if (activateScoreboard) {
                                    for (Player all : playerLobby.keySet()) {
                                        if (playerLobby.get(all).equals(s)) {
                                            Objects.requireNonNull(info.get(all).getTeam("players")).prefix(Component.text("" + ChatColor.DARK_AQUA + lobbyAmount.get(s).toString() + "/" + lobbymaxPlayers.get(s)));
                                        }
                                    }
                                }
                                sign.update();
                                infoLobby.replace(s, "" + sign.line(1) + ChatColor.WHITE + ", " + sign.line(2) + ChatColor.WHITE + ", " + sign.line(3));
                            }
                            if (countdownLobby.get(s) != 0 && countdownLobby.get(s) != -1 && countdownLobby.get(s) != -4) {
                                for (int world = 1; world < 27; world++) {
                                    if (arenadata.contains("pg.lobbies." + s + "." + world)) {
                                        String name = arenadata.getString("pg.lobbies." + s + "." + world + ".world");
                                        assert name != null;
                                        worlds.add(name);
                                        Objects.requireNonNull(getServer().getWorld(name)).setPVP(false);
                                        if (changeGamerules) {
                                            setGameRules(name);
                                            Objects.requireNonNull(getServer().getWorld(name)).setDifficulty(Difficulty.PEACEFUL);
                                            Objects.requireNonNull(getServer().getWorld(name)).setGameRule(GameRule.FALL_DAMAGE, false);
                                        }
                                    }
                                }
                            }
                            lobbyJoinable.replace(s, false);
                            if (lobbyAmount.get(s) != 0) {
                                if (countdownLobby.get(s) == 10) {
                                    for (Player all : playerLobby.keySet()) {
                                        if (playerLobby.get(all).equals(s)) {
                                            all.getInventory().clear();
                                            all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(1) + " " + ChatColor.AQUA + countdownLobby.get(s));
                                            ItemStack playercompass = new ItemStack(Material.COMPASS);
                                            ItemMeta playercompassmeta = playercompass.getItemMeta();
                                            assert playercompassmeta != null;
                                            playercompassmeta.displayName(Component.text(ChatColor.DARK_AQUA + chatmessages.get(2)));
                                            playercompass.setItemMeta(playercompassmeta);
                                            if (compassOnSpawn) {
                                                all.getInventory().setItem(8, playercompass);
                                            }
                                            all.playSound(all.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
                                        }
                                    }
                                    for (Player rich : richkidPlayers) {
                                        if (playerLobby.get(rich).equals(s)) {
                                            for (int i = 0; i < 5; i++) {
                                                rich.getInventory().addItem(coin);
                                            }
                                        }
                                    }
                                    countdownLobby.replace(s, countdownLobby.get(s) - 1);
                                } else if (countdownLobby.get(s) <= 9 && countdownLobby.get(s) > 5) {
                                    countdownLobby.replace(s, countdownLobby.get(s) - 1);
                                } else if (countdownLobby.get(s) <= 5 && countdownLobby.get(s) > 0) {
                                    for (Player all : playerLobby.keySet()) {
                                        if (playerLobby.get(all).equals(s)) {
                                            all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(1) + " " + ChatColor.AQUA + countdownLobby.get(s));
                                            all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                                        }
                                    }
                                    countdownLobby.replace(s, countdownLobby.get(s) - 1);
                                } else if (countdownLobby.get(s) == 0) {
                                    for (int world = 1; world < 27; world++) {
                                        if (arenadata.contains("pg.lobbies." + s + "." + world)) {
                                            String name = arenadata.getString("pg.lobbies." + s + "." + world + ".world");
                                            assert name != null;
                                            worlds.add(name);
                                            Objects.requireNonNull(getServer().getWorld(name)).setPVP(true);
                                            if (changeGamerules) {
                                                setGameRules(name);
                                                Objects.requireNonNull(getServer().getWorld(name)).setDifficulty(Difficulty.EASY);
                                                Objects.requireNonNull(getServer().getWorld(name)).setGameRule(GameRule.FALL_DAMAGE, true);
                                            }
                                        }
                                    }
                                    lobbyMove.replace(s, true);
                                    for (Player all : playerLobby.keySet()) {
                                        if (playerLobby.get(all).equals(s)) {
                                            all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(3));
                                            all.playSound(all.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                                            all.setGameMode(GameMode.SURVIVAL);
                                            countdownLobby.replace(s, -1);
                                        }
                                    }
                                } else if (countdownLobby.get(s) == -1) {
                                    if (activateScoreboard) {
                                        for (Player all : playerLobby.keySet()) {
                                            if (playerLobby.get(all).equals(s)) {
                                                info.get(all).resetScores("");
                                                info.get(all).resetScores("Map");
                                                info.get(all).resetScores(" ");
                                                info.get(all).resetScores("  ");
                                                info.get(all).resetScores("Team");
                                                info.get(all).resetScores("   ");
                                                info.get(all).resetScores("    ");
                                                info.get(all).resetScores("Kit");
                                                info.get(all).resetScores("     ");
                                                Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("           ").setScore(8);
                                                Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("Time").setScore(7);
                                                Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("          ").setScore(6);
                                                Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("         ").setScore(5);
                                                Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("Kills").setScore(4);
                                                Objects.requireNonNull(info.get(all).getObjective("main-content")).getScore("        ").setScore(3);
                                                Objects.requireNonNull(info.get(all).getTeam("timeScoreboard")).prefix(Component.text("" + ChatColor.DARK_AQUA + lobbyroundTimeSeconds.get(s) / 60 + " min"));
                                                Objects.requireNonNull(info.get(all).getTeam("kills")).prefix(Component.text(ChatColor.DARK_AQUA + "0"));
                                                String tempString = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(info.get(all).getTeam("kills")).prefix());
                                                tempString = ChatColor.stripColor(tempString);
                                                int tempInt = Integer.parseInt(tempString);
                                                Objects.requireNonNull(info.get(all).getTeam("kills")).prefix(Component.text("" + ChatColor.DARK_AQUA + tempInt));
                                            }
                                        }
                                    }
                                    int temp = 0;
                                    for (Player all : playerLobby.keySet()) {
                                        if (playerLobby.get(all).equals(s)) {
                                            temp++;
                                        }
                                    }
                                    if (lobbyActivateTeams.get(s)) {
                                        if (temp == 1 || lobbyteams.get(s).size() == 1) {
                                            countdownLobby.replace(s, -2);
                                        } else if (temp == 2 && lobbyteams.get(s).size() == 2 && activateDeathmatch) {
                                            countdownLobby.replace(s, -4);
                                        }
                                    } else {
                                        if (temp == 1) {
                                            countdownLobby.replace(s, -2);
                                        } else if (temp == 2 && activateDeathmatch) {
                                            countdownLobby.replace(s, -4);
                                        }
                                    }
                                    lobbyroundTimeSeconds.replace(s, lobbyroundTimeSeconds.get(s) - 1);
                                    if (lobbyroundTimeSeconds.get(s) == 600) {
                                        for (Player all : playerLobby.keySet()) {
                                            if (playerLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                        for (Player all : specLobby.keySet()) {
                                            if (specLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                    } else if (lobbyroundTimeSeconds.get(s) == 300) {
                                        for (Player all : playerLobby.keySet()) {
                                            if (playerLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                        for (Player all : specLobby.keySet()) {
                                            if (specLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                    } else if (lobbyroundTimeSeconds.get(s) == 240) {
                                        for (Player all : playerLobby.keySet()) {
                                            if (playerLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                        for (Player all : specLobby.keySet()) {
                                            if (specLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                    } else if (lobbyroundTimeSeconds.get(s) == 180) {
                                        for (Player all : playerLobby.keySet()) {
                                            if (playerLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                        for (Player all : specLobby.keySet()) {
                                            if (specLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                    } else if (lobbyroundTimeSeconds.get(s) == 120) {
                                        for (Player all : playerLobby.keySet()) {
                                            if (playerLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                        for (Player all : specLobby.keySet()) {
                                            if (specLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                    } else if (lobbyroundTimeSeconds.get(s) == 60) {
                                        for (Player all : playerLobby.keySet()) {
                                            if (playerLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                        for (Player all : specLobby.keySet()) {
                                            if (specLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                    } else if (lobbyroundTimeSeconds.get(s) == 30) {
                                        for (Player all : playerLobby.keySet()) {
                                            if (playerLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                        for (Player all : specLobby.keySet()) {
                                            if (specLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                    } else if (lobbyroundTimeSeconds.get(s) == 20) {
                                        for (Player all : playerLobby.keySet()) {
                                            if (playerLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                        for (Player all : specLobby.keySet()) {
                                            if (specLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                    } else if (lobbyroundTimeSeconds.get(s) == 10) {
                                        for (Player all : playerLobby.keySet()) {
                                            if (playerLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                        for (Player all : specLobby.keySet()) {
                                            if (specLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                    } else if (lobbyroundTimeSeconds.get(s) == 5) {
                                        for (Player all : playerLobby.keySet()) {
                                            if (playerLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                        for (Player all : specLobby.keySet()) {
                                            if (specLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                    } else if (lobbyroundTimeSeconds.get(s) == 4) {
                                        for (Player all : playerLobby.keySet()) {
                                            if (playerLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                        for (Player all : specLobby.keySet()) {
                                            if (specLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                    } else if (lobbyroundTimeSeconds.get(s) == 3) {
                                        for (Player all : playerLobby.keySet()) {
                                            if (playerLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                        for (Player all : specLobby.keySet()) {
                                            if (specLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                    } else if (lobbyroundTimeSeconds.get(s) == 2) {
                                        for (Player all : playerLobby.keySet()) {
                                            if (playerLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                        for (Player all : specLobby.keySet()) {
                                            if (specLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                    } else if (lobbyroundTimeSeconds.get(s) == 1) {
                                        for (Player all : playerLobby.keySet()) {
                                            if (playerLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                        for (Player all : specLobby.keySet()) {
                                            if (specLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.AQUA + lobbyroundTimeSeconds.get(s) + " " + ChatColor.GREEN + chatmessages.get(67));
                                            }
                                        }
                                    } else if (lobbyroundTimeSeconds.get(s) == 0) {
                                        for (Player all : playerLobby.keySet()) {
                                            if (playerLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(68));
                                            }
                                        }
                                        for (Player all : specLobby.keySet()) {
                                            if (specLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(68));
                                            }
                                        }
                                        countdownLobby.replace(s, -3);
                                    }
                                } else if (countdownLobby.get(s) == -2) {
                                    Player winner;
                                    for (Player all : playerLobby.keySet()) {
                                        if (playerLobby.get(all).equals(s)) {
                                            for (Player win : playerLobby.keySet()) {
                                                if (playerLobby.get(win).equals(s)) {
                                                    winner = win;
                                                    spawnFirework(winner.getLocation(), 1);
                                                    addWins(winner.getUniqueId().toString(), 1);
                                                    all.sendMessage(prefix + ChatColor.AQUA + winner.getName() + ChatColor.GREEN + " " + chatmessages.get(4));
                                                    if (enableRewards) {
                                                        EconomyResponse r = econ.depositPlayer(all, winningReward);
                                                        if (r.transactionSuccess()) {
                                                            all.sendMessage(String.format(prefix + ChatColor.AQUA + chatmessages.get(91) + ChatColor.GREEN + ": " + chatmessages.get(93) + ChatColor.LIGHT_PURPLE + " %s", econ.format(r.amount)));
                                                        } else {
                                                            all.sendMessage(String.format(prefix + ChatColor.RED + chatmessages.get(92) + ": %s", r.errorMessage));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    for (Player all : specLobby.keySet()) {
                                        if (specLobby.get(all).equals(s)) {
                                            for (Player win : playerLobby.keySet()) {
                                                if (playerLobby.get(win).equals(s)) {
                                                    winner = win;
                                                    all.sendMessage(prefix + ChatColor.AQUA + winner.getName() + ChatColor.GREEN + " " + chatmessages.get(4));
                                                }
                                            }
                                        }
                                    }
                                    countdownLobby.replace(s, -3);
                                } else if (countdownLobby.get(s) == -3) {
                                    if (resetLobby.get(s) == 10) {
                                        for (Player all : playerLobby.keySet()) {
                                            if (playerLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(5) + " " + ChatColor.AQUA + resetLobby.get(s));
                                            }
                                        }
                                        for (Player all : specLobby.keySet()) {
                                            if (specLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(5) + " " + ChatColor.AQUA + resetLobby.get(s));
                                            }
                                        }
                                        resetLobby.put(s, resetLobby.get(s) - 1);
                                    } else if (resetLobby.get(s) <= 9 && resetLobby.get(s) > 5) {
                                        resetLobby.put(s, resetLobby.get(s) - 1);
                                    } else if (resetLobby.get(s) <= 5 && resetLobby.get(s) > 0) {
                                        for (Player all : playerLobby.keySet()) {
                                            if (playerLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(5) + " " + ChatColor.AQUA + resetLobby.get(s));
                                            }
                                        }
                                        for (Player all : specLobby.keySet()) {
                                            if (specLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(5) + " " + ChatColor.AQUA + resetLobby.get(s));
                                            }
                                        }
                                        resetLobby.put(s, resetLobby.get(s) - 1);
                                    } else if (resetLobby.get(s) == 0) {
                                        for (Player all : playerLobby.keySet()) {
                                            if (playerLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(6));
                                            }
                                        }
                                        for (Player all : specLobby.keySet()) {
                                            if (specLobby.get(all).equals(s)) {
                                                all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(6));
                                            }
                                        }
                                        lobbyStates.replace(s, GameStates.RESET);
                                    }
                                } else if (countdownLobby.get(s) == -4) {
                                    if (!lobbyDeathmatch.get(s)) {
                                        if (resetLobby.get(s) == 10) {
                                            for (Player all : playerLobby.keySet()) {
                                                if (playerLobby.get(all).equals(s)) {
                                                    all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(81) + " " + ChatColor.AQUA + resetLobby.get(s));
                                                }
                                            }
                                            for (Player all : specLobby.keySet()) {
                                                if (specLobby.get(all).equals(s)) {
                                                    all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(81) + " " + ChatColor.AQUA + resetLobby.get(s));
                                                }
                                            }
                                            resetLobby.put(s, resetLobby.get(s) - 1);
                                        } else if (resetLobby.get(s) <= 9 && resetLobby.get(s) > 5) {
                                            resetLobby.put(s, resetLobby.get(s) - 1);
                                        } else if (resetLobby.get(s) <= 5 && resetLobby.get(s) > 0) {
                                            for (Player all : playerLobby.keySet()) {
                                                if (playerLobby.get(all).equals(s)) {
                                                    all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(81) + " " + ChatColor.AQUA + resetLobby.get(s));
                                                }
                                            }
                                            for (Player all : specLobby.keySet()) {
                                                if (specLobby.get(all).equals(s)) {
                                                    all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(81) + " " + ChatColor.AQUA + resetLobby.get(s));
                                                }
                                            }
                                            resetLobby.put(s, resetLobby.get(s) - 1);
                                        } else if (resetLobby.get(s) == 0) {
                                            for (Player all : playerLobby.keySet()) {
                                                if (playerLobby.get(all).equals(s)) {
                                                    all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(82));
                                                }
                                            }
                                            for (Player all : specLobby.keySet()) {
                                                if (specLobby.get(all).equals(s)) {
                                                    all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(82));
                                                }
                                            }
                                            teleportDeathmatchLobby(s);
                                            resetLobby.put(s, 10);
                                            lobbyDeathmatch.replace(s, true);
                                        }
                                    } else {
                                        if (resetLobby.get(s) == 10) {
                                            for (int world = 1; world < 27; world++) {
                                                if (arenadata.contains("pg.lobbies." + s + "." + world)) {
                                                    String name = arenadata.getString("pg.lobbies." + s + "." + world + ".world");
                                                    assert name != null;
                                                    worlds.add(name);
                                                    Objects.requireNonNull(getServer().getWorld(name)).setPVP(false);
                                                    if (changeGamerules) {
                                                        setGameRules(name);
                                                        Objects.requireNonNull(getServer().getWorld(name)).setDifficulty(Difficulty.PEACEFUL);
                                                        Objects.requireNonNull(getServer().getWorld(name)).setGameRule(GameRule.FALL_DAMAGE, false);
                                                    }
                                                }
                                            }
                                            for (Player all : playerLobby.keySet()) {
                                                if (playerLobby.get(all).equals(s)) {
                                                    all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(83) + " " + ChatColor.AQUA + resetLobby.get(s));
                                                }
                                            }
                                            for (Player all : specLobby.keySet()) {
                                                if (specLobby.get(all).equals(s)) {
                                                    all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(83) + " " + ChatColor.AQUA + resetLobby.get(s));
                                                }
                                            }
                                            resetLobby.put(s, resetLobby.get(s) - 1);
                                        } else if (resetLobby.get(s) <= 9 && resetLobby.get(s) > 5) {
                                            resetLobby.put(s, resetLobby.get(s) - 1);
                                        } else if (resetLobby.get(s) <= 5 && resetLobby.get(s) > 0) {
                                            for (Player all : playerLobby.keySet()) {
                                                if (playerLobby.get(all).equals(s)) {
                                                    all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(83) + " " + ChatColor.AQUA + resetLobby.get(s));
                                                    all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                                                }
                                            }
                                            for (Player all : specLobby.keySet()) {
                                                if (specLobby.get(all).equals(s)) {
                                                    all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(83) + " " + ChatColor.AQUA + resetLobby.get(s));
                                                    all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                                                }
                                            }
                                            resetLobby.put(s, resetLobby.get(s) - 1);
                                        } else if (resetLobby.get(s) == 0) {
                                            for (int world = 1; world < 27; world++) {
                                                if (arenadata.contains("pg.lobbies." + s + "." + world)) {
                                                    String name = arenadata.getString("pg.lobbies." + s + "." + world + ".world");
                                                    assert name != null;
                                                    worlds.add(name);
                                                    Objects.requireNonNull(getServer().getWorld(name)).setPVP(true);
                                                    if (changeGamerules) {
                                                        setGameRules(name);
                                                        Objects.requireNonNull(getServer().getWorld(name)).setDifficulty(Difficulty.EASY);
                                                        Objects.requireNonNull(getServer().getWorld(name)).setGameRule(GameRule.FALL_DAMAGE, true);
                                                    }
                                                }
                                            }
                                            lobbyMove.replace(s, true);
                                            for (Player all : playerLobby.keySet()) {
                                                if (playerLobby.get(all).equals(s)) {
                                                    all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(84));
                                                    all.playSound(all.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                                                    all.setGameMode(GameMode.SURVIVAL);
                                                }
                                            }
                                            for (Player all : specLobby.keySet()) {
                                                if (specLobby.get(all).equals(s)) {
                                                    all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(84));
                                                    all.playSound(all.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                                                }
                                            }
                                            resetLobby.put(s, resetLobby.get(s) - 1);
                                        } else if (resetLobby.get(s) == -1) {
                                            int temp = 0;
                                            for (Player all : playerLobby.keySet()) {
                                                if (playerLobby.get(all).equals(s)) {
                                                    temp++;
                                                }
                                            }
                                            if (temp == 1) {
                                                resetLobby.put(s, 10);
                                                countdownLobby.replace(s, -2);
                                                lobbyDeathmatch.replace(s, false);
                                            }
                                        }
                                    }
                                }
                            } else {
                                lobbyStates.replace(s, GameStates.RESET);
                            }
                        }
                        case RESET -> {
                            if (getConfig().contains("pg.RankWall.headp1") && getConfig().contains("pg.RankWall.headp2") && getConfig().contains("pg.RankWall.headp3") && getConfig().contains("pg.RankWall.signp1") && getConfig().contains("pg.RankWall.signp2") && getConfig().contains("pg.RankWall.signp3")) {
                                ResultSet rs = query("SELECT UUID FROM Stats ORDER BY WINS DESC LIMIT 3");
                                int ii = 0;
                                try {
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
                                        OfflinePlayer sname = Bukkit.getOfflinePlayer(UUID.fromString(rank.get(id)));
                                        skull.setOwningPlayer(sname);
                                        skull.update();
                                    }
                                    for (int iii = 0; iii < rank.size(); iii++) {
                                        int id = iii + 1;
                                        BlockState b = ranksign.get(iii).getBlock().getState();
                                        OfflinePlayer name = Bukkit.getOfflinePlayer(UUID.fromString(rank.get(id)));
                                        Sign sign = (Sign) b;
                                        sign.line(0, Component.text(chatmessages.get(33) + " #" + id));
                                        sign.line(1, Component.text(Objects.requireNonNull(name.getName())));
                                        sign.line(2, Component.text("Wins: " + getWins(rank.get(id))));
                                        sign.line(3, Component.text("K/D: " + getKD(rank.get(id))));
                                        sign.update();
                                    }
                                } catch (Exception e) {
                                    Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(85));
                                }
                            }
                            if (activateScoreboard) {
                                for (Player all : playerLobby.keySet()) {
                                    if (playerLobby.get(all).equals(s)) {
                                        info.get(all).resetScores("           ");
                                        info.get(all).resetScores("Time");
                                        info.get(all).resetScores("          ");
                                        info.get(all).resetScores("         ");
                                        info.get(all).resetScores("Kills");
                                        info.get(all).resetScores("        ");
                                        Objects.requireNonNull(Objects.requireNonNull(info.get(all).getTeam("team"))).prefix(Component.text(ChatColor.DARK_AQUA + "none"));
                                        Objects.requireNonNull(Objects.requireNonNull(info.get(all).getTeam("kit"))).prefix(Component.text(ChatColor.DARK_AQUA + "none"));
                                    }
                                }
                            }
                            if (arenadata.contains("pg.lobbies." + s) && arenadata.getLocation("pg.lobbies." + s + ".sign") != null) {
                                Location loc = arenadata.getLocation("pg.lobbies." + s + ".sign");
                                assert loc != null;
                                BlockState b = loc.getBlock().getState();
                                Sign sign = (Sign) b;
                                sign.line(0, Component.text(s));
                                if (lobbyStates.get(s) == GameStates.WAITING || lobbyStates.get(s) == GameStates.PREPARING) {
                                    sign.line(1, Component.text(ChatColor.GREEN + lobbyStates.get(s).toString()));
                                } else {
                                    sign.line(1, Component.text(ChatColor.RED + lobbyStates.get(s).toString()));
                                }
                                sign.line(2, Component.text(ChatColor.AQUA + "Voting"));
                                sign.line(3, Component.text(ChatColor.GRAY + "[" + lobbyAmount.get(s).toString() + "/" + lobbymaxPlayers.get(s) + "]"));
                                sign.update();
                                infoLobby.replace(s, "" + sign.line(1) + ChatColor.WHITE + ", " + sign.line(2) + ChatColor.WHITE + ", " + sign.line(3));
                            }
                            resetLobby.replace(s, reset);
                            countdownLobby.replace(s, getConfig().getInt("pg.countdown"));
                            lobbyroundTimeSeconds.put(s, roundTimeSecondsNumber);
                            lobbyVote.replace(s, null);
                            lobbyVotedarena.replace(s, null);
                            playerLobby.putAll(specLobby);
                            for (Player all : playerLobby.keySet()) {
                                if (playerLobby.get(all).equals(s)) {
                                    Location loc = (Location) arenadata.get("pg.lobbies." + s + ".coords");
                                    assert loc != null;
                                    all.teleport(loc);
                                    all.setGameMode(GameMode.ADVENTURE);
                                    PlayerInventory inv = all.getInventory();
                                    inv.clear();
                                    inv.setHelmet(null);
                                    inv.setChestplate(null);
                                    inv.setLeggings(null);
                                    inv.setBoots(null);
                                    all.setHealth(20);
                                    all.setFoodLevel(20);
                                    clearEffects(all);
                                    all.setFireTicks(0);
                                    if (lobbyActivateTeams.get(s)) {
                                        ItemStack teamselector = new ItemStack(Material.CLOCK);
                                        ItemMeta teamselectormeta = teamselector.getItemMeta();
                                        assert teamselectormeta != null;
                                        teamselectormeta.displayName(Component.text(ChatColor.DARK_AQUA + chatmessages.get(43)));
                                        teamselector.setItemMeta(teamselectormeta);
                                        inv.setItem(4, teamselector);
                                    }
                                    if (lobbyActivateKits.get(s)) {
                                        ItemStack kitselector = new ItemStack(Material.ENDER_CHEST);
                                        ItemMeta kitselectormeta = kitselector.getItemMeta();
                                        assert kitselectormeta != null;
                                        kitselectormeta.displayName(Component.text(ChatColor.DARK_AQUA + chatmessages.get(62)));
                                        kitselector.setItemMeta(kitselectormeta);
                                        inv.setItem(2, kitselector);
                                    }
                                    if (!lobbySingleArena.get(s)) {
                                        ItemStack votepaper = new ItemStack(Material.PAPER);
                                        ItemMeta votepapaermeta = votepaper.getItemMeta();
                                        assert votepapaermeta != null;
                                        votepapaermeta.displayName(Component.text(ChatColor.DARK_AQUA + chatmessages.get(14)));
                                        votepaper.setItemMeta(votepapaermeta);
                                        inv.setItem(0, votepaper);
                                    }
                                    if (!startOnJoin) {
                                        ItemStack leave = new ItemStack(Material.MAGMA_CREAM);
                                        ItemMeta leavemeta = leave.getItemMeta();
                                        assert leavemeta != null;
                                        leavemeta.displayName(Component.text(ChatColor.DARK_AQUA + chatmessages.get(80)));
                                        leave.setItemMeta(leavemeta);
                                        inv.setItem(8, leave);
                                    }
                                    ItemStack stats = new ItemStack(Material.EMERALD);
                                    ItemMeta statsmeta = stats.getItemMeta();
                                    assert statsmeta != null;
                                    statsmeta.displayName(Component.text(ChatColor.DARK_AQUA + chatmessages.get(56)));
                                    stats.setItemMeta(statsmeta);
                                    inv.setItem(6, stats);
                                }
                            }
                            for (Player all : specLobby.keySet()) {
                                if (specLobby.get(all).equals(s)) {
                                    specLobby.remove(all);
                                }
                            }
                            for (Location all : lobbychests.keySet()) {
                                if (lobbychests.get(all).contains(s)) {
                                    lobbychests.remove(all);
                                    lobbychestsdata.remove(all);
                                }
                            }
                            for (Player all : playerLobby.keySet()) {
                                if (playerLobby.get(all).equals(s)) {
                                    lobbyVoted.remove(all);
                                }
                            }
                            lobbyVoteallowed.replace(s, false);
                            lobbyForcearena.replace(s, false);
                            for (Player all : playerLobby.keySet()) {
                                if (playerLobby.get(all).equals(s)) {
                                    lobbyTeamed.remove(all, s);
                                    lobbyteamplayernames.get(s).remove(all);
                                }
                            }
                            lobbyTeamallowed.replace(s, false);
                            for (Player all : playerLobby.keySet()) {
                                if (playerLobby.get(all).equals(s)) {
                                    lobbyVoted.remove(all);
                                }
                            }
                            lobbyKitallowed.replace(s, false);
                            for (Player all : playerLobby.keySet()) {
                                if (playerLobby.get(all).equals(s)) {
                                    richkidPlayers.remove(all);
                                    lobbyKited.remove(all);
                                }
                            }
                            lobbyCheckArenas.replace(s, false);
                            lobbySingleArena.replace(s, false);
                            lobbyDeathmatch.replace(s, false);
                            Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(arenadata.getString("pg.lobbies." + s + ".world")))).setPVP(false);
                            if (changeGamerules) {
                                setGameRules(arenadata.getString("pg.lobbies." + s + ".world"));
                                Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(arenadata.getString("pg.lobbies." + s + ".world")))).setDifficulty(Difficulty.PEACEFUL);
                                Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(arenadata.getString("pg.lobbies." + s + ".world")))).setGameRule(GameRule.FALL_DAMAGE, false);
                            }
                            if (!lobbyVoteallowed.get(s)) {
                                lobbyVoteallowed.replace(s, true);
                                HashMap<String, Integer> temp = new HashMap<>();
                                temp.put(chatmessages.get(42), 0);
                                for (int world = 1; world < 27; world++) {
                                    if (arenadata.contains("pg.lobbies." + s + "." + world + ".name")) {
                                        temp.put(arenadata.getString("pg.lobbies." + s + "." + world + ".name"), 0);
                                    }
                                }
                                lobbyvotes.replace(s, temp);
                            }
                            if (!lobbyTeamallowed.get(s)) {
                                lobbyTeamallowed.replace(s, true);
                                HashMap<Integer, Integer> temp = new HashMap<>();
                                for (int max = 1; max <= lobbyteamAmount.get(s); max++) {
                                    temp.put(max, 0);
                                }
                                lobbyteams.replace(s, temp);
                            }
                            if (!lobbyKitallowed.get(s)) {
                                lobbyKitallowed.replace(s, true);
                                kitplayers.put(chatmessages.get(42), 0);
                                for (String all : kits) {
                                    kitplayers.put(all, 0);
                                }
                            }
                            for (Entry<Location, Block> entry : lobbyLiquidPlaced.get(s).entrySet()) {
                                Location loc = entry.getKey();
                                loc.getBlock().setType(Material.AIR);
                            }
                            for (Entry<Location, Material> entry : lobbyPlacedBlocks.get(s).entrySet()) {
                                Location loc = entry.getKey();
                                loc.getBlock().setType(Material.AIR);
                            }
                            for (Entry<Location, Material> entry : lobbyBreakedBlocks.get(s).entrySet()) {
                                Location loc = entry.getKey();
                                Material mat = entry.getValue();
                                loc.getBlock().setType(mat);
                            }
                            for (Entry<Location, BlockData> entry : lobbyWaterBlocks.get(s).entrySet()) {
                                Location loc = entry.getKey();
                                BlockData data = entry.getValue();
                                loc.getBlock().setBlockData(data);
                            }
                            for (int world = 1; world < 27; world++) {
                                if (arenadata.contains("pg.lobbies." + s + "." + world)) {
                                    String ename = arenadata.getString("pg.lobbies." + s + "." + world + ".world");
                                    assert ename != null;
                                    World worldName = getServer().getWorld(ename);
                                    assert worldName != null;
                                    List<Entity> entList = worldName.getEntities();
                                    for (Entity current : entList) {
                                        if (current instanceof Player) {
                                            break;
                                        } else {
                                            current.remove();
                                        }
                                    }
                                }
                            }
                            lobbyStates.replace(s, GameStates.WAITING);
                        }
                        default -> {
                            Bukkit.getScheduler().cancelTask(tick);
                            Bukkit.shutdown();
                        }
                    }
                }
            }, 0, 20);
        }
    }

    public void voteResultsLobby(String s) {
        try {
            int max = 0;
            for (int i : lobbyvotes.get(s).values()) {
                if (i > max) {
                    max = i;
                }
            }
            String winner = null;
            for (String all : lobbyvotes.get(s).keySet()) {
                if (lobbyvotes.get(s).get(all) == max) {
                    winner = all;
                }
            }
            ArrayList<Integer> arenaNumber = new ArrayList<>();
            for (int ii = 0; ii < 26; ii++) {
                if (arenadata.contains("pg.lobbies." + s + "." + ii)) {
                    arenaNumber.add(ii);
                }
            }
            Random generator = new Random();
            int rndIndex = generator.nextInt(arenaNumber.size());
            int rndArena = arenaNumber.get(rndIndex);
            if (!lobbyForcearena.get(s)) {
                boolean randomArena = false;
                int i = 1;
                boolean votedArena = false;
                while (!votedArena) {
                    assert winner != null;
                    if (winner.equals(chatmessages.get(42))) {
                        String arenaName = null;
                        while (arenaName == null) {
                            winner = Integer.toString(rndArena);
                            arenaName = arenadata.getString("pg.lobbies." + s + "." + winner + ".name");
                            if (arenaName != null) {
                                for (Player all : playerLobby.keySet()) {
                                    if (playerLobby.get(all).equals(s)) {
                                        all.sendMessage(prefix + ChatColor.AQUA + arenadata.get("pg.lobbies." + s + "." + winner + ".name") + ChatColor.GREEN + " " + chatmessages.get(7));
                                    }
                                }
                            }
                            randomArena = true;
                            votedArena = true;
                        }
                    }
                    if (!randomArena) {
                        if (winner.equals(arenadata.getString("pg.lobbies." + s + "." + i + ".name"))) {
                            winner = Integer.toString(i);
                            for (Player all : playerLobby.keySet()) {
                                if (playerLobby.get(all).equals(s)) {
                                    all.sendMessage(prefix + ChatColor.AQUA + arenadata.get("pg.lobbies." + s + "." + winner + ".name") + ChatColor.GREEN + " " + chatmessages.get(7));
                                }
                            }
                            votedArena = true;
                        } else {
                            i++;
                        }
                    }
                }
            } else if (lobbyForcearena.get(s) || lobbyForcearena.get(s)) {
                winner = lobbyVote.get(s);
                for (Player all : playerLobby.keySet()) {
                    if (playerLobby.get(all).equals(s)) {
                        all.sendMessage(prefix + ChatColor.AQUA + lobbyVotedarena.get(s) + ChatColor.GREEN + " " + chatmessages.get(7));
                    }
                }
            } else if (!lobbyForcearena.get(s)) {
                String arenaName = null;
                while (arenaName == null) {
                    winner = Integer.toString(rndArena);
                    arenaName = arenadata.getString("pg.lobbies." + s + "." + winner + ".name");
                    if (arenaName != null) {
                        for (Player all : playerLobby.keySet()) {
                            if (playerLobby.get(all).equals(s)) {
                                all.sendMessage(prefix + ChatColor.AQUA + arenadata.get("pg.lobbies." + s + "." + winner + ".name") + ChatColor.GREEN + " " + chatmessages.get(7));
                            }
                        }
                    }
                }
            }
            lobbyVote.replace(s, winner);
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(90) + " Lobby: " + s);
            for (Player all : playerLobby.keySet()) {
                if (playerLobby.get(all).equals(s)) {
                    all.sendMessage(prefix + ChatColor.RED + chatmessages.get(90));
                    all.sendMessage(prefix + ChatColor.RED + chatmessages.get(86));
                    onLeaveLobby(all, s);
                }
            }
        }
    }

    public void teleportAndStartLobby(String s) {
        try {
            for (Player all : playerLobby.keySet()) {
                if (playerLobby.get(all).equals(s)) {
                    int maxteamplayers = teamSize;
                    boolean teamfound = false;
                    if (lobbyActivateTeams.get(s)) {
                        if (!lobbyTeamed.containsKey(all)) {
                            while (!teamfound) {
                                Random rnd = new Random();
                                int rndTeam = rnd.nextInt(lobbyteams.get(s).size());
                                rndTeam++;
                                if (lobbyteams.get(s).get(rndTeam) < maxteamplayers && lobbyteams.get(s).get(rndTeam) >= 0 && lobbyteams.get(s).get(rndTeam) != null) {
                                    teamfound = true;
                                    int players = lobbyteams.get(s).get(rndTeam);
                                    players++;
                                    HashMap<Integer, Integer> temp = new HashMap<>();
                                    for (int max = 1; max <= lobbyteamAmount.get(s); max++) {
                                        int oldplayers = lobbyteams.get(s).get(max);
                                        temp.put(max, oldplayers);
                                    }
                                    temp.put(rndTeam, players);
                                    lobbyteams.replace(s, temp);
                                    all.sendMessage(prefix + "--------------" + chatmessages.get(43) + "--------------");
                                    all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(45) + ": " + ChatColor.LIGHT_PURPLE + rndTeam);
                                    all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(44) + ": " + ChatColor.AQUA + lobbyteams.get(s).get(rndTeam) + ChatColor.GRAY + "/" + ChatColor.AQUA + maxteamplayers);
                                    all.sendMessage(prefix + "--------------" + chatmessages.get(43) + "--------------");
                                    lobbyTeamed.put(all, s);
                                    lobbyteamplayernames.get(s).put(all, Integer.toString(rndTeam));
                                    if (activateScoreboard) {
                                        Objects.requireNonNull(all.getScoreboard().getTeam("team")).prefix(Component.text(ChatColor.DARK_AQUA + Integer.toString(rndTeam)));
                                    }
                                }
                            }
                        }
                    }
                    if (lobbyActivateKits.get(s)) {
                        if (!lobbyKited.containsKey(all)) {
                            Random rnd = new Random();
                            int rndKit = rnd.nextInt(activeKits);
                            all.sendMessage(prefix + "--------------" + chatmessages.get(62) + "--------------");
                            all.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(46) + ": " + ChatColor.LIGHT_PURPLE + kits.get(rndKit));
                            all.sendMessage(prefix + "--------------" + chatmessages.get(62) + "--------------");
                            lobbyKited.put(all, s);
                            kitplayernames.put(all, kits.get(rndKit));
                            if (activateScoreboard) {
                                Objects.requireNonNull(all.getScoreboard().getTeam("kit")).prefix(Component.text(ChatColor.DARK_AQUA + kits.get(rndKit)));
                            }
                            if (kits.get(rndKit).equals("Rich Kid")) {
                                richkidPlayers.add(all);
                            }
                        }
                    }
                }
            }
            if (lobbyActivateTeams.get(s)) {
                String teamname;
                for (int i = 1; i <= lobbyteamAmount.get(s); i++) {
                    teamname = Integer.toString(i);
                    if (lobbyteams.get(s).get(Integer.parseInt(teamname)) == 0) {
                        lobbyteams.get(s).remove(Integer.parseInt(teamname));
                    }
                }
            }
            int i = 1;
            for (Player all : playerLobby.keySet()) {
                if (playerLobby.get(all).equals(s)) {
                    String vote = lobbyVote.get(s);
                    Location loc = arenadata.getLocation("pg.lobbies." + s + "." + vote + ".spawns." + i);
                    assert loc != null;
                    all.teleport(loc);
                }
                i++;
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(88) + " Lobby: " + s);
            for (Player all : playerLobby.keySet()) {
                if (playerLobby.get(all).equals(s)) {
                    all.sendMessage(prefix + ChatColor.RED + chatmessages.get(88));
                    all.sendMessage(prefix + ChatColor.RED + chatmessages.get(86));
                }
            }
        }
    }

    public void teleportDeathmatchLobby(String s) {
        try {
            int i = 1;
            for (Player all : specLobby.keySet()) {
                if (specLobby.get(all).equals(s)) {
                    String vote = lobbyVote.get(s);
                    Location loc = arenadata.getLocation("pg.lobbies." + s + "." + vote + ".deathmatch." + 1);
                    assert loc != null;
                    all.teleport(loc);
                }
                i++;
            }
            i = 1;
            for (Player all : playerLobby.keySet()) {
                if (playerLobby.get(all).equals(s)) {
                    all.setGameMode(GameMode.ADVENTURE);
                    lobbyMove.replace(s, false);
                    String vote = lobbyVote.get(s);
                    Location loc = arenadata.getLocation("pg.lobbies." + s + "." + vote + ".deathmatch." + i);
                    assert loc != null;
                    all.teleport(loc);
                }
                i++;
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(89) + " Lobby: " + s);
            for (Player all : playerLobby.keySet()) {
                if (playerLobby.get(all).equals(s)) {
                    all.sendMessage(prefix + ChatColor.RED + chatmessages.get(89));
                    all.sendMessage(prefix + ChatColor.RED + chatmessages.get(86));
                }
            }
            for (Player all : specLobby.keySet()) {
                if (specLobby.get(all).equals(s)) {
                    all.sendMessage(prefix + ChatColor.RED + chatmessages.get(89));
                    all.sendMessage(prefix + ChatColor.RED + chatmessages.get(86));
                }
            }
        }
    }

    public void onJoinLobby(Player p, String s) {
        try {
            if (activateScoreboard) {
                Scoreboard temp = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
                temp.registerNewObjective("main-content", "dummy", Component.text(" " + prefix));
                Objects.requireNonNull(temp.getObjective("main-content")).setDisplaySlot(DisplaySlot.SIDEBAR);
                temp.registerNewTeam("team");
                temp.registerNewTeam("kit");
                temp.registerNewTeam("players");
                temp.registerNewTeam("timeScoreboard");
                temp.registerNewTeam("map");
                temp.registerNewTeam("kills");
                Objects.requireNonNull(Objects.requireNonNull(temp.getTeam("team"))).prefix(Component.text(ChatColor.DARK_AQUA + "none"));
                Objects.requireNonNull(Objects.requireNonNull(temp.getTeam("kit"))).prefix(Component.text(ChatColor.DARK_AQUA + "none"));
                Objects.requireNonNull(temp.getTeam("map")).addEntry(" ");
                Objects.requireNonNull(temp.getTeam("players")).addEntry("       ");
                Objects.requireNonNull(temp.getTeam("timeScoreboard")).addEntry("          ");
                Objects.requireNonNull(temp.getTeam("team")).addEntry("   ");
                Objects.requireNonNull(temp.getTeam("kit")).addEntry("     ");
                Objects.requireNonNull(temp.getTeam("kills")).addEntry("        ");
                info.put(p, temp);
                p.setScoreboard(temp);
            }
            joinChannel(p.getPlayer(), "Local");
            inv.put(p.getName(), p.getInventory().getContents());
            armor.put(p.getName(), p.getInventory().getArmorContents());
            lvl.put(p.getName(), p.getLevel());
            exp.put(p.getName(), p.getExp());
            loc.put(p.getName(), p.getLocation());
            gm.put(p.getName(), p.getGameMode());
            PlayerInventory inventory = p.getInventory();
            inventory.clear();
            inventory.setHelmet(null);
            inventory.setChestplate(null);
            inventory.setLeggings(null);
            inventory.setBoots(null);
            p.setHealth(20);
            p.setFoodLevel(20);
            p.setLevel(0);
            p.setExp(0);
            p.setGameMode(GameMode.ADVENTURE);
            clearEffects(p);
            p.setFireTicks(0);
            lobbyAmount.replace(s, lobbyAmount.get(s) + 1);
            if (lobbyJoinable.get(s) && lobbyAmount.get(s) <= lobbymaxPlayers.get(s)) {
                playerLobby.put(p, s);
                Location loc = (Location) arenadata.get("pg.lobbies." + s + ".coords");
                assert loc != null;
                p.teleport(loc);
                lobbyStates.replace(s, GameStates.WAITING);
                if (!lobbyTickstarted.get(s)) {
                    tickLobby(s);
                    lobbyTickstarted.replace(s, true);
                }
                if (lobbyActivateTeams.get(s)) {
                    ItemStack teamselector = new ItemStack(Material.CLOCK);
                    ItemMeta teamselectormeta = teamselector.getItemMeta();
                    assert teamselectormeta != null;
                    teamselectormeta.displayName(Component.text(ChatColor.DARK_AQUA + chatmessages.get(43)));
                    teamselector.setItemMeta(teamselectormeta);
                    p.getInventory().setItem(4, teamselector);
                }
                if (lobbyActivateKits.get(s)) {
                    ItemStack kitselector = new ItemStack(Material.ENDER_CHEST);
                    ItemMeta kitselectormeta = kitselector.getItemMeta();
                    assert kitselectormeta != null;
                    kitselectormeta.displayName(Component.text(ChatColor.DARK_AQUA + chatmessages.get(62)));
                    kitselector.setItemMeta(kitselectormeta);
                    p.getInventory().setItem(2, kitselector);
                }
                if (!lobbySingleArena.get(s)) {
                    ItemStack votepaper = new ItemStack(Material.PAPER);
                    ItemMeta votepapaermeta = votepaper.getItemMeta();
                    assert votepapaermeta != null;
                    votepapaermeta.displayName(Component.text(ChatColor.DARK_AQUA + chatmessages.get(14)));
                    votepaper.setItemMeta(votepapaermeta);
                    p.getInventory().setItem(0, votepaper);
                }
                if (!startOnJoin) {
                    ItemStack leave = new ItemStack(Material.MAGMA_CREAM);
                    ItemMeta leavemeta = leave.getItemMeta();
                    assert leavemeta != null;
                    leavemeta.displayName(Component.text(ChatColor.DARK_AQUA + chatmessages.get(80)));
                    leave.setItemMeta(leavemeta);
                    p.getInventory().setItem(8, leave);
                }
                ItemStack stats = new ItemStack(Material.EMERALD);
                ItemMeta statsmeta = stats.getItemMeta();
                assert statsmeta != null;
                statsmeta.displayName(Component.text(ChatColor.DARK_AQUA + chatmessages.get(56)));
                stats.setItemMeta(statsmeta);
                p.getInventory().setItem(6, stats);
                p.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(30) + " " + ChatColor.AQUA + s);
            }
            if (!lobbyJoinable.get(s) && lobbyAmount.get(s) <= lobbymaxPlayers.get(s) && joinStarted) {
                specLobby.put(p, s);
                String vote = lobbyVote.get(s);
                Location loc = arenadata.getLocation("pg.lobbies." + s + "." + vote + ".spawns." + 1);
                assert loc != null;
                p.teleport(loc);
                p.setGameMode(GameMode.SPECTATOR);
                p.setAllowFlight(true);
                p.setFlying(true);
                p.setLevel(0);
                p.setExp(0);
                p.setFireTicks(0);
                p.setHealth(20);
                p.setFoodLevel(20);
                p.setCanPickupItems(false);
                p.setCollidable(false);
                p.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(30) + " " + ChatColor.AQUA + s + " " + ChatColor.GREEN + chatmessages.get(17));
            }
            p.playSound(p.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(87) + " Lobby: " + s);
            p.sendMessage(prefix + ChatColor.RED + chatmessages.get(87));
            p.sendMessage(prefix + ChatColor.RED + chatmessages.get(86));
        }
    }

    public void onLeaveLobby(Player p, String s) {
        if (activateScoreboard) {
            Objects.requireNonNull(p.getScoreboard().getTeam("team")).prefix(Component.text(""));
            Objects.requireNonNull(p.getScoreboard().getTeam("kit")).prefix(Component.text(""));
            p.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());
        }
        joinChannel(p.getPlayer(), "Global");
        if (lobbyStates.get(s) == GameStates.INGAME && lobbyAmount.get(s) > 1 && playerLobby.containsKey(p) && reload) {
            addLosses(p.getUniqueId().toString(), 1);
        }
        p.getInventory().setContents(inv.get(p.getName()));
        p.getInventory().setArmorContents(armor.get(p.getName()));
        p.teleport(loc.get(p.getName()));
        p.setLevel(lvl.get(p.getName()));
        p.setExp(exp.get(p.getName()));
        p.setGameMode(gm.get(p.getName()));
        inv.remove(p.getName());
        armor.remove(p.getName());
        loc.remove(p.getName());
        lvl.remove(p.getName());
        exp.remove(p.getName());
        gm.remove(p.getName());
        specLobby.remove(p);
        playerLobby.remove(p);
        lobbyAmount.replace(s, lobbyAmount.get(s) - 1);
        if (lobbyAmount.get(s) == 0) {
            lobbyTeamed.remove(p, s);
        }
        if (lobbyAmount.get(s) != 0) {
            if (lobbyActivateKits.get(s)) {
                lobbyKited.remove(p);
            }
            if (lobbyActivateTeams.get(s)) {
                if (lobbyTeamed.containsKey(p)) {
                    String teamname = null;
                    for (int i = 1; i <= lobbyteamAmount.get(s); i++) {
                        if (lobbyteamplayernames.get(s).containsKey(p) && lobbyteamplayernames.get(s).containsValue(Integer.toString(i))) {
                            if (Objects.equals(lobbyteamplayernames.get(s).get(p), Integer.toString(i))) {
                                teamname = Integer.toString(i);
                            }
                        }
                    }
                    lobbyteamplayernames.get(s).remove(p, teamname);
                    assert teamname != null;
                    lobbyteams.get(s).replace(Integer.parseInt(teamname), lobbyteams.get(s).get(Integer.parseInt(teamname)) - 1);
                    if (lobbyStates.get(s) == GameStates.INGAME) {
                        if (lobbyteams.get(s).get(Integer.parseInt(teamname)) == 0) {
                            lobbyteams.get(s).remove(Integer.parseInt(teamname));
                        }
                    }
                    lobbyTeamed.remove(p, s);
                }
            }
            if (lobbyVoted.containsKey(p)) {
                String arenaName = null;
                for (int i = 1; i < 27; i++) {
                    if (lobbyvoteplayernames.get(s).containsKey(p) && lobbyvoteplayernames.get(s).containsValue(arenadata.getString("pg.lobbies." + s + "." + i + ".name"))) {
                        if (Objects.equals(lobbyvoteplayernames.get(s).get(p), arenadata.getString("pg.lobbies." + s + "." + i + ".name"))) {
                            arenaName = arenadata.getString("pg.lobbies." + s + "." + i + ".name");
                        }
                    }
                }
                lobbyvoteplayernames.get(s).remove(p, arenaName);
                lobbyvotes.get(s).replace(arenaName, lobbyvotes.get(s).get(arenaName) - 1);
                lobbyVoted.remove(p, s);
            }
        } else {
            lobbyStates.replace(s, GameStates.RESET);
        }
        p.sendMessage(prefix + ChatColor.GREEN + chatmessages.get(32) + " " + ChatColor.AQUA + s);
        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1, 1);
    }

    public void connect() {
        if (activateMysql) {
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", user, password);
                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.GREEN + chatmessages.get(36));
                mysql = true;
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(37) + ": " + e.getMessage());
                mysql = false;
            }
        } else {
            con = null;
            try {
                File dbFile = new File(getDataFolder(), "stats.db");
                String url = "jdbc:sqlite:" + dbFile.getPath();
                con = DriverManager.getConnection(url);
                st = con.createStatement();
                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.GREEN + chatmessages.get(36));
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(37) + ": " + e.getMessage());
            }
        }
    }

    public void close() {
        try {
            if (con != null) {
                con.close();
                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.GREEN + chatmessages.get(38));
            }
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(39) + ": " + e.getMessage());
        }
    }

    public void update(String qry) {
        if (activateMysql) {
            if (con != null) {
                try {
                    st = con.createStatement();
                    st.executeUpdate(qry);
                    st.close();
                } catch (SQLException e) {
                    connect();
                    Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(37) + ": " + e.getMessage());
                }
            }
        } else {
            try {
                st.execute(qry);
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(37) + ": " + e.getMessage());
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
                } catch (SQLException e) {
                    connect();
                    Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(37) + ": " + e.getMessage());
                }
                return rs;
            }
        } else {
            try {
                return st.executeQuery(qry);
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(37) + ": " + e.getMessage());
            }
        }
        return null;
    }

    public void ConnectMySQL() {
        if (con != null) {
            try {
                update("CREATE TABLE IF NOT EXISTS Stats(UUID varchar(64), ROUNDS int, WINS int, LOSSES int, KILLS int, DEATHS int, KD double);");
            } catch (Exception e) {
                update("CREATE TABLE IF NOT EXISTS Stats(UUID varchar(64), ROUNDS int, WINS int, LOSTS int, KILLS int, DEATHS int, KD double);");
            }
        }
    }

    public boolean playerExists(String uuid) {
        try {
            ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'");
            if (rs.next()) {
                return rs.getString("UUID") != null;
            }
            return false;
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(37) + ": " + e.getMessage());
        }
        return false;
    }

    public void createPlayer(String uuid) {
        if (!playerExists(uuid)) {
            try {
                update("INSERT INTO Stats(UUID, ROUNDS, WINS, LOSSES, KILLS, DEATHS, KD) VALUES ('" + uuid + "', '0', '0', '0', '0', '0', '0');");
            } catch (Exception e) {
                update("INSERT INTO Stats(UUID, ROUNDS, WINS, LOSTS, KILLS, DEATHS, KD) VALUES ('" + uuid + "', '0', '0', '0', '0', '0', '0');");
            }
        }
    }

    public int getKills(String uuid) {
        int i = 0;
        if (playerExists(uuid)) {
            try {
                ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'");
                if ((rs.next())) {
                    rs.getInt("KILLS");
                }
                i = rs.getInt("KILLS");
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(37) + ": " + e.getMessage());
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
            try {
                ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'");
                if ((rs.next())) {
                    rs.getInt("DEATHS");
                }
                i = rs.getInt("DEATHS");
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(37) + ": " + e.getMessage());
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
            try {
                ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'");
                if ((rs.next())) {
                    rs.getDouble("KD");
                }
                i = rs.getDouble("KD");
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(37) + ": " + e.getMessage());
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
            try {
                ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'");
                if ((rs.next())) {
                    rs.getInt("WINS");
                }
                i = rs.getInt("WINS");
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(37) + ": " + e.getMessage());
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
            try {
                ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'");
                if ((rs.next())) {
                    try {
                        rs.getInt("LOSSES");
                    } catch (Exception e) {
                        rs.getInt("LOSTS");
                    }
                }
                try {
                    i = rs.getInt("LOSSES");
                } catch (Exception e) {
                    i = rs.getInt("LOSTS");
                }
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(37) + ": " + e.getMessage());
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
            try {
                ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'");
                if ((rs.next())) {
                    rs.getInt("ROUNDS");
                }
                i = rs.getInt("ROUNDS");
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + chatmessages.get(37) + ": " + e.getMessage());
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
            } catch (Exception e) {
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

    public GameStates getGamestate() {
        return gamestate;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public int getPlayerAmount() {
        return playerAmount;
    }

    public int getTeamAmount() {
        return teamAmount;
    }

    public int getCountdown() {
        return countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public void setReload(boolean reload) {
        this.reload = reload;
    }

    public int getActivePotions() {
        return activePotions;
    }

    public int getActiveKits() {
        return activeKits;
    }

    public int getKillReward() {
        return killReward;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public void setForcearena(boolean forcearena) {
        this.forcearena = forcearena;
    }

    public void setVotedArena(String votedArena) {
        this.votedArena = votedArena;
    }

    public boolean isEnableRewards() {
        return enableRewards;
    }

    public boolean isStartOnJoin() {
        return startOnJoin;
    }

    public boolean isActivateTeams() {
        return activateTeams;
    }

    public boolean isActivateShop() {
        return activateShop;
    }

    public boolean isActivateAirdrops() {
        return activateAirdrops;
    }

    public boolean isLobbySystem() {
        return lobbySystem;
    }

    public boolean isGameServer() {
        return gameServer;
    }

    public boolean isAllowOutsideChat() {
        return allowOutsideChat;
    }

    public boolean isActivateScoreboard() {
        return activateScoreboard;
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public boolean isMove() {
        return move;
    }

    public boolean isPause() {
        return pause;
    }

    public void changePause() {
        pause = !pause;
    }

    public boolean isBuild() {
        return build;
    }

    public void changeBuild() {
        build = !build;
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
}
