package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

/**
 * /pg status - Show active lobbies, player counts, and database connection status
 */
public class StatusCommand implements ICommand {
    private final PotionGamesX plugin;

    public StatusCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "status";
    }

    @Override
    public String getPermission() {
        return "pg.status";
    }


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        var game = plugin.getGame();
        var lobbies = game.getLobbies();
        int active = game.getActivePlayers().size();
        int spectators = game.getSpectatorPlayers().size();

        sender.sendMessage(divider());
        sender.sendMessage(Component.text(Messages.StatusTitleText()).color(NamedTextColor.GOLD));

        sender.sendMessage(section(Messages.StatusServerText()));
        sender.sendMessage(line(Messages.StatusPlayersActiveText(), Integer.toString(active)));
        sender.sendMessage(line(Messages.StatusSpectatorsText(), Integer.toString(spectators)));
        sender.sendMessage(line(Messages.StatusTotalPlayersText(), Integer.toString(active + spectators)));

        sender.sendMessage(section(Messages.StatusLobbiesText()));
        if (lobbies.isEmpty()) {
            sender.sendMessage(Component.text("  " + Messages.StatusNoLobbiesText()).color(NamedTextColor.GRAY));
        } else {
            for (Lobby lobby : lobbies) {
                String state = lobby.getState().name();
                Component lobbyInfo = Component.text("  " + Messages.StatusLobbyLine(Integer.toString(lobby.getId()),
                        lobby.getActivePlayers().size(), lobby.getCapacity(),
                        lobby.getSpectatorPlayers().size()))
                    .color(NamedTextColor.GRAY)
                    .append(Component.text(" (" + state + ")").color(getStateColor(state)));
                sender.sendMessage(lobbyInfo);
            }
        }

        sender.sendMessage(section(Messages.StatusDatabaseText()));
        boolean mysqlActive = plugin.getConfigManager().isActivateMysql();
        boolean connected = plugin.getDatabaseManager().isConnected();
        sender.sendMessage(line(Messages.StatusModeText(), mysqlActive ? "MySQL" : "SQLite"));
        sender.sendMessage(Component.text("  " + Messages.StatusConnectionText()).color(NamedTextColor.GRAY)
            .append(Component.text(": ").color(NamedTextColor.DARK_GRAY))
            .append(Component.text(connected ? Messages.StatusConnectedText() : Messages.StatusDisconnectedText())
                .color(connected ? NamedTextColor.GREEN : NamedTextColor.RED)));

        sender.sendMessage(divider());
        return true;
    }

    private Component divider() {
        return Component.text("════════════════════════════════════════").color(NamedTextColor.DARK_GRAY);
    }

    private Component section(String label) {
        return Component.text("").color(NamedTextColor.DARK_GRAY)
            .append(Component.text(label + ":").color(NamedTextColor.GOLD));
    }

    private Component line(String label, String value) {
        return Component.text("  " + label).color(NamedTextColor.GRAY)
            .append(Component.text(": ").color(NamedTextColor.DARK_GRAY))
            .append(Component.text(value).color(NamedTextColor.GREEN));
    }

    private NamedTextColor getStateColor(String state) {
        return switch (state) {
            case "WAITING" -> NamedTextColor.BLUE;
            case "PREPARING" -> NamedTextColor.AQUA;
            case "INGAME" -> NamedTextColor.GREEN;
            case "DEATHMATCH" -> NamedTextColor.RED;
            case "ENDING" -> NamedTextColor.YELLOW;
            case "RESET" -> NamedTextColor.GRAY;
            default -> NamedTextColor.WHITE;
        };
    }

    @Override
    public String getUsage() {
        return "/pg status";
    }
}
