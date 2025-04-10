package com.halcyon.tinder.authservice.exception;

import com.halcyon.tinder.exceptioncore.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidTokenException extends ApiException {

    public InvalidTokenException(String message) {
        super(message);
    }
}
