package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.entity.Player;

/**
 * Handles food level change events.
 * Manages hunger during games.
 */
public class FoodLevelEventListener implements Listener {
    private final PotionGamesX plugin;
    
    public FoodLevelEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        
        Player p = (Player) e.getEntity();
        
        if (!plugin.getGame().isActivePlayer(p)) {
            return;
        }
        
        // Allow hunger changes during games (natural hunger is managed by games)
        // If hunger is dropping rapidly, player is using items - allow it
        // This lets soup healing work and natural hunger progression happen
    }
}
