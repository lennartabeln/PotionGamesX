package com.tw0far.potiongames.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.tw0far.potiongames.main.PotionGames;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class Lobby {
    private int id;
    private Location spawn;

    private boolean enabled = false;
    private boolean activateTeams = true;
    private boolean activateKits = true;
    private boolean activateShop = true;
    private boolean activateAirdrops = true;
    private int roundTime = 30;
    private int maxPlayers = 24;
    private int minPlayers = 2;
    private int teamSize = 2;
    private int teamAmount = maxPlayers / teamSize;
    
    private int playerCount = 0;
    private ArrayList<Participant> participants = new ArrayList<>();
    private ArrayList<Arena> arenas = new ArrayList<>();
    private Arena currentArena = null;
    private GameStates state = GameStates.WAITING;
    private Sign joinSign = null;

    private int tickTaskId = -1;
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
        Settings.arenadata.set("pg.lobbies." + id + ".enabled", enabled);
        try {
            Settings.arenadata.save(Settings.arenadatafile);
            return true;
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ex.getMessage());
            return false;
        }
    }

    public void load() {
        enabled = Settings.arenadata.getBoolean("pg.lobbies." + id + ".enabled");
        spawn = Settings.arenadata.getLocation("pg.lobbies." + id + ".spawn");
        activateTeams = Settings.arenadata.getBoolean("pg.lobbies." + id + ".activateTeams");
        activateKits = Settings.arenadata.getBoolean("pg.lobbies." + id + ".activateKits");
        activateShop = Settings.arenadata.getBoolean("pg.lobbies." + id + ".activateShop");
        activateAirdrops = Settings.arenadata.getBoolean("pg.lobbies." + id + ".activateAirdrops");
        roundTime = Settings.arenadata.getInt("pg.lobbies." + id + ".roundTime");
        maxPlayers = Settings.arenadata.getInt("pg.lobbies." + id + ".maxPlayers");
        minPlayers = Settings.arenadata.getInt("pg.lobbies." + id + ".minPlayers");
        teamSize = Settings.arenadata.getInt("pg.lobbies." + id + ".teamSize");
        joinSign = Settings.arenadata.contains("pg.lobbies." + id + ".joinSign") ? (Sign) Settings.arenadata.getLocation("pg.lobbies." + id + ".joinSign").getBlock().getState() : null;
        updateJoinSign();

        if (Settings.arenadata.contains("pg.lobbies." + id + ".arenas")) {
            for (String key : Settings.arenadata.getConfigurationSection("pg.lobbies." + id + ".arenas").getKeys(false)) {
                Arena arena = new Arena(key, id);
                arena.load();
                arenas.add(arena);
            }
        }
    }

    public boolean add(Location spawn) {
        this.spawn = spawn;
        Settings.arenadata.set("pg.lobbies." + id + ".enabled", enabled);
        Settings.arenadata.set("pg.lobbies." + id + ".spawn", spawn);
        Settings.arenadata.set("pg.lobbies." + id + ".activateTeams", activateTeams);
        Settings.arenadata.set("pg.lobbies." + id + ".activateKits", activateKits);
        Settings.arenadata.set("pg.lobbies." + id + ".activateShop", activateShop);
        Settings.arenadata.set("pg.lobbies." + id + ".activateAirdrops", activateAirdrops);
        Settings.arenadata.set("pg.lobbies." + id + ".roundTime", roundTime);
        Settings.arenadata.set("pg.lobbies." + id + ".maxPlayers", maxPlayers);
        Settings.arenadata.set("pg.lobbies." + id + ".minPlayers", minPlayers);
        Settings.arenadata.set("pg.lobbies." + id + ".teamSize", teamSize);
        try {
            Settings.arenadata.save(Settings.arenadatafile);
            return true;
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ex.getMessage());
            return false;
        }
    }

    public boolean remove() {
        Settings.arenadata.set("pg.lobbies." + id, null);
        try {
            Settings.arenadata.save(Settings.arenadatafile);
            return true;
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ex.getMessage());
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
        if (tickTaskId == -1) {
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
        setPlayerCount(0);
    }

    public void join(Player p) {
        addParticipant(p);
    }

    public void leave(Player p) {
        removeParticipant(p);
    }

    public boolean setJoinSign(Location loc) {
        BlockState b = loc.getBlock().getState();
        joinSign = (Sign) b;
        Settings.arenadata.set("pg.lobbies." + id + ".joinSign", loc);
        try {
            Settings.arenadata.save(Settings.arenadatafile);
            updateJoinSign();
            return true;
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ex.getMessage());
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
        tickTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(PotionGames.getInstance(), () -> {
            switch(state) {
                case WAITING:
                    for (Participant participant : participants) {
                        participant.sendActionBar(Messages.GameWaiting(playerCount, minPlayers));
                    }
                    if (playerCount >= minPlayers) {
                        setState(GameStates.PREPARING);
                        prepareTimer = 10; // for testing
                    }
                    break;
                case PREPARING:
                    for (Participant participant : participants) {
                        participant.sendActionBar(Messages.GameStartsIn(prepareTimer));
                    }
                    if (prepareTimer <= 0) {
                        setState(GameStates.INGAME);
                        gameTimer = roundTime * 1; // for testing
                        currentArena.teleport(participants);
                    } else if (prepareTimer == 10) {
                        setCurrentArena(getVotedArena());
                        for (Participant participant : participants) {
                            participant.sendMessage(Messages.WillBePlayed(currentArena.getName()));
                        }
                    }
                    if (playerCount < minPlayers) {
                        setState(GameStates.WAITING);
                    }
                    prepareTimer--;
                    break;
                case INGAME:
                    if (gameTimer >= 0 && Arrays.stream(announceRoundTimes).anyMatch(t -> t == gameTimer)) {
                        for (Participant participant : participants) {
                            if (gameTimer <= 0) {
                                // participant.sendMessage(Messages.RoundEnded());
                            } else if (gameTimer <= 60) {
                                participant.sendMessage(Messages.RoundSecondsRemaining(gameTimer));
                            } else {
                                participant.sendMessage(Messages.RoundMinutesRemaining(gameTimer / 60));
                            }
                        }
                    } else if (gameTimer < 0) {
                        setState(GameStates.ENDING);
                        endingTimer = 10;
                    }
                    gameTimer--;
                    break;
                case ENDING:
                    for (Participant participant : participants) {
                        participant.sendActionBar(Messages.TeleportToLobbyIn(endingTimer));
                    }
                    if (endingTimer <= 0) {
                        setState(GameStates.RESET);
                        for (Participant participant : participants) {
                            participant.teleportToLobby();
                        }
                    }
                    endingTimer--;
                    break;
                case RESET:
                    // Handle reset state logic here
                    setCurrentArena(null);
                    setState(GameStates.WAITING);
                    if (playerCount == 0) {
                        Bukkit.getScheduler().cancelTask(tickTaskId);
                        tickTaskId = -1;
                    }
                    break;
                default:
                    break;
            }
            if (playerCount == 0 && state != GameStates.WAITING) {
                setState(GameStates.RESET);
            }
        }, 0L, 20L);
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
            this.state = newState;
            updateJoinSign();
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
}
