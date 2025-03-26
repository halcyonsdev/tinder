package com.halcyon.tinder.userservice.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.halcyon.tinder.userservice.model.support.Gender;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileDto {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("gender")
    private Gender gender;

    @JsonProperty("bio")
    private String bio;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("interests")
    private List<String> interests;

    @JsonProperty("preferences")
    private UserPreferencesDto preferences;
}
