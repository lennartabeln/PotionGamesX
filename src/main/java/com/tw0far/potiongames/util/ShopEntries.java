package com.tw0far.potiongames.util;

/**
 * Turns the configured {@code pg.activePotions} value into the number of shop
 * entries that are actually shown and buyable.
 *
 * <p>The setting is a plain count: {@code 1} activates the first entry, {@code 20}
 * activates twenty, {@code 0} activates none. More than the file offers is
 * capped at the available entries; negatives count as zero.
 */
public final class ShopEntries {

    private ShopEntries() {
    }

    /**
     * @param configured the {@code pg.activePotions} value
     * @param available  how many entries {@code shop.yml} actually contains
     * @return the number of entries to show, never negative and never above {@code available}
     */
    public static int activeCount(int configured, int available) {
        if (available <= 0 || configured <= 0) {
            return 0;
        }
        return Math.min(available, configured);
    }
}
