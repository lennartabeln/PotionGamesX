package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

/**
 * /pg setlobby [lobbynumber] - Set/create a lobby
 */
public class SetLobbyCommand implements ICommand {
    private final PotionGames plugin;
    
    public SetLobbyCommand(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "setlobby";
    }
    
    @Override
    public String getPermission() {
        return "pg.setup";
    }
    
    @Override
    public boolean requiresGameServer() {
        return true;
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Messages.raw("command.setlobby.usage", "Usage: /pg setlobby <lobbynumber>"));
            return false;
        }
        try {
            int lobbyId = Integer.parseInt(args[1]);
            plugin.getSetupHandler().addLobby(player, lobbyId);
            return true;
        } catch (NumberFormatException ex) {
            player.sendMessage(Messages.raw("command.setlobby.usage", "Usage: /pg setlobby <lobbynumber>"));
            return false;
        }
    }
    
    @Override
    public String getUsage() {
        return Messages.raw("command.setlobby.usage", "/pg setlobby <lobbynumber>");
    }
}
