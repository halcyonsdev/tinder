package com.halcyon.tinder.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidTokenException extends ApiException {

    public InvalidTokenException(String message) {
        super(message);
    }
}
