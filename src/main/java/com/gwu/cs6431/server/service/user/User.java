package com.gwu.cs6431.server.service.user;

import com.gwu.cs6431.server.service.io.Courier;

import java.io.IOException;
import java.net.Socket;

public class User {
    private String userID;
    private String passwd;
    private Socket socket;
    private Courier courier;
    private boolean isOnline;

    public User(String userID, String passwd) {
        this.userID = userID;
        this.passwd = passwd;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    private void setSocket(Socket socket) {
        this.socket = socket;
        courier = new Courier(socket);
    }

    private void setCourier(Courier courier) {
        this.courier = courier;
        this.socket = courier.getSocket();
    }


    public String getUserID() {
        return userID;
    }

    public String getPasswd() {
        return passwd;
    }

    public Socket getSocket() {
        return socket;
    }

    public Courier getCourier() {
        return courier;
    }


    void setOnline(Socket socket) {
        this.isOnline = true;
        setSocket(socket);
    }

    void setOnline(Courier courier) {
        this.isOnline = true;
        setCourier(courier);
    }

    void setOffline() {
        this.isOnline = false;
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.socket = null;
        this.courier = null;
    }


    public boolean isOnline() {
        return isOnline;
    }

    public boolean isOffline() {
        return !isOnline();
    }


    @Override
    public int hashCode() {
        return userID.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            return ((User) obj).getUserID().equals(this.getUserID());
        } else {
            return false;
        }
    }
}
