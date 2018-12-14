package com.gwu.cs6431.server.service.io;

import com.gwu.cs6431.server.service.exception.MessageNotCompletedException;
import com.gwu.cs6431.server.service.message.Message;
import com.gwu.cs6431.server.service.user.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Courier {
    private Socket socket;

    public Courier(Socket socket) {
        this.socket = socket;
    }

    public Courier(User user) {
        this(user.getSocket());
    }

    public Socket getSocket() {
        return socket;
    }

    public void send(Message msg) throws IOException, MessageNotCompletedException{
        if (!msg.isCompleted()) {
            throw new MessageNotCompletedException(msg.getStartLine().toString() + " message is not completed.");
        }
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.print(msg.toString());
    }
}
