package com.gwu.cs6431.server.service.user;

import com.gwu.cs6431.server.service.exception.MessageNotCompletedException;
import com.gwu.cs6431.server.service.message.Message;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages users.
 *
 * @author qijiuzhi
 */
public class UserMaintenance {
    private static UserMaintenance ourInstance = new UserMaintenance();

    public static UserMaintenance getInstance() {
        return ourInstance;
    }

    private UserMaintenance() {
    }


    private Map<String, User> userMap = new ConcurrentHashMap<>();
    private Set<User> onlineUserSet = new HashSet<>();

    public boolean register(User newUser) {
        return register(newUser.getUserID(), newUser.getPasswd());
    }

    public boolean register(String userId, String passwd) {
        if (contains(userId)) {
            return false;
        }
        userMap.put(userId, new User(userId, passwd));
        System.out.println("Registered a new user: " + userId);
        return true;
    }


    public boolean isOnline(User user) {
        return onlineUserSet.contains(user);
    }

    public boolean isOnline(String userId) {
        for (User user : onlineUserSet) {
            if (userId.equals(user.getUserID())) {
                return true;
            }
        }
        return false;
    }


    public boolean contains(User user) {
        return contains(user.getUserID());
    }

    public boolean contains(String userId) {
        return userMap.containsKey(userId);
    }


    public boolean checkPasswd(String userId, String passwd) {
        User user = userMap.get(userId);
        return user.getPasswd().equals(passwd);
    }

    private void putOnline(String userId, Socket socket) {
        User user = userMap.get(userId);
        user.setOnline(socket);
        onlineUserSet.add(user);
    }

    public boolean login(String userId, String passwd, Socket socket) {

        if (!contains(userId)) {
            // if the user is not registered
            return false;
        } else {
            // if the user has already registered

            // check the password
            if (!checkPasswd(userId, passwd)) {
                return false;
            }

            if (isOnline(userId)) {
                // Send a QUIT message to the user
                Message quitMsg = new Message(Message.StartLine.QUIT);
                quitMsg.setUserID(userId);
                try {
                    getUser(userId).getCourier().send(quitMsg);
                } catch (IOException | MessageNotCompletedException e) {
                    e.printStackTrace();
                }
                // if the user has already login, log them out
                logout(userId);
            }
            // then, log the new connection in
            putOnline(userId, socket);
            return true;
        }
    }

    public void logout(User user) {
        logout(user.getUserID());
    }

    public void logout(String userId) {
        onlineUserSet.removeIf(user -> user.getUserID().equals(userId));
    }


    public User getUser(String userId) {
        return userMap.get(userId);
    }

    public User getOnlineUser(String userId) {
        if (isOnline(userId)) {
            return getUser(userId);
        }
        return null;
    }
}
