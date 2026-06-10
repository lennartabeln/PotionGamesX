package com.tw0far.potiongames.bootstrap;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.managers.IItemStateManager;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.GameStates;
import com.tw0far.potiongames.models.Settings;
import com.tw0far.potiongames.util.PotionSerialization;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.io.IOException;

public final class EnableBootstrapInitializer {
    private final PotionGamesX plugin;

    public EnableBootstrapInitializer(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        syncShop();
        syncKits();
        configureCoin(plugin);
        initializeLobbies();
    }

    private void syncShop() {
        IItemStateManager ism = plugin.getItemStateManager();

        for (int i = 0; i < ism.getShopItemsRaw().size(); i++) {
            String potionPath = "pg.potions." + (i + 1);
            Settings.shopdata.addDefault(potionPath, ism.getShopItemsRaw().get(i));
            Settings.shopdata.addDefault(potionPath + ".name", ism.getShopItemsRaw().get(i));
            Settings.shopdata.addDefault(potionPath + ".shoppotion", ism.getShopPotionsRaw().get(i));
            Settings.shopdata.addDefault(potionPath + ".shoppotiontype", ism.getShopPotionTypesRaw().get(i));
            Settings.shopdata.addDefault(potionPath + ".kit", ism.getShopKitsRaw().get(i));
            Settings.shopdata.addDefault(potionPath + ".cost", ism.getShopCostsRaw().get(i));
            Settings.shopdata.addDefault(potionPath + ".sale", ism.getShopSalesRaw().get(i));
        }
        Settings.shopdata.options().copyDefaults(true);

        for (int i = 0; i < ism.getShopItemsRaw().size(); i++) {
            String potionPath = "pg.potions." + (i + 1);
            String name = Settings.shopdata.getString(potionPath + ".name");
            if (name != null) ism.getShopItemsRaw().set(i, name);

            PotionEffect effect = PotionSerialization.deserializePotionEffect(Settings.shopdata.get(potionPath + ".shoppotion"), ism.getShopItemsRaw().get(i));
            if (effect != null) ism.getShopPotionsRaw().set(i, effect);

            ItemStack item = PotionSerialization.deserializeItemStack(Settings.shopdata.get(potionPath + ".shoppotiontype"));
            if (item != null) ism.getShopPotionTypesRaw().set(i, item);

            String kit = Settings.shopdata.getString(potionPath + ".kit");
            if (kit != null) ism.getShopKitsRaw().set(i, kit);

            if (Settings.shopdata.get(potionPath + ".cost") instanceof Integer cost) ism.getShopCostsRaw().set(i, cost);
            if (Settings.shopdata.get(potionPath + ".sale") instanceof Integer sale) ism.getShopSalesRaw().set(i, sale);
        }

        try {
            Settings.shopdata.save(Settings.shopFile);
            Settings.lobbies.save(Settings.lobbiesFile);
        } catch (IOException ex) {
            sendError(ex);
        }
    }

    private void syncKits() {
        IItemStateManager ism = plugin.getItemStateManager();
        ism.getKitplayersRaw().put(Messages.RandomText(), 0);
        for (String all : ism.getKitsRaw()) {
            ism.getKitplayersRaw().put(all, 0);
        }

        int kititem = 1;
        for (int i = 0; i < ism.getKitsRaw().size(); i++) {
            String kitPath = "pg.kits." + kititem;
            if (Settings.kitdata.get(kitPath) == null) {
                Settings.kitdata.addDefault(kitPath, ism.getKitsRaw().get(kititem - 1));
                Settings.kitdata.addDefault(kitPath + ".name", ism.getKitsRaw().get(kititem - 1));
                Settings.kitdata.options().copyDefaults(true);
            } else {
                ism.getKitsRaw().set(kititem - 1, Settings.kitdata.getString(kitPath + ".name"));
            }
            kititem++;
        }

        try {
            Settings.kitdata.save(Settings.kitsFile);
        } catch (IOException ex) {
            sendError(ex);
        }
    }

    private void configureCoin(PotionGamesX plugin) {
        ItemStack coin = plugin.getCoin();
        ItemMeta coinmeta = coin.getItemMeta();
        if (coinmeta == null) {
            plugin.getLogger().warning("[PotionGamesX] Coin ItemMeta is null, skipping coin configuration");
            return;
        }
        coinmeta.displayName(Messages.CoinSingle());
        coin.setItemMeta(coinmeta);
    }

