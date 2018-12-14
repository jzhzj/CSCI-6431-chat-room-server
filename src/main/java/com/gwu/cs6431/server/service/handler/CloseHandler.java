package com.gwu.cs6431.server.service.handler;

import com.gwu.cs6431.server.service.exception.MessageNotCompletedException;
import com.gwu.cs6431.server.service.session.Session;
import com.gwu.cs6431.server.service.message.Message;
import com.gwu.cs6431.server.service.user.User;
import com.gwu.cs6431.server.service.user.UserMaintenance;

import java.io.IOException;
import java.util.Set;

/**
 * This is a Close handler which is used to handle the close session requests from users.
 *
 * @author qijiuzhi
 */
public class CloseHandler implements Handler {

    CloseHandler() {
    }

    @Override
    public void handle(Message msg) {

        User targetUser = UserMaintenance.getInstance().getUser(msg.getTargetUser());

        send(targetUser, msg);

        Session.removeSession(msg.getSessionID());
    }

    public void informClose(Session session) {
        informCloseExcept(null, session);
    }

    private void informCloseExcept(User user, Session session) {
        User user1 = session.getUser1();
        User user2 = session.getUser2();

        // generate the close message
        Message closeMsg = new Message(Message.StartLine.CLOSE);
        closeMsg.setSourceUser(user1);
        closeMsg.setTargetUser(user2);
        closeMsg.setSessionID(session);

        // send close message to user1
        if (!user1.equals(user)) {
            send(user1, closeMsg);
        }

        // send close message to user2
        if (!user2.equals(user)) {
            send(user2, closeMsg);
        }
    }

    public void informClose(Set<Session> sessions) {
        informCloseExcept(null, sessions);
    }

    public void informCloseExcept(User user, Set<Session> sessions) {
        for (Session session : sessions) {
            informCloseExcept(user, sessions);
        }
    }

    private void send(User user, Message msg) {
        try {
            user.getCourier().send(msg);
        } catch (IOException e) {
            UserMaintenance.getInstance().logout(user);
        } catch (MessageNotCompletedException e) {
            e.printStackTrace();
        }
    }
}
