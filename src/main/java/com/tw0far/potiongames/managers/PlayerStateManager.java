package com.tw0far.potiongames.managers;

import org.bukkit.entity.Player;
import java.util.*;

/**
 * Implementation of IPlayerStateManager.
 */
public class PlayerStateManager implements IPlayerStateManager {

    private final Set<Player> activePlayers = new HashSet<>();
    private final Set<Player> spectators = new HashSet<>();

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
        clearAll();
    }

    // ===== ACTIVE PLAYER TRACKING =====
    @Override
    public void addActivePlayer(Player player) {
        activePlayers.add(player);
        spectators.remove(player);
    }

    @Override
    public void removeActivePlayer(Player player) {
        activePlayers.remove(player);
    }

    @Override
    public Collection<Player> getActivePlayers() {
        return new HashSet<>(activePlayers);
    }

    // ===== SPECTATOR TRACKING =====
    @Override
    public void removeSpectator(Player player) {
        spectators.remove(player);
    }

    @Override
    public Collection<Player> getSpectators() {
        return new HashSet<>(spectators);
    }

    // ===== BATCH OPERATIONS =====
    @Override
    public void clearAll() {
        activePlayers.clear();
        spectators.clear();
    }
}
