package com.tw0far.potiongames.managers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import java.util.*;

/**
 * Manager for block state tracking.
 */
public interface IBlockStateManager extends IManager {

    // ===== GLOBAL BLOCK TRACKING =====
    void trackPlacedBlock(Location loc, Material material);
    Collection<Location> getPlacedBlocks();

    // ===== GLOBAL BROKEN BLOCKS =====
    void trackBrokenBlock(Location loc, Material material);
    Collection<Location> getBrokenBlocks();

    // ===== GLOBAL WATER BLOCKS =====
    void trackWaterBlock(Location loc, BlockData data);

    // ===== GLOBAL LIQUID PLACED =====
    void trackLiquidPlaced(Location loc, Block block);

    // ===== PER-LOBBY BLOCK TRACKING =====
    void trackLobbyPlacedBlock(String lobbyId, Location loc, Material material);
    void trackLobbyBrokenBlock(String lobbyId, Location loc, Material material);
    void trackLobbyWaterBlock(String lobbyId, Location loc, BlockData data);

    // ===== BATCH OPERATIONS =====
    void clearAll();
}
