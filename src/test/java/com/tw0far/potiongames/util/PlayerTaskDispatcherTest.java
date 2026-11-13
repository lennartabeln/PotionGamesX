package com.tw0far.potiongames.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class PlayerTaskDispatcherTest {
    @Test
    void defersSetupActionUntilEntitySchedulerRunsIt() {
        AtomicBoolean executed = new AtomicBoolean();
        AtomicReference<Runnable> queuedTask = new AtomicReference<>();

        PlayerTaskDispatcher.dispatch(queuedTask::set, () -> executed.set(true));

        assertFalse(executed.get());
        assertNotNull(queuedTask.get());
        queuedTask.get().run();
        assertTrue(executed.get());
    }
}
