package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import org.bukkit.GameMode;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Handles player teleport/movement events.
 * Manages spectator mode interactions (invisibility, no-collision).
 */
public class SpectatorEventListener implements Listener {
    private final PotionGames plugin;
    
    public SpectatorEventListener(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onSpectatorMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        
        if (!plugin.getGame().isSpectatorPlayer(p)) {
            return;
        }
        
        // Ensure spectators remain invisible and in spectator mode
        if (p.getGameMode() != GameMode.SPECTATOR) {
            p.setGameMode(GameMode.SPECTATOR);
        }
        
        if (!p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        }
        
        // Spectators can move freely and see all players
        p.setCanPickupItems(false);
    }
}
