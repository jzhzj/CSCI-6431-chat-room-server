package com.gwu.cs6431.server.service.handler;

import com.gwu.cs6431.server.service.exception.MessageNotCompletedException;
import com.gwu.cs6431.server.service.io.Courier;
import com.gwu.cs6431.server.service.message.Message;
import com.gwu.cs6431.server.service.user.UserMaintenance;

import java.io.IOException;
import java.net.Socket;

/**
 * Handles registration requests.
 * If the user id is not occupied by others, then return a Successful REG message.
 * Otherwise, return a Failed REG message.
 *
 * @author qijiuzhi
 */
public class RegHandler implements Handler {
    private Socket socket;
    private Message msg;

    RegHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void handle(Message msg) {
        this.msg = msg;
        boolean flag = UserMaintenance.getInstance().register(msg.getUserID(), msg.getPasswd());
        feedback(flag);
    }

    private void feedback(boolean successful) {
        Message feedback = new Message(Message.StartLine.REG);
        feedback.setUserID(msg.getUserID());
        try {
            if (successful) {
                feedback.setStatus(Message.Status.Successful);
                new Courier(socket).send(feedback);
            } else {
                feedback.setStatus(Message.Status.Failed);
                feedback.setTxt("The user id has been used by others, please pick another one");
                new Courier(socket).send(feedback);
            }
        } catch (IOException | MessageNotCompletedException e) {
            e.printStackTrace();
        }
    }
}
