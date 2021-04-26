package com.asga.potiongames.commands;

import com.asga.potiongames.main.PotionGames;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class Commands implements CommandExecutor {
    private final PotionGames pg;

    public Commands(PotionGames pg) {
        this.pg = pg;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        FileConfiguration arenadata = YamlConfiguration.loadConfiguration(pg.arenadatafile);
        Player p = (Player) sender;
        if (pg.isGameServer()) {
            if (args.length == 0) {
                p.sendMessage(pg.prefix + "--------------" + pg.chat.get(64) + "--------------");
                if (p.hasPermission("pg.setup")) {
                    if (pg.isLobbySystem()) {
                        p.sendMessage(pg.prefix + "/pg setup - Set up plugin");
                        p.sendMessage(pg.prefix + "/pg setlobby [lobbynumber] - Set lobby");
                        p.sendMessage(pg.prefix + "/pg dellobby [lobbynumber] - Remove lobby");
                        p.sendMessage(pg.prefix + "/pg addarena [lobbynumber] [arenaname] - Add an arena");
                        p.sendMessage(pg.prefix + "/pg addspawn [lobbynumber] [arenaname] - Add a spawn");
                        p.sendMessage(pg.prefix + "/pg delarena [lobbynumber] [arenaname] - Remove an arena");
                        p.sendMessage(pg.prefix + "/pg delspawn [lobbynumber] [arenaname] - Remove last spawn");
                        p.sendMessage(pg.prefix + "/pg headp1(2;3) - Add Player Head to Stats-Wall");
                        p.sendMessage(pg.prefix + "/pg signp1(2;3) - Add Player Sign to Stats-Wall");
                        p.sendMessage(pg.prefix + "/pg joinsign [lobbynumber] - Add Join-Sign");
                    } else {
                        p.sendMessage(pg.prefix + "/pg setup - Set up plugin");
                        p.sendMessage(pg.prefix + "/pg setlobby - Set lobby");
                        p.sendMessage(pg.prefix + "/pg addarena [arenaname] - Add an arena");
                        p.sendMessage(pg.prefix + "/pg addspawn [arenaname] - Add a spawn");
                        p.sendMessage(pg.prefix + "/pg delarena [arenaname] - Remove an arena");
                        p.sendMessage(pg.prefix + "/pg delspawn [arenaname] - Remove last spawn");
                        p.sendMessage(pg.prefix + "/pg headp1(2;3) - Add Player Head to Stats-Wall");
                        p.sendMessage(pg.prefix + "/pg signp1(2;3) - Add Player Sign to Stats-Wall");
                        p.sendMessage(pg.prefix + "/pg joinsign - Set Join-Sign");
                    }
                }
                if (p.hasPermission("pg.build")) {
                    p.sendMessage(pg.prefix + "/pg build - Activate build mode");
                }
                if (p.hasPermission("pg.pause")) {
                    p.sendMessage(pg.prefix + "/pg pause - Pause timer/countdown");
                }
                if (p.hasPermission("pg.force")) {
                    p.sendMessage(pg.prefix + "/pg force [arenaname] - Force an arena");
                }
                if (p.hasPermission("pg.start")) {
                    p.sendMessage(pg.prefix + "/pg start - Set lobby countdown to 10");
                }
                if (p.hasPermission("pg.join")) {
                    if (pg.isLobbySystem()) {
                        p.sendMessage(pg.prefix + "/pg join # - Join a game (startOnJoin = false)");
                    } else {
                        p.sendMessage(pg.prefix + "/pg join - Join the game (startOnJoin = false)");
                    }
                }
                if (p.hasPermission("pg.leave")) {
                    p.sendMessage(pg.prefix + "/pg leave - Leave the game (startOnJoin = false)");
                }
                if (p.hasPermission("pg.stats")) {
                    p.sendMessage(pg.prefix + "/pg stats - Show your stats");
                }
                p.sendMessage(pg.prefix + "--------------" + pg.chat.get(64) + "--------------");
            } else if (args.length == 1) {
                if (p.hasPermission("pg.setup")) {
                    if (args[0].equalsIgnoreCase("headp1")) {
                        pg.getConfig().set("pg.RankWall.headp1", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                        pg.saveConfig();
                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(34));
                    } else if (args[0].equalsIgnoreCase("headp2")) {
                        pg.getConfig().set("pg.RankWall.headp2", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                        pg.saveConfig();
                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(34));
                    } else if (args[0].equalsIgnoreCase("headp3")) {
                        pg.getConfig().set("pg.RankWall.headp3", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                        pg.saveConfig();
                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(34));
                    } else if (args[0].equalsIgnoreCase("signp1")) {
                        pg.getConfig().set("pg.RankWall.signp1", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                        pg.saveConfig();
                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(35));
                    } else if (args[0].equalsIgnoreCase("signp2")) {
                        pg.getConfig().set("pg.RankWall.signp2", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                        pg.saveConfig();
                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(35));
                    } else if (args[0].equalsIgnoreCase("signp3")) {
                        pg.getConfig().set("pg.RankWall.signp3", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                        pg.saveConfig();
                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(35));
                    } else if (args[0].equalsIgnoreCase("joinsign")) {
                        if (!pg.isLobbySystem()) {
                            pg.getConfig().set("pg.Lobby.sign", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                            pg.saveConfig();
                            p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(35));
                        }
                    } else if (args[0].equalsIgnoreCase("setup")) {
                        PlayerInventory inventory = p.getInventory();
                        pg.inv.put(p.getName(), p.getInventory().getContents());
                        pg.armor.put(p.getName(), p.getInventory().getArmorContents());
                        pg.lvl.put(p.getName(), p.getLevel());
                        pg.exp.put(p.getName(), p.getExp());
                        pg.loc.put(p.getName(), p.getLocation());
                        pg.gm.put(p.getName(), p.getGameMode());
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
                    }
                }
                if (p.hasPermission("pg.stats")) {
                    if (args[0].equalsIgnoreCase("stats")) {
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
                if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("commands")) {
                    p.sendMessage(pg.prefix + "--------------" + pg.chat.get(64) + "--------------");
                    if (p.hasPermission("pg.setup")) {
                        if (pg.isLobbySystem()) {
                            p.sendMessage(pg.prefix + "/pg setup - Set up plugin");
                            p.sendMessage(pg.prefix + "/pg setlobby [lobbynumber] - Set lobby");
                            p.sendMessage(pg.prefix + "/pg dellobby [lobbynumber] - Remove lobby");
                            p.sendMessage(pg.prefix + "/pg addarena [lobbynumber] [arenaname] - Add an arena");
                            p.sendMessage(pg.prefix + "/pg addspawn [lobbynumber] [arenaname] - Add a spawn");
                            p.sendMessage(pg.prefix + "/pg delarena [lobbynumber] [arenaname] - Remove an arena");
                            p.sendMessage(pg.prefix + "/pg delspawn [lobbynumber] [arenaname] - Remove last spawn");
                            p.sendMessage(pg.prefix + "/pg headp1(2;3) - Add Player Head to Stats-Wall");
                            p.sendMessage(pg.prefix + "/pg signp1(2;3) - Add Player Sign to Stats-Wall");
                            p.sendMessage(pg.prefix + "/pg joinsign [lobbynumber] - Add Join-Sign");
                        } else {
                            p.sendMessage(pg.prefix + "/pg setup - Set up plugin");
                            p.sendMessage(pg.prefix + "/pg setlobby - Set lobby");
                            p.sendMessage(pg.prefix + "/pg addarena [arenaname] - Add an arena");
                            p.sendMessage(pg.prefix + "/pg addspawn [arenaname] - Add a spawn");
                            p.sendMessage(pg.prefix + "/pg delarena [arenaname] - Remove an arena");
                            p.sendMessage(pg.prefix + "/pg delspawn [arenaname] - Remove last spawn");
                            p.sendMessage(pg.prefix + "/pg headp1(2;3) - Add Player Head to Stats-Wall");
                            p.sendMessage(pg.prefix + "/pg signp1(2;3) - Add Player Sign to Stats-Wall");
                            p.sendMessage(pg.prefix + "/pg joinsign - Set Join-Sign");
                        }
                    }
                    if (p.hasPermission("pg.build")) {
                        p.sendMessage(pg.prefix + "/pg build - Activate build mode");
                    }
                    if (p.hasPermission("pg.pause")) {
                        p.sendMessage(pg.prefix + "/pg pause - Pause timer/countdown");
                    }
                    if (p.hasPermission("pg.force")) {
                        p.sendMessage(pg.prefix + "/pg force [arenaname] - Force an arena");
                    }
                    if (p.hasPermission("pg.start")) {
                        p.sendMessage(pg.prefix + "/pg start - Set lobby countdown to 10");
                    }
                    if (p.hasPermission("pg.join")) {
                        if (pg.isLobbySystem()) {
                            p.sendMessage(pg.prefix + "/pg join # - Join a game (startOnJoin = false)");
                        } else {
                            p.sendMessage(pg.prefix + "/pg join - Join the game (startOnJoin = false)");
                        }
                    }
                    if (p.hasPermission("pg.leave")) {
                        p.sendMessage(pg.prefix + "/pg leave - Leave the game (startOnJoin = false)");
                    }
                    if (p.hasPermission("pg.stats")) {
                        p.sendMessage(pg.prefix + "/pg stats - Show your stats");
                    }
                    p.sendMessage(pg.prefix + "--------------" + pg.chat.get(64) + "--------------");
                }
                if (args[0].equalsIgnoreCase("join")) {
                    if (p.hasPermission("pg.join")) {
                        if (pg.isLobbySystem()) {
                            p.sendMessage(pg.prefix + "/pg join # - Join a game (startOnJoin = false)");
                        } else {
                            if (!pg.pgPlayers.contains(p) && !pg.specPlayers.contains(p)) {
                                pg.onJoin(p);
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("leave")) {
                    if (p.hasPermission("pg.leave")) {
                        if (pg.isLobbySystem()) {
                            if (pg.playerLobby.containsKey(p) && !pg.isStartOnJoin()) {
                                String s = null;
                                for (int ii = 1; ii <= 1000; ii++) {
                                    if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
                                pg.leaveLobby(p, s);
                            } else if (pg.specLobby.containsKey(p) && !pg.isStartOnJoin()) {
                                String s = null;
                                for (int ii = 1; ii <= 1000; ii++) {
                                    if (pg.specLobby.get(p).contains(Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
                                pg.leaveLobby(p, s);
                            }
                        } else {
                            if (pg.pgPlayers.contains(p) && !pg.isStartOnJoin() || pg.specPlayers.contains(p) && !pg.isStartOnJoin()) {
                                pg.onLeave(p);
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("start")) {
                    if (p.hasPermission("pg.start")) {
                        if (pg.isLobbySystem()) {
                            if (pg.playerLobby.containsKey(p) || pg.specLobby.containsKey(p)) {
                                String s = null;
                                for (int ii = 1; ii <= 1000; ii++) {
                                    if (pg.playerLobby.get(p).contains(Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
                                if (pg.lobbyAmount.get(s) >= pg.lobbyminPlayers.get(s)) {
                                    if (pg.countdownLobby.get(s) >= 10) {
                                        pg.countdownLobby.replace(s, 10);
                                        for (Player all : pg.playerLobby.keySet()) {
                                            if (pg.playerLobby.get(all).equals(s)) {
                                                all.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(20));
                                            }
                                        }
                                    } else {
                                        p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(19));
                                    }
                                } else {
                                    p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(21));
                                }
                            }
                        } else {
                            if (pg.pgPlayers.contains(p) || pg.specPlayers.contains(p)) {
                                if (pg.pgPlayers.size() >= pg.getMinPlayers()) {
                                    if (pg.getCountdown() >= 10) {
                                        pg.setCountdown(10);
                                        for (Player all : pg.pgPlayers) {
                                            all.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(20));
                                        }
                                    } else {
                                        p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(19));
                                    }
                                } else {
                                    p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(21));
                                }
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("pause")) {
                    if (p.hasPermission("pg.pause")) {
                        if (pg.isLobbySystem()) {
                            if (pg.playerLobby.containsKey(p) || pg.specLobby.containsKey(p)) {
                                String s = null;
                                for (int ii = 1; ii <= 1000; ii++) {
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
                                            all.sendMessage(pg.prefix + ChatColor.AQUA + pg.chat.get(22) + ": " + ChatColor.GREEN + pg.lobbyPause.get(s));
                                        }
                                    }
                                    for (Player all : pg.specLobby.keySet()) {
                                        if (pg.specLobby.get(all).equals(s)) {
                                            all.sendMessage(pg.prefix + ChatColor.AQUA + pg.chat.get(22) + ": " + ChatColor.GREEN + pg.lobbyPause.get(s));
                                        }
                                    }
                                } else {
                                    for (Player all : pg.playerLobby.keySet()) {
                                        if (pg.playerLobby.get(all).equals(s)) {
                                            all.sendMessage(pg.prefix + ChatColor.AQUA + pg.chat.get(22) + ": " + ChatColor.RED + pg.lobbyPause.get(s));
                                        }
                                    }
                                    for (Player all : pg.specLobby.keySet()) {
                                        if (pg.specLobby.get(all).equals(s)) {
                                            all.sendMessage(pg.prefix + ChatColor.AQUA + pg.chat.get(22) + ": " + ChatColor.RED + pg.lobbyPause.get(s));
                                        }
                                    }
                                }
                            }
                        } else {
                            if (pg.pgPlayers.contains(p) || pg.specPlayers.contains(p)) {
                                pg.changePause();
                                if (pg.isPause()) {
                                    for (Player all : pg.pgPlayers) {
                                        all.sendMessage(pg.prefix + ChatColor.AQUA + pg.chat.get(22) + ": " + ChatColor.GREEN + pg.isPause());
                                    }
                                    for (Player all : pg.specPlayers) {
                                        all.sendMessage(pg.prefix + ChatColor.AQUA + pg.chat.get(22) + ": " + ChatColor.GREEN + pg.isPause());
                                    }
                                } else {
                                    for (Player all : pg.pgPlayers) {
                                        all.sendMessage(pg.prefix + ChatColor.AQUA + pg.chat.get(22) + ": " + ChatColor.RED + pg.isPause());
                                    }
                                    for (Player all : pg.specPlayers) {
                                        all.sendMessage(pg.prefix + ChatColor.AQUA + pg.chat.get(22) + ": " + ChatColor.RED + pg.isPause());
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
                                for (int ii = 1; ii <= 1000; ii++) {
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
                                            all.sendMessage(pg.prefix + ChatColor.AQUA + pg.chat.get(23) + ": " + ChatColor.GREEN + pg.lobbyBuild.get(s));
                                        }
                                    }
                                    for (Player all : pg.specLobby.keySet()) {
                                        if (pg.specLobby.get(all).equals(s)) {
                                            all.sendMessage(pg.prefix + ChatColor.AQUA + pg.chat.get(23) + ": " + ChatColor.GREEN + pg.lobbyBuild.get(s));
                                        }
                                    }
                                } else {
                                    for (Player all : pg.playerLobby.keySet()) {
                                        if (pg.playerLobby.get(all).equals(s)) {
                                            all.sendMessage(pg.prefix + ChatColor.AQUA + pg.chat.get(23) + ": " + ChatColor.RED + pg.lobbyBuild.get(s));
                                        }
                                    }
                                    for (Player all : pg.specLobby.keySet()) {
                                        if (pg.specLobby.get(all).equals(s)) {
                                            all.sendMessage(pg.prefix + ChatColor.AQUA + pg.chat.get(23) + ": " + ChatColor.RED + pg.lobbyBuild.get(s));
                                        }
                                    }
                                }
                            }
                        } else {
                            if (pg.pgPlayers.contains(p) || pg.specPlayers.contains(p)) {
                                pg.changeBuild();
                                if (pg.isBuild()) {
                                    for (Player all : pg.pgPlayers) {
                                        all.sendMessage(pg.prefix + ChatColor.AQUA + pg.chat.get(23) + ": " + ChatColor.GREEN + pg.isBuild());
                                    }
                                    for (Player all : pg.specPlayers) {
                                        all.sendMessage(pg.prefix + ChatColor.AQUA + pg.chat.get(23) + ": " + ChatColor.GREEN + pg.isBuild());
                                    }
                                } else {
                                    for (Player all : pg.pgPlayers) {
                                        all.sendMessage(pg.prefix + ChatColor.AQUA + pg.chat.get(23) + ": " + ChatColor.RED + pg.isBuild());
                                    }
                                    for (Player all : pg.specPlayers) {
                                        all.sendMessage(pg.prefix + ChatColor.AQUA + pg.chat.get(23) + ": " + ChatColor.RED + pg.isBuild());
                                    }
                                }
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("setlobby")) {
                    if (p.hasPermission("pg.setup")) {
                        if (!pg.isLobbySystem()) {
                            pg.getConfig().set("pg.Lobby.world", Objects.requireNonNull(p.getLocation().getWorld()).getName());
                            pg.getConfig().set("pg.Lobby.coords", Objects.requireNonNull(p.getLocation()));
                            pg.saveConfig();
                            p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(24));
                        }
                    }
                } else {
                    p.sendMessage(pg.prefix + ChatColor.GRAY + pg.chat.get(75));
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("setlobby")) {
                    if (p.hasPermission("pg.setup")) {
                        if (pg.isLobbySystem()) {
                            arenadata.set("pg.lobbies." + args[1] + ".world", Objects.requireNonNull(p.getLocation().getWorld()).getName());
                            arenadata.set("pg.lobbies." + args[1] + ".coords", Objects.requireNonNull(p.getLocation()));
                            arenadata.set("pg.lobbies." + args[1] + ".activateTeams", true);
                            arenadata.set("pg.lobbies." + args[1] + ".activateKits", true);
                            arenadata.set("pg.lobbies." + args[1] + ".activateShop", true);
                            arenadata.set("pg.lobbies." + args[1] + ".teamSize", 2);
                            arenadata.set("pg.lobbies." + args[1] + ".maxPlayers", 24);
                            arenadata.set("pg.lobbies." + args[1] + ".minPlayers", 12);
                            arenadata.set("pg.lobbies." + args[1] + ".roundTime", 30);
                            try {
                                arenadata.save(pg.arenadatafile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(24) + ChatColor.GRAY + " (" + "Lobby: " + args[1] + ")");
                        }
                    }
                } else if (args[0].equalsIgnoreCase("dellobby")) {
                    if (p.hasPermission("pg.setup")) {
                        if (pg.isLobbySystem()) {
                            arenadata.set("pg.lobbies." + args[1], null);
                            try {
                                arenadata.save(pg.arenadatafile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(66) + ChatColor.GRAY + " (" + "Lobby: " + args[1] + ")");
                        }
                    }
                } else if (args[0].equalsIgnoreCase("join")) {
                    if (pg.isLobbySystem()) {
                        String s = args[1];
                        if (p.hasPermission("pg.join")) {
                            if (arenadata.contains("pg.lobbies." + s)) {
                                if (!pg.playerLobby.containsKey(p)) {
                                    pg.joinLobby(p, s);
                                }
                            } else {
                                p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(74));
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("force")) {
                    if (p.hasPermission("pg.force")) {
                        if (pg.isLobbySystem()) {
                            if (pg.playerLobby.containsKey(p) || pg.specLobby.containsKey(p)) {
                                String s = null;
                                for (int ii = 1; ii <= 1000; ii++) {
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
                                        if (arena.matches(Objects.requireNonNull(arenadata.getString("pg.arenas." + i + ".name")))) {
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
                                            all.sendMessage(pg.prefix + ChatColor.AQUA + arena + ChatColor.GREEN + " " + pg.chat.get(26));
                                        }
                                    }
                                } catch (Exception e) {
                                    p.sendMessage(pg.prefix + ChatColor.RED + ChatColor.AQUA + args[1] + ChatColor.RED + " " + pg.chat.get(27));
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
                                        if (arena.matches(Objects.requireNonNull(arenadata.getString("pg.arenas." + i + ".name")))) {
                                            arenaNumber = Integer.toString(i);
                                            pg.setVotedArena(arena);
                                            votetedarena = true;
                                        } else {
                                            i++;
                                        }
                                    }
                                    pg.setVote(arenaNumber);
                                    for (Player all : pg.pgPlayers) {
                                        all.sendMessage(pg.prefix + ChatColor.AQUA + arena + ChatColor.GREEN + " " + pg.chat.get(26));
                                    }
                                } catch (Exception e) {
                                    p.sendMessage(pg.prefix + ChatColor.RED + ChatColor.AQUA + args[1] + ChatColor.RED + " " + pg.chat.get(27));
                                }
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("delarena")) {
                    if (p.hasPermission("pg.setup")) {
                        if (!pg.isLobbySystem()) {
                            int arenaNumber = 1;
                            try {
                                int i = 1;
                                boolean arenaID = false;
                                while (!arenaID) {
                                    if (args[1].matches(Objects.requireNonNull(arenadata.getString("pg.arenas." + i + ".name")))) {
                                        arenaNumber = i;
                                        arenaID = true;
                                    } else {
                                        i++;
                                    }
                                }
                                String arenaName = args[1];
                                arenadata.set("pg.arenas." + arenaNumber, null);
                                arenadata.save(pg.arenadatafile);
                                p.sendMessage(pg.prefix + ChatColor.AQUA + arenaName + ChatColor.GREEN + " " + pg.chat.get(28));
                            } catch (Exception e) {
                                p.sendMessage(pg.prefix + ChatColor.AQUA + args[1] + ChatColor.RED + " " + pg.chat.get(27));
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("addarena")) {
                    if (p.hasPermission("pg.setup")) {
                        if (!pg.isLobbySystem()) {
                            int arenaNumber = 1;
                            try {
                                while (arenadata.contains("pg.arenas." + arenaNumber)) {
                                    arenaNumber++;
                                }
                                String arenaName = args[1];
                                arenadata.set("pg.arenas." + arenaNumber, p.getWorld());
                                arenadata.set("pg.arenas." + arenaNumber + ".world", p.getWorld().getName());
                                arenadata.set("pg.arenas." + arenaNumber + ".name", arenaName);
                                arenadata.save(pg.arenadatafile);
                                p.sendMessage(pg.prefix + ChatColor.AQUA + arenaName + ChatColor.GREEN + " " + pg.chat.get(29));
                            } catch (Exception e) {
                                p.sendMessage(pg.prefix + ChatColor.AQUA + args[1] + ChatColor.RED + " " + pg.chat.get(27));
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("addspawn")) {
                    if (p.hasPermission("pg.setup")) {
                        if (!pg.isLobbySystem()) {
                            int spawnNumber = 1;
                            int arenaNumber = 1;
                            try {
                                int i = 1;
                                boolean arenaName = false;
                                while (!arenaName) {
                                    if (args[1].matches(Objects.requireNonNull(arenadata.getString("pg.arenas." + i + ".name")))) {
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
                            } catch (Exception e) {
                                p.sendMessage(pg.prefix + ChatColor.AQUA + spawnNumber + ChatColor.RED + " " + pg.chat.get(31));
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("delspawn")) {
                    if (p.hasPermission("pg.setup")) {
                        if (!pg.isLobbySystem()) {
                            int arenaNumber = 1;
                            int spawnNumber = 1;
                            try {
                                int i = 1;
                                boolean arenaName = false;
                                while (!arenaName) {
                                    if (args[1].matches(Objects.requireNonNull(arenadata.getString("pg.arenas." + i + ".name")))) {
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
                                p.sendMessage(pg.prefix + ChatColor.AQUA + spawnNumber + ChatColor.GREEN + " " + pg.chat.get(32) + ChatColor.GRAY + " (" + "Arena: " + args[1] + ")");
                            } catch (Exception e) {
                                p.sendMessage(pg.prefix + ChatColor.AQUA + args[1] + ChatColor.RED + " " + pg.chat.get(31) + ChatColor.GRAY + " (" + "Arena: " + args[1] + ")");
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("joinsign")) {
                    if (p.hasPermission("pg.setup")) {
                        if (pg.isLobbySystem()) {
                            arenadata.set("pg.lobbies." + args[1] + ".sign", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                            try {
                                arenadata.save(pg.arenadatafile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(35) + ChatColor.GRAY + " (" + "Lobby: " + args[1] + ")");
                        }
                    }
                } else {
                    p.sendMessage(pg.prefix + ChatColor.GRAY + pg.chat.get(75));
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("delarena")) {
                    if (p.hasPermission("pg.setup")) {
                        if (pg.isLobbySystem()) {
                            int arenaNumber = 1;
                            try {
                                int i = 1;
                                boolean arenaID = false;
                                while (!arenaID) {
                                    if (args[2].matches(Objects.requireNonNull(arenadata.getString("pg.lobbies." + args[1] + "." + i + ".name")))) {
                                        arenaNumber = i;
                                        arenaID = true;
                                    } else {
                                        i++;
                                    }
                                }
                                String arenaName = args[2];
                                arenadata.set("pg.lobbies." + args[1] + "." + arenaNumber, null);
                                arenadata.save(pg.arenadatafile);
                                p.sendMessage(pg.prefix + ChatColor.AQUA + arenaName + ChatColor.GREEN + " " + pg.chat.get(28) + ChatColor.GRAY + " (" + "Lobby: " + args[1] + ")");
                            } catch (Exception e) {
                                p.sendMessage(pg.prefix + ChatColor.AQUA + args[1] + ChatColor.RED + " " + pg.chat.get(27) + ChatColor.GRAY + " (" + "Lobby: " + args[1] + ")");
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("addarena")) {
                    if (p.hasPermission("pg.setup")) {
                        if (pg.isLobbySystem()) {
                            int arenaNumber = 1;
                            try {
                                while (arenadata.contains("pg.lobbies." + args[1] + "." + arenaNumber)) {
                                    arenaNumber++;
                                }
                                String arenaName = args[2];
                                arenadata.set("pg.lobbies." + args[1] + "." + arenaNumber, p.getWorld());
                                arenadata.set("pg.lobbies." + args[1] + "." + arenaNumber + ".world", p.getWorld().getName());
                                arenadata.set("pg.lobbies." + args[1] + "." + arenaNumber + ".name", arenaName);
                                arenadata.save(pg.arenadatafile);
                                p.sendMessage(pg.prefix + ChatColor.AQUA + arenaName + ChatColor.GREEN + " " + pg.chat.get(29) + ChatColor.GRAY + " (" + "Lobby: " + args[1] + ")");
                            } catch (Exception e) {
                                p.sendMessage(pg.prefix + ChatColor.AQUA + args[1] + ChatColor.RED + " " + pg.chat.get(27) + ChatColor.GRAY + " (" + "Lobby: " + args[1] + ")");
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("addspawn")) {
                    if (p.hasPermission("pg.setup")) {
                        if (pg.isLobbySystem()) {
                            int spawnNumber = 1;
                            int arenaNumber = 1;
                            try {
                                int i = 1;
                                boolean arenaName = false;
                                while (!arenaName) {
                                    if (args[2].matches(Objects.requireNonNull(arenadata.getString("pg.lobbies." + args[1] + "." + i + ".name")))) {
                                        arenaNumber = i;
                                        arenaName = true;
                                    } else {
                                        i++;
                                    }
                                }
                                while (arenadata.contains("pg.lobbies." + args[1] + "." + arenaNumber + ".spawns." + spawnNumber)) {
                                    spawnNumber++;
                                }
                                arenadata.set("pg.lobbies." + args[1] + "." + arenaNumber + ".spawns." + spawnNumber, p.getLocation());
                                arenadata.save(pg.arenadatafile);
                                p.sendMessage(pg.prefix + ChatColor.AQUA + spawnNumber + ChatColor.GREEN + " " + pg.chat.get(30) + ChatColor.GRAY + " (" + "Lobby: " + args[1] + ")" + " (" + "Arena: " + args[2] + ")");
                            } catch (Exception e) {
                                p.sendMessage(pg.prefix + ChatColor.AQUA + args[1] + ChatColor.RED + " " + pg.chat.get(31) + ChatColor.GRAY + " (" + "Lobby: " + args[1] + ")" + " (" + "Arena: " + args[2] + ")");
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("delspawn")) {
                    if (p.hasPermission("pg.setup")) {
                        if (pg.isLobbySystem()) {
                            int arenaNumber = 1;
                            int spawnNumber = 1;
                            try {
                                int i = 1;
                                boolean arenaName = false;
                                while (!arenaName) {
                                    if (args[2].matches(Objects.requireNonNull(arenadata.getString("pg.lobbies." + args[1] + "." + i + ".name")))) {
                                        arenaNumber = i;
                                        arenaName = true;
                                    } else {
                                        i++;
                                    }
                                }
                                int max = 1;
                                while (arenadata.contains("pg.lobbies." + args[1] + "." + arenaNumber + ".spawns." + max)) {
                                    spawnNumber = max;
                                    max++;
                                }
                                arenadata.set("pg.lobbies." + args[1] + "." + arenaNumber + ".spawns." + spawnNumber, null);
                                arenadata.save(pg.arenadatafile);
                                p.sendMessage(pg.prefix + ChatColor.AQUA + spawnNumber + ChatColor.GREEN + " " + pg.chat.get(32) + ChatColor.GRAY + " (" + "Lobby: " + args[1] + ")" + " (" + "Arena: " + args[2] + ")");
                            } catch (Exception e) {
                                p.sendMessage(pg.prefix + ChatColor.AQUA + args[2] + ChatColor.RED + " " + pg.chat.get(31) + ChatColor.GRAY + " (" + "Lobby: " + args[1] + ")" + " (" + "Arena: " + args[2] + ")");
                            }
                        }
                    }
                } else {
                    p.sendMessage(pg.prefix + ChatColor.GRAY + pg.chat.get(75));
                }
            } else {
                p.sendMessage(pg.prefix + ChatColor.GRAY + pg.chat.get(75));
            }
        } else {
            if (args.length == 0) {
                p.sendMessage(pg.prefix + "--------------" + pg.chat.get(64) + "--------------");
                if (p.hasPermission("pg.stats")) {
                    p.sendMessage(pg.prefix + "/pg stats - Show your stats");
                }
                if (p.hasPermission("pg.setup")) {
                    p.sendMessage(pg.prefix + "/pg headp1(2;3) - Add Player Head to Stats-Wall");
                    p.sendMessage(pg.prefix + "/pg signp1(2;3) - Add Player Sign to Stats-Wall");
                }
                p.sendMessage(pg.prefix + "--------------" + pg.chat.get(64) + "--------------");
            } else if (args.length == 1) {
                if (p.hasPermission("pg.stats")) {
                    if (args[0].equalsIgnoreCase("stats")) {
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
                if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("commands")) {
                    p.sendMessage(pg.prefix + "--------------" + pg.chat.get(64) + "--------------");
                    if (p.hasPermission("pg.stats")) {
                        p.sendMessage(pg.prefix + "/pg stats - Show your stats");
                    }
                    if (p.hasPermission("pg.setup")) {
                        p.sendMessage(pg.prefix + "/pg headp1(2;3) - Add Player Head to Stats-Wall");
                        p.sendMessage(pg.prefix + "/pg signp1(2;3) - Add Player Sign to Stats-Wall");
                    }
                    p.sendMessage(pg.prefix + "--------------" + pg.chat.get(64) + "--------------");
                }
                if (p.hasPermission("pg.setup")) {
                    if (args[0].equalsIgnoreCase("headp1")) {
                        pg.getConfig().set("pg.RankWall.headp1", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                        pg.saveConfig();
                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(34));
                    } else if (args[0].equalsIgnoreCase("headp2")) {
                        pg.getConfig().set("pg.RankWall.headp2", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                        pg.saveConfig();
                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(34));
                    } else if (args[0].equalsIgnoreCase("headp3")) {
                        pg.getConfig().set("pg.RankWall.headp3", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                        pg.saveConfig();
                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(34));
                    } else if (args[0].equalsIgnoreCase("signp1")) {
                        pg.getConfig().set("pg.RankWall.signp1", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                        pg.saveConfig();
                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(35));
                    } else if (args[0].equalsIgnoreCase("signp2")) {
                        pg.getConfig().set("pg.RankWall.signp2", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                        pg.saveConfig();
                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(35));
                    } else if (args[0].equalsIgnoreCase("signp3")) {
                        pg.getConfig().set("pg.RankWall.signp3", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                        pg.saveConfig();
                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(35));
                    }
                }
            } else {
                p.sendMessage(pg.prefix + ChatColor.GRAY + pg.chat.get(75));
            }
        }
        return false;
    }

}
