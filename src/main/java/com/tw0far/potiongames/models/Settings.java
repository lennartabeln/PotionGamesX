package com.tw0far.potiongames.models;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.tw0far.potiongames.main.PotionGames;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class Settings {
    public static File lobbiesFile = null;
    public static File chestsFile = null;
    public static File kitsFile = null;
    public static File messagesFile = null;
    public static File shopFile = null;
    public static FileConfiguration lobbies;
    public static FileConfiguration chests;
    public static FileConfiguration kitdata;
    public static FileConfiguration messages;
    public static FileConfiguration shopdata;
    public static Component prefix = Component.text("[").color(NamedTextColor.DARK_GRAY).append(Component.text("Potion").color(NamedTextColor.DARK_PURPLE)).append(Component.text("Games").color(NamedTextColor.GOLD)).append(Component.text("]").color(NamedTextColor.DARK_GRAY)).append(Component.text(" ").color(NamedTextColor.GRAY));
    public static boolean activateMySQL = false;
    public static boolean startOnJoin = false;
    public static boolean compassOnSpawn = false;
    public static boolean allowOutsideChat = false;
    public static boolean changeGamerules = true;
    public static boolean activateTeams = true;
    public static boolean activateKits = true;
    public static boolean activateShop = true;
    public static boolean activateAirdrops = true;
    public static boolean gameServer = true;
    public static boolean activateScoreboard = true;
    public static boolean friendlyFire = false;
    public static boolean joinStarted = false;
    public static boolean activateDeathmatch = true;
    public static boolean enableRewards = false;
    // Legacy flags missing in refactor; keep defaults for compatibility
    public static boolean move = false;
    public static boolean checkArenas = false;
    public static boolean broadcastStarting = false;
    public static int maxPlayers = 24;
    public static int minPlayers = 2;
    public static int teamSize = 2;
    public static int teamAmount = maxPlayers / teamSize;
    public static int roundTime = 30;
    public static int countdown = 60;
    public static int activePotions = 19;
    public static int activeKits = 6;
    public static int winningReward = 100;
    public static int killReward = 10;
    public static String language = "en_US";
    public static MySqlConfiguration mySqlConfig = null;

    // Bound configuration manager (optional). When set, read-through getters will prefer it.
    private static com.tw0far.potiongames.managers.IConfigurationManager configManager = null;

    public static void bindConfigManager(com.tw0far.potiongames.managers.IConfigurationManager cm) { configManager = cm; }

    // Read-through getters: prefer IConfigurationManager when bound, otherwise fall back to legacy static fields
    public static int getCountdown() { return configManager != null ? configManager.getCountdown() : countdown; }
    public static int getReset() { return configManager != null ? configManager.getReset() : 10; }
    public static int getTeamSize() { return configManager != null ? configManager.getTeamSize() : teamSize; }
    public static int getMaxPlayers() { return configManager != null ? configManager.getMaxPlayers() : maxPlayers; }
    public static int getMinPlayers() { return configManager != null ? configManager.getMinPlayers() : minPlayers; }
    public static int getPlayerAmount() { return configManager != null ? configManager.getPlayerAmount() : 0; }
    public static int getTeamAmount() { return configManager != null ? configManager.getTeamAmount() : teamAmount; }
    public static int getRoundTime() { return configManager != null ? configManager.getRoundTime() : roundTime; }
    public static int getRoundTimeSeconds() { return configManager != null ? configManager.getRoundTimeSeconds() : roundTime * 60; }
    public static int getActivePotions() { return configManager != null ? configManager.getActivePotions() : activePotions; }
    public static int getActiveKits() { return configManager != null ? configManager.getActiveKits() : activeKits; }
    public static int getWinningReward() { return configManager != null ? configManager.getWinningReward() : winningReward; }
    public static int getKillReward() { return configManager != null ? configManager.getKillReward() : killReward; }

    public static boolean isActivateTeams() { return configManager != null ? configManager.isActivateTeams() : activateTeams; }
    public static boolean isActivateKits() { return configManager != null ? configManager.isActivateKits() : activateKits; }
    public static boolean isActivateShop() { return configManager != null ? configManager.isActivateShop() : activateShop; }
    public static boolean isActivateAirdrops() { return configManager != null ? configManager.isActivateAirdrops() : activateAirdrops; }
    public static boolean isActivateMysql() { return configManager != null ? configManager.isActivateMysql() : activateMySQL; }
    public static boolean isActivateScoreboard() { return configManager != null ? configManager.isActivateScoreboard() : activateScoreboard; }
    public static boolean isActivateDeathmatch() { return configManager != null ? configManager.isActivateDeathmatch() : activateDeathmatch; }

    public static boolean isDeathmatch() { return configManager != null ? configManager.isDeathmatch() : false; }
    public static boolean isJoinable() { return configManager != null ? configManager.isJoinable() : true; }
    public static boolean isPause() { return configManager != null ? configManager.isPause() : false; }
    public static boolean isBuild() { return configManager != null ? configManager.isBuild() : false; }
    public static boolean isMove() { return configManager != null ? configManager.isMove() : true; }
    public static boolean isVoteallowed() { return configManager != null ? configManager.isVoteallowed() : false; }
    public static boolean isTeamallowed() { return configManager != null ? configManager.isTeamallowed() : false; }
    public static boolean isKitallowed() { return configManager != null ? configManager.isKitallowed() : false; }
    public static boolean isForcearena() { return configManager != null ? configManager.isForcearena() : false; }
    public static boolean isStartOnJoin() { return configManager != null ? configManager.isStartOnJoin() : startOnJoin; }
    public static boolean isTickStarted() { return configManager != null ? configManager.isTickStarted() : false; }
    public static boolean isMySQL() { return configManager != null ? configManager.isMySQL() : activateMySQL; }
    public static boolean isGameServer() { return configManager != null ? configManager.isGameServer() : gameServer; }
    public static boolean isAddlobby() { return configManager != null ? configManager.isAddlobby() : false; }
    public static boolean isAddarena() { return configManager != null ? configManager.isAddarena() : false; }
    public static boolean isDellobby() { return configManager != null ? configManager.isDellobby() : false; }
    public static boolean isDelarena() { return configManager != null ? configManager.isDelarena() : false; }
    public static boolean isReload() { return configManager != null ? configManager.isReload() : false; }
    public static boolean isCompassOnSpawn() { return configManager != null ? configManager.isCompassOnSpawn() : compassOnSpawn; }
    public static boolean isAllowOutsideChat() { return configManager != null ? configManager.isAllowOutsideChat() : allowOutsideChat; }
    public static boolean isChangeGamerules() { return configManager != null ? configManager.isChangeGamerules() : changeGamerules; }
    public static boolean isCheckArenas() { return configManager != null ? configManager.isCheckArenas() : false; }
    public static boolean isSingleArena() { return configManager != null ? configManager.isSingleArena() : false; }
    public static boolean isFriendlyFire() { return configManager != null ? configManager.isFriendlyFire() : friendlyFire; }
    public static boolean isJoinStarted() { return configManager != null ? configManager.isJoinStarted() : joinStarted; }
    public static boolean isEnableRewards() { return configManager != null ? configManager.isEnableRewards() : enableRewards; }
    public static boolean isBroadcastStarting() { return configManager != null ? configManager.isBroadcastStarting() : broadcastStarting; }

    public static String getLanguage() { return configManager != null ? configManager.getLanguage() : language; }
    public static String getHost() { return configManager != null ? configManager.getHost() : "localhost"; }
    public static String getPort() { return configManager != null ? configManager.getPort() : "3306"; }
    public static String getDatabase() { return configManager != null ? configManager.getDatabase() : "potiongames"; }
    public static String getUser() { return configManager != null ? configManager.getUser() : "root"; }

    public static void loadConfigurations() {
        // Arena/lobby/spawn data from lobbies.yml
        Settings.lobbies = YamlConfiguration.loadConfiguration(Settings.lobbiesFile);
        // Chest loot data from chests.yml
        Settings.chests = YamlConfiguration.loadConfiguration(Settings.chestsFile);
        // Messages from messages.yml
        Settings.messages = YamlConfiguration.loadConfiguration(Settings.messagesFile);
        // Kit data from kits.yml
        Settings.kitdata = YamlConfiguration.loadConfiguration(Settings.kitsFile);
        // Shop data from shop.yml
        Settings.shopdata = YamlConfiguration.loadConfiguration(Settings.shopFile);
    }

    public static void loadSettings(PotionGames pg) {
        FileConfiguration cfg = pg.getConfig();
        if (cfg.get("pg.activateMySQL") == null) {
            cfg.addDefault("pg.activateMySQL", activateMySQL);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            activateMySQL = cfg.getBoolean("pg.activateMySQL");
        }
        if (cfg.get("pg.countdown") == null) {
            cfg.addDefault("pg.countdown", countdown);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            countdown = cfg.getInt("pg.countdown");
        }
        if (cfg.get("pg.startOnJoin") == null) {
            cfg.addDefault("pg.startOnJoin", startOnJoin);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            startOnJoin = cfg.getBoolean("pg.startOnJoin");
        }
        if (cfg.get("pg.compassOnSpawn") == null) {
            cfg.addDefault("pg.compassOnSpawn", compassOnSpawn);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            compassOnSpawn = cfg.getBoolean("pg.compassOnSpawn");
        }
        if (cfg.get("pg.allowOutsideChat") == null) {
            cfg.addDefault("pg.allowOutsideChat", allowOutsideChat);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            allowOutsideChat = cfg.getBoolean("pg.allowOutsideChat");
        }
        if (cfg.get("pg.changeGamerules") == null) {
            cfg.addDefault("pg.changeGamerules", changeGamerules);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            changeGamerules = cfg.getBoolean("pg.changeGamerules");
        }
        if (cfg.get("pg.activateTeams") == null) {
            cfg.addDefault("pg.activateTeams", activateTeams);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            activateTeams = cfg.getBoolean("pg.activateTeams");
        }
        if (cfg.get("pg.activateKits") == null) {
            cfg.addDefault("pg.activateKits", activateKits);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            activateKits = cfg.getBoolean("pg.activateKits");
        }
        if (cfg.get("pg.activateShop") == null) {
            cfg.addDefault("pg.activateShop", activateShop);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            activateShop = cfg.getBoolean("pg.activateShop");
        }
        if (cfg.get("pg.activateAirdrops") == null) {
            cfg.addDefault("pg.activateAirdrops", activateAirdrops);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            activateAirdrops = cfg.getBoolean("pg.activateAirdrops");
        }
        if (cfg.get("pg.getGame()Server") == null) {
            cfg.addDefault("pg.getGame()Server", gameServer);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            gameServer = cfg.getBoolean("pg.getGame()Server");
        }
        if (cfg.get("pg.maxPlayers") == null) {
            cfg.addDefault("pg.maxPlayers", maxPlayers);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            maxPlayers = cfg.getInt("pg.maxPlayers");
        }
        if (cfg.get("pg.minPlayers") == null) {
            cfg.addDefault("pg.minPlayers", minPlayers);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            minPlayers = cfg.getInt("pg.minPlayers");
        }
        if (cfg.get("pg.teamSize") == null) {
            cfg.addDefault("pg.teamSize", teamSize);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            teamSize = cfg.getInt("pg.teamSize");
        }
        if (cfg.get("pg.roundTime") == null) {
            cfg.addDefault("pg.roundTime", roundTime / 60);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            roundTime = cfg.getInt("pg.roundTime") * 60;
        }
        if (cfg.get("pg.activePotions") == null) {
            cfg.addDefault("pg.activePotions", activePotions);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            activePotions = cfg.getInt("pg.activePotions");
        }
        if (cfg.get("pg.activeKits") == null) {
            cfg.addDefault("pg.activeKits", activeKits);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            activeKits = cfg.getInt("pg.activeKits");
        }
        if (cfg.get("pg.activateScoreboard") == null) {
            cfg.addDefault("pg.activateScoreboard", activateScoreboard);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            activateScoreboard = cfg.getBoolean("pg.activateScoreboard");
        }
        if (cfg.get("pg.friendlyFire") == null) {
            cfg.addDefault("pg.friendlyFire", friendlyFire);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            friendlyFire = cfg.getBoolean("pg.friendlyFire");
        }
        if (cfg.get("pg.joinStarted") == null) {
            cfg.addDefault("pg.joinStarted", joinStarted);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            joinStarted = cfg.getBoolean("pg.joinStarted");
        }
        if (cfg.get("pg.activateDeathmatch") == null) {
            cfg.addDefault("pg.activateDeathmatch", activateDeathmatch);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            activateDeathmatch = cfg.getBoolean("pg.activateDeathmatch");
        }
        if (cfg.get("pg.enableRewards") == null) {
            cfg.addDefault("pg.enableRewards", enableRewards);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            enableRewards = cfg.getBoolean("pg.enableRewards");
        }
        if (cfg.get("pg.broadcastStarting") == null) {
            cfg.addDefault("pg.broadcastStarting", broadcastStarting);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            broadcastStarting = cfg.getBoolean("pg.broadcastStarting");
        }
        if (cfg.get("pg.winningReward") == null) {
            cfg.addDefault("pg.winningReward", winningReward);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            winningReward = cfg.getInt("pg.winningReward");
        }
        if (cfg.get("pg.killReward") == null) {
            cfg.addDefault("pg.killReward", killReward);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            killReward = cfg.getInt("pg.killReward");
        }
        if (cfg.get("pg.language") == null) {
            cfg.addDefault("pg.language", language);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            language = cfg.getString("pg.language");
        }
        if (cfg.get("pg.mysql") == null) {
            cfg.addDefault("pg.mysql.host", "localhost");
            cfg.addDefault("pg.mysql.port", "3306");
            cfg.addDefault("pg.mysql.database", "potiongames");
            cfg.addDefault("pg.mysql.user", "root");
            cfg.addDefault("pg.mysql.password", "password");
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            String host = cfg.getString("pg.mysql.host");
            String port = cfg.getString("pg.mysql.port");
            String database = cfg.getString("pg.mysql.database");
            String user = cfg.getString("pg.mysql.user");
            String password = cfg.getString("pg.mysql.password");
            mySqlConfig = new MySqlConfiguration(host, port, database, user, password);
        }
    }
}
