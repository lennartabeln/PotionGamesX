package com.tw0far.potiongames.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a single arena within a lobby.
 * 
 * Encapsulates:
 * - Spawn locations (normal and deathmatch)
 * - Chest loot configuration
 * - Per-round voting results
 * - Arena metadata (name, status)
 * 
 * Thread-safe access patterns are used for voting to prevent race conditions.
 */
public class Arena {
    private final String name;
    private final int lobbyId;
    private final LobbyConfig lobbyConfig;
    
    // Spawn management
    private ArrayList<Location> spawns = new ArrayList<>();
    private ArrayList<Location> deathmatchSpawns = new ArrayList<>();
    private final Random random = new Random();
    
    // Loot configuration
    private ArrayList<Location> chestLocations = new ArrayList<>();
    private Map<Location, ItemStack[]> chestLoot = new HashMap<>();
    
    // Voting state (per-round)
    private int voteCount = 0;
    
    // Arena status
    private ArenaStatus status = ArenaStatus.AVAILABLE;
    
    public enum ArenaStatus {
        AVAILABLE,   // Ready for use
        IN_USE,      // Currently hosting a game
        DISABLED     // Not available
    }

    /**
     * Create a new arena with optional lobby configuration inheritance.
     * 
     * @param name The arena name (unique within lobby)
     * @param lobbyId The lobby ID this arena belongs to
     */
    public Arena(String name, int lobbyId) {
        this.name = name;
        this.lobbyId = lobbyId;
        this.lobbyConfig = null;
    }
    
    /**
     * Create a new arena with lobby configuration.
     * 
     * @param name The arena name (unique within lobby)
     * @param lobbyId The lobby ID this arena belongs to
     * @param lobbyConfig The lobby configuration for inheriting settings
     */
    public Arena(String name, int lobbyId, LobbyConfig lobbyConfig) {
        this.name = name;
        this.lobbyId = lobbyId;
        this.lobbyConfig = lobbyConfig;
    }

    /**
     * Load arena data from configuration file.
     */
    public void load() {
        loadSpawns();
        loadDeathmatchSpawns();
        loadChests();
    }
    
    /**
     * Load spawn locations from arena-data.yml.
     */
    private void loadSpawns() {
        String spawnPath = "pg.lobbies." + lobbyId + ".arenas." + name + ".spawns";
        if (Settings.arenadata.contains(spawnPath)) {
            for (String key : Settings.arenadata.getConfigurationSection(spawnPath).getKeys(false)) {
                try {
                    Location spawn = Settings.arenadata.getLocation(spawnPath + "." + key);
                    if (spawn != null) {
                        spawns.add(spawn);
                    }
                } catch (Exception ex) {
                    Bukkit.getConsoleSender().sendMessage("Error loading spawn " + key + " for arena " + name + ": " + ex.getMessage());
                }
            }
        }
    }
    
    /**
     * Load deathmatch spawn locations from arena-data.yml.
     */
    private void loadDeathmatchSpawns() {
        String deathmatchPath = "pg.lobbies." + lobbyId + ".arenas." + name + ".deathmatch";
        if (Settings.arenadata.contains(deathmatchPath)) {
            for (String key : Settings.arenadata.getConfigurationSection(deathmatchPath).getKeys(false)) {
                try {
                    Location spawn = Settings.arenadata.getLocation(deathmatchPath + "." + key);
                    if (spawn != null) {
                        deathmatchSpawns.add(spawn);
                    }
                } catch (Exception ex) {
                    Bukkit.getConsoleSender().sendMessage("Error loading deathmatch spawn " + key + " for arena " + name + ": " + ex.getMessage());
                }
            }
        }
    }
    
