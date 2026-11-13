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
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

/**
 * Handles player death events.
 * Manages death tracking, kill rewards, and game state after death.
 */
public class DeathEventListener implements Listener {
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

        int id;
        try {
            id = Integer.parseInt(lobbyId);
        } catch (NumberFormatException ex) {
            plugin.getLogger().warning("[PotionGamesX] Invalid lobby ID in death event: " + lobbyId);
            return;
        }

        Lobby lobby = plugin.getGame().getLobby(id);
        if (lobby == null) {
            return;
        }

        GameStates state = plugin.getLobbyStateManager().getGameState(lobbyId);
        if (state != GameStates.INGAME && state != GameStates.DEATHMATCH) {
            return;
        }

        Player killer = p.getKiller();

        // Update database stats
        plugin.getDatabaseManager().addDeaths(p.getUniqueId().toString(), 1);
        plugin.getDatabaseManager().addLosses(p.getUniqueId().toString(), 1);

        if (killer != null) {
            plugin.getDatabaseManager().addKills(killer.getUniqueId().toString(), 1);

            // Economy reward for killer
            if (plugin.getConfigManager().isEnableRewards() && PotionGamesX.getEconomy() != null) {
                EconomyResponse r = PotionGamesX.getEconomy().depositPlayer(killer, plugin.getConfigManager().getKillReward());
                if (r.transactionSuccess()) {
                    killer.sendMessage(Messages.KillReward(plugin.getConfigManager().getKillReward())
                        .append(Component.text(" " + PotionGamesX.getEconomy().format(r.amount)).color(NamedTextColor.LIGHT_PURPLE)));
                }
            }

            // Give coins to killer
            String victimKitName = lobby.getParticipant(p) != null && lobby.getParticipant(p).getKit() != null
                ? lobby.getParticipant(p).getKit().getName() : null;
            int coinAmount = Objects.equals(victimKitName, "Rich Kid") ? 10 : 5;
            for (int i = 0; i < coinAmount; i++) {
                killer.getInventory().addItem(plugin.getCoin());
            }

            killer.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 30 * 20, 0, true, true, true));
            killer.playSound(killer.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, 1, 1);
        }

        // Free the team slot and drop the vote of the eliminated player
        if (lobby.isActivateTeams()) {
            lobby.clearPlayerVoteAndTeam(p);
        }

        // Move player from active to spectator, but stay at the death location:
        // the pre-join state is restored only once spectating ends.
        int roundPlayers = lobby.getPlayerCount();
        plugin.getGame().setSpectatorLobby(p, lobbyId);

        // Set spectator mode
        p.setGameMode(GameMode.SPECTATOR);
        p.setLevel(0);
        p.setExp(0);
        p.setFireTicks(0);
        p.setCanPickupItems(false);
        p.setCollidable(false);
        p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1f, 1f);

        // Death message: one broadcast to players and spectators of this lobby
        int aliveCount = lobby.getActivePlayers().size();
        String verb = killer != null ? Messages.KilledByText() : Messages.DiedText();
        for (Player all : plugin.getGame().getPlayersInLobby(lobbyId)) {
            all.sendMessage(buildDeathMessage(p, killer, verb, aliveCount, roundPlayers));
        }
        for (Player all : plugin.getGame().getSpectatorsInLobby(lobbyId)) {
            all.sendMessage(buildDeathMessage(p, killer, verb, aliveCount, roundPlayers));
        }
        e.deathMessage(null);

        // Track per-round kill/death counters; the sidebar renders them
        if (killer != null) {
            lobby.addKill(killer);
        }
        lobby.addDeath(p);
    }

    private Component buildDeathMessage(Player victim, Player killer, String verb, int aliveCount, int playerCount) {
        Component msg = Settings.prefix
            .append(Component.text(victim.getName()).color(NamedTextColor.DARK_RED))
            .append(Component.text(" " + verb + " ").color(NamedTextColor.GRAY));
        if (killer != null) {
            msg = msg.append(Component.text(killer.getName()).color(NamedTextColor.DARK_GREEN));
        }
        return msg.append(Component.text(" [").color(NamedTextColor.GRAY))
            .append(Component.text(String.valueOf(aliveCount)).color(NamedTextColor.AQUA))
            .append(Component.text("/").color(NamedTextColor.GRAY))
            .append(Component.text(String.valueOf(playerCount)).color(NamedTextColor.AQUA))
            .append(Component.text("]").color(NamedTextColor.GRAY));
    }
}
