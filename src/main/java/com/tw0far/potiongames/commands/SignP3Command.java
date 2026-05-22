package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.entity.Player;

/**
 * /pg signp3 - Set sign for 3rd place on stats wall
 */
public class SignP3Command implements ICommand {
    private final PotionGames plugin;
    
    public SignP3Command(PotionGames plugin) {
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
    public boolean requiresGameServer() {
        return true;
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        org.bukkit.block.Block target = player.getTargetBlockExact(5);
        if (target == null || !(target.getState() instanceof org.bukkit.block.Sign)) {
            player.sendMessage("§cLook at a sign to set the 3rd place sign.");
            return false;
        }
        plugin.getConfig().set("pg.RankWall.signp3", target.getLocation());
        plugin.saveConfig();
        player.sendMessage("§a3rd place sign set.");
        return true;
    }
    
    @Override
    public String getUsage() {
        return "/pg signp3 (Look at 3rd place sign)";
    }
}
