package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

/**
 * /pg headp3 - Set player head for 3rd place on stats wall
 */
public class HeadP3Command implements ICommand {
    private final PotionGamesX plugin;

    public HeadP3Command(PotionGamesX plugin) {
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
    public boolean execute(Player player, String[] args) {
        org.bukkit.block.Block target = player.getTargetBlockExact(5);
        if (target == null) {
            player.sendMessage(Messages.HeadLookBlock3Text());
            return false;
        }
        plugin.getConfig().set("pg.RankWall.headp3", target.getLocation());
        plugin.saveConfig();
        player.sendMessage(Messages.HeadSet3Text());
        return true;
    }

    @Override
    public String getUsage() {
        return Messages.HelpHeadp3UsageText();
    }
}

