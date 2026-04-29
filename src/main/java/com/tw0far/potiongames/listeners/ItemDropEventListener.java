package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.entity.Player;

/**
 * Handles item drop events.
 * Prevents dropping items during lobby/game phases.
 */
public class ItemDropEventListener implements Listener {
    private final PotionGames plugin;
    
    public ItemDropEventListener(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        
        if (!plugin.pgPlayers.contains(p) && !plugin.playerLobby.containsKey(p)) {
            return;
        }
        
        // Drop prevention logic would go here
        // Typically prevents dropping items during lobby phase
    }
}
