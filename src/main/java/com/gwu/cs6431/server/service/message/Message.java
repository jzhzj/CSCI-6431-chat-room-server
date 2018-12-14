package com.gwu.cs6431.server.service.message;

import com.gwu.cs6431.server.service.constant.ServerProps;
import com.gwu.cs6431.server.service.exception.CanNotResolveException;
import com.gwu.cs6431.server.service.session.Session;
import com.gwu.cs6431.server.service.user.User;

public class Message {
    private final static String EOM = ServerProps.EOM;
    private final static String NEW_LINE = ServerProps.NEW_LINE;

    private StartLine startLine;
    private HeaderField<Status> status;
    private HeaderField<String> userID;
    private HeaderField<String> passwd;
    private HeaderField<String> sourceUser;
    private HeaderField<String> targetUser;
    private HeaderField<String> sessionID;
    private String txt;

    private Message() {

    }

    public Message(StartLine startLine) {
        setStartLine(startLine);
    }

    private enum Header {
        Status, UserID, Password, SourceUser, TargetUser, SessionID
    }

    private class HeaderField<V> {
        private Header key;
        private V value;

        public HeaderField(Header key, V value) {
            this.key = key;
            this.value = value;
        }

        public Header getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        @Override
        public String toString() {
            return key.toString() + "=" + value.toString();
        }
    }

    public enum StartLine {
        REG, SIGN, INVT, RSP, TXT, CLOSE, QUIT
    }

    public enum Status {
        Successful, Failed, Accepted, Refused
    }

    private void setStartLine(String startLine) throws CanNotResolveException {
        for (StartLine sl : StartLine.values()) {
            if (startLine.equalsIgnoreCase(sl.name())) {
                this.startLine = sl;
                break;
            }
        }
        if (startLine == null) {
            throw new CanNotResolveException("Wrong Start Line!");
        }
    }

    public void setStartLine(StartLine startLine) {
        this.startLine = startLine;
    }

    private void setStatus(String status) throws CanNotResolveException {
        for (Status st : Status.values()) {
            if (status.equalsIgnoreCase(st.name())) {
                this.status = new HeaderField<>(Header.Status, st);
                break;
            }
        }
        if (status == null) {
            throw new CanNotResolveException("Wrong Status!");
        }
    }

    public void setStatus(Status status) {
        this.status = new HeaderField<>(Header.Status, status);
    }

    public void setUserID(String userID) {
        this.userID = new HeaderField<>(Header.UserID, userID);
    }

    public void setPasswd(String passwd) {
        this.passwd = new HeaderField<>(Header.Password, passwd);
    }

    public void setSourceUser(String sourceUser) {
        this.sourceUser = new HeaderField<>(Header.SourceUser, sourceUser);
    }

    public void setSourceUser(User sourceUser) {
        setSourceUser(sourceUser.getUserID());
    }

    public void setTargetUser(String targetUser) {
        this.targetUser = new HeaderField<>(Header.TargetUser, targetUser);
    }

    public void setTargetUser(User targetUser) {
        setTargetUser(targetUser.getUserID());
    }

    public void setSessionID(String sessionID) {
        this.sessionID = new HeaderField<>(Header.SessionID, sessionID);
    }

    public void setSessionID(Session session) {
        setSessionID(session.getSessionId());
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    private void setHeader(String line) throws CanNotResolveException {
        String[] elements = line.split("=");
        if (elements.length != 2) {
            throw new CanNotResolveException("Wrong Header!");
        }
        // Status, UserID, Password, SourceUser, TargetUser, SessionID
        switch (elements[0]) {
            case "Status":
                setStatus(elements[1]);
                break;
            case "UserID":
                setUserID(elements[1]);
                break;
            case "Password":
                setPasswd(elements[1]);
                break;
            case "SourceUser":
                setSourceUser(elements[1]);
                break;
            case "TargetUser":
                setTargetUser(elements[1]);
                break;
            case "SessionID":
                setSessionID(elements[1]);
                break;
            default:
                throw new CanNotResolveException("Wrong Header!");
        }
    }

    public StartLine getStartLine() {
        return startLine;
    }

    public Status getStatus() {
        return status.getValue();
    }

    public String getUserID() {
        return userID.getValue();
    }

    public String getPasswd() {
        return passwd.getValue();
    }

    public String getSourceUser() {
        return sourceUser.getValue();
    }

    public String getTargetUser() {
        return targetUser.getValue();
    }

    public String getSessionID() {
        return sessionID.getValue();
    }

    public String getTxt() {
        return txt;
    }

    public static synchronized Message genMessage(String message) {
        if (message == null) {
            return null;
        }
        Message res = new Message();
        String[] lines = message.split("\\r\\n");
        // set Start line
        try {
            res.setStartLine(lines[0]);
        } catch (CanNotResolveException e) {
            return null;
        }

        // set Header fields
        int i = 1;
        try {
            for (; i < lines.length && !lines[i].equals(""); i++) {
                res.setHeader(lines[i]);
            }

        } catch (CanNotResolveException e) {
            return null;
        }

        // skip the line that only has <CRLF>
        i++;

        // set TXT
        StringBuilder sb = new StringBuilder();
        for (; i < lines.length && !lines[i].equals(EOM); i++) {
            sb.append(lines[i]);
            if (lines[i + 1].equals(EOM)) {
                break;
            }
            sb.append(NEW_LINE);
        }
        res.setTxt(sb.toString());
        return res;
    }

    public boolean isCompleted() {
        switch (startLine) {
            case TXT:
            case CLOSE:
                return this.sourceUser != null && this.targetUser != null && this.sessionID != null;
            case RSP:
                return this.sourceUser != null && this.targetUser != null && this.status != null;
            case INVT:
                return this.sourceUser != null && this.targetUser != null;
            case REG:
            case SIGN:
                return this.userID != null && this.passwd != null && this.status != null;
            case QUIT:
                return this.userID != null && this.passwd != null;
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (startLine != null) {
            sb.append(startLine);
            sb.append(NEW_LINE);
        }
//        Status, UserID, Password, SourceUser, TargetUser, SessionID
        if (status != null) {
            sb.append(status);
            sb.append(NEW_LINE);
        }
        if (userID != null) {
            sb.append(userID);
            sb.append(NEW_LINE);
        }
        if (passwd != null) {
            sb.append(passwd);
            sb.append(NEW_LINE);
        }
        if (sourceUser != null) {
            sb.append(sourceUser);
            sb.append(NEW_LINE);
        }
        if (targetUser != null) {
            sb.append(targetUser);
            sb.append(NEW_LINE);
        }
        if (sessionID != null) {
            sb.append(sessionID);
            sb.append(NEW_LINE);
        }
        sb.append(NEW_LINE);
        if (txt != null && !txt.equals("")) {
            sb.append(txt);
            sb.append(NEW_LINE);
        }
        sb.append(EOM);
        return sb.toString();
    }
}
