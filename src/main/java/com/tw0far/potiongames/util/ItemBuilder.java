package com.tw0far.potiongames.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Fluent builder for creating ItemStacks with metadata.
 */
public class ItemBuilder {
    private final ItemStack item;
    private ItemMeta meta;
    
    public ItemBuilder(ItemStack base) {
        this.item = base.clone();
        this.meta = item.getItemMeta();
        if (this.meta == null) {
            // If no meta exists, create a new one by cloning an item with similar material
            ItemStack temp = new ItemStack(base.getType());
            this.meta = temp.getItemMeta();
        }
    }
    
    public ItemBuilder displayName(String name) {
        if (meta != null) {
            meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));
        }
        return this;
    }
    
    public ItemBuilder displayName(Component component) {
        if (meta != null) {
            meta.displayName(component.decoration(TextDecoration.ITALIC, false));
        }
        return this;
    }
    
    public ItemBuilder lore(List<String> lore) {
        if (meta != null) {
            List<Component> componentLore = new ArrayList<>();
            for (String line : lore) {
                componentLore.add(Component.text(line).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            }
            meta.lore(componentLore);
        }
        return this;
    }
    
    public ItemBuilder addLoreLine(String line) {
        if (meta != null) {
            List<Component> currentLore = meta.lore() != null ? new ArrayList<>(meta.lore()) : new ArrayList<>();
            currentLore.add(Component.text(line).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            meta.lore(currentLore);
        }
        return this;
    }
    
    public ItemBuilder enchant(Enchantment enchantment, int level) {
        if (meta != null) {
            meta.addEnchant(enchantment, level, true);
        }
        return this;
    }
    
    public ItemBuilder flag(ItemFlag flag) {
        if (meta != null) {
            meta.addItemFlags(flag);
        }
        return this;
    }
    
    public ItemBuilder hideAttributes() {
        if (meta != null) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }
        return this;
    }
    
    public ItemStack build() {
        if (meta != null) {
            item.setItemMeta(meta);
        }
        return item;
    }
}
