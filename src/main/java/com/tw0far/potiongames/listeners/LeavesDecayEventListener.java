package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;

/**
 * Handles leaves decay events.
 * Prevents leaves from decaying during active games.
 */
public class LeavesDecayEventListener implements Listener {
    private final PotionGames plugin;
    
    public LeavesDecayEventListener(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent e) {
        if (!plugin.isGameServer()) {
            return;
        }
        
        // Prevent leaves decay during active games
        if (plugin.getGame().getActivePlayerCount() > 0) {
            e.setCancelled(true);
        }
    }
}
