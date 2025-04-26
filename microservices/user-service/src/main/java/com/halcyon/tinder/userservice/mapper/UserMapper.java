package com.halcyon.tinder.userservice.mapper;

import com.halcyon.tinder.userservice.dto.CreateUserRequest;
import com.halcyon.tinder.userservice.dto.UserGeolocationDto;
import com.halcyon.tinder.userservice.dto.UserProfileDto;
import com.halcyon.tinder.userservice.dto.UserPutRequest;
import com.halcyon.tinder.userservice.model.User;
import com.halcyon.tinder.userservice.model.UserGeolocation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toEntity(CreateUserRequest createUserRequest);

    @Mapping(target = "geolocation", source = "geolocation")
    UserProfileDto toProfile(User user);

    @Mapping(target = "longitude", source = "location.x")
    @Mapping(target = "latitude", source = "location.y")
    UserGeolocationDto toGeolocationDto(UserGeolocation userGeolocation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateUserFromPutRequest(UserPutRequest userPutRequest, @MappingTarget User user);
}
