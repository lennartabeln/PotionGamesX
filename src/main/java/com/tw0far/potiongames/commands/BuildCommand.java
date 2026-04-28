package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

/**
 * /pg build - Enable/disable build mode
 */
public class BuildCommand implements ICommand {
    private final PotionGames plugin;
    
    public BuildCommand(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "build";
    }
    
    @Override
    public String getPermission() {
        return "pg.build";
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
                    if (plugin.lobbyBuild.get(lobbyId)) {
                        plugin.lobbyBuild.replace(lobbyId, false);
                    } else {
                        plugin.lobbyBuild.replace(lobbyId, true);
                    }
                    
                    boolean buildEnabled = plugin.lobbyBuild.get(lobbyId);
                    for (Player all : plugin.playerLobby.keySet()) {
                        if (plugin.playerLobby.get(all).equals(lobbyId)) {
                            all.sendMessage(Messages.BuildToggle(buildEnabled));
                        }
                    }
                    for (Player all : plugin.specLobby.keySet()) {
                        if (plugin.specLobby.get(all).equals(lobbyId)) {
                            all.sendMessage(Messages.BuildToggle(buildEnabled));
                        }
                    }
                }
            }
        } else {
            if (plugin.pgPlayers.contains(player) || plugin.specPlayers.contains(player)) {
                plugin.changeBuild();
                boolean buildEnabled = plugin.isBuild();
                for (Player all : plugin.pgPlayers) {
                    all.sendMessage(Messages.BuildToggle(buildEnabled));
                }
                for (Player all : plugin.specPlayers) {
                    all.sendMessage(Messages.BuildToggle(buildEnabled));
                }
            }
        }
        return true;
    }
    
    @Override
    public String getUsage() {
        return "/pg build - Enable/disable build mode (requires pg.build)";
    }
}
