package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.util.PluginVersion;
import com.tw0far.potiongames.util.UpdateChecker;
import org.bukkit.command.CommandSender;

/**
 * /pg version - Show version info and check for updates
 */
public class VersionCommand implements ICommand {
    private final PotionGamesX plugin;

    public VersionCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "version";
    }

    @Override
    public String getPermission() {
        return "pg.version";
    }


    @Override
    public boolean execute(CommandSender player, String[] args) {
        String currentVersion = plugin.getPluginMeta().getVersion();
        player.sendMessage(Messages.VersionCurrent(currentVersion));
        new UpdateChecker(plugin).getVersion(version -> {
            if (version == null) {
                player.sendMessage(Messages.UpdateCheckerError());
                return;
            }
            if (PluginVersion.isNewer(version, currentVersion)) {
                player.sendMessage(Messages.UpdateAvailable(currentVersion, version));
            } else {
                player.sendMessage(Messages.UpdateNotAvailable());
            }
        });
        return true;
    }

    @Override
    public String getUsage() {
        return Messages.HelpVersionUsageText();
    }
}

