package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * Handles entity explosion events.
 * Manages TNT explosions and other entity explosions during games.
 */
public class ExplosionEventListener implements Listener {
    private final PotionGames plugin;
    
    public ExplosionEventListener(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (!plugin.isGameServer()) {
            return;
        }
        
        // Explosion handling logic would go here
        // Manages TNT and other explosions during games
    }
}
