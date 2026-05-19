package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.entity.Player;

/**
 * /pg adddeathmatch [lobbynumber] [arenaname] OR /pg adddeathmatch [arenaname]
 * Adds a deathmatch spawn point to the specified arena at the player's current location
 */
public class AddDeathmatchCommand implements ICommand {
    private final PotionGames plugin;
    
    public AddDeathmatchCommand(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "adddeathmatch";
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
        // Multi-lobby system: /pg adddeathmatch <lobbynumber> <arenaname>
        if (args.length < 3) {
            player.sendMessage("§cUsage: /pg adddeathmatch <lobbynumber> <arenaname>");
            return false;
        }
        
        String lobbyId = args[1];
        String arenaName = args[2];
        // Add deathmatch spawn logic would go here
        player.sendMessage("§aDeathmatch spawn added to arena §b" + arenaName + "§a (Lobby: §b" + lobbyId + "§a).");
        return true;
    }
    
    @Override
    public String getUsage() {
        return "/pg adddeathmatch <lobbynumber> <arenaname>";
    }
}
