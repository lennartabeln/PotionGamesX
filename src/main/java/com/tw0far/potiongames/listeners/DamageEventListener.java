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
        
        boolean isActive = plugin.getGame().isActivePlayer(p);
        boolean isSpectator = plugin.getGame().isSpectatorPlayer(p);
        
        if (!isActive && !isSpectator) {
            return;
        }
        
        // Spectators cannot take damage
        if (isSpectator) {
            e.setCancelled(true);
            return;
        }
        
        // Allow damage to active players
        // Damage is processed normally during games
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
        
        boolean victimActive = plugin.getGame().isActivePlayer(victim);
        boolean attackerActive = plugin.getGame().isActivePlayer(attacker);
        
        // Both must be active players to fight
        if (!victimActive || !attackerActive) {
            e.setCancelled(true);
            return;
        }
        
        // Check friendly fire (same team)
        if (plugin.isActivateTeams()) {
            String victimLobby = plugin.getGame().getPlayerLobby(victim);
            String attackerLobby = plugin.getGame().getPlayerLobby(attacker);
            
            // Must be in same lobby
            if (victimLobby == null || !victimLobby.equals(attackerLobby)) {
                e.setCancelled(true);
                return;
            }
            
            // Check if same team
            String victimTeam = plugin.getGame().getPlayerTeam(victim);
            String attackerTeam = plugin.getGame().getPlayerTeam(attacker);
            
            if (victimTeam != null && victimTeam.equals(attackerTeam)) {
                // Friendly fire - by default allow it, could add config option
                e.setCancelled(false);
                return;
            }
        }
        
        // Damage allowed - attacker has hit victim
    }
}
