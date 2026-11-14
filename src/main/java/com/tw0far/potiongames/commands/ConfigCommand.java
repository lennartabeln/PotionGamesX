package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.Settings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

/**
 * /pg config - View current plugin configuration settings
 */
public class ConfigCommand implements ICommand {
    private final PotionGamesX plugin;

    public ConfigCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "config";
    }

    @Override
    public String getPermission() {
        return "pg.config";
    }


    @Override
    public boolean execute(CommandSender player, String[] args) {
        var config = plugin.getConfigManager();

        player.sendMessage(Component.text("").color(NamedTextColor.DARK_GRAY)
            .append(Component.text("════════════════════════════════════════").color(NamedTextColor.DARK_GRAY)));

        player.sendMessage(Settings.prefix.append(Component.text(Messages.ConfigHeaderText()).color(NamedTextColor.GOLD)));

        player.sendMessage(section(Messages.ConfigSectionCoreText()));

        player.sendMessage(formatConfigLine("  " + Messages.ConfigLanguageText(), config.getLanguage()));
        player.sendMessage(flagLine("  " + Messages.ConfigGameServerText(), config.isGameServer()));
        player.sendMessage(formatConfigLine("  " + Messages.ConfigDatabaseModeText(), config.isActivateMysql() ? "MySQL" : "SQLite"));

        player.sendMessage(section(Messages.ConfigSectionDefaultsText()));

        player.sendMessage(formatConfigLine("  " + Messages.ConfigCountdownText(), config.getCountdown() + "s"));
        player.sendMessage(formatConfigLine("  " + Messages.ConfigStartDelayText(), config.getStartDelay() + "s"));
        player.sendMessage(formatConfigLine("  " + Messages.ConfigRoundTimeText(), config.getRoundTime() + " min"));
        player.sendMessage(formatConfigLine("  " + Messages.ConfigMaxPlayersText(), config.getMaxPlayers() + ""));
        player.sendMessage(formatConfigLine("  " + Messages.ConfigMinPlayersText(), config.getMinPlayers() + ""));
        player.sendMessage(formatConfigLine("  " + Messages.ConfigTeamSizeText(), config.getTeamSize() + ""));

        player.sendMessage(section(Messages.ConfigSectionFeaturesText()));

        player.sendMessage(flagLine("  " + Messages.ConfigTeamsText(), config.isActivateTeams()));
        player.sendMessage(flagLine("  " + Messages.ConfigKitsText(), config.isActivateKits()));
        player.sendMessage(flagLine("  " + Messages.ConfigShopText(), config.isActivateShop()));
        player.sendMessage(flagLine("  " + Messages.ConfigAirdropsText(), config.isActivateAirdrops()));
        player.sendMessage(flagLine("  " + Messages.ConfigDeathmatchText(), config.isActivateDeathmatch()));
        player.sendMessage(flagLine("  " + Messages.ConfigScoreboardText(), config.isActivateScoreboard()));

        player.sendMessage(Component.text("").color(NamedTextColor.DARK_GRAY)
            .append(Component.text("════════════════════════════════════════").color(NamedTextColor.DARK_GRAY)));

        return true;
    }

    /** Section heading of the config screen. */
    private Component section(String title) {
        return Component.text("").color(NamedTextColor.DARK_GRAY)
            .append(Component.text(title + ":").color(NamedTextColor.GOLD));
    }

    private Component formatConfigLine(String key, String value) {
        return Component.text(key).color(NamedTextColor.GRAY)
            .append(Component.text(": ").color(NamedTextColor.DARK_GRAY))
            .append(Component.text(value).color(NamedTextColor.GREEN));
    }

    /**
     * Boolean setting as {@code true}/{@code false} - the same spelling the
     * config file uses, instead of mixing ONLINE/OFFLINE and ticks.
     */
    private Component flagLine(String key, boolean value) {
        return Component.text(key).color(NamedTextColor.GRAY)
            .append(Component.text(": ").color(NamedTextColor.DARK_GRAY))
            .append(Component.text(Boolean.toString(value))
                .color(value ? NamedTextColor.GREEN : NamedTextColor.RED));
    }

    @Override
    public String getUsage() {
        return "/pg config";
    }
}
