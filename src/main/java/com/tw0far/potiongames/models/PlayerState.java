package com.tw0far.potiongames.models;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;


public class PlayerState {
    private ItemStack[] contents;
    private ItemStack[] armor;
    private double health;
    private int foodLevel;
    private float saturation;
    private int level;
    private float exp;
    private GameMode gameMode;
    private Location location;
    private ArrayList<PotionEffect> potionEffects;
    private boolean allowFlight;
    private boolean isFlying;

    public PlayerState(Player player) {
        ItemStack[] inv = player.getInventory().getContents();
        contents = new ItemStack[inv.length];
        for (int i = 0; i < inv.length; i++) {
            contents[i] = inv[i] == null ? null : inv[i].clone();
        }

        ItemStack[] arm = player.getInventory().getArmorContents();
        armor = new ItemStack[arm.length];
        for (int i = 0; i < arm.length; i++) {
            armor[i] = arm[i] == null ? null : arm[i].clone();
        }

        health = player.getHealth();
        foodLevel = player.getFoodLevel();
        saturation = player.getSaturation();
        level = player.getLevel();
        exp = player.getExp();
        gameMode = player.getGameMode();
        org.bukkit.Location loc = player.getLocation();
        location = loc != null ? loc.clone() : null;
        potionEffects = new ArrayList<>(player.getActivePotionEffects());
        allowFlight = player.getAllowFlight();
        isFlying = player.isFlying();
    }

    public void restore(Player player) {
        if (player == null) return;

        if (contents != null) {                         
            player.getInventory().setContents(copy(contents));
        }

        if (armor != null) {
            player.getInventory().setArmorContents(copy(armor));
        }

        for (PotionEffect e : player.getActivePotionEffects()) {
            player.removePotionEffect(e.getType());
        }

        if (potionEffects != null) {
            for (PotionEffect e : potionEffects) {
                player.addPotionEffect(e);
            }
        }

        if (gameMode != null) {
            player.setGameMode(gameMode);
        }
        player.setHealth(health);
        player.setFoodLevel(foodLevel);
        player.setSaturation(saturation);
        player.setLevel(level);
        player.setExp(exp);
        if (location != null) {
            player.teleport(location);
        }
        player.setAllowFlight(allowFlight);
        player.setFlying(isFlying);
    }

    private ItemStack[] copy(ItemStack[] source) {
        ItemStack[] copy = new ItemStack[source.length];
        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i] == null ? null : source[i].clone();
        }
        return copy;
    }
}
