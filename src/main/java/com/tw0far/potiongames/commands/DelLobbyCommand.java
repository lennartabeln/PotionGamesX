package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.entity.Player;

/**
 * /pg dellobby [lobbynumber] - Remove a lobby (Multi-Lobby only)
 */
public class DelLobbyCommand implements ICommand {
    private final PotionGames plugin;
    
    public DelLobbyCommand(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "dellobby";
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
        if (args.length < 2) {
            player.sendMessage("§cUsage: /pg dellobby <lobbynumber>");
            return false;
        }
        try {
            int lobbyId = Integer.parseInt(args[1]);
            plugin.setupHandler.removeLobby(player, lobbyId);
            return true;
        } catch (NumberFormatException ex) {
            player.sendMessage("§cUsage: /pg dellobby <lobbynumber>");
            return false;
        }
    }
    
    @Override
    public String getUsage() {
        return "/pg dellobby <lobbynumber>";
    }
}
