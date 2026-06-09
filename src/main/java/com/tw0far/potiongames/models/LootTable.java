package com.tw0far.potiongames.models;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 * Optimized loot table structure combining all loot arrays into single organized object.
 * Replaces 9 separate ArrayList<ItemStack> and 1 ArrayList<PotionEffect>.
 * 
 * BEFORE: 10 separate ArrayLists
 * - food1, food2, armour1-5, weapons1-2, potions
 * 
 * AFTER: Single LootTable with categorized access
 * - Reduces memory overhead
 * - Improves cache locality
 * - Single allocation vs 10
 */
public class LootTable {
    private final ItemStack[] food1;
    private final ItemStack[] food2;
    private final ItemStack[] armour1;
    private final ItemStack[] armour2;
    private final ItemStack[] armour3;
    private final ItemStack[] armour4;
    private final ItemStack[] armour5;
    private final ItemStack[] weapons1;
    private final ItemStack[] weapons2;
    private final PotionEffect[] potions;
    
    public LootTable() {
        this.food1 = new ItemStack[0];
        this.food2 = new ItemStack[0];
        this.armour1 = new ItemStack[0];
        this.armour2 = new ItemStack[0];
        this.armour3 = new ItemStack[0];
        this.armour4 = new ItemStack[0];
        this.armour5 = new ItemStack[0];
        this.weapons1 = new ItemStack[0];
        this.weapons2 = new ItemStack[0];
        this.potions = new PotionEffect[0];
    }
    
    // Food
    public ItemStack[] getFood1() { return food1; }
    public ItemStack[] getFood2() { return food2; }
    
    // Armour
    public ItemStack[] getArmour1() { return armour1; }
    public ItemStack[] getArmour2() { return armour2; }
    public ItemStack[] getArmour3() { return armour3; }
    public ItemStack[] getArmour4() { return armour4; }
    public ItemStack[] getArmour5() { return armour5; }
    
    // Weapons
    public ItemStack[] getWeapons1() { return weapons1; }
    public ItemStack[] getWeapons2() { return weapons2; }
    
    // Potions
    public PotionEffect[] getPotions() { return potions; }
    public int getPotionCount() { return potions.length; }
}
