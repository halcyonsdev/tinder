package com.halcyon.tinder.authservice.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserGeolocationDto {

    @JsonProperty("longitude")
    @Min(value = -180, message = "Longitude must not be less than -180")
    @Max(value = 180, message = "Longitude must not be more than 180")
    private Double longitude;

    @JsonProperty("latitude")
    @Min(value = -90, message = "Latitude must not be less than -90")
    @Max(value = 90, message = "Latitude must not be more than 90")
    private Double latitude;
}
