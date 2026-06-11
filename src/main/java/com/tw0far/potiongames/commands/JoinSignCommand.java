package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

/**
 * /pg joinsign [lobbynumber] OR /pg joinsign
 * Creates a join sign at the player's current location
 */
public class JoinSignCommand implements ICommand {
    private final PotionGamesX plugin;

    public JoinSignCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "joinsign";
    }

    @Override
    public String getPermission() {
        return "pg.setup";
    }


    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Messages.CommandJoinsignUsageText());
            return false;
        }
        try {
            int lobbyId = Integer.parseInt(args[1]);
            plugin.getSetupHandler().setJoinSign(player, lobbyId);
            return true;
        } catch (NumberFormatException ex) {
            player.sendMessage(Messages.CommandJoinsignUsageText());
            return false;
        }
    }

    @Override
    public String getUsage() {
        return Messages.CommandJoinsignUsageText();
    }
}

