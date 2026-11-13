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
        // Only the arena worlds of a running round are protected. The previous
        // "any player is in a lobby" check froze mob spawning on the whole
        // server, including worlds that have nothing to do with the plugin.
        if (e.getLocation() != null && plugin.getGame().isRoundWorld(e.getLocation().getWorld())) {
            e.setCancelled(true);
        }
    }
}
