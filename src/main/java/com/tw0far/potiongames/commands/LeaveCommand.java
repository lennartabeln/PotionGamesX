package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;

import org.bukkit.entity.Player;

/**
 * /pg leave - Leave current game
 */
public class LeaveCommand implements ICommand {
    private final PotionGames plugin;
    
    public LeaveCommand(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "leave";
    }
    
    @Override
    public String getPermission() {
        return null; // Everyone can use
    }
    
    @Override
    public boolean requiresGameServer() {
        return true;
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        Lobby lobby = plugin.getGame().getLobbyByPlayer(player);
        if (lobby != null) {
            lobby.leave(player);
        }
        return true;
    }
    
    @Override
    public String getUsage() {
        return Messages.raw("help.leave_usage", "/pg leave - Leave the current game");
    }
}
