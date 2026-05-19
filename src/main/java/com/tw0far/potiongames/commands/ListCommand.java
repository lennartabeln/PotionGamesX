package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.entity.Player;

/**
 * /pg list - Open GUI with all lobbies
 */
public class ListCommand implements ICommand {
    private final PotionGames plugin;
    
    public ListCommand(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "list";
    }
    
    @Override
    public String getPermission() {
        return "pg.join";
    }
    
    @Override
    public boolean requiresGameServer() {
        return false;
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        // Open GUI with all lobbies
        player.sendMessage("§aOpening lobbies GUI...");
        return true;
    }
    
    @Override
    public String getUsage() {
        return "/pg list";
    }
}
