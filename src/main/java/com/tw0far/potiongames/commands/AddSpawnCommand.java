package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

/**
 * /pg addspawn [lobbynumber] [arenaname] OR /pg addspawn [arenaname]
 * Adds a spawn point to the specified arena at the player's current location
 */
public class AddSpawnCommand implements ICommand {
    private final PotionGamesX plugin;

    public AddSpawnCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "addspawn";
    }

    @Override
    public String getPermission() {
        return "pg.setup";
    }


    @Override
    public boolean execute(Player player, String[] args) {
        // Multi-lobby system: /pg addspawn <lobbynumber> <arenaname>
        if (args.length < 3) {
            player.sendMessage(Messages.CommandAddspawnUsageText());
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
            plugin.getSetupHandler().addSpawn(player, arenaName, lobbyId);
            return true;
        } catch (NumberFormatException ex) {
            player.sendMessage(Messages.CommandAddspawnUsageText());
            return false;
        }
    }

    @Override
    public String getUsage() {
        return Messages.CommandAddspawnUsageText();
    }
}

