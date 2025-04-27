package com.halcyon.tinder.userservice.service;

import com.halcyon.tinder.exceptioncore.UserNotFoundException;
import com.halcyon.tinder.jwtcore.JwtProvider;
import com.halcyon.tinder.rediscache.CacheManager;
import com.halcyon.tinder.userservice.dto.CreateUserRequest;
import com.halcyon.tinder.userservice.dto.UserGeolocationDto;
import com.halcyon.tinder.userservice.dto.UserProfileDto;
import com.halcyon.tinder.userservice.dto.UserPutRequest;
import com.halcyon.tinder.userservice.exception.AccessDeniedException;
import com.halcyon.tinder.userservice.exception.ImageNotFoundException;
import com.halcyon.tinder.userservice.mapper.UserMapper;
import com.halcyon.tinder.userservice.model.User;
import com.halcyon.tinder.userservice.model.UserGeolocation;
import com.halcyon.tinder.userservice.model.UserImage;
import com.halcyon.tinder.userservice.repository.UserImageRepository;
import com.halcyon.tinder.userservice.repository.UserRepository;
import com.halcyon.tinder.userservice.service.support.ImageData;
import com.halcyon.tinder.userservice.util.PointFactory;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private final ImageStorageService imageStorageService;
    private final UserMapper userMapper;
    private final CacheManager cacheManager;
    private final JwtProvider jwtProvider;
    private final PointFactory pointFactory;

    private static final String USER_CACHE_PREFIX = "user-profile:";

    public void create(CreateUserRequest createUserRequest) {
        User user = userMapper.toEntity(createUserRequest);

        if (user.getPreferences() != null) {
            user.getPreferences().setUser(user);
        }

        var geolocation = createUserRequest.getGeolocation();
        if (geolocation != null) {
            Point location = pointFactory.createPoint(geolocation.getLongitude(), geolocation.getLatitude());
            user.setGeolocation(new UserGeolocation(location, user));
        }

        save(user);
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
        System.out.println(currentUser.getGeolocation());
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

    private User findById(UUID userId) {
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

    public UserProfileDto uploadAvatar(MultipartFile image) {
        User user = getCurrentUser();

        if (user.getAvatar() != null) {
            imageStorageService.deleteImage(user.getAvatar());
        }

        String imageName = imageStorageService.uploadImage(image);
        user.setAvatar(imageName);

        cacheManager.delete(USER_CACHE_PREFIX + user);
        return userMapper.toProfile(save(user));
    }

    public ImageData downloadImage(String imageName) {
        return imageStorageService.downloadImage(imageName);
    }

    public UserProfileDto uploadGalleryImages(List<MultipartFile> images) {
        User user = getCurrentUser();

        for (var image : images) {
            String imageName = imageStorageService.uploadImage(image);

            var userImage = new UserImage(imageName, user);
            userImageRepository.save(userImage);
        }

        return userMapper.toProfile(user);
    }

    public List<String> getGallery(UUID userId) {
        User user = findById(userId);

        return user.getGallery()
                .stream()
                .map(UserImage::getImageName)
                .toList();
    }

    public UserProfileDto deleteAvatar() {
        User user = getCurrentUser();

        if (user.getAvatar() != null) {
            imageStorageService.deleteImage(user.getAvatar());
        }

        user.setAvatar(null);

        cacheManager.delete(USER_CACHE_PREFIX + user);
        return userMapper.toProfile(save(user));
    }

    public void deleteGalleryImage(String imageName) {
        User user = getCurrentUser();
        UserImage userImage = userImageRepository.findByImageName(imageName)
                .orElseThrow(() -> new ImageNotFoundException("Image with name " + imageName + " not found in gallery"));

        if (!userImage.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("This image belongs to another user");
        }

        imageStorageService.deleteImage(imageName);
        userImageRepository.delete(userImage);
    }
}
