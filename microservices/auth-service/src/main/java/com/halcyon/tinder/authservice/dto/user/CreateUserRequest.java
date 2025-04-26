package com.halcyon.tinder.authservice.dto.user;

import com.halcyon.tinder.authservice.dto.support.Gender;
import java.util.List;
import lombok.Data;

@Data
public class CreateUserRequest {

    private String phoneNumber;
    private String firstName;
    private String lastName;
    private Integer age;
    private Gender gender;
    private String password;
    private String bio;
    private List<String> interests;
    private UserPreferencesDto preferences;
    private UserGeolocationDto geolocation;
}
