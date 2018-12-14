package com.gwu.cs6431.server.service.handler;

import com.gwu.cs6431.server.service.exception.MessageNotCompletedException;
import com.gwu.cs6431.server.service.io.Courier;
import com.gwu.cs6431.server.service.message.Message;
import com.gwu.cs6431.server.service.session.Session;
import com.gwu.cs6431.server.service.user.User;
import com.gwu.cs6431.server.service.user.UserMaintenance;

import java.io.IOException;

/**
 * Handles RSP messages.
 * The RSP message must origin from the user who gets an INVT message in advance,
 * and the status of this RSP message must either be Accepted or refused.
 * <p>
 * If the RSP is Accepted, this handler generates a RSP with the status of Successful
 * which contains a sessionId assigned for both users.
 * If in this case, one of these two users gets offline, the handler would initiate
 * a CLOSE message and send it to the user who is still online.
 * <p>
 * If the RSP is Refused, this handler forward this RSP to the source user.
 *
 * @author qijiuzhi
 */
public class RspHandler implements Handler {
    private Message response = new Message(Message.StartLine.RSP);

    RspHandler() {
    }

    @Override
    public void handle(Message msg) {
        if (msg.getStatus() == null) {
            return;
        }

        // assemble the response message
        response.setSourceUser(msg.getSourceUser());
        response.setTargetUser(msg.getTargetUser());


        // get both users in order to respond to them
        User sourceUser = UserMaintenance.getInstance().getOnlineUser(msg.getSourceUser());
        User targetUser = UserMaintenance.getInstance().getOnlineUser(msg.getTargetUser());

        // If the target user accepts the invitation
        if (Message.Status.Accepted.equals(msg.getStatus())) {


            // if both users still online
            if (sourceUser != null && targetUser != null) {
                Courier srcUsrCourier = sourceUser.getCourier();
                Courier tgtUsrCourier = targetUser.getCourier();

                // create new session for them
                Session session = Session.createSession(sourceUser, targetUser);
                String newSessionId = session.getSessionId();
                response.setSessionID(newSessionId);

                // add successful status
                response.setStatus(Message.Status.Successful);


                // try to send response to source and target users if they are still online
                try {
                    srcUsrCourier.send(response);
                    tgtUsrCourier.send(response);

                } catch (IOException | MessageNotCompletedException e) {

                    if (sourceUser.getSocket().isClosed()) {
                        //  if source user is closed, set user offline
                        UserMaintenance.getInstance().logout(sourceUser);
                    } else {
                        // else send a failure and close msg
                        sendRspMsg(sourceUser, Message.Status.Failed);
                    }

                    if (targetUser.getSocket().isClosed()) {
                        UserMaintenance.getInstance().logout(targetUser);
                    } else {
                        sendRspMsg(targetUser, Message.Status.Failed);
                    }

                    sendCloseMsg(session);

                    // If something goes wrong, close this session.
                    session.close();
                    e.printStackTrace();
                }

            } else {

                if (sourceUser != null && sourceUser.isOnline()) {
                    sendRspMsg(sourceUser, Message.Status.Failed);
                }

                if (targetUser != null && targetUser.isOnline()) {
                    sendRspMsg(targetUser, Message.Status.Failed);
                }
            }
        } else {
            if (Message.Status.Refused.equals(msg.getStatus())) {
                sendRspMsg(sourceUser, msg);
            }
        }
    }

    private void sendRspMsg(User user, Message.Status status) {
        response.setStatus(status);
        try {
            user.getCourier().send(response);
        } catch (IOException e) {
            UserMaintenance.getInstance().logout(user);
        } catch (MessageNotCompletedException e) {
            e.printStackTrace();
        }
    }

    private void sendRspMsg(User user, Message message) {
        try {
            user.getCourier().send(message);
        } catch (IOException e) {
            UserMaintenance.getInstance().logout(user);
        } catch (MessageNotCompletedException e) {
            e.printStackTrace();
        }
    }

    private void sendCloseMsg(Session session) {
        CloseHandler closeHandler = new CloseHandler();
        closeHandler.informClose(session);
        session.close();
    }

}
