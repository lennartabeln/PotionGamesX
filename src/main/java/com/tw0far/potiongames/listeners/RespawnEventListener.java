package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.entity.Player;

/**
 * Handles player respawn events.
 * Manages respawn location and game state after respawn.
 */
public class RespawnEventListener implements Listener {
    private final PotionGames plugin;
    
    public RespawnEventListener(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        
        if (!plugin.specPlayers.contains(p)) {
            return;
        }
        
        // Respawn location logic would go here
        // Sets spectator spawn location
    }
}
