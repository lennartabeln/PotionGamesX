package com.tw0far.potiongames.managers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import java.util.*;

/**
 * Manager for block state and chest inventory tracking.
 * Consolidates 12 HashMaps for block/chest management.
 * 
 * Manages:
 * - Global: chests, placedBlocks, breakedBlocks, waterBlocks, liquidPlaced
 * - Per-lobby: lobbychests, lobbychestsdata, lobbyPlacedBlocks, lobbyBreakedBlocks, 
 *              lobbyWaterBlocks, lobbyLiquidPlaced, lobbyLiquidPlacedData
 */
public interface IBlockStateManager extends IManager {
    
    // ===== GLOBAL BLOCK TRACKING =====
    /**
     * Track a placed block
     */
    void trackPlacedBlock(Location loc, Material material);
    
    /**
     * Get material of placed block
     */
    Material getPlacedBlockMaterial(Location loc);
    
    /**
     * Remove placed block tracking
     */
    void removePlacedBlock(Location loc);
    
    /**
     * Get all placed blocks
     */
    Collection<Location> getPlacedBlocks();
    
    // ===== GLOBAL BROKEN BLOCKS =====
    /**
     * Track a broken block
     */
    void trackBrokenBlock(Location loc, Material material);
    
    /**
     * Get material of broken block
     */
    Material getBrokenBlockMaterial(Location loc);
    
    /**
     * Remove broken block tracking
     */
    void removeBrokenBlock(Location loc);
    
    /**
     * Get all broken blocks
     */
    Collection<Location> getBrokenBlocks();
    
    // ===== GLOBAL WATER BLOCKS =====
    /**
     * Track water block data
     */
    void trackWaterBlock(Location loc, BlockData data);
    
    /**
     * Get water block data
     */
    BlockData getWaterBlockData(Location loc);
    
    /**
     * Remove water block tracking
     */
    void removeWaterBlock(Location loc);
    
    /**
     * Get all water blocks
     */
    Collection<Location> getWaterBlocks();
    
    // ===== GLOBAL LIQUID PLACED =====
    /**
     * Track placed liquid block
     */
    void trackLiquidPlaced(Location loc, Block block);
    
    /**
     * Get placed liquid block
     */
    Block getLiquidPlaced(Location loc);
    
    /**
     * Remove liquid block tracking
     */
    void removeLiquidPlaced(Location loc);
    
    /**
     * Get all placed liquid blocks
     */
    Collection<Location> getLiquidPlacedLocations();
    
    // ===== GLOBAL CHESTS =====
    /**
     * Get chest inventory
     */
    org.bukkit.inventory.Inventory getChestInventory(Location loc);
    
    /**
     * Set chest inventory
     */
    void setChestInventory(Location loc, org.bukkit.inventory.Inventory inventory);
    
    /**
     * Remove chest tracking
     */
    void removeChest(Location loc);
    
    /**
     * Get all tracked chests
     */
    Collection<Location> getChests();
    
    // ===== PER-LOBBY BLOCK TRACKING =====
    /**
     * Track placed block in lobby
     */
    void trackLobbyPlacedBlock(String lobbyId, Location loc, Material material);
    
    /**
     * Get placed block in lobby
     */
    Material getLobbyPlacedBlock(String lobbyId, Location loc);
    
    /**
     * Track broken block in lobby
     */
    void trackLobbyBrokenBlock(String lobbyId, Location loc, Material material);
    
    /**
     * Get broken block in lobby
     */
    Material getLobbyBrokenBlock(String lobbyId, Location loc);
    
    /**
     * Track water block in lobby
     */
    void trackLobbyWaterBlock(String lobbyId, Location loc, BlockData data);
    
    /**
     * Get water block in lobby
     */
    BlockData getLobbyWaterBlockData(String lobbyId, Location loc);
    
    /**
     * Track liquid placed in lobby
     */
    void trackLobbyLiquidPlaced(String lobbyId, Location loc, Block block);
    
    /**
     * Get liquid placed in lobby
     */
    Block getLobbyLiquidPlaced(String lobbyId, Location loc);
    
    /**
     * Get all placed blocks in lobby
     */
    Collection<Location> getLobbyPlacedBlocks(String lobbyId);
    
    /**
     * Get all broken blocks in lobby
     */
    Collection<Location> getLobbyBrokenBlocks(String lobbyId);
    
    /**
     * Get all water blocks in lobby
     */
    Collection<Location> getLobbyWaterBlocks(String lobbyId);
    
    /**
     * Get all liquid placed in lobby
     */
    Collection<Location> getLobbyLiquidPlaced(String lobbyId);
    
    // ===== PER-LOBBY CHESTS =====
    /**
     * Track chest in lobby
     */
    void trackLobbyChest(String lobbyId, Location loc, String chestName);
    
    /**
     * Get chest name in lobby
     */
    String getLobbyChestName(Location loc);
    
    /**
     * Set chest inventory in lobby
     */
    void setLobbyChestInventory(String lobbyId, Location loc, org.bukkit.inventory.Inventory inventory);
    
    /**
     * Get chest inventory in lobby
     */
    org.bukkit.inventory.Inventory getLobbyChestInventory(String lobbyId, Location loc);
    
    /**
     * Get all chests in lobby
     */
    Collection<Location> getLobbyChests(String lobbyId);
    
    // ===== BATCH OPERATIONS =====
    /**
     * Clear all block tracking for a lobby
     */
    void clearLobby(String lobbyId);
    
    /**
     * Clear all global block tracking
     */
    void clearGlobal();
    
    /**
     * Clear all block state
     */
    void clearAll();
}
