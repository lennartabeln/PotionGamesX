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
            // Multi-lobby mode: get player's lobby and toggle build mode for that lobby
            String lobbyId = plugin.game.getPlayerLobby(player);
            if (lobbyId == null) {
                lobbyId = plugin.game.getSpectatorLobby(player);
            }
            
            if (lobbyId != null) {
                // Toggle build mode
                if (plugin.lobbyBuild.get(lobbyId)) {
                    plugin.lobbyBuild.replace(lobbyId, false);
                } else {
                    plugin.lobbyBuild.replace(lobbyId, true);
                }
                
                // Broadcast to all players in this lobby
                boolean buildEnabled = plugin.lobbyBuild.get(lobbyId);
                for (Player p : plugin.game.getPlayersInLobby(lobbyId)) {
                    p.sendMessage(Messages.BuildToggle(buildEnabled));
                }
                for (Player p : plugin.game.getSpectatorsInLobby(lobbyId)) {
                    p.sendMessage(Messages.BuildToggle(buildEnabled));
                }
            }
        } else {
            // Single-lobby mode: toggle build mode for all players
            if (plugin.game.isActivePlayer(player) || plugin.game.isSpectatorPlayer(player)) {
                plugin.changeBuild();
                boolean buildEnabled = plugin.isBuild();
                for (Player all : plugin.game.getActivePlayers()) {
                    all.sendMessage(Messages.BuildToggle(buildEnabled));
                }
                for (Player all : plugin.game.getSpectatorPlayers()) {
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
