package com.tw0far.potiongames.models;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Arena {
    private String name;
    private ArrayList<Location> spawns = new ArrayList<>();
    private ArrayList<Location> deathmatchSpawns = new ArrayList<>();
    private int lobbyId;

    public Arena(String name, int lobbyId) {
        this.name = name;
        this.lobbyId = lobbyId;
    }

    public void load() {
        if (Settings.arenadata.contains("pg.lobbies." + lobbyId + ".arenas." + name + ".spawns")) {
            for (String key : Settings.arenadata.getConfigurationSection("pg.lobbies." + lobbyId + ".arenas." + name + ".spawns").getKeys(false)) {
                int spawnId = Integer.parseInt(key);
                Location spawn = Settings.arenadata.getLocation("pg.lobbies." + lobbyId + ".arenas." + name + ".spawns." + spawnId);
                spawns.add(spawn);
            }
        }
        if (Settings.arenadata.contains("pg.lobbies." + lobbyId + ".arenas." + name + ".deathmatch")) {
            for (String key : Settings.arenadata.getConfigurationSection("pg.lobbies." + lobbyId + ".arenas." + name + ".deathmatch").getKeys(false)) {
                int spawnId = Integer.parseInt(key);
                Location spawn = Settings.arenadata.getLocation("pg.lobbies." + lobbyId + ".arenas." + name + ".deathmatch." + spawnId);
                deathmatchSpawns.add(spawn);
            }
        }
    }

    public boolean add() {
        Settings.arenadata.createSection("pg.lobbies." + lobbyId + ".arenas." + name);
        try {
            Settings.arenadata.save(Settings.arenadatafile);
            return true;
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ex.getMessage());
            return false;
        }
    }

    public boolean remove() {
        Settings.arenadata.set("pg.lobbies." + lobbyId + ".arenas." + name, null);
        try {
            Settings.arenadata.save(Settings.arenadatafile);
            return true;
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ex.getMessage());
            return false;
        }
    }

    public boolean addSpawn(int spawnId, Location spawn) {
        Settings.arenadata.set("pg.lobbies." + lobbyId + ".arenas." + name + ".spawns." + spawnId, spawn);
        spawns.add(spawn);
        try {
            Settings.arenadata.save(Settings.arenadatafile);
            return true;
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ex.getMessage());
            return false;
        }
    }

    public boolean removeSpawn(int spawnId) {
        Settings.arenadata.set("pg.lobbies." + lobbyId + ".arenas." + name + ".spawns." + spawnId, null);
        spawns.remove(spawnId);
        try {
            Settings.arenadata.save(Settings.arenadatafile);
            return true;
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ex.getMessage());
            return false;
        }
    }

    public boolean addDeathmatchSpawn(int spawnId, Location spawn) {
        Settings.arenadata.set("pg.lobbies." + lobbyId + ".arenas." + name + ".deathmatch." + spawnId, spawn);
        deathmatchSpawns.add(spawn);
        try {
            Settings.arenadata.save(Settings.arenadatafile);
            return true;
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ex.getMessage());
            return false;
        }
    }

    public boolean removeDeathmatchSpawn(int spawnId) {
        Settings.arenadata.set("pg.lobbies." + lobbyId + ".arenas." + name + ".deathmatch." + spawnId, null);
        deathmatchSpawns.remove(spawnId);
        try {
            Settings.arenadata.save(Settings.arenadatafile);
            return true;
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ex.getMessage());
            return false;
        }
    }

    public void teleport(ArrayList<Participant> participants) {
        for (int i = 0; i < participants.size(); i++) {
            Participant participant = participants.get(i);
            Location spawnLocation = spawns.get(i % spawns.size());
            participant.getPlayer().teleport(spawnLocation);
            // participant.sendMessage(Messages.TeleportToArenaNow());
        }
    }

    public String getName() {
        return name;
    }

    public ArrayList<Location> getSpawns() {
        return spawns;
    }

    public ArrayList<Location> getDeathmatchSpawns() {
        return deathmatchSpawns;
    }
}