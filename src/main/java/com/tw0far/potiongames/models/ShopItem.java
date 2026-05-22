package com.tw0far.potiongames.models;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class ShopItem {
    private int id;
    private String name;
    private PotionEffect effect;
    private ItemStack itemStack;
    private int price;
    private int kitPrice;
    private Kit kit;

    public ShopItem(int id, String name, PotionEffect effect, ItemStack itemStack, int price, int kitPrice, Kit kit) {
        this.id = id;
        this.name = name;
        this.effect = effect;
        this.itemStack = itemStack;
        this.price = price;
        this.kitPrice = kitPrice;
        this.kit = kit;
    }
}
