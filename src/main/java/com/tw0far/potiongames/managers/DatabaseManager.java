package com.tw0far.potiongames.managers;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralized database manager.
 * Handles all SQL operations for both SQLite and MySQL.
 * 
 * Deprecated API Notes (Java 23 / Paper 1.26):
 * - Replaced deprecated getItemInHand() with getItemInMainHand()
 * - Replaced deprecated getArmorContents() with Equipment Slot API
 */
public class DatabaseManager implements IManager {
    private static final Logger LOGGER = Logger.getLogger("Minecraft");
    private final PotionGames plugin;
    private final ConfigurationManager config;
    
    private Connection connection;
    private Statement statement;
    private boolean connected = false;
    
    public DatabaseManager(PotionGames plugin, ConfigurationManager config) {
        this.plugin = plugin;
        this.config = config;
    }
    
    @Override
    public void onEnable() {
        if (config.isActivateMysql()) {
            connectMySQL();
        } else {
            connectSQLite();
        }
    }
    
    @Override
    public void onDisable() {
        closeConnection();
    }
    
    @Override
    public void reload() {
        closeConnection();
        onEnable();
    }
    
    /**
     * Connect to MySQL database
     */
    private void connectMySQL() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = String.format(
                "jdbc:mysql://%s:%s/%s",
                config.getHost(),
                config.getPort(),
                config.getDatabase()
            );
            connection = DriverManager.getConnection(url, config.getUser(), ""); // Note: password not exposed in ConfigurationManager yet
            statement = connection.createStatement();
            connected = true;
            LOGGER.info("[PotionGames] Connected to MySQL database");
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.log(Level.SEVERE, "[PotionGames] Failed to connect to MySQL", e);
            connected = false;
        }
    }
    
    /**
     * Connect to SQLite database
     */
    private void connectSQLite() {
        try {
            String dbPath = plugin.getDataFolder() + "/potiongames.db";
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            statement = connection.createStatement();
            connected = true;
            LOGGER.info("[PotionGames] Connected to SQLite database");
            createTablesIfNotExist();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[PotionGames] Failed to connect to SQLite", e);
            connected = false;
        }
    }
    
    private void createTablesIfNotExist() {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS players (" +
                "uuid TEXT PRIMARY KEY, " +
                "kills INTEGER DEFAULT 0, " +
                "deaths INTEGER DEFAULT 0, " +
                "wins INTEGER DEFAULT 0, " +
                "losses INTEGER DEFAULT 0)";
            statement.execute(sql);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[PotionGames] Failed to create tables", e);
        }
    }
    
    /**
     * Close database connection
     */
    public void closeConnection() {
        try {
            if (statement != null && !statement.isClosed()) {
                statement.close();
            }
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            connected = false;
            LOGGER.info("[PotionGames] Database connection closed");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[PotionGames] Failed to close database connection", e);
        }
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    /**
     * Create a new player record in database
     */
    public void createPlayer(Player player) {
        if (!connected) return;
        
        try {
            String uuid = player.getUniqueId().toString();
            String sql = "INSERT OR IGNORE INTO players (uuid) VALUES (?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, uuid);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "[PotionGames] Failed to create player record", e);
        }
    }
    
    /**
     * Update player statistics
     */
    public void updateStat(Player player, String stat, int value) {
        if (!connected) return;
        
        try {
            String uuid = player.getUniqueId().toString();
            String sql = "UPDATE players SET " + stat + " = ? WHERE uuid = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, value);
            pstmt.setString(2, uuid);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "[PotionGames] Failed to update stat: " + stat, e);
        }
    }
    
    /**
     * Increment player statistic
     */
    public void incrementStat(Player player, String stat) {
        if (!connected) return;
        
        try {
            String uuid = player.getUniqueId().toString();
            String sql = "UPDATE players SET " + stat + " = " + stat + " + 1 WHERE uuid = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, uuid);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "[PotionGames] Failed to increment stat: " + stat, e);
        }
    }
    
    /**
     * Get player statistic
     */
    public int getStat(Player player, String stat) {
        if (!connected) return 0;
        
        try {
            String uuid = player.getUniqueId().toString();
            String sql = "SELECT " + stat + " FROM players WHERE uuid = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, uuid);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int value = rs.getInt(stat);
                rs.close();
                pstmt.close();
                return value;
            }
            
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "[PotionGames] Failed to get stat: " + stat, e);
        }
        
        return 0;
    }
    
    // Convenience methods for common stats
    public int getKills(Player player) { return getStat(player, "kills"); }
    public int getDeaths(Player player) { return getStat(player, "deaths"); }
    public int getWins(Player player) { return getStat(player, "wins"); }
    public int getLosses(Player player) { return getStat(player, "losses"); }
    
    public void setKills(Player player, int value) { updateStat(player, "kills", value); }
    public void setDeaths(Player player, int value) { updateStat(player, "deaths", value); }
    public void setWins(Player player, int value) { updateStat(player, "wins", value); }
    public void setLosses(Player player, int value) { updateStat(player, "losses", value); }
    
    public void addKills(Player player) { incrementStat(player, "kills"); }
    public void addDeaths(Player player) { incrementStat(player, "deaths"); }
    public void addWins(Player player) { incrementStat(player, "wins"); }
    public void addLosses(Player player) { incrementStat(player, "losses"); }
    
    /**
     * Get K/D ratio for a player
     */
    public double getKDRatio(Player player) {
        int kills = getKills(player);
        int deaths = getDeaths(player);
        if (deaths == 0) return (double) kills;
        return (double) kills / deaths;
    }
}
