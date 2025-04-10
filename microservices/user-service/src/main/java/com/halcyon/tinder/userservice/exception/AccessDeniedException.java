package com.halcyon.tinder.userservice.exception;

import com.halcyon.tinder.exceptioncore.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedException extends ApiException {

    public AccessDeniedException(String message) {
        super(message);
    }
}
