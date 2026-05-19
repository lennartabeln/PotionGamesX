package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.Settings;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * /pg help - Show help message
 */
public class HelpCommand implements ICommand {
    private final PotionGames plugin;
    
    public HelpCommand(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "help";
    }
    
    @Override
    public String getPermission() {
        return null; // Everyone can use
    }
    
    @Override
    public boolean requiresGameServer() {
        return false;
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        player.sendMessage(Messages.CommandsLabel());
        
        if (player.hasPermission("pg.setup")) {
            player.sendMessage(Settings.prefix.append(Component.text("/pg setup - Set up plugin").color(NamedTextColor.GRAY)));
            player.sendMessage(Settings.prefix.append(Component.text("/pg addlobby [lobbynumber] - Add a lobby").color(NamedTextColor.GRAY)));
            player.sendMessage(Settings.prefix.append(Component.text("/pg removelobby [lobbynumber] - Remove a lobby").color(NamedTextColor.GRAY)));
            player.sendMessage(Settings.prefix.append(Component.text("/pg addarena [lobbynumber] [arenaname] - Add an arena").color(NamedTextColor.GRAY)));
            player.sendMessage(Settings.prefix.append(Component.text("/pg addspawn [lobbynumber] [arenaname] - Add a spawn").color(NamedTextColor.GRAY)));
            player.sendMessage(Settings.prefix.append(Component.text("/pg adddeathmatch [lobbynumber] [arenaname] - Add a deathmatch spawn").color(NamedTextColor.GRAY)));
            player.sendMessage(Settings.prefix.append(Component.text("/pg removerena [lobbynumber] [arenaname] - Remove an arena").color(NamedTextColor.GRAY)));
            player.sendMessage(Settings.prefix.append(Component.text("/pg removespawn [lobbynumber] [arenaname] - Remove last spawn").color(NamedTextColor.GRAY)));
            player.sendMessage(Settings.prefix.append(Component.text("/pg removedeathmatch [lobbynumber] [arenaname] - Remove last deathmatch spawn").color(NamedTextColor.GRAY)));
            player.sendMessage(Settings.prefix.append(Component.text("/pg headp1(2;3) - Add Player Head to Stats-Wall").color(NamedTextColor.GRAY)));
            player.sendMessage(Settings.prefix.append(Component.text("/pg signp1(2;3) - Add Player Sign to Stats-Wall").color(NamedTextColor.GRAY)));
            player.sendMessage(Settings.prefix.append(Component.text("/pg joinsign [lobbynumber] - Add Join-Sign").color(NamedTextColor.GRAY)));
            player.sendMessage(Settings.prefix.append(Component.text("/pg reload - Reload all configs").color(NamedTextColor.GRAY)));
        }
        
        if (player.hasPermission("pg.build")) {
            player.sendMessage(Settings.prefix.append(Component.text("/pg build - Activate build mode").color(NamedTextColor.GRAY)));
        }
        
        if (player.hasPermission("pg.pause")) {
            player.sendMessage(Settings.prefix.append(Component.text("/pg pause - Pause timer/countdown").color(NamedTextColor.GRAY)));
        }
        
        if (player.hasPermission("pg.force")) {
            player.sendMessage(Settings.prefix.append(Component.text("/pg force [arenaname] - Force an arena").color(NamedTextColor.GRAY)));
        }
        
        if (player.hasPermission("pg.start")) {
            player.sendMessage(Settings.prefix.append(Component.text("/pg start - Set lobby countdown to 10").color(NamedTextColor.GRAY)));
        }
        
        if (player.hasPermission("pg.join")) {
            if (!plugin.isStartOnJoin()) {
                player.sendMessage(Settings.prefix.append(Component.text("/pg join # - Join a game").color(NamedTextColor.GRAY)));
                player.sendMessage(Settings.prefix.append(Component.text("/pg list - List of all lobbies").color(NamedTextColor.GRAY)));
            }
        }
        
        if (!plugin.isStartOnJoin()) {
            player.sendMessage(Settings.prefix.append(Component.text("/pg leave - Leave the game").color(NamedTextColor.GRAY)));
        }
        
        if (player.hasPermission("pg.stats")) {
            player.sendMessage(Settings.prefix.append(Component.text("/pg stats [player] - Show player stats").color(NamedTextColor.GRAY)));
        }
        
        if (player.hasPermission("pg.update")) {
            player.sendMessage(Settings.prefix.append(Component.text("/pg version - Show your and latest version of plugin").color(NamedTextColor.GRAY)));
        }
        
        player.sendMessage(Messages.CommandsLabel());
        return true;
    }
    
    @Override
    public String getUsage() {
        return "/pg help - Show this help message";
    }
}
