package com.gwu.cs6431.service.threadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    public static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();
}
