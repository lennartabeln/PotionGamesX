package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Messages;
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
        return false;
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        org.bukkit.block.Block target = player.getTargetBlockExact(5);
        if (target == null) {
            player.sendMessage(Messages.raw("head.look_block_3", "Look at a block to set the 3rd place head."));
            return false;
        }
        plugin.getConfig().set("pg.RankWall.headp3", target.getLocation());
        plugin.saveConfig();
        player.sendMessage(Messages.raw("head.set_3", "3rd place head set."));
        return true;
    }
    
    @Override
    public String getUsage() {
        return Messages.raw("help.headp3_usage", "/pg headp3 (Look at 3rd place head)");
    }
}
