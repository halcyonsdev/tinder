package com.halcyon.tinder.authservice.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.halcyon.tinder.authservice.dto.support.Gender;
import com.halcyon.tinder.authservice.dto.user.UserPreferencesDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
import lombok.Data;

@Data
public class SignUpRequest {

    @JsonProperty("phone_number")
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\+?\\d{10,15}", message = "Invalid phone number format")
    private String phoneNumber;

    @JsonProperty("first_name")
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    private String firstName;

    @JsonProperty("last_name")
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    private String lastName;

    @JsonProperty("age")
    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 100, message = "Age must be at most 100")
    private Integer age;

    @JsonProperty("gender")
    @NotNull(message = "Gender is required")
    private Gender gender;

    @JsonProperty("password")
    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, and one number")
    private String password;

    @JsonProperty("bio")
    @Size(min = 2, max = 500, message = "Bio must be between 2 and 500 characters")
    private String bio;

    @JsonProperty("interests")
    private List<@Size(min = 2, max = 50, message = "Interest must be between 2 and 50 characters") String> interests;

    @JsonProperty("preferences")
    @Valid
    private UserPreferencesDto preferences;
}