    private void initializeLobbies() {
        if (!plugin.getConfigManager().isGameServer()) {
            new RankWallUpdater(plugin).start();
            return;
        }

        for (int lobby = 1; lobby <= 27; lobby++) {
            if (!Settings.lobbies.contains("pg.lobbies." + lobby)) {
                continue;
            }

            String s = Integer.toString(lobby);
            var lsm = plugin.getLobbyStateManager();
            var asm = plugin.getArenaStateManager();

            lsm.setCheckArenas(s, false);
            lsm.setActivateTeams(s, true);
            lsm.setActivateKits(s, true);
            lsm.setActivateShop(s, true);
            lsm.setActivateAirdrops(s, true);
            lsm.setJoinable(s, true);
            lsm.setForcearena(s, false);
            lsm.setDeathmatchEnabled(s, false);
            lsm.setMoveAllowed(s, true);
            lsm.setVoteallowed(s, false);
            lsm.setTeamallowed(s, false);
            lsm.setKitallowed(s, false);
            lsm.setLobbyAmount(s, 0);
            lsm.setTickstarted(s, true);
            lsm.setBuildAllowed(s, false);
            lsm.setPaused(s, false);
            lsm.setCurrentVote(s, null);
            lsm.setVotedArena(s, null);
            lsm.setMaxPlayers(s, 24);
            lsm.setMinPlayers(s, 12);
            lsm.setRoundTime(s, 30);
            lsm.setRoundTimeSeconds(s, 1800);
            asm.setLobbyTeamSize(s, 2);
            asm.initializeLobbyTeams(s, 12);

            syncLobbyConfig(s, "activateTeams", plugin.getConfigManager().isActivateTeams(),
                    value -> lsm.setActivateTeams(s, value));
            syncLobbyConfig(s, "activateKits", plugin.getConfigManager().isActivateKits(),
                    value -> lsm.setActivateKits(s, value));
            syncLobbyConfig(s, "activateShop", plugin.getConfigManager().isActivateShop(),
                    value -> lsm.setActivateShop(s, value));
            syncLobbyConfig(s, "activateAirdrops", false,
                    value -> lsm.setActivateAirdrops(s, value));
            syncLobbyConfig(s, "teamSize", 2,
                    value -> { asm.setLobbyTeamSize(s, value);
                               asm.initializeLobbyTeams(s, 12 / value); });
            syncLobbyConfig(s, "maxPlayers", 24,
                    value -> { lsm.setMaxPlayers(s, value);
                               lsm.setMinPlayers(s, value / 2); });
            syncLobbyConfig(s, "minPlayers", 12,
                    value -> lsm.setMinPlayers(s, value));
            syncLobbyConfig(s, "roundTime", 30,
                    value -> { lsm.setRoundTime(s, value);
                               lsm.setRoundTimeSeconds(s, value * 60); });

            lsm.setRoundTimeSeconds(s, lsm.getRoundTime(s) * 60);

            if (!lsm.isVoteallowed(s)) {
                lsm.setVoteallowed(s, true);
                for (int max = 1; max < 27; max++) {
                    String arenaPath = "pg.lobbies." + s + "." + max + ".name";
                    if (Settings.lobbies.contains(arenaPath)) {
                        String arenaName = Settings.lobbies.getString(arenaPath);
                        asm.addLobbyVote(s, arenaName);
                        asm.removeLobbyVote(s, arenaName); // keep at 0
                    }
                }
            }

            if (!lsm.isTeamallowed(s)) {
                lsm.setTeamallowed(s, true);
                asm.initializeLobbyTeams(s, 12 / asm.getLobbyTeamSize(s));
            }

            if (!lsm.isKitallowed(s)) {
                lsm.setKitallowed(s, true);
                var ism = plugin.getItemStateManager();
                ism.getKitplayersRaw().put(Messages.RandomText(), 0);
                for (String all : ism.getKitsRaw()) {
                    ism.getKitplayersRaw().put(all, 0);
                }
            }

            lsm.setGameState(s, GameStates.WAITING);
            if (plugin.getGame().getLobby(lobby) != null) {
                plugin.getGame().getLobby(lobby).startTick();
            }
        }
    }

    private <T> void syncLobbyConfig(String lobbyId, String key, T defaultValue, java.util.function.Consumer<T> setter) {
        String path = "pg.lobbies." + lobbyId + "." + key;
        if (Settings.lobbies.get(path) == null) {
            Settings.lobbies.addDefault(path, defaultValue);
            Settings.lobbies.options().copyDefaults(true);
            try {
                Settings.lobbies.save(Settings.lobbiesFile);
            } catch (IOException ex) {
                sendError(ex);
            }
            return;
        }
        @SuppressWarnings("unchecked")
        T value = (T) Settings.lobbies.get(path);
        setter.accept(value);
    }

    private void sendError(IOException ex) {
        plugin.getComponentLogger().info(Settings.prefix.append(
                Messages.FileSaveFailed().append(Component.text(": " + ex.getMessage()).color(NamedTextColor.RED))
        ));
    }
}
