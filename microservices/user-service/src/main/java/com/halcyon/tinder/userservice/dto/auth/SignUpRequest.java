package com.halcyon.tinder.userservice.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
}