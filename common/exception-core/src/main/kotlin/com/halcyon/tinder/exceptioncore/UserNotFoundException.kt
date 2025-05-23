package com.halcyon.tinder.exceptioncore

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
open class UserNotFoundException(message: String) : ApiException(message)