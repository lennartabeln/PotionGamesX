package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.GameStates;
import com.tw0far.potiongames.models.Settings;
import com.tw0far.potiongames.util.SafeMapAccess;
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
            if (plugin.worlds.contains(e.getDamager().getWorld().getName())) {
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
                        String s = null;
                        for (int ii = 1; ii <= 27; ii++) {
                            if (plugin.playerLobby.get(p).contains(Integer.toString(ii))) {
                                s = Integer.toString(ii);
                            }
                        }
                        if (Objects.equals(SafeMapAccess.get(plugin.lobbyteamplayernames, s, p, null), SafeMapAccess.get(plugin.lobbyteamplayernames, s, d, null))) {
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
        if (plugin.isGameServer()) {
            if (plugin.pgPlayers.contains(p) || plugin.playerLobby.containsKey(p)) {
                String s = null;
                for (int ii = 1; ii <= 27; ii++) {
                    if (plugin.playerLobby.get(p).contains(Integer.toString(ii))) {
                        s = Integer.toString(ii);
                    }
                }
                if (!plugin.lobbyBuild.get(s)) {
                    if (plugin.lobbyStates.get(s) == GameStates.INGAME) {
                        if (p.getKiller() != null) {
                            plugin.addDeaths(p.getUniqueId().toString(), 1);
                            plugin.addLosses(p.getUniqueId().toString(), 1);
                            plugin.addKills(p.getKiller().getUniqueId().toString(), 1);
                            if (plugin.isEnableRewards()) {
                                EconomyResponse r = PotionGames.getEconomy().depositPlayer(p.getKiller(), plugin.getKillReward());
                                if (r.transactionSuccess()) {
                                    Component comp = Settings.prefix
                                        .append(Component.text(plugin.chatmessages.get(91)).color(NamedTextColor.AQUA))
                                        .append(Component.text(": ")).append(Component.text(plugin.chatmessages.get(94)).color(NamedTextColor.GREEN))
                                        .append(Component.text(" ")).append(Component.text(PotionGames.getEconomy().format(r.amount)).color(NamedTextColor.LIGHT_PURPLE));
                                    p.getKiller().sendMessage(comp);
                                } else {
                                    Component comp = Settings.prefix
                                        .append(Component.text(plugin.chatmessages.get(92)).color(NamedTextColor.RED))
                                        .append(Component.text(": ")).append(Component.text(r.errorMessage));
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
                            plugin.addDeaths(p.getUniqueId().toString(), 1);
                            plugin.addLosses(p.getUniqueId().toString(), 1);
                        }
                        if (plugin.playerLobby.containsKey(p)) {
                            e.setKeepLevel(true);
                            plugin.playerLobby.remove(p);
                            plugin.specLobby.put(p, s);
                            if (plugin.lobbyActivateTeams.get(s)) {
                                String teamname = null;
                                for (int i = 1; i <= plugin.lobbyteamAmount.get(s); i++) {
                                    if (SafeMapAccess.contains(plugin.lobbyteamplayernames, s, p)) {
                                        @SuppressWarnings("unchecked")
                                        Map<Player, String> lobbyTeams = (Map<Player, String>) plugin.lobbyteamplayernames.get(s);
                                        if (lobbyTeams != null && lobbyTeams.containsValue(Integer.toString(i))) {
                                            if (Objects.equals(SafeMapAccess.get(plugin.lobbyteamplayernames, s, p, null), Integer.toString(i))) {
                                                teamname = Integer.toString(i);
                                            }
                                        }
                                    }
                                }
                                SafeMapAccess.remove(plugin.lobbyteamplayernames, s, p);
                                assert teamname != null;
                                int teamamount = SafeMapAccess.get(plugin.lobbyteams, s, Integer.parseInt(teamname), 0);
                                teamamount--;
                                SafeMapAccess.put(plugin.lobbyteams, s, Integer.parseInt(teamname), teamamount);
                                if (SafeMapAccess.get(plugin.lobbyteams, s, Integer.parseInt(teamname), 0) == 0) {
                                    SafeMapAccess.remove(plugin.lobbyteams, s, Integer.parseInt(teamname));
                                }
                                plugin.teamed.remove(p.getName());
                            }
                            int amountPlayers = plugin.lobbyAmount.get(s);
                            int player = 0;
                            for (Player all : plugin.playerLobby.keySet()) {
                                if (plugin.playerLobby.get(all).equals(s)) {
                                    player++;
                                }
                            }
                            try {
                                Player killer = p.getKiller();
                                assert killer != null;
                                killer.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 30 * 20, 0));
                                killer.playSound(killer.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, 1, 1);
                                p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 1);
                                if (Objects.equals(plugin.kitplayernames.get(p), "Rich Kid")) {
                                    for (int i = 0; i < 10; i++) {
                                        killer.getInventory().addItem(plugin.getCoin());
                                    }
                                } else {
                                    for (int i = 0; i < 5; i++) {
                                        killer.getInventory().addItem(plugin.getCoin());
                                    }
                                }
                                for (Player all : plugin.playerLobby.keySet()) {
                                    if (plugin.playerLobby.get(all).equals(s)) {
                                        all.sendMessage(Settings.prefix
                                            .append(Component.text(p.getName()).color(NamedTextColor.DARK_RED))
                                            .append(Component.text(" " + plugin.chatmessages.get(9) + " ").color(NamedTextColor.GRAY))
                                            .append(Component.text(killer.getName()).color(NamedTextColor.DARK_GREEN))
                                            .append(Component.text(" [").color(NamedTextColor.GRAY))
                                            .append(Component.text(String.valueOf(player)).color(NamedTextColor.AQUA))
                                            .append(Component.text("/").color(NamedTextColor.GRAY))
                                            .append(Component.text(String.valueOf(amountPlayers)).color(NamedTextColor.AQUA))
                                            .append(Component.text("]").color(NamedTextColor.GRAY)));
                                    }
                                }
                                for (Player all : plugin.specLobby.keySet()) {
                                    if (plugin.specLobby.get(all).equals(s)) {
                                        all.sendMessage(Settings.prefix
                                            .append(Component.text(p.getName()).color(NamedTextColor.DARK_RED))
                                            .append(Component.text(" " + plugin.chatmessages.get(9) + " ").color(NamedTextColor.GRAY))
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
                                for (Player all : plugin.playerLobby.keySet()) {
                                    if (plugin.playerLobby.get(all).equals(s)) {
                                        all.sendMessage(Settings.prefix
                                            .append(Component.text(p.getName()).color(NamedTextColor.DARK_RED))
                                            .append(Component.text(" " + plugin.chatmessages.get(10) + " ").color(NamedTextColor.GRAY))
                                            .append(Component.text(" [").color(NamedTextColor.GRAY))
                                            .append(Component.text(String.valueOf(player)).color(NamedTextColor.AQUA))
                                            .append(Component.text("/").color(NamedTextColor.GRAY))
                                            .append(Component.text(String.valueOf(amountPlayers)).color(NamedTextColor.AQUA))
                                            .append(Component.text("]").color(NamedTextColor.GRAY)));
                                    }
                                }
                                for (Player all : plugin.specLobby.keySet()) {
                                    if (plugin.specLobby.get(all).equals(s)) {
                                        all.sendMessage(Settings.prefix
                                            .append(Component.text(p.getName()).color(NamedTextColor.DARK_RED))
                                            .append(Component.text(" " + plugin.chatmessages.get(10) + " ").color(NamedTextColor.GRAY))
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
            }
        }
    }
}
