package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

/**
 * /pg headp1 - Set player head for 1st place on stats wall
 */
public class HeadP1Command implements ICommand {
    private final PotionGamesX plugin;

    public HeadP1Command(PotionGamesX plugin) {
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
    public boolean execute(Player player, String[] args) {
        org.bukkit.block.Block target = player.getTargetBlockExact(5);
        if (target == null) {
            player.sendMessage(Messages.HeadLookBlock1Text());
            return false;
        }
        plugin.getConfig().set("pg.RankWall.headp1", target.getLocation());
        plugin.saveConfig();
        player.sendMessage(Messages.HeadSet1Text());
        return true;
    }

    @Override
    public String getUsage() {
        return Messages.HelpHeadp1UsageText();
    }
}

