package com.tw0far.potiongames.util;

/**
 * Horizontal safe zone around a lobby spawn.
 *
 * <p>Lobby and arena can share one world, so "safe" cannot be decided by the
 * world alone. A lobby declares a radius around its spawn ({@code safeRadius});
 * everyone inside it neither takes damage, gets hungry nor can be hit. Players
 * who are actually playing a round are never covered by the zone, so a map that
 * overlaps the radius still fights normally.</p>
 */
public final class SafeZone {

    private SafeZone() {
    }

    /**
     * @param dx     east/west offset from the zone centre
     * @param dz     north/south offset from the zone centre
     * @param radius configured radius, {@code 0} or less disables the zone
     * @return true when the point lies inside the cylinder (height is ignored)
     */
    public static boolean within(double dx, double dz, double radius) {
        if (radius <= 0) {
            return false;
        }
        return dx * dx + dz * dz <= radius * radius;
    }
}