    /**
     * Load chest loot configuration from arena-data.yml.
     */
    private void loadChests() {
        String chestPath = "pg.lobbies." + lobbyId + ".arenas." + name + ".chests";
        if (Settings.arenadata.contains(chestPath)) {
            for (String key : Settings.arenadata.getConfigurationSection(chestPath).getKeys(false)) {
                try {
                    Location chestLoc = Settings.arenadata.getLocation(chestPath + "." + key + ".location");
                    if (chestLoc != null) {
                        chestLocations.add(chestLoc);
                        
                        // Load loot items if defined
                        String lootPath = chestPath + "." + key + ".items";
                        if (Settings.arenadata.contains(lootPath)) {
                            List<?> items = Settings.arenadata.getList(lootPath);
                            if (items != null && !items.isEmpty()) {
                                ItemStack[] loot = items.toArray(new ItemStack[0]);
                                chestLoot.put(chestLoc, loot);
                            }
                        }
                    }
                } catch (Exception ex) {
                    Bukkit.getConsoleSender().sendMessage("Error loading chest " + key + " for arena " + name + ": " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Add this arena to configuration file.
     */
    public boolean add() {
        Settings.arenadata.createSection("pg.lobbies." + lobbyId + ".arenas." + name);
        try {
            Settings.arenadata.save(Settings.arenadatafile);
            return true;
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ex.getMessage());
            return false;
        }
    }

    /**
     * Remove this arena from configuration file.
     */
    public boolean remove() {
        Settings.arenadata.set("pg.lobbies." + lobbyId + ".arenas." + name, null);
        try {
            Settings.arenadata.save(Settings.arenadatafile);
            return true;
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ex.getMessage());
            return false;
        }
    }

    // ===== SPAWN MANAGEMENT =====
    
    /**
     * Get a random spawn location from available spawns.
     * 
     * @return A random spawn location, or null if no spawns are configured
     */
    public Location getRandomSpawn() {
        if (spawns.isEmpty()) {
            return null;
        }
        return spawns.get(random.nextInt(spawns.size()));
    }
    
    /**
     * Get a specific spawn location by index.
     * 
     * @param index The spawn index
     * @return The spawn location at this index, or null if out of bounds
     */
    public Location getSpawn(int index) {
        if (index < 0 || index >= spawns.size()) {
            return null;
        }
        return spawns.get(index);
    }
    
    /**
     * Get all spawn locations.
     * 
     * @return A list of all spawn locations (may be empty)
     */
    public ArrayList<Location> getSpawns() {
        return new ArrayList<>(spawns);
    }
    
    /**
     * Add a new spawn location.
     * 
     * @param spawn The spawn location to add
     * @return true if successfully added and saved to config
     */
    public boolean addSpawn(Location spawn) {
        if (spawn == null) {
            return false;
        }
        
        int nextId = spawns.size();
        String spawnPath = "pg.lobbies." + lobbyId + ".arenas." + name + ".spawns." + nextId;
        Settings.arenadata.set(spawnPath, spawn);
        spawns.add(spawn);
        
        try {
            Settings.arenadata.save(Settings.arenadatafile);
            return true;
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ex.getMessage());
            spawns.remove(spawn);
            return false;
        }
    }
    
    /**
     * Add a spawn location with explicit spawn ID (for backwards compatibility).
     * 
     * @param spawnId The ID for this spawn
     * @param spawn The spawn location to add
     * @return true if successfully added and saved to config
     */
    public boolean addSpawn(int spawnId, Location spawn) {
        if (spawn == null) {
            return false;
        }
        
        String spawnPath = "pg.lobbies." + lobbyId + ".arenas." + name + ".spawns." + spawnId;
        Settings.arenadata.set(spawnPath, spawn);
        spawns.add(spawn);
        
        try {
            Settings.arenadata.save(Settings.arenadatafile);
            return true;
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ex.getMessage());
            spawns.remove(spawn);
            return false;
        }
    }

    /**
     * Remove a spawn location by index.
     * 
     * @param spawnId The index of the spawn to remove
     * @return true if successfully removed and saved to config
     */
    public boolean removeSpawn(int spawnId) {
        if (spawnId < 0 || spawnId >= spawns.size()) {
            return false;
        }
        
        String spawnPath = "pg.lobbies." + lobbyId + ".arenas." + name + ".spawns." + spawnId;
        Settings.arenadata.set(spawnPath, null);
        
        try {
            spawns.remove(spawnId);
            Settings.arenadata.save(Settings.arenadatafile);
            return true;
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ex.getMessage());
            return false;
        }
    }
    
