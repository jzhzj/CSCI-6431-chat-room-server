package com.gwu.cs6431.service.io;

import java.net.Socket;

public class ListenerTask implements Runnable {
    private Socket socket;

    public ListenerTask(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

    }
}
