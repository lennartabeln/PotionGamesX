package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.Settings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

/**
 * /pg join [lobby] - Join a game
 */
public class JoinCommand implements ICommand {
    private final PotionGamesX plugin;

    public JoinCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getPermission() {
        return "pg.join";
    }


    @Override
    public boolean execute(Player player, String[] args) {
        // Multi-lobby system: args should contain lobby ID
        if (args.length > 1) {
            try {
                int lobbyId = Integer.parseInt(args[1]);
                if (Settings.lobbies.contains("pg.lobbies." + lobbyId)) {
                    if (plugin.getGame().getLobbies().size() > 0) {
                        if (plugin.getGame().getPlayerLobby(player) != null || plugin.getGame().getSpectatorLobby(player) != null) {
                            player.sendMessage(Messages.JoinAlreadyInLobby());
                            return true;
                        }
                        Lobby lobby = plugin.getGame().getLobby(lobbyId);
                        if (lobby != null) {
                            lobby.join(player);
                        }
                    }
                } else {
                    player.sendMessage(Messages.LobbyDoesNotExist());
                }
            } catch (NumberFormatException e) {
                player.sendMessage(Messages.HelpUsePgHelp());
            }
        } else {
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpJoinText()).color(NamedTextColor.GRAY)));
        }

        return true;
    }

    @Override
    public String getUsage() {
        return Messages.HelpJoinUsageText();
    }
}

