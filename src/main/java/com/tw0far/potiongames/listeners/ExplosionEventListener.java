package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Lobby;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * Keeps explosions from permanently damaging arena geometry.
 *
 * <p>{@code Lobby#restoreBlocks} can only restore blocks the plugin tracked
 * itself, so blocks removed by TNT would stay missing forever. Explosions in
 * arena worlds therefore only damage entities; block damage is dropped unless
 * build mode is enabled for that lobby.</p>
 */
public class ExplosionEventListener implements Listener {
    private final PotionGamesX plugin;

    public ExplosionEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.blockList().isEmpty()) {
            return;
        }
        if (shouldProtect(event.getLocation())) {
            event.blockList().clear();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        if (event.blockList().isEmpty()) {
            return;
        }
        if (shouldProtect(event.getBlock().getLocation())) {
            event.blockList().clear();
        }
    }

    private boolean shouldProtect(Location origin) {
        World world = origin.getWorld();
        if (world == null) {
            return false;
        }
        for (Lobby lobby : plugin.getGame().getLobbies()) {
            if (!lobby.isArenaWorld(world)) {
                continue;
            }
            // Build mode still allows admins to reshape the arena
            if (!plugin.getLobbyStateManager().isBuildAllowed(Integer.toString(lobby.getId()))) {
                return true;
            }
        }
        return false;
    }
}