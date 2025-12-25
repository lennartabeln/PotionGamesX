package com.tw0far.potiongames.models;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Game {
    private ArrayList<Lobby> lobbies = new ArrayList<>();

    public void load() {
        if (Settings.arenadata.contains("pg.lobbies")) {
            for (String key : Settings.arenadata.getConfigurationSection("pg.lobbies").getKeys(false)) {
                int lobbyId = Integer.parseInt(key);
                Lobby lobby = new Lobby(lobbyId);
                lobby.load();
                lobbies.add(lobby);
            }
        }
    }

    public Lobby getLobby(int lobbyId) {
        for (Lobby lobby : lobbies) {
            if (lobby.getId() == lobbyId) {
                return lobby;
            }
        }
        return null;
    }

    public Lobby getLobbyByPlayer(Player p) {
        for (Lobby lobby : lobbies) {
            for (Participant part : lobby.getParticipants()) {
                if (part.getPlayer().equals(p)) {
                    return lobby;
                }
            }
        }
        return null;
    }

    public boolean addLobby(int lobbyId, Location location) {
        Lobby lobby = new Lobby(lobbyId);
        boolean success = lobby.add(location);
        if (success) {
            lobbies.add(lobby);
        }
        return success;
    }

    public boolean removeLobby(int lobbyId) {
        Lobby lobby = getLobby(lobbyId);
        if (lobby != null) {
            boolean success = lobby.remove();
            if (success) {
                lobbies.remove(lobby);
            }
            return success;
        }
        return false;
    }

    public ArrayList<Lobby> getLobbies() {
        return lobbies;
    }
}
