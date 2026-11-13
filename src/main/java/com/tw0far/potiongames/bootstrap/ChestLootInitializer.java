package com.tw0far.potiongames.bootstrap;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.managers.IItemStateManager;
import com.tw0far.potiongames.models.Settings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

        seedChestBlocks(config);
        seedCustomChests(config);

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
     * Older/empty configs ship `chestblocks` as empty lists, which made loot
     * and shop chests impossible to open. Seed the legacy defaults so a fresh
     * install works without manual edits.
     */
    private void seedChestBlocks(FileConfiguration config) {
        boolean changed = ensureChestBlock(config, "pg.chestblocks.normal", Material.END_PORTAL_FRAME);
        changed |= ensureChestBlock(config, "pg.chestblocks.shop", Material.COMPOSTER);
        if (!changed) {
            return;
        }
        try {
            config.save(Settings.chestsFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save chest block defaults: " + e.getMessage());
        }
    }

    private boolean ensureChestBlock(FileConfiguration config, String key, Material fallback) {
        Object configured = config.get(key);
        if (configured instanceof String name && !name.isBlank()) {
            return false;
        }
        if (configured instanceof List<?> names && !names.isEmpty()) {
            return false;
        }
        config.set(key, List.of(fallback.name()));
        return true;
    }

    /**
     * Seed the legacy special chest types. A fresh install ships an empty
     * {@code pg.customchests} section, which made every chest block configured
     * in the README unreachable. Existing entries are never overwritten, so
     * admin customisations survive.
     */
    private void seedCustomChests(FileConfiguration config) {
        boolean changed = seedCustomChest(config, 1, false, Material.COMMAND_BLOCK,
                Map.of(1, exampleChestItem()));
        changed |= seedCustomChest(config, 2, true, Material.TARGET, targetChestItems());
        changed |= seedCustomChest(config, 3, true, Material.NETHERITE_BLOCK, netheriteChestItems());
        changed |= seedCustomChest(config, 4, true, Material.DRIED_KELP_BLOCK, kelpChestItems());
        if (!changed) {
            return;
        }
        try {
            config.save(Settings.chestsFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save custom chest defaults: " + e.getMessage());
        }
    }

    private boolean seedCustomChest(FileConfiguration config, int chestId, boolean activate, Material chestType,
                                    Map<Integer, ItemStack> items) {
        String base = "pg.customchests." + chestId;
        if (config.contains(base)) {
            return false;
        }
        config.set(base + ".activate", activate);
        config.set(base + ".chesttype", chestType.name());
        config.set(base + ".chestsize", 9);
        int index = 1;
        for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
            config.set(base + "." + index + ".slot", entry.getKey());
            config.set(base + "." + index + ".item", entry.getValue());
            index++;
        }
        return true;
    }

    /** Legacy slot 1 of the disabled command-block example chest. */
    private ItemStack exampleChestItem() {
        ItemStack item = new ItemStack(Material.BOW, 3);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("EXAMPLE").color(NamedTextColor.RED));
            meta.addEnchant(Enchantment.FLAME, 1, true);
            if (meta instanceof Damageable damageable) {
                damageable.setDamage(60);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    /** Target block: arrows plus a flame bow. */
    private Map<Integer, ItemStack> targetChestItems() {
        Map<Integer, ItemStack> items = new LinkedHashMap<>();
        items.put(2, new ItemStack(Material.ARROW, 1));
        items.put(3, new ItemStack(Material.ARROW, 1));
        items.put(5, flameBow());
        items.put(7, new ItemStack(Material.ARROW, 1));
        items.put(8, new ItemStack(Material.ARROW, 1));
        return items;
    }

    private ItemStack flameBow() {
        ItemStack bow = new ItemStack(Material.BOW, 1);
        ItemMeta meta = bow.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("Flame Bow").color(NamedTextColor.DARK_AQUA));
            meta.addEnchant(Enchantment.FLAME, 1, true);
            bow.setItemMeta(meta);
        }
        return bow;
    }

    /** Netherite block: two netherite ingots. */
    private Map<Integer, ItemStack> netheriteChestItems() {
        Map<Integer, ItemStack> items = new LinkedHashMap<>();
        items.put(3, new ItemStack(Material.NETHERITE_INGOT, 1));
        items.put(7, new ItemStack(Material.NETHERITE_INGOT, 1));
        return items;
    }

    /** Dried kelp block: shield, diamond sword and a full diamond armour set. */
    private Map<Integer, ItemStack> kelpChestItems() {
        Map<Integer, ItemStack> items = new LinkedHashMap<>();
        items.put(2, new ItemStack(Material.SHIELD, 1));
        items.put(3, new ItemStack(Material.DIAMOND_SWORD, 1));
        items.put(5, new ItemStack(Material.DIAMOND_HELMET, 1));
        items.put(6, new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
        items.put(7, new ItemStack(Material.DIAMOND_LEGGINGS, 1));
        items.put(8, new ItemStack(Material.DIAMOND_BOOTS, 1));
        return items;
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

        mgr.addWeapon(1, new ItemStack(Material.FISHING_ROD, 1));
        mgr.addWeapon(1, new ItemStack(Material.BOW, 1));
        mgr.addWeapon(1, new ItemStack(Material.ARROW, 5));
        mgr.addWeapon(1, new ItemStack(Material.SPECTRAL_ARROW, 1));
        mgr.addWeapon(1, new ItemStack(Material.SHIELD, 1));
        mgr.addWeapon(1, new ItemStack(Material.FLINT_AND_STEEL, 1));
        mgr.addWeapon(1, new ItemStack(Material.COBWEB, 3));
        mgr.addWeapon(1, new ItemStack(Material.TNT, 1));
        mgr.addWeapon(1, new ItemStack(Material.WOODEN_SWORD, 1));
        mgr.addWeapon(1, new ItemStack(Material.STONE_SWORD, 1));
        mgr.addWeapon(1, new ItemStack(Material.WOODEN_AXE, 1));
        mgr.addWeapon(1, new ItemStack(Material.STONE_AXE, 1));

        mgr.addWeapon(2, new ItemStack(Material.GOLDEN_SWORD, 1));
        mgr.addWeapon(2, new ItemStack(Material.IRON_SWORD, 1));
        mgr.addWeapon(2, new ItemStack(Material.GOLDEN_AXE, 1));
        mgr.addWeapon(2, new ItemStack(Material.IRON_AXE, 1));
        mgr.addWeapon(2, new ItemStack(Material.DIAMOND_SWORD, 1));
        mgr.addWeapon(2, new ItemStack(Material.DIAMOND_AXE, 1));
        mgr.addWeapon(2, new ItemStack(Material.COMPASS, 1));
        mgr.addWeapon(2, new ItemStack(Material.REDSTONE_TORCH, 1));
    }
}
