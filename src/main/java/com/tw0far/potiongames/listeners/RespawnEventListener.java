package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.entity.Player;

/**
 * Handles player respawn events.
 * Manages respawn location and game state after respawn.
 */
public class RespawnEventListener implements Listener {
    private final PotionGamesX plugin;
    
    public RespawnEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        
        if (!plugin.getGame().isSpectatorPlayer(p)) {
            return;
        }
        
        // Spectators respawn at their current location (no special handling needed)
        // The spectator respawn location is handled by the game state system
    }
}
