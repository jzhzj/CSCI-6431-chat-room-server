package com.gwu.cs6431.server.service.handler;

import com.gwu.cs6431.server.service.message.Message;
import com.gwu.cs6431.server.service.session.Session;
import com.gwu.cs6431.server.service.user.User;
import com.gwu.cs6431.server.service.user.UserMaintenance;

import java.net.Socket;

/**
 * Handles QUIT messages sent from source users.
 *
 * @author qijiuzhi
 */
public class QuitHandler implements Handler {
    QuitHandler() {
    }

    @Override
    public void handle(Message msg) {

        String sourceUserId = msg.getSourceUser();

        // check if the user id and password are correct
        if (UserMaintenance.getInstance().checkPasswd(sourceUserId, msg.getPasswd())) {

            // close all sessions belong to the source user
            closeSessions(sourceUserId);

            // logout the source user
            UserMaintenance.getInstance().logout(sourceUserId);
        }
    }

    private void closeSessions(String userId) {
        // generate a close handler in order to close all sessions belong to the source user
        CloseHandler handler = (CloseHandler) HandlerFactory.getHandler(Message.StartLine.CLOSE, new Socket());

        // get the source user
        User user = UserMaintenance.getInstance().getUser(userId);

        // since the source user sends the QUIT message, there is no need to inform QUIT message back to them
        handler.informCloseExcept(user, Session.sessionSetOf(user));

        // remove all sessions belongs to the source user
        Session.removeAllSessionsOf(userId);
    }
}
