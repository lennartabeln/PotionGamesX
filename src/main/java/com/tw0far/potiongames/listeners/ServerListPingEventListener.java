package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.models.Messages;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Arena;
import com.tw0far.potiongames.models.GameStates;
import com.tw0far.potiongames.models.Lobby;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Reflects the plugin state in the server list entry.
 *
 * <p>The legacy release overrode the MOTD and max player count on every ping,
 * but only in game server mode ({@code pg.gameServer}). The rework tracks
 * lobbies instead, so the ping shows the state of the most relevant lobby (one
 * with players, otherwise one that is not waiting) and the summed capacity of
 * all configured lobbies. On a lobby/network server the entry is left alone.</p>
 */
public class ServerListPingEventListener implements Listener {
    private final PotionGamesX plugin;

    public ServerListPingEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        // Only a dedicated game server owns the server-list entry. A lobby or
        // network server (pg.gameServer: false, typically with several lobbies)
        // keeps the MOTD and slot count from server.properties - the legacy
        // 7.0.1 release behaved the same way.
        if (plugin.getConfigManager() == null || !plugin.getConfigManager().isGameServer()) {
            return;
        }

        ArrayList<Lobby> lobbies = plugin.getGame().getLobbies();
        if (lobbies == null || lobbies.isEmpty()) {
            return;
        }

        Lobby lobby = selectLobby(lobbies);
        if (lobby == null) {
            return;
        }

        int capacity = totalCapacity(lobbies);
        if (capacity > 0) {
            event.setMaxPlayers(capacity);
        }
        event.motd(motdFor(lobby));
    }

    /** Prefers the lobby players are actually in, then any non-waiting lobby. */
    private Lobby selectLobby(ArrayList<Lobby> lobbies) {
        for (Lobby lobby : lobbies) {
            if (lobby.getPlayerCount() > 0) {
                return lobby;
            }
        }
        for (Lobby lobby : lobbies) {
            if (lobby.getState() != GameStates.WAITING) {
                return lobby;
            }
        }
        return lobbies.get(0);
    }

    private int totalCapacity(ArrayList<Lobby> lobbies) {
        int capacity = 0;
        for (Lobby lobby : lobbies) {
            capacity += Math.max(0, lobby.getCapacity());
        }
        return capacity;
    }

    private Component motdFor(Lobby lobby) {
        GameStates state = lobby.getState();
        if (state == null) {
            return Messages.PluginName().colorIfAbsent(NamedTextColor.RED);
        }
        return switch (state) {
            case WAITING -> Component.text("WAITING").color(NamedTextColor.GREEN);
            case PREPARING -> preparingMotd(lobby);
            case INGAME, DEATHMATCH -> Component.text(state.toString()).color(NamedTextColor.GRAY);
            default -> Component.text(state.toString()).color(NamedTextColor.RED);
        };
    }

    private Component preparingMotd(Lobby lobby) {
        // getDisplayArena() also covers a single-map lobby, which never shows
        // the VOTING placeholder.
        Arena arena = lobby.getDisplayArena();
        if (arena != null && arena.getName() != null) {
            return Component.text(arena.getName().toUpperCase(Locale.ROOT)).color(NamedTextColor.GOLD);
        }
        return Component.text("VOTING").color(NamedTextColor.AQUA);
    }
}