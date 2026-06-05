package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.Settings;
import org.bukkit.entity.Player;

/**
 * /pg join [lobby] - Join a game
 */
public class JoinCommand implements ICommand {
    private final PotionGames plugin;
    
    public JoinCommand(PotionGames plugin) {
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
    public boolean requiresGameServer() {
        return true;
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        // Multi-lobby system: args should contain lobby ID
        if (args.length > 1) {
            try {
                int lobbyId = Integer.parseInt(args[1]);
                if (Settings.lobbies.contains("pg.lobbies." + lobbyId)) {
                    if (plugin.getGame().getLobbies().size() > 0) {
                        Lobby lobby = plugin.getGame().getLobby(lobbyId);
                        lobby.join(player);
                    }
                } else {
                    player.sendMessage(Messages.LobbyDoesNotExist());
                }
            } catch (NumberFormatException e) {
                player.sendMessage(Messages.HelpUsePgHelp());
            }
        } else {
            player.sendMessage(Settings.prefix.append(net.kyori.adventure.text.Component.text(Messages.raw("help.join", "/pg join # - Join a game")).color(net.kyori.adventure.text.format.NamedTextColor.GRAY)));
        }
        
        return true;
    }
    
    @Override
    public String getUsage() {
        return Messages.raw("help.join_usage", "/pg join <lobby_number> - Join a specific lobby");
    }
}
