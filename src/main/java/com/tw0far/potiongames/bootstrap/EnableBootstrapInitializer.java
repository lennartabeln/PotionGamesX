package com.tw0far.potiongames.bootstrap;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.GameStates;
import com.tw0far.potiongames.models.Settings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.io.IOException;
import java.util.HashMap;
import java.util.function.Consumer;

public final class EnableBootstrapInitializer {
    private final PotionGames plugin;

    public EnableBootstrapInitializer(PotionGames plugin) {
        this.plugin = plugin;
    }

    public void initialize(EnableBootstrapContext context) {
        loadGlobalConfig(context);
        syncShop(context);
        syncKits(context);
        configureMysql(context);
        plugin.getDatabaseManager().connect();
        configureCoin(context.getCoin());
        initializeLobbies(context);
    }

    private void syncShop(EnableBootstrapContext context) {
        int shopitem = 1;
        for (int i = 0; i < context.getShop().size(); i++) {
            String potionPath = "pg.potions." + shopitem;
            if (Settings.shopdata.get(potionPath) == null) {
                Settings.shopdata.addDefault(potionPath, context.getShop().get(shopitem - 1));
                Settings.shopdata.addDefault(potionPath + ".name", context.getShop().get(shopitem - 1));
                Settings.shopdata.addDefault(potionPath + ".shoppotion", context.getShoppotion().get(shopitem - 1));
                Settings.shopdata.addDefault(potionPath + ".shoppotiontype", context.getShoppotiontype().get(shopitem - 1));
                Settings.shopdata.addDefault(potionPath + ".kit", context.getShopkit().get(shopitem - 1));
                Settings.shopdata.addDefault(potionPath + ".cost", context.getShopcost().get(shopitem - 1));
                Settings.shopdata.addDefault(potionPath + ".sale", context.getShopsale().get(shopitem - 1));
                Settings.shopdata.options().copyDefaults(true);
            } else {
                context.getShop().set(shopitem - 1, Settings.shopdata.getString(potionPath + ".name"));
                context.getShoppotion().set(shopitem - 1, (PotionEffect) Settings.shopdata.get(potionPath + ".shoppotion"));
                context.getShoppotiontype().set(shopitem - 1, (ItemStack) Settings.shopdata.get(potionPath + ".shoppotiontype"));
                context.getShopkit().set(shopitem - 1, Settings.shopdata.getString(potionPath + ".kit"));
                context.getShopcost().set(shopitem - 1, (Integer) Settings.shopdata.get(potionPath + ".cost"));
                context.getShopsale().set(shopitem - 1, (Integer) Settings.shopdata.get(potionPath + ".sale"));
            }
            shopitem++;
        }

        try {
            Settings.shopdata.save(Settings.shopFile);
            Settings.lobbies.save(Settings.lobbiesFile);
        } catch (IOException ex) {
            sendError(context, ex);
        }
    }

    private void syncKits(EnableBootstrapContext context) {
        context.getKitplayers().put(Messages.RandomText(), 0);
        for (String all : context.getKits()) {
            context.getKitplayers().put(all, 0);
        }

        int kititem = 1;
        for (int i = 0; i < context.getKits().size(); i++) {
            String kitPath = "pg.kits." + kititem;
            if (Settings.kitdata.get(kitPath) == null) {
                Settings.kitdata.addDefault(kitPath, context.getKits().get(kititem - 1));
                Settings.kitdata.addDefault(kitPath + ".name", context.getKits().get(kititem - 1));
                Settings.kitdata.options().copyDefaults(true);
            } else {
                context.getKits().set(kititem - 1, Settings.kitdata.getString(kitPath + ".name"));
            }
            kititem++;
        }

        try {
            Settings.kitdata.save(Settings.kitsFile);
        } catch (IOException ex) {
            sendError(context, ex);
        }
    }

