package com.tw0far.potiongames.commands;

import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

/**
 * /pg setup - Start setup mode
 */
public class SetupCommand implements ICommand {
    private final PotionGamesX plugin;

    public SetupCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "setup";
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
        try {
            if (plugin.getSetupStateManager().isSetupPlayer(player)) {
                plugin.getSetupHandler().exitSetup(player);
                player.sendMessage(Messages.line(Messages.SetupDisabledText(), NamedTextColor.RED));
            } else {
                plugin.getSetupHandler().setup(player);
                player.sendMessage(Messages.line(Messages.SetupEnabledText(), NamedTextColor.GREEN));
            }
        } catch (Exception ex) {
            player.sendMessage(Messages.ErrorGeneric());
        }
        return true;
    }

    @Override
    public String getUsage() {
        return Messages.CommandHelpText("setup");
    }
}

