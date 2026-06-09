package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Arena;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

/**
 * /pg force [arena] - Force a specific arena
 */
public class ForceCommand implements ICommand {
    private final PotionGames plugin;
    
    public ForceCommand(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "force";
    }
    
    @Override
    public String getPermission() {
        return "pg.force";
    }
    
    @Override
    public boolean requiresGameServer() {
        return false;
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length < 2) {
            return false;
        }
        
        String arena = args[1];
        
        // Multi-lobby mode
        String lobbyId = plugin.getGame().getPlayerLobby(player);
        if (lobbyId == null) {
            lobbyId = plugin.getGame().getSpectatorLobby(player);
        }
        
        if (lobbyId != null) {
            try {
                Lobby lobby = plugin.getGame().getLobby(Integer.parseInt(lobbyId));
                if (lobby != null) {
                    Arena targetArena = lobby.getArena(arena);
                    if (targetArena != null) {
                        plugin.getLobbyStateManager().setForcearena(lobbyId, true);
                        lobby.setCurrentArena(targetArena);
                        lobby.setVotedArenaName(arena);
                        
                        // Broadcast to all players in this lobby
                        for (Player all : plugin.getGame().getPlayersInLobby(lobbyId)) {
                            all.sendMessage(Messages.ArenaForced(arena));
                        }
                    } else {
                        player.sendMessage(Messages.ArenaNotArena(arena));
                    }
                }
            } catch (Exception ex) {
                player.sendMessage(Messages.ArenaNotArena(arena));
            }
        }
        return true;
    }
    
    @Override
    public String getUsage() {
        return Messages.raw("help.force_usage", "/pg force <arena_name> - Force a specific arena (requires pg.force)");
    }
}
