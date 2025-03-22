package com.halcyon.tinder.userservice.mapper;

import com.halcyon.tinder.userservice.dto.auth.SignUpRequest;
import com.halcyon.tinder.userservice.dto.user.UserProfileDto;
import com.halcyon.tinder.userservice.model.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", expression = "java(passwordEncoder.encode(signUpRequest.getPassword()))")
    User toEntity(SignUpRequest signUpRequest, @Context PasswordEncoder passwordEncoder);

    UserProfileDto toProfile(User user);
}
