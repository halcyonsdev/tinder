package com.halcyon.tinder.userservice.dto;

import com.halcyon.tinder.userservice.model.support.Gender;
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
}
