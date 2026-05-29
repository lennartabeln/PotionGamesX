package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * /pg broadcast <message> - Send announcement to all players
 * Permission: pg.broadcast (or pg.admin)
 */
public class BroadcastCommand implements ICommand {
    private final PotionGames plugin;
    
    public BroadcastCommand(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "broadcast";
    }
    
    @Override
    public String getPermission() {
        return "pg.broadcast";
    }
    
    @Override
    public boolean requiresGameServer() {
        return false;
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("Usage: /pg broadcast <message>").color(NamedTextColor.RED));
            return true;
        }
        
        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(args[i]);
            if (i < args.length - 1) message.append(" ");
        }
        
        Component broadcast = Component.text("").color(NamedTextColor.GOLD)
            .append(Component.text("[PotionGames] ").color(NamedTextColor.GOLD))
            .append(Component.text(message.toString()).color(NamedTextColor.YELLOW));
        
        Bukkit.broadcast(broadcast);
        
        Component confirmMsg = Component.text("Broadcast sent to all players").color(NamedTextColor.GREEN);
        player.sendMessage(confirmMsg);
        
        return true;
    }
    
    @Override
    public String getUsage() {
        return "/pg broadcast <message>";
    }
}
