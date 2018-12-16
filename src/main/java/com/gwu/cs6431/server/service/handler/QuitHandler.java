package com.gwu.cs6431.server.service.handler;

import com.gwu.cs6431.server.service.message.Message;
import com.gwu.cs6431.server.service.user.UserMaintenance;


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

        String sourceUserId = msg.getUserID();

        // check if the user id and password are correct
        if (UserMaintenance.getInstance().checkPasswd(sourceUserId, msg.getPasswd())) {
            // logout the source user
            UserMaintenance.getInstance().logout(sourceUserId);
        }
    }
}