    /**
     * Get all deathmatch spawn locations.
     * 
     * @return A list of deathmatch spawns (may be empty)
     */
    public ArrayList<Location> getDeathmatchSpawns() {
        return new ArrayList<>(deathmatchSpawns);
    }

    /**
     * Get a random deathmatch spawn location.
     *
     * @return A random deathmatch spawn, or null if none are configured
     */
    public Location getRandomDeathmatchSpawn() {
        if (deathmatchSpawns.isEmpty()) {
            return null;
        }
        return deathmatchSpawns.get(random.nextInt(deathmatchSpawns.size()));
    }

    /**
     * Add a deathmatch spawn location.
     * 
     * @param spawn The deathmatch spawn location to add
     * @return true if successfully added and saved to config
     */
    public boolean addDeathmatchSpawn(Location spawn) {
        if (spawn == null) {
            return false;
        }
        
        int nextId = deathmatchSpawns.size();
        String spawnPath = "pg.lobbies." + lobbyId + ".arenas." + name + ".deathmatch." + nextId;
        Settings.arenadata.set(spawnPath, spawn);
        deathmatchSpawns.add(spawn);
        
        try {
            Settings.arenadata.save(Settings.arenadatafile);
            return true;
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ex.getMessage());
            deathmatchSpawns.remove(spawn);
            return false;
        }
    }
    
    /**
     * Add a deathmatch spawn location with explicit spawn ID (for backwards compatibility).
     * 
     * @param spawnId The ID for this spawn
     * @param spawn The deathmatch spawn location to add
     * @return true if successfully added and saved to config
     */
    public boolean addDeathmatchSpawn(int spawnId, Location spawn) {
        if (spawn == null) {
            return false;
        }
        
        String spawnPath = "pg.lobbies." + lobbyId + ".arenas." + name + ".deathmatch." + spawnId;
        Settings.arenadata.set(spawnPath, spawn);
        deathmatchSpawns.add(spawn);
        
        try {
            Settings.arenadata.save(Settings.arenadatafile);
            return true;
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ex.getMessage());
            deathmatchSpawns.remove(spawn);
            return false;
        }
    }

    /**
     * Remove a deathmatch spawn location by index.
     * 
     * @param spawnId The index of the deathmatch spawn to remove
     * @return true if successfully removed and saved to config
     */
    public boolean removeDeathmatchSpawn(int spawnId) {
        if (spawnId < 0 || spawnId >= deathmatchSpawns.size()) {
            return false;
        }
        
        String spawnPath = "pg.lobbies." + lobbyId + ".arenas." + name + ".deathmatch." + spawnId;
        Settings.arenadata.set(spawnPath, null);
        
        try {
            deathmatchSpawns.remove(spawnId);
            Settings.arenadata.save(Settings.arenadatafile);
            return true;
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ex.getMessage());
            return false;
        }
    }

    // ===== LOOT MANAGEMENT =====
    
    /**
     * Get all chest locations in this arena.
     * 
     * @return A list of chest locations (may be empty)
     */
    public List<Location> getChestLocations() {
        return new ArrayList<>(chestLocations);
    }

    /**
     * Get all configured chest loot mappings.
     *
     * @return A copy of chest location to loot map
     */
    public Map<Location, ItemStack[]> getChests() {
        Map<Location, ItemStack[]> copy = new HashMap<>();
        for (Map.Entry<Location, ItemStack[]> entry : chestLoot.entrySet()) {
            copy.put(entry.getKey(), entry.getValue() == null ? null : entry.getValue().clone());
        }
        return copy;
    }
    
    /**
     * Get the loot items for a specific chest.
     * 
     * @param location The chest location
     * @return The items in the chest, or null if not configured
     */
    public ItemStack[] getChestLoot(Location location) {
        if (location == null) {
            return null;
        }
        ItemStack[] loot = chestLoot.get(location);
        // Return a copy to prevent external modification
        return loot != null ? loot.clone() : null;
    }
    
    /**
     * Add or update a chest with loot.
     * 
     * @param location The chest location
     * @param items The loot items for this chest
     * @return true if successfully added and saved to config
     */
    public boolean addChest(Location location, ItemStack[] items) {
        if (location == null) {
            return false;
        }
        
        int chestId = chestLocations.size();
        String chestPath = "pg.lobbies." + lobbyId + ".arenas." + name + ".chests." + chestId;
        
        Settings.arenadata.set(chestPath + ".location", location);
        if (items != null && items.length > 0) {
            Settings.arenadata.set(chestPath + ".items", items);
        }
        
        chestLocations.add(location);
        if (items != null) {
            chestLoot.put(location, items);
        }
        
        try {
            Settings.arenadata.save(Settings.arenadatafile);
            return true;
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ex.getMessage());
            chestLocations.remove(location);
            chestLoot.remove(location);
            return false;
        }
    }
    
    /**
     * Generate/refresh loot for all chests in this arena.
     * This method can be overridden by subclasses to provide custom loot generation.
     */
    public void generateLoot() {
        for (Location chestLoc : chestLocations) {
            // Default: use configured loot or generate random
            if (!chestLoot.containsKey(chestLoc)) {
                // No loot configured; can be overridden by subclasses
                // or integrated with LootTable system
            }
        }
    }

    // ===== VOTING MANAGEMENT =====
    
    /**
     * Record a vote for this arena (synchronized to prevent race conditions).
     * 
     * @param player The player voting for this arena
     */
    public synchronized void recordVote(Player player) {
        if (player != null) {
            voteCount++;
        }
    }
    
    /**
     * Get the current vote count for this arena.
     * 
     * @return The number of votes received
     */
    public synchronized int getVoteCount() {
        return voteCount;
    }
    
    /**
     * Check if this arena wins the vote against another arena.
     * 
     * @param other The arena to compare against
     * @return true if this arena has more votes than the other
     */
    public synchronized boolean wins(Arena other) {
        if (other == null) {
            return voteCount > 0;
        }
        return voteCount > other.getVoteCount();
    }
    
    /**
     * Reset votes for the next round.
     */
    public synchronized void resetVotes() {
        voteCount = 0;
    }

    // ===== METADATA & STATUS =====
    
    /**
     * Get the name of this arena.
     * 
     * @return The arena name (unique within its lobby)
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get the lobby ID this arena belongs to.
     * 
     * @return The lobby ID
     */
    public int getLobbyId() {
        return lobbyId;
    }
    
    /**
     * Get the minimum players for this arena.
     * Inherits from LobbyConfig if available, otherwise uses default.
     * 
     * @return The minimum player count
     */
    public int getMinPlayers() {
        return lobbyConfig != null ? lobbyConfig.getMinPlayers() : 2;
    }
    
    /**
     * Get the maximum players for this arena.
     * Inherits from LobbyConfig if available, otherwise uses default.
     * 
     * @return The maximum player count
     */
    public int getMaxPlayers() {
        return lobbyConfig != null ? lobbyConfig.getMaxPlayers() : 24;
    }
    
    /**
     * Set the status of this arena.
     * 
     * @param newStatus The new arena status
     */
    public void setStatus(ArenaStatus newStatus) {
        if (newStatus != null) {
            this.status = newStatus;
        }
    }
    
    /**
     * Get the current status of this arena.
     * 
     * @return The arena status
     */
    public ArenaStatus getStatus() {
        return status;
    }

    // ===== PARTICIPANT TELEPORTATION =====
    
    /**
     * Teleport all participants to random spawns in this arena.
     * 
     * @param participants The list of participants to teleport
     */
    public void teleport(ArrayList<Participant> participants) {
        for (int i = 0; i < participants.size(); i++) {
            Participant participant = participants.get(i);
            Location spawnLocation = spawns.get(i % spawns.size());
            participant.getPlayer().teleport(spawnLocation);
        }
    }
}
