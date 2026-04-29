package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.entity.Player;

/**
 * Handles player chat events.
 * Manages team/spectator chat separation and message filtering.
 */
public class ChatEventListener implements Listener {
    private final PotionGames plugin;
    
    public ChatEventListener(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        
        if (!plugin.pgPlayers.contains(p) && !plugin.specPlayers.contains(p)) {
            return;
        }
        
        // Chat logic would go here
        // Handles team chat, spectator chat, colored names, etc.
    }
}
