package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Handles creature spawn events.
 * Prevents mobs from spawning during games if configured.
 */
public class CreatureSpawnEventListener implements Listener {
    private final PotionGamesX plugin;
    
    public CreatureSpawnEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        // Prevent mob spawning during active games
        if (plugin.getGame().getActivePlayerCount() > 0) {
            e.setCancelled(true);
        }
    }
}
