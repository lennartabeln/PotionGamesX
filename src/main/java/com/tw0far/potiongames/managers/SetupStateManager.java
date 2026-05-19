package com.tw0far.potiongames.managers;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Manages setup mode players and their saved state
 * 
 * Responsibilities:
 * - Track which players are in setup mode
 * - Save/restore player inventory, armor, level, exp, location, gamemode
 * - Used by /pg setup command to temporarily modify player state
 */
public class SetupStateManager implements ISetupStateManager {
    
    private ArrayList<Player> setupPlayers = new ArrayList<>();
    
    // State backups for setup players
    private HashMap<String, ItemStack[]> inv = new HashMap<>();         // player name -> inventory
    private HashMap<String, ItemStack[]> armor = new HashMap<>();       // player name -> armor
    private HashMap<String, Integer> lvl = new HashMap<>();             // player name -> level
    private HashMap<String, Float> exp = new HashMap<>();               // player name -> exp
    private HashMap<String, Location> loc = new HashMap<>();            // player name -> location
    private HashMap<String, GameMode> gm = new HashMap<>();             // player name -> game mode
    private HashMap<String, Integer> selectedLobby = new HashMap<>();   // player name -> lobby id
    private HashMap<String, String> selectedArena = new HashMap<>();    // player name -> arena name
    
    @Override
    public void onEnable() {
        // Nothing to initialize
    }
    
    @Override
    public void onDisable() {
        // Cleanup on disable
        clearAll();
    }
    
    @Override
    public void reload() {
        // No reload needed for setup state
        clearAll();
    }
    
    public void clearAll() {
        setupPlayers.clear();
        inv.clear();
        armor.clear();
        lvl.clear();
        exp.clear();
        loc.clear();
        gm.clear();
        selectedLobby.clear();
        selectedArena.clear();
    }
    
    // ===== Setup Player Tracking =====
    
    @Override
    public void addSetupPlayer(Player player) {
        if (!setupPlayers.contains(player)) {
            setupPlayers.add(player);
        }
    }
    
    @Override
    public void removeSetupPlayer(Player player) {
        setupPlayers.remove(player);
        clearSelection(player);
    }
    
    @Override
    public boolean isSetupPlayer(Player player) {
        return setupPlayers.contains(player);
    }
    
    @Override
    public void clearAllSetupPlayers() {
        setupPlayers.clear();
        inv.clear();
        armor.clear();
        lvl.clear();
        exp.clear();
        loc.clear();
        gm.clear();
        selectedLobby.clear();
        selectedArena.clear();
    }

    @Override
    public void setSelectedLobby(Player player, Integer lobbyId) {
        if (player == null) {
            return;
        }
        if (lobbyId == null) {
            selectedLobby.remove(player.getName());
        } else {
            selectedLobby.put(player.getName(), lobbyId);
        }
    }

    @Override
    public Integer getSelectedLobby(Player player) {
        return player == null ? null : selectedLobby.get(player.getName());
    }

    @Override
    public void removeSelectedLobby(Player player) {
        if (player != null) {
            selectedLobby.remove(player.getName());
        }
    }

    @Override
    public void setSelectedArena(Player player, String arenaName) {
        if (player == null) {
            return;
        }
        if (arenaName == null || arenaName.isBlank()) {
            selectedArena.remove(player.getName());
        } else {
            selectedArena.put(player.getName(), arenaName);
        }
    }

    @Override
    public String getSelectedArena(Player player) {
        return player == null ? null : selectedArena.get(player.getName());
    }

    @Override
    public void removeSelectedArena(Player player) {
        if (player != null) {
            selectedArena.remove(player.getName());
        }
    }

    @Override
    public void clearSelection(Player player) {
        removeSelectedLobby(player);
        removeSelectedArena(player);
    }
    
    // ===== Inventory Backup =====
    
    @Override
    public void savePlayerInventory(Player player, ItemStack[] inventory) {
        inv.put(player.getName(), inventory);
    }
    
    @Override
    public ItemStack[] getPlayerInventory(Player player) {
        return inv.get(player.getName());
    }
    
    @Override
    public void removeSavedInventory(Player player) {
        inv.remove(player.getName());
    }
    
    // ===== Armor Backup =====
    
    @Override
    public void savePlayerArmor(Player player, ItemStack[] armor) {
        this.armor.put(player.getName(), armor);
    }
    
    @Override
    public ItemStack[] getPlayerArmor(Player player) {
        return this.armor.get(player.getName());
    }
    
    @Override
    public void removeSavedArmor(Player player) {
        this.armor.remove(player.getName());
    }
    
    // ===== Level Backup =====
    
    @Override
    public void savePlayerLevel(Player player, int level) {
        lvl.put(player.getName(), level);
    }
    
    @Override
    public Integer getPlayerLevel(Player player) {
        return lvl.get(player.getName());
    }
    
    @Override
    public void removeSavedLevel(Player player) {
        lvl.remove(player.getName());
    }
    
    // ===== Experience Backup =====
    
    @Override
    public void savePlayerExp(Player player, float experience) {
        exp.put(player.getName(), experience);
    }
    
    @Override
    public Float getPlayerExp(Player player) {
        return exp.get(player.getName());
    }
    
    @Override
    public void removeSavedExp(Player player) {
        exp.remove(player.getName());
    }
    
    // ===== Location Backup =====
    
    @Override
    public void savePlayerLocation(Player player, Location location) {
        loc.put(player.getName(), location);
    }
    
    @Override
    public Location getPlayerLocation(Player player) {
        return loc.get(player.getName());
    }
    
    @Override
    public void removeSavedLocation(Player player) {
        loc.remove(player.getName());
    }
    
    // ===== GameMode Backup =====
    
    @Override
    public void savePlayerGameMode(Player player, GameMode gameMode) {
        gm.put(player.getName(), gameMode);
    }
    
    @Override
    public GameMode getPlayerGameMode(Player player) {
        return gm.get(player.getName());
    }
    
    @Override
    public void removeSavedGameMode(Player player) {
        gm.remove(player.getName());
    }
}
