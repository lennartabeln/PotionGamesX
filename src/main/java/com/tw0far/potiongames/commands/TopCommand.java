package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

/**
 * /pg top [type] - Show top players leaderboard
 * Types: kills, deaths, wins, kd (kill/death ratio)
 * Permission: pg.top
 */
public class TopCommand implements ICommand {
    private final PotionGames plugin;
    
    public TopCommand(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "top";
    }
    
    @Override
    public String getPermission() {
        return "pg.top";
    }
    
    @Override
    public boolean requiresGameServer() {
        return false;
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        String type = args.length > 1 ? args[1].toLowerCase() : "kills";
        
        player.sendMessage(Component.text("").color(NamedTextColor.DARK_GRAY)
            .append(Component.text("════════════════════════════════════════").color(NamedTextColor.DARK_GRAY)));
        
        player.sendMessage(Component.text("Top 10 Players by " + type.toUpperCase()).color(NamedTextColor.GOLD));
        
        player.sendMessage(Component.text("").color(NamedTextColor.DARK_GRAY)
            .append(Component.text("(Statistics feature coming soon - database integration needed)").color(NamedTextColor.GRAY)));
        
        player.sendMessage(Component.text("").color(NamedTextColor.DARK_GRAY)
            .append(Component.text("════════════════════════════════════════").color(NamedTextColor.DARK_GRAY)));
        
        return true;
    }
    
    @Override
    public String getUsage() {
        return "/pg top [kills|deaths|wins|kd]";
    }
}
