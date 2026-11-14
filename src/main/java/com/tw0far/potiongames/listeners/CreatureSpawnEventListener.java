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
        // Arena worlds stay free of mobs between rounds as well (legacy
        // pg.worlds). The old "any player is in a lobby" check froze mob
        // spawning on the whole server, including unrelated worlds.
        if (e.getLocation() != null && plugin.getGame().isProtectedArenaWorld(e.getLocation().getWorld())) {
            e.setCancelled(true);
        }
    }
}
