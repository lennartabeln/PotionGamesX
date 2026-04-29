package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.entity.Player;

/**
 * /pg signp3 - Set sign for 3rd place on stats wall
 */
public class SignP3Command implements ICommand {
    private final PotionGames plugin;
    
    public SignP3Command(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "signp3";
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
        // Look at the sign of the 3rd player on the podium and use this command
        // Set sign logic would go here
        player.sendMessage("§a3rd place sign set.");
        return true;
    }
    
    @Override
    public String getUsage() {
        return "/pg signp3 (Look at 3rd place sign)";
    }
}
