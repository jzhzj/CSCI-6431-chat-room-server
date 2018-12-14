package com.gwu.cs6431.server.service.handler;

import com.gwu.cs6431.server.service.exception.MessageNotCompletedException;
import com.gwu.cs6431.server.service.io.Courier;
import com.gwu.cs6431.server.service.message.Message;
import com.gwu.cs6431.server.service.user.UserMaintenance;

import java.io.IOException;
import java.net.Socket;

/**
 * Handles sign-in requests.
 *
 * @author qijiuzhi
 */
public class SignHandler implements Handler {
    private Socket socket;
    private Message msg;

    SignHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void handle(Message msg) {
        this.msg = msg;
        boolean flag = UserMaintenance.getInstance().login(msg.getUserID(), msg.getPasswd(), socket);
        feedback(flag);
    }

    private void feedback(boolean successful) {
        Message feedback = new Message(Message.StartLine.SIGN);
        feedback.setUserID(msg.getUserID());
        try {
            if (successful) {
                feedback.setStatus(Message.Status.Successful);
                new Courier(socket).send(feedback);
            } else {
                feedback.setStatus(Message.Status.Failed);
                feedback.setTxt("Sign in failed. Please check your id and password. Or your account might be signed in by others");
                new Courier(socket).send(feedback);
            }
        } catch (IOException | MessageNotCompletedException e) {
            e.printStackTrace();
        }
    }
}
