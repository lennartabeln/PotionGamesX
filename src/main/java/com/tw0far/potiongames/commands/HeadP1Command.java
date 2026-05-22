package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.entity.Player;

/**
 * /pg headp1 - Set player head for 1st place on stats wall
 */
public class HeadP1Command implements ICommand {
    private final PotionGames plugin;
    
    public HeadP1Command(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "headp1";
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
        org.bukkit.block.Block target = player.getTargetBlockExact(5);
        if (target == null) {
            player.sendMessage("§cLook at a block to set the 1st place head.");
            return false;
        }
        plugin.getConfig().set("pg.RankWall.headp1", target.getLocation());
        plugin.saveConfig();
        player.sendMessage("§a1st place head set.");
        return true;
    }
    
    @Override
    public String getUsage() {
        return "/pg headp1 (Look at 1st place head)";
    }
}
