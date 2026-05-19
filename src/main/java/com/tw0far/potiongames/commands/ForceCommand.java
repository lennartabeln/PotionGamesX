package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.Settings;
import org.bukkit.entity.Player;

import java.util.Objects;

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
        return true;
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length < 1) {
            return false;
        }
        
        String arena = args[0];
        
        // Multi-lobby mode
        String lobbyId = plugin.game.getPlayerLobby(player);
        if (lobbyId == null) {
            lobbyId = plugin.game.getSpectatorLobby(player);
        }
        
        if (lobbyId != null) {
            try {
                plugin.setLobbyForcearena(lobbyId, true);
                int i = 1;
                boolean votetedarena = false;
                while (!votetedarena) {
                    if (arena.matches(Objects.requireNonNull(Settings.arenadata.getString("pg.arenas." + i + ".name")))) {
                        String arenaNumber = Integer.toString(i);
                        plugin.setLobbyVotedArena(lobbyId, arena);
                        plugin.setLobbyCurrentVote(lobbyId, arenaNumber);
                        votetedarena = true;
                    } else {
                        i++;
                    }
                }
                // Broadcast to all players in this lobby
                for (Player all : plugin.game.getPlayersInLobby(lobbyId)) {
                    all.sendMessage(Messages.ArenaForced(arena));
                }
            } catch (Exception ex) {
                player.sendMessage(Messages.ArenaNotArena(arena));
            }
        }
        return true;
    }
    
    @Override
    public String getUsage() {
        return "/pg force <arena_name> - Force a specific arena (requires pg.force)";
    }
}
