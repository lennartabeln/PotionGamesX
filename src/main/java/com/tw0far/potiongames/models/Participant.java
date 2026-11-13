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

        ItemStack map = new ItemStack(Material.PAPER);
        ItemMeta mapMeta = map.getItemMeta();
        mapMeta.displayName(Messages.ArenaSelectorItem());
        map.setItemMeta(mapMeta);
        inv.setItem(0, map);

        // The kit selector is only handed out when kits are enabled.
        if (lobby.isActivateKits()) {
            ItemStack kitItem = new ItemStack(Material.ENDER_CHEST);
            ItemMeta kitMeta = kitItem.getItemMeta();
            kitMeta.displayName(Messages.KitSelectorItem());
            kitItem.setItemMeta(kitMeta);
            inv.setItem(1, kitItem);
        }

        ItemStack teamItem = new ItemStack(Material.CLOCK);
        ItemMeta teamMeta = teamItem.getItemMeta();
        teamMeta.displayName(Messages.SelectorTeamItem());
        teamItem.setItemMeta(teamMeta);
        inv.setItem(2, teamItem);

        ItemStack statsItem = new ItemStack(Material.EMERALD);
        ItemMeta statsMeta = statsItem.getItemMeta();
        statsMeta.displayName(Messages.StatsItem());
        statsItem.setItemMeta(statsMeta);
        inv.setItem(7, statsItem);

        ItemStack leaveItem = new ItemStack(Material.MAGMA_CREAM);
        ItemMeta leaveMeta = leaveItem.getItemMeta();
        leaveMeta.displayName(Messages.LeaveItem());
        leaveItem.setItemMeta(leaveMeta);
        inv.setItem(8, leaveItem);
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

    private void clearPlayer() {
        kit = null;
        player.getInventory().clear();
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
