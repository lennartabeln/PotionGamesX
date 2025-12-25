package com.tw0far.potiongames.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.Settings;
import com.tw0far.potiongames.updatechecker.UpdateChecker;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.Objects;

public record Commands(PotionGames pg) implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        Player p = (Player) sender;
        if (pg.isGameServer()) {
            if (args.length == 0) {
                p.sendMessage(Messages.CommandsLabel());
                if (p.hasPermission("pg.setup")) {
                    if (pg.isLobbySystem()) {
                        p.sendMessage(Settings.prefix.append(Component.text("/pg setup - Set up plugin").color(NamedTextColor.GRAY)));
                        p.sendMessage(Settings.prefix.append(Component.text("/pg addlobby [lobbynumber] - Add a lobby").color(NamedTextColor.GRAY)));
                        p.sendMessage(Settings.prefix.append(Component.text("/pg removelobby [lobbynumber] - Remove a lobby").color(NamedTextColor.GRAY)));
                        p.sendMessage(Settings.prefix.append(Component.text("/pg addarena [lobbynumber] [arenaname] - Add an arena").color(NamedTextColor.GRAY)));
                        p.sendMessage(Settings.prefix.append(Component.text("/pg addspawn [lobbynumber] [arenaname] - Add a spawn").color(NamedTextColor.GRAY)));
                        p.sendMessage(Settings.prefix.append(Component.text("/pg adddeathmatch [lobbynumber] [arenaname] - Add a deathmatch spawn").color(NamedTextColor.GRAY)));
                        p.sendMessage(Settings.prefix.append(Component.text("/pg removerena [lobbynumber] [arenaname] - Remove an arena").color(NamedTextColor.GRAY)));
                        p.sendMessage(Settings.prefix.append(Component.text("/pg removespawn [lobbynumber] [arenaname] - Remove last spawn").color(NamedTextColor.GRAY)));
                        p.sendMessage(Settings.prefix.append(Component.text("/pg removedeathmatch [lobbynumber] [arenaname] - Remove last deathmatch spawn").color(NamedTextColor.GRAY)));
                        p.sendMessage(Settings.prefix.append(Component.text("/pg headp1(2;3) - Add Player Head to Stats-Wall").color(NamedTextColor.GRAY)));
                        p.sendMessage(Settings.prefix.append(Component.text("/pg signp1(2;3) - Add Player Sign to Stats-Wall").color(NamedTextColor.GRAY)));
                        p.sendMessage(Settings.prefix.append(Component.text("/pg joinsign [lobbynumber] - Add Join-Sign").color(NamedTextColor.GRAY)));
                        p.sendMessage(Settings.prefix.append(Component.text("/pg reload - Reload all configs").color(NamedTextColor.GRAY)));
                    } else {
                        p.sendMessage(Settings.prefix.append(Component.text("/pg setup - Set up plugin").color(NamedTextColor.GRAY)));
                        p.sendMessage(Settings.prefix.append(Component.text("/pg addlobby - Add a lobby").color(NamedTextColor.GRAY)));
                        p.sendMessage(Settings.prefix.append(Component.text("/pg addarena [arenaname] - Add an arena").color(NamedTextColor.GRAY)));
                        p.sendMessage(Settings.prefix.append(Component.text("/pg addspawn [arenaname] - Add a spawn").color(NamedTextColor.GRAY)));
                        p.sendMessage(Settings.prefix.append(Component.text("/pg adddeathmatch [arenaname] - Add a deathmatch spawn").color(NamedTextColor.GRAY)));
                        p.sendMessage(Settings.prefix.append(Component.text("/pg removearena [arenaname] - Remove an arena").color(NamedTextColor.GRAY)));
                        p.sendMessage(Settings.prefix.append(Component.text("/pg removespawn [arenaname] - Remove last spawn").color(NamedTextColor.GRAY)));
                        p.sendMessage(Settings.prefix.append(Component.text("/pg removedeathmatch [arenaname] - Remove last deathmatch spawn").color(NamedTextColor.GRAY)));
                        p.sendMessage(Settings.prefix.append(Component.text("/pg headp1(2;3) - Add Player Head to Stats-Wall").color(NamedTextColor.GRAY)));
                        p.sendMessage(Settings.prefix.append(Component.text("/pg signp1(2;3) - Add Player Sign to Stats-Wall").color(NamedTextColor.GRAY)));
                        p.sendMessage(Settings.prefix.append(Component.text("/pg joinsign - Set Join-Sign").color(NamedTextColor.GRAY)));
                        p.sendMessage(Settings.prefix.append(Component.text("/pg reload - Reload all configs").color(NamedTextColor.GRAY)));
                    }
                }
                if (p.hasPermission("pg.build")) {
                    p.sendMessage(Settings.prefix.append(Component.text("/pg build - Activate build mode").color(NamedTextColor.GRAY)));
                }
                if (p.hasPermission("pg.pause")) {
                    p.sendMessage(Settings.prefix.append(Component.text("/pg pause - Pause timer/countdown").color(NamedTextColor.GRAY)));
                }
                if (p.hasPermission("pg.force")) {
                    p.sendMessage(Settings.prefix.append(Component.text("/pg force [arenaname] - Force an arena").color(NamedTextColor.GRAY)));
                }
                if (p.hasPermission("pg.start")) {
                    p.sendMessage(Settings.prefix.append(Component.text("/pg start - Set lobby countdown to 10").color(NamedTextColor.GRAY)));
                }
                if (p.hasPermission("pg.join")) {
                    if (!pg.isStartOnJoin()) {
                        if (pg.isLobbySystem()) {
                            p.sendMessage(Settings.prefix.append(Component.text("/pg join # - Join a game").color(NamedTextColor.GRAY)));
                            p.sendMessage(Settings.prefix.append(Component.text("/pg list - List of all lobbies").color(NamedTextColor.GRAY)));
                        } else {
                            p.sendMessage(Settings.prefix.append(Component.text("/pg join - Join the game").color(NamedTextColor.GRAY)));
                        }
                    }
                }
                if (!pg.isStartOnJoin()) {
                    p.sendMessage(Settings.prefix.append(Component.text("/pg leave - Leave the game").color(NamedTextColor.GRAY)));
                }
                if (p.hasPermission("pg.stats")) {
                    p.sendMessage(Settings.prefix.append(Component.text("/pg stats [player] - Show player stats").color(NamedTextColor.GRAY)));
                }
                if (p.hasPermission("pg.update")) {
                    p.sendMessage(Settings.prefix.append(Component.text("/pg version - Show your and latest version of plugin").color(NamedTextColor.GRAY)));
                }
                p.sendMessage(Messages.CommandsLabel());
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("commands")) {
                    p.sendMessage(Messages.CommandsLabel());
                    if (p.hasPermission("pg.setup")) {
                        if (pg.isLobbySystem()) {
                            p.sendMessage(Settings.prefix.append(Component.text("/pg setup - Set up plugin").color(NamedTextColor.GRAY)));
                            p.sendMessage(Settings.prefix.append(Component.text("/pg addlobby [lobbynumber] - Add a lobby").color(NamedTextColor.GRAY)));
                            p.sendMessage(Settings.prefix.append(Component.text("/pg removelobby [lobbynumber] - Remove a lobby").color(NamedTextColor.GRAY)));
                            p.sendMessage(Settings.prefix.append(Component.text("/pg addarena [lobbynumber] [arenaname] - Add an arena").color(NamedTextColor.GRAY)));
                            p.sendMessage(Settings.prefix.append(Component.text("/pg addspawn [lobbynumber] [arenaname] - Add a spawn").color(NamedTextColor.GRAY)));
                            p.sendMessage(Settings.prefix.append(Component.text("/pg adddeathmatch [lobbynumber] [arenaname] - Add a deathmatch spawn").color(NamedTextColor.GRAY)));
                            p.sendMessage(Settings.prefix.append(Component.text("/pg removerena [lobbynumber] [arenaname] - Remove an arena").color(NamedTextColor.GRAY)));
                            p.sendMessage(Settings.prefix.append(Component.text("/pg removespawn [lobbynumber] [arenaname] - Remove last spawn").color(NamedTextColor.GRAY)));
                            p.sendMessage(Settings.prefix.append(Component.text("/pg removedeathmatch [lobbynumber] [arenaname] - Remove last deathmatch spawn").color(NamedTextColor.GRAY)));
                            p.sendMessage(Settings.prefix.append(Component.text("/pg headp1(2;3) - Add Player Head to Stats-Wall").color(NamedTextColor.GRAY)));
                            p.sendMessage(Settings.prefix.append(Component.text("/pg signp1(2;3) - Add Player Sign to Stats-Wall").color(NamedTextColor.GRAY)));
                            p.sendMessage(Settings.prefix.append(Component.text("/pg joinsign [lobbynumber] - Add Join-Sign").color(NamedTextColor.GRAY)));
                            p.sendMessage(Settings.prefix.append(Component.text("/pg reload - Reload all configs").color(NamedTextColor.GRAY)));
                        } else {
                            p.sendMessage(Settings.prefix.append(Component.text("/pg setup - Set up plugin").color(NamedTextColor.GRAY)));
                            p.sendMessage(Settings.prefix.append(Component.text("/pg addlobby - Add a lobby").color(NamedTextColor.GRAY)));
                            p.sendMessage(Settings.prefix.append(Component.text("/pg addarena [arenaname] - Add an arena").color(NamedTextColor.GRAY)));
                            p.sendMessage(Settings.prefix.append(Component.text("/pg addspawn [arenaname] - Add a spawn").color(NamedTextColor.GRAY)));
                            p.sendMessage(Settings.prefix.append(Component.text("/pg adddeathmatch [arenaname] - Add a deathmatch spawn").color(NamedTextColor.GRAY)));
                            p.sendMessage(Settings.prefix.append(Component.text("/pg removearena [arenaname] - Remove an arena").color(NamedTextColor.GRAY)));
                            p.sendMessage(Settings.prefix.append(Component.text("/pg removespawn [arenaname] - Remove last spawn").color(NamedTextColor.GRAY)));
                            p.sendMessage(Settings.prefix.append(Component.text("/pg removedeathmatch [arenaname] - Remove last deathmatch spawn").color(NamedTextColor.GRAY)));
                            p.sendMessage(Settings.prefix.append(Component.text("/pg headp1(2;3) - Add Player Head to Stats-Wall").color(NamedTextColor.GRAY)));
                            p.sendMessage(Settings.prefix.append(Component.text("/pg signp1(2;3) - Add Player Sign to Stats-Wall").color(NamedTextColor.GRAY)));
                            p.sendMessage(Settings.prefix.append(Component.text("/pg joinsign - Set Join-Sign").color(NamedTextColor.GRAY)));
                            p.sendMessage(Settings.prefix.append(Component.text("/pg reload - Reload all configs").color(NamedTextColor.GRAY)));
                        }
                    }
                    if (p.hasPermission("pg.build")) {
                        p.sendMessage(Settings.prefix.append(Component.text("/pg build - Activate build mode").color(NamedTextColor.GRAY)));
                    }
                    if (p.hasPermission("pg.pause")) {
                        p.sendMessage(Settings.prefix.append(Component.text("/pg pause - Pause timer/countdown").color(NamedTextColor.GRAY)));
                    }
                    if (p.hasPermission("pg.force")) {
                        p.sendMessage(Settings.prefix.append(Component.text("/pg force [arenaname] - Force an arena").color(NamedTextColor.GRAY)));
                    }
                    if (p.hasPermission("pg.start")) {
                        p.sendMessage(Settings.prefix.append(Component.text("/pg start - Set lobby countdown to 10").color(NamedTextColor.GRAY)));
                    }
                    if (p.hasPermission("pg.join")) {
                        if (!pg.isStartOnJoin()) {
                            if (pg.isLobbySystem()) {
                                p.sendMessage(Settings.prefix.append(Component.text("/pg join # - Join a game").color(NamedTextColor.GRAY)));
                                p.sendMessage(Settings.prefix.append(Component.text("/pg list - List of all lobbies").color(NamedTextColor.GRAY)));
                            } else {
                                p.sendMessage(Settings.prefix.append(Component.text("/pg join - Join the game").color(NamedTextColor.GRAY)));
                            }
                        }
                    }
                    if (!pg.isStartOnJoin()) {
                        p.sendMessage(Settings.prefix.append(Component.text("/pg leave - Leave the game").color(NamedTextColor.GRAY)));
                    }
                    if (p.hasPermission("pg.stats")) {
                        p.sendMessage(Settings.prefix.append(Component.text("/pg stats [player] - Show player stats").color(NamedTextColor.GRAY)));
                    }
                    if (p.hasPermission("pg.update")) {
                        p.sendMessage(Settings.prefix.append(Component.text("/pg version - Show your and latest version of plugin").color(NamedTextColor.GRAY)));
                    }
                    p.sendMessage(Messages.CommandsLabel());
                } else if (args[0].equalsIgnoreCase("headp1")) {
                    if (p.hasPermission("pg.setup")) {
                        pg.getConfig().set("pg.RankWall.headp1", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                        pg.saveConfig();
                        p.sendMessage(Messages.HeadSet());
                    }
                } else if (args[0].equalsIgnoreCase("headp2")) {
                    if (p.hasPermission("pg.setup")) {
                        pg.getConfig().set("pg.RankWall.headp2", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                        pg.saveConfig();
                        p.sendMessage(Messages.HeadSet());
                    }
                } else if (args[0].equalsIgnoreCase("headp3")) {
                    if (p.hasPermission("pg.setup")) {
                        pg.getConfig().set("pg.RankWall.headp3", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                        pg.saveConfig();
                        p.sendMessage(Messages.HeadSet());
                    }
                } else if (args[0].equalsIgnoreCase("signp1")) {
                    if (p.hasPermission("pg.setup")) {
                        pg.getConfig().set("pg.RankWall.signp1", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                        pg.saveConfig();
                        p.sendMessage(Messages.SignSet());
                    }
                } else if (args[0].equalsIgnoreCase("signp2")) {
                    if (p.hasPermission("pg.setup")) {
                        pg.getConfig().set("pg.RankWall.signp2", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                        pg.saveConfig();
                        p.sendMessage(Messages.SignSet());
                    }
                } else if (args[0].equalsIgnoreCase("signp3")) {
                    if (p.hasPermission("pg.setup")) {
                        pg.getConfig().set("pg.RankWall.signp3", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                        pg.saveConfig();
                        p.sendMessage(Messages.SignSet());
                    }
                } else if (args[0].equalsIgnoreCase("joinsign")) {
                    if (p.hasPermission("pg.setup")) {
                        if (!pg.isLobbySystem()) {
                            pg.setupHandler.setJoinSign(p);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("setup")) {
                    if (p.hasPermission("pg.setup")) {
                        try {
                            pg.setupPlayer.add(p);
                            pg.inv.put(p.getName(), p.getInventory().getContents());
                            pg.armor.put(p.getName(), p.getInventory().getArmorContents());
                            pg.lvl.put(p.getName(), p.getLevel());
                            pg.exp.put(p.getName(), p.getExp());
                            pg.loc.put(p.getName(), p.getLocation());
                            pg.gm.put(p.getName(), p.getGameMode());
                            PlayerInventory inventory = p.getInventory();
                            inventory.clear();
                            inventory.setHelmet(null);
                            inventory.setChestplate(null);
                            inventory.setLeggings(null);
                            inventory.setBoots(null);
                            p.setHealth(20);
                            p.setFoodLevel(20);
                            p.setLevel(0);
                            p.setExp(0);
                            p.setGameMode(GameMode.ADVENTURE);
                            pg.clearEffects(p);
                            p.setFireTicks(0);
                            pg.setup(p);
                            p.setAllowFlight(true);
                            p.setFlying(true);
                        } catch (Exception ex) {
                            p.sendMessage(Messages.ErrorGeneric());
                        }
                    }
                } else if (args[0].equalsIgnoreCase("reload")) {
                    if (p.hasPermission("pg.setup")) {
                        try {
                            pg.setReload(true);
                            pg.close();
                            if (pg.isLobbySystem()) {
                                String s;
                                if (pg.playerLobby.containsKey(p)) {
                                    for (int ii = 1; ii <= 27; ii++) {
                                        if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                                            s = Integer.toString(ii);
                                            pg.onLeaveLobby(p, s);
                                            break;
                                        }
                                    }
                                } else if (pg.specLobby.containsKey(p)) {
                                    for (int ii = 1; ii <= 27; ii++) {
                                        if (pg.specLobby.get(p).contains(Integer.toString(ii))) {
                                            s = Integer.toString(ii);
                                            pg.onLeaveLobby(p, s);
                                            break;
                                        }
                                    }
                                }
                            } else {
                                if (pg.pgPlayers.contains(p) || pg.specPlayers.contains(p)) {
                                    pg.onLeave(p);
                                    if (pg.isStartOnJoin()) {
                                        p.kick(Messages.ServerStopped());
                                    }
                                }
                            }
                            pg.connect();
                            pg.ConnectMySQL();
                            pg.onReload();
                            p.sendMessage(Messages.PluginReloaded());
                            pg.setReload(false);
                        } catch (Exception ex) {
                            p.sendMessage(Messages.ErrorGeneric());
                        }
                    }
                } else if (args[0].equalsIgnoreCase("version")) {
                    if (p.hasPermission("pg.update")) {
                        new UpdateChecker(pg, 87633).getVersion(version -> {
                            String currentVersion = pg.getPluginMeta().getVersion();
                            if (currentVersion.equalsIgnoreCase(version)) {
                                p.sendMessage(Messages.UpdateNotAvailable());
                            } else {
                                p.sendMessage(Messages.UpdateAvailable(currentVersion, version));
                            }
                        });
                    }
                } else if (args[0].equalsIgnoreCase("list")) {
                    if (p.hasPermission("pg.join")) {
                        if (pg.isLobbySystem()) {
                                if (!pg.playerLobby.containsKey(p)) {
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, Settings.prefix.append(Component.text("Lobby List").color(NamedTextColor.DARK_AQUA)));
                                for (int slot = 1; slot <= 27; slot++) {
                                    if (Settings.arenadata.contains("pg.lobbies." + slot)) {
                                        ArrayList<Component> listlore = new ArrayList<>();
                                        ItemStack listmap = new ItemStack(Material.MAP);
                                        ItemMeta listmappmeta = listmap.getItemMeta();
                                        listmappmeta.displayName(Component.text(Integer.toString(slot)));
                                        listlore.add(Component.text(Integer.toString(slot)));
                                        listmappmeta.lore(listlore);
                                        listmap.setItemMeta(listmappmeta);
                                        inv.setItem(slot - 1, listmap);
                                    }
                                }
                                p.openInventory(inv);
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("join")) {
                    if (p.hasPermission("pg.join")) {
                        if (!pg.isStartOnJoin()) {
                            if (pg.isLobbySystem()) {
                                p.sendMessage(Settings.prefix.append(Component.text("/pg join # - Join a game").color(NamedTextColor.GRAY)));
                            } else {
                                if (pg.game.getLobbies().size() > 0) {
                                    Lobby lobby = pg.game.getLobby(1);
                                    lobby.join(p);
                                } else {
                                    p.sendMessage(Messages.LobbyDoesNotExist());
                                }
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("leave")) {
                    Lobby lobby = pg.game.getLobbyByPlayer(p);
                    if (lobby != null) {
                        lobby.leave(p);
                    }
                } else if (args[0].equalsIgnoreCase("start")) {
                    if (p.hasPermission("pg.start")) {
                        if (pg.isLobbySystem()) {
                            if (pg.playerLobby.containsKey(p) || pg.specLobby.containsKey(p)) {
                                String s = null;
                                for (int ii = 1; ii <= 27; ii++) {
                                    if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
                                if (pg.lobbyAmount.get(s) >= pg.lobbyminPlayers.get(s)) {
                                    if (pg.countdownLobby.get(s) >= 10) {
                                        pg.countdownLobby.replace(s, 10);
                                        for (Player all : pg.playerLobby.keySet()) {
                                            if (pg.playerLobby.get(all).equals(s)) {
                                                    all.sendMessage(Messages.GameStarted());
                                                }
                                        }
                                    } else {
                                        p.sendMessage(Messages.GameAlreadyStarted());
                                    }
                                } else {
                                    p.sendMessage(Messages.GameNotEnoughPlayers());
                                }
                            }
                        } else {
                            if (pg.pgPlayers.contains(p) || pg.specPlayers.contains(p)) {
                                if (pg.pgPlayers.size() >= pg.getMinPlayers()) {
                                    if (pg.getCountdown() >= 10) {
                                        pg.setCountdown(10);
                                        for (Player all : pg.pgPlayers) {
                                            all.sendMessage(Messages.GameStarted());
                                        }
                                    } else {
                                        p.sendMessage(Messages.GameAlreadyStarted());
                                    }
                                        } else {
                                    p.sendMessage(Messages.GameNotEnoughPlayers());
                                }
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("pause")) {
                    if (p.hasPermission("pg.pause")) {
                        if (pg.isLobbySystem()) {
                            if (pg.playerLobby.containsKey(p) || pg.specLobby.containsKey(p)) {
                                String s = null;
                                for (int ii = 1; ii <= 27; ii++) {
                                    if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
                                if (pg.lobbyPause.get(s)) {
                                    pg.lobbyPause.replace(s, false);
                                } else {
                                    pg.lobbyPause.replace(s, true);
                                }
                                if (pg.lobbyPause.get(s)) {
                                    for (Player all : pg.playerLobby.keySet()) {
                                        if (pg.playerLobby.get(all).equals(s)) {
                                            all.sendMessage(Messages.PauseToggle(pg.lobbyPause.get(s)));
                                        }
                                    }
                                    for (Player all : pg.specLobby.keySet()) {
                                        if (pg.specLobby.get(all).equals(s)) {
                                            all.sendMessage(Messages.PauseToggle(pg.lobbyPause.get(s)));
                                        }
                                    }
                                } else {
                                    for (Player all : pg.playerLobby.keySet()) {
                                        if (pg.playerLobby.get(all).equals(s)) {
                                            all.sendMessage(Messages.PauseToggle(pg.lobbyPause.get(s)));
                                        }
                                    }
                                    for (Player all : pg.specLobby.keySet()) {
                                        if (pg.specLobby.get(all).equals(s)) {
                                            all.sendMessage(Messages.PauseToggle(pg.lobbyPause.get(s)));
                                        }
                                    }
                                }
                            }
                        } else {
                            if (pg.pgPlayers.contains(p) || pg.specPlayers.contains(p)) {
                                pg.changePause();
                                if (pg.isPause()) {
                                    for (Player all : pg.pgPlayers) {
                                        all.sendMessage(Messages.PauseToggle(pg.isPause()));
                                    }
                                    for (Player all : pg.specPlayers) {
                                        all.sendMessage(Messages.PauseToggle(pg.isPause()));
                                    }
                                } else {
                                    for (Player all : pg.pgPlayers) {
                                        all.sendMessage(Messages.PauseToggle(pg.isPause()));
                                    }
                                    for (Player all : pg.specPlayers) {
                                        all.sendMessage(Messages.PauseToggle(pg.isPause()));
                                    }
                                }
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("build")) {
                    if (p.hasPermission("pg.build")) {
                        if (pg.isLobbySystem()) {
                            if (pg.playerLobby.containsKey(p) || pg.specLobby.containsKey(p)) {
                                String s = null;
                                for (int ii = 1; ii <= 27; ii++) {
                                    if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
                                if (pg.lobbyBuild.get(s)) {
                                    pg.lobbyBuild.replace(s, false);
                                } else {
                                    pg.lobbyBuild.replace(s, true);
                                }
                                if (pg.lobbyBuild.get(s)) {
                                    for (Player all : pg.playerLobby.keySet()) {
                                        if (pg.playerLobby.get(all).equals(s)) {
                                            all.sendMessage(Messages.BuildToggle(pg.lobbyBuild.get(s)));
                                        }
                                    }
                                    for (Player all : pg.specLobby.keySet()) {
                                        if (pg.specLobby.get(all).equals(s)) {
                                            all.sendMessage(Messages.BuildToggle(pg.lobbyBuild.get(s)));
                                        }
                                    }
                                } else {
                                    for (Player all : pg.playerLobby.keySet()) {
                                        if (pg.playerLobby.get(all).equals(s)) {
                                            all.sendMessage(Messages.BuildToggle(pg.lobbyBuild.get(s)));
                                        }
                                    }
                                    for (Player all : pg.specLobby.keySet()) {
                                        if (pg.specLobby.get(all).equals(s)) {
                                            all.sendMessage(Messages.BuildToggle(pg.lobbyBuild.get(s)));
                                        }
                                    }
                                }
                            }
                        } else {
                            if (pg.pgPlayers.contains(p) || pg.specPlayers.contains(p)) {
                                pg.changeBuild();
                                if (pg.isBuild()) {
                                    for (Player all : pg.pgPlayers) {
                                        all.sendMessage(Messages.BuildToggle(pg.isBuild()));
                                    }
                                    for (Player all : pg.specPlayers) {
                                        all.sendMessage(Messages.BuildToggle(pg.isBuild()));
                                    }
                                } else {
                                    for (Player all : pg.pgPlayers) {
                                        all.sendMessage(Messages.BuildToggle(pg.isBuild()));
                                    }
                                    for (Player all : pg.specPlayers) {
                                        all.sendMessage(Messages.BuildToggle(pg.isBuild()));
                                    }
                                }
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("enablelobby")) {
                    if (p.hasPermission("pg.setup")) {
                        if (!pg.isLobbySystem()) {
                            pg.setupHandler.enableLobby(p);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("addlobby")) {
                    if (p.hasPermission("pg.setup")) {
                        if (!pg.isLobbySystem()) {
                            pg.setupHandler.addLobby(p);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("stats")) {
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
                } else {
                    p.sendMessage(Messages.HelpUsePgHelp());
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("enablelobby")) {
                    if (p.hasPermission("pg.setup")) {
                        if (pg.isLobbySystem()) {
                            int lobbyId = Integer.parseInt(args[1]);
                            pg.setupHandler.enableLobby(p, lobbyId);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("addlobby")) {
                    if (p.hasPermission("pg.setup")) {
                        if (pg.isLobbySystem()) {
                            int lobbyId = Integer.parseInt(args[1]);
                            pg.setupHandler.addLobby(p, lobbyId);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("removelobby")) {
                    if (p.hasPermission("pg.setup")) {
                        if (pg.isLobbySystem()) {
                            int lobbyId = Integer.parseInt(args[1]);
                            pg.setupHandler.removeLobby(p, lobbyId);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("join")) {
                    if (pg.isLobbySystem()) {
                        int lobbyId = Integer.parseInt(args[1]);
                        if (p.hasPermission("pg.join")) {
                            if (Settings.arenadata.contains("pg.lobbies." + lobbyId)) {
                                if (pg.game.getLobbies().size() > 0) {
                                    Lobby lobby = pg.game.getLobby(lobbyId);
                                    lobby.join(p);
                                }
                            } else {
                                p.sendMessage(Messages.LobbyDoesNotExist());
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("force")) {
                    if (p.hasPermission("pg.force")) {
                        if (pg.isLobbySystem()) {
                            if (pg.playerLobby.containsKey(p) || pg.specLobby.containsKey(p)) {
                                String s = null;
                                for (int ii = 1; ii <= 27; ii++) {
                                    if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
                                String arena = args[1];
                                String arenaNumber = null;
                                try {
                                    pg.lobbyForcearena.replace(s, true);
                                    int i = 1;
                                    boolean votetedarena = false;
                                    while (!votetedarena) {
                                        if (arena.matches(Objects.requireNonNull(Settings.arenadata.getString("pg.arenas." + i + ".name")))) {
                                            arenaNumber = Integer.toString(i);
                                            pg.lobbyVotedarena.replace(s, arena);
                                            votetedarena = true;
                                        } else {
                                            i++;
                                        }
                                    }
                                    pg.lobbyVote.replace(s, arenaNumber);
                                    for (Player all : pg.playerLobby.keySet()) {
                                        if (pg.playerLobby.get(all).equals(s)) {
                                            all.sendMessage(Messages.ArenaForced(arena));
                                        }
                                    }
                                } catch (Exception ex) {
                                    p.sendMessage(Messages.ArenaNotArena(arena));
                                }
                            }
                        } else {
                            if (pg.pgPlayers.contains(p) || pg.specPlayers.contains(p)) {
                                String arena = args[1];
                                String arenaNumber = null;
                                try {
                                    pg.setForcearena(true);
                                    int i = 1;
                                    boolean votetedarena = false;
                                    while (!votetedarena) {
                                        if (arena.matches(Objects.requireNonNull(Settings.arenadata.getString("pg.arenas." + i + ".name")))) {
                                            arenaNumber = Integer.toString(i);
                                            pg.setVotedArena(arena);
                                            votetedarena = true;
                                        } else {
                                            i++;
                                        }
                                    }
                                    pg.setVote(arenaNumber);
                                    for (Player all : pg.pgPlayers) {
                                        all.sendMessage(Messages.ArenaForced(arena));
                                    }
                                } catch (Exception ex) {
                                    p.sendMessage(Messages.ArenaNotArena(arena));
                                }
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("removearena")) {
                    if (p.hasPermission("pg.setup")) {
                        if (!pg.isLobbySystem()) {
                            String arenaName = args[1];
                            pg.setupHandler.removeArena(p, arenaName);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("addarena")) {
                    if (p.hasPermission("pg.setup")) {
                        if (!pg.isLobbySystem()) {
                            String arenaName = args[1];
                            pg.setupHandler.addArena(p, arenaName);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("addspawn")) {
                    if (p.hasPermission("pg.setup")) {
                        if (!pg.isLobbySystem()) {
                            String arenaName = args[1];
                            pg.setupHandler.addSpawn(p, arenaName);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("removespawn")) {
                    if (p.hasPermission("pg.setup")) {
                        if (!pg.isLobbySystem()) {
                            String arenaName = args[1];
                            pg.setupHandler.removeSpawn(p, arenaName);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("adddeathmatch")) {
                    if (p.hasPermission("pg.setup")) {
                        if (!pg.isLobbySystem()) {
                            String arenaName = args[1];
                            pg.setupHandler.addDeathmatchSpawn(p, arenaName);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("removedeathmatch")) {
                    if (p.hasPermission("pg.setup")) {
                        if (!pg.isLobbySystem()) {
                            String arenaName = args[1];
                            pg.setupHandler.removeDeathmatchSpawn(p, arenaName);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("stats")) {
                    if (p.hasPermission("pg.stats")) {
                        Player pstats = Bukkit.getPlayer(args[1]);
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
                } else if (args[0].equalsIgnoreCase("joinsign")) {
                    if (p.hasPermission("pg.setup")) {
                        if (pg.isLobbySystem()) {
                            int lobbyId = Integer.parseInt(args[1]);
                            pg.setupHandler.setJoinSign(p, lobbyId);
                        }
                    }
                } else {
                    p.sendMessage(Messages.HelpUsePgHelp());
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("removearena")) {
                    if (p.hasPermission("pg.setup")) {
                        if (pg.isLobbySystem()) {
                            int lobbyId = Integer.parseInt(args[1]);
                            String arenaName = args[2];
                            pg.setupHandler.removeArena(p, arenaName, lobbyId);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("addarena")) {
                    if (p.hasPermission("pg.setup")) {
                        if (pg.isLobbySystem()) {
                            int lobbyId = Integer.parseInt(args[1]);
                            String arenaName = args[2];
                            pg.setupHandler.addArena(p, arenaName, lobbyId);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("addspawn")) {
                    if (p.hasPermission("pg.setup")) {
                        if (pg.isLobbySystem()) {
                            int lobbyId = Integer.parseInt(args[1]);
                            String arenaName = args[2];
                            pg.setupHandler.addSpawn(p, arenaName, lobbyId);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("removespawn")) {
                    if (p.hasPermission("pg.setup")) {
                        if (pg.isLobbySystem()) {
                            int lobbyId = Integer.parseInt(args[1]);
                            String arenaName = args[2];
                            pg.setupHandler.removeSpawn(p, arenaName, lobbyId);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("adddeathmatch")) {
                    if (p.hasPermission("pg.setup")) {
                        if (pg.isLobbySystem()) {
                            int lobbyId = Integer.parseInt(args[1]);
                            String arenaName = args[2];
                            pg.setupHandler.addDeathmatchSpawn(p, arenaName, lobbyId);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("removedeathmatch")) {
                    if (p.hasPermission("pg.setup")) {
                        if (pg.isLobbySystem()) {
                            int lobbyId = Integer.parseInt(args[1]);
                            String arenaName = args[2];
                            pg.setupHandler.removeDeathmatchSpawn(p, arenaName, lobbyId);
                        }
                    }
                } else {
                    p.sendMessage(Messages.HelpUsePgHelp());
                }
            } else {
                p.sendMessage(Messages.HelpUsePgHelp());
            }
        } else {
            if (args.length == 0) {
                p.sendMessage(Messages.CommandsLabel());
                if (p.hasPermission("pg.stats")) {
                    p.sendMessage(Settings.prefix.append(Component.text("/pg stats - Show your stats").color(NamedTextColor.GRAY)));
                }
                if (p.hasPermission("pg.setup")) {
                    p.sendMessage(Settings.prefix.append(Component.text("/pg headp1(2;3) - Add Player Head to Stats-Wall").color(NamedTextColor.GRAY)));
                    p.sendMessage(Settings.prefix.append(Component.text("/pg signp1(2;3) - Add Player Sign to Stats-Wall").color(NamedTextColor.GRAY)));
                }
                p.sendMessage(Messages.CommandsLabel());
            } else if (args.length == 1) {
                if (p.hasPermission("pg.stats")) {
                    if (args[0].equalsIgnoreCase("stats")) {
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
                if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("commands")) {
                    p.sendMessage(Messages.CommandsLabel());
                    if (p.hasPermission("pg.stats")) {
                        p.sendMessage(Settings.prefix.append(Component.text("/pg stats - Show your stats").color(NamedTextColor.GRAY)));
                    }
                    if (p.hasPermission("pg.setup")) {
                        p.sendMessage(Settings.prefix.append(Component.text("/pg headp1(2;3) - Add Player Head to Stats-Wall").color(NamedTextColor.GRAY)));
                        p.sendMessage(Settings.prefix.append(Component.text("/pg signp1(2;3) - Add Player Sign to Stats-Wall").color(NamedTextColor.GRAY)));
                    }
                    p.sendMessage(Messages.CommandsLabel());
                }
                if (p.hasPermission("pg.setup")) {
                    if (args[0].equalsIgnoreCase("headp1")) {
                        pg.getConfig().set("pg.RankWall.headp1", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                        pg.saveConfig();
                        p.sendMessage(Messages.HeadSet());
                    } else if (args[0].equalsIgnoreCase("headp2")) {
                        pg.getConfig().set("pg.RankWall.headp2", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                        pg.saveConfig();
                        p.sendMessage(Messages.HeadSet());
                    } else if (args[0].equalsIgnoreCase("headp3")) {
                        pg.getConfig().set("pg.RankWall.headp3", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                        pg.saveConfig();
                        p.sendMessage(Messages.HeadSet());
                    } else if (args[0].equalsIgnoreCase("signp1")) {
                        pg.getConfig().set("pg.RankWall.signp1", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                        pg.saveConfig();
                        p.sendMessage(Messages.SignSet());
                    } else if (args[0].equalsIgnoreCase("signp2")) {
                        pg.getConfig().set("pg.RankWall.signp2", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                        pg.saveConfig();
                        p.sendMessage(Messages.SignSet());
                    } else if (args[0].equalsIgnoreCase("signp3")) {
                        pg.getConfig().set("pg.RankWall.signp3", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                        pg.saveConfig();
                        p.sendMessage(Messages.SignSet());
                    }
                }
            } else {
                p.sendMessage(Messages.HelpUsePgHelp());
            }
        }
        return false;
    }
}
