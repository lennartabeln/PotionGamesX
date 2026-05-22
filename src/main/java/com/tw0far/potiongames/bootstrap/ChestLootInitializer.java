package com.tw0far.potiongames.bootstrap;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public final class ChestLootInitializer {
    private final PotionGames plugin;

    public ChestLootInitializer(PotionGames plugin) {
        this.plugin = plugin;
    }

    public void seed() {
        Collections.addAll(plugin.getFood1(),
                new ItemStack(Material.CAKE, 1),
                new ItemStack(Material.BREAD, 3),
                new ItemStack(Material.PUMPKIN_PIE, 3),
                new ItemStack(Material.COOKIE, 3),
                new ItemStack(Material.BAKED_POTATO, 3));

        Collections.addAll(plugin.getFood2(),
                new ItemStack(Material.RABBIT_STEW, 1),
                new ItemStack(Material.MUSHROOM_STEW, 1),
                new ItemStack(Material.BEETROOT_SOUP, 1),
                new ItemStack(Material.GOLDEN_CARROT, 1),
                new ItemStack(Material.MILK_BUCKET, 1));

        Collections.addAll(plugin.getArmour1(),
                new ItemStack(Material.LEATHER_HELMET, 1),
                new ItemStack(Material.LEATHER_CHESTPLATE, 1),
                new ItemStack(Material.LEATHER_LEGGINGS, 1),
                new ItemStack(Material.LEATHER_BOOTS, 1));

        Collections.addAll(plugin.getArmour2(),
                new ItemStack(Material.CHAINMAIL_HELMET, 1),
                new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1),
                new ItemStack(Material.CHAINMAIL_LEGGINGS, 1),
                new ItemStack(Material.CHAINMAIL_BOOTS, 1));

        Collections.addAll(plugin.getArmour3(),
                new ItemStack(Material.GOLDEN_HELMET, 1),
                new ItemStack(Material.GOLDEN_CHESTPLATE, 1),
                new ItemStack(Material.GOLDEN_LEGGINGS, 1),
                new ItemStack(Material.GOLDEN_BOOTS, 1));

        Collections.addAll(plugin.getArmour4(),
                new ItemStack(Material.IRON_HELMET, 1),
                new ItemStack(Material.IRON_CHESTPLATE, 1),
                new ItemStack(Material.IRON_LEGGINGS, 1),
                new ItemStack(Material.IRON_BOOTS, 1));

        Collections.addAll(plugin.getArmour5(),
                new ItemStack(Material.DIAMOND_HELMET, 1),
                new ItemStack(Material.DIAMOND_CHESTPLATE, 1),
                new ItemStack(Material.DIAMOND_LEGGINGS, 1),
                new ItemStack(Material.DIAMOND_BOOTS, 1));

        Collections.addAll(plugin.getWeapons1(),
                new ItemStack(Material.WOODEN_SWORD, 1),
                new ItemStack(Material.STONE_SWORD, 1),
                new ItemStack(Material.WOODEN_AXE, 1));

        Collections.addAll(plugin.getWeapons2(),
                new ItemStack(Material.IRON_SWORD, 1),
                new ItemStack(Material.DIAMOND_SWORD, 1),
                new ItemStack(Material.IRON_AXE, 1));
    }
}
