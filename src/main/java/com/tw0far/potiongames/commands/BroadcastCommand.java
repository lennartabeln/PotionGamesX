package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.Settings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * /pg broadcast [-l &lt;lobby&gt;] &lt;message&gt; - announcement to every PotionGamesX
 * player, or only to the players of one lobby.
 * Permission: pg.broadcast (or pg.admin)
 *
 * <p>Only players the plugin knows about receive it: participants and spectators
 * of the configured lobbies. A server-wide {@code Bukkit.broadcast} used to reach
 * everyone online, including players who are not playing at all.</p>
 */
public class BroadcastCommand implements ICommand {
    private static final String LOBBY_FLAG = "-l";

    private final PotionGamesX plugin;

    public BroadcastCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "broadcast";
    }

    @Override
    public String getPermission() {
        return "pg.broadcast";
    }


    @Override
    public boolean execute(CommandSender player, String[] args) {
        String lobbyId = null;
        int firstWord = 1;
        if (args.length >= 3 && LOBBY_FLAG.equalsIgnoreCase(args[1])) {
            lobbyId = args[2];
            firstWord = 3;
        }

        if (args.length <= firstWord) {
            player.sendMessage(Messages.CommandHelp("broadcast"));
            return true;
        }

        if (lobbyId != null) {
            Lobby lobby = lobbyById(lobbyId);
            if (lobby == null) {
                player.sendMessage(Settings.prefix.append(
                    Component.text(Messages.InvalidLobbyIdText()).color(NamedTextColor.RED)));
                return true;
            }
            lobbyId = String.valueOf(lobby.getId());
        }

        String text = String.join(" ", Arrays.copyOfRange(args, firstWord, args.length));
        Component announcement = Messages.Broadcast(text);

        Set<Player> recipients = new LinkedHashSet<>();
        if (lobbyId != null) {
            collect(recipients, lobbyId);
        } else {
            for (Lobby lobby : plugin.getGame().getLobbies()) {
                collect(recipients, String.valueOf(lobby.getId()));
            }
        }

        for (Player recipient : recipients) {
            recipient.sendMessage(announcement);
        }

        String confirmation = lobbyId == null
            ? String.format(Messages.BroadcastSentText(), recipients.size())
            : String.format(Messages.BroadcastSentLobbyText(), lobbyId);
        player.sendMessage(Settings.prefix.append(Component.text(confirmation).color(NamedTextColor.GREEN)));
        return true;
    }

    /** Participants and spectators of one lobby. */
    private void collect(Set<Player> recipients, String lobbyId) {
        recipients.addAll(plugin.getGame().getPlayersInLobby(lobbyId));
        recipients.addAll(plugin.getGame().getSpectatorsInLobby(lobbyId));
    }

    private Lobby lobbyById(String raw) {
        try {
            return plugin.getGame().getLobby(Integer.parseInt(raw));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getUsage() {
        return "/pg broadcast [-l <lobby>] <message>";
    }
}
