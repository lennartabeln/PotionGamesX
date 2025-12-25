package com.tw0far.potiongames.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
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
import org.bukkit.scoreboard.Team;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.GameStates;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.Settings;
import com.tw0far.potiongames.updatechecker.UpdateChecker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public record Events(PotionGames pg) implements Listener {
    private static final HashSet<Material> toDestroy = new HashSet<>();
    private static int amount;
    private static int bottle;
    private static Component lobby;
    private static Component arena;

    static {
        toDestroy.add(Material.AIR);
    }

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        Player p = e.getPlayer();
        if (pg.isGameServer()) {
            if (pg.setupPlayer.contains(p)) {
                if (pg.isAddlobby()) {
                    lobby = e.message();
                    e.setCancelled(true);
                    if (p.hasPermission("pg.setup")) {
                        if (pg.isLobbySystem()) {
                            Settings.arenadata.set("pg.lobbies." + lobby + ".world", Objects.requireNonNull(p.getLocation().getWorld()).getName());
                            Settings.arenadata.set("pg.lobbies." + lobby + ".coords", Objects.requireNonNull(p.getLocation()));
                            Settings.arenadata.set("pg.lobbies." + lobby + ".activateTeams", true);
                            Settings.arenadata.set("pg.lobbies." + lobby + ".activateKits", true);
                            Settings.arenadata.set("pg.lobbies." + lobby + ".activateShop", true);
                            Settings.arenadata.set("pg.lobbies." + lobby + ".teamSize", 2);
                            Settings.arenadata.set("pg.lobbies." + lobby + ".maxPlayers", 24);
                            Settings.arenadata.set("pg.lobbies." + lobby + ".minPlayers", 12);
                            Settings.arenadata.set("pg.lobbies." + lobby + ".roundTime", 30);
                            try {
                                Settings.arenadata.save(Settings.arenadatafile);
                            } catch (IOException ex) {
                                Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + pg.chatmessages.get(63) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
                            }
                            p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(24)).color(NamedTextColor.GREEN)).append(Component.text(" (Lobby: " + lobby + ")").color(NamedTextColor.GRAY)));
                        }
                    }
                    pg.setAddlobby(false);
                    pg.setup(p);
                }
                if (pg.isAddarena()) {
                    arena = e.message();
                    e.setCancelled(true);
                    if (p.hasPermission("pg.setup")) {
                        if (!pg.isLobbySystem()) {
                            int arenaNumber = 1;
                            try {
                                while (Settings.arenadata.contains("pg.arenas." + arenaNumber)) {
                                    arenaNumber++;
                                }
                                Settings.arenadata.set("pg.arenas." + arenaNumber, p.getWorld());
                                Settings.arenadata.set("pg.arenas." + arenaNumber + ".world", p.getWorld().getName());
                                Settings.arenadata.set("pg.arenas." + arenaNumber + ".name", arena);
                                Settings.arenadata.save(Settings.arenadatafile);
                                p.sendMessage(Settings.prefix.append(arena.color(NamedTextColor.AQUA)).append(Component.text(" " + pg.chatmessages.get(29)).color(NamedTextColor.GREEN)));
                            } catch (Exception ex) {
                                p.sendMessage(Settings.prefix.append(arena.color(NamedTextColor.AQUA)).append(Component.text(" " + pg.chatmessages.get(27)).color(NamedTextColor.RED)));
                            }
                        }
                    }
                    if (p.hasPermission("pg.setup")) {
                        if (pg.isLobbySystem()) {
                            int arenaNumber = 1;
                            try {
                                while (Settings.arenadata.contains("pg.lobbies." + lobby + "." + arenaNumber)) {
                                    arenaNumber++;
                                }
                                Settings.arenadata.set("pg.lobbies." + lobby + "." + arenaNumber, p.getWorld());
                                Settings.arenadata.set("pg.lobbies." + lobby + "." + arenaNumber + ".world", p.getWorld().getName());
                                Settings.arenadata.set("pg.lobbies." + lobby + "." + arenaNumber + ".name", arena);
                                Settings.arenadata.save(Settings.arenadatafile);
                                p.sendMessage(Settings.prefix.append(arena.color(NamedTextColor.AQUA)).append(Component.text(" " + pg.chatmessages.get(29)).color(NamedTextColor.GREEN)).append(Component.text(" (Lobby: " + lobby + ")").color(NamedTextColor.GRAY)));
                            } catch (Exception ex) {
                                p.sendMessage(Settings.prefix.append(lobby.color(NamedTextColor.AQUA)).append(Component.text(" " + pg.chatmessages.get(27)).color(NamedTextColor.RED)).append(Component.text(" (Lobby: " + lobby + ")").color(NamedTextColor.GRAY)));
                            }
                        }
                    }
                    pg.setAddarena(false);
                    pg.setup(p);
                }
                if (pg.isDellobby()) {
                    lobby = e.message();
                    e.setCancelled(true);
                    if (p.hasPermission("pg.setup")) {
                        if (pg.isLobbySystem()) {
                            Settings.arenadata.set("pg.lobbies." + lobby, null);
                            try {
                                Settings.arenadata.save(Settings.arenadatafile);
                            } catch (IOException ex) {
                                Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + pg.chatmessages.get(63) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
                            }
                            p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(66)).color(NamedTextColor.GREEN)).append(Component.text(" (Lobby: " + lobby + ")").color(NamedTextColor.GRAY)));
                        }
                    }
                    pg.setDellobby(false);
                    pg.setup(p);
                }
                if (pg.isDelarena()) {
                    arena = e.message();
                    e.setCancelled(true);
                    if (p.hasPermission("pg.setup")) {
                        if (!pg.isLobbySystem()) {
                            int arenaNumber = 1;
                            try {
                                int i = 1;
                                boolean arenaId = false;
                                while (!arenaId) {
                                    if (Settings.arenadata.getString("pg.arenas." + i + ".name") == null) {
                                        i++;
                                    } else {
                                        if (arena.toString().equals(Objects.requireNonNull(Settings.arenadata.getString("pg.arenas." + i + ".name")))) {
                                            arenaNumber = i;
                                            arenaId = true;
                                        } else {
                                            i++;
                                        }
                                    }
                                }
                                Component arenaName = arena;
                                Settings.arenadata.set("pg.arenas." + arenaNumber, null);
                                Settings.arenadata.save(Settings.arenadatafile);
                                p.sendMessage(Settings.prefix.append(arenaName.color(NamedTextColor.AQUA)).append(Component.text(" " + pg.chatmessages.get(28)).color(NamedTextColor.GREEN)));
                            } catch (Exception ex) {
                                p.sendMessage(Settings.prefix.append(arena.color(NamedTextColor.AQUA)).append(Component.text(" " + pg.chatmessages.get(27)).color(NamedTextColor.RED)));
                            }
                        }
                    }
                    if (p.hasPermission("pg.setup")) {
                        if (pg.isLobbySystem()) {
                            int arenaNumber = 1;
                            try {
                                int i = 1;
                                boolean arenaId = false;
                                while (!arenaId) {
                                    if (Settings.arenadata.getString("pg.lobbies." + lobby + "." + i + ".name") == null) {
                                        i++;
                                    } else {
                                        if (arena.toString().equals(Objects.requireNonNull(Settings.arenadata.getString("pg.lobbies." + lobby + "." + i + ".name")))) {
                                            arenaNumber = i;
                                            arenaId = true;
                                        } else {
                                            i++;
                                        }
                                    }
                                }
                                Component arenaName = arena;
                                Settings.arenadata.set("pg.lobbies." + lobby + "." + arenaNumber, null);
                                Settings.arenadata.save(Settings.arenadatafile);
                                p.sendMessage(Settings.prefix.append(arenaName.color(NamedTextColor.AQUA)).append(Component.text(" " + pg.chatmessages.get(28)).color(NamedTextColor.GREEN)).append(Component.text(" (Lobby: " + lobby + ")").color(NamedTextColor.GRAY)));
                            } catch (Exception ex) {
                                p.sendMessage(Settings.prefix.append(arena.color(NamedTextColor.AQUA)).append(Component.text(" " + pg.chatmessages.get(27)).color(NamedTextColor.RED)).append(Component.text(" (Lobby: " + lobby + ")").color(NamedTextColor.GRAY)));
                            }
                        }
                    }
                    pg.setDelarena(false);
                    pg.setup(p);
                }
            }
            if (pg.playerChannel.get(p) == null) {
                pg.joinChannel(p.getPlayer(), "Global");
            }
            if (pg.playerChannel.get(p).equals("Local") && pg.playerChannel.get(p) != null && !pg.setupPlayer.contains(p)) {
                if (!pg.isAllowOutsideChat()) {
                    if (pg.isLobbySystem()) {
                        String s = null;
                        for (int ii = 1; ii <= 27; ii++) {
                            if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                                s = Integer.toString(ii);
                            }
                        }
                        Component message = e.message();
                        if (!p.hasPermission("pg.admin")) {
                            if (pg.playerLobby.containsKey(p)) {
                                e.setCancelled(true);
                                for (Player pgchat : pg.playerLobby.keySet()) {
                                    if (pg.playerLobby.get(pgchat).equals(s)) {
                                        p.sendMessage(Settings.prefix.append(p.displayName()).append(Component.text(": ")).append(message));
                                    }
                                }
                                for (Player pgchat : pg.specLobby.keySet()) {
                                    if (pg.specLobby.get(pgchat).equals(s)) {
                                        p.sendMessage(Settings.prefix.append(p.displayName()).append(Component.text(": ")).append(message));
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
                                        p.sendMessage(Settings.prefix.append(p.displayName()).append(Component.text(": " + message).color(NamedTextColor.DARK_AQUA)));
                                    }
                                }
                                for (Player pgchat : pg.specLobby.keySet()) {
                                    if (pg.specLobby.get(pgchat).equals(s)) {
                                        p.sendMessage(Settings.prefix.append(Component.text(p.displayName() + ": " + message).color(NamedTextColor.DARK_AQUA)));
                                    }
                                }
                            } else if (pg.specPlayers.contains(p)) {
                                e.setCancelled(true);
                                for (Player pgchat : pg.playerLobby.keySet()) {
                                    if (pg.playerLobby.get(pgchat).equals(s)) {
                                        p.sendMessage(Messages.DeadLabel(p.displayName(), message));
                                    }
                                }
                                for (Player pgchat : pg.specLobby.keySet()) {
                                    if (pg.specLobby.get(pgchat).equals(s)) {
                                        p.sendMessage(Messages.DeadLabel(p.displayName(), message));
                                    }
                                }
                            }
                        }
                    } else {
                        Component message = e.message();
                        if (!p.hasPermission("pg.admin")) {
                            if (pg.pgPlayers.contains(p)) {
                                e.setCancelled(true);
                                for (Player pgchat : pg.pgPlayers) {
                                    p.sendMessage(Settings.prefix.append(p.displayName()).append(Component.text(": ")).append(message));
                                }
                                for (Player pgchat : pg.specPlayers) {
                                    p.sendMessage(Settings.prefix.append(p.displayName()).append(Component.text(": ")).append(message));
                                }
                            } else if (pg.specPlayers.contains(p)) {
                                e.setCancelled(true);
                            }
                        } else {
                            if (pg.pgPlayers.contains(p)) {
                                e.setCancelled(true);
                                for (Player pgchat : pg.pgPlayers) {
                                    p.sendMessage(Settings.prefix.append(Component.text(p.displayName() + ": " + message).color(NamedTextColor.DARK_AQUA)));
                                }
                                for (Player pgchat : pg.specPlayers) {
                                    p.sendMessage(Settings.prefix.append(Component.text(p.displayName() + ": " + message).color(NamedTextColor.DARK_AQUA)));
                                }
                            } else if (pg.specPlayers.contains(p)) {
                                e.setCancelled(true);
                                for (Player pgchat : pg.pgPlayers) {
                                   p.sendMessage(Messages.DeadLabel(p.displayName(), message));
                                }
                                for (Player pgchat : pg.specPlayers) {
                                    p.sendMessage(Messages.DeadLabel(p.displayName(), message));
                                }
                            }
                        }
                    }
                }
                if (!e.isCancelled()) {
                    Component fallback = e.message();
                    for (Player player : pg.getChannel(p)) {
                        if (player != null) {
                            player.sendMessage(fallback);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent e) {
        if (pg.isGameServer()) {
            if (pg.worlds.contains(e.getBlock().getWorld().getName())) {
                switch (e.getBlock().getType()) {
                    case ACACIA_LEAVES, BIRCH_LEAVES, DARK_OAK_LEAVES, JUNGLE_LEAVES, OAK_LEAVES, SPRUCE_LEAVES ->
                            e.setCancelled(true);
                    default -> e.setCancelled(false);
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
        Player p = e.getPlayer();
        if (pg.isGameServer()) {
            if (pg.pgPlayers.contains(p) || pg.playerLobby.containsKey(p)) {
                if (pg.isLobbySystem()) {
                    String s = null;
                    for (int ii = 1; ii <= 27; ii++) {
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
                                pg.liquidPlaced.put(loc, block);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (pg.isGameServer()) {
            if (pg.pgPlayers.contains(p) || pg.playerLobby.containsKey(p)) {
                if (pg.isLobbySystem()) {
                    String s = null;
                    for (int ii = 1; ii <= 27; ii++) {
                        if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                            s = Integer.toString(ii);
                        }
                    }
                    if (!pg.lobbyBuild.get(s)) {
                        if (pg.lobbyStates.get(s) == GameStates.INGAME) {
                            if (e.getBlock().getType() == Material.COBWEB || e.getBlock().getType() == Material.FIRE || e.getBlock().getType() == Material.CAKE || e.getBlock().getType() == Material.SHORT_GRASS || e.getBlock().getType() == Material.TALL_GRASS || e.getBlock().getType() == Material.DEAD_BUSH || e.getBlock().getType() == Material.ACACIA_LEAVES || e.getBlock().getType() == Material.BIRCH_LEAVES || e.getBlock().getType() == Material.DARK_OAK_LEAVES || e.getBlock().getType() == Material.JUNGLE_LEAVES || e.getBlock().getType() == Material.OAK_LEAVES || e.getBlock().getType() == Material.SPRUCE_LEAVES || e.getBlock().getType() == Material.WARPED_FUNGUS || e.getBlock().getType() == Material.CRIMSON_FUNGUS || e.getBlock().getType() == Material.BROWN_MUSHROOM || e.getBlock().getType() == Material.RED_MUSHROOM) {
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
                            if (e.getBlock().getType() == Material.COBWEB || e.getBlock().getType() == Material.FIRE || e.getBlock().getType() == Material.CAKE || e.getBlock().getType() == Material.SHORT_GRASS || e.getBlock().getType() == Material.TALL_GRASS || e.getBlock().getType() == Material.DEAD_BUSH || e.getBlock().getType() == Material.ACACIA_LEAVES || e.getBlock().getType() == Material.BIRCH_LEAVES || e.getBlock().getType() == Material.DARK_OAK_LEAVES || e.getBlock().getType() == Material.JUNGLE_LEAVES || e.getBlock().getType() == Material.OAK_LEAVES || e.getBlock().getType() == Material.SPRUCE_LEAVES || e.getBlock().getType() == Material.WARPED_FUNGUS || e.getBlock().getType() == Material.CRIMSON_FUNGUS || e.getBlock().getType() == Material.BROWN_MUSHROOM || e.getBlock().getType() == Material.RED_MUSHROOM) {
                                pg.breakedBlocks.put(e.getBlock().getLocation(), e.getBlock().getType());
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
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (pg.isGameServer()) {
            if (pg.pgPlayers.contains(p) || pg.playerLobby.containsKey(p)) {
                if (pg.isLobbySystem()) {
                    String s = null;
                    for (int ii = 1; ii <= 27; ii++) {
                        if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                            s = Integer.toString(ii);
                        }
                    }
                    if (!pg.lobbyBuild.get(s)) {
                        if (pg.lobbyStates.get(s) == GameStates.INGAME) {
                            if (e.getBlock().getType() == Material.COBWEB || e.getBlock().getType() == Material.FIRE || e.getBlock().getType() == Material.CAKE || e.getBlock().getType() == Material.SHORT_GRASS || e.getBlock().getType() == Material.TALL_GRASS || e.getBlock().getType() == Material.DEAD_BUSH || e.getBlock().getType() == Material.ACACIA_LEAVES || e.getBlock().getType() == Material.BIRCH_LEAVES || e.getBlock().getType() == Material.DARK_OAK_LEAVES || e.getBlock().getType() == Material.JUNGLE_LEAVES || e.getBlock().getType() == Material.OAK_LEAVES || e.getBlock().getType() == Material.SPRUCE_LEAVES || e.getBlock().getType() == Material.WARPED_FUNGUS || e.getBlock().getType() == Material.CRIMSON_FUNGUS || e.getBlock().getType() == Material.BROWN_MUSHROOM || e.getBlock().getType() == Material.RED_MUSHROOM) {
                                pg.lobbyPlacedBlocksData.put(e.getBlock().getLocation(), e.getBlock().getType());
                                pg.lobbyPlacedBlocks.put(s, pg.lobbyPlacedBlocksData);
                                e.setCancelled(false);
                            } else if (e.getBlock().getType() == Material.TNT) {
                                e.setCancelled(true);
                                p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                                Entity tnt = e.getBlock().getWorld().spawnEntity(e.getBlock().getLocation(), EntityType.TNT);
                                TNTPrimed tnt2 = (TNTPrimed) tnt;
                                tnt2.setFuseTicks(40);
                            } else {
                                e.setCancelled(true);
                            }
                        }
                    }
                } else {
                    if (!pg.isBuild()) {
                        if (pg.getGamestate() == GameStates.INGAME) {
                            if (e.getBlock().getType() == Material.COBWEB || e.getBlock().getType() == Material.FIRE || e.getBlock().getType() == Material.CAKE || e.getBlock().getType() == Material.SHORT_GRASS || e.getBlock().getType() == Material.TALL_GRASS || e.getBlock().getType() == Material.DEAD_BUSH || e.getBlock().getType() == Material.ACACIA_LEAVES || e.getBlock().getType() == Material.BIRCH_LEAVES || e.getBlock().getType() == Material.DARK_OAK_LEAVES || e.getBlock().getType() == Material.JUNGLE_LEAVES || e.getBlock().getType() == Material.OAK_LEAVES || e.getBlock().getType() == Material.SPRUCE_LEAVES || e.getBlock().getType() == Material.WARPED_FUNGUS || e.getBlock().getType() == Material.CRIMSON_FUNGUS || e.getBlock().getType() == Material.BROWN_MUSHROOM || e.getBlock().getType() == Material.RED_MUSHROOM) {
                                pg.placedBlocks.put(e.getBlock().getLocation(), e.getBlock().getType());
                                e.setCancelled(false);
                            } else if (e.getBlock().getType() == Material.TNT) {
                                e.setCancelled(true);
                                p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                                Entity tnt = e.getBlock().getWorld().spawnEntity(e.getBlock().getLocation(), EntityType.TNT);
                                TNTPrimed tnt2 = (TNTPrimed) tnt;
                                tnt2.setFuseTicks(40);
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
        Player p = e.getEntity();
        if (pg.isGameServer()) {
            if (pg.pgPlayers.contains(p) || pg.playerLobby.containsKey(p)) {
                if (pg.isLobbySystem()) {
                    String s = null;
                    for (int ii = 1; ii <= 27; ii++) {
                        if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                            s = Integer.toString(ii);
                        }
                    }
                    if (!pg.lobbyBuild.get(s)) {
                        if (pg.lobbyStates.get(s) == GameStates.INGAME) {
                            if (p.getKiller() != null) {
                                pg.addDeaths(p.getUniqueId().toString(), 1);
                                pg.addLosses(p.getUniqueId().toString(), 1);
                                pg.addKills(p.getKiller().getUniqueId().toString(), 1);
                                if (pg.isEnableRewards()) {
                                    EconomyResponse r = PotionGames.getEconomy().depositPlayer(p.getKiller(), pg.getKillReward());
                                    if (r.transactionSuccess()) {
                                        Component comp = Settings.prefix
                                            .append(Component.text(pg.chatmessages.get(91)).color(NamedTextColor.AQUA))
                                            .append(Component.text(": ")).append(Component.text(pg.chatmessages.get(94)).color(NamedTextColor.GREEN))
                                            .append(Component.text(" ")).append(Component.text(PotionGames.getEconomy().format(r.amount)).color(NamedTextColor.LIGHT_PURPLE));
                                        p.getKiller().sendMessage(comp);
                                    } else {
                                        Component comp = Settings.prefix
                                            .append(Component.text(pg.chatmessages.get(92)).color(NamedTextColor.RED))
                                            .append(Component.text(": ")).append(Component.text(r.errorMessage));
                                        p.getKiller().sendMessage(comp);
                                    }
                                }
                                if (pg.isActivateScoreboard()) {
                                    Team killsTeam = Objects.requireNonNull(p.getKiller().getScoreboard().getTeam("kills"));
                                    Component tempComponent = killsTeam.prefix();
                                    int tempInt = Integer.parseInt(tempComponent.toString());
                                    tempInt++;
                                    killsTeam.prefix(Component.text(String.valueOf(tempInt)).color(NamedTextColor.DARK_AQUA));
                                }
                            } else {
                                pg.addDeaths(p.getUniqueId().toString(), 1);
                                pg.addLosses(p.getUniqueId().toString(), 1);
                            }
                            if (pg.playerLobby.containsKey(p)) {
                                e.setKeepLevel(true);
                                pg.playerLobby.remove(p);
                                pg.specLobby.put(p, s);
                                if (pg.lobbyActivateTeams.get(s)) {
                                    String teamname = null;
                                    for (int i = 1; i <= pg.lobbyteamAmount.get(s); i++) {
                                        if (pg.lobbyteamplayernames.get(s).containsKey(p) && pg.lobbyteamplayernames.get(s).containsValue(Integer.toString(i))) {
                                            if (Objects.equals(pg.lobbyteamplayernames.get(s).get(p), Integer.toString(i))) {
                                                teamname = Integer.toString(i);
                                            }
                                        }
                                    }
                                    pg.lobbyteamplayernames.get(s).remove(p, teamname);
                                    assert teamname != null;
                                    int teamamount = pg.lobbyteams.get(s).get(Integer.parseInt(teamname));
                                    teamamount--;
                                    pg.lobbyteams.get(s).replace(Integer.parseInt(teamname), teamamount);
                                    if (pg.lobbyteams.get(s).get(Integer.parseInt(teamname)) == 0) {
                                        pg.lobbyteams.get(s).remove(Integer.parseInt(teamname));
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
                                    killer.playSound(killer.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, 1, 1);
                                    p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 1);
                                    if (Objects.equals(pg.kitplayernames.get(p), "Rich Kid")) {
                                        for (int i = 0; i < 10; i++) {
                                            killer.getInventory().addItem(pg.getCoin());
                                        }
                                    } else {
                                        for (int i = 0; i < 5; i++) {
                                            killer.getInventory().addItem(pg.getCoin());
                                        }
                                    }
                                    for (Player all : pg.playerLobby.keySet()) {
                                        if (pg.playerLobby.get(all).equals(s)) {
                                            all.sendMessage(Settings.prefix
                                                .append(Component.text(p.getName()).color(NamedTextColor.DARK_RED))
                                                .append(Component.text(" " + pg.chatmessages.get(9) + " ").color(NamedTextColor.GRAY))
                                                .append(Component.text(killer.getName()).color(NamedTextColor.DARK_GREEN))
                                                .append(Component.text(" [").color(NamedTextColor.GRAY))
                                                .append(Component.text(String.valueOf(player)).color(NamedTextColor.AQUA))
                                                .append(Component.text("/").color(NamedTextColor.GRAY))
                                                .append(Component.text(String.valueOf(amountPlayers)).color(NamedTextColor.AQUA))
                                                .append(Component.text("]").color(NamedTextColor.GRAY)));
                                        }
                                    }
                                    for (Player all : pg.specLobby.keySet()) {
                                        if (pg.specLobby.get(all).equals(s)) {
                                            all.sendMessage(Settings.prefix
                                                .append(Component.text(p.getName()).color(NamedTextColor.DARK_RED))
                                                .append(Component.text(" " + pg.chatmessages.get(9) + " ").color(NamedTextColor.GRAY))
                                                .append(Component.text(killer.getName()).color(NamedTextColor.DARK_GREEN))
                                                .append(Component.text(" [").color(NamedTextColor.GRAY))
                                                .append(Component.text(String.valueOf(player)).color(NamedTextColor.AQUA))
                                                .append(Component.text("/").color(NamedTextColor.GRAY))
                                                .append(Component.text(String.valueOf(amountPlayers)).color(NamedTextColor.AQUA))
                                                .append(Component.text("]").color(NamedTextColor.GRAY)));
                                        }
                                    }
                                    e.deathMessage(null);
                                } catch (Exception ex) {
                                    for (Player all : pg.playerLobby.keySet()) {
                                        if (pg.playerLobby.get(all).equals(s)) {
                                            all.sendMessage(Settings.prefix
                                                .append(Component.text(p.getName()).color(NamedTextColor.DARK_RED))
                                                .append(Component.text(" " + pg.chatmessages.get(10) + " ").color(NamedTextColor.GRAY))
                                                .append(Component.text(" [").color(NamedTextColor.GRAY))
                                                .append(Component.text(String.valueOf(player)).color(NamedTextColor.AQUA))
                                                .append(Component.text("/").color(NamedTextColor.GRAY))
                                                .append(Component.text(String.valueOf(amountPlayers)).color(NamedTextColor.AQUA))
                                                .append(Component.text("]").color(NamedTextColor.GRAY)));
                                        }
                                    }
                                    for (Player all : pg.specLobby.keySet()) {
                                        if (pg.specLobby.get(all).equals(s)) {
                                            all.sendMessage(Settings.prefix
                                                .append(Component.text(p.getName()).color(NamedTextColor.DARK_RED))
                                                .append(Component.text(" " + pg.chatmessages.get(10) + " ").color(NamedTextColor.GRAY))
                                                .append(Component.text(" [").color(NamedTextColor.GRAY))
                                                .append(Component.text(String.valueOf(player)).color(NamedTextColor.AQUA))
                                                .append(Component.text("/").color(NamedTextColor.GRAY))
                                                .append(Component.text(String.valueOf(amountPlayers)).color(NamedTextColor.AQUA))
                                                .append(Component.text("]").color(NamedTextColor.GRAY)));
                                        }
                                    }
                                    e.deathMessage(null);
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
                                pg.addLosses(p.getUniqueId().toString(), 1);
                                pg.addKills(p.getKiller().getUniqueId().toString(), 1);
                                if (pg.isEnableRewards()) {
                                    EconomyResponse r = PotionGames.getEconomy().depositPlayer(p.getKiller(), pg.getKillReward());
                                    if (r.transactionSuccess()) {
                                        Component comp = Settings.prefix
                                            .append(Component.text(pg.chatmessages.get(91)).color(NamedTextColor.AQUA))
                                            .append(Component.text(": ")).append(Component.text(pg.chatmessages.get(94)).color(NamedTextColor.GREEN))
                                            .append(Component.text(" ")).append(Component.text(PotionGames.getEconomy().format(r.amount)).color(NamedTextColor.LIGHT_PURPLE));
                                        p.getKiller().sendMessage(comp);
                                    } else {
                                        Component comp = Settings.prefix
                                            .append(Component.text(pg.chatmessages.get(92)).color(NamedTextColor.RED))
                                            .append(Component.text(": ")).append(Component.text(r.errorMessage));
                                        p.getKiller().sendMessage(comp);
                                    }
                                }
                                if (pg.isActivateScoreboard()) {
                                    Component prefix = Objects.requireNonNull(Objects.requireNonNull(p.getKiller().getScoreboard().getTeam("kills"))).prefix();
                                    prefix = Component.text(Integer.parseInt(prefix.toString()) + 1);
                                    p.getKiller().getScoreboard().getTeam("kills").prefix(prefix.color(NamedTextColor.DARK_AQUA));
                                }
                            } else {
                                pg.addDeaths(p.getUniqueId().toString(), 1);
                                pg.addLosses(p.getUniqueId().toString(), 1);
                            }
                            if (pg.pgPlayers.contains(p)) {
                                e.setKeepLevel(true);
                                pg.pgPlayers.remove(p);
                                pg.specPlayers.add(p);
                                if (pg.isActivateTeams()) {
                                    String teamname = null;
                                    for (int i = 1; i <= pg.getTeamAmount(); i++) {
                                        if (Objects.equals(pg.teamplayernames.get(p), Integer.toString(i))) {
                                            teamname = Integer.toString(i);
                                        }
                                    }
                                    pg.teamplayernames.remove(p, teamname);
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
                                    if (Objects.equals(pg.kitplayernames.get(p), "Rich Kid")) {
                                        for (int i = 0; i < 10; i++) {
                                            killer.getInventory().addItem(pg.getCoin());
                                        }
                                    } else {
                                        for (int i = 0; i < 5; i++) {
                                            killer.getInventory().addItem(pg.getCoin());
                                        }
                                    }
                                    for (Player all : pg.pgPlayers) {
                                        all.sendMessage(Settings.prefix.append(Component.text(p.getName()).color(NamedTextColor.DARK_RED)).append(Component.text(" " + pg.chatmessages.get(9) + " ").color(NamedTextColor.GRAY)).append(Component.text(killer.getName()).color(NamedTextColor.DARK_GREEN)).append(Component.text(" [").color(NamedTextColor.GRAY)).append(Component.text(String.valueOf(player)).color(NamedTextColor.AQUA)).append(Component.text("/").color(NamedTextColor.GRAY)).append(Component.text(String.valueOf(amountPlayers)).color(NamedTextColor.AQUA)).append(Component.text("]").color(NamedTextColor.GRAY)));
                                    }
                                    for (Player all : pg.specPlayers) {
                                        all.sendMessage(Settings.prefix.append(Component.text(p.getName()).color(NamedTextColor.DARK_RED)).append(Component.text(" " + pg.chatmessages.get(9) + " ").color(NamedTextColor.GRAY)).append(Component.text(killer.getName()).color(NamedTextColor.DARK_GREEN)).append(Component.text(" [").color(NamedTextColor.GRAY)).append(Component.text(String.valueOf(player)).color(NamedTextColor.AQUA)).append(Component.text("/").color(NamedTextColor.GRAY)).append(Component.text(String.valueOf(amountPlayers)).color(NamedTextColor.AQUA)).append(Component.text("]").color(NamedTextColor.GRAY)));
                                    }
                                    e.deathMessage(null);
                                } catch (Exception ex) {
                                    for (Player all : pg.pgPlayers) {
                                        all.sendMessage(Settings.prefix.append(Component.text(p.getName()).color(NamedTextColor.DARK_RED)).append(Component.text(" " + pg.chatmessages.get(10) + " ").color(NamedTextColor.GRAY)).append(Component.text(" [").color(NamedTextColor.GRAY)).append(Component.text(String.valueOf(player)).color(NamedTextColor.AQUA)).append(Component.text("/").color(NamedTextColor.GRAY)).append(Component.text(String.valueOf(amountPlayers)).color(NamedTextColor.AQUA)).append(Component.text("]").color(NamedTextColor.GRAY)));
                                    }
                                    for (Player all : pg.specPlayers) {
                                        all.sendMessage(Settings.prefix.append(Component.text(p.getName()).color(NamedTextColor.DARK_RED)).append(Component.text(" " + pg.chatmessages.get(10) + " ").color(NamedTextColor.GRAY)).append(Component.text(" [").color(NamedTextColor.GRAY)).append(Component.text(String.valueOf(player)).color(NamedTextColor.AQUA)).append(Component.text("/").color(NamedTextColor.GRAY)).append(Component.text(String.valueOf(amountPlayers)).color(NamedTextColor.AQUA)).append(Component.text("]").color(NamedTextColor.GRAY)));
                                    }
                                    e.deathMessage(null);
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
    public void onEntityExplode(EntityExplodeEvent e) {
        if (pg.worlds.contains(Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(e.getLocation().getWorld()).getName())))) {
            if (e.getEntity().getType() == EntityType.TNT) {
                ArrayList<Block> destroyed = (ArrayList<Block>) e.blockList();
                destroyed.removeIf(block -> !toDestroy.contains(block.getType()));
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (pg.isGameServer()) {
            if (pg.worlds.contains(e.getDamager().getWorld().getName())) {
                e.setCancelled(e.getDamager() instanceof LightningStrike || e.getDamager() instanceof Firework);
                if (e.getEntity() instanceof Player p && e.getDamager() instanceof TNTPrimed) {
                    if (p.getHealth() - 4 <= 0) {
                        p.setHealth(p.getHealth() - 4);
                    } else {
                        p.setHealth(p.getHealth() - 4 <= 0 ? 0D : p.getHealth() - 4);
                    }
                }
                if (!pg.isFriendlyFire()) {
                    if (e.getEntity() instanceof Player p && e.getDamager() instanceof Player d) {
                        if (!pg.isLobbySystem()) {
                            if (Objects.equals(pg.teamplayernames.get(p), pg.teamplayernames.get(d))) {
                                e.setCancelled(true);
                            }
                        } else {
                            String s = null;
                            for (int ii = 1; ii <= 27; ii++) {
                                if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                                    s = Integer.toString(ii);
                                }
                            }
                            if (Objects.equals(pg.lobbyteamplayernames.get(s).get(p), pg.lobbyteamplayernames.get(s).get(d))) {
                                e.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (pg.isGameServer()) {
            if (pg.pgPlayers.contains(p) || pg.playerLobby.containsKey(p)) {
                if (e.getAction() == Action.PHYSICAL && Objects.requireNonNull(e.getClickedBlock()).getType() == Material.FARMLAND) {
                    e.setCancelled(true);
                }
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (e.getHand() == EquipmentSlot.HAND) {
                        if ((Objects.requireNonNull(e.getClickedBlock())).getType().toString().equals(Objects.requireNonNull(Settings.chestdata.get("pg.chestblocks.normal")).toString())) {
                            if (pg.isLobbySystem()) {
                                String s = null;
                                for (int ii = 1; ii <= 27; ii++) {
                                    if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
                                if (pg.lobbyStates.get(s) == GameStates.INGAME) {
                                    if (!pg.lobbychests.containsKey(e.getClickedBlock().getLocation())) {
                                        Inventory inv;
                                        inv = Bukkit.createInventory(p, 27, Settings.prefix);
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
                                                if (pg.lobbyActivateShop.get(s)) {
                                                    ArrayList<ItemStack> potions1 = new ArrayList<>();
                                                    potions1.add(new ItemStack(Material.GLASS_BOTTLE, 1));
                                                    ArrayList<ItemStack> potions2 = new ArrayList<>();
                                                    potions2.add(pg.getCoin());
                                                    int item = rnd.nextInt(5);
                                                    if (item < 3) {
                                                        int item1 = rnd.nextInt(pg.food1.size());
                                                        inv.setItem(slot, pg.food1.get(item1));
                                                    } else if (item < 4) {
                                                        int item1 = 0;
                                                        inv.setItem(slot, potions1.get(item1));
                                                    } else {
                                                        int item1 = 0;
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
                                        inv = Bukkit.createInventory(p, 27, Settings.prefix);
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
                                                        int item1 = 0;
                                                        inv.setItem(slot, potions1.get(item1));
                                                    } else {
                                                        int item1 = 0;
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
                        while (Settings.chestdata.contains("pg.customchests." + chestnumber)) {
                            if (e.getClickedBlock().getType().toString().equals(Objects.requireNonNull(Settings.chestdata.get("pg.customchests." + chestnumber + ".chesttype")).toString())) {
                                if (Settings.chestdata.getBoolean("pg.customchests." + chestnumber + ".activate")) {
                                    if (pg.isLobbySystem()) {
                                        String s = null;
                                        for (int ii = 1; ii <= 27; ii++) {
                                            if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                                                s = Integer.toString(ii);
                                            }
                                        }
                                        if (pg.lobbyStates.get(s) == GameStates.INGAME) {
                                            if (!pg.lobbychests.containsKey(e.getClickedBlock().getLocation())) {
                                                Inventory inv;
                                                inv = Bukkit.createInventory(p, Settings.chestdata.getInt("pg.customchests." + chestnumber + "." + ".chestsize"), Settings.prefix);
                                                pg.lobbychests.put(e.getClickedBlock().getLocation(), s);
                                                pg.lobbychestsdata.put(e.getClickedBlock().getLocation(), inv);
                                                p.openInventory(pg.lobbychestsdata.get(e.getClickedBlock().getLocation()));
                                                while (Settings.chestdata.contains("pg.customchests." + chestnumber + "." + chestitem)) {
                                                    inv.setItem(Settings.chestdata.getInt("pg.customchests." + chestnumber + "." + chestitem + ".slot") - 1, Settings.chestdata.getItemStack("pg.customchests." + chestnumber + "." + chestitem + ".item"));
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
                                                inv = Bukkit.createInventory(p, Settings.chestdata.getInt("pg.customchests." + chestnumber + "." + ".chestsize"), Settings.prefix);
                                                pg.chests.put(e.getClickedBlock().getLocation(), inv);
                                                p.openInventory(pg.chests.get(e.getClickedBlock().getLocation()));
                                                while (Settings.chestdata.contains("pg.customchests." + chestnumber + "." + chestitem)) {
                                                    inv.setItem(Settings.chestdata.getInt("pg.customchests." + chestnumber + "." + chestitem + ".slot") - 1, Settings.chestdata.getItemStack("pg.customchests." + chestnumber + "." + chestitem + ".item"));
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
                        if ((e.getClickedBlock()).getType().toString().equals(Objects.requireNonNull(Settings.chestdata.get("pg.chestblocks.shop")).toString())) {
                            if (pg.isLobbySystem()) {
                                String s = null;
                                for (int ii = 1; ii <= 27; ii++) {
                                    if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
                                if (pg.lobbyActivateShop.get(s)) {
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
                                        inv = Bukkit.createInventory(p, 9 * 3, Settings.prefix.append(Component.text(pg.chatmessages.get(49))).color(NamedTextColor.DARK_AQUA));
                                        pg.lobbychests.put(e.getClickedBlock().getLocation(), s);
                                        pg.lobbychestsdata.put(e.getClickedBlock().getLocation(), inv);
                                        int shopitem = 1;
                                        for (int i = 0; i < pg.getActivePotions(); i++) {
                                            int coinamount;
                                            if (pg.kitplayernames.containsKey(p) && pg.kitplayernames.containsValue(pg.shopkit.get(shopitem - 1))) {
                                                coinamount = pg.shopsale.get(shopitem - 1);
                                            } else {
                                                coinamount = pg.shopcost.get(shopitem - 1);
                                            }
                                            ItemStack randombarrier = new ItemStack(pg.shoppotiontype.get(shopitem - 1));
                                            ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                            assert randombarriermeta != null;
                                            randombarriermeta.displayName(Component.text(pg.shop.get(shopitem - 1)));
                                            ArrayList<Component> lore = new ArrayList<>();
                                            lore.add(Component.text(pg.chatmessages.get(50) + ": " + pg.shoppotion.get(shopitem - 1).getDuration() / 20));
                                            lore.add(Component.text(pg.chatmessages.get(51) + ": " + coinamount + " " + pg.chatmessages.get(52)));
                                            randombarriermeta.lore(lore);
                                            randombarrier.setItemMeta(randombarriermeta);
                                            inv.setItem(shopitem - 1, randombarrier);
                                            shopitem++;
                                        }
                                    }
                                    p.openInventory(pg.lobbychestsdata.get(e.getClickedBlock().getLocation()));
                                }
                            } else {
                                if (pg.isActivateShop()) {
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
                                        inv = Bukkit.createInventory(p, 9 * 3, Settings.prefix.append(Component.text(pg.chatmessages.get(49))).color(NamedTextColor.DARK_AQUA));
                                        pg.chests.put(e.getClickedBlock().getLocation(), inv);
                                        int shopitem = 1;
                                        for (int i = 0; i < pg.getActivePotions(); i++) {
                                            int coinamount;
                                            if (pg.kitplayernames.containsKey(p) && pg.kitplayernames.containsValue(pg.shopkit.get(shopitem - 1))) {
                                                coinamount = pg.shopsale.get(shopitem - 1);
                                            } else {
                                                coinamount = pg.shopcost.get(shopitem - 1);
                                            }
                                            ItemStack randombarrier = new ItemStack(pg.shoppotiontype.get(shopitem - 1));
                                            ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                            assert randombarriermeta != null;
                                            randombarriermeta.displayName(Component.text(pg.shop.get(shopitem - 1)));
                                            ArrayList<Component> lore = new ArrayList<>();
                                            lore.add(Component.text(pg.chatmessages.get(50) + ": " + pg.shoppotion.get(shopitem - 1).getDuration() / 20));
                                            lore.add(Component.text(pg.chatmessages.get(51) + ": " + coinamount + " " + pg.chatmessages.get(52)));
                                            randombarriermeta.lore(lore);
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
                            for (int ii = 1; ii <= 27; ii++) {
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
                                pg.waterBlocks.put(e.getClickedBlock().getLocation(), e.getClickedBlock().getBlockData());
                            }
                        }
                    }
                }
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                    if (e.getHand() == EquipmentSlot.HAND) {
                        if (p.getInventory().getItemInMainHand().getType() == Material.MUSHROOM_STEW || p.getInventory().getItemInMainHand().getType() == Material.RABBIT_STEW || p.getInventory().getItemInMainHand().getType() == Material.BEETROOT_SOUP) {
                            if (pg.isLobbySystem()) {
                                String s = null;
                                for (int ii = 1; ii <= 27; ii++) {
                                    if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
                                if (pg.lobbyStates.get(s) == GameStates.INGAME) {
                                    double health = p.getHealth();
                                    int foodlvl = p.getFoodLevel();
                                    if (health >= 20 && foodlvl >= 13) {
                                        p.setFoodLevel(20);
                                        p.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                                    } else if (foodlvl < 13) {
                                        p.setFoodLevel(foodlvl + 7);
                                        p.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                                    } else {
                                        if (health < 20 && health >= 13) {
                                            p.setHealth(20);
                                            p.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                                        } else if (health < 13) {
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
                                    } else if (foodlvl < 13) {
                                        p.setFoodLevel(foodlvl + 7);
                                        p.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                                    } else {
                                        if (health < 20 && health >= 13) {
                                            p.setHealth(20);
                                            p.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                                        } else if (health < 13) {
                                            p.setHealth(health + 7);
                                            p.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                                        }
                                    }
                                }
                            }
                        }
                        if (p.getInventory().getItemInMainHand().getType() == Material.REDSTONE_TORCH) {
                            if (pg.isLobbySystem()) {
                                String s = null;
                                for (int ii = 1; ii <= 27; ii++) {
                                    if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
                                if (pg.lobbyActivateAirdrops.get(s)) {
                                    if (pg.lobbyStates.get(s) == GameStates.INGAME) {
                                        boolean blocked = false;
                                        Location loc = p.getEyeLocation().add(0, 1, 0);
                                        while (loc.getY() <= 320) {
                                            if (loc.getBlock().getType() != Material.AIR) {
                                                p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(96)).color(NamedTextColor.RED)));
                                                blocked = true;
                                                break;
                                            }
                                            loc.add(0, 1, 0);
                                        }
                                        if (!blocked) {
                                            for (Player all : pg.playerLobby.keySet()) {
                                                if (pg.playerLobby.get(all).equals(s)) {
                                                    all.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(98) + ": ").color(NamedTextColor.GREEN)).append(Component.text(p.getLocation().getBlockX() + " " + p.getLocation().getBlockY() + " " + p.getLocation().getBlockZ()).color(NamedTextColor.AQUA)));
                                                }
                                            }
                                            p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(97)).color(NamedTextColor.GREEN)));
                                            BlockData b = Material.DRIED_KELP_BLOCK.createBlockData();
                                            Location spawnLoc = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() + 100, p.getLocation().getZ());
                                            p.getWorld().spawn(spawnLoc, FallingBlock.class, fallingBlock -> fallingBlock.setBlockData(b));
                                            pg.lobbyPlacedBlocksData.put(p.getLocation(), b.getMaterial());
                                            pg.lobbyPlacedBlocks.put(s, pg.lobbyPlacedBlocksData);
                                            p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                                        }
                                    }
                                }
                            } else {
                                if (pg.isActivateAirdrops()) {
                                    if (pg.getGamestate() == GameStates.INGAME) {
                                        boolean blocked = false;
                                        Location loc = p.getEyeLocation().add(0, 1, 0);
                                        while (loc.getY() < 256) {
                                            if (loc.getBlock().getType() != Material.AIR) {
                                                p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(96)).color(NamedTextColor.RED)));
                                                blocked = true;
                                                break;
                                            }
                                            loc.add(0, 1, 0);
                                        }
                                        if (!blocked) {
                                            for (Player all : pg.pgPlayers) {
                                                all.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(98) + ": ").color(NamedTextColor.GREEN)).append(Component.text(p.getLocation().getBlockX() + " " + p.getLocation().getBlockY() + " " + p.getLocation().getBlockZ()).color(NamedTextColor.AQUA)));
                                            }
                                            p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(97)).color(NamedTextColor.GREEN)));
                                            BlockData b = Material.DRIED_KELP_BLOCK.createBlockData();
                                            Location spawnLoc = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() + 100, p.getLocation().getZ());
                                            p.getWorld().spawn(spawnLoc, FallingBlock.class, fallingBlock -> fallingBlock.setBlockData(b));
                                            pg.placedBlocks.put(p.getLocation(), b.getMaterial());
                                            p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                                        }
                                    }
                                }
                            }
                        }
                        if (p.getInventory().getItemInMainHand().getType() == Material.MILK_BUCKET) {
                            if (pg.isLobbySystem()) {
                                String s = null;
                                for (int ii = 1; ii <= 27; ii++) {
                                    if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
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
                                String s = null;
                                for (int ii = 1; ii <= 27; ii++) {
                                    if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
                                if (pg.lobbyStates.get(s) == GameStates.INGAME) {
                                    Player result = null;
                                    double lastDistance = Double.MAX_VALUE;
                                    for (Player cp : p.getWorld().getPlayers()) {
                                        if (pg.playerLobby.containsKey(cp) && !Objects.equals(pg.lobbyteamplayernames.get(s).get(p), pg.lobbyteamplayernames.get(s).get(cp))) {
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
                                        p.sendActionBar(Settings.prefix.append(Component.text(pg.chatmessages.get(12) + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf((int) lastDistance)).color(NamedTextColor.AQUA)));
                                    } else {
                                        p.sendActionBar(Settings.prefix.append(Component.text(pg.chatmessages.get(13)).color(NamedTextColor.RED))); 
                                    }
                                }
                            } else {
                                if (pg.getGamestate() == GameStates.INGAME) {
                                    Player result = null;
                                    double lastDistance = Double.MAX_VALUE;
                                    for (Player cp : p.getWorld().getPlayers()) {
                                        if (pg.pgPlayers.contains(cp) && !Objects.equals(pg.teamplayernames.get(p), pg.teamplayernames.get(cp))) {
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
                                        p.sendActionBar(Settings.prefix.append(Component.text(pg.chatmessages.get(12) + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf((int) lastDistance)).color(NamedTextColor.AQUA)));
                                    } else {
                                        p.sendActionBar(Settings.prefix.append(Component.text(pg.chatmessages.get(13)).color(NamedTextColor.RED)));
                                    }
                                }
                            }
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.PAPER) {
                        if (pg.isLobbySystem()) {
                            String s = null;
                            for (int ii = 1; ii <= 27; ii++) {
                                if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                                    s = Integer.toString(ii);
                                }
                            }
                            if (pg.lobbyStates.get(s) == GameStates.WAITING || pg.lobbyStates.get(s) == GameStates.PREPARING) {
                                ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
                                ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                assert randombarriermeta != null;
                                randombarriermeta.displayName(Messages.RandomLabel());
                                ArrayList<Component> randomlore = new ArrayList<>();
                                randomlore.add(0, Component.text(pg.chatmessages.get(15) + ": ").color(NamedTextColor.GREEN).append(Component.text(String.valueOf(pg.lobbyvotes.get(s).get("Random"))).color(NamedTextColor.AQUA)));
                                randombarriermeta.lore(randomlore);
                                randombarrier.setItemMeta(randombarriermeta);
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, Messages.ArenaSelectorTitle());
                                inv.setItem(0, randombarrier);
                                int slot = 1;
                                for (String all : pg.lobbyvotes.get(s).keySet()) {
                                    if (!all.matches("Random")) {
                                        ArrayList<Component> arenalore = new ArrayList<>();
                                        arenalore.add(0, Component.text(pg.chatmessages.get(15) + ": ").color(NamedTextColor.GREEN).append(Component.text(String.valueOf(pg.lobbyvotes.get(s).get(all))).color(NamedTextColor.AQUA)));
                                        ItemStack arenamap = new ItemStack(Material.MAP);
                                        ItemMeta arenamapmeta = arenamap.getItemMeta();
                                        assert arenamapmeta != null;
                                        arenamapmeta.displayName(Component.text(all).color(NamedTextColor.AQUA));
                                        arenamapmeta.lore(arenalore);
                                        arenamap.setItemMeta(arenamapmeta);
                                        inv.setItem(slot, arenamap);
                                        slot++;
                                    }
                                }
                                p.openInventory(inv);
                            }
                        } else {
                            if (pg.getGamestate() == GameStates.WAITING || pg.getGamestate() == GameStates.PREPARING) {
                                ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
                                ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                assert randombarriermeta != null;
                                randombarriermeta.displayName(Messages.RandomLabel());
                                ArrayList<Component> randomlore = new ArrayList<>();
                                randomlore.add(0, Component.text(pg.chatmessages.get(15) + ": ").color(NamedTextColor.GREEN).append(Component.text(String.valueOf(pg.votes.get("Random"))).color(NamedTextColor.AQUA)));
                                randombarriermeta.lore(randomlore);
                                randombarrier.setItemMeta(randombarriermeta);
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, Messages.ArenaSelectorTitle());
                                inv.setItem(0, randombarrier);
                                int slot = 1;
                                for (String all : pg.arenas) {
                                    if (!all.matches("Random")) {
                                        ArrayList<Component> arenalore = new ArrayList<>();
                                        arenalore.add(0, Component.text(pg.chatmessages.get(15) + ": ").color(NamedTextColor.GREEN).append(Component.text(String.valueOf(pg.votes.get(all))).color(NamedTextColor.AQUA)));
                                        ItemStack arenamap = new ItemStack(Material.MAP);
                                        ItemMeta arenamapmeta = arenamap.getItemMeta();
                                        assert arenamapmeta != null;
                                        arenamapmeta.displayName(Component.text(all).color(NamedTextColor.AQUA));
                                        arenamapmeta.lore(arenalore);
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
                            for (int ii = 1; ii <= 27; ii++) {
                                if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                                    s = Integer.toString(ii);
                                }
                            }
                            if (pg.lobbyStates.get(s) == GameStates.WAITING || pg.lobbyStates.get(s) == GameStates.PREPARING) {
                                ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
                                ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                assert randombarriermeta != null;
                                randombarriermeta.displayName(Messages.RandomLabel());
                                randombarrier.setItemMeta(randombarriermeta);
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, Settings.prefix.append(Component.text(pg.chatmessages.get(43)).color(NamedTextColor.DARK_AQUA)));
                                inv.setItem(0, randombarrier);
                                int slot = 1;
                                for (Integer all : pg.lobbyteams.get(s).keySet()) {
                                    ArrayList<Component> arenalore = new ArrayList<>();
                                    arenalore.add(0, Component.text(pg.chatmessages.get(44) + ": ").color(NamedTextColor.GREEN).append(Component.text(String.valueOf(pg.lobbyteams.get(s).get(all))).color(NamedTextColor.AQUA)));
                                    ItemStack arenamap = new ItemStack(Material.PLAYER_HEAD);
                                    ItemMeta arenamapmeta = arenamap.getItemMeta();
                                    assert arenamapmeta != null;
                                    arenamapmeta.displayName(Component.text(Integer.toString(all)).color(NamedTextColor.AQUA));
                                    for (Player temp : pg.lobbyteamplayernames.get(s).keySet()) {
                                        if (pg.lobbyteamplayernames.get(s).get(temp).equals(Integer.toString(all)) && temp != null) {
                                            arenalore.add(Component.text(temp.getName()).color(NamedTextColor.GRAY));
                                        }
                                    }
                                    arenamapmeta.lore(arenalore);
                                    arenamap.setItemMeta(arenamapmeta);
                                    inv.setItem(slot, arenamap);
                                    slot++;
                                }
                                p.openInventory(inv);
                            }
                        } else {
                            if (pg.getGamestate() == GameStates.WAITING || pg.getGamestate() == GameStates.PREPARING) {
                                ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
                                ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                assert randombarriermeta != null;
                                randombarriermeta.displayName(Messages.RandomLabel());
                                randombarrier.setItemMeta(randombarriermeta);
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, Settings.prefix.append(Component.text(pg.chatmessages.get(43)).color(NamedTextColor.DARK_AQUA)));
                                inv.setItem(0, randombarrier);
                                int slot = 1;
                                for (String all : pg.teams) {
                                    ArrayList<Component> arenalore = new ArrayList<>();
                                    arenalore.add(0, Component.text(pg.chatmessages.get(44) + ": ").color(NamedTextColor.GREEN).append(Component.text(String.valueOf(pg.teamplayers.get(all))).color(NamedTextColor.AQUA)));
                                    ItemStack arenamap = new ItemStack(Material.PLAYER_HEAD);
                                    ItemMeta arenamapmeta = arenamap.getItemMeta();
                                    assert arenamapmeta != null;
                                    arenamapmeta.displayName(Component.text(all).color(NamedTextColor.AQUA));
                                    for (Player temp : pg.teamplayernames.keySet()) {
                                        if (pg.teamplayernames.get(temp).equals(all) && temp != null) {
                                            arenalore.add(Component.text(temp.getName()).color(NamedTextColor.GRAY));
                                        }
                                    }
                                    arenamapmeta.lore(arenalore);
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
                            String s = null;
                            for (int ii = 1; ii <= 27; ii++) {
                                if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                                    s = Integer.toString(ii);
                                }
                            }
                            if (pg.lobbyStates.get(s) == GameStates.WAITING || pg.lobbyStates.get(s) == GameStates.PREPARING) {
                                ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
                                ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                assert randombarriermeta != null;
                                randombarriermeta.displayName(Messages.RandomLabel());
                                randombarrier.setItemMeta(randombarriermeta);
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, Settings.prefix.append(Component.text(pg.chatmessages.get(62)).color(NamedTextColor.DARK_AQUA)));
                                inv.setItem(0, randombarrier);
                                for (int i = 1; i <= pg.getActiveKits(); i++) {
                                    ItemStack arenamap = new ItemStack(Material.ARMOR_STAND);
                                    ItemMeta arenamapmeta = arenamap.getItemMeta();
                                    assert arenamapmeta != null;
                                    arenamapmeta.displayName(Component.text(Settings.kitdata.getString("pg.kits." + i + ".name")));
                                    arenamap.setItemMeta(arenamapmeta);
                                    inv.setItem(i, arenamap);
                                }
                                p.openInventory(inv);
                            }
                        } else {
                            if (pg.getGamestate() == GameStates.WAITING || pg.getGamestate() == GameStates.PREPARING) {
                                ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
                                ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                assert randombarriermeta != null;
                                randombarriermeta.displayName(Messages.RandomLabel());
                                randombarrier.setItemMeta(randombarriermeta);
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, Settings.prefix.append(Component.text(pg.chatmessages.get(62)).color(NamedTextColor.DARK_AQUA)));
                                inv.setItem(0, randombarrier);
                                for (int i = 1; i <= pg.getActiveKits(); i++) {
                                    ItemStack arenamap = new ItemStack(Material.ARMOR_STAND);
                                    ItemMeta arenamapmeta = arenamap.getItemMeta();
                                    assert arenamapmeta != null;
                                    arenamapmeta.displayName(Component.text(Settings.kitdata.getString("pg.kits." + i + ".name")));
                                    arenamap.setItemMeta(arenamapmeta);
                                    inv.setItem(i, arenamap);
                                }
                                p.openInventory(inv);
                            }
                        }
                    }
                }
                if (p.getInventory().getItemInMainHand().getType() == Material.MAGMA_CREAM) {
                    if (pg.isLobbySystem()) {
                        String s = null;
                        for (int ii = 1; ii <= 27; ii++) {
                            if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                                s = Integer.toString(ii);
                            }
                        }
                        if (pg.lobbyStates.get(s) == GameStates.WAITING || pg.lobbyStates.get(s) == GameStates.PREPARING) {
                            pg.onLeaveLobby(p, s);
                        }
                    } else {
                        if (pg.getGamestate() == GameStates.WAITING || pg.getGamestate() == GameStates.PREPARING) {
                            pg.onLeave(p);
                        }
                    }
                }
                if (p.getInventory().getItemInMainHand().getType() == Material.EMERALD) {
                    if (p.hasPermission("pg.stats")) {
                        int wins = pg.getWins(p.getUniqueId().toString());
                        int losses = pg.getLosses(p.getUniqueId().toString());
                        int rounds = pg.getRounds(p.getUniqueId().toString());
                        int kills = pg.getKills(p.getUniqueId().toString());
                        int deaths = pg.getDeaths(p.getUniqueId().toString());
                        double kd = pg.getKD(p.getUniqueId().toString());
                        p.sendMessage(Messages.StatsLabel());
                        p.sendMessage(Messages.RoundsLabel(rounds));
                        p.sendMessage(Messages.WinsLabel(wins));
                        p.sendMessage(Messages.LossesLabel(losses));
                        p.sendMessage(Messages.KillsLabel(kills));
                        p.sendMessage(Messages.DeathsLabel(deaths));
                        p.sendMessage(Messages.KDLabel(kd));
                        p.sendMessage(Messages.StatsLabel());
                    }
                }
            }
            if (e.getAction().equals(Action.LEFT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_AIR)) {
                if (e.getHand() == EquipmentSlot.HAND) {
                    if (p.getInventory().getItemInMainHand().getType() == Material.STICK) {
                        if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).displayName().equals(Component.text("Add(Left)/Del(Right) Lobby").color(NamedTextColor.DARK_AQUA))) {
                            if (p.hasPermission("pg.setup")) {
                                if (!pg.isLobbySystem()) {
                                    pg.getConfig().set("pg.Lobby.world", Objects.requireNonNull(p.getLocation().getWorld()).getName());
                                    pg.getConfig().set("pg.Lobby.coords", Objects.requireNonNull(p.getLocation()));
                                    pg.saveConfig();
                                    p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(24)).color(NamedTextColor.GREEN)));
                                }
                            }
                            if (p.hasPermission("pg.setup")) {
                                if (pg.isLobbySystem()) {
                                    p.getInventory().clear();
                                    pg.setAddlobby(true);
                                    e.setCancelled(true);
                                    p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(69)).color(NamedTextColor.GREEN)));
                                }
                            }
                        } else if (p.getInventory().getItemInMainHand().getItemMeta().displayName().equals(Component.text("Add(Left)/Del(Right) Arena").color(NamedTextColor.DARK_AQUA))) {
                            p.getInventory().clear();
                            pg.setAddarena(true);
                            p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(70)).color(NamedTextColor.GREEN)));
                        } else if (p.getInventory().getItemInMainHand().getItemMeta().displayName().equals(Component.text("Add(Left)/Del(Right) Spawn").color(NamedTextColor.DARK_AQUA))) {
                            if (p.hasPermission("pg.setup")) {
                                if (!pg.isLobbySystem()) {
                                    int spawnNumber = 1;
                                    int arenaNumber = 1;
                                    try {
                                        int i = 1;
                                        boolean arenaName = false;
                                        while (!arenaName) {
                                            if (arena.toString().matches(Objects.requireNonNull(Settings.arenadata.getString("pg.arenas." + i + ".name")))) {
                                                arenaNumber = i;
                                                arenaName = true;
                                            } else {
                                                i++;
                                            }
                                        }
                                        while (Settings.arenadata.contains("pg.arenas." + arenaNumber + ".spawns." + spawnNumber)) {
                                            spawnNumber++;
                                        }
                                        Settings.arenadata.set("pg.arenas." + arenaNumber + ".spawns." + spawnNumber, p.getLocation());
                                        Settings.arenadata.save(Settings.arenadatafile);
                                        p.sendMessage(Settings.prefix.append(Component.text(String.valueOf(spawnNumber)).color(NamedTextColor.AQUA)).append(Component.text(" " + pg.chatmessages.get(29)).color(NamedTextColor.GREEN)));
                                    } catch (Exception ex) {
                                        p.sendMessage(Settings.prefix.append(Component.text(String.valueOf(spawnNumber)).color(NamedTextColor.AQUA)).append(Component.text(" " + pg.chatmessages.get(31)).color(NamedTextColor.RED)));
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
                                            if (arena.toString().matches(Objects.requireNonNull(Settings.arenadata.getString("pg.lobbies." + lobby + "." + i + ".name")))) {
                                                arenaNumber = i;
                                                arenaName = true;
                                            } else {
                                                i++;
                                            }
                                        }
                                        while (Settings.arenadata.contains("pg.lobbies." + lobby + "." + arenaNumber + ".spawns." + spawnNumber)) {
                                            spawnNumber++;
                                        }
                                        Settings.arenadata.set("pg.lobbies." + lobby + "." + arenaNumber + ".spawns." + spawnNumber, p.getLocation());
                                        Settings.arenadata.save(Settings.arenadatafile);
                                        p.sendMessage(Settings.prefix.append(Component.text(String.valueOf(spawnNumber)).color(NamedTextColor.AQUA)).append(Component.text(" " + pg.chatmessages.get(29)).color(NamedTextColor.GREEN)).append(Component.text(" (Lobby: " + lobby + ") (Arena: " + arena + ")").color(NamedTextColor.GRAY)));
                                    } catch (Exception ex) {
                                        p.sendMessage(Settings.prefix.append(lobby.color(NamedTextColor.AQUA)).append(Component.text(" " + pg.chatmessages.get(31)).color(NamedTextColor.RED)).append(Component.text(" (Lobby: " + lobby + ") (Arena: " + arena + ")").color(NamedTextColor.GRAY)));
                                    }
                                }
                            }
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.CLOCK) {
                        if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).displayName().equals(Component.text("Choose Lobby").color(NamedTextColor.DARK_AQUA))) {
                            if (pg.isLobbySystem()) {
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, Settings.prefix.append(Component.text("Choose Lobby").color(NamedTextColor.DARK_AQUA)));
                                for (int slot = 1; slot <= 27; slot++) {
                                    if (Settings.arenadata.contains("pg.lobbies." + slot)) {
                                        ArrayList<Component> arenalore = new ArrayList<>();
                                        ItemStack arenamap = new ItemStack(Material.MAP);
                                        ItemMeta arenamapmeta = arenamap.getItemMeta();
                                        assert arenamapmeta != null;
                                        arenamapmeta.displayName(Component.text(Integer.toString(slot)));
                                        arenamapmeta.lore(arenalore);
                                        arenamap.setItemMeta(arenamapmeta);
                                        inv.setItem(slot - 1, arenamap);
                                    }
                                }
                                p.openInventory(inv);
                            }
                        } else if (p.getInventory().getItemInMainHand().getItemMeta().displayName().equals(Component.text("Choose Arena").color(NamedTextColor.DARK_AQUA))) {
                            Inventory inv = Bukkit.createInventory(null, 9 * 3, Settings.prefix.append(Component.text("Choose Arena").color(NamedTextColor.DARK_AQUA)));
                            if (pg.isLobbySystem()) {
                                for (int slot = 1; slot < 27; slot++) {
                                    if (Settings.arenadata.contains("pg.lobbies." + lobby + "." + slot)) {
                                        ArrayList<Component> arenalore = new ArrayList<>();
                                        ItemStack arenamap = new ItemStack(Material.MAP);
                                        ItemMeta arenamapmeta = arenamap.getItemMeta();
                                        assert arenamapmeta != null;
                                        arenamapmeta.displayName(Component.text(Settings.arenadata.getString("pg.lobbies." + lobby + "." + slot + ".name")));
                                        arenamapmeta.lore(arenalore);
                                        arenamap.setItemMeta(arenamapmeta);
                                        inv.setItem(slot - 1, arenamap);
                                    }
                                }
                            } else {
                                for (int slot = 1; slot < 27; slot++) {
                                    if (Settings.arenadata.contains("pg.arenas." + slot)) {
                                        ArrayList<Component> arenalore = new ArrayList<>();
                                        ItemStack arenamap = new ItemStack(Material.MAP);
                                        ItemMeta arenamapmeta = arenamap.getItemMeta();
                                        assert arenamapmeta != null;
                                        arenamapmeta.displayName(Component.text(Settings.arenadata.getString("pg.arenas." + slot + ".name")));
                                        arenamapmeta.lore(arenalore);
                                        arenamap.setItemMeta(arenamapmeta);
                                        inv.setItem(slot - 1, arenamap);
                                    }
                                }
                            }
                            p.openInventory(inv);
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.OAK_SIGN) {
                        if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).displayName().equals(Component.text("Set Join-Sign").color(NamedTextColor.DARK_AQUA))) {
                            if (p.getTargetBlock(null, 5).getState() instanceof org.bukkit.block.Sign) {
                                if (p.hasPermission("pg.setup")) {
                                    if (!pg.isLobbySystem()) {
                                        pg.getConfig().set("pg.Lobby.sign", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                                        pg.saveConfig();
                                        p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(35)).color(NamedTextColor.GREEN)));
                                    }
                                }
                                if (p.hasPermission("pg.setup")) {
                                    if (pg.isLobbySystem()) {
                                        Settings.arenadata.set("pg.lobbies." + lobby + ".sign", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                                        try {
                                            Settings.arenadata.save(Settings.arenadatafile);
                                        } catch (IOException ex) {
                                            Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + pg.chatmessages.get(63) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
                                        }
                                        p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(35)).color(NamedTextColor.GREEN)).append(Component.text(" (Lobby: ")).append(lobby).append(Component.text(")")).color(NamedTextColor.GRAY));
                                    }
                                }
                            }
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.BARRIER) {
                        if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).displayName().equals(Component.text("Leave Setup-Mode").color(NamedTextColor.DARK_AQUA))) {
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
                            pg.setupPlayer.remove(p);
                        }
                    }
                }
            }
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                if (e.getHand() == EquipmentSlot.HAND) {
                    if (p.getInventory().getItemInMainHand().getType() == Material.STICK) {
                        if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).displayName().equals(Component.text("Add(Left)/Del(Right) Lobby").color(NamedTextColor.DARK_AQUA))) {
                            if (pg.isLobbySystem()) {
                                p.getInventory().clear();
                                pg.setDellobby(true);
                                p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(71)).color(NamedTextColor.GREEN)));
                            }
                        } else if (p.getInventory().getItemInMainHand().getItemMeta().displayName().equals(Component.text("Add(Left)/Del(Right) Arena").color(NamedTextColor.DARK_AQUA))) {
                            p.getInventory().clear();
                            pg.setDelarena(true);
                            p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(72)).color(NamedTextColor.GREEN)));
                        } else if (p.getInventory().getItemInMainHand().getItemMeta().displayName().equals(Component.text("Add(Left)/Del(Right) Spawn").color(NamedTextColor.DARK_AQUA))) {
                            if (p.hasPermission("pg.setup")) {
                                if (!pg.isLobbySystem()) {
                                    int arenaNumber = 1;
                                    int spawnNumber = 1;
                                    try {
                                        int i = 1;
                                        boolean arenaName = false;
                                        while (!arenaName) {
                                            if (arena.toString().matches(Objects.requireNonNull(Settings.arenadata.getString("pg.arenas." + i + ".name")))) {
                                                arenaNumber = i;
                                                arenaName = true;
                                            } else {
                                                i++;
                                            }
                                        }
                                        int max = 1;
                                        while (Settings.arenadata.contains("pg.arenas." + arenaNumber + ".spawns." + max)) {
                                            spawnNumber = max;
                                            max++;
                                        }
                                        Settings.arenadata.set("pg.arenas." + arenaNumber + ".spawns." + spawnNumber, null);
                                        Settings.arenadata.save(Settings.arenadatafile);
                                        p.sendMessage(Settings.prefix.append(Component.text(String.valueOf(spawnNumber)).color(NamedTextColor.AQUA)).append(Component.text(" " + pg.chatmessages.get(28)).color(NamedTextColor.GREEN)));
                                    } catch (Exception ex) {
                                        p.sendMessage(Settings.prefix.append(arena.color(NamedTextColor.AQUA)).append(Component.text(" " + pg.chatmessages.get(31)).color(NamedTextColor.RED)));
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
                                            if (arena.toString().matches(Objects.requireNonNull(Settings.arenadata.getString("pg.lobbies." + lobby + "." + i + ".name")))) {
                                                arenaNumber = i;
                                                arenaName = true;
                                            } else {
                                                i++;
                                            }
                                        }
                                        int max = 1;
                                        while (Settings.arenadata.contains("pg.lobbies." + lobby + "." + arenaNumber + ".spawns." + max)) {
                                            spawnNumber = max;
                                            max++;
                                        }
                                        Settings.arenadata.set("pg.lobbies." + lobby + "." + arenaNumber + ".spawns." + spawnNumber, null);
                                        Settings.arenadata.save(Settings.arenadatafile);
                                        p.sendMessage(Settings.prefix.append(Component.text(String.valueOf(spawnNumber)).color(NamedTextColor.AQUA)).append(Component.text(" " + pg.chatmessages.get(28)).color(NamedTextColor.GREEN)).append(Component.text(" (Lobby: " + lobby + ")").color(NamedTextColor.GRAY)));
                                    } catch (Exception ex) {
                                        p.sendMessage(Settings.prefix.append(arena.color(NamedTextColor.AQUA)).append(Component.text(" " + pg.chatmessages.get(31)).color(NamedTextColor.RED)).append(Component.text(" (Lobby: " + lobby + ")").color(NamedTextColor.GRAY)));
                                    }
                                }
                            }
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.CLOCK) {
                        if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).displayName().equals(Component.text("Choose Lobby").color(NamedTextColor.DARK_AQUA))) {
                            if (pg.isLobbySystem()) {
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, Settings.prefix.append(Component.text("Choose Lobby").color(NamedTextColor.DARK_AQUA)));
                                for (int slot = 1; slot <= 27; slot++) {
                                    if (Settings.arenadata.contains("pg.lobbies." + slot)) {
                                        ArrayList<Component> arenalore = new ArrayList<>();
                                        ItemStack arenamap = new ItemStack(Material.MAP);
                                        ItemMeta arenamapmeta = arenamap.getItemMeta();
                                        assert arenamapmeta != null;
                                        arenamapmeta.displayName(Component.text(Integer.toString(slot)));
                                        arenamapmeta.lore(arenalore);
                                        arenamap.setItemMeta(arenamapmeta);
                                        inv.setItem(slot - 1, arenamap);
                                    }
                                }
                                p.openInventory(inv);
                            }
                        } else if (p.getInventory().getItemInMainHand().getItemMeta().displayName().equals(Component.text("Choose Arena").color(NamedTextColor.DARK_AQUA))) {
                            Inventory inv = Bukkit.createInventory(null, 9 * 3, Settings.prefix.append(Component.text("Choose Arena").color(NamedTextColor.DARK_AQUA)));
                            if (pg.isLobbySystem()) {
                                for (int slot = 1; slot < 27; slot++) {
                                    if (Settings.arenadata.contains("pg.lobbies." + lobby + "." + slot)) {
                                        ArrayList<Component> arenalore = new ArrayList<>();
                                        ItemStack arenamap = new ItemStack(Material.MAP);
                                        ItemMeta arenamapmeta = arenamap.getItemMeta();
                                        assert arenamapmeta != null;
                                        arenamapmeta.displayName(Component.text(Settings.arenadata.getString("pg.lobbies." + lobby + "." + slot + ".name")));
                                        arenamapmeta.lore(arenalore);
                                        arenamap.setItemMeta(arenamapmeta);
                                        inv.setItem(slot - 1, arenamap);
                                    }
                                }
                            } else {
                                for (int slot = 1; slot < 27; slot++) {
                                    if (Settings.arenadata.contains("pg.arenas." + slot)) {
                                        ArrayList<Component> arenalore = new ArrayList<>();
                                        ItemStack arenamap = new ItemStack(Material.MAP);
                                        ItemMeta arenamapmeta = arenamap.getItemMeta();
                                        assert arenamapmeta != null;
                                        arenamapmeta.displayName(Component.text(Settings.arenadata.getString("pg.arenas." + slot + ".name")));
                                        arenamapmeta.lore(arenalore);
                                        arenamap.setItemMeta(arenamapmeta);
                                        inv.setItem(slot - 1, arenamap);
                                    }
                                }
                            }
                            p.openInventory(inv);
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.BARRIER) {
                        if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).displayName().equals(Component.text("Leave Setup-Mode").color(NamedTextColor.DARK_AQUA))) {
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
                            pg.setupPlayer.remove(p);
                        }
                    }
                }
            }
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getHand() == EquipmentSlot.HAND) {
                    if (Objects.requireNonNull(e.getClickedBlock()).getType() == Material.SPRUCE_SIGN || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.ACACIA_SIGN || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.BIRCH_SIGN || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.DARK_OAK_SIGN || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.JUNGLE_SIGN || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.OAK_SIGN || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.SPRUCE_WALL_SIGN || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.ACACIA_WALL_SIGN || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.BIRCH_WALL_SIGN || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.DARK_OAK_WALL_SIGN || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.JUNGLE_WALL_SIGN || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.OAK_WALL_SIGN) {
                        Sign sign = (Sign) e.getClickedBlock().getState();
                        String line1 = PlainTextComponentSerializer.plainText().serialize(sign.getSide(Side.FRONT).line(0));
                        String line2 = PlainTextComponentSerializer.plainText().serialize(sign.getSide(Side.FRONT).line(1));
                        String line3 = PlainTextComponentSerializer.plainText().serialize(sign.getSide(Side.FRONT).line(2));
                        if (pg.isLobbySystem()) {
                            if (e.getClickedBlock().getLocation().equals(Settings.arenadata.getLocation("pg.lobbies." + line1 + ".sign"))) {
                                if (!pg.playerLobby.containsKey(p) && !pg.specLobby.containsKey(p)) {
                                    e.setCancelled(true);
                                    pg.onJoinLobby(p, line1);
                                }
                            }
                        } else {
                            if (e.getClickedBlock().getLocation().equals(pg.getConfig().getLocation("pg.Lobby.sign"))) {
                                if (!pg.pgPlayers.contains(p) && !pg.specPlayers.contains(p)) {
                                    e.setCancelled(true);
                                    pg.onJoin(p);
                                }
                            }
                        }
                        if (line2.matches("PotionGames") && line3.matches("Stats")) {
                            int wins = pg.getWins(p.getUniqueId().toString());
                            int losses = pg.getLosses(p.getUniqueId().toString());
                            int rounds = pg.getRounds(p.getUniqueId().toString());
                            int kills = pg.getKills(p.getUniqueId().toString());
                            int deaths = pg.getDeaths(p.getUniqueId().toString());
                            double kd = pg.getKD(p.getUniqueId().toString());
                            p.sendMessage(Messages.StatsLabel());
                            p.sendMessage(Messages.RoundsLabel(rounds));
                            p.sendMessage(Messages.WinsLabel(wins));
                            p.sendMessage(Messages.LossesLabel(losses));
                            p.sendMessage(Messages.KillsLabel(kills));
                            p.sendMessage(Messages.DeathsLabel(deaths));
                            p.sendMessage(Messages.KDLabel(kd));
                            p.sendMessage(Messages.StatsLabel());
                        }
                        if (line1.matches("Place #1") || line1.matches("Place #2") || line1.matches("Place #3")) {
                            Player pstats = Bukkit.getPlayer(line2.toString());
                            p.sendMessage(Messages.StatsLabel());
                            if (pstats != null) {
                                int wins = pg.getWins(pstats.getUniqueId().toString());
                                int losses = pg.getLosses(pstats.getUniqueId().toString());
                                int rounds = pg.getRounds(pstats.getUniqueId().toString());
                                int kills = pg.getKills(pstats.getUniqueId().toString());
                                int deaths = pg.getDeaths(pstats.getUniqueId().toString());
                                double kd = pg.getKD(pstats.getUniqueId().toString());
                                p.sendMessage(Messages.RoundsLabel(rounds));
                                p.sendMessage(Messages.WinsLabel(wins));
                                p.sendMessage(Messages.LossesLabel(losses));
                                p.sendMessage(Messages.KillsLabel(kills));
                                p.sendMessage(Messages.DeathsLabel(deaths));
                                p.sendMessage(Messages.KDLabel(kd));
                            } else {
                                p.sendMessage(Messages.NoPlayerFound());
                            }
                            p.sendMessage(Messages.StatsLabel());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (pg.isGameServer()) {
            if (pg.pgPlayers.contains(p) || pg.playerLobby.containsKey(p)) {
                if (pg.isLobbySystem()) {
                    String s = null;
                    for (int ii = 1; ii <= 27; ii++) {
                        if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                            s = Integer.toString(ii);
                        }
                    }
                    if (pg.lobbyStates.get(s) == GameStates.WAITING && !pg.lobbyBuild.get(s) && pg.playerLobby.containsKey(p) || pg.lobbyStates.get(s) == GameStates.PREPARING && !pg.lobbyBuild.get(s) && pg.playerLobby.containsKey(p)) {
                        if (e.getView().title().equals(Messages.ArenaSelector())) {
                            if (e.getCurrentItem() != null) {
                                int randomvotes;
                                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).hasDisplayName()) {
                                    String displayname = PlainTextComponentSerializer.plainText().serialize(e.getCurrentItem().getItemMeta().displayName());
                                    if (!pg.lobbyVoted.containsKey(p)) {
                                        p.closeInventory();
                                        int votes = pg.lobbyvotes.get(s).get(displayname);
                                        votes++;
                                        HashMap<String, Integer> temp = new HashMap<>();
                                        randomvotes = pg.lobbyvotes.get(s).get("Random");
                                        temp.put("Random", randomvotes);
                                        for (int max = 1; max < 27; max++) {
                                            if (Settings.arenadata.contains("pg.lobbies." + s + "." + max)) {
                                                int oldvotes = pg.lobbyvotes.get(s).get(Settings.arenadata.getString("pg.lobbies." + s + "." + max + ".name"));
                                                temp.put(Settings.arenadata.getString("pg.lobbies." + s + "." + max + ".name"), oldvotes);
                                            }
                                        }
                                        temp.put(displayname, votes);
                                        pg.lobbyvotes.replace(s, temp);
                                    } else {
                                        p.closeInventory();
                                        String arenaname = null;
                                        for (int i = 0; i <= pg.lobbyvotes.get(s).size(); i++) {
                                            for (String all : pg.lobbyvoteplayernames.get(s).values()) {
                                                if (Objects.equals(pg.lobbyvoteplayernames.get(s).get(p), all)) {
                                                    arenaname = all;
                                                }
                                            }
                                        }
                                        pg.lobbyvoteplayernames.get(s).remove(p, arenaname);
                                        int votes = pg.lobbyvotes.get(s).get(arenaname);
                                        votes--;
                                        HashMap<String, Integer> tempold = new HashMap<>();
                                        randomvotes = pg.lobbyvotes.get(s).get("Random");
                                        tempold.put("Random", randomvotes);
                                        for (int max = 1; max < 27; max++) {
                                            if (Settings.arenadata.contains("pg.lobbies." + s + "." + max)) {
                                                int oldvotes = pg.lobbyvotes.get(s).get(Settings.arenadata.getString("pg.lobbies." + s + "." + max + ".name"));
                                                tempold.put(Settings.arenadata.getString("pg.lobbies." + s + "." + max + ".name"), oldvotes);
                                            }
                                        }
                                        tempold.put(arenaname, votes);
                                        pg.lobbyvotes.replace(s, tempold);
                                        pg.lobbyVoted.remove(p, s);
                                        votes = pg.lobbyvotes.get(s).get(displayname);
                                        votes++;
                                        HashMap<String, Integer> temp = new HashMap<>();
                                        randomvotes = pg.lobbyvotes.get(s).get("Random");
                                        temp.put("Random", randomvotes);
                                        for (int max = 1; max < 27; max++) {
                                            if (Settings.arenadata.contains("pg.lobbies." + s + "." + max)) {
                                                int oldvotes = pg.lobbyvotes.get(s).get(Settings.arenadata.getString("pg.lobbies." + s + "." + max + ".name"));
                                                temp.put(Settings.arenadata.getString("pg.lobbies." + s + "." + max + ".name"), oldvotes);
                                            }
                                        }
                                        temp.put(displayname, votes);
                                        pg.lobbyvotes.replace(s, temp);
                                    }
                                    p.sendMessage(Messages.ArenaSelector());
                                    p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(16) + ": ").color(NamedTextColor.GREEN)).append(Component.text(displayname).color(NamedTextColor.LIGHT_PURPLE)));
                                    p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(15) + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf(pg.lobbyvotes.get(s).get(displayname))).color(NamedTextColor.AQUA)));
                                    p.sendMessage(Messages.ArenaSelector());
                                    pg.lobbyVoted.put(p, s);
                                    pg.lobbyvoteplayernames.get(s).put(p, displayname);
                                }
                            }
                        }
                        if (pg.lobbyActivateTeams.get(s)) {
                            if (e.getView().title().equals(Settings.prefix.append(Component.text(pg.chatmessages.get(43)).color(NamedTextColor.DARK_AQUA)))) {
                                if (e.getCurrentItem() != null) {
                                    if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).hasDisplayName()) {
                                        String displayname = PlainTextComponentSerializer.plainText().serialize(e.getCurrentItem().getItemMeta().displayName());
                                        int maxteamplayers = pg.lobbyteamSize.get(s);
                                        if (!pg.lobbyTeamed.containsKey(p)) {
                                            if (e.getCurrentItem().getItemMeta().displayName().equals(Messages.RandomLabel())) {
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
                                                        p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
                                                        p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(45) + ": ").color(NamedTextColor.GREEN)).append(Component.text(rndTeam).color(NamedTextColor.LIGHT_PURPLE)));
                                                        p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(44) + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf(pg.lobbyteams.get(s).get(rndTeam))).color(NamedTextColor.AQUA)).append(Component.text("/").color(NamedTextColor.GRAY)).append(Component.text(String.valueOf(maxteamplayers)).color(NamedTextColor.AQUA)));
                                                        p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
                                                        pg.lobbyTeamed.put((Player) e.getWhoClicked(), s);
                                                        pg.lobbyteamplayernames.get(s).put(p, Integer.toString(rndTeam));
                                                        if (pg.isActivateScoreboard()) {
                                                            Objects.requireNonNull(p.getScoreboard().getTeam("team")).prefix(Component.text(Integer.toString(rndTeam)).color(NamedTextColor.DARK_AQUA));
                                                        }
                                                    }
                                                }
                                            } else {
                                                if (pg.lobbyteams.get(s).get(Integer.parseInt(displayname)) < maxteamplayers) {
                                                    p.closeInventory();
                                                    int players = pg.lobbyteams.get(s).get(Integer.parseInt(displayname));
                                                    players++;
                                                    HashMap<Integer, Integer> temp = new HashMap<>();
                                                    for (int max = 1; max <= pg.lobbyteamAmount.get(s); max++) {
                                                        int oldplayers = pg.lobbyteams.get(s).get(max);
                                                        temp.put(max, oldplayers);
                                                    }
                                                    temp.put(Integer.parseInt(displayname), players);
                                                    pg.lobbyteams.replace(s, temp);
                                                    p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
                                                    p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(45) + ": ").color(NamedTextColor.GREEN)).append(Component.text(displayname).color(NamedTextColor.LIGHT_PURPLE)));
                                                    p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(44) + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf(pg.lobbyteams.get(s).get(Integer.parseInt(displayname)))).color(NamedTextColor.AQUA)).append(Component.text("/").color(NamedTextColor.GRAY)).append(Component.text(String.valueOf(maxteamplayers)).color(NamedTextColor.AQUA)));
                                                    p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
                                                    pg.lobbyTeamed.put((Player) e.getWhoClicked(), s);
                                                    pg.lobbyteamplayernames.get(s).put(p, displayname);
                                                    if (pg.isActivateScoreboard()) {
                                                        Objects.requireNonNull(p.getScoreboard().getTeam("team")).prefix(Component.text(displayname).color(NamedTextColor.DARK_AQUA));
                                                    }
                                                } else {
                                                    p.closeInventory();
                                                    p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
                                                    p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(47)).color(NamedTextColor.RED)));
                                                    p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
                                                }
                                            }
                                        } else {
                                            p.closeInventory();
                                            String teamname = null;
                                            for (String all : pg.lobbyteamplayernames.get(s).values()) {
                                                if (Objects.equals(pg.lobbyteamplayernames.get(s).get(p), all)) {
                                                    teamname = all;
                                                }
                                            }
                                            pg.lobbyteamplayernames.get(s).remove(p, teamname);
                                            assert teamname != null;
                                            int teamamount = pg.lobbyteams.get(s).get(Integer.parseInt(teamname));
                                            teamamount--;
                                            HashMap<Integer, Integer> tempold = new HashMap<>();
                                            for (int max = 1; max <= pg.lobbyteamAmount.get(s); max++) {
                                                int oldplayers = pg.lobbyteams.get(s).get(max);
                                                tempold.put(max, oldplayers);
                                            }
                                            tempold.put(Integer.parseInt(teamname), teamamount);
                                            pg.lobbyteams.replace(s, tempold);
                                            pg.lobbyTeamed.remove(p, s);
                                            if (e.getCurrentItem().getItemMeta().displayName().equals(Messages.RandomLabel())) {
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
                                                        p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
                                                        p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(45) + ": ").color(NamedTextColor.GREEN)).append(Component.text(rndTeam).color(NamedTextColor.LIGHT_PURPLE)));
                                                        p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(44) + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf(pg.lobbyteams.get(s).get(rndTeam))).color(NamedTextColor.AQUA)).append(Component.text("/").color(NamedTextColor.GRAY)).append(Component.text(String.valueOf(maxteamplayers)).color(NamedTextColor.AQUA)));
                                                        p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
                                                        pg.lobbyTeamed.put((Player) e.getWhoClicked(), s);
                                                        pg.lobbyteamplayernames.get(s).put(p, Integer.toString(rndTeam));
                                                        if (pg.isActivateScoreboard()) {
                                                            Objects.requireNonNull(p.getScoreboard().getTeam("team")).prefix(Component.text(Integer.toString(rndTeam)).color(NamedTextColor.DARK_AQUA));
                                                        }
                                                    }
                                                }
                                            } else {
                                                if (pg.lobbyteams.get(s).get(Integer.parseInt(displayname)) < maxteamplayers) {
                                                    p.closeInventory();
                                                    int players = pg.lobbyteams.get(s).get(Integer.parseInt(displayname));
                                                    players++;
                                                    HashMap<Integer, Integer> temp = new HashMap<>();
                                                    for (int max = 1; max <= pg.lobbyteamAmount.get(s); max++) {
                                                        int oldplayers = pg.lobbyteams.get(s).get(max);
                                                        temp.put(max, oldplayers);
                                                    }
                                                    temp.put(Integer.parseInt(displayname), players);
                                                    pg.lobbyteams.replace(s, temp);
                                                    p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
                                                    p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(45) + ": ").color(NamedTextColor.GREEN)).append(Component.text(displayname).color(NamedTextColor.LIGHT_PURPLE)));
                                                    p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(44) + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf(pg.lobbyteams.get(s).get(Integer.parseInt(displayname)))).color(NamedTextColor.AQUA)).append(Component.text("/").color(NamedTextColor.GRAY)).append(Component.text(String.valueOf(maxteamplayers)).color(NamedTextColor.AQUA)));
                                                    p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
                                                    pg.lobbyTeamed.put((Player) e.getWhoClicked(), s);
                                                    pg.lobbyteamplayernames.get(s).put(p, displayname);
                                                    if (pg.isActivateScoreboard()) {
                                                        Objects.requireNonNull(p.getScoreboard().getTeam("team")).prefix(Component.text(displayname).color(NamedTextColor.DARK_AQUA));
                                                    }
                                                } else {
                                                    p.closeInventory();
                                                    p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
                                                    p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(47)).color(NamedTextColor.RED)));
                                                    p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (e.getView().title().equals(Settings.prefix.append(Component.text(pg.chatmessages.get(62)).color(NamedTextColor.DARK_AQUA)))) {
                            if (e.getCurrentItem() != null) {
                                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).hasDisplayName()) {
                                    String displayname = PlainTextComponentSerializer.plainText().serialize(e.getCurrentItem().getItemMeta().displayName());
                                    if (!pg.lobbyKited.containsKey(p)) {
                                        if (e.getCurrentItem().getItemMeta().displayName().equals(Messages.RandomLabel())) {
                                            Random rnd = new Random();
                                            int rndKit = rnd.nextInt(pg.getActiveKits());
                                            p.closeInventory();
                                            p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(62) + "--------------").color(NamedTextColor.GRAY)));
                                            p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(46) + ": ").color(NamedTextColor.GREEN)).append(Component.text(pg.kits.get(rndKit)).color(NamedTextColor.LIGHT_PURPLE)));
                                            p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(62) + "--------------").color(NamedTextColor.GRAY)));
                                            pg.lobbyKited.put(p, s);
                                            pg.kitplayernames.put(p, pg.kits.get(rndKit));
                                            if (pg.isActivateScoreboard()) {
                                                Objects.requireNonNull(p.getScoreboard().getTeam("kit")).prefix(Component.text(pg.kits.get(rndKit)).color(NamedTextColor.DARK_AQUA));
                                            }
                                            if (pg.kits.get(rndKit).matches("Rich Kid")) {
                                                pg.richkidPlayers.add(p);
                                            }
                                        } else {
                                            p.closeInventory();
                                            p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(62) + "--------------").color(NamedTextColor.GRAY)));
                                            p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(46) + ": ").color(NamedTextColor.GREEN)).append(Component.text(displayname).color(NamedTextColor.LIGHT_PURPLE)));
                                            p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(62) + "--------------").color(NamedTextColor.GRAY)));
                                            pg.lobbyKited.put(p, s);
                                            pg.kitplayernames.put(p, displayname);
                                            if (pg.isActivateScoreboard()) {
                                                Objects.requireNonNull(p.getScoreboard().getTeam("kit")).prefix(Component.text(displayname).color(NamedTextColor.DARK_AQUA));
                                            }
                                            if (displayname.matches("Rich Kid")) {
                                                pg.richkidPlayers.add(p);
                                            }
                                        }
                                    } else {
                                        p.closeInventory();
                                        String kitname = null;
                                        for (int i = 0; i <= pg.getActiveKits(); i++) {
                                            if (pg.kitplayernames.containsKey(p) && pg.kitplayernames.containsValue(Integer.toString(i))) {
                                                kitname = Integer.toString(i);
                                            }
                                        }
                                        pg.kitplayernames.remove(p, kitname);
                                        pg.richkidPlayers.remove(p);
                                        if (e.getCurrentItem().getItemMeta().displayName().equals(Messages.RandomLabel())) {
                                            Random rnd = new Random();
                                            int rndKit = rnd.nextInt(pg.getActiveKits());
                                            p.closeInventory();
                                            p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(62) + "--------------").color(NamedTextColor.GRAY)));
                                            p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(46) + ": ").color(NamedTextColor.GREEN)).append(Component.text(pg.kits.get(rndKit)).color(NamedTextColor.LIGHT_PURPLE)));
                                            p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(62) + "--------------").color(NamedTextColor.GRAY)));
                                            pg.lobbyKited.put(p, s);
                                            pg.kitplayernames.put(p, pg.kits.get(rndKit));
                                            if (pg.isActivateScoreboard()) {
                                                Objects.requireNonNull(p.getScoreboard().getTeam("kit")).prefix(Component.text(pg.kits.get(rndKit)).color(NamedTextColor.DARK_AQUA));
                                            }
                                            if (pg.kits.get(rndKit).equals("Rich Kid")) {
                                                pg.richkidPlayers.add(p);
                                            }
                                        } else {
                                            p.closeInventory();
                                            p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(62) + "--------------").color(NamedTextColor.GRAY)));
                                            p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(46) + ": ").color(NamedTextColor.GREEN)).append(Component.text(displayname).color(NamedTextColor.LIGHT_PURPLE)));
                                            p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(62) + "--------------").color(NamedTextColor.GRAY)));
                                            pg.lobbyKited.put(p, s);
                                            pg.kitplayernames.put(p, displayname);
                                            if (pg.isActivateScoreboard()) {
                                                Objects.requireNonNull(p.getScoreboard().getTeam("kit")).prefix(Component.text(displayname).color(NamedTextColor.DARK_AQUA));
                                            }
                                            if (displayname.matches("Rich Kid")) {
                                                pg.richkidPlayers.add(p);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e.getView().title().equals(Settings.prefix.append(Component.text(pg.chatmessages.get(49)).color(NamedTextColor.DARK_AQUA)))) {
                        if (e.getCurrentItem() != null) {
                            int shopitem = 1;
                            for (int i = 0; i < pg.shop.size(); i++) {
                                int coinamount;
                                if (pg.kitplayernames.containsKey(p) && pg.kitplayernames.containsValue(pg.shopkit.get(shopitem - 1))) {
                                    coinamount = pg.shopsale.get(shopitem - 1);
                                } else {
                                    coinamount = pg.shopcost.get(shopitem - 1);
                                }
                                if (Objects.requireNonNull(PlainTextComponentSerializer.plainText().serialize(e.getCurrentItem().getItemMeta().displayName())).matches(pg.shop.get(shopitem - 1))) {
                                    if (bottle >= 1) {
                                        if (amount >= coinamount) {
                                            amount = amount - coinamount;
                                            bottle = bottle - 1;
                                            ItemStack randombarrier = new ItemStack(pg.shoppotiontype.get(shopitem - 1));
                                            PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                                            assert randombarriermeta != null;
                                            randombarriermeta.addCustomEffect(new PotionEffect(pg.shoppotion.get(shopitem - 1).getType(), pg.shoppotion.get(shopitem - 1).getDuration(), pg.shoppotion.get(shopitem - 1).getAmplifier()), true);
                                            randombarriermeta.displayName(Component.text(pg.shop.get(shopitem - 1)));
                                            randombarrier.setItemMeta(randombarriermeta);
                                            p.getInventory().addItem(randombarrier);
                                            for (int k = 0; k < coinamount; k++) {
                                                p.getInventory().removeItem(pg.getCoin());
                                            }
                                            for (int k = 0; k < 1; k++) {
                                                p.getInventory().removeItem(pg.getBottle());
                                            }
                                        } else {
                                            p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(53)).color(NamedTextColor.RED)));
                                        }
                                    } else {
                                        p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(54)).color(NamedTextColor.RED)));
                                    }
                                }
                                shopitem++;
                            }
                        }
                        e.setCancelled(true);
                    }
                } else {
                    if (pg.getGamestate() == GameStates.WAITING && !pg.isBuild() && pg.pgPlayers.contains(p) || pg.getGamestate() == GameStates.PREPARING && !pg.isBuild() && pg.pgPlayers.contains(p)) {
                        if (e.getView().title().equals(Messages.ArenaSelectorTitle())) {
                            if (e.getCurrentItem() != null) {
                                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).hasDisplayName()) {
                                    String displayname = PlainTextComponentSerializer.plainText().serialize(e.getCurrentItem().getItemMeta().displayName());
                                    if (!pg.voted.contains(p.getName())) {
                                        p.closeInventory();
                                        int votes = pg.votes.get(displayname);
                                        votes++;
                                        pg.votes.put(displayname, votes);
                                        p.sendMessage(Messages.ArenaSelector());
                                        p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(16) + ": ").color(NamedTextColor.GREEN)).append(Component.text(displayname).color(NamedTextColor.LIGHT_PURPLE)));
                                        p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(15) + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf(pg.votes.get(displayname))).color(NamedTextColor.AQUA)));
                                        p.sendMessage(Messages.ArenaSelector());
                                        pg.voted.add(e.getWhoClicked().getName());
                                        pg.voteplayernames.put(p, displayname);
                                    } else {
                                        p.closeInventory();
                                        String arenaname = null;
                                        for (int i = 0; i <= pg.arenas.size(); i++) {
                                            if (Settings.arenadata.contains("pg.arenas." + i)) {
                                                String name = Settings.arenadata.getString("pg.arenas." + i + ".name");
                                                if (pg.voteplayernames.containsKey(p) && pg.voteplayernames.containsValue(name)) {
                                                    arenaname = name;
                                                }
                                            } else {
                                                arenaname = "Random";
                                            }
                                        }
                                        pg.voteplayernames.remove(p, arenaname);
                                        int votes = pg.votes.get(arenaname) - 1;
                                        pg.votes.put(arenaname, votes);
                                        pg.voted.remove(p.getName());
                                        votes = pg.votes.get(displayname);
                                        votes++;
                                        pg.votes.put(displayname, votes);
                                        p.sendMessage(Messages.ArenaSelector());
                                        p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(16) + ": ").color(NamedTextColor.GREEN)).append(Component.text(displayname).color(NamedTextColor.LIGHT_PURPLE)));
                                        p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(15) + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf(pg.votes.get(displayname))).color(NamedTextColor.AQUA)));
                                        p.sendMessage(Messages.ArenaSelector());
                                        pg.voted.add(e.getWhoClicked().getName());
                                        pg.voteplayernames.put(p, displayname);
                                    }
                                }
                            }
                        }
                        if (pg.isActivateTeams()) {
                            if (e.getView().title().equals(Settings.prefix.append(Component.text(pg.chatmessages.get(43)).color(NamedTextColor.DARK_AQUA)))) {
                                if (e.getCurrentItem() != null) {
                                    if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).hasDisplayName()) {
                                        String displayname = PlainTextComponentSerializer.plainText().serialize(e.getCurrentItem().getItemMeta().displayName());
                                        int maxteamplayers = pg.getTeamSize();
                                        if (!pg.teamed.contains(p.getName())) {
                                            if (e.getCurrentItem().getItemMeta().displayName().equals(Messages.RandomLabel())) {
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
                                                        p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
                                                        p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(45) + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf(rndTeam)).color(NamedTextColor.LIGHT_PURPLE)));
                                                        p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(44) + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf(pg.teamplayers.get(Integer.toString(rndTeam)))).color(NamedTextColor.AQUA)).append(Component.text("/").color(NamedTextColor.GRAY)).append(Component.text(String.valueOf(maxteamplayers)).color(NamedTextColor.AQUA)));
                                                        p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
                                                        pg.teamed.add(e.getWhoClicked().getName());
                                                        pg.teamplayernames.put(p, Integer.toString(rndTeam));
                                                        if (pg.isActivateScoreboard()) {
                                                            Objects.requireNonNull(p.getScoreboard().getTeam("team")).prefix(Component.text(Integer.toString(rndTeam)).color(NamedTextColor.DARK_AQUA));
                                                        }
                                                    }
                                                }
                                            } else {
                                                if (pg.teamplayers.get(displayname) < maxteamplayers) {
                                                    p.closeInventory();
                                                    int players = pg.teamplayers.get(displayname);
                                                    players++;
                                                    pg.teamplayers.put(displayname, players);
                                                    p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
                                                    p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(45) + ": ").color(NamedTextColor.GREEN)).append(Component.text(displayname).color(NamedTextColor.LIGHT_PURPLE)));
                                                    p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(44) + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf(pg.teamplayers.get(displayname))).color(NamedTextColor.AQUA)).append(Component.text("/").color(NamedTextColor.GRAY)).append(Component.text(String.valueOf(maxteamplayers)).color(NamedTextColor.AQUA)));
                                                    p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
                                                    pg.teamed.add(e.getWhoClicked().getName());
                                                    pg.teamplayernames.put(p, displayname);
                                                    if (pg.isActivateScoreboard()) {
                                                        Objects.requireNonNull(p.getScoreboard().getTeam("team")).prefix(Component.text(displayname).color(NamedTextColor.DARK_AQUA));
                                                    }
                                                } else {
                                                    p.closeInventory();
                                                    p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
                                                    p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(47)).color(NamedTextColor.RED)));
                                                    p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
                                                }
                                            }
                                        } else {
                                            p.closeInventory();
                                            String teamname = null;
                                            for (int i = 1; i <= pg.getTeamAmount(); i++) {
                                                if (pg.teamplayernames.containsKey(p) && pg.teamplayernames.containsValue(Integer.toString(i))) {
                                                    teamname = Integer.toString(i);
                                                }
                                            }
                                            pg.teamplayernames.remove(p, teamname);
                                            int teamamount = pg.teamplayers.get(teamname) - 1;
                                            pg.teamplayers.put(teamname, teamamount);
                                            pg.teamed.remove(p.getName());
                                            if (e.getCurrentItem().getItemMeta().displayName().equals(Messages.RandomLabel())) {
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
                                                        p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
                                                        p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(45) + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf(rndTeam)).color(NamedTextColor.LIGHT_PURPLE)));
                                                        p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(44) + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf(pg.teamplayers.get(Integer.toString(rndTeam)))).color(NamedTextColor.AQUA)).append(Component.text("/").color(NamedTextColor.GRAY)).append(Component.text(String.valueOf(maxteamplayers)).color(NamedTextColor.AQUA)));
                                                        p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
                                                        pg.teamed.add(e.getWhoClicked().getName());
                                                        pg.teamplayernames.put(p, Integer.toString(rndTeam));
                                                        if (pg.isActivateScoreboard()) {
                                                            Objects.requireNonNull(p.getScoreboard().getTeam("team")).prefix(Component.text(Integer.toString(rndTeam)).color(NamedTextColor.DARK_AQUA));
                                                        }
                                                    }
                                                }
                                            } else {
                                                if (pg.teamplayers.get(displayname) < maxteamplayers) {
                                                    p.closeInventory();
                                                    int players = pg.teamplayers.get(displayname);
                                                    players++;
                                                    pg.teamplayers.put(displayname, players);
                                                    p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
                                                    p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(45) + ": ").color(NamedTextColor.GREEN)).append(Component.text(displayname).color(NamedTextColor.LIGHT_PURPLE)));
                                                    p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(44) + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf(pg.teamplayers.get(displayname))).color(NamedTextColor.AQUA)).append(Component.text("/").color(NamedTextColor.GRAY)).append(Component.text(String.valueOf(maxteamplayers)).color(NamedTextColor.AQUA)));
                                                    p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
                                                    pg.teamed.add(e.getWhoClicked().getName());
                                                    pg.teamplayernames.put(p, displayname);
                                                    if (pg.isActivateScoreboard()) {
                                                        Objects.requireNonNull(p.getScoreboard().getTeam("team")).prefix(Component.text(displayname).color(NamedTextColor.DARK_AQUA));
                                                    }
                                                } else {
                                                    p.closeInventory();
                                                    p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
                                                    p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(47)).color(NamedTextColor.RED)));
                                                    p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (e.getView().title().equals(Settings.prefix.append(Component.text(pg.chatmessages.get(62)).color(NamedTextColor.DARK_AQUA)))) {
                            if (e.getCurrentItem() != null) {
                                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).hasDisplayName()) {
                                    String displayname = PlainTextComponentSerializer.plainText().serialize(e.getCurrentItem().getItemMeta().displayName());
                                    if (!pg.kited.contains(p.getName())) {
                                        if (e.getCurrentItem().getItemMeta().displayName().equals(Messages.RandomLabel())) {
                                            Random rnd = new Random();
                                            int rndKit = rnd.nextInt(pg.getActiveKits());
                                            p.closeInventory();
                                            p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(62) + "--------------").color(NamedTextColor.GRAY)));
                                            p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(46) + ": ").color(NamedTextColor.GREEN)).append(Component.text(pg.kits.get(rndKit)).color(NamedTextColor.LIGHT_PURPLE)));
                                            p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(62) + "--------------").color(NamedTextColor.GRAY)));
                                            pg.kited.add(e.getWhoClicked().getName());
                                            pg.kitplayernames.put(p, pg.kits.get(rndKit));
                                            if (pg.isActivateScoreboard()) {
                                                Objects.requireNonNull(p.getScoreboard().getTeam("kit")).prefix(Component.text(pg.kits.get(rndKit)).color(NamedTextColor.DARK_AQUA));
                                            }
                                            if (pg.kits.get(rndKit).equals("Rich Kid")) {
                                                pg.richkidPlayers.add(p);
                                            }
                                            } else {
                                            p.closeInventory();
                                            p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(62) + "--------------").color(NamedTextColor.GRAY)));
                                            p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(46) + ": ").color(NamedTextColor.GREEN)).append(Component.text(displayname).color(NamedTextColor.LIGHT_PURPLE)));
                                            p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(62) + "--------------").color(NamedTextColor.GRAY)));
                                            pg.kited.add(e.getWhoClicked().getName());
                                            pg.kitplayernames.put(p, displayname);
                                            if (pg.isActivateScoreboard()) {
                                                Objects.requireNonNull(p.getScoreboard().getTeam("kit")).prefix(Component.text(displayname).color(NamedTextColor.DARK_AQUA));
                                            }
                                            if (displayname.equals("Rich Kid")) {
                                                pg.richkidPlayers.add(p);
                                            }
                                        }
                                    } else {
                                        p.closeInventory();
                                        String kitname = null;
                                        for (int i = 0; i <= pg.getActiveKits(); i++) {
                                            if (pg.kitplayernames.containsKey(p) && pg.kitplayernames.containsValue(Integer.toString(i))) {
                                                kitname = Integer.toString(i);
                                            }
                                        }
                                        pg.kitplayernames.remove(p, kitname);
                                        pg.richkidPlayers.remove(p);
                                        if (e.getCurrentItem().getItemMeta().displayName().equals(Messages.RandomLabel())) {
                                            Random rnd = new Random();
                                            int rndKit = rnd.nextInt(pg.getActiveKits());
                                            p.closeInventory();
                                            p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(62) + "--------------").color(NamedTextColor.GRAY)));
                                            p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(46) + ": ").color(NamedTextColor.GREEN)).append(Component.text(pg.kits.get(rndKit)).color(NamedTextColor.LIGHT_PURPLE)));
                                            p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(62) + "--------------").color(NamedTextColor.GRAY)));
                                            pg.kited.add(e.getWhoClicked().getName());
                                            pg.kitplayernames.put(p, pg.kits.get(rndKit));
                                            if (pg.isActivateScoreboard()) {
                                                Objects.requireNonNull(p.getScoreboard().getTeam("kit")).prefix(Component.text(pg.kits.get(rndKit)).color(NamedTextColor.DARK_AQUA));
                                            }
                                            if (pg.kits.get(rndKit).equals("Rich Kid")) {
                                                pg.richkidPlayers.add(p);
                                            }
                                            } else {
                                            p.closeInventory();
                                            p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(62) + "--------------").color(NamedTextColor.GRAY)));
                                            p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(46) + ": ").color(NamedTextColor.GREEN)).append(Component.text(displayname).color(NamedTextColor.LIGHT_PURPLE)));
                                            p.sendMessage(Settings.prefix.append(Component.text("--------------" + pg.chatmessages.get(62) + "--------------").color(NamedTextColor.GRAY)));
                                            pg.kited.add(e.getWhoClicked().getName());
                                            pg.kitplayernames.put(p, displayname);
                                            if (pg.isActivateScoreboard()) {
                                                Objects.requireNonNull(p.getScoreboard().getTeam("kit")).prefix(Component.text(displayname).color(NamedTextColor.DARK_AQUA));
                                            }
                                            if (displayname.equals("Rich Kid")) {
                                                pg.richkidPlayers.add(p);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e.getView().title().equals(Settings.prefix.append(Component.text(pg.chatmessages.get(49)).color(NamedTextColor.DARK_AQUA)))) {
                        if (e.getCurrentItem() != null) {
                            int shopitem = 1;
                            for (int i = 0; i < pg.shop.size(); i++) {
                                int coinamount;
                                if (pg.kitplayernames.containsKey(p) && pg.kitplayernames.containsValue(pg.shopkit.get(shopitem - 1))) {
                                    coinamount = pg.shopsale.get(shopitem - 1);
                                } else {
                                    coinamount = pg.shopcost.get(shopitem - 1);
                                }
                                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).displayName().equals(Component.text(pg.shop.get(shopitem - 1)))) {
                                    if (bottle >= 1) {
                                        if (amount >= coinamount) {
                                            amount = amount - coinamount;
                                            bottle = bottle - 1;
                                            ItemStack randombarrier = new ItemStack(pg.shoppotiontype.get(shopitem - 1));
                                            PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                                            assert randombarriermeta != null;
                                            randombarriermeta.addCustomEffect(new PotionEffect(pg.shoppotion.get(shopitem - 1).getType(), pg.shoppotion.get(shopitem - 1).getDuration(), pg.shoppotion.get(shopitem - 1).getAmplifier()), true);
                                            randombarriermeta.displayName(Component.text(pg.shop.get(shopitem - 1)));
                                            randombarrier.setItemMeta(randombarriermeta);
                                            p.getInventory().addItem(randombarrier);
                                            for (int k = 0; k < coinamount; k++) {
                                                p.getInventory().removeItem(pg.getCoin());
                                            }
                                            for (int k = 0; k < 1; k++) {
                                                p.getInventory().removeItem(pg.getBottle());
                                            }
                                        } else {
                                            p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(53)).color(NamedTextColor.RED)));
                                        }
                                    } else {
                                        p.sendMessage(Settings.prefix.append(Component.text(pg.chatmessages.get(54)).color(NamedTextColor.RED)));
                                    }
                                }
                                shopitem++;
                            }
                        }
                        e.setCancelled(true);
                    }
                }
            }
            if (e.getView().title().equals(Settings.prefix.append(Component.text("Lobby List").color(NamedTextColor.DARK_AQUA)))) {
                if (e.getCurrentItem() != null) {
                    p.closeInventory();
                    pg.onJoinLobby(p, PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(e.getCurrentItem().getItemMeta()).displayName()));
                }
                e.setCancelled(true);
            }
            if (e.getView().title().equals(Settings.prefix.append(Component.text("Choose Lobby").color(NamedTextColor.DARK_AQUA)))) {
                if (e.getCurrentItem() != null) {
                    p.closeInventory();
                    lobby = Objects.requireNonNull(e.getCurrentItem().getItemMeta()).displayName();
                    p.sendMessage(Settings.prefix.append(lobby.color(NamedTextColor.AQUA)).append(Component.text(" successfully chosen!").color(NamedTextColor.GREEN)));
                }
                e.setCancelled(true);
            }
            if (e.getView().title().equals(Settings.prefix.append(Component.text("Choose Arena").color(NamedTextColor.DARK_AQUA)))) {
                if (e.getCurrentItem() != null) {
                    p.closeInventory();
                    arena = Objects.requireNonNull(e.getCurrentItem().getItemMeta()).displayName();
                    p.sendMessage(Settings.prefix.append(arena.color(NamedTextColor.AQUA)).append(Component.text(" successfully chosen!").color(NamedTextColor.GREEN)));
                }
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        if (pg.isGameServer()) {
            if (pg.pgPlayers.contains(p) || pg.playerLobby.containsKey(p)) {
                if (pg.isLobbySystem()) {
                    String s = null;
                    for (int ii = 1; ii <= 27; ii++) {
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
        if (pg.isGameServer() && pg.isStartOnJoin() && !pg.isLobbySystem()) {
            pg.onJoin(p);
            e.joinMessage(null);
        }
        if (p.hasPermission("pg.update")) {
            new UpdateChecker(pg, 87633).getVersion(version -> {
                if (!pg.getPluginMeta().getVersion().equalsIgnoreCase(version)) {
                    p.sendMessage(Messages.UpdateAvailable(pg.getPluginMeta().getVersion(), version));
                }
            });
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (pg.isGameServer()) {
            if (pg.isLobbySystem()) {
                if (pg.playerLobby.containsKey(p)) {
                    String s = null;
                    for (int ii = 1; ii <= 27; ii++) {
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
        Player p = e.getPlayer();
        if (pg.isGameServer()) {
            if (pg.isLobbySystem()) {
                String s;
                if (pg.playerLobby.containsKey(p)) {
                    for (int ii = 1; ii <= 27; ii++) {
                        if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                            s = Integer.toString(ii);
                            pg.onLeaveLobby(p, s);
                            e.quitMessage(null);
                            break;
                        }
                    }
                } else if (pg.specLobby.containsKey(p)) {
                    for (int ii = 1; ii <= 27; ii++) {
                        if (pg.specLobby.get(p).contains(Integer.toString(ii))) {
                            s = Integer.toString(ii);
                            pg.onLeaveLobby(p, s);
                            e.quitMessage(null);
                            break;
                        }
                    }
                }
            } else {
                if (pg.pgPlayers.contains(p) || pg.specPlayers.contains(p)) {
                    pg.onLeave(p);
                    e.quitMessage(null);
                }
            }
        }
    }

    @EventHandler
    public void onPing(ServerListPingEvent e) {
        if (pg.isGameServer() && pg.isStartOnJoin() && !pg.isLobbySystem()) {
            e.setMaxPlayers(pg.getMaxPlayers());
            if (pg.getGamestate() == GameStates.WAITING) {
                e.motd(Component.text("" + pg.getGamestate()).color(NamedTextColor.GREEN));
            } else if (pg.getGamestate() == GameStates.PREPARING) {
                if (pg.getVote() != null) {
                    Component motd = Component.text("" + Settings.arenadata.get("pg.arenas." + pg.getVote() + ".name").toString().toUpperCase()).color(NamedTextColor.GOLD);
                    e.motd(motd);
                } else {
                    e.motd(Component.text("VOTING").color(NamedTextColor.AQUA));
                }
            } else if (pg.getGamestate() == GameStates.INGAME) {
                e.motd(Component.text("" + pg.getGamestate()).color(NamedTextColor.GRAY));
            } else {
                e.motd(Component.text("" + pg.getGamestate()).color(NamedTextColor.RED));
            }
        }
    }
}
