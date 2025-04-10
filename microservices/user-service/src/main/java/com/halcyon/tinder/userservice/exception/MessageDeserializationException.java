package com.halcyon.tinder.userservice.exception;

public class MessageDeserializationException extends RuntimeException {

    public MessageDeserializationException(String message, Exception e) {
        super(message, e);
    }
}
