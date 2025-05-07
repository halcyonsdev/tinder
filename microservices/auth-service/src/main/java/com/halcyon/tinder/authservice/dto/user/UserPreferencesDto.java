package com.halcyon.tinder.authservice.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.halcyon.tinder.authservice.dto.support.Gender;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserPreferencesDto {

    @JsonProperty("gender")
    private Gender gender;

    @JsonProperty("min_age")
    @Min(value = 18, message = "Min age must be at least 18")
    @Max(value = 100, message = "Min age must be at most 100")
    private Integer minAge;

    @JsonProperty("max_age")
    @Min(value = 18, message = "Max age must be at least 18")
    @Max(value = 100, message = "Max age must be at most 100")
    private Integer maxAge;

    @JsonProperty("distance_in_meters")
    @PositiveOrZero(message = "Distance must be positive or zero")
    @Max(value = 100_000, message = "Distance must be at most 100_000 meters (100 km)")
    private Double distanceInMeters;
}
