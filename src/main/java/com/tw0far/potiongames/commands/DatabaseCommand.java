package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /pg database - Toggle database mode (MySQL vs SQLite)
 */
public class DatabaseCommand implements ICommand {
    private final PotionGamesX plugin;

    public DatabaseCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "database";
    }

    @Override
    public String getPermission() {
        return "pg.database";
    }


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        boolean newState = !plugin.getConfigManager().isActivateMysql();

        // Update config manager
        plugin.getConfigManager().setActivateMysql(newState);

        // Also update the plugin's config file so it persists on reload
        plugin.getConfig().set("pg.activateMySQL", newState);
        plugin.saveConfig();

        // Apply immediately: reconnect with the new database mode
        try {
            plugin.getDatabaseManager().closeConnection();
            plugin.getDatabaseManager().connect();
        } catch (Exception ex) {
            plugin.getLogger().warning("Database reconnect failed: " + ex.getMessage());
        }
        boolean connected = plugin.getDatabaseManager().isConnected();

        String dbType = newState ? "MySQL" : "SQLite";
        sender.sendMessage(Messages.DatabaseModeChanged(dbType, connected));

        // Tell the players that are allowed to run this command as well
        for (Player other : plugin.getServer().getOnlinePlayers()) {
            if (other == null || other.equals(sender)) {
                continue;
            }
            if (other.hasPermission(getPermission())) {
                other.sendMessage(Messages.DatabaseModeBroadcast(sender.getName(), dbType));
            }
        }

        plugin.getLogger().info("Database mode switched to " + dbType + " by " + sender.getName());
        return true;
    }

    @Override
    public String getUsage() {
        return Messages.CommandHelpText("database");
    }
}
