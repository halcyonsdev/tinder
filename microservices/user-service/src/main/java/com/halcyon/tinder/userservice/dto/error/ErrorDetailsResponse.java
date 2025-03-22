package com.halcyon.tinder.userservice.dto.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@Data
@AllArgsConstructor
public class ErrorDetailsResponse {

    @JsonProperty("timestamp")
    private Instant timestamp;

    @JsonProperty("status")
    private Integer status;

    @JsonProperty("error")
    private String error;

    @JsonProperty("message")
    private String message;
}