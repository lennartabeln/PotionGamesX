package com.tw0far.potiongames.models;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.tw0far.potiongames.PotionGamesX;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class Lobby {
    private static final Logger LOGGER = Logger.getLogger("Minecraft");
    private int id;
    private Location spawn;
    private final LobbyConfig lobbyConfig;

    private boolean enabled = false;
    private boolean activateTeams = true;
    private boolean activateKits = true;
    private boolean activateShop = true;
    private boolean activateAirdrops = true;
    private int roundTime = 30;
    private int maxPlayers = 24;
    private int minPlayers = 2;
    private int teamSize = 2;
    private int teamAmount = teamSize > 0 ? maxPlayers / teamSize : maxPlayers;
    
    private int playerCount = 0;
    private ArrayList<Participant> participants = new ArrayList<>();
    private ArrayList<Player> activePlayers = new ArrayList<>();
    private ArrayList<Player> spectatorPlayers = new ArrayList<>();
    private HashMap<Player, Integer> playerKills = new HashMap<>();
    private HashMap<Player, Integer> playerDeaths = new HashMap<>();
    private ArrayList<Arena> arenas = new ArrayList<>();
    private Arena currentArena = null;
    private GameStates state = GameStates.WAITING;
    private Sign joinSign = null;

    private ScheduledTask tickTask = null;
    private int endingTimer = 10;
    private int prepareTimer = 60;
    private int gameTimer = roundTime * 60;
    private int[] announceRoundTimes = new int[] { 0, 1, 2, 3, 4, 5, 10, 30, 60, 60 * 5, 60 * 10, 60 * 30 };
    
    // ===== PHASE 7.2: Runtime State (Countdown, Flags, etc.) =====
    // Countdown timers
    private int countdown = 60;
    private int reset = 10;
    
    // Runtime boolean flags (per-lobby state)
    private boolean deathmatch = false;
    private boolean move = true;
    private boolean joinable = true;
    private boolean forcearena = false;
    private boolean voteallowed = false;
    private boolean teamallowed = false;
    private boolean kitallowed = false;
    private boolean tickstarted = false;
    private boolean build = false;
    private boolean pause = false;
    private boolean checkArenas = false;
    private boolean singleArena = false;
    
    // Per-lobby chests
    private HashMap<Location, ItemStack[]> chests = new HashMap<>();
    
    // Per-lobby block tracking
    private HashMap<Location, java.lang.Object> placedBlocks = new HashMap<>();    // Location -> Material
    private HashMap<Location, java.lang.Object> breakedBlocks = new HashMap<>();    // Location -> Material
    private HashMap<Location, Object> waterBlocks = new HashMap<>();               // Location -> BlockData
    private HashMap<Location, Object> liquidPlaced = new HashMap<>();              // Location -> Block
    
    // Per-lobby voting
    private HashMap<String, Integer> lobbyvotes = new HashMap<>();                 // arena -> count
    private HashMap<Player, String> lobbyvoteplayernames = new HashMap<>();        // player -> voted arena
    private HashMap<Player, String> lobbyVoted = new HashMap<>();                  // player -> vote status
    private String lobbyVote = null;                                              // current vote in progress
    private String lobbyVotedArena = null;                                        // final voted arena
    
    // Per-lobby teams
    private HashMap<Integer, Integer> lobbyteams = new HashMap<>();               // team id -> player count
    private HashMap<Player, String> lobbyteamplayernames = new HashMap<>();       // player -> team id
    private HashMap<Player, String> lobbyTeamed = new HashMap<>();                // player -> team status
    private int lobbyteamSize = teamSize;                                         // team size
    private int lobbyteamAmount = teamAmount;                                     // number of teams

    public Lobby(int id) {
        this.id = id;
        this.lobbyConfig = null;
    }
    
    /**
     * Create a Lobby with configuration management via LobbyConfig.
     * This constructor enables per-lobby setting overrides with global defaults.
     * 
     * @param id The lobby ID
     * @param lobbyConfig The configuration for this lobby (with inheritance from global defaults)
     */
    public Lobby(int id, LobbyConfig lobbyConfig) {
        this.id = id;
        this.lobbyConfig = lobbyConfig;
        // Sync runtime fields from LobbyConfig for existing lobby accessors.
        this.countdown = lobbyConfig.getCountdown();
        this.maxPlayers = lobbyConfig.getMaxPlayers();
        this.minPlayers = lobbyConfig.getMinPlayers();
        this.teamSize = lobbyConfig.getTeamSize();
        this.roundTime = lobbyConfig.getRoundTime();
        this.activateTeams = lobbyConfig.isActivateTeams();
        this.activateKits = lobbyConfig.isActivateKits();
        this.activateShop = lobbyConfig.isActivateShop();
        this.activateAirdrops = lobbyConfig.isActivateAirdrops();
        this.teamAmount = maxPlayers / teamSize;
    }

    public Arena getArena(String name) {
        for (Arena arena : arenas) {
            if (arena.getName().equalsIgnoreCase(name)) {
                return arena;
            }
        }
        return null;
    }

    public boolean enable() {
        enabled = !enabled;
        Settings.lobbies.set("pg.lobbies." + id + ".enabled", enabled);
        try {
            Settings.lobbies.save(Settings.lobbiesFile);
            return true;
        } catch (Exception ex) {
            PotionGamesX.getInstance().getLogger().warning(ex.getMessage());
            return false;
        }
    }

    public void load() {
        arenas.clear();
        
        // Load configuration from LobbyConfig if available
        if (lobbyConfig != null) {
            // Configuration is already loaded in the constructor
            activateTeams = lobbyConfig.isActivateTeams();
            activateKits = lobbyConfig.isActivateKits();
            activateShop = lobbyConfig.isActivateShop();
            activateAirdrops = lobbyConfig.isActivateAirdrops();
            roundTime = lobbyConfig.getRoundTime();
            maxPlayers = lobbyConfig.getMaxPlayers();
            minPlayers = lobbyConfig.getMinPlayers();
            teamSize = lobbyConfig.getTeamSize();
            countdown = lobbyConfig.getCountdown();
        } else {
            // Fallback loading directly from arena-data.yml when no LobbyConfig is provided.
            activateTeams = Settings.lobbies.getBoolean("pg.lobbies." + id + ".activateTeams");
            activateKits = Settings.lobbies.getBoolean("pg.lobbies." + id + ".activateKits");
            activateShop = Settings.lobbies.getBoolean("pg.lobbies." + id + ".activateShop");
            activateAirdrops = Settings.lobbies.getBoolean("pg.lobbies." + id + ".activateAirdrops");
            roundTime = Settings.lobbies.getInt("pg.lobbies." + id + ".roundTime");
            maxPlayers = Settings.lobbies.getInt("pg.lobbies." + id + ".maxPlayers");
            minPlayers = Settings.lobbies.getInt("pg.lobbies." + id + ".minPlayers");
            teamSize = Settings.lobbies.getInt("pg.lobbies." + id + ".teamSize");
        }
        
        enabled = Settings.lobbies.getBoolean("pg.lobbies." + id + ".enabled");
        spawn = Settings.lobbies.getLocation("pg.lobbies." + id + ".spawn");
        joinSign = null;
        if (Settings.lobbies.contains("pg.lobbies." + id + ".joinSign")) {
            Location signLoc = Settings.lobbies.getLocation("pg.lobbies." + id + ".joinSign");
            if (signLoc != null && signLoc.getBlock().getState() instanceof Sign) {
                joinSign = (Sign) signLoc.getBlock().getState();
            }
        }
        updateJoinSign();

        if (Settings.lobbies.contains("pg.lobbies." + id + ".arenas")) {
            for (String key : Settings.lobbies.getConfigurationSection("pg.lobbies." + id + ".arenas").getKeys(false)) {
                Arena arena = new Arena(key, id, lobbyConfig);
                arena.load();
                arenas.add(arena);
            }
        }
    }

    public boolean add(Location spawn) {
        this.spawn = spawn;
        Settings.lobbies.set("pg.lobbies." + id + ".enabled", enabled);
        Settings.lobbies.set("pg.lobbies." + id + ".spawn", spawn);
        Settings.lobbies.set("pg.lobbies." + id + ".activateTeams", activateTeams);
        Settings.lobbies.set("pg.lobbies." + id + ".activateKits", activateKits);
        Settings.lobbies.set("pg.lobbies." + id + ".activateShop", activateShop);
        Settings.lobbies.set("pg.lobbies." + id + ".activateAirdrops", activateAirdrops);
        Settings.lobbies.set("pg.lobbies." + id + ".roundTime", roundTime);
        Settings.lobbies.set("pg.lobbies." + id + ".maxPlayers", maxPlayers);
        Settings.lobbies.set("pg.lobbies." + id + ".minPlayers", minPlayers);
        Settings.lobbies.set("pg.lobbies." + id + ".teamSize", teamSize);
        try {
            Settings.lobbies.save(Settings.lobbiesFile);
            return true;
        } catch (Exception ex) {
            PotionGamesX.getInstance().getLogger().warning(ex.getMessage());
            return false;
        }
    }

    public boolean remove() {
        Settings.lobbies.set("pg.lobbies." + id, null);
        try {
            Settings.lobbies.save(Settings.lobbiesFile);
            return true;
        } catch (Exception ex) {
            PotionGamesX.getInstance().getLogger().warning(ex.getMessage());
            return false;
        }
    }

    public boolean addArena(String name) {
        Arena arena = new Arena(name, id);
        boolean success = arena.add();
        if (success) {
            arenas.add(arena);
        }
        return success;
    }

    public boolean removeArena(String name) {
        Arena arena = getArena(name);
        if (arena != null) {
            boolean success = arena.remove();
            if (success) {
                arenas.remove(arena);
            }
            return success;
        }
        return false;
    }

    public void addParticipant(Player p) {
        if (participants.size() < maxPlayers && getParticipant(p) == null) {
            Participant participant = new Participant(this, p);
            participants.add(participant);
            participant.teleportToLobby();
            setPlayerCount(playerCount + 1);
        }
        if (tickTask == null || tickTask.isCancelled()) {
            startTick();
        }
    }

    public void removeParticipant(Player p) {
        Participant participant = getParticipant(p);
        if (participant != null) {
            participants.remove(participant);
            participant.teleportToOriginalLocation();
            setPlayerCount(playerCount - 1);
        }
        if (playerCount == 0) {
            setState(GameStates.RESET);
        }
    }
    
    public Participant getParticipant(Player p) {
        if (p == null || participants == null) return null;
        return participants.stream()
                .filter(part -> part.getPlayer() != null
                        && part.getPlayer().getUniqueId().equals(p.getUniqueId()))
                .findFirst().orElse(null);
    }

    public void clearParticipants() {
        for (Participant part : participants) {
            part.teleportToOriginalLocation();
        }
        participants.clear();
        activePlayers.clear();
        spectatorPlayers.clear();
        playerKills.clear();
        playerDeaths.clear();
        setPlayerCount(0);
    }

    public boolean containsPlayer(Player player) {
        return isActivePlayer(player) || isSpectatorPlayer(player) || getParticipant(player) != null;
    }

    public ArrayList<Player> getActivePlayers() {
        return activePlayers;
    }

    public ArrayList<Player> getSpectatorPlayers() {
        return spectatorPlayers;
    }

    public void addActivePlayer(Player player) {
        if (player == null) {
            return;
        }
        spectatorPlayers.remove(player);
        if (!activePlayers.contains(player)) {
            activePlayers.add(player);
        }
        if (getParticipant(player) == null) {
            addParticipant(player);
        }
    }

    public void removeActivePlayer(Player player) {
        if (player == null) {
            return;
        }
        activePlayers.remove(player);
        removeParticipant(player);
    }

    public void addSpectatorPlayer(Player player) {
        if (player == null) {
            return;
        }
        activePlayers.remove(player);
        if (!spectatorPlayers.contains(player)) {
            spectatorPlayers.add(player);
        }
        removeParticipant(player);
    }

    public void removeSpectatorPlayer(Player player) {
        if (player == null) {
            return;
        }
        spectatorPlayers.remove(player);
    }

    public boolean isActivePlayer(Player player) {
        return player != null && activePlayers.contains(player);
    }

    public boolean isSpectatorPlayer(Player player) {
        return player != null && spectatorPlayers.contains(player);
    }

    public void addKill(Player player) {
        if (player != null) {
            playerKills.put(player, playerKills.getOrDefault(player, 0) + 1);
        }
    }

    public void addDeath(Player player) {
        if (player != null) {
            playerDeaths.put(player, playerDeaths.getOrDefault(player, 0) + 1);
        }
    }

    public int getKills(Player player) {
        return player == null ? 0 : playerKills.getOrDefault(player, 0);
    }

    public int getDeaths(Player player) {
        return player == null ? 0 : playerDeaths.getOrDefault(player, 0);
    }

    public void join(Player p) {
        if (!activePlayers.contains(p)) {
            activePlayers.add(p);
        }
        addParticipant(p);
    }

    public void leave(Player p) {
        activePlayers.remove(p);
        spectatorPlayers.remove(p);
        removeParticipant(p);
    }

    public boolean setJoinSign(Location loc) {
        BlockState b = loc.getBlock().getState();
        joinSign = (Sign) b;
        Settings.lobbies.set("pg.lobbies." + id + ".joinSign", loc);
        try {
            Settings.lobbies.save(Settings.lobbiesFile);
            updateJoinSign();
            return true;
        } catch (Exception ex) {
            PotionGamesX.getInstance().getLogger().warning(ex.getMessage());
            return false;
        }
    }

    public void updateJoinSign() {
        if (joinSign != null) {
            Location signLoc = joinSign.getLocation();
            if (signLoc != null && signLoc.getBlock().getState() instanceof Sign) {
                Sign sign = (Sign) signLoc.getBlock().getState();
                sign.getSide(Side.FRONT).line(0, Component.text(id));
                if (state == GameStates.WAITING || state == GameStates.PREPARING) {
                    sign.getSide(Side.FRONT).line(1, Component.text(state.toString()).color(NamedTextColor.GREEN));
                } else {
                    sign.getSide(Side.FRONT).line(1, Component.text(state.toString()).color(NamedTextColor.RED));
                }
                if (currentArena != null) {
                    sign.getSide(Side.FRONT).line(2, Component.text(currentArena.getName()).color(NamedTextColor.GOLD));
                } else {
                    sign.getSide(Side.FRONT).line(2, Component.text("VOTING").color(NamedTextColor.AQUA));
                }
                sign.getSide(Side.FRONT).line(3, Component.text("[" + playerCount + "/" + maxPlayers + "]").color(NamedTextColor.GRAY));
                sign.update();
                joinSign = sign;
            }
        }
    }

    private Arena getVotedArena() {
        Arena topArena = arenas.get((int)(Math.random() * arenas.size()));
        int topVotes = 0;
        for (Arena arena : arenas) {
            int votes = 0;
            for (Participant participant : participants) {
                Arena votedArena = participant.getVotedArena();
                if (votedArena != null && votedArena.equals(arena)) {
                    votes++;
                }
            }
            if (votes > topVotes) {
                topArena = arena;
                topVotes = votes;
            }
        }
        return topArena;
    }

    public void startTick() {
        tickTask = PotionGamesX.getInstance().getServer().getGlobalRegionScheduler().runAtFixedRate(PotionGamesX.getInstance(), scheduledTask -> runGameTick(), 1, 20);
    }

    public void startCountdown() {
        if (state == GameStates.WAITING) {
            prepareTimer = Math.max(1, countdown);
            setState(GameStates.PREPARING);
            if (tickTask == null || tickTask.isCancelled()) {
                startTick();
            }
        }
    }

    public void cancelCountdown() {
        if (state == GameStates.PREPARING) {
            prepareTimer = Math.max(1, countdown);
            setState(GameStates.WAITING);
        }
    }

    public void pauseGame() {
        pause = true;
    }

    public void resumeGame() {
        pause = false;
    }

    public void selectArena(Arena arena) {
        if (arena != null && arenas.contains(arena)) {
            setCurrentArena(arena);
            lobbyVotedArena = arena.getName();
        }
    }

    public void recordVote(Player player, String arenaName) {
        if (player == null || arenaName == null || arenaName.isBlank()) {
            return;
        }

        String oldVote = lobbyvoteplayernames.get(player);
        if (oldVote != null) {
            int oldVotes = lobbyvotes.getOrDefault(oldVote, 0);
            if (oldVotes > 0) {
                lobbyvotes.put(oldVote, oldVotes - 1);
            }
        }

        lobbyvoteplayernames.put(player, arenaName);
        lobbyVoted.put(player, "true");
        lobbyvotes.put(arenaName, lobbyvotes.getOrDefault(arenaName, 0) + 1);

        Participant participant = getParticipant(player);
        Arena arena = getArena(arenaName);
        if (arena != null) {
            arena.recordVote(player);
            if (participant != null) {
                participant.setVotedArena(arena);
            }
        } else if (participant != null) {
            participant.setVotedArena(null);
        }
    }

    public void recordTeamAssignment(Player player, String teamName) {
        if (player == null || teamName == null || teamName.isBlank()) {
            return;
        }

        String previousTeam = lobbyteamplayernames.get(player);
        if (previousTeam != null) {
            try {
                decrementTeamCount(Integer.parseInt(previousTeam));
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "[PotionGamesX] Invalid team ID", e);
            }
        }

        lobbyteamplayernames.put(player, teamName);
        lobbyTeamed.put(player, "true");

        try {
            incrementTeamCount(Integer.parseInt(teamName));
        } catch (NumberFormatException ignored) {
        }
    }

    public void activateTeam(Player player, String teamName) {
        recordTeamAssignment(player, teamName);
    }

    public List<Player> getTeamMembers(String teamName) {
        if (teamName == null) {
            return Collections.emptyList();
        }
        List<Player> members = new ArrayList<>();
        for (Map.Entry<Player, String> entry : lobbyteamplayernames.entrySet()) {
            if (teamName.equals(entry.getValue())) {
                members.add(entry.getKey());
            }
        }
        return members;
    }

    public void distributeTeams() {
        if (!activateTeams || activePlayers.isEmpty()) {
            return;
        }

        clearTeams();
        int computedTeamSize = Math.max(1, teamSize);
        int computedTeamAmount = Math.max(1, (int) Math.ceil((double) activePlayers.size() / computedTeamSize));
        lobbyteamSize = computedTeamSize;
        lobbyteamAmount = computedTeamAmount;

        for (int i = 1; i <= lobbyteamAmount; i++) {
            lobbyteams.put(i, 0);
        }

        int teamId = 1;
        for (Player player : new ArrayList<>(activePlayers)) {
            while (lobbyteams.getOrDefault(teamId, 0) >= lobbyteamSize) {
                teamId++;
                if (teamId > lobbyteamAmount) {
                    teamId = 1;
                }
            }
            recordTeamAssignment(player, Integer.toString(teamId));
            teamId++;
            if (teamId > lobbyteamAmount) {
                teamId = 1;
            }
        }
    }

    public void endRound() {
        setState(GameStates.ENDING);
        endingTimer = Math.max(1, reset);
    }

    public void runGameTick() {
        if (pause) {
            return;
        }

        switch (state) {
            case WAITING:
                for (Participant participant : participants) {
                    participant.sendActionBar(Messages.GameWaiting(playerCount, minPlayers));
                }
                if (playerCount >= minPlayers) {
                    prepareTimer = Math.max(1, countdown);
                    setState(GameStates.PREPARING);
                }
                break;
            case PREPARING:
                for (Participant participant : participants) {
                    participant.sendActionBar(Messages.GameStartsIn(prepareTimer));
                }
                if (prepareTimer <= 0) {
                    if (currentArena == null) {
                        setCurrentArena(getVotedArena());
                    }
                    if (currentArena != null) {
                        distributeTeams();
                        currentArena.teleport(participants);
                    }
                    gameTimer = Math.max(1, roundTime * 60);
                    deathmatch = false;
                    setState(GameStates.INGAME);
                } else if (prepareTimer == countdown) {
                    if (currentArena == null) {
                        setCurrentArena(getVotedArena());
                    }
                    if (currentArena != null) {
                        for (Participant participant : participants) {
                            participant.sendMessage(Messages.WillBePlayed(currentArena.getName()));
                        }
                    }
                }
                if (playerCount < minPlayers) {
                    cancelCountdown();
                    break;
                }
                prepareTimer--;
                break;
            case INGAME:
                if (gameTimer >= 0 && Arrays.stream(announceRoundTimes).anyMatch(t -> t == gameTimer)) {
                    for (Participant participant : participants) {
                        if (gameTimer <= 60) {
                            participant.sendMessage(Messages.RoundSecondsRemaining(gameTimer));
                        } else {
                            participant.sendMessage(Messages.RoundMinutesRemaining(gameTimer / 60));
                        }
                    }
                } else if (gameTimer < 0) {
                    setState(GameStates.ENDING);
                    endingTimer = Math.max(1, reset);
                }
                checkWinCondition();
                gameTimer--;
                break;
            case DEATHMATCH:
                if (gameTimer >= 0 && Arrays.stream(announceRoundTimes).anyMatch(t -> t == gameTimer)) {
                    for (Participant participant : participants) {
                        participant.sendMessage(Messages.DeathmatchStartingIn(gameTimer));
                    }
                } else if (gameTimer < 0) {
                    announceDraw();
                    setState(GameStates.ENDING);
                    endingTimer = Math.max(1, reset);
                }
                checkWinCondition();
                gameTimer--;
                break;
            case ENDING:
                for (Participant participant : participants) {
                    participant.sendActionBar(Messages.TeleportToLobbyIn(endingTimer));
                }
                if (endingTimer <= 0) {
                    setState(GameStates.RESET);
                }
                endingTimer--;
                break;
            case RESET:
                restoreBlocks();
                for (Player player : new ArrayList<>(activePlayers)) {
                    sendToServer(player, PotionGamesX.getInstance().getConfig().getString("pg.bungeeServer", "hub"));
                }
                for (Player player : new ArrayList<>(spectatorPlayers)) {
                    sendToServer(player, PotionGamesX.getInstance().getConfig().getString("pg.bungeeServer", "hub"));
                }
                PotionGamesX.getInstance().getGame().clearAllPlayers();
                setCurrentArena(null);
                clearVoting();
                clearArenaVotes();
                clearBlockTracking();
                clearChests();
                setState(GameStates.WAITING);
                if (playerCount == 0 && tickTask != null && !tickTask.isCancelled()) {
                    tickTask.cancel();
                    tickTask = null;
                }
                break;
            default:
                break;
        }

        if (playerCount == 0 && state != GameStates.WAITING) {
            setState(GameStates.RESET);
        }
    }

    private void checkWinCondition() {
        int aliveCount = 0;
        Player lastAlive = null;
        for (Player player : activePlayers) {
            if (player != null && player.isOnline() && player.getGameMode() != org.bukkit.GameMode.SPECTATOR) {
                aliveCount++;
                lastAlive = player;
            }
        }
        if (aliveCount <= 1 && lastAlive != null) {
            announceWinner(lastAlive);
            endRound();
        } else if (aliveCount <= 1) {
            announceDraw();
            endRound();
                } else if (aliveCount == 2 && PotionGamesX.getInstance().getConfigManager().isActivateDeathmatch() && !deathmatch) {
            activateDeathmatch();
        }
    }

    private void activateDeathmatch() {
        deathmatch = true;
        for (Player player : new ArrayList<>(activePlayers)) {
            if (currentArena != null) {
                Location spawn = currentArena.getRandomDeathmatchSpawn();
                if (spawn != null) {
                    player.teleport(spawn);
                }
            }
        }
        for (Participant participant : participants) {
            participant.sendMessage(Messages.DeathmatchStarted());
        }
        gameTimer = 60;
        setState(GameStates.DEATHMATCH);
    }

    private void announceWinner(Player winner) {
        String winnerName = winner == null ? "Unknown" : winner.getName();
        for (Participant participant : participants) {
            participant.sendMessage(Messages.WinnerHasWonTheGame(winnerName));
        }
        for (Player player : activePlayers) {
            if (player != null && player.equals(winner)) {
                PotionGamesX plugin = PotionGamesX.getInstance();
                if (plugin.getConfigManager().isEnableRewards()) {
                    player.sendMessage(Messages.WinReward(plugin.getConfigManager().getWinningReward()));
                }
                plugin.getDatabaseManager().addWins(player.getUniqueId().toString(), 1);
            } else if (player != null) {
                PotionGamesX plugin = PotionGamesX.getInstance();
                plugin.getDatabaseManager().addLosses(player.getUniqueId().toString(), 1);
            }
        }
    }

    private void announceDraw() {
        for (Participant participant : participants) {
            participant.sendMessage(Messages.RoundNobodyWon());
        }
    }

    private void restoreBlocks() {
        for (Map.Entry<Location, Object> entry : new HashMap<>(placedBlocks).entrySet()) {
            Location loc = entry.getKey();
            if (entry.getValue() instanceof Material) {
                loc.getBlock().setType((Material) entry.getValue());
            }
        }
        for (Map.Entry<Location, Object> entry : new HashMap<>(breakedBlocks).entrySet()) {
            Location loc = entry.getKey();
            if (entry.getValue() instanceof Material) {
                loc.getBlock().setType((Material) entry.getValue());
            }
        }
        for (Map.Entry<Location, Object> entry : new HashMap<>(waterBlocks).entrySet()) {
            Location loc = entry.getKey();
            if (entry.getValue() instanceof org.bukkit.block.data.BlockData) {
                loc.getBlock().setBlockData((org.bukkit.block.data.BlockData) entry.getValue());
            }
        }
        placedBlocks.clear();
        breakedBlocks.clear();
        waterBlocks.clear();
        liquidPlaced.clear();
    }

    public void sendToServer(Player player, String server) {
        if (player == null || !player.isOnline()) return;
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(PotionGamesX.getInstance(), "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            player.kick(Component.text("Game finished! Connecting to " + server + "...").color(NamedTextColor.GREEN));
        }
    }

    public void addPlacedBlock(Location location, Object material) {
        if (location != null && material != null) {
            placedBlocks.put(location, material);
        }
    }

    public int getId() {
        return this.id;
    }

    public Location getSpawn() {
        return this.spawn;
    }

    public ArrayList<Participant> getParticipants() {
        return this.participants;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setState(GameStates newState) {
        if (this.state != newState) {
            GameStates oldState = this.state;
            this.state = newState;
            updateJoinSign();
            
            PotionGamesX plugin = PotionGamesX.getInstance();
            
            // Game just ended (transitioned to RESET) - move players via BungeeCord
            if (newState == GameStates.RESET && oldState != GameStates.RESET && plugin.getConfigManager().isGameServer()) {
                PotionGamesX.getInstance().getComponentLogger().info(
                    com.tw0far.potiongames.models.Settings.prefix
                        .append(Component.text("Game finished. Sending players back to hub...").color(NamedTextColor.YELLOW)));
            }
        }
    }

    public GameStates getState() {
        return this.state;
    }

    public void setCurrentArena(Arena arena) {
        if (this.currentArena != arena) {
            this.currentArena = arena;
            updateJoinSign();
        }
    }

    public Arena getCurrentArena() {
        return this.currentArena;
    }

    public void setPlayerCount(int count) {
        if (this.playerCount != count) {
            this.playerCount = count;
            updateJoinSign();
        }
    }

    public int getPlayerCount() {
        return this.playerCount;
    }
    
    public int getMinPlayers() {
        return this.minPlayers;
    }
    
    // ===== PHASE 7.2: Runtime State Accessors =====
    
    /**
     * Get countdown timer
     */
    public int getCountdown() {
        return countdown;
    }
    
    /**
     * Set countdown timer
     */
    public void setCountdown(int countdown) {
        this.countdown = countdown;
        this.prepareTimer = countdown;
    }
    
    /**
     * Get reset timer
     */
    public int getReset() {
        return reset;
    }
    
    /**
     * Set reset timer
     */
    public void setReset(int reset) {
        this.reset = reset;
        this.endingTimer = reset;
    }

    public int getPrepareTimer() {
        return prepareTimer;
    }

    public void setPrepareTimer(int prepareTimer) {
        this.prepareTimer = prepareTimer;
    }

    public int getGameTimer() {
        return gameTimer;
    }

    public void setGameTimer(int gameTimer) {
        this.gameTimer = gameTimer;
    }

    public int getEndingTimer() {
        return endingTimer;
    }

    public void setEndingTimer(int endingTimer) {
        this.endingTimer = endingTimer;
    }
    
    /**
     * Check if teams are activated for this lobby
     */
    public boolean isActivateTeams() {
        return activateTeams;
    }
    
    /**
     * Set teams activation for this lobby
     */
    public void setActivateTeams(boolean value) {
        this.activateTeams = value;
    }
    
    /**
     * Check if kits are activated for this lobby
     */
    public boolean isActivateKits() {
        return activateKits;
    }
    
    /**
     * Set kits activation for this lobby
     */
    public void setActivateKits(boolean value) {
        this.activateKits = value;
    }
    
    /**
     * Check if airdrops are activated for this lobby
     */
    public boolean isActivateAirdrops() {
        return activateAirdrops;
    }
    
    /**
     * Set airdrops activation for this lobby
     */
    public void setActivateAirdrops(boolean value) {
        this.activateAirdrops = value;
    }

    /**
     * Check if shop is activated for this lobby
     */
    public boolean isActivateShop() {
        return activateShop;
    }
    
    /**
     * Set shop activation for this lobby
     */
    public void setActivateShop(boolean value) {
        this.activateShop = value;
    }

    
    // ===== PHASE 7.2: Boolean Flags =====
    
    public boolean isDeathmatch() { return deathmatch; }
    public void setDeathmatch(boolean value) { this.deathmatch = value; }
    
    public boolean isMoveAllowed() { return move; }
    public void setMoveAllowed(boolean value) { this.move = value; }
    
    public boolean isJoinable() { return joinable; }
    public void setJoinable(boolean value) { this.joinable = value; }
    
    public boolean isForcearena() { return forcearena; }
    public void setForcearena(boolean value) { this.forcearena = value; }
    
    public boolean isVoteallowed() { return voteallowed; }
    public void setVoteallowed(boolean value) { this.voteallowed = value; }
    
    public boolean isTeamallowed() { return teamallowed; }
    public void setTeamallowed(boolean value) { this.teamallowed = value; }
    
    public boolean isKitallowed() { return kitallowed; }
    public void setKitallowed(boolean value) { this.kitallowed = value; }
    
    public boolean isTickstarted() { return tickstarted; }
    public void setTickstarted(boolean value) { this.tickstarted = value; }
    
    public boolean isBuildAllowed() { return build; }
    public void setBuildAllowed(boolean value) { this.build = value; }
    
    public boolean isPaused() { return pause; }
    public void setPaused(boolean value) { this.pause = value; }
    
    public boolean isCheckArenas() { return checkArenas; }
    public void setCheckArenas(boolean value) { this.checkArenas = value; }
    
    public boolean isSingleArena() { return singleArena; }
    public void setSingleArena(boolean value) { this.singleArena = value; }
    
    // ===== PHASE 7.2: Chest Accessors =====
    
    /**
     * Get chests map for this lobby
     */
    public HashMap<Location, ItemStack[]> getChests() {
        return chests;
    }
    
    /**
     * Store chest inventory at location
     */
    public void setChestInventory(Location loc, ItemStack[] items) {
        chests.put(loc, items);
    }
    
    /**
     * Get chest inventory at location
     */
    public ItemStack[] getChestInventory(Location loc) {
        return chests.get(loc);
    }
    
    /**
     * Check if location has chest inventory stored
     */
    public boolean hasChestInventory(Location loc) {
        return chests.containsKey(loc);
    }
    
    /**
     * Remove chest inventory at location
     */
    public void removeChestInventory(Location loc) {
        chests.remove(loc);
    }
    
    /**
     * Clear all chests in this lobby
     */
    public void clearChests() {
        chests.clear();
    }

    
    // ===== PHASE 7.2: Block Tracking Accessors =====
    
    /**
     * Get placed blocks map
     */
    public HashMap<Location, java.lang.Object> getPlacedBlocks() {
        return placedBlocks;
    }
    
    /**
     * Get broken blocks map
     */
    public HashMap<Location, java.lang.Object> getBreakeedBlocks() {
        return breakedBlocks;
    }
    
    /**
     * Get water blocks map
     */
    public HashMap<Location, Object> getWaterBlocks() {
        return waterBlocks;
    }
    
    /**
     * Get liquid placed map
     */
    public HashMap<Location, Object> getLiquidPlaced() {
        return liquidPlaced;
    }
    
    /**
     * Clear all block tracking
     */
    public void clearBlockTracking() {
        placedBlocks.clear();
        breakedBlocks.clear();
        waterBlocks.clear();
        liquidPlaced.clear();
    }
    
    // ===== PHASE 7.2: Voting Accessors =====
    
    /**
     * Get voting map (arena -> votes)
     */
    public HashMap<String, Integer> getVotingMap() {
        return lobbyvotes;
    }
    
    /**
     * Get player vote player names map
     */
    public HashMap<Player, String> getVotePlayerNamesMap() {
        return lobbyvoteplayernames;
    }
    
    /**
     * Get player voted map
     */
    public HashMap<Player, String> getVotedMap() {
        return lobbyVoted;
    }
    
    /**
     * Get current vote
     */
    public String getCurrentVote() {
        return lobbyVote;
    }
    
    /**
     * Set current vote
     */
    public void setCurrentVote(String vote) {
        this.lobbyVote = vote;
    }
    
    /**
     * Get voted arena
     */
    public String getVotedArenaName() {
        return lobbyVotedArena;
    }
    
    /**
     * Set voted arena
     */
    public void setVotedArenaName(String arena) {
        this.lobbyVotedArena = arena;
    }
    
    /**
     * Clear all voting data
     */
    public void clearVoting() {
        lobbyvotes.clear();
        lobbyvoteplayernames.clear();
        lobbyVoted.clear();
        lobbyVote = null;
        lobbyVotedArena = null;
    }
    
    // ===== PHASE 7.2: Team Accessors =====
    
    /**
     * Get teams map (team id -> player count)
     */
    public HashMap<Integer, Integer> getTeamsMap() {
        return lobbyteams;
    }
    
    /**
     * Get team player names map (player -> team id)
     */
    public HashMap<Player, String> getTeamPlayerNamesMap() {
        return lobbyteamplayernames;
    }
    
    /**
     * Get teamed map (player -> team status)
     */
    public HashMap<Player, String> getTeamedMap() {
        return lobbyTeamed;
    }
    
    /**
     * Get team size
     */
    public int getTeamSize() {
        return lobbyteamSize;
    }
    
    /**
     * Set team size
     */
    public void setTeamSize(int size) {
        this.teamSize = size;
        this.lobbyteamSize = size;
    }
    
    /**
     * Get team amount
     */
    public int getTeamAmount() {
        return lobbyteamAmount;
    }
    
    /**
     * Set team amount
     */
    public void setTeamAmount(int amount) {
        this.teamAmount = amount;
        this.lobbyteamAmount = amount;
    }
    
    /**
     * Clear all team data
     */
    public void clearTeams() {
        lobbyteams.clear();
        lobbyteamplayernames.clear();
        lobbyTeamed.clear();
    }
    
    // ===== PHASE 7.5: Team Operations =====
    
    /**
     * Get player's team ID
     */
    public String getPlayerTeam(Player player) {
        return lobbyteamplayernames.get(player);
    }
    
    /**
     * Set player's team ID
     */
    public void setPlayerTeam(Player player, String teamId) {
        lobbyteamplayernames.put(player, teamId);
    }
    
    /**
     * Remove player from team
     */
    public void removePlayerTeam(Player player) {
        lobbyteamplayernames.remove(player);
    }
    
    /**
     * Increment team player count
     */
    public void incrementTeamCount(int teamId) {
        int count = lobbyteams.getOrDefault(teamId, 0);
        lobbyteams.put(teamId, count + 1);
    }
    
    /**
     * Decrement team player count
     */
    public void decrementTeamCount(int teamId) {
        int count = lobbyteams.getOrDefault(teamId, 0);
        if (count > 0) {
            count--;
            if (count == 0) {
                lobbyteams.remove(teamId);
            } else {
                lobbyteams.put(teamId, count);
            }
        }
    }
    
    // ===== PHASE 8.6: Arena Accessors =====
    
    /**
     * Get all arenas in this lobby.
     * 
     * @return A list of all arenas (may be empty)
     */
    public ArrayList<Arena> getArenas() {
        return new ArrayList<>(arenas);
    }
    
    /**
     * Get a random arena from available arenas.
     * 
     * @return A random arena, or null if no arenas exist
     */
    public Arena getRandomArena() {
        if (arenas.isEmpty()) {
            return null;
        }
        return arenas.get((int)(Math.random() * arenas.size()));
    }
    
    /**
     * Get the number of arenas in this lobby.
     * 
     * @return The arena count
     */
    public int getArenaCount() {
        return arenas.size();
    }
    
    /**
     * Check if an arena exists by name.
     * 
     * @param name The arena name
     * @return true if the arena exists
     */
    public boolean hasArena(String name) {
        return getArena(name) != null;
    }
    
    /**
     * Clear all votes for all arenas.
     * Call this at the start of a voting phase.
     */
    public void clearArenaVotes() {
        for (Arena arena : arenas) {
            arena.resetVotes();
        }
    }
}

