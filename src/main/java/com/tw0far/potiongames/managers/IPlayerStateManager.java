package com.tw0far.potiongames.managers;

import org.bukkit.entity.Player;
import java.util.Collection;

/**
 * Manager for all player-specific state tracking.
 */
public interface IPlayerStateManager extends IManager {

    // ===== ACTIVE PLAYER TRACKING =====
    void addActivePlayer(Player player);
    void removeActivePlayer(Player player);
    Collection<Player> getActivePlayers();

    // ===== SPECTATOR TRACKING =====
    void removeSpectator(Player player);
    Collection<Player> getSpectators();

    // ===== BATCH OPERATIONS =====
    void clearAll();
}
