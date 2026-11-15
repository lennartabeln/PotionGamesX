package com.tw0far.potiongames.managers;

import com.tw0far.potiongames.PotionGamesX;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigurationManager implements IConfigurationManager {
    private final PotionGamesX plugin;

    private String language = "en_US";
    private int countdown = 60;
    private int startDelay = 10;
    private int roundTime = 30;
    private int minPlayers = 2;
    private int teamSize = 2;
    private int maxPlayers = 24;
    private int activeKits = 6;
    /** Number of shop entries shown and buyable; legacy shipped 19 of 20. */
    private int activePotions = 19;
    private boolean playerFinder = true;
    private int winningReward = 100;
    private int killReward = 10;

    private boolean activateTeams = true;
    private boolean activateKits = true;
    private boolean activateShop = true;
    private boolean activateAirdrops = true;
    private boolean activateMysql = false;
    private boolean activateScoreboard = true;
    private boolean activateDeathmatch = true;
    private boolean enableRewards = false;

    private boolean startOnJoin = false;
    private boolean friendlyFire = false;
    private boolean gameServer = false;
    private boolean leaveOnRoundEnd = false;
    private boolean leaveOnLeave = true;
    private boolean joinStarted = false;
    private boolean allowOutsideChat = false;
    private boolean broadcastStarting = false;
    private boolean compassOnSpawn = false;
    private boolean changeGamerules = true;
    private boolean singleLobby = false;

    private String host = "localhost";
    private String port = "3306";
    private String database = "PotionGamesX";
    private String user = "root";

    public ConfigurationManager(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEnable() {
        seedMissingDefaults();
        loadAllConfig();
    }

    /**
     * Writes options that were added after the config file was created, so an
     * upgrade actually shows them. Values are only written when the key is
     * missing; legacy installs keep {@code pg.singleLobby} untouched because the
     * migration derives it from {@code pg.lobbySystem}.
     */
    private void seedMissingDefaults() {
        FileConfiguration config = plugin.getConfig();
        boolean changed = false;
        if (!config.contains("pg.activePotions")) {
            config.set("pg.activePotions", 19);
            changed = true;
        }
        if (!config.contains("pg.singleLobby") && !config.contains("pg.lobbySystem")) {
            config.set("pg.singleLobby", false);
            changed = true;
        }
        if (changed) {
            plugin.saveConfig();
        }
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void reload() {
        loadAllConfig();
    }

    private void loadAllConfig() {
        FileConfiguration config = plugin.getConfig();
        language = config.getString("pg.language", "en_US");
        countdown = config.getInt("pg.defaults.countdown", 60);
        startDelay = Math.max(1, config.getInt("pg.defaults.startDelay", 10));
        roundTime = config.getInt("pg.defaults.roundTime", 30);
        teamSize = config.getInt("pg.defaults.teamSize", 2);
        maxPlayers = config.getInt("pg.defaults.maxPlayers", 24);
        // Without an explicit value a lobby needs half of its slots, like the
        // legacy release (minPlayers = maxPlayers / 2).
        minPlayers = config.contains("pg.defaults.minPlayers")
            ? Math.max(1, config.getInt("pg.defaults.minPlayers"))
            : Math.max(1, maxPlayers / 2);
        activeKits = config.getInt("pg.activeKits", 6);
        activePotions = config.getInt("pg.activePotions", 19);
        winningReward = config.getInt("pg.winningReward", 100);
        killReward = config.getInt("pg.killReward", 10);

        activateTeams = config.getBoolean("pg.defaults.activateTeams", true);
        activateKits = config.getBoolean("pg.defaults.activateKits", true);
        activateShop = config.getBoolean("pg.defaults.activateShop", true);
        activateAirdrops = config.getBoolean("pg.defaults.activateAirdrops", true);
        activateMysql = config.getBoolean("pg.activateMySQL", false);
        activateScoreboard = config.getBoolean("pg.activateScoreboard", true);
        activateDeathmatch = config.getBoolean("pg.activateDeathmatch", true);
        enableRewards = config.getBoolean("pg.enableRewards", false);

        startOnJoin = config.getBoolean("pg.startOnJoin", false);
        joinStarted = config.getBoolean("pg.joinStarted", false);
        allowOutsideChat = config.getBoolean("pg.allowOutsideChat", false);
        broadcastStarting = config.getBoolean("pg.broadcastStarting", false);
        compassOnSpawn = config.getBoolean("pg.compassOnSpawn", false);
        playerFinder = config.getBoolean("pg.playerFinder", true);
        changeGamerules = config.getBoolean("pg.changeGamerules", true);
        friendlyFire = config.getBoolean("pg.friendlyFire", false);
        gameServer = config.getBoolean("pg.gameServer", false);
        leaveOnRoundEnd = config.getBoolean("pg.leaveOnRoundEnd", false);
        leaveOnLeave = config.getBoolean("pg.leaveOnLeave", true);
        singleLobby = config.getBoolean("pg.singleLobby", false);

        host = config.getString("pg.mysql.host", "localhost");
        port = String.valueOf(config.get("pg.mysql.port", 3306));
        database = config.getString("pg.mysql.database", "PotionGamesX");
        user = config.getString("pg.mysql.user", "root");
    }

    @Override
    public String getLanguage() { return language; }

    @Override
    public int getCountdown() { return countdown; }

    /** Seconds players stay frozen after the teleport into the arena. */
    public int getStartDelay() { return startDelay; }

    @Override
    public int getRoundTime() { return roundTime; }

    @Override
    public int getMinPlayers() { return minPlayers; }

    @Override
    public int getTeamSize() { return teamSize; }

    @Override
    public int getMaxPlayers() { return maxPlayers; }

    @Override
    public int getActiveKits() { return activeKits; }

    @Override
    public int getActivePotions() { return activePotions; }

    @Override
    public int getWinningReward() { return winningReward; }

    @Override
    public int getKillReward() { return killReward; }

    @Override
    public boolean isActivateTeams() { return activateTeams; }

    @Override
    public boolean isActivateKits() { return activateKits; }

    @Override
    public boolean isActivateShop() { return activateShop; }

    @Override
    public boolean isActivateAirdrops() { return activateAirdrops; }

    @Override
    public boolean isActivateMysql() { return activateMysql; }

    @Override
    public boolean isActivateScoreboard() { return activateScoreboard; }

    @Override
    public boolean isActivateDeathmatch() { return activateDeathmatch; }

    @Override
    public void setActivateTeams(boolean activate) { this.activateTeams = activate; }

    @Override
    public void setActivateKits(boolean activate) { this.activateKits = activate; }

    @Override
    public void setActivateShop(boolean activate) { this.activateShop = activate; }

    @Override
    public void setActivateAirdrops(boolean activate) { this.activateAirdrops = activate; }

    @Override
    public void setActivateMysql(boolean activate) { this.activateMysql = activate; }

    @Override
    public void setActivateScoreboard(boolean activate) { this.activateScoreboard = activate; }

    @Override
    public void setActivateDeathmatch(boolean activate) { this.activateDeathmatch = activate; }

    @Override
    public boolean isStartOnJoin() { return startOnJoin; }

    @Override
    public boolean isFriendlyFire() { return friendlyFire; }

    @Override
    public boolean isGameServer() { return gameServer; }

    @Override
    public boolean isLeaveOnRoundEnd() { return leaveOnRoundEnd; }

    @Override
    public boolean isLeaveOnLeave() { return leaveOnLeave; }

    @Override
    public boolean isEnableRewards() { return enableRewards; }

    @Override
    public boolean isJoinStarted() { return joinStarted; }

    @Override
    public boolean isAllowOutsideChat() { return allowOutsideChat; }

    @Override
    public boolean isBroadcastStarting() { return broadcastStarting; }

    @Override
    public boolean isPlayerFinder() { return playerFinder; }

    public boolean isCompassOnSpawn() { return compassOnSpawn; }

    @Override
    public boolean isChangeGamerules() { return changeGamerules; }

    @Override
    public boolean isSingleLobby() { return singleLobby; }

    @Override
    public void setGameServer(boolean gameServer) { this.gameServer = gameServer; }

    public void setLeaveOnRoundEnd(boolean leaveOnRoundEnd) { this.leaveOnRoundEnd = leaveOnRoundEnd; }

    public void setLeaveOnLeave(boolean leaveOnLeave) { this.leaveOnLeave = leaveOnLeave; }

    @Override
    public String getHost() { return host; }

    @Override
    public String getPort() { return port; }

    @Override
    public String getDatabase() { return database; }

    @Override
    public String getUser() { return user; }
}
