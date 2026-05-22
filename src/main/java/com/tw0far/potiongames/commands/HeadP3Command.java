package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.entity.Player;

/**
 * /pg headp3 - Set player head for 3rd place on stats wall
 */
public class HeadP3Command implements ICommand {
    private final PotionGames plugin;
    
    public HeadP3Command(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "headp3";
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
            player.sendMessage("§cLook at a block to set the 3rd place head.");
            return false;
        }
        plugin.getConfig().set("pg.RankWall.headp3", target.getLocation());
        plugin.saveConfig();
        player.sendMessage("§a3rd place head set.");
        return true;
    }
    
    @Override
    public String getUsage() {
        return "/pg headp3 (Look at 3rd place head)";
    }
}
