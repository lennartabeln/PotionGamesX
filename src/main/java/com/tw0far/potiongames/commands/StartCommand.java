package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

/**
 * /pg start - Set game countdown to 10 seconds
 */
public class StartCommand implements ICommand {
    private final PotionGames plugin;
    
    public StartCommand(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "start";
    }
    
    @Override
    public String getPermission() {
        return "pg.start";
    }
    
    @Override
    public boolean requiresGameServer() {
        return false;
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        // Multi-lobby mode
        String lobbyId = plugin.getGame().getPlayerLobby(player);
        if (lobbyId == null) {
            lobbyId = plugin.getGame().getSpectatorLobby(player);
        }
        
        if (lobbyId != null) {
            if (plugin.getLobbyAmount(lobbyId) >= plugin.getLobbyMinPlayers(lobbyId)) {
                if (plugin.getLobbyCountdown(lobbyId) >= 10) {
                    plugin.setLobbyCountdown(lobbyId, 10);
                    // Broadcast to all players in this lobby
                    for (Player all : plugin.getGame().getPlayersInLobby(lobbyId)) {
                        all.sendMessage(Messages.GameStarted());
                    }
                } else {
                    player.sendMessage(Messages.GameAlreadyStarted());
                }
            } else {
                player.sendMessage(Messages.GameNotEnoughPlayers());
            }
        }
        return true;
    }
    
    @Override
    public String getUsage() {
        return Messages.raw("help.start_usage", "/pg start - Set lobby countdown to 10 seconds (requires pg.start)");
    }
}
