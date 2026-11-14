package com.tw0far.potiongames.listeners;

import org.bukkit.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The start lock pins the position but still allows turning the head. */
class StartLockTest {

    private static Location at(double x, double y, double z, float yaw, float pitch) {
        return new Location(null, x, y, z, yaw, pitch);
    }

    @Test
    void turningAroundIsNotMovement() {
        assertFalse(StartLockListener.positionChanged(at(10, 64, 10, 0, 0), at(10, 64, 10, 180, -45)));
    }

    @Test
    void walkingIsBlocked() {
        assertTrue(StartLockListener.positionChanged(at(10, 64, 10, 0, 0), at(10.4, 64, 10, 0, 0)));
    }

    @Test
    void jumpingAndFallingAreBlocked() {
        assertTrue(StartLockListener.positionChanged(at(10, 64, 10, 0, 0), at(10, 64.5, 10, 0, 0)));
        assertTrue(StartLockListener.positionChanged(at(10, 64, 10, 0, 0), at(10, 63, 10, 0, 0)));
    }
}
