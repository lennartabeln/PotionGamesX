package com.tw0far.potiongames.models;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import com.tw0far.potiongames.PotionGamesX;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class Lobby {
    private static final Logger LOGGER = Logger.getLogger("Minecraft");
    private int id;
    private Location spawn;
    private final LobbyConfig lobbyConfig;

    private boolean activateTeams = true;
    private boolean activateKits = true;
    private boolean activateShop = true;
    private boolean activateAirdrops = true;
    private int roundTime = 1800; // seconds; loaded from LobbyConfig / lobbies.yml
    private int maxPlayers = 24;
    private int minPlayers = 2;
    private int teamSize = 2;

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
    private int gameTimer = roundTime;
    private int[] announceRoundTimes = new int[] { 0, 1, 2, 3, 4, 5, 10, 30, 60, 60 * 5, 60 * 10, 60 * 30 };

    // =====
    // Countdown timers
    private int countdown = 60;
    private int reset = 10;

    // Runtime boolean flags (per-lobby state)
    private boolean deathmatch = false;
    private boolean joinable = true;
    private boolean pause = false;

    /** Guards {@link #startTick()} against a second concurrent scheduling. */
    private boolean tickStarting = false;

    // Gamerule restore points (pg.changeGamerules)
    private World gameruleWorld = null;
    private Boolean prevDaylight = null;
    private Boolean prevWeather = null;
    private Boolean prevFireTick = null;
    private Boolean prevMobGriefing = null;
    private Boolean prevMobSpawning = null;

    // Per-lobby chests
    /**
     * Pre-join state of every spectator, restored when spectating ends. Dying
     * players keep the snapshot of their participant, late joiners get one on
     * the fly.
     */
    private final HashMap<UUID, PlayerState> spectatorStates = new HashMap<>();

    private HashMap<Location, ItemStack[]> chests = new HashMap<>();

    // Per-lobby block tracking (for restoring the arena at RESET)
    private HashMap<Location, Material> placedBlocks = new HashMap<>();
    private HashMap<Location, Material> brokenBlocks = new HashMap<>();
    private HashMap<Location, BlockData> liquidBlocks = new HashMap<>();

    // Per-lobby voting
    // Single source of truth for votes: ArenaStateManager (see recordVote/clearVoting).

    // Per-lobby teams
    private HashMap<Integer, Integer> lobbyteams = new HashMap<>();               // team id -> player count
    private HashMap<Player, String> lobbyteamplayernames = new HashMap<>();       // player -> team id

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
    }

    public Arena getArena(String name) {
        for (Arena arena : arenas) {
            if (arena.getName().equalsIgnoreCase(name)) {
                return arena;
            }
        }
        return null;
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
            roundTime = LobbyConfig.normalizeRoundTime(Settings.lobbies.getInt("pg.lobbies." + id + ".roundTime", 30));
            maxPlayers = Settings.lobbies.getInt("pg.lobbies." + id + ".maxPlayers");
            minPlayers = Settings.lobbies.getInt("pg.lobbies." + id + ".minPlayers");
            teamSize = Settings.lobbies.getInt("pg.lobbies." + id + ".teamSize");
        }

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
                Arena arena = new Arena(key, id);
                arena.load();
                arenas.add(arena);
            }
        }
        syncTeamSelector();
    }

    /**
     * Number of teams used by the round and the team selector. The legacy
     * release derived it from the lobby capacity ({@code maxPlayers / teamSize})
     * rather than from the players currently online, so a two-player round is a
     * 1v1 instead of both players sharing team 1.
     */
    public int getTeamAmount() {
        int size = Math.max(1, teamSize);
        int capacity = Math.max(1, Math.max(maxPlayers, minPlayers));
        return Math.max(2, capacity / size);
    }

    /**
     * Publishes team size and team slots to the selector state, including for
     * lobbies created at runtime (the selector used to stay empty until a
     * restart because the bootstrap only ran once).
     */
    public void syncTeamSelector() {
        PotionGamesX plugin = PotionGamesX.getInstance();
        if (plugin == null || plugin.getArenaStateManager() == null) {
            return;
        }
        var asm = plugin.getArenaStateManager();
        String lobbyId = Integer.toString(id);
        asm.setLobbyTeamSize(lobbyId, Math.max(1, teamSize));
        Map<Integer, Integer> existing = asm.getLobbyTeams(lobbyId);
        if (existing.isEmpty()) {
            asm.initializeLobbyTeams(lobbyId, getTeamAmount());
            return;
        }
        for (int teamId = 1; teamId <= getTeamAmount(); teamId++) {
            existing.putIfAbsent(teamId, 0);
        }
    }

    public boolean add(Location spawn) {
        this.spawn = spawn;
        Settings.lobbies.set("pg.lobbies." + id + ".spawn", spawn);
        Settings.lobbies.set("pg.lobbies." + id + ".activateTeams", activateTeams);
        Settings.lobbies.set("pg.lobbies." + id + ".activateKits", activateKits);
        Settings.lobbies.set("pg.lobbies." + id + ".activateShop", activateShop);
        Settings.lobbies.set("pg.lobbies." + id + ".activateAirdrops", activateAirdrops);
        // Persist minutes, the documented unit.
        Settings.lobbies.set("pg.lobbies." + id + ".roundTime", Math.max(1, roundTime / 60));
        Settings.lobbies.set("pg.lobbies." + id + ".maxPlayers", maxPlayers);
        Settings.lobbies.set("pg.lobbies." + id + ".minPlayers", minPlayers);
        Settings.lobbies.set("pg.lobbies." + id + ".teamSize", teamSize);
        try {
            Settings.lobbies.save(Settings.lobbiesFile);
            syncTeamSelector();
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
            PotionGamesX plugin = PotionGamesX.getInstance();
            if (plugin != null && plugin.getScoreboardManager() != null) {
                plugin.getScoreboardManager().show(p);
            }
        }
        if (tickTask == null || tickTask.isCancelled()) {
            startTick();
        }
    }

    public void removeParticipant(Player p) {
        clearPlayerVoteAndTeam(p);
        Participant participant = getParticipant(p);
        if (participant != null) {
            participants.remove(participant);
            participant.teleportToOriginalLocation();
            setPlayerCount(playerCount - 1);
            PotionGamesX plugin = PotionGamesX.getInstance();
            if (plugin != null && plugin.getScoreboardManager() != null) {
                plugin.getScoreboardManager().hide(p);
            }
        }
        if (playerCount == 0) {
            onLobbyEmptied();
        }
    }

    /**
     * Handles the last player leaving the lobby. A running round must not be
     * reset on the spot: it ends through ENDING like a normal round end, so the
     * draw is announced, the blocks are restored and the lobby returns to
     * WAITING. Only an empty countdown is reset directly.
     */
    private void onLobbyEmptied() {
        switch (state) {
            case INGAME:
            case DEATHMATCH:
                announceDraw();
                endRound();
                break;
            case PREPARING:
            case ENDING:
                setState(GameStates.RESET);
                break;
            default:
                // WAITING/RESET: nothing to tear down.
                break;
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
        PotionGamesX plugin = PotionGamesX.getInstance();
        for (Participant part : participants) {
            part.teleportToOriginalLocation();
            if (plugin != null && plugin.getScoreboardManager() != null) {
                plugin.getScoreboardManager().hide(part.getPlayer());
            }
        }
        for (Player spectator : new ArrayList<>(spectatorPlayers)) {
            removeSpectatorPlayer(spectator);
        }
        participants.clear();
        activePlayers.clear();
        spectatorPlayers.clear();
        spectatorStates.clear();
        playerKills.clear();
        playerDeaths.clear();
        setPlayerCount(0);
    }

    /**
     * Puts everyone of this lobby back into the hub after a finished round, the
     * way the legacy release did: eliminated players become players again, each
     * one keeps the pre-join snapshot (restored by /pg leave) and gets the
     * lobby inventory. Only this lobby is touched; players of other lobbies stay
     * where and as they are.
     */
    private void returnPlayersToLobby() {
        for (Player spectator : new ArrayList<>(spectatorPlayers)) {
            PlayerState state = spectatorStates.remove(spectator.getUniqueId());
            spectatorPlayers.remove(spectator);
            spectator.setCanPickupItems(true);
            spectator.setCollidable(true);
            spectator.setFireTicks(0);
            spectator.setGameMode(GameMode.ADVENTURE);
            Participant participant = getParticipant(spectator);
            if (participant == null) {
                participant = new Participant(this, spectator);
                participant.adoptSavedState(state);
                participants.add(participant);
            }
            if (!activePlayers.contains(spectator)) {
                activePlayers.add(spectator);
            }
        }

        PotionGamesX plugin = PotionGamesX.getInstance();
        if (getSpawn() == null) {
            // No hub configured: fall back to the pre-join location.
            clearParticipants();
            return;
        }
        for (Participant participant : new ArrayList<>(participants)) {
            Player player = participant.getPlayer();
            if (player == null || !player.isOnline()) {
                participants.remove(participant);
                activePlayers.remove(player);
                continue;
            }
            participant.setKit(null);
            participant.teleportToLobby();
            if (plugin != null && plugin.getScoreboardManager() != null) {
                plugin.getScoreboardManager().show(player);
            }
        }
        spectatorStates.clear();
        if (plugin != null && plugin.getScoreboardManager() != null) {
            plugin.getScoreboardManager().update(this);
        }
        setPlayerCount(participants.size());
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

    /**
     * Turn an active player into a spectator without moving them, so a killed
     * player stays at the death location instead of being teleported back to
     * their pre-join location. The pre-join state is kept for
     * {@link #removeSpectatorPlayer(Player)}.
     */
    public void addSpectatorPlayer(Player player) {
        if (player == null) {
            return;
        }
        Participant participant = getParticipant(player);
        if (participant != null) {
            participants.remove(participant);
            PlayerState state = participant.takeSavedState();
            if (state != null) {
                spectatorStates.put(player.getUniqueId(), state);
            }
            setPlayerCount(Math.max(0, playerCount - 1));
        }
        activePlayers.remove(player);
        if (!spectatorPlayers.contains(player)) {
            spectatorPlayers.add(player);
        }
    }

    /**
     * Stops spectating and restores the pre-join state: gamemode, inventory,
     * location, potion effects and the server scoreboard. Also clears the
     * spectator-only flags applied by the death and spectator listeners.
     */
    public void removeSpectatorPlayer(Player player) {
        if (player == null) {
            return;
        }
        boolean wasSpectator = spectatorPlayers.remove(player);
        PlayerState state = spectatorStates.remove(player.getUniqueId());
        if (state != null) {
            state.restore(player);
        } else if (wasSpectator && player.getGameMode() == GameMode.SPECTATOR) {
            player.setGameMode(GameMode.SURVIVAL);
        }
        if (!wasSpectator && state == null) {
            return;
        }
        player.setCanPickupItems(true);
        player.setCollidable(true);
        player.setFireTicks(0);
        PotionGamesX plugin = PotionGamesX.getInstance();
        if (plugin != null && plugin.getScoreboardManager() != null) {
            plugin.getScoreboardManager().hide(player);
        }
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
        // Never leave a player half-joined (active, but without a participant)
        if (p == null || !canJoin()) {
            return;
        }
        activePlayers.add(p);
        addParticipant(p);
    }

    /**
     * A player may actively join only while the lobby waits or counts down
     * and has free slots.
     */
    public boolean canJoin() {
        return playerCount < maxPlayers
            && (state == GameStates.WAITING || state == GameStates.PREPARING);
    }

    /**
     * Spectating a running round is possible while fights are ongoing.
     */
    public boolean canSpectate() {
        return state == GameStates.INGAME || state == GameStates.DEATHMATCH;
    }

    /**
     * Late join: become an invisible spectator of the running round.
     */
    public void joinAsSpectator(Player p) {
        if (p == null) {
            return;
        }
        if (!isActivePlayer(p) && !spectatorStates.containsKey(p.getUniqueId())) {
            spectatorStates.put(p.getUniqueId(), new PlayerState(p));
        }
        addSpectatorPlayer(p);
        p.setGameMode(GameMode.SPECTATOR);
        Location target = null;
        if (currentArena != null) {
            ArrayList<Location> spawns = currentArena.getSpawns();
            if (!spawns.isEmpty()) {
                target = spawns.get(0);
            }
        }
        if (target == null) {
            target = spawn;
        }
        if (target != null) {
            p.teleport(target);
        }
    }

    public void leave(Player p) {
        if (p == null) {
            return;
        }
        activePlayers.remove(p);
        removeSpectatorPlayer(p);
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
        if (arenas.isEmpty()) {
            return null;
        }
        Arena leading = getLeadingVotedArena();
        return leading != null ? leading : arenas.get((int)(Math.random() * arenas.size()));
    }

    /**
     * Arena to display outside of a running round: the vote leader, or null
     * when nobody voted yet.
     */
    public Arena getDisplayArena() {
        if (currentArena != null) {
            return currentArena;
        }
        return getLeadingVotedArena();
    }

    private Arena getLeadingVotedArena() {
        var asm = PotionGamesX.getInstance().getArenaStateManager();
        if (asm == null) {
            return null;
        }
        Arena leader = null;
        int topVotes = 0;
        for (Arena arena : arenas) {
            int votes = asm.getLobbyVoteCount(Integer.toString(id), arena.getName());
            if (votes > topVotes) {
                leader = arena;
                topVotes = votes;
            }
        }
        return leader;
    }

    /**
     * Starts the state machine task, at most once. The field used to be
     * assigned after scheduling, so two joins handled on different threads
     * could both pass the {@code tickTask == null} check and run the state
     * machine at double speed.
     */
    public synchronized void startTick() {
        if (tickStarting || (tickTask != null && !tickTask.isCancelled())) {
            return;
        }
        tickStarting = true;
        try {
            tickTask = PotionGamesX.getInstance().getServer().getGlobalRegionScheduler()
                    .runAtFixedRate(PotionGamesX.getInstance(), scheduledTask -> runGameTick(), 1, 20);
        } finally {
            tickStarting = false;
        }
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

    /**
     * Start the current countdown with a different length without changing the
     * configured countdown of later rounds.
     */
    public void forceStart(int seconds) {
        if (state != GameStates.WAITING) {
            return;
        }
        prepareTimer = Math.max(1, seconds);
        setState(GameStates.PREPARING);
        if (tickTask == null || tickTask.isCancelled()) {
            startTick();
        }
    }

    public void cancelCountdown() {
        if (state == GameStates.PREPARING) {
            prepareTimer = Math.max(1, countdown);
            setState(GameStates.WAITING);
        }
    }

    /**
     * Record (or switch) a player's arena vote. The ArenaStateManager is the
     * single source of truth for vote counts and per-player votes.
     */
    public void recordVote(Player player, String arenaName) {
        if (player == null || arenaName == null || arenaName.isBlank()) {
            return;
        }

        var asm = PotionGamesX.getInstance().getArenaStateManager();
        String lobbyId = Integer.toString(id);

        // Switching: remove the previous vote first
        String previousVote = asm.getPlayerVoteInLobby(lobbyId, player);
        if (previousVote != null) {
            asm.removeLobbyVote(lobbyId, previousVote);
        }
        asm.addLobbyVote(lobbyId, arenaName);
        asm.recordPlayerVoteInLobby(lobbyId, player, arenaName);
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

        try {
            incrementTeamCount(Integer.parseInt(teamName));
        } catch (NumberFormatException ignored) {
        }
    }

    public void distributeTeams() {
        if (!activateTeams || activePlayers.isEmpty()) {
            return;
        }

        int size = Math.max(1, teamSize);
        int amount = getTeamAmount();

        // Keep the teams picked in the selector before rebuilding the counters.
        Map<Player, String> picked = new HashMap<>(lobbyteamplayernames);
        clearTeams();
        for (int i = 1; i <= amount; i++) {
            lobbyteams.put(i, 0);
        }

        // 1) Honour the manual choices that still fit.
        for (Player player : new ArrayList<>(activePlayers)) {
            String pick = picked.get(player);
            if (pick == null) {
                continue;
            }
            int teamId;
            try {
                teamId = Integer.parseInt(pick);
            } catch (NumberFormatException ignored) {
                continue;
            }
            if (teamId < 1 || teamId > amount || lobbyteams.getOrDefault(teamId, 0) >= size) {
                continue;
            }
            recordTeamAssignment(player, Integer.toString(teamId));
        }

        // 2) Spread the rest over the emptiest teams, so two players no longer
        // end up in the same team and the round ends on its first tick.
        for (Player player : new ArrayList<>(activePlayers)) {
            if (lobbyteamplayernames.containsKey(player)) {
                continue;
            }
            int teamId = findEmptiestTeam(size, amount);
            if (teamId < 0) {
                break;
            }
            recordTeamAssignment(player, Integer.toString(teamId));
        }
    }

    /** Team with the fewest players that still has a free slot, or -1. */
    private int findEmptiestTeam(int size, int amount) {
        int bestTeam = -1;
        int bestCount = Integer.MAX_VALUE;
        for (int teamId = 1; teamId <= amount; teamId++) {
            int count = lobbyteams.getOrDefault(teamId, 0);
            if (count >= size) {
                continue;
            }
            if (count < bestCount) {
                bestCount = count;
                bestTeam = teamId;
            }
        }
        return bestTeam;
    }

    /** Copy of the current per-team player counts. */
    public Map<Integer, Integer> getTeamCounts() {
        return new HashMap<>(lobbyteams);
    }

    public boolean isTeamFull(int teamId, int maxTeamPlayers) {
        return lobbyteams.getOrDefault(teamId, 0) >= Math.max(1, maxTeamPlayers);
    }

    /**
     * Assigns a player to a team and keeps the lobby counters and the selector
     * counters in sync, so the GUI capacity matches reality.
     *
     * @return false when the team does not exist or is already full
     */
    public boolean assignTeam(Player player, int teamId, int maxTeamPlayers) {
        if (player == null || teamId < 1 || teamId > getTeamAmount() || isTeamFull(teamId, maxTeamPlayers)) {
            return false;
        }
        PotionGamesX plugin = PotionGamesX.getInstance();
        if (plugin != null && plugin.getArenaStateManager() != null) {
            var asm = plugin.getArenaStateManager();
            String lobbyId = Integer.toString(id);
            String previous = asm.getPlayerTeamInLobby(lobbyId, player);
            if (previous != null) {
                try {
                    asm.decrementLobbyTeamPlayers(lobbyId, Integer.parseInt(previous));
                } catch (NumberFormatException ignored) {
                }
                asm.removePlayerTeamInLobby(lobbyId, player);
            }
            asm.incrementLobbyTeamPlayers(lobbyId, teamId);
            asm.recordPlayerTeamInLobby(lobbyId, player, Integer.toString(teamId));
        }
        recordTeamAssignment(player, Integer.toString(teamId));
        return true;
    }

    public void endRound() {
        playLobbySound(Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
        setState(GameStates.ENDING);
        endingTimer = Math.max(1, reset);
    }

    /** Plays a round feedback sound for everyone currently in this lobby. */
    private void playLobbySound(Sound sound, float volume, float pitch) {
        for (Participant participant : participants) {
            Player player = participant.getPlayer();
            if (player != null && player.isOnline()) {
                player.playSound(player.getLocation(), sound, volume, pitch);
            }
        }
    }

    /**
     * Give gameplay items (player-finder compass) to everyone at round start.
     */
    private void giveSpawnItems() {
        if (!PotionGamesX.getInstance().getConfigManager().isCompassOnSpawn()) {
            return;
        }
        ItemStack compass = new ItemStack(Material.COMPASS);
        org.bukkit.inventory.meta.ItemMeta meta = compass.getItemMeta();
        if (meta != null) {
            meta.displayName(Messages.PlayerFinder());
            compass.setItemMeta(meta);
        }
        for (Player player : activePlayers) {
            if (player != null && player.isOnline()) {
                player.getInventory().addItem(compass);
            }
        }
    }

    /**
     * Freeze daylight/weather in the arena world while the round runs.
     */
    private void applyGameRules() {
        if (!PotionGamesX.getInstance().getConfigManager().isChangeGamerules()) {
            return;
        }
        World world = resolveArenaWorld();
        if (world == null || gameruleWorld != null) {
            return;
        }
        gameruleWorld = world;
        prevDaylight = world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE);
        prevWeather = world.getGameRuleValue(GameRule.DO_WEATHER_CYCLE);
        prevFireTick = world.getGameRuleValue(GameRule.DO_FIRE_TICK);
        prevMobGriefing = world.getGameRuleValue(GameRule.MOB_GRIEFING);
        prevMobSpawning = world.getGameRuleValue(GameRule.DO_MOB_SPAWNING);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        // Fire spread and mob grief would otherwise remove arena blocks that
        // the round cannot restore.
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
    }

    private void restoreGameRules() {
        if (gameruleWorld == null) {
            return;
        }
        if (prevDaylight != null) {
            gameruleWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, prevDaylight);
        }
        if (prevWeather != null) {
            gameruleWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, prevWeather);
        }
        if (prevFireTick != null) {
            gameruleWorld.setGameRule(GameRule.DO_FIRE_TICK, prevFireTick);
        }
        if (prevMobGriefing != null) {
            gameruleWorld.setGameRule(GameRule.MOB_GRIEFING, prevMobGriefing);
        }
        if (prevMobSpawning != null) {
            gameruleWorld.setGameRule(GameRule.DO_MOB_SPAWNING, prevMobSpawning);
        }
        gameruleWorld = null;
        prevDaylight = null;
        prevWeather = null;
        prevFireTick = null;
        prevMobGriefing = null;
        prevMobSpawning = null;
    }

    private World resolveArenaWorld() {
        if (currentArena != null) {
            ArrayList<Location> spawns = currentArena.getSpawns();
            if (!spawns.isEmpty() && spawns.get(0).getWorld() != null) {
                return spawns.get(0).getWorld();
            }
        }
        return spawn != null ? spawn.getWorld() : null;
    }

    public void runGameTick() {
        if (pause) {
            // Pausing freezes the round, never the reset. An emptied lobby used
            // to stay paused forever: RESET never ran, WAITING was never
            // restored and canJoin() rejected everybody ("lobby full").
            if (playerCount > 0 && state != GameStates.WAITING && state != GameStates.RESET) {
                return;
            }
            pause = false;
        }

        switch (state) {
            case WAITING:
                for (Participant participant : participants) {
                    participant.sendActionBar(Messages.GameWaiting(playerCount, minPlayers));
                }
                if (playerCount >= minPlayers) {
                    prepareTimer = Math.max(1, countdown);
                    setState(GameStates.PREPARING);
                    PotionGamesX plugin = PotionGamesX.getInstance();
                    if (plugin.getConfigManager().isBroadcastStarting()) {
                        plugin.getServer().broadcast(Messages.LobbyStartingBroadcast(Integer.toString(id)));
                    }
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
                        // Adventure mode (lobby) and the lobby items would make
                        // the whole arena block system unreachable.
                        for (Participant participant : participants) {
                            participant.startRound();
                        }
                        applyGameRules();
                        giveSpawnItems();
                    }
                    gameTimer = Math.max(1, roundTime);
                    deathmatch = false;
                    playLobbySound(Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1f, 1f);
                    playLobbySound(Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1f, 1f);
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
                    playLobbySound(Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 1f);
                }
                if (prepareTimer > 0 && prepareTimer <= 5) {
                    playLobbySound(Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f);
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
                restoreGameRules();
                PotionGamesX plugin = PotionGamesX.getInstance();
                // On a dedicated game server, send everyone back to the hub
                if (plugin.getConfigManager().isGameServer()) {
                    String hubServer = plugin.getConfig().getString("pg.bungeeServer", "hub");
                    for (Player player : new ArrayList<>(activePlayers)) {
                        sendToServer(player, hubServer);
                    }
                    for (Player player : new ArrayList<>(spectatorPlayers)) {
                        sendToServer(player, hubServer);
                    }
                    // The players leave this server, so there is nothing to keep.
                    clearParticipants();
                } else {
                    returnPlayersToLobby();
                }
                setCurrentArena(null);
                clearVoting();
                clearTeams();
                clearBlockTracking();
                clearChests();
                playerKills.clear();
                playerDeaths.clear();
                setState(GameStates.WAITING);
                if (playerCount == 0 && tickTask != null && !tickTask.isCancelled()) {
                    tickTask.cancel();
                    tickTask = null;
                }
                break;
            default:
                break;
        }

        PotionGamesX gamePlugin = PotionGamesX.getInstance();
        if (gamePlugin != null && gamePlugin.getScoreboardManager() != null) {
            gamePlugin.getScoreboardManager().update(this);
        }
    }

    private void checkWinCondition() {
        List<Player> alive = new ArrayList<>();
        for (Player player : activePlayers) {
            if (player != null && player.isOnline() && player.getGameMode() != GameMode.SPECTATOR) {
                alive.add(player);
            }
        }

        // Team mode: the round ends when a single team remains
        if (activateTeams && !lobbyteamplayernames.isEmpty()) {
            Set<String> aliveTeams = new LinkedHashSet<>();
            for (Player player : alive) {
                String team = lobbyteamplayernames.get(player);
                if (team != null) {
                    aliveTeams.add(team);
                }
            }
            if (aliveTeams.size() == 1 && !alive.isEmpty()) {
                announceTeamWin(aliveTeams.iterator().next(), alive);
                endRound();
                return;
            }
            if (alive.isEmpty()) {
                announceDraw();
                endRound();
                return;
            }
            // 1v1 from opposing teams: deathmatch applies here too
            if (alive.size() == 2 && aliveTeams.size() == 2
                    && PotionGamesX.getInstance().getConfigManager().isActivateDeathmatch() && !deathmatch) {
                activateDeathmatch();
            }
        } else if (alive.size() == 1) {
            announceWinner(alive.get(0));
            endRound();
        } else if (alive.isEmpty()) {
            announceDraw();
            endRound();
        } else if (alive.size() == 2 && PotionGamesX.getInstance().getConfigManager().isActivateDeathmatch() && !deathmatch) {
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
        sendToLobby(Messages.WinnerHasWonTheGame(winnerName));
        for (Player player : activePlayers) {
            if (player != null && player.equals(winner)) {
                rewardWin(player);
            } else if (player != null) {
                PotionGamesX.getInstance().getDatabaseManager().addLosses(player.getUniqueId().toString(), 1);
            }
        }
    }

    private void announceTeamWin(String teamId, List<Player> winningTeam) {
        String teamName = "Team " + teamId;
        sendToLobby(Messages.WinnerHasWonTheGame(teamName));
        for (Player player : activePlayers) {
            if (player == null) continue;
            if (winningTeam.contains(player)) {
                rewardWin(player);
            } else {
                PotionGamesX.getInstance().getDatabaseManager().addLosses(player.getUniqueId().toString(), 1);
            }
        }
    }

    private void rewardWin(Player player) {
        PotionGamesX plugin = PotionGamesX.getInstance();
        if (plugin.getConfigManager().isEnableRewards()) {
            Economy economy = PotionGamesX.getEconomy();
            if (economy != null) {
                int reward = plugin.getConfigManager().getWinningReward();
                EconomyResponse response = economy.depositPlayer(player, reward);
                if (response.transactionSuccess()) {
                    player.sendMessage(Messages.WinReward(reward)
                        .append(Component.text(" " + economy.format(response.amount)).color(NamedTextColor.LIGHT_PURPLE)));
                } else {
                    plugin.getLogger().warning("[PotionGamesX] Could not pay the win reward to "
                        + player.getName() + ": " + response.errorMessage);
                }
            }
        }
        plugin.getDatabaseManager().addWins(player.getUniqueId().toString(), 1);
        spawnWinFirework(player);
    }

    /** Celebratory firework on the winner's region thread (Folia-safe). */
    private void spawnWinFirework(Player player) {
        PotionGamesX plugin = PotionGamesX.getInstance();
        player.getScheduler().run(plugin, task -> {
            Location loc = player.getLocation();
            World world = loc.getWorld();
            if (world == null) {
                return;
            }
            Firework firework = world.spawn(loc, Firework.class, fw -> {
                FireworkMeta meta = fw.getFireworkMeta();
                meta.setPower(1);
                meta.addEffect(FireworkEffect.builder()
                    .flicker(true)
                    .with(FireworkEffect.Type.STAR)
                    .withColor(Color.GREEN, Color.AQUA, Color.RED, Color.PURPLE, Color.YELLOW)
                    .build());
                fw.setFireworkMeta(meta);
            });
            firework.detonate();
        }, null);
    }

    private void announceDraw() {
        sendToLobby(Messages.RoundNobodyWon());
    }

    /** Sends a round message to the remaining players and to the spectators. */
    private void sendToLobby(Component message) {
        for (Participant participant : participants) {
            participant.sendMessage(message);
        }
        for (Player spectator : new ArrayList<>(spectatorPlayers)) {
            if (spectator != null && spectator.isOnline()) {
                spectator.sendMessage(message);
            }
        }
    }

    private void restoreBlocks() {
        // Restore broken blocks to their original material
        for (Map.Entry<Location, Material> entry : brokenBlocks.entrySet()) {
            entry.getKey().getBlock().setType(entry.getValue());
        }
        // Restore liquids to the block data captured before placement
        for (Map.Entry<Location, BlockData> entry : liquidBlocks.entrySet()) {
            entry.getKey().getBlock().setBlockData(entry.getValue());
        }
        // Remove player-placed blocks
        for (Location loc : placedBlocks.keySet()) {
            loc.getBlock().setType(Material.AIR);
        }
        clearBlockTracking();
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

    public void addPlacedBlock(Location location, Material material) {
        if (location != null && material != null) {
            placedBlocks.put(location, material);
        }
    }

    public void addBrokenBlock(Location location, Material material) {
        if (location != null && material != null) {
            brokenBlocks.put(location, material);
        }
    }

    /**
     * Remember the block data a liquid is about to replace so it can be
     * restored when the round ends.
     */
    public void addLiquidBlock(Location location, BlockData blockData) {
        if (location != null && blockData != null && !liquidBlocks.containsKey(location)) {
            liquidBlocks.put(location, blockData);
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

    public void setState(GameStates newState) {
        if (this.state != newState) {
            GameStates oldState = this.state;
            this.state = newState;
            updateJoinSign();

            PotionGamesX plugin = PotionGamesX.getInstance();

            // Keep the LobbyStateManager view in sync so listeners observe real state
            if (plugin != null && plugin.getLobbyStateManager() != null) {
                plugin.getLobbyStateManager().setGameState(Integer.toString(id), newState);
            }

            // Game just ended (transitioned to RESET) - move players via BungeeCord
            if (newState == GameStates.RESET && oldState != GameStates.RESET && plugin != null
                    && plugin.getConfigManager() != null && plugin.getConfigManager().isGameServer()) {
                plugin.getComponentLogger().info(
                    Settings.prefix
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

    public int getGameTimer() {
        return this.gameTimer;
    }

    /**
     * True when the given world hosts this lobby's hub or one of its arenas.
     * Used to keep world-level events (flow, fade, decay, fire) away.
     */
    public boolean isGameWorld(World world) {
        if (world == null) {
            return false;
        }
        Location lobbySpawn = this.spawn;
        if (lobbySpawn != null && world.equals(lobbySpawn.getWorld())) {
            return true;
        }
        return isArenaWorld(world);
    }

    /**
     * True when the given world hosts one of this lobby's arenas.
     * Used to contain explosions inside arena geometry.
     */
    public boolean isArenaWorld(World world) {
        if (world == null) {
            return false;
        }
        if (currentArena != null && arenaUsesWorld(currentArena, world)) {
            return true;
        }
        for (Arena arena : arenas) {
            if (arenaUsesWorld(arena, world)) {
                return true;
            }
        }
        return false;
    }

    private static boolean arenaUsesWorld(Arena arena, World world) {
        if (arena == null) {
            return false;
        }
        for (Location spawn : arena.getSpawns()) {
            if (spawn != null && world.equals(spawn.getWorld())) {
                return true;
            }
        }
        return false;
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

    public int getTeamSize() {
        return teamSize;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public int getMinPlayers() {
        return this.minPlayers;
    }

    // =====

    /**
     * Set countdown timer
     */
    public void setCountdown(int countdown) {
        this.countdown = countdown;
        this.prepareTimer = countdown;
    }

    public boolean isActivateKits() {
        return activateKits;
    }

    public boolean isActivateTeams() {
        return activateTeams;
    }

    /**
     * Set teams activation for this lobby
     */
    public boolean isActivateAirdrops() {
        return activateAirdrops;
    }

    public boolean isActivateShop() {
        return activateShop;
    }




    // =====

    public boolean isDeathmatch() { return deathmatch; }
    public void setDeathmatch(boolean value) { this.deathmatch = value; }


    public boolean isJoinable() { return joinable; }
    public void setJoinable(boolean value) { this.joinable = value; }

    public boolean isPaused() { return pause; }
    public void setPaused(boolean value) { this.pause = value; }

    // =====

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

    public void clearChests() {
        chests.clear();
    }


    public void clearBlockTracking() {
        placedBlocks.clear();
        brokenBlocks.clear();
        liquidBlocks.clear();
    }

    // =====

    /**
     * Reset all votes for this lobby.
     */
    public void clearVoting() {
        PotionGamesX.getInstance().getArenaStateManager().resetLobbyVotes(Integer.toString(id));
    }

    // =====

    public HashMap<Player, String> getTeamPlayerNamesMap() {
        return lobbyteamplayernames;
    }

    public void clearTeams() {
        lobbyteams.clear();
        lobbyteamplayernames.clear();
        PotionGamesX plugin = PotionGamesX.getInstance();
        if (plugin != null && plugin.getArenaStateManager() != null) {
            plugin.getArenaStateManager().resetLobbyTeams(Integer.toString(id));
        }
    }

    /**
     * Drops the lobby vote and the team choice of a player who leaves the lobby
     * (command, quit, kick or death), so a disconnected player cannot decide
     * the map and a team slot does not stay occupied.
     */
    public void clearPlayerVoteAndTeam(Player p) {
        if (p == null) {
            return;
        }
        PotionGamesX plugin = PotionGamesX.getInstance();
        if (plugin != null && plugin.getArenaStateManager() != null) {
            var asm = plugin.getArenaStateManager();
            String lobbyId = Integer.toString(id);
            String vote = asm.getPlayerVoteInLobby(lobbyId, p);
            if (vote != null) {
                asm.removeLobbyVote(lobbyId, vote);
                asm.removePlayerVoteInLobby(lobbyId, p);
            }
            String team = asm.getPlayerTeamInLobby(lobbyId, p);
            if (team != null) {
                try {
                    asm.decrementLobbyTeamPlayers(lobbyId, Integer.parseInt(team));
                } catch (NumberFormatException ignored) {
                    // Team ids are numeric; a malformed one is simply dropped.
                }
                asm.removePlayerTeamInLobby(lobbyId, p);
            }
        }
        String ownTeam = lobbyteamplayernames.remove(p);
        if (ownTeam != null) {
            try {
                decrementTeamCount(Integer.parseInt(ownTeam));
            } catch (NumberFormatException ignored) {
                // Team ids are numeric; a malformed one is simply dropped.
            }
        }
    }

    // =====

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

    // =====

    /**
     * Get all arenas in this lobby.
     *
     * @return A list of all arenas (may be empty)
     */
    public ArrayList<Arena> getArenas() {
        return new ArrayList<>(arenas);
    }
}

