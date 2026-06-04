package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Messages;
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
        return false;
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        org.bukkit.block.Block target = player.getTargetBlockExact(5);
        if (target == null) {
            player.sendMessage(Messages.raw("head.look_block_2", "Look at a block to set the 2nd place head."));
            return false;
        }
        plugin.getConfig().set("pg.RankWall.headp2", target.getLocation());
        plugin.saveConfig();
        player.sendMessage(Messages.raw("head.set_2", "2nd place head set."));
        return true;
    }
    
    @Override
    public String getUsage() {
        return Messages.raw("help.headp2_usage", "/pg headp2 (Look at 2nd place head)");
    }
}
