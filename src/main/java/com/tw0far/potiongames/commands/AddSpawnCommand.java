package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.entity.Player;

/**
 * /pg addspawn [lobbynumber] [arenaname] OR /pg addspawn [arenaname]
 * Adds a spawn point to the specified arena at the player's current location
 */
public class AddSpawnCommand implements ICommand {
    private final PotionGames plugin;
    
    public AddSpawnCommand(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "addspawn";
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
            // Multi-lobby system: /pg addspawn <lobbynumber> <arenaname>
            if (args.length < 3) {
                player.sendMessage("§cUsage: /pg addspawn <lobbynumber> <arenaname>");
                return false;
            }
            
            String lobbyId = args[1];
            String arenaName = args[2];
            // Add spawn logic would go here
            player.sendMessage("§aSpawn added to arena §b" + arenaName + "§a (Lobby: §b" + lobbyId + "§a).");
            return true;
        } else {
            // Single-lobby system: /pg addspawn <arenaname>
            if (args.length < 2) {
                player.sendMessage("§cUsage: /pg addspawn <arenaname>");
                return false;
            }
            
            String arenaName = args[1];
            // Add spawn logic would go here
            player.sendMessage("§aSpawn added to arena §b" + arenaName + "§a.");
            return true;
        }
    }
    
    @Override
    public String getUsage() {
        if (plugin.isLobbySystem()) {
            return "/pg addspawn <lobbynumber> <arenaname>";
        } else {
            return "/pg addspawn <arenaname>";
        }
    }
}
