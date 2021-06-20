package com.asga.potiongames.events;

import com.asga.potiongames.gamestates.GameStates;
import com.asga.potiongames.main.PotionGames;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class Events implements Listener {
    private final PotionGames pg;
    private int amount;
    private int bottle;
    private String lobby;
    private String arena;

    public Events(PotionGames pg) {
        this.pg = pg;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (pg.isGameServer()) {
            FileConfiguration arenadata = YamlConfiguration.loadConfiguration(pg.arenadatafile);
            Player p = e.getPlayer();
            e.getRecipients().clear();
            if (pg.isAddlobby()) {
                lobby = e.getMessage();
                e.setCancelled(true);
                if (p.hasPermission("pg.setup")) {
                    if (pg.isLobbySystem()) {
                        arenadata.set("pg.lobbies." + lobby + ".world", Objects.requireNonNull(p.getLocation().getWorld()).getName());
                        arenadata.set("pg.lobbies." + lobby + ".coords", Objects.requireNonNull(p.getLocation()));
                        arenadata.set("pg.lobbies." + lobby + ".activateTeams", true);
                        arenadata.set("pg.lobbies." + lobby + ".activateKits", true);
                        arenadata.set("pg.lobbies." + lobby + ".activateShop", true);
                        arenadata.set("pg.lobbies." + lobby + ".teamSize", 2);
                        arenadata.set("pg.lobbies." + lobby + ".maxPlayers", 24);
                        arenadata.set("pg.lobbies." + lobby + ".minPlayers", 12);
                        arenadata.set("pg.lobbies." + lobby + ".roundTime", 30);
                        try {
                            arenadata.save(pg.arenadatafile);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(24) + ChatColor.GRAY + " (" + "Lobby: " + lobby + ")");
                    }
                }
                pg.setAddlobby(false);
                pg.setup(p);
            }
            if (pg.isAddarena()) {
                arena = e.getMessage();
                e.setCancelled(true);
                if (p.hasPermission("pg.setup")) {
                    if (!pg.isLobbySystem()) {
                        int arenaNumber = 1;
                        try {
                            while (arenadata.contains("pg.arenas." + arenaNumber)) {
                                arenaNumber++;
                            }
                            arenadata.set("pg.arenas." + arenaNumber, p.getWorld());
                            arenadata.set("pg.arenas." + arenaNumber + ".world", p.getWorld().getName());
                            arenadata.set("pg.arenas." + arenaNumber + ".name", arena);
                            arenadata.save(pg.arenadatafile);
                            p.sendMessage(pg.prefix + ChatColor.AQUA + arena + ChatColor.GREEN + " " + pg.chat.get(29));
                        } catch (Exception ex) {
                            p.sendMessage(pg.prefix + ChatColor.AQUA + arena + ChatColor.RED + " " + pg.chat.get(27));
                        }
                    }
                }
                if (p.hasPermission("pg.setup")) {
                    if (pg.isLobbySystem()) {
                        int arenaNumber = 1;
                        try {
                            while (arenadata.contains("pg.lobbies." + lobby + "." + arenaNumber)) {
                                arenaNumber++;
                            }
                            arenadata.set("pg.lobbies." + lobby + "." + arenaNumber, p.getWorld());
                            arenadata.set("pg.lobbies." + lobby + "." + arenaNumber + ".world", p.getWorld().getName());
                            arenadata.set("pg.lobbies." + lobby + "." + arenaNumber + ".name", arena);
                            arenadata.save(pg.arenadatafile);
                            p.sendMessage(pg.prefix + ChatColor.AQUA + arena + ChatColor.GREEN + " " + pg.chat.get(29) + ChatColor.GRAY + " (" + "Lobby: " + lobby + ")");
                        } catch (Exception ex) {
                            p.sendMessage(pg.prefix + ChatColor.AQUA + lobby + ChatColor.RED + " " + pg.chat.get(27) + ChatColor.GRAY + " (" + "Lobby: " + lobby + ")");
                        }
                    }
                }
                pg.setAddarena(false);
                pg.setup(p);
            }
            if (pg.isDellobby()) {
                lobby = e.getMessage();
                e.setCancelled(true);
                if (p.hasPermission("pg.setup")) {
                    if (pg.isLobbySystem()) {
                        arenadata.set("pg.lobbies." + lobby, null);
                        try {
                            arenadata.save(pg.arenadatafile);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(66) + ChatColor.GRAY + " (" + "Lobby: " + lobby + ")");
                    }
                }
                pg.setDellobby(false);
                pg.setup(p);
            }
            if (pg.isDelarena()) {
                arena = e.getMessage();
                e.setCancelled(true);
                if (p.hasPermission("pg.setup")) {
                    if (!pg.isLobbySystem()) {
                        int arenaNumber = 1;
                        try {
                            int i = 1;
                            boolean arenaID = false;
                            while (!arenaID) {
                                if (arena.matches(Objects.requireNonNull(arenadata.getString("pg.arenas." + i + ".name")))) {
                                    arenaNumber = i;
                                    arenaID = true;
                                } else {
                                    i++;
                                }
                            }
                            String arenaName = arena;
                            arenadata.set("pg.arenas." + arenaNumber, null);
                            arenadata.save(pg.arenadatafile);
                            p.sendMessage(pg.prefix + ChatColor.AQUA + arenaName + ChatColor.GREEN + " " + pg.chat.get(28));
                        } catch (Exception ex) {
                            p.sendMessage(pg.prefix + ChatColor.AQUA + arena + ChatColor.RED + " " + pg.chat.get(27));
                        }
                    }
                }
                if (p.hasPermission("pg.setup")) {
                    if (pg.isLobbySystem()) {
                        int arenaNumber = 1;
                        try {
                            int i = 1;
                            boolean arenaID = false;
                            while (!arenaID) {
                                if (arena.matches(Objects.requireNonNull(arenadata.getString("pg.lobbies." + lobby + "." + i + ".name")))) {
                                    arenaNumber = i;
                                    arenaID = true;
                                } else {
                                    i++;
                                }
                            }
                            String arenaName = arena;
                            arenadata.set("pg.lobbies." + lobby + "." + arenaNumber, null);
                            arenadata.save(pg.arenadatafile);
                            p.sendMessage(pg.prefix + ChatColor.AQUA + arenaName + ChatColor.GREEN + " " + pg.chat.get(28) + ChatColor.GRAY + " (" + "Lobby: " + lobby + ")");
                        } catch (Exception ex) {
                            p.sendMessage(pg.prefix + ChatColor.AQUA + lobby + ChatColor.RED + " " + pg.chat.get(27) + ChatColor.GRAY + " (" + "Lobby: " + lobby + ")");
                        }
                    }
                }
                pg.setDelarena(false);
                pg.setup(p);
            }
            if (pg.playerChannel.get(p) == null) {
                pg.joinChannel(p.getPlayer(), "Global");
            }
            if (pg.playerChannel.get(p).equals("Local") && pg.playerChannel.get(p) != null) {
                if (pg.isLobbySystem()) {
                    String s = null;
                    for (int ii = 1; ii <= 1000; ii++) {
                        if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                            s = Integer.toString(ii);
                        }
                    }
                    pg.playerLobby.keySet().forEach(ent -> {
                        if (ent != null) {
                            e.getRecipients().add(ent);
                        }
                    });
                    pg.specLobby.keySet().forEach(ent -> {
                        if (ent != null) {
                            e.getRecipients().add(ent);
                        }
                    });
                    e.getRecipients().add(p);
                    String message = ChatColor.WHITE + e.getMessage();
                    if (!p.hasPermission("pg.admin")) {
                        if (pg.playerLobby.containsKey(p)) {
                            e.setCancelled(true);
                            for (Player pgchat : pg.playerLobby.keySet()) {
                                if (pg.playerLobby.get(pgchat).equals(s)) {
                                    pgchat.sendMessage(pg.prefix + p.getDisplayName() + ": " + message);
                                }
                            }
                            for (Player pgchat : pg.specLobby.keySet()) {
                                if (pg.specLobby.get(pgchat).equals(s)) {
                                    pgchat.sendMessage(pg.prefix + p.getDisplayName() + ": " + message);
                                }
                            }
                        } else if (pg.specLobby.containsKey(p)) {
                            e.setCancelled(true);
                        }
                    } else {
                        if (pg.playerLobby.containsKey(p)) {
                            e.setCancelled(true);
                            for (Player pgchat : pg.playerLobby.keySet()) {
                                if (pg.playerLobby.get(pgchat).equals(s)) {
                                    pgchat.sendMessage(pg.prefix + ChatColor.DARK_AQUA + p.getDisplayName() + ": " + message);
                                }
                            }
                            for (Player pgchat : pg.specLobby.keySet()) {
                                if (pg.specLobby.get(pgchat).equals(s)) {
                                    pgchat.sendMessage(pg.prefix + ChatColor.DARK_AQUA + p.getDisplayName() + ": " + message);
                                }
                            }
                        } else if (pg.specPlayers.contains(p)) {
                            e.setCancelled(true);
                            for (Player pgchat : pg.playerLobby.keySet()) {
                                if (pg.playerLobby.get(pgchat).equals(s)) {
                                    pgchat.sendMessage(pg.prefix + ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + pg.chat.get(8) + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA + p.getDisplayName() + ": " + message);
                                }
                            }
                            for (Player pgchat : pg.specLobby.keySet()) {
                                if (pg.specLobby.get(pgchat).equals(s)) {
                                    pgchat.sendMessage(pg.prefix + ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + pg.chat.get(8) + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA + p.getDisplayName() + ": " + message);
                                }
                            }
                        }
                    }
                } else {
                    pg.pgPlayers.forEach(ent -> {
                        if (ent != null) {
                            e.getRecipients().add(ent);
                        }
                    });
                    pg.specPlayers.forEach(ent -> {
                        if (ent != null) {
                            e.getRecipients().add(ent);
                        }
                    });
                    e.getRecipients().add(p);
                    String message = ChatColor.WHITE + e.getMessage();
                    if (!p.hasPermission("pg.admin")) {
                        if (pg.pgPlayers.contains(p)) {
                            e.setCancelled(true);
                            for (Player pgchat : pg.pgPlayers) {
                                pgchat.sendMessage(pg.prefix + p.getDisplayName() + ": " + message);
                            }
                            for (Player pgchat : pg.specPlayers) {
                                pgchat.sendMessage(pg.prefix + p.getDisplayName() + ": " + message);
                            }
                        } else if (pg.specPlayers.contains(p)) {
                            e.setCancelled(true);
                        }
                    } else {
                        if (pg.pgPlayers.contains(p)) {
                            e.setCancelled(true);
                            for (Player pgchat : pg.pgPlayers) {
                                pgchat.sendMessage(pg.prefix + ChatColor.DARK_AQUA + p.getDisplayName() + ": " + message);
                            }
                            for (Player pgchat : pg.specPlayers) {
                                pgchat.sendMessage(pg.prefix + ChatColor.DARK_AQUA + p.getDisplayName() + ": " + message);
                            }
                        } else if (pg.specPlayers.contains(p)) {
                            e.setCancelled(true);
                            for (Player pgchat : pg.pgPlayers) {
                                pgchat.sendMessage(pg.prefix + ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + pg.chat.get(8) + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA + p.getDisplayName() + ": " + message);
                            }
                            for (Player pgchat : pg.specPlayers) {
                                pgchat.sendMessage(pg.prefix + ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + pg.chat.get(8) + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA + p.getDisplayName() + ": " + message);
                            }
                        }
                    }
                }
            }
            pg.getChannel(p).forEach(player -> e.getRecipients().add(player));
        }
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent e) {
        if (pg.isGameServer()) {
            if (pg.worlds.contains(e.getBlock().getWorld().getName())) {
                switch (e.getBlock().getType()) {
                    case ACACIA_LEAVES:
                    case BIRCH_LEAVES:
                    case DARK_OAK_LEAVES:
                    case JUNGLE_LEAVES:
                    case OAK_LEAVES:
                    case SPRUCE_LEAVES:
                        e.setCancelled(true);
                        break;
                    default:
                        e.setCancelled(false);
                        break;
                }
            }
        }
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
        if (pg.isGameServer()) {
            if (pg.worlds.contains(e.getBlock().getWorld().getName())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent e) {
        if (pg.isGameServer()) {
            if (pg.worlds.contains(e.getBlock().getWorld().getName())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        if (pg.isGameServer()) {
            Player p = e.getPlayer();
            if (pg.pgPlayers.contains(p) || pg.playerLobby.containsKey(p)) {
                if (pg.isLobbySystem()) {
                    String s = null;
                    for (int ii = 1; ii <= 1000; ii++) {
                        if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                            s = Integer.toString(ii);
                        }
                    }
                    if (!pg.lobbyBuild.get(s)) {
                        if (pg.lobbyStates.get(s) == GameStates.INGAME) {
                            if (e.getBucket() != Material.WATER_BUCKET || e.getBucket() != Material.LAVA_BUCKET) {
                                Block block = e.getBlockClicked().getRelative(e.getBlockFace());
                                Location loc = e.getBlockClicked().getRelative(e.getBlockFace()).getLocation();
                                pg.lobbyLiquidPlacedData.put(loc, block);
                                pg.lobbyLiquidPlaced.put(s, pg.lobbyLiquidPlacedData);
                            }
                        }
                    }
                } else {
                    if (!pg.isBuild()) {
                        if (pg.getGamestate() == GameStates.INGAME) {
                            if (e.getBucket() != Material.WATER_BUCKET || e.getBucket() != Material.LAVA_BUCKET) {
                                Block block = e.getBlockClicked().getRelative(e.getBlockFace());
                                Location loc = e.getBlockClicked().getRelative(e.getBlockFace()).getLocation();
                                pg.getLiquidPlaced().put(loc, block);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (pg.isGameServer()) {
            Player p = e.getPlayer();
            if (pg.pgPlayers.contains(p) || pg.playerLobby.containsKey(p)) {
                if (pg.isLobbySystem()) {
                    String s = null;
                    for (int ii = 1; ii <= 1000; ii++) {
                        if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                            s = Integer.toString(ii);
                        }
                    }
                    if (!pg.lobbyBuild.get(s)) {
                        if (pg.lobbyStates.get(s) == GameStates.INGAME) {
                            if (e.getBlock().getType() == Material.COBWEB || e.getBlock().getType() == Material.FIRE
                                    || e.getBlock().getType() == Material.CAKE || e.getBlock().getType() == Material.GRASS
                                    || e.getBlock().getType() == Material.TALL_GRASS || e.getBlock().getType() == Material.DEAD_BUSH
                                    || e.getBlock().getType() == Material.ACACIA_LEAVES
                                    || e.getBlock().getType() == Material.BIRCH_LEAVES
                                    || e.getBlock().getType() == Material.DARK_OAK_LEAVES
                                    || e.getBlock().getType() == Material.JUNGLE_LEAVES
                                    || e.getBlock().getType() == Material.OAK_LEAVES
                                    || e.getBlock().getType() == Material.SPRUCE_LEAVES
                                    || e.getBlock().getType() == Material.WARPED_FUNGUS
                                    || e.getBlock().getType() == Material.CRIMSON_FUNGUS
                                    || e.getBlock().getType() == Material.BROWN_MUSHROOM
                                    || e.getBlock().getType() == Material.RED_MUSHROOM) {
                                pg.lobbyPlacedBlocksData.put(e.getBlock().getLocation(), e.getBlock().getType());
                                pg.lobbyPlacedBlocks.put(s, pg.lobbyPlacedBlocksData);
                                e.setCancelled(false);
                            } else {
                                e.setCancelled(true);
                            }
                        }
                    }
                } else {
                    if (!pg.isBuild()) {
                        if (pg.getGamestate() == GameStates.INGAME) {
                            if (e.getBlock().getType() == Material.COBWEB || e.getBlock().getType() == Material.FIRE
                                    || e.getBlock().getType() == Material.CAKE || e.getBlock().getType() == Material.GRASS
                                    || e.getBlock().getType() == Material.TALL_GRASS || e.getBlock().getType() == Material.DEAD_BUSH
                                    || e.getBlock().getType() == Material.ACACIA_LEAVES
                                    || e.getBlock().getType() == Material.BIRCH_LEAVES
                                    || e.getBlock().getType() == Material.DARK_OAK_LEAVES
                                    || e.getBlock().getType() == Material.JUNGLE_LEAVES
                                    || e.getBlock().getType() == Material.OAK_LEAVES
                                    || e.getBlock().getType() == Material.SPRUCE_LEAVES
                                    || e.getBlock().getType() == Material.WARPED_FUNGUS
                                    || e.getBlock().getType() == Material.CRIMSON_FUNGUS
                                    || e.getBlock().getType() == Material.BROWN_MUSHROOM
                                    || e.getBlock().getType() == Material.RED_MUSHROOM) {
                                pg.getPlacedBlocks().put(e.getBlock().getLocation(), e.getBlock().getType());
                                e.setCancelled(false);
                            } else {
                                e.setCancelled(true);
                            }
                        } else {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (pg.isGameServer()) {
            Player p = e.getPlayer();
            if (pg.pgPlayers.contains(p) || pg.playerLobby.containsKey(p)) {

                if (pg.isLobbySystem()) {
                    String s = null;
                    for (int ii = 1; ii <= 1000; ii++) {
                        if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                            s = Integer.toString(ii);
                        }
                    }
                    if (!pg.lobbyBuild.get(s)) {
                        if (pg.lobbyStates.get(s) == GameStates.INGAME) {
                            if (e.getBlock().getType() == Material.COBWEB || e.getBlock().getType() == Material.FIRE
                                    || e.getBlock().getType() == Material.CAKE || e.getBlock().getType() == Material.GRASS
                                    || e.getBlock().getType() == Material.TALL_GRASS || e.getBlock().getType() == Material.DEAD_BUSH
                                    || e.getBlock().getType() == Material.ACACIA_LEAVES
                                    || e.getBlock().getType() == Material.BIRCH_LEAVES
                                    || e.getBlock().getType() == Material.DARK_OAK_LEAVES
                                    || e.getBlock().getType() == Material.JUNGLE_LEAVES
                                    || e.getBlock().getType() == Material.OAK_LEAVES
                                    || e.getBlock().getType() == Material.SPRUCE_LEAVES
                                    || e.getBlock().getType() == Material.WARPED_FUNGUS
                                    || e.getBlock().getType() == Material.CRIMSON_FUNGUS
                                    || e.getBlock().getType() == Material.BROWN_MUSHROOM
                                    || e.getBlock().getType() == Material.RED_MUSHROOM) {
                                pg.lobbyBreakedBlocksData.put(e.getBlock().getLocation(), e.getBlock().getType());
                                pg.lobbyBreakedBlocks.put(s, pg.lobbyBreakedBlocksData);
                                e.setCancelled(false);
                            } else {
                                e.setCancelled(true);
                            }
                        }
                    }
                } else {
                    if (!pg.isBuild()) {
                        if (pg.getGamestate() == GameStates.INGAME) {
                            if (e.getBlock().getType() == Material.COBWEB || e.getBlock().getType() == Material.FIRE
                                    || e.getBlock().getType() == Material.CAKE || e.getBlock().getType() == Material.GRASS
                                    || e.getBlock().getType() == Material.TALL_GRASS || e.getBlock().getType() == Material.DEAD_BUSH
                                    || e.getBlock().getType() == Material.ACACIA_LEAVES
                                    || e.getBlock().getType() == Material.BIRCH_LEAVES
                                    || e.getBlock().getType() == Material.DARK_OAK_LEAVES
                                    || e.getBlock().getType() == Material.JUNGLE_LEAVES
                                    || e.getBlock().getType() == Material.OAK_LEAVES
                                    || e.getBlock().getType() == Material.SPRUCE_LEAVES
                                    || e.getBlock().getType() == Material.WARPED_FUNGUS
                                    || e.getBlock().getType() == Material.CRIMSON_FUNGUS
                                    || e.getBlock().getType() == Material.BROWN_MUSHROOM
                                    || e.getBlock().getType() == Material.RED_MUSHROOM) {
                                pg.getBreakedBlocks().put(e.getBlock().getLocation(), e.getBlock().getType());
                                e.setCancelled(false);
                            } else {
                                e.setCancelled(true);
                            }
                        } else {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (pg.isGameServer()) {
            Player p = e.getEntity();
            if (pg.pgPlayers.contains(p) || pg.playerLobby.containsKey(p)) {
                if (pg.isLobbySystem()) {
                    String s = null;
                    for (int ii = 1; ii <= 1000; ii++) {
                        if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                            s = Integer.toString(ii);
                        }
                    }
                    if (!pg.lobbyBuild.get(s)) {
                        if (pg.lobbyStates.get(s) == GameStates.INGAME) {
                            if (p.getKiller() != null) {
                                pg.addDeaths(p.getUniqueId().toString(), 1);
                                pg.addLosts(p.getUniqueId().toString(), 1);
                                pg.addKills(p.getKiller().getUniqueId().toString(), 1);
                            } else {
                                pg.addDeaths(p.getUniqueId().toString(), 1);
                                pg.addLosts(p.getUniqueId().toString(), 1);
                            }
                            if (pg.playerLobby.containsKey(p)) {
                                e.setKeepLevel(true);
                                pg.playerLobby.remove(p);
                                pg.specLobby.put(p, s);
                                if (pg.lobbyActivateTeams.get(s)) {
                                    String teamname = null;
                                    for (int i = 1; i <= pg.lobbyteamAmount.get(s); i++) {
                                        if (pg.lobbyteamplayernames.get(s).containsKey(Integer.toString(i)) && pg.lobbyteamplayernames.get(s).containsValue(p)) {
                                            if (pg.lobbyteamplayernames.get(s).get(Integer.toString(i)) == p) {
                                                teamname = Integer.toString(i);
                                            }
                                        }
                                    }
                                    pg.lobbyteamplayernames.get(s).remove(teamname, p);
                                    assert teamname != null;
                                    int teamamount = pg.lobbyteams.get(s).get(Integer.valueOf(teamname));
                                    teamamount--;
                                    pg.lobbyteams.get(s).replace(Integer.valueOf(teamname), teamamount);
                                    if (pg.lobbyteams.get(s).get(Integer.valueOf(teamname)) == 0) {
                                        pg.lobbyteams.get(s).remove(Integer.valueOf(teamname));
                                    }
                                    pg.teamed.remove(p.getName());
                                }
                                int amountPlayers = pg.lobbyAmount.get(s);
                                int player = 0;
                                for (Player all : pg.playerLobby.keySet()) {
                                    if (pg.playerLobby.get(all).equals(s)) {
                                        player++;
                                    }
                                }
                                try {
                                    Player killer = p.getKiller();
                                    assert killer != null;
                                    killer.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 30 * 20, 0));
                                    if (pg.kitplayernames.containsKey("Rich Kid") && pg.kitplayernames.containsValue(p)) {
                                        for (int i = 0; i < 10; i++) {
                                            killer.getInventory().addItem(pg.getCoin());
                                        }
                                    } else {
                                        for (int i = 0; i < 5; i++) {
                                            killer.getInventory().addItem(pg.getCoin());
                                        }
                                    }
                                    e.setDeathMessage(pg.prefix + ChatColor.DARK_RED + p.getName() + ChatColor.GRAY + " " + pg.chat.get(9) + " " + ChatColor.DARK_GREEN + killer.getName() + " " + ChatColor.GRAY + "[" + ChatColor.AQUA + player + ChatColor.GRAY + "/" + ChatColor.AQUA + amountPlayers + ChatColor.GRAY + "]");
                                } catch (Exception ex) {
                                    e.setDeathMessage(pg.prefix + ChatColor.DARK_RED + p.getName() + ChatColor.GRAY + " " + pg.chat.get(10) + " " + ChatColor.GRAY + "[" + ChatColor.AQUA + player + ChatColor.GRAY + "/" + ChatColor.AQUA + amountPlayers + ChatColor.GRAY + "]");
                                }
                                p.setGameMode(GameMode.SPECTATOR);
                                p.getWorld().strikeLightning(p.getLocation());
                                p.setAllowFlight(true);
                                p.setFlying(true);
                                p.setLevel(0);
                                p.setExp(0);
                                p.setFireTicks(0);
                                p.setHealth(20);
                                p.setFoodLevel(20);
                                p.setCanPickupItems(false);
                                p.setCollidable(false);
                            }
                        }
                    }
                } else {
                    if (!pg.isBuild()) {
                        if (pg.getGamestate() == GameStates.INGAME) {
                            if (p.getKiller() != null) {
                                pg.addDeaths(p.getUniqueId().toString(), 1);
                                pg.addLosts(p.getUniqueId().toString(), 1);
                                pg.addKills(p.getKiller().getUniqueId().toString(), 1);
                            } else {
                                pg.addDeaths(p.getUniqueId().toString(), 1);
                                pg.addLosts(p.getUniqueId().toString(), 1);
                            }
                            if (pg.pgPlayers.contains(p)) {
                                e.setKeepLevel(true);
                                pg.pgPlayers.remove(p);
                                pg.specPlayers.add(p);
                                if (pg.isActivateTeams()) {
                                    String teamname = null;
                                    for (int i = 1; i <= pg.getTeamAmount(); i++) {
                                        if (pg.teamplayernames.containsKey(Integer.toString(i)) && pg.teamplayernames.containsValue(p)) {
                                            teamname = Integer.toString(i);
                                        }
                                    }
                                    pg.teamplayernames.remove(teamname, p);
                                    int teamamount = pg.teamplayers.get(teamname) - 1;
                                    pg.teamplayers.put(teamname, teamamount);
                                    if (pg.teamplayers.get(teamname) == 0) {
                                        pg.teams.remove(teamname);
                                    }
                                }
                                int amountPlayers = pg.getPlayerAmount();
                                int player = pg.pgPlayers.size();
                                try {
                                    Player killer = p.getKiller();
                                    assert killer != null;
                                    killer.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 30 * 20, 0));
                                    if (pg.kitplayernames.containsKey("Rich Kid") && pg.kitplayernames.containsValue(p)) {
                                        for (int i = 0; i < 10; i++) {
                                            killer.getInventory().addItem(pg.getCoin());
                                        }
                                    } else {
                                        for (int i = 0; i < 5; i++) {
                                            killer.getInventory().addItem(pg.getCoin());
                                        }
                                    }
                                    e.setDeathMessage(pg.prefix + ChatColor.DARK_RED + p.getName() + ChatColor.GRAY + " " + pg.chat.get(9) + " " + ChatColor.DARK_GREEN + killer.getName() + " " + ChatColor.GRAY + "[" + ChatColor.AQUA + player + ChatColor.GRAY + "/" + ChatColor.AQUA + amountPlayers + ChatColor.GRAY + "]");
                                } catch (Exception ex) {
                                    e.setDeathMessage(pg.prefix + ChatColor.DARK_RED + p.getName() + ChatColor.GRAY + " " + pg.chat.get(10) + " " + ChatColor.GRAY + "[" + ChatColor.AQUA + player + ChatColor.GRAY + "/" + ChatColor.AQUA + amountPlayers + ChatColor.GRAY + "]");
                                }
                                p.setGameMode(GameMode.SPECTATOR);
                                p.getWorld().strikeLightning(p.getLocation());
                                p.setAllowFlight(true);
                                p.setFlying(true);
                                p.setLevel(0);
                                p.setExp(0);
                                p.setFireTicks(0);
                                p.setHealth(20);
                                p.setFoodLevel(20);
                                p.setCanPickupItems(false);
                                p.setCollidable(false);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (pg.isGameServer()) {
            if (pg.worlds.contains(e.getDamager().getWorld().getName())) {
                e.setCancelled(e.getDamager() instanceof LightningStrike || e.getDamager() instanceof Firework);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (pg.isGameServer()) {
            FileConfiguration arenadata = YamlConfiguration.loadConfiguration(pg.arenadatafile);
            FileConfiguration kitdata = YamlConfiguration.loadConfiguration(pg.kitdatafile);
            FileConfiguration chestdata = YamlConfiguration.loadConfiguration(pg.chestdatafile);
            Player p = e.getPlayer();
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getHand() == EquipmentSlot.HAND) {
                    if (Objects.requireNonNull(e.getClickedBlock()).getType() == Material.SPRUCE_SIGN
                            || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.ACACIA_SIGN
                            || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.BIRCH_SIGN
                            || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.DARK_OAK_SIGN
                            || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.JUNGLE_SIGN
                            || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.OAK_SIGN
                            || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.SPRUCE_WALL_SIGN
                            || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.ACACIA_WALL_SIGN
                            || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.BIRCH_WALL_SIGN
                            || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.DARK_OAK_WALL_SIGN
                            || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.JUNGLE_WALL_SIGN
                            || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.OAK_WALL_SIGN) {
                        Sign sign = (Sign) e.getClickedBlock().getState();
                        String line1 = sign.getLine(0);
                        String line2 = sign.getLine(1);
                        String line3 = sign.getLine(2);
                        if (pg.isLobbySystem()) {
                            if (e.getClickedBlock().getLocation().equals(arenadata.getLocation("pg.lobbies." + line1 + ".sign"))) {
                                if (!pg.playerLobby.containsKey(p) && !pg.specLobby.containsKey(p)) {
                                    pg.onJoinLobby(p, line1);
                                }
                            }
                        } else {
                            if (e.getClickedBlock().getLocation().equals(pg.getConfig().getLocation("pg.Lobby.sign"))) {
                                if (!pg.pgPlayers.contains(p) && !pg.specPlayers.contains(p)) {
                                    pg.onJoin(p);
                                }
                            }
                        }
                        if (line2.matches("PotionGames") && line3.matches("Stats")) {
                            int wins = pg.getWins(p.getUniqueId().toString());
                            int losts = pg.getLosts(p.getUniqueId().toString());
                            int rounds = pg.getRounds(p.getUniqueId().toString());
                            int kills = pg.getKills(p.getUniqueId().toString());
                            int deaths = pg.getDeaths(p.getUniqueId().toString());
                            double kd = pg.getKD(p.getUniqueId().toString());
                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(56) + "--------------");
                            p.sendMessage(pg.prefix + pg.chat.get(65) + ": " + ChatColor.AQUA + rounds);
                            p.sendMessage(pg.prefix + pg.chat.get(57) + ": " + ChatColor.AQUA + wins);
                            p.sendMessage(pg.prefix + pg.chat.get(58) + ": " + ChatColor.AQUA + losts);
                            p.sendMessage(pg.prefix + pg.chat.get(59) + ": " + ChatColor.AQUA + kills);
                            p.sendMessage(pg.prefix + pg.chat.get(60) + ": " + ChatColor.AQUA + deaths);
                            p.sendMessage(pg.prefix + pg.chat.get(61) + ": " + ChatColor.AQUA + kd);
                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(56) + "--------------");
                        }
                    }
                }
            }
            if (pg.pgPlayers.contains(p) || pg.playerLobby.containsKey(p)) {
                if (e.getAction() == Action.PHYSICAL && Objects.requireNonNull(e.getClickedBlock()).getType() == Material.FARMLAND) {
                    e.setCancelled(true);
                }
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (e.getHand() == EquipmentSlot.HAND) {
                        if ((Objects.requireNonNull(e.getClickedBlock())).getType().toString().equals(Objects.requireNonNull(chestdata.get("pg.chestblocks.normal")).toString())) {
                            if (pg.isLobbySystem()) {
                                String s = null;
                                for (int ii = 1; ii <= 1000; ii++) {
                                    if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
                                if (pg.lobbyStates.get(s) == GameStates.INGAME) {
                                    if (!pg.lobbychests.containsKey(e.getClickedBlock().getLocation())) {
                                        Inventory inv;
                                        inv = Bukkit.createInventory(p, 27, pg.prefix);
                                        pg.chestData();
                                        Random rnd = new Random();
                                        int max = 6;
                                        int min = 2;
                                        int diff = max - min;
                                        int tries = rnd.nextInt(diff + 1);
                                        tries += min;
                                        while (tries != 0) {
                                            tries--;
                                            int slot = rnd.nextInt(27);
                                            int roll = rnd.nextInt(100);
                                            if (roll < 20) {
                                                if (pg.isActivateShop()) {
                                                    ArrayList<ItemStack> potions1 = new ArrayList<>();
                                                    potions1.add(new ItemStack(Material.GLASS_BOTTLE, 1));
                                                    ArrayList<ItemStack> potions2 = new ArrayList<>();
                                                    potions2.add(pg.getCoin());
                                                    int item = rnd.nextInt(5);
                                                    if (item < 3) {
                                                        int item1 = rnd.nextInt(pg.food1.size());
                                                        inv.setItem(slot, pg.food1.get(item1));
                                                    } else if (item < 4) {
                                                        int item1 = rnd.nextInt(potions1.size());
                                                        inv.setItem(slot, potions1.get(item1));
                                                    } else {
                                                        int item1 = rnd.nextInt(potions2.size());
                                                        inv.setItem(slot, potions2.get(item1));
                                                    }
                                                } else {
                                                    int item1 = rnd.nextInt(pg.food1.size());
                                                    inv.setItem(slot, pg.food1.get(item1));
                                                }
                                            } else if (roll < 30) {
                                                int item2 = rnd.nextInt(pg.food2.size());
                                                inv.setItem(slot, pg.food2.get(item2));
                                            } else if (roll < 45) {
                                                int item3 = rnd.nextInt(pg.armour1.size());
                                                inv.setItem(slot, pg.armour1.get(item3));
                                            } else if (roll < 60) {
                                                int item4 = rnd.nextInt(pg.armour2.size());
                                                inv.setItem(slot, pg.armour2.get(item4));
                                            } else if (roll < 67) {
                                                int item5 = rnd.nextInt(pg.armour3.size());
                                                inv.setItem(slot, pg.armour3.get(item5));
                                            } else if (roll < 72) {
                                                int item6 = rnd.nextInt(pg.armour4.size());
                                                inv.setItem(slot, pg.armour4.get(item6));
                                            } else if (roll < 75) {
                                                int item7 = rnd.nextInt(pg.armour5.size());
                                                inv.setItem(slot, pg.armour5.get(item7));
                                            } else if (roll < 95) {
                                                int item8 = rnd.nextInt(pg.weapons1.size());
                                                inv.setItem(slot, pg.weapons1.get(item8));
                                            } else {
                                                int item9 = rnd.nextInt(pg.weapons2.size());
                                                inv.setItem(slot, pg.weapons2.get(item9));
                                            }
                                        }
                                        pg.lobbychests.put(e.getClickedBlock().getLocation(), s);
                                        pg.lobbychestsdata.put(e.getClickedBlock().getLocation(), inv);
                                    }
                                    p.openInventory(pg.lobbychestsdata.get(e.getClickedBlock().getLocation()));
                                    if (p.getActivePotionEffects().isEmpty()) {
                                        Random effect = new Random();
                                        int max = 3;
                                        int min = 0;
                                        int diff = max - min;
                                        int tries = effect.nextInt(diff + 1);
                                        while (tries != 0) {
                                            tries--;
                                            int potion = effect.nextInt(pg.potions.size());
                                            p.addPotionEffect(pg.potions.get(potion));
                                        }
                                    }
                                }
                            } else {
                                if (pg.getGamestate() == GameStates.INGAME) {
                                    if (!pg.chests.containsKey(e.getClickedBlock().getLocation())) {
                                        Inventory inv;
                                        inv = Bukkit.createInventory(p, 27, pg.prefix);
                                        pg.chestData();
                                        Random rnd = new Random();
                                        int max = 6;
                                        int min = 2;
                                        int diff = max - min;
                                        int tries = rnd.nextInt(diff + 1);
                                        tries += min;
                                        while (tries != 0) {
                                            tries--;
                                            int slot = rnd.nextInt(27);
                                            int roll = rnd.nextInt(100);
                                            if (roll < 20) {
                                                if (pg.isActivateShop()) {
                                                    ArrayList<ItemStack> potions1 = new ArrayList<>();
                                                    potions1.add(new ItemStack(Material.GLASS_BOTTLE, 1));
                                                    ArrayList<ItemStack> potions2 = new ArrayList<>();
                                                    potions2.add(pg.getCoin());
                                                    int item = rnd.nextInt(5);
                                                    if (item < 3) {
                                                        int item1 = rnd.nextInt(pg.food1.size());
                                                        inv.setItem(slot, pg.food1.get(item1));
                                                    } else if (item < 4) {
                                                        int item1 = rnd.nextInt(potions1.size());
                                                        inv.setItem(slot, potions1.get(item1));
                                                    } else {
                                                        int item1 = rnd.nextInt(potions2.size());
                                                        inv.setItem(slot, potions2.get(item1));
                                                    }
                                                } else {
                                                    int item1 = rnd.nextInt(pg.food1.size());
                                                    inv.setItem(slot, pg.food1.get(item1));
                                                }
                                            } else if (roll < 30) {
                                                int item2 = rnd.nextInt(pg.food2.size());
                                                inv.setItem(slot, pg.food2.get(item2));
                                            } else if (roll < 45) {
                                                int item3 = rnd.nextInt(pg.armour1.size());
                                                inv.setItem(slot, pg.armour1.get(item3));
                                            } else if (roll < 60) {
                                                int item4 = rnd.nextInt(pg.armour2.size());
                                                inv.setItem(slot, pg.armour2.get(item4));
                                            } else if (roll < 67) {
                                                int item5 = rnd.nextInt(pg.armour3.size());
                                                inv.setItem(slot, pg.armour3.get(item5));
                                            } else if (roll < 72) {
                                                int item6 = rnd.nextInt(pg.armour4.size());
                                                inv.setItem(slot, pg.armour4.get(item6));
                                            } else if (roll < 75) {
                                                int item7 = rnd.nextInt(pg.armour5.size());
                                                inv.setItem(slot, pg.armour5.get(item7));
                                            } else if (roll < 95) {
                                                int item8 = rnd.nextInt(pg.weapons1.size());
                                                inv.setItem(slot, pg.weapons1.get(item8));
                                            } else {
                                                int item9 = rnd.nextInt(pg.weapons2.size());
                                                inv.setItem(slot, pg.weapons2.get(item9));
                                            }
                                        }
                                        pg.chests.put(e.getClickedBlock().getLocation(), inv);
                                    }
                                    p.openInventory(pg.chests.get(e.getClickedBlock().getLocation()));
                                    if (p.getActivePotionEffects().isEmpty()) {
                                        Random effect = new Random();
                                        int max = 3;
                                        int min = 0;
                                        int diff = max - min;
                                        int tries = effect.nextInt(diff + 1);
                                        while (tries != 0) {
                                            tries--;
                                            int potion = effect.nextInt(pg.potions.size());
                                            p.addPotionEffect(pg.potions.get(potion));
                                        }
                                    }
                                }
                            }
                        }
                        int chestnumber = 1;
                        int chestitem = 1;
                        while (chestdata.contains("pg.customchests." + chestnumber)) {
                            if (e.getClickedBlock().getType().toString().equals(Objects.requireNonNull(chestdata.get("pg.customchests." + chestnumber + ".chesttype")).toString())) {
                                if (chestdata.getBoolean("pg.customchests." + chestnumber + ".activate")) {
                                    if (pg.isLobbySystem()) {
                                        int i = 1;
                                        String s = Integer.toString(i);
                                        if (pg.lobbyStates.get(s) == GameStates.INGAME) {
                                            if (!pg.lobbychests.containsKey(e.getClickedBlock().getLocation())) {
                                                Inventory inv;
                                                inv = Bukkit.createInventory(p, chestdata.getInt("pg.customchests." + chestnumber + "." + ".chestsize"), pg.prefix);
                                                pg.lobbychests.put(e.getClickedBlock().getLocation(), s);
                                                pg.lobbychestsdata.put(e.getClickedBlock().getLocation(), inv);
                                                p.openInventory(pg.lobbychestsdata.get(e.getClickedBlock().getLocation()));
                                                while (chestdata.contains("pg.customchests." + chestnumber + "." + chestitem)) {
                                                    inv.setItem(chestdata.getInt("pg.customchests." + chestnumber + "." + chestitem + ".slot") - 1, chestdata.getItemStack("pg.customchests." + chestnumber + "." + chestitem + ".item"));
                                                    chestitem++;
                                                }
                                            } else {
                                                p.openInventory(pg.lobbychestsdata.get(e.getClickedBlock().getLocation()));
                                            }
                                        }
                                    } else {
                                        if (pg.getGamestate() == GameStates.INGAME) {
                                            if (!pg.chests.containsKey(e.getClickedBlock().getLocation())) {
                                                Inventory inv;
                                                inv = Bukkit.createInventory(p, chestdata.getInt("pg.customchests." + chestnumber + "." + ".chestsize"), pg.prefix);
                                                pg.chests.put(e.getClickedBlock().getLocation(), inv);
                                                p.openInventory(pg.chests.get(e.getClickedBlock().getLocation()));
                                                while (chestdata.contains("pg.customchests." + chestnumber + "." + chestitem)) {
                                                    inv.setItem(chestdata.getInt("pg.customchests." + chestnumber + "." + chestitem + ".slot") - 1, chestdata.getItemStack("pg.customchests." + chestnumber + "." + chestitem + ".item"));
                                                    chestitem++;
                                                }
                                            } else {
                                                p.openInventory(pg.chests.get(e.getClickedBlock().getLocation()));
                                            }
                                        }
                                    }
                                }
                            }
                            chestnumber++;
                        }
                        if (pg.isActivateShop()) {
                            if ((e.getClickedBlock()).getType().toString().equals(Objects.requireNonNull(chestdata.get("pg.chestblocks.shop")).toString())) {
                                if (pg.isLobbySystem()) {
                                    int ii = 1;
                                    String s = Integer.toString(ii);
                                    if (pg.lobbyStates.get(s) == GameStates.INGAME) {
                                        for (ItemStack item : p.getInventory().getContents()) {
                                            if (item != null) {
                                                if (item.getType() == pg.getCoin().getType()) {
                                                    amount = item.getAmount();
                                                }
                                                if (item.getType() == Material.GLASS_BOTTLE) {
                                                    bottle = item.getAmount();
                                                }
                                            }
                                        }
                                        Inventory inv;
                                        inv = Bukkit.createInventory(p, 9 * 3, pg.prefix + ChatColor.DARK_AQUA + pg.chat.get(49));
                                        pg.lobbychests.put(e.getClickedBlock().getLocation(), s);
                                        pg.lobbychestsdata.put(e.getClickedBlock().getLocation(), inv);
                                        int shopitem = 1;
                                        for (int i = 0; i < pg.getActivePotions(); i++) {
                                            int coinamount;
                                            if (pg.kitplayernames.containsKey(pg.shopkit.get(shopitem - 1)) && pg.kitplayernames.containsValue(p)) {
                                                coinamount = pg.shopsale.get(shopitem - 1);
                                            } else {
                                                coinamount = pg.shopcost.get(shopitem - 1);
                                            }
                                            ItemStack randombarrier = new ItemStack(pg.shoppotiontype.get(shopitem - 1));
                                            ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                            assert randombarriermeta != null;
                                            randombarriermeta.setDisplayName(pg.shop.get(shopitem - 1));
                                            ArrayList<String> lore = new ArrayList<>();
                                            lore.add(pg.chat.get(50) + ": " + pg.shoppotion.get(shopitem - 1).getDuration() / 20);
                                            lore.add(pg.chat.get(51) + ": " + coinamount + " " + pg.chat.get(52));
                                            randombarriermeta.setLore(lore);
                                            randombarrier.setItemMeta(randombarriermeta);
                                            inv.setItem(shopitem - 1, randombarrier);
                                            shopitem++;
                                        }
                                    }
                                    p.openInventory(pg.lobbychestsdata.get(e.getClickedBlock().getLocation()));
                                } else {
                                    if (pg.getGamestate() == GameStates.INGAME) {
                                        for (ItemStack item : p.getInventory().getContents()) {
                                            if (item != null) {
                                                if (item.getType() == pg.getCoin().getType()) {
                                                    amount = item.getAmount();
                                                }
                                                if (item.getType() == Material.GLASS_BOTTLE) {
                                                    bottle = item.getAmount();
                                                }
                                            }
                                        }
                                        Inventory inv;
                                        inv = Bukkit.createInventory(p, 9 * 3, pg.prefix + ChatColor.DARK_AQUA + pg.chat.get(49));
                                        pg.chests.put(e.getClickedBlock().getLocation(), inv);
                                        int shopitem = 1;
                                        for (int i = 0; i < pg.getActivePotions(); i++) {
                                            int coinamount;
                                            if (pg.kitplayernames.containsKey(pg.shopkit.get(shopitem - 1)) && pg.kitplayernames.containsValue(p)) {
                                                coinamount = pg.shopsale.get(shopitem - 1);
                                            } else {
                                                coinamount = pg.shopcost.get(shopitem - 1);
                                            }
                                            ItemStack randombarrier = new ItemStack(pg.shoppotiontype.get(shopitem - 1));
                                            ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                            assert randombarriermeta != null;
                                            randombarriermeta.setDisplayName(pg.shop.get(shopitem - 1));
                                            ArrayList<String> lore = new ArrayList<>();
                                            lore.add(pg.chat.get(50) + ": " + pg.shoppotion.get(shopitem - 1).getDuration() / 20);
                                            lore.add(pg.chat.get(51) + ": " + coinamount + " " + pg.chat.get(52));
                                            randombarriermeta.setLore(lore);
                                            randombarrier.setItemMeta(randombarriermeta);
                                            inv.setItem(shopitem - 1, randombarrier);
                                            shopitem++;
                                        }
                                    }
                                    p.openInventory(pg.chests.get(e.getClickedBlock().getLocation()));
                                }
                            }
                        }
                        if (pg.isLobbySystem()) {
                            String s = null;
                            for (int ii = 1; ii <= 1000; ii++) {
                                if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                                    s = Integer.toString(ii);
                                }
                            }
                            if (e.getClickedBlock().getBlockData() instanceof Waterlogged) {
                                pg.lobbyWaterBlocksData.put(e.getClickedBlock().getLocation(), e.getClickedBlock().getBlockData());
                                pg.lobbyWaterBlocks.put(s, pg.lobbyWaterBlocksData);
                            }
                        } else {
                            if (e.getClickedBlock().getBlockData() instanceof Waterlogged) {
                                pg.getWaterBlocks().put(e.getClickedBlock().getLocation(), e.getClickedBlock().getBlockData());
                            }
                        }
                    }
                }
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                    if (e.getHand() == EquipmentSlot.HAND) {
                        if (p.getInventory().getItemInMainHand().getType() == Material.MUSHROOM_STEW || p.getInventory().getItemInMainHand().getType() == Material.RABBIT_STEW || p.getInventory().getItemInMainHand().getType() == Material.BEETROOT_SOUP) {
                            if (pg.isLobbySystem()) {
                                String s = null;
                                for (int ii = 1; ii <= 1000; ii++) {
                                    if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
                                if (pg.lobbyStates.get(s) == GameStates.INGAME) {
                                    double health = p.getHealth();
                                    int foodlvl = p.getFoodLevel();
                                    if (health == 20 && foodlvl >= 13) {
                                        p.setFoodLevel(20);
                                        p.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                                    } else if (health == 20 && foodlvl < 13) {
                                        p.setFoodLevel(foodlvl + 7);
                                        p.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                                    } else {
                                        if (health < 20 && health >= 13) {
                                            p.setHealth(20);
                                            p.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                                        } else if (health < 20 && health < 13) {
                                            p.setHealth(health + 7);
                                            p.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                                        }
                                    }
                                }
                            } else {
                                if (pg.getGamestate() == GameStates.INGAME) {
                                    double health = p.getHealth();
                                    int foodlvl = p.getFoodLevel();
                                    if (health == 20 && foodlvl >= 13) {
                                        p.setFoodLevel(20);
                                        p.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                                    } else if (health == 20 && foodlvl < 13) {
                                        p.setFoodLevel(foodlvl + 7);
                                        p.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                                    } else {
                                        if (health < 20 && health >= 13) {
                                            p.setHealth(20);
                                            p.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                                        } else if (health < 20 && health < 13) {
                                            p.setHealth(health + 7);
                                            p.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                                        }
                                    }
                                }
                            }
                        }
                        if (p.getInventory().getItemInMainHand().getType() == Material.MILK_BUCKET) {
                            if (pg.isLobbySystem()) {
                                int i = 1;
                                String s = Integer.toString(i);
                                if (pg.lobbyStates.get(s) == GameStates.INGAME) {
                                    pg.clearEffects(p);
                                    p.getInventory().setItemInMainHand(new ItemStack(Material.BUCKET));
                                }
                            } else {
                                if (pg.getGamestate() == GameStates.INGAME) {
                                    pg.clearEffects(p);
                                    p.getInventory().setItemInMainHand(new ItemStack(Material.BUCKET));
                                }
                            }
                        }
                        if (p.getInventory().getItemInMainHand().getType() == Material.COMPASS) {
                            if (pg.isLobbySystem()) {
                                int i = 1;
                                String s = Integer.toString(i);
                                if (pg.lobbyStates.get(s) == GameStates.INGAME) {
                                    Player result = null;
                                    double lastDistance = Double.MAX_VALUE;
                                    for (Player cp : p.getWorld().getPlayers()) {
                                        if (pg.playerLobby.containsKey(cp)) {
                                            if (p == cp) {
                                                continue;
                                            }
                                            double distance = p.getLocation().distance(cp.getLocation());
                                            if (distance < lastDistance) {
                                                lastDistance = distance;
                                                result = cp;
                                            }
                                        } else {
                                            result = null;
                                        }
                                    }
                                    if (result != null) {
                                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(pg.prefix + ChatColor.GREEN + pg.chat.get(12) + ": " + ChatColor.AQUA + (int) lastDistance));
                                    } else {
                                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(pg.prefix + ChatColor.RED + pg.chat.get(13)));
                                    }
                                }
                            } else {
                                if (pg.getGamestate() == GameStates.INGAME) {
                                    Player result = null;
                                    double lastDistance = Double.MAX_VALUE;
                                    for (Player cp : p.getWorld().getPlayers()) {
                                        if (pg.getPgPlayers().contains(cp)) {
                                            if (p == cp) {
                                                continue;
                                            }
                                            double distance = p.getLocation().distance(cp.getLocation());
                                            if (distance < lastDistance) {
                                                lastDistance = distance;
                                                result = cp;
                                            }
                                        } else {
                                            result = null;
                                        }
                                    }
                                    if (result != null) {
                                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(pg.prefix + ChatColor.GREEN + pg.chat.get(12) + ": " + ChatColor.AQUA + (int) lastDistance));
                                    } else {
                                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(pg.prefix + ChatColor.RED + pg.chat.get(13)));
                                    }
                                }
                            }
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.PAPER) {
                        if (pg.isLobbySystem()) {
                            String s = null;
                            for (int ii = 1; ii <= 1000; ii++) {
                                if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                                    s = Integer.toString(ii);
                                }
                            }
                            if (pg.lobbyStates.get(s) == GameStates.WAITING || pg.lobbyStates.get(s) == GameStates.PREPARING) {
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, pg.prefix + ChatColor.DARK_AQUA + pg.chat.get(14));
                                ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
                                ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                assert randombarriermeta != null;
                                randombarriermeta.setDisplayName(pg.chat.get(42));
                                ArrayList<String> randomlore = new ArrayList<>();
                                randomlore.add(0, ChatColor.GREEN + pg.chat.get(15) + ": " + ChatColor.AQUA + pg.lobbyvotes.get(s).get(pg.chat.get(42)));
                                randombarriermeta.setLore(randomlore);
                                randombarrier.setItemMeta(randombarriermeta);
                                inv.setItem(0, randombarrier);
                                int slot = 1;
                                for (String all : pg.lobbyvotes.get(s).keySet()) {
                                    if (!all.equals(pg.chat.get(42))) {
                                        ArrayList<String> arenalore = new ArrayList<>();
                                        arenalore.add(0, ChatColor.GREEN + pg.chat.get(15) + ": " + ChatColor.AQUA + pg.lobbyvotes.get(s).get(all));
                                        ItemStack arenamap = new ItemStack(Material.MAP);
                                        ItemMeta arenamapmeta = arenamap.getItemMeta();
                                        assert arenamapmeta != null;
                                        arenamapmeta.setDisplayName(all);
                                        arenamapmeta.setLore(arenalore);
                                        arenamap.setItemMeta(arenamapmeta);
                                        inv.setItem(slot, arenamap);
                                        slot++;
                                    }
                                }
                                p.openInventory(inv);
                            }
                        } else {
                            if (pg.getGamestate() == GameStates.WAITING || pg.getGamestate() == GameStates.PREPARING) {
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, pg.prefix + ChatColor.DARK_AQUA + pg.chat.get(14));
                                ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
                                ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                assert randombarriermeta != null;
                                randombarriermeta.setDisplayName(pg.chat.get(42));
                                ArrayList<String> randomlore = new ArrayList<>();
                                randomlore.add(0, ChatColor.GREEN + pg.chat.get(15) + ": " + ChatColor.AQUA + pg.votes.get(pg.chat.get(42)));
                                randombarriermeta.setLore(randomlore);
                                randombarrier.setItemMeta(randombarriermeta);
                                inv.setItem(0, randombarrier);
                                int slot = 1;
                                for (String all : pg.arenas) {
                                    if (!all.equals(pg.chat.get(42))) {
                                        ArrayList<String> arenalore = new ArrayList<>();
                                        arenalore.add(0, ChatColor.GREEN + pg.chat.get(15) + ": " + ChatColor.AQUA + pg.votes.get(all).toString());
                                        ItemStack arenamap = new ItemStack(Material.MAP);
                                        ItemMeta arenamapmeta = arenamap.getItemMeta();
                                        assert arenamapmeta != null;
                                        arenamapmeta.setDisplayName(all);
                                        arenamapmeta.setLore(arenalore);
                                        arenamap.setItemMeta(arenamapmeta);
                                        inv.setItem(slot, arenamap);
                                        slot++;
                                    }
                                }
                                p.openInventory(inv);
                            }
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.CLOCK) {
                        if (pg.isLobbySystem()) {
                            String s = null;
                            for (int ii = 1; ii <= 1000; ii++) {
                                if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                                    s = Integer.toString(ii);
                                }
                            }
                            if (pg.lobbyStates.get(s) == GameStates.WAITING || pg.lobbyStates.get(s) == GameStates.PREPARING) {
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, pg.prefix + ChatColor.DARK_AQUA + pg.chat.get(43));
                                ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
                                ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                assert randombarriermeta != null;
                                randombarriermeta.setDisplayName(pg.chat.get(42));
                                randombarrier.setItemMeta(randombarriermeta);
                                inv.setItem(0, randombarrier);
                                int slot = 1;
                                for (Integer all : pg.lobbyteams.get(s).keySet()) {
                                    ArrayList<String> arenalore = new ArrayList<>();
                                    arenalore.add(0, ChatColor.GREEN + pg.chat.get(44) + ": " + ChatColor.AQUA + pg.lobbyteams.get(s).get(all));
                                    ItemStack arenamap = new ItemStack(Material.PLAYER_HEAD);
                                    ItemMeta arenamapmeta = arenamap.getItemMeta();
                                    assert arenamapmeta != null;
                                    arenamapmeta.setDisplayName(String.valueOf(all));
                                    arenamapmeta.setLore(arenalore);
                                    arenamap.setItemMeta(arenamapmeta);
                                    inv.setItem(slot, arenamap);
                                    slot++;
                                }
                                p.openInventory(inv);
                            }
                        } else {
                            if (pg.getGamestate() == GameStates.WAITING || pg.getGamestate() == GameStates.PREPARING) {
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, pg.prefix + ChatColor.DARK_AQUA + pg.chat.get(43));
                                ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
                                ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                assert randombarriermeta != null;
                                randombarriermeta.setDisplayName(pg.chat.get(42));
                                randombarrier.setItemMeta(randombarriermeta);
                                inv.setItem(0, randombarrier);
                                int slot = 1;
                                for (String all : pg.teams) {
                                    ArrayList<String> arenalore = new ArrayList<>();
                                    arenalore.add(0, ChatColor.GREEN + pg.chat.get(44) + ": " + ChatColor.AQUA + pg.teamplayers.get(all).toString());
                                    ItemStack arenamap = new ItemStack(Material.PLAYER_HEAD);
                                    ItemMeta arenamapmeta = arenamap.getItemMeta();
                                    assert arenamapmeta != null;
                                    arenamapmeta.setDisplayName(all);
                                    arenamapmeta.setLore(arenalore);
                                    arenamap.setItemMeta(arenamapmeta);
                                    inv.setItem(slot, arenamap);
                                    slot++;
                                }
                                p.openInventory(inv);
                            }
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.ENDER_CHEST) {
                        if (pg.isLobbySystem()) {
                            int ii = 1;
                            String s = Integer.toString(ii);
                            if (pg.lobbyStates.get(s) == GameStates.WAITING || pg.lobbyStates.get(s) == GameStates.PREPARING) {
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, pg.prefix + ChatColor.DARK_AQUA + pg.chat.get(62));
                                ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
                                ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                assert randombarriermeta != null;
                                randombarriermeta.setDisplayName(pg.chat.get(42));
                                randombarrier.setItemMeta(randombarriermeta);
                                inv.setItem(0, randombarrier);
                                for (int i = 1; i <= pg.getActiveKits(); i++) {
                                    ItemStack arenamap = new ItemStack(Material.ARMOR_STAND);
                                    ItemMeta arenamapmeta = arenamap.getItemMeta();
                                    assert arenamapmeta != null;
                                    arenamapmeta.setDisplayName(kitdata.getString("pg.kits." + i + ".name"));
                                    arenamap.setItemMeta(arenamapmeta);
                                    inv.setItem(i, arenamap);
                                }
                                p.openInventory(inv);
                            }
                        } else {
                            if (pg.getGamestate() == GameStates.WAITING || pg.getGamestate() == GameStates.PREPARING) {
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, pg.prefix + ChatColor.DARK_AQUA + pg.chat.get(62));
                                ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
                                ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                assert randombarriermeta != null;
                                randombarriermeta.setDisplayName(pg.chat.get(42));
                                randombarrier.setItemMeta(randombarriermeta);
                                inv.setItem(0, randombarrier);
                                for (int i = 1; i <= pg.getActiveKits(); i++) {
                                    ItemStack arenamap = new ItemStack(Material.ARMOR_STAND);
                                    ItemMeta arenamapmeta = arenamap.getItemMeta();
                                    assert arenamapmeta != null;
                                    arenamapmeta.setDisplayName(kitdata.getString("pg.kits." + i + ".name"));
                                    arenamap.setItemMeta(arenamapmeta);
                                    inv.setItem(i, arenamap);
                                }
                                p.openInventory(inv);
                            }
                        }
                    }
                }
            }
            if (e.getAction().equals(Action.LEFT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_AIR)) {
                if (e.getHand() == EquipmentSlot.HAND) {
                    if (p.getInventory().getItemInMainHand().getType() == Material.STICK) {
                        if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).getDisplayName().equals(ChatColor.DARK_AQUA + "Add(Left)/Del(Right) Lobby")) {
                            if (p.hasPermission("pg.setup")) {
                                if (!pg.isLobbySystem()) {
                                    pg.getConfig().set("pg.Lobby.world", Objects.requireNonNull(p.getLocation().getWorld()).getName());
                                    pg.getConfig().set("pg.Lobby.coords", Objects.requireNonNull(p.getLocation()));
                                    pg.saveConfig();
                                    p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(24));
                                }
                            }
                            if (p.hasPermission("pg.setup")) {
                                if (pg.isLobbySystem()) {
                                    p.getInventory().clear();
                                    pg.setAddlobby(true);
                                    e.setCancelled(true);
                                    p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(69));
                                }
                            }
                        } else if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.DARK_AQUA + "Add(Left)/Del(Right) Arena")) {
                            p.getInventory().clear();
                            pg.setAddarena(true);
                            p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(70));
                        } else if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.DARK_AQUA + "Add(Left)/Del(Right) Spawn")) {
                            if (p.hasPermission("pg.setup")) {
                                if (!pg.isLobbySystem()) {
                                    int spawnNumber = 1;
                                    int arenaNumber = 1;
                                    try {
                                        int i = 1;
                                        boolean arenaName = false;
                                        while (!arenaName) {
                                            if (arena.matches(Objects.requireNonNull(arenadata.getString("pg.arenas." + i + ".name")))) {
                                                arenaNumber = i;
                                                arenaName = true;
                                            } else {
                                                i++;
                                            }
                                        }
                                        while (arenadata.contains("pg.arenas." + arenaNumber + ".spawns." + spawnNumber)) {
                                            spawnNumber++;
                                        }
                                        arenadata.set("pg.arenas." + arenaNumber + ".spawns." + spawnNumber, p.getLocation());
                                        arenadata.save(pg.arenadatafile);
                                        p.sendMessage(pg.prefix + ChatColor.AQUA + spawnNumber + ChatColor.GREEN + " " + pg.chat.get(30));
                                    } catch (Exception ex) {
                                        p.sendMessage(pg.prefix + ChatColor.AQUA + spawnNumber + ChatColor.RED + " " + pg.chat.get(31));
                                    }
                                }
                            }
                            if (p.hasPermission("pg.setup")) {
                                if (pg.isLobbySystem()) {
                                    int spawnNumber = 1;
                                    int arenaNumber = 1;
                                    try {
                                        int i = 1;
                                        boolean arenaName = false;
                                        while (!arenaName) {
                                            if (arena.matches(Objects.requireNonNull(arenadata.getString("pg.lobbies." + lobby + "." + i + ".name")))) {
                                                arenaNumber = i;
                                                arenaName = true;
                                            } else {
                                                i++;
                                            }
                                        }
                                        while (arenadata.contains("pg.lobbies." + lobby + "." + arenaNumber + ".spawns." + spawnNumber)) {
                                            spawnNumber++;
                                        }
                                        arenadata.set("pg.lobbies." + lobby + "." + arenaNumber + ".spawns." + spawnNumber, p.getLocation());
                                        arenadata.save(pg.arenadatafile);
                                        p.sendMessage(pg.prefix + ChatColor.AQUA + spawnNumber + ChatColor.GREEN + " " + pg.chat.get(30) + ChatColor.GRAY + " (" + "Lobby: " + lobby + ")" + " (" + "Arena: " + arena + ")");
                                    } catch (Exception ex) {
                                        p.sendMessage(pg.prefix + ChatColor.AQUA + lobby + ChatColor.RED + " " + pg.chat.get(31) + ChatColor.GRAY + " (" + "Lobby: " + lobby + ")" + " (" + "Arena: " + arena + ")");
                                    }
                                }
                            }
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.CLOCK) {
                        if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).getDisplayName().equals(ChatColor.DARK_AQUA + "Choose Lobby")) {
                            if (pg.isLobbySystem()) {
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, pg.prefix + ChatColor.DARK_AQUA + "Choose Lobby");
                                for (int slot = 1; slot <= 1000; slot++) {
                                    if (arenadata.contains("pg.lobbies." + slot)) {
                                        ArrayList<String> arenalore = new ArrayList<>();
                                        ItemStack arenamap = new ItemStack(Material.MAP);
                                        ItemMeta arenamapmeta = arenamap.getItemMeta();
                                        assert arenamapmeta != null;
                                        arenamapmeta.setDisplayName(String.valueOf(slot));
                                        arenamapmeta.setLore(arenalore);
                                        arenamap.setItemMeta(arenamapmeta);
                                        inv.setItem(slot - 1, arenamap);
                                    }
                                }
                                p.openInventory(inv);
                            }
                        } else if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.DARK_AQUA + "Choose Arena")) {
                            Inventory inv = Bukkit.createInventory(null, 9 * 3, pg.prefix + ChatColor.DARK_AQUA + "Choose Arena");
                            if (pg.isLobbySystem()) {
                                for (int slot = 1; slot <= 1000; slot++) {
                                    if (arenadata.contains("pg.lobbies." + lobby + "." + slot)) {
                                        ArrayList<String> arenalore = new ArrayList<>();
                                        ItemStack arenamap = new ItemStack(Material.MAP);
                                        ItemMeta arenamapmeta = arenamap.getItemMeta();
                                        assert arenamapmeta != null;
                                        arenamapmeta.setDisplayName(arenadata.getString("pg.lobbies." + lobby + "." + slot + ".name"));
                                        arenamapmeta.setLore(arenalore);
                                        arenamap.setItemMeta(arenamapmeta);
                                        inv.setItem(slot - 1, arenamap);
                                    }
                                }
                            } else {
                                for (int slot = 1; slot <= 1000; slot++) {
                                    if (arenadata.contains("pg.arenas." + slot)) {
                                        ArrayList<String> arenalore = new ArrayList<>();
                                        ItemStack arenamap = new ItemStack(Material.MAP);
                                        ItemMeta arenamapmeta = arenamap.getItemMeta();
                                        assert arenamapmeta != null;
                                        arenamapmeta.setDisplayName(arenadata.getString("pg.arenas." + slot + ".name"));
                                        arenamapmeta.setLore(arenalore);
                                        arenamap.setItemMeta(arenamapmeta);
                                        inv.setItem(slot - 1, arenamap);
                                    }
                                }
                            }
                            p.openInventory(inv);
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.OAK_SIGN) {
                        if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).getDisplayName().equals(ChatColor.DARK_AQUA + "Set Join-Sign")) {
                            if (p.hasPermission("pg.setup")) {
                                if (!pg.isLobbySystem()) {
                                    pg.getConfig().set("pg.Lobby.sign", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                                    pg.saveConfig();
                                    p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(35));
                                }
                            }
                            if (p.hasPermission("pg.setup")) {
                                if (pg.isLobbySystem()) {
                                    arenadata.set("pg.lobbies." + lobby + ".sign", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                                    try {
                                        arenadata.save(pg.arenadatafile);
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                    p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(35) + ChatColor.GRAY + " (" + "Lobby: " + lobby + ")");
                                }
                            }
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.BARRIER) {
                        if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).getDisplayName().equals(ChatColor.DARK_AQUA + "Leave Setup-Mode")) {
                            p.getInventory().getItemInMainHand().setAmount(0);
                            p.getInventory().setContents(pg.inv.get(p.getName()));
                            p.getInventory().setArmorContents(pg.armor.get(p.getName()));
                            p.teleport(pg.loc.get(p.getName()));
                            p.setLevel(pg.lvl.get(p.getName()));
                            p.setExp(pg.exp.get(p.getName()));
                            p.setGameMode(pg.gm.get(p.getName()));
                            pg.inv.remove(p.getName());
                            pg.armor.remove(p.getName());
                            pg.loc.remove(p.getName());
                            pg.lvl.remove(p.getName());
                            pg.exp.remove(p.getName());
                            pg.gm.remove(p.getName());
                            p.setAllowFlight(false);
                        }
                    }
                }
            }
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                if (e.getHand() == EquipmentSlot.HAND) {
                    if (p.getInventory().getItemInMainHand().getType() == Material.STICK) {
                        if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).getDisplayName().equals(ChatColor.DARK_AQUA + "Add(Left)/Del(Right) Lobby")) {
                            if (pg.isLobbySystem()) {
                                p.getInventory().clear();
                                pg.setDellobby(true);
                                p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(71));
                            }
                        } else if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.DARK_AQUA + "Add(Left)/Del(Right) Arena")) {
                            p.getInventory().clear();
                            pg.setDelarena(true);
                            p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(72));
                        } else if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.DARK_AQUA + "Add(Left)/Del(Right) Spawn")) {
                            if (p.hasPermission("pg.setup")) {
                                if (!pg.isLobbySystem()) {
                                    int arenaNumber = 1;
                                    int spawnNumber = 1;
                                    try {
                                        int i = 1;
                                        boolean arenaName = false;
                                        while (!arenaName) {
                                            if (arena.matches(Objects.requireNonNull(arenadata.getString("pg.arenas." + i + ".name")))) {
                                                arenaNumber = i;
                                                arenaName = true;
                                            } else {
                                                i++;
                                            }
                                        }
                                        int max = 1;
                                        while (arenadata.contains("pg.arenas." + arenaNumber + ".spawns." + max)) {
                                            spawnNumber = max;
                                            max++;
                                        }
                                        arenadata.set("pg.arenas." + arenaNumber + ".spawns." + spawnNumber, null);
                                        arenadata.save(pg.arenadatafile);
                                        p.sendMessage(pg.prefix + ChatColor.AQUA + spawnNumber + ChatColor.GREEN + " " + pg.chat.get(32));
                                    } catch (Exception ex) {
                                        p.sendMessage(pg.prefix + ChatColor.AQUA + arena + ChatColor.RED + " " + pg.chat.get(31));
                                    }
                                }
                            }
                            if (p.hasPermission("pg.setup")) {
                                if (pg.isLobbySystem()) {
                                    int arenaNumber = 1;
                                    int spawnNumber = 1;
                                    try {
                                        int i = 1;
                                        boolean arenaName = false;
                                        while (!arenaName) {
                                            if (arena.matches(Objects.requireNonNull(arenadata.getString("pg.lobbies." + lobby + "." + i + ".name")))) {
                                                arenaNumber = i;
                                                arenaName = true;
                                            } else {
                                                i++;
                                            }
                                        }
                                        int max = 1;
                                        while (arenadata.contains("pg.lobbies." + lobby + "." + arenaNumber + ".spawns." + max)) {
                                            spawnNumber = max;
                                            max++;
                                        }
                                        arenadata.set("pg.lobbies." + lobby + "." + arenaNumber + ".spawns." + spawnNumber, null);
                                        arenadata.save(pg.arenadatafile);
                                        p.sendMessage(pg.prefix + ChatColor.AQUA + spawnNumber + ChatColor.GREEN + " " + pg.chat.get(32) + ChatColor.GRAY + " (" + "Lobby: " + lobby + ")");
                                    } catch (Exception ex) {
                                        p.sendMessage(pg.prefix + ChatColor.AQUA + arena + ChatColor.RED + " " + pg.chat.get(31) + ChatColor.GRAY + " (" + "Lobby: " + lobby + ")");
                                    }
                                }
                            }
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.CLOCK) {
                        if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).getDisplayName().equals(ChatColor.DARK_AQUA + "Choose Lobby")) {
                            if (pg.isLobbySystem()) {
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, pg.prefix + ChatColor.DARK_AQUA + "Choose Lobby");
                                for (int slot = 1; slot <= 1000; slot++) {
                                    if (arenadata.contains("pg.lobbies." + slot)) {
                                        ArrayList<String> arenalore = new ArrayList<>();
                                        ItemStack arenamap = new ItemStack(Material.MAP);
                                        ItemMeta arenamapmeta = arenamap.getItemMeta();
                                        assert arenamapmeta != null;
                                        arenamapmeta.setDisplayName(String.valueOf(slot));
                                        arenamapmeta.setLore(arenalore);
                                        arenamap.setItemMeta(arenamapmeta);
                                        inv.setItem(slot - 1, arenamap);
                                    }
                                }
                                p.openInventory(inv);
                            }
                        } else if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.DARK_AQUA + "Choose Arena")) {
                            Inventory inv = Bukkit.createInventory(null, 9 * 3, pg.prefix + ChatColor.DARK_AQUA + "Choose Arena");
                            if (pg.isLobbySystem()) {
                                for (int slot = 1; slot <= 1000; slot++) {
                                    if (arenadata.contains("pg.lobbies." + lobby + "." + slot)) {
                                        ArrayList<String> arenalore = new ArrayList<>();
                                        ItemStack arenamap = new ItemStack(Material.MAP);
                                        ItemMeta arenamapmeta = arenamap.getItemMeta();
                                        assert arenamapmeta != null;
                                        arenamapmeta.setDisplayName(arenadata.getString("pg.lobbies." + lobby + "." + slot + ".name"));
                                        arenamapmeta.setLore(arenalore);
                                        arenamap.setItemMeta(arenamapmeta);
                                        inv.setItem(slot - 1, arenamap);
                                    }
                                }
                            } else {
                                for (int slot = 1; slot <= 1000; slot++) {
                                    if (arenadata.contains("pg.arenas." + slot)) {
                                        ArrayList<String> arenalore = new ArrayList<>();
                                        ItemStack arenamap = new ItemStack(Material.MAP);
                                        ItemMeta arenamapmeta = arenamap.getItemMeta();
                                        assert arenamapmeta != null;
                                        arenamapmeta.setDisplayName(arenadata.getString("pg.arenas." + slot + ".name"));
                                        arenamapmeta.setLore(arenalore);
                                        arenamap.setItemMeta(arenamapmeta);
                                        inv.setItem(slot - 1, arenamap);
                                    }
                                }
                            }
                            p.openInventory(inv);
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.BARRIER) {
                        if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).getDisplayName().equals(ChatColor.DARK_AQUA + "Leave Setup-Mode")) {
                            p.getInventory().getItemInMainHand().setAmount(0);
                            p.getInventory().setContents(pg.inv.get(p.getName()));
                            p.getInventory().setArmorContents(pg.armor.get(p.getName()));
                            p.teleport(pg.loc.get(p.getName()));
                            p.setLevel(pg.lvl.get(p.getName()));
                            p.setExp(pg.exp.get(p.getName()));
                            p.setGameMode(pg.gm.get(p.getName()));
                            pg.inv.remove(p.getName());
                            pg.armor.remove(p.getName());
                            pg.loc.remove(p.getName());
                            pg.lvl.remove(p.getName());
                            pg.exp.remove(p.getName());
                            pg.gm.remove(p.getName());
                            p.setAllowFlight(false);
                        }
                    }
                }
            }
        } else {
            Player p = e.getPlayer();
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getHand() == EquipmentSlot.HAND) {
                    if (Objects.requireNonNull(e.getClickedBlock()).getType() == Material.SPRUCE_SIGN
                            || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.ACACIA_SIGN
                            || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.BIRCH_SIGN
                            || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.DARK_OAK_SIGN
                            || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.JUNGLE_SIGN
                            || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.OAK_SIGN
                            || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.SPRUCE_WALL_SIGN
                            || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.ACACIA_WALL_SIGN
                            || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.BIRCH_WALL_SIGN
                            || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.DARK_OAK_WALL_SIGN
                            || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.JUNGLE_WALL_SIGN
                            || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.OAK_WALL_SIGN) {
                        Sign sign = (Sign) e.getClickedBlock().getState();
                        String line2 = sign.getLine(1);
                        String line3 = sign.getLine(2);
                        if (line2.matches("PotionGames") && line3.matches("Stats")) {
                            int wins = pg.getWins(p.getUniqueId().toString());
                            int losts = pg.getLosts(p.getUniqueId().toString());
                            int rounds = pg.getRounds(p.getUniqueId().toString());
                            int kills = pg.getKills(p.getUniqueId().toString());
                            int deaths = pg.getDeaths(p.getUniqueId().toString());
                            double kd = pg.getKD(p.getUniqueId().toString());
                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(56) + "--------------");
                            p.sendMessage(pg.prefix + pg.chat.get(65) + ": " + ChatColor.AQUA + rounds);
                            p.sendMessage(pg.prefix + pg.chat.get(57) + ": " + ChatColor.AQUA + wins);
                            p.sendMessage(pg.prefix + pg.chat.get(58) + ": " + ChatColor.AQUA + losts);
                            p.sendMessage(pg.prefix + pg.chat.get(59) + ": " + ChatColor.AQUA + kills);
                            p.sendMessage(pg.prefix + pg.chat.get(60) + ": " + ChatColor.AQUA + deaths);
                            p.sendMessage(pg.prefix + pg.chat.get(61) + ": " + ChatColor.AQUA + kd);
                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(56) + "--------------");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (pg.isGameServer()) {
            FileConfiguration arenadata = YamlConfiguration.loadConfiguration(pg.arenadatafile);
            Player p = (Player) e.getWhoClicked();
            if (pg.pgPlayers.contains(p) || pg.playerLobby.containsKey(p)) {
                if (pg.isLobbySystem()) {
                    String s = null;
                    for (int ii = 1; ii <= 1000; ii++) {
                        if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                            s = Integer.toString(ii);
                        }
                    }
                    if (pg.lobbyStates.get(s) == GameStates.WAITING && !pg.lobbyBuild.get(s) && pg.playerLobby.containsKey(p) || pg.lobbyStates.get(s) == GameStates.PREPARING && !pg.lobbyBuild.get(s) && pg.playerLobby.containsKey(p)) {
                        if (e.getView().getTitle().equalsIgnoreCase(pg.prefix + ChatColor.DARK_AQUA + pg.chat.get(14))) {
                            if (e.getCurrentItem() != null) {
                                int randomvotes;
                                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).hasDisplayName()) {
                                    String displayname = e.getCurrentItem().getItemMeta().getDisplayName();
                                    if (!pg.lobbyVoted.containsValue(p.getName())) {
                                        p.closeInventory();
                                        int votes = pg.lobbyvotes.get(s).get(displayname);
                                        votes++;
                                        HashMap<String, Integer> temp = new HashMap<>();
                                        randomvotes = pg.lobbyvotes.get(s).get(pg.chat.get(42));
                                        temp.put(pg.chat.get(42), randomvotes);
                                        for (int max = 1; max <= 1000; max++) {
                                            if (arenadata.contains("pg.lobbies." + s + "." + max)) {
                                                int oldvotes = pg.lobbyvotes.get(s).get(arenadata.getString("pg.lobbies." + s + "." + max + ".name"));
                                                temp.put(arenadata.getString("pg.lobbies." + s + "." + max + ".name"), oldvotes);
                                            }
                                        }
                                        temp.put(displayname, votes);
                                        pg.lobbyvotes.replace(s, temp);
                                        p.sendMessage(pg.prefix + "--------------" + pg.chat.get(14) + "--------------");
                                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(16) + ": " + ChatColor.LIGHT_PURPLE + displayname);
                                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(15) + ": " + ChatColor.AQUA + pg.lobbyvotes.get(s).get(displayname));
                                        p.sendMessage(pg.prefix + "--------------" + pg.chat.get(14) + "--------------");
                                        pg.lobbyVoted.put(s, p.getName());
                                        pg.lobbyvoteplayernames.get(s).put(displayname, p);
                                    } else {
                                        p.closeInventory();
                                        String arenaname = null;
                                        for (int i = 0; i <= pg.lobbyvotes.get(s).size(); i++) {
                                            for (String all : pg.lobbyvoteplayernames.get(s).keySet()) {
                                                if (pg.lobbyvoteplayernames.get(s).get(all) == p) {
                                                    arenaname = all;
                                                }
                                            }
                                        }
                                        pg.lobbyvoteplayernames.get(s).remove(arenaname, p);
                                        int votes = pg.lobbyvotes.get(s).get(arenaname);
                                        votes--;
                                        HashMap<String, Integer> tempold = new HashMap<>();
                                        randomvotes = pg.lobbyvotes.get(s).get(pg.chat.get(42));
                                        tempold.put(pg.chat.get(42), randomvotes);
                                        for (int max = 1; max <= 1000; max++) {
                                            if (arenadata.contains("pg.lobbies." + s + "." + max)) {
                                                int oldvotes = pg.lobbyvotes.get(s).get(arenadata.getString("pg.lobbies." + s + "." + max + ".name"));
                                                tempold.put(arenadata.getString("pg.lobbies." + s + "." + max + ".name"), oldvotes);
                                            }
                                        }
                                        tempold.put(arenaname, votes);
                                        pg.lobbyvotes.replace(s, tempold);
                                        pg.lobbyVoted.remove(s, p.getName());
                                        votes = pg.lobbyvotes.get(s).get(displayname);
                                        votes++;
                                        HashMap<String, Integer> temp = new HashMap<>();
                                        randomvotes = pg.lobbyvotes.get(s).get(pg.chat.get(42));
                                        temp.put(pg.chat.get(42), randomvotes);
                                        for (int max = 1; max <= 1000; max++) {
                                            if (arenadata.contains("pg.lobbies." + s + "." + max)) {
                                                int oldvotes = pg.lobbyvotes.get(s).get(arenadata.getString("pg.lobbies." + s + "." + max + ".name"));
                                                temp.put(arenadata.getString("pg.lobbies." + s + "." + max + ".name"), oldvotes);
                                            }
                                        }
                                        temp.put(displayname, votes);
                                        pg.lobbyvotes.replace(s, temp);
                                        p.sendMessage(pg.prefix + "--------------" + pg.chat.get(14) + "--------------");
                                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(16) + ": " + ChatColor.LIGHT_PURPLE + displayname);
                                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(15) + ": " + ChatColor.AQUA + pg.lobbyvotes.get(s).get(displayname));
                                        p.sendMessage(pg.prefix + "--------------" + pg.chat.get(14) + "--------------");
                                        pg.lobbyVoted.put(s, p.getName());
                                        pg.lobbyvoteplayernames.get(s).put(displayname, p);
                                    }
                                }
                            }
                        }
                        if (pg.lobbyActivateTeams.get(s)) {
                            if (e.getView().getTitle().equalsIgnoreCase(pg.prefix + ChatColor.DARK_AQUA + pg.chat.get(43))) {
                                if (e.getCurrentItem() != null) {
                                    if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).hasDisplayName()) {
                                        String displayname = e.getCurrentItem().getItemMeta().getDisplayName();
                                        int maxteamplayers = pg.lobbyteamSize.get(s);
                                        if (!pg.lobbyTeamed.containsValue(p.getName())) {
                                            if (displayname.equals(pg.chat.get(42))) {
                                                boolean teamfound = false;
                                                while (!teamfound) {
                                                    Random rnd = new Random();
                                                    int rndTeam = rnd.nextInt(pg.lobbyteams.get(s).size());
                                                    rndTeam++;
                                                    if (pg.lobbyteams.get(s).get(rndTeam) < maxteamplayers && pg.lobbyteams.get(s).get(rndTeam) >= 0 && pg.lobbyteams.get(s).get(rndTeam) != null) {
                                                        teamfound = true;
                                                        p.closeInventory();
                                                        int players = pg.lobbyteams.get(s).get(rndTeam);
                                                        players++;
                                                        HashMap<Integer, Integer> temp = new HashMap<>();
                                                        for (int max = 1; max <= pg.lobbyteamAmount.get(s); max++) {
                                                            int oldplayers = pg.lobbyteams.get(s).get(max);
                                                            temp.put(max, oldplayers);
                                                        }
                                                        temp.put(rndTeam, players);
                                                        pg.lobbyteams.replace(s, temp);
                                                        p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(45) + ": " + ChatColor.LIGHT_PURPLE + rndTeam);
                                                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(44) + ": " + ChatColor.AQUA + pg.lobbyteams.get(s).get(rndTeam) + ChatColor.GRAY + "/" + ChatColor.AQUA + maxteamplayers);
                                                        p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                                        pg.lobbyTeamed.put(s, e.getWhoClicked().getName());
                                                        pg.lobbyteamplayernames.get(s).put(Integer.toString(rndTeam), p);
                                                    }
                                                }
                                            } else {
                                                if (pg.lobbyteams.get(s).get(Integer.valueOf(displayname)) < maxteamplayers) {
                                                    p.closeInventory();
                                                    int players = pg.lobbyteams.get(s).get(Integer.valueOf(displayname));
                                                    players++;
                                                    HashMap<Integer, Integer> temp = new HashMap<>();
                                                    for (int max = 1; max <= pg.lobbyteamAmount.get(s); max++) {
                                                        int oldplayers = pg.lobbyteams.get(s).get(max);
                                                        temp.put(max, oldplayers);
                                                    }
                                                    temp.put(Integer.valueOf(displayname), players);
                                                    pg.lobbyteams.replace(s, temp);
                                                    p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                                    p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(45) + ": " + ChatColor.LIGHT_PURPLE + displayname);
                                                    p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(44) + ": " + ChatColor.AQUA + pg.lobbyteams.get(s).get(Integer.valueOf(displayname)) + ChatColor.GRAY + "/" + ChatColor.AQUA + maxteamplayers);
                                                    p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                                    pg.lobbyTeamed.put(s, e.getWhoClicked().getName());
                                                    pg.lobbyteamplayernames.get(s).put(displayname, p);
                                                } else {
                                                    p.closeInventory();
                                                    p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                                    p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(47));
                                                    p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                                }
                                            }
                                        } else {
                                            p.closeInventory();
                                            String teamname = null;
                                            for (String all : pg.lobbyteamplayernames.get(s).keySet()) {
                                                if (pg.lobbyteamplayernames.get(s).get(all) == p) {
                                                    teamname = all;
                                                }
                                            }
                                            pg.lobbyteamplayernames.get(s).remove(teamname, p);
                                            assert teamname != null;
                                            int teamamount = pg.lobbyteams.get(s).get(Integer.valueOf(teamname));
                                            teamamount--;
                                            HashMap<Integer, Integer> tempold = new HashMap<>();
                                            for (int max = 1; max <= pg.lobbyteamAmount.get(s); max++) {
                                                int oldplayers = pg.lobbyteams.get(s).get(max);
                                                tempold.put(max, oldplayers);
                                            }
                                            tempold.put(Integer.valueOf(teamname), teamamount);
                                            pg.lobbyteams.replace(s, tempold);
                                            pg.lobbyTeamed.remove(s, p.getName());
                                            if (displayname.equals(pg.chat.get(42))) {
                                                boolean teamfound = false;
                                                while (!teamfound) {
                                                    Random rnd = new Random();
                                                    int rndTeam = rnd.nextInt(pg.lobbyteams.get(s).size());
                                                    rndTeam++;
                                                    if (pg.lobbyteams.get(s).get(rndTeam) < maxteamplayers && pg.lobbyteams.get(s).get(rndTeam) >= 0 && pg.lobbyteams.get(s).get(rndTeam) != null) {
                                                        teamfound = true;
                                                        p.closeInventory();
                                                        int players = pg.lobbyteams.get(s).get(rndTeam);
                                                        players++;
                                                        HashMap<Integer, Integer> temp = new HashMap<>();
                                                        for (int max = 1; max <= pg.lobbyteamAmount.get(s); max++) {
                                                            int oldplayers = pg.lobbyteams.get(s).get(max);
                                                            temp.put(max, oldplayers);
                                                        }
                                                        temp.put(rndTeam, players);
                                                        pg.lobbyteams.replace(s, temp);
                                                        p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(45) + ": " + ChatColor.LIGHT_PURPLE + rndTeam);
                                                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(44) + ": " + ChatColor.AQUA + pg.lobbyteams.get(s).get(rndTeam) + ChatColor.GRAY + "/" + ChatColor.AQUA + maxteamplayers);
                                                        p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                                        pg.lobbyTeamed.put(s, e.getWhoClicked().getName());
                                                        pg.lobbyteamplayernames.get(s).put(Integer.toString(rndTeam), p);
                                                    }
                                                }
                                            } else {
                                                if (pg.lobbyteams.get(s).get(Integer.valueOf(displayname)) < maxteamplayers) {
                                                    p.closeInventory();
                                                    int players = pg.lobbyteams.get(s).get(Integer.valueOf(displayname));
                                                    players++;
                                                    HashMap<Integer, Integer> temp = new HashMap<>();
                                                    for (int max = 1; max <= pg.lobbyteamAmount.get(s); max++) {
                                                        int oldplayers = pg.lobbyteams.get(s).get(max);
                                                        temp.put(max, oldplayers);
                                                    }
                                                    temp.put(Integer.valueOf(displayname), players);
                                                    pg.lobbyteams.replace(s, temp);
                                                    p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                                    p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(45) + ": " + ChatColor.LIGHT_PURPLE + displayname);
                                                    p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(44) + ": " + ChatColor.AQUA + pg.lobbyteams.get(s).get(Integer.valueOf(displayname)) + ChatColor.GRAY + "/" + ChatColor.AQUA + maxteamplayers);
                                                    p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                                    pg.lobbyTeamed.put(s, e.getWhoClicked().getName());
                                                    pg.lobbyteamplayernames.get(s).put(displayname, p);
                                                } else {
                                                    p.closeInventory();
                                                    p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                                    p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(47));
                                                    p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (e.getView().getTitle().equalsIgnoreCase(pg.prefix + ChatColor.DARK_AQUA + pg.chat.get(62))) {
                            if (e.getCurrentItem() != null) {
                                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).hasDisplayName()) {
                                    String displayname = e.getCurrentItem().getItemMeta().getDisplayName();
                                    if (!pg.lobbyKited.containsValue(p.getName())) {
                                        if (displayname.equals(pg.chat.get(42))) {
                                            Random rnd = new Random();
                                            int rndKit = rnd.nextInt(pg.getActiveKits());
                                            p.closeInventory();
                                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                            p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(46) + ": " + ChatColor.LIGHT_PURPLE + pg.kits.get(rndKit));
                                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                            pg.lobbyKited.put(s, e.getWhoClicked().getName());
                                            pg.kitplayernames.put(pg.kits.get(rndKit), p);
                                            if (pg.kits.get(rndKit).equals("Rich Kid")) {
                                                pg.richkidPlayers.add(p);
                                            }
                                        } else {
                                            p.closeInventory();
                                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                            p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(46) + ": " + ChatColor.LIGHT_PURPLE + displayname);
                                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                            pg.lobbyKited.put(s, e.getWhoClicked().getName());
                                            pg.kitplayernames.put(displayname, p);
                                            if (displayname.equals("Rich Kid")) {
                                                pg.richkidPlayers.add(p);
                                            }
                                        }
                                    } else {
                                        p.closeInventory();
                                        String kitname = null;
                                        for (int i = 0; i <= pg.getActiveKits(); i++) {
                                            if (pg.kitplayernames.containsKey(Integer.toString(i)) && pg.kitplayernames.containsValue(p)) {
                                                kitname = Integer.toString(i);
                                            }
                                        }
                                        pg.kitplayernames.remove(kitname, p);
                                        pg.richkidPlayers.remove(p);
                                        if (displayname.equals(pg.chat.get(42))) {
                                            Random rnd = new Random();
                                            int rndKit = rnd.nextInt(pg.getActiveKits());
                                            p.closeInventory();
                                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                            p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(46) + ": " + ChatColor.LIGHT_PURPLE + pg.kits.get(rndKit));
                                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                            pg.lobbyKited.put(s, e.getWhoClicked().getName());
                                            pg.kitplayernames.put(pg.kits.get(rndKit), p);
                                            if (pg.kits.get(rndKit).equals("Rich Kid")) {
                                                pg.richkidPlayers.add(p);
                                            }
                                        } else {
                                            p.closeInventory();
                                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                            p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(46) + ": " + ChatColor.LIGHT_PURPLE + displayname);
                                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                            pg.lobbyKited.put(s, e.getWhoClicked().getName());
                                            pg.kitplayernames.put(displayname, p);
                                            if (displayname.equals("Rich Kid")) {
                                                pg.richkidPlayers.add(p);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e.getView().getTitle().equalsIgnoreCase(pg.prefix + ChatColor.DARK_AQUA + pg.chat.get(49))) {
                        if (e.getCurrentItem() != null) {
                            int shopitem = 1;
                            for (int i = 0; i < pg.shop.size(); i++) {
                                int coinamount;
                                if (pg.kitplayernames.containsKey(pg.shopkit.get(shopitem - 1)) && pg.kitplayernames.containsValue(p)) {
                                    coinamount = pg.shopsale.get(shopitem - 1);
                                } else {
                                    coinamount = pg.shopcost.get(shopitem - 1);
                                }
                                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equals(pg.shop.get(shopitem - 1))) {
                                    if (bottle >= 1) {
                                        if (amount >= coinamount) {
                                            amount = amount - coinamount;
                                            bottle = bottle - 1;
                                            ItemStack randombarrier = new ItemStack(pg.shoppotiontype.get(shopitem - 1));
                                            PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                                            assert randombarriermeta != null;
                                            randombarriermeta.addCustomEffect(new PotionEffect(pg.shoppotion.get(shopitem - 1).getType(), pg.shoppotion.get(shopitem - 1).getDuration(), pg.shoppotion.get(shopitem - 1).getAmplifier()), true);
                                            randombarriermeta.setDisplayName(pg.shop.get(shopitem - 1));
                                            randombarrier.setItemMeta(randombarriermeta);
                                            p.getInventory().addItem(randombarrier);
                                            for (int k = 0; k < coinamount; k++) {
                                                p.getInventory().removeItem(pg.getCoin());
                                            }
                                            for (int k = 0; k < 1; k++) {
                                                p.getInventory().removeItem(pg.getBottle());
                                            }
                                        } else {
                                            p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(53));
                                        }
                                    } else {
                                        p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(54));
                                    }
                                }
                                shopitem++;
                            }
                        }
                        e.setCancelled(true);
                    }
                } else {
                    if (pg.getGamestate() == GameStates.WAITING && !pg.isBuild() && pg.pgPlayers.contains(p) || pg.getGamestate() == GameStates.PREPARING && !pg.isBuild() && pg.pgPlayers.contains(p)) {
                        if (e.getView().getTitle().equalsIgnoreCase(pg.prefix + ChatColor.DARK_AQUA + pg.chat.get(14))) {
                            if (e.getCurrentItem() != null) {
                                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).hasDisplayName()) {
                                    String displayname = e.getCurrentItem().getItemMeta().getDisplayName();
                                    if (!pg.voted.contains(p.getName())) {
                                        p.closeInventory();
                                        int votes = pg.votes.get(displayname);
                                        votes++;
                                        pg.votes.put(displayname, votes);
                                        p.sendMessage(pg.prefix + "--------------" + pg.chat.get(14) + "--------------");
                                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(16) + ": " + ChatColor.LIGHT_PURPLE + displayname);
                                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(15) + ": " + ChatColor.AQUA + pg.votes.get(displayname));
                                        p.sendMessage(pg.prefix + "--------------" + pg.chat.get(14) + "--------------");
                                        pg.voted.add(e.getWhoClicked().getName());
                                        pg.voteplayernames.put(displayname, p);
                                    } else {
                                        p.closeInventory();
                                        String arenaname = null;
                                        for (int i = 0; i <= pg.arenas.size(); i++) {
                                            if (arenadata.contains("pg.arenas." + i)) {
                                                String name = arenadata.getString("pg.arenas." + i + ".name");
                                                if (pg.voteplayernames.containsKey(name) && pg.voteplayernames.containsValue(p)) {
                                                    arenaname = name;
                                                }
                                            } else {
                                                arenaname = pg.chat.get(42);
                                            }
                                        }
                                        pg.voteplayernames.remove(arenaname, p);
                                        int votes = pg.votes.get(arenaname) - 1;
                                        pg.votes.put(arenaname, votes);
                                        pg.voted.remove(p.getName());
                                        votes = pg.votes.get(displayname);
                                        votes++;
                                        pg.votes.put(displayname, votes);
                                        p.sendMessage(pg.prefix + "--------------" + pg.chat.get(14) + "--------------");
                                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(16) + ": " + ChatColor.LIGHT_PURPLE + displayname);
                                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(15) + ": " + ChatColor.AQUA + pg.votes.get(displayname));
                                        p.sendMessage(pg.prefix + "--------------" + pg.chat.get(14) + "--------------");
                                        pg.voted.add(e.getWhoClicked().getName());
                                        pg.voteplayernames.put(displayname, p);
                                    }
                                }
                            }
                        }
                        if (pg.isActivateTeams()) {
                            if (e.getView().getTitle().equalsIgnoreCase(pg.prefix + ChatColor.DARK_AQUA + pg.chat.get(43))) {
                                if (e.getCurrentItem() != null) {
                                    if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).hasDisplayName()) {
                                        String displayname = e.getCurrentItem().getItemMeta().getDisplayName();
                                        int maxteamplayers = pg.getTeamSize();
                                        if (!pg.teamed.contains(p.getName())) {
                                            if (displayname.equals(pg.chat.get(42))) {
                                                boolean teamfound = false;
                                                while (!teamfound) {
                                                    Random rnd = new Random();
                                                    int rndTeam = rnd.nextInt(pg.teams.size());
                                                    rndTeam++;
                                                    if (pg.teamplayers.get(Integer.toString(rndTeam)) < maxteamplayers && pg.teamplayers.get(Integer.toString(rndTeam)) >= 0) {
                                                        teamfound = true;
                                                        p.closeInventory();
                                                        int players = pg.teamplayers.get(Integer.toString(rndTeam));
                                                        players++;
                                                        pg.teamplayers.put(Integer.toString(rndTeam), players);
                                                        p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(45) + ": " + ChatColor.LIGHT_PURPLE + rndTeam);
                                                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(44) + ": " + ChatColor.AQUA + pg.teamplayers.get(Integer.toString(rndTeam)) + ChatColor.GRAY + "/" + ChatColor.AQUA + maxteamplayers);
                                                        p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                                        pg.teamed.add(e.getWhoClicked().getName());
                                                        pg.teamplayernames.put(Integer.toString(rndTeam), p);
                                                    }
                                                }
                                            } else {
                                                if (pg.teamplayers.get(displayname) < maxteamplayers) {
                                                    p.closeInventory();
                                                    int players = pg.teamplayers.get(displayname);
                                                    players++;
                                                    pg.teamplayers.put(displayname, players);
                                                    p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                                    p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(45) + ": " + ChatColor.LIGHT_PURPLE + displayname);
                                                    p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(44) + ": " + ChatColor.AQUA + pg.teamplayers.get(displayname) + ChatColor.GRAY + "/" + ChatColor.AQUA + maxteamplayers);
                                                    p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                                    pg.teamed.add(e.getWhoClicked().getName());
                                                    pg.teamplayernames.put(displayname, p);
                                                } else {
                                                    p.closeInventory();
                                                    p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                                    p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(47));
                                                    p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                                }
                                            }
                                        } else {
                                            p.closeInventory();
                                            String teamname = null;
                                            for (int i = 1; i <= pg.getTeamAmount(); i++) {
                                                if (pg.teamplayernames.containsKey(Integer.toString(i)) && pg.teamplayernames.containsValue(p)) {
                                                    teamname = Integer.toString(i);
                                                }
                                            }
                                            pg.teamplayernames.remove(teamname, p);
                                            int teamamount = pg.teamplayers.get(teamname) - 1;
                                            pg.teamplayers.put(teamname, teamamount);
                                            pg.teamed.remove(p.getName());
                                            if (displayname.equals(pg.chat.get(42))) {
                                                boolean teamfound = false;
                                                while (!teamfound) {
                                                    Random rnd = new Random();
                                                    int rndTeam = rnd.nextInt(pg.teams.size());
                                                    rndTeam++;
                                                    if (pg.teamplayers.get(Integer.toString(rndTeam)) < maxteamplayers) {
                                                        teamfound = true;
                                                        p.closeInventory();
                                                        int players = pg.teamplayers.get(Integer.toString(rndTeam));
                                                        players++;
                                                        pg.teamplayers.put(Integer.toString(rndTeam), players);
                                                        p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(45) + ": " + ChatColor.LIGHT_PURPLE + rndTeam);
                                                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(44) + ": " + ChatColor.AQUA + pg.teamplayers.get(Integer.toString(rndTeam)) + ChatColor.GRAY + "/" + ChatColor.AQUA + maxteamplayers);
                                                        p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                                        pg.teamed.add(e.getWhoClicked().getName());
                                                        pg.teamplayernames.put(Integer.toString(rndTeam), p);
                                                    }
                                                }
                                            } else {
                                                if (pg.teamplayers.get(displayname) < maxteamplayers) {
                                                    p.closeInventory();
                                                    int players = pg.teamplayers.get(displayname);
                                                    players++;
                                                    pg.teamplayers.put(displayname, players);
                                                    p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                                    p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(45) + ": " + ChatColor.LIGHT_PURPLE + displayname);
                                                    p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(44) + ": " + ChatColor.AQUA + pg.teamplayers.get(displayname) + ChatColor.GRAY + "/" + ChatColor.AQUA + maxteamplayers);
                                                    p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                                    pg.teamed.add(e.getWhoClicked().getName());
                                                    pg.teamplayernames.put(displayname, p);
                                                } else {
                                                    p.closeInventory();
                                                    p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                                    p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(47));
                                                    p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (e.getView().getTitle().equalsIgnoreCase(pg.prefix + ChatColor.DARK_AQUA + pg.chat.get(62))) {
                            if (e.getCurrentItem() != null) {
                                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).hasDisplayName()) {
                                    String displayname = e.getCurrentItem().getItemMeta().getDisplayName();
                                    if (!pg.kited.contains(p.getName())) {
                                        if (displayname.equals(pg.chat.get(42))) {
                                            Random rnd = new Random();
                                            int rndKit = rnd.nextInt(pg.getActiveKits());
                                            p.closeInventory();
                                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                            p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(46) + ": " + ChatColor.LIGHT_PURPLE + pg.kits.get(rndKit));
                                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                            pg.kited.add(e.getWhoClicked().getName());
                                            pg.kitplayernames.put(pg.kits.get(rndKit), p);
                                            if (pg.kits.get(rndKit).equals("Rich Kid")) {
                                                pg.richkidPlayers.add(p);
                                            }
                                        } else {
                                            p.closeInventory();
                                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                            p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(46) + ": " + ChatColor.LIGHT_PURPLE + displayname);
                                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                            pg.kited.add(e.getWhoClicked().getName());
                                            pg.kitplayernames.put(displayname, p);
                                            if (displayname.equals("Rich Kid")) {
                                                pg.richkidPlayers.add(p);
                                            }
                                        }
                                    } else {
                                        p.closeInventory();
                                        String kitname = null;
                                        for (int i = 0; i <= pg.getActiveKits(); i++) {
                                            if (pg.kitplayernames.containsKey(Integer.toString(i)) && pg.kitplayernames.containsValue(p)) {
                                                kitname = Integer.toString(i);
                                            }
                                        }
                                        pg.kitplayernames.remove(kitname, p);
                                        pg.richkidPlayers.remove(p);
                                        if (displayname.equals(pg.chat.get(42))) {
                                            Random rnd = new Random();
                                            int rndKit = rnd.nextInt(pg.getActiveKits());
                                            p.closeInventory();
                                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                            p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(46) + ": " + ChatColor.LIGHT_PURPLE + pg.kits.get(rndKit));
                                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                            pg.kited.add(e.getWhoClicked().getName());
                                            pg.kitplayernames.put(pg.kits.get(rndKit), p);
                                            if (pg.kits.get(rndKit).equals("Rich Kid")) {
                                                pg.richkidPlayers.add(p);
                                            }
                                        } else {
                                            p.closeInventory();
                                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                            p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(46) + ": " + ChatColor.LIGHT_PURPLE + displayname);
                                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                            pg.kited.add(e.getWhoClicked().getName());
                                            pg.kitplayernames.put(displayname, p);
                                            if (displayname.equals("Rich Kid")) {
                                                pg.richkidPlayers.add(p);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e.getView().getTitle().equalsIgnoreCase(pg.prefix + ChatColor.DARK_AQUA + pg.chat.get(49))) {
                        if (e.getCurrentItem() != null) {
                            int shopitem = 1;
                            for (int i = 0; i < pg.shop.size(); i++) {
                                int coinamount;
                                if (pg.kitplayernames.containsKey(pg.shopkit.get(shopitem - 1)) && pg.kitplayernames.containsValue(p)) {
                                    coinamount = pg.shopsale.get(shopitem - 1);
                                } else {
                                    coinamount = pg.shopcost.get(shopitem - 1);
                                }
                                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equals(pg.shop.get(shopitem - 1))) {
                                    if (bottle >= 1) {
                                        if (amount >= coinamount) {
                                            amount = amount - coinamount;
                                            bottle = bottle - 1;
                                            ItemStack randombarrier = new ItemStack(pg.shoppotiontype.get(shopitem - 1));
                                            PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                                            assert randombarriermeta != null;
                                            randombarriermeta.addCustomEffect(new PotionEffect(pg.shoppotion.get(shopitem - 1).getType(), pg.shoppotion.get(shopitem - 1).getDuration(), pg.shoppotion.get(shopitem - 1).getAmplifier()), true);
                                            randombarriermeta.setDisplayName(pg.shop.get(shopitem - 1));
                                            randombarrier.setItemMeta(randombarriermeta);
                                            p.getInventory().addItem(randombarrier);
                                            for (int k = 0; k < coinamount; k++) {
                                                p.getInventory().removeItem(pg.getCoin());
                                            }
                                            for (int k = 0; k < 1; k++) {
                                                p.getInventory().removeItem(pg.getBottle());
                                            }
                                        } else {
                                            p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(53));
                                        }
                                    } else {
                                        p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(54));
                                    }
                                }
                                shopitem++;
                            }
                        }
                        e.setCancelled(true);
                    }
                }
            }
            if (e.getView().getTitle().equalsIgnoreCase(pg.prefix + ChatColor.DARK_AQUA + "Choose Lobby")) {
                if (e.getCurrentItem() != null) {
                    p.closeInventory();
                    lobby = Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName();
                    p.sendMessage(pg.prefix + ChatColor.AQUA + lobby + " " + ChatColor.GREEN + "successfully chosen!");
                }
                e.setCancelled(true);
            }
            if (e.getView().getTitle().equalsIgnoreCase(pg.prefix + ChatColor.DARK_AQUA + "Choose Arena")) {
                if (e.getCurrentItem() != null) {
                    p.closeInventory();
                    arena = Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName();
                    p.sendMessage(pg.prefix + ChatColor.AQUA + arena + " " + ChatColor.GREEN + "successfully chosen!");
                }
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
        if (pg.isGameServer()) {
            Player p = e.getPlayer();
            if (pg.pgPlayers.contains(p) || pg.playerLobby.containsKey(p)) {
                if (pg.isLobbySystem()) {
                    String s = null;
                    for (int ii = 1; ii <= 1000; ii++) {
                        if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                            s = Integer.toString(ii);
                        }
                    }
                    e.setCancelled(pg.lobbyStates.get(s) != GameStates.INGAME && pg.playerLobby.get(p).contains(Objects.requireNonNull(s)));
                } else {
                    e.setCancelled(pg.getGamestate() != GameStates.INGAME && pg.pgPlayers.contains(p));
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        pg.createPlayer(p.getUniqueId().toString());
        pg.joinChannel(p.getPlayer(), "Global");
        if (pg.isGameServer()) {
            if (pg.isStartOnJoin() && !pg.isLobbySystem()) {
                pg.onJoin(p);
                e.setJoinMessage(null);
            }
        }
        if (p.hasPermission("pg.update")) {
            String latest = null;
            try {
                URL url = new URL("https://raw.githubusercontent.com/andersspielen/PotionGamesIssues/master/version.txt");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String inputLine;
                while ((inputLine = bufferedReader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                    stringBuilder.append(System.lineSeparator());
                }
                bufferedReader.close();
                latest = stringBuilder.toString().trim();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            boolean upToDate = pg.getDescription().getVersion().equals(latest);
            if (!upToDate) {
                TextComponent message = new TextComponent(TextComponent.fromLegacyText(pg.prefix + "There is a newer version available: " + latest + ", you're on: " + pg.getDescription().getVersion() + " - "));
                TextComponent link = new TextComponent(TextComponent.fromLegacyText(ChatColor.GRAY + "Click here to download!"));
                link.setUnderlined(true);
                message.addExtra(link);
                message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/andersspielen/PotionGamesIssues/releases/latest"));
                p.spigot().sendMessage(message);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (pg.isGameServer()) {
            Player p = e.getPlayer();
            if (pg.isLobbySystem()) {
                if (pg.playerLobby.containsKey(p)) {
                    String s = null;
                    for (int ii = 1; ii <= 1000; ii++) {
                        if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                            s = Integer.toString(ii);
                        }
                    }
                    if (!pg.lobbyMove.get(s)) {
                        if (e.getFrom().getX() != Objects.requireNonNull(e.getTo()).getX() || e.getFrom().getZ() != e.getTo().getZ()) {
                            Location loc = new Location(p.getWorld(), e.getFrom().getX(), e.getTo().getY(), e.getFrom().getZ());
                            loc.setYaw(e.getTo().getYaw());
                            loc.setPitch(e.getTo().getPitch());
                            p.teleport(loc);
                        }
                    }
                }
            } else {
                if (pg.pgPlayers.contains(p)) {
                    if (!pg.isMove()) {
                        if (e.getFrom().getX() != Objects.requireNonNull(e.getTo()).getX() || e.getFrom().getZ() != e.getTo().getZ()) {
                            Location loc = new Location(p.getWorld(), e.getFrom().getX(), e.getTo().getY(), e.getFrom().getZ());
                            loc.setYaw(e.getTo().getYaw());
                            loc.setPitch(e.getTo().getPitch());
                            p.teleport(loc);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (pg.isGameServer()) {
            Player p = e.getPlayer();
            if (pg.isLobbySystem()) {
                if (pg.playerLobby.containsKey(p)) {
                    String s;
                    for (int ii = 1; ii <= 1000; ii++) {
                        if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                            s = Integer.toString(ii);
                            pg.onLeaveLobby(p, s);
                            e.setQuitMessage(null);
                            break;
                        }
                    }
                } else if (pg.specLobby.containsKey(p)) {
                    String s;
                    for (int ii = 1; ii <= 1000; ii++) {
                        if (pg.specLobby.get(p).contains(Integer.toString(ii))) {
                            s = Integer.toString(ii);
                            pg.onLeaveLobby(p, s);
                            e.setQuitMessage(null);
                            break;
                        }
                    }
                }
            } else {
                if (pg.pgPlayers.contains(p) || pg.specPlayers.contains(p)) {
                    pg.onLeave(p);
                    e.setQuitMessage(null);
                }
            }
        }
    }

    @EventHandler
    public void onPing(ServerListPingEvent e) {
        FileConfiguration arenadata = YamlConfiguration.loadConfiguration(pg.arenadatafile);
        if (pg.isGameServer()) {
            if (pg.isStartOnJoin()) {
                if (!pg.isLobbySystem()) {
                    e.setMaxPlayers(pg.getMaxPlayers());
                    if (pg.getGamestate() == GameStates.WAITING) {
                        e.setMotd("" + ChatColor.GREEN + pg.getGamestate());
                    } else if (pg.getGamestate() == GameStates.PREPARING) {
                        if (pg.getVote() != null) {
                            String motd = "" + ChatColor.GOLD + Objects.requireNonNull(arenadata.get("pg.arenas." + pg.getVote() + ".name"));
                            e.setMotd(motd.toUpperCase());
                        } else {
                            e.setMotd("" + ChatColor.AQUA + "VOTING");
                        }
                    } else if (pg.getGamestate() == GameStates.INGAME) {
                        e.setMotd("" + ChatColor.RED + pg.getGamestate());
                    } else {
                        e.setMotd("" + ChatColor.DARK_RED + pg.getGamestate());
                    }
                }
            }
        }
    }

}
