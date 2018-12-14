package com.gwu.cs6431.server.service.io;

import com.gwu.cs6431.server.service.constant.ServerProps;
import com.gwu.cs6431.server.service.threadPool.ThreadPool;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;

public class Listener implements Runnable{
    private static Listener ourInstance = new Listener();

    private Listener() {
    }

    public static Listener getInstance() {
        return ourInstance;
    }

    private ServerSocket ss = null;
    private boolean listening = true;
    private ExecutorService es = ThreadPool.THREAD_POOL;

    @Override
    public void run() {
        listen();
    }

    private void listen() {
        try {
            ss = new ServerSocket(ServerProps.SERVER_PORT);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + ServerProps.SERVER_PORT);
            System.exit(-1);
        }

        while (listening) {
            try {
                ListenerTask job = new ListenerTask(ss.accept());
                es.execute(job);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
