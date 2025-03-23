package com.halcyon.tinder.userservice.service;

import com.halcyon.tinder.userservice.dto.user.UserProfileDto;
import com.halcyon.tinder.userservice.dto.user.UserPutRequest;
import com.halcyon.tinder.userservice.exception.UserNotFoundException;
import com.halcyon.tinder.userservice.mapper.UserMapper;
import com.halcyon.tinder.userservice.model.User;
import com.halcyon.tinder.userservice.repository.UserRepository;
import com.halcyon.tinder.userservice.security.JwtProvider;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final UserMapper userMapper;

    public User save(User user) {
        return userRepository.save(user);
    }

    public User findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UserNotFoundException("User with phone number " + phoneNumber + " not found"));
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));

        return userMapper.toProfile(user);
    }

    public UserProfileDto update(UserPutRequest userPutRequest) {
        User user = getCurrentUser();

        userMapper.updateUserFromPutRequest(userPutRequest, user);

        if (user.getPreferences() != null) {
            user.getPreferences().setUser(user);
        }

        user = save(user);

        return userMapper.toProfile(user);
    }
}
