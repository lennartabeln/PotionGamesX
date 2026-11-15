package com.tw0far.potiongames.commands;

import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.util.BungeeConnector;

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
        Lobby lobby = plugin.getGame().getLobbyByPlayer(player);
        if (lobby != null) {
            lobby.leave(player);
            player.sendMessage(Messages.LeaveSuccess());
        } else if (!plugin.getConfigManager().isGameServer()) {
            // Nothing to leave: say so instead of failing silently.
            player.sendMessage(Messages.NotInLobby());
            return true;
        }

        if (plugin.getConfigManager().isGameServer()) {
            String hub = plugin.getConfig().getString("pg.bungeeServer", "hub");
            // Both paths end on the hub: through the proxy when it is reachable, otherwise
            // the player is disconnected instead of silently staying on the game server.
            BungeeConnector.send(plugin, player, hub);
        }

        return true;
    }

    @Override
    public String getUsage() {
        return Messages.CommandHelpText("leave");
    }
}

