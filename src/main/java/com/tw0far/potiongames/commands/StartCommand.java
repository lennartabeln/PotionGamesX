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
            if (plugin.playerLobby.containsKey(player) || plugin.specLobby.containsKey(player)) {
                String lobbyId = null;
                for (int ii = 1; ii <= 27; ii++) {
                    if (plugin.playerLobby.containsKey(player) && plugin.playerLobby.get(player).contains(Integer.toString(ii))) {
                        lobbyId = Integer.toString(ii);
                    }
                }
                if (lobbyId != null) {
                    if (plugin.lobbyAmount.get(lobbyId) >= plugin.lobbyminPlayers.get(lobbyId)) {
                        if (plugin.countdownLobby.get(lobbyId) >= 10) {
                            plugin.countdownLobby.replace(lobbyId, 10);
                            for (Player all : plugin.playerLobby.keySet()) {
                                if (plugin.playerLobby.get(all).equals(lobbyId)) {
                                    all.sendMessage(Messages.GameStarted());
                                }
                            }
                        } else {
                            player.sendMessage(Messages.GameAlreadyStarted());
                        }
                    } else {
                        player.sendMessage(Messages.GameNotEnoughPlayers());
                    }
                }
            }
        } else {
            if (plugin.pgPlayers.contains(player) || plugin.specPlayers.contains(player)) {
                if (plugin.pgPlayers.size() >= plugin.getMinPlayers()) {
                    if (plugin.getCountdown() >= 10) {
                        plugin.setCountdown(10);
                        for (Player all : plugin.pgPlayers) {
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
