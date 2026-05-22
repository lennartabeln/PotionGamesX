package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

/**
 * Handles water/lava flow events.
 * Prevents water and lava from flowing during games.
 */
public class BlockFlowEventListener implements Listener {
    private final PotionGames plugin;
    
    public BlockFlowEventListener(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockFlow(BlockFromToEvent e) {
        if (!plugin.isGameServer()) {
            return;
        }
        
        // Prevent water/lava flow during active games
        if (plugin.getGame().getActivePlayerCount() > 0) {
            e.setCancelled(true);
        }
    }
}