    private void loadGlobalConfig(EnableBootstrapContext context) {
        FileConfiguration config = plugin.getConfig();
        context.setActivateMysql(readBoolean(config, "pg.activateMySQL", context.isActivateMysql()));
        context.setCountdown(readInt(config, "pg.countdown", context.getCountdown()));
        context.setStartOnJoin(readBoolean(config, "pg.startOnJoin", context.isStartOnJoin()));
        context.setCompassOnSpawn(readBoolean(config, "pg.compassOnSpawn", context.isCompassOnSpawn()));
        context.setAllowOutsideChat(readBoolean(config, "pg.allowOutsideChat", context.isAllowOutsideChat()));
        context.setChangeGamerules(readBoolean(config, "pg.changeGamerules", context.isChangeGamerules()));
        context.setActivateTeams(readBoolean(config, "pg.activateTeams", context.isActivateTeams()));
        context.setActivateKits(readBoolean(config, "pg.activateKits", context.isActivateKits()));
        context.setActivateShop(readBoolean(config, "pg.activateShop", context.isActivateShop()));
        context.setActivateAirdrops(readBoolean(config, "pg.activateAirdrops", context.isActivateAirdrops()));
        context.setGameServer(readBoolean(config, "pg.getGame()Server", context.isGameServer()));
        context.setMaxPlayers(readInt(config, "pg.maxPlayers", context.getMaxPlayers()));
        context.setMinPlayers(readInt(config, "pg.minPlayers", context.getMinPlayers()));
        context.setTeamSize(readInt(config, "pg.teamSize", context.getTeamSize()));
        context.setTeamAmount(context.getMaxPlayers() / context.getTeamSize());
        context.setRoundTime(readInt(config, "pg.roundTime", context.getRoundTime()));
        context.setRoundTimeSeconds(context.getRoundTime() * 60);
        context.setActivePotions(readInt(config, "pg.activePotions", context.getActivePotions()));
        context.setActiveKits(readInt(config, "pg.activeKits", context.getActiveKits()));
        context.setActivateScoreboard(readBoolean(config, "pg.activateScoreboard", context.isActivateScoreboard()));
        context.setFriendlyFire(readBoolean(config, "pg.friendlyFire", context.isFriendlyFire()));
        context.setJoinStarted(readBoolean(config, "pg.joinStarted", context.isJoinStarted()));
        context.setActivateDeathmatch(readBoolean(config, "pg.activateDeathmatch", context.isActivateDeathmatch()));
        context.setEnableRewards(readBoolean(config, "pg.enableRewards", context.isEnableRewards()));
        context.setBroadcastStarting(readBoolean(config, "pg.broadcastStarting", context.isBroadcastStarting()));
        context.setWinningReward(readInt(config, "pg.winningReward", context.getWinningReward()));
        context.setKillReward(readInt(config, "pg.killReward", context.getKillReward()));
        context.setLanguage(readString(config, "pg.language", context.getLanguage()));
    }

    private boolean readBoolean(FileConfiguration config, String path, boolean defaultValue) {
        return readConfigValue(config, path, defaultValue, config::getBoolean);
    }

    private int readInt(FileConfiguration config, String path, int defaultValue) {
        return readConfigValue(config, path, defaultValue, config::getInt);
    }

    private String readString(FileConfiguration config, String path, String defaultValue) {
        return readConfigValue(config, path, defaultValue, config::getString);
    }

    private <T> T readConfigValue(FileConfiguration config, String path, T defaultValue, java.util.function.Function<String, T> getter) {
        if (config.get(path) == null) {
            config.addDefault(path, defaultValue);
            config.options().copyDefaults(true);
            plugin.saveConfig();
            return defaultValue;
        }
        return getter.apply(path);
    }

    private void configureCoin(ItemStack coin) {
        ItemMeta coinmeta = coin.getItemMeta();
        assert coinmeta != null;
        coinmeta.displayName(Messages.CoinSingle());
        coin.setItemMeta(coinmeta);
    }

    private void configureMysql(EnableBootstrapContext context) {
        FileConfiguration config = plugin.getConfig();
        if (config.get("pg.mysql") == null) {
            config.addDefault("pg.mysql.host", "localhost");
            config.addDefault("pg.mysql.port", "3306");
            config.addDefault("pg.mysql.database", "potiongames");
            config.addDefault("pg.mysql.user", "root");
            config.addDefault("pg.mysql.password", "");
            config.options().copyDefaults(true);
            plugin.saveConfig();
        } else {
            context.setHost(config.getString("pg.mysql.host"));
            context.setPort(config.getString("pg.mysql.port"));
            context.setDatabase(config.getString("pg.mysql.database"));
            context.setUser(config.getString("pg.mysql.user"));
            context.setPassword(config.getString("pg.mysql.password"));
        }
    }

