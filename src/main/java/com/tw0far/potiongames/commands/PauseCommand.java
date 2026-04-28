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
            if (plugin.playerLobby.containsKey(player) || plugin.specLobby.containsKey(player)) {
                String lobbyId = null;
                for (int ii = 1; ii <= 27; ii++) {
                    if (plugin.playerLobby.containsKey(player) && plugin.playerLobby.get(player).contains(Integer.toString(ii))) {
                        lobbyId = Integer.toString(ii);
                    }
                }
                if (lobbyId != null) {
                    if (plugin.lobbyPause.get(lobbyId)) {
                        plugin.lobbyPause.replace(lobbyId, false);
                    } else {
                        plugin.lobbyPause.replace(lobbyId, true);
                    }
                    
                    boolean paused = plugin.lobbyPause.get(lobbyId);
                    for (Player all : plugin.playerLobby.keySet()) {
                        if (plugin.playerLobby.get(all).equals(lobbyId)) {
                            all.sendMessage(Messages.PauseToggle(paused));
                        }
                    }
                    for (Player all : plugin.specLobby.keySet()) {
                        if (plugin.specLobby.get(all).equals(lobbyId)) {
                            all.sendMessage(Messages.PauseToggle(paused));
                        }
                    }
                }
            }
        } else {
            if (plugin.pgPlayers.contains(player) || plugin.specPlayers.contains(player)) {
                plugin.changePause();
                boolean paused = plugin.isPause();
                for (Player all : plugin.pgPlayers) {
                    all.sendMessage(Messages.PauseToggle(paused));
                }
                for (Player all : plugin.specPlayers) {
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
