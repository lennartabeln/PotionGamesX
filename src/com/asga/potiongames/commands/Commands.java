package com.asga.potiongames.commands;

import com.asga.potiongames.main.PotionGames;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
        FileConfiguration arenadata = YamlConfiguration.loadConfiguration(pg.arenadatafile);
        Player p = (Player) sender;
        if (args.length == 0) {
            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(64) + "--------------");
            if (p.hasPermission("pg.setup")) {
                p.sendMessage(pg.prefix + "/pg setlobby - Set waiting lobby - Permission: pg.setup");
                p.sendMessage(pg.prefix + "/pg addarena [arenaname] - Add arena - Permission: pg.setup");
                p.sendMessage(pg.prefix + "/pg addspawn [arenaname] - Add spawn - Permission: pg.setup");
                p.sendMessage(pg.prefix + "/pg delarena [arenaname] - Remove arena - Permission: pg.setup");
                p.sendMessage(pg.prefix + "/pg delspawn [arenaname] - Remove last added spawn - Permission: pg.setup");
                p.sendMessage(pg.prefix + "/pg headp1(2;3) - Add Player Head to Stats-Wall - Permission: pg.setup");
                p.sendMessage(pg.prefix + "/pg signp1(2;3) - Add Player Sign to Stats-Wall - Permission: pg.setup");
            }
            if (p.hasPermission("pg.build"))
                p.sendMessage(pg.prefix + "/pg build - Activate build mode - Permission: pg.build");
            if (p.hasPermission("pg.pause"))
                p.sendMessage(pg.prefix + "/pg pause - Pause timer/countdown - Permission: pg.pause");
            if (p.hasPermission("pg.force"))
                p.sendMessage(pg.prefix + "/pg force [arenaname] - Force arena - Permission: pg.force");
            if (p.hasPermission("pg.start"))
                p.sendMessage(pg.prefix + "/pg start - Set lobby countdown to 10 - Permission: pg.start");
            if (p.hasPermission("pg.join"))
                p.sendMessage(pg.prefix + "/pg join - Join the game - When startOnJoin = false - Permission: pg.join");
            if (p.hasPermission("pg.leave"))
                p.sendMessage(pg.prefix + "/pg leave - Leave the game - When startOnJoin = false - Permission: pg.leave");
            if (p.hasPermission("pg.stats"))
                p.sendMessage(pg.prefix + "/pg stats - Show your stats - Permission: pg.stats");
            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(64) + "--------------");
        }
        if (args.length == 1) {
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
            if (p.hasPermission("pg.stats")) {
                if (args[0].equalsIgnoreCase("stats")) {
                    int wins = pg.getWins(p.getUniqueId().toString());
                    int losts = pg.getLosts(p.getUniqueId().toString());
                    int kills = pg.getKills(p.getUniqueId().toString());
                    int deaths = pg.getDeaths(p.getUniqueId().toString());
                    double kd = pg.getKD(p.getUniqueId().toString());
                    p.sendMessage(pg.prefix + "--------------" + pg.chat.get(56) + "--------------");
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
                    p.sendMessage(pg.prefix + "/pg setlobby - Set waiting lobby - Permission: pg.setup");
                    p.sendMessage(pg.prefix + "/pg addarena [arenaname] - Add arena - Permission: pg.setup");
                    p.sendMessage(pg.prefix + "/pg addspawn [arenaname] - Add spawn - Permission: pg.setup");
                    p.sendMessage(pg.prefix + "/pg delarena [arenaname] - Remove arena - Permission: pg.setup");
                    p.sendMessage(pg.prefix + "/pg delspawn [arenaname] - Remove last added spawn - Permission: pg.setup");
                    p.sendMessage(pg.prefix + "/pg headp1(2;3) - Add Player Head to Stats-Wall - Permission: pg.setup");
                    p.sendMessage(pg.prefix + "/pg signp1(2;3) - Add Player Sign to Stats-Wall - Permission: pg.setup");
                }
                if (p.hasPermission("pg.build"))
                    p.sendMessage(pg.prefix + "/pg build - Activate build mode - Permission: pg.build");
                if (p.hasPermission("pg.pause"))
                    p.sendMessage(pg.prefix + "/pg pause - Pause timer/countdown - Permission: pg.pause");
                if (p.hasPermission("pg.force"))
                    p.sendMessage(pg.prefix + "/pg force [arenaname] - Force arena - Permission: pg.force");
                if (p.hasPermission("pg.start"))
                    p.sendMessage(pg.prefix + "/pg start - Set lobby countdown to 10 - Permission: pg.start");
                if (p.hasPermission("pg.join"))
                    p.sendMessage(pg.prefix + "/pg join - Join the game - When startOnJoin = false - Permission: pg.join");
                if (p.hasPermission("pg.leave"))
                    p.sendMessage(pg.prefix + "/pg leave - Leave the game - When startOnJoin = false - Permission: pg.leave");
                if (p.hasPermission("pg.stats"))
                    p.sendMessage(pg.prefix + "/pg stats - Show your stats - Permission: pg.stats");
                p.sendMessage(pg.prefix + "--------------" + pg.chat.get(64) + "--------------");
            }
            if (args[0].equalsIgnoreCase("join")) {
                if (p.hasPermission("pg.join")) {
                    if (!pg.pgPlayers.contains(p) && !pg.specPlayers.contains(p)) {
                        pg.onJoin(p);
                    }
                }
            } else if (args[0].equalsIgnoreCase("leave")) {
                if (p.hasPermission("pg.leave")) {
                    if (pg.pgPlayers.contains(p) && !pg.isStartOnJoin() || pg.specPlayers.contains(p) && !pg.isStartOnJoin()) {
                        pg.onLeave(p);
                    }
                }
            } else if (args[0].equalsIgnoreCase("start")) {
                if (p.hasPermission("pg.start")) {
                    if (pg.pgPlayers.contains(p) && p.isOp() || pg.specPlayers.contains(p) && p.isOp()) {
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
            } else if (args[0].equalsIgnoreCase("pause")) {
                if (p.hasPermission("pg.pause")) {
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
                }
            } else if (args[0].equalsIgnoreCase("build")) {
                if (p.hasPermission("pg.build")) {
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
                }
            } else if (args[0].equalsIgnoreCase("setlobby")) {
                if (p.hasPermission("pg.setup")) {
                    pg.getConfig().set("pg.Lobby.world", Objects.requireNonNull(p.getLocation().getWorld()).getName());
                    pg.getConfig().set("pg.Lobby.coords", Objects.requireNonNull(p.getLocation()));
                    pg.saveConfig();
                    p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(24));
                }
            } else if (args[0].equalsIgnoreCase("force")) {
                if (p.hasPermission("pg.force")) {
                    p.sendMessage(pg.prefix + "/pg force [arenaname] - Force arena - Permission: pg.force");
                }
            } else if (args[0].equalsIgnoreCase("delarena")) {
                if (p.hasPermission("pg.setup")) {
                    p.sendMessage(pg.prefix + "/pg delarena [arenaname] - Remove arena - Permission: pg.setup");
                }
            } else if (args[0].equalsIgnoreCase("addarena")) {
                if (p.hasPermission("pg.setup")) {
                    p.sendMessage(pg.prefix + "/pg addarena [arenaname] - Add arena - Permission: pg.setup");
                }
            } else if (args[0].equalsIgnoreCase("addspawn")) {
                if (p.hasPermission("pg.setup")) {
                    p.sendMessage(pg.prefix + "/pg addspawn [arenaname] - Add spawn - Permission: pg.setup");
                }
            } else if (args[0].equalsIgnoreCase("delspawn")) {
                if (p.hasPermission("pg.setup")) {
                    p.sendMessage(pg.prefix + "/pg delspawn [arenaname] - Remove last added spawn - Permission: pg.setup");
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("force")) {
                if (p.hasPermission("pg.force")) {
                    if (pg.pgPlayers.contains(p) && p.isOp() || pg.specPlayers.contains(p) && p.isOp()) {
                        String arena = args[1];
                        String arenaNumber = "1";
                        try {
                            pg.setForcearena(true);
                            int i = 1;
                            boolean votetedarena = false;
                            while (!votetedarena) {
                                if (arena.matches(Objects.requireNonNull(arenadata.getString("pg.arenas." + i + ".name")))) {
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
                }
            } else if (args[0].equalsIgnoreCase("delarena")) {
                if (p.hasPermission("pg.setup")) {
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
            } else if (args[0].equalsIgnoreCase("addarena")) {
                if (p.hasPermission("pg.setup")) {
                    int arenaNumber = 1;
                    try {
                        while (arenadata.contains("pg.arenas." + arenaNumber)) {
                            arenaNumber++;
                        }
                        String arenaName = args[1];
                        arenadata.set("pg.arenas." + arenaNumber, p.getWorld());
                        arenadata.set("pg.arenas." + arenaNumber + ".world", p.getWorld().getName());
                        arenadata.set("pg.arenas." + arenaNumber + ".name", arenaName);
                        p.sendMessage(pg.prefix + ChatColor.AQUA + arenaName + ChatColor.GREEN + " " + pg.chat.get(29));

                    } catch (Exception e) {
                        p.sendMessage(pg.prefix + ChatColor.AQUA + args[1] + ChatColor.RED + " " + pg.chat.get(27));
                    }
                }
            } else if (args[0].equalsIgnoreCase("addspawn")) {
                if (p.hasPermission("pg.setup")) {
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
                        p.sendMessage(pg.prefix + ChatColor.AQUA + args[1] + ChatColor.RED + " " + pg.chat.get(31));
                    }
                }
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("delspawn")) {
                if (p.hasPermission("pg.setup")) {
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
                        int spawnNumber = Integer.parseInt(args[2]);
                        arenadata.set("pg.arenas." + arenaNumber + ".spawns." + spawnNumber, null);
                        arenadata.save(pg.arenadatafile);
                        p.sendMessage(pg.prefix + ChatColor.AQUA + spawnNumber + ChatColor.GREEN + " " + pg.chat.get(32));
                    } catch (Exception e) {
                        p.sendMessage(pg.prefix + ChatColor.AQUA + args[1] + ChatColor.RED + " " + pg.chat.get(31));
                    }
                }
            }
        }
        return false;
    }
}
