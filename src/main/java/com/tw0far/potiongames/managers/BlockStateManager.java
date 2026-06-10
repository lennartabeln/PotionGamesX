package com.tw0far.potiongames.managers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.Inventory;
import java.util.*;

/**
 * Implementation of IBlockStateManager.
 * Consolidates 12 HashMaps for block/chest tracking from PotionGamesX:
 * - Global: chests, placedBlocks, breakedBlocks, waterBlocks, liquidPlaced
 * - Per-lobby: lobbychests, lobbychestsdata, lobbyPlacedBlocks, lobbyBreakedBlocks,
 *              lobbyWaterBlocks, lobbyLiquidPlaced, lobbyLiquidPlacedData
 */
public class BlockStateManager implements IBlockStateManager {
    
    // Global block tracking
    private final Map<Location, Inventory> chests = new HashMap<>();
    private final Map<Location, Material> placedBlocks = new HashMap<>();
    private final Map<Location, Material> breakedBlocks = new HashMap<>();
    private final Map<Location, BlockData> waterBlocks = new HashMap<>();
    private final Map<Location, Block> liquidPlaced = new HashMap<>();
    
    // Per-lobby block tracking
    private final Map<String, Map<Location, Material>> lobbyPlacedBlocks = new HashMap<>();
    private final Map<String, Map<Location, Material>> lobbyBreakedBlocks = new HashMap<>();
    private final Map<String, Map<Location, BlockData>> lobbyWaterBlocks = new HashMap<>();
    private final Map<String, Map<Location, Block>> lobbyLiquidPlaced = new HashMap<>();
    
    // Per-lobby chest tracking
    private final Map<Location, String> lobbychests = new HashMap<>();
    private final Map<Location, Inventory> lobbychestsdata = new HashMap<>();
    private final Map<String, Map<Location, Inventory>> lobbyChestsDataMap = new HashMap<>();
    
    @Override
    public void onEnable() {
        // No initialization needed
    }
    
    @Override
    public void onDisable() {
        clearAll();
    }
    
    @Override
    public void reload() {
        clearAll();
    }
    
    // ===== GLOBAL BLOCK TRACKING =====
    @Override
    public void trackPlacedBlock(Location loc, Material material) {
        placedBlocks.put(loc, material);
    }
    
    @Override
    public Material getPlacedBlockMaterial(Location loc) {
        return placedBlocks.get(loc);
    }
    
    @Override
    public void removePlacedBlock(Location loc) {
        placedBlocks.remove(loc);
    }
    
    @Override
    public Collection<Location> getPlacedBlocks() {
        return new HashSet<>(placedBlocks.keySet());
    }
    
    // ===== GLOBAL BROKEN BLOCKS =====
    @Override
    public void trackBrokenBlock(Location loc, Material material) {
        breakedBlocks.put(loc, material);
    }
    
    @Override
    public Material getBrokenBlockMaterial(Location loc) {
        return breakedBlocks.get(loc);
    }
    
    @Override
    public void removeBrokenBlock(Location loc) {
        breakedBlocks.remove(loc);
    }
    
    @Override
    public Collection<Location> getBrokenBlocks() {
        return new HashSet<>(breakedBlocks.keySet());
    }
    
    // ===== GLOBAL WATER BLOCKS =====
    @Override
    public void trackWaterBlock(Location loc, BlockData data) {
        waterBlocks.put(loc, data);
    }
    
    @Override
    public BlockData getWaterBlockData(Location loc) {
        return waterBlocks.get(loc);
    }
    
    @Override
    public void removeWaterBlock(Location loc) {
        waterBlocks.remove(loc);
    }
    
    @Override
    public Collection<Location> getWaterBlocks() {
        return new HashSet<>(waterBlocks.keySet());
    }
    
    // ===== GLOBAL LIQUID PLACED =====
    @Override
    public void trackLiquidPlaced(Location loc, Block block) {
        liquidPlaced.put(loc, block);
    }
    
    @Override
    public Block getLiquidPlaced(Location loc) {
        return liquidPlaced.get(loc);
    }
    
    @Override
    public void removeLiquidPlaced(Location loc) {
        liquidPlaced.remove(loc);
    }
    
    @Override
    public Collection<Location> getLiquidPlacedLocations() {
        return new HashSet<>(liquidPlaced.keySet());
    }
    
    // ===== GLOBAL CHESTS =====
    @Override
    public Inventory getChestInventory(Location loc) {
        return chests.get(loc);
    }
    
    @Override
    public void setChestInventory(Location loc, Inventory inventory) {
        chests.put(loc, inventory);
    }
    
    @Override
    public void removeChest(Location loc) {
        chests.remove(loc);
    }
    
    @Override
    public Collection<Location> getChests() {
        return new HashSet<>(chests.keySet());
    }
    
    // ===== PER-LOBBY BLOCK TRACKING =====
    @Override
    public void trackLobbyPlacedBlock(String lobbyId, Location loc, Material material) {
        Map<Location, Material> lobbyBlocks = lobbyPlacedBlocks.computeIfAbsent(lobbyId, k -> new HashMap<>());
        lobbyBlocks.put(loc, material);
    }
    
    @Override
    public Material getLobbyPlacedBlock(String lobbyId, Location loc) {
        Map<Location, Material> lobbyBlocks = lobbyPlacedBlocks.get(lobbyId);
        if (lobbyBlocks == null) return null;
        return lobbyBlocks.get(loc);
    }
    
