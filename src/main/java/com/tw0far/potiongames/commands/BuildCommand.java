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
        // Multi-lobby mode: get player's lobby and toggle build mode for that lobby
        String lobbyId = plugin.getGame().getPlayerLobby(player);
        if (lobbyId == null) {
            lobbyId = plugin.getGame().getSpectatorLobby(player);
        }
        
        if (lobbyId != null) {
            // Toggle build mode
            boolean currentBuild = plugin.isLobbyBuildAllowed(lobbyId);
            plugin.setLobbyBuildAllowed(lobbyId, !currentBuild);
            
            // Broadcast to all players in this lobby
            boolean buildEnabled = plugin.isLobbyBuildAllowed(lobbyId);
            for (Player p : plugin.getGame().getPlayersInLobby(lobbyId)) {
                p.sendMessage(Messages.BuildToggle(buildEnabled));
            }
            for (Player p : plugin.getGame().getSpectatorsInLobby(lobbyId)) {
                p.sendMessage(Messages.BuildToggle(buildEnabled));
            }
        }
        return true;
    }
    
    @Override
    public String getUsage() {
        return Messages.raw("help.build_usage", "/pg build - Enable/disable build mode (requires pg.build)");
    }
}
