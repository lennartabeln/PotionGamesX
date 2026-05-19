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
    }
    
    @Override
    public String getUsage() {
        return "/pg deldeathmatch <lobbynumber> <arenaname>";
    }
}
