package com.halcyon.tinder.userservice.mapper;

import com.halcyon.tinder.userservice.dto.CreateUserRequest;
import com.halcyon.tinder.userservice.dto.UserProfileDto;
import com.halcyon.tinder.userservice.dto.UserPutRequest;
import com.halcyon.tinder.userservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toEntity(CreateUserRequest createUserRequest);

    UserProfileDto toProfile(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateUserFromPutRequest(UserPutRequest userPutRequest, @MappingTarget User user);
}
