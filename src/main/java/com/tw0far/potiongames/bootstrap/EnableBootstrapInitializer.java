package com.tw0far.potiongames.bootstrap;

import com.tw0far.potiongames.main.PotionGames;
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
        syncMessages(context);
        syncShop(context);
        syncKits(context);
        configureMysql(context);
        plugin.getDatabaseManager().connect();
        configureCoin(context.coin, context.chatmessages);
        initializeLobbies(context);
    }

    private void loadGlobalConfig(EnableBootstrapContext context) {
        FileConfiguration config = plugin.getConfig();
        context.activateMysql = readBoolean(config, "pg.activateMySQL", context.activateMysql);
        context.countdown = readInt(config, "pg.countdown", context.countdown);
        context.startOnJoin = readBoolean(config, "pg.startOnJoin", context.startOnJoin);
        context.compassOnSpawn = readBoolean(config, "pg.compassOnSpawn", context.compassOnSpawn);
        context.allowOutsideChat = readBoolean(config, "pg.allowOutsideChat", context.allowOutsideChat);
        context.changeGamerules = readBoolean(config, "pg.changeGamerules", context.changeGamerules);
        context.activateTeams = readBoolean(config, "pg.activateTeams", context.activateTeams);
        context.activateKits = readBoolean(config, "pg.activateKits", context.activateKits);
        context.activateShop = readBoolean(config, "pg.activateShop", context.activateShop);
        context.activateAirdrops = readBoolean(config, "pg.activateAirdrops", context.activateAirdrops);
        context.gameServer = readBoolean(config, "pg.getGame()Server", context.gameServer);
        context.maxPlayers = readInt(config, "pg.maxPlayers", context.maxPlayers);
        context.minPlayers = readInt(config, "pg.minPlayers", context.minPlayers);
        context.teamSize = readInt(config, "pg.teamSize", context.teamSize);
        context.teamAmount = context.maxPlayers / context.teamSize;
        context.roundTime = readInt(config, "pg.roundTime", context.roundTime);
        context.roundTimeSeconds = context.roundTime * 60;
        context.activePotions = readInt(config, "pg.activePotions", context.activePotions);
        context.activeKits = readInt(config, "pg.activeKits", context.activeKits);
        context.activateScoreboard = readBoolean(config, "pg.activateScoreboard", context.activateScoreboard);
        context.friendlyFire = readBoolean(config, "pg.friendlyFire", context.friendlyFire);
        context.joinStarted = readBoolean(config, "pg.joinStarted", context.joinStarted);
        context.activateDeathmatch = readBoolean(config, "pg.activateDeathmatch", context.activateDeathmatch);
        context.enableRewards = readBoolean(config, "pg.enableRewards", context.enableRewards);
        context.broadcastStarting = readBoolean(config, "pg.broadcastStarting", context.broadcastStarting);
        context.winningReward = readInt(config, "pg.winningReward", context.winningReward);
        context.killReward = readInt(config, "pg.killReward", context.killReward);
        context.language = readString(config, "pg.language", context.language);
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

    private void syncMessages(EnableBootstrapContext context) {
        int message = 1;
        for (int i = 0; i < context.chatmessages.size(); i++) {
            String path = "pg.messages." + context.language + "." + message;
            if (Settings.messages.get(path) == null) {
                Settings.messages.addDefault(path, context.chatmessages.get(message - 1));
                Settings.messages.options().copyDefaults(true);
            } else {
                context.chatmessages.set(message - 1, Settings.messages.getString(path));
            }
            message++;
        }

        try {
            Settings.messages.save(Settings.messagesfile);
        } catch (IOException ex) {
            sendError(context, ex);
        }
    }

    private void syncShop(EnableBootstrapContext context) {
        int shopitem = 1;
        for (int i = 0; i < context.shop.size(); i++) {
            String potionPath = "pg.potions." + shopitem;
            if (Settings.shopdata.get(potionPath) == null) {
                Settings.shopdata.addDefault(potionPath, context.shop.get(shopitem - 1));
                Settings.shopdata.addDefault(potionPath + ".name", context.shop.get(shopitem - 1));
                Settings.shopdata.addDefault(potionPath + ".shoppotion", context.shoppotion.get(shopitem - 1));
                Settings.shopdata.addDefault(potionPath + ".shoppotiontype", context.shoppotiontype.get(shopitem - 1));
                Settings.shopdata.addDefault(potionPath + ".kit", context.shopkit.get(shopitem - 1));
                Settings.shopdata.addDefault(potionPath + ".cost", context.shopcost.get(shopitem - 1));
                Settings.shopdata.addDefault(potionPath + ".sale", context.shopsale.get(shopitem - 1));
                Settings.shopdata.options().copyDefaults(true);
            } else {
                context.shop.set(shopitem - 1, Settings.shopdata.getString(potionPath + ".name"));
                context.shoppotion.set(shopitem - 1, (PotionEffect) Settings.shopdata.get(potionPath + ".shoppotion"));
                context.shoppotiontype.set(shopitem - 1, (ItemStack) Settings.shopdata.get(potionPath + ".shoppotiontype"));
                context.shopkit.set(shopitem - 1, Settings.shopdata.getString(potionPath + ".kit"));
                context.shopcost.set(shopitem - 1, (Integer) Settings.shopdata.get(potionPath + ".cost"));
                context.shopsale.set(shopitem - 1, (Integer) Settings.shopdata.get(potionPath + ".sale"));
            }
            shopitem++;
        }

        try {
            Settings.shopdata.save(Settings.shopdatafile);
            Settings.arenadata.save(Settings.arenadatafile);
        } catch (IOException ex) {
            sendError(context, ex);
        }
    }

    private void syncKits(EnableBootstrapContext context) {
        context.kitplayers.put(context.chatmessages.get(42), 0);
        for (String all : context.kits) {
            context.kitplayers.put(all, 0);
        }

        int kititem = 1;
        for (int i = 0; i < context.kits.size(); i++) {
            String kitPath = "pg.kits." + kititem;
            if (Settings.kitdata.get(kitPath) == null) {
                Settings.kitdata.addDefault(kitPath, context.kits.get(kititem - 1));
                Settings.kitdata.addDefault(kitPath + ".name", context.kits.get(kititem - 1));
                Settings.kitdata.options().copyDefaults(true);
            } else {
                context.kits.set(kititem - 1, Settings.kitdata.getString(kitPath + ".name"));
            }
            kititem++;
        }

        try {
            Settings.kitdata.save(Settings.kitdatafile);
        } catch (IOException ex) {
            sendError(context, ex);
        }
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
            context.host = config.getString("pg.mysql.host");
            context.port = config.getString("pg.mysql.port");
            context.database = config.getString("pg.mysql.database");
            context.user = config.getString("pg.mysql.user");
            context.password = config.getString("pg.mysql.password");
        }
    }

    private void configureCoin(ItemStack coin, java.util.List<String> chatmessages) {
        ItemMeta coinmeta = coin.getItemMeta();
        assert coinmeta != null;
        coinmeta.displayName(Component.text(chatmessages.get(55)).color(NamedTextColor.DARK_AQUA));
        coin.setItemMeta(coinmeta);
    }

    private void initializeLobbies(EnableBootstrapContext context) {
        if (!context.gameServer) {
            new RankWallUpdater(plugin).start();
            return;
        }

        for (int lobby = 1; lobby <= 27; lobby++) {
            if (!Settings.arenadata.contains("pg.lobbies." + lobby)) {
                continue;
            }

            String s = Integer.toString(lobby);
            context.lobbyCheckArenas.put(s, false);
            context.lobbyActivateTeams.put(s, true);
            context.lobbyActivateKits.put(s, true);
            context.lobbyActivateShop.put(s, true);
            context.lobbyActivateAirdrops.put(s, true);
            context.lobbyJoinable.put(s, true);
            context.lobbyForcearena.put(s, false);
            context.lobbyDeathmatch.put(s, false);
            context.lobbyMove.put(s, true);
            context.lobbyVoteallowed.put(s, false);
            context.lobbyTeamallowed.put(s, false);
            context.lobbyKitallowed.put(s, false);
            context.lobbyAmount.put(s, 0);
            context.lobbyTickstarted.put(s, true);
            context.lobbyBuild.put(s, false);
            context.lobbyPause.put(s, false);
            context.lobbyVote.put(s, null);
            context.lobbyVotedarena.put(s, null);
            context.lobbyteamSize.put(s, 2);
            context.lobbymaxPlayers.put(s, 24);
            context.lobbyminPlayers.put(s, context.lobbymaxPlayers.get(s) / 2);
            context.lobbyteamAmount.put(s, context.lobbymaxPlayers.get(s) / context.lobbyteamSize.get(s));
            context.lobbyroundTime.put(s, 30);
            context.lobbyroundTimeSeconds.put(s, context.lobbyroundTime.get(s) * 60);

            syncLobbyConfig(context, s, "activateTeams", context.activateTeams, value -> context.lobbyActivateTeams.replace(s, value));
            syncLobbyConfig(context, s, "activateKits", context.activateKits, value -> context.lobbyActivateKits.replace(s, value));
            syncLobbyConfig(context, s, "activateShop", context.activateShop, value -> context.lobbyActivateShop.replace(s, value));
            syncLobbyConfig(context, s, "activateAirdrops", context.activateAirdrops, value -> context.lobbyActivateAirdrops.replace(s, value));
            syncLobbyConfig(context, s, "teamSize", context.teamSize, value -> context.lobbyteamSize.replace(s, value));
            syncLobbyConfig(context, s, "maxPlayers", context.maxPlayers, value -> context.lobbymaxPlayers.replace(s, value));
            syncLobbyConfig(context, s, "minPlayers", context.minPlayers, value -> context.lobbyminPlayers.replace(s, value));
            syncLobbyConfig(context, s, "roundTime", context.roundTime, value -> context.lobbyroundTime.replace(s, value));

            context.lobbyroundTimeSeconds.put(s, context.lobbyroundTime.get(s) * 60);
            context.lobbyteamAmount.put(s, context.lobbymaxPlayers.get(s) / context.lobbyteamSize.get(s));

            if (!context.lobbyVoteallowed.get(s)) {
                context.lobbyVoteallowed.replace(s, true);
                HashMap<String, Integer> temp = new HashMap<>();
                temp.put(context.chatmessages.get(42), 0);
                for (int max = 1; max < 27; max++) {
                    String arenaPath = "pg.lobbies." + s + "." + max + ".name";
                    if (Settings.arenadata.contains(arenaPath)) {
                        String arenaName = Settings.arenadata.getString(arenaPath);
                        temp.put(arenaName, 0);
                        context.lobbyvoteplayernamesdata.put(null, arenaName);
                    }
                }
                context.lobbyvotes.put(s, temp);
                context.lobbyvoteplayernames.put(s, context.lobbyvoteplayernamesdata);
            }

            if (!context.lobbyTeamallowed.get(s)) {
                context.lobbyTeamallowed.replace(s, true);
                HashMap<Integer, Integer> temp = new HashMap<>();
                for (int max = 1; max <= context.lobbyteamAmount.get(s); max++) {
                    temp.put(max, 0);
                    context.lobbyteamplayernamesdata.put(null, Integer.toString(max));
                }
                context.lobbyteams.put(s, temp);
                context.lobbyteamplayernames.put(s, context.lobbyteamplayernamesdata);
            }

            if (!context.lobbyKitallowed.get(s)) {
                context.lobbyKitallowed.replace(s, true);
                context.kitplayers.put(context.chatmessages.get(42), 0);
                for (String all : context.kits) {
                    context.kitplayers.put(all, 0);
                }
            }

            context.lobbyLiquidPlaced.put(s, context.lobbyLiquidPlacedData);
            context.lobbyPlacedBlocks.put(s, context.lobbyPlacedBlocksData);
            context.lobbyBreakedBlocks.put(s, context.lobbyBreakedBlocksData);
            context.lobbyWaterBlocks.put(s, context.lobbyWaterBlocksData);
            context.lobbyStates.put(s, GameStates.WAITING);
            if (plugin.getLobbyById(lobby) != null) {
                plugin.getLobbyById(lobby).startTick();
            }
        }
    }

    private <T> void syncLobbyConfig(EnableBootstrapContext context, String lobbyId, String key, T defaultValue, Consumer<T> setter) {
        String path = "pg.lobbies." + lobbyId + "." + key;
        if (Settings.arenadata.get(path) == null) {
            Settings.arenadata.addDefault(path, defaultValue);
            Settings.arenadata.options().copyDefaults(true);
            try {
                Settings.arenadata.save(Settings.arenadatafile);
            } catch (IOException ex) {
                sendError(context, ex);
            }
            return;
        }
        @SuppressWarnings("unchecked")
        T value = (T) Settings.arenadata.get(path);
        setter.accept(value);
    }

    private void sendError(EnableBootstrapContext context, IOException ex) {
        Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(
                Component.text(" " + context.chatmessages.get(63) + ": " + ex.getMessage()).color(NamedTextColor.RED)
        ));
    }
}
