package com.asga.potiongames.commands;

import com.asga.potiongames.main.PotionGames;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Commands implements CommandExecutor {
    private final PotionGames pg;

    public Commands(PotionGames pg) {
        this.pg = pg;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        Player p = (Player) sender;
        if (args.length == 1) {
            if (p.isOp()) {
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
            if (args[0].equalsIgnoreCase("stats")) {
                int wins = pg.getWins(p.getUniqueId().toString());
                int losts = pg.getLosts(p.getUniqueId().toString());
                int kills = pg.getKills(p.getUniqueId().toString());
                int deaths = pg.getDeaths(p.getUniqueId().toString());
                double kd = pg.getKD(p.getUniqueId().toString());
                p.sendMessage(pg.prefix + "--------------Stats--------------");
                p.sendMessage(pg.prefix + "Won: " + ChatColor.AQUA + wins);
                p.sendMessage(pg.prefix + "Lost: " + ChatColor.AQUA + losts);
                p.sendMessage(pg.prefix + "Kills: " + ChatColor.AQUA + kills);
                p.sendMessage(pg.prefix + "Deaths: " + ChatColor.AQUA + deaths);
                p.sendMessage(pg.prefix + "K/D: " + ChatColor.AQUA + kd);
                p.sendMessage(pg.prefix + "--------------Stats--------------");
            }
            if (args[0].equalsIgnoreCase("join")) {
                if (!pg.pgPlayers.contains(p) && !pg.specPlayers.contains(p)) {
                    pg.onJoin(p);
                }
            } else if (args[0].equalsIgnoreCase("leave")) {
                if (pg.pgPlayers.contains(p) && !pg.isStartOnJoin() || pg.specPlayers.contains(p) && !pg.isStartOnJoin()) {
                    pg.onLeave(p);
                }
            } else if (args[0].equalsIgnoreCase("start")) {
                if (pg.pgPlayers.contains(p) && p.isOp() || pg.specPlayers.contains(p) && p.isOp()) {
                    if (pg.pgPlayers.size() >= pg.getMinPlayers()) {
                        if (pg.getCountdown() >= 20) {
                            pg.setCountdown(20);
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
            } else if (args[0].equalsIgnoreCase("pause")) {
                if (pg.pgPlayers.contains(p) && p.isOp() || pg.specPlayers.contains(p) && p.isOp()) {
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
            } else if (args[0].equalsIgnoreCase("build")) {
                if (pg.pgPlayers.contains(p) && p.isOp() || pg.specPlayers.contains(p) && p.isOp()) {
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
            } else if (args[0].equalsIgnoreCase("setlobby")) {
                if (p.isOp()) {
                    pg.getConfig().set("pg.Lobby.world", Objects.requireNonNull(p.getLocation().getWorld()).getName());
                    pg.getConfig().set("pg.Lobby.coords", Objects.requireNonNull(p.getLocation()));
                    pg.saveConfig();
                    p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(24));
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("join")) {
                if (!pg.pgPlayers.contains(p) && !pg.specPlayers.contains(p)) {
                        int arenaNumber = 1;
                        try {
                            int i = 1;
                            boolean arenaName = false;
                            while (!arenaName) {
                                if (args[1].matches(Objects.requireNonNull(pg.getConfig().getString("pg.arenas." + i + ".name")))) {
                                    arenaNumber = i;
                                    arenaName = true;
                                } else {
                                    i++;
                                }
                            }
                            p.sendMessage(pg.prefix + "Test:true");
                        } catch (Exception ex) {
                            p.sendMessage(pg.prefix + "Test:false");
                        }
                    pg.setArenaID(String.valueOf(arenaNumber));
                    //pg.arenas.put(String.valueOf(arenaNumber), p);
                    pg.arenaplayers.put(String.valueOf(arenaNumber), p);
                    pg.onJoin(p);
                }
            } else if (args[0].equalsIgnoreCase("arena")) {
                if (pg.pgPlayers.contains(p) && p.isOp() || pg.specPlayers.contains(p) && p.isOp()) {
                    String arena = args[1];
                    String arenaNumber = "1";
                    try {
                        pg.setForcearena(true);
                        int i = 1;
                        boolean votetedarena = false;
                        while (!votetedarena) {
                            if (arena.matches(Objects.requireNonNull(pg.getConfig().getString("pg.arenas." + i + ".name")))) {
                                arenaNumber = String.valueOf(i);
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
            } else if (args[0].equalsIgnoreCase("setlobby")) {
                if (p.isOp()) {
                    int arenaNumber = 1;
                    try {
                        int i = 1;
                        boolean arenaName = false;
                        while (!arenaName) {
                            if (args[1].matches(Objects.requireNonNull(pg.getConfig().getString("pg.arenas." + i + ".name")))) {
                                arenaNumber = i;
                                arenaName = true;
                            } else {
                                i++;
                            }
                        }
                        pg.getConfig().set("pg.arenas." + arenaNumber + ".Lobby", p.getLocation());
                        pg.saveConfig();
                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(24));
                    } catch (Exception e) {
                        p.sendMessage(pg.prefix + ChatColor.AQUA + args[1] + ChatColor.RED + " " + pg.chat.get(27));
                    }
                }
            } else if (args[0].equalsIgnoreCase("delarena")) {
                if (p.isOp()) {
                    int arenaNumber = 1;
                    try {
                        int i = 1;
                        boolean arenaID = false;
                        while (!arenaID) {
                            if (args[1].matches(Objects.requireNonNull(pg.getConfig().getString("pg.arenas." + i + ".name")))) {
                                arenaNumber = i;
                                arenaID = true;
                            } else {
                                i++;
                            }
                        }
                        String arenaName = args[1];
                        pg.getConfig().set("pg.arenas." + arenaNumber, null);
                        pg.saveConfig();
                        p.sendMessage(pg.prefix + ChatColor.AQUA + arenaName + ChatColor.GREEN + " " + pg.chat.get(28));

                    } catch (Exception e) {
                        p.sendMessage(pg.prefix + ChatColor.AQUA + args[1] + ChatColor.RED + " " + pg.chat.get(27));
                    }
                }
            } else if (args[0].equalsIgnoreCase("addarena")) {
                if (p.isOp()) {
                    int arenaNumber = 1;
                    try {
                        while (pg.getConfig().contains("pg.arenas." + arenaNumber)) {
                            arenaNumber++;
                        }
                        String arenaName = args[1];
                        pg.getConfig().set("pg.arenas." + arenaNumber, p.getWorld());
                        pg.saveConfig();
                        pg.getConfig().set("pg.arenas." + arenaNumber + ".world", p.getWorld().getName());
                        pg.saveConfig();
                        pg.getConfig().set("pg.arenas." + arenaNumber + ".name", arenaName);
                        pg.saveConfig();
                        p.sendMessage(pg.prefix + ChatColor.AQUA + arenaName + ChatColor.GREEN + " " + pg.chat.get(29));

                    } catch (Exception e) {
                        p.sendMessage(pg.prefix + ChatColor.AQUA + args[1] + ChatColor.RED + " " + pg.chat.get(27));
                    }
                }
            } else if (args[0].equalsIgnoreCase("addspawn")) {
                if (p.isOp()) {
                    int spawnNumber = 1;
                    int arenaNumber = 1;
                    try {
                        int i = 1;
                        boolean arenaName = false;
                        while (!arenaName) {
                            if (args[1].matches(Objects.requireNonNull(pg.getConfig().getString("pg.arenas." + i + ".name")))) {
                                arenaNumber = i;
                                arenaName = true;
                            } else {
                                i++;
                            }
                        }
                        while (pg.getConfig().contains("pg.arenas." + arenaNumber + ".spawns." + spawnNumber)) {
                            spawnNumber++;
                        }
                        pg.getConfig().set("pg.arenas." + arenaNumber + ".spawns." + spawnNumber, p.getLocation());
                        pg.saveConfig();
                        p.sendMessage(pg.prefix + ChatColor.AQUA + spawnNumber + ChatColor.GREEN + " " + pg.chat.get(30));
                    } catch (Exception e) {
                        p.sendMessage(pg.prefix + ChatColor.AQUA + args[1] + ChatColor.RED + " " + pg.chat.get(31));
                    }
                }
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("delspawn")) {
                if (p.isOp()) {
                    int arenaNumber = 1;
                    try {
                        int i = 1;
                        boolean arenaName = false;
                        while (!arenaName) {
                            if (args[1].matches(Objects.requireNonNull(pg.getConfig().getString("pg.arenas." + i + ".name")))) {
                                arenaNumber = i;
                                arenaName = true;
                            } else {
                                i++;
                            }
                        }
                        int spawnNumber = Integer.parseInt(args[2]);
                        pg.getConfig().set("pg.arenas." + arenaNumber + ".spawns." + spawnNumber, null);
                        pg.saveConfig();
                        p.sendMessage(pg.prefix + ChatColor.AQUA + spawnNumber + ChatColor.GREEN + " " + pg.chat.get(32));
                    } catch (Exception e) {
                        p.sendMessage(pg.prefix + ChatColor.AQUA + args[1] + ChatColor.RED + " " + pg.chat.get(31));
                    }
                }
            } else if (args[0].equalsIgnoreCase("setcountdown")) {
                if (p.isOp()) {
                    int arenaNumber = 1;
                    try {
                        int i = 1;
                        boolean arenaName = false;
                        while (!arenaName) {
                            if (args[1].matches(Objects.requireNonNull(pg.getConfig().getString("pg.arenas." + i + ".name")))) {
                                arenaNumber = i;
                                arenaName = true;
                            } else {
                                i++;
                            }
                        }
                        pg.getConfig().set("pg.arenas." + arenaNumber + ".countdown", Integer.valueOf(args[2]));
                        pg.saveConfig();
                        p.sendMessage(pg.prefix + ChatColor.GRAY + "Countdown für Arena" + " " + ChatColor.AQUA + args[1] + ChatColor.GRAY + ": " + ChatColor.LIGHT_PURPLE + args[2]);
                    } catch (Exception e) {
                        p.sendMessage(pg.prefix + ChatColor.AQUA + args[1] + ChatColor.RED + " " + pg.chat.get(31));
                    }
                }
            }
        }
        return false;
    }

}
