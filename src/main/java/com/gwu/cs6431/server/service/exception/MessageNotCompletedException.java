package com.gwu.cs6431.server.service.exception;

public class MessageNotCompletedException extends Exception {
    public MessageNotCompletedException() {
    }

    public MessageNotCompletedException(String message) {
        super(message);
    }
}
