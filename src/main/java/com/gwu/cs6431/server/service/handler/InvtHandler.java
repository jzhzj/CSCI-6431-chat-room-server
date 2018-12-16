package com.gwu.cs6431.server.service.handler;

import com.gwu.cs6431.server.service.exception.MessageNotCompletedException;
import com.gwu.cs6431.server.service.user.User;
import com.gwu.cs6431.server.service.user.UserMaintenance;
import com.gwu.cs6431.server.service.io.Courier;
import com.gwu.cs6431.server.service.message.Message;

import java.io.IOException;
import java.net.Socket;

/**
 * Handles INVT messages.
 * When the InvtHandler gets an INVT from a source user,
 * it first check whether there is such user in this system.
 * <p>
 * If there is such a user, check whether they are online.
 * If they are online, forward this INVT message to them.
 * If not, send a RSP message with the status of Failed back to the source user.
 * <p>
 * If there is no such a user, send a RSP message with the status of Failed back to the source user.
 *
 * @author qijiuzhi
 */
public class InvtHandler implements Handler {
    private Courier sourceUserCourier;
    private Message msg;
    private Message respond = new Message(Message.StartLine.RSP);

    InvtHandler(Socket socket) {
        sourceUserCourier = new Courier(socket);
    }

    @Override
    public void handle(Message msg) {
        this.msg = msg;
        respond.setSourceUser(msg.getSourceUser());
        respond.setTargetUser(msg.getTargetUser());


        String targetUserId = msg.getTargetUser();

        // If there is no such target user, respond to source user
        if (!UserMaintenance.getInstance().contains(targetUserId)) {
            feedback("Sorry, there is no user \"" + targetUserId + "\"");
            return;
        }

        // If the target user is found
        User targetUser = UserMaintenance.getInstance().getOnlineUser(targetUserId);
        // If the target user is online
        if (targetUser != null) {
            if (targetUser.getSocket().isClosed()) {
                UserMaintenance.getInstance().logout(targetUser);
                feedback("Sorry, user " + msg.getTargetUser() + " is not online right now");
                return;
            }
            try {
                // send target user this invitation
                targetUser.getCourier().send(msg);
            } catch (IOException | MessageNotCompletedException e) {
                // TODO IOException
                e.printStackTrace();
            }
        } else {
            feedback("Sorry, user " + msg.getTargetUser() + " is not online right now");
        }
    }

    private void feedback(String feedback) {
        // if the feedback is initiated at this stage, and you don't need to wait for target user's response,
        // then the feedback is always a Failed msg.
        respond.setStatus(Message.Status.Failed);
        respond.setTxt(feedback);
        // send msg back
        try {
            sourceUserCourier.send(respond);
        } catch (IOException | MessageNotCompletedException e) {
            e.printStackTrace();
        }
    }
}
