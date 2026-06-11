package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

/**
 * /pg delspawn [lobbynumber] [arenaname] OR /pg delspawn [arenaname]
 * Removes the last added spawn point from the specified arena
 */
public class DelSpawnCommand implements ICommand {
    private final PotionGamesX plugin;

    public DelSpawnCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "delspawn";
    }

    @Override
    public String getPermission() {
        return "pg.setup";
    }


    @Override
    public boolean execute(Player player, String[] args) {
        // Multi-lobby system: /pg delspawn <lobbynumber> <arenaname>
        if (args.length < 3) {
            player.sendMessage(Messages.CommandDelspawnUsageText());
            return false;
        }

        try {
            int lobbyId = Integer.parseInt(args[1]);
            String arenaName = args[2];
            Lobby lobby = plugin.getGame().getLobby(lobbyId);
            if (lobby == null) {
                player.sendMessage(Messages.LobbyDoesNotExist());
                return false;
            }
            plugin.getSetupHandler().removeSpawn(player, arenaName, lobbyId);
            return true;
        } catch (NumberFormatException ex) {
            player.sendMessage(Messages.CommandDelspawnUsageText());
            return false;
        }
    }

    @Override
    public String getUsage() {
        return Messages.CommandDelspawnUsageText();
    }
}

