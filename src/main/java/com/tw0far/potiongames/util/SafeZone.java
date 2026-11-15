package com.tw0far.potiongames.util;

import org.bukkit.Location;

/**
 * Decides whether a player is on the lobby side of a lobby.
 *
 * <p>Lobby and arena usually share one world, so "safe" cannot be derived from
 * the world. The zone is player based: whoever is nearer to the lobby spawn
 * than to the nearest arena spawn of that lobby counts as being in the lobby and
 * gets no damage, no hunger and no PvP. Players in a running round are never
 * treated as lobby players by the callers, so a map that reaches towards the
 * spawn still fights normally.</p>
 */
public final class SafeZone {

    private SafeZone() {
    }

    /** Squared horizontal (x/z) distance between two locations. */
    public static double horizontalDistanceSquared(Location a, Location b) {
        double dx = a.getX() - b.getX();
        double dz = a.getZ() - b.getZ();
        return dx * dx + dz * dz;
    }

    /**
     * @param lobbyDistanceSquared squared distance to the lobby spawn
     * @param nearestArenaSquared  squared distance to the nearest arena spawn,
     *                             negative when the lobby has no arena spawns
     * @return true when the lobby is the nearer one (a lobby without arenas is
     *         lobby everywhere)
     */
    public static boolean isLobbySide(double lobbyDistanceSquared, double nearestArenaSquared) {
        if (nearestArenaSquared < 0) {
            return true;
        }
        return lobbyDistanceSquared < nearestArenaSquared;
    }
}
