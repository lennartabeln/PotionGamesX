package com.tw0far.potiongames.util;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;

/**
 * The random potion effects a loot chest hands out, taken verbatim from the
 * legacy {@code pg.potions} list.
 *
 * <p>Legacy rolled after opening a chest: only when the player had no active
 * effect, it applied a random number (0..3) of effects from this list.</p>
 */
public final class ChestEffects {

    /** Highest number of effects one chest can roll (legacy {@code max = 3}). */
    private static final int MAX_EFFECTS = 3;

    private static final List<PotionEffect> EFFECTS = List.of(
            new PotionEffect(PotionEffectType.SPEED, 40 * 20, 2),
            new PotionEffect(PotionEffectType.SLOWNESS, 40 * 20, 0),
            new PotionEffect(PotionEffectType.STRENGTH, 40 * 20, 0),
            new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 0),
            new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1, 0),
            new PotionEffect(PotionEffectType.JUMP_BOOST, 40 * 20, 3),
            new PotionEffect(PotionEffectType.NAUSEA, 40 * 20, 0),
            new PotionEffect(PotionEffectType.REGENERATION, 20 * 20, 1),
            new PotionEffect(PotionEffectType.RESISTANCE, 40 * 20, 0),
            new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 60 * 20, 0),
            new PotionEffect(PotionEffectType.INVISIBILITY, 40 * 20, 0),
            new PotionEffect(PotionEffectType.BLINDNESS, 20 * 20, 0),
            new PotionEffect(PotionEffectType.HUNGER, 10 * 20, 1),
            new PotionEffect(PotionEffectType.WEAKNESS, 60 * 20, 0),
            new PotionEffect(PotionEffectType.POISON, 10 * 20, 1),
            new PotionEffect(PotionEffectType.WITHER, 10 * 20, 1),
            new PotionEffect(PotionEffectType.ABSORPTION, 60 * 20, 2),
            new PotionEffect(PotionEffectType.GLOWING, 20 * 20, 0),
            new PotionEffect(PotionEffectType.HEALTH_BOOST, 60 * 20, 2),
            new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 60 * 20, 2),
            new PotionEffect(PotionEffectType.SATURATION, 40 * 20, 0),
            new PotionEffect(PotionEffectType.NIGHT_VISION, 60 * 20, 0),
            new PotionEffect(PotionEffectType.WATER_BREATHING, 60 * 20, 2));

    private ChestEffects() {
    }

    /** The legacy effect pool, for tests. */
    public static List<PotionEffect> pool() {
        return EFFECTS;
    }

    /**
     * Applies a random number of effects (0..3) to a player who has none, exactly
     * like the legacy chest interaction.
     *
     * @return how many effects were applied
     */
    public static int applyRandom(Player player, Random random) {
        if (player == null || random == null || !player.getActivePotionEffects().isEmpty()) {
            return 0;
        }
        int count = random.nextInt(MAX_EFFECTS + 1);
        for (int i = 0; i < count; i++) {
            player.addPotionEffect(EFFECTS.get(random.nextInt(EFFECTS.size())));
        }
        return count;
    }
}
