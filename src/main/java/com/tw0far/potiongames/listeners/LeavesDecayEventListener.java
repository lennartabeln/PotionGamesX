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
        // Prevent leaves decay in the worlds of a running round only
        for (Lobby lobby : plugin.getGame().getLobbies()) {
            if ((lobby.getState() == GameStates.INGAME || lobby.getState() == GameStates.DEATHMATCH)
                    && lobby.isGameWorld(e.getBlock().getWorld())) {
                e.setCancelled(true);
                return;
            }
        }
    }
}
