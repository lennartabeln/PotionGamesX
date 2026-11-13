package com.tw0far.potiongames.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.PlayerStats;
import com.tw0far.potiongames.util.PlayerUuidResolver;

/**
 * /pg stats [player] - Show player statistics
 *
 * <p>The database getters block, so the six lookups run on the async scheduler
 * and only the rendering happens on the player's thread. Running them inline
 * would stall the tick loop on a remote MySQL.</p>
 */
public class StatsCommand implements ICommand {
    private final PotionGamesX plugin;

    public StatsCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "stats";
    }

    @Override
    public String getPermission() {
        return "pg.stats";
    }


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof org.bukkit.entity.Player player)) {
            sender.sendMessage(Messages.PlayerOnly());
            return true;
        }
        // Without a database every getter returns 0 and would print an empty
        // stat sheet; say what is actually wrong instead.
        if (!plugin.getDatabaseManager().isConnected()) {
            player.sendMessage(Messages.DatabaseNotConnected());
            return true;
        }

        UUID target;
        if (args.length < 2) {
            target = player.getUniqueId();
        } else {
            target = PlayerUuidResolver.resolve(args[1],
                    name -> {
                        Player online = Bukkit.getPlayer(name);
                        return online == null ? null : online.getUniqueId();
                    },
                    name -> {
                        OfflinePlayer cached = Bukkit.getOfflinePlayerIfCached(name);
                        return cached == null ? null : cached.getUniqueId();
                    });
        }

        if (target == null) {
            player.sendMessage(Messages.StatsLabel());
            player.sendMessage(Messages.NoPlayerFound());
            player.sendMessage(Messages.StatsLabel());
            return true;
        }

        showStats(player, target.toString());
        return true;
    }

    /** Reads the stat sheet off-thread, renders it on the player's thread. */
    private void showStats(Player player, String uuid) {
        Bukkit.getAsyncScheduler().runNow(plugin, task -> {
            PlayerStats stats = PlayerStats.read(plugin.getDatabaseManager(), uuid);
            player.getScheduler().execute(plugin, () -> {
                player.sendMessage(Messages.StatsLabel());
                player.sendMessage(Messages.RoundsLabel(stats.rounds()));
                player.sendMessage(Messages.WinsLabel(stats.wins()));
                player.sendMessage(Messages.LossesLabel(stats.losses()));
                player.sendMessage(Messages.KillsLabel(stats.kills()));
                player.sendMessage(Messages.DeathsLabel(stats.deaths()));
                player.sendMessage(Messages.KDLabel(stats.kd()));
                player.sendMessage(Messages.StatsLabel());
            }, () -> { }, 1L);
        });
    }

    @Override
    public String getUsage() {
        return Messages.CommandHelpText("stats");
    }
}
