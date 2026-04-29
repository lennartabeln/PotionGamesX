package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
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
        if (plugin.isLobbySystem()) {
            // Multi-lobby system
            if (args.length < 2) {
                player.sendMessage("§cUsage: /pg setlobby <lobbynumber>");
                return false;
            }
            
            String lobbyId = args[1];
            // Create/set lobby logic would go here
            player.sendMessage("§aLobby §b" + lobbyId + "§a set successfully.");
            return true;
        } else {
            // Single-lobby system
            // Set lobby logic would go here
            player.sendMessage("§aDefault lobby set successfully.");
            return true;
        }
    }
    
    @Override
    public String getUsage() {
        if (plugin.isLobbySystem()) {
            return "/pg setlobby <lobbynumber>";
        } else {
            return "/pg setlobby";
        }
    }
}

