package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.GameStates;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.Settings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles player death events.
 * Manages death tracking, kill rewards, and game state after death.
 */
public class DeathEventListener implements Listener {
    private static final Logger LOGGER = Logger.getLogger("Minecraft");
    private final PotionGamesX plugin;
    
    public DeathEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        
        String lobbyId = plugin.getGame().getPlayerLobby(p);
        if (lobbyId == null) {
            return;
        }
        
        try {
            Lobby lobby = plugin.getGame().getLobby(Integer.parseInt(lobbyId));
            if (lobby == null) {
                return;
            }
            
            if (plugin.getLobbyStateManager().getGameState(lobbyId) != GameStates.INGAME && plugin.getLobbyStateManager().getGameState(lobbyId) != GameStates.DEATHMATCH) {
                return;
            }
            
            Player killer = p.getKiller();
            
            // Update database stats
            plugin.getDatabaseManager().addDeaths(p.getUniqueId().toString(), 1);
            plugin.getDatabaseManager().addLosses(p.getUniqueId().toString(), 1);
            
            if (killer != null) {
                plugin.getDatabaseManager().addKills(killer.getUniqueId().toString(), 1);
                
                // Economy reward for killer
                if (plugin.getConfigManager().isEnableRewards()) {
                    if (PotionGamesX.getEconomy() != null) {
                        EconomyResponse r = PotionGamesX.getEconomy().depositPlayer(killer, plugin.getConfigManager().getKillReward());
                        if (r.transactionSuccess()) {
                            killer.sendMessage(Messages.KillReward(plugin.getConfigManager().getKillReward())
                                .append(Component.text(" " + PotionGamesX.getEconomy().format(r.amount)).color(NamedTextColor.LIGHT_PURPLE)));
                        }
                    }
                }
                
                // Give coins to killer
                String kitName = lobby != null && lobby.getParticipant(p) != null && lobby.getParticipant(p).getKit() != null
                    ? lobby.getParticipant(p).getKit().getName() : null;
                if (Objects.equals(kitName, "Rich Kid")) {
                    for (int i = 0; i < 10; i++) {
                        killer.getInventory().addItem(plugin.getCoin());
                    }
                } else {
                    for (int i = 0; i < 5; i++) {
                        killer.getInventory().addItem(plugin.getCoin());
                    }
                }
                
                killer.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 30 * 20, 0, true, true, true));
                Location loc = killer.getLocation();
                if (loc != null) {
                    Sound sound = Sound.ENTITY_ENDER_DRAGON_HURT;
                    if (sound != null) {
                        killer.playSound(loc, sound, 1, 1);
                    }
                }
            }
            
            // Update team counts if teams enabled
            boolean activateTeams;
            try {
                Lobby _dl = plugin.getGame().getLobby(Integer.parseInt(lobbyId));
                activateTeams = _dl != null ? _dl.isActivateTeams() : plugin.getConfigManager().isActivateTeams();
            } catch (NumberFormatException _e) {
                activateTeams = plugin.getConfigManager().isActivateTeams();
            }
            if (activateTeams) {
                String teamname;
                try {
                    Lobby _dl = plugin.getGame().getLobby(Integer.parseInt(lobbyId));
                    teamname = _dl != null ? _dl.getPlayerTeam(p) : null;
                } catch (NumberFormatException _e) {
                    teamname = null;
                }
                if (teamname != null) {
                    try {
                        Lobby _dl = plugin.getGame().getLobby(Integer.parseInt(lobbyId));
                        if (_dl != null) _dl.removePlayerTeam(p);
                    } catch (NumberFormatException _e) {
                    }
                    try {
                        Lobby _dl = plugin.getGame().getLobby(Integer.parseInt(lobbyId));
                        if (_dl != null) _dl.decrementTeamCount(Integer.parseInt(teamname));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            
            // Move player from active to spectator
            plugin.getGame().removePlayerLobby(p);
            plugin.getGame().setSpectatorLobby(p, lobbyId);
            
            // Set spectator mode
            p.setGameMode(GameMode.SPECTATOR);
            p.setAllowFlight(true);
            p.setFlying(true);
            p.setLevel(0);
            p.setExp(0);
            p.setFireTicks(0);
            p.setHealth(20);
            p.setFoodLevel(20);
            p.setCanPickupItems(false);
            p.setCollidable(false);
            
            // Death message
            int aliveCount = lobby.getActivePlayers().size();
            if (killer != null) {
                for (Player all : plugin.getGame().getPlayersInLobby(lobbyId)) {
                    all.sendMessage(Settings.prefix
                        .append(Component.text(p.getName()).color(NamedTextColor.DARK_RED))
                        .append(Component.text(" " + Messages.raw("killed.by", "was killed by") + " ").color(NamedTextColor.GRAY))
                        .append(Component.text(killer.getName()).color(NamedTextColor.DARK_GREEN))
                        .append(Component.text(" [").color(NamedTextColor.GRAY))
                        .append(Component.text(String.valueOf(aliveCount)).color(NamedTextColor.AQUA))
                        .append(Component.text("/").color(NamedTextColor.GRAY))
                        .append(Component.text(String.valueOf(lobby.getPlayerCount())).color(NamedTextColor.AQUA))
                        .append(Component.text("]").color(NamedTextColor.GRAY)));
                }
                for (Player all : plugin.getGame().getSpectatorsInLobby(lobbyId)) {
                    all.sendMessage(Settings.prefix
                        .append(Component.text(p.getName()).color(NamedTextColor.DARK_RED))
                        .append(Component.text(" " + Messages.raw("killed.by", "was killed by") + " ").color(NamedTextColor.GRAY))
                        .append(Component.text(killer.getName()).color(NamedTextColor.DARK_GREEN))
                        .append(Component.text(" [").color(NamedTextColor.GRAY))
                        .append(Component.text(String.valueOf(aliveCount)).color(NamedTextColor.AQUA))
                        .append(Component.text("/").color(NamedTextColor.GRAY))
                        .append(Component.text(String.valueOf(lobby.getPlayerCount())).color(NamedTextColor.AQUA))
                        .append(Component.text("]").color(NamedTextColor.GRAY)));
                }
            } else {
                for (Player all : plugin.getGame().getPlayersInLobby(lobbyId)) {
                    all.sendMessage(Settings.prefix
                        .append(Component.text(p.getName()).color(NamedTextColor.DARK_RED))
                        .append(Component.text(" " + Messages.raw("died", "died") + " ").color(NamedTextColor.GRAY))
                        .append(Component.text(" [").color(NamedTextColor.GRAY))
                        .append(Component.text(String.valueOf(aliveCount)).color(NamedTextColor.AQUA))
                        .append(Component.text("/").color(NamedTextColor.GRAY))
                        .append(Component.text(String.valueOf(lobby.getPlayerCount())).color(NamedTextColor.AQUA))
                        .append(Component.text("]").color(NamedTextColor.GRAY)));
                }
                for (Player all : plugin.getGame().getSpectatorsInLobby(lobbyId)) {
                    all.sendMessage(Settings.prefix
                        .append(Component.text(p.getName()).color(NamedTextColor.DARK_RED))
                        .append(Component.text(" " + Messages.raw("died", "died") + " ").color(NamedTextColor.GRAY))
                        .append(Component.text(" [").color(NamedTextColor.GRAY))
                        .append(Component.text(String.valueOf(aliveCount)).color(NamedTextColor.AQUA))
                        .append(Component.text("/").color(NamedTextColor.GRAY))
                        .append(Component.text(String.valueOf(lobby.getPlayerCount())).color(NamedTextColor.AQUA))
                        .append(Component.text("]").color(NamedTextColor.GRAY)));
                }
            }
            
            e.deathMessage(null);
            
            // Update scoreboard kill counter for killer
            if (killer != null && plugin.getConfigManager().isActivateScoreboard()) {
                Team killsTeam = killer.getScoreboard().getTeam("kills");
                if (killsTeam != null) {
                    String currentValue = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(killsTeam.prefix());
                    try {
                        int tempInt = Integer.parseInt(currentValue) + 1;
                        killsTeam.prefix(Component.text(String.valueOf(tempInt)).color(NamedTextColor.DARK_AQUA));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
                } catch (NumberFormatException ex) {
                    LOGGER.log(Level.WARNING, "[PotionGamesX] Invalid team ID in death event", ex);
                }
    }
}
