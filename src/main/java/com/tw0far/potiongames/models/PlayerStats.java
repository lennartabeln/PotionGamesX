package com.tw0far.potiongames.models;

import com.tw0far.potiongames.managers.IDatabaseManager;

/**
 * Snapshot of a player's stat sheet.
 *
 * <p>The database getters block, so they are only ever called from the async
 * scheduler; the finished snapshot is then handed to the player's own thread,
 * where the localized components are built and sent.</p>
 */
public record PlayerStats(int wins, int losses, int rounds, int kills, int deaths, double kd) {

    /** Reads all figures in one go; call this off the main thread. */
    public static PlayerStats read(IDatabaseManager database, String uuid) {
        return new PlayerStats(
                database.getWins(uuid),
                database.getLosses(uuid),
                database.getRounds(uuid),
                database.getKills(uuid),
                database.getDeaths(uuid),
                database.getKD(uuid));
    }
}
