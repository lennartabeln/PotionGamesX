package com.asga.potiongames.main;

import com.asga.potiongames.commands.Commands;
import com.asga.potiongames.events.Events;
import com.asga.potiongames.gamestates.GameStates;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;

public class PotionGames extends JavaPlugin {
    private final ItemStack coin = new ItemStack(Material.GOLD_NUGGET);
    private final ItemStack bottle = new ItemStack(Material.GLASS_BOTTLE);
    public String prefixNoColor = "[PotionGames]";
    public String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_PURPLE + "Potion" + ChatColor.GOLD + "Games" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
    public ArrayList<Player> pgPlayers = new ArrayList<>();
    public ArrayList<Player> specPlayers = new ArrayList<>();
    public ArrayList<Player> richkidPlayers = new ArrayList<>();
    public ArrayList<String> arenas = new ArrayList<>();
    public ArrayList<String> voted = new ArrayList<>();
    public ArrayList<String> teams = new ArrayList<>();
    public ArrayList<String> teamed = new ArrayList<>();
    public ArrayList<String> kits = new ArrayList<>();
    public ArrayList<String> kited = new ArrayList<>();
    public ArrayList<String> chat = new ArrayList<>();
    public ArrayList<String> shop = new ArrayList<>();
    public ArrayList<PotionEffect> shoppotion = new ArrayList<>();
    public ArrayList<ItemStack> shoppotiontype = new ArrayList<>();
    public ArrayList<String> shopkit = new ArrayList<>();
    public ArrayList<Integer> shopcost = new ArrayList<>();
    public ArrayList<Integer> shopsale = new ArrayList<>();
    public ArrayList<Location> rankhead = new ArrayList<>();
    public ArrayList<Location> ranksign = new ArrayList<>();
    public ArrayList<ItemStack> food1 = new ArrayList<>();
    public ArrayList<ItemStack> food2 = new ArrayList<>();
    public ArrayList<ItemStack> armour1 = new ArrayList<>();
    public ArrayList<ItemStack> armour2 = new ArrayList<>();
    public ArrayList<ItemStack> armour3 = new ArrayList<>();
    public ArrayList<ItemStack> armour4 = new ArrayList<>();
    public ArrayList<ItemStack> armour5 = new ArrayList<>();
    public ArrayList<ItemStack> weapons1 = new ArrayList<>();
    public ArrayList<ItemStack> weapons2 = new ArrayList<>();
    public ArrayList<PotionEffect> potions = new ArrayList<>();
    public HashMap<Integer, String> rank = new HashMap<>();
    public HashMap<String, Integer> votes = new HashMap<>();
    public HashMap<String, Player> voteplayernames = new HashMap<>();
    public HashMap<String, Integer> teamplayers = new HashMap<>();
    public HashMap<String, Player> teamplayernames = new HashMap<>();
    public HashMap<String, Integer> kitplayers = new HashMap<>();
    public HashMap<String, Player> kitplayernames = new HashMap<>();
    public HashMap<Location, Inventory> chests = new HashMap<>();
    public HashMap<Location, String> lobbychests = new HashMap<>();
    public HashMap<Location, Inventory> lobbychestsdata = new HashMap<>();
    public HashMap<Location, Material> placedBlocks = new HashMap<>();
    public HashMap<Location, Material> breakedBlocks = new HashMap<>();
    public HashMap<Location, BlockData> waterBlocks = new HashMap<>();
    public HashMap<Location, Block> liquidPlaced = new HashMap<>();
    public HashMap<String, ItemStack[]> inv = new HashMap<>();
    public HashMap<String, ItemStack[]> armor = new HashMap<>();
    public HashMap<String, Integer> lvl = new HashMap<>();
    public HashMap<String, Float> exp = new HashMap<>();
    public HashMap<String, Location> loc = new HashMap<>();
    public HashMap<String, GameMode> gm = new HashMap<>();
    public HashMap<String, ArrayList<Player>> channels = new HashMap<>();
    public HashMap<Player, String> playerChannel = new HashMap<>();
    public HashMap<Player, String> playerLobby = new HashMap<>();
    public HashMap<Player, String> specLobby = new HashMap<>();
    public HashMap<String, Integer> countdownLobby = new HashMap<>();
    public HashMap<String, Integer> resetLobby = new HashMap<>();
    public HashMap<String, Integer> lobbyAmount = new HashMap<>();
    public HashMap<String, GameStates> lobbyStates = new HashMap<>();
    public HashMap<String, Boolean> lobbyMove = new HashMap<>();
    public HashMap<String, Boolean> lobbyJoinable = new HashMap<>();
    public HashMap<String, Boolean> lobbyForcearena = new HashMap<>();
    public HashMap<String, Boolean> lobbyVoteallowed = new HashMap<>();
    public HashMap<String, Boolean> lobbyTeamallowed = new HashMap<>();
    public HashMap<String, Boolean> lobbyKitallowed = new HashMap<>();
    public HashMap<String, Boolean> lobbyTickstarted = new HashMap<>();
    public HashMap<String, String> lobbyVoted = new HashMap<>();
    public HashMap<String, Integer> lobbyteamplayers = new HashMap<>();
    public HashMap<String, HashMap<String, Integer>> lobbyteams = new HashMap<>();
    public HashMap<String, Player> lobbyteamplayernamesdata = new HashMap<>();
    public HashMap<String, HashMap<String, Player>> lobbyteamplayernames = new HashMap<>();
    public HashMap<String, Integer> lobbyvoteplayers = new HashMap<>();
    public HashMap<String, HashMap<String, Integer>> lobbyvotes = new HashMap<>();
    public HashMap<String, Player> lobbyvoteplayernamesdata = new HashMap<>();
    public HashMap<String, HashMap<String, Player>> lobbyvoteplayernames = new HashMap<>();
    private int tick;
    private int countdown = 60;
    private int reset = 10;
    private int teamSize = 2;
    private int maxPlayers = 24;
    private int minPlayers = maxPlayers / 2;
    private int playerAmount = 0;
    private int teamAmount = maxPlayers / teamSize;
    private int activePotions = 19;
    private int activeKits = 6;
    public File shopdatafile = new File(getDataFolder() + File.separator + "shopdata.yml");
    public File kitdatafile = new File(getDataFolder() + File.separator + "kitdata.yml");
    public File messagesfile = new File(getDataFolder() + File.separator + "messages.yml");
    public File arenadatafile = new File(getDataFolder() + File.separator + "arenadata.yml");
    public File chestdatafile = new File(getDataFolder() + File.separator + "chestdata.yml");
    private String language = "en_US";
    private String vote;
    private String votedArena;
    private String host = "localhost";
    private String port = "3306";
    private String database = "potiongames";
    private String user = "root";
    private String password;
    private GameStates gamestate;
    private boolean joinable = true;
    private boolean pause = false;
    private boolean build = false;
    private boolean move = true;
    private boolean voteallowed = false;
    private boolean teamallowed = false;
    private boolean kitallowed = false;
    private boolean forcearena = false;
    private boolean startOnJoin = false;
    private boolean activateTeams = false;
    private boolean activateKits = false;
    private boolean activateShop = false;
    private boolean tickStarted = false;
    private boolean activateMySQL = false;
    private boolean arenaSystem = false;
    private Connection con;
    private Statement st;

