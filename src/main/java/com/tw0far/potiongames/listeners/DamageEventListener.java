package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.GameStates;
import com.tw0far.potiongames.models.Lobby;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Handles entity damage events.
 * Manages player damage, damage cancellation, and damage tracking.
 *
 * <p>The PvP gate looks at the <em>state</em> of the two lobbies instead of the
 * {@code activePlayers} list: a player who is playing a round may always hit
 * another player who is playing a round, however the internal bookkeeping looks
 * at that moment. Everyone else (lobby, lobby phase, spectator) stays
 * protected.</p>
 */
public class DamageEventListener implements Listener {

    private final PotionGamesX plugin;

    public DamageEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) {
            return;
        }

        boolean isActive = plugin.getGame().isActivePlayer(p);
        boolean isSpectator = plugin.getGame().isSpectatorPlayer(p);
        Lobby playerLobby = lobbyOf(p);
        boolean playing = playerLobby != null && isPlaying(playerLobby);

        // The lobby safe zone protects everyone standing in it - but never a
        // player who is actively fighting in a round, so a map that overlaps the
        // radius keeps working.
        if (!(isActive && playing) && plugin.getGame().isInSafeZone(p)) {
            e.setCancelled(true);
            return;
        }

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
        if (playerLobby != null && playerLobby.getState().isLobbyPhase()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player victim)) {
            return;
        }

        Player attacker = attackerOf(e);
        if (attacker == null || attacker.equals(victim)) {
            return;
        }

        Lobby victimLobby = lobbyOf(victim);
        Lobby attackerLobby = lobbyOf(attacker);

        // Nobody involved: untouched (build mode, hub, other plugins).
        if (victimLobby == null && attackerLobby == null) {
            return;
        }

        // Two players in a running round may fight; friendly fire and the
        // deathmatch rules are handled by CombatEventListener.
        if (isPlaying(victimLobby) && isPlaying(attackerLobby)) {
            return;
        }

        // A lobby player, a spectator or anyone outside the round stays safe.
        e.setCancelled(true);
    }

    /** True when the lobby is in a state the players can fight in. */
    private boolean isPlaying(Lobby lobby) {
        if (lobby == null) {
            return false;
        }
        GameStates state = lobby.getState();
        return state == GameStates.INGAME || state == GameStates.DEATHMATCH;
    }

    /** Player behind the hit: the attacker itself or the shooter of a projectile. */
    private Player attackerOf(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            return player;
        }
        if (event.getDamager() instanceof Projectile projectile
                && projectile.getShooter() instanceof Player player) {
            return player;
        }
        return null;
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
}
