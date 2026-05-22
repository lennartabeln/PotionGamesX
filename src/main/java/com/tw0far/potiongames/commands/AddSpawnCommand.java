package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Lobby;
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
        // Multi-lobby system: /pg addspawn <lobbynumber> <arenaname>
        if (args.length < 3) {
            player.sendMessage("§cUsage: /pg addspawn <lobbynumber> <arenaname>");
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
            plugin.getSetupHandler().addSpawn(player, arenaName, lobbyId);
            return true;
        } catch (NumberFormatException ex) {
            player.sendMessage("§cUsage: /pg addspawn <lobbynumber> <arenaname>");
            return false;
        }
    }
    
    @Override
    public String getUsage() {
        return "/pg addspawn <lobbynumber> <arenaname>";
    }
}
