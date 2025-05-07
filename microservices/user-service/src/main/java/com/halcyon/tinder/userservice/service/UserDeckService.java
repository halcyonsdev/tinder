package com.halcyon.tinder.userservice.service;

import com.halcyon.tinder.userservice.dto.UserProfileDto;
import com.halcyon.tinder.userservice.model.User;
import com.halcyon.tinder.userservice.model.UserGeolocation;
import com.halcyon.tinder.userservice.model.UserPreferences;
import com.halcyon.tinder.userservice.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDeckService {

    private final UserRepository userRepository;
    private final UserService userService;

    public List<UserProfileDto> getDeck() {
        User currentUser = userService.getCurrentUser();
        UserPreferences preferences = currentUser.getPreferences();
        UserGeolocation geolocation = currentUser.getGeolocation();

        return userRepository.findDeck(
                currentUser.getId(),
                preferences != null
                        ? preferences.getMinAge()
                        : null,
                preferences != null
                        ? preferences.getMaxAge()
                        : null,
                preferences != null
                        ? preferences.getGender()
                        : null,
                geolocation != null
                        ? geolocation.getLocation().getX()
                        : null,
                geolocation != null
                        ? geolocation.getLocation().getY()
                        : null,
                preferences != null && preferences.getDistanceInMeters() != null
                        ? preferences.getDistanceInMeters()
                        : 100_000D);
    }
}
