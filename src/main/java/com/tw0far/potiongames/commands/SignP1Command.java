package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

/**
 * /pg signp1 - Set sign for 1st place on stats wall
 */
public class SignP1Command implements ICommand {
    private final PotionGamesX plugin;
    
    public SignP1Command(PotionGamesX plugin) {
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
    public boolean execute(Player player, String[] args) {
        org.bukkit.block.Block target = player.getTargetBlockExact(5);
        if (target == null || !(target.getState() instanceof org.bukkit.block.Sign)) {
            player.sendMessage(Messages.raw("sign.look_sign_1", "Look at a sign to set the 1st place sign."));
            return false;
        }
        plugin.getConfig().set("pg.RankWall.signp1", target.getLocation());
        plugin.saveConfig();
        player.sendMessage(Messages.raw("sign.set_1", "1st place sign set."));
        return true;
    }
    
    @Override
    public String getUsage() {
        return Messages.raw("help.signp1_usage", "/pg signp1 (Look at 1st place sign)");
    }
}