    private void initializeLobbies(EnableBootstrapContext context) {
        if (!context.isGameServer()) {
            new RankWallUpdater(plugin).start();
            return;
        }

        for (int lobby = 1; lobby <= 27; lobby++) {
            if (!Settings.lobbies.contains("pg.lobbies." + lobby)) {
                continue;
            }

            String s = Integer.toString(lobby);
            context.getLobbyCheckArenas().put(s, false);
            context.getLobbyActivateTeams().put(s, true);
            context.getLobbyActivateKits().put(s, true);
            context.getLobbyActivateShop().put(s, true);
            context.getLobbyActivateAirdrops().put(s, true);
            context.getLobbyJoinable().put(s, true);
            context.getLobbyForcearena().put(s, false);
            context.getLobbyDeathmatch().put(s, false);
            context.getLobbyMove().put(s, true);
            context.getLobbyVoteallowed().put(s, false);
            context.getLobbyTeamallowed().put(s, false);
            context.getLobbyKitallowed().put(s, false);
            context.getLobbyAmount().put(s, 0);
            context.getLobbyTickstarted().put(s, true);
            context.getLobbyBuild().put(s, false);
            context.getLobbyPause().put(s, false);
            context.getLobbyVote().put(s, null);
            context.getLobbyVotedarena().put(s, null);
            context.getLobbyteamSize().put(s, 2);
            context.getLobbymaxPlayers().put(s, 24);
            context.getLobbyminPlayers().put(s, context.getLobbymaxPlayers().get(s) / 2);
            context.getLobbyteamAmount().put(s, context.getLobbymaxPlayers().get(s) / context.getLobbyteamSize().get(s));
            context.getLobbyroundTime().put(s, 30);
            context.getLobbyroundTimeSeconds().put(s, context.getLobbyroundTime().get(s) * 60);

            syncLobbyConfig(context, s, "activateTeams", context.isActivateTeams(), value -> context.getLobbyActivateTeams().replace(s, value));
            syncLobbyConfig(context, s, "activateKits", context.isActivateKits(), value -> context.getLobbyActivateKits().replace(s, value));
            syncLobbyConfig(context, s, "activateShop", context.isActivateShop(), value -> context.getLobbyActivateShop().replace(s, value));
            syncLobbyConfig(context, s, "activateAirdrops", context.isActivateAirdrops(), value -> context.getLobbyActivateAirdrops().replace(s, value));
            syncLobbyConfig(context, s, "teamSize", context.getTeamSize(), value -> context.getLobbyteamSize().replace(s, value));
            syncLobbyConfig(context, s, "maxPlayers", context.getMaxPlayers(), value -> context.getLobbymaxPlayers().replace(s, value));
            syncLobbyConfig(context, s, "minPlayers", context.getMinPlayers(), value -> context.getLobbyminPlayers().replace(s, value));
            syncLobbyConfig(context, s, "roundTime", context.getRoundTime(), value -> context.getLobbyroundTime().replace(s, value));

            context.getLobbyroundTimeSeconds().put(s, context.getLobbyroundTime().get(s) * 60);
            context.getLobbyteamAmount().put(s, context.getLobbymaxPlayers().get(s) / context.getLobbyteamSize().get(s));

            if (!context.getLobbyVoteallowed().get(s)) {
                context.getLobbyVoteallowed().replace(s, true);
                HashMap<String, Integer> temp = new HashMap<>();
                temp.put(Messages.RandomText(), 0);
                for (int max = 1; max < 27; max++) {
                    String arenaPath = "pg.lobbies." + s + "." + max + ".name";
                    if (Settings.lobbies.contains(arenaPath)) {
                        String arenaName = Settings.lobbies.getString(arenaPath);
                        temp.put(arenaName, 0);
                        context.getLobbyvoteplayernamesdata().put(null, arenaName);
                    }
                }
                context.getLobbyvotes().put(s, temp);
                context.getLobbyvoteplayernames().put(s, context.getLobbyvoteplayernamesdata());
            }

            if (!context.getLobbyTeamallowed().get(s)) {
                context.getLobbyTeamallowed().replace(s, true);
                HashMap<Integer, Integer> temp = new HashMap<>();
                for (int max = 1; max <= context.getLobbyteamAmount().get(s); max++) {
                    temp.put(max, 0);
                    context.getLobbyteamplayernamesdata().put(null, Integer.toString(max));
                }
                context.getLobbyteams().put(s, temp);
                context.getLobbyteamplayernames().put(s, context.getLobbyteamplayernamesdata());
            }

            if (!context.getLobbyKitallowed().get(s)) {
                context.getLobbyKitallowed().replace(s, true);
                context.getKitplayers().put(Messages.RandomText(), 0);
                for (String all : context.getKits()) {
                    context.getKitplayers().put(all, 0);
                }
            }

            context.getLobbyLiquidPlaced().put(s, context.getLobbyLiquidPlacedData());
            context.getLobbyPlacedBlocks().put(s, context.getLobbyPlacedBlocksData());
            context.getLobbyBreakedBlocks().put(s, context.getLobbyBreakedBlocksData());
            context.getLobbyWaterBlocks().put(s, context.getLobbyWaterBlocksData());
            context.getLobbyStates().put(s, GameStates.WAITING);
            if (plugin.getLobbyById(lobby) != null) {
                plugin.getLobbyById(lobby).startTick();
            }
        }
    }

    private <T> void syncLobbyConfig(EnableBootstrapContext context, String lobbyId, String key, T defaultValue, Consumer<T> setter) {
        String path = "pg.lobbies." + lobbyId + "." + key;
        if (Settings.lobbies.get(path) == null) {
            Settings.lobbies.addDefault(path, defaultValue);
            Settings.lobbies.options().copyDefaults(true);
            try {
                Settings.lobbies.save(Settings.lobbiesFile);
            } catch (IOException ex) {
                sendError(context, ex);
            }
            return;
        }
        @SuppressWarnings("unchecked")
        T value = (T) Settings.lobbies.get(path);
        setter.accept(value);
    }

    private void sendError(EnableBootstrapContext context, IOException ex) {
        Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(
                Messages.FileSaveFailed().append(Component.text(": " + ex.getMessage()).color(NamedTextColor.RED))
        ));
    }
}
