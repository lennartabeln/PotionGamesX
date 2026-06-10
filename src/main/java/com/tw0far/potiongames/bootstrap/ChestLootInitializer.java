package com.tw0far.potiongames.bootstrap;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.managers.IItemStateManager;
import com.tw0far.potiongames.models.Settings;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class ChestLootInitializer {
    private final PotionGamesX plugin;

    public ChestLootInitializer(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    public void seed() {
        // Load loot items from chests.yml
        var config = Settings.chests;
        if (config == null) {
            plugin.getLogger().warning("Chest loot configuration not loaded, using fallback defaults");
            seedDefaults();
            return;
        }

        ConfigurationSection itemsConfig = config.getConfigurationSection("pg.chestloot.normal.items");
        if (itemsConfig == null) {
            plugin.getLogger().warning("No pg.chestloot.normal.items section found in chests.yml, using fallback defaults");
            seedDefaults();
            return;
        }

        // Load each loot pool from config
        seedLootPool(itemsConfig, "food1", 1, true, false, false);
        seedLootPool(itemsConfig, "food2", 2, true, false, false);
        seedLootPool(itemsConfig, "armour1", 1, false, true, false);
        seedLootPool(itemsConfig, "armour2", 2, false, true, false);
        seedLootPool(itemsConfig, "armour3", 3, false, true, false);
        seedLootPool(itemsConfig, "armour4", 4, false, true, false);
        seedLootPool(itemsConfig, "armour5", 5, false, true, false);
        seedLootPool(itemsConfig, "weapons1", 1, false, false, true);
        seedLootPool(itemsConfig, "weapons2", 2, false, false, true);
    }

    /**
     * Load loot items from config into the specified loot pool.
     * Expected format: "MATERIAL:AMOUNT" (e.g., "CAKE:1", "BREAD:3")
     */
    private void seedLootPool(ConfigurationSection itemsConfig, String poolName, int poolId, boolean isFood, boolean isArmor, boolean isWeapon) {
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
                ItemStack stack = new ItemStack(material, amount);
                if (isFood) {
                    plugin.getItemStateManager().addFood(poolId, stack);
                } else if (isArmor) {
                    plugin.getItemStateManager().addArmor(poolId, stack);
                } else if (isWeapon) {
                    plugin.getItemStateManager().addWeapon(poolId, stack);
                }
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
        IItemStateManager mgr = plugin.getItemStateManager();

        mgr.addFood(1, new ItemStack(Material.CAKE, 1));
        mgr.addFood(1, new ItemStack(Material.BREAD, 3));
        mgr.addFood(1, new ItemStack(Material.PUMPKIN_PIE, 3));
        mgr.addFood(1, new ItemStack(Material.COOKIE, 3));
        mgr.addFood(1, new ItemStack(Material.BAKED_POTATO, 3));

        mgr.addFood(2, new ItemStack(Material.RABBIT_STEW, 1));
        mgr.addFood(2, new ItemStack(Material.MUSHROOM_STEW, 1));
        mgr.addFood(2, new ItemStack(Material.BEETROOT_SOUP, 1));
        mgr.addFood(2, new ItemStack(Material.GOLDEN_CARROT, 1));
        mgr.addFood(2, new ItemStack(Material.MILK_BUCKET, 1));

        mgr.addArmor(1, new ItemStack(Material.LEATHER_HELMET, 1));
        mgr.addArmor(1, new ItemStack(Material.LEATHER_CHESTPLATE, 1));
        mgr.addArmor(1, new ItemStack(Material.LEATHER_LEGGINGS, 1));
        mgr.addArmor(1, new ItemStack(Material.LEATHER_BOOTS, 1));

        mgr.addArmor(2, new ItemStack(Material.CHAINMAIL_HELMET, 1));
        mgr.addArmor(2, new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1));
        mgr.addArmor(2, new ItemStack(Material.CHAINMAIL_LEGGINGS, 1));
        mgr.addArmor(2, new ItemStack(Material.CHAINMAIL_BOOTS, 1));

        mgr.addArmor(3, new ItemStack(Material.GOLDEN_HELMET, 1));
        mgr.addArmor(3, new ItemStack(Material.GOLDEN_CHESTPLATE, 1));
        mgr.addArmor(3, new ItemStack(Material.GOLDEN_LEGGINGS, 1));
        mgr.addArmor(3, new ItemStack(Material.GOLDEN_BOOTS, 1));

        mgr.addArmor(4, new ItemStack(Material.IRON_HELMET, 1));
        mgr.addArmor(4, new ItemStack(Material.IRON_CHESTPLATE, 1));
        mgr.addArmor(4, new ItemStack(Material.IRON_LEGGINGS, 1));
        mgr.addArmor(4, new ItemStack(Material.IRON_BOOTS, 1));

        mgr.addArmor(5, new ItemStack(Material.DIAMOND_HELMET, 1));
        mgr.addArmor(5, new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
        mgr.addArmor(5, new ItemStack(Material.DIAMOND_LEGGINGS, 1));
        mgr.addArmor(5, new ItemStack(Material.DIAMOND_BOOTS, 1));

        mgr.addWeapon(1, new ItemStack(Material.WOODEN_SWORD, 1));
        mgr.addWeapon(1, new ItemStack(Material.STONE_SWORD, 1));
        mgr.addWeapon(1, new ItemStack(Material.WOODEN_AXE, 1));

        mgr.addWeapon(2, new ItemStack(Material.IRON_SWORD, 1));
        mgr.addWeapon(2, new ItemStack(Material.DIAMOND_SWORD, 1));
        mgr.addWeapon(2, new ItemStack(Material.IRON_AXE, 1));
    }
}
