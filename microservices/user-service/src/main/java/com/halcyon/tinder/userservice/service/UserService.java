package com.halcyon.tinder.userservice.service;

import com.halcyon.tinder.userservice.exception.UserNotFoundException;
import com.halcyon.tinder.userservice.mapper.UserMapper;
import com.halcyon.tinder.userservice.model.User;
import com.halcyon.tinder.userservice.repository.UserRepository;
import com.halcyon.tinder.userservice.security.JwtProvider;
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
}
