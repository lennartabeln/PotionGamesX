package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /pg gameserver - Toggle gameserver mode
 */
public class GameServerCommand implements ICommand {
    private final PotionGamesX plugin;

    public GameServerCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "gameserver";
    }

    @Override
    public String getPermission() {
        return "pg.gameserver";
    }


    @Override
    public boolean execute(CommandSender player, String[] args) {
        // Get current state
        boolean currentState = plugin.getConfigManager().isGameServer();
        boolean newState = !currentState;

        // Update config manager
        plugin.getConfigManager().setGameServer(newState);

        // Also update the plugin's config file so it persists on reload
        plugin.getConfig().set("pg.gameServer", newState);
        plugin.saveConfig();

        // Send message to player
        String statusText = newState ? Messages.GameServerEnabledText() : Messages.GameServerDisabledText();
        String pattern = Messages.GameServerStateText();
        int slot = pattern.indexOf("%s");
        Component message = slot < 0
            ? Component.text(pattern).color(NamedTextColor.AQUA)
            : Component.text(pattern.substring(0, slot)).color(NamedTextColor.AQUA)
                .append(Component.text(statusText).color(newState ? NamedTextColor.GREEN : NamedTextColor.RED))
                .append(Component.text(pattern.substring(slot + 2)).color(NamedTextColor.AQUA));

        player.sendMessage(message);

        // Broadcast to all ops
        for (Player op : plugin.getServer().getOnlinePlayers()) {
            if (op == null) continue;
            if (op.isOp() && !op.equals(player)) {
                op.sendMessage(Component.text(String.format(Messages.GameServerStateOtherText(), player.getName(), statusText))
                    .color(NamedTextColor.YELLOW));
            }
        }

        plugin.getLogger().info("GameServer mode toggled to " + newState + " by " + player.getName());
        return true;
    }

    @Override
    public String getUsage() {
        return Messages.HelpGameserverUsageText();
    }
}

