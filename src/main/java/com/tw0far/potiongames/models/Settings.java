package com.tw0far.potiongames.models;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.tw0far.potiongames.PotionGamesX;

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
    public static boolean activateTeams = true;
    public static boolean activateKits = true;
    public static boolean activateShop = true;
    public static boolean activateAirdrops = true;
    public static boolean activateDeathmatch = true;
    public static int maxPlayers = 24;
    public static int minPlayers = 2;
    public static int teamSize = 2;
    public static int roundTime = 30;
    public static int countdown = 60;
    public static String language = "en_US";


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

    public static void loadSettings(PotionGamesX pg) {
        FileConfiguration cfg = pg.getConfig();
        if (cfg.get("pg.countdown") == null) {
            cfg.addDefault("pg.countdown", countdown);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            countdown = cfg.getInt("pg.countdown");
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
        if (cfg.get("pg.activateDeathmatch") == null) {
            cfg.addDefault("pg.activateDeathmatch", activateDeathmatch);
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        } else {
            activateDeathmatch = cfg.getBoolean("pg.activateDeathmatch");
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
            cfg.addDefault("pg.mysql.database", "PotionGamesX");
            cfg.addDefault("pg.mysql.user", "root");
            cfg.addDefault("pg.mysql.password", "password");
            cfg.options().copyDefaults(true);
            pg.saveConfig();
        }
    }
}
