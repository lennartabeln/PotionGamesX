package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.weather.LightningStrikeEvent;

/**
 * Handles weather change events.
 * Prevents rain/weather changes during games if configured.
 */
public class WeatherEventListener implements Listener {
    private final PotionGames plugin;
    
    public WeatherEventListener(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        if (!plugin.getConfigManager().isGameServer()) {
            return;
        }
        
        // Check if there's an active game
        boolean gameActive = plugin.getGame().getActivePlayerCount() > 0;
        
        if (!gameActive) {
            return;
        }
        
        // Prevent weather changes during active games
        if (e.toWeatherState()) {
            // Prevent rain/storm during games
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onLightningStrike(LightningStrikeEvent e) {
        if (!plugin.getConfigManager().isGameServer()) {
            return;
        }
        
        // Check if there's an active game
        boolean gameActive = plugin.getGame().getActivePlayerCount() > 0;
        
        if (gameActive) {
            // Prevent lightning during games
            e.setCancelled(true);
        }
    }
}
