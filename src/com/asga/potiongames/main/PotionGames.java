package com.asga.potiongames.main;

import com.asga.potiongames.commands.Commands;
import com.asga.potiongames.events.Events;
import com.asga.potiongames.gamestates.GameStates;
import de.dytanic.cloudnet.ext.bridge.bukkit.BukkitCloudNetHelper;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

public class PotionGames extends JavaPlugin {
    public String prefixNoColor = "[PotionGames]";
    public String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_PURPLE + "Potion" + ChatColor.GOLD + "Games" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
    public ArrayList<Player> pgPlayers = new ArrayList<>();
    public ArrayList<Player> specPlayers = new ArrayList<>();
    public ArrayList<String> arenas = new ArrayList<>();
    public ArrayList<String> voted = new ArrayList<>();
    public ArrayList<String> chat = new ArrayList<>();
    public ArrayList<Location> rankhead = new ArrayList<>();
    public ArrayList<Location> ranksign = new ArrayList<>();
    public HashMap<String, Player> arenaplayers = new HashMap<>();
    public HashMap<Integer, String> rank = new HashMap<>();
    public HashMap<String, Integer> votes = new HashMap<>();
    public HashMap<Location, Inventory> chests = new HashMap<>();
    public HashMap<Location, Material> placedBlocks = new HashMap<>();
    public HashMap<Location, Material> breakedBlocks = new HashMap<>();
    public HashMap<Location, Block> liquidPlaced = new HashMap<>();
    public HashMap<String, ItemStack[]> inv = new HashMap<>();
    public HashMap<String, ItemStack[]> armor = new HashMap<>();
    public HashMap<String, Integer> lvl = new HashMap<>();
    public HashMap<String, Float> exp = new HashMap<>();
    public HashMap<String, Location> loc = new HashMap<>();
    public HashMap<String, GameMode> gm = new HashMap<>();
    public HashMap<String, ArrayList<Player>> channels = new HashMap<>();
    public HashMap<Player, String> playerChannel = new HashMap<>();
    private int countdown = 60;
    private int maxPlayers = 24;
    private int minPlayers = maxPlayers / 2;
    private int playerAmount = 0;
    private String language = "en_US";
    private String vote = "";
    private String votedArena = "";
    private String host = "localhost";
    private String port = "3306";
    private String database = "potiongames";
    private String user = "root";
    private String password = "";
    private String arenaID = "";
    private GameStates gamestate;
    private boolean joinable = true;
    private boolean pause = false;
    private boolean build = false;
    private boolean move = true;
    private boolean voteallowed = false;
    private boolean forcearena = false;
    private boolean startOnJoin = false;
    private boolean tickStarted = false;
    private Connection con;

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
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", user, password);
            System.out.println(prefixNoColor + " " + chat.get(36));
        } catch (SQLException e) {
            System.out.println(prefixNoColor + " " + chat.get(37) + ": " + e.getMessage());
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
        try {
            Statement st = con.createStatement();
            st.executeUpdate(qry);
            st.close();
        } catch (SQLException e) {
            connect();
            System.err.println();
        }
    }

    public ResultSet query(String qry) {
        ResultSet rs = null;

        try {
            Statement st = con.createStatement();
            rs = st.executeQuery(qry);
        } catch (SQLException e) {
            connect();
            System.err.println();
        }
        return rs;
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
        chat.add("Player Finder");
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
        chat.add("Arena Voting");
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
        File messagesfile = new File(getDataFolder() + File.separator + "messages.yml");
        FileConfiguration messages = YamlConfiguration.loadConfiguration(messagesfile);
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
        for (int i = 0; i < chat.size() * 2; i++) {
            if (messages.get("pg.messages." + language + "." + message) == null) {
                messages.addDefault("pg.messages." + language + "." + message, chat.get(message - 1));
                messages.options().copyDefaults(true);
            } else {
                String name = messages.getString("pg.messages." + language + "." + message);
                chat.set(message - 1, name);
            }
            message++;
            i++;
        }
        try {
            messages.save(messagesfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void tick() {
        for (String a : arenas) {
            ArrayList<String> added = new ArrayList<>();
            setGamestate(GameStates.WAITING);
            if (startOnJoin) {
                countdown = getConfig().getInt("pg.countdown");
            } else {
                countdown = getConfig().getInt("pg.arenas." + a + ".countdown");
            }
            AtomicInteger arenaCountdown = new AtomicInteger();
            arenaCountdown.set(countdown);
            AtomicInteger arenaRestart = new AtomicInteger();
            int restart = 10;
            arenaRestart.set(restart);
            AtomicInteger arenaAmount = new AtomicInteger();
            arenaAmount.set(playerAmount);
            AtomicInteger arenaTick = new AtomicInteger();
            int tick = 0;
            arenaTick.set(tick);
            Bukkit.broadcastMessage("Tick: " + arenas.size() + " " + a.length());
            arenaTick.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                Bukkit.broadcastMessage("Scheduler: " + arenas.size() + " " + a.length());
                if (!isPause()) {
                    switch (gamestate) {
                        case WAITING:
                            Bukkit.broadcastMessage("Test: WAITING");
                            //specPlayers.clear();
                            setMove(true);
                            setJoinable(true);
                            if (startOnJoin) {
                                if (getConfig().contains("pg.Lobby.world")) {
                                    Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("pg.Lobby.world")))).setDifficulty(Difficulty.PEACEFUL);
                                    Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("pg.Lobby.world")))).setPVP(false);
                                }
                                int gamerule = 1;
                                while (getConfig().contains("pg.arenas." + gamerule)) {
                                    String name = getConfig().getString("pg.arenas." + gamerule + ".world");
                                    setGameRules(name);
                                    gamerule++;
                                }
                                if (!voteallowed) {
                                    BukkitCloudNetHelper.setExtra(chat.get(14));
                                    voteallowed = true;
                                    int arena = 1;
                                    while (getConfig().contains("pg.arenas." + arena)) {
                                        String name = getConfig().getString("pg.arenas." + arena + ".name");
                                        arenas.add(name);
                                        arena++;
                                    }
                                    votes.put(chat.get(42), 0);
                                    for (String all : arenas) {
                                        votes.put(all, 0);
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
                            Bukkit.broadcastMessage("2. " + arenas.size() + " " + a.length());
                            if (arenaAmount.get() < minPlayers) {
                                Bukkit.broadcastMessage("2.1 " + arenas.size() + " " + a.length());
                                for (Player all : pgPlayers) {
                                    if (arenas.contains(a) && arenaplayers.containsValue(all)) {
                                        if (!added.contains(all.getName())) {
                                            Bukkit.broadcastMessage("2.2 " + arenas.size() + " " + a.length());
                                            arenaAmount.getAndIncrement();
                                            Bukkit.broadcastMessage("2.3 " + arenas.size() + " " + a.length());
                                            added.add(all.getName());
                                        }
                                        all.setLevel(0);
                                        all.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                                TextComponent.fromLegacyText(prefix + chat.get(0) + " " + "[" + ChatColor.AQUA + arenaAmount.get() + ChatColor.GRAY + "/" + ChatColor.AQUA + minPlayers + ChatColor.GRAY + "]"));

                                    }
                                }
                            } else {
                                setGamestate(GameStates.PREPARING);
                            }
                            break;
                        case PREPARING:
                            Bukkit.broadcastMessage("3. " + arenas.size() + " " + a.length());
                            if (arenaAmount.get() >= minPlayers) {
                                for (Player all : pgPlayers) {
                                    if (arenas.contains(a) && arenaplayers.containsValue(all)) {
                                        all.setLevel(arenaCountdown.get());
                                    }
                                }
                                if (arenaCountdown.get() == 20) {
                                    for (Player all : pgPlayers) {
                                        if (arenas.contains(a) && arenaplayers.containsValue(all)) {
                                            all.setLevel(arenaCountdown.get());
                                        }
                                    }
                                    if (startOnJoin) {
                                        voteResults();
                                    }
                                }
                                if (arenaCountdown.get() < 20) {
                                    for (Player all : pgPlayers) {
                                        if (arenas.contains(a) && arenaplayers.containsValue(all)) {
                                            all.setLevel(arenaCountdown.get());
                                        }
                                    }
                                }
                                if (arenaCountdown.get() == 0) {
                                    for (Player all : pgPlayers) {
                                        if (arenas.contains(a) && arenaplayers.containsValue(all)) {
                                            all.setLevel(arenaCountdown.get());
                                        }
                                    }
                                    arenaID = a;
                                    teleportAndStart();
                                    arenaCountdown.set(11);
                                    setGamestate(GameStates.INGAME);
                                    setMove(false);
                                }
                                arenaCountdown.getAndDecrement();
                            } else {
                                setGamestate(GameStates.WAITING);
                            }
                            break;
                        case INGAME:
                            int setting = 1;
                            while (getConfig().contains("pg.arenas." + setting)) {
                                String name = getConfig().getString("pg.arenas." + setting + ".world");
                                assert name != null;
                                Objects.requireNonNull(getServer().getWorld(name)).setDifficulty(Difficulty.EASY);
                                Objects.requireNonNull(getServer().getWorld(name)).setPVP(true);
                                setting++;
                            }
                            setJoinable(false);
                            if (arenaAmount.get() != 0) {
                                if (arenaCountdown.get() == 10) {
                                    for (Player all : pgPlayers) {
                                        if (arenas.contains(a) && arenaplayers.containsValue(all)) {
                                            all.getInventory().clear();
                                            all.sendMessage(prefix + ChatColor.GREEN + chat.get(1) + " " + ChatColor.AQUA + arenaCountdown.get());
                                            ItemStack playercompass = new ItemStack(Material.COMPASS);
                                            ItemMeta playercompassmeta = playercompass.getItemMeta();
                                            assert playercompassmeta != null;
                                            playercompassmeta.setDisplayName(ChatColor.DARK_AQUA + chat.get(2));
                                            playercompass.setItemMeta(playercompassmeta);
                                            all.getInventory().setItem(8, playercompass);
                                        }
                                    }
                                    arenaCountdown.getAndDecrement();
                                } else if (arenaCountdown.get() <= 9 && arenaCountdown.get() > 5) {
                                    arenaCountdown.getAndDecrement();
                                } else if (arenaCountdown.get() <= 5 && arenaCountdown.get() > 0) {
                                    for (Player all : pgPlayers) {
                                        if (arenas.contains(a) && arenaplayers.containsValue(all)) {
                                            all.sendMessage(prefix + ChatColor.GREEN + chat.get(1) + " " + ChatColor.AQUA + arenaCountdown.get());
                                            all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                                        }
                                    }
                                    arenaCountdown.getAndDecrement();
                                } else if (arenaCountdown.get() == 0) {
                                    setMove(true);
                                    for (Player all : pgPlayers) {
                                        if (arenas.contains(a) && arenaplayers.containsValue(all)) {
                                            all.sendMessage(prefix + ChatColor.GREEN + chat.get(3));
                                            all.playSound(all.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 1);
                                            all.setGameMode(GameMode.SURVIVAL);
                                        }
                                    }
                                    arenaCountdown.set(-1);
                                } else if (arenaCountdown.get() == -1) {
                                    if (a.length() <= 1) {
                                        for (int i = 0; i < pgPlayers.size(); i++) {
                                            Player winner = pgPlayers.get(i);
                                            for (Player all : pgPlayers) {
                                                if (arenas.contains(a) && arenaplayers.containsValue(all)) {
                                                    all.sendMessage(prefix + ChatColor.AQUA + winner.getName() + ChatColor.GREEN + " " + chat.get(4));
                                                }
                                            }
                                            for (Player all : specPlayers) {
                                                if (arenas.contains(a) && arenaplayers.containsValue(all)) {
                                                    all.sendMessage(prefix + ChatColor.AQUA + winner.getName() + ChatColor.GREEN + " " + chat.get(4));
                                                }
                                            }
                                            spawnFireworks(winner.getLocation(), 1);
                                            addWins(winner.getUniqueId().toString(), 1);
                                            arenaCountdown.set(-2);
                                        }
                                    }
                                } else if (arenaCountdown.get() == -2) {
                                    if (arenaRestart.get() == 10) {
                                        for (Player all : pgPlayers) {
                                            if (arenas.contains(a) && arenaplayers.containsValue(all)) {
                                                all.sendMessage(prefix + ChatColor.GREEN + chat.get(5) + " " + ChatColor.AQUA + arenaRestart.get());
                                            }
                                        }
                                        for (Player all : specPlayers) {
                                            if (arenas.contains(a) && arenaplayers.containsValue(all)) {
                                                all.sendMessage(prefix + ChatColor.GREEN + chat.get(5) + " " + ChatColor.AQUA + arenaRestart.get());
                                            }
                                        }
                                        arenaRestart.getAndDecrement();
                                    } else if (arenaRestart.get() <= 9 && arenaRestart.get() > 5) {
                                        arenaRestart.getAndDecrement();
                                    } else if (arenaRestart.get() <= 5 && arenaRestart.get() > 0) {
                                        for (Player all : pgPlayers) {
                                            if (arenas.contains(a) && arenaplayers.containsValue(all)) {
                                                all.sendMessage(prefix + ChatColor.GREEN + chat.get(5) + " " + ChatColor.AQUA + arenaRestart.get());
                                            }
                                        }
                                        for (Player all : specPlayers) {
                                            if (arenas.contains(a) && arenaplayers.containsValue(all)) {
                                                all.sendMessage(prefix + ChatColor.GREEN + chat.get(5) + " " + ChatColor.AQUA + arenaRestart.get());
                                            }
                                        }
                                        arenaRestart.getAndDecrement();
                                    } else if (arenaRestart.get() == 0) {
                                        for (Player all : pgPlayers) {
                                            if (arenas.contains(a) && arenaplayers.containsValue(all)) {
                                                all.sendMessage(prefix + ChatColor.GREEN + chat.get(6));
                                            }
                                        }
                                        for (Player all : specPlayers) {
                                            if (arenas.contains(a) && arenaplayers.containsValue(all)) {
                                                all.sendMessage(prefix + ChatColor.GREEN + chat.get(6));
                                            }
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
                            arenaRestart.set(10);
                            if (startOnJoin) {
                                arenaCountdown.set(getConfig().getInt("pg.countdown"));
                            } else {
                                arenaCountdown.set(getConfig().getInt("pg.arenas." + a + ".countdown"));
                            }
                            for (Player all : specPlayers) {
                                if (arenas.contains(a) && arenaplayers.containsValue(all)) {
                                    pgPlayers.add(all);
                                }
                            }
                            for (Player all : pgPlayers) {
                                if (arenas.contains(a) && arenaplayers.containsValue(all)) {
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
                                    if (startOnJoin) {
                                        ItemStack votepaper = new ItemStack(Material.PAPER);
                                        ItemMeta votepapaermeta = votepaper.getItemMeta();
                                        assert votepapaermeta != null;
                                        votepapaermeta.setDisplayName(ChatColor.DARK_AQUA + chat.get(14));
                                        votepaper.setItemMeta(votepapaermeta);
                                        inv.setItem(8, votepaper);
                                    }
                                }
                            }
                            //specPlayers.clear();
                            chests.clear();
                            if (startOnJoin) {
                                voted.clear();
                                voteallowed = false;
                                forcearena = false;
                                Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("pg.Lobby.world")))).setDifficulty(Difficulty.PEACEFUL);
                                Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("pg.Lobby.world")))).setPVP(false);
                                int gamerule = 1;
                                while (getConfig().contains("pg.arenas." + gamerule)) {
                                    String name = getConfig().getString("pg.arenas." + gamerule + ".world");
                                    setGameRules(name);
                                    gamerule++;
                                }
                                if (!voteallowed) {
                                    voteallowed = true;
                                    votes.put(chat.get(42), 0);
                                    for (String all : arenas) {
                                        votes.put(all, 0);
                                    }
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
                            int worldName = 1;
                            while (getConfig().contains("pg.arenas." + worldName)) {
                                String name = getConfig().getString("pg.arenas." + worldName + ".world");
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
                            Bukkit.getScheduler().cancelTask(arenaTick.get());
                            Bukkit.shutdown();
                            break;
                    }
                }
            }, 0, 20));
        }
    }

    public void clearEffects(Player all) {
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
    }

    public void setGameRules(String name) {
        Objects.requireNonNull(Bukkit.getWorld(name)).setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        Objects.requireNonNull(Bukkit.getWorld(name)).setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        Objects.requireNonNull(Bukkit.getWorld(name)).setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        Objects.requireNonNull(Bukkit.getWorld(name)).setGameRule(GameRule.DO_FIRE_TICK, false);
        Objects.requireNonNull(Bukkit.getWorld(name)).setGameRule(GameRule.DO_MOB_SPAWNING, false);
        Objects.requireNonNull(Bukkit.getWorld(name)).setGameRule(GameRule.MOB_GRIEFING, false);
        Objects.requireNonNull(Bukkit.getWorld(name)).setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        Objects.requireNonNull(Bukkit.getWorld(name)).setGameRule(GameRule.FALL_DAMAGE, false);
        Objects.requireNonNull(Bukkit.getWorld(name)).setTime(0);
    }

    public void voteResults() {
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
                        arenaName = getConfig().getString("pg.arenas." + winner + ".name");
                        if (arenaName != null) {
                            BukkitCloudNetHelper.setExtra(winner);
                            for (Player all : pgPlayers) {
                                all.sendMessage(prefix + ChatColor.AQUA + getConfig().get("pg.arenas." + winner + ".name") + ChatColor.GREEN + " " + chat.get(7));
                            }
                        }
                        randomArena = true;
                        votedArena = true;
                    }
                }
                if (!randomArena) {
                    if (winner == getConfig().get("pg.arenas." + i + ".name")) {
                        winner = String.valueOf(i);
                        BukkitCloudNetHelper.setExtra(winner);
                        for (Player all : pgPlayers) {
                            all.sendMessage(prefix + ChatColor.AQUA + getConfig().get("pg.arenas." + winner + ".name") + ChatColor.GREEN + " " + chat.get(7));
                        }
                        votedArena = true;
                    } else {
                        i++;
                    }
                }
            }
        } else if (isForcearena() && !voted.isEmpty() || isForcearena() && voted.isEmpty()) {
            winner = getVote();
            BukkitCloudNetHelper.setExtra(getVotedArena());
            for (Player all : pgPlayers) {
                all.sendMessage(prefix + ChatColor.AQUA + getVotedArena() + ChatColor.GREEN + " " + chat.get(7));
            }
        } else if (voted.isEmpty() && !isForcearena()) {
            String arenaName = null;
            while (arenaName == null) {
                Random rnd = new Random();
                int rndArena = rnd.nextInt(arenas.size() + 1);
                winner = String.valueOf(rndArena);
                arenaName = getConfig().getString("pg.arenas." + winner + ".name");
                if (arenaName != null) {
                    BukkitCloudNetHelper.setExtra(winner);
                    for (Player all : pgPlayers) {
                        all.sendMessage(prefix + ChatColor.AQUA + getConfig().get("pg.arenas." + winner + ".name") + ChatColor.GREEN + " " + chat.get(7));
                    }
                }
            }
        }
        setVote(winner);
    }

    public void teleportAndStart() {
        for (int i = 1; i <= pgPlayers.size(); i++) {
            Player p = pgPlayers.get(i - 1);
            try {
                if (startOnJoin) {
                    String vote = getVote();
                    Location loc = getConfig().getLocation("pg.arenas." + vote + ".spawns." + i);
                    assert loc != null;
                    p.teleport(loc);
                } else {
                    Location loc = getConfig().getLocation("pg.arenas." + arenaID + ".spawns." + i);
                    assert loc != null;
                    p.teleport(loc);
                }
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
        pgPlayers.add(p);
        addPlayerAmount();
        if (joinable && startOnJoin) {
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
            ItemStack votepaper = new ItemStack(Material.PAPER);
            ItemMeta votepapaermeta = votepaper.getItemMeta();
            assert votepapaermeta != null;
            votepapaermeta.setDisplayName(ChatColor.DARK_AQUA + chat.get(14));
            votepaper.setItemMeta(votepapaermeta);
            p.getInventory().setItem(8, votepaper);
        } else if (!startOnJoin) {
            try {
                Location loc = (Location) getConfig().get("pg.arenas." + arenaID + ".Lobby");
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
        } else {
            p.kickPlayer(prefix + chat.get(21));
        }
    }

    public void onLeave(Player p) {
        if (pgPlayers.contains(p)) {
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
            //arenas.remove();
        }
    }

    public void ConnectMySQL() {
        update("CREATE TABLE IF NOT EXISTS Stats(UUID varchar(64), WINS int, LOSTS int, KILLS int, DEATHS int, KD double);");
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
            update("INSERT INTO Stats(UUID, WINS, LOSTS, KILLS, DEATHS, KD) VALUES ('" + uuid + "', '0', '0', '0', '0', '0');");
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
        } else {
            createPlayer(uuid);
            addWins(uuid, wins);
        }
    }

    public void addLosts(String uuid, int losts) {
        if (playerExists(uuid)) {
            setLosts(uuid, (getLosts(uuid) + losts));
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

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setJoinable(boolean joinable) {
        this.joinable = joinable;
    }

    public int getCountdown() {
        return countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
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

    public HashMap<Location, Block> getLiquidPlaced() {
        return liquidPlaced;
    }

    public boolean isStartOnJoin() {
        return startOnJoin;
    }

    public ArrayList<Player> getChannel(Player player) {
        String channelName = playerChannel.get(player);
        return channels.get(channelName);
    }

    public void setArenaID(String arenaID) {
        this.arenaID = arenaID;
    }

}
