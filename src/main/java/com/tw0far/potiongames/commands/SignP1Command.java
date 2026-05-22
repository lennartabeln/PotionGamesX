package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.entity.Player;

/**
 * /pg signp1 - Set sign for 1st place on stats wall
 */
public class SignP1Command implements ICommand {
    private final PotionGames plugin;
    
    public SignP1Command(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "signp1";
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
        if (player.getTargetBlock(null, 5) == null || !(player.getTargetBlock(null, 5).getState() instanceof org.bukkit.block.Sign)) {
            player.sendMessage("§cLook at a sign to set the 1st place sign.");
            return false;
        }
        plugin.getConfig().set("pg.RankWall.signp1", player.getTargetBlock(null, 5).getLocation());
        plugin.saveConfig();
        player.sendMessage("§a1st place sign set.");
        return true;
    }
    
    @Override
    public String getUsage() {
        return "/pg signp1 (Look at 1st place sign)";
    }
}
