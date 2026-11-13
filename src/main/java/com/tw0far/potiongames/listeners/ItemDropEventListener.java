package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.GameStates;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.entity.Player;

/**
 * Handles item drop events.
 * Prevents dropping items during lobby/game phases.
 */
public class ItemDropEventListener implements Listener {
    private final PotionGamesX plugin;

    public ItemDropEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        String lobbyId = plugin.getGame().getPlayerLobby(p);
        if (lobbyId == null) {
            // Setup/build mode and every player outside the plugin is untouched.
            return;
        }
        // Dropping loot is part of a round; in the lobby the selector items have
        // to stay in the inventory (the old check tested isActivePlayer, which is
        // true for waiting players too, so nothing was ever cancelled).
        GameStates state = plugin.getLobbyStateManager().getGameState(lobbyId);
        if (state != GameStates.INGAME && state != GameStates.DEATHMATCH) {
            e.setCancelled(true);
        }
    }
}
