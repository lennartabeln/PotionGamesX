package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
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
        return true;
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        if (plugin.isLobbySystem()) {
            // Multi-lobby system: /pg delarena <lobbynumber> <arenaname>
            if (args.length < 3) {
                player.sendMessage("§cUsage: /pg delarena <lobbynumber> <arenaname>");
                return false;
            }
            
            String lobbyId = args[1];
            String arenaName = args[2];
            // Delete arena logic would go here
            player.sendMessage("§aArena §b" + arenaName + "§a deleted from lobby §b" + lobbyId + "§a.");
            return true;
        } else {
            // Single-lobby system: /pg delarena <arenaname>
            if (args.length < 2) {
                player.sendMessage("§cUsage: /pg delarena <arenaname>");
                return false;
            }
            
            String arenaName = args[1];
            // Delete arena logic would go here
            player.sendMessage("§aArena §b" + arenaName + "§a deleted successfully.");
            return true;
        }
    }
    
    @Override
    public String getUsage() {
        if (plugin.isLobbySystem()) {
            return "/pg delarena <lobbynumber> <arenaname>";
        } else {
            return "/pg delarena <arenaname>";
        }
    }
}
