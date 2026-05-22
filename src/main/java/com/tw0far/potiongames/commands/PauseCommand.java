package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

/**
 * /pg pause - Pause the game timer
 */
public class PauseCommand implements ICommand {
    private final PotionGames plugin;
    
    public PauseCommand(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "pause";
    }
    
    @Override
    public String getPermission() {
        return "pg.pause";
    }
    
    @Override
    public boolean requiresGameServer() {
        return true;
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        // Multi-lobby mode: get player's lobby and toggle pause for that lobby
        String lobbyId = plugin.getGame().getPlayerLobby(player);
        if (lobbyId == null) {
            lobbyId = plugin.getGame().getSpectatorLobby(player);
        }

        if (lobbyId != null) {
            boolean currentPause = plugin.isLobbyPaused(lobbyId);
            plugin.setLobbyPaused(lobbyId, !currentPause);

            boolean paused = plugin.isLobbyPaused(lobbyId);
            for (Player p : plugin.getGame().getPlayersInLobby(lobbyId)) {
                p.sendMessage(Messages.PauseToggle(paused));
            }
            for (Player p : plugin.getGame().getSpectatorsInLobby(lobbyId)) {
                p.sendMessage(Messages.PauseToggle(paused));
            }
        }
        return true;
    }
    
    @Override
    public String getUsage() {
        return "/pg pause - Pause/resume the game (requires pg.pause)";
    }
}
