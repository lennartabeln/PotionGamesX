package com.tw0far.potiongames.util;

/**
 * Turns the {@code pg.activeKits} / {@code pg.activePotions} settings into the
 * number of entries that are actually used.
 *
 * <p>Both settings are plain counts: {@code 1} uses the first entry, {@code 20}
 * uses twenty, {@code 0} uses none. Values above the file size are capped at the
 * entries the file offers, negatives count as zero.</p>
 */
public final class EntryLimits {

    private EntryLimits() {
    }

    /**
     * @param configured the configured number of active entries
     * @param available  how many entries the file actually contains
     * @return the number of entries to use, never negative and never above {@code available}
     */
    public static int activeCount(int configured, int available) {
        if (available <= 0 || configured <= 0) {
            return 0;
        }
        return Math.min(available, configured);
    }
}
