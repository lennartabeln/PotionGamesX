package com.tw0far.potiongames.config;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fluent API for configuring chest loot drops.
 * 
 * DESIGN:
 * - Chainable builder pattern for easy configuration
 * - Type-safe potion and item definitions
 * - Weighted random selection support
 * - Easy to add/remove items without arrays
 * - Serializable to configuration files
 * 
 * USAGE:
 * new ChestLootBuilder()
 *   .addItem("IRON_SWORD", 30)
 *   .addItem("DIAMOND_CHESTPLATE", 10)
 *   .addPotion("SPEED", 1, 5, 20)
 *   .addFood("COOKED_BEEF", 50)
 *   .build();
 */
public class ChestLootBuilder {
    private static final class WeightedItem {
        ItemStack item;
        double weight;
        
        WeightedItem(ItemStack item, double weight) {
            this.item = item;
            this.weight = weight;
        }
    }
    
    private final List<WeightedItem> items = new ArrayList<>();
    private double totalWeight = 0;
    
    /**
     * Add an item stack with weight
     * Weight determines selection probability relative to other items
     * Higher weight = higher chance of selection
     */
    public ChestLootBuilder addItem(ItemStack item, double weight) {
        if (item != null && weight > 0) {
            items.add(new WeightedItem(item, weight));
            totalWeight += weight;
        }
        return this;
    }
    
    /**
     * Add an item by material name and amount with weight
     */
    public ChestLootBuilder addItem(String material, int amount, double weight) {
        try {
            ItemStack item = new ItemStack(
                org.bukkit.Material.valueOf(material),
                amount
            );
            return addItem(item, weight);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown material: " + material);
        }
    }
    
    /**
     * Add food items
     */
    public ChestLootBuilder addFood(String material, double weight) {
        return addItem(material, 1, weight);
    }
    
    /**
     * Add armor pieces
     */
    public ChestLootBuilder addArmor(String material, double weight) {
        return addItem(material, 1, weight);
    }
    
    /**
     * Add weapons
     */
    public ChestLootBuilder addWeapon(String material, double weight) {
        return addItem(material, 1, weight);
    }
    
    /**
     * Add potion effect as item stack
     * 
     * @param effect Potion effect type (e.g., "SPEED", "STRENGTH")
     * @param amplifier Effect amplifier (0 = Level I)
     * @param duration Duration in ticks
     * @param weight Selection weight
     */
    public ChestLootBuilder addPotion(String effect, int amplifier, int duration, double weight) {
        try {
            PotionEffectType effectType = resolvePotionEffect(effect);
            if (effectType == null) {
                throw new IllegalArgumentException("Unknown potion effect: " + effect);
            }
            
            ItemStack potion = new ItemStack(org.bukkit.Material.POTION, 1);
            PotionMeta meta = (PotionMeta) potion.getItemMeta();
            if (meta != null) {
                meta.addCustomEffect(
                    new PotionEffect(effectType, duration, amplifier),
                    true
                );
                potion.setItemMeta(meta);
            }
            
            return addItem(potion, weight);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create potion: " + e.getMessage());
        }
    }
    
    /**
     * Add throwable potion (splash)
     */
    public ChestLootBuilder addSplashPotion(String effect, int amplifier, int duration, double weight) {
        try {
            PotionEffectType effectType = resolvePotionEffect(effect);
            if (effectType == null) {
                throw new IllegalArgumentException("Unknown potion effect: " + effect);
            }
            
            ItemStack potion = new ItemStack(org.bukkit.Material.SPLASH_POTION, 1);
            PotionMeta meta = (PotionMeta) potion.getItemMeta();
            if (meta != null) {
                meta.addCustomEffect(
                    new PotionEffect(effectType, duration, amplifier),
                    true
                );
                potion.setItemMeta(meta);
            }
            
            return addItem(potion, weight);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create splash potion: " + e.getMessage());
        }
    }
    
    /**
     * Build the loot table as ItemStack array
     * Respects weights but does not guarantee exact proportions
     */
    public ItemStack[] build() {
        if (items.isEmpty()) {
            throw new IllegalStateException("No items configured");
        }
        
        List<ItemStack> result = new ArrayList<>();
        
        // Each item appears proportional to its weight
        for (WeightedItem wi : items) {
            int count = (int) Math.max(1, (wi.weight / totalWeight) * 10);
            for (int i = 0; i < count; i++) {
                // Clone only once to avoid redundant copies
                result.add(wi.item.clone());
            }
        }
        
        return result.toArray(new ItemStack[0]);
    }

    @SuppressWarnings("deprecation")
    private PotionEffectType resolvePotionEffect(String name) {
        if (name == null) return null;
        try {
            // Try namespaced key lookup first (preferred)
            PotionEffectType byKey = PotionEffectType.getByKey(org.bukkit.NamespacedKey.minecraft(name.toLowerCase()));
            if (byKey != null) return byKey;
        } catch (Exception ignored) {
        }
        try {
            // Fallback to deprecated name lookup for compatibility
            return PotionEffectType.getByName(name);
        } catch (Throwable t) {
            return null;
        }
    }
    
    /**
     * Build weighted selection map (for random selection)
     * Returns references to original items - caller should clone before use
     */
    public Map<ItemStack, Double> buildWeightedMap() {
        Map<ItemStack, Double> result = new HashMap<>();
        for (WeightedItem wi : items) {
            // Store original item reference; map used for lookup only, not modification
            result.put(wi.item, wi.weight / totalWeight);
        }
        return result;
    }
    
    /**
     * Select a random item using weighted random selection
     */
    public ItemStack selectRandom(java.util.Random random) {
        if (items.isEmpty()) {
            return null;
        }
        
        double selection = random.nextDouble() * totalWeight;
        double current = 0;
        
        for (WeightedItem wi : items) {
            current += wi.weight;
            if (selection <= current) {
                // Return cloned copy for caller to add to inventory
                return wi.item.clone();
            }
        }
        
        // Return cloned copy of last item
        return items.get(items.size() - 1).item.clone();
    }
    
    /**
     * Get number of item types configured
     */
    public int size() {
        return items.size();
    }
    
    /**
     * Get total weight (for debugging)
     */
    public double getTotalWeight() {
        return totalWeight;
    }
    
    /**
     * Clear all items
     */
    public ChestLootBuilder clear() {
        items.clear();
        totalWeight = 0;
        return this;
    }
}
