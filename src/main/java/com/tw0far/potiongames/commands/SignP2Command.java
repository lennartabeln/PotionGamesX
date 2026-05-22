package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.entity.Player;

/**
 * /pg signp2 - Set sign for 2nd place on stats wall
 */
public class SignP2Command implements ICommand {
    private final PotionGames plugin;
    
    public SignP2Command(PotionGames plugin) {
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
    public boolean requiresGameServer() {
        return true;
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        if (player.getTargetBlock(null, 5) == null || !(player.getTargetBlock(null, 5).getState() instanceof org.bukkit.block.Sign)) {
            player.sendMessage("§cLook at a sign to set the 2nd place sign.");
            return false;
        }
        plugin.getConfig().set("pg.RankWall.signp2", player.getTargetBlock(null, 5).getLocation());
        plugin.saveConfig();
        player.sendMessage("§a2nd place sign set.");
        return true;
    }
    
    @Override
    public String getUsage() {
        return "/pg signp2 (Look at 2nd place sign)";
    }
}
