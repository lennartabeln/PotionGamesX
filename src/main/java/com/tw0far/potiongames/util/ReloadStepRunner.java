package com.tw0far.potiongames.util;

import java.util.function.Consumer;

/** Runs a reload step and reports whether it completed successfully. */
public final class ReloadStepRunner {
    @FunctionalInterface
    public interface ReloadOperation {
        void run() throws Exception;
    }

    private ReloadStepRunner() { }

    public static boolean run(ReloadOperation operation) {
        return run(operation, ignored -> { });
    }

    public static boolean run(ReloadOperation operation, Consumer<Exception> onFailure) {
        try {
            operation.run();
            return true;
        } catch (Exception exception) {
            onFailure.accept(exception);
            return false;
        }
    }
}
