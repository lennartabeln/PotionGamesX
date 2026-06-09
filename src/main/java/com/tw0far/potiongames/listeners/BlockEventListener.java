package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.GameStates;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

/**
 * Handles block-related events (multi-lobby only).
 * Extracted from monolithic Events.java.
 */
public class BlockEventListener implements Listener {
    private final PotionGames plugin;
    
    public BlockEventListener(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        var p = e.getPlayer();
        if (plugin.getConfigManager().isGameServer()) {
            String s = plugin.getGame().getPlayerLobby(p);
            if (s != null) {
                if (!plugin.getLobbyStateManager().isBuildAllowed(s)) {
                    if (plugin.getLobbyStateManager().getGameState(s) == GameStates.INGAME) {
                        if (isAllowedBreakBlock(e.getBlock().getType())) {
                            plugin.getBlockStateManager().trackBrokenBlock(e.getBlock().getLocation(), e.getBlock().getType());
                            e.setCancelled(false);
                        } else {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        var p = e.getPlayer();
        if (plugin.getConfigManager().isGameServer()) {
            String s = plugin.getGame().getPlayerLobby(p);
            if (s != null) {
                if (!plugin.getLobbyStateManager().isBuildAllowed(s)) {
                    if (plugin.getLobbyStateManager().getGameState(s) == GameStates.INGAME) {
                        if (isAllowedPlaceBlock(e.getBlock().getType())) {
                            plugin.getBlockStateManager().trackPlacedBlock(e.getBlock().getLocation(), e.getBlock().getType());
                            e.setCancelled(false);
                        } else if (e.getBlock().getType() == Material.TNT) {
                            e.setCancelled(true);
                            p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                            var tnt = e.getBlock().getWorld().spawnEntity(e.getBlock().getLocation(), EntityType.TNT);
                            TNTPrimed tnt2 = (TNTPrimed) tnt;
                            tnt2.setFuseTicks(40);
                        } else {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        var p = e.getPlayer();
        if (plugin.getConfigManager().isGameServer()) {
            String s = plugin.getGame().getPlayerLobby(p);
            if (s != null) {
                if (!plugin.getLobbyStateManager().isBuildAllowed(s)) {
                    if (plugin.getLobbyStateManager().getGameState(s) == GameStates.INGAME) {
                        if (e.getBucket() != Material.WATER_BUCKET && e.getBucket() != Material.LAVA_BUCKET) {
                            Block block = e.getBlockClicked().getRelative(e.getBlockFace());
                            Location loc = e.getBlockClicked().getRelative(e.getBlockFace()).getLocation();
                            plugin.getBlockStateManager().trackWaterBlock(loc, block.getBlockData());
                        }
                    }
                }
            }
        }
    }
    
    private boolean isAllowedBreakBlock(Material material) {
        return material == Material.COBWEB || material == Material.FIRE || material == Material.CAKE 
            || material == Material.SHORT_GRASS || material == Material.TALL_GRASS || material == Material.DEAD_BUSH 
            || material == Material.ACACIA_LEAVES || material == Material.BIRCH_LEAVES || material == Material.DARK_OAK_LEAVES 
            || material == Material.JUNGLE_LEAVES || material == Material.OAK_LEAVES || material == Material.SPRUCE_LEAVES 
            || material == Material.WARPED_FUNGUS || material == Material.CRIMSON_FUNGUS || material == Material.BROWN_MUSHROOM 
            || material == Material.RED_MUSHROOM;
    }
    
    private boolean isAllowedPlaceBlock(Material material) {
        return material == Material.COBWEB || material == Material.FIRE || material == Material.CAKE 
            || material == Material.SHORT_GRASS || material == Material.TALL_GRASS || material == Material.DEAD_BUSH 
            || material == Material.ACACIA_LEAVES || material == Material.BIRCH_LEAVES || material == Material.DARK_OAK_LEAVES 
            || material == Material.JUNGLE_LEAVES || material == Material.OAK_LEAVES || material == Material.SPRUCE_LEAVES 
            || material == Material.WARPED_FUNGUS || material == Material.CRIMSON_FUNGUS || material == Material.BROWN_MUSHROOM 
            || material == Material.RED_MUSHROOM;
    }
}