    public Thread checkUpdates = new Thread(() -> {
        String latest = "";
        getLogger().info("Checking for updates...");
        try {
            URL url = new URL("https://raw.githubusercontent.com/andersspielen/PotionGamesIssues/master/version.txt");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(url.openStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(inputLine);
                stringBuilder.append(System.lineSeparator());
            }
            bufferedReader.close();
            latest = stringBuilder.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean upToDate = getDescription().getVersion().equals(latest);
        if (upToDate) {
            getLogger().info("Plugin is up to date! (" + getDescription().getVersion() + ")");
        } else {
            getLogger().warning("There is a newer version available: " + latest + ", you're on: " + getDescription().getVersion() + " - Download it here: https://github.com/andersspielen/PotionGamesIssues/releases/latest");
        }
    });

    public static void spawnFireworks(Location loc, int amount) {
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

    public void connect() {
        if (activateMySQL) {
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", user, password);
                System.out.println(prefixNoColor + " " + chat.get(36));
            } catch (SQLException e) {
                System.out.println(prefixNoColor + " " + chat.get(37) + ": " + e.getMessage());
            }
        } else {
            con = null;
            try {
                File dbFile = new File(getDataFolder(), "stats.db");
                String url = "jdbc:sqlite:" + dbFile.getPath();
                con = DriverManager.getConnection(url);
                st = con.createStatement();
                System.out.println(prefixNoColor + " " + chat.get(36));
            } catch (SQLException e) {
                System.out.println(prefixNoColor + " " + chat.get(37) + ": " + e.getMessage());
            }
        }
    }

    public void close() {
        try {
            if (con != null) {
                con.close();
                System.out.println(prefixNoColor + " " + chat.get(38));
            }
        } catch (SQLException e) {
            System.out.println(prefixNoColor + " " + chat.get(39) + ": " + e.getMessage());
        }
    }

    public void update(String qry) {
        if (activateMySQL) {
            try {
                st = con.createStatement();
                st.executeUpdate(qry);
                st.close();
            } catch (SQLException e) {
                connect();
                System.out.println(prefixNoColor + " " + chat.get(37) + ": " + e.getMessage());
            }
        } else {
            try {
                st.execute(qry);
            } catch (SQLException e) {
                System.out.println(prefixNoColor + " " + chat.get(37) + ": " + e.getMessage());
            }
        }
    }

    public ResultSet query(String qry) {
        if (activateMySQL) {
            ResultSet rs = null;
            try {
                st = con.createStatement();
                rs = st.executeQuery(qry);
            } catch (SQLException e) {
                connect();
                System.out.println(prefixNoColor + " " + chat.get(37) + ": " + e.getMessage());
            }
            return rs;
        } else {
            try {
                return st.executeQuery(qry);
            } catch (SQLException e) {
                System.out.println(prefixNoColor + " " + chat.get(37) + ": " + e.getMessage());
            }
            return null;
        }
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

    @Override
    public void onEnable() {
        chat.add("Waiting for players!");
        chat.add("The game starts in");
        chat.add("Player-Finder");
        chat.add("The game starts now!");
        chat.add("has won the game!");
        chat.add("Teleporting to lobby in");
        chat.add("Teleporting to lobby now!");
        chat.add("will be played!");
        chat.add("Dead");
        chat.add("was killed by");
        chat.add("died");
        chat.add("I'm on fire!");
        chat.add("Blocks away from next player");
        chat.add("No player found!");
        chat.add("Arena-Selector");
        chat.add("Votes");
        chat.add("You have voted for");
        chat.add("You have already voted!");
        chat.add("could not be teleported to the lobby!");
        chat.add("The game has already started!");
        chat.add("The game has been started!");
        chat.add("Not enough players to start the game!");
        chat.add("Pause");
        chat.add("Build");
        chat.add("Lobby successfully set!");
        chat.add("Server stopped!");
        chat.add("has been forced as arena!");
        chat.add("is not an arena!");
        chat.add("successfully removed! (Arena)");
        chat.add("successfully set! (Arena)");
        chat.add("successfully set! (Spawn)");
        chat.add("is not a valid spawn!");
        chat.add("successfully removed! (Spawn)");
        chat.add("Place");
        chat.add("Head successfully set!");
        chat.add("Sign successfully set!");
        chat.add("Connection to database established!");
        chat.add("Connection to database failed! Error");
        chat.add("Connection to database closed!");
        chat.add("Failed to close connection to database! Error");
        chat.add("started successfully!");
        chat.add("stopped successfully!");
        chat.add("Random");
        chat.add("Team-Selector");
        chat.add("Player");
        chat.add("You are now in team");
        chat.add("You now have the kit");
        chat.add("This team is already full!");
        chat.add("You are already in a team!");
        chat.add("Shop");
        chat.add("Duration");
        chat.add("Price");
        chat.add("Coins");
        chat.add("You not have enough Coins!");
        chat.add("You not have an empty bottle!");
        chat.add("Coin");
        chat.add("Stats");
        chat.add("Won");
        chat.add("Lost");
        chat.add("Kills");
        chat.add("Deaths");
        chat.add("K/D");
        chat.add("Kit Selector");
        chat.add("You already have a kit!");
        chat.add("Commands");
        chat.add("Rounds");
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
        FileConfiguration messages = YamlConfiguration.loadConfiguration(messagesfile);
        FileConfiguration shopdata = YamlConfiguration.loadConfiguration(shopdatafile);
        FileConfiguration arenadata = YamlConfiguration.loadConfiguration(arenadatafile);
        FileConfiguration kitdata = YamlConfiguration.loadConfiguration(kitdatafile);
        if (getConfig().get("pg.activateMySQL") == null) {
            getConfig().addDefault("pg.activateMySQL", activateMySQL);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            activateMySQL = getConfig().getBoolean("pg.activateMySQL");
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
        if (getConfig().get("pg.arenaSystem") == null) {
            getConfig().addDefault("pg.arenaSystem", arenaSystem);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            arenaSystem = getConfig().getBoolean("pg.arenaSystem");
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
        if (messages.get("pg.messages." + language + ".prefixNoColor") == null) {
            messages.addDefault("pg.messages." + language + ".prefixNoColor", prefixNoColor);
            messages.options().copyDefaults(true);
        } else {
            prefixNoColor = messages.getString("pg.messages." + language + ".prefixNoColor");
        }
        int message = 1;
        for (int i = 0; i < chat.size(); i++) {
            if (messages.get("pg.messages." + language + "." + message) == null) {
                messages.addDefault("pg.messages." + language + "." + message, chat.get(message - 1));
                messages.options().copyDefaults(true);
            } else {
                String name = messages.getString("pg.messages." + language + "." + message);
                chat.set(message - 1, name);
            }
            message++;
        }
        try {
            messages.save(messagesfile);
        } catch (IOException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
        try {
            arenadata.save(arenadatafile);
        } catch (IOException e) {
            e.printStackTrace();
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
            kitplayers.put(chat.get(42), 0);
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
            e.printStackTrace();
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
        getServer().getConsoleSender().sendMessage(prefix + ChatColor.DARK_GREEN + chat.get(40));
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new Events(this), this);
        Objects.requireNonNull(this.getCommand("pg")).setExecutor(new Commands(this));
        int lobby = 1;
        while (arenadata.contains("pg.arenas." + lobby)) {
            String s = String.valueOf(lobby);
            lobbyStates.put(s, GameStates.WAITING);
            lobbyJoinable.put(s, true);
            lobbyForcearena.put(s, false);
            lobbyMove.put(s, true);
            lobbyVoteallowed.put(s, true);
            lobbyTeamallowed.put(s, true);
            lobbyKitallowed.put(s, true);
            lobbyAmount.put(s, 0);
            lobbyTickstarted.put(s, false);
            lobbyVoted.put(s, null);
            int team = 1;
            while (team <= teamAmount) {
                String tname = Integer.toString(team);
                lobbyteamplayers.put(tname, 0);
                lobbyteamplayernamesdata.put(tname, null);
                team++;
            }
            lobbyteams.put(s, lobbyteamplayers);
            lobbyteamplayernames.put(s, lobbyteamplayernamesdata);
            lobbyvoteplayers.put(chat.get(42), 0);
            lobbyvoteplayernames.put(chat.get(42), null);
            int arena = 1;
            while (arenadata.contains("pg.arenas." + arena)) {
                String aname = arenadata.getString("pg.arenas." + arena + ".name");
                lobbyvoteplayers.put(aname, 0);
                lobbyvoteplayernames.put(aname, null);
                arena++;
            }
            lobbyvotes.put(s, lobbyvoteplayers);
            lobbyvoteplayernames.put(s, lobbyvoteplayernamesdata);
            lobby++;
        }
        ItemMeta coinmeta = coin.getItemMeta();
        assert coinmeta != null;
        coinmeta.setDisplayName(ChatColor.DARK_AQUA + chat.get(55));
        coin.setItemMeta(coinmeta);
        checkUpdates.start();
    }

    @Override
    public void onDisable() {
        reset();
        close();
        getServer().getConsoleSender().sendMessage(prefix + ChatColor.DARK_RED + chat.get(41));
    }

    public void reset() {
        for (Player all : Bukkit.getOnlinePlayers()) {
            all.kickPlayer(prefix + ChatColor.RED + chat.get(25));
        }
    }

    public void chestData() {
        FileConfiguration chestdata = YamlConfiguration.loadConfiguration(chestdatafile);
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
        weapons1.add(new ItemStack(Material.WATER_BUCKET, 1));
        weapons1.add(new ItemStack(Material.LAVA_BUCKET, 1));
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
        try {
            chestdata.save(chestdatafile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void tick() {
        FileConfiguration arenadata = YamlConfiguration.loadConfiguration(arenadatafile);
        FileConfiguration kitdata = YamlConfiguration.loadConfiguration(kitdatafile);
        setCountdown(getConfig().getInt("pg.countdown"));
        tick = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            if (!isPause()) {
                int gamerule = 1;
                switch (gamestate) {
                    case WAITING:
                        specPlayers.clear();
                        setMove(true);
                        setJoinable(true);
                        if (getConfig().contains("pg.Lobby.world")) {
                            Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("pg.Lobby.world")))).setDifficulty(Difficulty.PEACEFUL);
                            Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("pg.Lobby.world")))).setPVP(false);
                        }
                        while (arenadata.contains("pg.arenas." + gamerule)) {
                            String name = arenadata.getString("pg.arenas." + gamerule + ".world");
                            setGameRules(name);
                            assert name != null;
                            Objects.requireNonNull(Bukkit.getWorld(name)).setGameRule(GameRule.FALL_DAMAGE, true);
                            gamerule++;
                        }
                        if (!voteallowed) {
                            voteallowed = true;
                            int arena = 1;
                            while (arenadata.contains("pg.arenas." + arena)) {
                                String name = arenadata.getString("pg.arenas." + arena + ".name");
                                arenas.add(name);
                                arena++;
                            }
                            votes.put(chat.get(42), 0);
                            for (String all : arenas) {
                                votes.put(all, 0);
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
                                teamplayers.put(chat.get(42), 0);
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
                        if (getConfig().contains("pg.RankWall.headp1") && getConfig().contains("pg.RankWall.headp2") && getConfig().contains("pg.RankWall.headp3") && getConfig().contains("pg.RankWall.signp1") && getConfig().contains("pg.RankWall.signp2") && getConfig().contains("pg.RankWall.signp3")) {
                            ResultSet rs = query("SELECT UUID FROM Stats ORDER BY WINS DESC LIMIT 3");
                            int ii = 0;
                            try {
                                while (rs.next()) {
                                    ii++;
                                    rank.put(ii, rs.getString("UUID"));
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            rankhead.add(getConfig().getLocation("pg.RankWall.headp1"));
                            rankhead.add(getConfig().getLocation("pg.RankWall.headp2"));
                            rankhead.add(getConfig().getLocation("pg.RankWall.headp3"));
                            ranksign.add(getConfig().getLocation("pg.RankWall.signp1"));
                            ranksign.add(getConfig().getLocation("pg.RankWall.signp2"));
                            ranksign.add(getConfig().getLocation("pg.RankWall.signp3"));
                            for (int iii = 0; iii < rank.size(); iii++) {
                                int id = iii + 1;
                                Skull s = (Skull) rankhead.get(iii).getBlock().getState();
                                OfflinePlayer name = Bukkit.getOfflinePlayer(UUID.fromString(rank.get(id)));
                                s.setOwningPlayer(name);
                                s.update();
                            }
                            for (int iii = 0; iii < rank.size(); iii++) {
                                int id = iii + 1;
                                BlockState b = ranksign.get(iii).getBlock().getState();
                                OfflinePlayer name = Bukkit.getOfflinePlayer(UUID.fromString(rank.get(id)));
                                Sign sign = (Sign) b;
                                sign.setLine(0, chat.get(33) + " #" + id);
                                sign.setLine(1, Objects.requireNonNull(name.getName()));
                                sign.setLine(2, "Wins: " + getWins(rank.get(id)));
                                sign.setLine(3, "K/D: " + getKD(rank.get(id)));
                                sign.update();
                            }
                        }
                        setJoinable(true);
                        if (getPlayerAmount() < minPlayers) {
                            for (Player all : pgPlayers) {
                                all.setLevel(0);
                                all.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                        TextComponent.fromLegacyText(prefix + chat.get(0) + " " + "[" + ChatColor.AQUA + getPlayerAmount() + ChatColor.GRAY + "/" + ChatColor.AQUA + minPlayers + ChatColor.GRAY + "]"));
                            }
                            setCountdown(getConfig().getInt("pg.countdown"));
                        } else {
                            setGamestate(GameStates.PREPARING);
                        }
                        break;
                    case PREPARING:
                        if (getPlayerAmount() >= minPlayers) {
                            for (Player all : pgPlayers) {
                                all.setLevel(countdown);
                            }
                            if (countdown == 10) {
                                for (Player all : pgPlayers) {
                                    all.setLevel(countdown);
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
                                setGamestate(GameStates.INGAME);
                                setMove(false);
                            }
                            countdown--;
                        } else {
                            setGamestate(GameStates.WAITING);
                        }
                        break;
                    case INGAME:
                        int setting = 1;
                        while (arenadata.contains("pg.arenas." + setting)) {
                            String name = arenadata.getString("pg.arenas." + setting + ".world");
                            assert name != null;
                            Objects.requireNonNull(getServer().getWorld(name)).setDifficulty(Difficulty.EASY);
                            Objects.requireNonNull(getServer().getWorld(name)).setPVP(true);
                            setting++;
                        }
                        setJoinable(false);
                        if (getPlayerAmount() != 0) {
                            if (countdown == 10) {
                                for (Player all : pgPlayers) {
                                    all.getInventory().clear();
                                    all.sendMessage(prefix + ChatColor.GREEN + chat.get(1) + " " + ChatColor.AQUA + countdown);
                                    ItemStack playercompass = new ItemStack(Material.COMPASS);
                                    ItemMeta playercompassmeta = playercompass.getItemMeta();
                                    assert playercompassmeta != null;
                                    playercompassmeta.setDisplayName(ChatColor.DARK_AQUA + chat.get(2));
                                    playercompass.setItemMeta(playercompassmeta);
                                    all.getInventory().setItem(8, playercompass);
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
                                    all.sendMessage(prefix + ChatColor.GREEN + chat.get(1) + " " + ChatColor.AQUA + countdown);
                                    all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                                }
                                countdown--;
                            } else if (countdown == 0) {
                                setMove(true);
                                for (Player all : pgPlayers) {
                                    all.sendMessage(prefix + ChatColor.GREEN + chat.get(3));
                                    all.playSound(all.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 1);
                                    all.setGameMode(GameMode.SURVIVAL);
                                    setCountdown(-1);
                                }
                            } else if (countdown == -1) {
                                if (activateTeams) {
                                    if (pgPlayers.size() == 1 || teams.size() == 1)
                                        setCountdown(-2);
                                } else {
                                    if (pgPlayers.size() == 1) {
                                        setCountdown(-2);
                                    }
                                }
                            } else if (countdown == -2) {
                                for (int i = 0; i < pgPlayers.size(); i++) {
                                    Player winner = pgPlayers.get(i);
                                    for (Player all : pgPlayers) {
                                        all.sendMessage(prefix + ChatColor.AQUA + winner.getName() + ChatColor.GREEN + " " + chat.get(4));
                                    }
                                    for (Player all : specPlayers) {
                                        all.sendMessage(prefix + ChatColor.AQUA + winner.getName() + ChatColor.GREEN + " " + chat.get(4));
                                    }
                                    spawnFireworks(winner.getLocation(), 1);
                                    addWins(winner.getUniqueId().toString(), 1);
                                    setCountdown(-3);
                                }
                            } else if (countdown == -3) {
                                if (getReset() == 10) {
                                    for (Player all : pgPlayers) {
                                        all.sendMessage(prefix + ChatColor.GREEN + chat.get(5) + " " + ChatColor.AQUA + getReset());
                                    }
                                    for (Player all : specPlayers) {
                                        all.sendMessage(prefix + ChatColor.GREEN + chat.get(5) + " " + ChatColor.AQUA + getReset());
                                    }
                                    reset--;
                                } else if (getReset() <= 9 && getReset() > 5) {
                                    reset--;
                                } else if (getReset() <= 5 && getReset() > 0) {
                                    for (Player all : pgPlayers) {
                                        all.sendMessage(prefix + ChatColor.GREEN + chat.get(5) + " " + ChatColor.AQUA + getReset());
                                    }
                                    for (Player all : specPlayers) {
                                        all.sendMessage(prefix + ChatColor.GREEN + chat.get(5) + " " + ChatColor.AQUA + getReset());
                                    }
                                    reset--;
                                } else if (getReset() == 0) {
                                    for (Player all : pgPlayers) {
                                        all.sendMessage(prefix + ChatColor.GREEN + chat.get(6));
                                    }
                                    for (Player all : specPlayers) {
                                        all.sendMessage(prefix + ChatColor.GREEN + chat.get(6));
                                    }
                                    setGamestate(GameStates.RESET);
                                }
                            }
                        } else {
                            setGamestate(GameStates.RESET);
                        }
                        break;
                    case RESET:
                        setMove(true);
                        setJoinable(true);
                        setReset(10);
                        setCountdown(getConfig().getInt("pg.countdown"));
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
                                teamselectormeta.setDisplayName(ChatColor.DARK_AQUA + chat.get(43));
                                teamselector.setItemMeta(teamselectormeta);
                                inv.setItem(4, teamselector);
                            }
                            if (activateKits) {
                                ItemStack kitselector = new ItemStack(Material.ENDER_CHEST);
                                ItemMeta kitselectormeta = kitselector.getItemMeta();
                                assert kitselectormeta != null;
                                kitselectormeta.setDisplayName(ChatColor.DARK_AQUA + chat.get(62));
                                kitselector.setItemMeta(kitselectormeta);
                                inv.setItem(0, kitselector);
                            }
                            ItemStack votepaper = new ItemStack(Material.PAPER);
                            ItemMeta votepapaermeta = votepaper.getItemMeta();
                            assert votepapaermeta != null;
                            votepapaermeta.setDisplayName(ChatColor.DARK_AQUA + chat.get(14));
                            votepaper.setItemMeta(votepapaermeta);
                            inv.setItem(8, votepaper);
                        }
                        specPlayers.clear();
                        chests.clear();
                        voted.clear();
                        voteallowed = false;
                        forcearena = false;
                        if (activateTeams)
                            teamed.clear();
                        teamallowed = false;
                        teamplayernames.clear();
                        kited.clear();
                        kitallowed = false;
                        kitplayernames.clear();
                        richkidPlayers.clear();
                        Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("pg.Lobby.world")))).setDifficulty(Difficulty.PEACEFUL);
                        Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("pg.Lobby.world")))).setPVP(false);
                        while (arenadata.contains("pg.arenas." + gamerule)) {
                            String name = arenadata.getString("pg.arenas." + gamerule + ".world");
                            setGameRules(name);
                            assert name != null;
                            Objects.requireNonNull(Bukkit.getWorld(name)).setGameRule(GameRule.FALL_DAMAGE, true);
                            gamerule++;
                        }
                        if (!voteallowed) {
                            voteallowed = true;
                            votes.put(chat.get(42), 0);
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
                            teamplayers.put(chat.get(42), 0);
                            for (String all : teams) {
                                teamplayers.put(all, 0);
                            }
                        }
                        if (!kitallowed) {
                            kitallowed = true;
                            kitplayers.put(chat.get(42), 0);
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
                        int worldName = 1;
                        while (arenadata.contains("pg.arenas." + worldName)) {
                            String name = arenadata.getString("pg.arenas." + worldName + ".world");
                            assert name != null;
                            World world = getServer().getWorld(name);
                            assert world != null;
                            List<Entity> entList = world.getEntities();
                            for (Entity current : entList) {
                                if (current != null) {
                                    current.remove();
                                }
                            }
                            worldName++;
                        }
                        setGamestate(GameStates.WAITING);
                        break;
                    default:
                        Bukkit.getScheduler().cancelTask(tick);
                        Bukkit.shutdown();
                        break;
                }
            }
        }, 0, 20);
    }

    public void clearEffects(Player all) {
        FileConfiguration chestdata = YamlConfiguration.loadConfiguration(chestdatafile);
        int chestitem = 1;
        while (chestdata.contains("pg.potions" + chestitem)) {
            PotionEffect effect = (PotionEffect) chestdata.get("pg.potions." + chestitem);
            assert effect != null;
            all.removePotionEffect(effect.getType());
            chestitem++;
        }
        if (all.hasPotionEffect(PotionEffectType.SPEED)) {
            all.removePotionEffect(PotionEffectType.SPEED);
        }
        if (all.hasPotionEffect(PotionEffectType.SLOW)) {
            all.removePotionEffect(PotionEffectType.SLOW);
        }
        if (all.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
            all.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
        }
        if (all.hasPotionEffect(PotionEffectType.HEAL)) {
            all.removePotionEffect(PotionEffectType.HEAL);
        }
        if (all.hasPotionEffect(PotionEffectType.HARM)) {
            all.removePotionEffect(PotionEffectType.HARM);
        }
        if (all.hasPotionEffect(PotionEffectType.JUMP)) {
            all.removePotionEffect(PotionEffectType.JUMP);
        }
        if (all.hasPotionEffect(PotionEffectType.CONFUSION)) {
            all.removePotionEffect(PotionEffectType.CONFUSION);
        }
        if (all.hasPotionEffect(PotionEffectType.REGENERATION)) {
            all.removePotionEffect(PotionEffectType.REGENERATION);
        }
        if (all.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            all.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        }
        if (all.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
            all.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
        }
        if (all.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            all.removePotionEffect(PotionEffectType.INVISIBILITY);
        }
        if (all.hasPotionEffect(PotionEffectType.BLINDNESS)) {
            all.removePotionEffect(PotionEffectType.BLINDNESS);
        }
        if (all.hasPotionEffect(PotionEffectType.HUNGER)) {
            all.removePotionEffect(PotionEffectType.HUNGER);
        }
        if (all.hasPotionEffect(PotionEffectType.WEAKNESS)) {
            all.removePotionEffect(PotionEffectType.WEAKNESS);
        }
        if (all.hasPotionEffect(PotionEffectType.POISON)) {
            all.removePotionEffect(PotionEffectType.POISON);
        }
        if (all.hasPotionEffect(PotionEffectType.WITHER)) {
            all.removePotionEffect(PotionEffectType.WITHER);
        }
        if (all.hasPotionEffect(PotionEffectType.ABSORPTION)) {
            all.removePotionEffect(PotionEffectType.ABSORPTION);
        }
        if (all.hasPotionEffect(PotionEffectType.GLOWING)) {
            all.removePotionEffect(PotionEffectType.GLOWING);
        }
        if (all.hasPotionEffect(PotionEffectType.HEALTH_BOOST)) {
            all.removePotionEffect(PotionEffectType.HEALTH_BOOST);
        }
        if (all.hasPotionEffect(PotionEffectType.DOLPHINS_GRACE)) {
            all.removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
        }
        if (all.hasPotionEffect(PotionEffectType.SATURATION)) {
            all.removePotionEffect(PotionEffectType.SATURATION);
        }
        if (all.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            all.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
        if (all.hasPotionEffect(PotionEffectType.WATER_BREATHING)) {
            all.removePotionEffect(PotionEffectType.WATER_BREATHING);
        }
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

    public void voteResults() {
        FileConfiguration arenadata = YamlConfiguration.loadConfiguration(arenadatafile);
        int max = 0;
        for (int i : votes.values()) {
            if (i > max) {
                max = i;
            }
        }
        String winner = "";
        for (String all : votes.keySet()) {
            if (votes.get(all) == max) {
                winner = all;
            }
        }
        if (!isForcearena() && !voted.isEmpty()) {
            boolean randomArena = false;
            int i = 1;
            boolean votedArena = false;
            while (!votedArena) {
                if (winner.equals(chat.get(42))) {
                    String arenaName = null;
                    while (arenaName == null) {
                        Random rnd = new Random();
                        int rndArena = rnd.nextInt(arenas.size() + 1);
                        winner = String.valueOf(rndArena);
                        arenaName = arenadata.getString("pg.arenas." + winner + ".name");
                        if (arenaName != null) {
                            for (Player all : pgPlayers) {
                                all.sendMessage(prefix + ChatColor.AQUA + arenadata.get("pg.arenas." + winner + ".name") + ChatColor.GREEN + " " + chat.get(7));
                            }
                        }
                        randomArena = true;
                        votedArena = true;
                    }
                }
                if (!randomArena) {
                    if (winner.equals(arenadata.getString("pg.arenas." + i + ".name"))) {
                        winner = String.valueOf(i);
                        for (Player all : pgPlayers) {
                            all.sendMessage(prefix + ChatColor.AQUA + arenadata.get("pg.arenas." + winner + ".name") + ChatColor.GREEN + " " + chat.get(7));
                        }
                        votedArena = true;
                    } else {
                        i++;
                    }
                }
            }
        } else if (isForcearena() && !voted.isEmpty() || isForcearena() && voted.isEmpty()) {
            winner = getVote();
            for (Player all : pgPlayers) {
                all.sendMessage(prefix + ChatColor.AQUA + getVotedArena() + ChatColor.GREEN + " " + chat.get(7));
            }
        } else if (voted.isEmpty() && !isForcearena()) {
            String arenaName = null;
            while (arenaName == null) {
                Random rnd = new Random();
                int rndArena = rnd.nextInt(arenas.size() + 1);
                winner = String.valueOf(rndArena);
                arenaName = arenadata.getString("pg.arenas." + winner + ".name");
                if (arenaName != null) {
                    for (Player all : pgPlayers) {
                        all.sendMessage(prefix + ChatColor.AQUA + arenadata.get("pg.arenas." + winner + ".name") + ChatColor.GREEN + " " + chat.get(7));
                    }
                }
            }
        }
        setVote(winner);
    }

    public void teleportAndStart() {
        FileConfiguration arenadata = YamlConfiguration.loadConfiguration(arenadatafile);
        for (Player all : pgPlayers) {
            int maxteamplayers = teamSize;
            boolean teamfound = false;
            if (activateTeams) {
                if (!teamed.contains(all.getName())) {
                    while (!teamfound) {
                        Random rnd = new Random();
                        int rndTeam = rnd.nextInt(teams.size() + 1);
                        if (teamplayers.get(Integer.toString(rndTeam)) < maxteamplayers && teamplayers.get(Integer.toString(rndTeam)) >= 0) {
                            teamfound = true;
                            int players = teamplayers.get(Integer.toString(rndTeam));
                            players++;
                            teamplayers.put(Integer.toString(rndTeam), players);
                            all.sendMessage(prefix + "--------------" + chat.get(43) + "--------------");
                            all.sendMessage(prefix + ChatColor.GREEN + chat.get(45) + ": " + ChatColor.LIGHT_PURPLE + rndTeam);
                            all.sendMessage(prefix + ChatColor.GREEN + chat.get(44) + ": " + ChatColor.AQUA + teamplayers.get(Integer.toString(rndTeam)) + ChatColor.GRAY + "/" + ChatColor.AQUA + maxteamplayers);
                            all.sendMessage(prefix + "--------------" + chat.get(43) + "--------------");
                            teamed.add(all.getName());
                            teamplayernames.put(Integer.toString(rndTeam), all);
                        }
                    }
                }
            }
            if (activateKits) {
                if (!kited.contains(all.getName())) {
                    Random rnd = new Random();
                    int rndKit = rnd.nextInt(activeKits);
                    all.sendMessage(prefix + "--------------" + chat.get(62) + "--------------");
                    all.sendMessage(prefix + ChatColor.GREEN + chat.get(46) + ": " + ChatColor.LIGHT_PURPLE + kits.get(rndKit));
                    all.sendMessage(prefix + "--------------" + chat.get(62) + "--------------");
                    kited.add(all.getName());
                    kitplayernames.put(kits.get(rndKit), all);
                    if (kits.get(rndKit).equals("Rich Kid")) {
                        richkidPlayers.add(all);
                    }
                }
            }
        }
        if (activateTeams) {
            String teamname;
            for (int i = 1; i <= teamAmount; i++) {
                teamname = String.valueOf(i);
                if (teamplayers.get(teamname) == 0) {
                    teams.remove(teamname);
                }
            }
        }
        for (int i = 1; i <= pgPlayers.size(); i++) {
            Player p = pgPlayers.get(i - 1);
            try {
                String vote = getVote();
                Location loc = arenadata.getLocation("pg.arenas." + vote + ".spawns." + i);
                assert loc != null;
                p.teleport(loc);
            } catch (Exception e) {
                for (Player all : pgPlayers) {
                    if (all.isOp()) {
                        all.sendMessage(prefix + p.getName() + ChatColor.RED + chat.get(8));
                    }
                }
            }
        }
    }

    public void onJoin(Player p) {
        FileConfiguration arenadata = YamlConfiguration.loadConfiguration(arenadatafile);
        joinChannel(p.getPlayer(), "Local");
        PlayerInventory inventory = p.getInventory();
        inv.put(p.getName(), p.getInventory().getContents());
        armor.put(p.getName(), p.getInventory().getArmorContents());
        lvl.put(p.getName(), p.getLevel());
        exp.put(p.getName(), p.getExp());
        loc.put(p.getName(), p.getLocation());
        gm.put(p.getName(), p.getGameMode());
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
        addPlayerAmount();
        if (joinable) {
            pgPlayers.add(p);
            try {
                Location loc = (Location) getConfig().get("pg.Lobby.coords");
                assert loc != null;
                p.teleport(loc);
                setGamestate(GameStates.WAITING);
                if (!tickStarted) {
                    tick();
                    tickStarted = true;
                }
            } catch (Exception ex) {
                for (Player all : pgPlayers) {
                    if (all.isOp()) {
                        all.sendMessage(prefix + p.getName() + ChatColor.RED + " " + chat.get(18));
                    }
                }
            }
            String name = getConfig().getString("pg.Lobby.world");
            setGameRules(name);
            assert name != null;
            Objects.requireNonNull(Bukkit.getWorld(name)).setGameRule(GameRule.FALL_DAMAGE, false);
            if (activateTeams) {
                ItemStack teamselector = new ItemStack(Material.CLOCK);
                ItemMeta teamselectormeta = teamselector.getItemMeta();
                assert teamselectormeta != null;
                teamselectormeta.setDisplayName(ChatColor.DARK_AQUA + chat.get(43));
                teamselector.setItemMeta(teamselectormeta);
                p.getInventory().setItem(4, teamselector);
            }
            if (activateKits) {
                ItemStack kitselector = new ItemStack(Material.ENDER_CHEST);
                ItemMeta kitselectormeta = kitselector.getItemMeta();
                assert kitselectormeta != null;
                kitselectormeta.setDisplayName(ChatColor.DARK_AQUA + chat.get(62));
                kitselector.setItemMeta(kitselectormeta);
                p.getInventory().setItem(0, kitselector);
            }
            ItemStack votepaper = new ItemStack(Material.PAPER);
            ItemMeta votepapaermeta = votepaper.getItemMeta();
            assert votepapaermeta != null;
            votepapaermeta.setDisplayName(ChatColor.DARK_AQUA + chat.get(14));
            votepaper.setItemMeta(votepapaermeta);
            p.getInventory().setItem(8, votepaper);
        }
        if (!joinable) {
            specPlayers.add(p);
            try {
                String vote = getVote();
                Location loc = arenadata.getLocation("pg.arenas." + vote + ".spawns." + 1);
                assert loc != null;
                p.teleport(loc);
            } catch (Exception e) {
                for (Player all : pgPlayers) {
                    if (all.isOp()) {
                        all.sendMessage(prefix + p.getName() + ChatColor.RED + chat.get(8));
                    }
                }
            }
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
        }
    }

    public void onLeave(Player p) {
        FileConfiguration arenadata = YamlConfiguration.loadConfiguration(arenadatafile);
        joinChannel(p.getPlayer(), "Global");
        if (getGamestate() == GameStates.INGAME && pgPlayers.size() > 1) {
            addLosts(p.getUniqueId().toString(), 1);
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
        setPlayerAmount(getPlayerAmount() - 1);
        if (activateTeams) {
            if (teamed.contains(p.getName())) {
                String teamname = "";
                for (int i = 1; i <= teamAmount; i++) {
                    if (teamplayernames.containsKey(Integer.toString(i)) && teamplayernames.containsValue(p)) {
                        teamname = String.valueOf(i);
                    }
                }
                teamplayernames.remove(teamname, p);
                int teamamount = teamplayers.get(teamname) - 1;
                teamplayers.put(teamname, teamamount);
                if (getGamestate() == GameStates.INGAME) {
                    if (teamplayers.get(teamname) == 0) {
                        teams.remove(teamname);
                    }
                }
                teamed.remove(p.getName());
            }
        }
        if (voted.contains(p.getName())) {
            String arenaname = "";
            for (int i = 0; i <= arenas.size(); i++) {
                if (arenadata.contains("pg.arenas." + i)) {
                    if (voteplayernames.containsKey(arenadata.getString("pg.arenas." + i + ".name")) && voteplayernames.containsValue(p)) {
                        arenaname = arenadata.getString("pg.arenas." + i + ".name");
                    }
                } else {
                    arenaname = chat.get(42);
                }
            }
            voteplayernames.remove(arenaname, p);
            votes.replace(arenaname, votes.get(arenaname) - 1);
            voted.remove(p.getName());
        }
    }

    public void joinLobby(Player p, String s) {
        FileConfiguration arenadata = YamlConfiguration.loadConfiguration(arenadatafile);
        joinChannel(p.getPlayer(), "Local");
        PlayerInventory inventory = p.getInventory();
        inv.put(p.getName(), p.getInventory().getContents());
        armor.put(p.getName(), p.getInventory().getArmorContents());
        lvl.put(p.getName(), p.getLevel());
        exp.put(p.getName(), p.getExp());
        loc.put(p.getName(), p.getLocation());
        gm.put(p.getName(), p.getGameMode());
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
        if (lobbyJoinable.get(s)) {
            playerLobby.put(p, s);
            try {
                Location loc = (Location) getConfig().get("pg.Lobby.coords");
                assert loc != null;
                p.teleport(loc);
                lobbyStates.replace(s, GameStates.WAITING);
                if (!lobbyTickstarted.get(s)) {
                    tickLobby(s);
                    lobbyTickstarted.replace(s, true);
                }
            } catch (Exception ex) {
                for (Player all : playerLobby.keySet()) {
                    if (playerLobby.get(all).equals(s)) {
                        if (all.isOp()) {
                            all.sendMessage(prefix + p.getName() + ChatColor.RED + " " + chat.get(18));
                        }
                    }
                }
            }
            String name = getConfig().getString("pg.Lobby.world");
            setGameRules(name);
            assert name != null;
            Objects.requireNonNull(Bukkit.getWorld(name)).setGameRule(GameRule.FALL_DAMAGE, false);
            if (activateTeams) {
                ItemStack teamselector = new ItemStack(Material.CLOCK);
                ItemMeta teamselectormeta = teamselector.getItemMeta();
                assert teamselectormeta != null;
                teamselectormeta.setDisplayName(ChatColor.DARK_AQUA + chat.get(43));
                teamselector.setItemMeta(teamselectormeta);
                p.getInventory().setItem(4, teamselector);
            }
            if (activateKits) {
                ItemStack kitselector = new ItemStack(Material.ENDER_CHEST);
                ItemMeta kitselectormeta = kitselector.getItemMeta();
                assert kitselectormeta != null;
                kitselectormeta.setDisplayName(ChatColor.DARK_AQUA + chat.get(62));
                kitselector.setItemMeta(kitselectormeta);
                p.getInventory().setItem(0, kitselector);
            }
            ItemStack votepaper = new ItemStack(Material.PAPER);
            ItemMeta votepapaermeta = votepaper.getItemMeta();
            assert votepapaermeta != null;
            votepapaermeta.setDisplayName(ChatColor.DARK_AQUA + chat.get(14));
            votepaper.setItemMeta(votepapaermeta);
            p.getInventory().setItem(8, votepaper);
        }
        if (!lobbyJoinable.get(s)) {
            specLobby.put(p, s);
            try {
                String vote = getVote();
                Location loc = arenadata.getLocation("pg.arenas." + vote + ".spawns." + 1);
                assert loc != null;
                p.teleport(loc);
            } catch (Exception e) {
                for (Player all : playerLobby.keySet()) {
                    if (playerLobby.get(all).equals(s)) {
                        if (all.isOp()) {
                            all.sendMessage(prefix + p.getName() + ChatColor.RED + chat.get(8));
                        }
                    }
                }
            }
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
        }
    }

    public void voteResults(String s) {
        FileConfiguration arenadata = YamlConfiguration.loadConfiguration(arenadatafile);
        int max = 0;
        for (int i : lobbyvotes.get(s).values()) {
            if (i > max) {
                max = i;
            }
        }
        String winner = "";
        for (String all : lobbyvotes.get(s).keySet()) {
            if (lobbyvotes.get(s).get(all) == max) {
                winner = all;
            }
        }
        if (!lobbyForcearena.get(s) && lobbyVoted.get(s) != null) {
            boolean randomArena = false;
            int i = 1;
            boolean votedArena = false;
            while (!votedArena) {
                if (winner.equals(chat.get(42))) {
                    String arenaName = null;
                    while (arenaName == null) {
                        Random rnd = new Random();
                        int rndArena = rnd.nextInt(lobbyvotes.get(s).keySet().size() + 1);
                        winner = String.valueOf(rndArena);
                        arenaName = arenadata.getString("pg.arenas." + winner + ".name");
                        if (arenaName != null) {
                            for (Player all : playerLobby.keySet()) {
                                if (playerLobby.get(all).equals(s)) {
                                    all.sendMessage(prefix + ChatColor.AQUA + arenadata.get("pg.arenas." + winner + ".name") + ChatColor.GREEN + " " + chat.get(7));
                                }
                            }
                        }
                        randomArena = true;
                        votedArena = true;
                    }
                }
                if (!randomArena) {
                    if (winner.equals(arenadata.getString("pg.arenas." + i + ".name"))) {
                        winner = String.valueOf(i);
                        for (Player all : playerLobby.keySet()) {
                            if (playerLobby.get(all).equals(s)) {
                                all.sendMessage(prefix + ChatColor.AQUA + arenadata.get("pg.arenas." + winner + ".name") + ChatColor.GREEN + " " + chat.get(7));
                            }
                        }
                        votedArena = true;
                    } else {
                        i++;
                    }
                }
            }
        } else if (lobbyForcearena.get(s) && lobbyVoted.get(s) != null || lobbyForcearena.get(s) && lobbyVoted.get(s) == null) {
            winner = getVote();
            for (Player all : playerLobby.keySet()) {
                if (playerLobby.get(all).equals(s)) {
                    all.sendMessage(prefix + ChatColor.AQUA + getVotedArena() + ChatColor.GREEN + " " + chat.get(7));
                }
            }
        } else if (lobbyVoted.get(s) == null && !lobbyForcearena.get(s)) {
            String arenaName = null;
            while (arenaName == null) {
                Random rnd = new Random();
                int rndArena = rnd.nextInt(lobbyvotes.get(s).keySet().size() + 1);
                winner = String.valueOf(rndArena);
                arenaName = arenadata.getString("pg.arenas." + winner + ".name");
                if (arenaName != null) {
                    for (Player all : playerLobby.keySet()) {
                        if (playerLobby.get(all).equals(s)) {
                            all.sendMessage(prefix + ChatColor.AQUA + arenadata.get("pg.arenas." + winner + ".name") + ChatColor.GREEN + " " + chat.get(7));
                        }
                    }
                }
            }
        }
        setVote(winner);
    }

    public void teleportAndStart(String s) {
        FileConfiguration arenadata = YamlConfiguration.loadConfiguration(arenadatafile);
        for (Player all : playerLobby.keySet()) {
            if (playerLobby.get(all).equals(s)) {
                int maxteamplayers = teamSize;
                boolean teamfound = false;
                if (activateTeams) {
                    if (!teamed.contains(all.getName())) {
                        while (!teamfound) {
                            Random rnd = new Random();
                            int rndTeam = rnd.nextInt(teams.size() + 1);
                            if (lobbyteams.get(s).get(Integer.toString(rndTeam)) < maxteamplayers && lobbyteams.get(s).get(Integer.toString(rndTeam)) >= 0 && lobbyteams.get(s).get(Integer.toString(rndTeam)) != null) {
                                teamfound = true;
                                int players = lobbyteams.get(s).get(Integer.toString(rndTeam));
                                players++;
                                HashMap<String, Integer> temp = new HashMap<>();
                                for (int max = 1; max <= getTeamAmount(); max++) {
                                    int oldplayers = lobbyteams.get(s).get(Integer.toString(max));
                                    temp.put(Integer.toString(max), oldplayers);
                                }
                                temp.put(Integer.toString(rndTeam), players);
                                lobbyteams.replace(s, temp);
                                all.sendMessage(prefix + "--------------" + chat.get(43) + "--------------");
                                all.sendMessage(prefix + ChatColor.GREEN + chat.get(45) + ": " + ChatColor.LIGHT_PURPLE + rndTeam);
                                all.sendMessage(prefix + ChatColor.GREEN + chat.get(44) + ": " + ChatColor.AQUA + lobbyteams.get(s).get(Integer.toString(rndTeam)) + ChatColor.GRAY + "/" + ChatColor.AQUA + maxteamplayers);
                                all.sendMessage(prefix + "--------------" + chat.get(43) + "--------------");
                                teamed.add(all.getName());
                                lobbyteamplayernames.get(s).put(Integer.toString(rndTeam), all);
                            }
                        }
                    }
                }
                if (activateKits) {
                    if (!kited.contains(all.getName())) {
                        Random rnd = new Random();
                        int rndKit = rnd.nextInt(activeKits);
                        all.sendMessage(prefix + "--------------" + chat.get(62) + "--------------");
                        all.sendMessage(prefix + ChatColor.GREEN + chat.get(46) + ": " + ChatColor.LIGHT_PURPLE + kits.get(rndKit));
                        all.sendMessage(prefix + "--------------" + chat.get(62) + "--------------");
                        kited.add(all.getName());
                        kitplayernames.put(kits.get(rndKit), all);
                        if (kits.get(rndKit).equals("Rich Kid")) {
                            richkidPlayers.add(all);
                        }
                    }
                }
            }
        }
        if (activateTeams) {
            String teamname;
            for (int i = 1; i <= teamAmount; i++) {
                teamname = String.valueOf(i);
                if (lobbyteams.get(s).get(teamname) == 0) {
                    lobbyteams.get(s).remove(teamname);
                }
            }
        }
        int i = 1;
        for (Player all : playerLobby.keySet()) {
            if (playerLobby.get(all).equals(s)) {
                try {
                    String vote = getVote();
                    Location loc = arenadata.getLocation("pg.arenas." + vote + ".spawns." + i);
                    assert loc != null;
                    all.teleport(loc);
                } catch (Exception e) {
                    for (Player op : playerLobby.keySet()) {
                        if (playerLobby.get(all).equals(s)) {
                            if (op.isOp()) {
                                op.sendMessage(prefix + all.getName() + ChatColor.RED + chat.get(8));
                            }
                        }
                    }
                }
            }
            i++;
        }
    }

    public void leaveLobby(Player p, String s) {
        FileConfiguration arenadata = YamlConfiguration.loadConfiguration(arenadatafile);
        joinChannel(p.getPlayer(), "Global");
        if (lobbyStates.get(s) == GameStates.INGAME && lobbyAmount.get(s) > 1 && playerLobby.containsKey(p)) {
            addLosts(p.getUniqueId().toString(), 1);
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
        if (lobbyStates.get(s) == GameStates.WAITING || lobbyStates.get(s) == GameStates.PREPARING) {
            if (activateTeams) {
                if (teamed.contains(p.getName())) {
                    String teamname = "";
                    for (int i = 1; i <= getTeamAmount(); i++) {
                        if (lobbyteamplayernames.get(s).containsKey(Integer.toString(i)) && lobbyteamplayernames.get(s).containsValue(p)) {
                            if (lobbyteamplayernames.get(s).get(Integer.toString(i)) == p) {
                                teamname = Integer.toString(i);
                            }
                        }
                    }
                    lobbyteamplayernames.get(s).remove(teamname, p);
                    int teamamount = lobbyteams.get(s).get(teamname);
                    teamamount--;
                    HashMap<String, Integer> temp = new HashMap<>();
                    for (int max = 1; max <= getTeamAmount(); max++) {
                        int oldplayers = lobbyteams.get(s).get(Integer.toString(max));
                        temp.put(Integer.toString(max), oldplayers);
                    }
                    temp.put(teamname, teamamount);
                    lobbyteams.replace(s, temp);
                    if (lobbyStates.get(s) == GameStates.INGAME) {
                        if (lobbyteams.get(s).get(teamname) == 0) {
                            lobbyteams.get(s).remove(teamname);
                        }
                    }
                    teamed.remove(p.getName());
                }
            }
            if (voted.contains(p.getName())) {
                String arenaname = "";
                for (int i = 1; i <= lobbyStates.keySet().size(); i++) {
                    if (lobbyvoteplayernames.get(s).containsKey(arenadata.getString("pg.arenas." + i + ".name")) && lobbyvoteplayernames.get(s).containsValue(p)) {
                        if (lobbyvoteplayernames.get(s).get(arenadata.getString("pg.arenas." + i + ".name")) == p) {
                            arenaname = arenadata.getString("pg.arenas." + i + ".name");
                        }
                    }
                }
                lobbyvoteplayernames.get(s).remove(arenaname, p);
                int voteamount = lobbyvotes.get(s).get(arenaname);
                voteamount--;
                HashMap<String, Integer> temp = new HashMap<>();
                int randomvotes;
                randomvotes = lobbyvotes.get(s).get(chat.get(42));
                temp.put(chat.get(42), randomvotes);
                for (int max = 1; max <= getTeamAmount(); max++) {
                    int oldplayers = lobbyvotes.get(s).get(arenadata.getString("pg.arenas." + max + ".name"));
                    temp.put(arenadata.getString("pg.arenas." + max + ".name"), oldplayers);
                }
                temp.put(arenaname, voteamount);
                lobbyvotes.replace(s, temp);
                voted.remove(p.getName());
            }
        }
    }

    public void tickLobby(String s) {
        FileConfiguration arenadata = YamlConfiguration.loadConfiguration(arenadatafile);
        FileConfiguration kitdata = YamlConfiguration.loadConfiguration(kitdatafile);
        countdownLobby.put(s, getConfig().getInt("pg.countdown"));
        resetLobby.put(s, reset);
        tick = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            if (!isPause()) {
                int gamerule = 1;
                switch (lobbyStates.get(s)) {
                    case WAITING:
                        for (Player all : specLobby.keySet()) {
                            if (specLobby.get(all).equals(s)) {
                                specLobby.remove(all);
                            }
                        }
                        lobbyMove.replace(s, true);
                        lobbyJoinable.replace(s, true);
                        if (getConfig().contains("pg.Lobby.world")) {
                            Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("pg.Lobby.world")))).setDifficulty(Difficulty.PEACEFUL);
                            Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("pg.Lobby.world")))).setPVP(false);
                        }
                        while (arenadata.contains("pg.arenas." + gamerule)) {
                            String name = arenadata.getString("pg.arenas." + gamerule + ".world");
                            setGameRules(name);
                            assert name != null;
                            Objects.requireNonNull(Bukkit.getWorld(name)).setGameRule(GameRule.FALL_DAMAGE, true);
                            gamerule++;
                        }
                        if (!voteallowed) {
                            voteallowed = true;
                            int arena = 1;
                            while (arenadata.contains("pg.arenas." + arena)) {
                                String name = arenadata.getString("pg.arenas." + arena + ".name");
                                arenas.add(name);
                                arena++;
                            }
                            votes.put(chat.get(42), 0);
                            for (String all : arenas) {
                                votes.put(all, 0);
                            }
                        }
                        if (activateTeams) {
                            if (!teamallowed) {
                                teamallowed = true;
                                int team = 1;
                                while (team <= teamAmount) {
                                    String tname = Integer.toString(team);
                                    teams.add(tname);
                                    team++;
                                }
                                teamplayers.put(chat.get(42), 0);
                                for (String all : teams) {
                                    teamplayers.put(all, 0);
                                }
                            }
                        }
                        int kit = 1;
                        for (int i = 0; i < activeKits; i++) {
                            String kname = kitdata.getString("pg.kits." + kit + ".name");
                            kits.add(kname);
                            kit++;
                        }
                        if (getConfig().contains("pg.RankWall.headp1") && getConfig().contains("pg.RankWall.headp2") && getConfig().contains("pg.RankWall.headp3") && getConfig().contains("pg.RankWall.signp1") && getConfig().contains("pg.RankWall.signp2") && getConfig().contains("pg.RankWall.signp3")) {
                            ResultSet rs = query("SELECT UUID FROM Stats ORDER BY WINS DESC LIMIT 3");
                            int ii = 0;
                            try {
                                while (rs.next()) {
                                    ii++;
                                    rank.put(ii, rs.getString("UUID"));
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
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
                                OfflinePlayer sname = Bukkit.getOfflinePlayer(UUID.fromString(rank.get(id)));
                                Sign sign = (Sign) b;
                                sign.setLine(0, chat.get(33) + " #" + id);
                                sign.setLine(1, Objects.requireNonNull(sname.getName()));
                                sign.setLine(2, "Wins: " + getWins(rank.get(id)));
                                sign.setLine(3, "K/D: " + getKD(rank.get(id)));
                                sign.update();
                            }
                        }
                        lobbyJoinable.replace(s, true);
                        if (lobbyAmount.get(s) < minPlayers) {
                            for (Player all : playerLobby.keySet()) {
                                if (playerLobby.get(all).equals(s)) {
                                    all.setLevel(0);
                                    all.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(prefix + chat.get(0) + " " + "[" + ChatColor.AQUA + lobbyAmount.get(s) + ChatColor.GRAY + "/" + ChatColor.AQUA + minPlayers + ChatColor.GRAY + "]"));
                                }
                            }
                            countdownLobby.replace(s, getConfig().getInt("pg.countdown"));
                        } else {
                            lobbyStates.replace(s, GameStates.PREPARING);
                        }
                        break;
                    case PREPARING:
                        if (lobbyAmount.get(s) >= minPlayers) {
                            for (Player all : playerLobby.keySet()) {
                                if (playerLobby.get(all).equals(s)) {
                                    all.setLevel(countdownLobby.get(s));
                                }
                            }
                            if (countdownLobby.get(s) == 10) {
                                for (Player all : playerLobby.keySet()) {
                                    if (playerLobby.get(all).equals(s)) {
                                        all.setLevel(countdownLobby.get(s));
                                    }
                                }
                                voteResults(s);
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
                                teleportAndStart(s);
                                countdownLobby.replace(s, 11);
                                lobbyStates.replace(s, GameStates.INGAME);
                                lobbyMove.replace(s, false);
                            }
                            countdownLobby.replace(s, countdownLobby.get(s) - 1);
                        } else {
                            lobbyStates.replace(s, GameStates.INGAME);
                        }
                        break;
                    case INGAME:
                        int setting = Integer.parseInt(s);
                        String wname = arenadata.getString("pg.arenas." + setting + ".world");
                        assert wname != null;
                        Objects.requireNonNull(getServer().getWorld(wname)).setDifficulty(Difficulty.EASY);
                        Objects.requireNonNull(getServer().getWorld(wname)).setPVP(true);
                        lobbyJoinable.replace(s, false);
                        if (lobbyAmount.get(s) != 0) {
                            if (countdownLobby.get(s) == 10) {
                                for (Player all : playerLobby.keySet()) {
                                    if (playerLobby.get(all).equals(s)) {
                                        all.getInventory().clear();
                                        all.sendMessage(prefix + ChatColor.GREEN + chat.get(1) + " " + ChatColor.AQUA + countdownLobby.get(s));
                                        ItemStack playercompass = new ItemStack(Material.COMPASS);
                                        ItemMeta playercompassmeta = playercompass.getItemMeta();
                                        assert playercompassmeta != null;
                                        playercompassmeta.setDisplayName(ChatColor.DARK_AQUA + chat.get(2));
                                        playercompass.setItemMeta(playercompassmeta);
                                        all.getInventory().setItem(8, playercompass);
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
                                        all.sendMessage(prefix + ChatColor.GREEN + chat.get(1) + " " + ChatColor.AQUA + countdownLobby.get(s));
                                        all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                                    }
                                }
                                countdownLobby.replace(s, countdownLobby.get(s) - 1);
                            } else if (countdownLobby.get(s) == 0) {
                                lobbyMove.replace(s, true);
                                for (Player all : playerLobby.keySet()) {
                                    if (playerLobby.get(all).equals(s)) {
                                        all.sendMessage(prefix + ChatColor.GREEN + chat.get(3));
                                        all.playSound(all.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 1);
                                        all.setGameMode(GameMode.SURVIVAL);
                                        countdownLobby.replace(s, -1);
                                    }
                                }
                            } else if (countdownLobby.get(s) == -1) {
                                if (activateTeams) {
                                    if (lobbyAmount.get(s) == 1 || lobbyteams.get(s).size() == 1)
                                        countdownLobby.replace(s, -2);
                                } else {
                                    if (lobbyAmount.get(s) == 1) {
                                        countdownLobby.replace(s, -2);
                                    }
                                }
                            } else if (countdownLobby.get(s) == -2) {
                                Player winner;
                                for (Player all : playerLobby.keySet()) {
                                    if (playerLobby.get(all).equals(s)) {
                                        for (Player win : playerLobby.keySet()) {
                                            if (playerLobby.get(win).equals(s)) {
                                                winner = win;
                                                spawnFireworks(winner.getLocation(), 1);
                                                addWins(winner.getUniqueId().toString(), 1);
                                                all.sendMessage(prefix + ChatColor.AQUA + winner.getName() + ChatColor.GREEN + " " + chat.get(4));
                                            }
                                        }
                                    }
                                }
                                for (Player all : specLobby.keySet()) {
                                    if (specLobby.get(all).equals(s)) {
                                        for (Player win : playerLobby.keySet()) {
                                            if (playerLobby.get(win).equals(s)) {
                                                winner = win;
                                                all.sendMessage(prefix + ChatColor.AQUA + winner.getName() + ChatColor.GREEN + " " + chat.get(4));
                                            }
                                        }
                                    }
                                }
                                countdownLobby.replace(s, -3);
                            } else if (countdownLobby.get(s) == -3) {
                                if (resetLobby.get(s) == 10) {
                                    for (Player all : playerLobby.keySet()) {
                                        if (playerLobby.get(all).equals(s)) {
                                            all.sendMessage(prefix + ChatColor.GREEN + chat.get(5) + " " + ChatColor.AQUA + resetLobby.get(s));
                                        }
                                    }
                                    for (Player all : specLobby.keySet()) {
                                        if (specLobby.get(all).equals(s)) {
                                            all.sendMessage(prefix + ChatColor.GREEN + chat.get(5) + " " + ChatColor.AQUA + resetLobby.get(s));
                                        }
                                    }
                                    resetLobby.put(s, resetLobby.get(s) - 1);
                                } else if (resetLobby.get(s) <= 9 && resetLobby.get(s) > 5) {
                                    resetLobby.put(s, resetLobby.get(s) - 1);
                                } else if (resetLobby.get(s) <= 5 && resetLobby.get(s) > 0) {
                                    for (Player all : playerLobby.keySet()) {
                                        if (playerLobby.get(all).equals(s)) {
                                            all.sendMessage(prefix + ChatColor.GREEN + chat.get(5) + " " + ChatColor.AQUA + resetLobby.get(s));
                                        }
                                    }
                                    for (Player all : specLobby.keySet()) {
                                        if (specLobby.get(all).equals(s)) {
                                            all.sendMessage(prefix + ChatColor.GREEN + chat.get(5) + " " + ChatColor.AQUA + resetLobby.get(s));
                                        }
                                    }
                                    resetLobby.put(s, resetLobby.get(s) - 1);
                                } else if (resetLobby.get(s) == 0) {
                                    for (Player all : playerLobby.keySet()) {
                                        if (playerLobby.get(all).equals(s)) {
                                            all.sendMessage(prefix + ChatColor.GREEN + chat.get(6));
                                        }
                                    }
                                    for (Player all : specLobby.keySet()) {
                                        if (specLobby.get(all).equals(s)) {
                                            all.sendMessage(prefix + ChatColor.GREEN + chat.get(6));
                                        }
                                    }
                                    lobbyStates.replace(s, GameStates.RESET);
                                }
                            }
                        } else {
                            lobbyStates.replace(s, GameStates.RESET);
                        }
                        break;
                    case RESET:
                        lobbyMove.replace(s, true);
                        lobbyJoinable.replace(s, true);
                        resetLobby.replace(s, reset);
                        countdownLobby.replace(s, getConfig().getInt("pg.countdown"));
                        playerLobby.putAll(specLobby);
                        for (Player all : playerLobby.keySet()) {
                            if (playerLobby.get(all).equals(s)) {
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
                                    teamselectormeta.setDisplayName(ChatColor.DARK_AQUA + chat.get(43));
                                    teamselector.setItemMeta(teamselectormeta);
                                    inv.setItem(4, teamselector);
                                }
                                if (activateKits) {
                                    ItemStack kitselector = new ItemStack(Material.ENDER_CHEST);
                                    ItemMeta kitselectormeta = kitselector.getItemMeta();
                                    assert kitselectormeta != null;
                                    kitselectormeta.setDisplayName(ChatColor.DARK_AQUA + chat.get(62));
                                    kitselector.setItemMeta(kitselectormeta);
                                    inv.setItem(0, kitselector);
                                }
                                ItemStack votepaper = new ItemStack(Material.PAPER);
                                ItemMeta votepapaermeta = votepaper.getItemMeta();
                                assert votepapaermeta != null;
                                votepapaermeta.setDisplayName(ChatColor.DARK_AQUA + chat.get(14));
                                votepaper.setItemMeta(votepapaermeta);
                                inv.setItem(8, votepaper);
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
                                voted.remove(all.getName());
                            }
                        }
                        lobbyVoteallowed.replace(s, false);
                        lobbyForcearena.replace(s, false);
                        if (activateTeams) {
                            for (Player all : playerLobby.keySet()) {
                                if (playerLobby.get(all).equals(s)) {
                                    teamed.remove(all.getName());
                                }
                            }
                        }
                        lobbyTeamallowed.replace(s, false);
                        for (Player all : playerLobby.keySet()) {
                            if (playerLobby.get(all).equals(s)) {
                                lobbyteamplayernames.remove(all.getName());
                            }
                        }
                        for (Player all : playerLobby.keySet()) {
                            if (playerLobby.get(all).equals(s)) {
                                kited.remove(all.getName());
                            }
                        }
                        lobbyKitallowed.replace(s, false);
                        for (Player all : playerLobby.keySet()) {
                            if (playerLobby.get(all).equals(s)) {
                                kitplayernames.remove(all.getName());
                            }
                        }
                        for (Player all : playerLobby.keySet()) {
                            if (playerLobby.get(all).equals(s)) {
                                richkidPlayers.remove(all);
                            }
                        }
                        Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("pg.Lobby.world")))).setDifficulty(Difficulty.PEACEFUL);
                        Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("pg.Lobby.world")))).setPVP(false);
                        String gname = arenadata.getString("pg.arenas." + gamerule + ".world");
                        setGameRules(gname);
                        assert gname != null;
                        Objects.requireNonNull(Bukkit.getWorld(gname)).setGameRule(GameRule.FALL_DAMAGE, true);
                        if (!lobbyVoteallowed.get(s)) {
                            lobbyVoteallowed.replace(s, true);
                            HashMap<String, Integer> temp = new HashMap<>();
                            temp.put(chat.get(42), 0);
                            for (int max = 1; max <= lobbyStates.keySet().size(); max++) {
                                temp.put(arenadata.getString("pg.arenas." + max + ".name"), 0);
                            }
                            lobbyvotes.replace(s, temp);
                        }
                        if (!lobbyTeamallowed.get(s)) {
                            lobbyTeamallowed.replace(s, true);
                            HashMap<String, Integer> temp = new HashMap<>();
                            for (int max = 1; max <= teamAmount; max++) {
                                temp.put(Integer.toString(max), 0);
                                lobbyteamplayernamesdata.put(Integer.toString(max), null);
                            }
                            lobbyteams.replace(s, temp);
                            lobbyteamplayernames.replace(s, lobbyteamplayernamesdata);
                        }
                        if (!lobbyKitallowed.get(s)) {
                            lobbyKitallowed.replace(s, true);
                            kitplayers.put(chat.get(42), 0);
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
                        int worldName = Integer.parseInt(s);
                        String ename = arenadata.getString("pg.arenas." + worldName + ".world");
                        assert ename != null;
                        World world = getServer().getWorld(ename);
                        assert world != null;
                        List<Entity> entList = world.getEntities();
                        for (Entity current : entList) {
                            if (current != null) {
                                current.remove();
                            }
                        }
                        lobbyStates.replace(s, GameStates.WAITING);
                        break;
                    default:
                        Bukkit.getScheduler().cancelTask(tick);
                        Bukkit.shutdown();
                        break;
                }
            }
        }, 0, 20);
    }

    public void ConnectMySQL() {
        update("CREATE TABLE IF NOT EXISTS Stats(UUID varchar(64), ROUNDS int, WINS int, LOSTS int, KILLS int, DEATHS int, KD double);");
    }

    public boolean playerExists(String uuid) {
        try {
            ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'");
            if (rs.next()) {
                return rs.getString("UUID") != null;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createPlayer(String uuid) {
        if (!playerExists(uuid)) {
            update("INSERT INTO Stats(UUID, ROUNDS, WINS, LOSTS, KILLS, DEATHS, KD) VALUES ('" + uuid + "', '0', '0', '0', '0', '0', '0');");
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
                e.printStackTrace();
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
                e.printStackTrace();
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
                e.printStackTrace();
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
                e.printStackTrace();
            }
        } else {
            createPlayer(uuid);
            getWins(uuid);
        }
        return i;
    }

    public int getLosts(String uuid) {
        int i = 0;
        if (playerExists(uuid)) {
            try {
                ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'");
                if ((rs.next())) {
                    rs.getInt("LOSTS");
                }
                i = rs.getInt("LOSTS");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            createPlayer(uuid);
            getLosts(uuid);
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
                e.printStackTrace();
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

    public void setWins(String uuid, int deaths) {
        if (playerExists(uuid)) {
            update("UPDATE Stats SET WINS= '" + deaths + "' WHERE UUID= '" + uuid + "';");
        } else {
            createPlayer(uuid);
            setDeaths(uuid, deaths);
        }
    }

    public void setLosts(String uuid, int deaths) {
        if (playerExists(uuid)) {
            update("UPDATE Stats SET LOSTS= '" + deaths + "' WHERE UUID= '" + uuid + "';");
        } else {
            createPlayer(uuid);
            setDeaths(uuid, deaths);
        }
    }

    public void setRounds(String uuid, int rounds) {
        if (playerExists(uuid)) {
            int wins = getWins(uuid);
            int losts = getLosts(uuid);
            rounds = wins + losts;
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

    public void addLosts(String uuid, int losts) {
        if (playerExists(uuid)) {
            setLosts(uuid, (getLosts(uuid) + losts));
            setRounds(uuid, 0);
        } else {
            createPlayer(uuid);
            addLosts(uuid, losts);
        }
    }

    public GameStates getGamestate() {
        return gamestate;
    }

    public void setGamestate(GameStates gamestate) {
        this.gamestate = gamestate;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public ItemStack getCoin() {
        return coin;
    }

    public ItemStack getBottle() {
        return bottle;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setJoinable(boolean joinable) {
        this.joinable = joinable;
    }

    public int getTeamSize() {
        return teamSize;
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

    public int getReset() {
        return reset;
    }

    public void setReset(int reset) {
        this.reset = reset;
    }

    public int getActivePotions() {
        return activePotions;
    }

    public int getActiveKits() {
        return activeKits;
    }

    public void changePause() {
        pause = !pause;
    }

    public boolean isPause() {
        return pause;
    }

    public void changeBuild() {
        build = !build;
    }

    public boolean isBuild() {
        return build;
    }

    public boolean isArenaSystem() {
        return arenaSystem;
    }

    public boolean isMove() {
        return move;
    }

    public void setMove(boolean move) {
        this.move = move;
    }

    public int getPlayerAmount() {
        return playerAmount;
    }

    public void setPlayerAmount(int playerAmount) {
        this.playerAmount = playerAmount;
    }

    public void addPlayerAmount() {
        playerAmount++;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public boolean isForcearena() {
        return forcearena;
    }

    public void setForcearena(boolean forcearena) {
        this.forcearena = forcearena;
    }

    public String getVotedArena() {
        return votedArena;
    }

    public void setVotedArena(String votedArena) {
        this.votedArena = votedArena;
    }

    public ArrayList<Player> getPgPlayers() {
        return pgPlayers;
    }

    public HashMap<Location, Material> getPlacedBlocks() {
        return placedBlocks;
    }

    public HashMap<Location, Material> getBreakedBlocks() {
        return breakedBlocks;
    }

    public HashMap<Location, BlockData> getWaterBlocks() {
        return waterBlocks;
    }

    public HashMap<Location, Block> getLiquidPlaced() {
        return liquidPlaced;
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

    public ArrayList<Player> getChannel(Player player) {
        String channelName = playerChannel.get(player);
        return channels.get(channelName);
    }

}
