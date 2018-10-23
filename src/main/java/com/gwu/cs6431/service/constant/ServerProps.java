package com.gwu.cs6431.service.constant;

import java.util.ResourceBundle;

public class ServerProps {
    static {
        ResourceBundle rb = ResourceBundle.getBundle("server");
        SERVER_PORT = Integer.parseInt(rb.getString("SERVER_PORT"));
        TIME_OUT = Integer.parseInt(rb.getString("TIME_OUT"));
        MAX_MSG_LEN = Integer.parseInt(rb.getString("MAX_MSG_LEN"));
    }

    public static final int SERVER_PORT;
    public static final int TIME_OUT;
    public static final int MAX_MSG_LEN;

    public static final String EOM = "\0";
    public static final String NEW_LINE = "\r\n";
}
