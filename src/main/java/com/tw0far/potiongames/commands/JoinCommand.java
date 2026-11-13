package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import com.tw0far.potiongames.models.Settings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /pg join [lobby] - Join a game
 */
public class JoinCommand implements ICommand {
    private final PotionGamesX plugin;

    public JoinCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getPermission() {
        return "pg.join";
    }


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Messages.PlayerOnly());
            return true;
        }
        if (args.length <= 1) {
            player.sendMessage(Messages.CommandHelp("join"));
            return true;
        }

        String lobbyId = args[1];
        try {
            Integer.parseInt(lobbyId);
        } catch (NumberFormatException e) {
            player.sendMessage(Messages.CommandHelp("join"));
            return true;
        }

        if (plugin.getGame().getPlayerLobby(player) != null || plugin.getGame().getSpectatorLobby(player) != null) {
            player.sendMessage(Messages.JoinAlreadyInLobby());
            return true;
        }

        // Share the validation (capacity, state, joinStarted, messages) with
        // the join sign and lobby selector instead of joining unconditionally.
        plugin.onJoinLobby(player, lobbyId);
        return true;
    }

    @Override
    public String getUsage() {
        return Messages.CommandHelpText("join");
    }
}