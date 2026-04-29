package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.entity.Player;

/**
 * Handles sign change events.
 * Manages join signs and other custom sign interactions.
 */
public class SignChangeEventListener implements Listener {
    private final PotionGames plugin;
    
    public SignChangeEventListener(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        Player p = e.getPlayer();
        
        // Sign change logic would go here
        // Handles custom sign creation and formatting
    }
}
