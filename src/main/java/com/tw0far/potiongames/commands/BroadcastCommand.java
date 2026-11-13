package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.Settings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * /pg broadcast <message> - Send announcement to all players
 * Permission: pg.broadcast (or pg.admin)
 *
 * <p>The announcement uses the same {@link Settings#prefix} as every other
 * message, so it renders in the plugin colours instead of a hardcoded one.</p>
 */
public class BroadcastCommand implements ICommand {
    public BroadcastCommand(PotionGamesX plugin) {
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
    public boolean execute(CommandSender player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Settings.prefix.append(
                Component.text(Messages.BroadcastUsageText()).color(NamedTextColor.RED)));
            return true;
        }

        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(args[i]);
            if (i < args.length - 1) message.append(" ");
        }

        Bukkit.broadcast(Messages.Broadcast(message.toString()));

        player.sendMessage(Settings.prefix.append(
            Component.text(Messages.BroadcastSentText()).color(NamedTextColor.GREEN)));

        return true;
    }

    @Override
    public String getUsage() {
        return "/pg broadcast <message>";
    }
}
