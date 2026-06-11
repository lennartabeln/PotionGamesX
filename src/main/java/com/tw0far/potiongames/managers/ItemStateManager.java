package com.tw0far.potiongames.managers;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import java.util.*;

/**
 * Implementation of IItemStateManager.
 * Consolidates 12 collections for loot and shop items from PotionGamesX:
 * - food1, food2, armour1-5, weapons1-2, potions (loot)
 * - shop, shoppotion, shoppotiontype, shopkit, shopcost, shopsale (shop config)
 */
public class ItemStateManager implements IItemStateManager {

    // Loot pools (food)
    private final List<ItemStack> food1 = new ArrayList<>();
    private final List<ItemStack> food2 = new ArrayList<>();

    // Armor loot pools (1-5)
    private final List<ItemStack> armour1 = new ArrayList<>();
    private final List<ItemStack> armour2 = new ArrayList<>();
    private final List<ItemStack> armour3 = new ArrayList<>();
    private final List<ItemStack> armour4 = new ArrayList<>();
    private final List<ItemStack> armour5 = new ArrayList<>();

    // Weapon loot pools (1-2)
    private final List<ItemStack> weapons1 = new ArrayList<>();
    private final List<ItemStack> weapons2 = new ArrayList<>();

    // Potion loot
    private final List<PotionEffect> potions = new ArrayList<>();

    // Shop items (parallel lists)
    private final List<String> shop = new ArrayList<>();
    private final List<PotionEffect> shoppotion = new ArrayList<>();
    private final List<ItemStack> shoppotiontype = new ArrayList<>();
    private final List<String> shopkit = new ArrayList<>();
    private final List<Integer> shopcost = new ArrayList<>();
    private final List<Integer> shopsale = new ArrayList<>();

    // Kit data
    private final List<String> kits = new ArrayList<>();
    private final Map<String, Integer> kitplayers = new HashMap<>();

    private final Random random = new Random();

    @Override
    public void onEnable() {
        // No initialization needed
    }

    @Override
    public void onDisable() {
        clearAll();
    }


    // ===== LOOT ITEMS (FOOD) =====
    @Override
    public ItemStack getRandomFood1() {
        if (food1.isEmpty()) return null;
        return food1.get(random.nextInt(food1.size()));
    }

    @Override
    public ItemStack getRandomFood2() {
        if (food2.isEmpty()) return null;
        return food2.get(random.nextInt(food2.size()));
    }

    @Override
    public void addFood(int poolId, ItemStack item) {
        if (poolId == 1) {
            food1.add(item);
        } else if (poolId == 2) {
            food2.add(item);
        }
    }

    @Override
    public Collection<ItemStack> getFoods(int poolId) {
        if (poolId == 1) {
            return new ArrayList<>(food1);
        } else if (poolId == 2) {
            return new ArrayList<>(food2);
        }
        return new ArrayList<>();
    }

    @Override
    public void clearFood() {
        food1.clear();
        food2.clear();
    }

    // ===== ARMOR LOOT =====
    @Override
    public ItemStack getRandomArmor(int poolId) {
        List<ItemStack> pool = getArmorPool(poolId);
        if (pool == null || pool.isEmpty()) return null;
        return pool.get(random.nextInt(pool.size()));
    }

    private List<ItemStack> getArmorPool(int poolId) {
        return switch (poolId) {
            case 1 -> armour1;
            case 2 -> armour2;
            case 3 -> armour3;
            case 4 -> armour4;
            case 5 -> armour5;
            default -> null;
        };
    }

    @Override
    public void addArmor(int poolId, ItemStack item) {
        List<ItemStack> pool = getArmorPool(poolId);
        if (pool != null) {
            pool.add(item);
        }
    }

    @Override
    public Collection<ItemStack> getArmors(int poolId) {
        List<ItemStack> pool = getArmorPool(poolId);
        if (pool == null) return new ArrayList<>();
        return new ArrayList<>(pool);
    }

    @Override
    public void clearArmor() {
        armour1.clear();
        armour2.clear();
        armour3.clear();
        armour4.clear();
        armour5.clear();
    }

    // ===== WEAPON LOOT =====
    @Override
    public ItemStack getRandomWeapon(int poolId) {
        List<ItemStack> pool = getWeaponPool(poolId);
        if (pool == null || pool.isEmpty()) return null;
        return pool.get(random.nextInt(pool.size()));
    }

    private List<ItemStack> getWeaponPool(int poolId) {
        return switch (poolId) {
            case 1 -> weapons1;
            case 2 -> weapons2;
            default -> null;
        };
    }

    @Override
    public void addWeapon(int poolId, ItemStack item) {
        List<ItemStack> pool = getWeaponPool(poolId);
        if (pool != null) {
            pool.add(item);
        }
    }

    @Override
    public Collection<ItemStack> getWeapons(int poolId) {
        List<ItemStack> pool = getWeaponPool(poolId);
        if (pool == null) return new ArrayList<>();
        return new ArrayList<>(pool);
    }

