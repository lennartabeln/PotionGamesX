package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.entity.Player;

/**
 * Handles item consumption events (eating, drinking potions).
 * Manages soup healing, potion consumption, etc.
 */
public class ItemConsumeEventListener implements Listener {
    private final PotionGames plugin;
    
    public ItemConsumeEventListener(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent e) {
        Player p = e.getPlayer();
        
        if (!plugin.pgPlayers.contains(p) && !plugin.playerLobby.containsKey(p)) {
            return;
        }
        
        // Item consumption logic would go here
        // Handles soup healing, potion effects, etc.
    }
}
