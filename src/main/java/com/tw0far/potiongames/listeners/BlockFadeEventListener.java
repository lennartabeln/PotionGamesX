package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;

/**
 * Handles block fade/decay events.
 * Prevents natural block decay (leaves, ice, etc.) during games.
 */
public class BlockFadeEventListener implements Listener {
    private final PotionGames plugin;
    
    public BlockFadeEventListener(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
        if (!plugin.getConfigManager().isGameServer()) {
            return;
        }
        
        // Prevent block fade during active games
        if (plugin.getGame().getActivePlayerCount() > 0) {
            e.setCancelled(true);
        }
    }
}
