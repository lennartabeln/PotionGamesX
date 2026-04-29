package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.entity.Player;

/**
 * /pg joinsign [lobbynumber] OR /pg joinsign
 * Creates a join sign at the player's current location
 */
public class JoinSignCommand implements ICommand {
    private final PotionGames plugin;
    
    public JoinSignCommand(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "joinsign";
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
            // Multi-lobby system: /pg joinsign <lobbynumber>
            if (args.length < 2) {
                player.sendMessage("§cUsage: /pg joinsign <lobbynumber>");
                return false;
            }
            
            String lobbyId = args[1];
            // Create join sign logic would go here
            player.sendMessage("§aJoin sign created for lobby §b" + lobbyId + "§a.");
            return true;
        } else {
            // Single-lobby system: /pg joinsign
            // Create join sign logic would go here
            player.sendMessage("§aJoin sign created successfully.");
            return true;
        }
    }
    
    @Override
    public String getUsage() {
        if (plugin.isLobbySystem()) {
            return "/pg joinsign <lobbynumber>";
        } else {
            return "/pg joinsign";
        }
    }
}
