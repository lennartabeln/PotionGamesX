package com.tw0far.potiongames.models;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;

public class Participant {
    private Lobby lobby;
    private Kit kit;
    private Team team;
    private Arena votedArena;
    private Player player;
    private PlayerState savedState;

    public Participant(Lobby lobby, Player player) {
        this.lobby = lobby;
        this.player = player;
    }

    public void teleportToLobby() {
        if (savedState == null) {
            savedState = new PlayerState(player);
        }
        clearPlayer();
        player.teleport(lobby.getSpawn());
        player.setGameMode(GameMode.ADVENTURE);
    }

    public void teleportToOriginalLocation() {
        savedState.restore(player);
        savedState = null;
    }

    private void clearPlayer() {
        kit = null;
        team = null;
        votedArena = null;
        player.getInventory().clear();
    }

    public void sendMessage(Component message) {
        player.sendMessage(message);
    }

    public void sendActionBar(Component message) {
        player.sendActionBar(message);
    }

    public Player getPlayer() {
        return player;
    }

    public Kit getKit() {
        return kit;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Arena getVotedArena() {
        return votedArena;
    }

    public void setVotedArena(Arena votedArena) {
        this.votedArena = votedArena;
    }

    public Lobby getLobby() {
        return lobby;
    }
}
