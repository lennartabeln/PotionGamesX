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
        if (plugin.isLobbySystem()) {
            // Multi-lobby mode: get player's lobby and toggle pause for that lobby
            String lobbyId = plugin.game.getPlayerLobby(player);
            if (lobbyId == null) {
                lobbyId = plugin.game.getSpectatorLobby(player);
            }
            
            if (lobbyId != null) {
                // Toggle pause mode
                boolean currentPause = plugin.isLobbyPaused(lobbyId);
                plugin.setLobbyPaused(lobbyId, !currentPause);
                
                // Broadcast to all players in this lobby
                boolean paused = plugin.isLobbyPaused(lobbyId);
                for (Player p : plugin.game.getPlayersInLobby(lobbyId)) {
                    p.sendMessage(Messages.PauseToggle(paused));
                }
                for (Player p : plugin.game.getSpectatorsInLobby(lobbyId)) {
                    p.sendMessage(Messages.PauseToggle(paused));
                }
            }
        } else {
            // Single-lobby mode: toggle pause mode for all players
            if (plugin.game.isActivePlayer(player) || plugin.game.isSpectatorPlayer(player)) {
                plugin.changePause();
                boolean paused = plugin.isPause();
                for (Player all : plugin.game.getActivePlayers()) {
                    all.sendMessage(Messages.PauseToggle(paused));
                }
                for (Player all : plugin.game.getSpectatorPlayers()) {
                    all.sendMessage(Messages.PauseToggle(paused));
                }
            }
        }
        return true;
    }
    
    @Override
    public String getUsage() {
        return "/pg pause - Pause/resume the game (requires pg.pause)";
    }
}
