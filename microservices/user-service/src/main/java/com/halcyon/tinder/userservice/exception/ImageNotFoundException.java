package com.halcyon.tinder.userservice.exception;

import com.halcyon.tinder.exceptioncore.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ImageNotFoundException extends ApiException {

    public ImageNotFoundException(String message) {
        super(message);
    }
}
