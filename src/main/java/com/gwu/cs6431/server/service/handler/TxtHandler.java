package com.gwu.cs6431.server.service.handler;

import com.gwu.cs6431.server.service.message.Message;
import com.gwu.cs6431.server.service.session.Session;

/**
 * Handles TXT messages.
 *
 * @author qijiuzhi
 */
public class TxtHandler implements Handler {
    TxtHandler() {
    }

    @Override
    public void handle(Message msg) {
        String sessionId = msg.getSessionID();
        if (Session.contains(sessionId)) {
            Session.getSession(sessionId).forward(msg);
        }
    }
}
