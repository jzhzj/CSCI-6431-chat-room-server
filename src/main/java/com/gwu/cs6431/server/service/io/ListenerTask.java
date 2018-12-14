package com.gwu.cs6431.server.service.io;

import com.gwu.cs6431.server.service.handler.HandlerFactory;
import com.gwu.cs6431.server.service.handler.Handler;
import com.gwu.cs6431.server.service.message.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import static com.gwu.cs6431.server.service.constant.ServerProps.*;

public class ListenerTask implements Runnable {
    private Socket socket;

    ListenerTask(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            StringBuilder sb;
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                sb = new StringBuilder();
                String line;
                int lineNum = 0;
                while (!(line = in.readLine()).equals(EOM) && lineNum < MAX_MSG_LEN) {
                    sb.append(line);
                    sb.append(NEW_LINE);
                    lineNum++;
                }
                sb.append(EOM);
                handle(sb.toString());
            }
        } catch (IOException e) {
            // TODO catch IOException
            e.printStackTrace();
        }
    }

    private void handle(String massageStr) {
        Message msg = Message.genMessage(massageStr);
        if (msg != null) {
            Handler handler = HandlerFactory.getHandler(msg.getStartLine(), socket);
            if (handler != null) {
                handler.handle(msg);
            }
        }
    }
}
