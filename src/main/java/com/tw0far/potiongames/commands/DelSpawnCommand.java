package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Lobby;
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
        // Multi-lobby system: /pg delspawn <lobbynumber> <arenaname>
        if (args.length < 3) {
            player.sendMessage("§cUsage: /pg delspawn <lobbynumber> <arenaname>");
            return false;
        }
        
        try {
            int lobbyId = Integer.parseInt(args[1]);
            String arenaName = args[2];
            Lobby lobby = plugin.getLobbyById(lobbyId);
            if (lobby == null) {
                player.sendMessage("§cThis lobby does not exists!");
                return false;
            }
            plugin.getSetupHandler().removeSpawn(player, arenaName, lobbyId);
            return true;
        } catch (NumberFormatException ex) {
            player.sendMessage("§cUsage: /pg delspawn <lobbynumber> <arenaname>");
            return false;
        }
    }
    
    @Override
    public String getUsage() {
        return "/pg delspawn <lobbynumber> <arenaname>";
    }
}
