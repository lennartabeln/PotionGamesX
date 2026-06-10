package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.PotionMeta;

/**
 * Handles item consumption events (eating, drinking potions).
 * Manages soup healing, potion consumption, etc.
 */
public class ItemConsumeEventListener implements Listener {
    private final PotionGamesX plugin;
    
    public ItemConsumeEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent e) {
        Player p = e.getPlayer();
        
        if (!plugin.getGame().isActivePlayer(p) && !plugin.getGame().isInLobby(p)) {
            return;
        }
        
        // Allow soup healing during games
        if (e.getItem().getType() == Material.MUSHROOM_STEW 
            || e.getItem().getType() == Material.RABBIT_STEW 
            || e.getItem().getType() == Material.BEETROOT_SOUP) {
            
            // Item consumption allowed - soup will heal player
            // This is handled by Bukkit normally, we just allow it
            return;
        }
        
        // Handle potion consumption
        if (e.getItem().hasItemMeta() && e.getItem().getItemMeta() instanceof PotionMeta) {
            // Potions are allowed during games
            return;
        }
    }
}
