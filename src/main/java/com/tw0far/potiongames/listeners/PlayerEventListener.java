package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.updatechecker.UpdateChecker;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

/**
 * Handles player-specific events (join, quit, move).
 * Extracted from monolithic Events.java.
 */
public class PlayerEventListener implements Listener {
    private final PotionGames plugin;

    public PlayerEventListener(PotionGames plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        plugin.createPlayer(p.getUniqueId().toString());
        plugin.joinChannel(p.getPlayer(), "Global");
        if (plugin.isGameServer() && plugin.isStartOnJoin() && !plugin.isLobbySystem()) {
            plugin.onJoin(p);
            e.joinMessage(null);
        }
        if (p.hasPermission("pg.update")) {
            new UpdateChecker(plugin, 87633).getVersion(version -> {
                if (!plugin.getPluginMeta().getVersion().equalsIgnoreCase(version)) {
                    p.sendMessage(Messages.UpdateAvailable(plugin.getPluginMeta().getVersion(), version));
                }
            });
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (plugin.isGameServer()) {
            if (plugin.isLobbySystem()) {
                String lobbyId = plugin.game.getPlayerLobby(p);
                if (lobbyId != null) {
                    if (!plugin.lobbyMove.get(lobbyId)) {
                        if (e.getFrom().getX() != Objects.requireNonNull(e.getTo()).getX() || e.getFrom().getZ() != e.getTo().getZ()) {
                            Location loc = new Location(p.getWorld(), e.getFrom().getX(), e.getTo().getY(), e.getFrom().getZ());
                            loc.setYaw(e.getTo().getYaw());
                            loc.setPitch(e.getTo().getPitch());
                            p.teleport(loc);
                        }
                    }
                }
            } else {
                if (plugin.game.isActivePlayer(p)) {
                    if (!plugin.isMove()) {
                        if (e.getFrom().getX() != Objects.requireNonNull(e.getTo()).getX() || e.getFrom().getZ() != e.getTo().getZ()) {
                            Location loc = new Location(p.getWorld(), e.getFrom().getX(), e.getTo().getY(), e.getFrom().getZ());
                            loc.setYaw(e.getTo().getYaw());
                            loc.setPitch(e.getTo().getPitch());
                            p.teleport(loc);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (plugin.isGameServer()) {
            if (plugin.isLobbySystem()) {
                String lobbyId = null;
                if (plugin.game.isInLobby(p)) {
                    lobbyId = plugin.game.getPlayerLobby(p);
                } else if (plugin.game.isInSpecLobby(p)) {
                    lobbyId = plugin.game.getSpectatorLobby(p);
                }
                if (lobbyId != null) {
                    plugin.onLeaveLobby(p, lobbyId);
                    e.quitMessage(null);
                }
            } else {
                if (plugin.game.isActivePlayer(p) || plugin.game.isSpectatorPlayer(p)) {
                    plugin.onLeave(p);
                    e.quitMessage(null);
                }
            }
        }
    }
}
