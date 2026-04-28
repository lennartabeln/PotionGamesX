package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.Settings;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * /pg force [arena] - Force a specific arena
 */
public class ForceCommand implements ICommand {
    private final PotionGames plugin;
    
    public ForceCommand(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "force";
    }
    
    @Override
    public String getPermission() {
        return "pg.force";
    }
    
    @Override
    public boolean requiresGameServer() {
        return true;
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length < 1) {
            return false;
        }
        
        String arena = args[0];
        
        if (plugin.isLobbySystem()) {
            if (plugin.playerLobby.containsKey(player) || plugin.specLobby.containsKey(player)) {
                String lobbyId = null;
                for (int ii = 1; ii <= 27; ii++) {
                    if (plugin.playerLobby.containsKey(player) && plugin.playerLobby.get(player).contains(Integer.toString(ii))) {
                        lobbyId = Integer.toString(ii);
                    }
                }
                if (lobbyId != null) {
                    try {
                        plugin.lobbyForcearena.replace(lobbyId, true);
                        int i = 1;
                        boolean votetedarena = false;
                        while (!votetedarena) {
                            if (arena.matches(Objects.requireNonNull(Settings.arenadata.getString("pg.arenas." + i + ".name")))) {
                                String arenaNumber = Integer.toString(i);
                                plugin.lobbyVotedarena.replace(lobbyId, arena);
                                plugin.lobbyVote.replace(lobbyId, arenaNumber);
                                votetedarena = true;
                            } else {
                                i++;
                            }
                        }
                        for (Player all : plugin.playerLobby.keySet()) {
                            if (plugin.playerLobby.get(all).equals(lobbyId)) {
                                all.sendMessage(Messages.ArenaForced(arena));
                            }
                        }
                    } catch (Exception ex) {
                        player.sendMessage(Messages.ArenaNotArena(arena));
                    }
                }
            }
        } else {
            if (plugin.pgPlayers.contains(player) || plugin.specPlayers.contains(player)) {
                try {
                    plugin.setForcearena(true);
                    int i = 1;
                    boolean votetedarena = false;
                    while (!votetedarena) {
                        if (arena.matches(Objects.requireNonNull(Settings.arenadata.getString("pg.arenas." + i + ".name")))) {
                            String arenaNumber = Integer.toString(i);
                            plugin.setVotedArena(arena);
                            plugin.setVote(arenaNumber);
                            votetedarena = true;
                        } else {
                            i++;
                        }
                    }
                    for (Player all : plugin.pgPlayers) {
                        all.sendMessage(Messages.ArenaForced(arena));
                    }
                } catch (Exception ex) {
                    player.sendMessage(Messages.ArenaNotArena(arena));
                }
            }
        }
        return true;
    }
    
    @Override
    public String getUsage() {
        return "/pg force <arena_name> - Force a specific arena (requires pg.force)";
    }
}
