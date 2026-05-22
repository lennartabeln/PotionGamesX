package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Handles creature spawn events.
 * Prevents mobs from spawning during games if configured.
 */
public class CreatureSpawnEventListener implements Listener {
    private final PotionGames plugin;
    
    public CreatureSpawnEventListener(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if (!plugin.isGameServer()) {
            return;
        }
        
        // Prevent mob spawning during active games
        if (plugin.getGame().getActivePlayerCount() > 0) {
            e.setCancelled(true);
        }
    }
}
