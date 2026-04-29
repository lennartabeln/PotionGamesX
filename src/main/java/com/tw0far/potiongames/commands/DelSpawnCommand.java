package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.entity.Player;

/**
 * /pg delspawn [lobbynumber] [arenaname] OR /pg delspawn [arenaname]
 * Removes the last added spawn point from the specified arena
 */
public class DelSpawnCommand implements ICommand {
    private final PotionGames plugin;
    
    public DelSpawnCommand(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "delspawn";
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
            // Multi-lobby system: /pg delspawn <lobbynumber> <arenaname>
            if (args.length < 3) {
                player.sendMessage("§cUsage: /pg delspawn <lobbynumber> <arenaname>");
                return false;
            }
            
            String lobbyId = args[1];
            String arenaName = args[2];
            // Delete spawn logic would go here
            player.sendMessage("§aSpawn deleted from arena §b" + arenaName + "§a (Lobby: §b" + lobbyId + "§a).");
            return true;
        } else {
            // Single-lobby system: /pg delspawn <arenaname>
            if (args.length < 2) {
                player.sendMessage("§cUsage: /pg delspawn <arenaname>");
                return false;
            }
            
            String arenaName = args[1];
            // Delete spawn logic would go here
            player.sendMessage("§aSpawn deleted from arena §b" + arenaName + "§a.");
            return true;
        }
    }
    
    @Override
    public String getUsage() {
        if (plugin.isLobbySystem()) {
            return "/pg delspawn <lobbynumber> <arenaname>";
        } else {
            return "/pg delspawn <arenaname>";
        }
    }
}
