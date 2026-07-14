package com.androidnexus.ui.utils;

import javafx.concurrent.Task;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Centered worker pool scheduler to execute device/file/app controller operations
 * on background threads, keeping the JavaFX Application Thread completely responsive.
 */
public class UiThreadExecutor {

    private static final ExecutorService executor = Executors.newFixedThreadPool(4, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true); // Daemon threads permit the JVM to exit gracefully
        return t;
    });

    /**
     * Submits a JavaFX Task to the thread pool for asynchronous execution.
     *
     * @param task the JavaFX Task containing background logic and thread state hooks
     */
    public static <V> void runInBackground(Task<V> task) {
        if (task == null) {
            return;
        }
        executor.submit(task);
    }
}
