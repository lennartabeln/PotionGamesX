package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;

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
        
        if (!plugin.pgPlayers.contains(p) && !plugin.playerLobby.containsKey(p)) {
            return;
        }
        
        // Interact handling logic would go here
        // Manages special blocks and interactions
    }
}
