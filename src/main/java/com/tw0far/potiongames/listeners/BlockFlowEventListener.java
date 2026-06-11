package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

/**
 * Handles water/lava flow events.
 * Prevents water and lava from flowing during games.
 */
public class BlockFlowEventListener implements Listener {
    private final PotionGamesX plugin;

    public BlockFlowEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockFlow(BlockFromToEvent e) {
        // Prevent water/lava flow during active games
        if (plugin.getGame().getActivePlayerCount() > 0) {
            e.setCancelled(true);
        }
    }
}
