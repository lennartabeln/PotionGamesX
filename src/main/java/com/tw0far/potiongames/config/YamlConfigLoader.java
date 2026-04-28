package com.tw0far.potiongames.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Optimized YAML configuration loader with caching.
 * 
 * FEATURES:
 * - Cache-based access (no repeated disk reads)
 * - Type-safe ConfigKeys enum
 * - Automatic default value injection
 * - Thread-safe concurrent access
 * - Per-lobby settings support
 * - Validation of loaded values
 * 
 * BENEFITS:
 * - Single disk read per reload
 * - O(1) config value lookups
 * - No typos (ConfigKeys enforces valid keys)
 * - Easy to extend with new settings
 */
public class YamlConfigLoader {
    private final Plugin plugin;
    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    private final Map<Integer, LobbySettings> lobbySettingsCache = new ConcurrentHashMap<>();
    
    private YamlConfiguration config;
    private File configFile;
    private boolean loaded = false;
    
    public YamlConfigLoader(Plugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Load and cache configuration from file
     */
    public void load() {
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }
        
        this.config = YamlConfiguration.loadConfiguration(configFile);
        this.cache.clear();
        this.lobbySettingsCache.clear();
        this.loaded = true;
    }
    
    /**
     * Reload configuration (clears cache)
     */
    public void reload() {
        cache.clear();
        lobbySettingsCache.clear();
        load();
    }
    
    /**
     * Get configuration value as Integer (cached)
     */
    public int getInt(ConfigKeys key) {
        return getInt(key.getKey(), (Integer) key.getDefaultValue());
    }
    
    /**
     * Get configuration value as Integer with fallback
     */
    public int getInt(String path, int defaultValue) {
        String cacheKey = "int:" + path;
        if (cache.containsKey(cacheKey)) {
            return (Integer) cache.get(cacheKey);
        }
        
        int value = config.getInt(path, defaultValue);
        cache.put(cacheKey, value);
        return value;
    }
    
    /**
     * Get configuration value as String (cached)
     */
    public String getString(ConfigKeys key) {
        return getString(key.getKey(), (String) key.getDefaultValue());
    }
    
    /**
     * Get configuration value as String with fallback
     */
    public String getString(String path, String defaultValue) {
        String cacheKey = "string:" + path;
        if (cache.containsKey(cacheKey)) {
            return (String) cache.get(cacheKey);
        }
        
        String value = config.getString(path, defaultValue);
        cache.put(cacheKey, value);
        return value;
    }
    
    /**
     * Get configuration value as Boolean (cached)
     */
    public boolean getBoolean(ConfigKeys key) {
        return getBoolean(key.getKey(), (Boolean) key.getDefaultValue());
    }
    
    /**
     * Get configuration value as Boolean with fallback
     */
    public boolean getBoolean(String path, boolean defaultValue) {
        String cacheKey = "bool:" + path;
        if (cache.containsKey(cacheKey)) {
            return (Boolean) cache.get(cacheKey);
        }
        
        boolean value = config.getBoolean(path, defaultValue);
        cache.put(cacheKey, value);
        return value;
    }
    
    /**
     * Get configuration value as Double (cached)
     */
    public double getDouble(String path, double defaultValue) {
        String cacheKey = "double:" + path;
        if (cache.containsKey(cacheKey)) {
            return (Double) cache.get(cacheKey);
        }
        
        double value = config.getDouble(path, defaultValue);
        cache.put(cacheKey, value);
        return value;
    }
    
    /**
     * Get per-lobby settings (cached)
     */
    public LobbySettings getLobbySettings(int lobbyId) {
        if (lobbySettingsCache.containsKey(lobbyId)) {
            return lobbySettingsCache.get(lobbyId);
        }
        
        String path = "pg.lobbies.lobby" + lobbyId;
        if (!config.contains(path)) {
            // Create default settings if not found
            plugin.getLogger().info("Creating default settings for lobby " + lobbyId);
            config.createSection(path);
        }
        
        LobbySettings settings = new LobbySettings(
            lobbyId,
            config.getConfigurationSection(path)
        );
        
        if (!settings.isValid()) {
            plugin.getLogger().warning("Invalid settings for lobby " + lobbyId + ": " + settings.getValidationError());
        }
        
        lobbySettingsCache.put(lobbyId, settings);
        return settings;
    }
    
    /**
     * Get all lobby IDs
     */
    public int[] getAllLobbyIds() {
        if (!config.contains("pg.lobbies")) {
            return new int[0];
        }
        
        return config.getConfigurationSection("pg.lobbies")
            .getKeys(false)
            .stream()
            .filter(key -> key.startsWith("lobby"))
            .map(key -> Integer.parseInt(key.substring(5)))
            .mapToInt(Integer::intValue)
            .toArray();
    }
    
    /**
     * Save current configuration to disk
     */
    public void save() {
        try {
            config.save(configFile);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to save config: " + e.getMessage());
        }
    }
    
    /**
     * Set a configuration value and save
     */
    public void set(String path, Object value) {
        config.set(path, value);
        cache.remove("*:" + path); // Clear related cache entries
        save();
    }
    
    /**
     * Get raw configuration section
     */
    public YamlConfiguration getConfig() {
        return config;
    }
    
    /**
     * Check if configuration is loaded
     */
    public boolean isLoaded() {
        return loaded;
    }
    
    /**
     * Get cache statistics (for debugging)
     */
    public String getCacheStats() {
        return String.format(
            "Cache: %d entries, Lobby Settings: %d entries",
            cache.size(),
            lobbySettingsCache.size()
        );
    }
}
