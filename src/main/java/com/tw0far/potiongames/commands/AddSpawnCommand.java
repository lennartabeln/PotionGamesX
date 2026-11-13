package com.tw0far.potiongames.commands;

import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.util.SetupArguments;
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
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof org.bukkit.entity.Player player)) {
            sender.sendMessage(Messages.PlayerOnly());
            return true;
        }
        // Long form: /pg addspawn <lobbynumber> <arenaname>; with pg.singleLobby the
        // legacy short form /pg addspawn <arenaname> targets lobby 1.
        SetupArguments.LobbyArguments parsed =
                SetupArguments.parse(plugin.getConfigManager().isSingleLobby(), args);
        if (parsed == null) {
            player.sendMessage(Messages.CommandHelp("addspawn"));
            return false;
        }
        int lobbyId = parsed.lobbyId();
        String arenaName = args[parsed.valueIndex()];
        if (plugin.getGame().getLobby(lobbyId) == null) {
            player.sendMessage(Messages.LobbyDoesNotExist());
            return false;
        }
        plugin.getSetupHandler().addSpawn(player, arenaName, lobbyId);
        return true;
    }

    @Override
    public String getUsage() {
        return Messages.CommandHelpText("addspawn");
    }
}

