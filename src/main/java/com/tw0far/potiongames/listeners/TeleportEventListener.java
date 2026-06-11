package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.entity.Player;

/**
 * Handles player teleport events.
 * Manages spectator teleportation and game boundaries.
 */
public class TeleportEventListener implements Listener {
    private final PotionGamesX plugin;

    public TeleportEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();

        boolean isActive = plugin.getGame().isActivePlayer(p);
        boolean isSpectator = plugin.getGame().isSpectatorPlayer(p);

        if (!isActive && !isSpectator) {
            return;
        }

        // Allow spectator teleportation (they can freely move around)
        if (isSpectator) {
            return;
        }

        // Active players can teleport normally during games
        // (Ender pearls, chorus fruit, etc. should work for pvp)
        PlayerTeleportEvent.TeleportCause cause = e.getCause();
        if (cause == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }
        // Chorus fruit may not be present in some API versions; check at runtime
        try {
            PlayerTeleportEvent.TeleportCause chorus = PlayerTeleportEvent.TeleportCause.valueOf("CHORUS_FRUIT");
            if (cause == chorus) {
                return;
            }
        } catch (IllegalArgumentException ignored) {
            // CHORUS_FRUIT not present in this API - ignore and continue checks
        }

        // Allow other teleport causes for active players
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.UNKNOWN
            || e.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) {
            // Allow plugin-based teleports (e.g., respawn)
            return;
        }

        // Allow command-based teleports
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND) {
            return;
        }
    }
}
