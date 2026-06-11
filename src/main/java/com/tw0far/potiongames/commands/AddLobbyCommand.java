package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

/**
 * /pg addlobby [lobbynumber] - Add a lobby (Multi-Lobby only)
 */
public class AddLobbyCommand implements ICommand {
    private final PotionGamesX plugin;

    public AddLobbyCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "addlobby";
    }

    @Override
    public String getPermission() {
        return "pg.setup";
    }


    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Messages.CommandAddlobbyUsageText());
            return false;
        }
        try {
            int lobbyId = Integer.parseInt(args[1]);
            plugin.getSetupHandler().addLobby(player, lobbyId);
            return true;
        } catch (NumberFormatException ex) {
            player.sendMessage(Messages.CommandAddlobbyUsageText());
            return false;
        }
    }

    @Override
    public String getUsage() {
        return Messages.CommandAddlobbyUsageText();
    }
}

