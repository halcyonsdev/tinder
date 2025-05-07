package com.halcyon.tinder.userservice.service;

import com.halcyon.tinder.exceptioncore.UserNotFoundException;
import com.halcyon.tinder.jwtcore.JwtProvider;
import com.halcyon.tinder.rediscache.CacheManager;
import com.halcyon.tinder.userservice.dto.*;
import com.halcyon.tinder.userservice.exception.WrongDataException;
import com.halcyon.tinder.userservice.mapper.UserMapper;
import com.halcyon.tinder.userservice.model.User;
import com.halcyon.tinder.userservice.model.UserGeolocation;
import com.halcyon.tinder.userservice.repository.UserRepository;
import com.halcyon.tinder.userservice.util.PointFactory;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CacheManager cacheManager;
    private final JwtProvider jwtProvider;
    private final PointFactory pointFactory;

    private static final String USER_CACHE_PREFIX = "user-profile:";

    public void create(CreateUserRequest createUserRequest) {
        User user = userMapper.toEntity(createUserRequest);

        if (user.getPreferences() != null) {
            validatePreferences(createUserRequest.getPreferences());
            user.getPreferences().setUser(user);
        }

        var geolocation = createUserRequest.getGeolocation();
        if (geolocation != null) {
            Point location = pointFactory.createPoint(geolocation.getLongitude(), geolocation.getLatitude());
            user.setGeolocation(new UserGeolocation(location, user));
        }

        save(user);
    }

    private void validatePreferences(UserPreferencesDto preferencesDto) {
        if (preferencesDto.getMinAge() != null && preferencesDto.getMaxAge() != null
                && preferencesDto.getMaxAge() < preferencesDto.getMinAge()) {
            throw new WrongDataException("Max age can not be greater than min");
        }
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UserNotFoundException("User with phone number " + phoneNumber + " not found"));
    }

    public UserProfileDto getCurrentUserProfile() {
        User currentUser = getCurrentUser();
        return userMapper.toProfile(currentUser);
    }

    public User getCurrentUser() {
        String phoneNumber = jwtProvider.getJwtAuthentication().getPhoneNumber();
        return findByPhoneNumber(phoneNumber);
    }

    public UserProfileDto getUserProfileById(UUID userId) {
        String key = USER_CACHE_PREFIX + userId;
        UserProfileDto profile = cacheManager.fetch(key, UserProfileDto.class);

        if (profile != null) {
            return profile;
        }

        profile = userMapper.toProfile(findById(userId));
        cacheManager.save(key, profile, Duration.ofMinutes(10));

        return profile;
    }

    public User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
    }

    @Transactional
    public UserProfileDto update(UserPutRequest userPutRequest) {
        User user = getCurrentUser();

        userMapper.updateUserFromPutRequest(userPutRequest, user);

        if (user.getPreferences() != null) {
            user.getPreferences().setUser(user);
        }

        updateGeolocation(user, userPutRequest.getGeolocation());

        user = save(user);
        cacheManager.delete(USER_CACHE_PREFIX + user);

        return userMapper.toProfile(user);
    }

    private void updateGeolocation(User user, UserGeolocationDto geolocation) {
        if (geolocation != null) {
            Point location = pointFactory.createPoint(geolocation.getLongitude(), geolocation.getLatitude());

            if (user.getGeolocation() != null) {
                user.getGeolocation().setLocation(location);
            } else {
                user.setGeolocation(new UserGeolocation(location, user));
            }
        } else {
            user.setGeolocation(null);
        }
    }
}
