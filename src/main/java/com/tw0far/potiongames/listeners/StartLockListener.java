package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Participant;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Holds players in place during the pre-round countdown after the teleport into
 * the arena (legacy behaviour) and during the deathmatch countdown.
 *
 * <p>Only the position is pinned: looking around and chat stay free, so the
 * countdown does not feel like a freeze screen.</p>
 */
public class StartLockListener implements Listener {

    private final PotionGamesX plugin;

    public StartLockListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMove(PlayerMoveEvent event) {
        Participant participant = participantOf(event.getPlayer());
        if (participant == null || !participant.isMovementLocked()) {
            return;
        }
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null || !positionChanged(from, to)) {
            return;
        }
        // Allow turning the head, block walking, jumping and falling.
        Location pinned = from.clone();
        pinned.setYaw(to.getYaw());
        pinned.setPitch(to.getPitch());
        event.setTo(pinned);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        Participant participant = participantOf(player);
        if (participant != null && participant.isMovementLocked()) {
            event.setCancelled(true);
        }
    }

    /** True when the player actually left their block (looking around is allowed). */
    static boolean positionChanged(Location from, Location to) {
        return from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ();
    }

    /** The player's participant, or null when they are not in a lobby. */
    private Participant participantOf(Player player) {
        String lobbyId = plugin.getGame().getPlayerLobby(player);
        if (lobbyId == null) {
            return null;
        }
        try {
            Lobby lobby = plugin.getGame().getLobby(Integer.parseInt(lobbyId));
            return lobby != null ? lobby.getParticipant(player) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
