package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.entity.Player;

/**
 * /pg signp2 - Set sign for 2nd place on stats wall
 */
public class SignP2Command implements ICommand {
    private final PotionGames plugin;
    
    public SignP2Command(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "signp2";
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
        // Look at the sign of the 2nd player on the podium and use this command
        // Set sign logic would go here
        player.sendMessage("§a2nd place sign set.");
        return true;
    }
    
    @Override
    public String getUsage() {
        return "/pg signp2 (Look at 2nd place sign)";
    }
}
