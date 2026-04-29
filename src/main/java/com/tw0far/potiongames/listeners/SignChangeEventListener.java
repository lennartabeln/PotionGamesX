package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;

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
        
        String line1 = e.getLine(0);
        
        if (line1 == null) {
            return;
        }
        
        // Check for PotionGames signs
        if (line1.equalsIgnoreCase("[PG]") || line1.equalsIgnoreCase("[PotionGames]")) {
            // Check if player has permission
            if (!p.hasPermission("pg.sign")) {
                e.setCancelled(true);
                p.sendMessage("§cYou don't have permission to create PotionGames signs!");
                return;
            }
            
            // Format the sign with colors
            e.setLine(0, ChatColor.GOLD + "[PG]");
            e.setLine(1, ChatColor.BLUE + e.getLine(1)); // Lobby ID or action
            e.setLine(2, ChatColor.AQUA + e.getLine(2)); // Additional info
            e.setLine(3, ChatColor.GREEN + e.getLine(3)); // Additional info
            
            p.sendMessage("§aSign created successfully!");
        }
    }
}