    @Override
    public void trackLobbyBrokenBlock(String lobbyId, Location loc, Material material) {
        Map<Location, Material> lobbyBlocks = lobbyBreakedBlocks.computeIfAbsent(lobbyId, k -> new HashMap<>());
        lobbyBlocks.put(loc, material);
    }
    
    @Override
    public Material getLobbyBrokenBlock(String lobbyId, Location loc) {
        Map<Location, Material> lobbyBlocks = lobbyBreakedBlocks.get(lobbyId);
        if (lobbyBlocks == null) return null;
        return lobbyBlocks.get(loc);
    }
    
    @Override
    public void trackLobbyWaterBlock(String lobbyId, Location loc, BlockData data) {
        Map<Location, BlockData> lobbyBlocks = lobbyWaterBlocks.computeIfAbsent(lobbyId, k -> new HashMap<>());
        lobbyBlocks.put(loc, data);
    }
    
    @Override
    public BlockData getLobbyWaterBlockData(String lobbyId, Location loc) {
        Map<Location, BlockData> lobbyBlocks = lobbyWaterBlocks.get(lobbyId);
        if (lobbyBlocks == null) return null;
        return lobbyBlocks.get(loc);
    }
    
    @Override
    public void trackLobbyLiquidPlaced(String lobbyId, Location loc, Block block) {
        Map<Location, Block> lobbyLiquids = lobbyLiquidPlaced.computeIfAbsent(lobbyId, k -> new HashMap<>());
        lobbyLiquids.put(loc, block);
    }
    
    @Override
    public Block getLobbyLiquidPlaced(String lobbyId, Location loc) {
        Map<Location, Block> lobbyLiquids = lobbyLiquidPlaced.get(lobbyId);
        if (lobbyLiquids == null) return null;
        return lobbyLiquids.get(loc);
    }
    
    @Override
    public Collection<Location> getLobbyPlacedBlocks(String lobbyId) {
        Map<Location, Material> lobbyBlocks = lobbyPlacedBlocks.get(lobbyId);
        if (lobbyBlocks == null) return new HashSet<>();
        return new HashSet<>(lobbyBlocks.keySet());
    }
    
    @Override
    public Collection<Location> getLobbyBrokenBlocks(String lobbyId) {
        Map<Location, Material> lobbyBlocks = lobbyBreakedBlocks.get(lobbyId);
        if (lobbyBlocks == null) return new HashSet<>();
        return new HashSet<>(lobbyBlocks.keySet());
    }
    
    @Override
    public Collection<Location> getLobbyWaterBlocks(String lobbyId) {
        Map<Location, BlockData> lobbyBlocks = lobbyWaterBlocks.get(lobbyId);
        if (lobbyBlocks == null) return new HashSet<>();
        return new HashSet<>(lobbyBlocks.keySet());
    }
    
    @Override
    public Collection<Location> getLobbyLiquidPlaced(String lobbyId) {
        Map<Location, Block> lobbyLiquids = lobbyLiquidPlaced.get(lobbyId);
        if (lobbyLiquids == null) return new HashSet<>();
        return new HashSet<>(lobbyLiquids.keySet());
    }
    
    // ===== PER-LOBBY CHESTS =====
    @Override
    public void trackLobbyChest(String lobbyId, Location loc, String chestName) {
        lobbychests.put(loc, chestName);
    }
    
    @Override
    public String getLobbyChestName(Location loc) {
        return lobbychests.get(loc);
    }
    
    @Override
    public void setLobbyChestInventory(String lobbyId, Location loc, Inventory inventory) {
        lobbychestsdata.put(loc, inventory);
        Map<Location, Inventory> lobbyChests = lobbyChestsDataMap.computeIfAbsent(lobbyId, k -> new HashMap<>());
        lobbyChests.put(loc, inventory);
    }
    
    @Override
    public Inventory getLobbyChestInventory(String lobbyId, Location loc) {
        Map<Location, Inventory> lobbyChests = lobbyChestsDataMap.get(lobbyId);
        if (lobbyChests == null) {
            return lobbychestsdata.get(loc);  // Fallback to global data
        }
        return lobbyChests.get(loc);
    }
    
    @Override
    public Collection<Location> getLobbyChests(String lobbyId) {
        Map<Location, Inventory> lobbyChests = lobbyChestsDataMap.get(lobbyId);
        if (lobbyChests == null) return new HashSet<>();
        return new HashSet<>(lobbyChests.keySet());
    }
    
    // ===== BATCH OPERATIONS =====
    @Override
    public void clearLobby(String lobbyId) {
        lobbyPlacedBlocks.remove(lobbyId);
        lobbyBreakedBlocks.remove(lobbyId);
        lobbyWaterBlocks.remove(lobbyId);
        lobbyLiquidPlaced.remove(lobbyId);
        lobbyChestsDataMap.remove(lobbyId);
    }
    
    @Override
    public void clearGlobal() {
        chests.clear();
        placedBlocks.clear();
        breakedBlocks.clear();
        waterBlocks.clear();
        liquidPlaced.clear();
        lobbychests.clear();
        lobbychestsdata.clear();
    }
    
    @Override
    public void clearAll() {
        clearGlobal();
        lobbyPlacedBlocks.clear();
        lobbyBreakedBlocks.clear();
        lobbyWaterBlocks.clear();
        lobbyLiquidPlaced.clear();
        lobbyChestsDataMap.clear();
    }
}