    @Override
    public void clearWeapons() {
        weapons1.clear();
        weapons2.clear();
    }

    // ===== POTION LOOT =====
    @Override
    public PotionEffect getRandomPotion() {
        if (potions.isEmpty()) return null;
        return potions.get(random.nextInt(potions.size()));
    }

    @Override
    public void addPotion(PotionEffect effect) {
        potions.add(effect);
    }

    @Override
    public Collection<PotionEffect> getPotions() {
        return new ArrayList<>(potions);
    }

    @Override
    public void clearPotions() {
        potions.clear();
    }

    // ===== SHOP ITEMS =====
    @Override
    public Collection<String> getShopItems() {
        return new ArrayList<>(shop);
    }

    @Override
    public void addShopItem(String itemName, PotionEffect effect, ItemStack itemStack, String kitName, int cost, int sale) {
        shop.add(itemName);
        shoppotion.add(effect);
        shoppotiontype.add(itemStack);
        shopkit.add(kitName);
        shopcost.add(cost);
        shopsale.add(sale);
    }

    @Override
    public PotionEffect getShopPotion(int index) {
        if (index < 0 || index >= shoppotion.size()) return null;
        return shoppotion.get(index);
    }

    @Override
    public ItemStack getShopPotionType(int index) {
        if (index < 0 || index >= shoppotiontype.size()) return null;
        return shoppotiontype.get(index);
    }

    @Override
    public String getShopKit(int index) {
        if (index < 0 || index >= shopkit.size()) return null;
        return shopkit.get(index);
    }

    @Override
    public int getShopCost(int index) {
        if (index < 0 || index >= shopcost.size()) return 0;
        return shopcost.get(index);
    }

    @Override
    public int getShopSale(int index) {
        if (index < 0 || index >= shopsale.size()) return 0;
        return shopsale.get(index);
    }

    @Override
    public int getShopItemCount() {
        return shop.size();
    }

    @Override
    public void clearShop() {
        shop.clear();
        shoppotion.clear();
        shoppotiontype.clear();
        shopkit.clear();
        shopcost.clear();
        shopsale.clear();
    }

    // ===== KITS =====
    @Override
    public List<String> getKitsRaw() { return kits; }
    @Override
    public void replaceKits(List<String> list) {
        kits.clear();
        if (list != null) kits.addAll(list);
    }
    @Override
    public Map<String, Integer> getKitplayersRaw() { return kitplayers; }
    @Override
    public void replaceKitplayers(Map<String, Integer> map) {
        kitplayers.clear();
        if (map != null) kitplayers.putAll(map);
    }
    @Override
    public void clearKits() {
        kits.clear();
        kitplayers.clear();
    }

    // ===== BATCH REPLACE (for bootstrap seeding) =====
    @Override
    public void replaceLoot(List<ItemStack> food1, List<ItemStack> food2,
                            List<ItemStack> armour1, List<ItemStack> armour2, List<ItemStack> armour3,
                            List<ItemStack> armour4, List<ItemStack> armour5,
                            List<ItemStack> weapons1, List<ItemStack> weapons2,
                            List<PotionEffect> potions) {
        replaceList(this.food1, food1);
        replaceList(this.food2, food2);
        replaceList(this.armour1, armour1);
        replaceList(this.armour2, armour2);
        replaceList(this.armour3, armour3);
        replaceList(this.armour4, armour4);
        replaceList(this.armour5, armour5);
        replaceList(this.weapons1, weapons1);
        replaceList(this.weapons2, weapons2);
        replaceList(this.potions, potions);
    }

    @Override
    public void replaceShop(List<String> shop, List<PotionEffect> shoppotion, List<ItemStack> shoppotiontype,
                            List<String> shopkit, List<Integer> shopcost, List<Integer> shopsale) {
        replaceList(this.shop, shop);
        replaceList(this.shoppotion, shoppotion);
        replaceList(this.shoppotiontype, shoppotiontype);
        replaceList(this.shopkit, shopkit);
        replaceList(this.shopcost, shopcost);
        replaceList(this.shopsale, shopsale);
    }

    private <T> void replaceList(List<T> target, List<T> source) {
        target.clear();
        if (source != null) {
            target.addAll(source);
        }
    }

    @Override
    public List<String> getShopItemsRaw() { return shop; }
    @Override
    public List<Integer> getShopCostsRaw() { return shopcost; }
    @Override
    public List<Integer> getShopSalesRaw() { return shopsale; }
    @Override
    public List<PotionEffect> getShopPotionsRaw() { return shoppotion; }
    @Override
    public List<ItemStack> getShopPotionTypesRaw() { return shoppotiontype; }
    @Override
    public List<String> getShopKitsRaw() { return shopkit; }

    // ===== BATCH OPERATIONS =====
    @Override
    public void clearAll() {
        clearFood();
        clearArmor();
        clearWeapons();
        clearPotions();
        clearShop();
        clearKits();
    }
}

