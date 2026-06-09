package com.tw0far.potiongames.bootstrap;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.managers.IItemStateManager;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.Settings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

        ensureFileExists(Settings.lobbiesFile, "lobbies.yml");
        saveResourceIfMissing("chests.yml", Settings.chestsFile);
        saveResourceIfMissing("messages.yml", Settings.messagesFile);
        saveResourceIfMissing("kits.yml", Settings.kitsFile);
        saveResourceIfMissing("shop.yml", Settings.shopFile);

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
                plugin.getComponentLogger().info(Messages.FileSaveFailed()
                    .append(Component.text(": Could not create " + name + " - " + e.getMessage()).color(NamedTextColor.RED)));
            }
        }
    }

    private void saveResourceIfMissing(String resourceName, File targetFile) {
        if (!targetFile.exists()) {
            plugin.saveResource(resourceName, false);
        }
    }

    private void seedShopData() {
        IItemStateManager ism = plugin.getItemStateManager();

        List<String> shop = ism.getShopItemsRaw();
        List<PotionEffect> shoppotion = ism.getShopPotionsRaw();
        List<ItemStack> shoppotiontype = ism.getShopPotionTypesRaw();
        List<String> shopkit = ism.getShopKitsRaw();
        List<Integer> shopcost = ism.getShopCostsRaw();
        List<Integer> shopsale = ism.getShopSalesRaw();

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
            String potionPath = "pg.potions." + shopitem;
            if (Settings.shopdata.get(potionPath) == null) {
                Settings.shopdata.addDefault(potionPath, shop.get(shopitem - 1));
                Settings.shopdata.addDefault(potionPath + ".name", shop.get(shopitem - 1));
                Settings.shopdata.addDefault(potionPath + ".shoppotion", shoppotion.get(shopitem - 1));
                Settings.shopdata.addDefault(potionPath + ".shoppotiontype", shoppotiontype.get(shopitem - 1));
                Settings.shopdata.addDefault(potionPath + ".kit", shopkit.get(shopitem - 1));
                Settings.shopdata.addDefault(potionPath + ".cost", shopcost.get(shopitem - 1));
                Settings.shopdata.addDefault(potionPath + ".sale", shopsale.get(shopitem - 1));
                Settings.shopdata.options().copyDefaults(true);
            } else {
                shop.set(shopitem - 1, Settings.shopdata.getString(potionPath + ".name"));
                shoppotion.set(shopitem - 1, (PotionEffect) Settings.shopdata.get(potionPath + ".shoppotion"));
                shoppotiontype.set(shopitem - 1, (ItemStack) Settings.shopdata.get(potionPath + ".shoppotiontype"));
                shopkit.set(shopitem - 1, Settings.shopdata.getString(potionPath + ".kit"));
                shopcost.set(shopitem - 1, (Integer) Settings.shopdata.get(potionPath + ".cost"));
                shopsale.set(shopitem - 1, (Integer) Settings.shopdata.get(potionPath + ".sale"));
            }
            shopitem++;
        }

        ism.replaceShop(shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale);

        try {
            Settings.shopdata.save(Settings.shopFile);
        } catch (Exception ex) {
            plugin.getComponentLogger().info(Messages.FileSaveFailed()
                .append(Component.text(": " + ex.getMessage()).color(NamedTextColor.RED)));
        }
    }

    private void addShopEntry(List<String> shop, List<PotionEffect> shoppotion, List<ItemStack> shoppotiontype, List<String> shopkit,
                              List<Integer> shopcost, List<Integer> shopsale, String name, PotionEffectType effect,
                              Material potionMaterial, String kit) {
        shop.add(name);
        shoppotion.add(new PotionEffect(effect, 30 * 20, 1, true, true, true));
        shoppotiontype.add(new ItemStack(potionMaterial));
        shopkit.add(kit);
        shopcost.add(4);
        shopsale.add(2);
    }

    private void seedKitData() {
        IItemStateManager ism = plugin.getItemStateManager();
        List<String> kits = ism.getKitsRaw();
        Collections.addAll(kits, "Rich Kid", "Fighter", "Healer", "Looter", "Ghost", "Tank");
        for (int i = 7; i < 27; i++) {
            kits.add("kit" + i);
        }

        java.util.Map<String, Integer> kitplayers = new java.util.HashMap<>();
        kitplayers.put(Messages.RandomText(), 0);
        for (String all : kits) {
            kitplayers.put(all, 0);
        }

        ism.replaceKitplayers(kitplayers);

        int kititem = 1;
        for (int i = 0; i < kits.size(); i++) {
            String kitPath = "pg.kits." + kititem;
            if (Settings.kitdata.get(kitPath) == null) {
                Settings.kitdata.addDefault(kitPath, kits.get(kititem - 1));
                Settings.kitdata.addDefault(kitPath + ".name", kits.get(kititem - 1));
                Settings.kitdata.options().copyDefaults(true);
            } else {
                kits.set(kititem - 1, Settings.kitdata.getString(kitPath + ".name"));
            }
            kititem++;
        }

        try {
            Settings.kitdata.save(Settings.kitsFile);
        } catch (IOException ex) {
            plugin.getComponentLogger().info(Messages.FileSaveFailed()
                .append(Component.text(": " + ex.getMessage()).color(NamedTextColor.RED)));
        }
    }
}
