package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.entity.Player;

/**
 * Handles bucket empty events.
 * Manages water bucket usage during games.
 */
public class BucketEventListener implements Listener {
    private final PotionGames plugin;
    
    public BucketEventListener(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        Player p = e.getPlayer();
        
        if (!plugin.pgPlayers.contains(p)) {
            return;
        }
        
        // Bucket usage logic would go here
        // Typically prevents water bucket placement during games
    }
}
