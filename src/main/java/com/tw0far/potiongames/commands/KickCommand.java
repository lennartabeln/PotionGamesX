package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /pg kick <player> - Remove player from lobby
 * Permission: pg.kick (or pg.admin)
 */
public class KickCommand implements ICommand {
    private final PotionGamesX plugin;

    public KickCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String getPermission() {
        return "pg.kick";
    }


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Messages.CommandKickUsageText());
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Messages.NoPlayerFound());
            return true;
        }

        String lobbyId = plugin.getGame().getPlayerLobby(target);
        if (lobbyId == null) {
            sender.sendMessage(Messages.KickNotInLobby(target.getName()));
            return true;
        }

        // unregisterPlayer restores the pre-join state, including the location:
        // an extra teleport would throw that away and drop the player at the
        // spawn of world 0 (which may even be an arena world).
        plugin.getGame().unregisterPlayer(target);

        sender.sendMessage(Messages.KickSuccess(target.getName(), lobbyId));
        target.sendMessage(Messages.KickedNotice());
        return true;
    }

    @Override
    public String getUsage() {
        return "/pg kick <player>";
    }
}
