package com.tw0far.potiongames.managers;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager implements IDatabaseManager {
    private static final Logger LOGGER = Logger.getLogger("Minecraft");
    private final PotionGames plugin;
    private final ConfigurationManager config;

    private Connection connection;
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

    private void connectMySQL() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = String.format(
                "jdbc:mysql://%s:%s/%s",
                config.getHost(),
                config.getPort(),
                config.getDatabase()
            );
            String password = plugin.getConfig().getString("pg.mysql.password", "");
            connection = DriverManager.getConnection(url, config.getUser(), password);
            connected = true;
            LOGGER.info("[PotionGames] Connected to MySQL database");
            createTablesIfNotExist();
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.log(Level.SEVERE, "[PotionGames] Failed to connect to MySQL", e);
            connected = false;
        }
    }

    private void connectSQLite() {
        try {
            String dbPath = plugin.getDataFolder() + "/stats.db";
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            connected = true;
            LOGGER.info("[PotionGames] Connected to SQLite database");
            createTablesIfNotExist();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[PotionGames] Failed to connect to SQLite", e);
            connected = false;
        }
    }

    private void createTablesIfNotExist() {
        String sql = "CREATE TABLE IF NOT EXISTS Stats (" +
            "UUID varchar(64), ROUNDS int, WINS int, LOSSES int, KILLS int, DEATHS int, KD double)";
        executeUpdate(sql);
    }

    public void closeConnection() {
        try {
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
        executeUpdate(qry);
    }

    @Override
    public ResultSet query(String qry) {
        return executeQuery(qry);
    }

    private void executeUpdate(String sql, Object... params) {
        if (!connected || connection == null) return;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setParameters(stmt, params);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "[PotionGames] Failed to execute update: " + sql, e);
        }
    }

    private ResultSet executeQuery(String sql, Object... params) {
        if (!connected || connection == null) return null;
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            setParameters(stmt, params);
            return stmt.executeQuery();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "[PotionGames] Failed to execute query: " + sql, e);
            return null;
        }
    }

    private void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (param == null) {
                stmt.setNull(i + 1, java.sql.Types.NULL);
            } else if (param instanceof String) {
                stmt.setString(i + 1, (String) param);
            } else if (param instanceof Integer) {
                stmt.setInt(i + 1, (Integer) param);
            } else if (param instanceof Double) {
                stmt.setDouble(i + 1, (Double) param);
            } else {
                stmt.setObject(i + 1, param);
            }
        }
    }

    @Override
    public boolean playerExists(String uuid) {
        String sql = "SELECT UUID FROM Stats WHERE UUID = ?";
        try (ResultSet rs = executeQuery(sql, uuid)) {
            return rs != null && rs.next() && rs.getString("UUID") != null;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "[PotionGames] Failed to check player existence", e);
            return false;
        }
    }

    @Override
    public void createPlayer(String uuid) {
        if (!playerExists(uuid)) {
            String sql = "INSERT INTO Stats(UUID, ROUNDS, WINS, LOSSES, KILLS, DEATHS, KD) VALUES (?, 0, 0, 0, 0, 0, 0)";
            executeUpdate(sql, uuid);
        }
    }

    @Override
    public int getKills(String uuid) {
        if (!playerExists(uuid)) {
            createPlayer(uuid);
            return getKills(uuid);
        }
        return getIntStat(uuid, "KILLS");
    }

    @Override
    public int getDeaths(String uuid) {
        if (!playerExists(uuid)) {
            createPlayer(uuid);
            return getDeaths(uuid);
        }
        return getIntStat(uuid, "DEATHS");
    }

    @Override
    public double getKD(String uuid) {
        if (!playerExists(uuid)) {
            createPlayer(uuid);
            return getKD(uuid);
        }
        String sql = "SELECT KD FROM Stats WHERE UUID = ?";
        try (ResultSet rs = executeQuery(sql, uuid)) {
            if (rs != null && rs.next()) {
                return rs.getDouble("KD");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "[PotionGames] Failed to get KD", e);
        }
        return 0;
    }

    @Override
    public int getWins(String uuid) {
        if (!playerExists(uuid)) {
            createPlayer(uuid);
            return getWins(uuid);
        }
        return getIntStat(uuid, "WINS");
    }

    @Override
    public int getLosses(String uuid) {
        if (!playerExists(uuid)) {
            createPlayer(uuid);
            return getLosses(uuid);
        }
        return getIntStat(uuid, "LOSSES");
    }

    @Override
    public int getRounds(String uuid) {
        if (!playerExists(uuid)) {
            createPlayer(uuid);
            return getRounds(uuid);
        }
        return getIntStat(uuid, "ROUNDS");
    }

    private int getIntStat(String uuid, String column) {
        String sql = "SELECT " + column + " FROM Stats WHERE UUID = ?";
        try (ResultSet rs = executeQuery(sql, uuid)) {
            if (rs != null && rs.next()) {
                return rs.getInt(column);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "[PotionGames] Failed to get " + column, e);
        }
        return 0;
    }

    @Override
    public void setKills(String uuid, int kills) {
        if (playerExists(uuid)) {
            String sql = "UPDATE Stats SET KILLS = ? WHERE UUID = ?";
            executeUpdate(sql, kills, uuid);
        } else {
            createPlayer(uuid);
            setKills(uuid, kills);
        }
    }

    @Override
    public void setDeaths(String uuid, int deaths) {
        if (playerExists(uuid)) {
            String sql = "UPDATE Stats SET DEATHS = ? WHERE UUID = ?";
            executeUpdate(sql, deaths, uuid);
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
            String sql = "UPDATE Stats SET KD = ? WHERE UUID = ?";
            executeUpdate(sql, kd, uuid);
        } else {
            createPlayer(uuid);
            setKD(uuid, kd);
        }
    }

    @Override
    public void setWins(String uuid, int wins) {
        if (playerExists(uuid)) {
            String sql = "UPDATE Stats SET WINS = ? WHERE UUID = ?";
            executeUpdate(sql, wins, uuid);
        } else {
            createPlayer(uuid);
            setWins(uuid, wins);
        }
    }

    @Override
    public void setLosses(String uuid, int losses) {
        if (playerExists(uuid)) {
            String sql = "UPDATE Stats SET LOSSES = ? WHERE UUID = ?";
            executeUpdate(sql, losses, uuid);
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
            String sql = "UPDATE Stats SET ROUNDS = ? WHERE UUID = ?";
            executeUpdate(sql, rounds, uuid);
        } else {
            createPlayer(uuid);
            setRounds(uuid, rounds);
        }
    }

    @Override
    public void addKills(String uuid, int kills) {
        if (playerExists(uuid)) {
            setKills(uuid, getKills(uuid) + kills);
            setKD(uuid, 0);
        } else {
            createPlayer(uuid);
            addKills(uuid, kills);
        }
    }

    @Override
    public void addDeaths(String uuid, int deaths) {
        if (playerExists(uuid)) {
            setDeaths(uuid, getDeaths(uuid) + deaths);
            setKD(uuid, 0);
        } else {
            createPlayer(uuid);
            addDeaths(uuid, deaths);
        }
    }

    @Override
    public void addWins(String uuid, int wins) {
        if (playerExists(uuid)) {
            setWins(uuid, getWins(uuid) + wins);
            setRounds(uuid, 0);
        } else {
            createPlayer(uuid);
            addWins(uuid, wins);
        }
    }

    @Override
    public void addLosses(String uuid, int losses) {
        if (playerExists(uuid)) {
            setLosses(uuid, getLosses(uuid) + losses);
            setRounds(uuid, 0);
        } else {
            createPlayer(uuid);
            addLosses(uuid, losses);
        }
    }

    public void createPlayer(Player player) {
        if (player != null) {
            createPlayer(player.getUniqueId().toString());
        }
    }

    public void updateStat(String uuid, String stat, int value) {
        String sql = "UPDATE Stats SET " + stat + " = ? WHERE UUID = ?";
        executeUpdate(sql, value, uuid);
    }

    public void incrementStat(String uuid, String stat) {
        String sql = "UPDATE Stats SET " + stat + " = " + stat + " + 1 WHERE UUID = ?";
        executeUpdate(sql, uuid);
    }

    public int getStat(String uuid, String stat) {
        if (!playerExists(uuid)) {
            createPlayer(uuid);
            return getStat(uuid, stat);
        }
        String sql = "SELECT " + stat + " FROM Stats WHERE UUID = ?";
        try (ResultSet rs = executeQuery(sql, uuid)) {
            if (rs != null && rs.next()) {
                return rs.getInt(stat);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "[PotionGames] Failed to get stat: " + stat, e);
        }
        return 0;
    }

    public void updateStat(Player player, String stat, int value) {
        if (player != null) {
            updateStat(player.getUniqueId().toString(), stat, value);
        }
    }

    public void incrementStat(Player player, String stat) {
        if (player != null) {
            incrementStat(player.getUniqueId().toString(), stat);
        }
    }

    public int getStat(Player player, String stat) {
        if (player != null) {
            return getStat(player.getUniqueId().toString(), stat);
        }
        return 0;
    }

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

    public double getKDRatio(Player player) {
        int kills = getKills(player);
        int deaths = getDeaths(player);
        if (deaths == 0) return (double) kills;
        return (double) kills / deaths;
    }
}
