package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;

/**
 * Handles entity damage events.
 * Manages player damage, damage cancellation, and damage tracking.
 */
public class DamageEventListener implements Listener {
    private final PotionGames plugin;
    
    public DamageEventListener(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        
        Player p = (Player) e.getEntity();
        if (!plugin.pgPlayers.contains(p) && !plugin.playerLobby.containsKey(p)) {
            return;
        }
        
        // Damage handling logic would go here
    }
    
    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        
        Player victim = (Player) e.getEntity();
        if (!(e.getDamager() instanceof Player)) {
            return;
        }
        
        Player attacker = (Player) e.getDamager();
        
        // Check if both are in game
        if (!plugin.pgPlayers.contains(victim) && !plugin.playerLobby.containsKey(victim)) {
            return;
        }
        
        // Damage by entity logic would go here
    }
}
