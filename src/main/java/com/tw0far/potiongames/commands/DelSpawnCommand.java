package com.tw0far.potiongames.commands;

import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.util.SetupArguments;
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
        return "pg.delspawn";
    }


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof org.bukkit.entity.Player player)) {
            sender.sendMessage(Messages.PlayerOnly());
            return true;
        }
        // Long form: /pg delspawn <lobbynumber> <arenaname>; with pg.singleLobby the
        // legacy short form /pg delspawn <arenaname> targets lobby 1.
        SetupArguments.LobbyArguments parsed =
                SetupArguments.parse(plugin.getConfigManager().isSingleLobby(), args);
        if (parsed == null) {
            player.sendMessage(Messages.CommandDelspawnUsageText());
            return false;
        }
        int lobbyId = parsed.lobbyId();
        String arenaName = args[parsed.valueIndex()];
        if (plugin.getGame().getLobby(lobbyId) == null) {
            player.sendMessage(Messages.LobbyDoesNotExist());
            return false;
        }
        plugin.getSetupHandler().removeSpawn(player, arenaName, lobbyId);
        return true;
    }

    @Override
    public String getUsage() {
        return Messages.CommandDelspawnUsageText();
    }
}

