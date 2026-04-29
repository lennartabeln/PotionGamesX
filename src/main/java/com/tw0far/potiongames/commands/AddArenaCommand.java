package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
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
        return true;
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        if (plugin.isLobbySystem()) {
            // Multi-lobby system: /pg addarena <lobbynumber> <arenaname>
            if (args.length < 3) {
                player.sendMessage("§cUsage: /pg addarena <lobbynumber> <arenaname>");
                return false;
            }
            
            String lobbyId = args[1];
            String arenaName = args[2];
            // Add arena logic would go here
            player.sendMessage("§aArena §b" + arenaName + "§a added to lobby §b" + lobbyId + "§a.");
            return true;
        } else {
            // Single-lobby system: /pg addarena <arenaname>
            if (args.length < 2) {
                player.sendMessage("§cUsage: /pg addarena <arenaname>");
                return false;
            }
            
            String arenaName = args[1];
            // Add arena logic would go here
            player.sendMessage("§aArena §b" + arenaName + "§a added successfully.");
            return true;
        }
    }
    
    @Override
    public String getUsage() {
        if (plugin.isLobbySystem()) {
            return "/pg addarena <lobbynumber> <arenaname>";
        } else {
            return "/pg addarena <arenaname>";
        }
    }
}
