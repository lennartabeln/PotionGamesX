package com.tw0far.potiongames.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Utility methods for location serialization and manipulation.
 */
public class LocationUtil {
    private LocationUtil() {}
    
    /**
     * Serialize a location to config section
     */
    public static void saveLocation(Location location, ConfigurationSection section, String key) {
        if (location == null || location.getWorld() == null) {
            return;
        }
        section.set(key + ".world", location.getWorld().getName());
        section.set(key + ".x", location.getX());
        section.set(key + ".y", location.getY());
        section.set(key + ".z", location.getZ());
        section.set(key + ".yaw", location.getYaw());
        section.set(key + ".pitch", location.getPitch());
    }
    
    /**
     * Load a location from config section
     */
    public static Location loadLocation(ConfigurationSection section, String key, World world) {
        if (!section.contains(key + ".x")) {
            return null;
        }
        double x = section.getDouble(key + ".x");
        double y = section.getDouble(key + ".y");
        double z = section.getDouble(key + ".z");
        float yaw = (float) section.getDouble(key + ".yaw", 0);
        float pitch = (float) section.getDouble(key + ".pitch", 0);
        return new Location(world, x, y, z, yaw, pitch);
    }
    
    /**
     * Clone a location
     */
    public static Location clone(Location location) {
        if (location == null) {
            return null;
        }
        return location.clone();
    }
    
    /**
     * Get distance between two locations
     */
    public static double distance(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null || !loc1.getWorld().equals(loc2.getWorld())) {
            return Double.MAX_VALUE;
        }
        return loc1.distance(loc2);
    }
}
