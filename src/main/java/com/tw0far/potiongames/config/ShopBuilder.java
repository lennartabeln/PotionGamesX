package com.tw0far.potiongames.config;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Fluent API for configuring shop items.
 * 
 * DESIGN:
 * - Chainable builder pattern for shop item configuration
 * - Easy to add/remove items without array manipulation
 * - Type-safe configuration
 * - Support for custom item descriptions
 * 
 * USAGE:
 * new ShopBuilder()
 *   .addItem("IRON_SWORD", 50, "Sharp blade")
 *   .addItem("DIAMOND_CHESTPLATE", 200, "Strong armor")
 *   .addPotion("SPEED_POTION", 100, "Run faster")
 *   .build();
 */
public class ShopBuilder {
    private static final class ShopItem {
        ItemStack item;
        int cost;
        String description;
        
        ShopItem(ItemStack item, int cost, String description) {
            this.item = item;
            this.cost = cost;
            this.description = description;
        }
    }
    
    private final List<ShopItem> items = new ArrayList<>();
    
    /**
     * Add an item to the shop
     */
    public ShopBuilder addItem(ItemStack item, int cost, String description) {
        if (item != null && cost > 0) {
            ItemStack itemCopy = item.clone();
            
            // Add description to lore if provided
            if (description != null && !description.isEmpty()) {
                ItemMeta meta = itemCopy.getItemMeta();
                if (meta != null) {
                    List<String> lore = meta.getLore();
                    if (lore == null) {
                        lore = new ArrayList<>();
                    }
                    lore.add("Cost: " + cost);
                    if (description.length() > 0) {
                        lore.add(description);
                    }
                    meta.setLore(lore);
                    itemCopy.setItemMeta(meta);
                }
            }
            
            items.add(new ShopItem(itemCopy, cost, description));
        }
        return this;
    }
    
    /**
     * Add an item by material name
     */
    public ShopBuilder addItem(String material, int amount, int cost, String description) {
        try {
            ItemStack item = new ItemStack(org.bukkit.Material.valueOf(material), amount);
            return addItem(item, cost, description);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown material: " + material);
        }
    }
    
    /**
     * Add a weapon to the shop
     */
    public ShopBuilder addWeapon(String material, int cost, String description) {
        return addItem(material, 1, cost, description);
    }
    
    /**
     * Add armor to the shop
     */
    public ShopBuilder addArmor(String material, int cost, String description) {
        return addItem(material, 1, cost, description);
    }
    
    /**
     * Add food to the shop
     */
    public ShopBuilder addFood(String material, int amount, int cost, String description) {
        return addItem(material, amount, cost, description);
    }
    
    /**
     * Add utility item (compass, torches, etc.)
     */
    public ShopBuilder addUtility(String material, int amount, int cost, String description) {
        return addItem(material, amount, cost, description);
    }
    
    /**
     * Build shop item array
     */
    public ItemStack[] buildItems() {
        return items.stream()
            .map(si -> si.item.clone())
            .toArray(ItemStack[]::new);
    }
    
    /**
     * Build shop costs array
     */
    public int[] buildCosts() {
        return items.stream()
            .mapToInt(si -> si.cost)
            .toArray();
    }
    
    /**
     * Get all shop items with costs
     */
    public List<ShopItemEntry> buildEntries() {
        List<ShopItemEntry> entries = new ArrayList<>();
        for (ShopItem si : items) {
            entries.add(new ShopItemEntry(si.item.clone(), si.cost, si.description));
        }
        return entries;
    }
    
    /**
     * Get number of items configured
     */
    public int size() {
        return items.size();
    }
    
    /**
     * Clear all items
     */
    public ShopBuilder clear() {
        items.clear();
        return this;
    }
    
    /**
     * Shop item entry with metadata
     */
    public static class ShopItemEntry {
        private final ItemStack item;
        private final int cost;
        private final String description;
        
        public ShopItemEntry(ItemStack item, int cost, String description) {
            this.item = item;
            this.cost = cost;
            this.description = description;
        }
        
        public ItemStack getItem() {
            return item;
        }
        
        public int getCost() {
            return cost;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
