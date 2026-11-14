package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Lobby;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * Lobby players do not get hungry. Legacy kept the lobby world on PEACEFUL,
 * which stops the hunger drain entirely; 1.0 cannot change the difficulty of a
 * hub world, so the event is cancelled per player instead.
 */
public class LobbyHungerListener implements Listener {

    private final PotionGamesX plugin;

    public LobbyHungerListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        Lobby lobby = lobbyOf(player);
        if (lobby == null || !lobby.getState().isLobbyPhase()) {
            return;
        }
        event.setCancelled(true);
        // Undo a drained bar left over from a round.
        player.setFoodLevel(20);
        player.setSaturation(20f);
        player.setExhaustion(0f);
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
