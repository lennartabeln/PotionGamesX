package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.GameStates;
import com.tw0far.potiongames.models.Lobby;
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
        // Arena worlds keep their blocks between rounds (legacy pg.worlds).
        if (plugin.getGame().isProtectedArenaWorld(e.getBlock().getWorld())) {
            e.setCancelled(true);
        }
    }
}
