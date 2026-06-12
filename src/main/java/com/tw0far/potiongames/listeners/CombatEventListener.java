package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Lobby;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Objects;

/**
 * Handles combat-related events (multi-lobby only).
 * Extracted from monolithic Events.java.
 */
public class CombatEventListener implements Listener {
    private final PotionGamesX plugin;

    public CombatEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    private String getPlayerTeam(String lobbyId, Player player) {
        try {
            Lobby lobby = plugin.getGame().getLobby(Integer.parseInt(lobbyId));
            if (lobby != null) return lobby.getPlayerTeam(player);
        } catch (NumberFormatException ignored) { }
        return null;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
            if (e.getEntity() instanceof Player) {
                e.setCancelled(e.getDamager() instanceof LightningStrike || e.getDamager() instanceof Firework);
            }
            if (e.getEntity() instanceof Player p && e.getDamager() instanceof TNTPrimed) {
                p.setHealth(Math.max(0, p.getHealth() - 4));
            }
            if (!plugin.getConfigManager().isFriendlyFire()) {
                if (e.getEntity() instanceof Player p && e.getDamager() instanceof Player d) {
                    // Get lobby ID for both players
                    String pLobby = plugin.getGame().getPlayerLobby(p);
                    String dLobby = plugin.getGame().getPlayerLobby(d);

                    // Both must be in same lobby for friendly fire check
                    if (pLobby != null && pLobby.equals(dLobby)) {
                        String pTeam = getPlayerTeam(pLobby, p);
                        String dTeam = getPlayerTeam(dLobby, d);
                        if (Objects.equals(pTeam, dTeam)) {
                            e.setCancelled(true);
                        }
                    }
                }
            }
    }
}
