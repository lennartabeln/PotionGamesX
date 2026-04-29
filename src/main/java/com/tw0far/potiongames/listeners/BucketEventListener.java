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
        
        if (!plugin.game.isActivePlayer(p)) {
            return;
        }
        
        // Get the player's lobby
        String lobbyId = plugin.game.getPlayerLobby(p);
        
        if (lobbyId == null) {
            return;
        }
        
        // Prevent water bucket placement during games
        // Water can be used for escape/defensive purposes, which may be too powerful
        if (e.getBucket().toString().contains("WATER")) {
            e.setCancelled(true);
            p.sendMessage("§cWater buckets are not allowed during games!");
            return;
        }
        
        // Allow lava bucket (for damage purposes)
        // Allow milk bucket (for potion effects)
    }
}
