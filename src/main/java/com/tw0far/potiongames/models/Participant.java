package com.tw0far.potiongames.models;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import net.kyori.adventure.text.Component;

public class Participant {

    // Hotbar slots of the lobby items, in the legacy 7.0.1 arrangement:
    // vote paper 0, kit selector 2, team selector 4, stats 6, leave 8.
    private static final int SLOT_ARENA_SELECTOR = 0;
    private static final int SLOT_KIT_SELECTOR = 2;
    private static final int SLOT_TEAM_SELECTOR = 4;
    private static final int SLOT_STATS = 6;
    private static final int SLOT_LEAVE = 8;
    private Lobby lobby;
    private Kit kit;
    private Player player;
    private PlayerState savedState;

    public Participant(Lobby lobby, Player player) {
        this.lobby = lobby;
        this.player = player;
    }

    public void teleportToLobby() {
        if (savedState == null) {
            savedState = new PlayerState(player);
        }
        clearPlayer();
        player.teleport(lobby.getSpawn());
        player.setGameMode(GameMode.ADVENTURE);

        PlayerInventory inv = player.getInventory();

        // Voting is pointless with a single arena, so the paper is only handed
        // out when there is something to choose (legacy behaviour).
        if (lobby.hasArenaChoice()) {
            inv.setItem(SLOT_ARENA_SELECTOR, item(Material.PAPER, Messages.ArenaSelectorItem()));
        }

        // The kit selector is only handed out when kits are enabled.
        if (lobby.isActivateKits()) {
            inv.setItem(SLOT_KIT_SELECTOR, item(Material.ENDER_CHEST, Messages.KitSelectorItem()));
        }

        inv.setItem(SLOT_TEAM_SELECTOR, item(Material.CLOCK, Messages.SelectorTeamItem()));
        inv.setItem(SLOT_STATS, item(Material.EMERALD, Messages.StatsItem()));
        inv.setItem(SLOT_LEAVE, item(Material.MAGMA_CREAM, Messages.LeaveItem()));
    }

    /**
     * Lobby item carrying its display name. A missing {@link ItemMeta} is not
     * fatal (an item keeps working, it just stays unnamed), which is why this
     * never dereferences the meta unguarded.
     */
    private static ItemStack item(Material material, Component name) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.displayName(name);
            stack.setItemMeta(meta);
        }
        return stack;
    }

    /**
     * Prepares the participant for a running round: the lobby inventory is
     * cleared and survival mode enabled, otherwise the arena block handling
     * (breaking, placing, buckets, flint and steel) is denied by adventure mode
     * and the lobby items stay in the inventory. The kit selection is kept.
     */
    public void startRound() {
        if (player == null) {
            return;
        }
        if (savedState == null) {
            savedState = new PlayerState(player);
        }
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getInventory().setItemInOffHand(null);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.setGameMode(GameMode.SURVIVAL);
        player.setFireTicks(0);
        player.setFoodLevel(20);
        player.setSaturation(20f);
        player.setLevel(0);
        player.setExp(0);
        player.setAllowFlight(false);
        player.setFlying(false);
        if (player.getAttribute(Attribute.MAX_HEALTH) != null) {
            player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getValue());
        }
    }

    public void teleportToOriginalLocation() {
        if (savedState == null) {
            return;
        }
        savedState.restore(player);
        savedState = null;
    }

    /**
     * Adopts a pre-join snapshot taken while the player was a spectator, so the
     * lobby keeps the state to restore on /pg leave.
     */
    public void adoptSavedState(PlayerState state) {
        if (state != null) {
            savedState = state;
        }
    }

    /**
     * Hands the pre-join state over to the caller (used when an active player
     * becomes a spectator and must not be teleported back yet).
     */
    public PlayerState takeSavedState() {
        PlayerState state = savedState;
        savedState = null;
        return state;
    }

    /**
     * Puts the player back into the lobby state the legacy release used: no
     * inventory, armour or offhand item, no leftover effects, no fire and full
     * health/food. Without this, arena armour and shop potion effects followed
     * the player into the lobby and into the next round.
     */
    private void clearPlayer() {
        kit = null;
        PlayerInventory inv = player.getInventory();
        inv.clear();
        inv.setArmorContents(null);
        inv.setItemInOffHand(null);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.setFireTicks(0);
        player.setFoodLevel(20);
        player.setSaturation(20f);
        if (player.getAttribute(Attribute.MAX_HEALTH) != null) {
            player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getValue());
        }
    }

    public void sendMessage(Component message) {
        player.sendMessage(message);
    }

    public void sendActionBar(Component message) {
        player.sendActionBar(message);
    }

    public Player getPlayer() {
        return player;
    }

    public Kit getKit() {
        return kit;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }
}
