package com.tw0far.potiongames.util;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Locale;

public final class PotionSerialization {

    private PotionSerialization() { }

    public static PotionEffectType getPotionEffectType(String name) {
        if (name == null) return null;
        NamespacedKey key = name.contains(":")
                ? NamespacedKey.fromString(name)
                : NamespacedKey.fromString("minecraft:" + name.toLowerCase(Locale.ROOT));
        if (key == null) return null;
        return Registry.POTION_EFFECT_TYPE.get(key);
    }

    public static PotionEffect deserializePotionEffect(Object raw, String fallbackEffectName) {
        if (raw instanceof PotionEffect pe) {
            return pe;
        }
        if (raw instanceof ConfigurationSection section) {
            String typeName = section.getString("type");
            if (typeName == null) typeName = section.getString("effect");
            if (typeName == null) typeName = fallbackEffectName;
            PotionEffectType type = getPotionEffectType(typeName);
            if (type == null) return null;
            int duration = section.getInt("duration", 600);
            int amplifier = section.getInt("amplifier", 1);
            boolean ambient = section.getBoolean("ambient", true);
            boolean particles = section.getBoolean("particles", section.getBoolean("has-particles", true));
            boolean icon = section.getBoolean("icon", section.getBoolean("has-icon", true));
            return new PotionEffect(type, duration, amplifier, ambient, particles, icon);
        }
        return null;
    }

    public static ItemStack deserializeItemStack(Object raw) {
        if (raw instanceof ItemStack is) {
            return is;
        }
        if (raw instanceof ConfigurationSection section) {
            String typeName = section.getString("type");
            if (typeName == null) return null;
            try {
                Material material = Material.valueOf(typeName.toUpperCase(Locale.ROOT));
                int amount = section.getInt("amount", 1);
                return new ItemStack(material, amount);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }
}
