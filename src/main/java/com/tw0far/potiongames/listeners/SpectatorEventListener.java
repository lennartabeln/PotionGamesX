package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;

/**
 * Handles player teleport/movement events.
 * Manages spectator mode interactions (invisibility, no-collision).
 */
public class SpectatorEventListener implements Listener {
    private final PotionGames plugin;
    
    public SpectatorEventListener(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onSpectatorMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        
        if (!plugin.specPlayers.contains(p)) {
            return;
        }
        
        // Spectator movement logic would go here
        // Could manage spectator-only zones or restrictions
    }
}
