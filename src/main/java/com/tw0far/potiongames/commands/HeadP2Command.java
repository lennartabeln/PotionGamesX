package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.entity.Player;

/**
 * /pg headp2 - Set player head for 2nd place on stats wall
 */
public class HeadP2Command implements ICommand {
    private final PotionGames plugin;
    
    public HeadP2Command(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "headp2";
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
        if (player.getTargetBlock(null, 5) == null) {
            player.sendMessage("§cLook at a block to set the 2nd place head.");
            return false;
        }
        plugin.getConfig().set("pg.RankWall.headp2", player.getTargetBlock(null, 5).getLocation());
        plugin.saveConfig();
        player.sendMessage("§a2nd place head set.");
        return true;
    }
    
    @Override
    public String getUsage() {
        return "/pg headp2 (Look at 2nd place head)";
    }
}
