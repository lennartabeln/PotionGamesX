package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

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
        if (!plugin.isGameServer()) {
            return;
        }
        
        // Weather change prevention logic would go here
        // Typically prevents weather during active games
    }
}
