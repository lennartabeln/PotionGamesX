package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

/**
 * /pg delarena [lobbynumber] [arenaname] OR /pg delarena [arenaname]
 * Removes an arena from the specified lobby or the default lobby
 */
public class DelArenaCommand implements ICommand {
    private final PotionGames plugin;
    
    public DelArenaCommand(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "delarena";
    }
    
    @Override
    public String getPermission() {
        return "pg.setup";
    }
    
    @Override
    public boolean requiresGameServer() {
        return false;
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        // Multi-lobby system: /pg delarena <lobbynumber> <arenaname>
        if (args.length < 3) {
            player.sendMessage(Messages.raw("command.delarena.usage", "Usage: /pg delarena <lobbynumber> <arenaname>"));
            return false;
        }
        
        try {
            int lobbyId = Integer.parseInt(args[1]);
            String arenaName = args[2];
            Lobby lobby = plugin.getLobbyById(lobbyId);
            if (lobby == null) {
                player.sendMessage(Messages.LobbyDoesNotExist());
                return false;
            }
            if (lobby.removeArena(arenaName)) {
                player.sendMessage(Messages.ArenaRemoved(arenaName, lobbyId));
                return true;
            }
            player.sendMessage(Messages.ArenaCouldNotLoad());
            return false;
        } catch (NumberFormatException ex) {
            player.sendMessage(Messages.raw("command.delarena.usage", "Usage: /pg delarena <lobbynumber> <arenaname>"));
            return false;
        }
    }
    
    @Override
    public String getUsage() {
        return Messages.raw("command.delarena.usage", "/pg delarena <lobbynumber> <arenaname>");
    }
}
