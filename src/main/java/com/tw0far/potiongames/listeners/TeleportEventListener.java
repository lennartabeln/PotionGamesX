package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.entity.Player;

/**
 * Handles player teleport events.
 * Manages spectator teleportation and game boundaries.
 */
public class TeleportEventListener implements Listener {
    private final PotionGames plugin;
    
    public TeleportEventListener(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        
        if (!plugin.pgPlayers.contains(p) && !plugin.specPlayers.contains(p)) {
            return;
        }
        
        // Teleport handling logic would go here
        // Could prevent unauthorized teleports during games
    }
}
