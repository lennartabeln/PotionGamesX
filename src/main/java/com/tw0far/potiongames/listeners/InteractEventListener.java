package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.block.Block;

/**
 * Handles player interact events.
 * Manages interact restrictions and special interactions (signs, etc.).
 */
public class InteractEventListener implements Listener {
    private final PotionGames plugin;
    
    public InteractEventListener(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        
        boolean isActive = plugin.getGame().isActivePlayer(p);
        boolean isInLobby = plugin.getGame().isInLobby(p);
        
        if (!isActive && !isInLobby) {
            return;
        }
        
        Block block = e.getClickedBlock();
        
        if (block == null) {
            return;
        }
        
        // Check for sign interactions
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            
            // Handle join signs
            if (sign.getLine(0).contains("PG") || sign.getLine(0).contains("Join")) {
                e.setCancelled(true);
                // Sign interaction would be handled by other listeners
                return;
            }
        }
        
        // Allow normal interactions during games
        // (eating, drinking potions, etc. are handled by specific listeners)
    }
}
