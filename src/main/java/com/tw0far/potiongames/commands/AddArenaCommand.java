package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

/**
 * /pg addarena [lobbynumber] [arenaname] OR /pg addarena [arenaname]
 * Adds an arena to the specified lobby or the default lobby
 */
public class AddArenaCommand implements ICommand {
    private final PotionGames plugin;
    
    public AddArenaCommand(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "addarena";
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
        // Multi-lobby system: /pg addarena <lobbynumber> <arenaname>
        if (args.length < 3) {
            player.sendMessage(Messages.raw("command.addarena.usage", "Usage: /pg addarena <lobbynumber> <arenaname>"));
            return false;
        }
        
        try {
            int lobbyId = Integer.parseInt(args[1]);
            String arenaName = args[2];
            Lobby lobby = plugin.getGame().getLobby(lobbyId);
            if (lobby == null) {
                player.sendMessage(Messages.LobbyDoesNotExist());
                return false;
            }
            if (lobby.addArena(arenaName)) {
                player.sendMessage(Messages.ArenaAdded(arenaName, lobbyId));
                return true;
            }
            player.sendMessage(Messages.ArenaCouldNotLoad());
            return false;
        } catch (NumberFormatException ex) {
            player.sendMessage(Messages.raw("command.addarena.usage", "Usage: /pg addarena <lobbynumber> <arenaname>"));
            return false;
        }
    }
    
    @Override
    public String getUsage() {
        return Messages.raw("command.addarena.usage", "/pg addarena <lobbynumber> <arenaname>");
    }
}
