package com.halcyon.tinder.exceptioncore

import com.fasterxml.jackson.annotation.JsonProperty
import lombok.Builder
import java.time.Instant

@Builder
data class ValidationErrorsResponse (
    @JsonProperty("timestamp")
    val timestamp: Instant = Instant.now(),

    @JsonProperty("status")
    val status: Int,

    @JsonProperty("errors")
    val errors: List<Map<String, String>>
)