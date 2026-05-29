package com.tw0far.potiongames.bootstrap;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class ChestLootInitializer {
    private final PotionGames plugin;

    public ChestLootInitializer(PotionGames plugin) {
        this.plugin = plugin;
    }

    public void seed() {
        // Load loot items from config
        var config = plugin.getConfig();
        if (config == null) {
            plugin.getLogger().warning("Config not loaded, using fallback chest loot");
            seedDefaults();
            return;
        }

        ConfigurationSection itemsConfig = config.getConfigurationSection("pg.chestloot.normal.items");
        if (itemsConfig == null) {
            plugin.getLogger().warning("No pg.chestloot.normal.items section found, using fallback chest loot");
            seedDefaults();
            return;
        }

        // Load each loot pool from config
        seedLootPool(itemsConfig, "food1", plugin.getFood1());
        seedLootPool(itemsConfig, "food2", plugin.getFood2());
        seedLootPool(itemsConfig, "armour1", plugin.getArmour1());
        seedLootPool(itemsConfig, "armour2", plugin.getArmour2());
        seedLootPool(itemsConfig, "armour3", plugin.getArmour3());
        seedLootPool(itemsConfig, "armour4", plugin.getArmour4());
        seedLootPool(itemsConfig, "armour5", plugin.getArmour5());
        seedLootPool(itemsConfig, "weapons1", plugin.getWeapons1());
        seedLootPool(itemsConfig, "weapons2", plugin.getWeapons2());
    }

    /**
     * Load loot items from config into the specified loot pool.
     * Expected format: "MATERIAL:AMOUNT" (e.g., "CAKE:1", "BREAD:3")
     */
    private void seedLootPool(ConfigurationSection itemsConfig, String poolName, java.util.List<ItemStack> targetPool) {
        if (!itemsConfig.contains(poolName)) {
            plugin.getLogger().warning("Loot pool '" + poolName + "' not found in config");
            return;
        }

        List<String> items = itemsConfig.getStringList(poolName);
        for (String itemStr : items) {
            try {
                String[] parts = itemStr.split(":");
                if (parts.length != 2) {
                    plugin.getLogger().warning("Invalid item format: " + itemStr + " (expected MATERIAL:AMOUNT)");
                    continue;
                }

                Material material = Material.valueOf(parts[0].toUpperCase());
                int amount = Integer.parseInt(parts[1]);
                targetPool.add(new ItemStack(material, amount));
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Invalid amount in item: " + itemStr);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Unknown material in item: " + itemStr);
            }
        }
    }

    /**
     * Fallback seeding with hardcoded defaults
     */
    private void seedDefaults() {
        java.util.Collections.addAll(plugin.getFood1(),
                new ItemStack(Material.CAKE, 1),
                new ItemStack(Material.BREAD, 3),
                new ItemStack(Material.PUMPKIN_PIE, 3),
                new ItemStack(Material.COOKIE, 3),
                new ItemStack(Material.BAKED_POTATO, 3));

        java.util.Collections.addAll(plugin.getFood2(),
                new ItemStack(Material.RABBIT_STEW, 1),
                new ItemStack(Material.MUSHROOM_STEW, 1),
                new ItemStack(Material.BEETROOT_SOUP, 1),
                new ItemStack(Material.GOLDEN_CARROT, 1),
                new ItemStack(Material.MILK_BUCKET, 1));

        java.util.Collections.addAll(plugin.getArmour1(),
                new ItemStack(Material.LEATHER_HELMET, 1),
                new ItemStack(Material.LEATHER_CHESTPLATE, 1),
                new ItemStack(Material.LEATHER_LEGGINGS, 1),
                new ItemStack(Material.LEATHER_BOOTS, 1));

        java.util.Collections.addAll(plugin.getArmour2(),
                new ItemStack(Material.CHAINMAIL_HELMET, 1),
                new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1),
                new ItemStack(Material.CHAINMAIL_LEGGINGS, 1),
                new ItemStack(Material.CHAINMAIL_BOOTS, 1));

        java.util.Collections.addAll(plugin.getArmour3(),
                new ItemStack(Material.GOLDEN_HELMET, 1),
                new ItemStack(Material.GOLDEN_CHESTPLATE, 1),
                new ItemStack(Material.GOLDEN_LEGGINGS, 1),
                new ItemStack(Material.GOLDEN_BOOTS, 1));

        java.util.Collections.addAll(plugin.getArmour4(),
                new ItemStack(Material.IRON_HELMET, 1),
                new ItemStack(Material.IRON_CHESTPLATE, 1),
                new ItemStack(Material.IRON_LEGGINGS, 1),
                new ItemStack(Material.IRON_BOOTS, 1));

        java.util.Collections.addAll(plugin.getArmour5(),
                new ItemStack(Material.DIAMOND_HELMET, 1),
                new ItemStack(Material.DIAMOND_CHESTPLATE, 1),
                new ItemStack(Material.DIAMOND_LEGGINGS, 1),
                new ItemStack(Material.DIAMOND_BOOTS, 1));

        java.util.Collections.addAll(plugin.getWeapons1(),
                new ItemStack(Material.WOODEN_SWORD, 1),
                new ItemStack(Material.STONE_SWORD, 1),
                new ItemStack(Material.WOODEN_AXE, 1));

        java.util.Collections.addAll(plugin.getWeapons2(),
                new ItemStack(Material.IRON_SWORD, 1),
                new ItemStack(Material.DIAMOND_SWORD, 1),
                new ItemStack(Material.IRON_AXE, 1));
    }
}
