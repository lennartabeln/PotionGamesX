package com.tw0far.potiongames.commands;

import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.util.SetupArguments;
import org.bukkit.entity.Player;

/**
 * /pg adddeathmatch [lobbynumber] [arenaname] OR /pg adddeathmatch [arenaname]
 * Adds a deathmatch spawn point to the specified arena at the player's current location
 */
public class AddDeathmatchCommand implements ICommand {
    private final PotionGamesX plugin;

    public AddDeathmatchCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "adddeathmatch";
    }

    @Override
    public String getPermission() {
        return "pg.adddeathmatch";
    }


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof org.bukkit.entity.Player player)) {
            sender.sendMessage(Messages.PlayerOnly());
            return true;
        }
        // Long form: /pg adddeathmatch <lobbynumber> <arenaname>; with pg.singleLobby the
        // legacy short form /pg adddeathmatch <arenaname> targets lobby 1.
        SetupArguments.LobbyArguments parsed =
                SetupArguments.parse(plugin.getConfigManager().isSingleLobby(), args);
        if (parsed == null) {
            player.sendMessage(Messages.CommandAdddeathmatchUsageText());
            return false;
        }
        int lobbyId = parsed.lobbyId();
        String arenaName = args[parsed.valueIndex()];
        if (plugin.getGame().getLobby(lobbyId) == null) {
            player.sendMessage(Messages.LobbyDoesNotExist());
            return false;
        }
        plugin.getSetupHandler().addDeathmatchSpawn(player, arenaName, lobbyId);
        return true;
    }

    @Override
    public String getUsage() {
        return Messages.CommandAdddeathmatchUsageText();
    }
}

