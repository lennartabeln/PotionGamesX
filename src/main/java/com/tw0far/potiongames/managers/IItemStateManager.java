package com.tw0far.potiongames.managers;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import java.util.*;

/**
 * Manager for loot and shop item state.
 * Consolidates 12 ArrayLists and HashMaps for loot/shop configuration.
 *
 * Manages:
 * - Loot items: food1, food2, armour1-5, weapons1-2, potions
 * - Shop items: shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale
 */
public interface IItemStateManager extends IManager {

    // ===== LOOT ITEMS =====
    /**
     * Get random food item from loot pool 1
     */
    ItemStack getRandomFood1();

    /**
     * Get random food item from loot pool 2
     */
    ItemStack getRandomFood2();

    /**
     * Add food to loot pool
     */
    void addFood(int poolId, ItemStack item);

    /**
     * Get all food items in pool
     */
    Collection<ItemStack> getFoods(int poolId);

    /**
     * Clear food loot
     */
    void clearFood();

    // ===== ARMOR LOOT =====
    /**
     * Get random armor from pool (1-5)
     */
    ItemStack getRandomArmor(int poolId);

    /**
     * Add armor to loot pool
     */
    void addArmor(int poolId, ItemStack item);

    /**
     * Get all armor in pool
     */
    Collection<ItemStack> getArmors(int poolId);

    /**
     * Clear all armor loot
     */
    void clearArmor();

    // ===== WEAPON LOOT =====
    /**
     * Get random weapon from pool (1-2)
     */
    ItemStack getRandomWeapon(int poolId);

    /**
     * Add weapon to loot pool
     */
    void addWeapon(int poolId, ItemStack item);

    /**
     * Get all weapons in pool
     */
    Collection<ItemStack> getWeapons(int poolId);

    /**
     * Clear all weapon loot
     */
    void clearWeapons();

    // ===== POTION LOOT =====
    /**
     * Get random potion effect
     */
    PotionEffect getRandomPotion();

    /**
     * Add potion to loot pool
     */
    void addPotion(PotionEffect effect);

    /**
     * Get all potions
     */
    Collection<PotionEffect> getPotions();

    /**
     * Clear all potions
     */
    void clearPotions();

    // ===== SHOP ITEMS =====
    /**
     * Get shop item names
     */
    Collection<String> getShopItems();

    /**
     * Add shop item
     */
    void addShopItem(String itemName, PotionEffect effect, ItemStack itemStack, String kitName, int cost, int sale);

    /**
     * Get potion effect for shop item
     */
    PotionEffect getShopPotion(int index);

    /**
     * Get item type for shop item
     */
    ItemStack getShopPotionType(int index);

    /**
     * Get kit name for shop item
     */
    String getShopKit(int index);

    /**
     * Get cost for shop item
     */
    int getShopCost(int index);

    /**
     * Get sale price for shop item
     */
    int getShopSale(int index);

    /**
     * Get all shop items count
     */
    int getShopItemCount();

    /**
     * Clear all shop items
     */
    void clearShop();

    // ===== BATCH REPLACE (for bootstrap seeding) =====
    /**
     * Replace all loot pools from parallel lists
     */
    void replaceLoot(List<ItemStack> food1, List<ItemStack> food2,
                     List<ItemStack> armour1, List<ItemStack> armour2, List<ItemStack> armour3,
                     List<ItemStack> armour4, List<ItemStack> armour5,
                     List<ItemStack> weapons1, List<ItemStack> weapons2,
                     List<PotionEffect> potions);

    /**
     * Replace shop items from parallel lists
     */
    void replaceShop(List<String> shop, List<PotionEffect> shoppotion, List<ItemStack> shoppotiontype,
                     List<String> shopkit, List<Integer> shopcost, List<Integer> shopsale);

    /**
     * Get mutable shop items list
     */
    List<String> getShopItemsRaw();

    /**
     * Get mutable shop cost list
     */
    List<Integer> getShopCostsRaw();

    /**
     * Get mutable shop sale list
     */
    List<Integer> getShopSalesRaw();

    /**
     * Get mutable shop potion list
     */
    List<PotionEffect> getShopPotionsRaw();

    /**
     * Get mutable shop potion type list
     */
    List<ItemStack> getShopPotionTypesRaw();

    /**
     * Get mutable shop kit list
     */
    List<String> getShopKitsRaw();

    // ===== KITS =====
    List<String> getKitsRaw();
    void replaceKits(List<String> kits);
    Map<String, Integer> getKitplayersRaw();
    void replaceKitplayers(Map<String, Integer> kitplayers);
    void clearKits();

    // ===== BATCH OPERATIONS =====
    /**
     * Clear all loot and shop items
     */
    void clearAll();
}
