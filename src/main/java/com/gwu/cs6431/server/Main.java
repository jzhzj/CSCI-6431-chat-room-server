package com.gwu.cs6431.server;

import com.gwu.cs6431.server.service.io.Listener;
import com.gwu.cs6431.server.service.threadPool.ThreadPool;

public class Main {
    public static void main(String[] args) {
        ThreadPool.THREAD_POOL.execute(Listener.getInstance());
    }
}
