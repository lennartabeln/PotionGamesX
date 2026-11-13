package com.tw0far.potiongames.commands;

import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.util.SetupArguments;
import org.bukkit.entity.Player;

/**
 * /pg delarena [lobbynumber] [arenaname] OR /pg delarena [arenaname]
 * Removes an arena from the specified lobby or the default lobby
 */
public class DelArenaCommand implements ICommand {
    private final PotionGamesX plugin;

    public DelArenaCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "delarena";
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
        // Long form: /pg delarena <lobbynumber> <arenaname>; with pg.singleLobby the
        // legacy short form /pg delarena <arenaname> targets lobby 1.
        SetupArguments.LobbyArguments parsed =
                SetupArguments.parse(plugin.getConfigManager().isSingleLobby(), args);
        if (parsed == null) {
            player.sendMessage(Messages.CommandHelp("delarena"));
            return false;
        }
        int lobbyId = parsed.lobbyId();
        String arenaName = args[parsed.valueIndex()];
        if (plugin.getGame().getLobby(lobbyId) == null) {
            player.sendMessage(Messages.LobbyDoesNotExist());
            return false;
        }
        plugin.getSetupHandler().removeArena(player, arenaName, lobbyId);
        return true;
    }

    @Override
    public String getUsage() {
        return Messages.CommandHelpText("delarena");
    }
}

