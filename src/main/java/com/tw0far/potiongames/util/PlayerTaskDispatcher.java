package com.tw0far.potiongames.util;

import java.util.function.Consumer;

/** Dispatches player-affine work through a supplied entity scheduler. */
public final class PlayerTaskDispatcher {
    private PlayerTaskDispatcher() { }

    public static void dispatch(Consumer<Runnable> entityScheduler, Runnable task) {
        entityScheduler.accept(task);
    }
}
