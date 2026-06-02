package com.tw0far.potiongames.managers;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralized database manager.
 * Handles all SQL operations for both SQLite and MySQL.
 * 
 * Deprecated API Notes (Java 23 / Paper 26.1.x):
 * - Replaced deprecated getItemInHand() with getItemInMainHand()
 * - Replaced deprecated getArmorContents() with Equipment Slot API
 */
public class DatabaseManager implements IDatabaseManager {
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
        connect();
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
            createTablesIfNotExist();
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
            String dbPath = plugin.getDataFolder() + "/stats.db";
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
            String sql = "CREATE TABLE IF NOT EXISTS Stats (" +
                "UUID varchar(64), ROUNDS int, WINS int, LOSSES int, KILLS int, DEATHS int, KD double)";
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

    @Override
    public void connect() {
        if (config.isActivateMysql()) {
            connectMySQL();
        } else {
            connectSQLite();
        }
    }

    @Override
    public void update(String qry) {
        if (!connected || statement == null) {
            return;
        }
        try {
            statement.executeUpdate(qry);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "[PotionGames] Failed to execute update", e);
        }
    }

    @Override
    public ResultSet query(String qry) {
        if (!connected || statement == null) {
            return null;
        }
        try {
            return statement.executeQuery(qry);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "[PotionGames] Failed to execute query", e);
            return null;
        }
    }

    @Override
    public boolean playerExists(String uuid) {
        try (ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'")) {
            return rs != null && rs.next() && rs.getString("UUID") != null;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "[PotionGames] Failed to check player existence", e);
            return false;
        }
    }

    @Override
    public void createPlayer(String uuid) {
        if (!playerExists(uuid)) {
            update("INSERT INTO Stats(UUID, ROUNDS, WINS, LOSSES, KILLS, DEATHS, KD) VALUES ('" + uuid + "', '0', '0', '0', '0', '0', '0')");
        }
    }

    @Override
    public int getKills(String uuid) {
        int i = 0;
        if (playerExists(uuid)) {
            try (ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'")) {
                if (rs != null && rs.next()) {
                    i = rs.getInt("KILLS");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "[PotionGames] Failed to get kills", e);
            }
        } else {
            createPlayer(uuid);
            return getKills(uuid);
        }
        return i;
    }

    @Override
    public int getDeaths(String uuid) {
        int i = 0;
        if (playerExists(uuid)) {
            try (ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'")) {
                if (rs != null && rs.next()) {
                    i = rs.getInt("DEATHS");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "[PotionGames] Failed to get deaths", e);
            }
        } else {
            createPlayer(uuid);
            return getDeaths(uuid);
        }
        return i;
    }

    @Override
    public double getKD(String uuid) {
        double i = 0;
        if (playerExists(uuid)) {
            try (ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'")) {
                if (rs != null && rs.next()) {
                    i = rs.getDouble("KD");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "[PotionGames] Failed to get KD", e);
            }
        } else {
            createPlayer(uuid);
            return getKD(uuid);
        }
        return i;
    }

    @Override
    public int getWins(String uuid) {
        int i = 0;
        if (playerExists(uuid)) {
            try (ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'")) {
                if (rs != null && rs.next()) {
                    i = rs.getInt("WINS");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "[PotionGames] Failed to get wins", e);
            }
        } else {
            createPlayer(uuid);
            return getWins(uuid);
        }
        return i;
    }

    @Override
    public int getLosses(String uuid) {
        int i = 0;
        if (playerExists(uuid)) {
            try (ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'")) {
                if (rs != null && rs.next()) {
                    try {
                        i = rs.getInt("LOSSES");
                    } catch (SQLException ex) {
                        i = rs.getInt("LOSTS");
                    }
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "[PotionGames] Failed to get losses", e);
            }
        } else {
            createPlayer(uuid);
            return getLosses(uuid);
        }
        return i;
    }

    @Override
    public int getRounds(String uuid) {
        int i = 0;
        if (playerExists(uuid)) {
            try (ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'")) {
                if (rs != null && rs.next()) {
                    i = rs.getInt("ROUNDS");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "[PotionGames] Failed to get rounds", e);
            }
        } else {
            createPlayer(uuid);
            return getRounds(uuid);
        }
        return i;
    }

    @Override
    public void setKills(String uuid, int kills) {
        if (playerExists(uuid)) {
            update("UPDATE Stats SET KILLS= '" + kills + "' WHERE UUID= '" + uuid + "';");
        } else {
            createPlayer(uuid);
            setKills(uuid, kills);
        }
    }

    @Override
    public void setDeaths(String uuid, int deaths) {
        if (playerExists(uuid)) {
            update("UPDATE Stats SET DEATHS= '" + deaths + "' WHERE UUID= '" + uuid + "';");
        } else {
            createPlayer(uuid);
            setDeaths(uuid, deaths);
        }
    }

    @Override
    public void setKD(String uuid, double kd) {
        if (playerExists(uuid)) {
            int kills = getKills(uuid);
            int deaths = getDeaths(uuid);
            if (deaths != 0) {
                kd = ((double) kills) / ((double) deaths);
            } else {
                kd = kills;
            }
            kd = Math.round(kd * 1000) / 1000.0;
            update("UPDATE Stats SET KD= '" + kd + "' WHERE UUID= '" + uuid + "';");
        } else {
            createPlayer(uuid);
            setKD(uuid, kd);
        }
    }

    @Override
    public void setWins(String uuid, int wins) {
        if (playerExists(uuid)) {
            update("UPDATE Stats SET WINS= '" + wins + "' WHERE UUID= '" + uuid + "';");
        } else {
            createPlayer(uuid);
            setWins(uuid, wins);
        }
    }

    @Override
    public void setLosses(String uuid, int losses) {
        if (playerExists(uuid)) {
            try {
                update("UPDATE Stats SET LOSSES= '" + losses + "' WHERE UUID= '" + uuid + "';");
            } catch (Exception ex) {
                update("UPDATE Stats SET LOSTS= '" + losses + "' WHERE UUID= '" + uuid + "';");
            }
        } else {
            createPlayer(uuid);
            setLosses(uuid, losses);
        }
    }

    @Override
    public void setRounds(String uuid, int rounds) {
        if (playerExists(uuid)) {
            int wins = getWins(uuid);
            int losses = getLosses(uuid);
            rounds = wins + losses;
            update("UPDATE Stats SET ROUNDS= '" + rounds + "' WHERE UUID= '" + uuid + "';");
        } else {
            createPlayer(uuid);
            setRounds(uuid, rounds);
        }
    }

    @Override
    public void addKills(String uuid, int kills) {
        if (playerExists(uuid)) {
            setKills(uuid, (getKills(uuid) + kills));
            setKD(uuid, 0);
        } else {
            createPlayer(uuid);
            addKills(uuid, kills);
        }
    }

    @Override
    public void addDeaths(String uuid, int deaths) {
        if (playerExists(uuid)) {
            setDeaths(uuid, (getDeaths(uuid) + deaths));
            setKD(uuid, 0);
        } else {
            createPlayer(uuid);
            addDeaths(uuid, deaths);
        }
    }

    @Override
    public void addWins(String uuid, int wins) {
        if (playerExists(uuid)) {
            setWins(uuid, (getWins(uuid) + wins));
            setRounds(uuid, 0);
        } else {
            createPlayer(uuid);
            addWins(uuid, wins);
        }
    }

    @Override
    public void addLosses(String uuid, int losses) {
        if (playerExists(uuid)) {
            setLosses(uuid, (getLosses(uuid) + losses));
            setRounds(uuid, 0);
        } else {
            createPlayer(uuid);
            addLosses(uuid, losses);
        }
    }

    /**
     * Create a new player record in database
     */
    public void createPlayer(Player player) {
        if (player != null) {
            createPlayer(player.getUniqueId().toString());
        }
    }

    public void updateStat(String uuid, String stat, int value) {
        update("UPDATE Stats SET " + stat + "= '" + value + "' WHERE UUID= '" + uuid + "';");
    }

    public void incrementStat(String uuid, String stat) {
        update("UPDATE Stats SET " + stat + "= " + stat + " + 1 WHERE UUID= '" + uuid + "';");
    }

    public int getStat(String uuid, String stat) {
        int i = 0;
        if (playerExists(uuid)) {
            try (ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'")) {
                if (rs != null && rs.next()) {
                    i = rs.getInt(stat);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "[PotionGames] Failed to get stat: " + stat, e);
            }
        } else {
            createPlayer(uuid);
            return getStat(uuid, stat);
        }
        return i;
    }
    
    /**
     * Update player statistics
     */
    public void updateStat(Player player, String stat, int value) {
        if (player != null) {
            updateStat(player.getUniqueId().toString(), stat, value);
        }
    }
    
    /**
     * Increment player statistic
     */
    public void incrementStat(Player player, String stat) {
        if (player != null) {
            incrementStat(player.getUniqueId().toString(), stat);
        }
    }
    
    /**
     * Get player statistic
     */
    public int getStat(Player player, String stat) {
        if (player != null) {
            return getStat(player.getUniqueId().toString(), stat);
        }
        return 0;
    }

    // Convenience methods for common stats
    public int getKills(Player player) { return getStat(player, "KILLS"); }
    public int getDeaths(Player player) { return getStat(player, "DEATHS"); }
    public int getWins(Player player) { return getStat(player, "WINS"); }
    public int getLosses(Player player) { return getStat(player, "LOSSES"); }
    
    public void setKills(Player player, int value) { updateStat(player, "KILLS", value); }
    public void setDeaths(Player player, int value) { updateStat(player, "DEATHS", value); }
    public void setWins(Player player, int value) { updateStat(player, "WINS", value); }
    public void setLosses(Player player, int value) { updateStat(player, "LOSSES", value); }
    
    public void addKills(Player player) { incrementStat(player, "KILLS"); }
    public void addDeaths(Player player) { incrementStat(player, "DEATHS"); }
    public void addWins(Player player) { incrementStat(player, "WINS"); }
    public void addLosses(Player player) { incrementStat(player, "LOSSES"); }
    
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
