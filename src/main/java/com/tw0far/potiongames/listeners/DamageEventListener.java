package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.GameStates;
import com.tw0far.potiongames.models.Lobby;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;

/**
 * Handles entity damage events.
 * Manages player damage, damage cancellation, and damage tracking.
 */
public class DamageEventListener implements Listener {
    private final PotionGamesX plugin;

    public DamageEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        Player p = (Player) e.getEntity();

        boolean isActive = plugin.getGame().isActivePlayer(p);
        boolean isSpectator = plugin.getGame().isSpectatorPlayer(p);

        if (!isActive && !isSpectator) {
            return;
        }

        // Spectators cannot take damage
        if (isSpectator) {
            e.setCancelled(true);
            return;
        }

        // Lobby phase: no damage at all. Legacy switched the lobby world to
        // PEACEFUL with FALL_DAMAGE=false while waiting, so a fall off the lobby
        // platform or a stray hit cannot hurt (or kill) anyone before the round.
        Lobby lobby = lobbyOf(p);
        if (lobby != null && lobbyPhase(lobby.getState())) {
            e.setCancelled(true);
        }
    }

    /** States in which players are in the lobby and must not take damage. */
    static boolean lobbyPhase(GameStates state) {
        return state == GameStates.WAITING || state == GameStates.PREPARING || state == GameStates.RESET;
    }

    private Lobby lobbyOf(Player player) {
        String lobbyId = plugin.getGame().getPlayerLobby(player);
        if (lobbyId == null) {
            return null;
        }
        try {
            return plugin.getGame().getLobby(Integer.parseInt(lobbyId));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        Player victim = (Player) e.getEntity();

        if (!(e.getDamager() instanceof Player)) {
            return;
        }

        Player attacker = (Player) e.getDamager();

        // Only manage PvP when at least one participant belongs to a game lobby
        boolean victimInLobby = plugin.getGame().getPlayerLobby(victim) != null;
        boolean attackerInLobby = plugin.getGame().getPlayerLobby(attacker) != null;
        if (!victimInLobby && !attackerInLobby) {
            return;
        }

        boolean victimActive = plugin.getGame().isActivePlayer(victim);
        boolean attackerActive = plugin.getGame().isActivePlayer(attacker);

        // Both must be active players to fight
        if (!victimActive || !attackerActive) {
            e.setCancelled(true);
            return;
        }

        // Damage allowed - attacker has hit victim
    }
}
