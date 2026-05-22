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
        
        if (!plugin.getGame().isActivePlayer(p) && !plugin.getGame().isInLobby(p)) {
            return;
        }
        
        // During lobby phase, prevent dropping items
        if (plugin.getGame().isInLobby(p) && !plugin.getGame().isActivePlayer(p)) {
            e.setCancelled(true);
            return;
        }
        
        // During active games, allow item dropping (for PvP purposes)
        if (plugin.getGame().isActivePlayer(p)) {
            return;
        }
    }
}
