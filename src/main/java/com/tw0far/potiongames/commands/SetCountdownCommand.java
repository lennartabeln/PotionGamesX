package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.Settings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * {@code /pg setcountdown <seconds>} - change the lobby countdown, the legacy
 * way of shortening the wait without editing a file.
 *
 * <p>A player standing in a lobby changes that lobby only
 * ({@code pg.lobbies.<id>.settings.countdown}); console or a player outside a
 * lobby changes the default for every lobby that does not override it
 * ({@code pg.defaults.countdown}). Lobbies that are counting down right now are
 * left alone, otherwise the value would silently shorten a running countdown.</p>
 */
public class SetCountdownCommand implements ICommand {

    /** Longest countdown the command accepts, in seconds. */
    private static final int MAX_SECONDS = 600;

    private final PotionGamesX plugin;

    public SetCountdownCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "setcountdown";
    }

    @Override
    public String getPermission() {
        return "pg.setup";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Messages.CommandHelp("setcountdown"));
            return false;
        }
        int seconds;
        try {
            seconds = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Messages.CommandHelp("setcountdown"));
            return false;
        }
        if (seconds < 1 || seconds > MAX_SECONDS) {
            sender.sendMessage(Messages.CountdownInvalid(MAX_SECONDS));
            return false;
        }

        String lobbyId = sender instanceof Player player ? plugin.getGame().getPlayerLobby(player) : null;
        if (lobbyId == null) {
            applyAsDefault(sender, seconds);
            return true;
        }

        Lobby lobby;
        try {
            lobby = plugin.getGame().getLobby(Integer.parseInt(lobbyId));
        } catch (NumberFormatException e) {
            lobby = null;
        }
        if (lobby == null) {
            applyAsDefault(sender, seconds);
            return true;
        }

        Settings.lobbies.set("pg.lobbies." + lobbyId + ".settings.countdown", seconds);
        saveLobbies();
        lobby.setCountdown(seconds);
        sender.sendMessage(Messages.CountdownSet(seconds));
        return true;
    }

    /** Writes {@code pg.defaults.countdown} and updates waiting lobbies. */
    private void applyAsDefault(CommandSender sender, int seconds) {
        plugin.getConfig().set("pg.defaults.countdown", seconds);
        plugin.saveConfig();
        plugin.getConfigManager().reload();
        // A running countdown keeps its length; the new default applies from the
        // next WAITING phase on.
        for (Lobby lobby : plugin.getGame().getLobbies()) {
            if (lobby.getState() == com.tw0far.potiongames.models.GameStates.WAITING) {
                lobby.setCountdown(seconds);
            }
        }
        sender.sendMessage(Messages.CountdownSetDefault(seconds));
    }

    private void saveLobbies() {
        try {
            Settings.lobbies.save(Settings.lobbiesFile);
        } catch (Exception e) {
            plugin.getLogger().warning("Could not save lobbies.yml: " + e.getMessage());
        }
    }

    @Override
    public String getUsage() {
        return Messages.CommandHelpText("setcountdown");
    }
}
