package com.halcyon.tinder.authservice.exception;

public class MessageSerializationException extends RuntimeException {

    public MessageSerializationException(String message, Exception e) {
        super(message, e);
    }
}
