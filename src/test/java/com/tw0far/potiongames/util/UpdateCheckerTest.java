package com.tw0far.potiongames.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** The release list is parsed with a linear scan, no backtracking regex. */
class UpdateCheckerTest {

    private static String release(String tag, boolean draft) {
        return """
            {
              "url": "https://api.github.com/repos/x/y/releases/1",
              "assets_url": "https://api.github.com/repos/x/y/releases/1/assets",
              "tag_name": "%s",
              "target_commitish": "main",
              "name": "PotionGamesX-%s",
              "draft": %s,
              "prerelease": true,
              "created_at": "2026-11-13T21:08:14Z"
            }""".formatted(tag, tag, draft);
    }

    private static String body(String... releases) {
        return "[" + String.join(",", releases) + "]";
    }

    @Test
    void picksTheHighestVersionNotTheFirstEntry() {
        String json = body(
            release("1.0.0-dev.3", false),
            release("1.0.0-dev.20", false),
            release("1.0.0-dev.9", false));
        assertEquals("1.0.0-dev.20", UpdateChecker.newestTagIn(json));
    }

    @Test
    void aDevelopmentBuildAlsoSeesTheStableRelease() {
        String json = body(
            release("1.0.0-dev.60", false),
            release("1.0.0", false));
        assertEquals("1.0.0", UpdateChecker.newestTagIn(json, true));
    }

    @Test
    void aStableInstallIgnoresDevelopmentBuilds() {
        String json = body(
            release("0.9.0", false),
            release("1.0.0-dev.60", false));
        assertEquals("0.9.0", UpdateChecker.newestTagIn(json, false));
    }

    @Test
    void aStableInstallWithoutAnyStableReleaseSeesNothing() {
        String json = body(release("1.0.0-dev.60", false));
        assertNull(UpdateChecker.newestTagIn(json, false));
    }

    @Test
    void preReleaseDetectionUsesTheQualifier() {
        org.junit.jupiter.api.Assertions.assertTrue(PluginVersion.isPreRelease("1.0.0-dev.60"));
        org.junit.jupiter.api.Assertions.assertFalse(PluginVersion.isPreRelease("1.0.0"));
        org.junit.jupiter.api.Assertions.assertFalse(PluginVersion.isPreRelease(null));
    }

    @Test
    void skipsDraftReleases() {
        String json = body(
            release("1.0.0-dev.20", false),
            release("1.0.0-dev.21", true));
        assertEquals("1.0.0-dev.20", UpdateChecker.newestTagIn(json));
    }

    @Test
    void draftAsFirstEntryIsStillSkipped() {
        String json = body(release("2.0.0", true), release("1.0.0-dev.25", false));
        assertEquals("1.0.0-dev.25", UpdateChecker.newestTagIn(json));
    }

    @Test
    void emptyListHasNoVersion() {
        assertNull(UpdateChecker.newestTagIn("[]"));
        assertNull(UpdateChecker.newestTagIn(""));
    }

    @Test
    void garbageDoesNotThrow() {
        assertNull(UpdateChecker.newestTagIn("{\"message\":\"Not Found\"}"));
        assertNull(UpdateChecker.newestTagIn("\"tag_name\" ".repeat(10_000)));
    }
}
