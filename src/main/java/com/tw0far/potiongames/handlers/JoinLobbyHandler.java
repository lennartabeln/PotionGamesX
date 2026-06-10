package com.tw0far.potiongames.handlers;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Lobby;
import org.bukkit.entity.Player;

public class JoinLobbyHandler {
    private final PotionGamesX plugin;

    public JoinLobbyHandler(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    public void onJoinLobby(Player player, String lobbyId) {
        try {
            Lobby lobby = plugin.getGame().getLobby(Integer.parseInt(lobbyId));
            if (lobby != null) {
                plugin.getGame().setPlayerLobby(player, lobbyId);
                lobby.join(player);
            }
        } catch (NumberFormatException ignored) { }
    }
}
