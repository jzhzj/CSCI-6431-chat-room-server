package com.gwu.cs6431.server.service.handler;

import com.gwu.cs6431.server.service.message.Message;

import java.net.Socket;

/**
 * A Factory that produces handlers.
 *
 * @author qijiuzhi
 */
public class HandlerFactory {

    public static Handler getHandler(Message.StartLine startLine, Socket socket) {
        switch (startLine) {
            case INVT:
                return new InvtHandler(socket);
            case REG:
                return new RegHandler(socket);
            case SIGN:
                return new SignHandler(socket);
            case RSP:
                return new RspHandler();
            case CLOSE:
                return new CloseHandler();
            case QUIT:
                return new QuitHandler();
            case TXT:
                return new TxtHandler();
            default:
                return null;
        }
    }
}
