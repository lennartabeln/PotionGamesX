package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;

/**
 * Handles leaves decay events.
 * Prevents leaves from decaying during active games.
 */
public class LeavesDecayEventListener implements Listener {
    private final PotionGamesX plugin;
    
    public LeavesDecayEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent e) {
        if (!plugin.getConfigManager().isGameServer()) {
            return;
        }
        
        // Prevent leaves decay during active games
        if (plugin.getGame().getActivePlayerCount() > 0) {
            e.setCancelled(true);
        }
    }
}
