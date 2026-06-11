package com.tw0far.potiongames.managers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import java.util.*;

/**
 * Implementation of IBlockStateManager.
 */
public class BlockStateManager implements IBlockStateManager {

    // Global block tracking
    private final Map<Location, Material> placedBlocks = new HashMap<>();
    private final Map<Location, Material> breakedBlocks = new HashMap<>();
    private final Map<Location, BlockData> waterBlocks = new HashMap<>();
    private final Map<Location, Block> liquidPlaced = new HashMap<>();

    // Per-lobby block tracking
    private final Map<String, Map<Location, Material>> lobbyPlacedBlocks = new HashMap<>();
    private final Map<String, Map<Location, Material>> lobbyBreakedBlocks = new HashMap<>();
    private final Map<String, Map<Location, BlockData>> lobbyWaterBlocks = new HashMap<>();

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
        clearAll();
    }

    // ===== GLOBAL BLOCK TRACKING =====
    @Override
    public void trackPlacedBlock(Location loc, Material material) {
        placedBlocks.put(loc, material);
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
    public Collection<Location> getBrokenBlocks() {
        return new HashSet<>(breakedBlocks.keySet());
    }

    // ===== GLOBAL WATER BLOCKS =====
    @Override
    public void trackWaterBlock(Location loc, BlockData data) {
        waterBlocks.put(loc, data);
    }

    // ===== GLOBAL LIQUID PLACED =====
    @Override
    public void trackLiquidPlaced(Location loc, Block block) {
        liquidPlaced.put(loc, block);
    }

    // ===== PER-LOBBY BLOCK TRACKING =====
    @Override
    public void trackLobbyPlacedBlock(String lobbyId, Location loc, Material material) {
        Map<Location, Material> lobbyBlocks = lobbyPlacedBlocks.computeIfAbsent(lobbyId, k -> new HashMap<>());
        lobbyBlocks.put(loc, material);
    }

    @Override
    public void trackLobbyBrokenBlock(String lobbyId, Location loc, Material material) {
        Map<Location, Material> lobbyBlocks = lobbyBreakedBlocks.computeIfAbsent(lobbyId, k -> new HashMap<>());
        lobbyBlocks.put(loc, material);
    }

    @Override
    public void trackLobbyWaterBlock(String lobbyId, Location loc, BlockData data) {
        Map<Location, BlockData> lobbyBlocks = lobbyWaterBlocks.computeIfAbsent(lobbyId, k -> new HashMap<>());
        lobbyBlocks.put(loc, data);
    }

    // ===== BATCH OPERATIONS =====
    @Override
    public void clearAll() {
        placedBlocks.clear();
        breakedBlocks.clear();
        waterBlocks.clear();
        liquidPlaced.clear();
        lobbyPlacedBlocks.clear();
        lobbyBreakedBlocks.clear();
        lobbyWaterBlocks.clear();
    }
}
