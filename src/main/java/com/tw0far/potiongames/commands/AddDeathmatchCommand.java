package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

/**
 * /pg adddeathmatch [lobbynumber] [arenaname] OR /pg adddeathmatch [arenaname]
 * Adds a deathmatch spawn point to the specified arena at the player's current location
 */
public class AddDeathmatchCommand implements ICommand {
    private final PotionGames plugin;
    
    public AddDeathmatchCommand(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "adddeathmatch";
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
        if (args.length < 3) {
            player.sendMessage(Messages.raw("command.adddeathmatch.usage", "Usage: /pg adddeathmatch <lobbynumber> <arenaname>"));
            return false;
        }
        try {
            int lobbyId = Integer.parseInt(args[1]);
            String arenaName = args[2];
            plugin.getSetupHandler().addDeathmatchSpawn(player, arenaName, lobbyId);
            return true;
        } catch (NumberFormatException ex) {
            player.sendMessage(Messages.raw("command.adddeathmatch.usage", "Usage: /pg adddeathmatch <lobbynumber> <arenaname>"));
            return false;
        }
    }
    
    @Override
    public String getUsage() {
        return Messages.raw("command.adddeathmatch.usage", "/pg adddeathmatch <lobbynumber> <arenaname>");
    }
}
