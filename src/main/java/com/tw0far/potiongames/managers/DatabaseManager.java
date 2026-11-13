package com.tw0far.potiongames.managers;

import com.tw0far.potiongames.PotionGamesX;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager implements IDatabaseManager {
    private static final Logger LOGGER = Logger.getLogger("Minecraft");

    /** Column names the stats table is built from. */
    private static final String CREATE_TABLE =
        "CREATE TABLE IF NOT EXISTS Stats (" +
            "UUID varchar(64) NOT NULL PRIMARY KEY, " +
            "ROUNDS int DEFAULT 0, WINS int DEFAULT 0, LOSSES int DEFAULT 0, " +
            "KILLS int DEFAULT 0, DEATHS int DEFAULT 0, KD double DEFAULT 0)";

    private final PotionGamesX plugin;
    private final IConfigurationManager config;

    /** Guards the single shared connection against concurrent use and reconnects. */
    private final Object lock = new Object();

    private Connection connection;
    private boolean connected = false;
    private boolean mysql = false;
    /** Set during onDisable so pending queries do not try to reconnect. */
    private boolean shuttingDown = false;

    public DatabaseManager(PotionGamesX plugin, IConfigurationManager config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public void onEnable() {
        connect();
    }

    @Override
    public void onDisable() {
        synchronized (lock) {
            shuttingDown = true;
        }
        closeConnection();
    }

    @Override
    public void connect() {
        synchronized (lock) {
            shuttingDown = false;
            mysql = config.isActivateMysql();
            if (mysql) {
                connectMySQL();
            } else {
                connectSQLite();
            }
        }
    }

    private void connectMySQL() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // allowPublicKeyRetrieval keeps caching_sha2_password users working
            // with mysql-connector-j 8+; serverTimezone/characterEncoding avoid
            // the usual timestamp and umlaut problems.
            String url = String.format(
                "jdbc:mysql://%s:%s/%s?allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=utf8",
                config.getHost(),
                config.getPort(),
                config.getDatabase()
            );
            String password = plugin.getConfig().getString("pg.mysql.password", "");
            connection = DriverManager.getConnection(url, config.getUser(), password);
            connected = true;
            LOGGER.info("[PotionGamesX] Connected to MySQL database");
            createTablesIfNotExist();
        } catch (ClassNotFoundException | SQLException | RuntimeException e) {
            LOGGER.log(Level.SEVERE, "[PotionGamesX] Failed to connect to MySQL", e);
            connected = false;
            connection = null;
        }
    }

    private void connectSQLite() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ignored) {
            // The driver is provided through plugin.yml libraries; if it is
            // missing, DriverManager reports it below.
        }
        try {
            String dbPath = plugin.getDataFolder() + File.separator + "stats.db";
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            connected = true;
            LOGGER.info("[PotionGamesX] Connected to SQLite database");
            createTablesIfNotExist();
        } catch (SQLException | RuntimeException e) {
            LOGGER.log(Level.SEVERE, "[PotionGamesX] Failed to connect to SQLite", e);
            connected = false;
            connection = null;
        }
    }

    private void createTablesIfNotExist() {
        executeUpdate(CREATE_TABLE);
        // Databases created by older builds have no unique key, so the async
        // join insert could duplicate a player's row.
        if (mysql) {
            if (!tryUpdate("CREATE UNIQUE INDEX stats_uuid ON Stats (UUID)")) {
                LOGGER.info("[PotionGamesX] Stats.UUID is not unique yet. Remove duplicate rows "
                    + "and restart to enforce the unique key.");
            }
        } else {
            executeUpdate("CREATE UNIQUE INDEX IF NOT EXISTS stats_uuid ON Stats (UUID)");
        }
    }

    @Override
    public void closeConnection() {
        synchronized (lock) {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
                connected = false;
                LOGGER.info("[PotionGamesX] Database connection closed");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "[PotionGamesX] Failed to close database connection", e);
            }
        }
    }

    @Override
    public boolean isConnected() {
        return connected && connection != null;
    }

    /**
     * Verifies the connection and re-establishes it once when the link dropped
     * or the server closed it. Returns false when the database stays
     * unreachable, so callers can skip their work instead of logging a warning
     * per statement.
     */
    private boolean ensureConnection() {
        synchronized (lock) {
            if (connected && connection != null) {
                if (!isClosed()) {
                    return true;
                }
                LOGGER.warning("[PotionGamesX] Database connection lost, reconnecting");
            }
            connected = false;
            closeQuietly();
            if (shuttingDown || plugin == null || config == null) {
                return false;
            }
            if (mysql) {
                connectMySQL();
            } else {
                connectSQLite();
            }
            return connected && connection != null;
        }
    }

    /** True while the driver still reports the connection as open. */
    private boolean isClosed() {
        try {
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }

    private void closeQuietly() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ignored) {
        }
        connection = null;
    }

    /**
     * Drops a connection the driver reported as broken (SQLState class 08), so
     * the next query reconnects instead of logging a warning per statement.
     */
    private void handleSqlFailure(SQLException e) {
        String state = e.getSQLState();
        boolean connectionLost = (state != null && state.startsWith("08")) || isClosed();
        if (!connectionLost) {
            return;
        }
        synchronized (lock) {
            connected = false;
            closeQuietly();
            LOGGER.warning("[PotionGamesX] Database connection lost: " + e.getMessage());
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
        synchronized (lock) {
            if (!ensureConnection()) {
                return;
            }
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                setParameters(stmt, params);
                stmt.executeUpdate();
            } catch (SQLException e) {
                handleSqlFailure(e);
                LOGGER.log(Level.WARNING, "[PotionGamesX] Failed to execute update: " + sql, e);
            }
        }
    }

    private ResultSet executeQuery(String sql, Object... params) {
        synchronized (lock) {
            if (!ensureConnection()) {
                return null;
            }
            try {
                PreparedStatement stmt = connection.prepareStatement(sql);
                // Close the statement automatically when its ResultSet is closed,
                // so callers using try-with-resources on the ResultSet don't leak cursors.
                stmt.closeOnCompletion();
                setParameters(stmt, params);
                return stmt.executeQuery();
            } catch (SQLException e) {
                handleSqlFailure(e);
                LOGGER.log(Level.WARNING, "[PotionGamesX] Failed to execute query: " + sql, e);
                return null;
            }
        }
    }

    private void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (param == null) {
                stmt.setNull(i + 1, Types.NULL);
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
        if (!ensureConnection()) {
            return false;
        }
        String sql = "SELECT UUID FROM Stats WHERE UUID = ?";
        try (ResultSet rs = executeQuery(sql, uuid)) {
            return rs != null && rs.next() && rs.getString("UUID") != null;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "[PotionGamesX] Failed to check player existence", e);
            return false;
        }
    }

    /**
     * Creates the player's row when it is missing. The insert is idempotent
     * ({@code INSERT IGNORE} / {@code INSERT OR IGNORE}) and the table has a
     * unique key, so concurrent joins and stat calls can no longer produce
     * duplicate rows or recurse forever.
     */
    private void ensurePlayer(String uuid) {
        if (uuid == null || uuid.isBlank() || !ensureConnection()) {
            return;
        }
        String sql = mysql
            ? "INSERT IGNORE INTO Stats(UUID, ROUNDS, WINS, LOSSES, KILLS, DEATHS, KD) VALUES (?, 0, 0, 0, 0, 0, 0)"
            : "INSERT OR IGNORE INTO Stats(UUID, ROUNDS, WINS, LOSSES, KILLS, DEATHS, KD) VALUES (?, 0, 0, 0, 0, 0, 0)";
        try {
            executeUpdate(sql, uuid);
        } catch (RuntimeException e) {
            LOGGER.log(Level.WARNING, "[PotionGamesX] Failed to create player " + uuid, e);
        }
    }

    @Override
    public void createPlayer(String uuid) {
        ensurePlayer(uuid);
    }

    @Override
    public int getKills(String uuid) {
        return readInt(uuid, "KILLS");
    }

    @Override
    public int getDeaths(String uuid) {
        return readInt(uuid, "DEATHS");
    }

    @Override
    public int getWins(String uuid) {
        return readInt(uuid, "WINS");
    }

    @Override
    public int getLosses(String uuid) {
        return readInt(uuid, "LOSSES");
    }

    @Override
    public int getRounds(String uuid) {
        return readInt(uuid, "ROUNDS");
    }

    /**
     * Reads an integer column, creating the player row first when needed. A
     * missing connection returns 0 instead of recursing.
     */
    private int readInt(String uuid, String column) {
        if (uuid == null || uuid.isBlank() || !ensureConnection()) {
            return 0;
        }
        ensurePlayer(uuid);
        String sql = "SELECT " + column + " FROM Stats WHERE UUID = ?";
        try (ResultSet rs = executeQuery(sql, uuid)) {
            if (rs != null && rs.next()) {
                return rs.getInt(column);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "[PotionGamesX] Failed to get " + column, e);
        }
        return 0;
    }

    @Override
    public double getKD(String uuid) {
        if (uuid == null || uuid.isBlank() || !ensureConnection()) {
            return 0;
        }
        ensurePlayer(uuid);
        String sql = "SELECT KD FROM Stats WHERE UUID = ?";
        try (ResultSet rs = executeQuery(sql, uuid)) {
            if (rs != null && rs.next()) {
                return rs.getDouble("KD");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "[PotionGamesX] Failed to get KD", e);
        }
        return 0;
    }

    @Override
    public void setKills(String uuid, int kills) {
        writeInt(uuid, "KILLS", kills);
    }

    @Override
    public void setDeaths(String uuid, int deaths) {
        writeInt(uuid, "DEATHS", deaths);
    }

    @Override
    public void setWins(String uuid, int wins) {
        writeInt(uuid, "WINS", wins);
    }

    @Override
    public void setLosses(String uuid, int losses) {
        writeInt(uuid, "LOSSES", losses);
    }

    @Override
    public void setRounds(String uuid, int rounds) {
        // Rounds are derived from wins + losses, matching the original contract.
        int wins = getWins(uuid);
        int losses = getLosses(uuid);
        writeInt(uuid, "ROUNDS", wins + losses);
    }

    private void writeInt(String uuid, String column, int value) {
        if (uuid == null || uuid.isBlank() || !ensureConnection()) {
            return;
        }
        ensurePlayer(uuid);
        executeUpdate("UPDATE Stats SET " + column + " = ? WHERE UUID = ?", value, uuid);
    }

    @Override
    public void setKD(String uuid, double kd) {
        if (uuid == null || uuid.isBlank() || !ensureConnection()) {
            return;
        }
        ensurePlayer(uuid);
        int kills = readInt(uuid, "KILLS");
        int deaths = readInt(uuid, "DEATHS");
        double computed = deaths != 0 ? ((double) kills) / ((double) deaths) : kills;
        computed = Math.round(computed * 1000) / 1000.0;
        executeUpdate("UPDATE Stats SET KD = ? WHERE UUID = ?", computed, uuid);
    }

    @Override
    public void addKills(String uuid, int kills) {
        increment(uuid, "KILLS", kills, true);
    }

    @Override
    public void addDeaths(String uuid, int deaths) {
        increment(uuid, "DEATHS", deaths, true);
    }

    @Override
    public void addWins(String uuid, int wins) {
        increment(uuid, "WINS", wins, false);
    }

    @Override
    public void addLosses(String uuid, int losses) {
        increment(uuid, "LOSSES", losses, false);
    }

    /**
     * Increments a counter inside the database, so two threads cannot overwrite
     * each other's read-modify-write result.
     */
    private void increment(String uuid, String column, int amount, boolean refreshKd) {
        if (uuid == null || uuid.isBlank() || amount == 0 || !ensureConnection()) {
            return;
        }
        ensurePlayer(uuid);
        executeUpdate("UPDATE Stats SET " + column + " = " + column + " + ? WHERE UUID = ?", amount, uuid);
        if (refreshKd) {
            setKD(uuid, 0);
        } else {
            setRounds(uuid, 0);
        }
    }

    /**
     * Runs a DDL statement whose failure is expected (for example an index that
     * already exists) without logging a warning. Returns true on success.
     */
    private boolean tryUpdate(String sql) {
        synchronized (lock) {
            if (!ensureConnection()) {
                return false;
            }
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(sql);
                return true;
            } catch (SQLException e) {
                LOGGER.log(Level.FINE, "[PotionGamesX] Statement skipped: " + sql, e);
                return false;
            }
        }
    }
}
