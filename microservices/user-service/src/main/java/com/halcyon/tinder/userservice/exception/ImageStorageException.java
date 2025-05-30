package com.halcyon.tinder.userservice.exception;

import com.halcyon.tinder.exceptioncore.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ImageStorageException extends ApiException {

    public ImageStorageException(String message) {
        super(message);
    }
}
