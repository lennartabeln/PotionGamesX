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

        Lobby lobby = lobbyOf(p);
        // Two rules, both per player: spectators never take damage, and a lobby
        // member is only vulnerable while their round is running. Players who are
        // in no lobby at all keep the normal server rules.
        if (plugin.getGame().isSpectatorPlayer(p)) {
            e.setCancelled(true);
            return;
        }
        if (lobby != null && !lobby.getState().isRunning()) {
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
