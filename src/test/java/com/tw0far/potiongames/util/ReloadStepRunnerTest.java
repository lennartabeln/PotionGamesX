package com.tw0far.potiongames.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ReloadStepRunnerTest {
    @Test
    void returnsTrueWhenReloadStepCompletes() {
        assertTrue(ReloadStepRunner.run(() -> { }));
    }

    @Test
    void returnsFalseWhenReloadStepThrows() {
        assertFalse(ReloadStepRunner.run(() -> {
            throw new IllegalStateException("reload failed");
        }));
    }
}
