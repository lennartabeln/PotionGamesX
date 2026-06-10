package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * Handles entity explosion events.
 * Manages TNT explosions and other entity explosions during games.
 */
public class ExplosionEventListener implements Listener {
    private final PotionGamesX plugin;
    
    public ExplosionEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (!plugin.getConfigManager().isGameServer()) {
            return;
        }
        
        // Check if there's an active game
        boolean gameActive = plugin.getGame().getActivePlayerCount() > 0;
        
        if (!gameActive) {
            return;
        }
        
        // During games, allow explosions but prevent block destruction if configured
        // TNT explosions should be allowed for PvP purposes
        // This allows players to damage each other with explosives
    }
}
