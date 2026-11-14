package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.GameStates;
import com.tw0far.potiongames.models.Lobby;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;

/**
 * Handles block fade/decay events.
 * Prevents natural block decay (leaves, ice, etc.) during games.
 */
public class BlockFadeEventListener implements Listener {
    private final PotionGamesX plugin;

    public BlockFadeEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
        if (plugin.getGame().isProtectedArenaWorld(e.getBlock().getWorld())) {
            e.setCancelled(true);
        }
    }
}
