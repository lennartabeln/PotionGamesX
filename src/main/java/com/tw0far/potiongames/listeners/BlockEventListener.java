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
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.BlockFromToEvent;
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
        if (plugin.isGameServer()) {
            // Try to get from active player list or by lobby ID
            String s = null;
            if (plugin.game.isActivePlayer(p)) {
                // Single-lobby mode - player is active
                s = "0"; // Single-lobby mode uses implicit lobby
            } else {
                // Multi-lobby mode - get from game
                s = plugin.game.getPlayerLobby(p);
            }
            
            if (s != null) {
                if (!plugin.lobbyBuild.get(s)) {
                    if (plugin.lobbyStates.get(s) == GameStates.INGAME) {
                        if (isAllowedBreakBlock(e.getBlock().getType())) {
                            plugin.lobbyBreakedBlocksData.put(e.getBlock().getLocation(), e.getBlock().getType());
                            plugin.lobbyBreakedBlocks.put(s, plugin.lobbyBreakedBlocksData);
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
        if (plugin.isGameServer()) {
            // Try to get from active player list or by lobby ID
            String s = null;
            if (plugin.game.isActivePlayer(p)) {
                // Single-lobby mode - player is active
                s = "0"; // Single-lobby mode uses implicit lobby
            } else {
                // Multi-lobby mode - get from game
                s = plugin.game.getPlayerLobby(p);
            }
            
            if (s != null) {
                if (!plugin.lobbyBuild.get(s)) {
                    if (plugin.lobbyStates.get(s) == GameStates.INGAME) {
                        if (isAllowedPlaceBlock(e.getBlock().getType())) {
                            plugin.lobbyPlacedBlocksData.put(e.getBlock().getLocation(), e.getBlock().getType());
                            plugin.lobbyPlacedBlocks.put(s, plugin.lobbyPlacedBlocksData);
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
    public void onBlockFade(BlockFadeEvent event) {
        if (plugin.isGameServer()) {
            if (plugin.worlds.contains(event.getBlock().getWorld().getName())) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        if (plugin.isGameServer()) {
            if (plugin.worlds.contains(event.getBlock().getWorld().getName())) {
                switch (event.getBlock().getType()) {
                    case ACACIA_LEAVES, BIRCH_LEAVES, DARK_OAK_LEAVES, JUNGLE_LEAVES, OAK_LEAVES, SPRUCE_LEAVES ->
                            event.setCancelled(true);
                    default -> event.setCancelled(false);
                }
            }
        }
    }
    
    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        if (plugin.isGameServer()) {
            if (plugin.worlds.contains(event.getBlock().getWorld().getName())) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        var p = e.getPlayer();
        if (plugin.isGameServer()) {
            if (plugin.game.isActivePlayer(p) || plugin.game.isInLobby(p)) {
                if (plugin.isLobbySystem()) {
                    String s = plugin.game.getPlayerLobby(p);
                    if (s != null) {
                        if (!plugin.lobbyBuild.get(s)) {
                            if (plugin.lobbyStates.get(s) == GameStates.INGAME) {
                                if (e.getBucket() != Material.WATER_BUCKET || e.getBucket() != Material.LAVA_BUCKET) {
                                    Block block = e.getBlockClicked().getRelative(e.getBlockFace());
                                    Location loc = e.getBlockClicked().getRelative(e.getBlockFace()).getLocation();
                                    plugin.lobbyLiquidPlacedData.put(loc, block);
                                    plugin.lobbyLiquidPlaced.put(s, plugin.lobbyLiquidPlacedData);
                                }
                            }
                        }
                    }
                } else {
                    if (!plugin.isBuild()) {
                        if (plugin.getGamestate() == GameStates.INGAME) {
                            if (e.getBucket() != Material.WATER_BUCKET || e.getBucket() != Material.LAVA_BUCKET) {
                                Block block = e.getBlockClicked().getRelative(e.getBlockFace());
                                Location loc = e.getBlockClicked().getRelative(e.getBlockFace()).getLocation();
                                plugin.liquidPlaced.put(loc, block);
                            }
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
