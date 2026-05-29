package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.GameStates;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.Settings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;

import java.util.Map;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import java.util.Objects;

/**
 * Handles combat-related events (multi-lobby only).
 * Extracted from monolithic Events.java.
 */
public class CombatEventListener implements Listener {
    private final PotionGames plugin;
    
    public CombatEventListener(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (plugin.isGameServer()) {
            if (plugin.getWorlds().contains(e.getDamager().getWorld().getName())) {
                e.setCancelled(e.getDamager() instanceof LightningStrike || e.getDamager() instanceof Firework);
                if (e.getEntity() instanceof Player p && e.getDamager() instanceof TNTPrimed) {
                    if (p.getHealth() - 4 <= 0) {
                        p.setHealth(p.getHealth() - 4);
                    } else {
                        p.setHealth(p.getHealth() - 4 <= 0 ? 0D : p.getHealth() - 4);
                    }
                }
                if (!plugin.isFriendlyFire()) {
                    if (e.getEntity() instanceof Player p && e.getDamager() instanceof Player d) {
                        // Get lobby ID for both players
                        String pLobby = plugin.getGame().getPlayerLobby(p);
                        String dLobby = plugin.getGame().getPlayerLobby(d);
                        
                        // Both must be in same lobby for friendly fire check
                        if (pLobby != null && pLobby.equals(dLobby)) {
                            String pTeam = plugin.getPlayerTeam(pLobby, p);
                            String dTeam = plugin.getPlayerTeam(dLobby, d);
                            if (Objects.equals(pTeam, dTeam)) {
                                e.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (plugin.isGameServer()) {
            String s = plugin.getGame().getPlayerLobby(p);
            if (s != null) {
                if (!plugin.isLobbyBuildAllowed(s)) {
                    if (plugin.getLobbyGameState(s) == GameStates.INGAME) {
                        if (p.getKiller() != null) {
                            plugin.getDatabaseManager().addDeaths(p.getUniqueId().toString(), 1);
                            plugin.getDatabaseManager().addLosses(p.getUniqueId().toString(), 1);
                            plugin.getDatabaseManager().addKills(p.getKiller().getUniqueId().toString(), 1);
                            if (plugin.isEnableRewards()) {
                                EconomyResponse r = PotionGames.getEconomy().depositPlayer(p.getKiller(), plugin.getKillReward());
                                if (r.transactionSuccess()) {
                                    Component comp = Messages.KillReward(plugin.getKillReward())
                                        .append(Component.text(" " + PotionGames.getEconomy().format(r.amount)).color(NamedTextColor.LIGHT_PURPLE));
                                    p.getKiller().sendMessage(comp);
                                } else {
                                    Component comp = Messages.ErrorGeneric()
                                        .append(Component.text(": " + r.errorMessage).color(NamedTextColor.RED));
                                    p.getKiller().sendMessage(comp);
                                }
                            }
                            if (plugin.isActivateScoreboard()) {
                                Team killsTeam = Objects.requireNonNull(p.getKiller().getScoreboard().getTeam("kills"));
                                Component tempComponent = killsTeam.prefix();
                                int tempInt = Integer.parseInt(tempComponent.toString());
                                tempInt++;
                                killsTeam.prefix(Component.text(String.valueOf(tempInt)).color(NamedTextColor.DARK_AQUA));
                            }
                        } else {
                            plugin.getDatabaseManager().addDeaths(p.getUniqueId().toString(), 1);
                            plugin.getDatabaseManager().addLosses(p.getUniqueId().toString(), 1);
                        }
                        // Move player from active to spectator
                        plugin.getGame().removePlayerLobby(p);
                        plugin.setSpectatorLobby(p, s);
                        if (plugin.isLobbyActivateTeams(s)) {
                            String teamname = plugin.getPlayerTeam(s, p);
                            plugin.removePlayerTeam(s, p);
                            if (teamname != null) {
                                try {
                                    int teamId = Integer.parseInt(teamname);
                                    plugin.decrementTeamCount(s, teamId);
                                } catch (NumberFormatException ex) {
                                    // Invalid team ID
                                }
                            }
                            plugin.getTeamed().remove(p.getName());
                        }
                        int amountPlayers = plugin.getLobbyAmount(s);
                        int player = 0;
                        for (Player all : plugin.getGame().getPlayersInLobby(s)) {
                            player++;
                        }
                        try {
                            Player killer = p.getKiller();
                            assert killer != null;
                            killer.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 30 * 20, 0));
                            killer.playSound(killer.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, 1, 1);
                            p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 1);
                            if (Objects.equals(plugin.getPlayerKit(p), "Rich Kid")) {
                                for (int i = 0; i < 10; i++) {
                                    killer.getInventory().addItem(plugin.getCoin());
                                }
                            } else {
                                for (int i = 0; i < 5; i++) {
                                    killer.getInventory().addItem(plugin.getCoin());
                                }
                            }
                            for (Player all : plugin.getGame().getPlayersInLobby(s)) {
                                all.sendMessage(Settings.prefix
                                    .append(Component.text(p.getName()).color(NamedTextColor.DARK_RED))
                                    .append(Component.text(" " + Messages.raw("killed.by", "was killed by") + " ").color(NamedTextColor.GRAY))
                                    .append(Component.text(killer.getName()).color(NamedTextColor.DARK_GREEN))
                                    .append(Component.text(" [").color(NamedTextColor.GRAY))
                                    .append(Component.text(String.valueOf(player)).color(NamedTextColor.AQUA))
                                    .append(Component.text("/").color(NamedTextColor.GRAY))
                                    .append(Component.text(String.valueOf(amountPlayers)).color(NamedTextColor.AQUA))
                                    .append(Component.text("]").color(NamedTextColor.GRAY)));
                            }
                            for (Player all : plugin.getGame().getSpectatorsInLobby(s)) {
                                all.sendMessage(Settings.prefix
                                    .append(Component.text(p.getName()).color(NamedTextColor.DARK_RED))
                                    .append(Component.text(" " + Messages.raw("killed.by", "was killed by") + " ").color(NamedTextColor.GRAY))
                                    .append(Component.text(killer.getName()).color(NamedTextColor.DARK_GREEN))
                                    .append(Component.text(" [").color(NamedTextColor.GRAY))
                                    .append(Component.text(String.valueOf(player)).color(NamedTextColor.AQUA))
                                    .append(Component.text("/").color(NamedTextColor.GRAY))
                                    .append(Component.text(String.valueOf(amountPlayers)).color(NamedTextColor.AQUA))
                                    .append(Component.text("]").color(NamedTextColor.GRAY)));
                            }
                            e.deathMessage(null);
                        } catch (Exception ex) {
                            for (Player all : plugin.getGame().getPlayersInLobby(s)) {
                                all.sendMessage(Settings.prefix
                                    .append(Component.text(p.getName()).color(NamedTextColor.DARK_RED))
                                    .append(Component.text(" " + Messages.raw("died", "died") + " ").color(NamedTextColor.GRAY))
                                    .append(Component.text(" [").color(NamedTextColor.GRAY))
                                    .append(Component.text(String.valueOf(player)).color(NamedTextColor.AQUA))
                                    .append(Component.text("/").color(NamedTextColor.GRAY))
                                    .append(Component.text(String.valueOf(amountPlayers)).color(NamedTextColor.AQUA))
                                    .append(Component.text("]").color(NamedTextColor.GRAY)));
                            }
                            for (Player all : plugin.getGame().getSpectatorsInLobby(s)) {
                                all.sendMessage(Settings.prefix
                                    .append(Component.text(p.getName()).color(NamedTextColor.DARK_RED))
                                    .append(Component.text(" " + Messages.raw("died", "died") + " ").color(NamedTextColor.GRAY))
                                    .append(Component.text(" [").color(NamedTextColor.GRAY))
                                    .append(Component.text(String.valueOf(player)).color(NamedTextColor.AQUA))
                                    .append(Component.text("/").color(NamedTextColor.GRAY))
                                    .append(Component.text(String.valueOf(amountPlayers)).color(NamedTextColor.AQUA))
                                    .append(Component.text("]").color(NamedTextColor.GRAY)));
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
