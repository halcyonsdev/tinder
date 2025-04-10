package com.halcyon.tinder.authservice.mapper;

import com.halcyon.tinder.authservice.dto.auth.SignUpRequest;
import com.halcyon.tinder.authservice.dto.user.CreateUserRequest;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", expression = "java(passwordEncoder.encode(signUpRequest.getPassword()))")
    CreateUserRequest toCreateUserRequest(SignUpRequest signUpRequest, @Context PasswordEncoder passwordEncoder);
}
