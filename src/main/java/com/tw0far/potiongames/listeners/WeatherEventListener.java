package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.weather.LightningStrikeEvent;

/**
 * Handles weather change events.
 * Prevents rain/weather changes during games if configured.
 */
public class WeatherEventListener implements Listener {
    private final PotionGamesX plugin;

    public WeatherEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        // Rain is only suppressed in the arena worlds of a running round; the
        // global check used to silence the weather on the whole server.
        if (e.toWeatherState() && plugin.getGame().isRoundWorld(e.getWorld())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onLightningStrike(LightningStrikeEvent e) {
        if (e.getWorld() != null && plugin.getGame().isRoundWorld(e.getWorld())) {
            e.setCancelled(true);
        }
    }
}
