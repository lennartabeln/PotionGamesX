package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;

/**
 * Handles player death events.
 * Manages death tracking, kill rewards, and game state after death.
 */
public class DeathEventListener implements Listener {
    private final PotionGames plugin;
    
    public DeathEventListener(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        
        if (!plugin.getGame().isActivePlayer(p) && !plugin.getGame().isInLobby(p)) {
            return;
        }
        
        // Death logic: move to spectators, increment deaths, etc.
        if (plugin.getGame().isActivePlayer(p)) {
            plugin.getGame().removeActivePlayer(p);
            plugin.getGame().addSpectatorPlayer(p);
        }
        
    }
}
