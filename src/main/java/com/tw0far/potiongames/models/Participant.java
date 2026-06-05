package com.tw0far.potiongames.models;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;

public class Participant {
    private Lobby lobby;
    private Kit kit;
    private Team team;
    private Arena votedArena;
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
        
        // Give lobby items
        PlayerInventory inv = player.getInventory();
        
        // Map/Arena selector
        ItemStack map = new ItemStack(Material.PAPER);
        ItemMeta mapMeta = map.getItemMeta();
        mapMeta.displayName(Messages.ArenaSelector());
        map.setItemMeta(mapMeta);
        inv.setItem(0, map);
        
        // Kit selector
        ItemStack kitItem = new ItemStack(Material.ENDER_CHEST);
        ItemMeta kitMeta = kitItem.getItemMeta();
        kitMeta.displayName(Messages.KitSelector());
        kitItem.setItemMeta(kitMeta);
        inv.setItem(1, kitItem);
        
        // Team selector
        ItemStack teamItem = new ItemStack(Material.CLOCK);
        ItemMeta teamMeta = teamItem.getItemMeta();
        teamMeta.displayName(Messages.SelectorTeam());
        teamItem.setItemMeta(teamMeta);
        inv.setItem(2, teamItem);
        
        // Stats
        ItemStack statsItem = new ItemStack(Material.EMERALD);
        ItemMeta statsMeta = statsItem.getItemMeta();
        statsMeta.displayName(Messages.StatsLabel());
        statsItem.setItemMeta(statsMeta);
        inv.setItem(7, statsItem);
        
        // Leave
        ItemStack leaveItem = new ItemStack(Material.MAGMA_CREAM);
        ItemMeta leaveMeta = leaveItem.getItemMeta();
        leaveMeta.displayName(Messages.LeaveLabel());
        leaveItem.setItemMeta(leaveMeta);
        inv.setItem(8, leaveItem);
    }

    public void teleportToOriginalLocation() {
        savedState.restore(player);
        savedState = null;
    }

    private void clearPlayer() {
        kit = null;
        team = null;
        votedArena = null;
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

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Arena getVotedArena() {
        return votedArena;
    }

    public void setVotedArena(Arena votedArena) {
        this.votedArena = votedArena;
    }

    public Lobby getLobby() {
        return lobby;
    }
}
