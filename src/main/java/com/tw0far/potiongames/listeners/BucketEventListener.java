package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.entity.Player;

/**
 * Handles bucket empty events.
 * Manages water bucket usage during games.
 */
public class BucketEventListener implements Listener {
    private final PotionGamesX plugin;

    public BucketEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        Player p = e.getPlayer();

        if (!plugin.getGame().isActivePlayer(p)) {
            return;
        }

        // Get the player's lobby
        String lobbyId = plugin.getGame().getPlayerLobby(p);

        if (lobbyId == null) {
            return;
        }

        // Prevent water bucket placement during games
        // Water can be used for escape/defensive purposes, which may be too powerful
        if (e.getBucket().toString().contains("WATER")) {
            e.setCancelled(true);
            p.sendMessage(Messages.line(Messages.BucketWaterNotAllowedText(), NamedTextColor.RED));
            return;
        }

        // Allow lava bucket (for damage purposes)
        // Allow milk bucket (for potion effects)
    }

    /**
     * A placed lava bucket could be scooped up again for free, which made lava
     * buckets infinite (and the tracked liquid block disappear).
     */
    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent e) {
        // Clicking no block means milking a cow, which stays allowed.
        if (e.getBlockClicked() == null) {
            return;
        }
        Player p = e.getPlayer();
        if (!plugin.getGame().isActivePlayer(p)) {
            return;
        }
        String lobbyId = plugin.getGame().getPlayerLobby(p);
        if (lobbyId == null) {
            return;
        }
        if (plugin.getLobbyStateManager().isBuildAllowed(lobbyId)) {
            return;
        }
        e.setCancelled(true);
        p.sendMessage(Messages.line(Messages.BucketFillNotAllowedText(), NamedTextColor.RED));
    }
}

