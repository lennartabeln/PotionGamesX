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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles player-specific events (join, quit, move).
 * Extracted from monolithic Events.java.
 */
public class PlayerEventListener implements Listener {
    private static final Logger LOGGER = Logger.getLogger("Minecraft");
    private final PotionGames plugin;

    public PlayerEventListener(PotionGames plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        plugin.getDatabaseManager().createPlayer(p.getUniqueId().toString());
        if (p.hasPermission("pg.update")) {
            new UpdateChecker(plugin, 87633).getVersion(version -> {
                if (!plugin.getPluginMeta().getVersion().equalsIgnoreCase(version)) {
                    p.sendMessage(Messages.UpdateAvailable(plugin.getPluginMeta().getVersion(), version));
                }
            });
        }
        
        // Auto-join first lobby if configured
        if (plugin.getConfigManager().isGameServer() && plugin.getConfigManager().isStartOnJoin()) {
            plugin.getGame().autoJoinLobby(p);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (plugin.getConfigManager().isGameServer()) {
            String lobbyId = plugin.getGame().getPlayerLobby(p);
            if (lobbyId != null) {
                try {
                    com.tw0far.potiongames.models.Lobby lobby = plugin.getGame().getLobby(Integer.parseInt(lobbyId));
                    if (lobby != null && !lobby.isMoveAllowed()) {
                        if (e.getFrom().getX() != Objects.requireNonNull(e.getTo()).getX() || e.getFrom().getZ() != e.getTo().getZ()) {
                            Location loc = new Location(p.getWorld(), e.getFrom().getX(), e.getTo().getY(), e.getFrom().getZ());
                            loc.setYaw(e.getTo().getYaw());
                            loc.setPitch(e.getTo().getPitch());
                            p.teleport(loc);
                        }
                    }
                } catch (NumberFormatException ex) {
                    LOGGER.log(Level.WARNING, "[PotionGames] Invalid lobby ID in move event", ex);
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (plugin.getConfigManager().isGameServer()) {
            String lobbyId = null;
            if (plugin.getGame().isInLobby(p)) {
                lobbyId = plugin.getGame().getPlayerLobby(p);
            } else if (plugin.getGame().isInSpecLobby(p)) {
                lobbyId = plugin.getGame().getSpectatorLobby(p);
            }
            if (lobbyId != null) {
                plugin.onLeaveLobby(p, lobbyId);
                e.quitMessage(null);
            }
        }
    }
}
