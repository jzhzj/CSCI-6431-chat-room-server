package com.gwu.cs6431.server.service.threadPool;

import java.util.concurrent.*;

/**
 * Provides thread pool executor.
 *
 * @author qijiuzhi
 */
public class ThreadPool {
    public static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(0, 100,
            0, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());

    public static void shutDown() {
        THREAD_POOL.shutdown();
    }
}
