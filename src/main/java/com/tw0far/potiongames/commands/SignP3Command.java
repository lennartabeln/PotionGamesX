package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

/**
 * /pg signp3 - Set sign for 3rd place on stats wall
 */
public class SignP3Command implements ICommand {
    private final PotionGamesX plugin;

    public SignP3Command(PotionGamesX plugin) {
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
    public boolean execute(Player player, String[] args) {
        org.bukkit.block.Block target = player.getTargetBlockExact(5);
        if (target == null || !(target.getState() instanceof org.bukkit.block.Sign)) {
            player.sendMessage(Messages.SignLookSign3Text());
            return false;
        }
        plugin.getConfig().set("pg.RankWall.signp3", target.getLocation());
        plugin.saveConfig();
        player.sendMessage(Messages.SignSet3Text());
        return true;
    }

    @Override
    public String getUsage() {
        return Messages.HelpSignp3UsageText();
    }
}

