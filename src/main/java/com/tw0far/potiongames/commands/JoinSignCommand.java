package com.tw0far.potiongames.commands;

import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.util.SetupArguments;
import org.bukkit.entity.Player;

/**
 * /pg sign [lobbynumber] OR /pg sign
 * Creates a join sign at the player's current location
 */
public class JoinSignCommand implements ICommand {
    private final PotionGamesX plugin;

    public JoinSignCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "sign";
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
        // /pg sign <lobbynumber>; with pg.singleLobby /pg sign targets lobby 1
        Integer lobbyId = SetupArguments.parseLobbyOnly(plugin.getConfigManager().isSingleLobby(), args);
        if (lobbyId == null) {
            player.sendMessage(Messages.CommandHelp("sign"));
            return false;
        }
        plugin.getSetupHandler().setJoinSign(player, lobbyId);
        return true;
    }

    @Override
    public String getUsage() {
        return Messages.CommandHelpText("sign");
    }
}

