package com.tw0far.potiongames.commands;

import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;


/**
 * /pg leave - Leave current game
 */
public class LeaveCommand implements ICommand {
    private final PotionGamesX plugin;

    public LeaveCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getPermission() {
        return "pg.leave";
    }


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof org.bukkit.entity.Player player)) {
            sender.sendMessage(Messages.PlayerOnly());
            return true;
        }
        plugin.leaveLobby(player);
        return true;
    }

    @Override
    public String getUsage() {
        return Messages.CommandHelpText("leave");
    }
}

