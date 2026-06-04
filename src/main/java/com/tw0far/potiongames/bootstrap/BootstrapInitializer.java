package com.tw0far.potiongames.bootstrap;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Messages;
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
        plugin.getDataFolder().mkdirs();
        Settings.lobbiesFile = new File(plugin.getDataFolder() + File.separator + "lobbies.yml");
        Settings.chestsFile = new File(plugin.getDataFolder() + File.separator + "chests.yml");
        Settings.messagesFile = new File(plugin.getDataFolder() + File.separator + "messages.yml");
        Settings.kitsFile = new File(plugin.getDataFolder() + File.separator + "kits.yml");
        Settings.shopFile = new File(plugin.getDataFolder() + File.separator + "shop.yml");
        
        // Ensure all files exist before loading
        ensureFileExists(Settings.lobbiesFile, "lobbies.yml");
        ensureFileExists(Settings.chestsFile, "chests.yml");
        ensureFileExists(Settings.messagesFile, "messages.yml");
        ensureFileExists(Settings.kitsFile, "kits.yml");
        ensureFileExists(Settings.shopFile, "shop.yml");
        
        // Config.yml: Main config with database, global settings
        // lobbies.yml: Lobbies, arenas, spawns
        // chests.yml: Chest loot definitions
        // messages.yml: Localized messages
        // kits.yml: Kit definitions
        // shop.yml: Shop items
        
        Settings.loadConfigurations();
        
        Settings.loadSettings(plugin);
        plugin.getGame().load();

        Messages.seed();
        seedShopData();
        seedKitData();
    }
    
    private void ensureFileExists(File file, String name) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Bukkit.getConsoleSender().sendMessage(Messages.FileSaveFailed().append(Component.text(": Could not create " + name + " - " + e.getMessage()).color(NamedTextColor.RED)));
            }
        }
    }

    private void seedShopData() {
        // Work on mutable copies, then replace plugin's internal collections via API
        List<String> shop = new java.util.ArrayList<>(plugin.getShop());
        List<PotionEffect> shoppotion = new java.util.ArrayList<>(plugin.getShoppotion());
        List<ItemStack> shoppotiontype = new java.util.ArrayList<>(plugin.getShoppotiontype());
        List<String> shopkit = new java.util.ArrayList<>(plugin.getShopkit());
        List<Integer> shopcost = new java.util.ArrayList<>(plugin.getShopcost());
        List<Integer> shopsale = new java.util.ArrayList<>(plugin.getShopsale());

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

        // Replace plugin internal collections so other code sees final data
        plugin.replaceShop(shop);
        plugin.replaceShoppotion(shoppotion);
        plugin.replaceShoppotiontype(shoppotiontype);
        plugin.replaceShopkit(shopkit);
        plugin.replaceShopcost(shopcost);
        plugin.replaceShopsale(shopsale);

        try {
            Settings.shopdata.save(Settings.shopFile);
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(Messages.FileSaveFailed().append(Component.text(": " + ex.getMessage()).color(NamedTextColor.RED)));
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
        // Use a mutable copy for kits and replace plugin data after population
        List<String> kits = new java.util.ArrayList<>(plugin.getKits());
        Collections.addAll(kits, "Rich Kid", "Fighter", "Healer", "Looter", "Ghost", "Tank");
        for (int i = 7; i < 27; i++) {
            kits.add("kit" + i);
        }

        // Build mutable kitplayers map and replace after population
        java.util.Map<String, Integer> kitplayers = new java.util.HashMap<>();
        kitplayers.put(Messages.RandomText(), 0);
        for (String all : kits) {
            kitplayers.put(all, 0);
        }

        // Replace plugin internal collections
        plugin.replaceKits(kits);
        plugin.replaceKitplayers(kitplayers);

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
            Settings.kitdata.save(Settings.kitsFile);
        } catch (IOException ex) {
            Bukkit.getConsoleSender().sendMessage(Messages.FileSaveFailed().append(Component.text(": " + ex.getMessage()).color(NamedTextColor.RED)));
        }
    }
}
