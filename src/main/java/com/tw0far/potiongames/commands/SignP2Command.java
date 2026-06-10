package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

/**
 * /pg signp2 - Set sign for 2nd place on stats wall
 */
public class SignP2Command implements ICommand {
    private final PotionGamesX plugin;
    
    public SignP2Command(PotionGamesX plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "signp2";
    }
    
    @Override
    public String getPermission() {
        return "pg.setup";
    }

    
    @Override
    public boolean execute(Player player, String[] args) {
        org.bukkit.block.Block target = player.getTargetBlockExact(5);
        if (target == null || !(target.getState() instanceof org.bukkit.block.Sign)) {
            player.sendMessage(Messages.raw("sign.look_sign_2", "Look at a sign to set the 2nd place sign."));
            return false;
        }
        plugin.getConfig().set("pg.RankWall.signp2", target.getLocation());
        plugin.saveConfig();
        player.sendMessage(Messages.raw("sign.set_2", "2nd place sign set."));
        return true;
    }
    
    @Override
    public String getUsage() {
        return Messages.raw("help.signp2_usage", "/pg signp2 (Look at 2nd place sign)");
    }
}
