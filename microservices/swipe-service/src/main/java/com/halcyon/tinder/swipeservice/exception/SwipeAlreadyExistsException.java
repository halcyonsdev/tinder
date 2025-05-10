package com.halcyon.tinder.swipeservice.exception;

import com.halcyon.tinder.exceptioncore.ApiException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SwipeAlreadyExistsException extends ApiException {

    public SwipeAlreadyExistsException(@NotNull String message) {
        super(message);
    }
}
