package com.halcyon.tinder.exceptioncore

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class ErrorDetailsResponse(
    @JsonProperty("timestamp")
    val timestamp: Instant = Instant.now(),

    @JsonProperty("status")
    val status: Int,

    @JsonProperty("error")
    val error: String,

    @JsonProperty("message")
    val message: String
) {
    companion object {
        @JvmStatic
        fun builder() = Builder()
    }

    class Builder {
        private var timestamp: Instant = Instant.now()
        private var status: Int = 0
        private var error: String = ""
        private var message: String = ""

        fun timestamp(timestamp: Instant) = apply { this.timestamp = timestamp }
        fun status(status: Int) = apply { this.status = status }
        fun error(error: String) = apply { this.error = error }
        fun message(message: String) = apply { this.message = message }
        fun build() = ErrorDetailsResponse(timestamp, status, error, message)
    }
}