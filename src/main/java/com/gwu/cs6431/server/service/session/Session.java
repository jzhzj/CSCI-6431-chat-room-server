package com.gwu.cs6431.server.service.session;

import com.gwu.cs6431.server.service.exception.MessageNotCompletedException;
import com.gwu.cs6431.server.service.user.User;
import com.gwu.cs6431.server.service.message.Message;
import com.gwu.cs6431.server.service.user.UserMaintenance;

import java.io.IOException;
import java.util.*;

/**
 * This class stands for the sessions shared by users.
 * Each session can only support 2 users.
 * This class also maintains a map of all living sessions.
 *
 * @author qijiuzhi
 */
public class Session {
    private static Map<String, Session> sessionMap = new HashMap<>();
    private static int count = 0;
    private static final String ID_PREFIX = "S";

    private String sessionId;
    private User user1;
    private User user2;

    private Session(String sessionId, User user1, User user2) {
        this.sessionId = sessionId;
        this.user1 = user1;
        this.user2 = user2;
    }


    //------------------------Static Method-------------------------------------


    public static boolean contains(String sessionId) {
        return sessionMap.containsKey(sessionId);
    }

    public static Session createSession(User u1, User u2) {
        String sessionId = genId();
        Session session = new Session(sessionId, u1, u2);
        sessionMap.put(sessionId, session);
        return session;
    }

    public static void removeSession(String sessionId) {
        sessionMap.remove(sessionId);
    }

    public static void removeSession(Session session) {
        removeSession(session.getSessionId());
    }

    public static void removeAllSessionsOf(User user) {
        removeSession(user.getUserID());
    }

    public static void removeAllSessionsOf(String userId) {
        for (String id : sessionMap.keySet()) {
            if (sessionMap.get(id).havingUser(userId)) {
                // close this session
                removeSession(id);
            }
        }
    }

    public static Session getSession(String sessionId) {
        return sessionMap.get(sessionId);
    }

    public static Set<Session> sessionSetOf(User user) {
        Set<Session> set = new HashSet<>();
        for (String sessionId : sessionMap.keySet()) {
            if (getSession(sessionId).havingUser(user)) {
                set.add(getSession(sessionId));
            }
        }
        return set;
    }

    private static String genId() {
        count++;
        String str = new String(ID_PREFIX);
        str += String.format("%05d", count);
        return str;
    }


    //----------------------Member Method---------------------------------------


    public void forward(Message msg) {
        if (msg.getSessionID().equals(sessionId)) {

            if (msg.getTargetUser().equals(user1.getUserID())) {
                // TODO
                send(user1, msg);
            } else if (msg.getTargetUser().equals(user2.getUserID())) {
                // TODO
                send(user2, msg);
            }
        }
    }

    private void send(User user, Message msg) {
        try {
            user.getCourier().send(msg);
        } catch (IOException e) {
            if (user.getSocket().isClosed()) {
                UserMaintenance.getInstance().logout(user);
            }
        } catch (MessageNotCompletedException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        removeSession(this);
    }

    private boolean havingUser(User user) {
        return havingUser(user.getUserID());
    }

    private boolean havingUser(String userId) {
        return user1.getUserID().equals(userId) || user2.getUserID().equals(userId);
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public User getUser1() {
        return user1;
    }

    public User getUser2() {
        return user2;
    }


    //----------------------Override Method---------------------------------------


    @Override
    public int hashCode() {
        return user1.hashCode() + user2.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Session) {
            Session anotherSession = (Session) obj;
            return havingUser(anotherSession.getUser1()) && havingUser(anotherSession.getUser2());
        }
        return false;
    }
}
