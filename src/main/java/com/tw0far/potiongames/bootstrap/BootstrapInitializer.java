package com.tw0far.potiongames.bootstrap;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Settings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public final class BootstrapInitializer {
    private final PotionGames plugin;

    public BootstrapInitializer(PotionGames plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        Settings.arenadatafile = new File(plugin.getDataFolder() + File.separator + "arenadata.yml");
        Settings.chestdatafile = new File(plugin.getDataFolder() + File.separator + "chestdata.yml");
        Settings.kitdatafile = new File(plugin.getDataFolder() + File.separator + "kitdata.yml");
        Settings.messagesfile = new File(plugin.getDataFolder() + File.separator + "messages.yml");
        Settings.shopdatafile = new File(plugin.getDataFolder() + File.separator + "shopdata.yml");

        if (!Settings.messagesfile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        Settings.loadConfigurations();
        Settings.loadSettings(plugin);
        plugin.getGame().load();

        seedChatMessages();
        seedShopData();
        seedKitData();
    }

    private void seedChatMessages() {
        List<String> chatmessages = plugin.getChatmessages();
        Collections.addAll(chatmessages,
                "Waiting for players!",
                "The game starts in",
                "Player-Finder",
                "The game starts now!",
                "has won the game!",
                "Teleporting to lobby in",
                "Teleporting to lobby now!",
                "will be played!",
                "Dead",
                "was killed by",
                "died",
                "I'm on fire!",
                "Blocks away from next player",
                "No player found!",
                "Arena-Selector",
                "Votes",
                "You have voted for",
                "in spectator mode",
                "could not be teleported to the lobby!",
                "The game has already started!",
                "The game has been started!",
                "Not enough players to start the game!",
                "Pause",
                "Build",
                "Lobby successfully set!",
                "Server stopped!",
                "has been forced as arena!",
                "is not an arena!",
                "successfully removed!",
                "successfully set!",
                "Successfully joined lobby",
                "is not a valid spawn!",
                "Successfully left lobby",
                "Place",
                "Head successfully set!",
                "Sign successfully set!",
                "Connection to database established!",
                "Connection to database failed! For more information see console.",
                "Connection to database closed!",
                "Failed to close connection to database! For more information see console.",
                "Plugin started successfully!",
                "Plugin stopped successfully!",
                "Random",
                "Team-Selector",
                "Players",
                "You are now in team",
                "You now have the kit",
                "This team is already full!",
                "Update-Checker-Error",
                "Shop",
                "Duration",
                "Price",
                "Coins",
                "You not have enough Coins!",
                "You not have an empty bottle!",
                "Coin",
                "Stats",
                "Won",
                "Lost",
                "Kills",
                "Deaths",
                "K/D",
                "Kit-Selector",
                "File loading / saving fail! For more information see console.",
                "Commands",
                "Rounds",
                "Lobby successfully removed!",
                "seconds remaining to end this round!",
                "Nobody won this round!",
                "Type lobby number in chat to add it!",
                "Type arena name in chat to add it!",
                "Type lobby number in chat to remove it!",
                "Type arena name in chat to remove it!",
                "could not be teleported to a spawn!",
                "This lobby does not exists!",
                "Use /pg help for help!",
                "There is not a new update available.",
                "There is a new update available.",
                "Plugin successfully reloaded!",
                "Extremely explosive TNT",
                "Leave",
                "Teleporting to deathmatch arena in",
                "Teleporting to deathmatch arena now!",
                "Deathmatch is starting in",
                "Deathmatch started!",
                "Could not update Rank-Wall!",
                "Please inform an admin!",
                "Could not join lobby!",
                "Could not teleport to arena!",
                "Could not teleport to deathmatch arena!",
                "Could not load an arena!",
                "Reward",
                "An error occurred",
                "For winning the round you get",
                "For killing a player you get",
                "Lobby%s is starting! Join with /pg join%s",
                "You have a block above you!",
                "Airdrop is falling at your location!",
                "Airdrop is falling at",
                "This arena does not exists!",
                "Lobby enabled!",
                "Lobby disabled!");

        int message = 1;
        for (int i = 0; i < chatmessages.size(); i++) {
            if (Settings.messages.get("pg.messages." + Settings.language + "." + message) == null) {
                Settings.messages.addDefault("pg.messages." + Settings.language + "." + message, chatmessages.get(message - 1));
                Settings.messages.options().copyDefaults(true);
            } else {
                String text = Settings.messages.getString("pg.messages." + Settings.language + "." + message);
                chatmessages.set(message - 1, text);
            }
            message++;
        }

        try {
            Settings.messages.save(Settings.messagesfile);
        } catch (IOException ex) {
            Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + chatmessages.get(63) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
        }
    }

    private void seedShopData() {
        List<String> shop = plugin.getShop();
        List<PotionEffect> shoppotion = plugin.getShoppotion();
        List<ItemStack> shoppotiontype = plugin.getShoppotiontype();
        List<String> shopkit = plugin.getShopkit();
        List<Integer> shopcost = plugin.getShopcost();
        List<Integer> shopsale = plugin.getShopsale();

        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "JUMP_BOOST", PotionEffectType.JUMP_BOOST, Material.POTION, "Looter");
        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "RESISTANCE", PotionEffectType.RESISTANCE, Material.POTION, "Tank");
        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "SPEED", PotionEffectType.SPEED, Material.POTION, "Looter");
        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "ABSORPTION", PotionEffectType.ABSORPTION, Material.POTION, "Tank");
        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "FIRE_RESISTANCE", PotionEffectType.FIRE_RESISTANCE, Material.POTION, "Tank");
        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "INSTANT_HEALTH", PotionEffectType.INSTANT_HEALTH, Material.POTION, "Healer");
        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "HEALTH_BOOST", PotionEffectType.HEALTH_BOOST, Material.POTION, "Healer");
        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "INVISIBILITY", PotionEffectType.INVISIBILITY, Material.POTION, "Ghost");
        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "REGENERATION", PotionEffectType.REGENERATION, Material.POTION, "Healer");
        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "SATURATION", PotionEffectType.SATURATION, Material.POTION, "Looter");
        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "STRENGTH", PotionEffectType.STRENGTH, Material.POTION, "Fighter");
        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "DOLPHINS_GRACE", PotionEffectType.DOLPHINS_GRACE, Material.POTION, "Looter");
        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "NIGHT_VISION", PotionEffectType.NIGHT_VISION, Material.POTION, "Ghost");
        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "WATER_BREATHING", PotionEffectType.WATER_BREATHING, Material.POTION, "Ghost");
        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "WEAKNESS", PotionEffectType.WEAKNESS, Material.SPLASH_POTION, "Fighter");
        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "WITHER", PotionEffectType.WITHER, Material.SPLASH_POTION, "Fighter");
        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "GLOWING", PotionEffectType.GLOWING, Material.SPLASH_POTION, "Fighter");
        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "BLINDNESS", PotionEffectType.BLINDNESS, Material.SPLASH_POTION, "Fighter");
        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "NAUSEA", PotionEffectType.NAUSEA, Material.SPLASH_POTION, "Fighter");
        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "BAD_OMEN", PotionEffectType.BAD_OMEN, Material.SPLASH_POTION, "Fighter");
        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "BAD_OMEN2", PotionEffectType.BAD_OMEN, Material.SPLASH_POTION, "Fighter");
        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "BAD_OMEN3", PotionEffectType.BAD_OMEN, Material.SPLASH_POTION, "Fighter");
        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "BAD_OMEN4", PotionEffectType.BAD_OMEN, Material.SPLASH_POTION, "Fighter");
        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "BAD_OMEN5", PotionEffectType.BAD_OMEN, Material.SPLASH_POTION, "Fighter");
        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "BAD_OMEN6", PotionEffectType.BAD_OMEN, Material.SPLASH_POTION, "Fighter");
        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "BAD_OMEN7", PotionEffectType.BAD_OMEN, Material.SPLASH_POTION, "Fighter");
        addShopEntry(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale, "BAD_OMEN8", PotionEffectType.BAD_OMEN, Material.SPLASH_POTION, "Fighter");

        int shopitem = 1;
        for (int i = 0; i < shop.size(); i++) {
            if (Settings.shopdata.get("pg.potions." + shopitem) == null) {
                Settings.shopdata.addDefault("pg.potions." + shopitem, shop.get(shopitem - 1));
                Settings.shopdata.addDefault("pg.potions." + shopitem + ".name", shop.get(shopitem - 1));
                Settings.shopdata.addDefault("pg.potions." + shopitem + ".shoppotion", shoppotion.get(shopitem - 1));
                Settings.shopdata.addDefault("pg.potions." + shopitem + ".shoppotiontype", shoppotiontype.get(shopitem - 1));
                Settings.shopdata.addDefault("pg.potions." + shopitem + ".kit", shopkit.get(shopitem - 1));
                Settings.shopdata.addDefault("pg.potions." + shopitem + ".cost", shopcost.get(shopitem - 1));
                Settings.shopdata.addDefault("pg.potions." + shopitem + ".sale", shopsale.get(shopitem - 1));
                Settings.shopdata.options().copyDefaults(true);
            } else {
                String name = Settings.shopdata.getString("pg.potions." + shopitem + ".name");
                shop.set(shopitem - 1, name);
                PotionEffect potion = (PotionEffect) Settings.shopdata.get("pg.potions." + shopitem + ".shoppotion");
                shoppotion.set(shopitem - 1, potion);
                ItemStack potiontype = (ItemStack) Settings.shopdata.get("pg.potions." + shopitem + ".shoppotiontype");
                shoppotiontype.set(shopitem - 1, potiontype);
                String kitname = Settings.shopdata.getString("pg.potions." + shopitem + ".kit");
                shopkit.set(shopitem - 1, kitname);
                Integer cost = (Integer) Settings.shopdata.get("pg.potions." + shopitem + ".cost");
                shopcost.set(shopitem - 1, cost);
                Integer sale = (Integer) Settings.shopdata.get("pg.potions." + shopitem + ".sale");
                shopsale.set(shopitem - 1, sale);
            }
            shopitem++;
        }

        try {
            Settings.shopdata.save(Settings.shopdatafile);
        } catch (IOException ex) {
            Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + plugin.getChatmessages().get(63) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
        }

        try {
            Settings.arenadata.save(Settings.arenadatafile);
        } catch (IOException ex) {
            Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + plugin.getChatmessages().get(63) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
        }
    }

    private void addShopEntry(List<String> shop, List<PotionEffect> shoppotion, List<ItemStack> shoppotiontype, List<String> shopkit,
                              List<Integer> shopcost, List<Integer> shopsale, String name, PotionEffectType effect,
                              Material potionMaterial, String kit) {
        shop.add(name);
        shoppotion.add(new PotionEffect(effect, 30 * 20, 1));
        shoppotiontype.add(new ItemStack(potionMaterial));
        shopkit.add(kit);
        shopcost.add(4);
        shopsale.add(2);
    }

    private void seedKitData() {
        List<String> kits = plugin.getKits();
        Collections.addAll(kits, "Rich Kid", "Fighter", "Healer", "Looter", "Ghost", "Tank");
        for (int i = 7; i < 27; i++) {
            kits.add("kit" + i);
        }

        plugin.getKitplayers().put(plugin.getChatmessages().get(42), 0);
        for (String all : kits) {
            plugin.getKitplayers().put(all, 0);
        }

        int kititem = 1;
        for (int i = 0; i < kits.size(); i++) {
            if (Settings.kitdata.get("pg.kits." + kititem) == null) {
                Settings.kitdata.addDefault("pg.kits." + kititem, kits.get(kititem - 1));
                Settings.kitdata.addDefault("pg.kits." + kititem + ".name", kits.get(kititem - 1));
                Settings.kitdata.options().copyDefaults(true);
            } else {
                String name = Settings.kitdata.getString("pg.kits." + kititem + ".name");
                kits.set(kititem - 1, name);
            }
            kititem++;
        }

        try {
            Settings.kitdata.save(Settings.kitdatafile);
        } catch (IOException ex) {
            Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + plugin.getChatmessages().get(63) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
        }
    }
}
