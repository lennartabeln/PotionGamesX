package com.tw0far.potiongames.models;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.tw0far.potiongames.PotionGamesX;

/**
 * Represents a single arena within a lobby.
 *
 * Encapsulates spawn locations (normal and deathmatch)
 * and per-round voting results.
 */
public class Arena {
    private final String name;
    private final int lobbyId;

    private ArrayList<Location> spawns = new ArrayList<>();
    private ArrayList<Location> deathmatchSpawns = new ArrayList<>();
    private final Random random = new Random();

    private int voteCount = 0;

    public Arena(String name, int lobbyId) {
        this.name = name;
        this.lobbyId = lobbyId;
    }

    public void load() {
        loadSpawns();
        loadDeathmatchSpawns();
    }

    private void loadSpawns() {
        String spawnPath = "pg.lobbies." + lobbyId + ".arenas." + name + ".spawns";
        if (Settings.lobbies.contains(spawnPath)) {
            for (String key : Settings.lobbies.getConfigurationSection(spawnPath).getKeys(false)) {
                try {
                    Location spawn = Settings.lobbies.getLocation(spawnPath + "." + key);
                    if (spawn != null) {
                        spawns.add(spawn);
                    }
                } catch (Exception ex) {
                    PotionGamesX.getInstance().getLogger().warning("Error loading spawn " + key + " for arena " + name + ": " + ex.getMessage());
                }
            }
        }
    }

    private void loadDeathmatchSpawns() {
        String deathmatchPath = "pg.lobbies." + lobbyId + ".arenas." + name + ".deathmatch";
        if (Settings.lobbies.contains(deathmatchPath)) {
            for (String key : Settings.lobbies.getConfigurationSection(deathmatchPath).getKeys(false)) {
                try {
                    Location spawn = Settings.lobbies.getLocation(deathmatchPath + "." + key);
                    if (spawn != null) {
                        deathmatchSpawns.add(spawn);
                    }
                } catch (Exception ex) {
                    PotionGamesX.getInstance().getLogger().warning("Error loading deathmatch spawn " + key + " for arena " + name + ": " + ex.getMessage());
                }
            }
        }
    }

    public boolean add() {
        Settings.lobbies.createSection("pg.lobbies." + lobbyId + ".arenas." + name);
        try {
            Settings.lobbies.save(Settings.lobbiesFile);
            return true;
        } catch (Exception ex) {
            PotionGamesX.getInstance().getLogger().warning(ex.getMessage());
            return false;
        }
    }

    public boolean remove() {
        Settings.lobbies.set("pg.lobbies." + lobbyId + ".arenas." + name, null);
        try {
            Settings.lobbies.save(Settings.lobbiesFile);
            return true;
        } catch (Exception ex) {
            PotionGamesX.getInstance().getLogger().warning(ex.getMessage());
            return false;
        }
    }

    public ArrayList<Location> getSpawns() {
        return new ArrayList<>(spawns);
    }

    public boolean addSpawn(Location spawn) {
        if (spawn == null) {
            return false;
        }

        int nextId = spawns.size();
        String spawnPath = "pg.lobbies." + lobbyId + ".arenas." + name + ".spawns." + nextId;
        Settings.lobbies.set(spawnPath, spawn);
        spawns.add(spawn);

        try {
            Settings.lobbies.save(Settings.lobbiesFile);
            return true;
        } catch (Exception ex) {
            PotionGamesX.getInstance().getLogger().warning(ex.getMessage());
            spawns.remove(spawn);
            return false;
        }
    }

    public boolean addSpawn(int spawnId, Location spawn) {
        if (spawn == null) {
            return false;
        }

        String spawnPath = "pg.lobbies." + lobbyId + ".arenas." + name + ".spawns." + spawnId;
        Settings.lobbies.set(spawnPath, spawn);
        spawns.add(spawn);

        try {
            Settings.lobbies.save(Settings.lobbiesFile);
            return true;
        } catch (Exception ex) {
            PotionGamesX.getInstance().getLogger().warning(ex.getMessage());
            spawns.remove(spawn);
            return false;
        }
    }

    public boolean removeSpawn(int spawnId) {
        int index = spawnId - 1;
        if (index < 0 || index >= spawns.size()) {
            return false;
        }

        String spawnPath = "pg.lobbies." + lobbyId + ".arenas." + name + ".spawns." + spawnId;
        Settings.lobbies.set(spawnPath, null);

        try {
            spawns.remove(index);
            Settings.lobbies.save(Settings.lobbiesFile);
            return true;
        } catch (Exception ex) {
            PotionGamesX.getInstance().getLogger().warning(ex.getMessage());
            return false;
        }
    }

    public ArrayList<Location> getDeathmatchSpawns() {
        return new ArrayList<>(deathmatchSpawns);
    }

    public Location getRandomDeathmatchSpawn() {
        if (deathmatchSpawns.isEmpty()) {
            return null;
        }
        return deathmatchSpawns.get(random.nextInt(deathmatchSpawns.size()));
    }

    public boolean addDeathmatchSpawn(Location spawn) {
        if (spawn == null) {
            return false;
        }

        int nextId = deathmatchSpawns.size();
        String spawnPath = "pg.lobbies." + lobbyId + ".arenas." + name + ".deathmatch." + nextId;
        Settings.lobbies.set(spawnPath, spawn);
        deathmatchSpawns.add(spawn);

        try {
            Settings.lobbies.save(Settings.lobbiesFile);
            return true;
        } catch (Exception ex) {
            PotionGamesX.getInstance().getLogger().warning(ex.getMessage());
            deathmatchSpawns.remove(spawn);
            return false;
        }
    }

    public boolean addDeathmatchSpawn(int spawnId, Location spawn) {
        if (spawn == null) {
            return false;
        }

        String spawnPath = "pg.lobbies." + lobbyId + ".arenas." + name + ".deathmatch." + spawnId;
        Settings.lobbies.set(spawnPath, spawn);
        deathmatchSpawns.add(spawn);

        try {
            Settings.lobbies.save(Settings.lobbiesFile);
            return true;
        } catch (Exception ex) {
            PotionGamesX.getInstance().getLogger().warning(ex.getMessage());
            deathmatchSpawns.remove(spawn);
            return false;
        }
    }

    public boolean removeDeathmatchSpawn(int spawnId) {
        int index = spawnId - 1;
        if (index < 0 || index >= deathmatchSpawns.size()) {
            return false;
        }

        String spawnPath = "pg.lobbies." + lobbyId + ".arenas." + name + ".deathmatch." + spawnId;
        Settings.lobbies.set(spawnPath, null);

        try {
            deathmatchSpawns.remove(index);
            Settings.lobbies.save(Settings.lobbiesFile);
            return true;
        } catch (Exception ex) {
            PotionGamesX.getInstance().getLogger().warning(ex.getMessage());
            return false;
        }
    }

    public synchronized void recordVote(Player player) {
        if (player != null) {
            voteCount++;
        }
    }

    public synchronized int getVoteCount() {
        return voteCount;
    }

    public synchronized void resetVotes() {
        voteCount = 0;
    }

    public String getName() {
        return name;
    }

    public void teleport(ArrayList<Participant> participants) {
        if (spawns.isEmpty()) {
            return;
        }
        for (int i = 0; i < participants.size(); i++) {
            Participant participant = participants.get(i);
            Location spawnLocation = spawns.get(i % spawns.size());
            participant.getPlayer().teleport(spawnLocation);
        }
    }
}
