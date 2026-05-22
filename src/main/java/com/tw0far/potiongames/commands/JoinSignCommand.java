package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.entity.Player;

/**
 * /pg joinsign [lobbynumber] OR /pg joinsign
 * Creates a join sign at the player's current location
 */
public class JoinSignCommand implements ICommand {
    private final PotionGames plugin;
    
    public JoinSignCommand(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "joinsign";
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
        if (args.length < 2) {
            player.sendMessage("§cUsage: /pg joinsign <lobbynumber>");
            return false;
        }
        try {
            int lobbyId = Integer.parseInt(args[1]);
            plugin.setupHandler.setJoinSign(player, lobbyId);
            return true;
        } catch (NumberFormatException ex) {
            player.sendMessage("§cUsage: /pg joinsign <lobbynumber>");
            return false;
        }
    }
    
    @Override
    public String getUsage() {
        return "/pg joinsign <lobbynumber>";
    }
}
