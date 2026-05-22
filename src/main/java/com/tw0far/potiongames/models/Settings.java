package com.tw0far.potiongames.models;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.tw0far.potiongames.main.PotionGames;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class Settings {
    public static File arenadatafile = null;
    public static File chestdatafile = null;
    public static File kitdatafile = null;
    public static File messagesfile = null;
    public static File shopdatafile = null;
    public static FileConfiguration arenadata;
    public static FileConfiguration chestdata;
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

    public static void loadConfigurations() {
        Settings.arenadata = YamlConfiguration.loadConfiguration(Settings.arenadatafile);
        Settings.chestdata = YamlConfiguration.loadConfiguration(Settings.chestdatafile);
        Settings.kitdata = YamlConfiguration.loadConfiguration(Settings.kitdatafile);
        Settings.messages = YamlConfiguration.loadConfiguration(Settings.messagesfile);
        Settings.shopdata = YamlConfiguration.loadConfiguration(Settings.shopdatafile);
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
