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
        return true;
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        if (plugin.isLobbySystem()) {
            // Multi-lobby mode
            String lobbyId = plugin.game.getPlayerLobby(player);
            if (lobbyId == null) {
                lobbyId = plugin.game.getSpectatorLobby(player);
            }
            
            if (lobbyId != null) {
                if (plugin.lobbyAmount.get(lobbyId) >= plugin.lobbyminPlayers.get(lobbyId)) {
                    if (plugin.countdownLobby.get(lobbyId) >= 10) {
                        plugin.countdownLobby.replace(lobbyId, 10);
                        // Broadcast to all players in this lobby
                        for (Player all : plugin.game.getPlayersInLobby(lobbyId)) {
                            all.sendMessage(Messages.GameStarted());
                        }
                    } else {
                        player.sendMessage(Messages.GameAlreadyStarted());
                    }
                } else {
                    player.sendMessage(Messages.GameNotEnoughPlayers());
                }
            }
        } else {
            // Single-lobby mode
            if (plugin.game.isActivePlayer(player) || plugin.game.isSpectatorPlayer(player)) {
                if (plugin.game.getActivePlayers().size() >= plugin.getMinPlayers()) {
                    if (plugin.getCountdown() >= 10) {
                        plugin.setCountdown(10);
                        for (Player all : plugin.game.getActivePlayers()) {
                            all.sendMessage(Messages.GameStarted());
                        }
                    } else {
                        player.sendMessage(Messages.GameAlreadyStarted());
                    }
                } else {
                    player.sendMessage(Messages.GameNotEnoughPlayers());
                }
            }
        }
        return true;
    }
    
    @Override
    public String getUsage() {
        return "/pg start - Set lobby countdown to 10 seconds (requires pg.start)";
    }
}
