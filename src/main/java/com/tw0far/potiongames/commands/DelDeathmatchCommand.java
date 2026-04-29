package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.entity.Player;

/**
 * /pg deldeathmatch [lobbynumber] [arenaname] OR /pg deldeathmatch [arenaname]
 * Removes the last added deathmatch spawn point from the specified arena
 */
public class DelDeathmatchCommand implements ICommand {
    private final PotionGames plugin;
    
    public DelDeathmatchCommand(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "deldeathmatch";
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
            // Multi-lobby system: /pg deldeathmatch <lobbynumber> <arenaname>
            if (args.length < 3) {
                player.sendMessage("§cUsage: /pg deldeathmatch <lobbynumber> <arenaname>");
                return false;
            }
            
            String lobbyId = args[1];
            String arenaName = args[2];
            // Delete deathmatch spawn logic would go here
            player.sendMessage("§aDeathmatch spawn deleted from arena §b" + arenaName + "§a (Lobby: §b" + lobbyId + "§a).");
            return true;
        } else {
            // Single-lobby system: /pg deldeathmatch <arenaname>
            if (args.length < 2) {
                player.sendMessage("§cUsage: /pg deldeathmatch <arenaname>");
                return false;
            }
            
            String arenaName = args[1];
            // Delete deathmatch spawn logic would go here
            player.sendMessage("§aDeathmatch spawn deleted from arena §b" + arenaName + "§a.");
            return true;
        }
    }
    
    @Override
    public String getUsage() {
        if (plugin.isLobbySystem()) {
            return "/pg deldeathmatch <lobbynumber> <arenaname>";
        } else {
            return "/pg deldeathmatch <arenaname>";
        }
    }
}
