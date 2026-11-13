package com.tw0far.potiongames.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PluginVersionTest {

    @Test
    void developmentBuildsCountUpNumerically() {
        assertTrue(PluginVersion.isNewer("1.0.0-dev.20", "1.0.0-dev.19"));
        assertFalse(PluginVersion.isNewer("1.0.0-dev.9", "1.0.0-dev.20"));
    }

    @Test
    void aReleaseBeatsItsPreReleases() {
        assertTrue(PluginVersion.isNewer("1.0.0", "1.0.0-dev.20"));
        assertFalse(PluginVersion.isNewer("1.0.0-dev.20", "1.0.0"));
    }

    @Test
    void aHigherMajorWinsWhichIsWhyTheLegacyResourceCannotBeTheSource() {
        // 7.0.1 is newer by semver than 1.0.0-dev.20, so the old SpigotMC
        // resource would keep telling every 1.x install to "update" downwards.
        // That is why UpdateChecker reads the GitHub releases instead.
        assertTrue(PluginVersion.isNewer("7.0.1", "1.0.0-dev.20"));
        assertEquals(-1, PluginVersion.compare("1.0.0-dev.20", "7.0.1"));
    }

    @Test
    void equalVersionsDoNotCountAsUpdate() {
        assertEquals(0, PluginVersion.compare("1.0.0-dev.20", "1.0.0-dev.20"));
        assertFalse(PluginVersion.isNewer("1.0.0-dev.20", "1.0.0-dev.20"));
    }

    @Test
    void missingSegmentsAreZero() {
        assertEquals(0, PluginVersion.compare("1.0", "1.0.0"));
        assertTrue(PluginVersion.isNewer("1.0.1", "1.0"));
    }

    @Test
    void qualifiersCompareNumerically() {
        assertTrue(PluginVersion.isNewer("1.0.0-beta.10", "1.0.0-beta.2"));
    }

    @Test
    void nullsSortLowest() {
        assertEquals(0, PluginVersion.compare(null, null));
        assertTrue(PluginVersion.isNewer("1.0.0", null));
        assertFalse(PluginVersion.isNewer(null, "1.0.0"));
    }
}
